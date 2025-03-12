// import { describe, expect, it, beforeAll, afterAll } from 'vitest'
import { describe, expect, it } from 'vitest'
// import { waitFor } from '@testing-library/vue'
// import { http, HttpResponse } from 'msw'
// import { setupServer } from 'msw/node'
import { ref } from 'vue'
import ComboboxItemService from '@/services/comboboxItemService.ts'
import type { ComboboxItem } from '@/components/input/types.ts'

// const server = setupServer(
//   http.get('/api/lookup-tables/document-types', () =>
//     HttpResponse.json({
//       documentTypes: [
//         {
//           abbreviation: 'VE',
//           name: 'Verwaltungsvereinbarung',
//         },
//       ],
//     }),
//   ),
// )

// describe('comboboxItemService', () => {
//   beforeAll(() => server.listen())
//   afterAll(() => server.close())

//   it('should fetch document type from lookup table', async () => {
//     const { data, execute } = ComboboxItemService.getDocumentTypes(ref())

//     await execute()
//     await waitFor(() => {
//       expect(data.value?.[0].label).toEqual('Verwaltungsvereinbarung')
//       // expect(data.value?.[0].value).toEqual({
//       //   abbreviation: 'VE',
//       //   label: 'Verwaltungsvereinbarung',
//       // })
//     })
//   })
// })

describe('comboboxItemService', () => {
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

  it('getFieldOfLawSearchByIdentifier.execute with filter', async () => {
    const fieldsOfLaw = ComboboxItemService.getFieldOfLawSearchByIdentifier(ref('arbeitsr'))

    const executionResult = await fieldsOfLaw.execute()

    expect(executionResult.data.value).not.toEqual(fieldsOfLaw.data.value)
  })
})
