<script setup lang="ts">
import type { Game, Icon } from '@/types/model/standalone';
import { computed, type ComputedRef, ref } from 'vue';
import { type EntityTreeService, useEntityTreeService } from '@/services/useEntityTreeService';
import { type FormRules } from 'element-plus';
import { useIconStore } from '@/stores/model/iconStore';
import { elFormGameEntityNameUniqueValidator } from '@/utils/utils';
import { useEntityUsagesService } from '@/services/useEntityUsagesService';
import EntityEditor from '@/components/gameEditor/EntityEditor.vue';
import { useIconApi } from '@/api/useIconApi';

export interface IconEditorProps {
  game: Game;
}

const props: IconEditorProps = defineProps<IconEditorProps>();

const iconStore = useIconStore();
const iconApi = useIconApi();
const entityUsageService = useEntityUsagesService();

const icons: ComputedRef<Icon[]> = computed(() => iconStore.getByGameId(props.game.id));

const entityEditor = ref();

const service: EntityTreeService<Icon> = useEntityTreeService(
  computed(() => props.game),
  icons,
  'Icon',
  () => ({
    gameId: props.game.id,
    name: '',
    // category is set by service
  }),
  (id: number) => {
    const icon: Icon = iconStore.getById(id)!;
    return {
      id: icon.id,
      name: icon.name,
      category: icon.category,
    };
  },
  () => entityEditor.value?.validateForm(),
  () => entityEditor.value?.validateFolderForm(),
  entityUsageService.findIconUsages,
  iconApi,
);

const formRules: ComputedRef<FormRules> = computed(() => ({
  name: [
    { required: true, message: 'Please enter a name for the icon.', trigger: 'change' },
    {
      validator: elFormGameEntityNameUniqueValidator,
      store: iconStore,
      gameId: props.game.id,
      ownId: service.state.editingEntityId.value,
      message: 'An icon with that name already exists.',
    },
  ],
}));

</script>

<template>
  <EntityEditor ref="entityEditor" :game="game" :service="service" entity-type="Icon" :form-rules="formRules" />
</template>

<style scoped>

</style>