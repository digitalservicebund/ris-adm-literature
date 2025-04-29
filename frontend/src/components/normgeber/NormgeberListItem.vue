<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import ToolTip from '@/components/ToolTip.vue'
import IconArrowDown from '~icons/ic/baseline-keyboard-arrow-down'
import Button from 'primevue/button'
import InputField from '../input/InputField.vue'
import ComboboxInput from '../ComboboxInput.vue'
import ComboboxItemService from '@/services/comboboxItemService'
import { type Region, type Normgeber, type Institution, InstitutionType } from '@/domain/normgeber'
import InputText from 'primevue/inputtext'

const props = defineProps<{
  normgeber: Normgeber
}>()
const emit = defineEmits<{
  addNormgeber: [normgeber: Normgeber]
  updateNormgeber: [normgeber: Normgeber]
  removeNormgeber: [label: string]
  removeEmptyNormgeber: [void]
}>()

const institution = ref<Institution>(props.normgeber.institution as Institution)
const regions = ref<Region[]>(props.normgeber.regions as Region[])
const selectedRegion = ref<Region>({} as Region)
const regionsInputText = computed(() => {
  if (institution.value && institution.value.regions) {
    return institution.value.regions
      .map((r) => {
        return r.label
      })
      .join(', ')
  } else {
    return ''
  }
})
const regionsIsReadonly = computed(() => {
  return !institution.value || institution.value.type === InstitutionType.LegalEntity
})
const regionLabel = computed(() => {
  if (regionsIsReadonly.value) return 'Region'
  return 'Region *'
})
const isEmpty = computed(() => !props.normgeber.institution && !props.normgeber.regions)
const isEditMode = ref<boolean>(isEmpty.value)
const isNewItem = ref<boolean>(isEmpty.value)
const label = computed(() => {
  let labelTmp = ''
  if (regions.value) labelTmp += regions.value.map((r) => r.label).join(' ') + ', '
  if (institution.value) labelTmp += institution.value.label
  return labelTmp
})

const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
}

const onExpandAccordion = () => {
  toggleEditMode()
}

const onClickSave = () => {
  if (!institution.value) return
  if (institution.value.type === InstitutionType.Institution && !regions.value) return

  const normgeber = {
    institution: institution.value,
    regions: regions.value,
  } as Normgeber

  // block when click save when both inputs are empty
  if (isNewItem.value) emit('addNormgeber', normgeber)
  else emit('updateNormgeber', normgeber)

  toggleEditMode()
}

const onClickCancel = () => {
  // Reset local state
  institution.value = props.normgeber.institution as Institution
  regions.value = props.normgeber.regions as Region[]
  // Remove normgeber if empty
  if (isEmpty.value) {
    emit('removeEmptyNormgeber')
  }
  toggleEditMode()
}

const onClickDelete = () => {
  if (isEmpty.value) {
    emit('removeEmptyNormgeber')
  }
  emit('removeNormgeber', props.normgeber.institution!.label)
  toggleEditMode()
}

watch(
  () => props.normgeber,
  (newVal) => {
    institution.value = newVal.institution as Institution
    regions.value = newVal.regions as Region[]
  },
)

watch(institution, () => {
  if (
    institution.value &&
    institution.value.type == InstitutionType.LegalEntity &&
    institution.value.regions &&
    institution.value.regions.length > 0
  ) {
    regions.value = institution.value.regions
  }
})

watch(selectedRegion, (region) => {
  regions.value = [region]
})
</script>

<template>
  <div>
    <template v-if="isEditMode">
      <div class="flex flex-row gap-24">
        <InputField id="institution" label="Normgeber *" class="w-full">
          <ComboboxInput
            id="institution"
            v-model="institution"
            aria-label="Normgeber"
            clear-on-choosing-item
            :has-error="false"
            :item-service="ComboboxItemService.getInstitutions"
          ></ComboboxInput>
        </InputField>
        <InputField id="region" :label="regionLabel" class="w-full">
          <InputText
            v-if="regionsIsReadonly"
            id="region"
            :value="regionsInputText"
            aria-label="Region"
            size="small"
            fluid
            readonly
          />
          <ComboboxInput
            v-else
            id="region"
            v-model="selectedRegion"
            :has-error="false"
            :item-service="ComboboxItemService.getRegions"
            aria-label="Region"
            clear-on-choosing-item
          ></ComboboxInput>
        </InputField>
      </div>
      <div class="flex w-full gap-16 mt-16">
        <Button
          aria-label="Normgeber übernehmen"
          label="Übernehmen"
          size="small"
          @click.stop="onClickSave"
        />
        <Button
          aria-label="Abbrechen"
          label="Abbrechen"
          size="small"
          text
          @click.stop="onClickCancel"
        />
        <Button
          class="ml-auto"
          aria-label="Eintrag löschen"
          severity="danger"
          label="Eintrag löschen"
          size="small"
          @click.stop="onClickDelete"
        />
      </div>
    </template>
    <template v-else>
      <div class="flex w-full">
        <div class="ris-label1-regular">
          {{ label }}
        </div>
        <ToolTip class="ml-auto" text="Aufklappen">
          <button
            aria-label="Normgeber Editieren"
            class="flex h-32 w-32 items-center justify-center text-blue-800 hover:bg-blue-100 focus:shadow-[inset_0_0_0_0.125rem] focus:shadow-blue-800 focus:outline-none cursor-pointer"
            @click="onExpandAccordion"
          >
            <IconArrowDown />
          </button>
        </ToolTip>
      </div>
    </template>
  </div>
</template>
