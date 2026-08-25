<!-- 
@name:MembersFrom.vue 
@description: 会员明细表格弹窗（消费记录与优惠券记录）
@author: crj
@date: 2020/08/14
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" width="800px">
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column
          v-for="(item,index) in  tableContentData"
          :key="index"
          :label="item.label"
          :prop="item.propName"
          :sortable="item.sortable"
        ></el-table-column>
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
  </el-dialog>
</template>

<script>
import qs from "qs";
export default {
  props: {},
  data() {
    return {
      pkey: "",
      tableData: [],
      page: 1, //显示页码
      pageSize: 8, //表格一页显示几条
      visible: false,
      title: "",
      tableContentData: [],
      total: 0,
      loading: false,
      url: "",
      params: {}
    };
  },
  mounted() {},
  methods: {
    /**
     * 页码改变事件
     */
    handleCurrentChange(val) {
      this.page = val;
      this.loading = true;
      this.getData();
    },
    clearData() {
      (this.title = ""),
        (this.tableData = []),
        (this.tableContentData = []),
        (this.total = 0),
        (this.page = 1);
    },
    show: function(pkey, title) {
      this.visible = true;
      this.clearData();
      const params = {
        pkey,
        title
      };
      this.pkey= pkey;
      this.title = title;
      const query = this.judgement(params);
      this.getData(query);
    },
    /**
     * 获取列表
     */
    getData: function(query = "") {
      this.loading = true;
      let params, url;
      query
        ? (url = query.url) &&
          (params = {
            ...query.params,
            page: this.page - 1,
            pagesize: this.pageSize
          })
        : (url = this.url) &&
          (params = {
            ...this.params,
            page: this.page - 1,
            pagesize: this.pageSize
          });

      axios
        .post(this.url, qs.stringify(params), {
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
     * 路由判断调用接口以及表格显示内容
     */
    judgement: function({ pkey, title }) {
      let url, params;
      switch (title) {
        //消费记录
        case "消费记录":
          url = api.data.consumption;
          params = {
            member:pkey
          };
          this.tableContentData = [
            {
              propName: "code",
              label: "订单号"
            },
            {
              propName: "farmer",
              label: "消费市场"
            },
            {
              propName: "consumption",
              label: "付费金额"
            },
            {
              propName: "goodsName",
              label: "购买商品"
            },
            {
              propName: "createdTime",
              label: "消费时间",
              sortable: true
            }
          ];
          break;
        case "优惠券记录":
          url = api.data.couponList;
          params = {
            member:pkey
          };
          this.tableContentData = [
            {
              propName: "code",
              label: "编号"
            },
            {
              propName: "userFarmerName",
              label: "市场"
            },
            {
              propName: "cardName",
              label: "优惠券"
            },
            {
              propName: "userTime",
              label: "时间"
            }
          ];
          break;
      }
      this.url = url;
      this.params = params;
      return {
        url,
        params
      };
    }
  }
};
</script>

<style lang="less" scoped>
/deep/ .el-dialog {
  padding-bottom: 40px ;
}
</style>