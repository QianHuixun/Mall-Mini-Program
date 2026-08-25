<!-- 
@name: MarketCaigouUpd.vue 
@description: 采购--编辑模板 
@author: crj
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" class="dispatch-dialog">
    <p>商品信息</p>
    <el-table :data="tableData" border style="width: 100%">
      <!-- :loading="loading" -->
      <el-table-column label="商品名称" prop="goodsName"> </el-table-column>
      <el-table-column label="规格" prop="spaceName"> </el-table-column>

      <el-table-column label="数量" prop="num"></el-table-column>
      <el-table-column label="供应商">
        <template slot-scope="scope">
          <el-select v-model="scope.row.vendorObject" placeholder="请选择" :ref="`vendorInput${scope.$index}`"
            @change="handleVendorChange($event,scope.$index)" @visible-change="handleGetVendor($event,scope.row)"
            value-key="vendor">
            <!-- <el-option label="自采" :value="0">
            </el-option> -->
            <el-option :label="item.vendorName" :value="item" v-for="(item,index) in scope.row.vendorList"
              :key="item.vendor">
            </el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column v-if="settlementMethod == 'PURCHASE_SETTLEMENT'" label="采购单价">
        <template slot-scope="scope">
          <el-input type="number" v-model="scope.row.price" :ref="`priceInput${scope.$index}`" placeholder="采购单价"
            @input="handleChange(scope.$index)"></el-input>
        </template>
      </el-table-column>
      <el-table-column v-if="settlementMethod == 'COMMISSION_SETTLEMENT'" label="销售价格">
        <template slot-scope="scope">
          <!-- <el-input type="number" v-model="scope.row.orderPrice" :ref="`priceInput${scope.$index}`" placeholder="销售单价"
            ></el-input> -->
            {{scope.row.orderPrice}}
        </template>
      </el-table-column>
      <el-table-column v-if="settlementMethod == 'PURCHASE_SETTLEMENT'" label="采购总价" prop="amt"></el-table-column>
      <el-table-column v-if="settlementMethod == 'COMMISSION_SETTLEMENT'" label="销售总价">
        <template slot-scope="scope">
            {{(scope.row.orderPrice * scope.row.num).toFixed(2)}}
        </template>
      </el-table-column>
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
import utils from '@/assets/js/utils';
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
    /**采购单价改变 */
    handleChange(index) {
      console.log(this.tableData[index]);
      this.tableData[index].amt =
        this.tableData[index].price * this.tableData[index].num;
    },
    /**采购单价改变 */
    handleOrderPriceChange (index) {
      this.tableData[index].amt =
        this.tableData[index].orderPrice * this.tableData[index].num;
    },
    /**下拉框选中改变 */
    handleVendorChange(e, index) {
      console.log(e, index);
      if (e == 0) {
        this.tableData[index].price = '';
        this.tableData[index].amt = '';
        this.tableData[index].vendor = e;
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
     * 初始化数据
     */
    initData: function ({ tableData, orderPkey }) {
      this.tableData = tableData.map((item) => {
        item.vendorList = [];
        return item;
      });

      this.orderPkey = orderPkey;
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
      // setTimeout(() => {
      //   this.loading = false;
      // }, 1000);
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
      let tableData = this.tableData,
        amt = 0;
      for (let i = 0; i < tableData.length; i++) {
        if (tableData[i].vendor != 0 && !tableData[i].vendor) {
          this.$message.error('请选择供应商');
          this.$refs[`vendorInput${i}`].focus();
          return;
        }
        if (this.settlementMethod == 'PURCHASE_SETTLEMENT' && !tableData[i].price) {
          // console.log(tableData[i].price)
          this.$refs[`priceInput${i}`].focus();
          this.$message.error('请填写单价');
          return;
        }
      }
      this.loading = true;
      tableData.map((item) => {
        delete item.vendorObject;
        amt = amt + item.amt;
        return item;
      });
      let inputModel = {
        amt,
        list: this.tableData,
        orderPkey: this.orderPkey,
      };
      this.$emit('confirm', {
        inputModel,
      });
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
<style lang="less">
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
}

input[type='number'] {
  -moz-appearance: textfield;
}
</style>