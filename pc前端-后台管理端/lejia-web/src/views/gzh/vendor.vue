<template>
  <div class="gzh-rider">
    <img src="../../assets/images/vendor_message.jpg" alt="" @click="handleGetAgain">
  </div>

</template>

<script>
export default {
  data() {
    return {
      openid: this.$route.query.openid || '',
      code: this.$route.query.code || '',
      miniopenid: localStorage.getItem('miniopenid'),
      ascription: localStorage.getItem('ascription'),
    };
  },
  mounted() {
    if (this.$route.query.miniopenid) {
      localStorage.setItem('miniopenid', this.$route.query.miniopenid);
    }
    if (this.$route.query.ascription) {
      localStorage.setItem('ascription', this.$route.query.ascription);
    }
    if (this.$route.query.openid) {
      console.log('去验证登录');
      this.openid = this.$route.query.openid;
      this.logClick();
    } else {
      this.getOpenid();
    }
  },
  methods: {
    logClick() {
      let params = {
        openid1: this.miniopenid,
        openid2: this.openid,
      };
      axios
        .post(api.gzh.bindVendorOpenid, this.$qs.stringify(params))
        .then((res) => {
          this.$message.success('启动成功');
        });
    },
    /**
     * @desc 获取openid
     */
    getOpenid() {
      if (this.code) {
        let params = {
          code: this.code,
          ascription: this.$route.query.ascription
              ? this.$route.query.ascription
              : localStorage.getItem('ascription')
        };
        let url = api.gzh.getOpenid + '?' + this.$qs.stringify(params);
        axios.get(url).then((response) => {
          console.log(response);
          this.openid = response;
          this.logClick();
        });
      } else {
        let url =
          location.href.split('?')[0] +
          `?miniopenid=${
            this.$route.query.miniopenid
              ? this.$route.query.miniopenid
              : localStorage.getItem('miniopenid')
          }`;
        let params = {
          redirect_url: url,
          ascription: this.$route.query.ascription
              ? this.$route.query.ascription
              : localStorage.getItem('ascription')
        };
        url = api.gzh.getCode + '?' + this.$qs.stringify(params);
        axios.get(url).then((response) => {
          location.href = response;
        });
      }
    },
    handleGetAgain() {
      this.code = '';
      this.openid = '';
      this.getOpenid();
    },
  },
};
</script>

<style lang="less" scoped>
.gzh-rider {
  position: relative;
  width: 100vw;
  height: 100vh;

  img {
    width: 100vw;
    height: 100vh;
  }

  .font-content {
    position: absolute;
    top: 70vh;
    left: 0;
    right: 0;
    text-align: center;
  }

  .font-content .title {
    margin-bottom: 10px;
    font-size: 20px;
    letter-spacing: 3px;
    font-weight: 500;
  }

  .font-content .tips {
    font-size: 16px;
    letter-spacing: 1px;
    color: rgb(153, 153, 153);
  }
}
</style>