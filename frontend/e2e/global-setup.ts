import authSetup from './auth-setup.js'
import seedDataSetup from './seed-data.js'

async function globalSetup() {
  console.log('--- Starting Global Setup ---')
  await authSetup()
  await seedDataSetup()
  console.log('--- Global Setup Complete ---')
}

export default globalSetup
