import { createTestingPinia } from '@pinia/testing'
import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest'
import { createRouter, createWebHistory, type Router } from 'vue-router'
import ExtraContentSidePanel from '@/components/ExtraContentSidePanel.vue'
import type { DocumentUnit } from '@/domain/documentUnit'
import type { Fundstelle } from '@/domain/fundstelle'

let router: Router

function renderComponent(
  options: {
    note?: string
    fundstellen?: Fundstelle[]
    showEditButton?: boolean
    isEditable?: boolean
    hidePanelModeBar?: boolean
  } = {},
) {
  const user = userEvent.setup()

  const documentUnit = <DocumentUnit>{
    documentNumber: '1234567891234',
    note: options.note ?? '',
  }

  return {
    user,
    ...render(ExtraContentSidePanel, {
      props: {
        showEditButton: options.showEditButton,
        documentUnit: documentUnit,
        hidePanelModeBar: options.hidePanelModeBar ?? false,
      },
      global: {
        plugins: [
          [router],
          [
            createTestingPinia({
              initialState: {
                docunitStore: {
                  documentUnit: documentUnit,
                },
              },
              stubActions: false, // To use the store functions in extraContentSidePanelStore
            }),
          ],
        ],
      },
    }),
  }
}

describe('ExtraContentSidePanel', () => {
  beforeEach(() => {
    router = createRouter({
      history: createWebHistory(),
      routes: [
        {
          path: '/',
          name: 'home',
          component: {},
        },
      ],
    })
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('Open/close the panel', () => {
    const testCases = [
      {
        hasNote: false,
        queryParam: undefined,
        expectedIsOpen: false,
      },
      {
        hasNote: true,
        queryParam: undefined,
        expectedIsOpen: true,
      },
      {
        hasNote: false,
        queryParam: 'true',
        expectedIsOpen: false,
      },
      {
        hasNote: true,
        queryParam: 'false',
        expectedIsOpen: true,
      },
      {
        hasNote: false,
        queryParam: 'false',
        expectedIsOpen: false,
      },
    ]
    testCases.forEach(({ hasNote, queryParam, expectedIsOpen }) =>
      test(`panel inititally ${expectedIsOpen ? 'opened' : 'closed'} ${hasNote ? 'with' : 'without'} note with query param ${queryParam}`, async () => {
        await router.push({
          path: '',
        })

        renderComponent({
          note: hasNote ? 'note' : '',
        })

        await screen.findByLabelText(
          expectedIsOpen ? 'Seitenpanel schließen' : 'Seitenpanel öffnen',
        )

        if (expectedIsOpen) {
          expect(screen.getByLabelText('Notiz anzeigen')).toBeVisible()
        } else {
          expect(screen.getByLabelText('Notiz anzeigen')).not.toBeVisible()
        }
      }),
    )
  })

  test('toggle panel open and closed', async () => {
    const { user } = renderComponent()
    expect(await screen.findByLabelText('Seitenpanel öffnen')).toBeVisible()

    // Opening side panel
    await user.click(screen.getByLabelText('Seitenpanel öffnen'))
    expect(await screen.findByLabelText('Seitenpanel schließen')).toBeVisible()

    // Closing side panel
    await user.click(screen.getByLabelText('Seitenpanel schließen'))
    expect(await screen.findByLabelText('Seitenpanel öffnen')).toBeVisible()
  })

  describe('Select panel content', () => {
    test('initially open note without note', async () => {
      const { user } = renderComponent()
      await user.click(screen.getByLabelText('Seitenpanel öffnen'))

      expect(await screen.findByLabelText('Notiz Eingabefeld')).toBeVisible()
    })

    test('initially open note with note', async () => {
      renderComponent({ note: 'some note' })

      expect(await screen.findByDisplayValue('some note')).toBeVisible()
    })
  })
})
