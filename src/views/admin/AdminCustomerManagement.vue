<template>
  <div>
    <h1 class="page-title">Quản lý khách hàng</h1>

    <div class="card" style="margin-bottom: 24px;">
      <div class="card-body">
        <div class="grid grid-3">
          <div class="form-group">
            <label class="field-label">Từ khóa</label>
            <input
              v-model="filters.keyword"
              class="input"
              placeholder="Tên, email, số điện thoại..."
              @keyup.enter="fetchCustomers"
            />
          </div>

          <div class="form-group">
            <label class="field-label">Trạng thái</label>
            <select v-model="filters.status" class="select">
              <option value="">Tất cả</option>
              <option value="ACTIVE">Đang hoạt động</option>
              <option value="LOCKED">Đã khóa</option>
              <option value="INACTIVE">Ngưng hoạt động</option>
            </select>
          </div>

          <div class="form-group" style="display:flex; align-items:flex-end;">
            <button class="btn btn-primary" :disabled="loading" @click="fetchCustomers">
              {{ loading ? 'Đang tải...' : 'Tìm kiếm' }}
            </button>
          </div>
        </div>

        <AppAlert type="success" :message="message" />
        <AppAlert type="error" :message="error" />
      </div>
    </div>

    <div v-if="loading" class="card">
      <div class="card-body">Đang tải danh sách khách hàng...</div>
    </div>

    <div v-else-if="customers.length === 0" class="card">
      <div class="card-body">Không có khách hàng phù hợp.</div>
    </div>

    <div v-else class="grid">
      <div v-for="customer in customers" :key="customer.id" class="card">
        <div class="card-body">
          <div class="flex-between" style="align-items:flex-start; gap:16px;">
            <div style="flex:1;">
              <h2 style="margin:0 0 8px; font-size:22px;">{{ customer.fullName }}</h2>
              <p class="text-muted" style="margin:0;">{{ customer.email }}</p>
              <p class="text-muted" style="margin:6px 0 0;">{{ customer.phone || 'Chưa cập nhật số điện thoại' }}</p>
            </div>

            <span class="badge">
              {{ formatAccountStatus(customer.status) }}
            </span>
          </div>

          <div class="grid grid-2" style="margin-top: 16px;">
            <div>
              <p class="text-muted" style="margin:0 0 6px;">Vai trò</p>
              <p style="margin:0; font-weight:700;">{{ formatRoles(customer.roles) }}</p>
            </div>

            <div>
              <p class="text-muted" style="margin:0 0 6px;">Provider</p>
              <p style="margin:0; font-weight:700;">{{ customer.provider || 'LOCAL' }}</p>
            </div>

            <div>
              <p class="text-muted" style="margin:0 0 6px;">Tổng đơn hàng</p>
              <p style="margin:0; font-weight:700;">{{ customer.totalOrders || 0 }}</p>
            </div>

            <div>
              <p class="text-muted" style="margin:0 0 6px;">Tổng chi tiêu</p>
              <p style="margin:0; font-weight:700; color:#dc2626;">
                {{ formatPrice(customer.totalSpent || 0) }}
              </p>
            </div>

            <div>
              <p class="text-muted" style="margin:0 0 6px;">Đăng nhập gần nhất</p>
              <p style="margin:0; font-weight:700;">
                {{ customer.lastLoginAt ? formatDateTime(customer.lastLoginAt) : 'Chưa có' }}
              </p>
            </div>

            <div>
              <p class="text-muted" style="margin:0 0 6px;">Ngày tạo</p>
              <p style="margin:0; font-weight:700;">{{ formatDateTime(customer.createdAt) }}</p>
            </div>
          </div>

          <div style="display:flex; gap:10px; flex-wrap:wrap; margin-top:18px;">
            <button
              class="btn btn-primary"
              :disabled="updatingId === customer.id || customer.status === 'ACTIVE'"
              @click="updateStatus(customer.id, 'ACTIVE')"
            >
              {{ updatingId === customer.id ? 'Đang xử lý...' : 'Kích hoạt' }}
            </button>

            <button
              class="btn btn-secondary"
              :disabled="updatingId === customer.id || customer.status === 'LOCKED'"
              @click="updateStatus(customer.id, 'LOCKED')"
            >
              Khóa tài khoản
            </button>

            <button
              class="btn"
              style="background:#f3f4f6; color:#111827;"
              :disabled="updatingId === customer.id || customer.status === 'INACTIVE'"
              @click="updateStatus(customer.id, 'INACTIVE')"
            >
              Ngưng hoạt động
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
  formatAccountStatus,
  formatDateTime,
  formatPrice,
  formatRole,
} from '../../utils/format';

const loading = ref(false);
const updatingId = ref(null);
const customers = ref([]);
const message = ref('');
const error = ref('');

const filters = reactive({
  keyword: '',
  status: '',
});

const formatRoles = (roles) => {
  if (!roles || !roles.length) return 'Chưa có';
  return roles.map(formatRole).join(', ');
};

const fetchCustomers = async () => {
  loading.value = true;
  error.value = '';
  message.value = '';

  try {
    const res = await api.get('/admin/accounts', {
      params: {
        keyword: filters.keyword || undefined,
        status: filters.status || undefined,
      },
    });

    customers.value = res.data?.data || [];
  } catch (err) {
    error.value = extractErrorMessage(err, 'Không thể tải danh sách khách hàng.');
  } finally {
    loading.value = false;
  }
};

const updateStatus = async (accountId, status) => {
  updatingId.value = accountId;
  error.value = '';
  message.value = '';

  try {
    const res = await api.put(`/admin/accounts/${accountId}/status`, { status });
    const updated = res.data?.data;

    customers.value = customers.value.map((item) =>
      item.id === accountId ? updated : item
    );

    message.value = 'Cập nhật trạng thái khách hàng thành công.';
  } catch (err) {
    error.value = extractErrorMessage(err, 'Không thể cập nhật trạng thái khách hàng.');
  } finally {
    updatingId.value = null;
  }
};

onMounted(fetchCustomers);
</script>