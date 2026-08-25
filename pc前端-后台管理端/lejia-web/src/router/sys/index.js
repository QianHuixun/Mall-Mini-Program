/*
 * @Author: 沙晓
 * @Date: 2024-01-25 14:52:29
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-04-07 11:44:28
 * @Description: 系统管理
 * @FilePath: /lejia-web/src/router/sys/index.js
 */
// 设置相关路由
// 异步加载
const setAccount = () => import("@/views/sys/Account"); //账号设置
const setRole = () => import("@/views/sys/Role"); //角色设置
const setLog = () => import("@/views/sys/Log"); //操作日志
const setVersion = () => import("@/views/sys/Version"); //版本屏蔽设置
const setGzh = () => import("@/views/sys/gzh")  //公众号消息推送
const MallAdmin = () => import("@/views/sys/MallAdmin") //商城管理员
export default [
  {
    path: '/sys/user', // 账号设置
    name: "setAccount",
    component: setAccount,
    meta: {
      notKeepAlive: true
    }
  }, {
    path: '/sys/role', // 角色设置
    name: "setRole",
    component: setRole,
    meta: {
      notKeepAlive: true
    }
  }, {
    path: '/sys/log', // 操作日志
    name: "setLog",
    component: setLog,
    meta: {
      notKeepAlive: true
    }
  }, {
    path: '/sys/gzh',
    name: 'setGzh',
    component: setGzh,
    meta: {
      notKeepAlive: true
    }
  }, {
    path: '/sys/version',
    name: 'setVersion',
    component: setVersion,
    meta: {
      notKeepAlive: true
    }
  }, {
    path: '/sys/mallAdmin',
    name: 'mallAdmin',
    component: MallAdmin,
    meta: {
      notKeepAlive: true
    }
  }
]