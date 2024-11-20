<script setup lang="ts">
import AppHeader from '@/components/AppHeader.vue';
import NavBar from '@/components/NavBar.vue';
</script>

<template>
  <AppHeader />
  <NavBar />

  <div style="margin: 10px 20px;">
    <router-view />
  </div>
</template>

<style scoped></style>

<!--suppress CssUnusedSymbol -->
<style>
html {
  scrollbar-gutter: stable;
  background-color: #373737;
  height: 100%;
}

body {
  min-height: 100vh;
  width: 1600px !important;
  background-color: #414141;
  margin: 0 auto;
  color: #dddddd;
  font-family: Arial, Helvetica, sans-serif;
  font-size: 16px;

  --el-input-bg-color: #00000030;
  --el-fill-color-blank: #00000030;
}

.el-switch__core {
  background-color: #30303080;
}

.el-link {
  vertical-align: unset;
  --el-link-hover-text-color: var(--el-color-primary-dark-2) !important;
}

.full-width {
  width: 100%;
}

.row {
  display: flex;
  gap: 8px;
}

.row.items-center {
  align-items: center;
}

.column {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.column.items-center {
  align-items: center;
}

.row > .grow, .column > .grow {
  flex-grow: 1;
}

.row > .shrink, .column > .shrink {
  flex-shrink: 1;
}

.ellipsis {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}
</style>
