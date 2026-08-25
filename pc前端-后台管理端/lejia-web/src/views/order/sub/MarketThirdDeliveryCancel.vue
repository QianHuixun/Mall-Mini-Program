<template>
  <el-dialog title="取消配送订单" center :visible.sync="visible" :closeOnClickModal="false" class="dispatch-dialog" append-to-body>
    <el-form>
      <el-form-item label="取消原因" label-width="120px" required>
        <el-select v-model="cancelType" placeholder="请选择取消原因">
          <el-option v-for="item in list" :value="item.pkey" :label="item.name" :key="item.pkey"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide"> 取 消 </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
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
      cancelType: '',
      list: [],
      pkey: '',
    };
  },
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.cancelType = ''
      this.pkey = ''
      this.loading = false
    },
    show: function (inputModel) {
      this.clearData();
      this.pkey = inputModel.pkey
      this.visible = true;
      this.getData();
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
    },
    getData() {
      axios.post(api.order.thirdDeliveryCancelType)
        .then((res) => {
          console.log(res);
          this.list = res
        });
    },
    handleSubmit() {
      if(!this.cancelType) {
        this.$message.error("请选择取消原因");
        return;
      }
      this.loading = true
      const params = {
        pkey: this.pkey,
        cancelType: this.cancelType
      }
      axios.post(api.order.thirdDeliveryCancel, this.$qs.stringify(params))
        .then(res => {
          this.$message.success('取消配送成功')
          this.loading = false
          this.$emit("refresh");
          this.hide()
        })
        .catch(() => {
          this.loading = false
        })
    },
  },
};
</script>

<style scoped>
.el-dialog__wrapper .el-dialog .el-dialog__body .el-form {
  overflow: auto;
}
</style>