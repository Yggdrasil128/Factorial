<script setup lang="ts">
import {
  type EntityTreeService,
  type EntityType,
  iconOptions,
  iconOptionsForRecipe,
} from '@/services/useEntityTreeService';
import { ElButtonGroup, ElFormItem, ElInput, ElTooltip, type FormRules } from 'element-plus';
import { ref } from 'vue';
import { Check, Close, Folder, Plus, Search } from '@element-plus/icons-vue';
import CascaderSelect from '@/components/common/input/CascaderSelect.vue';
import IconUpload from '@/components/gameEditor/IconUpload.vue';
import EntityTree from '@/components/gameEditor/EntityTree.vue';
import type { Game } from '@/types/model/standalone';
import { useIconStore } from '@/stores/model/iconStore';

export interface EntityEditorProps {
  game: Game;
  service: EntityTreeService<any>;
  entityType: EntityType;
  formRules: FormRules;
  formLabelWidth?: string;
}

const props: EntityEditorProps = defineProps<EntityEditorProps>();

const iconStore = useIconStore();
const service = props.service;

const form = ref();
const folderForm = ref();
const folderFormRules: FormRules = {
  name: [{ required: true, message: 'Please enter a name for the folder.', trigger: 'change' }],
};

async function validateForm(): Promise<boolean> {
  return await form.value.validate(() => ({}));
}

async function validateFolderForm(): Promise<boolean> {
  return await folderForm.value.validate(() => ({}));
}

defineExpose({ validateForm, validateFolderForm });
</script>

<template>
  <div class="main">
    <div class="left">
      <div class="top">
        <h2 class="title">{{ entityType }}s</h2>

        <div class="spacer" />

        <el-input v-model="service.state.filterInputModel.value"
                  class="search"
                  placeholder="Search..."
                  :prefix-icon="Search"
                  clearable />

        <el-button-group class="buttons">
          <el-tooltip :content="service.getButtonTooltip('New ' + entityType.toLowerCase())"
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

      <EntityTree :service="service" :entity-type="entityType" />
    </div>
    <div class="right">
      <div class="sticky">
        <template v-if="service.state.editingEntityId.value !== undefined">
          <div class="formContainer">
            <h2 v-if="service.state.editingEntityId.value === 0">New {{ entityType.toLowerCase() }}</h2>
            <h2 v-else>Edit {{ entityType.toLowerCase() }} '{{ service.state.editingEntityOriginalModel.value.name
              }}'</h2>

            <p v-if="entityType === 'Recipe' && service.state.editingEntityId.value === 0">
              Tip: After selecting a recipe product, the name for the recipe will be automatically filled in.
            </p>

            <el-form :label-width="formLabelWidth ?? '120px'"
                     :model="service.state.editingEntityModel.value"
                     ref="form"
                     :rules="formRules"
                     @submit.prevent="service.save"
            >
              <el-form-item label="Name" prop="name">
                <el-input id="formNameInput" v-model="service.state.editingEntityModel.value.name" />
              </el-form-item>

              <el-form-item label="Path">
                {{ service.state.editingEntityDisplayPath.value }}
              </el-form-item>

              <el-form-item v-if="entityType !== 'Icon'" label="Description" prop="description">
                <el-input v-model="service.state.editingEntityModel.value.description"
                          type="textarea" autosize />
              </el-form-item>

              <el-form-item v-if="entityType !== 'Icon'" label="Icon" prop="iconId">
                <el-segmented v-model="service.state.selectedIconOption.value"
                              :options="entityType === 'Recipe' ? iconOptionsForRecipe : iconOptions">
                  <template #default="{ item }">
                    <div style="margin: 4px;">
                      <div v-if="typeof item.icon === 'string'" class="maskIcon"
                           style="width: 20px; height: 20px;"
                           :style="{mask: 'url(\'' + item.icon + '\') center/contain'}">
                      </div>
                      <el-icon v-else size="20">
                        <component :is="item.icon" />
                      </el-icon>
                      <div>{{ item.label }}</div>
                    </div>
                  </template>
                </el-segmented>

                <template v-if="service.state.selectedIconOption.value === 'none' && entityType === 'Recipe'">
                  <div style="margin-top: 12px; line-height: 20px;">
                    When 'Same as product' is selected, the recipe will have the same icon as the (first) product item.
                  </div>
                </template>
                <template v-if="service.state.selectedIconOption.value === 'select'">
                  <CascaderSelect v-model="service.state.editingEntityModel.value.iconId!"
                                  :options="iconStore.getByGameId(game.id)"
                                  is-icon-entity
                                  style="margin-top: 12px; width: 100%;" />
                </template>
                <template v-else-if="service.state.selectedIconOption.value === 'new'">
                  <div style="margin-top: 12px;">
                    <div style="line-height: 20px;">
                      The new icon will have the same name as this {{ entityType.toLowerCase() }}, and it will be
                      created
                      at the path:<br>
                      <div style="margin-top: 4px;">
                        / {{ entityType }}s {{ service.state.editingEntityDisplayPath.value }}
                      </div>
                      <IconUpload v-model:data-base64="service.state.editingEntityIconDataBase64.value"
                                  style="margin-top: 12px;" />
                    </div>
                  </div>
                </template>
                <template v-else-if="service.state.selectedIconOption.value === 'url'">
                  <div class="full-width" style="margin-top: 12px;">
                    <el-input v-model="service.state.editingEntityIconDataUrl.value" placeholder="Enter icon URL" />
                  </div>
                </template>
              </el-form-item>

              <slot name="form"></slot>

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
                     @submit.prevent="service.saveFolder"
            >
              <el-form-item label="Folder name" prop="name">
                <el-input id="formNameInput" v-model="service.state.editingFolderModel.value.name" />
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
  width: 40%;
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
  gap: 16px;
  align-items: center;
  width: 100%;
}

.top .title {
  flex: 0 0 auto;
}

.top .spacer {
  flex: 1 0 0;
}

.top .search {
  flex: 0 1 160px;
}

.top .title {
  flex: 0 0 auto;
}

.top .buttons {
  flex: 0 0 auto;
  margin-right: 7px;
}

.maskIcon {
  background-color: var(--el-segmented-color);
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 3px;
}
</style>

<style>
.el-segmented__item:hover .maskIcon {
  background-color: var(--el-segmented-item-hover-color);
}
</style>