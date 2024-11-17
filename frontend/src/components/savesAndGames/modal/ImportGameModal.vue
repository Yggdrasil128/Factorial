<script setup lang="ts">
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import EditModal from '@/components/common/EditModal.vue';
import { onBeforeRouteUpdate, useRouter } from 'vue-router';
import { computed, nextTick, ref, type Ref, watch } from 'vue';
import _ from 'lodash';
import { useGameStore } from '@/stores/model/gameStore';
import { useGameApi } from '@/api/model/useGameApi';
import { useEntityCloneNameGeneratorService } from '@/services/useEntityCloneNameGeneratorService';
import type { GameSummary } from '@/types/model/summary';
import { ElFormItem, ElInput, ElMessage, type FormItemInstance } from 'element-plus';
import JsonFileUpload from '@/components/savesAndGames/JsonFileUpload.vue';
import { elFormEntityNameUniqueValidator } from '@/utils/utils';

const router = useRouter();

const gameStore = useGameStore();
const gameApi = useGameApi();
const entityCloneNameGeneratorService = useEntityCloneNameGeneratorService();

type FormModel = {
  gameSummary: GameSummary | undefined;
  gameName: string;
};

function emptyFormModel(): FormModel {
  return { gameSummary: undefined, gameName: '' };
}

const formModel: Ref<FormModel> = ref(emptyFormModel());
const formItemUpload = ref<FormItemInstance>();
const hasChanges = computed(() => !_.isEqual(formModel.value, emptyFormModel()));
const isSaving: Ref<boolean> = ref(false);
const editModal = ref();

const json: Ref<string | null> = ref(null);

const formRules = computed(() => ({
  gameSummary: [{ required: true, message: 'Please select a file to upload.', trigger: 'change' }],
  gameName: [
    { required: true, message: 'Please enter a name for the game.', trigger: 'blur' },
    {
      validator: elFormEntityNameUniqueValidator,
      entities: gameStore.getAll(),
      message: 'A game with that name already exists.',
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
  formModel.value.gameName = formModel.value.gameName.trim();

  if (!(await editModal.value.validate())) {
    return;
  }

  const gameSummary: GameSummary = _.cloneDeep(formModel.value.gameSummary)!;
  gameSummary.game.name = formModel.value.gameName;

  isSaving.value = true;
  await gameApi.import(gameSummary);

  await router.push({ name: 'savesAndGames' });
}

async function convertJsonToGameSummary(json: string): Promise<GameSummary> {
  const gameSummary: any = JSON.parse(json);
  // Some rudimentary checks to ensure that "gameSummary.game.name" is a string.
  if (typeof gameSummary !== 'object') throw Error('Invalid game file');
  if (typeof gameSummary.game !== 'object') throw Error('Invalid game file');
  if (typeof gameSummary.game.name !== 'string') throw Error('Invalid game file');
  if (gameSummary.game.name.trim() === '') throw Error('Invalid game file');
  return gameSummary as GameSummary;
}

watch(json, () => {
  if (json.value === null) {
    formModel.value = emptyFormModel();
    return;
  }

  convertJsonToGameSummary(json.value)
    .then((gameSummary: GameSummary) => {
      const gameName = entityCloneNameGeneratorService.generateName(
        gameSummary.game.name,
        gameStore.getAll(),
      );

      formModel.value.gameSummary = gameSummary;
      formModel.value.gameName = gameName;

      formItemUpload.value?.clearValidate();
    })
    .catch(() => {
      ElMessage.error({
        message: 'Invalid game file.',
      });
      void nextTick(() => (json.value = null));
    });
});
</script>

<template>
  <edit-modal
    title="Import game"
    form-label-width="120px"
    :form-model="formModel"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="isSaving"
    @submit="submitForm"
    ref="editModal"
  >
    <template #description> Upload a file to import it as a game.</template>

    <template #form>
      <el-form-item label="Upload file" prop="gameSummary" ref="formItemUpload">
        <JsonFileUpload v-model="json" />
      </el-form-item>

      <el-form-item label="Name" prop="gameName">
        <el-input
          v-model="formModel.gameName"
          :disabled="!formModel.gameSummary"
          :placeholder="!formModel.gameSummary ? 'Select file to upload first' : ''"
        />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>
