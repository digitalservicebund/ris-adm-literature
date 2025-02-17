import { describe, expect, it } from 'vitest'
import ComboboxItemService from '@/services/comboboxItemService.ts'
import { ref } from 'vue'
import type { ComboboxItem } from '@/components/input/types.ts'

describe('comboboxItemService', () => {
  it('getDocumentTypes.data', () => {
    const documentTypes = ComboboxItemService.getDocumentTypes(ref(''))

    expect(documentTypes.data.value as ComboboxItem[]).toEqual(
      expect.arrayContaining([<ComboboxItem>{ label: 'VR', value: { label: 'VR' } }]),
    )
  })

  it('getDocumentTypes.execute', async () => {
    const documentTypes = ComboboxItemService.getDocumentTypes(ref(''))

    const executionResult = await documentTypes.execute()

    expect(executionResult.data.value).toEqual(documentTypes.data.value)
  })

  it('getFieldOfLawSearchByIdentifier.data', () => {
    const fieldsOfLaw = ComboboxItemService.getFieldOfLawSearchByIdentifier(ref(''))
    expect(fieldsOfLaw.data.value as ComboboxItem[]).toEqual(
      expect.arrayContaining([
        <ComboboxItem>{
          label: 'Arbeitsrecht',
          value: {
            hasChildren: true,
            identifier: 'AR',
            text: 'Arbeitsrecht',
            linkedFields: [],
            norms: [],
            children: [],
            parent: undefined,
          },
        },
      ]),
    )
  })

  it('getFieldOfLawSearchByIdentifier.execute', async () => {
    const fieldsOfLaw = ComboboxItemService.getFieldOfLawSearchByIdentifier(ref(''))

    const executionResult = await fieldsOfLaw.execute()

    expect(executionResult.data.value).toEqual(fieldsOfLaw.data.value)
  })
})
