<script setup>
import {Delete, Edit,} from '@element-plus/icons-vue';
import MachineCountInput from "@/components/factories/MachineCountInput.vue";
import QuantityDisplay from "@/components/factories/QuantityDisplay.vue";

defineProps(["step"]);
</script>

<template>
  <div class="step">
    <div style="overflow:auto;">
      <div class="stepIcon">
        <img :src="step.machine.icon.url" :alt="step.machine.name"/>
        <div style="vertical-align: top; display: inline; line-height: 60px">
          x
          <quantity-display :quantity="step.machineCount"/>
          &emsp;
        </div>
        <img :src="step.recipe.icon.url" :alt="step.recipe.name"/>
      </div>
      <div class="stepInfo">
        <div class="stepName">Recipe: {{ step.recipe.name }}</div>
        <div class="stepThroughput">
          <div>
            <div v-for="resource in step.throughput.output" style="margin-right: 10px; ">
              <quantity-display :quantity="resource.quantity"/>
              <img :src="resource.item.icon.url" :alt="resource.item.name" style="margin-left: 3px"/>
              <span>{{ resource.item.name }}</span>
              <span class="unit">/ min</span>
            </div>
          </div>
          <span>
            &#x27F5;&ensp;
          </span>
          <div>
            <div v-for="resource in step.throughput.input" style="margin-right: 10px">
              <quantity-display :quantity="resource.quantity"/>
              <img :src="resource.item.icon.url" :alt="resource.item.name" style="margin-left: 3px"/>
              <span>{{ resource.item.name }}</span>
              <span class="unit">/ min</span>
            </div>
          </div>
        </div>
      </div>
      <div class="stepActions">
        <machine-count-input v-model:quantity="step.machineCount"/>
        &ensp;
        <el-button-group>
          <el-button type="" :icon="Edit"/>
          <el-button type="danger" :icon="Delete"/>
        </el-button-group>
      </div>
    </div>
  </div>
</template>

<style scoped>
.step {
  margin-left: 80px;
  background-color: #555555;
  border-radius: 16px;
  padding: 8px 8px 0;
  margin-top: 4px;
  margin-bottom: 4px;
}

.stepIcon {
  float: left;
}

.stepIcon img {
  height: 48px;
  width: 48px;
}

.stepInfo {
  float: left;
  margin-left: 16px;
  margin-top: 2px;
  vertical-align: top;
  overflow: auto;
}

.stepName {
  font-size: 16px;
}

.stepThroughput {
  margin-top: 4px;
  display: flex;
  flex-wrap: wrap;
  max-width: 700px;
}

.stepThroughput > div {
  display: inline;
}

.stepThroughput img {
  height: 24px;
  width: 24px;
}

.stepThroughput span {
  line-height: 24px;
  vertical-align: top;
  margin-left: 5px;
}

.unit {
  color: #909090;
}

.stepActions {
  float: right;
  padding: 8px 8px 0;
  margin-bottom: 8px;
}
</style>