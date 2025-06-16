<script lang="ts" setup>
import { ref } from 'vue'
import InputField from '@/components/input/InputField.vue'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'
import { RisChipsInput } from '@digitalservicebund/ris-ui/components'
import { useValidationStore } from '@/composables/useValidationStore'
import { parseIsoDateToLocal, parseLocalDateToIso } from '@/utils/dateHelpers'
import { getFutureDateErrMessage, getInvalidDateErrMessage } from '@/utils/validators'

const docUnitStore = useDocumentUnitStore()
const validationStore = useValidationStore<'zitierdaten'>()

const zitierdaten = ref<string[]>(
  docUnitStore
    .documentUnit!.zitierdaten?.map((d) => parseIsoDateToLocal(d))
    .filter((d) => d !== null) || [],
)

function onUpdate(dates: string[]) {
  zitierdaten.value = dates
  const isValid = validate(dates)
  if (isValid) {
    docUnitStore.documentUnit!.zitierdaten = zitierdaten.value
      .map((d) => parseLocalDateToIso(d))
      .filter((d) => d !== null)
  }
}

function validate(dates: string[]): boolean {
  const messages = [getInvalidDateErrMessage(dates), getFutureDateErrMessage(dates)].filter(Boolean)
  if (messages.length > 0) {
    validationStore.add(messages[0], 'zitierdaten')
    return false
  } else {
    validationStore.remove('zitierdaten')
    return true
  }
}
</script>

<template>
  <InputField
    id="zitierdaten"
    label="Zitierdaten *"
    class="w-full min-w-0"
    :validation-error="validationStore.getByField('zitierdaten')"
    v-slot="slotProps"
  >
    <RisChipsInput
      id="zitierdaten"
      :model-value="zitierdaten"
      @update:model-value="onUpdate"
      aria-label="Zitierdaten"
      mask="99.99.9999"
      placeholder="TT.MM.JJJJ"
      :has-error="slotProps.hasError"
    />
  </InputField>
</template>
