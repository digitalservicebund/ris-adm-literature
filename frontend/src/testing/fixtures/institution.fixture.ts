import { InstitutionType, type Institution } from "@/domain/normgeber";

export const legalEntityFixture: Institution = {
  id: "legalEntityId",
  name: "legalEntity",
  officialName: "legalEntity",
  type: InstitutionType.LegalEntity,
  regions: [],
};

export const institutionFixture: Institution = {
  id: "institutionId",
  name: "institution",
  officialName: "institution",
  type: InstitutionType.Institution,
  regions: [],
};
