<script setup lang="ts">
import type {Game, Save} from '@/types/model/standalone';
import IconImg from '@/components/common/IconImg.vue';
import {ArrowDown, Connection, CopyDocument, Delete, Download, Edit, Promotion} from '@element-plus/icons-vue';
import {
  ElButton,
  ElButtonGroup,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElMessage,
  ElMessageBox,
  ElTooltip,
} from 'element-plus';
import {type GameStore, useGameStore} from '@/stores/model/gameStore';
import {computed, type ComputedRef, type Ref, ref} from 'vue';
import {type CurrentGameAndSaveStore, useCurrentGameAndSaveStore} from '@/stores/currentGameAndSaveStore';
import {getModelSyncService, type ModelSyncService} from '@/services/useModelSyncService';
import {type Router, useRouter} from 'vue-router';
import {SaveApi, useSaveApi} from '@/api/model/useSaveApi';
import {
  type EntityCloneNameGeneratorService,
  useEntityCloneNameGeneratorService
} from '@/services/useEntityCloneNameGeneratorService';
import {type SaveStore, useSaveStore} from '@/stores/model/saveStore';
import type {SaveSummary} from '@/types/model/summary';
import {downloadJsonFile} from '@/utils/downloadFileUtil';

export interface SaveCardProps {
  save: Save;
}

const props: SaveCardProps = defineProps<SaveCardProps>();

const router: Router = useRouter();

const currentGameAndSaveStore: CurrentGameAndSaveStore = useCurrentGameAndSaveStore();
const saveStore: SaveStore = useSaveStore();
const gameStore: GameStore = useGameStore();
const modelSyncService: ModelSyncService = getModelSyncService();
const saveApi: SaveApi = useSaveApi();
const entityCloneNameGeneratorService: EntityCloneNameGeneratorService = useEntityCloneNameGeneratorService();

const dropdownMenuOpen: Ref<boolean> = ref(false);

const isCurrentSave: ComputedRef<boolean> = computed(() => props.save.id === currentGameAndSaveStore.currentSaveId);

const game: ComputedRef<Game | undefined> = computed(() => gameStore.getById(props.save.gameId));

const isSaveLoading: Ref<boolean> = ref(false);

function loadSave(): void {
  isSaveLoading.value = true;

  modelSyncService.setCurrentSaveIdAndLoad(props.save.id)
    .then(() => {
      ElMessage.success({ message: 'Save loaded.' });
    })
    .finally(() => {
      isSaveLoading.value = false;
    });
}

function editSave(): void {
  router.push({ name: 'editSave', params: { saveId: props.save.id } });
}

function cloneSave(): void {
  const saveName: string = entityCloneNameGeneratorService.generateName(
    props.save.name,
    saveStore.getAll(),
  );
  saveApi.clone(props.save.id, saveName);
}

async function exportSave(): Promise<void> {
  const saveSummary: SaveSummary = await saveApi.export(props.save.id);
  const data: string = JSON.stringify(saveSummary, undefined, 2);
  const filename: string = props.save.name + '.json';
  downloadJsonFile(data, filename);
}

function migrateSave(): void {
  router.push({ name: 'migrateSave', params: { saveId: props.save.id } });
}

function deleteSave(): void {
  ElMessageBox.confirm(
    'Are you sure you want to delete save \'' + props.save.name + '\'?',
    'Warning',
    {
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      type: 'warning',
    },
  ).then(() => {
    saveApi.delete(props.save.id);
  }).catch(() => {
  });
}

function onDropdownVisibleChange(visible: boolean): void {
  if (visible) {
    dropdownMenuOpen.value = true;
  } else {
    setTimeout(() => dropdownMenuOpen.value = false, 150);
  }
}

</script>

<template>
  <div class="card row items-center" :class="{current: isCurrentSave, forceShowButtons: dropdownMenuOpen}">
    <IconImg class="icon" :icon="save.iconId" :size="48" />

    <div class="nameAndInfo">
      <div class="name">
        {{ save.name }}
      </div>
      <div v-if="game">
        Using game:
        <IconImg :icon="game.iconId" :size="16" />
        {{ game.name }}
      </div>
    </div>

    <div class="buttons">
      <el-button v-if="!isCurrentSave"
                 type="primary" :icon="Promotion" :loading="isSaveLoading"
                 @click="loadSave">
        Load this save
      </el-button>

      <el-button-group style="margin-left: 12px;">
        <el-tooltip content="Edit"
                    effect="dark" placement="top" transition="none" :hide-after="0">
          <el-button :icon="Edit" @click="editSave" />
        </el-tooltip>

        <el-dropdown class="saveDropdown" trigger="click" @visible-change="onDropdownVisibleChange">
          <el-button :icon="ArrowDown" />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="CopyDocument" @click="cloneSave">
                Clone
              </el-dropdown-item>
              <el-dropdown-item :icon="Download" @click="exportSave">
                Export
              </el-dropdown-item>
              <el-dropdown-item :icon="Connection" @click="migrateSave">
                Migrate to another game
              </el-dropdown-item>
              <el-dropdown-item :icon="Delete" @click="deleteSave"
                                style="color: var(--el-color-danger);">
                Delete
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-button-group>
    </div>
  </div>
</template>

<style scoped>
.card {
  --padding: 10px;
  width: calc(100% - 2 * var(--padding));
  padding: var(--padding);
  overflow: hidden;
  background-color: #4b4b4b;
  border-radius: 8px;
}

.card.current {
  background-color: #556e55;
}

.nameAndInfo {
  flex: 1 1 auto;
  min-width: 0;
  margin-left: 6px;
}

.card .buttons {
  flex: 0 0 auto;
  display: none;
}

.card:hover .buttons, .card.forceShowButtons .buttons {
  display: block;
}

.name {
  font-size: 24px;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

/*noinspection CssUnusedSymbol*/
.saveDropdown > .el-button {
  --el-button-divide-border-color: #60606080;
  padding-left: 8px;
  padding-right: 8px;
}
</style>