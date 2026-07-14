<template>
  <div class="register-page">
    <AppHeader />

    <div class="register-shell">
      <div class="register-container">
        <div class="register-card">
          <div class="register-header">
            <h1 class="register-title">Đăng ký tài khoản</h1>

            <div class="register-logo-wrap">
              <img
                v-if="showLogo"
                src="/tphone/logo.png"
                alt="TPhone"
                class="register-logo"
                @error="showLogo = false"
              />
              <div v-else class="register-logo-fallback">TP</div>
            </div>

            <p class="register-subtitle">
              Tạo tài khoản để đăng nhập bằng email, theo dõi đơn hàng và quản lý thông tin cá nhân.
            </p>
          </div>

          <form class="register-form" @submit.prevent="handleRegister">
            <section class="form-section">
              <h2 class="section-title">Thông tin cá nhân</h2>

              <div class="form-grid">
                <div class="form-group">
                  <label class="form-label">
                    Họ và tên <span class="required">*</span>
                  </label>
                  <input
                    v-model.trim="form.fullName"
                    type="text"
                    class="form-input"
                    placeholder="Nhập họ và tên"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">
                    Ngày sinh <span class="optional-text">(không bắt buộc)</span>
                  </label>
                  <input
                    v-model="form.birthDate"
                    type="date"
                    class="form-input"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">
                    Số điện thoại <span class="required">*</span>
                  </label>
                  <input
                    v-model.trim="form.phone"
                    type="tel"
                    class="form-input"
                    placeholder="Nhập số điện thoại"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">
                    Email <span class="required">*</span>
                  </label>
                  <input
                    v-model.trim="form.email"
                    type="email"
                    class="form-input"
                    placeholder="Nhập email"
                  />
                  <p class="form-note">
                    Email sẽ dùng để đăng nhập và nhận thông tin đơn hàng.
                  </p>
                </div>
              </div>
            </section>

            <section class="form-section">
              <h2 class="section-title">Tạo mật khẩu</h2>

              <div class="form-grid">
                <div class="form-group">
                  <label class="form-label">
                    Mật khẩu <span class="required">*</span>
                  </label>
                  <div class="password-wrap">
                    <input
                      v-model="form.password"
                      :type="showPassword ? 'text' : 'password'"
                      class="form-input form-input-password"
                      placeholder="Nhập mật khẩu của bạn"
                    />
                    <button
                      type="button"
                      class="password-toggle"
                      @click="showPassword = !showPassword"
                    >
                      {{ showPassword ? "Ẩn" : "Hiện" }}
                    </button>
                  </div>
                  <p class="form-note">
                    Mật khẩu tối thiểu 6 ký tự.
                  </p>
                </div>

                <div class="form-group">
                  <label class="form-label">
                    Nhập lại mật khẩu <span class="required">*</span>
                  </label>
                  <div class="password-wrap">
                    <input
                      v-model="form.confirmPassword"
                      :type="showConfirmPassword ? 'text' : 'password'"
                      class="form-input form-input-password"
                      placeholder="Nhập lại mật khẩu của bạn"
                    />
                    <button
                      type="button"
                      class="password-toggle"
                      @click="showConfirmPassword = !showConfirmPassword"
                    >
                      {{ showConfirmPassword ? "Ẩn" : "Hiện" }}
                    </button>
                  </div>
                </div>
              </div>
            </section>

            <div class="register-options">
              <label class="checkbox-row">
                <input v-model="form.acceptPromo" type="checkbox" />
                <span>Đăng ký nhận tin khuyến mãi từ TPhone</span>
              </label>

              <label class="checkbox-row">
                <input v-model="form.acceptTerms" type="checkbox" />
                <span>
                  Bằng việc đăng ký, bạn đã đọc và đồng ý với
                  <a href="#" @click.prevent>Điều khoản sử dụng</a>
                  và
                  <a href="#" @click.prevent>Chính sách bảo mật</a>.
                </span>
              </label>
            </div>

            <p v-if="errorMessage" class="form-error">
              {{ errorMessage }}
            </p>

            <div class="register-actions">
              <button type="submit" class="register-btn" :disabled="loading">
                {{ loading ? "Đang tạo tài khoản..." : "Đăng ký" }}
              </button>

              <div class="register-links">
                Đã có tài khoản?
                <router-link to="/login">Đăng nhập</router-link>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/auth.store";
import AppHeader from "../../components/AppHeader.vue"; // đổi path nếu project bạn khác

const router = useRouter();
const authStore = useAuthStore();

const loading = ref(false);
const showLogo = ref(true);
const showPassword = ref(false);
const showConfirmPassword = ref(false);
const errorMessage = ref("");

const form = reactive({
  fullName: "",
  email: "",
  phone: "",
  birthDate: "",
  password: "",
  confirmPassword: "",
  acceptPromo: false,
  acceptTerms: true,
});

const validateForm = () => {
  errorMessage.value = "";

  if (!form.fullName) {
    errorMessage.value = "Vui lòng nhập họ và tên.";
    return false;
  }

  if (!form.phone) {
    errorMessage.value = "Vui lòng nhập số điện thoại.";
    return false;
  }

  if (!form.email) {
    errorMessage.value = "Vui lòng nhập email.";
    return false;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(form.email)) {
    errorMessage.value = "Email không hợp lệ.";
    return false;
  }

  if (!form.password || !form.confirmPassword) {
    errorMessage.value = "Vui lòng nhập đầy đủ mật khẩu.";
    return false;
  }

  if (form.password.length < 6) {
    errorMessage.value = "Mật khẩu phải có ít nhất 6 ký tự.";
    return false;
  }

  if (form.password !== form.confirmPassword) {
    errorMessage.value = "Mật khẩu xác nhận không khớp.";
    return false;
  }

  if (!form.acceptTerms) {
    errorMessage.value = "Bạn cần đồng ý điều khoản sử dụng để tiếp tục.";
    return false;
  }

  return true;
};

const handleRegister = async () => {
  if (!validateForm()) return;

  loading.value = true;

  try {
    const payload = {
      fullName: form.fullName,
      email: form.email,
      phone: form.phone,
      password: form.password,
    };

    if (typeof authStore.register === "function") {
      await authStore.register(payload);
    } else if (typeof authStore.signUp === "function") {
      await authStore.signUp(payload);
    }

    router.push("/login");
  } catch (error) {
    console.error("Register failed:", error);
    errorMessage.value =
      error?.response?.data?.message ||
      error?.message ||
      "Đăng ký thất bại. Vui lòng thử lại.";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f3f4f6 100%);
}

.register-shell {
  padding: 28px 16px 40px;
}

.register-container {
  max-width: 760px;
  margin: 0 auto;
}

.register-card {
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 10px 32px rgba(15, 23, 42, 0.07);
  padding: 28px 30px 30px;
  border: 1px solid #eceff3;
}

.register-header {
  text-align: center;
  margin-bottom: 22px;
}

.register-title {
  margin: 0;
  font-size: 32px;
  line-height: 1.2;
  font-weight: 800;
  color: #d70018;
}

.register-logo-wrap {
  margin: 16px auto 14px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-logo {
  width: 76px;
  height: 76px;
  object-fit: contain;
}

.register-logo-fallback {
  width: 76px;
  height: 76px;
  border-radius: 50%;
  background: #fff1f2;
  color: #d70018;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  font-weight: 800;
}

.register-subtitle {
  margin: 0 auto;
  max-width: 540px;
  font-size: 15px;
  color: #667085;
  line-height: 1.7;
}

.register-form {
  margin-top: 6px;
}

.form-section + .form-section {
  margin-top: 22px;
}

.section-title {
  margin: 0 0 14px;
  font-size: 22px;
  font-weight: 800;
  color: #111827;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 700;
  color: #111827;
}

.required {
  color: #d70018;
}

.optional-text {
  color: #9ca3af;
  font-weight: 500;
}

.form-input {
  width: 100%;
  height: 46px;
  border: 1px solid #d9dde3;
  border-radius: 10px;
  padding: 0 14px;
  font-size: 14px;
  outline: none;
  background: #fff;
  color: #111827;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.form-input::placeholder {
  color: #b3b8c2;
}

.form-input:focus {
  border-color: #d70018;
  box-shadow: 0 0 0 4px rgba(215, 0, 24, 0.08);
}

.password-wrap {
  position: relative;
}

.form-input-password {
  padding-right: 66px;
}

.password-toggle {
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
  border: none;
  background: transparent;
  color: #6b7280;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}

.form-note {
  margin: 7px 0 0;
  font-size: 12px;
  line-height: 1.5;
  color: #15803d;
}

.register-options {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.checkbox-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-size: 13px;
  color: #374151;
  line-height: 1.6;
}

.checkbox-row input {
  margin-top: 3px;
}

.checkbox-row a {
  color: #2563eb;
  text-decoration: none;
  font-weight: 600;
}

.checkbox-row a:hover {
  text-decoration: underline;
}

.form-error {
  margin: 16px 0 0;
  color: #dc2626;
  font-size: 13px;
  font-weight: 500;
}

.register-actions {
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px dashed #d7dce3;
}

.register-btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #d70018 0%, #c30015 100%);
  color: #fff;
  font-size: 15px;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.2s ease;
}

.register-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 20px rgba(215, 0, 24, 0.18);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.register-links {
  margin-top: 14px;
  text-align: center;
  font-size: 13px;
  color: #6b7280;
}

.register-links a {
  color: #2563eb;
  text-decoration: none;
  font-weight: 700;
  margin-left: 4px;
}

.register-links a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-shell {
    padding: 18px 12px 28px;
  }

  .register-container {
    max-width: 100%;
  }

  .register-card {
    padding: 22px 16px 24px;
    border-radius: 16px;
  }

  .register-title {
    font-size: 26px;
  }

  .section-title {
    font-size: 20px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .register-logo,
  .register-logo-fallback {
    width: 66px;
    height: 66px;
  }

  .register-logo-fallback {
    font-size: 22px;
  }
}
</style>