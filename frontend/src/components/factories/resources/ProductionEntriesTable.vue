<script setup lang="ts">
import type { ProductionEntry } from '@/types/model/standalone';
import QuantityDisplay from '@/components/factories/resources/QuantityDisplay.vue';
import { useItemStore } from '@/stores/model/itemStore';
import IconImg from '@/components/common/IconImg.vue';
import { useRoute, useRouter } from 'vue-router';

export interface ProductionEntriesTableProps {
  productionEntries: ProductionEntry[];
}

defineProps<ProductionEntriesTableProps>();

const itemStore = useItemStore();
const router = useRouter();
const route = useRoute();

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
        <quantity-display :quantity="productionEntry.quantity" convert-unit />
      </td>
      <td>
        <icon-img :icon="itemStore.getById(productionEntry.itemId)?.iconId" :size="24" />
      </td>
      <td>
        <span class="itemName" @click="onClick(productionEntry.itemId)">
          {{ itemStore.getById(productionEntry.itemId)?.name }}
        </span>
        <quantity-display :quantity="undefined" show-unit convert-unit />
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