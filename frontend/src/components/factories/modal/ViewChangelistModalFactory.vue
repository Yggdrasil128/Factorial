<script setup lang="ts">
import type { ProductionStepChangesByFactory } from '@/components/factories/modal/ViewChangelistModal.vue';
import ViewChangelistModalProductionStep from '@/components/factories/modal/ViewChangelistModalProductionStep.vue';
import IconImg from '@/components/common/IconImg.vue';
import type { Changelist } from '@/types/model/standalone';

export interface ViewChangelistModalFactoryProps {
  changelist: Changelist;
  changes: ProductionStepChangesByFactory;
  productionStepIdsToClear: Set<number>;
}

defineProps<ViewChangelistModalFactoryProps>();

</script>

<template>
  <div class="main">
    <div class="row items-center">
      <IconImg :icon="changes.factory.iconId" :size="32" />
      <h2>{{ changes.factory.name }}</h2>
    </div>

    <div class="list column">
      <ViewChangelistModalProductionStep v-for="change in changes.changes"
                                         :key="change.productionStep.id"
                                         :changelist="changelist"
                                         :change="change"
                                         :production-step-ids-to-clear="productionStepIdsToClear" />
    </div>
  </div>
</template>

<style scoped>

</style>