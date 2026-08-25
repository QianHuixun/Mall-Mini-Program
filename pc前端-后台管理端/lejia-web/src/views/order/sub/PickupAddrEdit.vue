<template>
  <el-dialog
    class="dialog"
    title="修改提货地点"
    center
    :visible.sync="visible"
    :closeOnClickModal="false"
    :append-to-body="true"
    width="40%"
  >
    <el-form>
      <el-form-item label-width="40px">
        <el-radio class="radio-item" v-for="item in list" v-model="radio" :label="item.key">{{ item.value }}</el-radio>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
        确 定
      </el-button>
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import qs from "qs";
export default {
  data() {
    return {
      visible: false,
      loading: false,
      orderPkey: null,
      radio: null,
      list: [],
    };
  },
  methods: {
    /**弹出弹窗 */
    show: function ({pkey}, {addr}) {
      this.orderPkey = pkey
      console.log(pkey, addr);
      this.visible = true;
      this.getData(pkey, addr)
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.visible = false;
      this.clearData()
    },
    clearData() {
      this.orderPkey = null
      this.radio = null
      this.list = []
    },
    getData(pkey, addr) {
      axios
        .post(api.order.pickupLocalList, this.$qs.stringify({pkey}))
        .then((response) => {
          console.log(response);
          this.list = response
          this.radio = response.find(item => item.value == addr.addrDetail).key
          console.log(this.radio);
        });
    },
    handleSubmit() {
      const params = {
        pkey: this.orderPkey,
        pickupLocation: this.radio
      }
      axios
        .post(api.order.pickupLocalUpd, this.$qs.stringify(params))
        .then((response) => {
          console.log(response);
          this.$emit('confirm', {inputModel:{pkey: this.orderPkey}})
          this.hide()
        });
    }
  },
};
</script>

<style lang="less" scoped>
.dialog ::-webkit-scrollbar {
  display: none;
}
.radio-item {
  display: block;
  height: 40px;
  line-height: 40px;
}
</style>
