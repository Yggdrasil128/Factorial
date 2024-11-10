<script setup lang="ts">
import { getModelSyncService } from '@/services/useModelSyncService';
import {
  onBeforeRouteLeave,
  onBeforeRouteUpdate,
  type RouteLocationNormalizedLoadedGeneric,
  useRoute,
} from 'vue-router';
import { computed, type ComputedRef, ref, type Ref } from 'vue';
import { useGameStore } from '@/stores/model/gameStore';
import type { Game } from '@/types/model/standalone';
import ItemEditor from '@/components/gameEditor/tabs/ItemEditor.vue';
import MachineEditor from '@/components/gameEditor/tabs/MachineEditor.vue';
import RecipeEditor from '@/components/gameEditor/tabs/RecipeEditor.vue';
import RecipeModifierEditor from '@/components/gameEditor/tabs/RecipeModifierEditor.vue';
import IconEditor from '@/components/gameEditor/tabs/IconEditor.vue';

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
      <h1>Game Editor - {{ game.name }}</h1>

      <el-tabs class="tabs" v-model="activeTab">
        <el-tab-pane label="Items" name="items" />
        <el-tab-pane label="Machines" name="machines" />
        <el-tab-pane label="Recipes" name="recipes" />
        <el-tab-pane label="Recipe Modifiers" name="recipeModifiers" />
        <el-tab-pane label="Icons" name="icons" />
      </el-tabs>
    </div>

    <ItemEditor v-show="activeTab === 'items'" :game="game" />
    <MachineEditor v-show="activeTab === 'machines'" :game="game" />
    <RecipeEditor v-show="activeTab === 'recipes'" :game="game" />
    <RecipeModifierEditor v-show="activeTab === 'recipeModifiers'" :game="game" />
    <IconEditor v-show="activeTab === 'icons'" :game="game" />
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