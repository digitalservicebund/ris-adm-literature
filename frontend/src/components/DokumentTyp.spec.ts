import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import {
  docTypeAnordnungFixture,
  docTypeBekanntmachungFixture,
} from '@/testing/fixtures/documentType'
import DokumentTyp from './DokumentTyp.vue'
import { DocumentCategory } from '@/domain/documentType'

vi.mock('@digitalservicebund/ris-ui/components', () => ({
  RisAutoCompleteMultiple: {
    name: 'RisAutoCompleteMultiple',
    template: `<div><input data-testid="autocomplete" @input="$emit('update:model-value', $event.target.value)" /></div>`,
    props: ['modelValue', 'suggestions'],
  },
}))

describe('DokumentTyp', () => {
  it('renders correctly', async () => {
    const fetchSpy = vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(
        JSON.stringify({ documentTypes: [docTypeAnordnungFixture, docTypeBekanntmachungFixture] }),
        {
          status: 200,
        },
      ),
    )

    const wrapper = mount(DokumentTyp, {
      props: {
        inputId: 'foo',
        invalid: false,
        modelValue: [],
        documentCategory: DocumentCategory.LITERATUR_UNSELBSTSTAENDIG,
      },
    })

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1))

    const input = wrapper.find('[data-testid="autocomplete"]')
    expect(input.exists()).toBe(true)
  })

  it('renders correctly on fetching error', async () => {
    const fetchSpy = vi.spyOn(window, 'fetch').mockRejectedValue('fetch error')

    const wrapper = mount(DokumentTyp, {
      props: {
        inputId: 'foo',
        invalid: false,
        modelValue: [],
        documentCategory: DocumentCategory.LITERATUR_UNSELBSTSTAENDIG,
      },
    })

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1))

    const input = wrapper.find('[data-testid="autocomplete"]')
    expect(input.exists()).toBe(true)
  })
})
