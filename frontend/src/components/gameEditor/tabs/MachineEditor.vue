<script setup lang="ts">
import type { Game, Machine, RecipeModifier } from '@/types/model/standalone';
import { computed, type ComputedRef, ref } from 'vue';
import { type EntityTreeService, useEntityTreeService } from '@/services/useEntityTreeService';
import { ElFormItem, type FormRules } from 'element-plus';
import { useIconStore } from '@/stores/model/iconStore';
import { elFormGameEntityNameUniqueValidator } from '@/utils/utils';
import { useEntityUsagesService } from '@/services/useEntityUsagesService';
import EntityEditor from '@/components/gameEditor/EntityEditor.vue';
import { useMachineStore } from '@/stores/model/machineStore';
import { useMachineApi } from '@/api/model/useMachineApi';
import CascaderMultiSelect from '@/components/common/input/CascaderMultiSelect.vue';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';

export interface MachineEditorProps {
  game: Game;
}

const props: MachineEditorProps = defineProps<MachineEditorProps>();

const machineStore = useMachineStore();
const recipeModifierStore = useRecipeModifierStore();
const iconStore = useIconStore();
const machineApi = useMachineApi();
const entityUsageService = useEntityUsagesService();

const machines: ComputedRef<Machine[]> = computed(() => machineStore.getByGameId(props.game.id));
const recipeModifiers: ComputedRef<RecipeModifier[]> = computed(() => recipeModifierStore.getByGameId(props.game.id));

const entityEditor = ref();

const service: EntityTreeService<Machine> = useEntityTreeService<Machine>(
  computed(() => props.game),
  machines,
  'Machine',
  () => ({
    gameId: props.game.id,
    name: '',
    iconId: 0,
    // category is set by service
    machineModifierIds: [],
  }),
  (id: number) => {
    const machine: Machine = machineStore.getById(id)!;
    return {
      id: machine.id,
      name: machine.name,
      iconId: machine.iconId,
      category: machine.category,
      machineModifierIds: [...machine.machineModifierIds],
    };
  },
  () => entityEditor.value?.validateForm(),
  () => entityEditor.value?.validateFolderForm(),
  entityUsageService.findMachineUsages,
  machineApi,
);

const formRules: ComputedRef<FormRules> = computed(() => ({
  name: [
    { required: true, message: 'Please enter a name for the machine.', trigger: 'change' },
    {
      validator: elFormGameEntityNameUniqueValidator,
      store: machineStore,
      gameId: props.game.id,
      ownId: service.state.editingEntityId.value,
      message: 'A machine with that name already exists.',
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
  <EntityEditor ref="entityEditor"
                :game="game"
                :service="service"
                entity-type="Machine"
                :form-rules="formRules"
                form-label-width="150px">
    <template #form>
      <el-form-item label="Machine modifier(s)" prop="machineModifierIds">
        <CascaderMultiSelect style="width: 100%;"
                             v-model="service.state.editingEntityModel.value.machineModifierIds!"
                             :options="recipeModifiers"
                             clearable />
      </el-form-item>
    </template>
  </EntityEditor>
</template>

<style scoped>

</style>