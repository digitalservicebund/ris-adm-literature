import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
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
      veroeffentlichungsJahr: '',
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
      veroeffentlichungsJahr: '2024',
      dokumenttypen: [],
      verfasser: [],
    }

    const { user, emitted } = renderComponent({ aktivzitierungLiterature: existing })

    const deleteButton = screen.getByRole('button', { name: 'Eintrag löschen' })
    await user.click(deleteButton)

    expect(emitted().deleteAktivzitierungLiterature).toBeTruthy()
    expect((emitted().deleteAktivzitierungLiterature as [string[]])[0][0]).toBe('id-1')
  })

  it('updates local state when aktivzitierungLiterature prop changes', async () => {
    const { rerender } = renderComponent({
      aktivzitierungLiterature: {
        id: '1',
        uuid: '1',
        titel: 'Alt',
        veroeffentlichungsJahr: '',
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

    const searchButton = screen.getByRole('button', { name: 'Selbständige Literatur suchen' })

    await user.click(searchButton)

    expect(emitted().search).toBeTruthy()
    const payload = (emitted().search as [SliDocUnitSearchParams[]])[0][0]
    expect(payload?.titel).toBe('Testtitel')
  })

  it('clears search fields when clearSearchFields is called in creation mode', async () => {
    const wrapper = mount(AktivzitierungLiteratureInput, {
      props: { showCancelButton: false },
    })

    const titleInput = wrapper.find('input[aria-label="Hauptsachtitel / Dokumentarischer Titel"]')
    const yearInput = wrapper.find('input[aria-label="Veröffentlichungsjahr"]')

    await titleInput.setValue('Test Title')
    await yearInput.setValue('2024')

    expect((titleInput.element as HTMLInputElement).value).toBe('Test Title')
    expect((yearInput.element as HTMLInputElement).value).toBe('2024')

    wrapper.vm.clearSearchFields()
    await nextTick()

    expect((titleInput.element as HTMLInputElement).value).toBe('')
    expect((yearInput.element as HTMLInputElement).value).toBe('')
  })
})
