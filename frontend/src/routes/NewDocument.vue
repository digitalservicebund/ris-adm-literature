<script setup lang="ts">
import { onBeforeMount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostDocUnit } from '@/services/documentUnitService'
import { until } from '@vueuse/core'
import { useToast } from 'primevue'
import errorMessages from '@/i18n/errors.json'
import { ROUTE_PATHS } from '@/constants/routes'
import type { DocumentTypeCode } from '@/domain/documentType'

const toast = useToast()
const router = useRouter()
const route = useRoute()

onBeforeMount(async () => {
  const { data, error, isFinished } = usePostDocUnit(
    route.meta.documentTypeCode as DocumentTypeCode,
  )
  await until(isFinished).toBe(true)

  if (error.value) {
    toast.add({
      severity: 'error',
      summary: errorMessages.DOCUMENT_UNIT_CREATION_FAILED.title,
    })
  }

  if (data.value) {
    const sectionPath = route.matched[0]?.path || ''
    const newPath = `${sectionPath}/${ROUTE_PATHS.DOCUMENT_UNIT_BASE}/${data.value.documentNumber}`
    router.replace(newPath)
  }
})
</script>

<template>
  <div></div>
</template>
