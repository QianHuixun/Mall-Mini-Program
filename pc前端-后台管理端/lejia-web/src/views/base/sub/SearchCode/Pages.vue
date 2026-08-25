<template>
  <div class="table-container">
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字"
          :select-options="selectOptions"></search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" size="medium" @click="handleAdd">
          新增
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" element-loading-text="拼命导入中" element-loading-spinner="el-icon-loading" border style="width: 100%">
        <el-table-column label="关键词" prop="keyword"></el-table-column>
        <el-table-column label="排序" prop="sort"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
              <el-button slot="reference" size="mini" type="danger">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <add-comp ref="addComp" :type="type" @confirm="getData"></add-comp>
    <edit-comp ref="editComp" :type="type" @confirm="getData"></edit-comp>
  </div>
</template>

<script>
import addComp from './add.vue'
import editComp from './edit.vue'
export default {
  props: ['type'],
  data() {
    return {
      loading: false,
      searchKey: 'keyword',
      keywords: '',
      selectOptions: [{ name: '关键词', key: 'keyword' }],
      page: 1,
      pageSize: 10,
      total: 0,
      tableData: [],
    }
  },
  components: {
    addComp,
    editComp,
  },
  mounted() {
    console.log(this.type);
    this.getData()
  },
  methods: {
    handleAdd() {
      this.$refs.addComp.show()
    },
    startSearch({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    getData() {
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        module: this.type
      }
      params[this.searchKey] = this.keywords;
      axios.post(api.mall.keywordQuery, this.$qs.stringify(params))
        .then(res => {
          this.tableData = res.content
          this.total = res.total
        })
    },
    handleEdit(row) {
      this.$refs.editComp.show(row)
    },
    handleDelete({pkey}) {
      axios.post(api.mall.keywordDel, this.$qs.stringify({pkey}))
        .then(res => {
          if(res === true) this.$message.success("删除成功!")
          this.getData()
        })
    },
    handleCurrentChange(val) {
      this.page = val;
      this.getData();
    }
  }
}
</script>
