<script setup>
import {computed, inject, reactive, ref} from "vue";
import {onBeforeRouteUpdate, useRoute, useRouter} from "vue-router";
import _ from "lodash";
import EditModal from "@/components/EditModal.vue";
import IconSelect from "@/components/iconselect/IconSelect.vue";

const router = useRouter();
const route = useRoute();

const axios = inject('axios');
const globalEventBus = inject('globalEventBus');

const loading = ref(true);
const saving = ref(false);
const changelist = ref(emptyChangelistData());
const original = ref(null);
const editModal = ref();

const hasChanges = computed(() => !_.isEqual(changelist.value, original.value));

const formRules = reactive({
  name: [
    {required: true, message: 'Please enter a name for the changelist', trigger: 'blur'},
  ],
});

function emptyChangelistData() {
  return {
    id: null,
    name: '',
    iconId: 0,
    primary: true,
    active: true,
  }
}

async function loadChangelistData(route) {
  if (route.name === 'newChangelist') {
    changelist.value = emptyChangelistData();
  } else {
    let response = await axios.get('/api/changelist', {params: {changelistId: route.params.editChangelistId}});
    changelist.value = response.data;
    changelist.value.iconId = changelist.value.icon ? changelist.value.icon.id : 0;
  }
  original.value = Object.assign({}, changelist.value);
  loading.value = false;
}

loadChangelistData(route);
onBeforeRouteUpdate(loadChangelistData);

async function submitForm() {
  changelist.value.name = changelist.value.name.trim();

  if (!await editModal.value.validate()) {
    return;
  }

  saving.value = true;

  if (route.name === 'newChangelist') {
    await axios.post('/api/save/changelists', changelist.value, {params: {saveId: 1}});
  } else {
    await axios.patch('/api/changelist', changelist.value, {params: {changelistId: route.params.editChangelistId}});
  }

  globalEventBus.emit('updateChangelists');

  await router.push({name: 'factories', params: {factoryId: route.params.factoryId}});
}
</script>

<template>
  <edit-modal
      :title="route.name === 'newChangelist' ? 'New changelist' : 'Edit changelist'"
      form-label-width="150px"
      :form-model="changelist"
      :form-loading="loading"
      :form-rules="formRules"
      :has-changes="hasChanges"
      :is-saving="saving"
      @submit="submitForm"
      ref="editModal">

    <template #description>
      Insert changelist explanation here...
    </template>

    <template #form>
      <el-form-item label="Changelist name" prop="name">
        <el-input v-model="changelist.name"/>
      </el-form-item>

      <el-form-item label="Icon">
        <icon-select v-model="changelist.iconId"/>
      </el-form-item>

      <template v-if="route.name === 'newChangelist'">
        <el-form-item label="Primary">
          <el-switch v-model="changelist.primary"
                     @update:model-value="changelist.primary ? changelist.active = true : ''"/>
        </el-form-item>

        <el-form-item label="Active">
          <el-switch v-model="changelist.active" :disabled="changelist.primary"/>
        </el-form-item>
      </template>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>