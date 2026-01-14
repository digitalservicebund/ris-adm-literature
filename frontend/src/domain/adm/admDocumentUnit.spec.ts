import { describe, it, expect } from "vitest";
import { type AdmDocumentationUnit } from "./admDocumentUnit";
import { fundstelleFixture } from "@/testing/fixtures/fundstelle.fixture";

describe("DocumentUnit", () => {
  it("instantiates with id and documentNumber", () => {
    // given when
    const documentUnit: AdmDocumentationUnit = {
      id: "foo",
      documentNumber: "KSNR054920707",
      fieldsOfLaw: [],
      fundstellen: [],
      note: "",
    };

    // then
    expect(documentUnit.id).toEqual("foo");
    expect(documentUnit.documentNumber).toEqual("KSNR054920707");
  });

  it("sets a fundstelle", () => {
    // given when
    const documentUnit: AdmDocumentationUnit = {
      id: "foo",
      documentNumber: "KSNR054920707",
      fundstellen: [fundstelleFixture],
      fieldsOfLaw: [],
      note: "",
    };

    // then
    expect(documentUnit.fundstellen).toHaveLength(1);
    expect(documentUnit.fundstellen?.at(0)?.periodikum?.title).toEqual("Bundesanzeiger");
    expect(documentUnit.fundstellen?.at(0)?.periodikum?.abbreviation).toEqual("BAnz");
    expect(documentUnit.fundstellen?.at(0)?.zitatstelle).toEqual("1973, 608");
  });
});
