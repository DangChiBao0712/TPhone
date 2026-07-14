const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
const API_ORIGIN = API_BASE_URL.replace(/\/api\/?$/, '');

export const unwrapData = (payload) => {
  if (payload && typeof payload === 'object' && 'data' in payload) {
    return payload.data;
  }
  return payload;
};

export const resolveImageUrl = (path) => {
  if (!path) return 'https://via.placeholder.com/400x300?text=No+Image';
  if (/^https?:\/\//i.test(path)) return path;
  return `${API_ORIGIN}${path}`;
};

export { API_BASE_URL, API_ORIGIN };