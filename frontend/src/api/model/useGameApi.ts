import type { Game } from '@/types/model/standalone';
import type { EntityWithOrdinal } from '@/types/model/basic';
import { AbstractBulkCrudEntityApi } from '@/api/model/abstractBulkCrudEntityApi';
import type { GameSummary } from '@/types/model/summary';

export class GameApi extends AbstractBulkCrudEntityApi<Game> {
  constructor() {
    super('Game');
  }

  protected callPost(games: Partial<Game>[]): Promise<void> {
    return this.api.post('/api/games', games);
  }

  protected callPatch(games: Partial<Game>[]): Promise<void> {
    return this.api.patch('/api/games', games);
  }

  protected callDelete(gameIds: number[]): Promise<void> {
    return this.api.delete('/api/games', { gameIds: gameIds.join(',') });
  }


  public retrieveAll(): Promise<Game[]> {
    return this.api.get('/api/games');
  }

  public reorder(input: EntityWithOrdinal[]): Promise<void> {
    return this.api.patch('/api/games/order', input);
  }

  public clone(gameId: number, newName: string): Promise<void> {
    return this.api.post('api/migration/game/clone', undefined, { gameId, newName });
  }

  public import(gameSummary: GameSummary): Promise<void> {
    return this.api.post('api/migration/game', gameSummary);
  }

  public export(gameId: number): Promise<GameSummary> {
    return this.api.get('api/migration/game', { gameId });
  }
}

let instance: GameApi | undefined = undefined;

export function useGameApi(): GameApi {
  if (!instance) instance = new GameApi();
  return instance;
}