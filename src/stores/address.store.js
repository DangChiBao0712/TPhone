import { defineStore } from 'pinia';
import api from '../api/axios';
import { unwrapData } from '../utils/api';

export const useAddressStore = defineStore('address', {
  state: () => ({
    addresses: [],
    loading: false,
  }),

  actions: {
    async fetchAddresses() {
      this.loading = true;
      try {
        const res = await api.get('/addresses');
        this.addresses = unwrapData(res.data) || [];
      } finally {
        this.loading = false;
      }
    },

    async createAddress(payload) {
      await api.post('/addresses', payload);
      await this.fetchAddresses();
    },

    async updateAddress(id, payload) {
      await api.put(`/addresses/${id}`, payload);
      await this.fetchAddresses();
    },

    async deleteAddress(id) {
      await api.delete(`/addresses/${id}`);
      await this.fetchAddresses();
    },
  },
});