import { ParsedFraction } from '@/utils/fractionUtils';
import grammar from '@/utils/termParser.grammar.txt?raw';
import peg, { type Parser } from 'pegjs';

// TODO: Is it possible to precompile the parser as part of frontend build?
const parser: Parser = peg.generate(grammar);
const doErrorLog: boolean = false;

export function parseTerm(s: string): ParsedFraction | undefined {
  s = s.replaceAll(' ', '');
  if (!s) return undefined;

  let term: Term;
  try {
    term = parser.parse(s) as Term;
  } catch (e) {
    if (doErrorLog) console.warn('Unable to parse term \'' + s + '\'', e);
    return undefined;
  }

  let fraction: ParsedFraction;
  try {
    fraction = evaluateTerm(term);
  } catch (e) {
    if (doErrorLog) console.warn('Unable to evaluate term \'' + s + '\'', e);
    return undefined;
  }

  return fraction;
}

function evaluateTerm(term: Term): ParsedFraction {
  let fraction: ParsedFraction = evaluateAdditive(term.head);
  for (const additiveTail of term.tail) {
    const operand: ParsedFraction = evaluateAdditive(additiveTail.operand);
    if (additiveTail.operation === '+') {
      fraction = fraction.add(operand);
    } else if (additiveTail.operation === '-') {
      fraction = fraction.subtract(operand);
    } else {
      throw Error('Unknown operation: ' + additiveTail.operation);
    }
  }
  return fraction;
}

function evaluateAdditive(additive: Additive): ParsedFraction {
  let fraction: ParsedFraction = evaluateMultiplicative(additive.head);
  for (const multiplicativeTail of additive.tail) {
    const operand: ParsedFraction = evaluateMultiplicative(multiplicativeTail.operand);
    if (multiplicativeTail.operation === '*') {
      fraction = fraction.multiply(operand);
    } else if (multiplicativeTail.operation === '/') {
      fraction = fraction.divide(operand);
    } else {
      throw Error('Unknown operation: ' + multiplicativeTail.operation);
    }
  }
  return fraction;
}

function evaluateMultiplicative(multiplicative: Multiplicative): ParsedFraction {
  if (isMultiplicativeTerm(multiplicative)) {
    return evaluateTerm(multiplicative.term);
  }
  if (isMultiplicativeNumber(multiplicative)) {
    return evaluateNumber(multiplicative.number);
  }
  throw Error('Unknown multiplicative type: ' + JSON.stringify(multiplicative));
}

function evaluateNumber(number: ParsedNumber): ParsedFraction {
  if (!number.digitsBeforeDecimal && !number.digitsAfterDecimal) {
    throw Error('Empty number');
  }

  let fraction: ParsedFraction = ParsedFraction.of(number.digitsBeforeDecimal || '0');

  if (number.digitsAfterDecimal) {
    const length: number = number.digitsAfterDecimal.length;
    const numerator: bigint = BigInt(number.digitsAfterDecimal);
    const denominator: bigint = BigInt(10) ** BigInt(length);
    fraction = fraction.add(new ParsedFraction(numerator, denominator));
  }

  if (number.isNegative) {
    fraction = fraction.negative();
  }

  return fraction;
}

// Type definitions

type Term = {
  head: Additive;
  tail: AdditiveTail[];
}

type AdditiveTail = {
  operation: '+' | '-';
  operand: Additive;
}

type Additive = {
  head: Multiplicative;
  tail: MultiplicativeTail[];
}

type MultiplicativeTail = {
  operation: '*' | '/';
  operand: Multiplicative;
}

type Multiplicative = MultiplicativeNumber | MultiplicativeTerm;

type MultiplicativeNumber = {
  number: ParsedNumber;
}

function isMultiplicativeNumber(multiplicative: Multiplicative): multiplicative is MultiplicativeNumber {
  return 'number' in multiplicative;
}

type MultiplicativeTerm = {
  term: Term;
}

function isMultiplicativeTerm(multiplicative: Multiplicative): multiplicative is MultiplicativeTerm {
  return 'term' in multiplicative;
}

type ParsedNumber = {
  isNegative: boolean;
  digitsBeforeDecimal: string;
  digitsAfterDecimal: string;
}