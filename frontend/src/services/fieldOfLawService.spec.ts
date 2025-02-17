import { describe, expect, it } from 'vitest'
import FieldOfLawService from '@/services/fieldOfLawService.ts'

describe('fieldOfLawService', () => {
  it('getChildrenOf', async () => {
    const fields = await FieldOfLawService.getChildrenOf('')
    expect(fields.status).toEqual(200)
  })
  it('getTreeForIdentifier', async () => {
    const fields = await FieldOfLawService.getTreeForIdentifier('')
    expect(fields.status).toEqual(200)
  })
  it('searchForFieldsOfLaw', async () => {
    const fields = await FieldOfLawService.searchForFieldsOfLaw(0, 0)
    expect(fields.status).toEqual(200)
  })
})
