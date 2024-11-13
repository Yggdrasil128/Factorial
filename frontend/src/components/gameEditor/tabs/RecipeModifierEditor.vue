<script setup lang="ts">
import type { Game, RecipeModifier } from '@/types/model/standalone';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import { useIconStore } from '@/stores/model/iconStore';
import { useEntityUsagesService } from '@/services/useEntityUsagesService';
import { computed, type ComputedRef, ref } from 'vue';
import { type EntityTreeService, useEntityTreeService } from '@/services/useEntityTreeService';
import { ElFormItem, ElInput, type FormRules } from 'element-plus';
import { elFormGameEntityNameUniqueValidator } from '@/utils/utils';
import EntityEditor from '@/components/gameEditor/EntityEditor.vue';
import { useRecipeModifierApi } from '@/api/useRecipeModifierApi';
import { elFormFractionValidator } from '@/utils/fractionUtils';
import type { RuleItem } from 'async-validator/dist-types/interface';

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

const service: EntityTreeService<RecipeModifier> = useEntityTreeService<RecipeModifier>(
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
  durationMultiplier: [{
    required: true, message: 'Please enter a valid duration multiplier', trigger: 'blur',
    validator: elFormFractionValidator, allowZero: false, allowNegative: false,
  } as RuleItem],
  inputQuantityMultiplier: [{
    required: true, message: 'Please enter a valid input quantity multiplier', trigger: 'blur',
    validator: elFormFractionValidator, allowZero: true, allowNegative: false,
  } as RuleItem],
  outputQuantityMultiplier: [{
    required: true, message: 'Please enter a valid output quantity multiplier', trigger: 'blur',
    validator: elFormFractionValidator, allowZero: false, allowNegative: false,
  } as RuleItem],
}));

</script>

<template>
  <EntityEditor ref="entityEditor" :game="game" :service="service" entity-type="Recipe modifier"
                :form-rules="formRules">
    <template #form>
      <h3>Multipliers</h3>
      <el-form-item label="Duration" prop="durationMultiplier">
        <el-input v-model="service.state.editingEntityModel.value.durationMultiplier" />
      </el-form-item>
      <el-form-item label="Input Quantity" prop="inputQuantityMultiplier">
        <el-input v-model="service.state.editingEntityModel.value.inputQuantityMultiplier" />
      </el-form-item>
      <el-form-item label="Output Quantity" prop="outputQuantityMultiplier">
        <el-input v-model="service.state.editingEntityModel.value.outputQuantityMultiplier" />
      </el-form-item>
    </template>
  </EntityEditor>
</template>

<style scoped>

</style>