<!-- 
@name: PurchaseAgain.vue 
@description: 采购详情-再次采购
@author: crj
@date: 2020/10/08
-->
<template lang="html">
  <el-dialog title="重新采购" center :visible.sync="visible" :append-to-body="true" :closeOnClickModal="false"
    class="purchase-again">
    <p>商品信息</p>
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column label="商品名称" prop="goodsName"> </el-table-column>
      <el-table-column label="数量" prop="num"></el-table-column>
      <el-table-column label="供应商">
        <template slot-scope="scope">
          <el-select v-model="scope.row.vendorObject" placeholder="请选择" :ref="`vendorInput${scope.$index}`"
            @change="handleVendorChange($event,scope.$index)" value-key="vendor">
            <!-- <el-option label="自采" :value="{vendor:0}">
            </el-option> -->
            <el-option :label="item.vendorName" :value="item" v-for="(item,index) in scope.row.vendorList"
              :key="item.vendor">
            </el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column v-if="settlementMethod == 'PURCHASE_SETTLEMENT'" label="采购单价">
        <template slot-scope="scope">
          <el-input type="number" v-model="scope.row.price" :ref="`priceInput${scope.$index}`" placeholder="单价"
            @input="handleChange(scope.$index)"></el-input>
        </template>
      </el-table-column>
      <el-table-column v-if="settlementMethod == 'COMMISSION_SETTLEMENT'" label="销售单价">
        <template slot-scope="scope">
          <!-- <el-input type="number" v-model="scope.row.price" :ref="`priceInput${scope.$index}`" placeholder="单价"
            @input="handleChange(scope.$index)"></el-input> -->
            {{scope.row.price}}
        </template>
      </el-table-column>
      <el-table-column v-if="settlementMethod == 'PURCHASE_SETTLEMENT'" label="采购总价" prop="amt"></el-table-column>
      <el-table-column v-if="settlementMethod == 'COMMISSION_SETTLEMENT'" label="销售总价" prop="amt"></el-table-column>
      <el-table-column label="备注">
        <template slot-scope="scope">
          <el-input v-model="scope.row.remark" :ref="`remarkInput${scope.$index}`" placeholder="备注"></el-input>
        </template>
      </el-table-column>
    </el-table>
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
import qs from 'qs';

export default {
  data() {
    return {
      labelWidth: '100px',
      visible: false,
      loading: false,
      courier: [],
      tableData: [],
      vendorList: [],
      vendorPkey: '',
    };
  },
  computed: {
    settlementMethod() {
      return this.$store.state.settlementMethod
    }
  },
  mounted() {},
  methods: {
    /**单价改变 */
    handleChange(index) {
      this.tableData[index].amt =
        this.tableData[index].price * this.tableData[index].num;
    },
    /**下拉框选中改变 */
    handleVendorChange(e, index) {
      console.log(e, index);
      if (e.vendor == 0) {
        let item = JSON.parse(JSON.stringify(this.tableData[index]));
        item.price = '';
        item.amt = '';
        item.vendor = e.vendor;
        this.$set(this.tableData, index, item);
        return;
      }
      if (e.price == null) e.price = 0;
      this.tableData[index].price = e.price;
      this.tableData[index].amt = e.price * this.tableData[index].num;
      this.tableData[index].vendor = e.vendor;
    },
    /**点击下拉框，获取商户列表 */
    handleGetVendor(e, row) {
      if (e) {
        if (row.vendorList.length) {
          return;
        } else {
          row.vendorList = [];
        }
        let params = {
          Pkey: row.space,
          orderPkey: this.orderPkey
        };
        axios
          .post(api.order.vendorList, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then((response) => {
            row.vendorList = response;
          });
      }
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.tableData = [];
      this.vendorList = [];
      this.vendorPkey = '';
    },
    /**
     *@desc 初始化数据并显示
     *@param {Object} row 行数据
     *@param {String} orderPkey 订单pkey
     */
    show: function ({ row, orderPkey }) {
      this.clearData();
      row.map((item) => {
        item.vendorObject = {
          vendor: item.vendor,
        };
        item.amt = item.price * item.num;

        return item;
      });
      const tableData = row;
      this.tableData = tableData;
      for (let i in row) {
        let params = {
          Pkey: row[i].space,
          orderPkey: orderPkey
        };
        axios
          .post(api.order.vendorList, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then((response) => {
            this.$set(this.tableData[i], 'vendorList', response);
          });
      }
      console.log(this.tableData);
      this.orderPkey = orderPkey;
      this.visible = true;
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
      axios
        .post(api.order.queryCourier, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.courier = response;
        });
    },

    /**
     * 处理提交
     */
    handleSubmit: function () {
      let tableData = this.tableData;
      console.log(this.tableData);
      for (let i = 0; i < tableData.length; i++) {
        if (tableData[i].vendor != 0 && !tableData[i].vendor) {
          this.$message.error('请选择商户');
          this.$refs[`vendorInput${i}`].focus();
          return;
        }
        if (!tableData[i].price && this.settlementMethod == 'PURCHASE_SETTLEMENT') {
          // console.log(tableData[i].price)
          this.$refs[`priceInput${i}`].focus();
          this.$message.error('请填写单价');
          return;
        }
      }
      tableData.map((item) => {
        delete item.vendorObject;
        delete item.vendorName;
        delete item.purchaseStatus;
        delete item.purchaseStatusName;
        return item;
      });
      let params = {
        ...this.tableData[0],
        orderPkey: this.orderPkey,
      };
      this.loading = true;
      axios
        .post(api.order.againPurchase, params, {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success('重新采购成功');
          this.$emit('refresh');
          this.hide();
        });
      setTimeout(() => {
        this.loading = false;
      }, 300);
    },
  },
  props: {
    title: {
      type: String,
      default: '发货',
    },
  },
};
</script>

<style lang="less">
.purchase-again {
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
<style lang="less">
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
}

input[type='number'] {
  -moz-appearance: textfield;
}
</style>
<style lang="less" scoped>
/deep/.el-dialog {
  width: 50% !important;

  .el-dialog__body {
    padding: 10px 15px 10px 15px;
  }
}
</style>