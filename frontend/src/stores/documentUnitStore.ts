import { defineStore } from 'pinia'
import { ref } from 'vue'
import { type DocumentUnit } from '@/domain/documentUnit'
import documentUnitService from '@/services/documentUnitService'
import type { FailedValidationServerResponse, ServiceResponse } from '@/services/httpClient'
import errorMessages from '@/i18n/errors.json'
import { type DocumentUnitResponse } from '@/domain/documentUnitResponse.ts'

export const useDocumentUnitStore = defineStore('docunitStore', () => {
  const documentUnit = ref<DocumentUnit | undefined>(undefined)

  async function loadDocumentUnit(
    documentNumber: string,
  ): Promise<ServiceResponse<DocumentUnitResponse>> {
    const response = await documentUnitService.getByDocumentNumber(documentNumber)
    if (!response.data) {
      documentUnit.value = undefined
    }
    if (response.data) {
      documentUnit.value = <DocumentUnit>{
        id: response.data?.id,
        documentNumber: response.data?.documentNumber,
      }
    }
    if (response.data?.json) {
      documentUnit.value = response.data.json
    }
    return response
  }

  async function unloadDocumentUnit(): Promise<void> {
    documentUnit.value = undefined
  }

  async function updateDocumentUnit(): Promise<
    ServiceResponse<DocumentUnitResponse | FailedValidationServerResponse>
  > {
    if (!documentUnit.value) {
      return {
        status: 404,
        data: undefined,
        error: errorMessages.DOCUMENT_UNIT_COULD_NOT_BE_LOADED,
      }
    }
    const response = await documentUnitService.update(documentUnit.value)

    if (response.status === 200) {
      documentUnit.value = (response.data as DocumentUnitResponse).json
    } else {
      return {
        status: response.status,
        data: undefined,
        error:
          response.status === 403
            ? errorMessages.NOT_ALLOWED
            : errorMessages.DOCUMENT_UNIT_UPDATE_FAILED,
      }
    }
    return response
  }

  return {
    documentUnit,
    loadDocumentUnit,
    unloadDocumentUnit,
    updateDocumentUnit,
  }
})
