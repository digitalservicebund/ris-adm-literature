import { useFetchInstitutions } from "@/services/institutionService";
import { describe, expect, it, vi } from "vitest";
import { ref } from "vue";

vi.mock("@/services/apiService", () => {
  return {
    useApiFetch: vi.fn().mockReturnValue({
      json: () => ({
        data: ref({
          institutions: [
            {
              id: "institutionId1",
              name: "Erstes Organ",
              officialName: "Organ Eins",
              type: "INSTITUTION",
              regions: [],
            },
            {
              id: "institutionId2",
              name: "Zweite Jurpn",
              officialName: null,
              type: "LEGAL_ENTITY",
              regions: [],
            },
          ],
        }),
      }),
    }),
  };
});

describe("institution service", () => {
  it("calls useApiFetch with the correct URL and returns institutions", async () => {
    const { data } = await useFetchInstitutions();

    expect(data.value?.institutions).toHaveLength(2);
    expect(data.value?.institutions[0].name).toBe("Erstes Organ");
  });
});
