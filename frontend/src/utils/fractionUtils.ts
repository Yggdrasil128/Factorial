import type { Fraction } from '@/types/model/basic';

// noinspection JSUnusedGlobalSymbols
export class ParsedFraction {
  public static readonly ZERO: ParsedFraction = new ParsedFraction(0n);
  public static readonly ONE: ParsedFraction = new ParsedFraction(1n);

  private numerator: bigint;
  private denominator: bigint;

  public static of(fraction: Fraction): ParsedFraction {
    const i = fraction.indexOf('/');
    if (i < 0) {
      return new ParsedFraction(BigInt(fraction.trim()));
    }
    return new ParsedFraction(
      BigInt(fraction.substring(0, i).trim()),
      BigInt(fraction.substring(i + 1).trim()),
    );
  }

  constructor(numerator: bigint, denominator?: bigint) {
    this.numerator = numerator;
    this.denominator = denominator ?? 1n;
    this.normalize();
  }

  private normalize(): void {
    if (this.denominator === 0n) {
      throw new Error('Division by zero');
    }
    if (this.denominator < 0n) {
      this.numerator *= -1n;
      this.denominator *= -1n;
    }
    const n: bigint = gcd(this.numerator, this.denominator);
    if (n === 1n) {
      return;
    }
    this.numerator /= n;
    this.denominator /= n;
  }

  toFraction(): Fraction {
    if (this.denominator === 1n) {
      return String(this.numerator);
    } else {
      return String(this.numerator) + '/' + String(this.denominator);
    }
  }

  toNumber(): number {
    return Number(this.numerator) / Number(this.denominator);
  }

  add(that: ParsedFraction): ParsedFraction {
    return new ParsedFraction(
      this.numerator * that.denominator + that.numerator * this.denominator,
      this.denominator * that.denominator,
    );
  }

  negative(): ParsedFraction {
    return new ParsedFraction(-this.numerator, this.denominator);
  }

  subtract(that: ParsedFraction): ParsedFraction {
    return this.add(that.negative());
  }

  multiply(that: ParsedFraction): ParsedFraction {
    return new ParsedFraction(
      this.numerator * that.numerator,
      this.denominator * that.denominator,
    );
  }

  inverse(): ParsedFraction {
    return new ParsedFraction(this.denominator, this.numerator);
  }

  divide(that: ParsedFraction): ParsedFraction {
    return this.multiply(that.inverse());
  }
}

export function gcd(a: bigint, b: bigint): bigint {
  // Euclid's algorithm
  if (b === 0n) {
    return a;
  }
  return gcd(b, a % b);
}

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