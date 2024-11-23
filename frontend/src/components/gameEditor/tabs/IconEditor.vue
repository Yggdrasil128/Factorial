<script setup lang="ts">
import type { Game, Icon } from '@/types/model/standalone';
import { computed, type ComputedRef, ref, watch } from 'vue';
import { type EntityTreeService, iconOptionsForIcon, useEntityTreeService } from '@/services/useEntityTreeService';
import { ElFormItem, ElInput, type FormItemInstance, type FormRules } from 'element-plus';
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
      required: true,
      message: service.state.selectedIconOption.value === 'new'
        ? 'Please select an image file to upload.'
        : 'Please enter a URL',
      validator(_, __, callback) {
        if (service.state.selectedIconOption.value === 'new' && service.state.editingEntityIconDataBase64.value) {
          callback();
        } else if (service.state.selectedIconOption.value === 'url' && service.state.editingEntityIconDataUrl.value) {
          callback();
        } else {
          callback(new Error());
        }
      },
    }],
  }),
}));

const formItemImageData = ref<FormItemInstance>();

watch(() => service.state.selectedIconOption.value, async () => {
  setTimeout(() => formItemImageData.value?.clearValidate(), 50);
});

</script>

<template>
  <EntityEditor ref="entityEditor" :game="game" :service="service" entity-type="Icon" :form-rules="formRules">
    <template #form>
      <el-form-item v-if="service.state.editingEntityId.value" label="Current image">
        <IconImg :icon="service.state.editingEntityId.value" :size="64" />
      </el-form-item>

      <el-form-item :label="service.state.editingEntityId.value ? 'Replace image' : 'Upload image'"
                    :prop="service.state.editingEntityId.value ? undefined : 'imageData'"
                    ref="formItemImageData">

        <el-segmented v-model="service.state.selectedIconOption.value"
                      :options="iconOptionsForIcon">
          <template #default="{ item }">
            <div style="margin: 4px;">
              <div v-if="typeof item.icon === 'string'" class="maskIcon"
                   style="width: 20px; height: 20px;"
                   :style="{mask: 'url(\'' + item.icon + '\') center/contain'}">
              </div>
              <el-icon v-else size="20">
                <component :is="item.icon" />
              </el-icon>
              <div>{{ item.label }}</div>
            </div>
          </template>
        </el-segmented>

        <template v-if="service.state.selectedIconOption.value === 'new'">
          <div class="full-width" style="margin-top: 12px;">
            <IconUpload v-model:data-base64="service.state.editingEntityIconDataBase64.value" />
          </div>
        </template>
        <template v-if="service.state.selectedIconOption.value === 'url'">
          <div class="full-width" style="margin-top: 12px;">
            <el-input v-model="service.state.editingEntityIconDataUrl.value" placeholder="Enter icon URL" />
          </div>
        </template>
      </el-form-item>
    </template>
  </EntityEditor>
</template>

<style scoped>
.maskIcon {
  background-color: var(--el-segmented-color);
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 3px;
}
</style>