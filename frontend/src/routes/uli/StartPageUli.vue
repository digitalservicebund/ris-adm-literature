<script lang="ts" setup>
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'
import router from '@/router.ts'
</script>

<template>
  <div class="flex flex-col gap-16 p-24 bg-white">
    <div class="flex justify-between items-end">
      <h1 class="ris-heading2-regular">Übersicht Unselbstständige Literatur</h1>
      <Button
        label="Neue Dokumentationseinheit"
        @click="router.push({ path: '/documentUnit/new' })"
      >
        <template #icon>
          <IconAdd />
        </template>
      </Button>
    </div>
  </div>
</template>
