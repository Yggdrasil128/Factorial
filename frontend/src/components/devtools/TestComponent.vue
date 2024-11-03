<script setup lang="ts">

import { useIconStore } from '@/stores/model/iconStore';
import { type Ref, ref } from 'vue';
import IconImg from '@/components/IconImg.vue';
import IconCascaderSelect from '@/components/input/CascaderSelect.vue';
import { useItemStore } from '@/stores/model/itemStore';

const iconStore = useIconStore();
const itemStore = useItemStore();

const selectedIcon: Ref<number> = ref(4);
const selectedItem: Ref<number> = ref(0);

</script>

<template>
  <icon-img :icon="iconStore.map.get(1)" :size="64" />
  <icon-img :icon="2" :size="64" />

  <!--  <pre>{{ JSON.stringify(convertToTreeByCategory([...iconStore.map.values()]), null, 2) }}</pre>-->

  <IconCascaderSelect v-model="selectedIcon" :options="[...iconStore.map.values()]" is-icon-entity clearable />
  {{ selectedIcon }}

  <IconCascaderSelect v-model="selectedItem" :options="[...itemStore.map.values()]" clearable />
  {{ selectedItem }}
</template>

<style scoped>

</style>