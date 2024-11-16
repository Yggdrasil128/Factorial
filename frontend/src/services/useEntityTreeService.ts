import type { EntityWithCategory, TreeNode } from '@/utils/treeUtils';
import { convertToTreeByCategory } from '@/utils/treeUtils';
import type { Component, ComputedRef, Ref } from 'vue';
import { computed, nextTick, reactive, ref, watch } from 'vue';
import { until } from '@vueuse/core';
import _ from 'lodash';
import Node from 'element-plus/es/components/tree/src/model/node';
import { Close, Link, Menu, UploadFilled } from '@element-plus/icons-vue';
import type { Game, Icon } from '@/types/model/standalone';
import { ElMessage } from 'element-plus';
import { useIconStore } from '@/stores/model/iconStore';
import { useIconApi } from '@/api/model/useIconApi';
import { type EntityUsages, useEntityUsagesService } from '@/services/useEntityUsagesService';
import type { NodeDropType } from 'element-plus/es/components/tree/src/tree.type';

import { AbstractBulkCrudEntityApi } from '@/api/model/abstractBulkCrudEntityApi';

export type EntityTreeServiceState<T extends EntityWithCategory> = {
  entities: ComputedRef<T[]>;
  tree: Ref<TreeNode[]>;
  treeLoading: Ref<boolean>;
  categories: Ref<string[][]>;
  expandedKeys: Set<string>;
  expandedKeysList: ComputedRef<string[]>;

  editingEntityId: Ref<number | undefined>;
  editingEntityModel: Ref<Partial<T>>;
  editingEntityOriginalModel: Ref<Partial<T>>;
  editingEntityIconDataBase64: Ref<string>;
  editingEntityDisplayPath: ComputedRef<string>;
  selectedIconOption: Ref<IconOptionValue>;

  editingFolderPath: Ref<string[] | undefined>;
  editingFolderModel: Ref<FolderModel>;
  editingFolderOriginalModel: Ref<FolderModel>;

  autoDeleteIcons: Ref<boolean>;
  editingHasChanges: ComputedRef<boolean>;
  isSaving: Ref<boolean>;
}

export interface EntityTreeService<T extends EntityWithCategory> {
  state: EntityTreeServiceState<T>;

  getButtonTooltip: (msg: string) => string;

  onTreeNodeExpanded: (node: TreeNode) => void;
  onTreeNodeCollapsed: (node: TreeNode) => void;
  onDragAndDrop: (sourceNode: Node, targetNode: Node, dropType: NodeDropType) => Promise<void>;

  createNewEntityAtRoot: () => void;
  createNewEntityAtNode: (node: Node) => void;
  editEntity: (id: number) => void;
  deleteEntity: (id: number) => void;
  checkDeleteEntity: (id: number, event: PointerEvent) => void;

  createNewFolderAtRoot: () => void;
  createNewFolderAtNode: (node: Node) => void;
  editFolder: (node: Node) => void;
  deleteFolder: (node: Node) => void;

  save: () => Promise<void>;
  saveFolder: () => Promise<void>;
  cancel: () => void;
}

export interface FolderModel {
  name: string;
}

export type EntityType = 'Item' | 'Machine' | 'Recipe' | 'Recipe modifier' | 'Icon';

const waitUntilDoneTimeoutMillis: number = 5000;

export function useEntityTreeService<T extends EntityWithCategory>(
  game: ComputedRef<Game>,
  entities: ComputedRef<T[]>,
  entityType: EntityType,
  getNewEntityModel: () => Partial<T>,
  getExistingEntityModel: (id: number) => Partial<T>,
  validateForm: () => Promise<boolean>,
  validateFolderForm: () => Promise<boolean>,
  findEntityUsages: (entityId: number) => EntityUsages,
  entityApi: AbstractBulkCrudEntityApi<T>,
): EntityTreeService<T> {
  const iconApi = useIconApi();
  const iconStore = useIconStore();
  const entityUsageService = useEntityUsagesService();

  const categories: Ref<string[][]> = ref([]);

  const treeUpdatesPaused: Ref<boolean> = ref(false);
  const treeLoading: Ref<boolean> = ref(false);
  const computedTree: ComputedRef<TreeNode[]> = computed(() =>
    convertToTreeByCategory(entities.value, entityType === 'Icon', categories.value),
  );

  function updateTree(): void {
    tree.value = _.cloneDeep(computedTree.value);
  }

  const tree: Ref<TreeNode[]> = ref([]);

  watch(computedTree, () => {
    if (!treeUpdatesPaused.value) {
      updateTree();
    }
  }, { deep: true, immediate: true });

  function addCategory(categoryToAdd: string[]): void {
    let found: boolean = false;
    for (const category of categories.value) {
      if (_.isEqual(category, categoryToAdd)) {
        found = true;
        break;
      }
    }
    if (!found) {
      if (categoryToAdd.length > 1) {
        const parentCategory: string[] = categoryToAdd.slice(0, -1);
        addCategory(parentCategory);
      }
      categories.value.push(categoryToAdd);
    }
  }

  function categoryStartsWith(category: string[], startsWith: string[]): boolean {
    if (category.length < startsWith.length) {
      return false;
    }
    for (let i = 0; i < startsWith.length; i++) {
      if (startsWith[i] !== category[i]) {
        return false;
      }
    }
    return true;
  }

  function removeCategory(categoryToRemove: string[]): void {
    for (let i = categories.value.length - 1; i >= 0; i--) {
      if (categoryStartsWith(categories.value[i], categoryToRemove)) {
        categories.value.splice(i, 1);
      }
    }
  }

  function addCategoriesFromTree(): void {
    for (const entity of entities.value) {
      addCategory(entity.category);
    }
  }

  watch(
    entities,
    () => addCategoriesFromTree(),
    { deep: true, immediate: true },
  );

  function getButtonTooltip(msg: string): string {
    if (editingHasChanges.value) {
      return 'You have unsaved changes. Please save or cancel your changes first.';
    }
    return msg;
  }

  const expandedKeys: Set<string> = reactive(new Set());
  const expandedKeysList: ComputedRef<string[]> = computed(() => [...expandedKeys.values()]);

  function onTreeNodeExpanded(node: TreeNode): void {
    expandedKeys.add(node.key);
  }

  function onTreeNodeCollapsed(node: TreeNode): void {
    const keys: string[] = [...expandedKeys.values()];
    for (const key of keys) {
      if (key.startsWith(node.key)) {
        expandedKeys.delete(key);
      }
    }
  }

  const editingEntityId: Ref<number | undefined> = ref();
  const editingEntityModel: Ref<Partial<T>> = ref({});
  const editingEntityOriginalModel: Ref<Partial<T>> = ref({});

  const editingFolderPath: Ref<string[] | undefined> = ref();
  const editingFolderModel: Ref<FolderModel> = ref({ name: '' });
  const editingFolderOriginalModel: Ref<FolderModel> = ref({ name: '' });

  const autoDeleteIcons: Ref<boolean> = ref(true);

  const editingHasChanges = computed(() =>
    !_.isEqual(editingEntityModel.value, editingEntityOriginalModel.value)
    || editingEntityIconDataBase64.value !== ''
    || editingFolderModel.value.name !== editingFolderOriginalModel.value.name,
  );

  const editingEntityDisplayPath: ComputedRef<string> = computed(() => {
    if (editingFolderPath.value !== undefined) {
      if (editingFolderPath.value.length === 0) {
        return '/';
      }
      return '/ ' + _.join(editingFolderPath.value, ' / ') + ' /';
    }
    if (!editingEntityModel.value.category || editingEntityModel.value.category.length === 0) {
      return '/';
    }
    return '/ ' + _.join(editingEntityModel.value.category, ' / ') + ' /';
  });

  const selectedIconOption: Ref<IconOptionValue> = ref('none');
  const editingEntityIconDataBase64: Ref<string> = ref('');

  watch(selectedIconOption, () => {
    if (selectedIconOption.value !== 'select') {
      editingEntityModel.value.iconId = 0;
    }
    if (selectedIconOption.value !== 'new') {
      editingEntityIconDataBase64.value = '';
    }
  });

  function getNodePath(node: Node): string[] {
    const path: string[] = [];
    const level = node.level;
    for (let i = 0; i < level; i++) {
      path.unshift(node.data.label);
      node = node.parent;
    }
    return path;
  }

  function focusNameInputField(): void {
    void nextTick(() => {
      const input: HTMLInputElement | null = document.querySelector('#formNameInput');
      input?.focus();
    });
  }

  function createNewEntityAtRoot(): void {
    createNewEntity([]);
  }

  function createNewEntityAtNode(node: Node): void {
    createNewEntity(getNodePath(node));
  }

  function createNewEntity(path: string[]) {
    resetForms();

    const entity: Partial<T> = getNewEntityModel();
    entity.category = path;

    editingEntityId.value = 0;
    editingEntityModel.value = entity;
    editingEntityOriginalModel.value = _.clone(entity);

    editingEntityIconDataBase64.value = '';

    focusNameInputField();
  }

  function editEntity(id: number) {
    resetForms();

    const entity: Partial<T> = getExistingEntityModel(id);

    editingEntityId.value = id;
    editingEntityModel.value = entity;
    editingEntityOriginalModel.value = _.clone(entity);

    selectedIconOption.value = entity.iconId ? 'select' : 'none';
    editingEntityIconDataBase64.value = '';

    focusNameInputField();
  }

  function checkDeleteEntity(id: number, event: PointerEvent): void {
    const entityUsages: EntityUsages = findEntityUsages(id);
    if (entityUsages.hasAnyUsages()) {
      const entity: T | undefined = entities.value.filter(e => e.id == id)[0];
      entityUsages.showMessageBox(
        'Cannot delete ' + entityType.toLowerCase() + ' \'' + (entity?.name ?? '') + '\'.',
        'The ' + entityType.toLowerCase() + ' is still being used in the following places:',
      );
      event.stopPropagation();
    }
  }

  async function deleteEntity(id: number): Promise<void> {
    const entity: ComputedRef<T | undefined> = computed(() =>
      entities.value.filter(e => e.id == id)[0],
    );

    const entityUsages: EntityUsages = findEntityUsages(id);
    if (entityUsages.hasAnyUsages()) {
      entityUsages.showMessageBox(
        'Cannot delete ' + entityType.toLowerCase() + ' \'' + (entity.value?.name ?? '') + '\'.',
        'The ' + entityType.toLowerCase() + ' is still being used in the following places:',
      );
      return;
    }

    const iconId: number = entity.value?.iconId ?? 0;

    await entityApi.delete(id);

    if (iconId && autoDeleteIcons.value) {
      await until(entity).toBeUndefined({ timeout: waitUntilDoneTimeoutMillis });

      if (!entityUsageService.findIconUsages(iconId).hasAnyUsages()) {
        await iconApi.delete(iconId);
      }
    }

    resetForms();
  }

  const isSaving: Ref<boolean> = ref(false);

  async function save(): Promise<void> {
    editingEntityModel.value.name = editingEntityModel.value.name?.trim();
    if (entityType !== 'Icon') {
      const editingEntityModelDescription: { description?: string; } = editingEntityModel.value as {
        description?: string;
      };
      editingEntityModelDescription.description = editingEntityModelDescription.description?.trim();
    }

    if (!await validateForm()) {
      return;
    }

    isSaving.value = true;
    try {
      const entity: Partial<T> = editingEntityModel.value;

      if (entityType === 'Icon') {
        const entityAsIcon: Partial<Icon> = entity as Partial<Icon>;

        if (editingEntityIconDataBase64.value) {
          const { mimeType, imageData } = parseImageData(editingEntityIconDataBase64.value);
          if (!imageData) {
            ElMessage.error({
              message: 'Unable to upload icon.',
            });
            isSaving.value = false;
            return;
          }

          entityAsIcon.mimeType = mimeType;
          entityAsIcon.imageData = imageData;
        }
      } else {
        if (selectedIconOption.value === 'new' && editingEntityIconDataBase64.value) {
          // create new icon first

          const { mimeType, imageData } = parseImageData(editingEntityIconDataBase64.value);
          if (!imageData) {
            ElMessage.error({
              message: 'Unable to upload icon.',
            });
            isSaving.value = false;
            return;
          }

          const icon: Partial<Icon> = {
            name: entity.name,
            gameId: game.value.id,
            category: [entityType + 's', ...(entity.category ?? [])],
            mimeType: mimeType,
            imageData: imageData,
          };

          await iconApi.create(icon);

          const savedIcon: ComputedRef<Icon | undefined> = computed(() =>
            iconStore.getByGameId(game.value.id).filter(icon => icon.name === entity.name)[0],
          );

          await until(savedIcon).not.toBeUndefined({ timeout: waitUntilDoneTimeoutMillis });

          if (!savedIcon.value) {
            ElMessage.error({
              message: 'Icon could not be saved.',
            });
            isSaving.value = false;
            return;
          }

          entity.iconId = savedIcon.value.id;
        }
      }

      if (editingEntityId.value === 0) {
        await entityApi.create(entity);
      } else {
        await entityApi.edit(entity);
      }

    } catch {
      isSaving.value = false;
      return;
    }

    isSaving.value = false;
    resetForms();
  }

  function parseImageData(imageData: string): { mimeType: string, imageData: string } {
    const regexResult = /data:([^;]+);base64,(.*)/.exec(imageData);
    if (!regexResult) {
      return { mimeType: '', imageData: '' };
    }
    return { mimeType: regexResult[1], imageData: regexResult[2] };
  }

  function resetForms(): void {
    editingEntityId.value = undefined;
    editingEntityModel.value = {};
    editingEntityOriginalModel.value = {};
    editingEntityIconDataBase64.value = '';

    editingFolderPath.value = undefined;
    editingFolderModel.value = { name: '' };
    editingFolderOriginalModel.value = { name: '' };
  }

  function createNewFolderAtRoot(): void {
    createNewFolder([]);
  }

  function createNewFolderAtNode(node: Node): void {
    createNewFolder(getNodePath(node));
  }

  function createNewFolder(parentPath: string[]): void {
    resetForms();

    editingFolderPath.value = parentPath;
    editingFolderModel.value = { name: '' };
    editingFolderOriginalModel.value = { name: '' };

    focusNameInputField();
  }

  function editFolder(node: Node): void {
    resetForms();

    const path: string[] = getNodePath(node);
    const name: string = path.pop()!;

    editingFolderPath.value = path;
    editingFolderModel.value = { name };
    editingFolderOriginalModel.value = { name };

    focusNameInputField();
  }

  async function deleteFolder(node: Node): Promise<void> {
    const path: string[] = getNodePath(node);

    const entitiesToDelete: T[] = entities.value.filter(
      entity => categoryStartsWith(entity.category, path),
    );

    for (const entity of entitiesToDelete) {
      const entityUsages: EntityUsages = findEntityUsages(entity.id);
      if (entityUsages.hasAnyUsages()) {
        entityUsages.showMessageBox(
          'Cannot delete ' + entityType.toLowerCase() + ' \'' + (entity.name ?? '') + '\'.',
          'The ' + entityType.toLowerCase() + ' is still being used in the following places:',
        );
        return;
      }
    }

    const idsToDelete: number[] = entitiesToDelete.map(entity => entity.id);

    await entityApi.bulkDelete(idsToDelete);

    removeCategory(path);

    if (!autoDeleteIcons.value) {
      return;
    }

    const numberOfEntitiesLeftToDelete: ComputedRef<number> = computed(() =>
      entities.value.filter(entity => idsToDelete.includes(entity.id)).length,
    );

    await until(numberOfEntitiesLeftToDelete).toBe(0, { timeout: waitUntilDoneTimeoutMillis });

    const iconIdsToDelete: number[] = [];

    for (const entity of entitiesToDelete) {
      if (entity.iconId && !entityUsageService.findIconUsages(entity.iconId).hasAnyUsages()) {
        iconIdsToDelete.push(entity.iconId);
      }
    }

    await iconApi.bulkDelete(iconIdsToDelete);

    resetForms();
  }

  async function saveFolder(): Promise<void> {
    if (!editingFolderPath.value) {
      resetForms();
      return;
    }

    editingFolderModel.value.name = editingFolderModel.value.name.trim();

    if (!await validateFolderForm()) {
      return;
    }

    if (editingFolderOriginalModel.value.name === '') {
      // new folder
      addCategory([...editingFolderPath.value, editingFolderModel.value.name]);

      ElMessage.success({
        message: 'Folder created.',
      });

      resetForms();
      return;
    }

    await moveFolder(
      [...editingFolderPath.value, editingFolderOriginalModel.value.name],
      [...editingFolderPath.value, editingFolderModel.value.name],
    );

    resetForms();
  }

  async function moveFolder(source: string[], destination: string[]): Promise<void> {
    const entityUpdates: Partial<T>[] = [];
    for (const entity of entities.value) {
      if (categoryStartsWith(entity.category, source)) {
        entityUpdates.push({
          id: entity.id,
          category: [...destination, ...entity.category.slice(source.length)],
        } as Partial<T>);
      }
    }

    treeLoading.value = true;
    treeUpdatesPaused.value = true;

    try {
      await entityApi.bulkEdit(entityUpdates);

      const isDone: ComputedRef<boolean> = computed(() => {
        for (const entityUpdate of entityUpdates) {
          const entity: T | undefined = entities.value.filter(e => e.id === entityUpdate.id)[0];
          if (!_.isEqual(entity?.category, entityUpdate.category)) {
            return false;
          }
        }
        return true;
      });

      await until(isDone).toBeTruthy({ timeout: waitUntilDoneTimeoutMillis });

      removeCategory(source);

    } finally {
      treeLoading.value = false;
      treeUpdatesPaused.value = false;
      updateTree();
    }
  }

  async function onDragAndDrop(sourceNode: Node, targetNode: Node, dropType: NodeDropType): Promise<void> {
    updateTree();

    if (dropType !== 'inner') {
      targetNode = targetNode.parent;
    }

    const sourceIsFolder: boolean = sourceNode.data.id === undefined;
    const targetIsFolder: boolean = targetNode.data.id === undefined;

    if (sourceIsFolder) {
      // Workaround: sourceNode.parent is null for some reason,
      // thus we need to parse the sourcePath from sourceNode.data.key
      const sourceKey: string = sourceNode.data.key;
      const sourcePath: string[] = sourceKey.slice(1, sourceKey.length - 1).split('/');
      const targetPath: string[] = getNodePath(targetIsFolder ? targetNode : targetNode.parent);
      targetPath.push(sourcePath[sourcePath.length - 1]);

      if (!_.isEqual(sourcePath, targetPath)) {
        await moveFolder(sourcePath, targetPath);
      }
      return;
    }

    const sourceId: number = sourceNode.data.id;
    const targetPath: string[] = getNodePath(targetIsFolder ? targetNode : targetNode.parent);

    const entity: ComputedRef<T> = computed(() => entities.value.filter(e => e.id === sourceId)[0]!);

    if (_.isEqual(entity.value.category, targetPath)) {
      return;
    }

    treeLoading.value = true;

    try {
      await entityApi.edit({
        id: entity.value.id,
        category: targetPath,
      } as Partial<T>);

      const isDone: ComputedRef<boolean> = computed(() =>
        _.isEqual(entity.value.category, targetPath),
      );

      await until(isDone).toBeTruthy({ timeout: waitUntilDoneTimeoutMillis });

    } finally {
      treeLoading.value = false;
    }
  }

  return {
    state: {
      entities,
      tree,
      treeLoading,
      categories,
      expandedKeys,
      expandedKeysList,

      editingEntityId,
      editingEntityModel,
      editingEntityOriginalModel,
      editingEntityIconDataBase64,
      editingEntityDisplayPath,
      selectedIconOption,

      editingFolderPath,
      editingFolderModel,
      editingFolderOriginalModel,

      autoDeleteIcons,
      editingHasChanges,
      isSaving,
    },

    getButtonTooltip,

    onTreeNodeExpanded,
    onTreeNodeCollapsed,
    onDragAndDrop,

    createNewEntityAtRoot,
    createNewEntityAtNode,
    editEntity,
    deleteEntity,
    checkDeleteEntity,

    createNewFolderAtRoot,
    createNewFolderAtNode,
    editFolder,
    deleteFolder,

    save,
    saveFolder,
    cancel: resetForms,
  };
}

export type IconOptionValue = 'none' | 'select' | 'new' | 'item';
export const iconOptions: { label: string, value: IconOptionValue, icon: Component }[] = [
  { label: 'No icon', value: 'none', icon: Close },
  { label: 'Select icon', value: 'select', icon: Menu },
  { label: 'New icon', value: 'new', icon: UploadFilled },
];
export const iconOptions2: { label: string, value: IconOptionValue, icon: Component }[] = [
  { label: 'Same as product', value: 'none', icon: Link },
  { label: 'Select icon', value: 'select', icon: Menu },
  { label: 'New icon', value: 'new', icon: UploadFilled },
];
