<script lang="ts" setup>
import { ref } from 'vue'
import ComboboxInput from '@/components/ComboboxInput.vue'
import TitleElement from '@/components/TitleElement.vue'
import InputField from '@/components/input/InputField.vue'
import DateInput from '@/components/input/DateInput.vue'
import ComboboxItemService from '@/services/comboboxItemService.ts'
import Textarea from 'primevue/textarea'

const selectedCourt = ref()
const zitierdatum = ref()
const inkrafttretedatum = ref()
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col p-24">
    <div aria-label="Formaldaten" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Formaldaten</TitleElement>
      <div class="flex flex-col">
        <div class="flex flex-row gap-24">
          <InputField id="zitierdatum" label="Zitierdatum *" class="w-full min-w-0">
            <DateInput
              id="zitierdatum"
              v-model="zitierdatum"
              aria-label="fasdf"
              class="ds-input-medium"
            ></DateInput>
          </InputField>
          <InputField id="courtInput" label="Normgeber *" class="w-full">
            <ComboboxInput
              id="courtInput"
              v-model="selectedCourt"
              aria-label="Normgeber"
              clear-on-choosing-item
              :has-error="false"
              :item-service="ComboboxItemService.getCourts"
            ></ComboboxInput>
          </InputField>
        </div>
      </div>
      <div class="flex flex-col">
        <InputField id="langue" label="Amtl. Langüberschrift *">
          <Textarea
            id="langue"
            class="w-full"
            v-bind="{
              autoResize: true,
            }"
          />
        </InputField>
      </div>

      <InputField id="inkrafttretedatum" label="Inkrafttretedatum *" class="w-full min-w-0">
            <DateInput
              id="inkrafttretedatum"
              v-model="inkrafttretedatum"
              aria-label="Inkrafttretedatum"
              class="ds-input-medium"
            ></DateInput>
          </InputField>

    </div>
  </div>
</template>
