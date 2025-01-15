<script setup lang="ts">
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import IconCheck from '~icons/material-symbols/check'
import TitleElement from '@/components/TitleElement.vue'
const router = useRouter()
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col p-24">
    <div aria-label="Abgabe" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Abgabe</TitleElement>
      <div class="flex flex-row">
        <Button
          :disabled="false"
          label="Zur Veröffentlichung freigeben"
          :loading="false"
          :text="false"
          @click="router.push({ path: '/' })"
        >
          <template #icon>
            <IconCheck />
          </template>
        </Button>
      </div>
    </div>
  </div>
</template>
