<script lang="ts" setup>
import ToolTip from '@/components/ToolTip.vue'
import Button from 'primevue/button'
import ActiveCitationLiterature from '@/domain/activeReferenceLiterature'
import IconBaselineContentCopy from '~icons/ic/baseline-content-copy'

const props = defineProps<{
  data: ActiveCitationLiterature
}>()

async function copySummary() {
  if (props.data) await navigator.clipboard.writeText(props.data.renderSummary)
}
</script>

<template>
  <div class="flex w-full justify-between">
    <span class="flex w-full flex-row flex-wrap items-center">
      {{ data.renderSummary }}
    </span>
    <div class="flex flex-row -space-x-2">
      <ToolTip text="Kopieren">
        <Button
          id="category-import"
          aria-label="Aktivzitierung in die Zwischenablage kopieren"
          data-testid="copy-summary"
          size="small"
          text
          @click="copySummary"
          @keypress.enter="copySummary"
        >
          <template #icon> <IconBaselineContentCopy /> </template>
        </Button>
      </ToolTip>
    </div>
  </div>
</template>
