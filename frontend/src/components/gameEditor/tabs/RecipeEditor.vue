ItemEditor.vue
<script setup lang="ts">
import type { Game, Recipe } from '@/types/model/standalone';
import { computed, type ComputedRef, ref } from 'vue';
import { type EntityTreeService, useEntityTreeService } from '@/services/useEntityTreeService';
import { type FormRules } from 'element-plus';
import { useIconStore } from '@/stores/model/iconStore';
import { elFormGameEntityNameUniqueValidator } from '@/utils/utils';
import { useEntityUsagesService } from '@/services/useEntityUsagesService';
import EntityEditor from '@/components/gameEditor/EntityEditor.vue';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useRecipeApi } from '@/api/useRecipeApi';

export interface RecipeEditorProps {
  game: Game;
}

const props: RecipeEditorProps = defineProps<RecipeEditorProps>();

const recipeStore = useRecipeStore();
const iconStore = useIconStore();
const recipeApi = useRecipeApi();
const entityUsageService = useEntityUsagesService();

const recipes: ComputedRef<Recipe[]> = computed(() => recipeStore.getByGameId(props.game.id));

const entityEditor = ref();

const service: EntityTreeService<Recipe> = useEntityTreeService(
  computed(() => props.game),
  recipes,
  'Recipe',
  () => ({
    gameId: props.game.id,
    name: '',
    description: '',
    iconId: 0,
    // category is set by service
  }),
  (id: number) => {
    const recipe: Recipe = recipeStore.getById(id)!;
    return {
      id: recipe.id,
      name: recipe.name,
      description: recipe.description,
      iconId: recipe.iconId,
      category: recipe.category,
    };
  },
  () => entityEditor.value?.validateForm(),
  () => entityEditor.value?.validateFolderForm(),
  entityUsageService.findRecipeUsages,
  recipeApi,
);

const formRules: ComputedRef<FormRules> = computed(() => ({
  name: [
    { required: true, message: 'Please enter a name for the recipe.', trigger: 'change' },
    {
      validator: elFormGameEntityNameUniqueValidator,
      store: recipeStore,
      gameId: props.game.id,
      ownId: service.state.editingEntityId.value,
      message: 'A recipe with that name already exists.',
    },
    ...(service.state.selectedIconOption.value !== 'new' ? [] : [{
      validator: elFormGameEntityNameUniqueValidator,
      store: iconStore,
      gameId: props.game.id,
      message: 'An icon with that name already exists.',
    }]),
  ],
}));

</script>

<template>
  <EntityEditor ref="entityEditor" :game="game" :service="service" entity-type="Recipe" :form-rules="formRules" />
</template>

<style scoped>

</style>