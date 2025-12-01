import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import { describe, expect, it } from 'vitest'
import AktivzitierungLiteratureInput from './AktivzitierungLiteratureInput.vue'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature'

type Props = {
  aktivzitierungLiterature?: AktivzitierungLiterature
  showCancelButton?: boolean
}

function renderComponent(props: Props = {}) {
  const user = userEvent.setup()

  const result = render(AktivzitierungLiteratureInput, {
    props: {
      showCancelButton: true,
      ...props,
    },
  })

  return { user, ...result }
}

describe('AktivzitierungLiteratureInput', () => {
  it('disables Übernehmen when all fields are empty', () => {
    renderComponent({ showCancelButton: false })

    const saveButton = screen.getByRole('button', { name: 'Aktivzitierung übernehmen' })
    expect(saveButton).toBeDisabled()
  })

  it('enables Übernehmen when at least one field is filled and emits update on click', async () => {
    const { user, emitted } = renderComponent()

    const titleInput = screen.getByRole('textbox', { name: 'Hauptsachtitel' })
    await user.type(titleInput, 'Testtitel')

    const saveButton = screen.getByRole('button', { name: 'Aktivzitierung übernehmen' })
    expect(saveButton).toBeEnabled()

    await user.click(saveButton)

    expect(emitted().updateAktivzitierungLiterature).toBeTruthy()
    const payload = (emitted().updateAktivzitierungLiterature as [AktivzitierungLiterature[]])[0][0]
    expect(payload?.hauptsachtitel).toBe('Testtitel')
  })

  it('enables Übernehmen when array field (verfasser) is prefilled', () => {
    const existing: AktivzitierungLiterature = {
      id: 'id-2',
      uuid: 'uuid-2',
      newEntry: false,
      hauptsachtitel: '',
      veroeffentlichungsjahr: '',
      dokumenttypen: [],
      verfasser: ['Autor'],
    }

    renderComponent({ aktivzitierungLiterature: existing, showCancelButton: false })

    const saveButton = screen.getByRole('button', { name: 'Aktivzitierung übernehmen' })
    expect(saveButton).toBeEnabled()
  })

  it('shows cancel button when showCancelButton is true and emits cancel on click', async () => {
    const { user, emitted } = renderComponent({ showCancelButton: true })

    const cancelButton = screen.getByRole('button', { name: 'Abbrechen' })
    await user.click(cancelButton)

    expect(emitted().cancel).toBeTruthy()
  })

  it('does not show cancel button when showCancelButton is false', () => {
    renderComponent({ showCancelButton: false })
    expect(screen.queryByRole('button', { name: 'Abbrechen' })).not.toBeInTheDocument()
  })

  it('shows delete button for existing entry and emits deleteAktivzitierungLiterature with id', async () => {
    const existing: AktivzitierungLiterature = {
      id: 'id-1',
      uuid: 'uuid-1',
      newEntry: false,
      hauptsachtitel: 'Titel',
      veroeffentlichungsjahr: '2024',
      dokumenttypen: [],
      verfasser: [],
    }

    const { user, emitted } = renderComponent({ aktivzitierungLiterature: existing })

    const deleteButton = screen.getByRole('button', { name: 'Eintrag löschen' })
    await user.click(deleteButton)

    expect(emitted().deleteAktivzitierungLiterature).toBeTruthy()
    expect((emitted().deleteAktivzitierungLiterature as [string[]])[0][0]).toBe('id-1')
  })

  it('does not emit delete when entry has no uuid (new entry)', async () => {
    const newEntry: AktivzitierungLiterature = { id: 'tmp-1', newEntry: true }
    const { emitted } = renderComponent({ aktivzitierungLiterature: newEntry })

    const deleteButton = screen.queryByRole('button', { name: 'Eintrag löschen' })
    expect(deleteButton).not.toBeInTheDocument()
    expect(emitted().deleteAktivzitierungLiterature).toBeUndefined()
  })

  it('updates local state when aktivzitierungLiterature prop changes', async () => {
    const { rerender } = renderComponent({
      aktivzitierungLiterature: {
        id: '1',
        uuid: '1',
        newEntry: false,
        hauptsachtitel: 'Alt',
        veroeffentlichungsjahr: '',
        dokumenttypen: [],
        verfasser: [],
      },
    })

    expect(screen.getByRole('textbox', { name: 'Hauptsachtitel' })).toHaveValue('Alt')

    await rerender({
      aktivzitierungLiterature: {
        id: '1',
        uuid: '1',
        newEntry: false,
        hauptsachtitel: 'Neu',
        veroeffentlichungsjahr: '',
        dokumenttypen: [],
        verfasser: [],
      },
      showCancelButton: true,
    })

    expect(screen.getByRole('textbox', { name: 'Hauptsachtitel' })).toHaveValue('Neu')
  })
})
