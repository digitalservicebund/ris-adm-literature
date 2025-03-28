import { ref } from 'vue'
import { beforeEach, describe, expect, it, type MockInstance, vi } from 'vitest'
import { userEvent, type UserEvent } from '@testing-library/user-event'
import { render, screen, waitFor } from '@testing-library/vue'
import { createTestingPinia } from '@pinia/testing'
import FieldsOfLawVue from '@/components/field-of-law/FieldsOfLaw.vue'
import { type DocumentUnit } from '@/domain/documentUnit'
import FieldOfLawService from '@/services/fieldOfLawService'
import ComboboxItemService from '@/services/comboboxItemService'
import type { FieldOfLaw } from '@/domain/fieldOfLaw'

function renderComponent() {
  const user = userEvent.setup()
  return {
    user,
    ...render(FieldsOfLawVue, {
      global: {
        plugins: [
          [
            createTestingPinia({
              initialState: {
                docunitStore: {
                  documentUnit: <DocumentUnit>{
                    id: '123',
                    documentNumber: '1234567891234',
                    fieldsOfLaw: [] as FieldOfLaw[],
                  },
                },
              },
              // stubActions: false,
            }),
          ],
        ],
        // stubs: {
        //   routerLink: {
        //     template: '<a><slot/></a>',
        //   },
        // },
      },
    }),
  }
}

describe('FieldsOfLaw', () => {
  const getChildrenOfRoot = () =>
    Promise.resolve({
      status: 200,
      data: [
        {
          hasChildren: true,
          identifier: 'PR',
          text: 'Phantasierecht',
          linkedFields: [],
          norms: [],
          children: [],
        },
        {
          hasChildren: true,
          identifier: 'AV',
          text: 'Allgemeines Verwaltungsrecht',
          linkedFields: [],
          norms: [],
          children: [],
        },
        {
          identifier: 'AB-01',
          text: 'Text for AB',
          children: [],
          norms: [],
          isExpanded: false,
          hasChildren: false,
        },
        {
          identifier: 'CD-02',
          text: 'And text for CD with link to AB-01',
          children: [],
          norms: [],
          linkedFields: ['AB-01'],
          isExpanded: false,
          hasChildren: false,
        },
      ],
    })
  const getChildrenOfPR = () =>
    Promise.resolve({
      status: 200,
      data: [
        {
          hasChildren: true,
          identifier: 'PR-05',
          text: 'Beendigung der Phantasieverhältnisse',
          linkedFields: [],
          norms: [
            {
              abbreviation: 'PStG',
              singleNormDescription: '§ 99',
            },
          ],
          children: [],
          parent: {
            hasChildren: true,
            identifier: 'PR',
            text: 'Phantasierecht',
            linkedFields: [],
            norms: [],
            children: [],
            parent: undefined,
          },
        },
      ],
    })
  const getChildrenOfPRO5 = () =>
    Promise.resolve({
      status: 200,
      data: [
        {
          hasChildren: false,
          identifier: 'PR-05-01',
          text: 'Phantasie besonderer Art, Ansprüche anderer Art',
          norms: [],
          children: [],
          parent: {
            hasChildren: true,
            identifier: 'PR-05',
            text: 'Beendigung der Phantasieverhältnisse',
            linkedFields: [],
            norms: [],
            children: [],
            parent: {
              hasChildren: true,
              identifier: 'PR',
              text: 'Phantasierecht',
              norms: [],
              children: [],
            },
          },
        },
      ],
    })
  const getChildrenOfPR0501 = () =>
    Promise.resolve({
      status: 200,
      data: [
        {
          hasChildren: false,
          identifier: 'PR-05-01',
          text: 'Phantasie besonderer Art, Ansprüche anderer Art',
          norms: [],
          children: [],
          parent: {
            hasChildren: true,
            identifier: 'PR-05',
            text: 'Beendigung der Phantasieverhältnisse',
            linkedFields: [],
            norms: [],
            children: [],
            parent: {
              hasChildren: true,
              identifier: 'PR',
              text: 'Phantasierecht',
              norms: [],
              children: [],
            },
          },
        },
      ],
    })
  const getParentAndChildrenForIdentifierPR05 = () =>
    Promise.resolve({
      status: 200,
      data: {
        hasChildren: true,
        identifier: 'PR-05',
        text: 'Beendigung der Phantasieverhältnisse',
        norms: [
          {
            abbreviation: 'PStG',
            singleNormDescription: '§ 99',
          },
        ],
        children: [
          {
            hasChildren: false,
            identifier: 'PR-05-01',
            text: 'Phantasie besonderer Art, Ansprüche anderer Art',
            norms: [],
            children: [],
            parent: {
              hasChildren: true,
              identifier: 'PR-05',
              text: 'Beendigung der Phantasieverhältnisse',
              linkedFields: [],
              norms: [],
              children: [],
              parent: {
                hasChildren: true,
                identifier: 'PR',
                text: 'Phantasierecht',
                norms: [],
                children: [],
              },
            },
          },
        ],
        parent: {
          id: 'a785fb96-a45d-4d4c-8d9c-92d8a6592b22',
          hasChildren: true,
          identifier: 'PR',
          text: 'Phantasierecht',
          norms: [],
          children: [],
        },
      },
    })
  const searchForFieldsOfLawForPR05 = () =>
    Promise.resolve({
      status: 200,
      data: {
        content: [
          {
            hasChildren: true,
            identifier: 'PR-05',
            text: 'Beendigung der Phantasieverhältnisse',
            norms: [],
            children: [],
            parent: {
              id: 'a785fb96-a45d-4d4c-8d9c-92d8a6592b22',
              hasChildren: true,
              identifier: 'PR',
              text: 'Phantasierecht',
              norms: [],
              children: [],
            },
          },
          {
            hasChildren: false,
            identifier: 'PR-05-01',
            text: 'Phantasie besonderer Art, Ansprüche anderer Art',
            norms: [],
            children: [],
            parent: {
              hasChildren: true,
              identifier: 'PR-05',
              text: 'Beendigung der Phantasieverhältnisse',
              norms: [],
              children: [],
              parent: {
                id: 'a785fb96-a45d-4d4c-8d9c-92d8a6592b22',
                hasChildren: true,
                identifier: 'PR',
                text: 'Phantasierecht',
                norms: [],
                children: [],
              },
            },
          },
        ],
        size: 2,
        number: 0,
        numberOfElements: 2,
        first: true,
        last: true,
        empty: false,
      },
    })

  let fetchSpyGetChildrenOf: MockInstance
  let fetchSpyGetParentAndChildrenForIdentifier: MockInstance
  let fetchSpySearchForFieldsOfLawForPR05: MockInstance

  beforeEach(() => {
    fetchSpyGetChildrenOf = vi
      .spyOn(FieldOfLawService, 'getChildrenOf')
      .mockImplementation((identifier: string) => {
        if (identifier == 'root') return getChildrenOfRoot()
        else if (identifier == 'PR') return getChildrenOfPR()
        else if (identifier == 'PR-05') return getChildrenOfPRO5()
        return getChildrenOfPR0501()
      })
    fetchSpyGetParentAndChildrenForIdentifier = vi
      .spyOn(FieldOfLawService, 'getParentAndChildrenForIdentifier')
      .mockImplementation(() => {
        return getParentAndChildrenForIdentifierPR05()
      })
    fetchSpySearchForFieldsOfLawForPR05 = vi
      .spyOn(FieldOfLawService, 'searchForFieldsOfLaw')
      .mockImplementation(() => {
        return searchForFieldsOfLawForPR05()
      })
    // vi.spyOn(ComboboxItemService, 'getFieldOfLawSearchByIdentifier').mockImplementation(() => {
    //   return {
    //     data: ref([]),
    //     execute: ComboboxItemService.getFieldOfLawSearchByIdentifier,
    //     abort: () => {},
    //     canAbort: false,
    //   }
    // })
  })

  it.skip('Node of interest is set and corresponding nodes are opened in the tree (other nodes truncated) - when root child node is collapsed all other root children shall be loaded', async () => {
    // given
    const { user } = renderComponent()
    await user.click(screen.getByRole('button', { name: 'Sachgebiete' }))
    await user.click(screen.getByLabelText('Suche'))
    await user.type(screen.getByLabelText('Sachgebietskürzel'), 'PR-05')
    await user.click(screen.getByRole('button', { name: 'Sachgebietssuche ausführen' }))

    await waitFor(() => {
      expect(fetchSpySearchForFieldsOfLawForPR05).toBeCalledTimes(1)
    })
    await waitFor(() => {
      expect(fetchSpyGetParentAndChildrenForIdentifier).toBeCalledTimes(1)
    })
    await waitFor(() => {
      expect(fetchSpyGetChildrenOf).toBeCalledTimes(2)
    })
    await waitFor(() => {
      expect(
        screen.getAllByText('Phantasie besonderer Art, Ansprüche anderer Art')[0],
      ).toBeInTheDocument()
    })

    // when
    await user.click(screen.getByLabelText('Phantasierecht einklappen'))

    // this means one more call for children
    await waitFor(() => {
      expect(fetchSpyGetChildrenOf).toBeCalledTimes(3)
    })

    // then
    await waitFor(() => {
      expect(screen.getByText('Allgemeines Verwaltungsrecht')).toBeInTheDocument()
    })
  })
})
