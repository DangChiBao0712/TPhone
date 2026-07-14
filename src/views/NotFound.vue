<template>
  <div class="card">
    <div class="card-body empty-state" style="padding: 56px 20px;">
      <div style="font-size:72px; font-weight:800; color:#2563eb; line-height:1;">404</div>
      <h1 style="margin:16px 0 10px; font-size:30px;">Không tìm thấy trang</h1>
      <p class="text-muted" style="max-width:600px; margin:0 auto 20px; line-height:1.7;">
        Trang bạn đang truy cập không tồn tại hoặc đã được di chuyển.
        Vui lòng quay lại trang chủ hoặc tiếp tục mua sắm.
      </p>

      <div style="display:flex; gap:12px; justify-content:center; flex-wrap:wrap;">
        <router-link to="/" class="btn btn-primary">Về trang chủ</router-link>
        <router-link to="/products" class="btn btn-secondary">Xem sản phẩm</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>