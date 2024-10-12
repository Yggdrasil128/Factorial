import { createApp } from 'vue';
import ElementPlus from 'element-plus';
import router from './router';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import mitt from 'mitt';
import axios from 'axios';
import { createPinia } from 'pinia';

import App from './App.vue';

const app = createApp(App);

app.use(createPinia());
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
