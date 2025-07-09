import { beforeEach, describe, expect, it, vi } from 'vitest'
import { render, screen } from '@testing-library/vue'
import DocumentUnitInfoPanel from '@/components/DocumentUnitInfoPanel.vue'
import { useDocumentUnitStore } from '@/stores/documentUnitStore.ts'
import { setActivePinia } from 'pinia'
import { createTestingPinia } from '@pinia/testing'
import userEvent from '@testing-library/user-event'

function mockDocumentUnitStore(callback = vi.fn()) {
  const documentUnitStore = useDocumentUnitStore()
  documentUnitStore.updateDocumentUnit = callback

  return documentUnitStore
}

function renderComponent(options?: { heading?: string /*coreData?: CoreData*/ }) {
  return {
    ...render(DocumentUnitInfoPanel, {
      props: { heading: options?.heading ?? '' },
    }),
  }
}

describe('documentUnit InfoPanel', () => {
  beforeEach(() => {
    setActivePinia(createTestingPinia())
  })

  it('renders heading if given', async () => {
    renderComponent({ heading: 'Header' })

    expect(await screen.findByText('Header')).toBeVisible()
  })

  it('click on save renders last saved information', async () => {
    const user = userEvent.setup()

    // given
    mockDocumentUnitStore(vi.fn().mockResolvedValueOnce(true))
    renderComponent()

    // when
    await user.click(screen.getByRole('button', { name: 'Speichern' }))

    // then
    expect(await screen.findByText('Zuletzt', { exact: false })).toBeInTheDocument()
  })

  it('click on save renders error information', async () => {
    const user = userEvent.setup()

    // given
    mockDocumentUnitStore(vi.fn().mockResolvedValueOnce(false))
    renderComponent()

    // when
    await user.click(screen.getByRole('button', { name: 'Speichern' }))

    // then
    expect(
      await screen.findByText(
        'Fehler beim Speichern: Dokumentationseinheit konnte nicht aktualisiert werden.',
      ),
    ).toBeInTheDocument()
  })
})
