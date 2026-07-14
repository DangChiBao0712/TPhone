<template>
  <div
    v-if="open"
    style="position:fixed; inset:0; background:rgba(15,23,42,0.45); display:flex; align-items:center; justify-content:center; z-index:9999; padding:16px;"
  >
    <div class="card" style="width:100%; max-width:460px;">
      <div class="card-body">
        <h3 style="margin:0 0 10px; font-size:22px;">{{ title }}</h3>
        <p class="text-muted" style="margin:0 0 20px; line-height:1.7;">
          {{ message }}
        </p>

        <div style="display:flex; justify-content:flex-end; gap:10px; flex-wrap:wrap;">
          <button class="btn btn-secondary" @click="$emit('cancel')">Hủy</button>
          <button class="btn btn-danger" @click="$emit('confirm')">Xác nhận</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: 'Xác nhận',
  },
  message: {
    type: String,
    default: 'Bạn có chắc chắn muốn tiếp tục?',
  },
});

defineEmits(['confirm', 'cancel']);
</script>