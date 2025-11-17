export const ROUTE_NAMES = {
  ROOT_REDIRECT: 'RootRedirect',
  FORBIDDEN: 'Forbidden',
  NOT_FOUND: 'Error 404 not found',

  // ADM (Verwaltungsvorschriften)
  ADM: {
    START_PAGE: 'adm-start-page',
    DOCUMENT_UNIT: {
      NEW: 'adm-documentUnit-new',
      EDIT: 'adm-documentUnit-documentNumber',
      FUNDSTELLEN: 'adm-documentUnit-documentNumber-fundstellen',
      RUBRIKEN: 'adm-documentUnit-documentNumber-rubriken',
      ABGABE: 'adm-documentUnit-documentNumber-abgabe',
    },
  },

  // ULI (Unselbstständige Literatur)
  ULI: {
    START_PAGE: 'uli-start-page',
    DOCUMENT_UNIT: {
      NEW: 'uli-documentUnit-new',
      EDIT: 'uli-documentUnit-documentNumber',
      RUBRIKEN: 'uli-documentUnit-documentNumber-rubriken',
      ABGABE: 'uli-documentUnit-documentNumber-abgabe',
    },
  },
  // SLI (Selbstständige Literatur)
  SLI: {
    START_PAGE: 'sli-start-page',
    DOCUMENT_UNIT: {
      NEW: 'sli-documentUnit-new',
      EDIT: 'sli-documentUnit-documentNumber',
      RUBRIKEN: 'sli-documentUnit-documentNumber-rubriken',
    },
  },
} as const

const DOCUMENT_UNIT_BASE = 'dokumentationseinheit'

export const ROUTE_PATHS = {
  ROOT: '/',
  FORBIDDEN: '/forbidden',
  DOCUMENT_UNIT_BASE: DOCUMENT_UNIT_BASE,
  ADM: {
    BASE: '/verwaltungsvorschriften',
    DOCUMENT_UNIT: {
      NEW: `${DOCUMENT_UNIT_BASE}/new`,
      EDIT: `${DOCUMENT_UNIT_BASE}/:documentNumber`,
      FUNDSTELLEN: 'fundstellen',
      RUBRIKEN: 'rubriken',
      ABGABE: 'abgabe',
    },
  },
  ULI: {
    BASE: '/literatur-unselbststaendig',
    DOCUMENT_UNIT: {
      NEW: `${DOCUMENT_UNIT_BASE}/new`,
      EDIT: `${DOCUMENT_UNIT_BASE}/:documentNumber`,
      RUBRIKEN: 'rubriken',
      ABGABE: 'abgabe',
    },
  },
  SLI: {
    BASE: '/literatur-selbststaendig',
    DOCUMENT_UNIT: {
      NEW: `${DOCUMENT_UNIT_BASE}/new`,
      EDIT: `${DOCUMENT_UNIT_BASE}/:documentNumber`,
      RUBRIKEN: 'rubriken',
    },
  },
} as const
