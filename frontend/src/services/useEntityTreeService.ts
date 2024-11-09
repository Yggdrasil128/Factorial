import type { EntityWithCategory, TreeNode } from '@/utils/treeUtils';
import { convertToTreeByCategory } from '@/utils/treeUtils';
import type { Component, ComputedRef, Ref } from 'vue';
import { computed, reactive, ref, watch } from 'vue';
import { until } from '@vueuse/core';
import _ from 'lodash';
import Node from 'element-plus/es/components/tree/src/model/node';
import { Close, Menu, UploadFilled } from '@element-plus/icons-vue';
import type { Game, Icon } from '@/types/model/standalone';
import { ElMessage } from 'element-plus';
import { useIconStore } from '@/stores/model/iconStore';
import { useIconApi } from '@/api/useIconApi';

export type EntityTreeServiceState<T extends EntityWithCategory> = {
  entities: ComputedRef<T[]>;
  tree: ComputedRef<TreeNode[]>;
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
  editingFolderIsNew: Ref<boolean>;
  editingFolderName: Ref<string>;
  editingFolderOriginalName: Ref<string>;

  editingHasChanges: ComputedRef<boolean>;
  isSaving: Ref<boolean>;
}

export interface EntityTreeService<T extends EntityWithCategory> {
  state: EntityTreeServiceState<T>;

  getButtonTooltip: (msg: string) => string;

  onTreeNodeExpanded: (node: TreeNode) => void;
  onTreeNodeCollapsed: (node: TreeNode) => void;

  createNewEntityAtRoot: () => void;
  createNewEntityAtNode: (node: Node) => void;
  editEntity: (id: number) => void;
  deleteEntity: (id: number) => void;

  createNewFolderAtRoot: () => void;
  createNewFolderAtNode: (node: Node) => void;
  editFolder: (node: Node) => void;
  deleteFolder: (node: Node) => void;

  save: () => Promise<void>;
  saveFolder: () => Promise<void>;
  cancel: () => void;
}

export interface EntityApi<T> {
  createEntity: (entity: Partial<T>) => Promise<void>;
  editEntity: (entity: Partial<T>) => Promise<void>;
  deleteEntity: (entityId: number) => Promise<void>;

  bulkEditEntities: (entities: Partial<T>[]) => Promise<void>;
  bulkDeleteEntities: (entityIds: number[]) => Promise<void>;
}

export function useEntityTreeService<T extends EntityWithCategory>(
  game: ComputedRef<Game>,
  entities: ComputedRef<T[]>,
  isIconEntity: boolean,
  iconSubfolder: string,
  getNewEntityModel: () => Partial<T>,
  getExistingEntityModel: (id: number) => Partial<T>,
  validateForm: () => Promise<boolean>,
  entityApi: EntityApi<T>,
): EntityTreeService<T> {
  const iconApi = useIconApi();
  const iconStore = useIconStore();

  const categories: Ref<string[][]> = ref([['foo', 'bar', 'baz']]);

  const tree: ComputedRef<TreeNode[]> = computed(() =>
    convertToTreeByCategory(entities.value, isIconEntity, categories.value),
  );

  function addNewCategories(): void {
    for (const entity of entities.value) {
      let found: boolean = false;
      for (const category of categories.value) {
        if (_.isEqual(category, entity.category)) {
          found = true;
          break;
        }
      }
      if (!found) {
        categories.value.push(entity.category);
      }
    }
  }

  watch(
    entities,
    () => addNewCategories(),
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
  const editingFolderIsNew: Ref<boolean> = ref(false);
  const editingFolderName: Ref<string> = ref('');
  const editingFolderOriginalName: Ref<string> = ref('');

  const editingHasChanges = computed(() =>
    !_.isEqual(editingEntityModel.value, editingEntityOriginalModel.value)
    || editingEntityIconDataBase64.value !== ''
    || editingFolderName.value !== editingFolderOriginalName.value,
  );

  const editingEntityDisplayPath: ComputedRef<string> = computed(() => {
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

  function createNewEntityAtRoot(): void {
    createNewEntity([]);
  }

  function createNewEntityAtNode(node: Node): void {
    createNewEntity(getNodePath(node));
  }

  function createNewEntity(path: string[]) {
    cancel();

    const entity: Partial<T> = getNewEntityModel();
    entity.category = path;

    editingEntityId.value = 0;
    editingEntityModel.value = entity;
    editingEntityOriginalModel.value = _.clone(entity);

    editingEntityIconDataBase64.value = '';
  }

  function editEntity(id: number) {
    cancel();

    const entity: Partial<T> = getExistingEntityModel(id);

    editingEntityId.value = id;
    editingEntityModel.value = entity;
    editingEntityOriginalModel.value = _.clone(entity);

    selectedIconOption.value = entity.iconId ? 'select' : 'none';
    editingEntityIconDataBase64.value = '';
  }

  function deleteEntity(id: number): void {
    void entityApi.deleteEntity(id);
  }

  const isSaving: Ref<boolean> = ref(false);

  async function save(): Promise<void> {
    editingEntityModel.value.name = editingEntityModel.value.name?.trim();
    // also trim description

    if (!await validateForm()) {
      return;
    }

    isSaving.value = true;
    try {
      const entity: Partial<T> = editingEntityModel.value;

      if (selectedIconOption.value === 'new' && editingEntityIconDataBase64.value) {
        // create new icon first

        const iconData: string = editingEntityIconDataBase64.value;

        const regexResult = /data:([^;]+);base64,(.*)/.exec(iconData);
        if (!regexResult) {
          ElMessage.error({
            message: 'Unable to upload icon.',
          });
          isSaving.value = false;
          return;
        }
        const mimeType = regexResult[1];
        const base64 = regexResult[2];

        const icon: Partial<Icon> = {
          name: entity.name,
          gameId: game.value.id,
          category: [iconSubfolder, ...(entity.category ?? [])],
          mimeType: mimeType,
          imageData: base64,
        };

        await iconApi.createIcon(icon);

        const savedIcon: ComputedRef<Icon | undefined> = computed(() =>
          iconStore.getByGameId(game.value.id).filter(icon => icon.name === entity.name)[0],
        );

        await until(savedIcon).not.toBeUndefined({ timeout: 5000 });

        if (!savedIcon.value) {
          ElMessage.error({
            message: 'Icon could not be saved.',
          });
          isSaving.value = false;
          return;
        }

        entity.iconId = savedIcon.value.id;
      }

      if (editingEntityId.value === 0) {
        await entityApi.createEntity(entity);
      } else {
        await entityApi.editEntity(entity);
      }

    } catch (error) {
      isSaving.value = false;
      return;
    }

    isSaving.value = false;
    cancel(); // to close and reset the form
  }

  function cancel(): void {
    editingEntityId.value = undefined;
    editingEntityModel.value = {};
    editingEntityOriginalModel.value = {};
    editingEntityIconDataBase64.value = '';

    editingFolderPath.value = undefined;
    editingFolderIsNew.value = false;
    editingFolderName.value = '';
    editingFolderOriginalName.value = '';
  }

  function createNewFolderAtRoot(): void {
    createNewFolder([]);
  }

  function createNewFolderAtNode(node: Node): void {
    createNewFolder(getNodePath(node));
  }

  function createNewFolder(parentPath: string[]) {
    cancel();

    editingFolderPath.value = parentPath;
    editingFolderIsNew.value = true;
    editingFolderName.value = '';
    editingFolderOriginalName.value = '';
  }

  function editFolder(node: Node) {
    cancel();

    const path: string[] = getNodePath(node);
    const name: string = path.pop()!;

    editingFolderPath.value = path;
    editingFolderIsNew.value = false;
    editingFolderName.value = name;
    editingFolderOriginalName.value = name;
  }

  function deleteFolder(node: Node) {

  }

  async function saveFolder(): Promise<void> {

  }

  return {
    state: {
      entities,
      tree,
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
      editingFolderIsNew,
      editingFolderName,
      editingFolderOriginalName,

      editingHasChanges,
      isSaving,
    },

    getButtonTooltip,

    onTreeNodeExpanded,
    onTreeNodeCollapsed,

    createNewEntityAtRoot,
    createNewEntityAtNode,
    editEntity,
    deleteEntity,

    createNewFolderAtRoot,
    createNewFolderAtNode,
    editFolder,
    deleteFolder,

    save,
    saveFolder,
    cancel,
  };
}

export type IconOptionValue = 'none' | 'select' | 'new';
export const iconOptions: { label: string, value: IconOptionValue, icon: Component }[] = [
  { label: 'No icon', value: 'none', icon: Close },
  { label: 'Select icon', value: 'select', icon: Menu },
  { label: 'New icon', value: 'new', icon: UploadFilled },
];
