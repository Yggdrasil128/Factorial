<script setup lang="ts">
import type { Game, Save } from '@/types/model/standalone';
import IconImg from '@/components/common/IconImg.vue';
import { ArrowDown, Connection, CopyDocument, Delete, Download, Edit, Promotion } from '@element-plus/icons-vue';
import {
  ElButton,
  ElButtonGroup,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElMessage,
  ElTooltip,
} from 'element-plus';
import { useGameStore } from '@/stores/model/gameStore';
import { computed, type ComputedRef, type Ref, ref } from 'vue';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { getModelSyncService } from '@/services/useModelSyncService';

export interface SaveCardProps {
  save: Save;
}

const props: SaveCardProps = defineProps<SaveCardProps>();

const dropdownMenuOpen: Ref<boolean> = ref(false);

const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const gameStore = useGameStore();
const modelSyncService = getModelSyncService();

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

}

function cloneSave(): void {

}

function exportSave(): void {

}

function migrateSave(): void {

}

function deleteSave(): void {

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
  <div class="card" :class="{current: isCurrentSave, forceShowButtons: dropdownMenuOpen}">
    <div class="topRow">
      <div class="left">
        <IconImg :icon="save.iconId" :size="32" />
        <div class="name">
          {{ save.name }}
        </div>
      </div>
      <div class="right">
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
    </div>
    <div v-if="game">
      Using game:
      <IconImg :icon="game.iconId" :size="16" />
      {{ game.name }}
    </div>
  </div>
</template>

<style scoped>
.card {
  width: calc(100% - 20px);
  padding: 10px;
  overflow: hidden;
  background-color: #4b4b4b;
  border-radius: 8px;
}

.card.current {
  background-color: #556e55;
}

.topRow {
  display: flex;
  align-items: center;
  height: 32px;
  width: 100%;
  max-width: 100%;
}

.topRow .left {
  flex: 1 1 auto;
  min-width: 0;
}

.topRow .right {
  flex: 0 0 auto;
}

.card .buttons {
  display: none;
}

.card:hover .buttons, .card.forceShowButtons .buttons {
  display: block;
}

.name {
  padding-top: 2px;
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