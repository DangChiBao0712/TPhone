<template>
  <div class="analytics-page">
    <div class="analytics-header">
      <div>
        <h1>Trung tâm thống kê</h1>
        <p>Tất cả dữ liệu phân tích được gom trong một khu vực thống kê riêng.</p>
      </div>

      <router-link to="/admin/dashboard" class="back-link">
        ← Quay lại dashboard
      </router-link>
    </div>

    <div class="filter-card">
      <div class="filter-grid">
        <div class="form-group">
          <label class="field-label">Kiểu thống kê</label>
          <select v-model="filters.period" class="select">
            <option value="day">Theo ngày</option>
            <option value="month">Theo tháng</option>
            <option value="year">Theo năm</option>
          </select>
        </div>

        <div class="form-group" v-if="filters.period === 'day'">
          <label class="field-label">Chọn ngày</label>
          <input v-model="filters.date" type="date" class="input" />
        </div>

        <div class="form-group" v-if="filters.period === 'month'">
          <label class="field-label">Chọn tháng</label>
          <input v-model="filters.month" type="month" class="input" />
        </div>

        <div class="form-group" v-if="filters.period === 'year'">
          <label class="field-label">Chọn năm</label>
          <input v-model="filters.year" type="number" min="2020" max="2100" class="input" />
        </div>

        <div class="form-group action-group">
          <button class="btn-primary" :disabled="loading" @click="fetchAnalytics">
            {{ loading ? 'Đang tải...' : 'Xem thống kê' }}
          </button>
        </div>
      </div>

      <div class="period-chip">
        <span>Kỳ đang xem:</span>
        <strong>{{ analytics.label || '---' }}</strong>
      </div>
    </div>

    <AppAlert type="error" :message="error" />

    <div v-if="loading" class="loading-card">
      Đang tải dữ liệu thống kê...
    </div>

    <template v-else>
      <div class="stats-grid">
        <div class="stat-card">
          <p class="stat-label">Doanh thu trong kỳ</p>
          <p class="stat-value text-green">{{ formatPrice(analytics.revenueInPeriod) }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Giá trị đơn trung bình</p>
          <p class="stat-value">{{ formatPrice(analytics.averageOrderValue) }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Tổng đơn trong kỳ</p>
          <p class="stat-value">{{ analytics.totalOrdersInPeriod || 0 }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Đơn hoàn tất</p>
          <p class="stat-value">{{ analytics.completedOrdersInPeriod || 0 }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Đơn chờ xử lý</p>
          <p class="stat-value">{{ analytics.pendingOrdersInPeriod || 0 }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Đơn đã hủy</p>
          <p class="stat-value text-red">{{ analytics.cancelledOrdersInPeriod || 0 }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Tổng khách hàng</p>
          <p class="stat-value">{{ analytics.totalUsers || 0 }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Khách mới trong kỳ</p>
          <p class="stat-value">{{ analytics.newUsersInPeriod || 0 }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Khách phát sinh đơn</p>
          <p class="stat-value">{{ analytics.activeCustomersInPeriod || 0 }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Tổng sản phẩm</p>
          <p class="stat-value">{{ analytics.totalProducts || 0 }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Sản phẩm sắp hết hàng</p>
          <p class="stat-value text-orange">{{ analytics.lowStockProducts || 0 }}</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Khoảng thời gian</p>
          <p class="stat-subvalue">{{ analytics.fromDate || '---' }} → {{ analytics.toDate || '---' }}</p>
        </div>
      </div>

      <AnalyticsBarChart
        title="Biểu đồ doanh thu"
        :subtitle="`Hiển thị doanh thu theo ${periodLabel}`"
        :points="analytics.revenueSeries"
      />

      <div class="content-grid">
        <div class="panel">
          <div class="panel-header">
            <h3>Trạng thái đơn hàng</h3>
            <p>Tổng hợp số lượng đơn theo trạng thái</p>
          </div>

          <div class="metric-list">
            <div v-for="item in analytics.orderStatusSummary" :key="item.name" class="metric-row">
              <span>{{ formatOrderStatus(item.name) }}</span>
              <strong>{{ item.value || 0 }}</strong>
            </div>
          </div>
        </div>

        <div class="panel">
          <div class="panel-header">
            <h3>Trạng thái thanh toán</h3>
            <p>Tổng hợp số lượng thanh toán</p>
          </div>

          <div class="metric-list">
            <div v-for="item in analytics.paymentStatusSummary" :key="item.name" class="metric-row">
              <span>{{ formatPaymentStatus(item.name) }}</span>
              <strong>{{ item.value || 0 }}</strong>
            </div>
          </div>
        </div>

        <div class="panel">
          <div class="panel-header">
            <h3>Top khách hàng mua nhiều</h3>
            <p>Xếp hạng theo số đơn</p>
          </div>

          <div v-if="analytics.topCustomersByOrders?.length" class="ranking-list">
            <div v-for="(item, index) in analytics.topCustomersByOrders" :key="item.accountId" class="ranking-row">
              <div>
                <p class="ranking-title">#{{ index + 1 }} - {{ item.fullName }}</p>
                <p class="ranking-sub">{{ item.email }} - {{ item.phone || 'Chưa có SĐT' }}</p>
              </div>
              <div class="ranking-right">
                <strong>{{ item.totalOrders || 0 }} đơn</strong>
                <span>{{ formatPrice(item.totalSpent) }}</span>
              </div>
            </div>
          </div>

          <div v-else class="empty-text">Chưa có dữ liệu.</div>
        </div>

        <div class="panel">
          <div class="panel-header">
            <h3>Top khách hàng chi tiêu cao</h3>
            <p>Xếp hạng theo doanh thu</p>
          </div>

          <div v-if="analytics.topCustomersByRevenue?.length" class="ranking-list">
            <div v-for="(item, index) in analytics.topCustomersByRevenue" :key="item.accountId" class="ranking-row">
              <div>
                <p class="ranking-title">#{{ index + 1 }} - {{ item.fullName }}</p>
                <p class="ranking-sub">{{ item.email }} - {{ item.phone || 'Chưa có SĐT' }}</p>
              </div>
              <div class="ranking-right">
                <strong>{{ formatPrice(item.totalSpent) }}</strong>
                <span>{{ item.totalOrders || 0 }} đơn</span>
              </div>
            </div>
          </div>

          <div v-else class="empty-text">Chưa có dữ liệu.</div>
        </div>

        <div class="panel">
          <div class="panel-header">
            <h3>Top sản phẩm bán chạy</h3>
            <p>Theo số lượng</p>
          </div>

          <div v-if="analytics.topProductsByQuantity?.length" class="ranking-list">
            <div v-for="(item, index) in analytics.topProductsByQuantity" :key="item.id" class="ranking-row">
              <div>
                <p class="ranking-title">#{{ index + 1 }} - {{ item.name }}</p>
                <p class="ranking-sub">Doanh thu: {{ formatPrice(item.totalRevenue) }}</p>
              </div>
              <div class="ranking-right">
                <strong>{{ item.totalQuantity || 0 }}</strong>
                <span>sản phẩm</span>
              </div>
            </div>
          </div>

          <div v-else class="empty-text">Chưa có dữ liệu.</div>
        </div>

        <div class="panel">
          <div class="panel-header">
            <h3>Top sản phẩm theo doanh thu</h3>
            <p>Theo tổng tiền bán ra</p>
          </div>

          <div v-if="analytics.topProductsByRevenue?.length" class="ranking-list">
            <div v-for="(item, index) in analytics.topProductsByRevenue" :key="item.id" class="ranking-row">
              <div>
                <p class="ranking-title">#{{ index + 1 }} - {{ item.name }}</p>
                <p class="ranking-sub">Số lượng: {{ item.totalQuantity || 0 }}</p>
              </div>
              <div class="ranking-right">
                <strong>{{ formatPrice(item.totalRevenue) }}</strong>
              </div>
            </div>
          </div>

          <div v-else class="empty-text">Chưa có dữ liệu.</div>
        </div>

        <div class="panel">
          <div class="panel-header">
            <h3>Top hãng bán chạy</h3>
            <p>Theo số lượng</p>
          </div>

          <div v-if="analytics.topBrandsByQuantity?.length" class="ranking-list">
            <div v-for="(item, index) in analytics.topBrandsByQuantity" :key="item.id" class="ranking-row">
              <div>
                <p class="ranking-title">#{{ index + 1 }} - {{ item.name }}</p>
                <p class="ranking-sub">Doanh thu: {{ formatPrice(item.totalRevenue) }}</p>
              </div>
              <div class="ranking-right">
                <strong>{{ item.totalQuantity || 0 }}</strong>
                <span>sản phẩm</span>
              </div>
            </div>
          </div>

          <div v-else class="empty-text">Chưa có dữ liệu.</div>
        </div>

        <div class="panel">
          <div class="panel-header">
            <h3>Top hãng theo doanh thu</h3>
            <p>Theo tổng tiền bán ra</p>
          </div>

          <div v-if="analytics.topBrandsByRevenue?.length" class="ranking-list">
            <div v-for="(item, index) in analytics.topBrandsByRevenue" :key="item.id" class="ranking-row">
              <div>
                <p class="ranking-title">#{{ index + 1 }} - {{ item.name }}</p>
                <p class="ranking-sub">Số lượng: {{ item.totalQuantity || 0 }}</p>
              </div>
              <div class="ranking-right">
                <strong>{{ formatPrice(item.totalRevenue) }}</strong>
              </div>
            </div>
          </div>

          <div v-else class="empty-text">Chưa có dữ liệu.</div>
        </div>

        <div class="panel">
          <div class="panel-header">
            <h3>Top danh mục bán chạy</h3>
            <p>Theo số lượng</p>
          </div>

          <div v-if="analytics.topCategoriesByQuantity?.length" class="ranking-list">
            <div v-for="(item, index) in analytics.topCategoriesByQuantity" :key="item.id" class="ranking-row">
              <div>
                <p class="ranking-title">#{{ index + 1 }} - {{ item.name }}</p>
                <p class="ranking-sub">Doanh thu: {{ formatPrice(item.totalRevenue) }}</p>
              </div>
              <div class="ranking-right">
                <strong>{{ item.totalQuantity || 0 }}</strong>
                <span>sản phẩm</span>
              </div>
            </div>
          </div>

          <div v-else class="empty-text">Chưa có dữ liệu.</div>
        </div>

        <div class="panel">
          <div class="panel-header">
            <h3>Top danh mục theo doanh thu</h3>
            <p>Theo tổng tiền bán ra</p>
          </div>

          <div v-if="analytics.topCategoriesByRevenue?.length" class="ranking-list">
            <div v-for="(item, index) in analytics.topCategoriesByRevenue" :key="item.id" class="ranking-row">
              <div>
                <p class="ranking-title">#{{ index + 1 }} - {{ item.name }}</p>
                <p class="ranking-sub">Số lượng: {{ item.totalQuantity || 0 }}</p>
              </div>
              <div class="ranking-right">
                <strong>{{ formatPrice(item.totalRevenue) }}</strong>
              </div>
            </div>
          </div>

          <div v-else class="empty-text">Chưa có dữ liệu.</div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import api from '../../api/axios';
import AppAlert from '../../components/AppAlert.vue';
import AnalyticsBarChart from '../../components/admin/AnalyticsBarChart.vue';
import { extractErrorMessage } from '../../utils/error';
import { formatOrderStatus, formatPaymentStatus, formatPrice } from '../../utils/format';

const today = new Date();
const yyyy = today.getFullYear();
const mm = String(today.getMonth() + 1).padStart(2, '0');
const dd = String(today.getDate()).padStart(2, '0');

const loading = ref(false);
const error = ref('');

const filters = reactive({
  period: 'day',
  date: `${yyyy}-${mm}-${dd}`,
  month: `${yyyy}-${mm}`,
  year: String(yyyy),
});

const analytics = reactive({
  label: '',
  fromDate: '',
  toDate: '',
  revenueInPeriod: 0,
  averageOrderValue: 0,
  totalOrdersInPeriod: 0,
  completedOrdersInPeriod: 0,
  pendingOrdersInPeriod: 0,
  cancelledOrdersInPeriod: 0,
  totalUsers: 0,
  newUsersInPeriod: 0,
  activeCustomersInPeriod: 0,
  totalProducts: 0,
  lowStockProducts: 0,
  orderStatusSummary: [],
  paymentStatusSummary: [],
  revenueSeries: [],
  topCustomersByOrders: [],
  topCustomersByRevenue: [],
  topProductsByQuantity: [],
  topProductsByRevenue: [],
  topBrandsByQuantity: [],
  topBrandsByRevenue: [],
  topCategoriesByQuantity: [],
  topCategoriesByRevenue: [],
});

const periodLabel = computed(() => {
  if (filters.period === 'month') return 'ngày trong tháng';
  if (filters.period === 'year') return 'tháng trong năm';
  return 'giờ trong ngày';
});

const resetAnalytics = () => {
  Object.assign(analytics, {
    label: '',
    fromDate: '',
    toDate: '',
    revenueInPeriod: 0,
    averageOrderValue: 0,
    totalOrdersInPeriod: 0,
    completedOrdersInPeriod: 0,
    pendingOrdersInPeriod: 0,
    cancelledOrdersInPeriod: 0,
    totalUsers: 0,
    newUsersInPeriod: 0,
    activeCustomersInPeriod: 0,
    totalProducts: 0,
    lowStockProducts: 0,
    orderStatusSummary: [],
    paymentStatusSummary: [],
    revenueSeries: [],
    topCustomersByOrders: [],
    topCustomersByRevenue: [],
    topProductsByQuantity: [],
    topProductsByRevenue: [],
    topBrandsByQuantity: [],
    topBrandsByRevenue: [],
    topCategoriesByQuantity: [],
    topCategoriesByRevenue: [],
  });
};

const buildDateParam = () => {
  if (filters.period === 'month') return `${filters.month}-01`;
  if (filters.period === 'year') return `${filters.year}-01-01`;
  return filters.date;
};

const fetchAnalytics = async () => {
  loading.value = true;
  error.value = '';

  try {
    const res = await api.get('/admin/dashboard/analytics', {
      params: {
        period: filters.period,
        date: buildDateParam(),
      },
    });

    resetAnalytics();
    Object.assign(analytics, res.data || {});
  } catch (err) {
    error.value = extractErrorMessage(err, 'Không thể tải dữ liệu thống kê.');
  } finally {
    loading.value = false;
  }
};

onMounted(fetchAnalytics);
</script>

<style scoped>
.analytics-page {
  padding: 18px 0 20px;
}

.analytics-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 18px;
  margin-bottom: 24px;
  padding-bottom: 18px;
  border-bottom: 1px solid #d9dee7;
}

.analytics-header h1 {
  margin: 0 0 8px;
  font-size: 34px;
  font-weight: 800;
  color: #111827;
}

.analytics-header p {
  margin: 0;
  color: #6b7280;
  font-size: 16px;
}

.back-link {
  text-decoration: none;
  padding: 10px 14px;
  border: 1px solid #dbe3ee;
  border-radius: 12px;
  background: #fff;
  color: #2563eb;
  font-weight: 700;
}

.filter-card,
.loading-card,
.panel,
.stat-card {
  background: #fff;
  border: 1px solid #edf0f4;
  border-radius: 18px;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.05);
}

.filter-card {
  padding: 20px;
  margin-bottom: 22px;
}

.filter-grid {
  display: grid;
  grid-template-columns: 220px 220px 220px 1fr;
  gap: 16px;
  align-items: end;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  font-size: 14px;
  font-weight: 700;
  color: #374151;
}

.input,
.select {
  height: 46px;
  border-radius: 12px;
  border: 1px solid #d8e0ea;
  padding: 0 14px;
  font-size: 15px;
  outline: none;
}

.action-group {
  justify-content: end;
  align-items: flex-end;
}

.btn-primary {
  height: 46px;
  min-width: 160px;
  border: none;
  border-radius: 12px;
  background: #2563eb;
  color: #fff;
  font-weight: 800;
  cursor: pointer;
  padding: 0 18px;
}

.period-chip {
  margin-top: 16px;
  display: inline-flex;
  gap: 10px;
  align-items: center;
  padding: 10px 14px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  border: 1px solid #bfdbfe;
}

.loading-card {
  padding: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 22px;
}

.stat-card {
  padding: 18px;
}

.stat-label {
  margin: 0 0 10px;
  color: #6b7280;
  font-size: 14px;
  font-weight: 600;
}

.stat-value {
  margin: 0;
  font-size: 26px;
  font-weight: 800;
  color: #111827;
}

.stat-subvalue {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #111827;
  line-height: 1.5;
}

.text-green { color: #16a34a; }
.text-red { color: #dc2626; }
.text-orange { color: #ea580c; }

.content-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-top: 18px;
}

.panel {
  padding: 20px;
}

.panel-header {
  margin-bottom: 16px;
}

.panel-header h3 {
  margin: 0 0 6px;
  font-size: 22px;
  color: #111827;
}

.panel-header p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.metric-list,
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.metric-row,
.ranking-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 14px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid #edf2f7;
}

.ranking-title {
  margin: 0 0 4px;
  font-weight: 800;
  color: #111827;
}

.ranking-sub {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
}

.ranking-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  gap: 4px;
  white-space: nowrap;
}

.empty-text {
  color: #6b7280;
  font-size: 14px;
}

@media (max-width: 1200px) {
  .filter-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .stats-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .analytics-header {
    flex-direction: column;
  }

  .stats-grid,
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .filter-grid {
    grid-template-columns: 1fr;
  }

  .analytics-header h1 {
    font-size: 28px;
  }

  .btn-primary {
    width: 100%;
  }
}
</style>