import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import {
  docTypeAnordnungFixture,
  docTypeBekanntmachungFixture,
} from '@/testing/fixtures/documentType'
import DokumentTyp from './DokumentTyp.vue'
import { DocumentCategory } from '@/domain/documentType'

vi.mock('@digitalservicebund/ris-ui/components', () => ({
  RisAutoComplete: {
    name: 'RisAutoComplete',
    template: `<div><input data-testid="autocomplete" @input="$emit('update:model-value', $event.target.value)" /></div>`,
    props: ['modelValue', 'suggestions', 'initialLabel'],
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

  it.skip('emits updated model value when selection changes', async () => {
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

    // when
    const input = wrapper.find('[data-testid="autocomplete"]')
    await input.setValue('Anordnung')

    // then
    const emitted = wrapper.emitted('update:modelValue')!
    expect(emitted).toHaveLength(1)
    expect(emitted[0]?.[0]).toEqual(docTypeAnordnungFixture)

    // when
    await input.setValue('unknownId')

    // then undefined is emitted
    expect(emitted).toHaveLength(2)
    expect(emitted[1]?.[0]).toEqual(undefined)
  })
})
