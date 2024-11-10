<script setup lang="ts">
import type { Game, Item } from '@/types/model/standalone';
import { useItemStore } from '@/stores/model/itemStore';
import { computed, type ComputedRef, ref } from 'vue';
import EntityTree from '@/components/gameEditor/EntityTree.vue';
import { type EntityTreeService, iconOptions, useEntityTreeService } from '@/services/useEntityTreeService';
import { Check, Close, Folder, Plus } from '@element-plus/icons-vue';
import { ElButtonGroup, ElFormItem, ElInput, ElTooltip, type FormRules } from 'element-plus';
import CascaderSelect from '@/components/common/input/CascaderSelect.vue';
import { useIconStore } from '@/stores/model/iconStore';
import IconUpload from '@/components/gameEditor/IconUpload.vue';
import { elFormGameEntityNameUniqueValidator } from '@/utils/utils';
import { useItemApi } from '@/api/useItemApi';
import { useEntityUsagesService } from '@/services/useEntityUsagesService';

export interface ItemEditorProps {
  game: Game;
}

const props: ItemEditorProps = defineProps<ItemEditorProps>();

const itemStore = useItemStore();
const iconStore = useIconStore();
const itemApi = useItemApi();
const entityUsageService = useEntityUsagesService();

const items: ComputedRef<Item[]> = computed(() => itemStore.getByGameId(props.game.id));

const service: EntityTreeService<Item> = useEntityTreeService(
  computed(() => props.game),
  items,
  false,
  'Items',
  () => ({
    gameId: props.game.id,
    name: '',
    description: '',
    iconId: 0,
    // category is set by service
  }),
  (id: number) => {
    const item: Item = itemStore.getById(id)!;
    return {
      id: item.id,
      name: item.name,
      description: item.description,
      iconId: item.iconId,
      category: item.category,
    };
  },
  () => form.value.validate(() => ({})),
  () => folderForm.value.validate(() => ({})),
  entityUsageService.findItemUsages,
  itemApi,
);

const form = ref();
const formRules: ComputedRef<FormRules> = computed(() => ({
  name: [
    { required: true, message: 'Please enter a name for the item.', trigger: 'change' },
    {
      validator: elFormGameEntityNameUniqueValidator,
      store: itemStore,
      gameId: props.game.id,
      ownId: service.state.editingEntityId.value,
      message: 'An item with that name already exists.',
    },
    ...(service.state.selectedIconOption.value !== 'new' ? [] : [{
      validator: elFormGameEntityNameUniqueValidator,
      store: iconStore,
      gameId: props.game.id,
      message: 'An icon with that name already exists.',
    }]),
  ],
}));

const folderForm = ref();
const folderFormRules: FormRules = {
  name: [{ required: true, message: 'Please enter a name for the folder.', trigger: 'change' }],
};

</script>

<template>
  <div class="main">
    <div class="left">
      <div class="top">
        <h2 class="title">Items</h2>

        <el-button-group class="buttons">
          <el-tooltip :content="service.getButtonTooltip('New item')"
                      effect="dark" placement="top" transition="none" :hide-after="0">
            <el-button type="primary" :icon="Plus"
                       :disabled="service.state.editingHasChanges.value"
                       @click="service.createNewEntityAtRoot()" />
          </el-tooltip>
          <el-tooltip :content="service.getButtonTooltip('New folder')"
                      effect="dark" placement="top" transition="none" :hide-after="0">
            <el-button :icon="Folder"
                       :disabled="service.state.editingHasChanges.value"
                       @click="service.createNewFolderAtRoot()" />
          </el-tooltip>
        </el-button-group>
      </div>

      <EntityTree :service="service" entity-name="item" />
    </div>
    <div class="right">
      <div class="sticky">
        <template v-if="service.state.editingEntityId.value !== undefined">
          <div class="formContainer">
            <h2 v-if="service.state.editingEntityId.value === 0">New item</h2>
            <h2 v-else>Edit item '{{ service.state.editingEntityOriginalModel.value.name }}'</h2>

            <el-form label-width="120px"
                     :model="service.state.editingEntityModel.value"
                     ref="form"
                     :rules="formRules"
            >
              <el-form-item label="Item name" prop="name">
                <el-input v-model="service.state.editingEntityModel.value.name" />
              </el-form-item>

              <el-form-item label="Path">
                {{ service.state.editingEntityDisplayPath.value }}
              </el-form-item>

              <el-form-item label="Icon" prop="iconId">
                <el-segmented v-model="service.state.selectedIconOption.value" :options="iconOptions">
                  <template #default="{ item }">
                    <div style="margin: 4px;">
                      <el-icon size="20">
                        <component :is="item.icon" />
                      </el-icon>
                      <div>{{ item.label }}</div>
                    </div>
                  </template>
                </el-segmented>

                <template v-if="service.state.selectedIconOption.value === 'select'">
                  <CascaderSelect v-model="service.state.editingEntityModel.value.iconId!"
                                  :options="iconStore.getByGameId(game.id)"
                                  is-icon-entity
                                  style="margin-top: 12px; width: 100%;" />
                </template>
                <div v-else-if="service.state.selectedIconOption.value === 'new'" style="margin-top: 12px;">
                  <div style="line-height: 20px;">
                    The new icon will have the same name as this item, and it will be created at the path:<br>
                    <div style="margin-top: 4px;">
                      / Items {{ service.state.editingEntityDisplayPath.value }}
                    </div>
                  </div>
                  <IconUpload v-model:data-base64="service.state.editingEntityIconDataBase64.value"
                              style="margin-top: 12px;" />
                </div>
              </el-form-item>

              <div class="formFooter">
                <el-button :icon="Close"
                           :disabled="service.state.isSaving.value"
                           @click="service.cancel">
                  Cancel
                </el-button>
                <el-button type="primary" :icon="Check"
                           :loading="service.state.isSaving.value"
                           @click="service.save">
                  Save
                </el-button>
              </div>
            </el-form>
          </div>
        </template>
        <template v-else-if="service.state.editingFolderPath.value !== undefined">
          <div class="formContainer">
            <h2 v-if="service.state.editingFolderOriginalModel.value.name === ''">New folder</h2>
            <h2 v-else>Edit folder '{{ service.state.editingFolderOriginalModel.value.name }}'</h2>

            <el-form label-width="120px"
                     :model="service.state.editingFolderModel.value"
                     ref="folderForm"
                     :rules="folderFormRules"
            >
              <el-form-item label="Folder name" prop="name">
                <el-input v-model="service.state.editingFolderModel.value.name" />
              </el-form-item>

              <el-form-item label="Path">
                {{ service.state.editingEntityDisplayPath.value }}
                {{ service.state.editingFolderModel.value.name }}
              </el-form-item>

              <div class="formFooter">
                <el-button :icon="Close"
                           :disabled="service.state.isSaving.value"
                           @click="service.cancel">
                  Cancel
                </el-button>
                <el-button type="primary" :icon="Check"
                           :loading="service.state.isSaving.value"
                           @click="service.saveFolder">
                  Save
                </el-button>
              </div>
            </el-form>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.main {
  display: flex;
  gap: 72px;
}

.left {
  width: 30%;
}

.right {
  width: 30%;
}

.right .sticky {
  position: sticky;
  top: 24px;
}

.formContainer {
  background-color: #4b4b4b;
  border-radius: 8px;
  padding: 10px;

  --el-fill-color-light: #3d3d3d;
  --el-fill-color-lighter: #3d3d3d;
}

.formContainer h2 {
  margin-top: 0;
}

.formFooter {
  margin-top: 32px;
  display: flex;
  justify-content: end;
}

.top {
  display: flex;
  align-items: center;
  width: 100%;
}

.top .title {
  flex-grow: 1;
}

.top .buttons {
  margin-right: 7px;
}

</style>