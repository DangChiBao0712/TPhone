import { defineStore } from 'pinia';
import api from '../api/axios';
import { unwrapData } from '../utils/api';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    currentUser: JSON.parse(localStorage.getItem('currentUser') || 'null'),
  }),

  getters: {
    isAuthenticated: (state) => !!state.accessToken,
    isAdmin: (state) => {
      if (!state.currentUser?.roles) return false;
      return state.currentUser.roles.includes('ROLE_ADMIN');
    },
  },

  actions: {
    setTokens(accessToken, refreshToken) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
    },

    setCurrentUser(user) {
      this.currentUser = user;
      localStorage.setItem('currentUser', JSON.stringify(user));
    },

    async login(payload) {
      const res = await api.post('/auth/login', payload);
      const data = unwrapData(res.data);

      this.setTokens(data.accessToken, data.refreshToken);
      await this.fetchMe();
    },

    async register(payload) {
      const res = await api.post('/auth/register', payload);
      const data = unwrapData(res.data);

      this.setTokens(data.accessToken, data.refreshToken);
      await this.fetchMe();
    },

    async fetchMe() {
      const res = await api.get('/auth/me');
      const user = unwrapData(res.data);
      this.setCurrentUser(user);
      return user;
    },

    async updateProfile(payload) {
      const res = await api.put('/auth/me', payload);
      const user = unwrapData(res.data);
      this.setCurrentUser(user);
      return user;
    },

    async bootstrapAuth() {
      if (!this.accessToken) return;

      try {
        await this.fetchMe();
      } catch (error) {
        this.logout();
      }
    },

    logout() {
      this.accessToken = null;
      this.refreshToken = null;
      this.currentUser = null;

      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('currentUser');
    },
  },
});