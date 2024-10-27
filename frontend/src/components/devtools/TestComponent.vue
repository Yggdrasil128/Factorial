<script setup lang="ts">

import { useIconStore } from '@/stores/model/iconStore';
import { type Ref, ref } from 'vue';
import IconImg from '@/components/IconImg.vue';
import IconCascaderSelect from '@/components/CascaderSelect.vue';

const iconStore = useIconStore();

const selected: Ref<number | undefined> = ref(4);

</script>

<template>
  <icon-img :icon="iconStore.map.get(1)" :size="64" />
  <icon-img :icon-id="2" :size="64" />

  <!--  <pre>{{ JSON.stringify(convertToTreeByCategory([...iconStore.map.values()]), null, 2) }}</pre>-->

  <IconCascaderSelect v-model="selected" :options="[...iconStore.map.values()]" clearable />
  {{ selected }}
</template>

<style scoped>

</style>