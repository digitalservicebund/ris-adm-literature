import { computed, ref } from 'vue'
import { useAdmDocUnitStore } from '@/stores/admDocumentUnitStore'
import errorMessages from '@/i18n/errors.json'

function getCurrentTime(dateSaved: Date) {
  const fullHour = ('0' + dateSaved.getHours()).slice(-2)
  const fullMinute = ('0' + dateSaved.getMinutes()).slice(-2)
  return `${fullHour}:${fullMinute}`
}

export function useSaveToRemote() {
  const store = useAdmDocUnitStore()
  const saveIsInProgress = ref(false)
  const lastSaveError = ref<{ title: string } | undefined>(undefined)
  const lastSavedOn = ref<Date | undefined>(undefined)

  const formattedLastSavedOn = computed(
    () => lastSavedOn.value && getCurrentTime(lastSavedOn.value),
  )

  async function triggerSave(): Promise<void> {
    if (saveIsInProgress.value) return

    saveIsInProgress.value = true
    lastSaveError.value = undefined

    try {
      const success = await store.update()

      if (success) {
        lastSavedOn.value = new Date()
      } else {
        lastSaveError.value = { title: errorMessages.DOCUMENT_UNIT_UPDATE_FAILED.title }
      }

      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (error) {
      lastSaveError.value = { title: 'Verbindung fehlgeschlagen' }
    } finally {
      saveIsInProgress.value = false
    }
  }

  return {
    saveIsInProgress,
    triggerSave,
    lastSaveError,
    formattedLastSavedOn,
  }
}
