"use strict";
import Vue from "vue";
import axios from "axios";
import router from "@/router";
import store from '@/store'
// Full config:  https://github.com/axios/axios#request-config
// axios.defaults.baseURL = process.env.baseURL || process.env.apiUrl || '';
// axios.defaults.headers["Authorization"] = store.state.token;
axios.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded;charset=UTF-8";
let config = {
  // baseURL: process.env.baseURL || process.env.apiUrl || ""
  timeout: 60 * 1000, // Timeout
  // withCredentials: true, // Check cross-site Access-Control
};
const _axios = axios.create(config);
_axios.interceptors.request.use(
  function (config) {
    // Do something before request is sent
    if (!config.headers.Authorization || typeof config.headers.Authorization != 'string')
      config.headers.Authorization = store.state.token;
    return config;
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error);
  }
);
// Add a response interceptor
_axios.interceptors.response.use(
  function (response) {
    //如果返回时false，则显示错误信息，并返回

    /**获取blob文件的封装回调 */
    if (response.request.responseType == 'blob') {
      return response;
    } else if (!response.data.success) {
      Vue.prototype.$message.error(response.data.msg || response.data.message || "网络数据异常，请重试");
      return Promise.reject(response);

    }
    // console.debug('response: ', response.data.data);
    // Do something with response data
    return response.data.data || response.data.result || response.data;
  },
  function (error) {
    console.log(error);
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 返回 401 清除token信息并跳转到登录页面
          Vue.prototype.$message.error("登录超时");
          router.replace({
            path: '/login'
          })
      }
    }

    Vue.prototype.$message.error(error.data.msg || error.data.message || "网络数据异常，请重试");
    // Do something with response error
    // return Promise.reject(error.data);
  }
);

Plugin.install = function (Vue, options) {
  Vue.axios = _axios;
  window.axios = _axios;
  Object.defineProperties(Vue.prototype, {
    axios: {
      get() {
        return _axios;
      }
    },
    $axios: {
      get() {
        return _axios;
      }
    }
  });
};
Vue.use(Plugin);
export default Plugin;