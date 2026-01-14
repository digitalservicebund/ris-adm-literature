<script setup lang="ts">
import NavbarTop from "./components/NavbarTop.vue";
import Toast from "primevue/toast";
</script>

<template>
  <div class="flex min-h-screen flex-col">
    <Toast />

    <NavbarTop />
    <Suspense>
      <main class="flex grow">
        <router-view class="grow" />
      </main>
    </Suspense>
  </div>
</template>
