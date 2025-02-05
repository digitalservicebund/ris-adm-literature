import { describe, it, expect } from 'vitest'
import DocumentUnit from './documentUnit'

describe('DocumentUnit', () => {
  it('instantiates with uuid', () => {
    const documentUnit = new DocumentUnit('foo')
    expect(documentUnit.uuid).toEqual('foo')
  })
})
