<script lang="ts" setup>
import type { DocumentUnitListItem } from '@/domain/documentUnit'
import { ref } from 'vue'
import DocumentUnitList from './DocumentUnitList.vue'

const ITEMS_PER_PAGE = 100

const isLoading = ref(false)
const firstRowIndex = ref<number>(0)
const totalRows = ref<number>(0)
// TODO remove mock data
const docUnits = ref<DocumentUnitListItem[]>([
  {
    id: 'docUnitId1',
    documentNumber: 'documentNumber1',
    zitierdatum: '2025-01-01',
    langueberschrift:
      'Berücksichtigung von Sonderleistungen im Rahmen der Übergangsregelung gemäß § 3 Abs. 1 Nr. 2 Übergangsverordnung (ÜV)',
    fundstellen: [
      {
        id: 'referenceId1',
        zitatstelle: '§2.1',
        periodika: [
          {
            id: 'periodicalId1',
            abbreviation: 'Zentralblatt',
          },
        ],
      },
      {
        id: 'referenceId2',
        zitatstelle: 'Kapitel 4',
        periodika: [
          {
            id: 'periodicalId2',
            abbreviation: 'Dokumentenzentrum Süd',
          },
        ],
      },
    ],
  },
  {
    id: 'docUnitId2',
    documentNumber: 'documentNumber2',
    zitierdatum: '2025-01-01',
    langueberschrift:
      'Verwaltungsvorschrift zur Prüfung von Einmalzahlungen im Rahmen des § 6 Abs. 3 Satz 2 Sozialleistungsharmonisierungsgesetzes (SLHG)',
    fundstellen: [],
  },
])
</script>

<template>
  <DocumentUnitList
    :doc-units="docUnits"
    :first-row-index="firstRowIndex"
    :rows-per-page="ITEMS_PER_PAGE"
    :total-rows="totalRows"
    :loading="isLoading"
  />
</template>
