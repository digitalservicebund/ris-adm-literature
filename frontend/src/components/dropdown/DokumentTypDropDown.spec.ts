import { beforeEach, describe, expect, it, vi } from 'vitest'
import {
  docTypeAnordnungFixture,
  docTypeBekanntmachungFixture,
} from '@/testing/fixtures/documentType.fixture'
import { DocumentCategory } from '@/domain/documentType'
import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import DokumentTypDropDown from './DokumentTypDropDown.vue'

describe('DokumentTypDropDown', () => {
  const baseProps = {
    inputId: 'foo',
    isInvalid: false,
    documentCategory: DocumentCategory.VERWALTUNGSVORSCHRIFTEN,
    ariaLabel: 'DokumentTyp',
  }

  beforeEach(() => {
    vi.resetAllMocks()
    vi.resetModules()
  })

  it('renders correctly', async () => {
    const fetchSpy = vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(
        JSON.stringify({ documentTypes: [docTypeAnordnungFixture, docTypeBekanntmachungFixture] }),
        {
          status: 200,
        },
      ),
    )

    render(DokumentTypDropDown, {
      props: {
        ...baseProps,
        modelValue: '',
      },
    })

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1))
    const autocomplete = screen.getByRole('combobox', { name: 'DokumentTyp' })
    expect(autocomplete).toBeVisible()
  })

  it('renders correctly on fetching error', async () => {
    const fetchSpy = vi.spyOn(window, 'fetch').mockRejectedValue('fetch error')

    render(DokumentTypDropDown, {
      props: {
        ...baseProps,
      },
    })

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1))
    const autocomplete = screen.getByRole('combobox', { name: 'DokumentTyp' })
    expect(autocomplete).toBeVisible()
  })

  it('renders correctly with an existing value', async () => {
    const user = userEvent.setup()
    const fetchSpy = vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(
        JSON.stringify({ documentTypes: [docTypeAnordnungFixture, docTypeBekanntmachungFixture] }),
        {
          status: 200,
        },
      ),
    )

    render(DokumentTypDropDown, {
      props: {
        ...baseProps,
        modelValue: 'Anordnung',
      },
    })

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1))
    await user.click(screen.getByRole('button', { name: 'Vorschläge anzeigen' }))
    expect(screen.getByRole('option', { name: 'Anordnung' })).toBeVisible()
  })

  it('emits the abbreviation up', async () => {
    const user = userEvent.setup()
    const fetchSpy = vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(
        JSON.stringify({ documentTypes: [docTypeAnordnungFixture, docTypeBekanntmachungFixture] }),
        {
          status: 200,
        },
      ),
    )

    const { emitted } = render(DokumentTypDropDown, {
      props: {
        ...baseProps,
        modelValue: '',
      },
    })

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1))
    await user.click(screen.getByRole('button', { name: 'Vorschläge anzeigen' }))
    expect(screen.getByRole('option', { name: 'Bekanntmachung' })).toBeVisible()

    await user.click(screen.getByRole('option', { name: 'Bekanntmachung' }))
    const emittedVal = emitted('update:modelValue') as [string[]]
    const abbr = emittedVal?.[0][0]
    expect(abbr).toBe('Bekanntmachung')
  })
})
