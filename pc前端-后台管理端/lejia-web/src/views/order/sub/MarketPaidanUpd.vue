<!-- 
@name: MarketPaidanUpd.vue 
@description: 派单--编辑模板 
@author: zs
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" class="dispatch-dialog" append-to-body>
    <p>请选择配送人员</p>
    <el-radio-group v-model="inputModel.courier">
      <el-radio-button :label="item.pkey" :key="item.pkey" v-for="(item,$index) in courier">{{item.name}}
        {{item.mobile}}</el-radio-button>
    </el-radio-group>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
        确 定
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
  import utils from "@/assets/js/utils";
  import qs from "qs";

  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        courier: [],
        inputModel: {
          courier:""
        },
      };
    },
    mounted() {},
    methods: {
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          courier:""
        };
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel
      }) {
        this.inputModel = inputModel;
      },
      show: function () {
        this.visible = true;
        this.clearData();
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.clearData();
        this.visible = false;
      },

      /**
       * 读取跑脚员列表
       */
      getCourier: function () {
        const params = {};
        axios.post(api.order.queryCourier, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            }
          })
          .then(response => {
            this.courier = response;
          });
      },

      /**
       * 处理提交
       */
      handleSubmit: function () {
        if (!this.inputModel.courier) {
          this.$message.error("请选择快递员");
          return;
        }

        this.$emit("confirm", {
          inputModel: this.inputModel
        });
      }
    },
    props: {
      title: {
        type: String,
        default: "发货"
      }
    }
  };
</script>

<style lang="less">
  .dispatch-dialog {
    .el-radio-group {
      margin: 0;
      display: block;

      .el-radio-button {
        width: 25%;
        padding-top: 10px;
        padding-right: 10px;

        &:first-child {
          .el-radio-button__inner {
            border-radius: 5px;
            font-size: 12px;
          }
        }

        .el-radio-button__inner {
          width: 100%;
          border: 1px solid #dcdfe6;
          border-radius: 5px;
          font-size: 12px;
          box-shadow: none;
        }
      }
    }
  }
</style>