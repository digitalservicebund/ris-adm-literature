import { describe, expect, it, vi } from 'vitest'
import { useFetchZitierArten } from '@/services/zitierArtService.ts'
import {
  zitierArtAbgrenzungFixture,
  zitierArtUebernahmeFixture,
} from '@/testing/fixtures/zitierArt.fixture.ts'

describe('ZitierArtService', () => {
  it('returns zitier arten', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(
        JSON.stringify({
          zitierArten: [zitierArtAbgrenzungFixture, zitierArtUebernahmeFixture],
        }),
        { status: 200 },
      ),
    )

    const { data } = await useFetchZitierArten()

    expect(data.value?.zitierArten).toHaveLength(2)
    expect(data.value?.zitierArten[0]?.label).toBe(zitierArtAbgrenzungFixture.label)
  })
})
