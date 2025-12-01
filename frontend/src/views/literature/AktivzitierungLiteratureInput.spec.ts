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
})
