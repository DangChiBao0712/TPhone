export const extractErrorMessage = (error, fallback = 'Đã xảy ra lỗi') => {
  const data = error?.response?.data;

  if (!data) return fallback;

  if (typeof data === 'string') return data;

  if (data.message && typeof data.message === 'string') {
    return data.message;
  }

  if (data.error && typeof data.error === 'string') {
    return data.error;
  }

  if (data.errors && typeof data.errors === 'object') {
    const firstEntry = Object.values(data.errors)[0];
    if (Array.isArray(firstEntry)) {
      return firstEntry[0] || fallback;
    }
    if (typeof firstEntry === 'string') {
      return firstEntry;
    }
  }

  if (Array.isArray(data.details) && data.details.length > 0) {
    return data.details[0];
  }

  return fallback;
};