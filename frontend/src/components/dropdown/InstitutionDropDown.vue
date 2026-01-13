<script setup lang="ts">
import { RisAutoComplete } from "@digitalservicebund/ris-ui/components";
import { onMounted, ref } from "vue";
import { useFetchInstitutions } from "@/services/institutionService.ts";
import { type Institution } from "@/domain/normgeber.ts";
import { useAutoComplete, useInstitutionSearch } from "@/composables/useAutoComplete";

defineProps<{
  inputId: string;
  isInvalid: boolean;
}>();

const modelValue = defineModel<Institution | undefined>();
const emit = defineEmits<{
  "update:modelValue": [value: Institution | undefined];
}>();

const institutions = ref<Institution[]>([]);
const selectedInstitutionId = ref<string | undefined>(modelValue.value?.id);

const searchFn = useInstitutionSearch(institutions);

const { suggestions, onComplete } = useAutoComplete(searchFn);

function onModelValueChange(id: string | undefined) {
  selectedInstitutionId.value = id;
  const selectedInstitution = institutions.value.find((inst: Institution) => inst.id === id);
  emit("update:modelValue", selectedInstitution);
}

onMounted(async () => {
  const { data } = await useFetchInstitutions();
  institutions.value = data.value?.institutions || [];
});
</script>

<template>
  <RisAutoComplete
    :model-value="selectedInstitutionId"
    :suggestions="suggestions"
    :invalid="isInvalid"
    :initial-label="modelValue?.name"
    :input-id="inputId"
    aria-label="Normgeber"
    append-to="self"
    typeahead
    dropdown
    dropdown-mode="blank"
    force-selection
    complete-on-focus
    @update:model-value="onModelValueChange"
    @complete="onComplete"
  />
</template>
