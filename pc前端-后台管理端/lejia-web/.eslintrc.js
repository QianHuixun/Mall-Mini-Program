/*
 * @Author: 沙晓
 * @Date: 2024-03-18 16:20:34
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-04-12 16:33:02
 * @Description: file content
 * @FilePath: /lejia-web/.eslintrc.js
 */
module.exports = {
  "env": {
    "browser": true,
    "es6": true
  },
  "extends": [
    "eslint:recommended",
    "plugin:vue/essential"
  ],
  "globals": {
    "Atomics": "readonly",
    "SharedArrayBuffer": "readonly"
  },
  "parserOptions": {
    "ecmaVersion": 2018,
    "sourceType": "module"
  },
  "plugins": [
    "vue"
  ],
  "rules": {
    "no-useless-escape": 0
  },
  "globals": {
    "axios": "writable",
    "api": "writable",
    "require": "writable",
    "process": "writable"
  }
};