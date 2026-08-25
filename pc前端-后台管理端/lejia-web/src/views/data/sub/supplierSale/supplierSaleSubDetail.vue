<template lang="html">
  <div class="table-container">
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-date-picker v-model="startDate" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
        <el-select v-model="payTypes" multiple collapse-tags clearable placeholder="支付方式" @change="handleChange" style="width: 200px;">
          <el-option value="ORDER_WEIXIN" label="微信"></el-option>
          <el-option value="ORDER_ELECTRONIC_ACCOUNT" label="电子账户"></el-option>
          <el-option value="NM_MEMBER" label="农贸会员卡"></el-option>
          <el-option value="ORDER_MSD" label="热力豆"></el-option>
        </el-select>
        <el-select v-model="tags" @change="handleChange" filterable multiple collapse-tags placeholder="选择标签" clearable style="width: 200px;">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in tagList"></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <div class="count-container">
          <div class="count-box">
            <span class="count-tit">商品合计：</span>
            <span class="count-num-xs">{{ numData }}</span>
          </div>
        </div>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleImportExcel">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%" >
        <el-table-column label="订单编号" prop="kcCode" min-width="140"></el-table-column>
        <el-table-column label="供应商" prop="supplierName"></el-table-column>
        <el-table-column label="商品名" prop="goodsName"></el-table-column>
        <el-table-column label="商品规格" prop="spaceName"></el-table-column>
        <el-table-column label="数量" prop="num"></el-table-column>
        <el-table-column label="商品单价" prop="pricen"></el-table-column>
        <el-table-column label="商品总价" prop="amt"></el-table-column>
        <el-table-column label="商品退款" prop="refundAmt">
          <template slot-scope="scope">
            {{ scope.row.refundAmt || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="合计金额" prop="sumGoodsAmt"></el-table-column>
        <el-table-column label="积分单价" prop="point"></el-table-column>
        <el-table-column label="积分总价" prop="pointSum"></el-table-column>
        <el-table-column label="积分退款" prop="refundPoint"></el-table-column>
        <el-table-column label="购买用户" prop="memberMobile" min-width="100"></el-table-column>
        <el-table-column label="用户标签" prop="tagName" min-width="100" show-overflow-tooltip></el-table-column>
        <el-table-column label="支付方式" prop="payTypeName" min-width="100"></el-table-column>
        <el-table-column label="付款时间" prop="createdTime" min-width="140"></el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>
<script>
import qs from "qs";
import utils from '@/assets/js/utils.js';
export default {
  data() {
    return {
      loading: false,
      searchKey: "kcCode",
      selectOptions: [
        { name: "订单编号", key: "kcCode" },
        { name: "供应商", key: "supplierName" },
        { name: "商品名称", key: "goodsName" }
      ],
      tableData: [],
      tagList:[],
      startDate:[utils.getCustDate(30), utils.getNowDate()],
      tags: [],
      payTypes: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: "", // 搜索关键字
      total: 0, //总页数
      downLoading:false,
      numData:""
    };
  },
  computed: {},
  components: {},
  mounted() {
    this.getData();
    this.getDataNum();
    this.getTagData();
  },
  methods: {
    handleChange: function () {
      this.page = 1;
      this.getData();
      this.getDataNum();
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
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
      this.getDataNum();
    },
    /**
     * 获取列表
     * @return {[type]} [description]
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        payTypes: this.payTypes.join(','),
        tags: this.tags.join(',')
      };
      params[this.searchKey] = this.keywords;
       if(this.startDate && this.startDate.length == 2){
        params['startTime'] = this.startDate[0];
        params['endTime'] = this.startDate[1];
      }
      axios
        .post(api.data.querySupplierSalesline, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },

    getDataNum: function () {
      this.loading = true;
      const params = {
        payTypes: this.payTypes.join(','),
        tags: this.tags.join(',')
      };
      params[this.searchKey] = this.keywords;
      if(this.startDate && this.startDate.length == 2){
        params['startTime'] = this.startDate[0];
        params['endTime'] = this.startDate[1];
      }
      axios
        .post(api.data.querySupplierSalesSumline, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.numData = response.success ? "0" : response;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },

    /**列表导出 */
    handleImportExcel() {
      const params = {
        payTypes: this.payTypes.join(','),
        tags: this.tags.join(',')
      };
      params[this.searchKey] = this.keywords;
      if(this.startDate && this.startDate.length == 2){
        params['startTime'] = this.startDate[0];
        params['endTime'] = this.startDate[1];
      }
      let that = this;
      this.downLoading = true;
      axios
        .post(api.data.querySupplierSaleslineExport, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
          responseType: "blob",
          timeout: 0,
        })
        .then((res) => {
          let data = new Blob([res.data], {
            type: "application/json",
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
                type: "application/vnd.ms-excel",
              });
              if (!!window.ActiveXObject || "ActiveXObject" in window) {
                window.navigator.msSaveOrOpenBlob(blob, `${"供应商明细统计"}.xlsx`);
              } else {
                const link = document.createElement("a");
                link.style.display = "none";
                link.href = URL.createObjectURL(blob);
                link.setAttribute("download", `${"供应商明细统计"}.xlsx`);
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
    /**
     * @desc 获取标签列表
     */
     getTagData() {
      axios.post(api.marketing.tagsDrop).then((response) => {
        this.tagList = response;
      });
    },
  },
};
</script>

<style lang="less" scope>
  .count-container {
    display: inline-block;
    padding-right: 10px;

    .count-box {
      display: inline-block;
      padding: 0 10px;

      .count-tit {
        padding-right: 5px;
      }

      .count-num-rk {
        color: #67c23a;
      }

      .count-num-xs {
        color: #409eff;
      }
    }
  }
</style>