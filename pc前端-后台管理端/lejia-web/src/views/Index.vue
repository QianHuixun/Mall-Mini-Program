`
<!-- 
@name: Index.vue 
@description: 框架页 
@author: sx
@date: 2020/03/19 
-->
<template lang="html">
  <el-container style="height: 100vh;" class="zy-container">
    <!-- 左侧侧边栏 -->
    <siderbar-frame
      ref="siderbarFrame"
      @handleSubMenu="getMenu"
    ></siderbar-frame>
    <!-- 右侧 -->
    <div class="right-container">
      <!-- 顶部栏 -->
      <header-frame></header-frame>
      <el-main>
        <!-- 二级菜单 -->
        <subsiderbar-frame
          ref="subsiderbarFrame"
          v-if="visible"
        ></subsiderbar-frame>
        <!-- 主体页面 -->
        <keep-alive>
          <router-view
            class="jc-container r-3x box-shadow-2 bg-white wrapper-sm vbox"
            v-if="!$route.meta.notKeepAlive && isRouterAlive"
            :key="$route.fullPath + compkey"
          ></router-view>
        </keep-alive>
        <router-view
          class="jc-container r-3x box-shadow-2 bg-white wrapper-sm vbox"
          v-if="$route.meta.notKeepAlive && isRouterAlive"
          :key="$route.fullPath + compkey"
        ></router-view>
        <!-- 主体页面 end -->
      </el-main>
    </div>
  </el-container>
</template>
<script>
import headerFrame from "@/components/index/Header"; //顶部栏
import siderbarFrame from "@/components/index/Siderbar"; //左侧导航栏
import subsiderbarFrame from "@/components/index/Subsiderbar"; //子导航栏

export default {
  data() {
    return {
      iconSrc: require('@/assets/images/favicon_13.png'),
      isRouterAlive: true, // 刷新页面控量
      visible: true,
      menuList: [],
      compkey: 0
    };
  },
  created() {
    if(localStorage.getItem("ascription") == 13) {
      document.title="滨海民生商城管理后台";
      document.querySelector("link[rel~='icon']").href = this.iconSrc;
    }
  },
  mounted() {
    // this.getOrder();
  },
  watch: {
    $route: {
      handler: function(val, oldVal) {
        this.reload();
      },
      deep: true
    }
  },
  methods: {
    getMenu: function() {
      this.menuList = this.$store.state.activeMenu;
      if (!this.menuList.sub.length) {
        this.visible = false;
        return;
      }
      this.visible = true;
      setTimeout(() => {
        this.$refs.subsiderbarFrame.menuList = this.$store.state.activeMenu;
        this.$refs.subsiderbarFrame.getActiveName();
      }, 0);
    },
    reload() {
      console.log(1);
      this.isRouterAlive = false;
      this.compkey = Math.random();
      this.$nextTick(function() {
        setTimeout(() => {
          this.isRouterAlive = true;
        }, 100);
      });
    }
    // getOrder() {
    //   let response = true;
    //   if (response) {
    //     let audio = document.getElementById("audio");
    //     this.$notify.info({
    //       title: '订单提醒',
    //       message: '你有新的订单，请及时处理'
    //     });
    //     audio.play().then(() => {}).catch(err => {
    //       // 不支持自动播放
    //       this.$alert('提示', '请打开新订单语音提示', {
    //         confirmButtonText: '确定 ',
    //         callback: action => {
    //           this.$message.success('语音提示已打开');
    //           audio.play();
    //         }
    //       });
    //     });

    //   }
    // },
  },
  components: {
    headerFrame,
    siderbarFrame,
    subsiderbarFrame
  }
};
</script>
<style lang="less" scoped>
.zy-container {
  display: flex;
  display: -webkit-flex;
  display: -moz-flex;
  width: 100%;
  height: 100vh;

  .right-container {
    height: 100%;
    flex: 1;

    .el-main {
      display: flex;
      display: -webkit-flex;
      display: -moz-flex;
      height: calc(100% - 50px);
      padding: 10px 10px 20px 10px;
      box-sizing: border-box;

      .jc-container {
        flex: 1;
        width: 0; //解决flex布局内容超出问题
        overflow-y: auto;
      }

      .jc-container {
        //隐藏滚动条
        overflow-y: scroll;
        // scrollbar-color: transparent transparent;
        scrollbar-track-color: transparent;
        -ms-scrollbar-track-color: transparent;
      }
    }
  }
}
</style>
