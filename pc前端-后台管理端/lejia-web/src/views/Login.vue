<template>
  <div id="login">
    <!-- 登录框 -->
    <div class="login-container">
      <!-- 左侧图片 -->
      <!--   <div class="login-left">
        <img :src="imgSrc" alt="" height="100%" />
      </div> -->
      <!-- 右侧登录/修改密码 -->
      <div class="login-right">
        <!-- 标题 -->
        <h1 class="login-title">{{ ascription== 13 ? "滨海民生" : "致一云商城"}}</h1>
        <!-- 密码登录表单 -->
        <div class="login-form">
          <el-form ref="loginForm">
            <el-form-item>
              <el-input
                ref="accountInput"
                placeholder="请输入账号"
                v-model="inputModel.account"
                prefix-icon="iconfont iconshouji"
                clearable
                @keyup.enter.native="psdLogin"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-input
                ref="passwordInput"
                placeholder="请输入密码"
                v-model="inputModel.password"
                prefix-icon="iconfont iconmima"
                show-password
                @keyup.enter.native="psdLogin"
              ></el-input>
            </el-form-item>
            <el-form-item class="el-form-last-item">
              <el-input
                ref="codeInput"
                placeholder="请输入验证码"
                v-model="inputModel.code"
                prefix-icon="iconfont iconyanzhengma"
                @keyup.enter.native="psdLogin"
              >
                <template slot="append">
                  <img
                    alt=""
                    class="code-img"
                    ref="codeImg"
                    @click="updateCode"
                  />
                </template>
              </el-input>
            </el-form-item>

            <div class="login-btn-item">
              <el-button
                class="submit-btn"
                :loading="loading"
                @click="psdLogin"
              >
                立即登录
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
    <div class="bottom-info">
      <!-- <p>Copyright &copy; 杭州泽起电子商务有限公司</p> -->
    </div>
  </div>
</template>
<script>
import qs from "qs";
// import utils from "@/assets/js/utils";
export default {
  data() {
    return {
      imgSrc: require("@/assets/images/banner_login.png"),
      iconSrc: require('@/assets/images/favicon_13.png'),
      inputModel: {
        account: "", //15825605939 市场 jftSystem 运营
        password: "", // 123456
        code: ""
      },
      loading: false, //登录按钮是否显示loading
      codeTarget:"",
      ascription: ""
    };
  },
  created() {},
  mounted() {
    this.updateCode();
    this.ascription = this.$route.query.ascription || "";
        // 滨海民生定制标题和logo
    if(this.ascription ==  13) {
      document.title="滨海民生商城管理后台";
      document.querySelector("link[rel~='icon']").href = this.iconSrc;
    }

  },
  methods: {
    /**
     * 重置inputModel
     */
    clearForm: function() {
      this.inputModel = {
        account: "",
        password: "",
        code: ""
      };
    },
    /**
     * 更新图片验证码
     */
    updateCode() {
      // axios.get(api.login.codeImg, qs.stringify({}))
      // .then(response => {
      console.log("123");
      //   });
      this.codeTarget = new Date().getTime();
      this.$refs.codeImg.src =
        api.login.codeImg + "?codeTarget=" + this.codeTarget;
    },
    /**
     * 密码登录
     */
    psdLogin() {
      if (!this.inputModel.account) {
        this.$message.error("请输入账号");
        return;
      }

      if (!this.inputModel.password) {
        this.$message.error("请输入密码");
        return;
      }
      this.loading = true;
      const url = api.login.login,
        params = {
          account: this.inputModel.account,
          password: this.inputModel.password,
          code: this.inputModel.code,
          codeTarget: this.codeTarget
        };

      axios.post(url, qs.stringify(params)).then(async response => {
        localStorage.clear();
        this.$message.success("登录成功");
        this.$store.dispatch("SET_USERINFO", response);
        await this.getSettlementMethod();
        const menu = await axios.post(api.login.getMenu);
        console.log("menu:", menu);
        this.$store.dispatch("GET_MENULIST", menu);
        this.loading = false;
        this.$router.push("/index");
      });
      this.loading = false;
    },
    /**
     * @desc 获取采购结算方式
     */
    async getSettlementMethod() {
      await axios.post(api.common.querySettlementMethod).then(response => {
        this.$store.dispatch("SET_SETTLEMENTMETHOD", response.english);
      });
    }
  }
};
</script>
<style lang="less" scoped>
#login {
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 100vh;
  padding: 50px;
  //底部备案号
  .bottom-info {
    width: 100%;
    height: 50px;
    padding: 10px;
    position: absolute;
    bottom: 20px;
    left: 0;
    color: #fff;
    font-size: 24px;
    text-align: center;
    line-height: 30px;
    // background: rgba(0, 0, 0, 0.4);
  }
  // background: -webkit-gradient(linear, 0 0, 0 100%, from(#c1e8fb), to(#a1c4fd));
  // background: -moz-gradient(linear, 0 0, 0 100%, from(#c1e8fb), to(#a1c4fd));
  // background: -webkit-gradient(linear,
  //   left top,
  //   left bottom,
  //   from(#c1e8fb),
  //   to(#a1c4fd));
  background: url(../assets/images/banner_login.png) bottom center no-repeat;
  background-size: 100% auto;

  // 登录框
  & > .login-container {
    position: fixed;
    right: 150px;
    display: flex;
    width: 425px;
    height: 475px;
    margin: auto;
    border-radius: 20px;

    background: #fff;

    .code-img {
      display: block;
      width: 80px;
      height: 30px;
    }

    // 登录框左侧
    & > .login-left {
      width: 525px;
      height: 100%;
    }

    // 登录框右侧
    & > .login-right {
      flex: 1;

      // 登录标题
      .login-title {
        font-size: 35px;
        font-weight: normal;
        margin: 0;
        padding: 45px 0;
        text-align: center;
        letter-spacing: 2px;

        color: #00a0e9;
      }

      // 登录表单
      .login-form {
        padding: 0 50px;

        .el-form-item {
          margin-bottom: 20px;
        }

        .el-form-last-item {
          margin-bottom: 35px;
        }

        //文本按钮栏
        .login-textbtn-item {
          margin-bottom: 10px;

          .textbtn-right {
            float: right;
          }
        }

        .submit-btn {
          width: 100%;
          height: 40px;
          border: 0;
          border-radius: 5px;

          font-size: 16px;

          background-color: #569ce8;
          box-shadow: 0px 6px 12px 0px rgba(122, 100, 240, 0.35);
          color: #fff;
        }
      }
    }
  }
}
</style>
<style lang="less">
#login {
  .el-input__inner {
    border-color: #f3f8fe;

    background: #f3f8fe;
  }

  .el-input-group__append {
    width: auto;
    border: 0;

    background: #f3f8fe;

    button {
      margin: 0;

      background: none;
      color: #409eff;
    }
  }



  .el-icon-view:before {
    // content: "\e8c7";

    font-size: 16px;
  }

  .el-icon-circle-close:before {
    // content: "\e632";

    font-size: 12px;
  }
}
</style>
