import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature'
import AktivzitierungLiteratureItem from './AktivzitierungLiteratureItem.vue'

const mockAktivzitierung: AktivzitierungLiterature = {
  id: 'aktiv-1',
  uuid: 'aktiv-1',
  veroeffentlichungsjahr: '2025',
  verfasser: ['again and again'],
  dokumenttypen: [{ uuid: 'Ebs', abbreviation: 'Ebs', name: 'Ebs' }],
  titel: 'a new one',
}

function renderComponent(
  aktivzitierungLiterature: AktivzitierungLiterature = mockAktivzitierung,
  extraProps: Record<string, unknown> = {},
) {
  const user = userEvent.setup()

  const utils = render(AktivzitierungLiteratureItem, {
    props: { aktivzitierungLiterature, ...extraProps },
  })

  return { user, ...utils }
}

describe('AktivzitierungLiteratureItem', () => {
  it('renders the summary in view mode', () => {
    renderComponent()

    expect(screen.getByText('2025, again and again, (Ebs)')).toBeInTheDocument()
    expect(screen.getByText('a new one')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Eintrag bearbeiten' })).toBeInTheDocument()
  })

  it('emits editStart when clicking the edit button', async () => {
    const { user, emitted } = renderComponent()

    await user.click(screen.getByRole('button', { name: 'Eintrag bearbeiten' }))

    expect(emitted().editStart).toBeTruthy()
  })

  it('shows the input form when isEditing is true', () => {
    renderComponent(mockAktivzitierung, { isEditing: true })

    const titleInput = screen.getByRole('textbox', {
      name: 'Hauptsachtitel / Dokumentarischer Titel',
    })
    expect(titleInput).toBeInTheDocument()
    expect(titleInput).toHaveValue('a new one')
  })

  it('emits updateAktivzitierungLiterature when save is clicked in edit mode', async () => {
    const { user, emitted } = renderComponent(mockAktivzitierung, { isEditing: true })

    await user.click(screen.getByRole('button', { name: 'Aktivzitierung übernehmen' }))

    expect(emitted().updateAktivzitierungLiterature).toBeTruthy()
  })

  it('emits cancelEdit when cancel is clicked in edit mode', async () => {
    const { user, emitted } = renderComponent(mockAktivzitierung, { isEditing: true })

    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))

    expect(emitted().cancelEdit).toBeTruthy()
  })

  it('emits deleteAktivzitierungLiterature and cancelEdit when delete is clicked', async () => {
    const { user, emitted } = renderComponent(mockAktivzitierung, { isEditing: true })

    const deleteButtons = screen.getAllByRole('button', { name: 'Eintrag löschen' })
    const entryDeleteButton = deleteButtons[deleteButtons.length - 1]

    await user.click(entryDeleteButton!)

    expect(emitted().deleteAktivzitierungLiterature).toBeTruthy()
    expect(emitted().cancelEdit).toBeTruthy()
  })

  it('renders summary with only title when other fields are missing', () => {
    const onlyTitle: AktivzitierungLiterature = {
      id: '1',
      titel: 'Nur Titel',
      veroeffentlichungsjahr: undefined,
      verfasser: [],
      dokumenttypen: [],
    }
    renderComponent(onlyTitle)
    expect(screen.getByText('Nur Titel')).toBeVisible()
  })

  it('renders only title when other summary fields are missing', () => {
    const onlyTitle: AktivzitierungLiterature = {
      id: '1',
      titel: 'Nur Titel',
    }

    renderComponent(onlyTitle)

    expect(screen.getByText('Nur Titel')).toBeVisible()
  })
})
