<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="postage">
      <div style="width: 500px;">
        <el-form>
          <span class="title-sub"><span class="red"></span>骑手配送派单配置</span>
          <el-form-item :label-width="labelWidth">
            <el-radio-group v-model="automaticCourier">
              <el-radio :label="false">人工指派</el-radio>
              <el-radio :label="true">系统自动派单
                <el-button v-if="automaticCourier" type="text" @click="handeleSelectRider">选择骑手</el-button>
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <span class="title-sub"><span class="red"></span>商户采购派单配置</span>
          <el-form-item :label-width="labelWidth">
            <el-radio-group v-model="automaticPurchase" >
              <el-radio :label="false" :disabled="marketType === 'VENDOR_SHOPPING_MALL'">人工指派</el-radio>
              <el-radio :label="true">系统自动派单</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <el-row class="btn-bar">
        <el-col>
          <el-button size="medium" @click="getData">
            还 原
          </el-button>
          <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
            保 存
          </el-button>
        </el-col>
      </el-row>
    </div>
    <dispatch-upd ref="DispatchUpd"></dispatch-upd>
  </div>
</template>

<script>
import qs from 'qs';
import DispatchUpd from './sub/DispatchUpd';

export default {
  data() {
    return {
      automaticCourier: false,
      automaticPurchase: false,
      labelWidth: '60px',
      loading: false,
      marketType: this.$store.state.marketType
    };
  },
  computed: {
    title() {
      return this.$store.state.activeName;
    },
  },
  components: {
    DispatchUpd,
  },
  mounted() {
    console.log(this.marketType);
    this.getData();
  },
  methods: {
    /**
     * @desc 选择骑手
     */
    handeleSelectRider() {
      this.$refs.DispatchUpd.show();
    },
    /**
     * @desc 获取数据
     */
    getData() {
      let params = {
        pkey: this.$store.state.marketPkey
      };
      this.loading = true;
      axios
        .post(api.market.getMarketInfo, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          this.loading = false;
          this.automaticCourier = res.config.automaticCourier;
          this.automaticPurchase = res.config.automaticPurchase;
        });
    },
    /**
     * @desc 保存
     */
    async handleSubmit() {
      if (this.automaticCourier) {
        let hasRider = false;
        await axios
          .post(
            api.mall.queryDispatchCourier,
            {},
            {
              headers: {
                Authorization: this.$store.state.token,
              },
            }
          )
          .then((res) => {
            res.map((item) => {
              if (item.selected) {
                hasRider = true;
              }
            });
          });
        if (!hasRider) {
          this.$message.warning('请选择骑手');
          return;
        }
      }
      this.loading = true;
      let params = {
        automaticCourier: this.automaticCourier,
        automaticPurchase: this.automaticPurchase,
      };
      axios
        .post(api.mall.updDispatchConfig, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          this.loading = false;
          this.$message.success('配置成功');
        })
        .catch((err) => {
          this.loading = false;
        });
    },
  },
};
</script>

<style lang="less" scoped>
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