import type { EntityWithOrdinal } from '@/types/model/basic';

export function ordinalComparator(a: EntityWithOrdinal, b: EntityWithOrdinal) {
  return a.ordinal - b.ordinal;
}