import {createApp} from 'vue';
import * as VueRouter from 'vue-router';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import mitt from 'mitt';
import axios from "axios";

import App from '@/App.vue';
import Home from "@/components/Home.vue";
import About from "@/components/About.vue";
import FactoriesOverview from "@/components/factories/FactoriesOverview.vue";
import DevTools from "@/components/devtools/DevTools.vue";
import EditFactoryModal from "@/components/factories/EditFactoryModal.vue";
import EditChangelistModal from "@/components/factories/EditChangelistModal.vue";
import EditProductionStepModal from "@/components/factories/EditProductionStepModal.vue";

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080'
});

const routes = [
    {path: '/', component: Home},
    {path: '/about', component: About},
    {
        path: '/factories/:factoryId?',
        name: 'factories',
        component: FactoriesOverview,
        children: [
            {
                path: 'new',
                name: 'newFactory',
                components: {modal: EditFactoryModal}
            },
            {
                path: 'edit/:editFactoryId',
                name: 'editFactory',
                components: {modal: EditFactoryModal}
            },
            {
                path: 'newChangelist',
                name: 'newChangelist',
                components: {modal: EditChangelistModal}
            },
            {
                path: 'editChangelist/:editChangelistId',
                name: 'editChangelist',
                components: {modal: EditChangelistModal}
            },
            {
                path: 'newProductionStep',
                name: 'newProductionStep',
                components: {modal: EditProductionStepModal}
            },
            {
                path: 'editProductionStep/:editProductionStepId',
                name: 'editProductionStep',
                components: {modal: EditProductionStepModal}
            },
        ]
    },
    {path: '/devtools', component: DevTools},
];

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes,
});

createApp(App)
    .use(router)
    .use(ElementPlus)
    .provide("globalEventBus", mitt())
    .provide("axios", axiosInstance)
    .mount('#app');
