<script setup>
import {inject, ref, watch} from "vue";
import {OnClickOutside} from '@vueuse/components';
import IconImg from "@/components/IconImg.vue";
import _ from 'lodash';

const axios = inject('axios');

const props = defineProps(['modelValue', 'fetchEndpoint', 'entityName']);
const emit = defineEmits(['update:modelValue']);

const selectedId = ref();
const loading = ref(true);
const searching = ref(false);
const expanded = ref(false);
const allOptions = ref([]);
const filteredOptions = ref([]);
const itemMap = ref({});
const inputString = ref('');
const cascaderProps = ref({
  emitPath: false,
  value: 'id',
  expandTrigger: 'hover',
});
const cascaderRef = ref();
const inputRef = ref();

async function loadData() {
  loading.value = true;

  let response = await axios.get(props.fetchEndpoint, {params: {saveId: 1}});

  let items = response.data;
  console.log(items);
  if (props.entityName === 'icon') {
    for (let item of items) {
      item.icon = item;
    }
  }

  let options = [];
  let newItemMap = {};

  function insert(tree, item) {
    if (item.category.length > 0) {
      const category = item.category.shift();
      for (let child of tree) {
        if (child.children && child.label === category) {
          insert(child.children, item);
          return;
        }
      }
      let child = {label: category, children: []};
      tree.push(child);
      insert(child.children, item);
      return;
    }
    item.label = item.name;
    tree.push(item);
  }

  for (let item of items) {
    insert(options, item);
    newItemMap[String(item.id)] = item;
  }

  function sort(tree) {
    tree.sort((a, b) => a.label.localeCompare(b.label));
    for (let child of tree) {
      if (child.children) {
        sort(child.children)
      }
    }
  }

  sort(options);

  allOptions.value = options;
  filteredOptions.value = options;
  itemMap.value = newItemMap;
  selectedId.value = props.modelValue;
  loading.value = false;
}

loadData();

function updateFilteredOptions() {
  const search = inputString.value.toLowerCase().trim();
  if (search.length === 0 || !searching.value) {
    filteredOptions.value = allOptions.value;
    return;
  }

  let directMatches = [];

  function filterNode(node) {
    if (node.label.toLowerCase().indexOf(search) >= 0) {
      directMatches.push(node);
      return true;
    }
    if (!node.children) {
      return false;
    }
    node.children = node.children.filter(filterNode);
    return node.children.length > 0;
  }

  let options = _.cloneDeep(allOptions.value);
  options = options.filter(filterNode);

  if (directMatches.length <= 5) {
    directMatches.sort((a, b) => a.label.localeCompare(b.label));
    options = directMatches;
  }

  filteredOptions.value = options;
}

function getLeafCount(node) {
  if (node.isLeaf) {
    return 1;
  }
  let c = 0;
  for (let child of node.children) {
    c += getLeafCount(child);
  }
  return c;
}

watch(selectedId, (id) => {
  setEventBackoff(300);
  searching.value = false;
  inputString.value = id ? itemMap.value[id].name : '';
  setTimeout(updateFilteredOptions, 290);
  emit('update:modelValue', id);
});

function checkCascaderPanelFocus() {
  const panel = cascaderRef.value.$el;
  for (let element = document.activeElement; element !== null; element = element.parentElement) {
    if (element === panel) {
      return true;
    }
  }
  return false;
}

let eventBackoff = false;
let clickOutsideBackoff = false;

function onInputFocus() {
  setClickOutsideBackoff(500);
  if (eventBackoff) {
    inputRef.value.blur();
    return;
  }
  setEventBackoff(100);
  inputRef.value.select();
  expanded.value = true;
}

function onInputBlur() {
  setTimeout(() => {
    if (!checkCascaderPanelFocus()) {
      setEventBackoff(100);
      setClickOutsideBackoff(500);
      expanded.value = false;
      inputString.value = selectedId.value ? itemMap.value[selectedId.value].name : '';
      searching.value = false;
      updateFilteredOptions();
    }
  }, 100);
}

function onInputClear() {
  selectedId.value = null;
  setEventBackoff(100);
}

function onInputInput() {
  if (eventBackoff) {
    return;
  }
  searching.value = true;
  updateFilteredOptions();
}

function onInputArrowDown() {
  if (eventBackoff) {
    return;
  }
  if (filteredOptions.value.length === 0) {
    return;
  }
  let element = cascaderRef.value.$el;
  for (let i = 0; i < 4; i++) {
    element = element.children[0];
  }
  element.focus();
}

function onClickOutside() {
  setTimeout(() => {
    if (clickOutsideBackoff) {
      return;
    }
    setEventBackoff(100);
    expanded.value = false;
    inputString.value = selectedId.value ? itemMap.value[selectedId.value].name : '';
    searching.value = false;
    updateFilteredOptions();
  }, 50);
}

function setEventBackoff(ms) {
  eventBackoff = true;
  setTimeout(() => eventBackoff = false, ms);
}

function setClickOutsideBackoff(ms) {
  clickOutsideBackoff = true;
  setTimeout(() => clickOutsideBackoff = false, ms);
}

</script>

<template>
  <div>
    <el-input v-model="inputString" ref="inputRef"
              :placeholder="loading ? 'Loading...' : 'Select ' + entityName + '...'"
              size="large" clearable class="el-dark iconCascaderInput" @keyup.down="onInputArrowDown"
              @focus="onInputFocus" @blur="onInputBlur" @clear="onInputClear" @input="onInputInput"
              v-loading="loading" element-loading-background="rgba(65, 65, 65, 0.6)">
      <template #prefix v-if="selectedId">
        <icon-img :icon="itemMap[selectedId].icon" :size="32"/>
      </template>
    </el-input>
    <OnClickOutside @trigger="onClickOutside">
      <el-collapse class="el-dark iconCascaderCollapse" :model-value="expanded ? ['item'] : []">
        <el-collapse-item name="item" disabled>
          <!--suppress VueUnrecognizedSlot -->
          <template #title>
          </template>
          <el-cascader-panel v-model="selectedId" :options="filteredOptions" class="el-dark iconCascader"
                             :props="cascaderProps"
                             @close="expanded = false" ref="cascaderRef">
            <template #default="{ node, data }">
              <template v-if="node.isLeaf">
                <div style="height: 36px; display: flex;">
                <span style="margin-right: 8px;">
                  <icon-img :icon="data.icon" :size="32"/>
                </span>
                  <span>{{ data.label }}</span>
                </div>
              </template>
              <template v-else>
                <span>{{ data.label }}</span>
                <span v-if="!node.isLeaf"> ({{ getLeafCount(node) }}) </span>
              </template>
            </template>
          </el-cascader-panel>
        </el-collapse-item>
      </el-collapse>
    </OnClickOutside>
  </div>
</template>

<style scoped>
.iconCascaderCollapse {
  width: fit-content;
  min-width: 250px;
}

.iconCascaderInput {
  width: 250px;
}

.iconCascader {
  border: 1px solid #808080;
  width: fit-content;
  min-width: 248px;
  margin-top: 3px;
}
</style>

<!--suppress CssUnusedSymbol -->
<style>
.iconCascaderCollapse, .iconCascaderCollapse .el-collapse-item__wrap {
  border: none;
}

.iconCascaderCollapse .el-collapse-item__header {
  display: none;
}

.iconCascaderCollapse .el-collapse-item__content {
  padding-bottom: 0;
}

.iconCascaderCollapse .el-cascader-menu:not(:first-child) {
  border-left: 1px solid #808080;
}
</style>