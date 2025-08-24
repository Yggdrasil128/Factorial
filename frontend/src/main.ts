import {type App, createApp} from 'vue';
import ElementPlus from 'element-plus';
import router from './router';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import mitt from 'mitt';
import axios from 'axios';
import {createPinia, type Pinia} from 'pinia';
import piniaPluginPersistedState from 'pinia-plugin-persistedstate';

import AppRoot from './AppRoot.vue';

const pinia: Pinia = createPinia();
pinia.use(piniaPluginPersistedState);

const app: App<Element> = createApp(AppRoot);
app.use(pinia);
app.use(router);
app.use(ElementPlus);
app.provide('globalEventBus', mitt());
app.provide('axios', axios.create());

app.mount('#app');
