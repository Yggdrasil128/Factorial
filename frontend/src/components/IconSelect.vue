<script setup>
import {ref} from "vue";

defineProps(['modelValue']);
const emit = defineEmits(["update:modelValue"]);

function emitVal(value) {
  emit("update:modelValue", value ? value : null);
}

const icons = ref([]);
const iconsLoading = ref(true);

async function loadIcons() {
  await new Promise(r => setTimeout(r, 2000));
  icons.value = [
    {
      id: 1,
      name: "Iron Ingot",
      url: "https://satisfactory.wiki.gg/images/0/0a/Iron_Ingot.png"
    },
    {
      id: 2,
      name: "Steel Ingot",
      url: "https://satisfactory.wiki.gg/images/b/bd/Steel_Ingot.png"
    }
  ];
  iconsLoading.value = false;
}

loadIcons();

</script>

<template>
  <el-select :model-value="modelValue" @update:modelValue="emitVal" :placeholder="modelValue ? '' : 'Select icon'"
             clearable size="large" filterable value-key="id" class="iconSelect" :loading="iconsLoading">
    <el-option v-for="icon in icons" :value="icon" :label="icon.name">
      <div class="iconOption">
        <img :src="icon.url" :alt="icon.name">
        <span>{{ icon.name }}</span>
      </div>
    </el-option>

    <template v-if="modelValue" #prefix>
      <div class="iconSelected">
        <img :src="modelValue.url" :alt="modelValue.name">
        <span>{{ modelValue.name }}</span>
      </div>
    </template>
  </el-select>
</template>

<style scoped>
.iconOption img {
  width: 32px;
  height: 32px;
}

.iconOption span {
  margin-left: 4px;
  vertical-align: top;
}

.iconSelected {
  margin-left: -4px;
  height: 40px;
}

.iconSelected img {
  width: 40px;
  height: 40px;
}

.iconSelected span {
  margin-left: 4px;
  vertical-align: top;
}
</style>

<style>
.iconSelect .el-input:not(.is-focus) .el-input__prefix + .el-input__inner {
  visibility: hidden;
}
</style>