<script setup>
import ProductionStep from "@/components/factories/ProductionStep.vue";
import QuantityDisplay from "@/components/factories/QuantityDisplay.vue";
import IconImg from "@/components/IconImg.vue";

defineProps(["item", "productionSteps", "itemMap"]);

</script>

<template>
  <div class="item">
    <div style="overflow:auto;">
      <div class="itemIcon">
        <icon-img :icon="item.icon" :size="64"/>
      </div>
      <div class="itemInfo">
        <div class="itemName">{{ item.name }}</div>
        <div class="itemBalance">
          <el-tooltip effect="dark" placement="top-start" transition="none" :hide-after="0"
                      :content="'the total amount of production'">
              Total Production:
          </el-tooltip>
          <quantity-display :quantity="item.balances.production.total" color="green" show-unit/>
          &emsp;
          <el-tooltip effect="dark" placement="top-start" transition="none" :hide-after="0"
                      :content="'the minimum amount of production that is required to avoid unclogging of inputs'">
              Required Production:
          </el-tooltip>
          <quantity-display :quantity="item.balances.production.required" color="red" show-unit/>
          &emsp;
          <el-tooltip effect="dark" placement="top-start" transition="none" :hide-after="0"
                      :content="'the amonut of production that is available for reallocation'">
          Available Production:
          </el-tooltip>
          <quantity-display :quantity="item.balances.production.available" color="auto" show-unit/>
        </div>
      </div>
    </div>
    <production-step v-for="step in productionSteps" :step="step" :item-map="itemMap"/>
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
</style>