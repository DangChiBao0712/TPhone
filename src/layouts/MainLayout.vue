<template>
  <div class="app-shell">
    <AppHeader />

    <main class="main-content">
      <div class="page-container">
        <router-view />
      </div>
    </main>

    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from "../components/AppHeader.vue";
import AppFooter from "../components/AppFooter.vue";
</script>