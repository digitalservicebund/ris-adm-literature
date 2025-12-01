import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import { createTestingPinia } from '@pinia/testing'
import AktivzitierungLiteratures from './AktivzitierungLiteratures.vue'
import type { SliDocumentationUnit } from '@/domain/sli/sliDocumentUnit'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature'

const mockAktivzitierungen: AktivzitierungLiterature[] = [
  {
    id: 'aktiv-1',
    uuid: 'aktiv-1',
    newEntry: false,
    veroeffentlichungsjahr: '2025',
    verfasser: ['again and again'],
    dokumenttypen: [{ uuid: 'Ebs', abbreviation: 'Ebs', name: 'Ebs' }],
    hauptsachtitel: 'a new one',
  },
]

function renderComponent(aktivzitierungLiteratures: AktivzitierungLiterature[] = []) {
  const user = userEvent.setup()

  return {
    user,
    ...render(AktivzitierungLiteratures, {
      global: {
        plugins: [
          [
            createTestingPinia({
              initialState: {
                sliDocumentUnit: {
                  documentUnit: <SliDocumentationUnit>{
                    id: '123',
                    documentNumber: 'KSLS2025000001',
                    note: '',
                    aktivzitierungLiteratures,
                  },
                },
              },
            }),
          ],
        ],
      },
    }),
  }
}

describe('AktivzitierungLiteratures', () => {
  it('renders creation panel when there are no aktivzitierung entries', () => {
    renderComponent()

    // Component wrapper from AktivzitierungLiteratures.vue
    expect(screen.getByLabelText('Aktivzitierung')).toBeInTheDocument()

    // No list items yet
    expect(screen.queryAllByRole('listitem')).toHaveLength(0)

    // Creation form from AktivzitierungLiteratureInput is visible
    expect(screen.getByLabelText('Hauptsachtitel')).toBeInTheDocument()

    // Add button should not be visible in this state
    expect(
      screen.queryByRole('button', { name: 'Aktivzitierung hinzufügen' }),
    ).not.toBeInTheDocument()
  })

  it('renders a list of existing aktivzitierung entries', () => {
    renderComponent(mockAktivzitierungen)

    expect(screen.queryAllByRole('listitem')).toHaveLength(1)
    expect(screen.getByText('2025, again and again, (Ebs), a new one')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Aktivzitierung Editieren' })).toBeInTheDocument()
  })

  it('opens the creation panel when clicking the add button', async () => {
    const { user } = renderComponent(mockAktivzitierungen)

    await user.click(screen.getByRole('button', { name: 'Aktivzitierung hinzufügen' }))

    expect(screen.getByLabelText('Hauptsachtitel')).toBeInTheDocument()
    expect(
      screen.queryByRole('button', { name: 'Aktivzitierung hinzufügen' }),
    ).not.toBeInTheDocument()
  })

  it('shows creation panel when list is empty', () => {
    renderComponent([])
    expect(screen.getByLabelText('Aktivzitierung übernehmen')).toBeInTheDocument()
  })
  it('hides creation input and shows add button when list has entries and panel is closed', () => {
    const mockAktivzitierung: AktivzitierungLiterature = {
      id: '1',
      newEntry: false,
      hauptsachtitel: 'Titel',
    }

    renderComponent([mockAktivzitierung])
    expect(
      screen.queryByRole('button', { name: 'Aktivzitierung übernehmen' }),
    ).not.toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Aktivzitierung hinzufügen' })).toBeVisible()
  })
})
