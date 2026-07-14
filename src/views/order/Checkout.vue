<template>
  <div>
    <h1 class="page-title">Thanh toán đơn hàng</h1>

    <div v-if="cartStore.loading || addressStore.loading" class="card">
      <div class="card-body">Đang tải dữ liệu...</div>
    </div>

    <template v-else>
      <div v-if="!cartItems.length" class="card">
        <div class="card-body empty-state">
          <p style="margin:0 0 12px;">Giỏ hàng đang trống.</p>
          <router-link to="/cart" class="btn btn-secondary">Về giỏ hàng</router-link>
        </div>
      </div>

      <div v-else class="grid grid-2" style="align-items:start;">
        <div class="card">
          <div class="card-body">
            <h2 class="section-title" style="font-size:22px;">Thông tin đặt hàng</h2>

            <div class="grid grid-2">
              <input v-model.trim="form.customerName" class="input" placeholder="Tên khách hàng" />
              <input v-model.trim="form.customerEmail" class="input" placeholder="Email khách hàng" />
              <input v-model.trim="form.customerPhone" class="input" placeholder="Số điện thoại khách hàng" />

              <div class="address-select-wrap">
                <select v-model="selectedAddressId" class="select">
                  <option :value="null">Nhập địa chỉ thủ công</option>
                  <option
                    v-for="address in addressStore.addresses"
                    :key="address.id"
                    :value="address.id"
                  >
                    {{ address.recipientName }} - {{ address.line1 }}
                  </option>
                </select>

                <button
                  v-if="selectedAddressId === null"
                  type="button"
                  class="btn-outline-address"
                  @click="goToAddressList"
                >
                  + Thêm địa chỉ mới
                </button>
              </div>

              <input v-model.trim="form.shippingRecipientName" class="input" placeholder="Tên người nhận" />
              <input v-model.trim="form.shippingRecipientPhone" class="input" placeholder="Số điện thoại người nhận" />
              <input v-model.trim="form.shippingLine1" class="input" placeholder="Địa chỉ dòng 1" />
              <input v-model.trim="form.shippingLine2" class="input" placeholder="Địa chỉ dòng 2" />
              <input v-model.trim="form.shippingWard" class="input" placeholder="Phường / Xã" />
              <input v-model.trim="form.shippingDistrict" class="input" placeholder="Quận / Huyện" />
              <input v-model.trim="form.shippingCity" class="input" placeholder="Thành phố" />
              <input v-model.trim="form.shippingStateProvince" class="input" placeholder="Tỉnh / Bang" />
              <input v-model.trim="form.shippingPostalCode" class="input" placeholder="Mã bưu chính" />
              <input v-model.trim="form.shippingCountry" class="input" placeholder="Quốc gia" />
            </div>

            <textarea
              v-model.trim="form.note"
              class="textarea"
              placeholder="Ghi chú đơn hàng"
              style="margin-top:16px;"
            />

            <div style="margin-top:16px;">
              <label style="display:block; margin-bottom:8px; font-weight:600;">Phương thức thanh toán</label>
              <select v-model="form.paymentMethod" class="select">
                <option value="COD">COD</option>
                <option value="VNPAY">VNPAY</option>
              </select>
            </div>

            <div style="margin-top:16px;">
              <button class="btn btn-primary" :disabled="submitting" @click="createOrder">
                {{ submitting ? "Đang đặt hàng..." : "Đặt hàng" }}
              </button>
            </div>

            <p v-if="message" class="text-success" style="margin-top:14px;">{{ message }}</p>
            <p v-if="error" class="text-danger" style="margin-top:14px;">{{ error }}</p>
          </div>
        </div>

        <div class="card">
          <div class="card-body">
            <h2 class="section-title" style="font-size:22px;">Tóm tắt đơn hàng</h2>

            <div
              v-for="item in cartItems"
              :key="item.id"
              style="padding:12px 0; border-bottom:1px solid #e5e7eb;"
            >
              <div style="font-weight:700; margin-bottom:6px;">{{ item.productName }}</div>
              <div class="text-muted">{{ item.quantity }} x {{ formatPrice(item.unitPrice) }}</div>
            </div>

            <div class="flex-between" style="margin-top:18px;">
              <span class="text-muted">Tổng cộng</span>
              <strong style="font-size:22px; color:#dc2626;">
                {{ formatPrice(cartTotal) }}
              </strong>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useRouter } from "vue-router";
import api from "../../api/axios";
import { useAddressStore } from "../../stores/address.store";
import { useAuthStore } from "../../stores/auth.store";
import { useCartStore } from "../../stores/cart.store";
import { unwrapData } from "../../utils/api";

const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();
const addressStore = useAddressStore();

const selectedAddressId = ref(null);
const submitting = ref(false);
const message = ref("");
const error = ref("");

const form = reactive({
  shippingAddressId: null,
  billingAddressId: null,
  customerName: "",
  customerEmail: "",
  customerPhone: "",
  shippingRecipientName: "",
  shippingRecipientPhone: "",
  shippingLine1: "",
  shippingLine2: "",
  shippingWard: "",
  shippingDistrict: "",
  shippingCity: "",
  shippingStateProvince: "",
  shippingPostalCode: "",
  shippingCountry: "Vietnam",
  note: "",
  paymentMethod: "COD",
});

const cartItems = computed(() => cartStore.cart?.items || []);
const cartTotal = computed(() => cartStore.cart?.totalAmount || 0);

const fillFromAddress = (address) => {
  if (!address) return;
  form.shippingAddressId = address.id;
  form.billingAddressId = address.id;
  form.shippingRecipientName = address.recipientName || "";
  form.shippingRecipientPhone = address.recipientPhone || "";
  form.shippingLine1 = address.line1 || "";
  form.shippingLine2 = address.line2 || "";
  form.shippingWard = address.ward || "";
  form.shippingDistrict = address.district || "";
  form.shippingCity = address.city || "";
  form.shippingStateProvince = address.stateProvince || "";
  form.shippingPostalCode = address.postalCode || "";
  form.shippingCountry = address.country || "Vietnam";
};

const clearManualAddress = () => {
  form.shippingAddressId = null;
  form.billingAddressId = null;
  form.shippingRecipientName = "";
  form.shippingRecipientPhone = "";
  form.shippingLine1 = "";
  form.shippingLine2 = "";
  form.shippingWard = "";
  form.shippingDistrict = "";
  form.shippingCity = "";
  form.shippingStateProvince = "";
  form.shippingPostalCode = "";
  form.shippingCountry = "Vietnam";
};

watch(selectedAddressId, (id) => {
  if (!id) {
    clearManualAddress();
    return;
  }

  const address = addressStore.addresses.find((item) => item.id === id);
  fillFromAddress(address);
});

const goToAddressList = () => {
  router.push("/addresses");
};

const validateForm = () => {
  if (!form.customerName || !form.customerEmail || !form.customerPhone) {
    return "Vui lòng nhập đầy đủ thông tin khách hàng.";
  }

  if (!form.shippingRecipientName || !form.shippingRecipientPhone || !form.shippingLine1) {
    return "Vui lòng nhập đầy đủ thông tin người nhận và địa chỉ giao hàng.";
  }

  if (!form.shippingCity) {
    return "Vui lòng nhập thành phố giao hàng.";
  }

  return "";
};

const createOrder = async () => {
  error.value = "";
  message.value = "";

  const validationError = validateForm();
  if (validationError) {
    error.value = validationError;
    return;
  }

  submitting.value = true;

  try {
    const payload = {
      shippingAddressId: form.shippingAddressId,
      billingAddressId: form.billingAddressId,
      customerName: form.customerName,
      customerEmail: form.customerEmail,
      customerPhone: form.customerPhone,
      shippingRecipientName: form.shippingRecipientName,
      shippingRecipientPhone: form.shippingRecipientPhone,
      shippingLine1: form.shippingLine1,
      shippingLine2: form.shippingLine2,
      shippingWard: form.shippingWard,
      shippingDistrict: form.shippingDistrict,
      shippingCity: form.shippingCity,
      shippingStateProvince: form.shippingStateProvince,
      shippingPostalCode: form.shippingPostalCode,
      shippingCountry: form.shippingCountry || "Vietnam",
      note: form.note,
      paymentMethod: form.paymentMethod,
    };

    const res = await api.post("/orders", payload);
    const order = unwrapData(res.data);

    message.value = `Đặt hàng thành công. Mã đơn: ${order.orderCode}`;
    await cartStore.fetchCart();
    router.push("/orders");
  } catch (err) {
    error.value =
      err?.response?.data?.message ||
      err?.response?.data?.error ||
      "Đặt hàng thất bại";
  } finally {
    submitting.value = false;
  }
};

const formatPrice = (value) => {
  return new Intl.NumberFormat("vi-VN").format(value || 0) + " đ";
};

onMounted(async () => {
  form.customerName = authStore.currentUser?.fullName || "";
  form.customerEmail = authStore.currentUser?.email || "";
  form.customerPhone = "";

  await Promise.all([cartStore.fetchCart(), addressStore.fetchAddresses()]);

  const defaultAddress =
    addressStore.addresses.find((item) => item.isDefaultShipping) ||
    addressStore.addresses[0];

  if (defaultAddress) {
    selectedAddressId.value = defaultAddress.id;
    fillFromAddress(defaultAddress);

    if (!form.customerPhone) {
      form.customerPhone = defaultAddress.recipientPhone || "";
    }
  }
});
</script>

<style scoped>
.address-select-wrap {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.btn-outline-address {
  border: 1px dashed #d70018;
  background: #fff;
  color: #d70018;
  padding: 10px 12px;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s ease;
}

.btn-outline-address:hover {
  background: #fff1f2;
}
</style>