<script setup lang="ts">
import { useRouter } from 'vue-router'
import TextButton from '@/components/input/TextButton.vue'

const router = useRouter()
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col p-24">
    <div aria-label="Abgabe" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Abgabe</TitleElement>
      <div class="flex flex-row">
        <TextButton label="Abgeben" @click="router.push({ path: '/' })" />
      </div>
    </div>
  </div>
</template>
