<script setup>
import {Delete, Edit,} from '@element-plus/icons-vue';
import MachineCountInput from "@/components/factories/MachineCountInput.vue";
import QuantityDisplay from "@/components/factories/QuantityDisplay.vue";
import IconImg from "@/components/IconImg.vue";

defineProps(["step", "itemMap"]);
</script>

<template>
  <div class="step">
    <div style="overflow:auto;">
      <div class="stepIcon">
        <icon-img :icon="step.machine.icon" :size="48"/>
        <div style="vertical-align: top; display: inline; line-height: 60px">
          x
          <quantity-display :quantity="step.machineCount"/>
          &emsp;
        </div>
        <icon-img :icon="step.recipe.icon" :size="48"/>
      </div>
      <div class="stepInfo">
        <div class="stepName">Recipe: {{ step.recipe.name }}</div>
        <div class="stepThroughput">
          <div>
            <div v-for="resource in step.output" style="margin-right: 10px; ">
              <quantity-display :quantity="resource.quantity"/>
              <icon-img :icon="itemMap[resource.itemId].icon" :size="24" style="margin-left: 3px;"/>
              <span>{{ itemMap[resource.itemId].name }}</span>
              <quantity-display :quantity="null" show-unit/>
            </div>
            <div v-if="step.output.length === 0" class="nothing">(nothing)</div>
          </div>
          <span>
            &#x27F5;&ensp;
          </span>
          <div>
            <div v-for="resource in step.input" style="margin-right: 10px">
              <quantity-display :quantity="resource.quantity"/>
              <icon-img :icon="itemMap[resource.itemId].icon" :size="24" style="margin-left: 3px;"/>
              <span>{{ itemMap[resource.itemId].name }}</span>
              <quantity-display :quantity="null" show-unit/>
            </div>
            <div v-if="step.input.length === 0" class="nothing">(nothing)</div>
          </div>
        </div>
      </div>
      <div class="stepActions">
        <machine-count-input v-model:quantity="step.machineCount" :production-step-id="step.id"/>
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

.stepThroughput span {
  line-height: 24px;
  vertical-align: top;
  margin-left: 5px;
}

.nothing {
  color: #a0a0a0;
}

.nothing {
  margin-top: 3px;
}

.stepActions {
  float: right;
  padding: 8px 8px 0;
  margin-bottom: 8px;
}
</style>