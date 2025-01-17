import { describe, expect, it } from 'vitest'
import ComboboxItemService from '@/services/comboboxItemService.ts'
import { ref } from 'vue'
import type { ComboboxItem } from '@/components/input/types.ts'

describe('comboboxItemService', () => {
  it('getDocumentTypes', () => {
    const documentTypes = ComboboxItemService.getDocumentTypes(ref(''))

    expect(documentTypes.data.value as ComboboxItem[]).toEqual(
      expect.arrayContaining([<ComboboxItem>{ label: 'VR', value: { label: 'VR' } }]),
    )
  })
})
