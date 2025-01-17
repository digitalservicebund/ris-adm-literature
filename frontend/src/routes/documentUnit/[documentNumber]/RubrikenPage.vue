<script lang="ts" setup>
import { ref } from 'vue'
import ComboboxInput from '@/components/ComboboxInput.vue'
import TitleElement from '@/components/TitleElement.vue'
import InputField from '@/components/input/InputField.vue'
import DateInput from '@/components/input/DateInput.vue'
import ComboboxItemService from '@/services/comboboxItemService.ts'
import Textarea from 'primevue/textarea'
import TextInput from '@/components/input/TextInput.vue'

const selectedCourt = ref()
const zitierdatum = ref()
const inkrafttretedatum = ref()
const ausserkrafttretedatum = ref()
const selectedDocumentType = ref()
const documentTypeLongText = ref()
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
              ariaLabel="Zitierdatum"
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
      <div class="flex flex-row gap-24">
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
      <div class="border-b-1 border-b-gray-400"></div>
      <div class="flex flex-row gap-24">
        <InputField id="documentType" label="Dokumenttyp *">
          <ComboboxInput
            id="documentType"
            v-model="selectedDocumentType"
            aria-label="Dokumenttyp"
            :item-service="ComboboxItemService.getDocumentTypes"
          ></ComboboxInput>
        </InputField>
        <InputField id="documentTypeLongText" label="Dokumenttyp Zusatz *">
          <TextInput
            id="documentTypeLongText"
            v-model="documentTypeLongText"
            ariaLabel="Dokumenttyp Zusatz"
            :has-error="false"
            size="medium"
          />
        </InputField>
      </div>
      <div class="flex flex-col">
        <div class="flex flex-row gap-24">
          <InputField
            id="inkrafttretedatum"
            label="Datum des Inkrafttretens *"
            class="w-full min-w-0"
          >
            <DateInput
              id="inkrafttretedatum"
              v-model="inkrafttretedatum"
              ariaLabel="Inkrafttretedatum"
              class="ds-input-medium"
            ></DateInput>
          </InputField>
          <InputField
            id="ausserkrafttretedatum"
            label="Datum des Ausserkrafttretens"
            class="w-full min-w-0"
          >
            <DateInput
              id="ausserkrafttretedatum"
              v-model="ausserkrafttretedatum"
              ariaLabel="Ausserkrafttretedatum"
              class="ds-input-medium"
            ></DateInput>
          </InputField>
        </div>
      </div>
      <div class="mt-4">* Pflichtfelder für die Veröffentlichung</div>
    </div>
  </div>
</template>
