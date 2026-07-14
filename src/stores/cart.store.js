import { defineStore } from "pinia";
import api from "../api/axios";

export const useCartStore = defineStore("cart", {
  state: () => ({
    cart: {
      items: [],
      totalItems: 0,
      totalAmount: 0,
    },
    loading: false,
  }),

  actions: {
    async fetchCart() {
      this.loading = true;
      try {
        const res = await api.get("/cart");
        this.cart = res.data.data;
      } finally {
        this.loading = false;
      }
    },

    async addToCart(productId, quantity = 1) {
      const res = await api.post("/cart/items", {
        productId,
        quantity,
      });
      this.cart = res.data.data;
    },

    async updateCartItem(cartItemId, quantity) {
      const res = await api.put(`/cart/items/${cartItemId}`, {
        quantity,
      });
      this.cart = res.data.data;
    },

    async removeCartItem(cartItemId) {
      const res = await api.delete(`/cart/items/${cartItemId}`);
      this.cart = res.data.data;
    },

    async clearCart() {
      await api.delete("/cart/clear");
      this.cart = {
        items: [],
        totalItems: 0,
        totalAmount: 0,
      };
    },
  },
});