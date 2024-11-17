<script setup lang="ts">
import { onBeforeRouteUpdate, type RouteLocationNormalizedLoadedGeneric, useRoute, useRouter } from 'vue-router';
import { useSaveStore } from '@/stores/model/saveStore';
import { useGameStore } from '@/stores/model/gameStore';
import { computed, reactive, type Ref, ref, watch } from 'vue';
import type { Icon, Save } from '@/types/model/standalone';
import { useSaveApi } from '@/api/model/useSaveApi';
import { useIconApi } from '@/api/model/useIconApi';
import _ from 'lodash';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import EditModal from '@/components/common/EditModal.vue';
import { ElFormItem, ElInput } from 'element-plus';
import FlatSelect from '@/components/common/input/FlatSelect.vue';

const router = useRouter();
const route = useRoute();

const saveStore = useSaveStore();
const gameStore = useGameStore();

const saveApi = useSaveApi();
const iconApi = useIconApi();

const save: Ref<Partial<Save>> = ref({});
const original: Ref<Partial<Save>> = ref({});
const hasChanges = computed(() => !_.isEqual(save.value, original.value));
const isSaving: Ref<boolean> = ref(false);
const editModal = ref();

const formRules = reactive({
  name: [{ required: true, message: 'Please enter a name for the save.', trigger: 'blur' }],
  gameId: [{
    required: true, message: 'Please select a game.', trigger: 'change',
    validator: (_: any, value: any, callback: any) => {
      if (!value) {
        callback(new Error());
      }
      callback();
    },
  }],
});

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  if (route.name === 'newSave') {
    save.value = {
      name: '',
      description: '',
      iconId: 0,
      gameId: 0,
    };
  } else {
    const saveId: number = Number(route.params.editSaveId);
    const currentSave: Save | undefined = saveStore.getById(saveId);
    if (!currentSave) {
      console.error('Save with id ' + saveId + ' not found');
      router.push({ name: 'savesAndGames' });
      return;
    }
    save.value = {
      id: saveId,
      name: currentSave.name,
      description: currentSave.description,
      iconId: currentSave.iconId,
      gameId: currentSave.gameId,
    };
  }
  original.value = _.clone(save.value);
  isSaving.value = false;
}

initFromRoute(route);
onBeforeRouteUpdate(initFromRoute);

async function submitForm(): Promise<void> {
  save.value.name = save.value.name?.trim();
  save.value.description = save.value.description?.trim();

  if (!(await editModal.value.validate())) {
    return;
  }

  isSaving.value = true;

  if (route.name === 'newSave') {
    await saveApi.create(save.value);
  } else {
    await saveApi.edit({ ...save.value, gameId: undefined });
  }

  await router.push({ name: 'savesAndGames' });
}

const iconOptions: Ref<Icon[]> = ref([]);

watch(() => save.value.gameId, () => {
  if (!save.value.gameId) return;
  iconApi.retrieveAllByGameId(save.value.gameId)
    .then((icons: Icon[]) => {
      iconOptions.value = icons;
    });
}, { immediate: true });
</script>

<template>
  <edit-modal
    :title="route.name === 'newSave' ? 'New save' : 'Edit save'"
    form-label-width="120px"
    :form-model="save"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="isSaving"
    @submit="submitForm"
    ref="editModal"
  >
    <template #description>
      A save represents a specific play-through of a game. It contains your factories, production steps,
      transport links, etc.<br>
      A save is always linked to one specific game, which contains information about the items, machines,
      recipes, etc. in that game.
    </template>

    <template #form>
      <el-form-item label="Name" prop="name">
        <el-input v-model="save.name" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input v-model="save.description" type="textarea" autosize />
      </el-form-item>

      <el-form-item label="Game" prop="gameId">
        <FlatSelect class="full-width" v-model="save.gameId!" :options="gameStore.getAll()"
                    :disabled="route.name !== 'newSave'" />
        <span v-if="route.name !== 'newSave'" style="line-height: 20px; margin-top: 8px;">
          If you want to change this save's game, close this dialog and select
          'Migrate to another game' from the save's dropdown menu, or click here.
        </span>
      </el-form-item>

      <el-form-item label="Icon" prop="iconId">
        <FlatSelect class="full-width" v-model="save.iconId!" :options="iconOptions" :disabled="!save.gameId"
                    :placeholder="save.gameId ? 'Select icon' : 'Select a game first'"
                    is-icon-entity clearable />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>