/*
 * @Author: 沙晓
 * @Date: 2022-05-17 16:38:43
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-06-06 15:04:26
 * @Description: file content
 * @FilePath: /lejia-web/src/router/index.js
 */
import Vue from "vue";
import VueRouter from "vue-router";

import base from "./base";
import mall from "./mall";
import goods from "./goods";
import member from "./member";
import activity from "./activity";
import coupon from "./coupon";
import order from "./order";
import operation from "./operation";
import vendor from "./vendor";
import kry from "./kry";
import sys from "./sys";

import publicity from "./publicity";
import market from "./market";
import cookfd from "./cookfd";

import company from "./company";
import data from "./data";
import ads from "./ads";
import gzh from "./gzh";
import finance from "./finance";
import jd from './jd';
// 异步加载
const Index = () => import("@/views/Index");
const Login = () => import("@/views/Login");
const Home = () => import("@/views/Home");
const bigData = () => import("@/views/bigData/bigData");
Vue.use(VueRouter);

const routes = [
  {
    path: "/login",
    name: "login",
    component: Login
  },
  {
    path: "",
    component: Index,
    children: [
      {
        path: "/index",
        name: "home",
        component: Home
      },
      ...base,
      ...mall,
      ...goods,
      ...member,
      ...activity,
      ...coupon,
      ...order,
      ...operation,
      ...vendor,
      ...kry,
      ...sys,
      ...publicity,
      ...market,
      ...cookfd,
      ...company,
      ...data,
      ...ads,
      ...finance,
      ...jd
    ]
  },
  {
    path: "/bigData",
    name: "bigData",
    component: bigData,
    meta: {
      notKeepAlive: true
    }
  },
  ...gzh
];

const router = new VueRouter({
  mode: "history",
  base:
    process.env.VUE_APP_TITLE === "test" ||
      process.env.VUE_APP_TITLE === "development"
      ? "/zy"
      : "/",
  routes
});

router.beforeEach(function (to, from, next) {
  if (to.path == "/") {
    if (localStorage.getItem("userinfo")) {
      next("/index");
    } else {
      next("/login");
    }
  } else {
    next();
  }
});

export default router;
