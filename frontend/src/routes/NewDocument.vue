<script setup lang="ts">
import { onBeforeMount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { until } from '@vueuse/core'
import { useToast } from 'primevue'
import errorMessages from '@/i18n/errors.json'
import { ROUTE_PATHS } from '@/constants/routes'
import { usePostAdmDocUnit, usePostUliDocUnit } from '@/services/documentUnitService.ts'
import { DocumentTypeCode } from '@/domain/documentType.ts'

const toast = useToast()
const router = useRouter()
const route = useRoute()

onBeforeMount(async () => {
  const { data, error, isFinished } =
    route.meta.documentTypeCode === DocumentTypeCode.LITERATUR_UNSELBSTSTAENDIG
      ? usePostUliDocUnit()
      : usePostAdmDocUnit()
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
