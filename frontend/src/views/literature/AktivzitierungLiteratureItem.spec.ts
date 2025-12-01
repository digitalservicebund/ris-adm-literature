import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature'
import AktivzitierungLiteratureItem from './AktivzitierungLiteratureItem.vue'

const mockAktivzitierung: AktivzitierungLiterature = {
  id: 'aktiv-1',
  uuid: 'aktiv-1',
  newEntry: false,
  veroeffentlichungsjahr: '2025',
  verfasser: ['again and again'],
  dokumenttypen: [{ uuid: 'Ebs', abbreviation: 'Ebs', name: 'Ebs' }],
  hauptsachtitel: 'a new one',
}

function renderComponent(aktivzitierungLiterature: AktivzitierungLiterature = mockAktivzitierung) {
  const user = userEvent.setup()

  const utils = render(AktivzitierungLiteratureItem, {
    props: { aktivzitierungLiterature },
  })

  return { user, ...utils }
}

describe('AktivzitierungLiteratureItem', () => {
  it('renders the summary in view mode', () => {
    renderComponent()

    expect(screen.getByText('2025, again and again, (Ebs), a new one')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Aktivzitierung Editieren' })).toBeInTheDocument()
  })

  it('toggles to edit mode when clicking the expand button', async () => {
    const { user } = renderComponent()

    await user.click(screen.getByRole('button', { name: 'Aktivzitierung Editieren' }))
    expect(screen.getByLabelText('Hauptsachtitel')).toBeInTheDocument()
  })

  it('emits updateAktivzitierungLiterature when save is clicked in edit mode', async () => {
    const { user, emitted } = renderComponent()

    await user.click(screen.getByRole('button', { name: 'Aktivzitierung Editieren' }))
    await user.click(screen.getByRole('button', { name: 'Aktivzitierung übernehmen' }))

    expect(emitted().updateAktivzitierungLiterature).toBeTruthy()
    expect(screen.getByRole('button', { name: 'Aktivzitierung Editieren' })).toBeInTheDocument()
  })

  it('leaves edit mode on cancel', async () => {
    const { user } = renderComponent()

    await user.click(screen.getByRole('button', { name: 'Aktivzitierung Editieren' }))
    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))

    expect(screen.getByRole('button', { name: 'Aktivzitierung Editieren' })).toBeInTheDocument()
    expect(screen.getByText('2025, again and again, (Ebs), a new one')).toBeInTheDocument()
  })

  it('emits deleteAktivzitierungLiterature when delete is clicked', async () => {
    const { user, emitted } = renderComponent()

    await user.click(screen.getByRole('button', { name: 'Aktivzitierung Editieren' }))

    const deleteButtons = screen.getAllByRole('button', { name: 'Eintrag löschen' })

    const entryDeleteButton = deleteButtons[1]

    await user.click(entryDeleteButton!)

    expect(emitted().deleteAktivzitierungLiterature).toBeTruthy()
  })

  it('enters and leaves edit mode on edit and cancel', async () => {
    const { user } = renderComponent()
    await user.click(screen.getByRole('button', { name: 'Aktivzitierung Editieren' }))
    expect(screen.getByRole('button', { name: 'Abbrechen' })).toBeVisible()

    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))
    expect(screen.getByRole('button', { name: 'Aktivzitierung Editieren' })).toBeVisible()
  })

  it('renders summary with only title when other fields are missing', () => {
    const mockAktivzitierung: AktivzitierungLiterature = {
      id: '1',
      newEntry: false,
      hauptsachtitel: 'Nur Titel',
      veroeffentlichungsjahr: undefined,
      verfasser: [],
      dokumenttypen: [],
    }
    renderComponent(mockAktivzitierung)
    expect(screen.getByText('Nur Titel')).toBeVisible()
  })

  it('renders only title when other summary fields are missing', () => {
    const mockAktivzitierung: AktivzitierungLiterature = {
      id: '1',
      newEntry: false,
      hauptsachtitel: 'Nur Titel',
    }

    renderComponent(mockAktivzitierung)

    expect(screen.getByText('Nur Titel')).toBeVisible()
  })
})
