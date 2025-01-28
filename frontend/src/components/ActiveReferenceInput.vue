<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import { type ValidationError } from './input/types'
import ComboboxInput from '@/components/ComboboxInput.vue'
import InputField, { LabelPosition } from '@/components/input/InputField.vue'
import TextButton from '@/components/input/TextButton.vue'
import SingleNormInput from '@/components/SingleNormInput.vue'
import { useValidationStore } from '@/composables/useValidationStore'
import LegalForce from '@/domain/legalForce'
import { type NormAbbreviation } from '@/domain/normAbbreviation'
import SingleNorm from '@/domain/singleNorm'
import ComboboxItemService from '@/services/comboboxItemService'
import IconAdd from '~icons/ic/baseline-add'
import ActiveReference, {
  ActiveReferenceDocumentType,
  ActiveReferenceType,
} from '@/domain/activeReference.ts'
import RadioInput from '@/components/input/RadioInput.vue'
import labels from '@/components/activeReferenceInputLabels.json'
import DropdownInput from '@/components/input/DropdownInput.vue'

const props = defineProps<{
  modelValue?: ActiveReference
  modelValueList?: ActiveReference[]
}>()
const emit = defineEmits<{
  'update:modelValue': [value: ActiveReference]
  addEntry: [void]
  cancelEdit: [void]
  removeEntry: [void]
}>()

const validationStore =
  useValidationStore<
    ['referenceType', 'normAbbreviation', 'singleNorm', 'dateOfVersion', 'dateOfRelevance'][number]
  >()

const referenceTypeItems = [
  { label: 'Anwendung', value: `${ActiveReferenceType.ANWENDUNG}` },
  { label: 'Neuregelung', value: `${ActiveReferenceType.NEUREGELUNG}` },
  { label: 'Rechtsgrundlage', value: `${ActiveReferenceType.RECHTSGRUNDLAGE}` },
]

// TODO: Rename norm to activeReference
const norm = ref(new ActiveReference({ ...props.modelValue }))
const lastSavedModelValue = ref(new ActiveReference({ ...props.modelValue }))

const singleNorms = ref(
  props.modelValue?.singleNorms
    ? props.modelValue?.singleNorms?.map((norm) => new SingleNorm({ ...norm }))
    : ([] as SingleNorm[]),
)

/**
 * Data restructuring from norm abbreviation props to combobox item. When item in combobox set, it is validated
 * against already existing norm abbreviations in the list.
 */
const normAbbreviation = computed({
  get: () =>
    norm.value.normAbbreviation
      ? {
          label: norm.value.normAbbreviation.abbreviation,
          value: norm.value.normAbbreviation,
          additionalInformation: norm.value.normAbbreviation.officialLongTitle,
        }
      : undefined,
  set: (newValue) => {
    const newNormAbbreviation = { ...newValue } as NormAbbreviation
    if (newValue) {
      validationStore.remove('normAbbreviation')
      // Check if newValue.abbreviation is already in singleNorms
      const isAbbreviationPresent = props.modelValueList?.some(
        (norm) => norm.normAbbreviation?.abbreviation === newNormAbbreviation.abbreviation,
      )
      if (isAbbreviationPresent) {
        validationStore.add('RIS-Abkürzung bereits eingegeben', 'normAbbreviation')
      } else {
        norm.value.normAbbreviation = newNormAbbreviation
      }
    }
  },
})

/**
 * If there are no format validations, the new norm references is emitted to the parent and automatically
 * sa new emtpy entry is added to the list
 */
async function addNormReference() {
  if (!norm.value.referenceType) {
    console.log('nu')
    validationStore.add('Art der Verweisung fehlt', 'referenceType')
  }
  if (
    !validationStore.getByField('referenceType') &&
    !validationStore.getByField('singleNorm') &&
    !validationStore.getByField('dateOfVersion') &&
    !validationStore.getByField('dateOfRelevance') &&
    !validationStore.getByMessage('RIS-Abkürzung bereits eingegeben').length
  ) {
    const normRef = new ActiveReference({
      ...norm.value,
      singleNorms: singleNorms.value
        .map((singleNorm) =>
          !singleNorm.isEmpty
            ? new SingleNorm({
                ...singleNorm,
                legalForce: singleNorm.legalForce
                  ? new LegalForce({ ...singleNorm.legalForce })
                  : undefined,
              })
            : null,
        )
        .filter((norm) => norm !== null) as SingleNorm[],
    })
    emit('update:modelValue', normRef)
    emit('addEntry')
  }
}

/**
 * Emits to the editable list to removes the current norm reference and empties the local single norm list. The truthy
 * boolean value indicates, that the edit index should be resetted to undefined, ergo show all list items in summary mode.
 */
function removeNormReference() {
  singleNorms.value = []
  norm.value.normAbbreviation = undefined
  emit('removeEntry')
}

/**
 * Adds a new single norm entry to the local single norms list.
 */
function addSingleNormEntry() {
  singleNorms.value.push(new SingleNorm())
}

/**
 * Removes the single norm entry, with the given index.
 * @param {number} index - The index of the list item to be removed
 */
function removeSingleNormEntry(index: number) {
  singleNorms.value.splice(index, 1)
}

function cancelEdit() {
  if (new ActiveReference({ ...props.modelValue }).isEmpty) {
    removeNormReference()
    addSingleNormEntry()
  }
  emit('cancelEdit')
}

/**
 * The child components emit format validations, this function updates the local validation store accordingly in order to
 * prevent the norm reference input from being saved with validation errors
 * @param validationError A validation error has either a message and an instance field or is undefined
 * @param field If the validation error is undefined, the validation store for this field needs to be resetted
 */
function updateFormatValidation(validationError: ValidationError | undefined, field?: string) {
  if (validationError) {
    validationStore.add(
      validationError.message,
      validationError.instance as [
        'referenceType',
        'singleNorm',
        'dateOfVersion',
        'dateOfRelevance',
      ][number],
    )
  } else {
    validationStore.remove(
      field as ['referenceType', 'singleNorm', 'dateOfVersion', 'dateOfRelevance'][number],
    )
  }
}

/**
 * This updates the local norm with the updated model value from the props. It also stores a copy of the last saved
 * model value, because the local norm might change in between. When the new model value is empty, all validation
 * errors are resetted. If it has an amiguous norm reference, the validation store is updated. When the list of
 * single norms is empty, a new empty single norm entry is added.
 */
watch(
  () => props.modelValue,
  () => {
    norm.value = new ActiveReference({ ...props.modelValue })
    lastSavedModelValue.value = new ActiveReference({ ...props.modelValue })
    if (lastSavedModelValue.value.isEmpty) {
      validationStore.reset()
    } else if (lastSavedModelValue.value.hasAmbiguousNormReference) {
      validationStore.add('Mehrdeutiger Verweis', 'normAbbreviation')
    }
    if (singleNorms.value?.length == 0 || !singleNorms.value) addSingleNormEntry()
  },
  { immediate: true, deep: true },
)
</script>

<template>
  <div class="flex flex-col gap-24">
    <div class="flex w-full flex-row justify-between">
      <div class="flex flex-row gap-24">
        <!-- TODO: Adjust ids, no overlap with norms -->
        <InputField
          id="direct"
          label="Norm"
          label-class="ds-label-01-reg"
          :label-position="LabelPosition.RIGHT"
        >
          <RadioInput
            id="direct"
            v-model="norm.referenceDocumentType"
            aria-label="Norm auswählen"
            size="small"
            :value="`${ActiveReferenceDocumentType.NORM}`"
          />
        </InputField>

        <InputField
          id="search"
          label="Verwaltungsvorschrift"
          label-class="ds-label-01-reg"
          :label-position="LabelPosition.RIGHT"
        >
          <RadioInput
            id="search"
            v-model="norm.referenceDocumentType"
            aria-label="Verwaltungsvorschrift auswählen"
            size="small"
            :value="`${ActiveReferenceDocumentType.ADMINISTRATIVE_REGULATION}`"
          />
        </InputField>
      </div>
    </div>
    <InputField
      id="active-reference-type-field"
      v-slot="slotProps"
      label="Art der Verweisung *"
      :validation-error="validationStore.getByField('referenceType')"
    >
      <DropdownInput
        id="id"
        v-model="norm.referenceType"
        aria-label="Art der Verweisung"
        :items="referenceTypeItems"
        placeholder="Bitte auswählen"
        :has-error="slotProps.hasError"
        @focus="validationStore.remove('referenceType')"
      />
    </InputField>
    <InputField
      id="norm-reference-abbreviation-field"
      v-slot="slotProps"
      :label="labels[`${norm.referenceDocumentType}`].risAbbreviation + ` *`"
      :validation-error="validationStore.getByField('normAbbreviation')"
    >
      <ComboboxInput
        id="norm-reference-abbreviation"
        v-model="normAbbreviation"
        :aria-label="labels[`${norm.referenceDocumentType}`].risAbbreviation"
        :has-error="slotProps.hasError"
        :item-service="ComboboxItemService.getRisAbbreviations"
        no-clear
        :placeholder="labels[`${norm.referenceDocumentType}`].risAbbreviationPlaceholder"
        @focus="validationStore.remove('normAbbreviation')"
      ></ComboboxInput>
    </InputField>
    <div v-if="normAbbreviation || norm.normAbbreviationRawValue">
      <SingleNormInput
        v-for="(singleNorm, index) in singleNorms"
        :key="index"
        v-model="singleNorm as SingleNorm"
        aria-label="Einzelnorm"
        :index="index"
        norm-abbreviation="normAbbreviation.abbreviation"
        :show-single-norm-input="norm.referenceDocumentType == ActiveReferenceDocumentType.NORM"
        :show-date-of-relevance-button="
          norm.referenceDocumentType == ActiveReferenceDocumentType.NORM
        "
        @remove-entry="removeSingleNormEntry(index)"
        @update:validation-error="
          (validationError: ValidationError | undefined, field?: string) =>
            updateFormatValidation(validationError, field)
        "
      />
      <div class="flex w-full flex-row justify-between">
        <div>
          <div class="flex gap-24">
            <TextButton
              aria-label="Weitere Einzelnorm"
              button-type="tertiary"
              :icon="IconAdd"
              label="Weitere Einzelnorm"
              size="small"
              @click.stop="addSingleNormEntry"
            />
            <TextButton
              aria-label="Norm speichern"
              button-type="primary"
              label="Übernehmen"
              size="small"
              @click.stop="addNormReference"
            />
            <TextButton
              aria-label="Abbrechen"
              button-type="ghost"
              label="Abbrechen"
              size="small"
              @click.stop="cancelEdit"
            />
          </div>
        </div>
        <TextButton
          v-if="!lastSavedModelValue.isEmpty"
          aria-label="Eintrag löschen"
          button-type="destructive"
          label="Eintrag löschen"
          size="small"
          @click.stop="removeNormReference"
        />
      </div>
    </div>
  </div>
</template>
