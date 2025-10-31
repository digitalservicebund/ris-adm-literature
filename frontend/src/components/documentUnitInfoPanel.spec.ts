import { beforeEach, describe, expect, it, vi } from 'vitest'
import { render, screen } from '@testing-library/vue'
import DocumentUnitInfoPanel from '@/components/DocumentUnitInfoPanel.vue'
import { setActivePinia } from 'pinia'
import { createTestingPinia } from '@pinia/testing'
import userEvent from '@testing-library/user-event'
import { useStoreForRoute } from '@/composables/useStoreForRoute'
import type { DocumentUnitStore } from '@/stores/types'
import { DocumentCategory } from '@/domain/documentType'

vi.mock('vue-router', () => ({
  useRoute: () => ({
    meta: { documentCategory: DocumentCategory.VERWALTUNGSVORSCHRIFTEN },
  }),
}))

function mockDocumentUnitStore(callback = vi.fn()) {
  const documentUnitStore = useStoreForRoute<DocumentUnitStore>()
  documentUnitStore.update = callback

  return documentUnitStore
}

function renderComponent(options?: {
  heading?: string /*coreData?: CoreData*/
  hideSaveButton?: boolean
}) {
  return {
    ...render(DocumentUnitInfoPanel, {
      props: { heading: options?.heading ?? '', hideSaveButton: options?.hideSaveButton ?? false },
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
    expect(await screen.findByText('Gespeichert', { exact: false })).toBeInTheDocument()
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

  it('hide the save button when prop is set', async () => {
    // given
    mockDocumentUnitStore(vi.fn().mockResolvedValueOnce(false))
    renderComponent({ hideSaveButton: true })

    // then
    expect(screen.queryByRole('button', { name: 'Speichern' })).not.toBeInTheDocument()
  })
})
