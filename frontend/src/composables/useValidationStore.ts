import { ref } from 'vue'
import type { ValidationError } from '@/components/input/types'

type ValidationStore<T> = {
  getAll: () => ValidationError[]
  getByField: (field: T) => ValidationError | undefined
  getByMessage: (message: string) => ValidationError[]
  add: (message: string, instance: T) => void
  remove: (field: T) => void
  reset: () => void
  isValid: () => boolean
}

/**
 * A composable for managing validation errors in a reactive store.
 *
 * This utility allows you to add, retrieve, remove, and reset validation errors
 * associated with form fields or other identifiable instances.
 *
 * @template T - A string literal type representing the valid field identifiers.
 *
 * Exposed methods:
 * - `getAll`: Returns all current validation errors.
 * - `add`: Adds a new validation error for a specific instance, replacing any existing one for that instance.
 * - `getByField`: Retrieves the validation error for a specific field/instance.
 * - `getByMessage`: Returns all validation errors matching a specific message.
 * - `remove`: Removes the validation error for a given field.
 * - `isValid`: Returns true if there are no validation errors.
 * - `reset`: Clears all validation errors.
 */
export function useValidationStore<T extends string>(): ValidationStore<T> {
  const validationErrors = ref<ValidationError[]>([])

  function getAll() {
    return validationErrors.value
  }

  function add(message: string, instance: T) {
    remove(instance)
    validationErrors.value?.push({ message, instance })
  }

  function getByField(field: T): ValidationError | undefined {
    return validationErrors.value.find((error) => error.instance == field)
  }

  function getByMessage(message: string): ValidationError[] {
    return validationErrors.value.filter((error) => error.message == message)
  }

  function remove(field: T) {
    if (getByField(field))
      validationErrors.value.splice(
        validationErrors.value.findIndex((error) => error.instance == field),
        1,
      )
  }

  function isValid(): boolean {
    return getAll().length == 0
  }

  function reset() {
    validationErrors.value = []
  }

  return {
    getAll,
    getByField,
    getByMessage,
    add,
    remove,
    reset,
    isValid,
  }
}
