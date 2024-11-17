<script setup lang="ts">

import { useIconStore } from '@/stores/model/iconStore';
import { inject, type Ref, ref } from 'vue';
import IconImg from '@/components/common/IconImg.vue';
import CascaderSelect from '@/components/common/input/CascaderSelect.vue';
import { useItemStore } from '@/stores/model/itemStore';
import type { AxiosInstance } from 'axios';
import CascaderMultiSelect from '@/components/common/input/CascaderMultiSelect.vue';
import FlatSelect from '@/components/common/input/FlatSelect.vue';
import FlatMultiSelect from '@/components/common/input/FlatMultiSelect.vue';

const iconStore = useIconStore();
const itemStore = useItemStore();

const selectedItem: Ref<number> = ref(0);
const selectedItems: Ref<number[]> = ref([]);

const axios: AxiosInstance = inject('axios') as AxiosInstance;

async function download(): Promise<void> {
  const url: string = 'https://satisfactory.wiki.gg/images/thumb/7/79/Lizard_Doggo.png/300px-Lizard_Doggo.png';

  axios.get(url, { responseType: 'blob' })
    .then((response) => {
      console.log(response);
    });
}

</script>

<template>
  <icon-img :icon="iconStore.getById(1)" :size="64" />
  <icon-img :icon="2" :size="64" />

  <div>
    <h2>FlatSelect and CascaderSelect</h2>

    <FlatSelect style="width: 250px;" v-model="selectedItem" :options="[...itemStore.getAll()]" clearable />
    <br>
    <CascaderSelect style="width: 250px;" v-model="selectedItem" :options="[...itemStore.getAll()]" clearable />

    <pre>{{ JSON.stringify(selectedItem) }}</pre>
  </div>

  <div>
    <h2>FlatMultiSelect and CascaderMultiSelect</h2>

    <FlatMultiSelect style="width: 250px;" v-model="selectedItems" :options="[...itemStore.getAll()]" clearable />
    <br><br>
    <CascaderMultiSelect style="width: 250px;" v-model="selectedItems" :options="[...itemStore.getAll()]" clearable />

    <pre>{{ JSON.stringify(selectedItems) }}</pre>
  </div>

  <br />
  <el-button @click="download">Download</el-button>
</template>

<style scoped>

</style>