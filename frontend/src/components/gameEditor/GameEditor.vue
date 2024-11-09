<script setup lang="ts">
import { getModelSyncService } from '@/services/model/modelSyncService';
import {
  onBeforeRouteLeave,
  onBeforeRouteUpdate,
  type RouteLocationNormalizedLoadedGeneric,
  useRoute,
} from 'vue-router';
import { computed, type ComputedRef, ref, type Ref } from 'vue';
import { useGameStore } from '@/stores/model/gameStore';
import type { Game } from '@/types/model/standalone';
import ItemEditor from '@/components/gameEditor/items/ItemEditor.vue';

const route = useRoute();

const gameStore = useGameStore();
const modelSyncService = getModelSyncService();

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  modelSyncService.setEditingGameIdAndLoad(Number(route.params.editGameId));
}

initFromRoute(route);
onBeforeRouteUpdate(route => initFromRoute(route));
onBeforeRouteLeave(() => modelSyncService.clearEditingGameId());

const gameId: ComputedRef<number> = computed(() => Number(route.params.editGameId));

const game: ComputedRef<Game | undefined> = computed(() => gameStore.getById(gameId.value));

type EditorTabs = 'items' | 'machines' | 'recipes' | 'recipeModifiers' | 'icons';
const activeTab: Ref<EditorTabs> = ref('items');

</script>

<template>
  <template v-if="!game">
    Loading...
  </template>
  <template v-else>
    <div class="titleBar">
      <h1>Game Editor</h1>

      <el-tabs class="tabs" v-model="activeTab">
        <el-tab-pane label="Items" name="items" />
        <el-tab-pane label="Machines" name="machines" />
        <el-tab-pane label="Recipes" name="recipes" />
        <el-tab-pane label="Recipe Modifiers" name="recipeModifiers" />
        <el-tab-pane label="Icons" name="icons" />
      </el-tabs>
    </div>

    <ItemEditor v-show="activeTab === 'items'" :game="game" />
  </template>
</template>

<style scoped>
.titleBar {
  display: flex;
  align-items: center;
}

.tabs {
  margin-left: 48px;
  margin-top: 12px;
  --el-font-size-base: 16px;
}
</style>