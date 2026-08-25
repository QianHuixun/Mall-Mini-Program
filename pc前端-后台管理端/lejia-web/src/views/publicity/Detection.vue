<!-- 
@name: Detection.vue 
@description: 检测信息
@author: sx
@url: /publicity/detection
@date: 2020/07/06
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="testResult" @change="handleChange" placeholder="选择状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelDownload">
          模板下载
        </el-button>
        <el-upload class="upload-demo" action="" :show-file-list="false" :http-request="handleImport" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
          <el-button type="primary" size="medium">
            导入
          </el-button>
        </el-upload>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="检测商品" prop="goods"></el-table-column>
        <el-table-column label="检测项目" prop="entry"></el-table-column>
        <el-table-column label="检测结论" prop="testResult">
          <template slot-scope="scope">
            {{scope.row.testResult ? "合格" : "不合格"}}
          </template>
        </el-table-column>
        <el-table-column label="被检商户" prop="merchant"></el-table-column>
        <el-table-column label="所属市场" prop="farmer"></el-table-column>
        <el-table-column label="检测时间" prop="testDate"></el-table-column>
        <el-table-column label="录入人员" prop="createdByName"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
  <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
    <el-button slot="reference" size="mini" type="danger">
      删除
    </el-button>
  </el-popconfirm>
</template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <!-- 组件 -->
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
  </div>
</template>
<script>
import qs from 'qs';
import AddComp from './sub/DetectionAdd.vue';
export default {
  data() {
    return {
      loading: false,
      searchKey: 'entry',
      selectOptions: [
        { name: '检测项目', key: 'entry' },
        { name: '检测商品', key: 'goods' },
        { name: '检测商户', key: 'merchant' },
      ],
      tableData: [],
      statusList: [
        { pkey: '', name: '检测结果' },
        { pkey: 'true', name: '合格' },
        { pkey: 'false', name: '不合格' },
      ],
      testResult: '',
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
    };
  },
  mounted() {
    this.getData();
  },
  components: {
    AddComp,
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
  },
  methods: {
    handleChange() {
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
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    /**
     * 模板下载
     */
    handelDownload: function () {
      location.href = api.market.downDetection;
    },
    /**
     * 导入
     */
    handleImport: function (file) {
      let params = {};
      params = new FormData();
      params.append('myfile', file.file);
      axios
        .post(api.market.importDetection, params, {
          headers: {
            Authorization: this.$store.state.token,
            contentType: 'multipart/form-data',
          },
        })
        .then((response) => {
          this.$message.success('上传成功！');
          this.getData();
        });
    },
    /**
     * 新增
     */
    handelAdd: function () {
      this.$refs.AddComp.show();
    },
    /**
     * 删除
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.market.delDetection, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success('删除成功');
          this.getData();
        });
    },
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        testResult: this.testResult,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.market.queryDetection, qs.stringify(params), {
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