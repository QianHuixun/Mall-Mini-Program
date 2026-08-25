import Vue from 'vue';
import {
    common,
    dropdown,
    login,
    market,
    mall,
    goods,
    marketing,
    mkt_marketing,
    cookfd,
    order,
    sys,
    sale,
    data,
    index,
    popup,
    ads,
    gzh,
    bigData,
    vendor,
    jd
} from './login'
const api = {
    common,
    dropdown,
    login,
    market,
    mall,
    goods,
    marketing,
    mkt_marketing,
    cookfd,
    order,
    sys,
    sale,
    data,
    index,
    popup,
    ads,
    gzh,
    bigData,
    vendor,
    jd

};
const Api = {
    install(Vue, options) {
        Vue.api = api;
        window.api = api;
        Object.defineProperties(Vue.prototype, {
            api: {
                get() {
                    return api;
                }
            },

        });
    }
};
Vue.use(Api)
export default Api;