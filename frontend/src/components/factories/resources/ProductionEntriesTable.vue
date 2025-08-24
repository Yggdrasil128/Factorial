<script setup lang="ts">
import type {ProductionEntry} from '@/types/model/standalone';
import {type ItemStore, useItemStore} from '@/stores/model/itemStore';
import IconImg from '@/components/common/IconImg.vue';
import {type RouteLocationNormalizedLoadedGeneric, type Router, useRoute, useRouter} from 'vue-router';
import QuantityByChangelistDisplay from "@/components/factories/resources/QuantityByChangelistDisplay.vue";
import FractionDisplay from "@/components/common/FractionDisplay.vue";

export interface ProductionEntriesTableProps {
  productionEntries: ProductionEntry[];
}

defineProps<ProductionEntriesTableProps>();

const router: Router = useRouter();
const route: RouteLocationNormalizedLoadedGeneric = useRoute();

const itemStore: ItemStore = useItemStore();

async function onClick(itemId: number): Promise<void> {
  const hash: string = '#item' + itemId;
  if (hash === route.hash) {
    await router.replace({
      name: 'factories',
      params: { factoryId: route.params.factoryId },
    });
  }
  await router.push({
    name: 'factories',
    params: { factoryId: route.params.factoryId },
    hash: hash,
  });
}

</script>

<template>
  <table>
    <tr v-for="productionEntry in productionEntries" :key="productionEntry.itemId">
      <td>
        <quantity-by-changelist-display :quantity="productionEntry.quantity" is-throughput hide-throughput-unit/>
      </td>
      <td>
        <icon-img :icon="itemStore.getById(productionEntry.itemId)?.iconId" :size="24" />
      </td>
      <td>
        <span class="itemName" @click="onClick(productionEntry.itemId)">
          {{ itemStore.getById(productionEntry.itemId)?.name }}
        </span>
        <fraction-display :fraction="undefined" is-throughput/>
      </td>
    </tr>
  </table>
</template>

<style scoped>
table {
  border-collapse: collapse;
}

td {
  padding: 0 4px;
}

.itemName:hover {
  text-decoration: underline;
  cursor: pointer;
}
</style>