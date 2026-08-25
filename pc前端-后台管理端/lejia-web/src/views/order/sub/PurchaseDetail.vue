<!-- 
@name: PurchaseDetail.vue 
@description: 市场订单-采购详情
@author: crj
@date: 2020/10/08
-->
<template>
  <el-dialog title="采购详情" center :visible.sync="visible" :closeOnClickModal="false" class="purchase-detail">
    <div class="el-form">
      <p>基本信息</p>
      <div class="base">
        <div class="base-item">
          <span class="base-item-title">采购状态：</span>
          <span class="base-item-content">{{ inputModel.statusName }}</span>
        </div>
        <div class="base-item">
          <span class="base-item-title">订单号：</span>
          <span class="base-item-content">{{ inputModel.code }}</span>
        </div>
      </div>
    </div>
    <p> <span>商品信息</span>
      <el-button v-if="inputModel.statusName != '确认完成'" class="left-btn" type="primary" size="mini" @click="handleConfirm(1)">全部确认</el-button>
    </p>
    <el-table :data="inputModel.vendors" border style="width: 100%">
      <el-table-column label="商品名称" min-width="100" prop="goodsName"> </el-table-column>
      <el-table-column label="规格" prop="spaceName"></el-table-column>
      <el-table-column label="类型" prop="typeName"></el-table-column>
      <el-table-column label="数量" prop="num" min-width="80"></el-table-column>
      <el-table-column label="商品现价" prop="goodsPrice"></el-table-column>
      <el-table-column label="供应商" prop="vendorName"></el-table-column>
      <el-table-column label="推荐采购价" width="120" prop="recommendPrice"></el-table-column>

      <el-table-column label="采购单价" prop="price"></el-table-column>
      <el-table-column label="采购总价" prop="amt">
        <!-- <template slot-scope="scope">
          <span>{{scope.row.price *scope.row.num}}</span>
        </template> -->
      </el-table-column>
      <el-table-column label="采购状态" prop="purchaseStatusName">
        <template slot-scope="scope">
          <span :class="scope.row.purchaseStatus == 'PURCHASEING' ? 'red-font' : (scope.row.purchaseStatus == 'PURCHASE_FINISH' ? 'green-font' : '')">{{ scope.row.purchaseStatusName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="价格波动" prop="priceStatusName">
        <template slot-scope="scope">
          <span :class="scope.row.priceStatusName == '异常' ? 'red-font' : ''">{{ scope.row.priceStatusName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="采购备注" width="120" prop="remark">
        <template slot-scope="scope">
          <span>{{ scope.row.remark || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="采购时间" min-width="150" prop="createdTime"></el-table-column>
      <el-table-column label="商户确认时间" min-width="150" prop="vendorTime">
        <template slot-scope="scope">
          <span :class="scope.row.vendorTime ? '' : 'red-font'">{{ scope.row.vendorTime || '未确认' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="市场确认时间" min-width="150" prop="farmerTime">
        <template slot-scope="scope">
          <span :class="scope.row.farmerTime ? '' : 'red-font'">{{ scope.row.farmerTime || '未确认' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right" v-if="inputModel.statusName != '确认完成'">
        <template slot-scope="scope">
          <div v-if="scope.row.purchaseStatus != 'PURCHASE_CONFIRM'">
            <el-button type="primary" size="mini" @click="handleConfirm(0, scope.row)">确定</el-button>
            <el-button plain type="primary" size="mini" @click="handlePurchase(scope.row)">重新采购</el-button>
          </div>
          <div v-else>无</div>
        </template>
      </el-table-column>
    </el-table>
    <p>撤销记录</p>
    <el-table :data="inputModel.revokes" border style="width: 100%">
      <el-table-column label="商品名称" min-width="100" prop="goodsName"> </el-table-column>
      <el-table-column label="规格" prop="spaceName"></el-table-column>
      <el-table-column label="类型" prop="typeName"></el-table-column>
      <el-table-column label="数量" prop="num" min-width="80"></el-table-column>
      <el-table-column label="商品现价" prop="goodsPrice"></el-table-column>
      <el-table-column label="供应商" prop="vendorName"></el-table-column>
      <el-table-column label="采购单价" prop="price"></el-table-column>
      <el-table-column label="采购总价" prop="totalPrice">
        <!-- <template slot-scope="scope">
          <span>{{ scope.row.price * scope.row.num }}</span>
        </template> -->
      </el-table-column>
      <el-table-column label="采购状态" prop="purchaseStatusName">
      </el-table-column>
      <el-table-column label="采购备注" width="120" prop="remark">
        <template slot-scope="scope">
          <span>{{ scope.row.remark || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="采购时间" min-width="150" prop="createdTime">
        <template slot-scope="scope">
          <span>{{ scope.row.createdTime || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商户确认时间" min-width="150" prop="vendorTime">
        <template slot-scope="scope">
          <span :class="scope.row.vendorTime ? '' : 'red-font'">{{ scope.row.vendorTime || '未确认' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="撤销时间" min-width="150" prop="revokeTime">
        <template slot-scope="scope">
          <span>{{ scope.row.revokeTime || '--' }}</span>
        </template>
      </el-table-column>
    </el-table>
    <purchase-again ref="PurchaseAgain" @refresh="getData"></purchase-again>
  </el-dialog>
</template>

<script>
import qs from 'qs';
import PurchaseAgain from './PurchaseAgain.vue';
export default {
  data () {
    return {
      visible: false,
      inputModel: {
        code: '',
        statusName: '',
        vendors: [],
        revokes: [],
      },
      pkey: '',
    };
  },
  components: {
    PurchaseAgain,
  },
  methods: {
    /**
     * @desc 显示弹窗并初始化数据
     */
    show ({ row }) {
      this.pkey = row.pkey;
      this.getData();
      this.visible = true;
    },
    hide () {
      this.clearData();
      this.visible = false;
    },
    clearData () {
      this.inputModel = {
        code: '',
        statusName: '',
        vendors: [],
        revokes: [],
      };
    },
    getData () {
      let params = {
        pkey: this.pkey,
      };
      axios
        .post(api.order.queryPurchaseDetail, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          console.log(res);
          this.inputModel = res;
        });
    },
    /**
     * @desc 重新采购
     * @param {Object} row  行数据
     */
    handlePurchase (row) {
      this.$refs.PurchaseAgain.show({
        row: JSON.parse(JSON.stringify([row])),
        orderPkey: this.pkey,
      });
    },
    /**
     * @desc 确认采购
     * @param {Boolean} type 是否为批量确认
     * @param {Object} row  行数据
     */
    handleConfirm (type, row) {
      let pkeys = [];
      if (type) {
        for (let i in this.inputModel.vendors) {
          let item = this.inputModel.vendors[i];
          if (item.purchaseStatus != 'PURCHASE_CONFIRM') {
            pkeys.push(item.pkey);
          }
        }
      } else {
        pkeys = [row.pkey];
      }
      this.$confirm('是否确认采购完成', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        let params = {
          pkeys: pkeys.join(','),
        };
        axios
          .post(api.order.confirmPurchaseComp, qs.stringify(params))
          .then((res) => {
            this.$message.success('确认成功!');
            this.$emit('refresh');
            this.getData();
          });
      });
    },
  },
};
</script>

<style lang="less" scoped>
@import url('~@/assets/css/variable.less');

/deep/.el-dialog {
  width: 1000px;

  .el-dialog__body {
    padding: 10px 15px 10px 15px;

    .red-font {
      color: #f56c6c;
    }

    .green-font {
      color: #67c23a;
    }
  }
}

/deep/.el-table {
  margin-bottom: 12px;
}

p {
  margin-bottom: 12px;
  overflow: hidden;

  .left-btn {
    float: right;
  }
}

.base {
  padding-left: 2rem;
  display: flex;
  flex-wrap: wrap;
  padding-bottom: 12px;

  .base-item {
    text-align: left;

    .base-item-title {
      width: auto;
      text-align: right;
      display: inline-block;
    }
  }
}

.base {
  .base-item {
    width: 50%;
  }
}

/deep/ .el-table__body-wrapper::-webkit-scrollbar {
  display: inline-block;
}
</style>