<!--
 * @Author: 沙晓
 * @Date: 2024-02-26 16:53:54
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-03-05 13:26:27
 * @Description: file content
 * @FilePath: /lejia-web/src/views/vendor/sub/Wallet/Detail.vue
-->
<template>
  <el-dialog
    :title="title"
    center
    :visible.sync="visible"
    :closeOnClickModal="false"
    append-to-body
    id="wallet-detail-dialog"
    @closed="handleCancel"
  >
    <div class="table-container">
      <div class="table-box">
        <el-table
          :data="tableData"
          :loading="loading"
          border
          style="width: 100%"
        >
          <el-table-column
            label="交易类型"
            prop="orderType"
            min-width="120"
            align="center"
          ></el-table-column>
          <el-table-column
            label="交易金额"
            prop="orderAmount"
            min-width="120"
            align="center"
          >
            <template slot-scope="scope">
              {{
                scope.row.source == "CONSUME"
                  ? "+"
                  : scope.row.source == "WITHDRAWAL"
                  ? "-"
                  : ""
              }}{{ scope.row.orderAmount }}
            </template>
          </el-table-column>
          <el-table-column
            label="余额"
            prop="balance"
            min-width="120"
            align="center"
          ></el-table-column>
          <el-table-column
            label="状态"
            prop="status"
            min-width="120"
            align="center"
          ></el-table-column>
          <el-table-column
            label="时间"
            prop="settlementTime"
            min-width="120"
            align="center"
          ></el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                @click="handleDetail(scope.row)"
                :disabled="scope.row.source !== 'CONSUME'"
                >明细</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <!-- 页码 -->
        <el-pagination
          hide-on-single-page
          background
          layout="prev, pager, next"
          :total="total"
          :current-page="page"
          :page-size="pageSize"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import qs from "qs";
export default {
  data() {
    return {
      title: "商户钱包明细",
      visible: false,
      loading: false,
      tableData: [],
      page: 1, //显示页码
      pageSize: 6, //表格一页显示几条
      total: 0, //总页数,
      pkey: "",
      name: ""
    };
  },
  methods: {
    handleCancel() {
      this.visible = false;
    },
    show({ row }) {
      console.log(row, row.pkey);
      this.visible = true;
      this.pkey = row.pkey;
      this.name = row.name;

      this.getData();
    },
    getData() {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        pkey: this.pkey
      };
      axios
        .post(api.vendor.walletLineQuery, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    /**
     * 页码改变事件
     */
    handleCurrentChange(val) {
      this.page = val;
      this.loading = true;
      this.getData();
    },
    handleDetail(row) {
      console.log(row);
      const date = row.orderType.slice(0, 10);
      const settleDate = row.settlementTime.slice(0, 10);
      this.$router.push(
        `/vendor/combill?date=${date}&settleDate=${settleDate}&name=${this.name}`
      );
    }
  }
};
</script>
<style lang="less">
#wallet-detail-dialog .el-dialog__body {
  padding-bottom: 10px;
}
</style>
