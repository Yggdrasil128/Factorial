<script setup lang="ts">
import EditModal from '@/components/common/EditModal.vue';
import {onBeforeRouteUpdate, type Router, useRouter} from 'vue-router';
import {computed, type ComputedRef, nextTick, ref, type Ref, watch} from 'vue';
import _ from 'lodash';
import {type GameStore, useGameStore} from '@/stores/model/gameStore';
import {
  type EntityCloneNameGeneratorService,
  useEntityCloneNameGeneratorService
} from '@/services/useEntityCloneNameGeneratorService';
import type {SaveSummary} from '@/types/model/summary';
import {ElFormItem, ElInput, ElMessage, type FormItemInstance, type FormRules} from 'element-plus';
import JsonFileUpload from '@/components/savesAndGames/JsonFileUpload.vue';
import {elFormEntityNameUniqueValidator} from '@/utils/utils';
import {type SaveStore, useSaveStore} from '@/stores/model/saveStore';
import {SaveApi, useSaveApi} from '@/api/model/useSaveApi';
import type {Game} from '@/types/model/standalone';
import FlatSelect from '@/components/common/input/FlatSelect.vue';

const router: Router = useRouter();

const gameStore: GameStore = useGameStore();
const saveStore: SaveStore = useSaveStore();
const saveApi: SaveApi = useSaveApi();
const entityCloneNameGeneratorService: EntityCloneNameGeneratorService = useEntityCloneNameGeneratorService();

type FormModel = {
  saveSummary: SaveSummary | undefined;
  saveName: string;
  gameId: number;
};

function emptyFormModel(): FormModel {
  return { saveSummary: undefined, saveName: '', gameId: 0 };
}

const formModel: Ref<FormModel> = ref(emptyFormModel());
const formItemUpload: Ref<FormItemInstance | undefined> = ref();
const hasChanges: ComputedRef<boolean> = computed(() => !_.isEqual(formModel.value, emptyFormModel()));
const isSaving: Ref<boolean> = ref(false);
const editModal: Ref<InstanceType<typeof EditModal> | undefined> = ref();

const json: Ref<string | null> = ref(null);

const formRules: ComputedRef<FormRules> = computed(() => ({
  saveSummary: [{ required: true, message: 'Please select a file to upload.', trigger: 'change' }],
  saveName: [
    { required: true, message: 'Please enter a name for the save.', trigger: 'blur' },
    {
      validator: elFormEntityNameUniqueValidator,
      entities: saveStore.getAll(),
      message: 'A save with that name already exists.',
    },
  ],
  gameId: [
    {
      required: true,
      message: 'Please select a game.',
      trigger: 'change',
      validator: (_: any, value: any, callback: (error?: Error) => void): void => {
        if (!value) {
          callback(new Error());
        }
        callback();
      },
    },
  ],
}));

function initFromRoute(): void {
  formModel.value = emptyFormModel();
  json.value = null;
  isSaving.value = false;
}

initFromRoute();
onBeforeRouteUpdate(initFromRoute);

async function submitForm(): Promise<void> {
  formModel.value.saveName = formModel.value.saveName.trim();

  if (!(await editModal.value?.validate())) {
    return;
  }

  const saveSummary: SaveSummary = _.cloneDeep(formModel.value.saveSummary)!;
  saveSummary.save.name = formModel.value.saveName;

  const game: Game | undefined = gameStore.getById(formModel.value.gameId);
  if (game) {
    saveSummary.save = {
      ...saveSummary.save,
      gameId: game.name as unknown as number,
    };
  }

  isSaving.value = true;
  await saveApi.import(saveSummary);

  await router.push({ name: 'savesAndGames' });
}

async function convertJsonToSaveSummary(json: string): Promise<SaveSummary> {
  const saveSummary: any = JSON.parse(json);
  // Some rudimentary checks to ensure that "saveSummary.save.name" and "saveSummary.save.gameId" are strings.
  if (typeof saveSummary !== 'object') throw Error('Invalid save file');
  if (typeof saveSummary.save !== 'object') throw Error('Invalid save file');
  if (typeof saveSummary.save.name !== 'string') throw Error('Invalid save file');
  if (saveSummary.save.name.trim() === '') throw Error('Invalid save file');
  if (typeof saveSummary.save.gameId !== 'string') throw Error('Invalid save file');
  if (saveSummary.save.gameId.trim() === '') throw Error('Invalid save file');
  return saveSummary as SaveSummary;
}

watch(json, () => {
  if (json.value === null) {
    formModel.value = emptyFormModel();
    return;
  }

  convertJsonToSaveSummary(json.value)
    .then((saveSummary: SaveSummary) => {
      const saveName: string = entityCloneNameGeneratorService.generateName(
        saveSummary.save.name,
        saveStore.getAll(),
      );

      const gameName: string = saveSummary.save.gameId as unknown as string;
      const game: Game | undefined = gameStore.getAll().filter((game: Game) => game.name === gameName)[0];
      const gameId: number = game?.id ?? 0;

      formModel.value.saveSummary = saveSummary;
      formModel.value.saveName = saveName;
      formModel.value.gameId = gameId;

      formItemUpload.value?.clearValidate();
    })
    .catch(() => {
      ElMessage.error({
        message: 'Invalid save file.',
      });
      void nextTick(() => (json.value = null));
    });
});
</script>

<template>
  <edit-modal
    title="Import save"
    form-label-width="120px"
    :form-model="formModel"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="isSaving"
    @submit="submitForm"
    ref="editModal"
  >
    <template #description> Upload a file to import it as a save.</template>

    <template #form>
      <el-form-item label="Upload file" prop="saveSummary" ref="formItemUpload">
        <JsonFileUpload v-model="json" />
      </el-form-item>

      <el-form-item label="Name" prop="saveName">
        <el-input
          v-model="formModel.saveName"
          :disabled="!formModel.saveSummary"
          :placeholder="!formModel.saveSummary ? 'Select file to upload first' : ''"
        />
      </el-form-item>

      <el-form-item label="Game" prop="gameId">
        <FlatSelect
          class="full-width"
          :disabled="!formModel.saveSummary"
          :placeholder="!formModel.saveSummary ? 'Select file to upload first' : 'Select game'"
          v-model="formModel.gameId!"
          :options="gameStore.getAll()"
        />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>
