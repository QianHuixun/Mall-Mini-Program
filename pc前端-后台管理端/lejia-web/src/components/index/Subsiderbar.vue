<template lang="html">
  <!-- 子导航栏 -->
  <div class="subsiderbar-container">
    <el-menu :default-active="$route.path" class="el-menu-vertical-demo">
      <template v-for="(item,index) in menuList.sub">
        <!-- <el-menu-item-group> -->
          <!-- <template slot="title">{{item.title}}</template> -->
          <el-menu-item :key="index" :index="item.url"  @click="clickMenus(item)" :class="item.url === $route.path ? 'is-active' : ''">{{item.title }}</el-menu-item>
        <!-- </el-menu-item-group> -->
      </template>
    </el-menu>
  </div>
</template>
<script>
export default {
  data() {
    return {
      menuList: [],
      activeMenu: {}
    };
  },
  computed: {},
  created() {},
  mounted() {

  },
  methods: {
    getActiveName: function() {
      const _this = this;
      // menuList = JSON.parse(this.$store.state.menuList);

      this.menuList.sub.forEach((item) => {
        // item.sub.forEach((subitem) => {
          if (_this.$route.path == item.url) {
            _this.$store.dispatch("GET_ACTIVENAME", item.title);
          }
        // });
      });

    },
    /**
     *  菜单点击事件
     */
    clickMenus: function(item) {
      console.log(item)
      if (!item.url) {
        this.$message("敬请期待");
        return;
      }

      this.activeMenu = item;
      if (item.url != this.$route.path) {
        this.$store.dispatch("GET_ACTIVENAME", item.title);
        this.$router.push(item.url);
      }
    }
  },
}
</script>
<style lang="less" scoped>
.subsiderbar-container {
  width: 130px;
  height: calc(100vh - 80px);
  padding: 0 10px 10px 10px;

  .el-menu {
    border-right: 0;

    background: none;

    .el-menu-item-group {
      // padding: 12px 0;
      // margin: 0 10px;

      &::after {
        content: "";
        display: block;
        height: 1px;
        margin: 12px 10px;

        background: #e3e2e5;
      }

      &:last-of-type::after {
        display: none;
      }
    }

    .el-menu-item {
      position: relative;

      display: block;
      height: 42px;
      margin: 2px 0;

      line-height: 42px;
      font-size: 12px;


      [class^=el-icon-] {
        width: 18px;
      }
    }

    .el-menu-item:not(.is-active):hover {
      position: relative;
      border-radius: 6px;

      color: #35323b;
      background: #e9eaf0;

      &.is-active::after {
        border-color: transparent transparent transparent #35323b;
      }
    }

    .is-active {
      position: relative;
      border-radius: 6px;

      color: #fff;
      background: #4696e7;

      &::after {
        position: absolute;
        right: 10px;
        top: 16px;

        content: "";
        width: 0;
        height: 0;
        border-color: transparent transparent transparent #fff;
        border-style: solid;
        border-width: 4px 0 4px 6px;
      }

    }
  }
}
</style>
<style lang="less">
.subsiderbar-container {

  .el-menu-item-group__title {
    padding: 12px 10px !important;

    font-weight: 700;
    font-size: 12px;

    color: #595961;
  }

  .el-menu-item {
    padding-left: 10px !important;

    background: none;
  }
}
</style>