<template>
  <div>
    <h1 class="page-title">Giỏ hàng của tôi</h1>

    <div v-if="cartStore.loading" class="card">
      <div class="card-body">Đang tải giỏ hàng...</div>
    </div>

    <div v-else-if="cartStore.cart.items.length === 0" class="card">
      <div class="card-body empty-state">
        <p style="margin:0 0 12px;">Giỏ hàng của bạn đang trống.</p>
        <router-link to="/products" class="btn btn-primary">Tiếp tục mua sắm</router-link>
      </div>
    </div>

    <div v-else class="grid" style="gap: 20px;">
      <div class="card">
        <div class="card-body">
          <div
            v-for="item in cartStore.cart.items"
            :key="item.id"
            style="display:flex; gap:16px; padding:16px 0; border-bottom:1px solid #e5e7eb; align-items:center; flex-wrap:wrap;"
          >
            <img
              :src="resolveImageUrl(item.thumbnailUrl)"
              alt="thumb"
              style="width:96px; height:96px; object-fit:cover; border-radius:14px; background:#f3f4f6;"
            />

            <div style="flex:1; min-width:240px;">
              <p style="margin:0 0 8px; font-size:18px; font-weight:700;">{{ item.productName }}</p>
              <p class="text-muted" style="margin:0 0 6px;">Đơn giá: {{ formatPrice(item.unitPrice) }}</p>
              <p class="text-muted" style="margin:0 0 6px;">Số lượng: {{ item.quantity }}</p>
              <p style="margin:0; font-weight:700; color:#dc2626;">
                Thành tiền: {{ formatPrice(item.lineTotal) }}
              </p>
            </div>

            <div style="display:flex; gap:8px; flex-wrap:wrap;">
              <button class="btn btn-secondary" @click="decrease(item)">-</button>
              <button class="btn btn-secondary" @click="increase(item)">+</button>
              <button class="btn btn-danger" @click="remove(item.id)">Xóa</button>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-body">
          <h2 class="section-title" style="font-size:22px;">Tóm tắt giỏ hàng</h2>
          <div class="flex-between" style="margin-bottom: 14px;">
            <span class="text-muted">Tổng số lượng</span>
            <strong>{{ cartStore.cart.totalItems || 0 }}</strong>
          </div>
          <div class="flex-between" style="margin-bottom: 20px;">
            <span class="text-muted">Tổng cộng</span>
            <strong style="font-size:22px; color:#dc2626;">
              {{ formatPrice(cartStore.cart.totalAmount || 0) }}
            </strong>
          </div>

          <div style="display:flex; gap:12px; flex-wrap:wrap;">
            <button class="btn btn-danger" @click="cartStore.clearCart">Xóa toàn bộ</button>
            <router-link to="/checkout" class="btn btn-primary">Tiến hành đặt hàng</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useCartStore } from '../../stores/cart.store';
import { resolveImageUrl } from '../../utils/api';

const cartStore = useCartStore();

onMounted(async () => {
  await cartStore.fetchCart();
});

const increase = async (item) => {
  await cartStore.updateCartItem(item.id, item.quantity + 1);
};

const decrease = async (item) => {
  if (item.quantity > 1) {
    await cartStore.updateCartItem(item.id, item.quantity - 1);
  }
};

const remove = async (cartItemId) => {
  await cartStore.removeCartItem(cartItemId);
};

const formatPrice = (value) => {
  return new Intl.NumberFormat('vi-VN').format(value || 0) + ' đ';
};
</script>