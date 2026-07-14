<template>
  <div v-if="product" class="product-detail-page">
    <div class="detail-container">
      <div class="product-detail-card">
        <div class="product-detail-grid">
          <!-- LEFT: GALLERY -->
          <section class="product-detail-gallery">
            <div class="product-detail-image-box">
              <img
                v-if="imageUrl"
                :src="imageUrl"
                :alt="productName"
                class="product-detail-image"
              />
              <div v-else class="product-thumb-fallback">Không có ảnh</div>
            </div>

            <div class="product-detail-thumbs">
              <button class="thumb-item active" type="button">
                <img
                  v-if="imageUrl"
                  :src="imageUrl"
                  :alt="productName"
                />
                <span v-else>Ảnh</span>
              </button>
            </div>
          </section>

          <!-- RIGHT: INFO -->
          <section class="product-detail-info">
            <div class="product-brand-row">
              <span class="brand-badge">{{ productBrand }}</span>
              <span class="stock-badge" :class="inStock ? 'is-instock' : 'is-outstock'">
                {{ inStock ? "Còn hàng" : "Hết hàng" }}
              </span>
            </div>

            <h1 class="product-detail-title">{{ productName }}</h1>

            <div class="product-price-box">
              <div class="product-sale-price">
                {{ formatCurrency(productPrice) }}
              </div>

              <div
                v-if="hasComparePrice"
                class="product-compare-price"
              >
                {{ formatCurrency(productCompareAtPrice) }}
              </div>

              <div
                v-if="discountPercent > 0"
                class="discount-badge"
              >
                Giảm {{ discountPercent }}%
              </div>
            </div>

            <div class="product-meta-grid">
              <div class="meta-item">
                <span class="meta-label">Danh mục</span>
                <span class="meta-value">{{ productCategory }}</span>
              </div>

              <div class="meta-item" v-if="product?.sku">
                <span class="meta-label">SKU</span>
                <span class="meta-value">{{ product.sku }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">Thương hiệu</span>
                <span class="meta-value">{{ productBrand }}</span>
              </div>

              <div class="meta-item">
                <span class="meta-label">Tồn kho</span>
                <span class="meta-value">
                  {{ stockQuantityText }}
                </span>
              </div>

              <div class="meta-item" v-if="product?.weightGrams">
                <span class="meta-label">Khối lượng</span>
                <span class="meta-value">{{ product.weightGrams }} gram</span>
              </div>
            </div>

            <div class="product-short-desc">
              {{ productDescription }}
            </div>

            <div class="purchase-panel">
              <div class="purchase-label">Số lượng</div>

              <div class="product-detail-actions">
                <div class="product-qty-box">
                  <button type="button" class="qty-btn" @click="decreaseQty">-</button>
                  <input
                    v-model.number="quantity"
                    type="number"
                    min="1"
                    class="qty-input"
                  />
                  <button type="button" class="qty-btn" @click="increaseQty">+</button>
                </div>

                <button
                  class="btn-buy"
                  :disabled="!inStock"
                  @click="handleAddToCart"
                >
                  Thêm vào giỏ hàng
                </button>

                <router-link to="/products" class="btn-back">
                  Quay lại
                </router-link>
              </div>

              <p v-if="error" class="error-text">{{ error }}</p>
            </div>

            <div class="feature-box">
              <div class="feature-item">✅ Hàng chính hãng</div>
              <div class="feature-item">🚚 Giao hàng nhanh</div>
              <div class="feature-item">🔒 Thanh toán an toàn</div>
            </div>
          </section>
        </div>
      </div>

      <div class="product-extra-grid">
        <div class="info-card">
          <h2 class="section-title">Thông tin sản phẩm</h2>
          <div class="section-content">
            {{ productDescription }}
          </div>
        </div>

        <div class="info-card">
          <h2 class="section-title">Thông số nhanh</h2>
          <div class="spec-list">
            <div class="spec-row">
              <span>Danh mục</span>
              <strong>{{ productCategory }}</strong>
            </div>
            <div class="spec-row">
              <span>Thương hiệu</span>
              <strong>{{ productBrand }}</strong>
            </div>
            <div class="spec-row" v-if="product?.sku">
              <span>SKU</span>
              <strong>{{ product.sku }}</strong>
            </div>
            <div class="spec-row">
              <span>Trạng thái</span>
              <strong>{{ inStock ? "Còn hàng" : "Hết hàng" }}</strong>
            </div>
            <div class="spec-row" v-if="product?.weightGrams">
              <span>Khối lượng</span>
              <strong>{{ product.weightGrams }} gram</strong>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-else-if="loading" class="detail-container">
    <div class="state-card">Đang tải chi tiết sản phẩm...</div>
  </div>

  <div v-else class="detail-container">
    <div class="state-card">Không tìm thấy sản phẩm.</div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useCartStore } from "../../stores/cart.store";
import { useProductStore } from "../../stores/product.store";
import { resolveImageUrl } from "../../utils/api";

const route = useRoute();
const router = useRouter();
const cartStore = useCartStore();
const productStore = useProductStore();

const loading = ref(false);
const quantity = ref(1);
const error = ref("");

const product = computed(() => productStore.productDetail || null);

const productName = computed(() => {
  return product.value?.name || product.value?.productName || product.value?.title || "Sản phẩm";
});

const productBrand = computed(() => {
  return product.value?.brandName || product.value?.brand?.name || product.value?.brand || "TPhone";
});

const productCategory = computed(() => {
  return product.value?.categoryName || product.value?.category?.name || "Chưa phân loại";
});

const productPrice = computed(() => {
  return Number(product.value?.price || product.value?.salePrice || product.value?.unitPrice || 0);
});

const productCompareAtPrice = computed(() => {
  return Number(product.value?.compareAtPrice || product.value?.compare_at_price || 0);
});

const hasComparePrice = computed(() => {
  return productCompareAtPrice.value > productPrice.value;
});

const discountPercent = computed(() => {
  if (!hasComparePrice.value) return 0;
  const discount = ((productCompareAtPrice.value - productPrice.value) / productCompareAtPrice.value) * 100;
  return Math.round(discount);
});

const imageUrl = computed(() => {
  const raw =
    product.value?.thumbnailUrl ||
    product.value?.imageUrl ||
    product.value?.thumbnail ||
    product.value?.image ||
    product.value?.productImage ||
    "";
  return resolveImageUrl(raw);
});

const productDescription = computed(() => {
  return (
    product.value?.description ||
    product.value?.shortDescription ||
    "Chưa có mô tả cho sản phẩm này."
  );
});

const stockRaw = computed(() => {
  return product.value?.stockQuantity ?? product.value?.quantity ?? product.value?.inventory ?? 0;
});

const inStock = computed(() => {
  const stock = stockRaw.value;
  return Number(stock) > 0 || stock === true;
});

const stockQuantityText = computed(() => {
  const stock = Number(stockRaw.value || 0);
  if (stock > 0) return `${stock} sản phẩm`;
  return "Hết hàng";
});

const formatCurrency = (value) => {
  const number = Number(value || 0);
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(number);
};

const increaseQty = () => {
  quantity.value += 1;
};

const decreaseQty = () => {
  if (quantity.value > 1) quantity.value -= 1;
};

const handleAddToCart = async () => {
  error.value = "";

  try {
    await cartStore.addToCart(route.params.id, quantity.value);
  } catch (err) {
    if (err?.response?.status === 401) {
      router.push("/login");
      return;
    }
    error.value = err?.response?.data?.message || "Không thể thêm sản phẩm vào giỏ hàng.";
  }
};

const loadProduct = async () => {
  loading.value = true;
  error.value = "";
  try {
    await productStore.fetchProductDetail(route.params.id);
  } catch (err) {
    error.value = err?.response?.data?.message || "Tải chi tiết sản phẩm thất bại.";
  } finally {
    loading.value = false;
  }
};

onMounted(loadProduct);

onUnmounted(() => {
  if (typeof productStore.clearProductDetail === "function") {
    productStore.clearProductDetail();
  }
});
</script>

<style scoped>
.product-detail-page {
  background: #f3f4f6;
  min-height: 100vh;
  padding: 24px 0 40px;
}

.detail-container {
  width: min(1200px, calc(100% - 32px));
  margin: 0 auto;
}

.product-detail-card {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
  overflow: hidden;
  border: 1px solid #eceff3;
}

.product-detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(360px, 0.95fr);
  gap: 32px;
  padding: 28px;
  align-items: start;
}

.product-detail-gallery {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-detail-image-box {
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  min-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.product-detail-image {
  width: 100%;
  max-width: 420px;
  max-height: 440px;
  object-fit: contain;
  display: block;
}

.product-thumb-fallback {
  color: #64748b;
  font-size: 15px;
}

.product-detail-thumbs {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.thumb-item {
  width: 74px;
  height: 74px;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: default;
}

.thumb-item.active {
  border-color: #d70018;
  box-shadow: 0 0 0 3px rgba(215, 0, 24, 0.08);
}

.thumb-item img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.product-detail-info {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.product-brand-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.brand-badge,
.stock-badge {
  display: inline-flex;
  align-items: center;
  height: 32px;
  border-radius: 999px;
  padding: 0 14px;
  font-size: 13px;
  font-weight: 700;
}

.brand-badge {
  background: #fff1f2;
  color: #d70018;
}

.stock-badge.is-instock {
  background: #ecfdf3;
  color: #15803d;
}

.stock-badge.is-outstock {
  background: #fef2f2;
  color: #dc2626;
}

.product-detail-title {
  margin: 0;
  font-size: 34px;
  line-height: 1.25;
  font-weight: 800;
  color: #0f172a;
}

.product-price-box {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 16px 18px;
  border-radius: 18px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe9ec 100%);
  border: 1px solid #ffd5db;
}

.product-sale-price {
  font-size: 32px;
  font-weight: 800;
  color: #d70018;
  line-height: 1;
}

.product-compare-price {
  font-size: 18px;
  color: #94a3b8;
  text-decoration: line-through;
}

.discount-badge {
  background: #d70018;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  border-radius: 999px;
  padding: 6px 10px;
}

.product-meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.meta-item {
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 12px 14px;
  background: #fafafa;
}

.meta-label {
  display: block;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 6px;
}

.meta-value {
  display: block;
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  word-break: break-word;
}

.product-short-desc {
  font-size: 15px;
  line-height: 1.75;
  color: #334155;
  background: #f8fafc;
  border: 1px solid #e9eef5;
  border-radius: 16px;
  padding: 16px 18px;
}

.purchase-panel {
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  padding: 18px;
  background: #fff;
}

.purchase-label {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
  margin-bottom: 12px;
}

.product-detail-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.product-qty-box {
  display: inline-flex;
  align-items: center;
  border: 1px solid #dbe2ea;
  border-radius: 14px;
  overflow: hidden;
  background: #fff;
}

.qty-btn {
  width: 42px;
  height: 44px;
  border: none;
  background: #f8fafc;
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
}

.qty-btn:hover {
  background: #eef2f7;
}

.qty-input {
  width: 68px;
  height: 44px;
  border: none;
  text-align: center;
  font-size: 15px;
  font-weight: 700;
  outline: none;
}

.btn-buy,
.btn-back {
  height: 46px;
  border-radius: 14px;
  padding: 0 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 700;
  text-decoration: none;
  border: none;
  cursor: pointer;
  transition: 0.2s ease;
}

.btn-buy {
  background: linear-gradient(135deg, #d70018 0%, #c30015 100%);
  color: #fff;
  min-width: 180px;
}

.btn-buy:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 20px rgba(215, 0, 24, 0.18);
}

.btn-buy:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-back {
  background: #f1f5f9;
  color: #0f172a;
}

.btn-back:hover {
  background: #e2e8f0;
}

.error-text {
  margin: 12px 0 0;
  color: #dc2626;
  font-size: 14px;
}

.feature-box {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.feature-item {
  border: 1px solid #e5e7eb;
  background: #fff;
  border-radius: 14px;
  padding: 12px;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}

.product-extra-grid {
  margin-top: 24px;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 0.8fr);
  gap: 24px;
}

.info-card {
  background: #fff;
  border: 1px solid #eceff3;
  border-radius: 18px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
  padding: 22px;
}

.section-title {
  margin: 0 0 16px;
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
}

.section-content {
  font-size: 15px;
  line-height: 1.8;
  color: #334155;
}

.spec-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.spec-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  border-bottom: 1px dashed #e5e7eb;
  padding-bottom: 10px;
  font-size: 14px;
  color: #475569;
}

.spec-row strong {
  color: #0f172a;
  text-align: right;
}

.state-card {
  background: #fff;
  border: 1px solid #eceff3;
  border-radius: 18px;
  padding: 28px;
  text-align: center;
  color: #475569;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

@media (max-width: 1024px) {
  .product-detail-grid,
  .product-extra-grid {
    grid-template-columns: 1fr;
  }

  .product-detail-image-box {
    min-height: 420px;
  }
}

@media (max-width: 768px) {
  .product-detail-page {
    padding: 16px 0 28px;
  }

  .detail-container {
    width: min(100%, calc(100% - 24px));
  }

  .product-detail-grid {
    padding: 18px;
    gap: 22px;
  }

  .product-detail-title {
    font-size: 28px;
  }

  .product-sale-price {
    font-size: 26px;
  }

  .product-meta-grid,
  .feature-box {
    grid-template-columns: 1fr;
  }

  .product-detail-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .product-qty-box {
    width: fit-content;
  }

  .btn-buy,
  .btn-back {
    width: 100%;
  }

  .product-detail-image-box {
    min-height: 320px;
  }

  .thumb-item {
    width: 64px;
    height: 64px;
  }
}

@media (max-width: 480px) {
  .product-detail-title {
    font-size: 24px;
  }

  .product-sale-price {
    font-size: 22px;
  }

  .product-price-box {
    padding: 14px;
  }

  .meta-item {
    padding: 11px 12px;
  }

  .info-card {
    padding: 18px;
  }
}
</style>