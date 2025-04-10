<script setup lang="ts">
import NavbarTop from './components/NavbarTop.vue'
</script>






<template>
  <div class="flex min-h-screen flex-col">
    <NavbarTop />
    <Suspense>
      <main class="flex grow">
        <router-view class="grow" />
      </main>
    </Suspense>
  </div>
</template>
