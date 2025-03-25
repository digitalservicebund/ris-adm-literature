import { describe, expect, it } from 'vitest'
import { ref } from 'vue'
import ComboboxItemService from '@/services/comboboxItemService.ts'
import type { ComboboxItem } from '@/components/input/types.ts'

describe('comboboxItemService', () => {
  it('getFieldOfLawSearchByIdentifier.data', () => {
    const fieldsOfLaw = ComboboxItemService.getFieldOfLawSearchByIdentifier(ref(''))
    expect(fieldsOfLaw.data.value as ComboboxItem[]).toEqual(
      expect.arrayContaining([
        <ComboboxItem>{
          label: 'Phantasierecht',
          value: {
            hasChildren: true,
            identifier: 'PR',
            text: 'Phantasierecht',
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

  it('getFieldOfLawSearchByIdentifier.execute with filter', async () => {
    const fieldsOfLaw = ComboboxItemService.getFieldOfLawSearchByIdentifier(ref('arbeitsr'))

    const executionResult = await fieldsOfLaw.execute()

    expect(executionResult.data.value).not.toEqual(fieldsOfLaw.data.value)
  })
})
