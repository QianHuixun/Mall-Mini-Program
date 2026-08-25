<!-- 
@name: MallDeliveryUpd.vue 
@description: 发货--编辑模板 
@author: zs
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="快递公司" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.logistics" placeholder="请选择" ref="logisticsInput">
          <el-option v-for="item in companyList" :key="item.pkey" :label="item.name" :value="item.pkey">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="运单号" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.code" ref="codeInput" placeholder="请输入运单号"
        v-on:input="(val)=>{ val =val.replace(/[\u4e00-\u9fa5d]/g, ''); inputModel.code =val;}"></el-input>
      </el-form-item>
    </el-form>
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
  import dropdown from "@/assets/js/dropdown";

  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        companyList: [{
            pkey: "顺丰快递",
            name: "顺丰快递"
          },
          {
            pkey: "申通快递",
            name: "申通快递"
          },
          {
            pkey: "天天快递",
            name: "天天快递"
          },
          {
            pkey: "ems",
            name: "ems"
          },
          {
            pkey: "宅急送",
            name: "宅急送"
          },
          {
            pkey: "汇通快递",
            name: "汇通快递"
          },
          {
            pkey: "韵达快递",
            name: "韵达快递"
          },
          {
            pkey: "京东快递",
            name: "京东快递"
          },
          {
            pkey: "圆通快递",
            name: "圆通快递"
          }
        ],
        inputModel: {
          code: "",
          logistics: ""
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
          code: "",
          logistics: ""
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
       * 处理提交
       */
      handleSubmit: function () {
        if (!this.inputModel.logistics) {
          this.$message.error("请选择快递公司");
          this.$refs.logisticsInput.focus();
          return;
        }

        if (!this.inputModel.code) {
          this.$message.error("请输入快递单号");
          this.$refs.codeInput.focus();
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