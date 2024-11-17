<script setup lang="ts">
import type { Game } from '@/types/model/standalone';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { useSaveStore } from '@/stores/model/saveStore';
import { computed, type ComputedRef, ref, type Ref } from 'vue';
import { ArrowDown, CopyDocument, Delete, Download, Edit, EditPen } from '@element-plus/icons-vue';
import IconImg from '@/components/common/IconImg.vue';
import { ElButtonGroup, ElDropdown, ElDropdownItem, ElDropdownMenu, ElMessageBox, ElTooltip } from 'element-plus';
import { useRouter } from 'vue-router';
import { useEntityUsagesService } from '@/services/useEntityUsagesService';
import { useGameApi } from '@/api/model/useGameApi';
import { useEntityCloneNameGeneratorService } from '@/services/useEntityCloneNameGeneratorService';
import { useGameStore } from '@/stores/model/gameStore';
import type { GameSummary } from '@/types/model/summary';
import { downloadJsonFile } from '@/utils/downloadFileUtil';

export interface GameCardProps {
  game: Game;
}

const props: GameCardProps = defineProps<GameCardProps>();

const dropdownMenuOpen: Ref<boolean> = ref(false);

const router = useRouter();
const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const saveStore = useSaveStore();
const gameStore = useGameStore();
const gameApi = useGameApi();
const entityUsagesService = useEntityUsagesService();
const entityCloneNameGeneratorService = useEntityCloneNameGeneratorService();

const isCurrentGame: ComputedRef<boolean> = computed(() =>
  props.game.id === currentGameAndSaveStore.currentGameId,
);

const numberOfSaves: ComputedRef<number> = computed(() =>
  saveStore.getByGameId(props.game.id).length,
);

function openEditor(): void {
  router.push({ name: 'gameEditor', params: { editGameId: props.game.id, tab: 'items' } });
}

function editGame(): void {
  router.push({ name: 'editGame', params: { gameId: props.game.id } });
}

function cloneGame(): void {
  const gameName = entityCloneNameGeneratorService.generateName(
    props.game.name,
    gameStore.getAll(),
  );
  gameApi.clone(props.game.id, gameName);
}

async function exportGame(): Promise<void> {
  const gameSummary: GameSummary = await gameApi.export(props.game.id);
  const data: string = JSON.stringify(gameSummary, undefined, 2);
  const filename = props.game.name + '.json';
  downloadJsonFile(data, filename);
}

function deleteGame(): void {
  const entityUsages = entityUsagesService.findGameUsages(props.game.id);
  if (entityUsages.hasAnyUsages()) {
    entityUsages.showMessageBox(
      'Cannot delete game \'' + props.game.name + '\'.',
      'The game is still used in the following places:',
    );
    return;
  }
  ElMessageBox.confirm(
    'Are you sure you want to delete game \'' + props.game.name + '\'?',
    'Warning',
    {
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      type: 'warning',
    },
  ).then(() => {
    gameApi.delete(props.game.id);
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
  <div class="card row items-center" :class="{current: isCurrentGame, forceShowButtons: dropdownMenuOpen}">
    <IconImg :icon="game.iconId" :size="32" />

    <div class="nameAndInfo">
      <div class="name">
        {{ game.name }}
      </div>
      <div>
        <template v-if="numberOfSaves === 0">No save is using this game.</template>
        <template v-else-if="numberOfSaves === 1">1 save is using this game.</template>
        <template v-else>{{ numberOfSaves }} saves are using this game.</template>
      </div>
    </div>

    <div class="buttons">
      <el-button :icon="EditPen" @click="openEditor">
        Open editor
      </el-button>

      <el-button-group style="margin-left: 12px;">

        <el-tooltip content="Edit name / icon"
                    effect="dark" placement="top" transition="none" :hide-after="0">
          <el-button :icon="Edit" @click="editGame" />
        </el-tooltip>

        <el-dropdown class="gameDropdown" trigger="click" @visible-change="onDropdownVisibleChange">
          <el-button :icon="ArrowDown" />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="CopyDocument" @click="cloneGame">
                Clone
              </el-dropdown-item>
              <el-dropdown-item :icon="Download" @click="exportGame">
                Export
              </el-dropdown-item>
              <el-dropdown-item :icon="Delete" @click="deleteGame"
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
.gameDropdown > .el-button {
  --el-button-divide-border-color: #60606080;
  padding-left: 8px;
  padding-right: 8px;
}
</style>
