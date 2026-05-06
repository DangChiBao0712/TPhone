package com.tphone.service.impl;

import com.tphone.dto.response.AdminDashboardResponse;
import com.tphone.entity.Account;
import com.tphone.entity.Order;
import com.tphone.entity.OrderItem;
import com.tphone.entity.Product;
import com.tphone.enums.OrderStatus;
import com.tphone.enums.PaymentStatus;
import com.tphone.repository.AccountRepository;
import com.tphone.repository.OrderItemRepository;
import com.tphone.repository.OrderRepository;
import com.tphone.repository.ProductRepository;
import com.tphone.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public AdminDashboardResponse getDashboard(String period, LocalDate date) {
        String normalizedPeriod = normalizePeriod(period);
        LocalDate selectedDate = date != null ? date : LocalDate.now();

        DateRange range = buildRange(normalizedPeriod, selectedDate);

        List<Account> allUsers = accountRepository.findAllByDeletedAtIsNull();
        List<Product> allProducts = productRepository.findAllByDeletedAtIsNull();
        List<Order> allOrders = orderRepository.findAll();
        List<OrderItem> allOrderItems = orderItemRepository.findAll();

        Map<Long, Account> accountMap = allUsers.stream()
                .collect(Collectors.toMap(Account::getId, Function.identity(), (a, b) -> a));

        Map<Long, Product> productMap = allProducts.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity(), (a, b) -> a));

        List<Order> ordersInPeriod = allOrders.stream()
                .filter(order -> order.getPlacedAt() != null)
                .filter(order -> isWithin(order.getPlacedAt(), range.getFrom(), range.getTo()))
                .toList();

        Set<Long> orderIdsInPeriod = ordersInPeriod.stream()
                .map(Order::getId)
                .collect(Collectors.toSet());

        List<OrderItem> orderItemsInPeriod = allOrderItems.stream()
                .filter(item -> item.getOrder() != null)
                .filter(item -> orderIdsInPeriod.contains(item.getOrder().getId()))
                .toList();

        BigDecimal revenueInPeriod = ordersInPeriod.stream()
                .filter(order -> order.getPaymentStatus() == PaymentStatus.SUCCESS)
                .map(order -> defaultMoney(order.getTotalAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long completedOrders = ordersInPeriod.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.COMPLETED)
                .count();

        long pendingOrders = ordersInPeriod.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.PENDING)
                .count();

        long cancelledOrders = ordersInPeriod.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.CANCELLED)
                .count();

        BigDecimal averageOrderValue = ordersInPeriod.isEmpty()
                ? BigDecimal.ZERO
                : revenueInPeriod.divide(BigDecimal.valueOf(ordersInPeriod.size()), 2, RoundingMode.HALF_UP);

        long newUsersInPeriod = allUsers.stream()
                .filter(account -> account.getCreatedAt() != null)
                .filter(account -> isWithin(account.getCreatedAt(), range.getFrom(), range.getTo()))
                .count();

        long activeCustomersInPeriod = ordersInPeriod.stream()
                .map(Order::getAccount)
                .filter(Objects::nonNull)
                .map(account -> account.getId())
                .filter(Objects::nonNull)
                .distinct()
                .count();

        long lowStockProducts = allProducts.stream()
                .filter(product -> product.getStockQuantity() != null && product.getMinStockAlert() != null)
                .filter(product -> product.getStockQuantity() <= product.getMinStockAlert())
                .count();

        AdminDashboardResponse response = new AdminDashboardResponse();
        response.setPeriod(normalizedPeriod);
        response.setLabel(buildRangeLabel(normalizedPeriod, selectedDate));
        response.setFromDate(range.getFrom().toLocalDate().toString());
        response.setToDate(range.getTo().toLocalDate().toString());

        response.setTotalUsers((long) allUsers.size());
        response.setNewUsersInPeriod(newUsersInPeriod);
        response.setActiveCustomersInPeriod(activeCustomersInPeriod);

        response.setTotalProducts((long) allProducts.size());
        response.setLowStockProducts(lowStockProducts);

        response.setTotalOrdersInPeriod((long) ordersInPeriod.size());
        response.setCompletedOrdersInPeriod(completedOrders);
        response.setPendingOrdersInPeriod(pendingOrders);
        response.setCancelledOrdersInPeriod(cancelledOrders);

        response.setRevenueInPeriod(revenueInPeriod);
        response.setAverageOrderValue(averageOrderValue);

        response.setOrderStatusSummary(buildOrderStatusSummary(ordersInPeriod));
        response.setPaymentStatusSummary(buildPaymentStatusSummary(ordersInPeriod));
        response.setRevenueSeries(buildRevenueSeries(normalizedPeriod, selectedDate, allOrders));

        response.setTopCustomersByOrders(buildTopCustomersByOrders(ordersInPeriod, accountMap));
        response.setTopCustomersByRevenue(buildTopCustomersByRevenue(ordersInPeriod, accountMap));

        response.setTopProductsByQuantity(buildTopProductsByQuantity(orderItemsInPeriod, productMap));
        response.setTopProductsByRevenue(buildTopProductsByRevenue(orderItemsInPeriod, productMap));

        response.setTopBrandsByQuantity(buildTopBrandsByQuantity(orderItemsInPeriod, productMap));
        response.setTopBrandsByRevenue(buildTopBrandsByRevenue(orderItemsInPeriod, productMap));

        response.setTopCategoriesByQuantity(buildTopCategoriesByQuantity(orderItemsInPeriod, productMap));
        response.setTopCategoriesByRevenue(buildTopCategoriesByRevenue(orderItemsInPeriod, productMap));

        return response;
    }

    private List<AdminDashboardResponse.MetricItem> buildOrderStatusSummary(List<Order> orders) {
        return Arrays.stream(OrderStatus.values())
                .map(status -> new AdminDashboardResponse.MetricItem(
                        status.name(),
                        orders.stream().filter(order -> order.getOrderStatus() == status).count()
                ))
                .toList();
    }

    private List<AdminDashboardResponse.MetricItem> buildPaymentStatusSummary(List<Order> orders) {
        return Arrays.stream(PaymentStatus.values())
                .map(status -> new AdminDashboardResponse.MetricItem(
                        status.name(),
                        orders.stream().filter(order -> order.getPaymentStatus() == status).count()
                ))
                .toList();
    }

    private List<AdminDashboardResponse.ChartPoint> buildRevenueSeries(
            String period,
            LocalDate selectedDate,
            List<Order> allOrders
    ) {
        if ("day".equals(period)) {
            return buildHourlySeries(selectedDate, allOrders);
        }
        if ("month".equals(period)) {
            return buildDailySeries(selectedDate, allOrders);
        }
        return buildMonthlySeries(selectedDate, allOrders);
    }

    private List<AdminDashboardResponse.ChartPoint> buildHourlySeries(LocalDate selectedDate, List<Order> allOrders) {
        List<AdminDashboardResponse.ChartPoint> points = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            final int currentHour = hour;
            BigDecimal value = allOrders.stream()
                    .filter(order -> order.getPaymentStatus() == PaymentStatus.SUCCESS)
                    .filter(order -> order.getPlacedAt() != null)
                    .filter(order -> order.getPlacedAt().toLocalDate().equals(selectedDate))
                    .filter(order -> order.getPlacedAt().getHour() == currentHour)
                    .map(order -> defaultMoney(order.getTotalAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            points.add(new AdminDashboardResponse.ChartPoint(String.format("%02d:00", hour), value));
        }

        return points;
    }

    private List<AdminDashboardResponse.ChartPoint> buildDailySeries(LocalDate selectedDate, List<Order> allOrders) {
        YearMonth yearMonth = YearMonth.from(selectedDate);
        int days = yearMonth.lengthOfMonth();
        List<AdminDashboardResponse.ChartPoint> points = new ArrayList<>();

        for (int day = 1; day <= days; day++) {
            LocalDate currentDate = yearMonth.atDay(day);
            BigDecimal value = allOrders.stream()
                    .filter(order -> order.getPaymentStatus() == PaymentStatus.SUCCESS)
                    .filter(order -> order.getPlacedAt() != null)
                    .filter(order -> order.getPlacedAt().toLocalDate().equals(currentDate))
                    .map(order -> defaultMoney(order.getTotalAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            points.add(new AdminDashboardResponse.ChartPoint(String.valueOf(day), value));
        }

        return points;
    }

    private List<AdminDashboardResponse.ChartPoint> buildMonthlySeries(LocalDate selectedDate, List<Order> allOrders) {
        int year = selectedDate.getYear();
        List<AdminDashboardResponse.ChartPoint> points = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            final int currentMonth = month;
            BigDecimal value = allOrders.stream()
                    .filter(order -> order.getPaymentStatus() == PaymentStatus.SUCCESS)
                    .filter(order -> order.getPlacedAt() != null)
                    .filter(order -> order.getPlacedAt().getYear() == year)
                    .filter(order -> order.getPlacedAt().getMonthValue() == currentMonth)
                    .map(order -> defaultMoney(order.getTotalAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            points.add(new AdminDashboardResponse.ChartPoint("T" + month, value));
        }

        return points;
    }

    private List<AdminDashboardResponse.TopCustomerItem> buildTopCustomersByOrders(
            List<Order> ordersInPeriod,
            Map<Long, Account> accountMap
    ) {
        Map<Long, List<Order>> grouped = ordersInPeriod.stream()
                .filter(order -> order.getAccount() != null && order.getAccount().getId() != null)
                .collect(Collectors.groupingBy(order -> order.getAccount().getId()));

        return grouped.entrySet().stream()
                .map(entry -> mapCustomerAggregate(entry.getKey(), entry.getValue(), accountMap))
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparing(AdminDashboardResponse.TopCustomerItem::getTotalOrders, Comparator.reverseOrder())
                        .thenComparing(AdminDashboardResponse.TopCustomerItem::getTotalSpent, Comparator.reverseOrder()))
                .limit(5)
                .toList();
    }

    private List<AdminDashboardResponse.TopCustomerItem> buildTopCustomersByRevenue(
            List<Order> ordersInPeriod,
            Map<Long, Account> accountMap
    ) {
        Map<Long, List<Order>> grouped = ordersInPeriod.stream()
                .filter(order -> order.getAccount() != null && order.getAccount().getId() != null)
                .collect(Collectors.groupingBy(order -> order.getAccount().getId()));

        return grouped.entrySet().stream()
                .map(entry -> mapCustomerAggregate(entry.getKey(), entry.getValue(), accountMap))
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparing(AdminDashboardResponse.TopCustomerItem::getTotalSpent, Comparator.reverseOrder())
                        .thenComparing(AdminDashboardResponse.TopCustomerItem::getTotalOrders, Comparator.reverseOrder()))
                .limit(5)
                .toList();
    }

    private AdminDashboardResponse.TopCustomerItem mapCustomerAggregate(
            Long accountId,
            List<Order> orders,
            Map<Long, Account> accountMap
    ) {
        Account account = accountMap.get(accountId);
        if (account == null) {
            return null;
        }

        BigDecimal totalSpent = orders.stream()
                .filter(order -> order.getPaymentStatus() == PaymentStatus.SUCCESS)
                .map(order -> defaultMoney(order.getTotalAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminDashboardResponse.TopCustomerItem(
                account.getId(),
                account.getFullName(),
                account.getEmail(),
                account.getPhone(),
                (long) orders.size(),
                totalSpent
        );
    }

    private List<AdminDashboardResponse.TopSellingItem> buildTopProductsByQuantity(
            List<OrderItem> items,
            Map<Long, Product> productMap
    ) {
        Map<Long, AggregateValue> map = new HashMap<>();

        for (OrderItem item : items) {
            Long productId = resolveProductId(item);
            if (productId == null) continue;

            Product product = productMap.get(productId);
            String name = item.getProductName() != null
                    ? item.getProductName()
                    : product != null ? product.getName() : "N/A";

            AggregateValue current = map.getOrDefault(productId, new AggregateValue(productId, name, 0L, BigDecimal.ZERO));
            current.totalQuantity += item.getQuantity() != null ? item.getQuantity().longValue() : 0L;
            current.totalRevenue = current.totalRevenue.add(defaultMoney(item.getLineTotal()));
            map.put(productId, current);
        }

        return map.values().stream()
                .sorted(Comparator.comparing((AggregateValue x) -> x.totalQuantity).reversed())
                .limit(5)
                .map(AggregateValue::toTopSellingItem)
                .toList();
    }

    private List<AdminDashboardResponse.TopSellingItem> buildTopProductsByRevenue(
            List<OrderItem> items,
            Map<Long, Product> productMap
    ) {
        Map<Long, AggregateValue> map = new HashMap<>();

        for (OrderItem item : items) {
            Long productId = resolveProductId(item);
            if (productId == null) continue;

            Product product = productMap.get(productId);
            String name = item.getProductName() != null
                    ? item.getProductName()
                    : product != null ? product.getName() : "N/A";

            AggregateValue current = map.getOrDefault(productId, new AggregateValue(productId, name, 0L, BigDecimal.ZERO));
            current.totalQuantity += item.getQuantity() != null ? item.getQuantity().longValue() : 0L;
            current.totalRevenue = current.totalRevenue.add(defaultMoney(item.getLineTotal()));
            map.put(productId, current);
        }

        return map.values().stream()
                .sorted(Comparator.comparing((AggregateValue x) -> x.totalRevenue).reversed())
                .limit(5)
                .map(AggregateValue::toTopSellingItem)
                .toList();
    }

    private List<AdminDashboardResponse.TopSellingItem> buildTopBrandsByQuantity(
            List<OrderItem> items,
            Map<Long, Product> productMap
    ) {
        return aggregateBy(
                items,
                productMap,
                product -> product.getBrand() != null ? product.getBrand().getId() : null,
                product -> product.getBrand() != null ? product.getBrand().getName() : null,
                Comparator.comparing((AggregateValue x) -> x.totalQuantity).reversed()
        );
    }

    private List<AdminDashboardResponse.TopSellingItem> buildTopBrandsByRevenue(
            List<OrderItem> items,
            Map<Long, Product> productMap
    ) {
        return aggregateBy(
                items,
                productMap,
                product -> product.getBrand() != null ? product.getBrand().getId() : null,
                product -> product.getBrand() != null ? product.getBrand().getName() : null,
                Comparator.comparing((AggregateValue x) -> x.totalRevenue).reversed()
        );
    }

    private List<AdminDashboardResponse.TopSellingItem> buildTopCategoriesByQuantity(
            List<OrderItem> items,
            Map<Long, Product> productMap
    ) {
        return aggregateBy(
                items,
                productMap,
                product -> product.getCategory() != null ? product.getCategory().getId() : null,
                product -> product.getCategory() != null ? product.getCategory().getName() : null,
                Comparator.comparing((AggregateValue x) -> x.totalQuantity).reversed()
        );
    }

    private List<AdminDashboardResponse.TopSellingItem> buildTopCategoriesByRevenue(
            List<OrderItem> items,
            Map<Long, Product> productMap
    ) {
        return aggregateBy(
                items,
                productMap,
                product -> product.getCategory() != null ? product.getCategory().getId() : null,
                product -> product.getCategory() != null ? product.getCategory().getName() : null,
                Comparator.comparing((AggregateValue x) -> x.totalRevenue).reversed()
        );
    }

    private List<AdminDashboardResponse.TopSellingItem> aggregateBy(
            List<OrderItem> items,
            Map<Long, Product> productMap,
            Function<Product, Long> idExtractor,
            Function<Product, String> nameExtractor,
            Comparator<AggregateValue> comparator
    ) {
        Map<Long, AggregateValue> map = new HashMap<>();

        for (OrderItem item : items) {
            Long productId = resolveProductId(item);
            if (productId == null) continue;

            Product product = productMap.get(productId);
            if (product == null) continue;

            Long groupId = idExtractor.apply(product);
            if (groupId == null) continue;

            String name = nameExtractor.apply(product);
            if (name == null || name.isBlank()) continue;

            AggregateValue current = map.getOrDefault(groupId, new AggregateValue(groupId, name, 0L, BigDecimal.ZERO));
            current.totalQuantity += item.getQuantity() != null ? item.getQuantity().longValue() : 0L;
            current.totalRevenue = current.totalRevenue.add(defaultMoney(item.getLineTotal()));
            map.put(groupId, current);
        }

        return map.values().stream()
                .sorted(comparator)
                .limit(5)
                .map(AggregateValue::toTopSellingItem)
                .toList();
    }

    private Long resolveProductId(OrderItem item) {
        if (item == null) return null;
        if (item.getProduct() != null && item.getProduct().getId() != null) {
            return item.getProduct().getId();
        }
        return null;
    }

    private boolean isWithin(LocalDateTime value, LocalDateTime from, LocalDateTime to) {
        return (value.isEqual(from) || value.isAfter(from))
                && (value.isEqual(to) || value.isBefore(to));
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String normalizePeriod(String period) {
        if (period == null || period.isBlank()) return "day";
        String value = period.trim().toLowerCase();
        if (!value.equals("day") && !value.equals("month") && !value.equals("year")) {
            return "day";
        }
        return value;
    }

    private DateRange buildRange(String period, LocalDate selectedDate) {
        if ("month".equals(period)) {
            YearMonth yearMonth = YearMonth.from(selectedDate);
            return new DateRange(
                    yearMonth.atDay(1).atStartOfDay(),
                    yearMonth.atEndOfMonth().atTime(23, 59, 59)
            );
        }

        if ("year".equals(period)) {
            LocalDate firstDay = LocalDate.of(selectedDate.getYear(), 1, 1);
            LocalDate lastDay = LocalDate.of(selectedDate.getYear(), 12, 31);
            return new DateRange(
                    firstDay.atStartOfDay(),
                    lastDay.atTime(23, 59, 59)
            );
        }

        return new DateRange(
                selectedDate.atStartOfDay(),
                selectedDate.atTime(23, 59, 59)
        );
    }

    private String buildRangeLabel(String period, LocalDate selectedDate) {
        if ("month".equals(period)) {
            return "Tháng " + selectedDate.getMonthValue() + "/" + selectedDate.getYear();
        }
        if ("year".equals(period)) {
            return "Năm " + selectedDate.getYear();
        }
        return "Ngày " + selectedDate;
    }

    private static class DateRange {
        private final LocalDateTime from;
        private final LocalDateTime to;

        private DateRange(LocalDateTime from, LocalDateTime to) {
            this.from = from;
            this.to = to;
        }

        public LocalDateTime getFrom() {
            return from;
        }

        public LocalDateTime getTo() {
            return to;
        }
    }

    private static class AggregateValue {
        private final Long id;
        private final String name;
        private Long totalQuantity;
        private BigDecimal totalRevenue;

        private AggregateValue(Long id, String name, Long totalQuantity, BigDecimal totalRevenue) {
            this.id = id;
            this.name = name;
            this.totalQuantity = totalQuantity;
            this.totalRevenue = totalRevenue;
        }

        private AdminDashboardResponse.TopSellingItem toTopSellingItem() {
            return new AdminDashboardResponse.TopSellingItem(id, name, totalQuantity, totalRevenue);
        }
    }
}