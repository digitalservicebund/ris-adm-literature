<script lang="ts" setup>
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'
import { createEmptyNormSettingAuthority, type NormSettingAuthority } from '@/domain/normSettingAuthority'
import NormSettingAuthorityListItem from './NormSettingAuthorityListItem.vue'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'
import { computed } from 'vue'

const store = useDocumentUnitStore()

const authorities = computed({
  get: () => store.documentUnit!.normSettingAuthorities ?? [],
  set: (newValue) => store.documentUnit!.normSettingAuthorities = newValue,
})

const onClickAddAuthority = () => {
  authorities.value = [...authorities.value, createEmptyNormSettingAuthority()]
}

const onUpdateAuthority = (updated: NormSettingAuthority | undefined) => {
  if (updated) {
    const index = authorities.value.findIndex(a => a.id === updated.id)
    authorities.value[index] = updated
  }
}

const onRemoveAuthority = (id: string) => {
  authorities.value = authorities.value.filter(a => a.id !== id)
}

const buttonLabel = computed(() => authorities.value.length > 0 ? 'Weitere Angabe' : 'Normgeber hinzufügen')
</script>

<template>
  <div class="authorities">
    <h2 class="ris-label1-bold mb-16">Normgeber</h2>
    <ol v-if="authorities.length > 0">
      <li class="border-b-1 border-blue-300 py-16" v-for="(authority, index) in authorities" :key="authority.id">
        <NormSettingAuthorityListItem
          :authority="authorities[index]"
          @update-authority="onUpdateAuthority"
          @remove-authority="onRemoveAuthority"
        />
      </li>
    </ol>
    <Button
      class="mt-16"
      :aria-label="buttonLabel"
      severity="secondary"
      :label="buttonLabel"
      size="small"
      @click="onClickAddAuthority"
    >
      <template #icon><IconAdd /></template>
    </Button>
  </div>
</template>
