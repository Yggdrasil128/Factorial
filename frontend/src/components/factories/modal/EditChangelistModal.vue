<script setup lang="ts">
import { onBeforeRouteUpdate, type RouteLocationNormalizedLoadedGeneric, useRoute, useRouter } from 'vue-router';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { useChangelistStore } from '@/stores/model/changelistStore';
import { useIconStore } from '@/stores/model/iconStore';
import { useChangelistApi } from '@/api/useChangelistApi';
import { computed, reactive, ref, type Ref } from 'vue';
import type { Changelist } from '@/types/model/standalone';
import _ from 'lodash';
import { ElFormItem, ElInput, ElSwitch } from 'element-plus';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import EditModal from '@/components/EditModal.vue';
import CascaderSelect from '@/components/input/CascaderSelect.vue';

const router = useRouter();
const route = useRoute();

const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const changelistStore = useChangelistStore();
const iconStore = useIconStore();

const changelistApi = useChangelistApi();

const isSaving: Ref<boolean> = ref(false);
const isEditingPrimaryChangelist: Ref<boolean> = ref(false);
const changelist: Ref<Partial<Changelist>> = ref({});
const original: Ref<Partial<Changelist>> = ref({});
const editModal = ref();

const hasChanges = computed(() => !_.isEqual(changelist.value, original.value));

const formRules = reactive({
  name: [{ required: true, message: 'Please enter a name for the changelist', trigger: 'blur' }]
});

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  if (route.name === 'newChangelist') {
    changelist.value = {
      saveId: currentGameAndSaveStore.save?.id,
      name: '',
      iconId: 0,
      primary: false,
      active: true
    };
    isEditingPrimaryChangelist.value = false;
  } else {
    const changelistId: number = Number(route.params.editChangelistId);
    const currentChangelist: Changelist | undefined = changelistStore.getById(changelistId);
    if (!currentChangelist) {
      console.error('Changelist with id ' + changelistId + ' not found');
      router.push({ name: 'factories', params: { factoryId: route.params.factoryId } });
      return;
    }
    changelist.value = {
      id: changelistId,
      name: currentChangelist.name,
      iconId: currentChangelist.iconId,
      primary: currentChangelist.primary,
      active: currentChangelist.active
    };
    isEditingPrimaryChangelist.value = currentChangelist.primary;
  }
  original.value = _.clone(changelist.value);
  isSaving.value = false;
}

initFromRoute(route);
onBeforeRouteUpdate(initFromRoute);

async function submitForm(): Promise<void> {
  changelist.value.name = changelist.value.name?.trim();

  if (!(await editModal.value.validate())) {
    return;
  }

  isSaving.value = true;

  if (route.name === 'newChangelist') {
    await changelistApi.createChangelist(changelist.value);
  } else {
    await changelistApi.editChangelist(changelist.value);
  }

  await router.push({ name: 'factories', params: { factoryId: route.params.factoryId } });
}
</script>

<template>
  <edit-modal
    :title="route.name === 'newChangelist' ? 'New changelist' : 'Edit changelist'"
    form-label-width="150px"
    :form-model="changelist"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="isSaving"
    @submit="submitForm"
    ref="editModal"
  >
    <template #description> Insert changelist explanation here...</template>

    <template #form>
      <el-form-item label="Changelist name" prop="name">
        <el-input v-model="changelist.name" />
      </el-form-item>

      <el-form-item label="Icon">
        <CascaderSelect v-model="changelist.iconId!"
                        :options="iconStore.getByGameId(currentGameAndSaveStore.game?.id)"
                        is-icon-entity
                        clearable
                        style="width: 100%;" />
      </el-form-item>

      <el-form-item label="Primary">
        <el-switch
          v-model="changelist.primary"
          :disabled="isEditingPrimaryChangelist"
          @update:model-value="changelist.primary ? (changelist.active = true) : ''"
        />
      </el-form-item>

      <el-form-item label="Active">
        <el-switch
          v-model="changelist.active"
          :disabled="isEditingPrimaryChangelist || changelist.primary" />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>