import { describe, expect, it } from 'vitest'
import service from '@/services/comboboxItemService'
import { ref } from 'vue'

describe('comboboxItemService', () => {
  it('should return a list of dummy courts', async () => {
    // when
    const result = await service.getCourts(ref(undefined))

    // then
    expect(result.data.value?.length).toEqual(2)
  })

  it('should return a list of dummy organs', async () => {
    // when
    const result = await service.getOrgans(ref(undefined))

    // then
    expect(result.data.value?.length).toEqual(2)
  })

  it('should return a list of dummy regions', async () => {
    // when
    const result = await service.getRegions(ref(undefined))

    // then
    expect(result.data.value?.length).toEqual(2)
  })
})
