<script setup lang="ts">
import { WarningFilled } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();

</script>

<template>
  <div class="main">
    <div class="box">
      <div class="headline">
        <el-icon :size="32">
          <WarningFilled />
        </el-icon>
        <h3>You don't have any save loaded at the moment.</h3>
      </div>
      <p>
        Go to
        <el-link class="link" type="primary" @click="router.push({ name: 'savesAndGames' })">
          Saves / Games
        </el-link>
        and load a save.
      </p>
    </div>
  </div>
</template>

<style scoped>
.main {
  width: 100%;
  display: flex;
  justify-content: center;
}

.box {
  width: 500px;
  background-color: #4b4b4b;
  border-radius: 8px;
  padding: 0 16px;
}

.headline {
  display: flex;
  align-items: center;
  gap: 12px;
}

.link {
  vertical-align: unset;
  --el-link-hover-text-color: var(--el-color-primary-dark-2) !important;
}
</style>