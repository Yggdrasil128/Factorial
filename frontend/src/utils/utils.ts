import type { EntityWithOrdinal } from '@/types/model/basic';
import type { RuleItem } from 'async-validator/dist-types/interface';

export function ordinalComparator(a: EntityWithOrdinal, b: EntityWithOrdinal) {
  return a.ordinal - b.ordinal;
}

export interface GameEntityNameUniqueValidationSettings {
  store: { getByGameId: (gameId: number) => { id: number, name: string }[] };
  gameId: number;
  ownId: undefined | number;
}

function elFormGameEntityNameUniqueValidatorFunction(rule: GameEntityNameUniqueValidationSettings,
                                                     value: any,
                                                     callback: any): void {
  if (!value) {
    callback();
    return;
  }

  for (const entity of rule.store.getByGameId(rule.gameId)) {
    if (entity.id === rule.ownId) {
      continue;
    }
    if (entity.name === value) {
      callback(new Error());
      return;
    }
  }
  callback();
}

export const elFormGameEntityNameUniqueValidator: RuleItem['validator'] =
  elFormGameEntityNameUniqueValidatorFunction as unknown as RuleItem['validator'];