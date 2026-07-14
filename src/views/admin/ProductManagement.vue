<template>
  <div>
    <h1 class="page-title">Quản lý sản phẩm</h1>

    <div class="card" style="margin-bottom: 24px">
      <div class="card-body">
        <h2 class="section-title" style="font-size: 22px">
          {{ editingId ? "Cập nhật sản phẩm" : "Thêm sản phẩm mới" }}
        </h2>

        <div class="grid grid-2">
          <div class="form-group">
            <label class="field-label">Danh mục</label>
            <select
              v-model="form.categoryId"
              class="select"
              :disabled="submitting || uploadingImage"
            >
              <option :value="null">Chọn danh mục</option>
              <option v-for="item in categories" :key="item.id" :value="item.id">
                {{ item.name }}
              </option>
            </select>
            <small class="field-hint">Loại sản phẩm, ví dụ: Điện thoại, Phụ kiện.</small>
          </div>

          <div class="form-group">
            <label class="field-label">Thương hiệu</label>
            <select
              v-model="form.brandId"
              class="select"
              :disabled="submitting || uploadingImage"
            >
              <option :value="null">Chọn thương hiệu</option>
              <option v-for="item in brands" :key="item.id" :value="item.id">
                {{ item.name }}
              </option>
            </select>
            <small class="field-hint">Hãng sản xuất, ví dụ: Apple, Samsung.</small>
          </div>

          <div class="form-group">
            <label class="field-label">Tên sản phẩm</label>
            <input
              v-model="form.name"
              class="input"
              placeholder="VD: iPhone 16 128GB"
              :disabled="submitting || uploadingImage"
            />
            <small class="field-hint">Tên hiển thị cho khách hàng.</small>
          </div>

          <div class="form-group">
            <label class="field-label">Slug</label>
            <input
              v-model="form.slug"
              class="input"
              placeholder="Tự tạo từ tên sản phẩm"
              :disabled="true"
            />
            <small class="field-hint">Đường dẫn URL, hệ thống tự tạo.</small>
          </div>

          <div class="form-group">
            <label class="field-label">SKU</label>
            <input
              v-model="form.sku"
              class="input"
              placeholder="VD: IP16-128-BLK"
              :disabled="submitting || uploadingImage"
            />
            <small class="field-hint">Mã nội bộ để quản lý kho, không được trùng.</small>
          </div>

          <div class="form-group">
            <label class="field-label">Giá bán</label>
            <input
              v-model.number="form.price"
              type="number"
              min="0"
              class="input"
              placeholder="VD: 24990000"
              :disabled="submitting || uploadingImage"
            />
            <small class="field-hint">Giá bán thực tế cho khách.</small>
          </div>

          <div class="form-group">
            <label class="field-label">Giá niêm yết</label>
            <input
              v-model.number="form.compareAtPrice"
              type="number"
              min="0"
              class="input"
              placeholder="VD: 27990000"
              :disabled="submitting || uploadingImage"
            />
            <small class="field-hint">Giá gốc để hiển thị khuyến mãi nếu có.</small>
          </div>

          <div class="form-group">
            <label class="field-label">Tồn kho</label>
            <input
              v-model.number="form.stockQuantity"
              type="number"
              min="0"
              class="input"
              placeholder="VD: 50"
              :disabled="submitting || uploadingImage"
            />
            <small class="field-hint">Số lượng hiện còn trong kho.</small>
          </div>

          <div class="form-group">
            <label class="field-label">Cảnh báo tồn kho</label>
            <input
              v-model.number="form.minStockAlert"
              type="number"
              min="0"
              class="input"
              placeholder="VD: 5"
              :disabled="submitting || uploadingImage"
            />
            <small class="field-hint">Báo động khi tồn kho thấp hơn mức này.</small>
          </div>

          <div class="form-group">
            <label class="field-label">Khối lượng (gram)</label>
            <input
              v-model.number="form.weightGrams"
              type="number"
              min="0"
              class="input"
              placeholder="VD: 171"
              :disabled="submitting || uploadingImage"
            />
            <small class="field-hint">Dùng cho vận chuyển, đơn vị gram.</small>
          </div>

          <div class="form-group">
            <label class="field-label">Trạng thái</label>
            <select
              v-model="form.status"
              class="select"
              :disabled="submitting || uploadingImage"
            >
              <option value="ACTIVE">Đang bán</option>
              <option value="DRAFT">Nháp</option>
              <option value="INACTIVE">Ngừng bán</option>
            </select>
            <small class="field-hint">Chọn trạng thái hiển thị của sản phẩm.</small>
          </div>
        </div>

        <div style="margin-top: 14px">
          <label class="field-label" style="display: block; margin-bottom: 8px">
            Ảnh đại diện sản phẩm
          </label>

          <input
            ref="fileInputRef"
            type="file"
            accept="image/*"
            class="input"
            :disabled="submitting || uploadingImage"
            @change="handleImageChange"
          />

          <small class="field-hint" style="display: block; margin-top: 6px">
            Chọn 1 ảnh đại diện chính cho sản phẩm.
          </small>

          <p v-if="uploadingImage" class="text-muted" style="margin: 8px 0 0">
            Đang tải ảnh lên...
          </p>

          <p
            v-else-if="form.thumbnailUrl"
            class="text-muted"
            style="margin: 8px 0 0; word-break: break-all"
          >
            URL ảnh: {{ form.thumbnailUrl }}
          </p>

          <div
            v-if="previewImageUrl"
            style="
              margin-top: 12px;
              border: 1px solid #e5e7eb;
              border-radius: 12px;
              padding: 12px;
              max-width: 320px;
              background: #fff;
            "
          >
            <img
              :src="previewImageUrl"
              alt="Preview"
              style="
                width: 100%;
                height: 220px;
                object-fit: cover;
                border-radius: 10px;
                display: block;
              "
            />
          </div>
        </div>

        <div class="form-group" style="margin-top: 12px">
          <label class="field-label">Mô tả ngắn</label>
          <textarea
            v-model="form.shortDescription"
            class="textarea"
            placeholder="VD: iPhone 16 bản 128GB, chip mới, camera nâng cấp..."
            :disabled="submitting || uploadingImage"
          />
          <small class="field-hint">1–2 dòng nổi bật để hiển thị nhanh.</small>
        </div>

        <div class="form-group" style="margin-top: 12px">
          <label class="field-label">Mô tả chi tiết</label>
          <textarea
            v-model="form.description"
            class="textarea"
            placeholder="Nhập thông tin chi tiết về cấu hình, tính năng, màu sắc, bảo hành..."
            :disabled="submitting || uploadingImage"
          />
          <small class="field-hint">Thông tin đầy đủ cho trang chi tiết sản phẩm.</small>
        </div>

        <div style="display: flex; gap: 10px; margin-top: 16px; flex-wrap: wrap">
          <button
            class="btn btn-primary"
            :disabled="submitting || uploadingImage"
            @click="handleSubmit"
          >
            {{
              uploadingImage
                ? "Đang tải ảnh..."
                : submitting
                ? "Đang lưu..."
                : editingId
                ? "Cập nhật"
                : "Thêm mới"
            }}
          </button>

          <button
            v-if="editingId"
            class="btn btn-secondary"
            :disabled="submitting || uploadingImage"
            @click="resetForm"
          >
            Hủy
          </button>
        </div>

        <AppAlert type="success" :message="message" />
        <AppAlert type="error" :message="error" />
      </div>
    </div>

    <div v-if="loading" class="card">
      <div class="card-body">Đang tải dữ liệu...</div>
    </div>

    <div v-else class="grid">
      <div v-for="product in products" :key="product.id" class="card">
        <div class="card-body">
          <div class="flex-between" style="align-items: flex-start; gap: 16px">
            <div style="flex: 1">
              <p style="margin: 0 0 8px; font-size: 20px; font-weight: 700">
                {{ product.name }}
              </p>
              <p class="text-muted" style="margin: 0">SKU: {{ product.sku }}</p>
              <p class="text-muted" style="margin: 6px 0 0">Slug: {{ product.slug }}</p>
            </div>

            <div v-if="product.thumbnailUrl" style="width: 92px; flex-shrink: 0">
              <img
                :src="resolveImageUrl(product.thumbnailUrl)"
                :alt="product.name"
                style="
                  width: 92px;
                  height: 92px;
                  object-fit: cover;
                  border-radius: 10px;
                  border: 1px solid #e5e7eb;
                "
              />
            </div>

            <span class="badge">{{ formatProductStatus(product.status) }}</span>
          </div>

          <div class="grid grid-2" style="margin-top: 16px">
            <div>
              <p class="text-muted" style="margin: 0 0 6px">Danh mục</p>
              <p style="margin: 0; font-weight: 700">{{ product.categoryName }}</p>
            </div>
            <div>
              <p class="text-muted" style="margin: 0 0 6px">Thương hiệu</p>
              <p style="margin: 0; font-weight: 700">{{ product.brandName }}</p>
            </div>
            <div>
              <p class="text-muted" style="margin: 0 0 6px">Giá bán</p>
              <p style="margin: 0; font-weight: 700; color: #dc2626">
                {{ formatPrice(product.price) }}
              </p>
            </div>
            <div>
              <p class="text-muted" style="margin: 0 0 6px">Tồn kho</p>
              <p style="margin: 0; font-weight: 700">{{ product.stockQuantity }}</p>
            </div>
          </div>

          <div style="display: flex; gap: 10px; margin-top: 16px">
            <button
              class="btn btn-secondary"
              :disabled="submitting || uploadingImage"
              @click="editProduct(product)"
            >
              Sửa
            </button>
            <button
              class="btn btn-danger"
              :disabled="submitting || uploadingImage"
              @click="askDelete(product.id)"
            >
              Xóa
            </button>
          </div>
        </div>
      </div>
    </div>

    <ConfirmDialog
      :open="confirmOpen"
      title="Xác nhận xóa sản phẩm"
      message="Bạn có chắc chắn muốn xóa sản phẩm này không?"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import api from "../../api/axios";
import { resolveImageUrl, unwrapData } from "../../utils/api";
import AppAlert from "../../components/AppAlert.vue";
import ConfirmDialog from "../../components/ConfirmDialog.vue";
import { formatPrice, formatProductStatus } from "../../utils/format";
import { extractErrorMessage } from "../../utils/error";

const loading = ref(false);
const submitting = ref(false);
const uploadingImage = ref(false);
const products = ref([]);
const brands = ref([]);
const categories = ref([]);
const editingId = ref(null);
const message = ref("");
const error = ref("");
const confirmOpen = ref(false);
const deletingId = ref(null);
const fileInputRef = ref(null);

const form = reactive({
  categoryId: null,
  brandId: null,
  name: "",
  slug: "",
  sku: "",
  shortDescription: "",
  description: "",
  price: null,
  compareAtPrice: null,
  stockQuantity: null,
  minStockAlert: null,
  thumbnailUrl: "",
  weightGrams: null,
  status: "ACTIVE",
});

const previewImageUrl = computed(() => {
  if (!form.thumbnailUrl) return "";
  return resolveImageUrl(form.thumbnailUrl);
});

const generateSlug = (value) => {
  return String(value || "")
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/đ/g, "d")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "");
};

watch(
  () => form.name,
  (value) => {
    if (!editingId.value) {
      form.slug = generateSlug(value);
    }
  }
);

const resetForm = () => {
  editingId.value = null;
  form.categoryId = null;
  form.brandId = null;
  form.name = "";
  form.slug = "";
  form.sku = "";
  form.shortDescription = "";
  form.description = "";
  form.price = null;
  form.compareAtPrice = null;
  form.stockQuantity = null;
  form.minStockAlert = null;
  form.thumbnailUrl = "";
  form.weightGrams = null;
  form.status = "ACTIVE";
  error.value = "";

  if (fileInputRef.value) {
    fileInputRef.value.value = "";
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    const [productRes, brandRes, categoryRes] = await Promise.all([
      api.get("/admin/products"),
      api.get("/admin/brands"),
      api.get("/admin/categories"),
    ]);

    products.value = unwrapData(productRes.data) || [];
    brands.value = unwrapData(brandRes.data) || [];
    categories.value = unwrapData(categoryRes.data) || [];
  } finally {
    loading.value = false;
  }
};

const handleImageChange = async (event) => {
  const file = event.target.files?.[0];
  if (!file) return;

  error.value = "";
  message.value = "";
  uploadingImage.value = true;

  try {
    const formData = new FormData();
    formData.append("file", file);

    const response = await api.post("/admin/uploads/images", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    const uploadResult = unwrapData(response.data) || response.data;
    form.thumbnailUrl = uploadResult.fileUrl || "";
    message.value = "Tải ảnh lên thành công";
  } catch (err) {
    form.thumbnailUrl = "";
    error.value = extractErrorMessage(err, "Không thể tải ảnh lên");
  } finally {
    uploadingImage.value = false;
  }
};

const validateForm = () => {
  if (!form.categoryId) return "Vui lòng chọn danh mục";
  if (!form.brandId) return "Vui lòng chọn thương hiệu";
  if (!form.name?.trim()) return "Vui lòng nhập tên sản phẩm";
  if (!form.slug?.trim()) return "Slug chưa được tạo";
  if (!form.sku?.trim()) return "Vui lòng nhập SKU";
  if (form.price == null || form.price < 0) return "Vui lòng nhập giá bán hợp lệ";
  if (form.compareAtPrice != null && form.compareAtPrice < 0) return "Giá niêm yết không hợp lệ";
  if (form.stockQuantity == null || form.stockQuantity < 0) return "Vui lòng nhập tồn kho hợp lệ";
  if (form.minStockAlert == null || form.minStockAlert < 0) return "Vui lòng nhập mức cảnh báo hợp lệ";
  if (form.weightGrams == null || form.weightGrams < 0) return "Vui lòng nhập khối lượng hợp lệ";
  if (!form.thumbnailUrl) return "Vui lòng tải ảnh đại diện sản phẩm";
  return "";
};

const handleSubmit = async () => {
  if (submitting.value || uploadingImage.value) return;

  message.value = "";
  error.value = "";

  const validationError = validateForm();
  if (validationError) {
    error.value = validationError;
    return;
  }

  submitting.value = true;

  try {
    const payload = {
      categoryId: form.categoryId,
      brandId: form.brandId,
      name: form.name?.trim(),
      slug: form.slug?.trim(),
      sku: form.sku?.trim(),
      shortDescription: form.shortDescription?.trim() || "",
      description: form.description?.trim() || "",
      price: form.price,
      compareAtPrice: form.compareAtPrice,
      stockQuantity: form.stockQuantity,
      minStockAlert: form.minStockAlert,
      thumbnailUrl: form.thumbnailUrl,
      weightGrams: form.weightGrams,
      status: form.status,
    };

    if (editingId.value) {
      await api.put(`/admin/products/${editingId.value}`, payload);
      message.value = "Cập nhật sản phẩm thành công";
    } else {
      await api.post("/admin/products", payload);
      message.value = "Thêm sản phẩm thành công";
    }

    resetForm();
    await loadData();
  } catch (err) {
    error.value = extractErrorMessage(err, "Không thể lưu sản phẩm");
  } finally {
    submitting.value = false;
  }
};

const editProduct = (product) => {
  if (submitting.value || uploadingImage.value) return;

  editingId.value = product.id;
  form.categoryId = product.categoryId;
  form.brandId = product.brandId;
  form.name = product.name || "";
  form.slug = product.slug || "";
  form.sku = product.sku || "";
  form.shortDescription = product.shortDescription || "";
  form.description = product.description || "";
  form.price = product.price ?? null;
  form.compareAtPrice = product.compareAtPrice ?? null;
  form.stockQuantity = product.stockQuantity ?? null;
  form.minStockAlert = product.minStockAlert ?? null;
  form.thumbnailUrl = product.thumbnailUrl || "";
  form.weightGrams = product.weightGrams ?? null;
  form.status = product.status || "ACTIVE";

  if (fileInputRef.value) {
    fileInputRef.value.value = "";
  }
};

const askDelete = (id) => {
  if (submitting.value || uploadingImage.value) return;
  deletingId.value = id;
  confirmOpen.value = true;
};

const cancelDelete = () => {
  confirmOpen.value = false;
  deletingId.value = null;
};

const confirmDelete = async () => {
  if (submitting.value || uploadingImage.value) return;

  submitting.value = true;
  message.value = "";
  error.value = "";

  try {
    await api.delete(`/admin/products/${deletingId.value}`);
    message.value = "Xóa sản phẩm thành công";
    await loadData();
  } catch (err) {
    error.value = extractErrorMessage(err, "Không thể xóa sản phẩm");
  } finally {
    submitting.value = false;
    cancelDelete();
  }
};

onMounted(loadData);
</script>

<style scoped>
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 14px;
  font-weight: 700;
  color: #111827;
}

.field-hint {
  font-size: 12px;
  line-height: 1.45;
  color: #6b7280;
}

.input[disabled] {
  background: #f9fafb;
  color: #6b7280;
  cursor: not-allowed;
}
</style>