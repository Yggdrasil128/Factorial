ItemEditor.vue
<script setup lang="ts">
import type { Game, Item, Machine, Recipe, RecipeModifier } from '@/types/model/standalone';
import { computed, type ComputedRef, type Ref, ref, watch } from 'vue';
import { type EntityTreeService, useEntityTreeService } from '@/services/useEntityTreeService';
import { ElFormItem, ElInput, type FormRules } from 'element-plus';
import { useIconStore } from '@/stores/model/iconStore';
import { elFormGameEntityNameUniqueValidator } from '@/utils/utils';
import { useEntityUsagesService } from '@/services/useEntityUsagesService';
import EntityEditor from '@/components/gameEditor/EntityEditor.vue';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useRecipeApi } from '@/api/useRecipeApi';
import { elFormFractionValidator, isValidFraction, ParsedFraction } from '@/utils/fractionUtils';
import ItemQuantityListEditor from '@/components/gameEditor/ItemQuantityListEditor.vue';
import { useMachineStore } from '@/stores/model/machineStore';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import CascaderMultiSelect from '@/components/common/input/CascaderMultiSelect.vue';
import type { RuleItem } from 'async-validator/dist-types/interface';
import _ from 'lodash';
import type { ItemQuantity } from '@/types/model/basic';
import { useItemStore } from '@/stores/model/itemStore';

export interface RecipeEditorProps {
  game: Game;
}

const props: RecipeEditorProps = defineProps<RecipeEditorProps>();

const recipeStore = useRecipeStore();
const machineStore = useMachineStore();
const recipeModifierStore = useRecipeModifierStore();
const itemStore = useItemStore();
const iconStore = useIconStore();
const recipeApi = useRecipeApi();
const entityUsageService = useEntityUsagesService();

const recipes: ComputedRef<Recipe[]> = computed(() => recipeStore.getByGameId(props.game.id));
const machines: ComputedRef<Machine[]> = computed(() => machineStore.getByGameId(props.game.id));
const recipeModifiers: ComputedRef<RecipeModifier[]> = computed(() => recipeModifierStore.getByGameId(props.game.id));

const entityEditor = ref();

const service: EntityTreeService<Recipe> = useEntityTreeService<Recipe>(
  computed(() => props.game),
  recipes,
  'Recipe',
  () => {
    speedInputModel.value = '1';
    return {
      gameId: props.game.id,
      name: '',
      description: '',
      iconId: 0,
      // category is set by service
      duration: convertSpeedToSecondsPerCycle(ParsedFraction.ONE, selectedSpeedUnit.value).toFraction(),
      ingredients: [],
      products: [],
      applicableMachineIds: [],
      applicableModifierIds: [],
    };
  },
  (id: number) => {
    const recipe: Recipe = recipeStore.getById(id)!;

    speedInputModel.value = convertSpeedFromSecondsPerCycle(
      ParsedFraction.of(recipe.duration), selectedSpeedUnit.value,
    ).toFraction();

    return {
      id: recipe.id,
      name: recipe.name,
      description: recipe.description,
      iconId: recipe.iconId,
      category: recipe.category,
      duration: recipe.duration,
      ingredients: _.cloneDeep(recipe.ingredients),
      products: _.cloneDeep(recipe.products),
      applicableMachineIds: _.cloneDeep(recipe.applicableMachineIds),
      applicableModifierIds: _.cloneDeep(recipe.applicableModifierIds),
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
  duration: [{
    required: true, message: 'Please enter a valid speed.', trigger: 'blur',
    validator: elFormFractionValidator, allowZero: false, allowNegative: false,
  } as RuleItem],
  applicableMachineIds: [
    { required: true, message: 'Please select at least one machine.', trigger: 'change' },
  ],
}));

type SpeedUnit = 'secondsPerCycle' | 'minutesPerCycle' | 'cyclesPerSecond' | 'cyclesPerMinute';

function convertSpeedToSecondsPerCycle(speed: ParsedFraction, unit: SpeedUnit): ParsedFraction {
  if (unit === 'secondsPerCycle') return speed;
  else if (unit === 'minutesPerCycle') return speed.multiply(new ParsedFraction(60n));
  else if (unit === 'cyclesPerSecond') return speed.inverse();
  else return new ParsedFraction(60n).divide(speed);
}

function convertSpeedFromSecondsPerCycle(speed: ParsedFraction, unit: SpeedUnit): ParsedFraction {
  if (unit === 'secondsPerCycle') return speed;
  else if (unit === 'minutesPerCycle') return speed.divide(new ParsedFraction(60n));
  else if (unit === 'cyclesPerSecond') return speed.inverse();
  else return new ParsedFraction(60n).divide(speed);
}

const speedInputModel: Ref<string> = ref('');
const selectedSpeedUnit: Ref<SpeedUnit> = ref('secondsPerCycle');

watch(speedInputModel, () => updateRecipeDuration());
watch(selectedSpeedUnit, () => updateRecipeDuration());

function updateRecipeDuration(): void {
  if (!isValidFraction(speedInputModel.value, { allowZero: false, allowNegative: false })) {
    service.state.editingEntityModel.value.duration = '';
    return;
  }
  let speed: ParsedFraction = ParsedFraction.of(speedInputModel.value);
  speed = convertSpeedToSecondsPerCycle(speed, selectedSpeedUnit.value);
  service.state.editingEntityModel.value.duration = speed.toFraction();
}

watch(() => service.state.editingEntityModel.value.products, () => {
  if (service.state.editingEntityModel.value.name !== '') return;
  const products: ItemQuantity[] = service.state.editingEntityModel.value.products!;
  if (products.length === 0 || products[0].itemId === 0) return;
  const item: Item | undefined = itemStore.getById(products[0].itemId);
  if (!item) return;
  service.state.editingEntityModel.value.name = item.name;
}, { deep: true });

</script>

<template>
  <EntityEditor ref="entityEditor"
                :game="game"
                :service="service"
                entity-type="Recipe"
                :form-rules="formRules"
                form-label-width="150px">
    <template #form>
      <h3>Recipe</h3>
      <el-form-item label="Speed" prop="duration">
        <div style="width: 100%; display: flex; gap: 4px;">
          <el-input style="flex: 1 1 auto;" v-model="speedInputModel" />
          <el-select style="flex: 0 0 180px;" v-model="selectedSpeedUnit">
            <el-option value="secondsPerCycle" label="seconds / cycle" />
            <el-option value="minutesPerCycle" label="minutes / cycle" />
            <el-option value="cyclesPerSecond" label="cycles / second" />
            <el-option value="cyclesPerMinute" label="cycles / minute" />
          </el-select>
        </div>
      </el-form-item>
      <el-form-item label="Ingredients" prop="ingredients">
        <ItemQuantityListEditor v-model="service.state.editingEntityModel.value.ingredients!"
                                type="ingredients"
                                :game-id="game.id" />
      </el-form-item>
      <el-form-item label="Products" prop="products">
        <ItemQuantityListEditor v-model="service.state.editingEntityModel.value.products!"
                                type="products"
                                :game-id="game.id" />
      </el-form-item>

      <el-form-item label="Applicable machines" prop="applicableMachineIds">
        <CascaderMultiSelect style="width: 100%;"
                             v-model="service.state.editingEntityModel.value.applicableMachineIds!"
                             :options="machines"
                             clearable />
      </el-form-item>
      <el-form-item label="Applicable modifiers" prop="applicableModifierIds">
        <CascaderMultiSelect style="width: 100%;"
                             v-model="service.state.editingEntityModel.value.applicableModifierIds!"
                             :options="recipeModifiers"
                             clearable />
      </el-form-item>
    </template>
  </EntityEditor>
</template>

<style scoped>

</style>