/*
 * @Author: 沙晓
 * @Date: 2024-01-25 14:52:29
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-06-06 14:36:57
 * @Description: file content
 * @FilePath: /lejia-web/vue.config.js
 */
const path = require("path");
const webpack = require('webpack')

function resolve (dir) {
  return path.join(__dirname, dir);
}


module.exports = {
  publicPath: (process.env.VUE_APP_TITLE === 'test' || process.env.VUE_APP_TITLE === 'development') ? '/zy' : '/',
  lintOnSave: false,
  // 生产环境是否生成 sourceMap 文件
  productionSourceMap: false,
  // 开启 CSS source maps?
  css: {
    sourceMap: false,
  },
  chainWebpack: (config) => {
    config.resolve.alias
      .set("@", resolve("src"))
      .set("@/static", resolve("public"));
    config.plugin('provide').use(webpack.ProvidePlugin, [{
      $: 'jquery',
      jquery: 'jquery',
      jQuery: 'jquery',
      'window.jQuery': 'jquery'
    }])
  },
  //跨域问题
  devServer: {
    proxy: {
      "/api": {
        target: "http://192.168.128.91",
        changeOrigin: true,
        pathRewrite: {
          "^/api": ""
        }
      },
      "/foo": {
        target: "http://192.168.128.91",
        changeOrigin: true,
        pathRewrite: {
          "^/foo": ""
        }
      },
    },
  }
}