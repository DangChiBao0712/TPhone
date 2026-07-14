<template>
  <div class="login-page">
    <AppHeader />

    <div class="login-shell">
      <div class="login-container">
        <div class="login-card">
          <div class="login-header">
            <h1 class="login-title">Đăng nhập tài khoản</h1>

            <div class="login-logo-wrap">
              <img
                v-if="showLogo"
                src="/tphone/logo.png"
                alt="TPhone"
                class="login-logo"
                @error="showLogo = false"
              />
              <div v-else class="login-logo-fallback">TP</div>
            </div>

            <p class="login-subtitle">
              Sử dụng email đã đăng ký để đăng nhập, theo dõi đơn hàng và quản lý tài khoản của bạn.
            </p>
          </div>

          <form class="login-form" @submit.prevent="handleLogin">
            <section class="form-section">
              <h2 class="section-title">Thông tin đăng nhập</h2>

              <div class="form-grid">
                <div class="form-group form-group-full">
                  <label class="form-label">
                    Email <span class="required">*</span>
                  </label>
                  <input
                    v-model.trim="form.email"
                    type="email"
                    class="form-input"
                    placeholder="Nhập email của bạn"
                  />
                  <p class="form-note">
                    Vui lòng nhập đúng email đã dùng khi đăng ký.
                  </p>
                </div>

                <div class="form-group form-group-full">
                  <label class="form-label">
                    Mật khẩu <span class="required">*</span>
                  </label>
                  <div class="password-wrap">
                    <input
                      v-model="form.password"
                      :type="showPassword ? 'text' : 'password'"
                      class="form-input form-input-password"
                      placeholder="Nhập mật khẩu"
                      @keyup.enter="handleLogin"
                    />
                    <button
                      type="button"
                      class="password-toggle"
                      @click="showPassword = !showPassword"
                    >
                      {{ showPassword ? "Ẩn" : "Hiện" }}
                    </button>
                  </div>
                </div>
              </div>
            </section>

            <div class="login-links">
              <router-link to="/forgot-password">Quên mật khẩu</router-link>
              <router-link to="/register">Đăng ký thành viên</router-link>
            </div>

            <p v-if="error" class="form-error">{{ error }}</p>

            <div class="login-actions">
              <button type="submit" class="login-btn" :disabled="loading">
                {{ loading ? "Đang đăng nhập..." : "Đăng nhập" }}
              </button>

              <router-link to="/register" class="register-btn">
                Đăng ký thành viên
              </router-link>

              <router-link to="/" class="back-home">
                Quay lại trang chủ
              </router-link>
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
import AppHeader from "../../components/AppHeader.vue"; // đổi path nếu khác

const router = useRouter();
const authStore = useAuthStore();

const loading = ref(false);
const error = ref("");
const showLogo = ref(true);
const showPassword = ref(false);

const form = reactive({
  email: "",
  password: "",
});

const handleLogin = async () => {
  error.value = "";

  if (!form.email || !form.password) {
    error.value = "Vui lòng nhập email và mật khẩu.";
    return;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(form.email)) {
    error.value = "Email không hợp lệ.";
    return;
  }

  loading.value = true;

  try {
    await authStore.login({
      email: form.email,
      password: form.password,
    });

    router.push("/");
  } catch (err) {
    error.value =
      err?.response?.data?.message ||
      err?.response?.data?.errors?.[0]?.defaultMessage ||
      "Đăng nhập thất bại.";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f3f4f6 100%);
}

.login-shell {
  padding: 28px 16px 40px;
}

.login-container {
  max-width: 640px;
  margin: 0 auto;
}

.login-card {
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 10px 32px rgba(15, 23, 42, 0.07);
  padding: 28px 30px 30px;
  border: 1px solid #eceff3;
}

.login-header {
  text-align: center;
  margin-bottom: 22px;
}

.login-title {
  margin: 0;
  font-size: 32px;
  line-height: 1.2;
  font-weight: 800;
  color: #d70018;
}

.login-logo-wrap {
  margin: 16px auto 14px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-logo {
  width: 76px;
  height: 76px;
  object-fit: contain;
}

.login-logo-fallback {
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

.login-subtitle {
  margin: 0 auto;
  max-width: 480px;
  font-size: 15px;
  color: #667085;
  line-height: 1.7;
}

.login-form {
  margin-top: 6px;
}

.form-section {
  margin-top: 4px;
}

.section-title {
  margin: 0 0 14px;
  font-size: 22px;
  font-weight: 800;
  color: #111827;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group-full {
  grid-column: 1 / -1;
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
  color: #2563eb;
}

.login-links {
  margin-top: 18px;
  display: flex;
  justify-content: center;
  gap: 18px;
  flex-wrap: wrap;
}

.login-links a {
  color: #2563eb;
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
}

.login-links a:hover {
  text-decoration: underline;
}

.form-error {
  margin: 16px 0 0;
  color: #dc2626;
  font-size: 13px;
  font-weight: 500;
  text-align: center;
}

.login-actions {
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px dashed #d7dce3;
}

.login-btn,
.register-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 800;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  box-sizing: border-box;
  transition: all 0.2s ease;
}

.login-btn {
  border: none;
  background: linear-gradient(135deg, #d70018 0%, #c30015 100%);
  color: #fff;
  cursor: pointer;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 20px rgba(215, 0, 24, 0.18);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.register-btn {
  margin-top: 12px;
  border: 1px solid #cfd6df;
  background: #fff;
  color: #111827;
}

.register-btn:hover {
  background: #f8fafc;
}

.back-home {
  margin-top: 14px;
  display: block;
  text-align: center;
  color: #6b7280;
  text-decoration: none;
  font-size: 13px;
}

.back-home:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .login-shell {
    padding: 18px 12px 28px;
  }

  .login-container {
    max-width: 100%;
  }

  .login-card {
    padding: 22px 16px 24px;
    border-radius: 16px;
  }

  .login-title {
    font-size: 26px;
  }

  .section-title {
    font-size: 20px;
  }

  .login-logo,
  .login-logo-fallback {
    width: 66px;
    height: 66px;
  }

  .login-logo-fallback {
    font-size: 22px;
  }
}
</style>