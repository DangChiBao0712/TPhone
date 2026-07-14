<template>
  <div>
    <h1 class="page-title">Quản lý thương hiệu</h1>

    <div class="card" style="margin-bottom:24px;">
      <div class="card-body">
        <h2 class="section-title" style="font-size:22px;">
          {{ editingId ? 'Cập nhật thương hiệu' : 'Thêm thương hiệu mới' }}
        </h2>

        <div class="grid grid-2">
          <input v-model="form.name" class="input" placeholder="Tên thương hiệu" :disabled="submitting" />
          <input v-model="form.slug" class="input" placeholder="Slug" :disabled="submitting" />
          <input v-model="form.logoUrl" class="input" placeholder="Đường dẫn logo" :disabled="submitting" />
          <input v-model.number="form.sortOrder" type="number" class="input" placeholder="Thứ tự hiển thị" :disabled="submitting" />
        </div>

        <textarea
          v-model="form.description"
          class="textarea"
          placeholder="Mô tả"
          style="margin-top:12px;"
          :disabled="submitting"
        />

        <label style="display:block; margin-top:14px;">
          <input type="checkbox" v-model="form.isActive" :disabled="submitting" />
          Đang hoạt động
        </label>

        <div style="display:flex; gap:10px; margin-top:16px; flex-wrap:wrap;">
          <button class="btn btn-primary" :disabled="submitting" @click="handleSubmit">
            {{ submitting ? 'Đang lưu...' : editingId ? 'Cập nhật' : 'Thêm mới' }}
          </button>
          <button v-if="editingId" class="btn btn-secondary" :disabled="submitting" @click="resetForm">Hủy</button>
        </div>

        <AppAlert type="success" :message="message" />
        <AppAlert type="error" :message="error" />
      </div>
    </div>

    <div v-if="loading" class="card">
      <div class="card-body">Đang tải dữ liệu...</div>
    </div>

    <div v-else class="grid">
      <div
        v-for="brand in brands"
        :key="brand.id"
        class="card"
      >
        <div class="card-body">
          <div class="flex-between" style="align-items:flex-start;">
            <div>
              <p style="margin:0 0 8px; font-size:20px; font-weight:700;">{{ brand.name }}</p>
              <p class="text-muted" style="margin:0;">Slug: {{ brand.slug }}</p>
            </div>
            <span class="badge">
              {{ brand.isActive ? 'Đang hoạt động' : 'Ngừng hoạt động' }}
            </span>
          </div>

          <div class="grid grid-2" style="margin-top:16px;">
            <div>
              <p class="text-muted" style="margin:0 0 6px;">Thứ tự hiển thị</p>
              <p style="margin:0; font-weight:700;">{{ brand.sortOrder }}</p>
            </div>
          </div>

          <div style="display:flex; gap:10px; margin-top:16px;">
            <button class="btn btn-secondary" :disabled="submitting" @click="editBrand(brand)">Sửa</button>
            <button class="btn btn-danger" :disabled="submitting" @click="askDelete(brand.id)">Xóa</button>
          </div>
        </div>
      </div>
    </div>

    <ConfirmDialog
      :open="confirmOpen"
      title="Xác nhận xóa thương hiệu"
      message="Bạn có chắc chắn muốn xóa thương hiệu này không?"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import api from '../../api/axios';
import { unwrapData } from '../../utils/api';
import AppAlert from '../../components/AppAlert.vue';
import ConfirmDialog from '../../components/ConfirmDialog.vue';
import { extractErrorMessage } from '../../utils/error';

const loading = ref(false);
const submitting = ref(false);
const brands = ref([]);
const editingId = ref(null);
const message = ref('');
const error = ref('');
const confirmOpen = ref(false);
const deletingId = ref(null);

const form = reactive({
  name: '',
  slug: '',
  description: '',
  logoUrl: '',
  isActive: true,
  sortOrder: 0,
});

const resetForm = () => {
  editingId.value = null;
  form.name = '';
  form.slug = '';
  form.description = '';
  form.logoUrl = '';
  form.isActive = true;
  form.sortOrder = 0;
  error.value = '';
};

const loadBrands = async () => {
  loading.value = true;
  try {
    const res = await api.get('/admin/brands');
    brands.value = unwrapData(res.data) || [];
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (submitting.value) return;

  message.value = '';
  error.value = '';
  submitting.value = true;

  try {
    const payload = { ...form };

    if (editingId.value) {
      await api.put(`/admin/brands/${editingId.value}`, payload);
      message.value = 'Cập nhật thương hiệu thành công';
    } else {
      await api.post('/admin/brands', payload);
      message.value = 'Thêm thương hiệu thành công';
    }

    resetForm();
    await loadBrands();
  } catch (err) {
    error.value = extractErrorMessage(err, 'Không thể lưu thương hiệu');
  } finally {
    submitting.value = false;
  }
};

const editBrand = (brand) => {
  if (submitting.value) return;

  editingId.value = brand.id;
  form.name = brand.name || '';
  form.slug = brand.slug || '';
  form.description = brand.description || '';
  form.logoUrl = brand.logoUrl || '';
  form.isActive = !!brand.isActive;
  form.sortOrder = brand.sortOrder || 0;
};

const askDelete = (id) => {
  if (submitting.value) return;
  deletingId.value = id;
  confirmOpen.value = true;
};

const cancelDelete = () => {
  confirmOpen.value = false;
  deletingId.value = null;
};

const confirmDelete = async () => {
  if (submitting.value) return;

  submitting.value = true;
  message.value = '';
  error.value = '';

  try {
    await api.delete(`/admin/brands/${deletingId.value}`);
    message.value = 'Xóa thương hiệu thành công';
    await loadBrands();
  } catch (err) {
    error.value = extractErrorMessage(err, 'Không thể xóa thương hiệu');
  } finally {
    submitting.value = false;
    cancelDelete();
  }
};

onMounted(loadBrands);
</script>