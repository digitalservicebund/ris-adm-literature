import { request, expect, APIRequestContext } from '@playwright/test'
import fs from 'fs'
import path, { dirname } from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

async function waitForDocumentByDocumentNumber(
  apiContext: APIRequestContext,
  documentNumber: string,
) {
  console.log(`\nPolling for document with documentNumber: "${documentNumber}"...`)
  const maxRetries = 10
  const retryDelay = 500

  for (let i = 0; i < maxRetries; i++) {
    try {
      const response = await apiContext.get('api/documentation-units', {
        params: { documentNumber: documentNumber },
      })

      if (response.ok()) {
        const data = await response.json()
        const documents = data.documentationUnitsOverview

        if (
          Array.isArray(documents) &&
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          documents.some((doc: any) => doc.documentNumber === documentNumber)
        ) {
          console.log(`Document with documentNumber "${documentNumber}" is indexed and found.`)
          return
        }
      } else {
        console.error(`ERROR: ${response.status()}`)
        const responseBody = await response.text()
        console.error(`Response Body: ${responseBody}`)
      }
    } catch (error) {
      console.error(`ERROR: ${error}`)
    }
    await new Promise((resolve) => setTimeout(resolve, retryDelay))
  }

  throw new Error(
    `Global setup failed: Document with documentNumber "${documentNumber}" was not indexed in time.`,
  )
}

async function globalSetup() {
  const apiContext = await request.newContext({ baseURL: 'http://localhost:5173' })
  console.log('--- Starting Global Setup ---')

  const doc1Title = 'Alpha Global Setup Document'
  const doc2Title = 'Beta Global Setup Document'
  const doc1References = [{ legalPeriodicalRawValue: 'BGB', citation: '123' }]
  const doc2References = [{ legalPeriodicalRawValue: 'BGB', citation: '456' }]
  const doc1Zitierdaten = ['2024-06-17']
  const doc2Zitierdaten = ['2024-06-18']

  const createResponse1 = await apiContext.post('api/documentation-units')
  expect(createResponse1.ok()).toBeTruthy()
  const doc1 = await createResponse1.json()

  const updateResponse1 = await apiContext.put(`api/documentation-units/${doc1.documentNumber}`, {
    data: {
      documentNumber: doc1.documentNumber,
      langueberschrift: doc1Title,
      references: doc1References,
      zitierdaten: doc1Zitierdaten,
    },
  })
  expect(updateResponse1.ok()).toBeTruthy()

  const createResponse2 = await apiContext.post('api/documentation-units')
  expect(createResponse2.ok()).toBeTruthy()
  const doc2 = await createResponse2.json()

  const updateResponse2 = await apiContext.put(`api/documentation-units/${doc2.documentNumber}`, {
    data: {
      documentNumber: doc2.documentNumber,
      langueberschrift: doc2Title,
      references: doc2References,
      zitierdaten: doc2Zitierdaten,
    },
  })
  expect(updateResponse2.ok()).toBeTruthy()

  await waitForDocumentByDocumentNumber(apiContext, doc1.documentNumber)
  await waitForDocumentByDocumentNumber(apiContext, doc2.documentNumber)

  const testData = {
    docNumber1: doc1.documentNumber,
    docNumber2: doc2.documentNumber,
    doc1Title: doc1Title,
    doc2Title: doc2Title,
    doc1References: doc1References,
    doc2References: doc2References,
    doc1Zitierdaten: doc1Zitierdaten,
    doc2Zitierdaten: doc2Zitierdaten,
  }

  const outputPath = path.join(__dirname, 'test-data.json')
  fs.writeFileSync(outputPath, JSON.stringify(testData, null, 2))

  await apiContext.dispose()
  console.log(`--- Global Setup Complete: test-data.json created at ${outputPath} ---`)
}

export default globalSetup
