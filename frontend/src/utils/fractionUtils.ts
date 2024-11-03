import type { Fraction } from '@/types/model/basic';

export function fractionToNumber(fraction: Fraction): number {
  const i = fraction.indexOf('/');
  if (i < 0) {
    return Number(fraction);
  }
  return Number(fraction.substring(0, i)) / Number(fraction.substring(i + 1));
}

export function modifyFraction(fraction: Fraction, integerDelta: number, allowNegative?: boolean): Fraction {
  const i = fraction.indexOf('/');
  if (i < 0) {
    return String(Math.max(Number(fraction) + integerDelta, 0));
  } else {
    let n = Number(fraction.substring(0, i));
    const d = Number(fraction.substring(i + 1));
    n += d * integerDelta;
    if (n <= 0 && !allowNegative) {
      return '0';
    }
    return String(n) + '/' + String(d);
  }
}

export interface FractionValidationSettings {
  allowZero?: boolean;
  allowNegative?: boolean;
}

export function isValidFraction(fraction: Fraction, settings?: FractionValidationSettings): boolean {
  if (fraction.startsWith('-')) {
    if (settings?.allowNegative) {
      fraction = fraction.substring(1);
    } else {
      return false;
    }
  }
  if (!settings?.allowZero && /^0+ ?(\/ ?\d+)?$/.test(fraction)) {
    return false;
  }
  return /^(\d+) ?(\/ ?\d+)?$/.test(fraction);
}

export function elFormFractionValidator(rule: FractionValidationSettings, value: any, callback: any): void {
  if (!isValidFraction(value, rule)) {
    callback(new Error());
  } else {
    callback();
  }
}