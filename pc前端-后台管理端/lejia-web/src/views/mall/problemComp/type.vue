<!-- 
@name: upd.vue 
@description: 常见问题--分类管理 
@author: sx
@date: 2020/07/01
-->
<template lang="html">
  <el-dialog title="分类管理" center :visible.sync="visible" :closeOnClickModal="false" append-to-body>
    <div class="top-bar">
      <el-button type="primary" size="medium" @click="handleAddShow()">新增</el-button>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="分类名称" prop="name" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
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
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
        确 定
      </el-button>
    </div>
    <!-- 新增分类 -->
    <el-dialog title="新增分类" center :visible.sync="addVisible" :closeOnClickModal="false" append-to-body>
      <el-form>
        <el-form-item label="分类名称" label-width="100px" required>
          <el-input v-model="name" maxlength="20" placeholder="请输入分类名称"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="medium" @click="addHide">
          取 消
        </el-button>
        <el-button size="medium" type="primary" @click="handleAddSubmit" :loading="loading">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-dialog>
</template>
<script>
import qs from 'qs'
export default {
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      loading: false,
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数
      tableData: [],
      addVisible: false,
      name: '',
    };
  },
  mounted() {},
  methods: {
    show: function () {
      this.visible = true;
      this.getData()
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.visible = false;
      this.$emit('hide')
    },
    handleDelete(row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.mall.problemTypeDel, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success('删除成功');
          this.getData();
        });
    },
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
      };
      axios
        .post(api.mall.problemTypeQuery, qs.stringify(params), {
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
    /**
     * 页码改变事件
     */
    handleCurrentChange(val) {
      this.page = val;
      this.loading = true;
      this.getData();
    },
    handleSubmit() {
      this.hide()
    },
    handleAddShow() {
      this.addVisible = true
    },
    addHide() {
      this.addVisible = false;
      this.name = ''
    },
    handleAddSubmit() {
      if(!this.name) {
        this.$message.error("请输入分类名称");
        return;
      }
      let params = {
        name: this.name
      }
      axios.post(api.mall.problemTypeAdd, params, {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success("新增成功");
          this.addHide()
          this.getData()
        });
    }
  },
  props: {
    title: {
      type: String,
      default: "新增"
    }
  }
};
</script>
<style lang="less" scoped>
/deep/ .el-form {
  overflow: visible !important;
}
.table-box {
  margin-top: 12px;
}
</style>