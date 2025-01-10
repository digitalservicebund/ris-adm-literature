import type { ComputedRef, Ref } from 'vue'
import type { Fn } from '@vueuse/core'

export interface ComboboxResult<T> {
  /**
   * The fetch response body on success, may either be JSON or text
   */
  data: Ref<T | null>
  execute: (throwOnFailed?: boolean) => Promise<never>
  /**
   * Indicates if the fetch request is able to be aborted
   */
  canAbort: ComputedRef<boolean>
  /**
   * Abort the fetch request
   */
  abort: Fn
}
