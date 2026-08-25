<!-- 
@name: refund.vue
@description:  退款管理
@author:  zs
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="status" clearable @change="handleChange" placeholder="退款状态">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList"></el-option>
        </el-select>
        <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="申请开始日期"
          end-placeholder="申请结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入订单号" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-download" size="medium" @click="handleImportExcel">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="订单编号" prop="code" min-width="140"></el-table-column>
        <el-table-column label="商品价格" prop="goodsAmt" width="120"></el-table-column>
        <el-table-column label="配送费" prop="postage" width="120"></el-table-column>
        <el-table-column label="商品优惠" prop="preferentialAmt" v-if="userIdentity != 1" width="120"></el-table-column>
        <el-table-column label="配送优惠" prop="preferentialPostageAmt" v-if="userIdentity != 1" width="120"></el-table-column>
        <el-table-column label="订单合计" prop="amtall" width="120"></el-table-column>
        <el-table-column label="退款商品总价" prop="refundGoodsAmt" width="120"></el-table-column>
        <el-table-column label="配送费退款" prop="refundPostage" width="120"></el-table-column>
        <el-table-column label="退款合计" prop="amtre" width="120"></el-table-column>
        <el-table-column label="积分退款" prop="refundPoint" width="120"></el-table-column>
        <el-table-column label="退款状态" prop="statusName"></el-table-column>
        <el-table-column label="退款原因" prop="reason"  min-width="150"></el-table-column>
        <el-table-column label="申请时间" prop="createdTime" min-width="150"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="all-num">
        <span class="title">合计</span>
        <span>退款订单数：{{ num }}笔</span>
        <span>总退款金额：{{ refundAmt }}元</span>
      </div>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <refund-detail ref="RefundDetail" @refresh="getData"></refund-detail>
  </div>
</template>
<script>
  import qs from "qs";
  import RefundDetail from "./sub/RefundDetail.vue";
  export default {
    data() {
      return {
        downLoading: false,
        loading: false,
        numData: [],
        tableData: [],
        searchKey: "code",
        date: ["", ""],
        selectOptions: [{
          name: "订单编号",
          key: "code"
        }],
        statusList: [{
            pkey: "REFUND_APPLYING",
            name: "未处理"
          },
          // {
          //   pkey: "REFUND_AGREE",
          //   name: "已同意"
          // },
          {
            pkey: "REFUND_FINAL",
            name: "退款成功"
          },
          {
            pkey: "REFUND_REFUSE",
            name: "退款失败"
          }
        ],
        status: "",
        noPic: require("@/assets/images/no-pic.jpg"),
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条
        keywords: "", // 搜索关键字
        total: 0, //总页数
        num: 0,
        refundAmt: 0,

      };
    },
    mounted() {
      this.status = this.$route.query.status;
      this.getData();
    },
    components: {
      RefundDetail
    },
    computed: {
      /**
       * 获取菜单标题
       * @return {[title]} [返回从state状态中获取的选中菜单名]
       */
      title() {
        return this.$store.state.activeName;
      },
      userIdentity() {
        return this.$store.state.userIdentity;
      },
    },
    methods: {
      handleDetail: function (row) {
        this.$refs.RefundDetail.show(row);
      },
      handleChange: function () {
        if (!this.date) {
          this.date = ["", ""];
        }

        this.page = 1;
        this.getData();
      },
      /**
       * 页码改变事件
       */
      handleCurrentChange(val) {
        this.page = val;
        this.loading = true;
        this.getData();
      },
      /**
       * 开始搜索
       */
      startSearch: function ({
        key,
        keywords
      }) {
        this.keywords = keywords;
        this.searchKey = key;
        this.page = 1;
        this.getData();
      },
      /**
       * 获取列表
       */
      getData: function () {
        this.loading = true;
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          status: this.status,
          startDate: this.date[0] || "",
          endDate: this.date[1] || "",
          code: this.keywords
        };
        params[this.searchKey] = this.keywords;
        axios
          .post(api.sale.refundList, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            this.tableData = response.onPage.content;
            this.total = response.onPage.total;
            this.num = response.num;
            this.refundAmt = response.refundAmt;

            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
      },
      /**列表导出 */
      handleImportExcel() {
        const params = {
          status: this.status,
          startDate: this.date[0] || "",
          endDate: this.date[1] || "",
          code: this.keywords
        };
        let that = this;
        this.downLoading = true;
        axios
          .post(api.sale.refundImport, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            },
            responseType: "blob",
            timeout: 0
          })
          .then(res => {
            let data = new Blob([res.data], {
              type: "application/json"
            });
            var reader = new FileReader();
            reader.addEventListener("loadend", function (e) {
              if (e.target.result.indexOf("result") > 0) {
                let result = JSON.parse(e.target.result);
                that.downLoading = false;
                that.$message.error(result.codeMsg);
                return;
              } else {
                let blob = new Blob([res.data], {
                  type: "application/vnd.ms-excel"
                });
                if (!!window.ActiveXObject || "ActiveXObject" in window) {
                  window.navigator.msSaveOrOpenBlob(blob, "市场退款订单.xlsx");
                } else {
                  const link = document.createElement("a");
                  link.style.display = "none";
                  link.href = URL.createObjectURL(blob);
                  link.setAttribute("download", "市场退款订单.xlsx");
                  document.body.appendChild(link);
                  link.click();
                  document.body.removeChild(link);
                }
                that.downLoading = false;
                that.$message.success("导出成功");
              }
            });
            reader.readAsText(data);
          });
      },
    }
  };
</script>
<style lang="less" scoped>
  .all-num {
    height: 100%;
    text-align: right;
    padding-right: 20px;
    line-height: 60px;
    border: 1px solid #ebeef5;
    border-top: none;

    .title {
      font-weight: bold;
    }

    span {
      display: inline-block;
      margin-right: 10px;
    }
  }
</style>