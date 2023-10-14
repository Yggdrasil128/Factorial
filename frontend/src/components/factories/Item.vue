<script setup>
import ProductionStep from "@/components/factories/ProductionStep.vue";
import QuantityDisplay from "@/components/factories/QuantityDisplay.vue";

defineProps(["item", "productionSteps"]);

</script>

<template>
  <div class="item">
    <div style="overflow:auto;">
      <div class="itemIcon">
        <img :src="item.icon.url" :alt="item.name"/>
      </div>
      <div class="itemInfo">
        <div class="itemName">{{ item.name }}</div>
        <div class="itemBalance">
          Production:
          <quantity-display :quantity="item.balances.production" color="green" show-unit/>
          &emsp;
          Consumption:
          <quantity-display :quantity="item.balances.consumption" color="red" show-unit/>
          &emsp;
          Net:
          <quantity-display :quantity="item.balances.net" color="auto" show-unit/>
        </div>
      </div>
    </div>
    <production-step v-for="step in productionSteps" :step="step"/>
  </div>
</template>

<style scoped>
.item {
  background-color: #4b4b4b;
  border-radius: 24px;
  padding: 8px;
  margin-bottom: 24px;
}

.itemIcon {
  float: left;
}

.itemIcon img {
  height: 64px;
  width: 64px;
}

.itemInfo {
  float: left;
  margin-left: 16px;
  margin-top: 5px;
  vertical-align: top;
  overflow: auto;
}

.itemName {
  font-size: 24px;
}

.itemBalance {
  margin-top: 4px;
}

.negative {
  color: #ff6666;
}

.positive {
  color: #89ff89;
}

.unit {
  color: #808080;
}
</style>