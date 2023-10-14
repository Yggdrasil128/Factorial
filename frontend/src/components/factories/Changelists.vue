<script setup>
import {computed, ref} from "vue";
import draggable from 'vuedraggable';
import {Check, Delete, Edit, Plus, Star} from "@element-plus/icons-vue";

const changelists = ref([
  {
    id: 1,
    name: "Default changelist bla",
    // icon: {
    //   url: "https://satisfactory.wiki.gg/images/thumb/a/ae/Assembler.png/300px-Assembler.png"
    // },
    primary: true,
    active: true,
    ordinal: 1
  },
  {
    id: 2,
    name: "Upgrade steel production",
    icon: {
      url: "https://satisfactory.wiki.gg/images/b/bd/Steel_Ingot.png"
    },
    primary: false,
    active: true,
    ordinal: 2
  }
]);

const primaryChangelistId = computed({
  get() {
    for (let changelist of changelists.value) {
      if (changelist.primary) {
        return changelist.id;
      }
    }
    return null;
  },
  set(id) {
    for (let changelist of changelists.value) {
      changelist.primary = changelist.id === id;
    }
  },
})

</script>

<template>
  <div class="changelists">
    <h2>Changelists</h2>
    <draggable :list="changelists" item-key="id">
      <!--suppress VueUnrecognizedSlot -->
      <template #item="{ element }">
        <div class="list-group-item"
             :class="{active: element.active, primary: element.primary, hasIcon: !!element.icon}">
          <div class="left">
            <div class="icon" v-if="element.icon">
              <img :src="element.icon.url" :alt="element.name" v-if="element.icon"/>
            </div>
            <div class="name" v-if="!element.icon">
              {{ element.name }}
            </div>
          </div>
          <div class="right">
            <div class="name" v-if="element.icon">
              {{ element.name }}
            </div>
            <div class="buttons">
              <div style="float: right;">
                <el-switch v-model="element.active" :disabled="element.primary" style="margin-right: 8px;"/>

                <el-button-group>
                  <el-button :icon="Star" v-if="!element.primary"
                             @click="element.active = true; primaryChangelistId = element.id"/>
                  <el-button :icon="Check"/>
                  <el-button :icon="Edit"/>
                  <el-button type="danger" :icon="Delete" :disabled="changelists.length === 1 || element.primary"/>
                </el-button-group>
              </div>
            </div>
          </div>
        </div>
      </template>
    </draggable>

    <div class="createChangelist">
      <el-button type="primary" :icon="Plus">Create changelist</el-button>
    </div>
  </div>
</template>

<!--suppress CssUnusedSymbol -->
<style scoped>
.list-group-item {
  width: 100%;
  overflow: hidden;
  font-size: 20px;
  background-color: #4b4b4b;
  border-radius: 8px;
}

.list-group-item.active {
  background-color: #555555;
}

.list-group-item.primary {
  background-color: #556e55;
}

.list-group-item + .list-group-item {
  margin-top: 10px;
}

.left {
  float: left;
}

.right {
  float: right;
}

.hasIcon .left {
  width: 80px;
}

.hasIcon .right {
  width: calc(100% - 80px - 16px);
}

.icon, .name {
  margin-left: 8px;
}

.list-group-item:not(.hasIcon) .name {
  margin-left: 16px;
}

.icon, .icon img {
  width: 80px;
  height: 80px;
  cursor: grab;
}

.name {
  padding-top: 8px;
  padding-bottom: 8px;
}

.primary .name {
  font-weight: bold;
}

.buttons {
  margin-top: 4px;
  margin-right: 8px;
  margin-bottom: 4px;
  overflow: auto;
}

.createChangelist {
  width: 100%;
  margin-top: 8px;
  display: flex;
  justify-content: center;
}
</style>