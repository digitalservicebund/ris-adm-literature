import { describe, expect, it, vi } from 'vitest'
import { useFetchRegions } from './regionService'
import { ref } from 'vue'

vi.mock('@/services/apiService', () => {
  return {
    useApiFetch: vi.fn().mockReturnValue({
      json: () => ({
        data: ref({
          regions: [
            {
              id: 'regionId0',
              code: 'AA',
              longText: null,
            },
            {
              id: 'regionId1',
              code: 'BB',
              longText: null,
            },
          ],
        }),
      }),
    }),
  }
})

describe('regions service', () => {
  it('calls useFetch with the correct URL and returns regions', async () => {
    const { data } = await useFetchRegions()

    expect(data.value?.regions).toHaveLength(2)
    expect(data.value?.regions[0].code).toBe('AA')
  })
})
