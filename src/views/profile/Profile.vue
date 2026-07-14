<template>
  <div>
    <h1 class="page-title">Thông tin tài khoản</h1>

    <div v-if="loading" class="card">
      <div class="card-body">Đang tải thông tin tài khoản...</div>
    </div>

    <div v-else class="card">
      <div class="card-body">
        <div class="grid grid-2">
          <div class="form-group">
            <label class="field-label">Họ và tên</label>
            <input
              v-model="form.fullName"
              class="input"
              placeholder="Nhập họ và tên"
              :disabled="submitting"
            />
          </div>

          <div class="form-group">
            <label class="field-label">Email</label>
            <input
              :value="authStore.currentUser?.email || ''"
              class="input"
              disabled
            />
            <small class="field-hint">Email dùng để đăng nhập, hiện chưa cho phép sửa.</small>
          </div>

          <div class="form-group">
            <label class="field-label">Số điện thoại</label>
            <input
              v-model="form.phone"
              class="input"
              placeholder="Nhập số điện thoại"
              :disabled="submitting"
            />
          </div>

          <div class="form-group">
            <label class="field-label">Ngày sinh</label>
            <input
              v-model="form.birthDate"
              type="date"
              class="input"
              :disabled="submitting"
            />
          </div>

          <div class="form-group" style="grid-column: 1 / -1;">
            <label class="field-label">Avatar URL</label>
            <input
              v-model="form.avatarUrl"
              class="input"
              placeholder="https://... hoặc /uploads/..."
              :disabled="submitting"
            />
          </div>
        </div>

        <div
          v-if="form.avatarUrl"
          style="
            margin-top: 16px;
            width: 140px;
            height: 140px;
            border-radius: 16px;
            overflow: hidden;
            border: 1px solid #e5e7eb;
            background: #fff;
          "
        >
          <img
            :src="form.avatarUrl"
            alt="Avatar preview"
            style="width:100%; height:100%; object-fit:cover; display:block;"
          />
        </div>

        <div class="grid grid-2" style="margin-top: 18px;">
          <div>
            <p class="text-muted" style="margin:0 0 6px;">Vai trò</p>
            <p style="margin:0; font-weight:700;">{{ displayRoles }}</p>
          </div>

          <div>
            <p class="text-muted" style="margin:0 0 6px;">Trạng thái</p>
            <p style="margin:0; font-weight:700;">{{ displayStatus }}</p>
          </div>
        </div>

        <div style="display:flex; gap:10px; margin-top:18px; flex-wrap:wrap;">
          <button class="btn btn-primary" :disabled="submitting" @click="handleSubmit">
            {{ submitting ? 'Đang lưu...' : 'Cập nhật hồ sơ' }}
          </button>

          <button class="btn btn-secondary" :disabled="submitting" @click="resetForm">
            Khôi phục
          </button>
        </div>

        <AppAlert type="success" :message="message" />
        <AppAlert type="error" :message="error" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import AppAlert from '../../components/AppAlert.vue';
import { useAuthStore } from '../../stores/auth.store';
import { extractErrorMessage } from '../../utils/error';
import { formatAccountStatus, formatDateInput, formatRole } from '../../utils/format';

const authStore = useAuthStore();

const loading = ref(false);
const submitting = ref(false);
const message = ref('');
const error = ref('');

const form = reactive({
  fullName: '',
  phone: '',
  birthDate: '',
  avatarUrl: '',
});

const fillForm = (user) => {
  form.fullName = user?.fullName || '';
  form.phone = user?.phone || '';
  form.birthDate = formatDateInput(user?.birthDate);
  form.avatarUrl = user?.avatarUrl || '';
};

const resetForm = () => {
  fillForm(authStore.currentUser);
  message.value = '';
  error.value = '';
};

const displayRoles = computed(() => {
  const roles = authStore.currentUser?.roles || [];
  if (!roles.length) return 'Chưa có';
  return roles.map(formatRole).join(', ');
});

const displayStatus = computed(() => {
  return formatAccountStatus(authStore.currentUser?.status);
});

const handleSubmit = async () => {
  message.value = '';
  error.value = '';

  submitting.value = true;
  try {
    await authStore.updateProfile({
      fullName: form.fullName,
      phone: form.phone,
      birthDate: form.birthDate || null,
      avatarUrl: form.avatarUrl,
    });

    fillForm(authStore.currentUser);
    message.value = 'Cập nhật hồ sơ thành công.';
  } catch (err) {
    error.value = extractErrorMessage(err, 'Không thể cập nhật hồ sơ.');
  } finally {
    submitting.value = false;
  }
};

onMounted(async () => {
  if (!authStore.currentUser) {
    loading.value = true;
    try {
      await authStore.fetchMe();
    } finally {
      loading.value = false;
    }
  }

  fillForm(authStore.currentUser);
});
</script>