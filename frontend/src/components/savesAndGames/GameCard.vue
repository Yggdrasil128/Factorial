<script setup lang="ts">
import type { Game } from '@/types/model/standalone';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { useSaveStore } from '@/stores/model/saveStore';
import { computed, type ComputedRef } from 'vue';
import { Delete, EditPen } from '@element-plus/icons-vue';
import IconImg from '@/components/common/IconImg.vue';
import { ElButton, ElPopconfirm, ElTooltip } from 'element-plus';
import BgcElButton from '@/components/common/input/BgcElButton.vue';
import { useRouter } from 'vue-router';

export interface GameCardProps {
  game: Game;
}

const props: GameCardProps = defineProps<GameCardProps>();

const router = useRouter();
const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const saveStore = useSaveStore();

const isCurrentGame: ComputedRef<boolean> = computed(() =>
  props.game.id === currentGameAndSaveStore.currentGameId
);

const numberOfSaves: ComputedRef<number> = computed(() =>
  saveStore.getByGameId(props.game.id).length
);

function openEditor(): void {
  router.push({ name: 'gameEditor', params: { editGameId: props.game.id } });
}

function deleteGame(): void {

}

</script>

<template>
  <div class="card" :class="{current: isCurrentGame}">
    <div class="topRow">
      <div class="left">
        <IconImg :icon="game.iconId" :size="32" />
        <div class="name">
          {{ game.name }}
        </div>
      </div>
      <div class="right">
        <div class="buttons">
          <bgc-el-button :icon="EditPen" @click="openEditor">
            Open editor
          </bgc-el-button>

          <el-popconfirm
            title="Delete this game?"
            width="200px"
            @confirm="deleteGame"
          >
            <template #reference>
                  <span class="row center tooltipHelperSpan">
                    <el-tooltip
                      effect="dark"
                      placement="top-start"
                      transition="none"
                      :hide-after="0"
                      content="Delete"
                    >
                      <el-button style="margin-left: 12px;" type="danger" :icon="Delete" />
                    </el-tooltip>
                  </span>
            </template>
          </el-popconfirm>
        </div>
      </div>
    </div>
    <div>
      <template v-if="numberOfSaves === 0">No saves are using this game.</template>
      <template v-else-if="numberOfSaves === 1">1 save is using this game.</template>
      <template v-else>{{ numberOfSaves }} saves are using this game.</template>
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

.card:hover .buttons {
  display: block;
}

.name {
  padding-top: 2px;
  font-size: 24px;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}
</style>