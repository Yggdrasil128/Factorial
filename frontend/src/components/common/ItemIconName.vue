<script setup lang="ts">
import type {Item} from "@/types/model/standalone";
import IconImg from "@/components/common/IconImg.vue";

export type ItemIconNameProps = {
  item: Item;
}

defineProps<ItemIconNameProps>();
</script>

<template>
  <icon-img :icon="item?.iconId" :size="24" style='vertical-align:middle;'/>
  <b>{{ item?.name }}</b>
</template>

<style scoped>

</style>