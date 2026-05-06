package com.tphone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private String period;
    private String label;
    private String fromDate;
    private String toDate;

    private Long totalUsers;
    private Long newUsersInPeriod;
    private Long activeCustomersInPeriod;

    private Long totalProducts;
    private Long lowStockProducts;

    private Long totalOrdersInPeriod;
    private Long completedOrdersInPeriod;
    private Long pendingOrdersInPeriod;
    private Long cancelledOrdersInPeriod;

    private BigDecimal revenueInPeriod;
    private BigDecimal averageOrderValue;

    private List<MetricItem> orderStatusSummary = new ArrayList<>();
    private List<MetricItem> paymentStatusSummary = new ArrayList<>();
    private List<ChartPoint> revenueSeries = new ArrayList<>();

    private List<TopCustomerItem> topCustomersByOrders = new ArrayList<>();
    private List<TopCustomerItem> topCustomersByRevenue = new ArrayList<>();

    private List<TopSellingItem> topProductsByQuantity = new ArrayList<>();
    private List<TopSellingItem> topProductsByRevenue = new ArrayList<>();

    private List<TopSellingItem> topBrandsByQuantity = new ArrayList<>();
    private List<TopSellingItem> topBrandsByRevenue = new ArrayList<>();

    private List<TopSellingItem> topCategoriesByQuantity = new ArrayList<>();
    private List<TopSellingItem> topCategoriesByRevenue = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricItem {
        private String name;
        private Long value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartPoint {
        private String label;
        private BigDecimal value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCustomerItem {
        private Long accountId;
        private String fullName;
        private String email;
        private String phone;
        private Long totalOrders;
        private BigDecimal totalSpent;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopSellingItem {
        private Long id;
        private String name;
        private Long totalQuantity;
        private BigDecimal totalRevenue;
    }
}