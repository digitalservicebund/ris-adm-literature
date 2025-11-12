import ActiveCitation from '@/domain/activeCitation'
import ActiveReference from '@/domain/activeReference'
import type { AdmDocumentationUnit } from '@/domain/adm/admDocumentUnit'
import SingleNorm from '@/domain/singleNorm'
import { activeCitationFixture } from './activeCitation'
import NormReference from '@/domain/normReference'

export const admDocumentUnitFixture: AdmDocumentationUnit = {
  id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
  documentNumber: 'KSNR054920707',
  fieldsOfLaw: [],
  fundstellen: [],
  activeCitations: [new ActiveCitation(activeCitationFixture)],
  activeReferences: [new ActiveReference({ singleNorms: [new SingleNorm({ singleNorm: '§ 5' })] })],
  normReferences: [new NormReference({ singleNorms: [new SingleNorm({ singleNorm: '§ 7' })] })],
  note: '',
}
