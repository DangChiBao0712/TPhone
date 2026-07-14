<template>
  <footer class="site-footer">
    <div class="footer-top">
      <div class="footer-container">
        <div class="footer-grid">
          <!-- Cột thương hiệu -->
          <div class="footer-col footer-brand-col">
            <div class="footer-brand">
              <img src="/tphone/logo.png" alt="TPhone" class="footer-logo" />
              <div>
                <div class="footer-brand-name">TPhone</div>
                <div class="footer-brand-slogan">Chuyên gia giá trị vượt trội</div>
              </div>
            </div>

            <p class="footer-desc">
              Hệ thống bán điện thoại với giao diện hiện đại, tông đỏ nổi bật,
              tối ưu trải nghiệm mua sắm nhanh chóng và trực quan.
            </p>
          </div>

          <!-- Cột danh mục -->
          <div class="footer-col">
            <h4 class="footer-title">Danh mục</h4>
            <div class="footer-links">
              <router-link to="/products">Tất cả sản phẩm</router-link>
              <router-link to="/products?category=iphone">iPhone</router-link>
              <router-link to="/products?category=ipad">iPad</router-link>
              <router-link to="/products?category=macbook">MacBook</router-link>
            </div>
          </div>

          <!-- Cột liên hệ -->
          <div class="footer-col">
            <h4 class="footer-title">Liên hệ</h4>
            <div class="footer-contact">
              <div>📞 0901 492 155</div>
              <div>📍 TP. Hồ Chí Minh</div>
              <div>✉️ supporttpaqua@gmail.com</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      © 2026 TPhone. All rights reserved.
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.site-footer {
  margin-top: 48px;
  background: #b60018;
  color: #fff;
}

.footer-top {
  padding: 40px 0 24px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1.3fr 1fr 1fr;
  gap: 40px;
  align-items: start;
}

.footer-col {
  display: flex;
  flex-direction: column;
}

.footer-brand-col {
  padding-right: 20px;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.footer-logo {
  width: 56px;
  height: 56px;
  object-fit: contain;
  border-radius: 12px;
  background: #fff;
  padding: 6px;
}

.footer-brand-name {
  font-size: 24px;
  font-weight: 800;
  line-height: 1.2;
}

.footer-brand-slogan {
  font-size: 13px;
  opacity: 0.9;
  margin-top: 2px;
}

.footer-desc {
  max-width: 360px;
  font-size: 14px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.9);
}

.footer-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 16px;
  position: relative;
}

.footer-title::after {
  content: "";
  display: block;
  width: 42px;
  height: 2px;
  background: rgba(255, 255, 255, 0.85);
  margin-top: 8px;
  border-radius: 999px;
}

.footer-links,
.footer-contact {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.92);
  text-decoration: none;
  transition: 0.2s ease;
}

.footer-links a:hover {
  color: #fff;
  transform: translateX(4px);
}

.footer-contact div {
  color: rgba(255, 255, 255, 0.92);
  line-height: 1.6;
}

.footer-bottom {
  text-align: center;
  padding: 16px 24px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.88);
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  background: #980014;
}

@media (max-width: 992px) {
  .footer-grid {
    grid-template-columns: 1fr 1fr;
    gap: 28px;
  }

  .footer-brand-col {
    grid-column: 1 / -1;
    padding-right: 0;
  }

  .footer-desc {
    max-width: 100%;
  }
}

@media (max-width: 576px) {
  .footer-top {
    padding: 32px 0 20px;
  }

  .footer-container {
    padding: 0 16px;
  }

  .footer-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .footer-brand {
    align-items: center;
  }

  .footer-logo {
    width: 50px;
    height: 50px;
  }

  .footer-brand-name {
    font-size: 22px;
  }

  .footer-bottom {
    font-size: 13px;
    padding: 14px 16px;
  }
}
</style>