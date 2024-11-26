<script setup lang="ts">
import { computed, type ComputedRef, reactive, ref, type Ref } from 'vue';
import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router';
import { sleep } from '@/utils/utils';
import { useChangelistStore } from '@/stores/model/changelistStore';
import type { Changelist, Factory, ProductionStep } from '@/types/model/standalone';
import type { Fraction } from '@/types/model/basic';
import { useProductionStepStore } from '@/stores/model/productionStepStore';
import { useFactoryStore } from '@/stores/model/factoryStore';
import ViewChangelistModalFactory from '@/components/factories/modal/ViewChangelistModalFactory.vue';
import { useChangelistApi } from '@/api/model/useChangelistApi';
import PlaceholderHelpBox from '@/components/common/PlaceholderHelpBox.vue';

const router = useRouter();
const route = useRoute();

const visible: Ref<boolean> = ref(true);

onBeforeRouteLeave(async () => {
  if (visible.value) {
    await close();
  }
});

async function beforeClose(): Promise<void> {
  await close();
  router.back();
}

async function close(): Promise<void> {
  visible.value = false;
  await Promise.all([
    sleep(200),
    ...[...productionStepIdsToClear.values()]
      .map(productionStepId => changelistApi.deleteChange(changelistId.value, productionStepId)),
  ]);
  await sleep(200);
}

const changelistStore = useChangelistStore();
const productionStepStore = useProductionStepStore();
const factoryStore = useFactoryStore();
const changelistApi = useChangelistApi();

const productionStepIdsToClear: Set<number> = reactive(new Set());

const changelistId: ComputedRef<number> = computed(() =>
  Number(route.params.changelistId),
);

const changelist: ComputedRef<Changelist | undefined> = computed(() =>
  changelistStore.getById(changelistId.value),
);

export interface ProductionStepChangesByFactory {
  factory: Factory;
  changes: ProductionStepChange[];
}

export interface ProductionStepChange {
  productionStep: ProductionStep;
  change: Fraction;
}

const changesByFactory: ComputedRef<ProductionStepChangesByFactory[]> = computed(() => {
  if (!changelist.value) return [];
  const result: ProductionStepChangesByFactory[] = [];
  const mapByFactoryId: Map<number, ProductionStepChangesByFactory> = new Map();
  for (const productionStepChange of changelist.value.productionStepChanges) {
    const productionStep: ProductionStep | undefined = productionStepStore.getById(productionStepChange.productionStepId);
    if (!productionStep) continue;
    const factory: Factory | undefined = factoryStore.getById(productionStep.factoryId);
    if (!factory) continue;

    let productionStepChangesByFactory: ProductionStepChangesByFactory | undefined = mapByFactoryId.get(factory.id);
    if (!productionStepChangesByFactory) {
      productionStepChangesByFactory = {
        factory: factory,
        changes: [],
      };
      result.push(productionStepChangesByFactory);
      mapByFactoryId.set(factory.id, productionStepChangesByFactory);
    }

    productionStepChangesByFactory.changes.push({
      productionStep: productionStep,
      change: productionStepChange.change,
    });
  }

  result.sort((a, b) => a.factory.ordinal - b.factory.ordinal);

  return result;
});

</script>

<template>
  <el-dialog
    :model-value="visible"
    :before-close="beforeClose"
    width="600px"
    style="padding-right: 8px;"
    :title="'Changes in changelist \'' + changelist?.name + '\''"
    :close-on-click-modal="true"
  >
    <div v-if="changelist" class="dialogContent column">
      <PlaceholderHelpBox v-if="changesByFactory.length === 0"
                          title="This changelist is empty."
                          :width="400"
                          background-color="#282828">
        <p>
          You can add changes to the primary changelist (highlighted in green) by simply editing the number of machines
          for any production step.
        </p>
      </PlaceholderHelpBox>

      <ViewChangelistModalFactory v-else
                                  v-for="changes in changesByFactory"
                                  :key="changes.factory.id"
                                  :changelist="changelist"
                                  :changes="changes"
                                  :production-step-ids-to-clear="productionStepIdsToClear"
                                  style="flex: 0 0 auto;" />
    </div>
  </el-dialog>
</template>

<style scoped>
.dialogContent {
  max-height: 70vh;
  overflow: auto;
  padding-right: 8px;
}
</style>