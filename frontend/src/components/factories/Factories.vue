<script setup>
import {computed, ref} from "vue";
import draggable from 'vuedraggable';
import {Delete, Edit, Plus} from "@element-plus/icons-vue";
import {useRoute, useRouter} from "vue-router";

const router = useRouter();
const route = useRoute();

const factories = ref([]);
const currentFactoryId = computed(() => route.params.factoryId);

function newFactory() {
  router.push({name: 'newFactory', params: {factoryId: route.params.factoryId}});
}

function editFactory(editFactoryId) {
  router.push({name: 'editFactory', params: {factoryId: route.params.factoryId, editFactoryId: editFactoryId}});
}

async function loadFactories() {
  await new Promise(r => setTimeout(r, 500));
  factories.value = [
    {
      id: 1,
      name: "Main factory",
      // icon: {
      //   url: "https://satisfactory.wiki.gg/images/thumb/a/ae/Assembler.png/300px-Assembler.png"
      // },
      ordinal: 1
    },
    {
      id: 2,
      name: "Steel production north",
      icon: {
        id: 2,
        name: "Steel Ingot",
        url: "https://satisfactory.wiki.gg/images/b/bd/Steel_Ingot.png"
      },
      ordinal: 2
    }
  ];
  if (factories.value.length > 0 && !currentFactoryId.value) {
    await router.replace({name: 'factories', params: {factoryId: factories.value[0].id}});
  }
}

function viewFactory(id) {
  if (currentFactoryId.value === id) {
    return;
  }
  router.push({name: 'factories', params: {factoryId: id}});
}

loadFactories();

</script>

<template>
  <div class="factoryList">
    <h2>Factory list</h2>
    <draggable :list="factories" item-key="id">
      <!--suppress VueUnrecognizedSlot -->
      <template #item="{ element }">
        <div class="list-group-item" :class="{active: element.id === currentFactoryId}">
          <div class="icon" @click="viewFactory(element.id)">
            <img :src="element.icon.url" :alt="element.name" v-if="element.icon"/>
          </div>
          <div class="name" @click="viewFactory(element.id)">
            {{ element.name }}
          </div>
          <div class="buttons">
            <el-button-group>
              <el-tooltip effect="dark" placement="top-start" transition="none" hide-after="0"
                          content="Edit">
                <el-button :icon="Edit" @click="editFactory(element.id)"/>
              </el-tooltip>
              <el-tooltip effect="dark" placement="top-start" transition="none" hide-after="0"
                          content="Delete">
                <el-button type="danger" :icon="Delete" :disabled="factories.length === 1"/>
              </el-tooltip>
            </el-button-group>
          </div>
        </div>
      </template>
    </draggable>

    <div class="createFactory">
      <el-button type="primary" :icon="Plus" @click="newFactory()">Create factory</el-button>
    </div>
  </div>
</template>

<style scoped>
.list-group-item {
  width: 100%;
  overflow: hidden;
  font-size: 20px;
  background-color: #4b4b4b;
  border-radius: 8px;
}

.list-group-item.active {
  background-color: #556e55;
}

.list-group-item + .list-group-item {
  margin-top: 10px;
}

.icon, .name {
  float: left;
  margin-left: 8px;
}

.icon, .icon img {
  width: 40px;
  height: 40px;
  cursor: grab;
}

.name {
  padding-top: 8px;
  padding-bottom: 8px;
  cursor: pointer;
  width: 280px;
}

.active .name {
  font-weight: bold;
}

.buttons {
  float: right;
  margin-top: 4px;
  margin-right: 8px;
}

.createFactory {
  width: 100%;
  margin-top: 8px;
  display: flex;
  justify-content: center;
}
</style>