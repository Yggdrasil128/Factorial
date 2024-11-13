<script setup lang="ts">
import { getModelSyncService } from '@/services/useModelSyncService';
import {
  onBeforeRouteLeave,
  onBeforeRouteUpdate,
  type RouteLocationNormalizedLoadedGeneric,
  useRoute,
  useRouter,
} from 'vue-router';
import { computed, type ComputedRef, ref, type Ref } from 'vue';
import { useGameStore } from '@/stores/model/gameStore';
import type { Game } from '@/types/model/standalone';
import ItemEditor from '@/components/gameEditor/tabs/ItemEditor.vue';
import MachineEditor from '@/components/gameEditor/tabs/MachineEditor.vue';
import RecipeEditor from '@/components/gameEditor/tabs/RecipeEditor.vue';
import RecipeModifierEditor from '@/components/gameEditor/tabs/RecipeModifierEditor.vue';
import IconEditor from '@/components/gameEditor/tabs/IconEditor.vue';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';

const route = useRoute();
const router = useRouter();

const gameStore = useGameStore();
const modelSyncService = getModelSyncService();
const currentGameAndSaveStore = useCurrentGameAndSaveStore();

const isLoading: Ref<boolean> = ref(false);

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  const gameId = Number(route.params.editGameId);
  if (currentGameAndSaveStore.editingGameId === gameId) {
    return;
  }

  isLoading.value = true;
  modelSyncService.setEditingGameIdAndLoad(gameId)
    .finally(() => isLoading.value = false);
}

initFromRoute(route);
onBeforeRouteUpdate(route => initFromRoute(route));
onBeforeRouteLeave(() => modelSyncService.clearEditingGameId());

const gameId: ComputedRef<number> = computed(() => Number(route.params.editGameId));

const game: ComputedRef<Game | undefined> = computed(() => gameStore.getById(gameId.value));

type EditorTab = 'items' | 'machines' | 'recipes' | 'recipeModifiers' | 'icons';
const tab: Ref<EditorTab> = computed({
  get(): EditorTab {
    return route.params.tab as EditorTab;
  },
  set(newTab: EditorTab) {
    router.push({ name: 'gameEditor', params: { editGameId: route.params.editGameId, tab: newTab } });
  },
});

</script>

<template>
  <template v-if="!game || isLoading">
    <h1>Loading...</h1>
  </template>
  <template v-else>
    <div class="titleBar">
      <h1>Game Editor - {{ game.name }}</h1>

      <el-tabs class="tabs" v-model="tab">
        <el-tab-pane label="Items" name="items" />
        <el-tab-pane label="Machines" name="machines" />
        <el-tab-pane label="Recipes" name="recipes" />
        <el-tab-pane label="Recipe Modifiers" name="recipeModifiers" />
        <el-tab-pane label="Icons" name="icons" />
      </el-tabs>
    </div>

    <ItemEditor v-show="tab === 'items'" :game="game" />
    <MachineEditor v-show="tab === 'machines'" :game="game" />
    <RecipeEditor v-show="tab === 'recipes'" :game="game" />
    <RecipeModifierEditor v-show="tab === 'recipeModifiers'" :game="game" />
    <IconEditor v-show="tab === 'icons'" :game="game" />
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