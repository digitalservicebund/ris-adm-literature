<script lang="ts" setup>
import InputField from "@/components/input/InputField.vue";
import { Textarea } from "primevue";
import { computed } from "vue";

const hauptsachtitel = defineModel<string>("hauptsachtitel");
const hauptsachtitelZusatz = defineModel<string>("hauptsachtitelZusatz");
const dokumentarischerTitel = defineModel<string>("dokumentarischerTitel");

const hauptsachtitelLabel = computed(() =>
  dokumentarischerTitel.value ? "Hauptsachtitel" : "Hauptsachtitel *",
);
const dokumentarischerTitelLabel = computed(() =>
  hauptsachtitel.value ? "Dokumentarischer Titel" : "Dokumentarischer Titel *",
);
</script>

<template>
  <div class="flex flex-col w-full gap-24">
    <InputField id="hauptsachtitel" v-slot="slotProps" :label="hauptsachtitelLabel">
      <Textarea
        :id="slotProps.id"
        v-model="hauptsachtitel"
        :disabled="dokumentarischerTitel !== ''"
        aria-label="Hauptsachtitel"
        rows="1"
        auto-resize
        fluid
      />
    </InputField>
    <InputField id="hauptsachtitelZusatz" v-slot="slotProps" label="Zusatz zum Hauptsachtitel">
      <Textarea
        :id="slotProps.id"
        v-model="hauptsachtitelZusatz"
        :disabled="dokumentarischerTitel !== ''"
        aria-label="Zusatz zum Hauptsachtitel"
        rows="1"
        auto-resize
        fluid
      />
    </InputField>
    <InputField id="dokumentarischerTitel" v-slot="slotProps" :label="dokumentarischerTitelLabel">
      <Textarea
        :id="slotProps.id"
        v-model="dokumentarischerTitel"
        :disabled="hauptsachtitel !== '' || hauptsachtitelZusatz !== ''"
        aria-label="Dokumentarischer Titel"
        rows="1"
        auto-resize
        fluid
      />
    </InputField>
  </div>
</template>
