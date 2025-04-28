<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import ToolTip from '@/components/ToolTip.vue'
import IconArrowDown from '~icons/ic/baseline-keyboard-arrow-down'
import Button from 'primevue/button'
import InputField from '../input/InputField.vue'
import ComboboxInput from '../ComboboxInput.vue'
import ComboboxItemService from '@/services/comboboxItemService'
import { type NormgeberRegion, type Normgeber, type Organ, OrganType } from '@/domain/normgeber'
import InputText from 'primevue/inputtext'

const props = defineProps<{
  normgeber: Normgeber
}>()
const emit = defineEmits<{
  updateNormgeber: [normgeber: Normgeber]
  removeNormgeber: [id: string]
}>()

const organ = ref<Organ>({ ...props.normgeber.organ } as Organ)
const region = ref<NormgeberRegion>({ ...props.normgeber.region } as NormgeberRegion)
const isEmpty = computed(() => !props.normgeber.organ && !props.normgeber.region)
const isEditMode = ref<boolean>(isEmpty.value)

const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
}

const onExpandAccordion = () => {
  toggleEditMode()
}

const onClickSave = () => {
  emit('updateNormgeber', { ...props.normgeber, organ: organ.value, region: region.value })
  toggleEditMode()
}

const onClickCancel = () => {
  // Reset local state
  organ.value = props.normgeber.organ as Organ
  region.value = props.normgeber.region as NormgeberRegion
  // Remove normgeber if empty
  if (isEmpty.value) {
    emit('removeNormgeber', props.normgeber.id)
  }
  toggleEditMode()
}

const onClickDelete = () => {
  emit('removeNormgeber', props.normgeber.id)
  toggleEditMode()
}

const label = computed(() =>
  [props.normgeber.region?.label, props.normgeber.organ?.label]
    .filter(Boolean)
    .join(', ')
    .toString(),
)

watch(
  () => props.normgeber,
  (newVal) => {
    organ.value = { ...newVal?.organ } as Organ
    region.value = { ...newVal?.region } as NormgeberRegion
  },
)
</script>

<template>
  <div>
    <template v-if="isEditMode">
      <div class="flex flex-row gap-24">
        <InputField id="court" label="Normgeber *" class="w-full">
          <ComboboxInput
            id="court"
            v-model="organ"
            aria-label="Normgeber"
            clear-on-choosing-item
            :has-error="false"
            :item-service="ComboboxItemService.getOrgans"
          ></ComboboxInput>
        </InputField>
        <InputField id="region" label="Region *" class="w-full">
          <ComboboxInput
            v-if="organ?.type === OrganType.Institution"
            id="region"
            v-model="region"
            :has-error="false"
            :item-service="ComboboxItemService.getRegions"
            aria-label="Region"
            clear-on-choosing-item
          ></ComboboxInput>
          <InputText
            v-else
            id="region"
            :value="region.label"
            placeholder="Keine Region zugeordnet"
            aria-label="Region"
            size="small"
            fluid
            readonly
          />
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
