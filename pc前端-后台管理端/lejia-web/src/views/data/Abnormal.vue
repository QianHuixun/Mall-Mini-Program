<!-- 
@name: Abnormal.vue 
@description: 异常货物统计页面
@author: crj
@url: /data/abnormal
@date: 2020/08/14
-->

<template lang="html">
  <div class="table-container">
    <h1 class="title">{{ title }}</h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelEdit">交易异常设置</el-button>
        <div class="count-box">异常总数：{{total}}</div>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button v-loading="downLoading" type="primary" icon="el-icon-edit" size="medium" @click="handelExport">导出</el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table  :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column  label="货品名称" prop="name" ></el-table-column>
        <el-table-column  label="近30天销售额" prop="Sales" ></el-table-column>
        <el-table-column label="近30天销售笔数" prop="SalesNum"></el-table-column>
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
     <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
  </div>
</template>

<script>
import qs from 'qs';
import EditComp from './sub/AbnormalEdit.vue';
export default {
  data() {
    return {
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      loading: false,
      marketData: {},
      downLoading: false,
    };
  },
  mounted() {
    this.getData();
    this.getMaretData();
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return '异常货物分析';
      // return this.$store.state.activeName;
    },
  },
  components: {
    EditComp,
  },
  methods: {
    /**
     * @desc 导出
     */
    handelExport() {
      const params = {};
      let that = this;
      this.downLoading = true;
      axios
        .post(api.data.exportAbnormal, qs.stringify(params), {
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
     * 编辑时间
     */
    handelEdit() {
      this.$refs.EditComp.show(this.marketData);
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
     * 获取市场信息
     */
    getMaretData() {
      axios
        .post(api.market.getMarketInfo, '', {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.marketData = response;
        });
    },
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1, //显示页码
        pagesize: this.pageSize, //表格一页显示几条
      };
      axios
        .post(api.data.getAbnormal, qs.stringify(params), {
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
.count-box {
  display: inline-block;
  color: #409eff;
  margin-left: 20px;
}
</style>
