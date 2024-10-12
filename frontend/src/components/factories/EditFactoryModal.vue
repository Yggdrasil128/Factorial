<script setup lang="js">
import { computed, inject, reactive, ref } from 'vue';
import { onBeforeRouteUpdate, useRoute, useRouter } from 'vue-router';
import _ from 'lodash';
import IconSelect from '@/components/iconselect/IconSelect.vue';

const router = useRouter();
const route = useRoute();

const axios = inject('axios');
const globalEventBus = inject('globalEventBus');

const loading = ref(true);
const saving = ref(false);
const factory = ref(emptyFactoryData());
const original = ref(null);
const editModal = ref();

const hasChanges = computed(() => !_.isEqual(factory.value, original.value));

const formRules = reactive({
  name: [{ required: true, message: 'Please enter a name for the factory', trigger: 'blur' }]
});

function emptyFactoryData() {
  return {
    id: null,
    name: '',
    iconId: 0
  };
}

async function loadFactoryData(route) {
  loading.value = true;
  if (route.name === 'newFactory') {
    factory.value = emptyFactoryData();
  } else {
    const response = await axios.get('/api/factory', {
      params: { factoryId: route.params.editFactoryId }
    });
    factory.value = response.data;
    factory.value.iconId = factory.value.icon ? factory.value.icon.id : 0;
  }
  original.value = Object.assign({}, factory.value);
  loading.value = false;
}

loadFactoryData(route);
onBeforeRouteUpdate(loadFactoryData);

async function submitForm() {
  factory.value.name = factory.value.name.trim();

  if (!(await editModal.value.validate())) {
    return;
  }

  saving.value = true;

  if (route.name === 'newFactory') {
    await axios.post('/api/save/factories', factory.value, { params: { saveId: 1 } });
  } else {
    await axios.patch('/api/factory', factory.value, {
      params: { factoryId: route.params.editFactoryId }
    });
  }

  globalEventBus.emit('updateFactories');

  await router.push({ name: 'factories', params: { factoryId: route.params.factoryId } });
}
</script>

<template>
  <edit-modal
    :title="route.name === 'newFactory' ? 'New factory' : 'Edit factory'"
    form-label-width="120px"
    :form-model="factory"
    :form-loading="loading"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="saving"
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
        <icon-select v-model="factory.iconId" />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped></style>
