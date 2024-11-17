<script setup lang="ts">
import type { Game, Icon } from '@/types/model/standalone';
import { computed, type ComputedRef, ref } from 'vue';
import { type EntityTreeService, useEntityTreeService } from '@/services/useEntityTreeService';
import { ElFormItem, type FormRules } from 'element-plus';
import { useIconStore } from '@/stores/model/iconStore';
import { elFormEntityNameUniqueValidator } from '@/utils/utils';
import { useEntityUsagesService } from '@/services/useEntityUsagesService';
import EntityEditor from '@/components/gameEditor/EntityEditor.vue';
import { useIconApi } from '@/api/model/useIconApi';
import IconImg from '@/components/common/IconImg.vue';
import IconUpload from '@/components/gameEditor/IconUpload.vue';

export interface IconEditorProps {
  game: Game;
}

const props: IconEditorProps = defineProps<IconEditorProps>();

const iconStore = useIconStore();
const iconApi = useIconApi();
const entityUsageService = useEntityUsagesService();

const icons: ComputedRef<Icon[]> = computed(() => iconStore.getByGameId(props.game.id));

const entityEditor = ref();

const service: EntityTreeService<Icon> = useEntityTreeService<Icon>(
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
      validator: elFormEntityNameUniqueValidator,
      entities: iconStore.getByGameId(props.game.id),
      ownId: service.state.editingEntityId.value,
      message: 'An icon with that name already exists.',
    },
  ],
  ...(service.state.editingEntityId.value !== 0 ? {} : {
    imageData: [{
      required: true, message: 'Please select an image file to upload.',
      validator(_, __, callback) {
        if (!service.state.editingEntityIconDataBase64.value) {
          callback(new Error());
        } else {
          callback();
        }
      },
    }],
  }),
}));

</script>

<template>
  <EntityEditor ref="entityEditor" :game="game" :service="service" entity-type="Icon" :form-rules="formRules">
    <template #form>
      <template v-if="service.state.editingEntityId.value === 0">
        <el-form-item label="Upload image" prop="imageData">
          <IconUpload v-model:data-base64="service.state.editingEntityIconDataBase64.value" />
        </el-form-item>
      </template>
      <template v-else>
        <el-form-item label="Current image">
          <IconImg :icon="service.state.editingEntityId.value" :size="64" />
        </el-form-item>

        <el-form-item label="Replace image">
          <IconUpload v-model:data-base64="service.state.editingEntityIconDataBase64.value" />
        </el-form-item>
      </template>
    </template>
  </EntityEditor>
</template>

<style scoped>

</style>