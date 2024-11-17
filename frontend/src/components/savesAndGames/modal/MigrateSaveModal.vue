<script setup lang="ts">
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import EditModal from '@/components/common/EditModal.vue';
import { onBeforeRouteUpdate, type RouteLocationNormalizedLoadedGeneric, useRoute, useRouter } from 'vue-router';
import { useSaveStore } from '@/stores/model/saveStore';
import { useGameStore } from '@/stores/model/gameStore';
import { useSaveApi } from '@/api/model/useSaveApi';
import { computed, type ComputedRef, reactive, ref, type Ref } from 'vue';
import type { Game, Save } from '@/types/model/standalone';
import { ElFormItem, ElInput } from 'element-plus';
import FlatSelect from '@/components/common/input/FlatSelect.vue';
import { useEntityCloneNameGeneratorService } from '@/services/useEntityCloneNameGeneratorService';
import _ from 'lodash';
import { elFormEntityNameUniqueValidator } from '@/utils/utils';

const router = useRouter();
const route = useRoute();

const saveStore = useSaveStore();
const gameStore = useGameStore();
const saveApi = useSaveApi();
const entityCloneNameGeneratorService = useEntityCloneNameGeneratorService();

type FormModel = {
  saveId: number;
  saveName: string;
  gameId: number;
}

const formModel: Ref<FormModel> = ref({ saveId: 0, saveName: '', gameId: 0 });
const original: Ref<FormModel> = ref(formModel.value);
const hasChanges = computed(() => !_.isEqual(formModel.value, original.value));
const isSaving: Ref<boolean> = ref(false);
const editModal = ref();

const formRules = reactive({
  gameId: [{
    required: true, message: 'Please select a game.', trigger: 'change',
    validator: (_: any, value: any, callback: any) => {
      if (!value) {
        callback(new Error());
      }
      callback();
    },
  }],
  saveName: [
    { required: true, message: 'Please enter a name for the new save.', trigger: 'blur' },
    {
      validator: elFormEntityNameUniqueValidator,
      entities: saveStore.getAll(),
      message: 'A save with that name already exists.',
    },
  ],
});

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  const saveId = Number(route.params.saveId);
  const save: Save | undefined = saveStore.getById(saveId);
  if (!save) {
    console.warn('Save with ID ' + saveId + ' not found.');
    router.push({ name: 'savesAndGames' });
    return;
  }
  const saveName: string = entityCloneNameGeneratorService.generateName(
    save.name,
    saveStore.getAll(),
  );
  formModel.value = {
    saveId: saveId,
    saveName: saveName,
    gameId: 0,
  };
  original.value = _.clone(formModel.value);
  isSaving.value = false;
}

initFromRoute(route);
onBeforeRouteUpdate(initFromRoute);

async function submitForm(): Promise<void> {
  if (!(await editModal.value.validate())) {
    return;
  }

  isSaving.value = true;

  await saveApi.migrateToGameId(
    formModel.value.saveId,
    formModel.value.saveName,
    formModel.value.gameId,
  );

  await router.push({ name: 'savesAndGames' });
}

const save: ComputedRef<Save | undefined> = computed(() => saveStore.getById(formModel.value.saveId));

const gameOptions: ComputedRef<Game[]> = computed(() => {
  if (!save.value) return [];
  return gameStore.getAll().filter(game => game.id !== save.value?.gameId);
});

</script>

<template>
  <edit-modal
    :title="'Migrate save \'' + save?.name + '\''"
    form-label-width="140px"
    :form-model="formModel"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="isSaving"
    @submit="submitForm"
    ref="editModal"
  >
    <template #description>
      Migrating a save to another game will <b>not</b> modify the old save, but create a new one instead.
      <br>
      Migration will attempt to match items, recipes, machines, etc. by their name,
      and fail if the save is referencing something that doesn't exist in the new game.
    </template>

    <template #form>
      <el-form-item label="Save">
        {{ save?.name }}
      </el-form-item>

      <el-form-item label="New save name" prop="saveName">
        <el-input v-model="formModel.saveName" />
      </el-form-item>

      <el-form-item label="Game" prop="gameId">
        <FlatSelect class="full-width" v-model="formModel.gameId" :options="gameOptions" />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>