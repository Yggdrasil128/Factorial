<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { useFactoryStore } from '@/stores/model/factoryStore';
import { computed, type ComputedRef } from 'vue';
import type { Factory, Resource } from '@/types/model/standalone';
import { useResourceStore } from '@/stores/model/resourceStore';
import { ElDropdown, ElDropdownItem, ElDropdownMenu, ElIcon } from 'element-plus';
import { VueDraggableNext } from 'vue-draggable-next';
import { Plus } from '@element-plus/icons-vue';
import FactoryResource from '@/components/factories/resources/FactoryResource.vue';
import { useResourceApi } from '@/api/useResourceApi';
import { type DraggableSupport, useDraggableSupport } from '@/utils/useDraggableSupport';
import type { EntityWithOrdinal } from '@/types/model/basic';

const route = useRoute();
const router = useRouter();

const factoryStore = useFactoryStore();
const resourceStore = useResourceStore();
const resourceApi = useResourceApi();

const currentFactoryId: ComputedRef<number | undefined> = computed(() =>
  route.params.factoryId ? Number(route.params.factoryId) : undefined
);

const factory: ComputedRef<Factory> = computed(() =>
  factoryStore.getById(currentFactoryId.value!)!
);

const resources: ComputedRef<Resource[]> = computed(() =>
  resourceStore.getByFactoryId(currentFactoryId.value)
    .sort((a, b) => a.ordinal - b.ordinal)
);

const draggableSupport: DraggableSupport = useDraggableSupport(resources,
  (input: EntityWithOrdinal[]) => resourceApi.reorder(currentFactoryId.value!, input)
);

function newProductionStep() {
  router.push({ name: 'newProductionStep', params: { factoryId: route.params.factoryId } });
}

function newTransportLink() {
}

function newIngress() {
}

function newEgress() {
}

</script>

<template>
  <template v-if="factory">
    <div style="overflow: auto;">
      <div style="float: left;">
        <h2>Factory name: {{ factory.name }}</h2>
      </div>
      <div style="float: right; margin-top: 16px;">
        <el-dropdown split-button type="primary" @click="newProductionStep">
          <el-icon class="el-icon--left">
            <plus />
          </el-icon>
          New production step
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="newTransportLink">New transport link</el-dropdown-item>
              <el-dropdown-item @click="newIngress">New ingress</el-dropdown-item>
              <el-dropdown-item @click="newEgress">New egress</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <vue-draggable-next :model-value="resources" @end="draggableSupport.onDragEnd">
      <factory-resource
        v-for="resource in resources"
        :key="resource.id"
        :resource="resource"
      />
    </vue-draggable-next>
  </template>
</template>

<style scoped>

</style>