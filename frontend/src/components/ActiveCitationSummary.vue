<script lang="ts" setup>
// import dayjs from 'dayjs'
import { computed } from 'vue'
import ToolTip from './ToolTip.vue'
import DocumentationUnitSummary from '@/components/DocumentationUnitSummary.vue'
// import { DocumentUnitCategoriesEnum } from '@/components/enumDocumentUnitCategories'
import TextButton from '@/components/input/TextButton.vue'
import ActiveCitation from '@/domain/activeCitation'
// import { useDocumentUnitStore } from '@/stores/documentUnitStore'
// import { useExtraContentSidePanelStore } from '@/stores/extraContentSidePanelStore'
import IconBaselineContentCopy from '~icons/ic/baseline-content-copy'
import IconGenerateText from '~icons/ic/round-auto-fix-high'
import IconImportCategories from '~icons/material-symbols/text-select-move-back-word'

const props = defineProps<{
  data: ActiveCitation
}>()
// const extraContentSidePanelStore = useExtraContentSidePanelStore()
// const documentUnitStore = useDocumentUnitStore()

const isParallelDecision = computed(
  () =>
    props.data.citationType?.label == 'Parallelentscheidung' ||
    props.data.citationType?.label == 'Teilweise Parallelentscheidung',
)

async function copySummary() {
  if (props.data) await navigator.clipboard.writeText(props.data.renderSummary)
}

async function generateHeadnote() {
  // const text = `${props.data.citationType?.label == 'Teilweise Parallelentscheidung' ? 'Teilweise ' : ''}Parallelentscheidung zu der Entscheidung (${props.data.documentType?.label}) des ${props.data.court?.label} vom ${dayjs(props.data.decisionDate).format('DD.MM.YYYY')} - ${props.data.fileNumber}${props.data.citationType?.label == 'Teilweise Parallelentscheidung' ? '.' : ', welche vollständig dokumentiert ist.'}`

  // documentUnitStore.documentUnit!.shortTexts.headnote = text
  // await documentUnitStore.updateDocumentUnit()
  //scroll to headnote
  // const element = document.getElementById(DocumentUnitCategoriesEnum.TEXTS)
  const element = document.getElementById('texts')
  const headerOffset = 80
  const elementPosition = element ? element.getBoundingClientRect().top : 0
  const offsetPosition = elementPosition + window.scrollY - headerOffset
  window.scrollTo({
    top: offsetPosition,
    behavior: 'smooth',
  })
}
</script>

<template>
  <div class="flex w-full justify-between">
    <DocumentationUnitSummary :data="data"> </DocumentationUnitSummary>

    <!-- Button group -->
    <div class="flex flex-row -space-x-2">
      <ToolTip v-if="isParallelDecision" text="Rubriken importieren">
        <TextButton
          id="category-import"
          aria-label="Rubriken-Import anzeigen"
          button-type="ghost"
          data-testid="import-categories"
          :icon="IconImportCategories"
          size="small"
        />
      </ToolTip>
      <ToolTip v-if="isParallelDecision" text="O-Satz generieren">
        <TextButton
          id="generate-headnote"
          aria-label="O-Satz generieren"
          button-type="ghost"
          data-testid="generate-headnote"
          :disabled="true"
          :icon="IconGenerateText"
          size="small"
          @click="generateHeadnote"
        />
      </ToolTip>
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
