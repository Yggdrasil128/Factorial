<script setup lang="ts">
import type {Game, Item} from '@/types/model/standalone';
import {type ItemStore, useItemStore} from '@/stores/model/itemStore';
import {computed, type ComputedRef, type Ref, ref} from 'vue';
import {type EntityTreeService, useEntityTreeService} from '@/services/useEntityTreeService';
import {type FormRules} from 'element-plus';
import {type IconStore, useIconStore} from '@/stores/model/iconStore';
import {elFormEntityNameUniqueValidator} from '@/utils/utils';
import {ItemApi, useItemApi} from '@/api/model/useItemApi';
import {type EntityUsagesService, useEntityUsagesService} from '@/services/useEntityUsagesService';
import EntityEditor from '@/components/gameEditor/EntityEditor.vue';

export interface ItemEditorProps {
  game: Game;
}

const props: ItemEditorProps = defineProps<ItemEditorProps>();

const itemStore: ItemStore = useItemStore();
const iconStore: IconStore = useIconStore();
const itemApi: ItemApi = useItemApi();
const entityUsageService: EntityUsagesService = useEntityUsagesService();

const items: ComputedRef<Item[]> = computed(() => itemStore.getByGameId(props.game.id));

const entityEditor: Ref<InstanceType<typeof EntityEditor> | undefined> = ref();

const service: EntityTreeService<Item> = useEntityTreeService<Item>(
    computed(() => props.game),
    items,
    'Item',
    () => ({
      gameId: props.game.id,
      name: '',
      description: '',
      iconId: 0,
      // category is set by service
    }),
    (id: number) => {
      const item: Item = itemStore.getById(id)!;
      return {
        id: item.id,
        name: item.name,
        description: item.description,
        iconId: item.iconId,
        category: item.category,
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
    entityUsageService.findItemUsages,
    itemApi,
);

const formRules: ComputedRef<FormRules> = computed(() => ({
  name: [
    {required: true, message: 'Please enter a name for the item.', trigger: 'change'},
    {
      validator: elFormEntityNameUniqueValidator,
      entities: itemStore.getByGameId(props.game.id),
      ownId: service.state.editingEntityId.value,
      message: 'An item with that name already exists.',
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
  <EntityEditor ref="entityEditor" :game="game" :service="service" entity-type="Item" :form-rules="formRules"/>
</template>

<style scoped>

</style>