import { createApp } from 'vue';
import ElementPlus from 'element-plus';
import router from './router/routes';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import mitt from 'mitt';
import axios from 'axios';
import { createPinia } from 'pinia';
import piniaPluginPersistedState from 'pinia-plugin-persistedstate';

import App from './App.vue';

const pinia = createPinia();
pinia.use(piniaPluginPersistedState);

const app = createApp(App);
app.use(pinia);
app.use(router);
app.use(ElementPlus);
app.provide('globalEventBus', mitt());
app.provide(
  'axios',
  axios.create({
    baseURL: 'http://localhost:8080'
  })
);

app.mount('#app');
