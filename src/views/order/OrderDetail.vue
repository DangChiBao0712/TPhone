<template>
  <div>
    <div v-if="orderStore.loading" class="card">
      <div class="card-body">Đang tải chi tiết đơn hàng...</div>
    </div>

    <div v-else-if="!orderStore.orderDetail" class="card">
      <div class="card-body empty-state">Không tìm thấy đơn hàng.</div>
    </div>

    <div v-else class="grid" style="gap:20px;">
      <div class="card">
        <div class="card-body">
          <div class="flex-between" style="align-items:flex-start;">
            <div>
              <h1 class="page-title" style="margin-bottom:8px; font-size:30px;">
                Chi tiết đơn hàng {{ orderStore.orderDetail.orderCode }}
              </h1>
              <p class="text-muted" style="margin:0;">
                Thời gian đặt: {{ formatDateTime(orderStore.orderDetail.placedAt) }}
              </p>
            </div>
            <span class="badge">{{ formatOrderStatus(orderStore.orderDetail.orderStatus) }}</span>
          </div>

          <div class="grid grid-2" style="margin-top:20px;">
            <div>
              <p class="text-muted" style="margin:0 0 6px;">Trạng thái thanh toán</p>
              <p style="margin:0; font-weight:700;">
                {{ formatPaymentStatus(orderStore.orderDetail.paymentStatus) }}
              </p>
            </div>
            <div>
              <p class="text-muted" style="margin:0 0 6px;">Phương thức thanh toán</p>
              <p style="margin:0; font-weight:700;">{{ orderStore.orderDetail.paymentMethod }}</p>
            </div>
            <div>
              <p class="text-muted" style="margin:0 0 6px;">Khách hàng</p>
              <p style="margin:0; font-weight:700;">{{ orderStore.orderDetail.customerName }}</p>
            </div>
            <div>
              <p class="text-muted" style="margin:0 0 6px;">Email</p>
              <p style="margin:0; font-weight:700;">{{ orderStore.orderDetail.customerEmail }}</p>
            </div>
            <div>
              <p class="text-muted" style="margin:0 0 6px;">Số điện thoại</p>
              <p style="margin:0; font-weight:700;">{{ orderStore.orderDetail.customerPhone }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-body">
          <h2 class="section-title" style="font-size:22px;">Thông tin giao hàng</h2>
          <div style="line-height:1.8;">
            <div><strong>Người nhận:</strong> {{ orderStore.orderDetail.shippingRecipientName }}</div>
            <div><strong>Số điện thoại:</strong> {{ orderStore.orderDetail.shippingRecipientPhone }}</div>
            <div>
              <strong>Địa chỉ:</strong>
              {{ orderStore.orderDetail.shippingLine1 }}
              {{ orderStore.orderDetail.shippingLine2 }}
            </div>
            <div>
              {{ orderStore.orderDetail.shippingWard }},
              {{ orderStore.orderDetail.shippingDistrict }},
              {{ orderStore.orderDetail.shippingCity }}
            </div>
            <div>
              {{ orderStore.orderDetail.shippingStateProvince }}
              {{ orderStore.orderDetail.shippingPostalCode }}
            </div>
            <div>{{ orderStore.orderDetail.shippingCountry }}</div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-body">
          <h2 class="section-title" style="font-size:22px;">Danh sách sản phẩm</h2>

          <div
            v-for="item in orderStore.orderDetail.items || []"
            :key="item.id"
            style="padding:14px 0; border-bottom:1px solid #e5e7eb;"
          >
            <div style="font-weight:700; margin-bottom:8px;">{{ item.productName }}</div>
            <div class="grid grid-2">
              <div><span class="text-muted">SKU:</span> {{ item.productSku }}</div>
              <div><span class="text-muted">Số lượng:</span> {{ item.quantity }}</div>
              <div><span class="text-muted">Đơn giá:</span> {{ formatPrice(item.unitPrice) }}</div>
              <div><span class="text-muted">Thành tiền:</span> {{ formatPrice(item.lineTotal) }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-body">
          <h2 class="section-title" style="font-size:22px;">Tóm tắt thanh toán</h2>

          <div class="flex-between" style="margin-bottom:10px;">
            <span class="text-muted">Tạm tính</span>
            <strong>{{ formatPrice(orderStore.orderDetail.subtotalAmount) }}</strong>
          </div>

          <div class="flex-between" style="margin-bottom:10px;">
            <span class="text-muted">Phí vận chuyển</span>
            <strong>{{ formatPrice(orderStore.orderDetail.shippingFee) }}</strong>
          </div>

          <div class="flex-between" style="margin-bottom:10px;">
            <span class="text-muted">Giảm giá</span>
            <strong>{{ formatPrice(orderStore.orderDetail.discountAmount) }}</strong>
          </div>

          <div class="flex-between" style="margin-bottom:10px;">
            <span class="text-muted">Thuế</span>
            <strong>{{ formatPrice(orderStore.orderDetail.taxAmount) }}</strong>
          </div>

          <div class="flex-between" style="padding-top:14px; border-top:1px solid #e5e7eb;">
            <span style="font-size:18px; font-weight:700;">Tổng cộng</span>
            <strong style="font-size:24px; color:#dc2626;">
              {{ formatPrice(orderStore.orderDetail.totalAmount) }}
            </strong>
          </div>

          <div v-if="orderStore.orderDetail.note" style="margin-top:16px;">
            <p class="text-muted" style="margin:0 0 6px;">Ghi chú</p>
            <p style="margin:0; line-height:1.7;">{{ orderStore.orderDetail.note }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onUnmounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useOrderStore } from '../../stores/order.store';
import {
  formatPrice,
  formatDateTime,
  formatOrderStatus,
  formatPaymentStatus,
} from '../../utils/format';

const route = useRoute();
const orderStore = useOrderStore();

const loadOrder = async (id) => {
  await orderStore.fetchOrderDetail(id);
};

watch(() => route.params.id, loadOrder, { immediate: true });

onUnmounted(() => {
  orderStore.clearOrderDetail();
});
</script>