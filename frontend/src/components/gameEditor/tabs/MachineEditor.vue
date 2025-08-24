<script setup lang="ts">
import type {Game, Machine, RecipeModifier} from '@/types/model/standalone';
import {computed, type ComputedRef, type Ref, ref} from 'vue';
import {type EntityTreeService, useEntityTreeService} from '@/services/useEntityTreeService';
import {ElFormItem, type FormRules} from 'element-plus';
import {type IconStore, useIconStore} from '@/stores/model/iconStore';
import {elFormEntityNameUniqueValidator} from '@/utils/utils';
import {type EntityUsagesService, useEntityUsagesService} from '@/services/useEntityUsagesService';
import EntityEditor from '@/components/gameEditor/EntityEditor.vue';
import {type MachineStore, useMachineStore} from '@/stores/model/machineStore';
import {MachineApi, useMachineApi} from '@/api/model/useMachineApi';
import CascaderMultiSelect from '@/components/common/input/CascaderMultiSelect.vue';
import {type RecipeModifierStore, useRecipeModifierStore} from '@/stores/model/recipeModifierStore';

export interface MachineEditorProps {
  game: Game;
}

const props: MachineEditorProps = defineProps<MachineEditorProps>();

const machineStore: MachineStore = useMachineStore();
const recipeModifierStore: RecipeModifierStore = useRecipeModifierStore();
const iconStore: IconStore = useIconStore();
const machineApi: MachineApi = useMachineApi();
const entityUsageService: EntityUsagesService = useEntityUsagesService();

const machines: ComputedRef<Machine[]> = computed(() => machineStore.getByGameId(props.game.id));
const recipeModifiers: ComputedRef<RecipeModifier[]> = computed(() => recipeModifierStore.getByGameId(props.game.id));

const entityEditor: Ref<InstanceType<typeof EntityEditor> | undefined> = ref();

const service: EntityTreeService<Machine> = useEntityTreeService<Machine>(
    computed(() => props.game),
    machines,
    'Machine',
    () => ({
      gameId: props.game.id,
      name: '',
      description: '',
      iconId: 0,
      // category is set by service
      machineModifierIds: [],
    }),
    (id: number) => {
      const machine: Machine = machineStore.getById(id)!;
      return {
        id: machine.id,
        name: machine.name,
        description: machine.description,
        iconId: machine.iconId,
        category: machine.category,
        machineModifierIds: [...machine.machineModifierIds],
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
    entityUsageService.findMachineUsages,
    machineApi,
);

const formRules: ComputedRef<FormRules> = computed(() => ({
  name: [
    {required: true, message: 'Please enter a name for the machine.', trigger: 'change'},
    {
      validator: elFormEntityNameUniqueValidator,
      entities: machineStore.getByGameId(props.game.id),
      ownId: service.state.editingEntityId.value,
      message: 'A machine with that name already exists.',
    },
    ...(service.state.selectedIconOption.value !== 'new' ? [] : [{
      validator: elFormEntityNameUniqueValidator,
      entities: iconStore.getByGameId(props.game.id),
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
                             placeholder=" "
                             clearable/>
      </el-form-item>
    </template>
  </EntityEditor>
</template>

<style scoped>

</style>