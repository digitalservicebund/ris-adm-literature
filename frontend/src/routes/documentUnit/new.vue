<script setup lang="ts">
import { onBeforeMount } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onBeforeMount(async () => {
  await router.replace({
    name: 'documentUnit-documentNumber-fundstellen',
    params: { documentNumber: 'KSNR054920707' },
  })
})
</script>

<template>
  <div></div>
</template>
