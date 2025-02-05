import { describe, expect, test } from 'vitest'
import { render, screen } from '@testing-library/vue'
import PageError from '@/components/PageError.vue'
import errorMessages from '@/i18n/errors.json'

function renderComponent(options?: {
  title?: string
  description?: string
  backButtonLabel?: string
}) {
  return render(PageError, {
    props: {
      title: options?.title,
      description: options?.description,
      backButtonLabel: options?.backButtonLabel,
    },
  })
}

describe('PageError', () => {
  test('display page error', async () => {
    renderComponent({
      title: errorMessages.DOCUMENT_UNIT_SEARCH_FAILED.title,
      description: errorMessages.DOCUMENT_UNIT_SEARCH_FAILED.description,
    })

    expect(await screen.findByText(/Die Suchergebnisse konnten nicht geladen werden/)).toBeVisible()
  })
})
