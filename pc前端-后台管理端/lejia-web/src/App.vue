<template>
  <div id="app">
    <router-view class="wx-pages" />
  </div>
</template>
<script>
export default {
  name: "App",
  mounted() {
    //检测关闭浏览器
    var _beforeUnload_time = 0,
      _gap_time = 0;
    //浏览器关闭,则清除localStorage
    window.onunload = function() {
      _gap_time = new Date().getTime() - _beforeUnload_time;

      if (_gap_time <= 5) {
        //浏览器关闭
        localStorage.clear();
      }
    };
    //即将离开当前页面(刷新或关闭)时记录当前时间
    window.onbeforeunload = function() {
      _beforeUnload_time = new Date().getTime();
    };
  },
  /**
   * 离开页面时清除localStorage
   */
  destroyed() {
    localStorage.clear();
  }
};
</script>
<style lang="less">
@import url("~@/assets/css/common.css");
@import url("~@/assets/css/variable.less");
@import url("~@/assets/css/components/tableContaner.less");
@import url("~@/assets/css/components/dialog.less");
</style>