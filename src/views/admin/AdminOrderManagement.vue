<template>
  <div>
    <h1 class="page-title">Quản lý đơn hàng</h1>

    <div class="card" style="margin-bottom: 24px;">
      <div class="card-body">
        <div class="grid grid-3">
          <div class="form-group">
            <label class="field-label">Từ khóa</label>
            <input
              v-model="filters.keyword"
              class="input"
              placeholder="Mã đơn, tên, email, số điện thoại..."
              @keyup.enter="fetchOrders"
            />
          </div>

          <div class="form-group">
            <label class="field-label">Trạng thái đơn</label>
            <select v-model="filters.orderStatus" class="select">
              <option value="">Tất cả</option>
              <option value="PENDING">Chờ xử lý</option>
              <option value="CONFIRMED">Đã xác nhận</option>
              <option value="PROCESSING">Đang xử lý</option>
              <option value="SHIPPING">Đang giao</option>
              <option value="COMPLETED">Hoàn tất</option>
              <option value="CANCELLED">Đã hủy</option>
              <option value="REFUNDED">Hoàn đơn</option>
            </select>
          </div>

          <div class="form-group">
            <label class="field-label">Trạng thái thanh toán</label>
            <select v-model="filters.paymentStatus" class="select">
              <option value="">Tất cả</option>
              <option value="PENDING">Chờ thanh toán</option>
              <option value="SUCCESS">Đã thanh toán</option>
              <option value="FAILED">Thất bại</option>
              <option value="CANCELLED">Đã hủy</option>
              <option value="REFUNDED">Đã hoàn tiền</option>
            </select>
          </div>
        </div>

        <div style="display:flex; gap:10px; margin-top:14px;">
          <button class="btn btn-primary" :disabled="loading" @click="fetchOrders">
            {{ loading ? 'Đang tải...' : 'Lọc đơn hàng' }}
          </button>
        </div>

        <AppAlert type="success" :message="message" />
        <AppAlert type="error" :message="error" />
      </div>
    </div>

    <div v-if="loading" class="card">
      <div class="card-body">Đang tải danh sách đơn hàng...</div>
    </div>

    <div v-else-if="orders.length === 0" class="card">
      <div class="card-body">Không có đơn hàng phù hợp.</div>
    </div>

    <div v-else class="grid">
      <div v-for="order in orders" :key="order.id" class="card">
        <div class="card-body">
          <div class="flex-between" style="align-items:flex-start; gap:16px;">
            <div>
              <h2 style="margin:0 0 8px; font-size:22px;">{{ order.orderCode }}</h2>
              <p class="text-muted" style="margin:0;">
                {{ order.customerName }} - {{ order.customerPhone }}
              </p>
              <p class="text-muted" style="margin:6px 0 0;">
                {{ order.customerEmail }}
              </p>
            </div>

            <div style="text-align:right;">
              <p class="badge" style="margin:0 0 8px;">
                {{ formatOrderStatus(order.orderStatus) }}
              </p>
              <p class="text-muted" style="margin:0;">
                {{ formatDateTime(order.placedAt) }}
              </p>
            </div>
          </div>

          <div class="grid grid-2" style="margin-top:16px;">
            <div>
              <p class="text-muted" style="margin:0 0 6px;">Thanh toán</p>
              <p style="margin:0; font-weight:700;">
                {{ formatPaymentStatus(order.paymentStatus) }}
              </p>
            </div>

            <div>
              <p class="text-muted" style="margin:0 0 6px;">Tổng tiền</p>
              <p style="margin:0; font-weight:700; color:#dc2626;">
                {{ formatPrice(order.totalAmount) }}
              </p>
            </div>
          </div>

          <div v-if="order.items?.length" style="margin-top:16px;">
            <p style="margin:0 0 8px; font-weight:700;">Sản phẩm</p>
            <ul style="margin:0; padding-left:18px; line-height:1.8;">
              <li v-for="item in order.items" :key="item.id">
                {{ item.productName }} - {{ item.quantity }} x {{ formatPrice(item.unitPrice) }}
              </li>
            </ul>
          </div>

          <div class="grid grid-2" style="margin-top:16px;">
            <div class="form-group">
              <label class="field-label">Cập nhật trạng thái đơn</label>
              <select v-model="editMap[order.id].orderStatus" class="select">
                <option value="PENDING">Chờ xử lý</option>
                <option value="CONFIRMED">Đã xác nhận</option>
                <option value="PROCESSING">Đang xử lý</option>
                <option value="SHIPPING">Đang giao</option>
                <option value="COMPLETED">Hoàn tất</option>
                <option value="CANCELLED">Đã hủy</option>
                <option value="REFUNDED">Hoàn đơn</option>
              </select>
            </div>

            <div class="form-group">
              <label class="field-label">Cập nhật thanh toán</label>
              <select v-model="editMap[order.id].paymentStatus" class="select">
                <option value="PENDING">Chờ thanh toán</option>
                <option value="SUCCESS">Đã thanh toán</option>
                <option value="FAILED">Thất bại</option>
                <option value="CANCELLED">Đã hủy</option>
                <option value="REFUNDED">Đã hoàn tiền</option>
              </select>
            </div>
          </div>

          <div class="form-group" style="margin-top:10px;">
            <label class="field-label">Ghi chú nội bộ</label>
            <textarea
              v-model="editMap[order.id].internalNote"
              class="textarea"
              placeholder="Ghi chú nội bộ cho admin..."
            />
          </div>

          <div style="margin-top:16px;">
            <button
              class="btn btn-primary"
              :disabled="savingId === order.id"
              @click="saveOrder(order.id)"
            >
              {{ savingId === order.id ? 'Đang lưu...' : 'Lưu thay đổi' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import api from '../../api/axios';
import AppAlert from '../../components/AppAlert.vue';
import { extractErrorMessage } from '../../utils/error';
import {
  formatDateTime,
  formatOrderStatus,
  formatPaymentStatus,
  formatPrice,
} from '../../utils/format';

const loading = ref(false);
const savingId = ref(null);
const orders = ref([]);
const message = ref('');
const error = ref('');

const filters = reactive({
  keyword: '',
  orderStatus: '',
  paymentStatus: '',
});

const editMap = reactive({});

const buildEditMap = (items) => {
  Object.keys(editMap).forEach((key) => delete editMap[key]);

  items.forEach((order) => {
    editMap[order.id] = {
      orderStatus: order.orderStatus || 'PENDING',
      paymentStatus: order.paymentStatus || 'PENDING',
      internalNote: order.internalNote || '',
    };
  });
};

const fetchOrders = async () => {
  loading.value = true;
  error.value = '';
  message.value = '';

  try {
    const res = await api.get('/admin/orders', {
      params: {
        keyword: filters.keyword || undefined,
        orderStatus: filters.orderStatus || undefined,
        paymentStatus: filters.paymentStatus || undefined,
      },
    });

    orders.value = res.data || [];
    buildEditMap(orders.value);
  } catch (err) {
    error.value = extractErrorMessage(err, 'Không thể tải danh sách đơn hàng.');
  } finally {
    loading.value = false;
  }
};

const saveOrder = async (orderId) => {
  savingId.value = orderId;
  error.value = '';
  message.value = '';

  try {
    const payload = {
      orderStatus: editMap[orderId].orderStatus,
      paymentStatus: editMap[orderId].paymentStatus,
      internalNote: editMap[orderId].internalNote,
    };

    const res = await api.put(`/admin/orders/${orderId}/status`, payload);
    const updated = res.data;

    orders.value = orders.value.map((item) => (item.id === orderId ? updated : item));
    buildEditMap(orders.value);

    message.value = 'Cập nhật đơn hàng thành công.';
  } catch (err) {
    error.value = extractErrorMessage(err, 'Không thể cập nhật đơn hàng.');
  } finally {
    savingId.value = null;
  }
};

onMounted(fetchOrders);
</script>