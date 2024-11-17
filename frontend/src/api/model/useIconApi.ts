import type { Icon } from '@/types/model/standalone';

import { AbstractBulkCrudEntityApi } from '@/api/model/abstractBulkCrudEntityApi';

export class IconApi extends AbstractBulkCrudEntityApi<Icon> {
  constructor() {
    super('Icon');
  }

  protected callPost(icons: Partial<Icon>[]): Promise<void> {
    return this.api.post('/api/game/icons', icons, { gameId: icons[0].gameId });
  }

  protected callPatch(icons: Partial<Icon>[]): Promise<void> {
    return this.api.patch('/api/icons', icons);
  }

  protected callDelete(iconIds: number[]): Promise<void> {
    return this.api.delete('/api/icons', { iconIds: iconIds.join(',') });
  }

  public retrieveAllByGameId(gameId: number): Promise<Icon[]> {
    return this.api.get('api/game/icons', { gameId: gameId });
  }
}

let instance: IconApi | undefined = undefined;

export function useIconApi(): IconApi {
  if (!instance) instance = new IconApi();
  return instance;
}