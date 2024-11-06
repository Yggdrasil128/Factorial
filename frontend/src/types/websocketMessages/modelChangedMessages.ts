import type {
  Changelist,
  Factory,
  Game,
  Icon,
  Item,
  Machine,
  ProductionStep,
  Recipe,
  RecipeModifier,
  Resource,
  Save
} from '@/types/model/standalone';
import type { EntityWithOrdinal } from '@/types/model/basic';

export type WebsocketMessage = {
  runtimeId: string;
  messageId: number;
  type: WebsocketMessageType;
}

export type WebsocketMessageType = 'initial'
  | 'saveUpdated' | 'saveRemoved' | 'savesReordered'
  | 'factoryUpdated' | 'factoryRemoved' | 'factoriesReordered'
  | 'changelistUpdated' | 'changelistRemoved' | 'changelistsReordered'
  | 'productionStepUpdated' | 'productionStepRemoved'
  | 'resourceUpdated' | 'resourceRemoved' | 'resourcesReordered'

  | 'gameUpdated' | 'gameRemoved' | 'gamesReordered'
  | 'iconUpdated' | 'iconRemoved'
  | 'itemUpdated' | 'itemRemoved'
  | 'recipeUpdated' | 'recipeRemoved'
  | 'recipeModifierUpdated' | 'recipeModifierRemoved'
  | 'machineUpdated' | 'machineRemoved';

export type InitialMessage = WebsocketMessage & {
  type: 'initial';
}

export function isInitialMessage(message: WebsocketMessage): message is InitialMessage {
  return message.type === 'initial';
}

export type SaveRelatedModelChangedMessage = WebsocketMessage & {
  saveId: number;
}

export function isSaveRelatedModelChangedMessage(message: WebsocketMessage): message is SaveRelatedModelChangedMessage {
  return 'saveId' in message;
}

export type GameRelatedModelChangedMessage = WebsocketMessage & {
  gameId: number;
}

export function isGameRelatedModelChangedMessage(message: WebsocketMessage): message is GameRelatedModelChangedMessage {
  return 'gameId' in message;
}

// --- SAVE RELATED MESSAGES ---

// Save

export type SaveUpdatedMessage = SaveRelatedModelChangedMessage & {
  type: 'saveUpdated';
  save: Save;
}

export function isSaveUpdatedMessage(message: WebsocketMessage): message is SaveUpdatedMessage {
  return message.type === 'saveUpdated';
}

export type SaveRemovedMessage = SaveRelatedModelChangedMessage & {
  type: 'saveRemoved';
  // saveId: number; already defined in SaveRelatedModelChangedMessage
}

export function isSaveRemovedMessage(message: WebsocketMessage): message is SaveRemovedMessage {
  return message.type === 'saveRemoved';
}

export type SavesReorderedMessage = WebsocketMessage & {
  type: 'savesReordered';
  order: EntityWithOrdinal[];
}

export function isSavesReorderedMessage(message: WebsocketMessage): message is SavesReorderedMessage {
  return message.type === 'savesReordered';
}

// Factory

export type FactoryUpdatedMessage = SaveRelatedModelChangedMessage & {
  type: 'factoryUpdated';
  factory: Factory;
}

export function isFactoryUpdatedMessage(message: WebsocketMessage): message is FactoryUpdatedMessage {
  return message.type === 'factoryUpdated';
}

export type FactoryRemovedMessage = SaveRelatedModelChangedMessage & {
  type: 'factoryRemoved';
  factoryId: number;
}

export function isFactoryRemovedMessage(message: WebsocketMessage): message is FactoryRemovedMessage {
  return message.type === 'factoryRemoved';
}

export type FactoriesReorderedMessage = WebsocketMessage & {
  type: 'factoriesReordered';
  order: EntityWithOrdinal[];
}

export function isFactoriesReorderedMessage(message: WebsocketMessage): message is FactoriesReorderedMessage {
  return message.type === 'factoriesReordered';
}

// Changelist

export type ChangelistUpdatedMessage = SaveRelatedModelChangedMessage & {
  type: 'changelistUpdated';
  changelist: Changelist;
}

export function isChangelistUpdatedMessage(message: WebsocketMessage): message is ChangelistUpdatedMessage {
  return message.type === 'changelistUpdated';
}

export type ChangelistRemovedMessage = SaveRelatedModelChangedMessage & {
  type: 'changelistRemoved';
  changelistId: number;
}

export function isChangelistRemovedMessage(message: WebsocketMessage): message is ChangelistRemovedMessage {
  return message.type === 'changelistRemoved';
}

export type ChangelistsReorderedMessage = WebsocketMessage & {
  type: 'changelistsReordered';
  order: EntityWithOrdinal[];
}

export function isChangelistsReorderedMessage(message: WebsocketMessage): message is ChangelistsReorderedMessage {
  return message.type === 'changelistsReordered';
}

// Production Step

export type ProductionStepUpdatedMessage = SaveRelatedModelChangedMessage & {
  type: 'productionStepUpdated';
  productionStep: ProductionStep;
}

export function isProductionStepUpdatedMessage(message: WebsocketMessage): message is ProductionStepUpdatedMessage {
  return message.type === 'productionStepUpdated';
}

export type ProductionStepRemovedMessage = SaveRelatedModelChangedMessage & {
  type: 'productionStepRemoved';
  productionStepId: number;
}

export function isProductionStepRemovedMessage(message: WebsocketMessage): message is ProductionStepRemovedMessage {
  return message.type === 'productionStepRemoved';
}

// Resource

export type ResourceUpdatedMessage = SaveRelatedModelChangedMessage & {
  type: 'resourceUpdated';
  resource: Resource;
}

export function isResourceUpdatedMessage(message: WebsocketMessage): message is ResourceUpdatedMessage {
  return message.type === 'resourceUpdated';
}

export type ResourceRemovedMessage = SaveRelatedModelChangedMessage & {
  type: 'resourceRemoved';
  resourceId: number;
}

export function isResourceRemovedMessage(message: WebsocketMessage): message is ResourceRemovedMessage {
  return message.type === 'resourceRemoved';
}

export type ResourcesReorderedMessage = WebsocketMessage & {
  type: 'resourcesReordered';
  order: EntityWithOrdinal[];
}

export function isResourcesReorderedMessage(message: WebsocketMessage): message is ResourcesReorderedMessage {
  return message.type === 'resourcesReordered';
}

// --- GAME RELATED MESSAGES ---

// Game

export type GameUpdatedMessage = GameRelatedModelChangedMessage & {
  type: 'gameUpdated';
  game: Game;
}

export function isGameUpdatedMessage(message: WebsocketMessage): message is GameUpdatedMessage {
  return message.type === 'gameUpdated';
}

export type GameRemovedMessage = GameRelatedModelChangedMessage & {
  type: 'gameRemoved';
  // gameId: number; already defined in SaveRelatedModelChangedMessage
}

export function isGameRemovedMessage(message: WebsocketMessage): message is GameRemovedMessage {
  return message.type === 'gameRemoved';
}

export type GamesReorderedMessage = WebsocketMessage & {
  type: 'gamesReordered';
  order: EntityWithOrdinal[];
}

export function isGamesReorderedMessage(message: WebsocketMessage): message is GamesReorderedMessage {
  return message.type === 'gamesReordered';
}

// Icon

export type IconUpdatedMessage = GameRelatedModelChangedMessage & {
  type: 'iconUpdated';
  icon: Icon;
}

export function isIconUpdatedMessage(message: WebsocketMessage): message is IconUpdatedMessage {
  return message.type === 'iconUpdated';
}

export type IconRemovedMessage = GameRelatedModelChangedMessage & {
  type: 'iconRemoved';
  iconId: number;
}

export function isIconRemovedMessage(message: WebsocketMessage): message is IconRemovedMessage {
  return message.type === 'iconRemoved';
}

// Item

export type ItemUpdatedMessage = GameRelatedModelChangedMessage & {
  type: 'itemUpdated';
  item: Item;
}

export function isItemUpdatedMessage(message: WebsocketMessage): message is ItemUpdatedMessage {
  return message.type === 'itemUpdated';
}

export type ItemRemovedMessage = GameRelatedModelChangedMessage & {
  type: 'itemRemoved';
  itemId: number;
}

export function isItemRemovedMessage(message: WebsocketMessage): message is ItemRemovedMessage {
  return message.type === 'itemRemoved';
}

// Recipe

export type RecipeUpdatedMessage = GameRelatedModelChangedMessage & {
  type: 'recipeUpdated';
  recipe: Recipe;
}

export function isRecipeUpdatedMessage(message: WebsocketMessage): message is RecipeUpdatedMessage {
  return message.type === 'recipeUpdated';
}

export type RecipeRemovedMessage = GameRelatedModelChangedMessage & {
  type: 'recipeRemoved';
  recipeId: number;
}

export function isRecipeRemovedMessage(message: WebsocketMessage): message is RecipeRemovedMessage {
  return message.type === 'recipeRemoved';
}

// Recipe modifier

export type RecipeModifierUpdatedMessage = GameRelatedModelChangedMessage & {
  type: 'recipeModifierUpdated';
  recipeModifier: RecipeModifier;
}

export function isRecipeModifierUpdatedMessage(message: WebsocketMessage): message is RecipeModifierUpdatedMessage {
  return message.type === 'recipeModifierUpdated';
}

export type RecipeModifierRemovedMessage = GameRelatedModelChangedMessage & {
  type: 'recipeModifierRemoved';
  recipeModifierId: number;
}

export function isRecipeModifierRemovedMessage(message: WebsocketMessage): message is RecipeModifierRemovedMessage {
  return message.type === 'recipeModifierRemoved';
}

// Machine

export type MachineUpdatedMessage = GameRelatedModelChangedMessage & {
  type: 'machineUpdated';
  machine: Machine;
}

export function isMachineUpdatedMessage(message: WebsocketMessage): message is MachineUpdatedMessage {
  return message.type === 'machineUpdated';
}

export type MachineRemovedMessage = GameRelatedModelChangedMessage & {
  type: 'machineRemoved';
  machineId: number;
}

export function isMachineRemovedMessage(message: WebsocketMessage): message is MachineRemovedMessage {
  return message.type === 'machineRemoved';
}
