<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-input
          class="medium-input"
          v-model="searchData.title"
          placeholder="请输入推广标题"
        ></el-input>
        <el-input
          class="medium-input"
          v-model="searchData.content"
          placeholder="请输入推广内容"
        ></el-input>
        <el-button type="primary" size="medium" @click="startSearch"
          >搜索</el-button
        >
        <el-button size="medium" @click="clearSearch">清空</el-button>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="medium"
          @click="handelAdd"
        >
          新增推广
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="市场" prop="farmerName"></el-table-column>
        <el-table-column label="推广标题" prop="title"></el-table-column>
        <el-table-column label="推广内容" prop="content"></el-table-column>
        <el-table-column label="推广封面" prop="photo">
          <template slot-scope="scope">
            <img
              :src="scope.row.photo ? scope.row.photo : noPic"
              width="35px"
              height="35px"
            />
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="enabled">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.enabled"
              @change="handleStatus(scope.row.enabled, scope.row.pkey)"
              active-color="#13ce66"
              inactive-color="#ff4949"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="新增时间" prop="createdTime"></el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-popconfirm
              title="确定删除吗？"
              placement="top"
              @onConfirm="handleDelete(scope.row)"
            >
              <el-button slot="reference" size="mini" type="danger">
                删除
              </el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>
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
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
  </div>
</template>

<script>
import AddComp from "./sub/PromoteAdd.vue";
import EditComp from "./sub/PromoteEdit.vue";
export default {
  components: {
    AddComp,
    EditComp
  },
  data() {
    return {
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: "", // 搜索关键字
      total: 0, //总页数
      tableData: [],
      loading: false,
      searchData: {
        title: "",
        content: ""
      }
      // selectOptions1: [{ name: '推广标题', key: 'title' }],
      // selectOptions2: [{ name: '推广内容', key: 'content' }],
    };
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    }
  },
  mounted() {
    this.getData();
  },
  methods: {
    getData() {
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        title: this.searchData.title,
        content: this.searchData.content
      };
      axios
        .post(api.mall.promotequery, this.$qs.stringify(params))
        .then(res => {
          // console.log(res)
          this.tableData = res.content;
          this.total = res.total;
          this.loading = false;
        });
    },
    startSearch() {
      this.page = 1;
      this.loading = true;
      this.getData();
    },
    clearSearch() {
      this.searchData = {
        title: "",
        content: ""
      };
    },
    handleCurrentChange(val) {
      this.page = val;
      this.loading = true;
      this.getData();
    },
    handelAdd() {
      this.$refs.AddComp.show();
    },
    /**
     * 点击修改
     */
    handleEdit: function(row) {
      this.$refs.EditComp.show({ row: row });
    },
    handleDelete(row) {
      const params = {
        pkey: row.pkey
      };
      axios
        .post(api.mall.promotedel, this.$qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.$message.success("删除成功");
          this.getData();
        });
    },
    handleStatus(enabled, pkey) {
      console.log(enabled, pkey);
      if (enabled) {
        this.$confirm("推广状态只能开启一个，是否继续操作？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            this.confirmStatusChange(enabled, pkey);
          })
          .catch(() => {
            this.$message("已取消开启");
            this.getData();
          });
      } else {
        this.confirmStatusChange(enabled, pkey);
      }
    },
    confirmStatusChange(enabled, pkey) {
      axios
        .post(
          enabled ? api.mall.promoteenabledstart : api.mall.promoteenabledstop,
          this.$qs.stringify({ pkey })
        )
        .then(res => {
          this.$message.success(enabled ? "启用成功" : "关闭成功");
          this.getData();
        })
        .catch(res => {
          this.getData();
        });
    }
  }
};
</script>

<style lang="less" scoped>
.search-box-form {
  display: flex;
  align-items: center;
}
.medium-input {
  width: 300px !important;
  margin-right: 10px;
}
</style>
