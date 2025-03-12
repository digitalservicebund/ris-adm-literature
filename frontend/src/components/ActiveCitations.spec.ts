import { describe, expect, it, beforeAll, afterAll } from 'vitest'
import { userEvent } from '@testing-library/user-event'
import { fireEvent, render, screen, waitFor } from '@testing-library/vue'
import ActiveCitations from '@/components/ActiveCitations.vue'
import ActiveCitation from '@/domain/activeCitation'
import { type CitationType } from '@/domain/citationType'
import { type Court, type DocumentType, type DocumentUnit } from '@/domain/documentUnit'
import { onSearchShortcutDirective } from '@/utils/onSearchShortcutDirective'
import { createTestingPinia } from '@pinia/testing'
import { http, HttpResponse } from 'msw'
import { setupServer } from 'msw/node'

const server = setupServer(
  http.get('/api/lookup-tables/document-types', () =>
    HttpResponse.json({
      documentTypes: [
        {
          abbreviation: 'VE',
          name: 'Verwaltungsvereinbarung',
        },
      ],
    }),
  ),
)

function renderComponent(activeCitations?: ActiveCitation[]) {
  const user = userEvent.setup()

  return {
    user,
    ...render(ActiveCitations, {
      global: {
        directives: { 'ctrl-enter': onSearchShortcutDirective },
        plugins: [
          [
            createTestingPinia({
              initialState: {
                docunitStore: {
                  documentUnit: <DocumentUnit>{
                    id: '123',
                    documentNumber: '1234567891234',
                    activeCitations: activeCitations ?? [],
                  },
                },
              },
              stubActions: false,
            }),
          ],
        ],
        stubs: {
          routerLink: {
            template: '<a><slot/></a>',
          },
        },
      },
    }),
  }
}

function generateActiveCitation(options?: {
  uuid?: string
  documentNumber?: string
  court?: Court
  decisionDate?: string
  fileNumber?: string
  documentType?: DocumentType
  citationStyle?: CitationType
}) {
  return new ActiveCitation({
    uuid: options?.uuid ?? crypto.randomUUID(),
    documentNumber: options?.documentNumber ?? undefined,
    court: options?.court ?? {
      type: 'type1',
      location: 'location1',
      label: 'label1',
    },
    decisionDate: options?.decisionDate ?? '2022-02-01',
    fileNumber: options?.fileNumber ?? 'test fileNumber',
    documentType: options?.documentType ?? {
      abbreviation: 'documentTypeShortcut1',
      label: 'documentType1',
    },
    citationType: options?.citationStyle ?? {
      uuid: '123',
      jurisShortcut: 'Änderung',
      label: 'Änderung',
    },
  })
}

describe('active citations', () => {
  beforeAll(() => server.listen())
  afterAll(() => server.close())

  it('renders empty active citation in edit mode, when no activeCitations in list', async () => {
    renderComponent()
    expect((await screen.findAllByLabelText('Listen Eintrag')).length).toBe(1)
    expect(screen.getByLabelText('Art der Zitierung')).toBeVisible()
    expect(screen.getByLabelText('Gericht Aktivzitierung')).toBeVisible()
    expect(screen.getByLabelText('Entscheidungsdatum Aktivzitierung')).toBeVisible()
    expect(screen.getByLabelText('Aktenzeichen Aktivzitierung')).toBeInTheDocument()
    expect(screen.getByLabelText('Dokumenttyp Aktivzitierung')).toBeInTheDocument()
    expect(screen.getByLabelText('Aktivzitierung speichern')).toBeDisabled()
  })

  it('renders activeCitations as list entries', () => {
    renderComponent([
      generateActiveCitation({ fileNumber: '123' }),
      generateActiveCitation({ fileNumber: '345' }),
    ])

    expect(screen.queryByLabelText('Art der Zitierung')).not.toBeInTheDocument()
    expect(screen.getByText(/123/)).toBeInTheDocument()
    expect(screen.getByText(/345/)).toBeInTheDocument()
  })

  it('creates new active citation manually', async () => {
    const { user } = renderComponent()
    const input = await screen.findByLabelText('Aktenzeichen Aktivzitierung')
    await user.type(input, '123')
    const button = screen.getByLabelText('Aktivzitierung speichern')
    await user.click(button)

    expect(screen.getAllByLabelText('Listen Eintrag').length).toBe(2)
  })

  it('click on edit icon, opens the list entry in edit mode', async () => {
    const { user } = renderComponent([
      generateActiveCitation({
        fileNumber: '123',
      }),
    ])
    const itemHeader = screen.getByTestId('list-entry-0')
    await user.click(itemHeader)

    expect(screen.getByLabelText('Art der Zitierung')).toBeVisible()
    expect(screen.getByLabelText('Entscheidungsdatum Aktivzitierung')).toBeVisible()
    expect(screen.getByLabelText('Entscheidungsdatum Aktivzitierung')).toBeInTheDocument()
    expect(screen.getByLabelText('Aktenzeichen Aktivzitierung')).toBeInTheDocument()
    expect(screen.getByLabelText('Dokumenttyp Aktivzitierung')).toBeInTheDocument()
  })

  it('renders manually added active citations as editable list item', async () => {
    renderComponent([generateActiveCitation()])
    expect(screen.getByTestId('list-entry-0')).toBeInTheDocument()
  })

  it('correctly updates value citation style input', async () => {
    const { user } = renderComponent([
      generateActiveCitation({
        citationStyle: {
          uuid: '123',
          jurisShortcut: 'ABC',
          label: 'ABC',
        },
      }),
    ])

    expect(screen.queryByText(/Änderung/)).not.toBeInTheDocument()

    const itemHeader = screen.getByTestId('list-entry-0')
    await user.click(itemHeader)

    const activeCitationInput = await screen.findByLabelText('Art der Zitierung')
    await user.click(activeCitationInput)
    await user.type(activeCitationInput, 'Änderung')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[0]).toHaveTextContent('Änderung')
    })
    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[0])
    const button = screen.getByLabelText('Aktivzitierung speichern')
    await user.click(button)

    expect(screen.getByText(/Änderung/)).toBeVisible()
  })

  it('correctly updates value document type input', async () => {
    const { user } = renderComponent([generateActiveCitation()])

    expect(screen.queryByText(/EuGH-Vorlage/)).not.toBeInTheDocument()

    const itemHeader = screen.getByTestId('list-entry-0')
    await user.click(itemHeader)

    await user.type(await screen.findByLabelText('Dokumenttyp Aktivzitierung'), 'VE')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[0]).toHaveTextContent('VE')
    })
    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[0])
    const button = screen.getByLabelText('Aktivzitierung speichern')
    await user.click(button)

    expect(screen.getByText(/Verwaltungsvereinbarung/)).toBeVisible()
  })

  it('correctly updates value court input', async () => {
    const { user } = renderComponent([generateActiveCitation()])

    expect(screen.queryByText(/AG Aachen/)).not.toBeInTheDocument()

    const itemHeader = screen.getByTestId('list-entry-0')
    await user.click(itemHeader)

    await user.type(await screen.findByLabelText('Gericht Aktivzitierung'), 'AG')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[0]).toHaveTextContent('AG Aachen')
    })
    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[0])
    const button = screen.getByLabelText('Aktivzitierung speichern')
    await user.click(button)

    expect(screen.getByText(/AG Aachen/)).toBeVisible()
  })

  it('correctly updates value of fileNumber input', async () => {
    const { user } = renderComponent([generateActiveCitation()])

    expect(screen.queryByText(/new fileNumber/)).not.toBeInTheDocument()
    const itemHeader = screen.getByTestId('list-entry-0')
    await user.click(itemHeader)

    const fileNumberInput = await screen.findByLabelText('Aktenzeichen Aktivzitierung')

    await user.clear(fileNumberInput)
    await user.type(fileNumberInput, 'new fileNumber')
    const button = screen.getByLabelText('Aktivzitierung speichern')
    await user.click(button)

    expect(screen.getByText(/new fileNumber/)).toBeVisible()
  })

  it('correctly updates value of decision date input', async () => {
    const { user } = renderComponent([generateActiveCitation()])

    expect(screen.queryByText(/02.02.2022/)).not.toBeInTheDocument()
    const itemHeader = screen.getByTestId('list-entry-0')
    await user.click(itemHeader)

    const fileNumberInput = await screen.findByLabelText('Entscheidungsdatum Aktivzitierung')

    await user.clear(fileNumberInput)
    await user.type(fileNumberInput, '02.02.2022')
    const button = screen.getByLabelText('Aktivzitierung speichern')
    await user.click(button)

    expect(screen.getByText(/02.02.2022/)).toBeVisible()
  })

  it('correctly deletes manually added active citations', async () => {
    const { user } = renderComponent([generateActiveCitation(), generateActiveCitation()])
    const activeCitations = screen.getAllByLabelText('Listen Eintrag')
    expect(activeCitations.length).toBe(2)
    await user.click(screen.getByTestId('list-entry-0'))
    await user.click(screen.getByLabelText('Eintrag löschen'))
    expect(screen.getAllByLabelText('Listen Eintrag').length).toBe(1)
  })

  it('correctly deletes active citations added by search', async () => {
    const { user } = renderComponent([generateActiveCitation(), generateActiveCitation()])
    const activeCitations = screen.getAllByLabelText('Listen Eintrag')
    expect(activeCitations.length).toBe(2)
    await user.click(screen.getByTestId('list-entry-0'))
    await user.click(screen.getByLabelText('Eintrag löschen'))
    expect(screen.getAllByLabelText('Listen Eintrag').length).toBe(1)
  })

  it('correctly updates deleted values in active citations', async () => {
    const { user } = renderComponent([generateActiveCitation()])

    expect(
      screen.getByText('Änderung, label1, 01.02.2022, test fileNumber, documentType1'),
    ).toBeInTheDocument()
    const itemHeader = screen.getByTestId('list-entry-0')
    await user.click(itemHeader)

    const fileNumberInput = await screen.findByLabelText('Aktenzeichen Aktivzitierung')
    const courtInput = await screen.findByLabelText('Gericht Aktivzitierung')

    await user.clear(fileNumberInput)
    await user.clear(courtInput)

    await user.click(screen.getByLabelText('Aktivzitierung speichern'))

    expect(screen.getByText(/Änderung, 01.02.2022, documentType1/)).toBeInTheDocument()
  })

  it('lists search results', async () => {
    const { user } = renderComponent()

    expect(screen.queryByText(/test fileNumber/)).not.toBeInTheDocument()
    await user.click(await screen.findByLabelText('Nach Entscheidung suchen'))

    expect(screen.getAllByText(/test fileNumber/).length).toBe(1)
  })

  it('search is triggered with shortcut', async () => {
    const { user } = renderComponent()

    expect(screen.queryByText(/test fileNumber/)).not.toBeInTheDocument()
    await user.type(await screen.findByLabelText('Aktenzeichen Aktivzitierung'), 'test')
    await user.keyboard('{Control>}{Enter}')

    expect(screen.getAllByText(/test fileNumber/).length).toBe(1)
  })

  it('adds active citation from search results', async () => {
    const { user } = renderComponent()

    await user.click(await screen.findByLabelText('Nach Entscheidung suchen'))
    await user.click(await screen.findByLabelText('Treffer übernehmen'))
    expect(screen.getAllByText(/test fileNumber/).length).toBe(1)
  })

  it('indicates that search result already added to active citations', async () => {
    const { user } = renderComponent([generateActiveCitation({ uuid: '123' })])
    await user.click(screen.getByText(/Weitere Angabe/))
    await user.click(screen.getByLabelText('Nach Entscheidung suchen'))
    expect(screen.getByText(/Bereits hinzugefügt/)).toBeInTheDocument()
  })

  it('displays error in list and edit component when fields missing', async () => {
    const { user } = renderComponent([generateActiveCitation()])
    const itemHeader = screen.getByTestId('list-entry-0')
    await user.click(itemHeader)

    const fileInput = await screen.findByLabelText('Aktenzeichen Aktivzitierung')
    await user.clear(fileInput)
    await user.click(screen.getByLabelText('Aktivzitierung speichern'))
    expect(screen.getByText(/Fehlende Daten/)).toBeInTheDocument()
    await user.click(itemHeader)
    expect(screen.getAllByText(/Pflichtfeld nicht befüllt/).length).toBe(1)
  })

  it('shows missing citationStyle validation on entry in other field', async () => {
    const { user } = renderComponent()

    const getStyleValidation = () => screen.queryByTestId('activeCitationPredicate-validationError')

    expect(getStyleValidation()).not.toBeInTheDocument()

    await user.type(await screen.findByLabelText('Aktenzeichen Aktivzitierung'), 'test')

    expect(getStyleValidation()).toBeVisible()
  })

  it('shows missing citationStyle validation for linked decision', async () => {
    const { user } = renderComponent([
      generateActiveCitation({
        documentNumber: '123',
        citationStyle: {
          label: 'invalid',
        },
      }),
    ])
    const itemHeader = screen.getByTestId('list-entry-0')
    await user.click(itemHeader)

    expect(screen.getByText('Art der Zitierung *')).toBeVisible()
    expect(screen.getAllByText(/Pflichtfeld nicht befüllt/).length).toBe(1)
  })

  it('does not add active citation with invalid date input', async () => {
    const { user } = renderComponent()

    const dateInput = await screen.findByLabelText('Entscheidungsdatum Aktivzitierung')
    expect(dateInput).toHaveValue('')

    await user.type(dateInput, '00.00.0231')

    await screen.findByText(/Kein valides Datum/)
    screen.getByLabelText('Aktivzitierung speichern').click()
    expect(dateInput).toBeVisible()
  })

  it('does not add active citation with incomplete date input', async () => {
    const { user } = renderComponent()

    const dateInput = await screen.findByLabelText('Entscheidungsdatum Aktivzitierung')
    expect(dateInput).toHaveValue('')

    await user.type(dateInput, '01.02.')
    await user.tab()

    await screen.findByText(/Unvollständiges Datum/)
    screen.getByLabelText('Aktivzitierung speichern').click()
    expect(dateInput).toBeVisible()
  })

  it('does not add active citation with date in the future', async () => {
    const { user } = renderComponent()

    const dateInput = await screen.findByLabelText('Entscheidungsdatum Aktivzitierung')
    expect(dateInput).toHaveValue('')

    await user.type(dateInput, '01.02.2090')
    await user.tab()

    await screen.findByText(/Das Datum darf nicht in der Zukunft liegen/)
    screen.getByLabelText('Aktivzitierung speichern').click()
    expect(dateInput).toBeVisible()
  })

  it('should copy text of active citation summary', async () => {
    const { user } = renderComponent([generateActiveCitation()])
    const copyButton = screen.getByTestId('copy-summary')
    await user.click(copyButton)

    // Read from the stub clipboard
    const clipboardText = await navigator.clipboard.readText()

    expect(clipboardText).toBe('Änderung, label1, 01.02.2022, test fileNumber, documentType1')
  })

  describe('keyboard navigation', () => {
    it('should copy text of active citation summary', async () => {
      const { user } = renderComponent([generateActiveCitation()])
      const copyButton = screen.getByTestId('copy-summary')
      await fireEvent.focus(copyButton)
      await user.type(copyButton, '{enter}')

      // Read from the stub clipboard
      const clipboardText = await navigator.clipboard.readText()

      expect(clipboardText).toBe('Änderung, label1, 01.02.2022, test fileNumber, documentType1')
    })
  })
})
