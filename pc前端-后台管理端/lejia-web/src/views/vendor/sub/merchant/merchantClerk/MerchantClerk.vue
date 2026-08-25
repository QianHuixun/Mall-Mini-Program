<!-- 
@name: MerchantClerk.vue 
@description: 商户管理--店员管理 
@author: crj
@date: 2022/2/10        /18
-->
<template lang="html">
  <el-dialog title="店员管理" center :visible.sync="visible" :closeOnClickModal="false" @close="hide"
    :close-on-click-modal="false">
     <div class="table-container">
        <!-- 搜索栏 -->
        <div class="search-box">
          <!-- 搜索表单 -->
          <div class="search-box-form">
            <el-input class="medium-input"  v-model="searchData.keywords"  placeholder="请输入店员名字/手机号码/商户名称"></el-input>
            <el-button  type="primary"  size="medium" @click="handleChange">
              搜索
            </el-button>
          </div>
          <!-- 操作按钮 -->
          <div class="search-box-button">
            <el-button type="primary" icon="el-icon-plus" size="medium" @click="handelAdd">
              新增
            </el-button>
          </div>
        </div>
        <!-- 表格框 -->
        <div class="table-box">
          <el-table :data="dataList" :loading="loading" border style="width: 100%" class="table-fixed">
            <el-table-column  label="序号"  min-width="80">
              <template slot-scope="scope">
                {{scope.$index+1}}
              </template>
            </el-table-column>
            <el-table-column  label="市场" prop="farmerName" min-width="120"></el-table-column>
            <el-table-column  label="商户名称" prop="vendorName" min-width="120"></el-table-column>
            <el-table-column  label="店员" prop="name" min-width="120"></el-table-column>
            <el-table-column  label="电话" prop="mobile" min-width="120"></el-table-column>
            <el-table-column  label="是否启用" prop="enabled" min-width="80">
              <template slot-scope="scope">
                <el-switch v-model="scope.row.enabled" active-color="#13ce66" inactive-color="#ff4949"
                  @change="handleEnable(scope.row.enabled,scope.row.pkey)"></el-switch>
              </template>
              </el-table-column>
            <el-table-column label="操作" width="120"   fixed="right"  align="center">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handleEdit(scope.row)" >
                  编辑
                </el-button>
                <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)" >
                  <el-button slot="reference" size="mini" type="danger">
                    删除
                  </el-button>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
          <!-- 页码 -->
          <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
            :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
        </div>
         <add-comp ref="AddComp" @refresh="getData"></add-comp>
         <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
      </div>
  </el-dialog>
</template>
<script>
export default {
  data() {
    return {
      visible: false,
      vendor: '',
      dataList: [],
      total: 0,
      loading: false,
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      searchData: {
        keywords: '',
      },
    };
  },
  components: {
    AddComp(resolve) {
      require(['./subComp/MerchantClerkAdd.vue'], resolve);
    },
    EditComp(resolve) {
      require(['./subComp/MerchantClerkEdit.vue'], resolve);
    },
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
    /**
     * @desc 改变
     */
    handleChange() {
      this.page = 1;
      this.loading = true;
      this.getData();
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.dataList = [];
      this.searchData.keywords = '';
      this.vendor = '';
    },
    /**
     * @desc  显示并初始化数据
     * @param {Array} pkey
     */
    show: function (pkey) {
      if (pkey) {
        this.vendor = pkey;
      }
      this.getData();
      this.visible = true;
    },
    getData() {
      let params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        content: this.searchData.keywords,
        vendor: this.vendor,
      };
      axios
        .post(api.market.queryClerk, this.$qs.stringify(params))
        .then((res) => {
          this.dataList = res.content;
          this.total = res.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    handelAdd() {
      this.$refs.AddComp.show();
    },
    /**
     * @Desc 编辑
     */
    handleEdit(row) {
      this.$refs.EditComp.show({ row });
    },
    handleDelete(row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.market.delClerk, this.$qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success('删除成功');
          this.getData();
        });
    },
    handleEnable(status, pkey) {
      let url = api.market.stopClerk,
        text = '',
        params = {
          pkey: pkey,
        };
      if (status) {
        params.enabled = true;
        text = '启用';
      } else {
        params.enabled = false;
        text = '停用';
      }
      axios
        .post(url, this.$qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success(text + '成功');
          this.getData();
        })
        .catch((error) => {
          this.getData();
        });
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();

      this.visible = false;
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
    settlementMethod: {
      type: String,
      default: 'PURCHASE_SETTLEMENT',
    },
  },
};
</script>
<style lang="less" scoped>
/deep/.el-dialog {
  width: 1000px !important;
}
/deep/.el-dialog__body {
  text-align: center;
  .title {
    margin-bottom: 10px;
    font-weight: bold;
    text-align: left;
  }
}
.medium-input {
  width: 300px !important;
}
.table-container > .search-box > .search-box-form > .el-input {
  margin: 5px;
}
.table-container > .search-box > .search-box-form {
  text-align: left;
}
.table-container > .table-box {
  padding-bottom: 20px;
}
</style>