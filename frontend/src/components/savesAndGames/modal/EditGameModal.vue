<script setup lang="ts">
import { onBeforeRouteUpdate, type RouteLocationNormalizedLoadedGeneric, useRoute, useRouter } from 'vue-router';
import { useGameStore } from '@/stores/model/gameStore';
import { computed, type Ref, ref, watch } from 'vue';
import type { Game, Icon } from '@/types/model/standalone';
import { useIconApi } from '@/api/model/useIconApi';
import _ from 'lodash';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import EditModal from '@/components/common/EditModal.vue';
import { ElFormItem, ElInput } from 'element-plus';
import FlatSelect from '@/components/common/input/FlatSelect.vue';
import { elFormEntityNameUniqueValidator } from '@/utils/utils';
import { useGameApi } from '@/api/model/useGameApi';

const router = useRouter();
const route = useRoute();

const gameStore = useGameStore();

const gameApi = useGameApi();
const iconApi = useIconApi();

const game: Ref<Partial<Game>> = ref({});
const original: Ref<Partial<Game>> = ref({});
const hasChanges = computed(() => !_.isEqual(game.value, original.value));
const isSaving: Ref<boolean> = ref(false);
const editModal = ref();

const formRules = computed(() => ({
  name: [
    { required: true, message: 'Please enter a name for the game.', trigger: 'blur' },
    {
      validator: elFormEntityNameUniqueValidator,
      entities: gameStore.getAll(),
      ownId: game.value.id,
      message: 'A game with that name already exists.',
    },
  ],
}));

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  if (route.name === 'newGame') {
    game.value = {
      name: '',
      description: '',
      iconId: 0,
    };
  } else {
    const gameId: number = Number(route.params.gameId);
    const currentGame: Game | undefined = gameStore.getById(gameId);
    if (!currentGame) {
      console.error('Game with id ' + gameId + ' not found');
      router.push({ name: 'savesAndGames' });
      return;
    }
    game.value = {
      id: gameId,
      name: currentGame.name,
      description: currentGame.description,
      iconId: currentGame.iconId,
    };
  }
  original.value = _.clone(game.value);
  isSaving.value = false;
}

initFromRoute(route);
onBeforeRouteUpdate(initFromRoute);

async function submitForm(): Promise<void> {
  game.value.name = game.value.name?.trim();
  game.value.description = game.value.description?.trim();

  if (!(await editModal.value.validate())) {
    return;
  }

  isSaving.value = true;

  if (route.name === 'newGame') {
    await gameApi.create(game.value);
  } else {
    await gameApi.edit(game.value);
  }

  await router.push({ name: 'savesAndGames' });
}

const iconOptions: Ref<Icon[]> = ref([]);

watch(() => game.value.id, () => {
  if (!game.value.id) {
    iconOptions.value = [];
    return;
  }
  iconApi.retrieveAllByGameId(game.value.id)
    .then((icons: Icon[]) => {
      iconOptions.value = icons;
    });
}, { immediate: true });
</script>

<template>
  <edit-modal
    :title="route.name === 'newGame' ? 'New game' : 'Edit game'"
    form-label-width="120px"
    :form-model="game"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="isSaving"
    @submit="submitForm"
    ref="editModal"
  >
    <template #description>
      A game contains information about all available items, machines, recipes, etc.<br>
      Multiple saves can share the same game, and you can migrate saves from one game to another,
      for example, if the game updates and you want to use new recipes in your save.
    </template>

    <template #form>
      <el-form-item label="Name" prop="name">
        <el-input v-model="game.name" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input v-model="game.description" type="textarea" autosize />
      </el-form-item>

      <el-form-item v-if="route.name !== 'newGame'" label="Icon" prop="iconId">
        <FlatSelect class="full-width" v-model="game.iconId!" :options="iconOptions"
                    is-icon-entity clearable />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>