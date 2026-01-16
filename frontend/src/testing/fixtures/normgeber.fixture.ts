import { type Normgeber } from "@/domain/normgeber";
import { legalEntityFixture } from "./institution.fixture";

export const normgeberFixture: Normgeber = {
  id: "testNormgeberId",
  institution: legalEntityFixture,
  regions: [],
};
