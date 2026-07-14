export const formatPrice = (value) => {
  return new Intl.NumberFormat('vi-VN').format(value || 0) + ' đ';
};

export const formatDateTime = (value) => {
  if (!value) return '';
  return new Date(value).toLocaleString('vi-VN');
};

export const formatDateInput = (value) => {
  if (!value) return '';
  return String(value).slice(0, 10);
};

export const formatOrderStatus = (status) => {
  const map = {
    PENDING: 'Chờ xử lý',
    CONFIRMED: 'Đã xác nhận',
    PROCESSING: 'Đang xử lý',
    SHIPPING: 'Đang giao hàng',
    COMPLETED: 'Hoàn tất',
    CANCELLED: 'Đã hủy',
    REFUNDED: 'Đã hoàn đơn',
  };

  return map[status] || status || 'Không xác định';
};

export const formatPaymentStatus = (status) => {
  const map = {
    PENDING: 'Chờ thanh toán',
    SUCCESS: 'Đã thanh toán',
    FAILED: 'Thanh toán thất bại',
    REFUNDED: 'Đã hoàn tiền',
    CANCELLED: 'Đã hủy',
  };

  return map[status] || status || 'Không xác định';
};

export const formatProductStatus = (status) => {
  const map = {
    ACTIVE: 'Đang kinh doanh',
    DRAFT: 'Bản nháp',
    INACTIVE: 'Ngừng kinh doanh',
    OUT_OF_STOCK: 'Hết hàng',
  };

  return map[status] || status || 'Không xác định';
};

export const formatAccountStatus = (status) => {
  const map = {
    ACTIVE: 'Đang hoạt động',
    LOCKED: 'Đã khóa',
    INACTIVE: 'Ngưng hoạt động',
  };

  return map[status] || status || 'Không xác định';
};

export const formatRole = (role) => {
  const map = {
    ROLE_ADMIN: 'Quản trị viên',
    ROLE_USER: 'Người dùng',
  };

  return map[role] || role;
};