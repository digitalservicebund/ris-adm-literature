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
    },
  },
} as const

export const ROUTE_PATHS = {
  ROOT: '/',
  FORBIDDEN: '/forbidden',
  ADM: {
    BASE: '/verwaltungsvorschriften',
    DOCUMENT_UNIT: {
      NEW: 'documentUnit/new',
      EDIT: 'documentUnit/:documentNumber',
      FUNDSTELLEN: 'fundstellen',
      RUBRIKEN: 'rubriken',
      ABGABE: 'abgabe',
    },
  },
  ULI: {
    BASE: '/literatur-unselbstaendig',
    DOCUMENT_UNIT: {
      NEW: 'documentUnit/new',
      EDIT: 'documentUnit/:documentNumber',
      RUBRIKEN: 'rubriken',
    },
  },
} as const
