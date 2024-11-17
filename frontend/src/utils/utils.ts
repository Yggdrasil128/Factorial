import type { EntityWithOrdinal } from '@/types/model/basic';
import type { RuleItem } from 'async-validator/dist-types/interface';

export function ordinalComparator(a: EntityWithOrdinal, b: EntityWithOrdinal) {
  return a.ordinal - b.ordinal;
}


export interface EntityNameUniqueValidationSettings {
  entities: { id: number, name: string }[];
  ownId: undefined | number;
}

function elFormEntityNameUniqueValidatorFunction(rule: EntityNameUniqueValidationSettings,
                                                 value: any,
                                                 callback: any): void {
  if (!value) {
    callback();
    return;
  }

  console.log(rule);

  for (const entity of rule.entities) {
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

export const elFormEntityNameUniqueValidator: RuleItem['validator'] =
  elFormEntityNameUniqueValidatorFunction as unknown as RuleItem['validator'];