<!-- 
@name: Company.vue 
@description: 公司市场管理
@author: sx
@url: /operation/company
@date: 2020/06/23
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
        <!-- <el-select v-model="status" @change="getData" placeholder="选择状态">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList"></el-option>
        </el-select> -->
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button"  v-if="!isOnlyBrowse">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增公司
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column type="expand">
          <template slot-scope="props" v-if="props.row.markets.length > 0">
            <el-table :data="props.row.markets">
              <el-table-column width="48"></el-table-column>
              <el-table-column label="名称" prop="name"></el-table-column>
              <el-table-column label="管理员" prop="manager"></el-table-column>
              <el-table-column label="手机号" prop="mobile"></el-table-column>
              <el-table-column label="创建时间" prop="createdTime"></el-table-column>
              <el-table-column label="是否启用" prop="enabled">
                <template slot-scope="scope">
                  <el-switch active-color="#13ce66" v-model="scope.row.enabled"
                    @change="handleMarketStatus(scope.row.enabled,scope.row.pkey)" :disabled="isOnlyBrowse"></el-switch>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="199" v-if="!isOnlyBrowse">
                <template slot-scope="scope">
                  <el-button type="text" size="small" @click="handleMarketEdit(scope.row)">
                    编辑
                  </el-button>
                  <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleMarketDelete(scope.row)">
                    <el-button slot="reference" size="mini" type="danger">
                      删除
                    </el-button>
                  </el-popconfirm>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-table-column>
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="管理员" prop="manager"></el-table-column>
        <el-table-column label="手机号" prop="mobile"></el-table-column>
        <el-table-column label="创建时间" prop="createdTime"></el-table-column>
        <el-table-column label="是否启用" prop="enabled">
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.enabled"
              @change="handleStatus(scope.row.enabled,scope.row.pkey)" :disabled="isOnlyBrowse"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" v-if="!isOnlyBrowse">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleMarketAdd(scope.row)">
              新增市场
            </el-button>
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
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
    <!-- 组件 -->
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
    <add-market-comp ref="AddMarketComp" @refresh="getData"></add-market-comp>
    <edit-market-comp ref="EditMarketComp" @refresh="getData"></edit-market-comp>
  </div>
</template>
<script>
import qs from 'qs';
import AddComp from './sub/CompanyAdd.vue';
import EditComp from './sub/CompanyEdit.vue';
import AddMarketComp from './sub/MarketAdd.vue';
import EditMarketComp from './sub/MarketEdit.vue';
export default {
  data() {
    return {
      loading: false,
      searchKey: 'companyName',
      selectOptions: [
        {
          name: '公司名称',
          key: 'companyName',
        },
      ],
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
    };
  },
  components: {
    AddComp,
    EditComp,
    AddMarketComp,
    EditMarketComp,
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
    /**是否为仅浏览 */
    isOnlyBrowse() {
      let hasBrowse = false;
      if (this.$store.state.activeName) {
        hasBrowse =
          this.$store.state.activeName.indexOf('仅浏览') > 0 ? true : false;
      }
      return hasBrowse;
    },
  },
  mounted() {
    this.getData();
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
     * 点击新增公司
     */
    handelAdd: function () {
      this.$refs.AddComp.show();
    },
    /**
     * 点击新增市场
     */
    handleMarketAdd: function (row) {
      this.$refs.AddMarketComp.show({
        pkey: row.pkey,
      });
    },
    /**
     * 点击修改公司
     */
    handleEdit: function (row) {
      this.$refs.EditComp.show({
        row: row,
      });
    },
    /**
     * 点击修改市场
     */
    handleMarketEdit: function (row) {
      this.$refs.EditMarketComp.show({
        row: row,
      });
    },
    /**
     * 删除公司
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.market.delCom, qs.stringify(params), {
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
     * 删除市场
     */
    handleMarketDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.market.delMarket, qs.stringify(params), {
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
     * 启停状态
     * @param  {[type]} status [新状态值]
     * @param  {[type]} pkey   [记录的pkey]
     * @return {[type]}        [description]
     */
    handleStatus: function (status, pkey) {
      let url = '',
        text = '',
        params = {
          pkey: pkey,
        };
      if (status) {
        url = api.market.startCom;
        text = '启用';
      } else {
        url = api.market.stopCom;
        text = '停用';
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success(text + '成功');
          // this.getData();
        });
    },
    /**
     * 启停状态--市场
     * @param  {[type]} status [新状态值]
     * @param  {[type]} pkey   [记录的pkey]
     * @return {[type]}        [description]
     */
    handleMarketStatus: function (status, pkey) {
      let url = '',
        text = '',
        params = {
          pkey: pkey,
        };
      if (status) {
        url = api.market.startMarket;
        text = '启用';
      } else {
        url = api.market.stopMarket;
        text = '停用';
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success(text + '成功');
          // this.getData();
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
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.market.queryCom, qs.stringify(params), {
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