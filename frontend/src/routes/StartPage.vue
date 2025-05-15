<script lang="ts" setup>
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'
import DocumentUnits from '@/components/document-units/DocumentUnits.vue'

const router = useRouter()
</script>

<template>
  <div class="flex flex-col gap-16 p-16">
    <div class="flex justify-between">
      <h1 class="ris-heading2-regular">Übersicht Verwaltungsvorschriften</h1>

      <Button
        label="Neue Dokumentationseinheit"
        @click="router.push({ path: '/documentUnit/new' })"
      >
        <template #icon>
          <IconAdd />
        </template>
      </Button>
    </div>
    <DocumentUnits />
  </div>
</template>
