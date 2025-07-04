import Keycloak from 'keycloak-js'

const keycloak = new Keycloak({
  url: 'http://localhost:8443',
  realm: 'ris',
  clientId: 'ris-vwv-local',
})

export default keycloak
