<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import ToolTip from '@/components/ToolTip.vue'
import IconArrowDown from '~icons/ic/baseline-keyboard-arrow-down'
import Button from 'primevue/button'
import InputField from '../input/InputField.vue'
import ComboboxInput from '../ComboboxInput.vue'
import ComboboxItemService from '@/services/comboboxItemService'
import type { AuthorityRegion, NormSettingAuthority } from '@/domain/normSettingAuthority'
import type { Court } from '@/domain/documentUnit'

const props = defineProps<{
  authority: NormSettingAuthority
}>()
const emit = defineEmits<{
  updateAuthority: [authority: NormSettingAuthority]
  removeAuthority: [id: string]
}>()

const court = ref<Court>({ ...props.authority.court } as Court)
const region = ref<AuthorityRegion>({ ...props.authority.region } as AuthorityRegion)
const isEmpty = computed(() => !props.authority.court && !props.authority.region)
const isEditMode = ref<boolean>(isEmpty.value)

const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
}

const onExpandAccordion = () => {
  toggleEditMode()
}

const onClickSave = () => {
  emit('updateAuthority', { ...props.authority, court: court.value, region: region.value })
  toggleEditMode()
}

const onClickCancel = () => {
  // Reset local state
  court.value = props.authority.court as Court
  region.value = props.authority.region as AuthorityRegion
  // Remove authority if empty
  if (isEmpty.value) {
    emit('removeAuthority', props.authority.id)
  }
  toggleEditMode()
}

const onClickDelete = () => {
  emit('removeAuthority', props.authority.id)
  toggleEditMode()
}

const label = computed(() =>
  [props.authority.region?.label, props.authority.court?.label]
    .filter(Boolean)
    .join(', ')
    .toString(),
)

watch(
  () => props.authority,
  (newVal) => {
    court.value = { ...newVal?.court } as Court
    region.value = { ...newVal?.region } as AuthorityRegion
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
            v-model="court"
            aria-label="Normgeber"
            clear-on-choosing-item
            :has-error="false"
            :item-service="ComboboxItemService.getCourts"
          ></ComboboxInput>
        </InputField>
        <InputField id="region" label="Region *" class="w-full">
          <ComboboxInput
            id="region"
            v-model="region"
            aria-label="Region"
            clear-on-choosing-item
            :has-error="false"
            :item-service="ComboboxItemService.getRegions"
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
