<script setup lang="ts">
import NavbarTop from './components/NavbarTop.vue'
</script>

<template>
  <div class="flex min-h-screen flex-col">
    <NavbarTop />
    👉 Falls es um Verwaltungsvorschriften (ADM/VwV) geht, sind Sie am richtigen Ort, aber es gibt
    noch nichts weiter zu sehen.
    <Suspense>
      <main class="flex grow">
        <router-view class="grow" />
      </main>
    </Suspense>
  </div>
</template>
