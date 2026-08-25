<!-- 
@name: Classic.vue 
@description: 商品分类
@author: sx
@route: /base/classic
@date: 2020/06/28
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
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增分类
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="图片" prop="photo">
            <template slot-scope="scope">
              <img :src="scope.row.photo ? scope.row.photo : noPic" width="35px" height="35px" />
            </template>
          </el-table-column>
        <el-table-column label="所属平台">
        	<template slot-scope="scope">
            {{ scope.row.showMarket ? "市场" : ""}}
            {{scope.row.showMarket && scope.row.showPoint ? "," : ""}}
            {{ scope.row.showPoint ? "积分商城" : ""}}
            {{!scope.row.showMarket && !scope.row.showPoint ? "无" : ""}}
          </template>	
        </el-table-column>
        <el-table-column label="市场排序" prop="marketSort">
        </el-table-column>
        <el-table-column label="积分排序" prop="pointSort">
        </el-table-column>
        <el-table-column label="是否启用" prop="enabled">
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.enabled" @change="handleStatus(scope.row.enabled,scope.row.pkey)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)" v-if="scope.row.name!='优惠券'&&scope.row.name!='礼券'">
              <el-button slot="reference" size="mini" type="danger" :disabled="scope.row.name=='优惠券'||scope.row.name=='礼券'">
                删除
              </el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
      <!-- 组件 -->
      <add-comp ref="AddComp" @refresh="getData"></add-comp>
      <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
    </div>
  </div>
</template>
<script>
import qs from 'qs';
import AddComp from './sub/ClassicAdd.vue';
import EditComp from './sub/ClassicEdit.vue';
export default {
  data() {
    return {
      noPic: require('@/assets/images/no-pic.jpg'),
      loading: false,
      searchKey: 'gtyprName',
      selectOptions: [{ name: '分类名称', key: 'gtyprName' }],
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
    };
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
  components: {
    AddComp,
    EditComp,
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
     * 删除公司
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.mall.delClassic, qs.stringify(params), {
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
        url = api.mall.startClassic;
        text = '启用';
      } else {
        url = api.mall.stopClassic;
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
     * @return {[type]} [description]
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.mall.queryClassic, qs.stringify(params), {
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