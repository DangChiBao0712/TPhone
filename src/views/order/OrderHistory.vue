<template>
  <div>
    <h1 class="page-title">Đơn hàng của tôi</h1>

    <div v-if="orderStore.loading" class="card">
      <div class="card-body">Đang tải đơn hàng...</div>
    </div>

    <div v-else-if="orderStore.orders.length === 0" class="card">
      <div class="card-body empty-state">Bạn chưa có đơn hàng nào.</div>
    </div>

    <div v-else class="grid">
      <div
        v-for="order in orderStore.orders"
        :key="order.id"
        class="card"
      >
        <div class="card-body">
          <div class="flex-between" style="align-items:flex-start; margin-bottom:10px;">
            <div>
              <h2 style="margin:0 0 8px; font-size:22px;">Mã đơn: {{ order.orderCode }}</h2>
              <p class="text-muted" style="margin:0;">Thời gian đặt: {{ formatDateTime(order.placedAt) }}</p>
            </div>

            <span class="badge">{{ formatOrderStatus(order.orderStatus) }}</span>
          </div>

          <div class="grid grid-2" style="margin-top: 16px;">
            <div>
              <p class="text-muted" style="margin:0 0 6px;">Trạng thái thanh toán</p>
              <p style="margin:0; font-weight:700;">{{ formatPaymentStatus(order.paymentStatus) }}</p>
            </div>

            <div>
              <p class="text-muted" style="margin:0 0 6px;">Khách hàng</p>
              <p style="margin:0; font-weight:700;">{{ order.customerName }}</p>
            </div>
          </div>

          <div style="margin-top: 16px;">
            <p class="text-muted" style="margin:0 0 6px;">Tổng tiền</p>
            <p style="margin:0; font-size:22px; font-weight:700; color:#dc2626;">
              {{ formatPrice(order.totalAmount) }}
            </p>
          </div>

          <div v-if="order.items?.length" style="margin-top:16px;">
            <p style="margin:0 0 8px; font-weight:700;">Sản phẩm trong đơn</p>
            <ul style="margin:0; padding-left:18px; line-height:1.8;">
              <li v-for="item in order.items" :key="item.id">
                {{ item.productName }} - {{ item.quantity }} x {{ formatPrice(item.unitPrice) }}
              </li>
            </ul>
          </div>

          <div style="margin-top:18px;">
            <router-link :to="`/orders/${order.id}`" class="btn btn-primary">
              Xem chi tiết
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useOrderStore } from '../../stores/order.store';
import {
  formatPrice,
  formatDateTime,
  formatOrderStatus,
  formatPaymentStatus,
} from '../../utils/format';

const orderStore = useOrderStore();

onMounted(async () => {
  await orderStore.fetchMyOrders();
});
</script>