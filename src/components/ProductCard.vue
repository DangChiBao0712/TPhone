<template>
  <article class="card product-card">
    <router-link :to="productLink">
      <div class="product-thumb-wrap">
        <img
          v-if="resolvedImageUrl"
          :src="resolvedImageUrl"
          :alt="productName"
          class="product-thumb"
        />
        <div v-else class="product-thumb-fallback">Không có ảnh</div>
      </div>
    </router-link>

    <div class="product-info">
      <p class="product-brand">{{ productBrand }}</p>

      <router-link :to="productLink">
        <h3 class="product-name">{{ productName }}</h3>
      </router-link>

      <p class="product-price">{{ formatCurrency(productPrice) }}</p>

      <div class="flex-between">
        <router-link :to="productLink" class="btn btn-secondary">
          Xem chi tiết
        </router-link>

        <button class="btn btn-primary" @click="handleAddToCart">
          Thêm giỏ
        </button>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useCartStore } from "../stores/cart.store";
import { resolveImageUrl } from "../utils/api";

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
});

const router = useRouter();
const cartStore = useCartStore();

const productId = computed(() => {
  return props.product?.id || props.product?.productId || props.product?.slug;
});

const productName = computed(() => {
  return props.product?.name || props.product?.productName || props.product?.title || "Sản phẩm";
});

const productBrand = computed(() => {
  return props.product?.brandName || props.product?.brand?.name || props.product?.brand || "TPhone";
});

const productPrice = computed(() => {
  return props.product?.price || props.product?.salePrice || props.product?.unitPrice || 0;
});

const rawImageUrl = computed(() => {
  return (
    props.product?.thumbnailUrl ||
    props.product?.imageUrl ||
    props.product?.thumbnail ||
    props.product?.image ||
    props.product?.productImage ||
    ""
  );
});

const resolvedImageUrl = computed(() => resolveImageUrl(rawImageUrl.value));

const productLink = computed(() => `/products/${productId.value}`);

const formatCurrency = (value) => {
  const number = Number(value || 0);
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(number);
};

const handleAddToCart = async () => {
  try {
    await cartStore.addToCart(productId.value, 1);
  } catch (error) {
    if (error?.response?.status === 401) {
      router.push("/login");
      return;
    }
    console.error("Add to cart failed:", error);
  }
};
</script>