import { describe, it, expect } from "vitest";
import { splitTrimFirstComma } from "./stringsUtil";

describe("splitTrimFirstComma", () => {
  it.each([
    ["  apple pie ,  vanilla ice cream  ", ["apple pie", "vanilla ice cream"]],
    ["  no comma here  ", ["no comma here", ""]],
    ["foo,bar", ["foo", "bar"]],
    ["foo, bar, baz", ["foo", "bar, baz"]],
    [" , value", ["", "value"]],
    ["value, ", ["value", ""]],
    ["", ["", ""]],
    [",", ["", ""]],
    ["   ", ["", ""]],
    ["   ,   ", ["", ""]],
  ])("splitTrimFirstComma(%j)", (input, expected) => {
    expect(splitTrimFirstComma(input)).toEqual(expected);
  });
});
