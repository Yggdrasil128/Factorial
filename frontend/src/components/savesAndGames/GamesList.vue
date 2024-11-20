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
import { useRouter } from 'vue-router';
import PlaceholderHelpBox from '@/components/common/PlaceholderHelpBox.vue';

const router = useRouter();
const gameStore = useGameStore();
const gameApi = useGameApi();

const games: ComputedRef<Game[]> = computed(() => gameStore.getAll().sort(ordinalComparator));

const draggableSupport: DraggableSupport = useDraggableSupport(games, gameApi.reorder);

function newGame(): void {
  router.push({ name: 'newGame' });
}

function importGame(): void {
  router.push({ name: 'importGame' });
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

  <PlaceholderHelpBox v-if="games.length === 0" show-only-when-ready
                      title="You don't have any games at the moment.">
    <p>
      <el-link type="primary" @click="newGame">
        Create
      </el-link>
      or
      <el-link type="primary" @click="newGame">
        import
      </el-link>
      a game to get started.
    </p>
    <p>
      A game contains information about all available items, machines, recipes, etc.<br>
      Multiple saves can share the same game, and you can migrate saves from one game to another,
      for example, if the game updates and you want to use new recipes in your save.
    </p>
  </PlaceholderHelpBox>

  <vue-draggable-next v-else :model-value="games" @end="draggableSupport.onDragEnd">
    <GameCard style="margin-bottom: 12px;" v-for="game in games" :key="game.id" :game="game" />
  </vue-draggable-next>
</template>

<style scoped>

</style>