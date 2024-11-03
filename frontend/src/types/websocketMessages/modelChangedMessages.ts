import type { Changelist, Factory, ProductionStep, Resource } from '@/types/model/standalone';

export type WebsocketMessage = {
  runtimeId: string;
  messageId: number;
  type: WebsocketMessageType;
}

export type WebsocketMessageType = 'initial'
  | 'factoryUpdated' | 'factoryRemoved'
  | 'changelistUpdated' | 'changelistRemoved'
  | 'productionStepUpdated' | 'productionStepRemoved'
  | 'resourceUpdated' | 'resourceRemoved';

export type InitialMessage = WebsocketMessage & {
  type: 'initial';
}

export function isInitialMessage(message: WebsocketMessage): message is InitialMessage {
  return message.type === 'initial';
}

export type ModelChangedMessage = WebsocketMessage & {
  saveId: number;
}

export function isModelChangedMessage(message: WebsocketMessage): message is ModelChangedMessage {
  return message.type !== 'initial';
}


export type FactoryUpdatedMessage = ModelChangedMessage & {
  type: 'factoryUpdated';
  factory: Factory;
}

export function isFactoryUpdatedMessage(message: WebsocketMessage): message is FactoryUpdatedMessage {
  return message.type === 'factoryUpdated';
}


export type FactoryRemovedMessage = ModelChangedMessage & {
  type: 'factoryRemoved';
  factoryId: number;
}

export function isFactoryRemovedMessage(message: WebsocketMessage): message is FactoryRemovedMessage {
  return message.type === 'factoryRemoved';
}


export type ChangelistUpdatedMessage = ModelChangedMessage & {
  type: 'changelistUpdated';
  changelist: Changelist;
}

export function isChangelistUpdatedMessage(message: WebsocketMessage): message is ChangelistUpdatedMessage {
  return message.type === 'changelistUpdated';
}


export type ChangelistRemovedMessage = ModelChangedMessage & {
  type: 'changelistRemoved';
  changelistId: number;
}

export function isChangelistRemovedMessage(message: WebsocketMessage): message is ChangelistRemovedMessage {
  return message.type === 'changelistRemoved';
}


export type ProductionStepUpdatedMessage = ModelChangedMessage & {
  type: 'productionStepUpdated';
  productionStep: ProductionStep;
}

export function isProductionStepUpdatedMessage(message: WebsocketMessage): message is ProductionStepUpdatedMessage {
  return message.type === 'productionStepUpdated';
}


export type ProductionStepRemovedMessage = ModelChangedMessage & {
  type: 'productionStepRemoved';
  productionStepId: number;
}

export function isProductionStepRemovedMessage(message: WebsocketMessage): message is ProductionStepRemovedMessage {
  return message.type === 'productionStepRemoved';
}


export type ResourceUpdatedMessage = ModelChangedMessage & {
  type: 'resourceUpdated';
  resource: Resource;
}

export function isResourceUpdatedMessage(message: WebsocketMessage): message is ResourceUpdatedMessage {
  return message.type === 'resourceUpdated';
}

export type ResourceRemovedMessage = ModelChangedMessage & {
  type: 'resourceRemoved';
  resourceId: number;
}

export function isResourceRemovedMessage(message: WebsocketMessage): message is ResourceRemovedMessage {
  return message.type === 'resourceRemoved';
}