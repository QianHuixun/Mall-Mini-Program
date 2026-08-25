<template>
  <el-dialog
    title="退款"
    :visible.sync="visible"
    center
    append-to-body
    :closeOnClickModal="false"
    @close="handleClose"
  >
    <el-table style="width: 100%" class="table-box">
      <el-table-column label="" width="55px"> </el-table-column>
      <el-table-column label="商品"> </el-table-column>
      <el-table-column label="数量" width="100px"> </el-table-column>
      <el-table-column label="实付金额" width="100px"> </el-table-column>
      <el-table-column label="退款金额" width="150px"> </el-table-column>
      <div slot="append">
        <div v-for="(item, index) in tableData" :key="index">
          <div class="table-herder">
            商户：{{ item.name }} 摊位号：{{ item.booth }}
          </div>
          <el-table
            :data="item.list"
            :show-header="false"
            style="width: 100%"
          >
            <el-table-column width="55px">
              <template slot-scope="scope">
                <el-checkbox :disabled="scope.row.surplusRefundAmt === 0" :checked="scope.row.checked" @change="handleChange(scope.row)"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column prop="date" label="商品">
              <template slot-scope="scope">
                <div class="foods">
                  <div class="image">
                    <div class="specifications" v-if="scope.row.surplusRefundAmt === 0">已退完</div>
                    <el-image :src="scope.row.photo"></el-image>
                  </div>
                  <div>
                    <div class="name">{{scope.row.goodsName}}</div>
                    <div class="specs">{{scope.row.spaceName}}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="num" label="数量" width="100px"></el-table-column>
            <el-table-column prop="date" label="实付金额" width="100px">
              <template slot-scope="scope">
              ￥{{scope.row.sumPrice.toFixed(2)}}
              </template>
            </el-table-column>
            <el-table-column prop="date" label="退款金额" width="150px">
              <template slot-scope="scope">
                <el-input type="number" v-model="scope.row.refundAmt" :disabled="scope.row.surplusRefundAmt === 0"
                v-on:input="(val) => {scope.row.refundAmt = inputNumberFixed(val);}" @blur="handleRefundAmtConfirm(scope.row)">
                  <template slot="append">元</template>
                </el-input>
                <div>剩余可退￥{{scope.row.surplusRefundAmt.toFixed(2)}}</div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-table>

    <div slot="footer" class="dialog-footer">
      <div class="dialog-footer-content">
        <div style="text-align: start">
          <div>退款金额：{{refundAmtAll.toFixed(2)}}元</div>
          <div style="margin-top: 10px;">
            退款原因：
            <el-select v-model="reason" placeholder="请选择退款原因">
              <el-option v-for="item in dropList" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </div>
        </div>
        <div>
          <el-button type="primary" size="medium" @click="handleConfirm"> 确定 </el-button>
          <el-button @click="handleClose">取消</el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import utils from '@/assets/js/utils';
export default {
  data() {
    return {
      visible: false,
      pkey: "",
      status:"",
      refundAmtAll: 0,
      tableData: [],
      dropList: [],
      reason:""
    };
  },
  methods: {
     /**
     * @desc 精确到小数点后两位
     * @param {String} val  输入的值
     */
    inputNumberFixed(val) {
      val = utils.inputNumberFixed(val);
      return val;
    },
    show(pkey,status) {
      this.visible = true;
      this.pkey = pkey;
      this.status = status;
      this.getData();
      this.getDrop();
    },

    /**获取退款原因 */
    getDrop() {
      axios.post(api.order.getRefundReason, this.$qs.stringify({ status: this.status }))
        .then(res => {
          this.dropList = res || [];
        })
    },

    /**获取退款信息 */
    getData() {
      axios
        .post(
          api.order.refundLoadOrder,
          this.$qs.stringify({ pkey: this.pkey })
        )
        .then((res) => {
          console.log(res, "refundLoadOrder");
          res.map((item) => {
            item.list.map((listItem) => {
              listItem.checked = false;
              listItem.refundAmt = 0;
              return listItem;
            })
            return item;
          });
          this.tableData = res || [];
        });
    },

    /* 勾选修改*/
    handleChange(row) {
      row.checked = !row.checked;
      if(row.checked) {
        row.refundAmt = row.surplusRefundAmt;
      } else {
        row.refundAmt = 0;
      }
      this.getRefundAmtAll();
    },
    /**
     * @desc 退款金额确认
     * @param {Object} row  行数据
     */
    handleRefundAmtConfirm(row) {
      if(row.refundAmt > row.surplusRefundAmt) {
        row.refundAmt = row.surplusRefundAmt;
        this.$message.error('退款金额不能大于可退金额');
      }
      console.log(row.refundAmt, 'row.refundAmt');
      console.log(this.tableData);
      this.getRefundAmtAll();
    },
    /**
     * @desc 计算总退款金额
     */
    getRefundAmtAll() {
      this.refundAmtAll = 0;
      this.tableData.forEach((item) => {
        item.list.forEach((listItem) => {
          if(listItem.checked) {
            this.refundAmtAll +=  Number(listItem.refundAmt);
          }
        })
      })
    },
    handleClose() {
      this.visible = false;
      this.tableData = [];
      this.reason = "";
      this.refundAmtAll = 0;
      this.dropList = [];
    },
    handleConfirm() {
      if(!this.refundAmtAll) {
        return this.$message.warning('退款金额不能为0')
      }
      if(!this.reason) {
        return this.$message.warning('请选择退款原因')
      }
      let lines = [];
      this.tableData.forEach((item) => {
        item.list.forEach((listItem) => {
          if(listItem.checked) {
            lines.push({
              pkey: listItem.pkey,
              refundAmt: listItem.refundAmt,
            })
          }
        })
      })
      let params = {
        pkey: this.pkey,
        reason: this.reason,
        lines: lines,
      };
      axios.post(api.order.refund_agree, params)
        .then(res => {
           this.$message.success('退款成功');
           this.$emit('refresh');
           this.handleClose();
        })
    }
  },
};
</script>

<style lang="less" scoped>
.foods {
  display: flex;

  .image {
    position: relative;
    border-radius: 4px;
    overflow: hidden;
    margin-right: 8px;
    width: 80px;
    height: 80px;
    .specifications {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #333333;
      background: rgba(0, 0, 0, 0.1);
      z-index: 1;
    }
  }

  .name {
    font-weight: bold;
  }

  .specs {
    color: #999999;
  }
}

.dialog-footer-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.table-box {
  /deep/ .el-table__empty-block {
    display: none;
  }
}

.table-content {
  /deep/ .el-table__empty-block {
    display: flex;
  }
}

.table-herder {
  height: 40px;
  line-height: 40px;
  padding-left: 12px;
  font-size: 16px;
  font-weight: bold;
  text-align: left;
  background: #f2f2f2;
}
</style>