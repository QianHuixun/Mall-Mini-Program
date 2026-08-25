<!-- 
@name: Header.vue 
@description: header组件 
@author: sx
@date: 2020/03/20
-->
<template lang="html">
  <el-header id="header">
    <span class="title left">{{
      userIdentity == 1
        ? yunyName + "运营管理中心"
        : userIdentity == 2
        ? marketName
        : orgName
    }}</span>
    <span class="title">{{ loginName }}</span>
    <el-dropdown trigger="click">
      <span class="el-dropdown-link">
        <i class="iconfont icongengduo"></i>
      </span>
      <el-dropdown-menu slot="dropdown" class="header-dropdown-menu">
        <el-dropdown-item
          icon="iconfont icontuichu-"
          @click.native="handleUpdate"
        >
          修改密码
        </el-dropdown-item>
        <el-dropdown-item icon="iconfont icontuichu-" @click.native="loginOut">
          退出登录
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
    <password-upd ref="PasswordUpd"></password-upd>
    <audio
      id="audio"
      src="@/assets/images/notify.mp3"
      controls="controls"
      hidden
    />
    <audio id="refund_audio"
    src="@/assets/images/refundNotify.mp3" controls="controls" hidden />
  </el-header>
</template>
<script>
import qs from "qs";
import PasswordUpd from "./PasswordUpd";

export default {
  data() {
    return {
      marketName: this.$store.state.marketName,
      orgName: this.$store.state.userinfo.lastAccessOrg
        ? this.$store.state.userinfo.lastAccessOrg.name
        : " ", //公司名称
      // yunyName: '泽起邻里运营管理中心',
      loginName: this.$store.state.userinfo.nickname,
      noticeNumTimer: "",
      refundTimer: "",
    };
  },
  components: {
    PasswordUpd
  },
  created() {},
  mounted() {
    console.log(this.$store.state);
    this.setNoticeNumTimer();
  },
  beforeDestroy() {
    this.clearTimer();
  },
  computed: {
    userIdentity: function() {
      return this.$store.state.userIdentity; //1 运营商 2 市场 3公司
    },
    yunyName() {
      return this.$store.state.saasName
        ? this.$store.state.saasName
        : localStorage.getItem("saasName");
    }
  },
  methods: {
    loginOut: function() {
      const params = {};
      this.$confirm("确定退出该账号?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          axios
            .post(api.login.removeToken, qs.stringify(params), {
              headers: {
                Authorization: this.$store.state.token
              }
            })
            .then(() => {
              this.$message.success("退出成功！");
              if(localStorage.getItem("ascription") == 13) {
                this.$router.push("/login?ascription=13");
              } else {
                this.$router.push("/login");
              }
            });
        })
        .catch(() => {
          // this.$message({
          //   type: 'info',
          //   message: '已取消删除'
          // });
        });

      // access_token: this.$store.state.token
    },
    handleUpdate() {
      console.log(this.$refs);
      this.$refs.PasswordUpd.show();
    },
    /* 待发货订单提示 */
    LoadMsg() {
      axios.post(api.popup.deliveredOrder).then(res => {
        const hasResult = Object.prototype.hasOwnProperty.call(res, 'result');
        res = hasResult ? res.result : res;
        if (res) {
          // const h = this.$createElement;

          this.$notify({
            title: "订单通知",
            duration: 12000,
            dangerouslyUseHTMLString: true,
            message: `<i  style="color: teal;">当前有${res}个待发货订单</i>`,
            offset: 45,
            onClick: () => {
              if (this.userIdentity == 1)
                this.$router.push({
                  path: "/order/mall",
                  query: { status: "DELIVERED_ORDER" }
                });
              else if (this.userIdentity == 2) {
                this.$router.push({
                  path: "/order/market",
                  query: { status: "DELIVERED_ORDER" }
                });
              }
            }
          });
        }
      });
    },
     /* 退款订单提示 */
    LoadRefundMsg() {
      axios.post(api.popup.newRefundVoice).then(res => {
        const hasResult = Object.prototype.hasOwnProperty.call(res, 'result');
        res = hasResult ? res.result : res;
        if (res) {
          let audio = document.getElementById("refund_audio");
          audio
            .play()
            .then(() => {})
            .catch(() => {
            });
          this.$notify({
            title: "退款订单通知",
            duration: 12000,
            dangerouslyUseHTMLString: true,
            message: `<i  style="color: teal;">当前有${res}个待处理退款订单</i>`,
            offset: 45,
            onClick: () => {
                this.$router.push({
                  path: "/order/refund?status=REFUND_APPLYING",
                });
            }
          });
        }
      });
    },
    /**
     * @desc 新订单提示
     */
    newOrderVoice() {
      axios.post(api.popup.newOdrderVoice).then(res => {
        // console.log(res);
        const hasResult = Object.prototype.hasOwnProperty.call(res, 'result');
        res = hasResult ? res.result : res;
        if (res) {
          let audio = document.getElementById("audio");
          audio
            .play()
            .then(() => {})
            .catch(() => {
              // console.log(err);
              // 不支持自动播放
              // this.$alert('提示', '请打开新订单语音提示', {
              //   confirmButtonText: '确定 ',
              //   callback: (action) => {
              //     this.$message.success('语音提示已打开');
              //     audio.play();
              //   },
              // });
            });
        }
      });
    },
    /**
     * @desc 设置每2分钟获取各类信息数量
     */
    setNoticeNumTimer() {
      this.LoadMsg();
      if (this.noticeNumTimer) {
        clearInterval(this.noticeNumTimer);
      }
      if(this.refundTimer) {
        clearInterval(this.refundTimer);
      }
      this.noticeNumTimer = setInterval(() => {
        this.LoadMsg();
        this.newOrderVoice();
      }, 15000);

      this.refundTimer = setInterval(()=> {
        this.LoadRefundMsg();
      },27000);

    },
    /**
     * @desc 清空计时器
     */
    clearTimer() {
      clearInterval(this.noticeNumTimer);
      clearInterval(this.refundTimer);
    }
  }
};
</script>
<style lang="less" scoped>
@import url("~@/assets/css/variable.less");

.el-header {
  height: 50px !important;
  width: 100%;
  -webkit-box-shadow: 0 2px 20px 0 rgba(15, 12, 70, 0.1);
  box-shadow: 0 2px 20px 0 rgba(15, 12, 70, 0.1);

  display: flex;
  align-items: center;
  justify-content: space-between;

  background: #fff;

  .left {
    flex: 1;
    font-weight: bold;
    font-size: 18px;
  }

  .el-dropdown {
    padding: 15px 0 15px 20px;
    height: 50px;

    font-size: 20px;
    line-height: 20px;

    cursor: pointer;

    .icongengduo {
      font-size: 20px;
    }
  }
}

.el-dropdown-menu {
  .el-dropdown-menu__item {
    border-top: 1px solid @color-line;

    line-height: 40px;

    &:first-child {
      border-top: 0;
    }
  }
}

.iconfont {
  float: left;
  margin-left: 8px;

  font-size: 20px;
}
</style>
<style lang="less">
.header-dropdown-menu {
  .iconfont {
    float: left;
    margin-left: 8px;
    font-size: 20px;
  }
}
</style>
