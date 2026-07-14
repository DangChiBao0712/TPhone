<template>
  <div class="home-page">
    <section class="home-hero">
      <div class="home-hero-grid">
        <div class="card hero-main">
          <div class="hero-slider">
            <img :src="currentHeroBanner" alt="Banner" class="hero-main-image" />
          </div>

          <div class="hero-dots">
            <button
              v-for="(banner, index) in siteContent.heroBanners"
              :key="banner"
              type="button"
              class="hero-dot"
              :class="{ active: index === heroIndex }"
              @click="heroIndex = index"
            />
          </div>
        </div>

        <div class="hero-side-list">
          <div
            v-for="banner in siteContent.sideBanners.slice(0, 2)"
            :key="banner"
            class="card hero-side-card"
          >
            <img :src="banner" alt="Promo" class="hero-side-image" />
          </div>
        </div>
      </div>
    </section>

    <section class="home-section">
      <div class="section-head">
        <div>
          <h2 class="section-title">Sản phẩm nổi bật</h2>
          <p class="text-muted">
            Các mẫu đang được quan tâm nhiều tại cửa hàng TPhone
          </p>
        </div>

        <router-link to="/products" class="btn btn-secondary">
          Xem tất cả
        </router-link>
      </div>

      <div v-if="loading" class="card empty-state">Đang tải sản phẩm...</div>

      <div v-else-if="error" class="card empty-state">
        {{ error }}
      </div>

      <div v-else-if="featuredProducts.length" class="product-grid">
        <ProductCard
          v-for="item in featuredProducts"
          :key="item.id || item.productId || item.slug"
          :product="item"
        />
      </div>

      <div v-else class="card empty-state">
        Chưa có dữ liệu sản phẩm để hiển thị.
      </div>
    </section>

    <section class="home-section">
      <div class="section-head">
        <div>
          <h2 class="section-title">Gợi ý cho bạn</h2>
          <p class="text-muted">
            Danh sách sản phẩm được đề xuất theo phong cách giao diện store
          </p>
        </div>
      </div>

      <div v-if="loading" class="card empty-state">Đang tải sản phẩm gợi ý...</div>

      <div v-else-if="recommendedProducts.length" class="product-grid">
        <ProductCard
          v-for="item in recommendedProducts"
          :key="item.id || item.productId || item.slug"
          :product="item"
        />
      </div>

      <div v-else class="card empty-state">
        Chưa có sản phẩm gợi ý.
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
import ProductCard from "../components/ProductCard.vue";
import { siteContent } from "../constants/site-content";
import { useProductStore } from "../stores/product.store";

const productStore = useProductStore();

const loading = ref(false);
const error = ref("");
const heroIndex = ref(0);
let heroInterval = null;

const currentHeroBanner = computed(() => {
  return siteContent.heroBanners[heroIndex.value] || siteContent.heroBanners[0];
});

const allProducts = computed(() => productStore.products || []);
const featuredProducts = computed(() => allProducts.value.slice(0, 8));
const recommendedProducts = computed(() => allProducts.value.slice(4, 12));

const loadProducts = async () => {
  loading.value = true;
  error.value = "";
  try {
    await productStore.fetchProducts();
  } catch (err) {
    error.value =
      err?.response?.data?.message ||
      err?.response?.data?.error ||
      "Không thể tải danh sách sản phẩm.";
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  await loadProducts();

  heroInterval = setInterval(() => {
    heroIndex.value =
      (heroIndex.value + 1) % Math.max(siteContent.heroBanners.length, 1);
  }, 3500);
});

onBeforeUnmount(() => {
  if (heroInterval) clearInterval(heroInterval);
});
</script>