<script setup>
import IconCascader from "@/components/iconselect/IconCascader.vue";

const props = defineProps(['modelValue']);
const emit = defineEmits(['update:modelValue']);
</script>

<template>
  <icon-cascader :model-value="modelValue" @update:model-value="value => emit('update:modelValue', value)"
                 fetch-endpoint="/api/save/recipes" entity-name="recipe" :recipe-icons="true"/>
</template>

<style scoped>

</style>