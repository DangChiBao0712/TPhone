<template>
  <div class="product-list-page">
    <div class="card product-filter-card">
      <div class="product-filter-top">
        <div>
          <h1 class="page-title">Tất cả sản phẩm</h1>
          <p class="text-muted">
            Khám phá các mẫu điện thoại, tablet, laptop và phụ kiện tại TPhone
          </p>
        </div>
      </div>

      <div class="product-filter-bar">
        <input
          v-model.trim="keyword"
          type="text"
          class="input"
          placeholder="Tìm theo tên sản phẩm..."
          @keyup.enter="applyFilters"
        />

        <select v-model="selectedCategory" class="select">
          <option value="">Tất cả danh mục</option>
          <option
            v-for="item in categories"
            :key="item.value"
            :value="item.value"
          >
            {{ item.label }}
          </option>
        </select>

        <button class="btn btn-primary" @click="applyFilters">
          Lọc sản phẩm
        </button>
      </div>
    </div>

    <div class="product-list-toolbar">
      <div class="text-muted">
        Hiển thị <strong>{{ filteredProducts.length }}</strong> sản phẩm
      </div>
    </div>

    <div v-if="loading" class="card empty-state">Đang tải sản phẩm...</div>

    <div v-else-if="error" class="card empty-state">
      {{ error }}
    </div>

    <div v-else-if="filteredProducts.length" class="product-grid">
      <ProductCard
        v-for="item in filteredProducts"
        :key="item.id || item.productId || item.slug"
        :product="item"
      />
    </div>

    <div v-else class="card empty-state">
      Không tìm thấy sản phẩm phù hợp.
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import ProductCard from "../../components/ProductCard.vue";
import { useProductStore } from "../../stores/product.store";

const router = useRouter();
const route = useRoute();
const productStore = useProductStore();

const loading = ref(false);
const error = ref("");
const keyword = ref(route.query.keyword || "");
const selectedCategory = ref(route.query.category || "");

const products = computed(() => productStore.products || []);

const getCategoryValue = (item) => {
  return (
    item?.categoryId ||
    item?.category?.id ||
    item?.categoryName ||
    item?.category?.name ||
    ""
  );
};

const getCategoryLabel = (item) => {
  return item?.categoryName || item?.category?.name || "Chưa phân loại";
};

const categories = computed(() => {
  const map = new Map();

  products.value.forEach((item) => {
    const value = getCategoryValue(item);
    const label = getCategoryLabel(item);

    if (value && !map.has(String(value))) {
      map.set(String(value), {
        value: String(value),
        label,
      });
    }
  });

  return Array.from(map.values());
});

const filteredProducts = computed(() => {
  const kw = keyword.value.trim().toLowerCase();

  return products.value.filter((item) => {
    const name = (item?.name || item?.productName || item?.title || "").toLowerCase();
    const categoryValue = String(getCategoryValue(item));

    const matchedKeyword = !kw || name.includes(kw);
    const matchedCategory =
      !selectedCategory.value || categoryValue === String(selectedCategory.value);

    return matchedKeyword && matchedCategory;
  });
});

const loadData = async () => {
  loading.value = true;
  error.value = "";

  try {
    const params = {};

    if (keyword.value) params.keyword = keyword.value;

    if (selectedCategory.value && !Number.isNaN(Number(selectedCategory.value))) {
      params.categoryId = Number(selectedCategory.value);
    }

    await productStore.fetchProducts(params);
  } catch (err) {
    error.value =
      err?.response?.data?.message ||
      err?.response?.data?.error ||
      "Tải danh sách sản phẩm thất bại.";
  } finally {
    loading.value = false;
  }
};

const applyFilters = async () => {
  const query = {};

  if (keyword.value) query.keyword = keyword.value;
  if (selectedCategory.value) query.category = selectedCategory.value;

  await router.push({
    path: "/products",
    query,
  });

  await loadData();
};

watch(
  () => route.query,
  (query) => {
    keyword.value = query.keyword || "";
    selectedCategory.value = query.category || "";
  }
);

onMounted(loadData);
</script>