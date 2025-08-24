<script setup lang="ts">
import {
  onBeforeRouteUpdate,
  type RouteLocationNormalizedLoadedGeneric,
  type Router,
  useRoute,
  useRouter
} from 'vue-router';
import {computed, type ComputedRef, reactive, type Ref, ref} from 'vue';
import type {Factory} from '@/types/model/standalone';
import {type CurrentGameAndSaveStore, useCurrentGameAndSaveStore} from '@/stores/currentGameAndSaveStore';
import {type FactoryStore, useFactoryStore} from '@/stores/model/factoryStore';
import _ from 'lodash';
import {FactoryApi, useFactoryApi} from '@/api/model/useFactoryApi';
import {ElFormItem, ElInput, type FormRules} from 'element-plus';
import EditModal from '@/components/common/EditModal.vue';
import {type IconStore, useIconStore} from '@/stores/model/iconStore';
import CascaderSelect from '@/components/common/input/CascaderSelect.vue';

const router: Router = useRouter();
const route: RouteLocationNormalizedLoadedGeneric = useRoute();

const currentGameAndSaveStore: CurrentGameAndSaveStore = useCurrentGameAndSaveStore();
const factoryStore: FactoryStore = useFactoryStore();
const iconStore: IconStore = useIconStore();

const factoryApi: FactoryApi = useFactoryApi();

const isSaving: Ref<boolean> = ref(false);
const factory: Ref<Partial<Factory>> = ref({});
const original: Ref<Partial<Factory>> = ref({});
const editModal: Ref<InstanceType<typeof EditModal> | undefined> = ref();

const hasChanges: ComputedRef<boolean> = computed(() => !_.isEqual(factory.value, original.value));

const formRules: FormRules = reactive({
  name: [{ required: true, message: 'Please enter a name for the factory', trigger: 'blur' }]
});

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  if (route.name === 'newFactory') {
    factory.value = {
      saveId: currentGameAndSaveStore.currentSaveId,
      name: '',
      description: '',
      iconId: 0
    };
  } else {
    const factoryId: number = Number(route.params.editFactoryId);
    const currentFactory: Factory | undefined = factoryStore.getById(factoryId);
    if (!currentFactory) {
      console.error('Factory with id ' + factoryId + ' not found');
      router.push({ name: 'factories', params: { factoryId: route.params.factoryId } });
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
  factory.value.name = factory.value.name?.trim();

  if (!(await editModal.value?.validate())) {
    return;
  }

  isSaving.value = true;

  if (route.name === 'newFactory') {
    await factoryApi.create(factory.value);
  } else {
    await factoryApi.edit(factory.value);
  }

  await router.push({ name: 'factories', params: { factoryId: route.params.factoryId } });
}
</script>

<template>
  <edit-modal
    :title="route.name === 'newFactory' ? 'New factory' : 'Edit factory'"
    form-label-width="120px"
    :form-model="factory"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="isSaving"
    @submit="submitForm"
    ref="editModal"
  >
    <template #description>
      Factories allow you to group production steps. They also accumulate their inputs and outputs,
      showing you how much of each item is produced and consumed.
    </template>

    <template #form>
      <el-form-item label="Factory name" prop="name">
        <el-input v-model="factory.name" />
      </el-form-item>

      <el-form-item label="Icon">
        <CascaderSelect v-model="factory.iconId!"
                        :options="iconStore.getByGameId(currentGameAndSaveStore.currentGameId)"
                        is-icon-entity
                        clearable
                        style="width: 100%;" />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>