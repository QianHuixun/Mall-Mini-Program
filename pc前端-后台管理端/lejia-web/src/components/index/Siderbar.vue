<!-- 
@name: Siderbar.vue 
@description: 左侧导航页 
@author: sx
@date: 2020/03/20
-->
<template lang="html">
  <!-- 左侧导航 -->
  <div class="mainsiderbar-container">
    <!-- 导航栏头部 -->
    <div class="siderbar-header">
      <img :src="saasLogo" alt="" height="30px">
    </div>
    <!-- 菜单 -->
    <el-menu :default-active="currentMenu.pkey" text-color="#a0aabb" active-text-color="#fff">
      <template v-for="(item, index) in menuList">
        <el-menu-item :index="item.pkey" :key="item.pkey" @click.native.stop="clickMenus(item)" :class="item.url === $route.path ? 'is-active' : ''">
          <i :class="item.icon"></i>
          <span>{{item.title}}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>
<script>
import { mapState, mapActions } from 'vuex';

export default {
  data() {
    return {
      imgUrl: require('@/assets/images/logo.png'),
      currentMenu: {},
      menuList: this.$store.state.menuList,
      activeMenu: this.$store.state.activeMenu,
    };
  },
  computed: {
    // ...mapState({
    // menuList: state => state.menuList,
    // activeMenu: state => state.activeMenu
    // })
    saasLogo() {
      return this.$store.state.saasPhoto ? this.$store.state.saasPhoto : localStorage.getItem('saasPhoto')
    }
  },
  watch: {
    $route(to, from) {
      this.initMenu();
    },
  },
  mounted() {
    this.initMenu();
  },
  methods: {
    /**
     * 初始化菜单
     */
    initMenu: function () {
      const moduleName = this.$route.path.split('/')[1];
      if (this.$route.path == '/') {
        this.currentMenu = this.menuList[0];
      } else {
        var currentMenu = this.menuList.filter((item) => {
          if (item.url) {
            // console.log(item.url.split("/")[1])
            if (item.url.split('/')[1] == moduleName) {
              return item;
            }
          }
        });
        this.currentMenu = currentMenu[0];
      }
      this.$store.dispatch('GET_ACTIVEMENU', this.currentMenu).then(() => {
        this.$emit('handleSubMenu');
      });
    },
    /**菜单点击事件*/
    clickMenus: function (item) {
      console.log(item)
      if (!item.url) {
        this.$message('敬请期待');
        return;
      }

      this.currentMenu = item;
      this.$store.dispatch('GET_ACTIVEMENU', this.currentMenu).then(() => {
        this.$emit('handleSubMenu');
      });

      if (item.url != this.$route.path) {
        this.$store.dispatch('GET_ACTIVENAME', item.title);
        this.$router.push(item.url);
      }
    },
  },
};
</script>
<style lang="less" scoped>
.mainsiderbar-container {
  flex-shrink: 0;
  width: 120px;
  height: 100vh;
  overflow-y: auto;

  background: #2f4050;

  .siderbar-header {
    padding: 10px 0;
    height: 50px;
    -webkit-box-shadow: 0 2px 20px 0 rgba(15, 12, 70, 0.1);
    box-shadow: 0 2px 20px 0 rgba(15, 12, 70, 0.1);

    text-align: center;

    img {
      font-size: 0;
    }
  }

  .el-menu {
    padding-top: 20px;
    border-right: 0;

    background: none;

    .el-menu-item {
      height: 42px;

      line-height: 42px;

      background: none;

      .iconfont {
        display: inline-block;
        margin-right: 5px;
        width: 18px;

        font-size: 16px;
      }
    }

    .is-active,
    .el-menu-item:hover {
      position: relative;

      i,
      span {
        color: #fff;
      }

      &::after {
        content: '';
        position: absolute;
        right: 10px;
        top: 12px;

        display: block;
        width: 4px;
        height: 20px;
        border-radius: 2px;

        background: #fafafa;
      }
    }
  }
}
</style>