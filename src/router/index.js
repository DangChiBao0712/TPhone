import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth.store";

import MainLayout from "../layouts/MainLayout.vue";
import Home from "../views/Home.vue";
import Login from "../views/auth/Login.vue";
import Register from "../views/auth/Register.vue";
import ProductList from "../views/product/ProductList.vue";
import ProductDetail from "../views/product/ProductDetail.vue";
import Cart from "../views/cart/Cart.vue";
import Checkout from "../views/order/Checkout.vue";
import OrderHistory from "../views/order/OrderHistory.vue";
import OrderDetail from "../views/order/OrderDetail.vue";
import AddressList from "../views/profile/AddressList.vue";
import Profile from "../views/profile/Profile.vue";
import Dashboard from "../views/admin/Dashboard.vue";
import AdminAnalytics from "../views/admin/AdminAnalytics.vue";
import ProductManagement from "../views/admin/ProductManagement.vue";
import BrandManagement from "../views/admin/BrandManagement.vue";
import CategoryManagement from "../views/admin/CategoryManagement.vue";
import AdminCustomerManagement from "../views/admin/AdminCustomerManagement.vue";
import AdminOrderManagement from "../views/admin/AdminOrderManagement.vue";
import NotFound from "../views/NotFound.vue";

const routes = [
  {
    path: "/",
    component: MainLayout,
    children: [
      { path: "", name: "home", component: Home },
      { path: "products", name: "products", component: ProductList },
      { path: "products/:id", name: "product-detail", component: ProductDetail },
      { path: "cart", name: "cart", component: Cart, meta: { requiresAuth: true } },
      { path: "checkout", name: "checkout", component: Checkout, meta: { requiresAuth: true } },
      { path: "orders", name: "orders", component: OrderHistory, meta: { requiresAuth: true } },
      { path: "orders/:id", name: "order-detail", component: OrderDetail, meta: { requiresAuth: true } },
      { path: "addresses", name: "addresses", component: AddressList, meta: { requiresAuth: true } },
      { path: "profile", name: "profile", component: Profile, meta: { requiresAuth: true } },

      { path: "admin/dashboard", name: "admin-dashboard", component: Dashboard, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: "admin/analytics", name: "admin-analytics", component: AdminAnalytics, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: "admin/products", name: "admin-products", component: ProductManagement, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: "admin/brands", name: "admin-brands", component: BrandManagement, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: "admin/categories", name: "admin-categories", component: CategoryManagement, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: "admin/customers", name: "admin-customers", component: AdminCustomerManagement, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: "admin/orders", name: "admin-orders", component: AdminOrderManagement, meta: { requiresAuth: true, requiresAdmin: true } },
    ],
  },
  {
    path: "/login",
    name: "login",
    component: Login,
    meta: { guestOnly: true },
  },
  {
    path: "/register",
    name: "register",
    component: Register,
    meta: { guestOnly: true },
  },
  {
    path: "/:pathMatch(.*)*",
    name: "not-found",
    component: NotFound,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();

  if (authStore.accessToken && !authStore.currentUser) {
    await authStore.bootstrapAuth();
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return next("/login");
  }

  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    return next("/");
  }

  if (to.meta.guestOnly && authStore.isAuthenticated) {
    return next("/");
  }

  next();
});

export default router;