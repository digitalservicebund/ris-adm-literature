export default class StringsUtil {
  public static isEmpty(value: string | undefined): boolean {
    return value == null || value.trim().length == 0
  }
}

/**
 * Splits a string on the first comma and trims both parts.
 *
 * @param {string} input
 * @returns {[string, string]}
 */
export function splitTrimFirstComma(input: string) {
  const commaIndex = input.indexOf(',')

  if (commaIndex === -1) {
    return [input.trim(), '']
  }

  return [input.slice(0, commaIndex).trim(), input.slice(commaIndex + 1).trim()]
}
