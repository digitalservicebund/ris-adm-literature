import { LabelPosition } from '@/components/input/InputField.vue'
import LegalPeriodical from '@/domain/legalPeriodical'
import type { Ref } from 'vue'
import type { ComboboxResult } from '@/domain/comboboxResult.ts'

export enum InputType {
  TEXT = 'text',
  FILE = 'file',
  DROPDOWN = 'dropdown',
  DATE = 'date',
  CHECKBOX = 'checkbox',
  RADIO = 'radio',
  CHIPS = 'chips',
  DATECHIPS = 'datechips',
  NESTED = 'nested',
  COMBOBOX = 'combobox',
  TEXTAREA = 'textarea',
  DATE_TIME = 'date_time',
  YEAR = 'year',
  TIME = 'time',
  UNDEFINED_DATE = 'undefined_date',
}

//BASE
export interface BaseInputAttributes {
  ariaLabel: string
  validationError?: ValidationError
  labelPosition?: LabelPosition
}

export interface BaseInputField {
  name: string
  type: InputType
  label: string
  required?: boolean
  inputAttributes: BaseInputAttributes
}

//TEXT

export interface TextInputAttributes extends BaseInputAttributes {
  placeholder?: string
  readOnly?: boolean
  maxlength?: string
  autofocus?: boolean
}

export interface TextInputField extends BaseInputField {
  type: InputType.TEXT
  inputAttributes: TextInputAttributes
}

//CHIPS
export type ChipsInputModelType = string[]

export interface ChipsInputAttributes extends BaseInputAttributes {
  placeholder?: string
  readOnly?: boolean
}

export interface ChipsInputField extends BaseInputField {
  type: InputType.CHIPS
  inputAttributes: ChipsInputAttributes
}

export interface DateChipsInputField extends BaseInputField {
  type: InputType.DATECHIPS
  inputAttributes: ChipsInputAttributes
}

//NESTED INPUT
export interface NestedInputModelType {
  fields: {
    parent: ModelType
    child: ModelType
  }
}

export interface NestedInputAttributes extends BaseInputAttributes {
  fields: { parent: InputField; child: InputField }
}

export interface NestedInputField extends Omit<BaseInputField, 'name'> {
  name: `nestedInputOf${Capitalize<string>}And${Capitalize<string>}`
  type: InputType.NESTED
  inputAttributes: NestedInputAttributes
}

//DATE
export interface DateAttributes extends BaseInputAttributes {
  isFutureDate?: boolean
}

export interface DateInputField extends BaseInputField {
  placeholder?: string
  type: InputType.DATE
  inputAttributes: DateAttributes
}

export type DateInputModelType = string | undefined

//DROPDOWN

export type DropdownItem = {
  label: string
  value: string
}

export interface DropdownAttributes extends BaseInputAttributes {
  placeholder?: string
  items: DropdownItem[]
}

export interface DropdownInputField extends BaseInputField {
  type: InputType.DROPDOWN
  inputAttributes: DropdownAttributes
}

//COMBOBOX
export type ComboboxInputModelType = LegalPeriodical

export type ComboboxItem = {
  label: string
  value?: ComboboxInputModelType
  labelCssClasses?: string
  additionalInformation?: string
  sideInformation?: string
}

export interface ComboboxAttributes extends BaseInputAttributes {
  itemService: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  placeholder?: string
  manualEntry?: boolean
  noClear?: boolean
}

export interface ComboboxInputField extends BaseInputField {
  type: InputType.COMBOBOX
  inputAttributes: ComboboxAttributes
}

//CHECKBOX
export interface CheckboxInputField extends BaseInputField {
  type: InputType.CHECKBOX
  inputAttributes: BaseInputAttributes
}

//TEXTAREA
export interface TextAreaInputAttributes extends BaseInputAttributes {
  placeholder?: string
  readOnly?: boolean
  autosize?: boolean
  rows?: number
  fieldSize: 'max' | 'big' | 'medium' | 'small'
}

export interface TextAreaInputField extends BaseInputField {
  type: InputType.TEXTAREA
  inputAttributes: TextAreaInputAttributes
}

export type InputField =
  | TextInputField
  | DropdownInputField
  | DateInputField
  | CheckboxInputField
  | ChipsInputField
  | DateChipsInputField
  | NestedInputField
  | ComboboxInputField
  | TextAreaInputField

export type InputAttributes =
  | TextInputAttributes
  | DropdownAttributes
  | ChipsInputAttributes
  | NestedInputAttributes
  | DateAttributes
  | ComboboxAttributes
  | TextAreaInputAttributes

export type ModelType =
  | string
  | DateInputModelType
  | ChipsInputModelType
  | NestedInputModelType
  | ComboboxInputModelType

export type ValidationError = {
  code?: string
  message: string
  instance: string
}
