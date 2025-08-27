<!-- eslint-disable vue/multi-word-component-names -->
<script lang="ts" setup>
import { computed, ref } from 'vue'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'
import { RisChipsInput } from '@digitalservicebund/ris-ui/components'
import InputField from './input/InputField.vue'
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'

const store = useDocumentUnitStore()

const berufsbilder = computed({
  get: () => store.documentUnit!.berufsbilder ?? [],
  set: (newValues: string[]) => {
    store.documentUnit!.berufsbilder = newValues
  },
})

const showChips = ref(false)
</script>

<template>
  <InputField v-if="berufsbilder.length > 0 || showChips" id="Berufsbild" label="Berufsbild">
    <RisChipsInput id="berufsbild" v-model="berufsbilder" aria-label="Berufsbild"></RisChipsInput>
  </InputField>
  <Button
    v-else
    aria-label="Berufsbild hinzufügen"
    class="self-start"
    label="Berufsbild"
    size="small"
    severity="secondary"
    @click="showChips = true"
  >
    <template #icon> <IconAdd /> </template>
  </Button>
</template>
