import Vue from "vue";
import App from "./App.vue";
import ElementUI from "element-ui"; 
import "element-ui/lib/theme-chalk/index.css";
import apiConfig from "./apiConfig/index";
import axios from "./plugins/axios";
import router from "./router";
import store from "./store";
import $ from 'jquery';
import echarts from 'echarts'
import QRCode from 'qrcodejs2'
import qs from 'qs';

//自定义全局搜索组件
import SearchBar from "./components/global/SearchBar";

Vue.config.productionTip = false;
Vue.prototype.$qs = qs;
Vue.use(ElementUI);
Vue.component("search-bar", SearchBar);

//重写element-ui 的 MessageBox组件
Vue.prototype.$msgbox = function (options) {
  ElementUI.MessageBox(options)
}

Vue.prototype.$echarts = echarts

//重写element-ui 的 message组件
Vue.prototype.$message = function (msg) {
  ElementUI.Message({message: msg,duration:2000})
}

Vue.prototype.$message.success = function (msg) {
  return ElementUI.Message.success({message: msg,duration:2000})
}

Vue.prototype.$message.warning = function (msg) {
 return ElementUI.Message.warning({message: msg,duration:2000})
}

Vue.prototype.$message.error = function (msg) {
  return ElementUI.Message.error({message: msg,duration:2000})
}

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
