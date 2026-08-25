<template>
  <el-dialog
    title="第三方派单"
    center
    :visible.sync="visible"
    :closeOnClickModal="false"
    class="dispatch-dialog"
    append-to-body
  >
    <div v-loading="loading">
      <p>请选择第三方配送</p>
      <el-checkbox-group v-model="checkList">
        <el-checkbox-button
          :label="item.deliveryCode"
          :key="item.deliveryCode"
          v-for="(item, $index) in courier"
          >
          {{ item.deliveryChannelName }} 价格：{{item.estimatePrice/100}}元
        </el-checkbox-button>
      </el-checkbox-group>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide"> 取 消 </el-button>
      <el-button
        size="medium"
        type="primary"
        @click="handleSubmit"
        :loading="submitLoading"
      >
        确 定
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      loading: false,
      courier: [],
      checkList: [],
      submitLoading: false,
      row: null
    };
  },
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.checkList = []
      this.courier = []
      this.row = null
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      this.inputModel = inputModel;
    },
    show: function (row) {
      this.clearData();
      this.visible = true;
      this.row = row
      this.getData();
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
    },
    getData(row) {
      this.loading = true
      axios.post(api.order.thirdDeliveryBilling, this.$qs.stringify({ pkey: this.row.pkey }))
        .then((res) => {
          console.log(res);
          this.courier = res
          this.loading = false
        });
    },
    handleSubmit() {
      if(!this.checkList || !this.checkList.length) {
        this.$message.error("请选择第三方配送");
        return;
      }
      this.submitLoading = true
      const params = {
        pkey: this.row.pkey,
        multipleSupplierCodes: this.checkList.join(',')
      }
      axios.post(api.order.thirdDeliveryCreate, this.$qs.stringify(params))
        .then(res => {
          this.$message.success('派单成功')
          this.submitLoading = false
          this.$emit("refresh");
          this.hide()
        })
        .catch(() => {
          this.submitLoading = false
        })
    },
  },
};
</script>

<style lang="less">
  .dispatch-dialog {
    .el-checkbox-group {
      margin: 0;
      display: block;

      .el-checkbox-button {
        width: 25%;
        padding-top: 10px;
        padding-right: 10px;

        &:first-child {
          .el-checkbox-button__inner {
            border-radius: 5px;
            font-size: 12px;
          }
        }

        .el-checkbox-button__inner {
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