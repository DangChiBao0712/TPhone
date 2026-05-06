package com.tphone.service.impl;

import com.tphone.dto.request.CreateOrderRequest;
import com.tphone.dto.request.UpdateOrderStatusRequest;
import com.tphone.dto.response.OrderItemResponse;
import com.tphone.dto.response.OrderResponse;
import com.tphone.entity.Account;
import com.tphone.entity.AccountAddress;
import com.tphone.entity.Cart;
import com.tphone.entity.CartItem;
import com.tphone.entity.Order;
import com.tphone.entity.OrderItem;
import com.tphone.entity.Product;
import com.tphone.enums.OrderStatus;
import com.tphone.enums.PaymentMethod;
import com.tphone.enums.PaymentStatus;
import com.tphone.exception.BadRequestException;
import com.tphone.exception.ResourceNotFoundException;
import com.tphone.repository.AccountAddressRepository;
import com.tphone.repository.AccountRepository;
import com.tphone.repository.CartItemRepository;
import com.tphone.repository.CartRepository;
import com.tphone.repository.OrderItemRepository;
import com.tphone.repository.OrderRepository;
import com.tphone.repository.ProductRepository;
import com.tphone.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final AccountRepository accountRepository;
    private final AccountAddressRepository accountAddressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(Long accountId, CreateOrderRequest request) {
        validateCreateOrderRequest(request);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Cart cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        AccountAddress shippingAddress = null;
        if (request.getShippingAddressId() != null) {
            shippingAddress = accountAddressRepository
                    .findByIdAndAccountIdAndDeletedAtIsNull(request.getShippingAddressId(), accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));
        }

        AccountAddress billingAddress = null;
        if (request.getBillingAddressId() != null) {
            billingAddress = accountAddressRepository
                    .findByIdAndAccountIdAndDeletedAtIsNull(request.getBillingAddressId(), accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Billing address not found"));
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getDeletedAt() != null) {
                throw new BadRequestException("Product has been deleted: " + product.getName());
            }

            if (product.getStockQuantity() == null || product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Not enough stock for product: " + product.getName());
            }

            BigDecimal lineTotal = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.add(shippingFee).add(taxAmount).subtract(discountAmount);

        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setAccount(account);

        if (shippingAddress != null) {
            order.setShippingAddress(shippingAddress);
        }
        if (billingAddress != null) {
            order.setBillingAddress(billingAddress);
        }

        order.setCustomerName(trim(request.getCustomerName()));
        order.setCustomerEmail(trim(request.getCustomerEmail()));
        order.setCustomerPhone(trim(request.getCustomerPhone()));

        order.setShippingRecipientName(trim(request.getShippingRecipientName()));
        order.setShippingRecipientPhone(trim(request.getShippingRecipientPhone()));
        order.setShippingLine1(trim(request.getShippingLine1()));
        order.setShippingLine2(trim(request.getShippingLine2()));
        order.setShippingWard(trim(request.getShippingWard()));
        order.setShippingDistrict(trim(request.getShippingDistrict()));
        order.setShippingCity(trim(request.getShippingCity()));
        order.setShippingStateProvince(trim(request.getShippingStateProvince()));
        order.setShippingPostalCode(trim(request.getShippingPostalCode()));
        order.setShippingCountry(
                request.getShippingCountry() != null && !request.getShippingCountry().isBlank()
                        ? request.getShippingCountry().trim()
                        : "Vietnam"
        );

        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentMethod(parsePaymentMethod(request.getPaymentMethod()));

        order.setSubtotalAmount(subtotal);
        order.setShippingFee(shippingFee);
        order.setDiscountAmount(discountAmount);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount);

        order.setNote(trim(request.getNote()));
        order.setPlacedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductSku(product.getSku());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscountAmount(BigDecimal.ZERO);
            orderItem.setTaxAmount(BigDecimal.ZERO);
            orderItem.setLineTotal(
                    cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );

            orderItemRepository.save(orderItem);

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        cartItemRepository.deleteAll(cartItems);

        return toResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders(Long accountId) {
        return orderRepository.findAllByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getMyOrderById(Long accountId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getAccount().getId().equals(accountId)) {
            throw new BadRequestException("Order does not belong to current user");
        }

        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders(String keyword, String orderStatus, String paymentStatus) {
        OrderStatus parsedOrderStatus = parseOrderStatusOrNull(orderStatus);
        PaymentStatus parsedPaymentStatus = parsePaymentStatusOrNull(paymentStatus);
        String normalizedKeyword = normalizeNullable(keyword);

        return orderRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .filter(order -> {
                    if (parsedOrderStatus == null) return true;
                    return order.getOrderStatus() == parsedOrderStatus;
                })
                .filter(order -> {
                    if (parsedPaymentStatus == null) return true;
                    return order.getPaymentStatus() == parsedPaymentStatus;
                })
                .filter(order -> {
                    if (normalizedKeyword == null) return true;

                    String q = normalizedKeyword.toLowerCase();
                    return contains(order.getOrderCode(), q)
                            || contains(order.getCustomerName(), q)
                            || contains(order.getCustomerEmail(), q)
                            || contains(order.getCustomerPhone(), q)
                            || contains(order.getShippingRecipientPhone(), q);
                })
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (request.getOrderStatus() != null && !request.getOrderStatus().isBlank()) {
            OrderStatus newOrderStatus = parseOrderStatus(request.getOrderStatus());
            order.setOrderStatus(newOrderStatus);

            switch (newOrderStatus) {
                case CONFIRMED -> order.setConfirmedAt(LocalDateTime.now());
                case SHIPPING -> order.setShippedAt(LocalDateTime.now());
                case COMPLETED -> order.setCompletedAt(LocalDateTime.now());
                case CANCELLED -> order.setCancelledAt(LocalDateTime.now());
                default -> {
                }
            }
        }

        if (request.getPaymentStatus() != null && !request.getPaymentStatus().isBlank()) {
            PaymentStatus newPaymentStatus = parsePaymentStatus(request.getPaymentStatus());
            order.setPaymentStatus(newPaymentStatus);

            if (newPaymentStatus == PaymentStatus.SUCCESS || newPaymentStatus == PaymentStatus.REFUNDED) {
                if (order.getPaidAt() == null) {
                    order.setPaidAt(LocalDateTime.now());
                }
            }
        }

        if (request.getInternalNote() != null) {
            order.setInternalNote(request.getInternalNote().trim());
        }

        return toResponse(orderRepository.save(order));
    }

    private void validateCreateOrderRequest(CreateOrderRequest request) {
        if (isBlank(request.getCustomerName())) {
            throw new BadRequestException("Customer name is required");
        }
        if (isBlank(request.getCustomerEmail())) {
            throw new BadRequestException("Customer email is required");
        }
        if (isBlank(request.getCustomerPhone())) {
            throw new BadRequestException("Customer phone is required");
        }
        if (isBlank(request.getShippingRecipientName())) {
            throw new BadRequestException("Shipping recipient name is required");
        }
        if (isBlank(request.getShippingRecipientPhone())) {
            throw new BadRequestException("Shipping recipient phone is required");
        }
        if (isBlank(request.getShippingLine1())) {
            throw new BadRequestException("Shipping address line 1 is required");
        }
        if (isBlank(request.getShippingCity())) {
            throw new BadRequestException("Shipping city is required");
        }
    }

    private PaymentMethod parsePaymentMethod(String value) {
        if (value == null || value.isBlank()) {
            return PaymentMethod.COD;
        }

        try {
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid payment method");
        }
    }

    private OrderStatus parseOrderStatus(String value) {
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid order status");
        }
    }

    private OrderStatus parseOrderStatusOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        return parseOrderStatus(value);
    }

    private PaymentStatus parsePaymentStatus(String value) {
        try {
            return PaymentStatus.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid payment status");
        }
    }

    private PaymentStatus parsePaymentStatusOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        return parsePaymentStatus(value);
    }

    private String generateOrderCode() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean contains(String source, String keyword) {
        return source != null && source.toLowerCase().contains(keyword);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = orderItemRepository.findAllByOrderId(order.getId())
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct() != null ? item.getProduct().getId() : null,
                        item.getProductName(),
                        item.getProductSku(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getDiscountAmount(),
                        item.getTaxAmount(),
                        item.getLineTotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getAccount().getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                order.getShippingRecipientName(),
                order.getShippingRecipientPhone(),
                order.getShippingLine1(),
                order.getShippingLine2(),
                order.getShippingWard(),
                order.getShippingDistrict(),
                order.getShippingCity(),
                order.getShippingStateProvince(),
                order.getShippingPostalCode(),
                order.getShippingCountry(),
                order.getOrderStatus() != null ? order.getOrderStatus().name() : null,
                order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null,
                order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null,
                order.getSubtotalAmount(),
                order.getShippingFee(),
                order.getDiscountAmount(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getNote(),
                order.getInternalNote(),
                order.getPlacedAt(),
                items
        );
    }
}