import { InstitutionType, type Institution, type Normgeber } from "@/domain/normgeber";

const institutionFixture: Institution = {
  id: "testInstId",
  name: "",
  type: InstitutionType.LegalEntity,
};

export const normgeberFixture: Normgeber = {
  id: "testNormgeberId",
  institution: institutionFixture,
  regions: [],
};
