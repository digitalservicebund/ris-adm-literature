<script lang="ts" setup>
import { useRouter } from 'vue-router'
import TextButton from '@/components/input/TextButton.vue'

const router = useRouter()
</script>

<template>
  <div class="flex flex-col gap-16 p-16">
    <div class="flex justify-between">
      <h1 class="ds-heading-02-reg">Übersicht Verwaltungsvorschriften</h1>

      <TextButton
        label="Neue Dokumentationseinheit"
        @click="router.push({ path: '/documentUnit/new' })"
      />
    </div>
  </div>
</template>
