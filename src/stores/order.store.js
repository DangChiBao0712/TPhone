import { defineStore } from 'pinia';
import api from '../api/axios';
import { unwrapData } from '../utils/api';

export const useOrderStore = defineStore('order', {
  state: () => ({
    orders: [],
    orderDetail: null,
    loading: false,
  }),

  actions: {
    async fetchMyOrders() {
      this.loading = true;
      try {
        const res = await api.get('/orders');
        this.orders = unwrapData(res.data) || [];
      } finally {
        this.loading = false;
      }
    },

    async fetchOrderDetail(id) {
      this.loading = true;
      try {
        const res = await api.get(`/orders/${id}`);
        this.orderDetail = unwrapData(res.data);
      } finally {
        this.loading = false;
      }
    },

    clearOrderDetail() {
      this.orderDetail = null;
    },
  },
});