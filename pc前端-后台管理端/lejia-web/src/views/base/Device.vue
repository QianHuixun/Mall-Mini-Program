<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="postage">
      <div style="width: 500px;">
        <el-form :label-width="labelWidth">
          <el-form-item label="小票打印机"></el-form-item>
          <el-form-item label="设备编号">
            <el-input v-model="inputModel.printCode" placeholder="请输入设备编号"></el-input>
            绑定前需前往芯烨云后台添加打印机设备
          </el-form-item>
          <el-form-item label="小票内容">
            <el-input v-model="inputModel.content" placeholder="请输入小票内容"></el-input>
          </el-form-item>
          <el-form-item label="">
            <div class="image-box">
              <div class="image-box-item">
              <!-- <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload> -->
                <el-input v-model="inputModel.photo1" placeholder="请输入URL"></el-input>
                <el-input v-model="inputModel.photo1Text" placeholder="请输入内容"></el-input>
              </div>
              <div class="image-box-item">
                <el-input v-model="inputModel.photo2" placeholder="请输入URL"></el-input>
                <el-input v-model="inputModel.photo2Text" placeholder="请输入内容"></el-input>
              </div>
            </div>
          </el-form-item>
            <el-form-item  label="骑手配置"></el-form-item>
          <el-form-item :label-width="labelWidth" label="应用id">
            <el-input v-model="inputModel.wanliAppId" placeholder="请输入应用id"></el-input>
          </el-form-item>
          <el-form-item :label-width="labelWidth" label="应用密钥">
            <el-input v-model="inputModel.wanliSecret" placeholder="请输入应用密钥"></el-input>
          </el-form-item>
          <el-form-item :label-width="labelWidth" label="门店id">
            <el-input v-model="inputModel.storeId" placeholder="请输入门店id"></el-input>
          </el-form-item>
          <el-form-item :label-width="labelWidth" label="店铺id">
            <el-input v-model="inputModel.shopId" placeholder="请输入店铺id"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <el-row class="btn-bar">
        <el-col>
          <el-button size="medium" @click="getData">
            取 消
          </el-button>
          <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
            保 存
          </el-button>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import ImgUpload from "@/components/global/ImgUpload";
export default {
  data() {
    return {
      isTianJin: localStorage.getItem("ascription") ==  (process.env.VUE_APP_TITLE =='production' ? 13 : 22) ? true : false,
      labelWidth: '120px',
      loading: false,
      inputModel:  {
        printCode: "",
        wanliAppId: "",
        wanliSecret: "",
        storeId: "",
        shopId: "",
        content: "",
        photo1: "",
        photo2: "",
        photo1Text: "",
        photo2Text: ""
      },
    };
  },
  computed: {
    title() {
      return this.$store.state.activeName;
    },
  },
  components: {
    ImgUpload
  },
  mounted() {
    this.getData();
  },
  methods: {
    /**
       * 图片修改事件
       */
      changeImg: function (imgUrl) {
        this.inputModel.photo1 = imgUrl[0];
      },
      changeImg2: function (imgUrl) {
        this.inputModel.photo2 = imgUrl[0];
      },
    /**
     * @desc 获取数据
     */
    getData() {
      this.loading = true;
      axios.post(api.market.getPrintCode)
        .then((res) => {
          console.log('res', res);
          this.loading = false;
          this.inputModel = res
        //   this.$nextTick(() => {
        //   setTimeout(() => {
        //     this.$refs.ImgUpload.updateImg(this.inputModel.photo1);
        //     this.$refs.ImgUpload2.updateImg(this.inputModel.photo2);
        //   }, 0);
        // });
        });
    },
    /**
     * @desc 保存
     */
    async handleSubmit() {
      let params = this.inputModel;
      
      axios.post(api.market.updPrintCode, params)
        .then(() => {
          this.$message.success('设置成功')
          this.getData()
        })
    },
  },
};
</script>

<style lang="less" scoped>

.image-box {
  display: flex;
  align-items: center;

.image-box-item {
  margin: 5px;
  flex: 1;
  .el-input {
    margin-top: 10px;
  }

  .img-upload{
    margin: auto;
    width: 100px;
  }
}
}

.postage {
  margin-left: 20px;

  .el-checkbox {
    display: block;

    .el-input {
      width: 80px;
      margin: 0 8px;
    }
  }

  /deep/.el-radio__input.is-checked + .el-radio__label {
    color: #606266;
  }

  .el-row {
    width: 800px;
    margin-left: 50px;
    display: flex;
    align-items: center;

    .el-col {
      // border: 1px solid #eee;
      width: 200px;
      padding-left: 30px;
      flex: 1;
    }

    .el-col:first-child {
      text-align: center;

      .text {
        background: #ddd;
        padding: 2px 5px;
      }

      .iconfont {
        font-size: 40px;
        margin-left: 20px;
      }

      .iconfont:first-child {
        margin-left: 60px;
      }
    }
  }

  .title-sub {
    display: block;
    margin: 14px;
  }

  .btn-bar {
    margin-top: 50px;
    margin-left: 0;
  }
}
</style>