<script setup lang="ts">
import { useGameStore } from '@/stores/model/gameStore';
import { useGameApi } from '@/api/model/useGameApi';
import { computed, type ComputedRef } from 'vue';
import type { Game } from '@/types/model/standalone';
import { ordinalComparator } from '@/utils/utils';
import { type DraggableSupport, useDraggableSupport } from '@/utils/useDraggableSupport';
import { Plus, Upload } from '@element-plus/icons-vue';
import { VueDraggableNext } from 'vue-draggable-next';
import GameCard from '@/components/savesAndGames/GameCard.vue';
import { ElButton } from 'element-plus';

const gameStore = useGameStore();
const gameApi = useGameApi();

const games: ComputedRef<Game[]> = computed(() => gameStore.getAll().sort(ordinalComparator));

const draggableSupport: DraggableSupport = useDraggableSupport(games, gameApi.reorder);

function newGame(): void {

}

function importGame(): void {

}

</script>

<template>
  <div style="display: flex; align-items: center;">
    <h1 style="">Games ({{ games.length }})</h1>
    <div style="flex-grow: 1;"></div>
    <div>
      <el-button type="primary" :icon="Plus" @click="newGame">New game</el-button>
      <el-button :icon="Upload" @click="importGame">Import game</el-button>
    </div>
  </div>

  <vue-draggable-next :model-value="games" @end="draggableSupport.onDragEnd">
    <GameCard style="margin-bottom: 12px;" v-for="game in games" :key="game.id" :game="game" />
  </vue-draggable-next>
</template>

<style scoped>

</style>