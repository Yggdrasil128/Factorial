<script setup lang="ts">
import type {Game, RecipeModifier} from '@/types/model/standalone';
import {type RecipeModifierStore, useRecipeModifierStore} from '@/stores/model/recipeModifierStore';
import {type IconStore, useIconStore} from '@/stores/model/iconStore';
import {type EntityUsagesService, useEntityUsagesService} from '@/services/useEntityUsagesService';
import {computed, type ComputedRef, type Ref, ref} from 'vue';
import {type EntityTreeService, useEntityTreeService} from '@/services/useEntityTreeService';
import {ElFormItem, type FormRules} from 'element-plus';
import {elFormEntityNameUniqueValidator} from '@/utils/utils';
import EntityEditor from '@/components/gameEditor/EntityEditor.vue';
import {RecipeModifierApi, useRecipeModifierApi} from '@/api/model/useRecipeModifierApi';
import {elFormFractionValidator} from '@/utils/fractionUtils';
import type {RuleItem} from 'async-validator/dist-types/interface';
import FractionInput from '@/components/common/input/FractionInput.vue';

export interface RecipeModifierEditorProps {
  game: Game;
}

const props: RecipeModifierEditorProps = defineProps<RecipeModifierEditorProps>();

const recipeModifierStore: RecipeModifierStore = useRecipeModifierStore();
const iconStore: IconStore = useIconStore();
const recipeModifierApi: RecipeModifierApi = useRecipeModifierApi();
const entityUsageService: EntityUsagesService = useEntityUsagesService();

const recipeModifiers: ComputedRef<RecipeModifier[]> = computed(() => recipeModifierStore.getByGameId(props.game.id));

const entityEditor: Ref<InstanceType<typeof EntityEditor> | undefined> = ref();

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
    async (): Promise<boolean> => {
      if (!entityEditor.value) return false;
      return await entityEditor.value.validateForm();
    },
    async (): Promise<boolean> => {
      if (!entityEditor.value) return false;
      return await entityEditor.value.validateFolderForm();
    },
    entityUsageService.findRecipeModifierUsages,
    recipeModifierApi,
);

const formRules: ComputedRef<FormRules> = computed(() => ({
  name: [
    {required: true, message: 'Please enter a name for the recipe modifier.', trigger: 'change'},
    {
      validator: elFormEntityNameUniqueValidator,
      entities: recipeModifierStore.getByGameId(props.game.id),
      ownId: service.state.editingEntityId.value,
      message: 'A recipe modifier with that name already exists.',
    },
    ...(service.state.selectedIconOption.value !== 'new' ? [] : [{
      validator: elFormEntityNameUniqueValidator,
      entities: iconStore.getByGameId(props.game.id),
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
        <fraction-input v-model:fraction="service.state.editingEntityModel.value.durationMultiplier"/>
      </el-form-item>
      <el-form-item label="Input Quantity" prop="inputQuantityMultiplier">
        <fraction-input v-model:fraction="service.state.editingEntityModel.value.inputQuantityMultiplier"/>
      </el-form-item>
      <el-form-item label="Output Quantity" prop="outputQuantityMultiplier">
        <fraction-input v-model:fraction="service.state.editingEntityModel.value.outputQuantityMultiplier"/>
      </el-form-item>
    </template>
  </EntityEditor>
</template>

<style scoped>

</style>