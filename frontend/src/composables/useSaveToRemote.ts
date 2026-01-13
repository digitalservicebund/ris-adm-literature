import { computed, ref } from "vue";
import errorMessages from "@/i18n/errors.json";

function getCurrentTime(dateSaved: Date) {
  const fullHour = ("0" + dateSaved.getHours()).slice(-2);
  const fullMinute = ("0" + dateSaved.getMinutes()).slice(-2);
  return `${fullHour}:${fullMinute}`;
}

/**
 * A generic composable for saving data to a remote backend and abstracts the common "save" (update) workflow.
 *
 * @template T - A store-like object type that implements an `update()` method returning `Promise<boolean>`.
 *
 * @param {T} store - The store instance or object that exposes an asynchronous `update()` method.
 *                    The `update()` method should return `true` on success and `false` on failure.
 *
 * @returns {{
 *   saveIsInProgress: Ref<boolean>,
 *   triggerSave: () => Promise<void>,
 *   lastSaveError: Ref<{ title: string } | undefined>,
 *   formattedLastSavedOn: ComputedRef<string | undefined>
 * }}
 */
export function useSaveToRemote<T extends { update: () => Promise<boolean> }>(store: T) {
  const saveIsInProgress = ref(false);
  const lastSaveError = ref<{ title: string } | undefined>(undefined);
  const lastSavedOn = ref<Date | undefined>(undefined);

  const formattedLastSavedOn = computed(
    () => lastSavedOn.value && getCurrentTime(lastSavedOn.value),
  );

  async function triggerSave(): Promise<void> {
    if (saveIsInProgress.value) return;

    saveIsInProgress.value = true;
    lastSaveError.value = undefined;

    try {
      const success = await store.update();

      if (success) {
        lastSavedOn.value = new Date();
      } else {
        lastSaveError.value = { title: errorMessages.DOCUMENT_UNIT_UPDATE_FAILED.title };
      }

      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (error) {
      lastSaveError.value = { title: "Verbindung fehlgeschlagen" };
    } finally {
      saveIsInProgress.value = false;
    }
  }

  return {
    saveIsInProgress,
    triggerSave,
    lastSaveError,
    formattedLastSavedOn,
  };
}
