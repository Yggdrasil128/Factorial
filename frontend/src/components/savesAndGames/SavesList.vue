<script setup lang="ts">
import { useSaveStore } from '@/stores/model/saveStore';
import { Plus, Upload } from '@element-plus/icons-vue';
import { ElButton } from 'element-plus';
import { computed, type ComputedRef } from 'vue';
import type { Save } from '@/types/model/standalone';
import { ordinalComparator } from '@/utils/utils';
import { type DraggableSupport, useDraggableSupport } from '@/utils/useDraggableSupport';
import { useSaveApi } from '@/api/model/useSaveApi';
import { VueDraggableNext } from 'vue-draggable-next';
import SaveCard from '@/components/savesAndGames/SaveCard.vue';
import { useRouter } from 'vue-router';
import PlaceholderHelpBox from '@/components/common/PlaceholderHelpBox.vue';
import { useGameStore } from '@/stores/model/gameStore';

const router = useRouter();
const saveStore = useSaveStore();
const gameStore = useGameStore();
const saveApi = useSaveApi();

const saves: ComputedRef<Save[]> = computed(() => saveStore.getAll().sort(ordinalComparator));

const draggableSupport: DraggableSupport = useDraggableSupport(saves, input => saveApi.reorder(input));

function newSave(): void {
  router.push({ name: 'newSave' });
}

function importSave(): void {
  router.push({ name: 'importSave' });
}
</script>

<template>
  <div style="display: flex; align-items: center;">
    <h1 style="">Saves ({{ saves.length }})</h1>
    <div style="flex-grow: 1;"></div>
    <div>
      <el-button type="primary" :icon="Plus" @click="newSave">New save</el-button>
      <el-button :icon="Upload" @click="importSave">Import save</el-button>
    </div>
  </div>


  <PlaceholderHelpBox v-if="saves.length === 0" show-only-when-ready
                      title="You don't have any saves at the moment.">
    <template v-if="gameStore.getAll().length === 0">
      <p>
        Before you can get started with your first save, you need to create or import a <b>game</b> first (see right).
      </p>
    </template>
    <template v-else>
      <p>
        <el-link type="primary" @click="newSave">
          Create
        </el-link>
        or
        <el-link type="primary" @click="importSave">
          import
        </el-link>
        a save to get started.
      </p>
      <p>
        A save represents a specific play-through of a game. It contains your factories, production steps,
        transport links, etc.<br>
        A save is always linked to one specific game, which contains information about the items, machines,
        recipes, etc. in that game.
      </p>
    </template>
  </PlaceholderHelpBox>

  <vue-draggable-next v-else :model-value="saves" @end="draggableSupport.onDragEnd">
    <SaveCard style="margin-bottom: 12px;" v-for="save in saves" :key="save.id" :save="save" />
  </vue-draggable-next>
</template>

<style scoped>

</style>