import { request, expect, APIRequestContext } from '@playwright/test'
import fs from 'fs'
import path, { dirname } from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

async function waitForDocumentByTitle(apiContext: APIRequestContext, title: string) {
  console.log(`\nPolling for document with title: "${title}"...`)
  const maxRetries = 10
  const retryDelay = 500

  for (let i = 0; i < maxRetries; i++) {
    try {
      const response = await apiContext.get('api/documentation-units', {
        params: { langueberschrift: title },
      })

      if (response.ok()) {
        const data = await response.json()
        const documents = data.documentationUnitsOverview

        if (
          Array.isArray(documents) &&
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          documents.some((doc: any) => doc.langueberschrift === title)
        ) {
          console.log(`Document with title "${title}" is indexed and found.`)
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

  throw new Error(`Global setup failed: Document with title "${title}" was not indexed in time.`)
}

async function globalSetup() {
  const apiContext = await request.newContext({ baseURL: 'http://localhost:5173' })
  console.log('--- Starting Global Setup ---')

  const doc1Title = 'Alpha Global Setup Document'
  const doc2Title = 'Beta Global Setup Document'

  const createResponse1 = await apiContext.post('api/documentation-units')
  expect(createResponse1.ok()).toBeTruthy()
  const doc1Shell = await createResponse1.json()

  const updateResponse1 = await apiContext.put(
    `api/documentation-units/${doc1Shell.documentNumber}`,
    {
      data: {
        documentNumber: doc1Shell.documentNumber,
        langueberschrift: doc1Title,
        references: [{ legalPeriodicalRawValue: 'BGB', citation: '123' }],
        zitierdaten: ['2024-06-17'],
      },
    },
  )
  expect(updateResponse1.ok()).toBeTruthy()

  const createResponse2 = await apiContext.post('api/documentation-units')
  expect(createResponse2.ok()).toBeTruthy()
  const doc2Shell = await createResponse2.json()

  const updateResponse2 = await apiContext.put(
    `api/documentation-units/${doc2Shell.documentNumber}`,
    {
      data: {
        documentNumber: doc2Shell.documentNumber,
        langueberschrift: doc2Title,
        references: [{ legalPeriodicalRawValue: 'BGB', citation: '456' }],
        zitierdaten: ['2024-06-18'],
      },
    },
  )
  expect(updateResponse2.ok()).toBeTruthy()

  await waitForDocumentByTitle(apiContext, doc1Title)
  await waitForDocumentByTitle(apiContext, doc2Title)

  const testData = {
    docNumber1: doc1Shell.documentNumber,
    docNumber2: doc2Shell.documentNumber,
    doc1Title: doc1Title,
    doc2Title: doc2Title,
  }

  const outputPath = path.join(__dirname, 'test-data.json')
  fs.writeFileSync(outputPath, JSON.stringify(testData, null, 2))

  await apiContext.dispose()
  console.log(`--- Global Setup Complete: test-data.json created at ${outputPath} ---`)
}

export default globalSetup
