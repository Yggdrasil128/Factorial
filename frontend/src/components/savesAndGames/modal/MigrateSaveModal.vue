<script setup lang="ts">
import EditModal from '@/components/common/EditModal.vue';
import {
  onBeforeRouteUpdate,
  type RouteLocationNormalizedLoadedGeneric,
  type Router,
  useRoute,
  useRouter
} from 'vue-router';
import {type SaveStore, useSaveStore} from '@/stores/model/saveStore';
import {type GameStore, useGameStore} from '@/stores/model/gameStore';
import {SaveApi, useSaveApi} from '@/api/model/useSaveApi';
import {computed, type ComputedRef, ref, type Ref} from 'vue';
import type {Game, Save} from '@/types/model/standalone';
import {ElFormItem, ElInput, type FormRules} from 'element-plus';
import FlatSelect from '@/components/common/input/FlatSelect.vue';
import {
  type EntityCloneNameGeneratorService,
  useEntityCloneNameGeneratorService
} from '@/services/useEntityCloneNameGeneratorService';
import _ from 'lodash';
import {elFormEntityNameUniqueValidator} from '@/utils/utils';

const router: Router = useRouter();
const route: RouteLocationNormalizedLoadedGeneric = useRoute();

const saveStore: SaveStore = useSaveStore();
const gameStore: GameStore = useGameStore();
const saveApi: SaveApi = useSaveApi();
const entityCloneNameGeneratorService: EntityCloneNameGeneratorService = useEntityCloneNameGeneratorService();

type FormModel = {
  saveId: number;
  saveName: string;
  gameId: number;
}

const formModel: Ref<FormModel> = ref({ saveId: 0, saveName: '', gameId: 0 });
const original: Ref<FormModel> = ref(formModel.value);
const hasChanges: ComputedRef<boolean> = computed(() => !_.isEqual(formModel.value, original.value));
const isSaving: Ref<boolean> = ref(false);
const editModal: Ref<InstanceType<typeof EditModal> | undefined> = ref();

const formRules: ComputedRef<FormRules> = computed(() => ({
  gameId: [{
    required: true, message: 'Please select a game.', trigger: 'change',
    validator: (_: any, value: any, callback: (error?: Error) => void): void => {
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
}));

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  const saveId: number = Number(route.params.saveId);
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
  if (!(await editModal.value?.validate())) {
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
  return gameStore.getAll().filter((game: Game) => game.id !== save.value?.gameId);
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