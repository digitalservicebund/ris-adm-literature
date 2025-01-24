<script lang="ts" setup>
import { ref } from 'vue'
import ComboboxInput from '@/components/ComboboxInput.vue'
import TitleElement from '@/components/TitleElement.vue'
import InputField, { LabelPosition } from '@/components/input/InputField.vue'
import DateInput from '@/components/input/DateInput.vue'
import ComboboxItemService from '@/services/comboboxItemService.ts'
import Textarea from 'primevue/textarea'
import TextInput from '@/components/input/TextInput.vue'
import ChipsInput from '@/components/input/ChipsInput.vue'
import CheckboxInput from '@/components/input/CheckboxInput.vue'
import KeyWords from '@/components/KeyWords.vue'
import TextEditorCategory from '@/components/texts/TextEditorCategory.vue'

const selectedCourt = ref()
const zitierdatum = ref()
const inkrafttretedatum = ref()
const ausserkrafttretedatum = ref()
const selectedDocumentType = ref()
const documentTypeLongText = ref()
const noAktenzeichen = ref()
const noAktenzeichenId = 'noAktenzeichenID'
const fileNumbers = ref()
const outline = ref()
const kurzreferat = ref()
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col gap-24 p-24">
    <div id="formaldaten" aria-label="Formaldaten" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Formaldaten</TitleElement>
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

      <div class="flex flex-row gap-24 w-full">
        <div class="flex flex-col w-full">
          <InputField id="fileNumbers" label="Aktenzeichen *">
            <ChipsInput
              id="fileNumbers"
              v-model="fileNumbers"
              aria-label="Aktenzeichen"
            ></ChipsInput>
          </InputField>
        </div>
        <div class="flex flex-col pt-[30px] w-full">
          <InputField
            :id="noAktenzeichenId"
            label="kein Aktenzeichen"
            label-class="ds-label-01-reg"
            :label-position="LabelPosition.RIGHT"
          >
            <CheckboxInput
              :id="noAktenzeichenId"
              v-model="noAktenzeichen"
              aria-label="Kein Aktenzeichen"
              size="small"
            />
          </InputField>
        </div>
      </div>

      <div class="mt-4">* Pflichtfelder für die Veröffentlichung</div>
    </div>

    <div id="gliederung" aria-label="Gliederung" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Gliederung</TitleElement>
      <div class="flex flex-row gap-24">
        <div class="gap-0 w-full">
          <TextEditorCategory
            id="outline"
            v-model="outline"
            :editable="true"
            label="Gliederung"
            :should-show-button="false"
            :show-formatting-buttons="false"
            field-size="small"
          />
        </div>
      </div>
    </div>

    <div id="inhaltlicheErschliessung" aria-label="Inhaltliche Erschließung" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Inhaltliche Erschließung</TitleElement>
      <div class="flex flex-row gap-24 w-full">
        <div class="flex flex-col w-full">
          <KeyWords data-testid="keywords" />
        </div>
      </div>
    </div>

    <div id="kurzreferat" aria-label="Kurzreferat" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Kurzreferat</TitleElement>
      <div class="flex flex-row gap-24">
        <div class="gap-0 w-full">
          <TextEditorCategory
            id="kurzreferat"
            v-model="kurzreferat"
            :editable="true"
            label="Kurzreferat"
            :should-show-button="false"
            :show-formatting-buttons="false"
            field-size="small"
          />
        </div>
      </div>
    </div>
  </div>
</template>
