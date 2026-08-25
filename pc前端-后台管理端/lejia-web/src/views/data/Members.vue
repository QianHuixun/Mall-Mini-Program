<!-- 
@name: Members.vue
@description: 付费会员明细页面
@author: crj
@url: /data/members
@date: 2020/08/14
-->

<template lang="html">
  <div class="table-container">
    <h1 class="title">{{ title }}</h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button v-loading="downLoading" type="primary" icon="el-icon-edit" size="medium" @click="handelExport">导出</el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%" class="table-fixed">
        <el-table-column label="ID" prop="pkey" min-width="80"></el-table-column>
        <el-table-column label="昵称" prop="name"></el-table-column>
        <el-table-column label="会员类型" prop="levelName"></el-table-column>
        <el-table-column label="手机号" prop="mobile" width="120"></el-table-column>
        <el-table-column label="地区" prop="area"></el-table-column>
        <el-table-column label="注册日期" prop="createdTime" min-width="150"></el-table-column>
        <el-table-column label="会员到期时间" prop="endDate" min-width="150"></el-table-column>
        <el-table-column label="积分" prop="points"></el-table-column>
        <el-table-column label="账户余额" prop="balance"></el-table-column>
        <el-table-column label="消费金额" prop="consumptionAmount"></el-table-column>
        <el-table-column label="剩余优惠券" prop="remainingCard" min-width="100"></el-table-column>
        <el-table-column label="备注" prop="remark"></el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
           <template slot-scope="scope">
            <el-button type="text" size="small" @click="checkfrom(scope.row.pkey,'消费记录')">
              消费记录
            </el-button>
            <el-button  size="mini" type="text"  @click="checkCard(scope.row,pointConfig)">
              积分记录
             </el-button>
             <el-button size="mini" type="text"  @click="checkCard(scope.row,balanceConfig)">
              余额
             </el-button>
             <el-button size="mini" type="text"  @click="checkfrom(scope.row.pkey,'优惠券记录')">
              优惠券记录
             </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <from-comp ref="FromComp" @refresh="getData"></from-comp>
    <card-comp ref="CardComp" @refresh="getData" ></card-comp>
  </div>
</template>

<script>
import qs from 'qs';
import FromComp from './sub/MembersFrom.vue';
import CardComp from './sub/MembersCard.vue';
export default {
  data() {
    return {
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      loading: false,
      searchKey: 'name',
      selectOptions: [
        //搜索的选项
        {
          name: '昵称',
          key: 'name',
        },
        {
          name: '手机号',
          key: 'mobile',
        },
      ],
      pointConfig: {
        tabs: [
          {
            pkey: 'ALL',
            name: '全部',
          },
          {
            pkey: 'POINTS_EMPTY',
            name: '已过期',
          },
        ],
        title: '积分记录',
        typeField: 'source',
      },
      balanceConfig: {
        tabs: [
          {
            pkey: 'ALL',
            name: '全部',
          },
          {
            pkey: 'false',
            name: '支出',
          },
          {
            pkey: 'true',
            name: '收入',
          },
        ],
        title: '余额',
        typeField: 'direct',
      },
      downLoading: false,
    };
  },
  mounted() {
    this.getData();
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return '会员明细';
      // return this.$store.state.activeName;
    },
    abnormalCount() {
      return this.tableData.length;
    },
  },
  components: {
    FromComp,
    CardComp,
  },
  methods: {
    /**
     * @desc 导出
     */
    handelExport() {
      const params = {
        level: 'PAID_MEMBER',
      };
      params[this.searchKey] = this.keywords;
      let that = this;
      this.downLoading = true;
      axios
        .post(api.data.exportMember, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
          responseType: 'blob',
          timeout: 0,
        })
        .then((res) => {
          let data = new Blob([res.data], {
            type: 'application/json',
          });
          var reader = new FileReader();
          reader.addEventListener('loadend', function (e) {
            if (e.target.result.indexOf('result') > 0) {
              let result = JSON.parse(e.target.result);
              that.downLoading = false;
              that.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: 'application/vnd.ms-excel',
              });
              if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                window.navigator.msSaveOrOpenBlob(blob, `${that.title}.xlsx`);
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute('download', `${that.title}.xlsx`);
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
              that.downLoading = false;
              that.$message.success('导出成功');
            }
          });
          reader.readAsText(data);
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
    /**
     * 开始搜索
     */
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    handleChange: function () {
      this.page = 1;
      this.getData();
    },
    /**
     * 查看列表
     */
    checkfrom: function (peky, title) {
      this.$refs.FromComp.show(peky, title);
    },
    /**
     * 查看详情卡片
     */
    checkCard: function (row, { tabs, title, typeField }) {
      this.$refs.CardComp.show(row, { tabs, title, typeField });
    },
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1, //显示页码
        pagesize: this.pageSize, //表格一页显示几条
        level: 'PAID_MEMBER',
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.marketing.queryMember, qs.stringify(params), {
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
  },
};
</script>
<style lang="less" scoped>
/deep/ .el-table__fixed-right {
  height: 100% !important;
}
</style>
