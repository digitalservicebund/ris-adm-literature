<script lang="ts" setup>
import ToolTip from './ToolTip.vue'
import DocumentationUnitSummary from '@/components/DocumentationUnitSummary.vue'
import TextButton from '@/components/input/TextButton.vue'
import ActiveCitation from '@/domain/activeCitation'
import IconBaselineContentCopy from '~icons/ic/baseline-content-copy'

const props = defineProps<{
  data: ActiveCitation
}>()

async function copySummary() {
  if (props.data) await navigator.clipboard.writeText(props.data.renderSummary)
}
</script>

<template>
  <div class="flex w-full justify-between">
    <DocumentationUnitSummary :data="data"> </DocumentationUnitSummary>

    <!-- Button group -->
    <div class="flex flex-row -space-x-2">
      <ToolTip text="Kopieren">
        <TextButton
          id="category-import"
          aria-label="Rubriken-Import anzeigen"
          button-type="ghost"
          data-testid="copy-summary"
          :icon="IconBaselineContentCopy"
          size="small"
          @click="copySummary"
          @keypress.enter="copySummary"
        />
      </ToolTip>
    </div>
  </div>
</template>
