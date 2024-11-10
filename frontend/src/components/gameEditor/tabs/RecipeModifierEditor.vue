<script setup lang="ts">
import type { Game, Item, RecipeModifier } from '@/types/model/standalone';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import { useIconStore } from '@/stores/model/iconStore';
import { useEntityUsagesService } from '@/services/useEntityUsagesService';
import { computed, type ComputedRef, ref } from 'vue';
import { type EntityTreeService, useEntityTreeService } from '@/services/useEntityTreeService';
import type { FormRules } from 'element-plus';
import { elFormGameEntityNameUniqueValidator } from '@/utils/utils';
import EntityEditor from '@/components/gameEditor/EntityEditor.vue';
import { useRecipeModifierApi } from '@/api/useRecipeModifierApi';

export interface RecipeModifierEditorProps {
  game: Game;
}

const props: RecipeModifierEditorProps = defineProps<RecipeModifierEditorProps>();

const recipeModifierStore = useRecipeModifierStore();
const iconStore = useIconStore();
const recipeModifierApi = useRecipeModifierApi();
const entityUsageService = useEntityUsagesService();

const recipeModifiers: ComputedRef<RecipeModifier[]> = computed(() => recipeModifierStore.getByGameId(props.game.id));

const entityEditor = ref();

const service: EntityTreeService<Item> = useEntityTreeService(
  computed(() => props.game),
  recipeModifiers,
  'Recipe modifier',
  () => ({
    gameId: props.game.id,
    name: '',
    description: '',
    iconId: 0,
    // category is set by service
    durationMultiplier: '1',
    inputQuantityMultiplier: '1',
    outputQuantityMultiplier: '1',
  }),
  (id: number) => {
    const recipeModifier: RecipeModifier = recipeModifierStore.getById(id)!;
    return {
      id: recipeModifier.id,
      name: recipeModifier.name,
      description: recipeModifier.description,
      iconId: recipeModifier.iconId,
      category: recipeModifier.category,
      durationMultiplier: recipeModifier.durationMultiplier,
      inputQuantityMultiplier: recipeModifier.inputQuantityMultiplier,
      outputQuantityMultiplier: recipeModifier.outputQuantityMultiplier,
    };
  },
  () => entityEditor.value?.validateForm(),
  () => entityEditor.value?.validateFolderForm(),
  entityUsageService.findRecipeModifierUsages,
  recipeModifierApi,
);

const formRules: ComputedRef<FormRules> = computed(() => ({
  name: [
    { required: true, message: 'Please enter a name for the recipe modifier.', trigger: 'change' },
    {
      validator: elFormGameEntityNameUniqueValidator,
      store: recipeModifierStore,
      gameId: props.game.id,
      ownId: service.state.editingEntityId.value,
      message: 'A recipe modifier with that name already exists.',
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
  <EntityEditor ref="entityEditor" :game="game" :service="service" entity-type="Recipe modifier"
                :form-rules="formRules" />
</template>

<style scoped>

</style>