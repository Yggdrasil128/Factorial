export interface EntityCloneNameGeneratorService {
  generateName: (baseName: string, entities: { name: string }[]) => string;
}

export function useEntityCloneNameGeneratorService(): EntityCloneNameGeneratorService {

  function isNameInUse(name: string, entities: { name: string }[]): boolean {
    for (const entity of entities) {
      if (entity.name === name) {
        return true;
      }
    }
    return false;
  }

  function generateName(baseName: string, entities: { name: string }[]): string {
    if (!isNameInUse(baseName, entities)) {
      return baseName;
    }

    let n: number = 1;
    const match: RegExpExecArray | null = /(.*) \((\d+)\)$/.exec(baseName);
    if (match) {
      baseName = match[1];
      n = Number(match[2]);
    }

    let name: string;
    do {
      n++;
      name = baseName + ' (' + n + ')';
    } while (isNameInUse(name, entities));

    return name;
  }

  return { generateName };
}