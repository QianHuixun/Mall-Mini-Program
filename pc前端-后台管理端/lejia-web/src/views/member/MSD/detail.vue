<!--
* @description 热力豆管理
* @fileName TagManage.vue
* @author zs
* @date 2025/08/19
!-->
<template lang="html">
  <div class="table-container">
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
        <el-select v-model="searchData.tags" multiple collapse-tags clearable placeholder="用户标签" @change="handleChange">
          <el-option v-for="item in tagsList" :key="item.pkey" :value="item.pkey" :label="item.name"></el-option>
        </el-select>
        <el-select v-model="searchData.operationTypes" multiple collapse-tags clearable placeholder="类型" @change="handleChange">
          <el-option label="消费" value="CONSUME"></el-option>
          <el-option label="清空" value="CLEAR"></el-option>
          <el-option label="充值" value="RECHARGE"></el-option>
          <el-option label="手动调整" value="MANUAL_ADJUST"></el-option>
        </el-select>
        <el-date-picker v-model="searchData.date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" size="medium" @click="handleDownload">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="会员名称" prop="name"></el-table-column>
        <el-table-column label="手机号" prop="mobile"></el-table-column>
        <el-table-column label="用户标签" prop="tagName"></el-table-column>
        <el-table-column label="类型" prop="operationTypeName"></el-table-column>
        <el-table-column label="交易金额" prop="amt"></el-table-column>
        <el-table-column label="热力豆余额" prop="balance"></el-table-column>
        <el-table-column label="备注" prop="remark"></el-table-column>
        <el-table-column label="时间" prop="createdTime"></el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      loading: false,
      searchKey: 'mobile',
      selectOptions: [{ name: '手机号', key: 'mobile' }, { name: '备注', key: 'remark' }],
      searchData: {
        tags: [],
        operationTypes: [],
        date: [],
      },
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      tagsList: [],
    };
  },
  mounted() {
    this.getData();
    this.getTagList()
  },
  methods: {
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
     * 搜索选项改变
     */
    handleChange() {
      this.getData()
    },
    /**
     * 点击新增
     */
    handelAdd: function () {
      this.$refs.AddComp.show();
    },
    /**
     * 点击修改
     */
    handleEdit: function (row) {
      this.$refs.EditComp.show({ row: row });
    },
    /**
     * 删除
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.marketing.tagsDel, this.$qs.stringify(params), {
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
     * @return {[type]} [description]
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        tags: this.searchData.tags.join(','),
        operationTypes: this.searchData.operationTypes.join(','),
        startDate: this.searchData.date && this.searchData.date.length ? this.searchData.date[0] : '',
        endDate: this.searchData.date && this.searchData.date.length ? this.searchData.date[1] : '',
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.marketing.msdLineQuery, this.$qs.stringify(params), {
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
    /**列表导出 */
    handleDownload() {
      const params = {
        tags: this.searchData.tags.join(','),
        operationTypes: this.searchData.operationTypes.join(','),
        startDate: this.searchData.date && this.searchData.date.length ? this.searchData.date[0] : '',
        endDate: this.searchData.date && this.searchData.date.length ? this.searchData.date[1] : '',
      }
      params[this.searchKey] = this.keywords;
      axios
        .post(api.marketing.msdLineExport, this.$qs.stringify(params), {
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
              this.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: 'application/vnd.ms-excel',
              });
              var disposition = res.headers['content-disposition'];
              var headersFileName = disposition ? disposition.split('=') : '';
              if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                window.navigator.msSaveOrOpenBlob(
                  blob,
                  headersFileName && headersFileName.length != 0 ?
                  decodeURI(headersFileName[1]) :
                  `${'充值模板'
                  }.xlsx`
                );
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute(
                  'download',
                  headersFileName && headersFileName.length != 0 ?
                  decodeURI(headersFileName[1]) :
                  `${'充值模板'
                  }.xlsx`
                );
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
              this.$message.success('导出成功');
            }
          });
          reader.readAsText(data);
        });
    },
    getTagList() {
      axios
        .post(api.marketing.msdTagDrop)
        .then((response) => {
          this.tagsList = response
        });
    }
  },
};
</script>

<style lang="less" scoped>
.search-box-form .el-select {
  width: 200px !important;
}
</style>