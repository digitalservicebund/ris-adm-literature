import { defineStore } from 'pinia'
import { ref } from 'vue'
import DocumentUnit from '@/domain/documentUnit'
import documentUnitService from '@/services/documentUnitService'
import type { ServiceResponse } from '@/services/httpClient'

export const useDocumentUnitStore = defineStore('docunitStore', () => {
  const documentUnit = ref<DocumentUnit | undefined>(undefined)

  async function loadDocumentUnit(documentNumber: string): Promise<ServiceResponse<DocumentUnit>> {
    const response = await documentUnitService.getByDocumentNumber(documentNumber)
    console.log(response)
    if (response.data) {
      documentUnit.value = response.data
    } else {
      documentUnit.value = undefined
    }
    return response
  }

  async function unloadDocumentUnit(): Promise<void> {
    documentUnit.value = undefined
  }

  return {
    documentUnit,
    loadDocumentUnit,
    unloadDocumentUnit,
  }
})
