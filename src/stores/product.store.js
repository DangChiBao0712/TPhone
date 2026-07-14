import { defineStore } from 'pinia';
import api from '../api/axios';
import { unwrapData } from '../utils/api';

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],
    productDetail: null,
    pagination: {
      page: 0,
      size: 12,
      totalElements: 0,
      totalPages: 0,
      last: true,
    },
    loading: false,
  }),

  actions: {
    async fetchProducts(params = {}) {
      this.loading = true;
      try {
        const hasSearchParams =
          params &&
          Object.entries(params).some(
            ([, value]) => value !== undefined && value !== null && value !== ''
          );

        let res;
        let data;

        if (hasSearchParams) {
          res = await api.get('/products/search', { params });
          data = unwrapData(res.data);

          let productList = [];
          let paginationSource = {};

          if (Array.isArray(data)) {
            productList = data;
            paginationSource = {
              page: 0,
              size: data.length,
              totalElements: data.length,
              totalPages: data.length ? 1 : 0,
              last: true,
            };
          } else if (Array.isArray(data?.content)) {
            productList = data.content;
            paginationSource = data;
          } else if (Array.isArray(data?.items)) {
            productList = data.items;
            paginationSource = data;
          }

          this.products = productList;
          this.pagination = {
            page: paginationSource.page ?? 0,
            size: paginationSource.size ?? productList.length ?? 12,
            totalElements: paginationSource.totalElements ?? productList.length ?? 0,
            totalPages: paginationSource.totalPages ?? (productList.length ? 1 : 0),
            last: paginationSource.last ?? true,
          };
        } else {
          res = await api.get('/products');
          data = unwrapData(res.data);

          const productList = Array.isArray(data) ? data : [];
          this.products = productList;
          this.pagination = {
            page: 0,
            size: productList.length,
            totalElements: productList.length,
            totalPages: productList.length ? 1 : 0,
            last: true,
          };
        }
      } finally {
        this.loading = false;
      }
    },

    async fetchProductDetail(id) {
      this.loading = true;
      try {
        const res = await api.get(`/products/${id}`);
        this.productDetail = unwrapData(res.data);
      } finally {
        this.loading = false;
      }
    },

    clearProductDetail() {
      this.productDetail = null;
    },
  },
});