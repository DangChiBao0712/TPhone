<template>
  <div class="admin-dashboard-page">
    <div class="admin-dashboard-header">
      <h1>Bảng điều khiển Admin</h1>
      <p>Truy cập nhanh các chức năng quản trị hệ thống.</p>
    </div>

    <div class="admin-dashboard-grid">
      <router-link to="/admin/products" class="dashboard-card card-blue">
        <div class="dashboard-card-icon">📦</div>
        <h3>QUẢN LÝ SẢN PHẨM</h3>
        <p>Thêm, sửa, xóa sản phẩm và cập nhật tồn kho.</p>
        <span class="dashboard-card-btn">Truy cập</span>
      </router-link>

      <router-link to="/admin/customers" class="dashboard-card card-yellow">
        <div class="dashboard-card-icon">👥</div>
        <h3>QUẢN LÝ KHÁCH HÀNG</h3>
        <p>Quản lý thông tin khách hàng, trạng thái tài khoản.</p>
        <span class="dashboard-card-btn">Truy cập</span>
      </router-link>

      <router-link to="/admin/orders" class="dashboard-card card-cyan">
        <div class="dashboard-card-icon">🧾</div>
        <h3>QUẢN LÝ ĐƠN HÀNG</h3>
        <p>Xem chi tiết, lọc đơn hàng và cập nhật trạng thái.</p>
        <span class="dashboard-card-btn">Truy cập</span>
      </router-link>

      <router-link to="/admin/categories" class="dashboard-card card-gray">
        <div class="dashboard-card-icon">🏷️</div>
        <h3>QUẢN LÝ DANH MỤC</h3>
        <p>Quản lý danh mục sản phẩm trong hệ thống.</p>
        <span class="dashboard-card-btn">Truy cập</span>
      </router-link>

      <router-link to="/admin/brands" class="dashboard-card card-dark">
        <div class="dashboard-card-icon">🏢</div>
        <h3>QUẢN LÝ HÃNG</h3>
        <p>Quản lý thương hiệu và thông tin hãng.</p>
        <span class="dashboard-card-btn">Truy cập</span>
      </router-link>

      <router-link to="/admin/analytics" class="dashboard-card card-green">
        <div class="dashboard-card-icon">📊</div>
        <h3>THỐNG KÊ</h3>
        <p>Tất cả dữ liệu doanh thu, khách hàng, sản phẩm, hãng, danh mục và biểu đồ nằm trong đây.</p>
        <span class="dashboard-card-btn">Xem thống kê</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.admin-dashboard-page {
  padding: 18px 0 8px;
}

.admin-dashboard-header {
  margin-bottom: 28px;
  padding-bottom: 18px;
  border-bottom: 1px solid #d9dee7;
}

.admin-dashboard-header h1 {
  margin: 0 0 10px;
  font-size: 34px;
  line-height: 1.15;
  font-weight: 800;
  color: #111827;
}

.admin-dashboard-header p {
  margin: 0;
  font-size: 17px;
  color: #6b7280;
}

.admin-dashboard-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
}

.dashboard-card {
  min-height: 230px;
  background: #fff;
  border-radius: 18px;
  padding: 28px 24px;
  text-decoration: none;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
  border: 1px solid #edf0f4;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.dashboard-card-icon {
  font-size: 54px;
  margin-bottom: 18px;
}

.dashboard-card h3 {
  margin: 0 0 12px;
  font-size: 21px;
  font-weight: 800;
  color: #111827;
}

.dashboard-card p {
  margin: 0 0 22px;
  color: #6b7280;
  font-size: 16px;
  line-height: 1.6;
}

.dashboard-card-btn {
  margin-top: auto;
  width: 100%;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  height: 48px;
  border-radius: 12px;
  font-weight: 700;
  border: 1.5px solid currentColor;
}

.card-blue { color: #2563eb; }
.card-yellow { color: #f59e0b; }
.card-green { color: #16a34a; }
.card-cyan { color: #06b6d4; }
.card-gray { color: #6b7280; }
.card-dark { color: #374151; }

@media (max-width: 1100px) {
  .admin-dashboard-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 700px) {
  .admin-dashboard-grid {
    grid-template-columns: 1fr;
  }

  .admin-dashboard-header h1 {
    font-size: 28px;
  }
}
</style>