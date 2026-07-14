<template>
  <div
    v-if="message"
    :style="alertStyle"
  >
    {{ message }}
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  type: {
    type: String,
    default: 'success',
  },
  message: {
    type: String,
    default: '',
  },
});

const alertStyle = computed(() => {
  const base = {
    padding: '12px 14px',
    borderRadius: '12px',
    marginTop: '14px',
    fontWeight: '600',
    border: '1px solid',
  };

  if (props.type === 'error') {
    return {
      ...base,
      color: '#b91c1c',
      background: '#fef2f2',
      borderColor: '#fecaca',
    };
  }

  if (props.type === 'warning') {
    return {
      ...base,
      color: '#92400e',
      background: '#fffbeb',
      borderColor: '#fde68a',
    };
  }

  return {
    ...base,
    color: '#166534',
    background: '#f0fdf4',
    borderColor: '#bbf7d0',
  };
});
</script>