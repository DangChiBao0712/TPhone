<template>
  <div>
    <h1 class="page-title">Địa chỉ của tôi</h1>

    <div class="card" style="margin-bottom: 24px;">
      <div class="card-body">
        <h2 class="section-title" style="font-size: 22px;">
          {{ editingId ? 'Cập nhật địa chỉ' : 'Thêm địa chỉ mới' }}
        </h2>

        <div class="grid grid-2">
          <input v-model="form.recipientName" class="input" placeholder="Tên người nhận" :disabled="submitting" />
          <input v-model="form.recipientPhone" class="input" placeholder="Số điện thoại người nhận" :disabled="submitting" />
          <input v-model="form.line1" class="input" placeholder="Địa chỉ dòng 1" :disabled="submitting" />
          <input v-model="form.line2" class="input" placeholder="Địa chỉ dòng 2" :disabled="submitting" />
          <input v-model="form.ward" class="input" placeholder="Phường / Xã" :disabled="submitting" />
          <input v-model="form.district" class="input" placeholder="Quận / Huyện" :disabled="submitting" />
          <input v-model="form.city" class="input" placeholder="Thành phố" :disabled="submitting" />
          <input v-model="form.stateProvince" class="input" placeholder="Tỉnh / Bang" :disabled="submitting" />
          <input v-model="form.postalCode" class="input" placeholder="Mã bưu chính" :disabled="submitting" />
          <input v-model="form.country" class="input" placeholder="Quốc gia" :disabled="submitting" />
        </div>

        <div style="display:flex; gap:18px; margin-top:16px; flex-wrap:wrap;">
          <label>
            <input type="checkbox" v-model="form.isDefaultShipping" :disabled="submitting" />
            Địa chỉ giao hàng mặc định
          </label>

          <label>
            <input type="checkbox" v-model="form.isDefaultBilling" :disabled="submitting" />
            Địa chỉ thanh toán mặc định
          </label>
        </div>

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

    <div v-if="addressStore.loading" class="card">
      <div class="card-body">Đang tải danh sách địa chỉ...</div>
    </div>

    <div v-else-if="addressStore.addresses.length === 0" class="card">
      <div class="card-body empty-state">Bạn chưa có địa chỉ nào.</div>
    </div>

    <div v-else class="grid">
      <div
        v-for="address in addressStore.addresses"
        :key="address.id"
        class="card"
      >
        <div class="card-body">
          <div class="flex-between" style="align-items:flex-start;">
            <div>
              <p style="margin:0 0 8px; font-size:18px; font-weight:700;">
                {{ address.recipientName }}
              </p>
              <p class="text-muted" style="margin:0 0 8px;">{{ address.recipientPhone }}</p>
            </div>

            <div style="display:flex; gap:8px; flex-wrap:wrap; justify-content:flex-end;">
              <span v-if="address.isDefaultShipping" class="badge">Giao hàng mặc định</span>
              <span v-if="address.isDefaultBilling" class="badge">Thanh toán mặc định</span>
            </div>
          </div>

          <div style="margin-top:10px; line-height:1.7;">
            <div>{{ address.line1 }} {{ address.line2 }}</div>
            <div>{{ address.ward }}, {{ address.district }}, {{ address.city }}</div>
            <div>{{ address.stateProvince }} {{ address.postalCode }}</div>
            <div>{{ address.country }}</div>
          </div>

          <div style="display:flex; gap:10px; margin-top:16px;">
            <button class="btn btn-secondary" :disabled="submitting" @click="editAddress(address)">Sửa</button>
            <button class="btn btn-danger" :disabled="submitting" @click="askDelete(address.id)">Xóa</button>
          </div>
        </div>
      </div>
    </div>

    <ConfirmDialog
      :open="confirmOpen"
      title="Xác nhận xóa địa chỉ"
      message="Bạn có chắc chắn muốn xóa địa chỉ này không?"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { useAddressStore } from '../../stores/address.store';
import AppAlert from '../../components/AppAlert.vue';
import ConfirmDialog from '../../components/ConfirmDialog.vue';
import { extractErrorMessage } from '../../utils/error';

const addressStore = useAddressStore();
const editingId = ref(null);
const message = ref('');
const error = ref('');
const confirmOpen = ref(false);
const deletingId = ref(null);
const submitting = ref(false);

const form = reactive({
  recipientName: '',
  recipientPhone: '',
  line1: '',
  line2: '',
  ward: '',
  district: '',
  city: '',
  stateProvince: '',
  postalCode: '',
  country: 'Vietnam',
  isDefaultShipping: false,
  isDefaultBilling: false,
});

const resetForm = () => {
  editingId.value = null;
  form.recipientName = '';
  form.recipientPhone = '';
  form.line1 = '';
  form.line2 = '';
  form.ward = '';
  form.district = '';
  form.city = '';
  form.stateProvince = '';
  form.postalCode = '';
  form.country = 'Vietnam';
  form.isDefaultShipping = false;
  form.isDefaultBilling = false;
  error.value = '';
};

const handleSubmit = async () => {
  if (submitting.value) return;

  message.value = '';
  error.value = '';
  submitting.value = true;

  try {
    const payload = { ...form };

    if (editingId.value) {
      await addressStore.updateAddress(editingId.value, payload);
      message.value = 'Cập nhật địa chỉ thành công';
    } else {
      await addressStore.createAddress(payload);
      message.value = 'Thêm địa chỉ thành công';
    }

    resetForm();
  } catch (err) {
    error.value = extractErrorMessage(err, 'Không thể lưu địa chỉ');
  } finally {
    submitting.value = false;
  }
};

const editAddress = (address) => {
  if (submitting.value) return;

  editingId.value = address.id;
  form.recipientName = address.recipientName || '';
  form.recipientPhone = address.recipientPhone || '';
  form.line1 = address.line1 || '';
  form.line2 = address.line2 || '';
  form.ward = address.ward || '';
  form.district = address.district || '';
  form.city = address.city || '';
  form.stateProvince = address.stateProvince || '';
  form.postalCode = address.postalCode || '';
  form.country = address.country || 'Vietnam';
  form.isDefaultShipping = !!address.isDefaultShipping;
  form.isDefaultBilling = !!address.isDefaultBilling;
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
    await addressStore.deleteAddress(deletingId.value);
    message.value = 'Xóa địa chỉ thành công';
  } catch (err) {
    error.value = extractErrorMessage(err, 'Không thể xóa địa chỉ');
  } finally {
    submitting.value = false;
    cancelDelete();
  }
};

onMounted(async () => {
  await addressStore.fetchAddresses();
});
</script>