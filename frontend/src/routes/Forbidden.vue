<template>
  <div class="flex flex-col gap-16 p-16">
    <div class="flex flex-col gap-4 items-start">
      <h1 class="ris-heading2-regular">Keine Berechtigung (403)</h1>
      <Button @click="home">Zur Startseite</Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import Button from 'primevue/button'

import { useRouter } from 'vue-router'

const router = useRouter()

function home() {
  router.push('/')
}
</script>
