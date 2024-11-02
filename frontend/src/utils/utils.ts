import type { Fraction } from '@/types/model/basic';

export function fractionToNumber(fraction: Fraction): number {
  const i = fraction.indexOf('/');
  if (i < 0) {
    return Number(fraction);
  }
  return Number(fraction.substring(0, i)) / Number(fraction.substring(i + 1));
}