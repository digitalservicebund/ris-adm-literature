import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import { describe, expect, it } from 'vitest'
import AktivzitierungLiteratureInput from './AktivzitierungLiteratureInput.vue'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature'
import type { SliDocUnitSearchParams } from '@/domain/sli/sliDocumentUnit'

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

    const titleInput = screen.getByRole('textbox', {
      name: 'Hauptsachtitel / Dokumentarischer Titel',
    })
    await user.type(titleInput, 'Testtitel')

    const saveButton = screen.getByRole('button', { name: 'Aktivzitierung übernehmen' })
    expect(saveButton).toBeEnabled()

    await user.click(saveButton)

    expect(emitted().updateAktivzitierungLiterature).toBeTruthy()
    const payload = (emitted().updateAktivzitierungLiterature as [AktivzitierungLiterature[]])[0][0]
    expect(payload?.titel).toBe('Testtitel')
  })

  it('enables Übernehmen when array field (verfasser) is prefilled', () => {
    const existing: AktivzitierungLiterature = {
      id: 'id-2',
      uuid: 'uuid-2',
      titel: '',
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
      titel: 'Titel',
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
    const newEntry: AktivzitierungLiterature = { id: 'tmp-1' }
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
        titel: 'Alt',
        veroeffentlichungsjahr: '',
        dokumenttypen: [],
        verfasser: [],
      },
    })

    expect(
      screen.getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' }),
    ).toHaveValue('Alt')

    await rerender({
      aktivzitierungLiterature: {
        id: '1',
        uuid: '1',
        titel: 'Neu',
        veroeffentlichungsjahr: '',
        dokumenttypen: [],
        verfasser: [],
      },
      showCancelButton: true,
    })

    expect(
      screen.getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' }),
    ).toHaveValue('Neu')
  })

  it('emits the search params when clicking on the search button', async () => {
    const { user, emitted } = renderComponent()

    const titleInput = screen.getByRole('textbox', {
      name: 'Hauptsachtitel / Dokumentarischer Titel',
    })
    await user.type(titleInput, 'Testtitel')

    const searchButton = screen.getByRole('button', { name: 'Selbststaendige Literatur suchen' })

    await user.click(searchButton)

    expect(emitted().search).toBeTruthy()
    const payload = (emitted().search as [SliDocUnitSearchParams[]])[0][0]
    expect(payload?.titel).toBe('Testtitel')
  })
})
