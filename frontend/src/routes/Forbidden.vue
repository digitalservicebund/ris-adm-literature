<script setup lang="ts">
import Button from 'primevue/button'
</script>

<template>
  <div class="flex flex-col gap-16 p-16">
    <div class="flex flex-col gap-4 items-start">
      <h1 class="ris-heading2-regular">Keine Berechtigung (403)</h1>
      <router-link :to="{ name: 'RootRedirect' }"> <Button label="Zur Startseite" /> </router-link>
    </div>
  </div>
</template>
