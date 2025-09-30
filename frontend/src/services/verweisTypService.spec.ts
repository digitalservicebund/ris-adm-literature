import { describe, expect, it, vi } from 'vitest'
import { useFetchVerweisTypen } from './verweisTypService'
import {
  anwendungFixture,
  neuregelungFixture,
  rechtsgrundlageFixture,
} from '@/testing/fixtures/verweisTyp'

describe('reference type service', () => {
  it('returns reference types', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(
        JSON.stringify({
          verweisTypen: [anwendungFixture, neuregelungFixture, rechtsgrundlageFixture],
        }),
        { status: 200 },
      ),
    )

    const { data } = await useFetchVerweisTypen()

    expect(data.value?.verweisTypen).toHaveLength(3)
    expect(data.value?.verweisTypen[0]?.name).toBe(anwendungFixture.name)
  })
})
