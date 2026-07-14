<template>
  <header class="header-shell">
    <div class="header-top">
      <div class="header-inner">
        <router-link to="/" class="brand-logo">
          <img src="/tphone/logo.png" class="brand-image" />

          <div class="brand-text">
            <div class="brand-name">TPhone</div>
            <div class="brand-slogan">Tấn Phúc bán điện thoại</div>
          </div>
        </router-link>

        <router-link to="/products" class="catalog-btn">
          <span class="icon-wrap icon-sm" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="none" class="icon-svg">
              <rect x="4" y="4" width="6" height="6" rx="1.5" stroke="currentColor" stroke-width="2" />
              <rect x="14" y="4" width="6" height="6" rx="1.5" stroke="currentColor" stroke-width="2" />
              <rect x="4" y="14" width="6" height="6" rx="1.5" stroke="currentColor" stroke-width="2" />
              <rect x="14" y="14" width="6" height="6" rx="1.5" stroke="currentColor" stroke-width="2" />
            </svg>
          </span>
          <span>Danh mục</span>
        </router-link>

        <form class="search-box" @submit.prevent="handleSearch">
          <input
            v-model.trim="keyword"
            type="text"
            class="search-input"
            placeholder="tìm kiếm... "
          />
          <button type="submit" class="search-btn" aria-label="Tìm kiếm">
            <span class="icon-wrap icon-md" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" class="icon-svg">
                <circle cx="11" cy="11" r="6.5" stroke="currentColor" stroke-width="2" />
                <path d="M16 16L20 20" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
              </svg>
            </span>
          </button>
        </form>

        <div class="quick-actions">
          <a href="tel:0901492155" class="quick-item">
            <span class="icon-wrap icon-lg" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" class="icon-svg">
                <path
                  d="M6.5 4.5C6.9 4.1 7.5 3.9 8.1 4.1L10.6 4.9C11.2 5.1 11.6 5.6 11.6 6.2V8C11.6 8.5 11.4 9 11 9.3L9.8 10.2C10.5 11.8 11.8 13.1 13.5 13.8L14.3 12.6C14.6 12.2 15.1 12 15.6 12H17.4C18 12 18.5 12.4 18.7 13L19.5 15.5C19.7 16.1 19.5 16.7 19.1 17.1L17.8 18.4C17.3 18.9 16.6 19.1 15.9 19C9.7 18.2 5.8 14.3 5 8.1C4.9 7.4 5.1 6.7 5.6 6.2L6.5 4.5Z"
                  stroke="currentColor"
                  stroke-width="1.8"
                  stroke-linejoin="round"
                />
              </svg>
            </span>
            <span class="quick-text">
              <small>Đặt hàng</small>
              <strong>0901 492 155</strong>
            </span>
          </a>

          <!-- <router-link to="/products" class="quick-item">
            <span class="icon-wrap icon-lg" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" class="icon-svg">
                <path d="M3 10L12 4L21 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                <path d="M5 10V19H19V10" stroke="currentColor" stroke-width="2" stroke-linejoin="round" />
                <path d="M9 19V13H15V19" stroke="currentColor" stroke-width="2" stroke-linejoin="round" />
              </svg>
            </span>
            <span class="quick-text">
              <small>Cửa hàng</small>
              <strong>gần bạn</strong>
            </span>
          </router-link> -->

          <router-link to="/orders" class="quick-item">
            <span class="icon-wrap icon-lg" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" class="icon-svg">
                <circle cx="11" cy="11" r="6.5" stroke="currentColor" stroke-width="2" />
                <path d="M16 16L20 20" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
              </svg>
            </span>
            <span class="quick-text">
              <small>Tra cứu</small>
              <strong>đơn hàng</strong>
            </span>
          </router-link>

          <router-link to="/cart" class="quick-item cart-link" :class="{ active: route.path.startsWith('/cart') }">
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
            <span class="icon-wrap icon-lg" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" class="icon-svg">
                <path d="M3 5H5L7.2 14.5H18.2L20.2 8H8.2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                <circle cx="9.5" cy="18.5" r="1.5" fill="currentColor" />
                <circle cx="17.5" cy="18.5" r="1.5" fill="currentColor" />
              </svg>
            </span>
            <span class="quick-text">
              <strong>Giỏ hàng</strong>
            </span>
          </router-link>

          <router-link
            v-if="authStore.isAuthenticated && authStore.isAdmin"
            to="/admin/dashboard"
            class="quick-item admin-shortcut"
            :class="{ active: route.path.startsWith('/admin') }"
          >
            <span class="icon-wrap icon-lg" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" class="icon-svg">
                <path
                  d="M12 3L19 7V12C19 16.5 16.2 19.7 12 21C7.8 19.7 5 16.5 5 12V7L12 3Z"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linejoin="round"
                />
                <path
                  d="M9.5 12L11 13.5L14.5 10"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
            </span>
<span class="quick-text single-line-text">
  <strong>Quản trị</strong>
</span>
          </router-link>

          <div v-if="authStore.isAuthenticated" class="account-dropdown" ref="dropdownRef">
            <button
              type="button"
              class="account-trigger"
              :class="{ active: showAccountMenu }"
              @click="showAccountMenu = !showAccountMenu"
            >
              <span class="icon-wrap icon-lg" aria-hidden="true">
                <svg viewBox="0 0 24 24" fill="none" class="icon-svg">
                  <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2" />
                  <path d="M4 20C5.8 16.8 8.5 15 12 15C15.5 15 18.2 16.8 20 20" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                </svg>
              </span>
              <span class="quick-text">
                <small>Tài khoản</small>
                <strong class="user-name">{{ displayName }}</strong>
              </span>
              <span class="caret">▼</span>
            </button>

            <div v-if="showAccountMenu" class="account-menu">
              <router-link class="account-menu-item" to="/orders" @click="closeMenu">
                <span>🕘</span>
                <span>Đơn hàng</span>
              </router-link>

              <router-link class="account-menu-item" to="/profile" @click="closeMenu">
                <span>🪪</span>
                <span>Hồ sơ</span>
              </router-link>

              <div class="account-menu-divider"></div>

              <button type="button" class="account-menu-item logout-item" @click="handleLogout">
                <span>⏻</span>
                <span>Đăng xuất</span>
              </button>
            </div>
          </div>

          <router-link v-else to="/login" class="quick-item" :class="{ active: route.path === '/login' }">
            <span class="icon-wrap icon-lg" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" class="icon-svg">
                <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2" />
                <path d="M4 20C5.8 16.8 8.5 15 12 15C15.5 15 18.2 16.8 20 20" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
              </svg>
            </span>
            <span class="quick-text">
              <strong>Đăng nhập</strong>
            </span>
          </router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth.store';
import { useCartStore } from '../stores/cart.store';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const cartStore = useCartStore();

const keyword = ref('');
const showAccountMenu = ref(false);
const dropdownRef = ref(null);

const cartCount = computed(() => cartStore.cart?.totalItems || 0);
const displayName = computed(() => authStore.currentUser?.fullName || 'Người dùng');

onMounted(async () => {
  if (route.query.keyword) {
    keyword.value = String(route.query.keyword);
  }

  if (authStore.isAuthenticated) {
    await cartStore.fetchCart();
  }

  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

watch(
  () => route.query.keyword,
  (value) => {
    keyword.value = value ? String(value) : '';
  }
);

watch(
  () => route.fullPath,
  () => {
    showAccountMenu.value = false;
  }
);

const handleClickOutside = (event) => {
  if (!dropdownRef.value) return;
  if (!dropdownRef.value.contains(event.target)) {
    showAccountMenu.value = false;
  }
};

const closeMenu = () => {
  showAccountMenu.value = false;
};

const handleLogout = () => {
  closeMenu();
  authStore.logout();
  cartStore.$reset();
  router.push('/login');
};

const handleSearch = () => {
  const q = keyword.value.trim();

  if (!q) {
    router.push('/products');
    return;
  }

  router.push({
    path: '/products',
    query: { keyword: q },
  });
};
</script>

<style scoped>
.header-shell {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: #d70018;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}

.header-top {
  background: linear-gradient(180deg, #d70018 0%, #c8102e 100%);
  color: #ffffff;
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 18px;
}

.header-top .header-inner {
  min-height: 76px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 230px;
  color: #fff;
  text-decoration: none;
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.05;
}

.brand-name {
  font-size: 2rem;
  font-weight: 800;
}

.brand-slogan {
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.03em;
}

.catalog-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-width: 134px;
  justify-content: center;
  height: 42px;
  padding: 0 16px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  color: #fff;
  text-decoration: none;
  font-weight: 700;
}

.search-box {
  flex: 1;
  max-width: 560px;
  height: 46px;
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 999px;
  overflow: hidden;
}

.search-input {
  flex: 1;
  height: 100%;
  border: none;
  outline: none;
  padding: 0 18px;
  font-size: 15px;
}

.search-btn {
  width: 58px;
  height: 46px;
  border: none;
  background: transparent;
  color: #d70018;
  cursor: pointer;
}

.quick-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

.quick-item,
.account-trigger {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  height: 46px;
  padding: 0 12px;
  border-radius: 14px;
  color: #fff;
  text-decoration: none;
  border: none;
  background: transparent;
  cursor: pointer;
}

.quick-item:hover,
.account-trigger:hover,
.account-trigger.active,
.quick-item.active {
  background: rgba(255, 255, 255, 0.12);
}

.quick-text {
  display: flex;
  flex-direction: column;
  line-height: 1.05;
  text-align: left;
}

.quick-text small {
  font-size: 12px;
  opacity: 0.92;
}

.quick-text strong {
  font-size: 14px;
  font-weight: 700;
}

.user-name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.caret {
  font-size: 11px;
  opacity: 0.85;
}

.account-dropdown {
  position: relative;
}

.account-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 210px;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.18);
  border: 1px solid #eceff3;
  z-index: 1200;
}

.account-menu-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: #fff;
  border: none;
  text-decoration: none;
  color: #374151;
  font-size: 15px;
  cursor: pointer;
}

.account-menu-item:hover {
  background: #f8fafc;
}

.account-menu-divider {
  height: 1px;
  background: #eceff3;
}

.logout-item {
  color: #dc2626;
}

/* Cân layout tổng */
.quick-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Admin giống các nút khác */
.admin-shortcut {
  background: transparent;
  border: none;
  padding: 0 12px;
  min-width: auto;
}

/* Hover giống hệ thống */
.admin-shortcut:hover,
.admin-shortcut.active {
  background: rgba(255, 255, 255, 0.12);
}

/* Text 1 dòng cho cân */
.single-line-text {
  justify-content: center;
}

.single-line-text strong {
  font-size: 14px;
  font-weight: 700;
  line-height: 1;
}

.icon-wrap {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.icon-sm {
  width: 18px;
  height: 18px;
}

.icon-md {
  width: 22px;
  height: 22px;
}

.icon-lg {
  width: 24px;
  height: 24px;
}

.icon-svg {
  width: 100%;
  height: 100%;
}

.cart-link {
  position: relative;
}

.cart-badge {
  position: absolute;
  top: -6px;
  right: -2px;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  border-radius: 999px;
  background: #fff;
  color: #d70018;
  font-size: 12px;
  font-weight: 800;
  display: grid;
  place-items: center;
}

.brand-image {
  width: 42px;
  height: 42px;
  object-fit: contain;
}

@media (max-width: 1200px) {
  .header-top .header-inner {
    flex-wrap: wrap;
    padding-top: 12px;
    padding-bottom: 12px;
  }

  .search-box {
    order: 3;
    width: 100%;
    max-width: 100%;
  }

  .quick-actions {
    flex-wrap: wrap;
  }
}
</style>