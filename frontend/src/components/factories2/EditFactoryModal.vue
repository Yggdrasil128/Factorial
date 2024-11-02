<script setup lang="ts">
import { onBeforeRouteUpdate, type RouteLocationNormalizedLoadedGeneric, useRoute, useRouter } from 'vue-router';
import { computed, reactive, type Ref, ref } from 'vue';
import type { Factory } from '@/types/model/standalone';
import { useCurrentSaveStore } from '@/stores/currentSaveStore';
import { useFactoryStore } from '@/stores/model/factoryStore';
import _ from 'lodash';
import { useFactoryApi } from '@/api/useFactoryApi';
import { ElFormItem, ElInput } from 'element-plus';
import EditModal from '@/components/EditModal.vue';
import { useIconStore } from '@/stores/model/iconStore';
import CascaderSelect from '@/components/CascaderSelect.vue';

const router = useRouter();
const route = useRoute();

const currentSaveStore = useCurrentSaveStore();
const factoryStore = useFactoryStore();
const iconStore = useIconStore();

const factoryApi = useFactoryApi();

const isSaving: Ref<boolean> = ref(false);
const factory: Ref<Partial<Factory>> = ref({});
const original: Ref<Partial<Factory>> = ref({});
const editModal = ref();

const hasChanges = computed(() => !_.isEqual(factory.value, original.value));

const formRules = reactive({
  name: [{ required: true, message: 'Please enter a name for the factory', trigger: 'blur' }]
});

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  if (route.name === 'newFactory2') {
    factory.value = {
      saveId: currentSaveStore.save?.id,
      name: '',
      description: '',
      iconId: 0
    };
  } else {
    const factoryId: number = Number(route.params.editFactoryId);
    const currentFactory: Factory | undefined = factoryStore.map.get(factoryId);
    if (!currentFactory) {
      console.error('Factory with id ' + factoryId + ' not found');
      router.push({ name: 'factories2', params: { factoryId: route.params.factoryId } });
      return;
    }
    factory.value = {
      id: factoryId,
      name: currentFactory.name,
      description: currentFactory.description,
      iconId: currentFactory.iconId
    };
  }
  original.value = _.clone(factory.value);
  isSaving.value = false;
}

initFromRoute(route);
onBeforeRouteUpdate(initFromRoute);

async function submitForm(): Promise<void> {
  if (!factory.value) {
    return;
  }
  factory.value.name = factory.value.name?.trim();

  if (!(await editModal.value.validate())) {
    return;
  }

  isSaving.value = true;

  if (route.name === 'newFactory2') {
    await factoryApi.createFactory(factory.value);
  } else {
    await factoryApi.editFactory(factory.value);
  }

  await router.push({ name: 'factories2', params: { factoryId: route.params.factoryId } });
}
</script>

<template>
  <edit-modal
    :title="route.name === 'newFactory2' ? 'New factory' : 'Edit factory'"
    form-label-width="120px"
    :form-model="factory"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="isSaving"
    @submit="submitForm"
    ref="editModal"
  >
    <template #description>
      Factories allow you to group production steps. Blablabla, more explanation here...
    </template>

    <template #form>
      <el-form-item label="Factory name" prop="name">
        <el-input v-model="factory.name" />
      </el-form-item>

      <el-form-item label="Icon">
        <CascaderSelect v-model="factory.iconId!" :options="[...iconStore.map.values()]" is-icon-entity clearable />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>