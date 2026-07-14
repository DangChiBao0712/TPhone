<template>
  <div class="chart-card">
    <div class="chart-header">
      <h3>{{ title }}</h3>
      <p>{{ subtitle }}</p>
    </div>

    <div v-if="!points || points.length === 0" class="empty-state">
      Chưa có dữ liệu biểu đồ.....
    </div>

    <div v-else class="chart-wrapper">
      <div class="bars">
        <div v-for="(point, index) in points" :key="index" class="bar-item">
          <div class="bar-value">{{ formatPrice(point.value) }}</div>
          <div class="bar-track">
            <div class="bar-fill" :style="{ height: getHeight(point.value) }"></div>
          </div>
          <div class="bar-label">{{ point.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { formatPrice } from '../../utils/format';

const props = defineProps({
  title: {
    type: String,
    default: 'Biểu đồ',
  },
  subtitle: {
    type: String,
    default: '',
  },
  points: {
    type: Array,
    default: () => [],
  },
});

const maxValue = computed(() => {
  const values = props.points.map((item) => Number(item?.value || 0));
  return Math.max(...values, 0);
});

const getHeight = (value) => {
  const max = maxValue.value || 1;
  const percent = (Number(value || 0) / max) * 100;
  return `${Math.max(percent, 4)}%`;
};
</script>

<style scoped>
.chart-card {
  background: #fff;
  border: 1px solid #edf0f4;
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.05);
}

.chart-header {
  margin-bottom: 18px;
}

.chart-header h3 {
  margin: 0 0 6px;
  font-size: 22px;
  color: #111827;
}

.chart-header p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.chart-wrapper {
  overflow-x: auto;
}

.bars {
  min-height: 320px;
  display: flex;
  align-items: flex-end;
  gap: 14px;
  padding-top: 18px;
}

.bar-item {
  min-width: 54px;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar-value {
  font-size: 11px;
  font-weight: 700;
  color: #374151;
  text-align: center;
  line-height: 1.3;
  min-height: 30px;
}

.bar-track {
  width: 100%;
  height: 220px;
  background: #eef2f7;
  border-radius: 14px;
  display: flex;
  align-items: flex-end;
  padding: 6px;
}

.bar-fill {
  width: 100%;
  border-radius: 10px;
  background: linear-gradient(180deg, #22c55e 0%, #2563eb 100%);
  transition: 0.3s ease;
}

.bar-label {
  font-size: 12px;
  font-weight: 700;
  color: #111827;
  text-align: center;
}

.empty-state {
  color: #6b7280;
  font-size: 14px;
}
</style>