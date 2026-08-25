<!-- 
@name: Cookfd.vue 
@description: 菜谱管理
@author: sx
@url: /cookfd/cookfd
@date: 2020/07/02
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
        <el-select v-model="enabled" @change="handleChange" placeholder="上下架" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in enabledList">
          </el-option>
        </el-select>
        <!-- <el-select v-model="recom" @change="getData" placeholder="今日推荐">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in recomList"></el-option>
        </el-select> -->
        <el-select v-model="cookfdType" @change="handleChange" placeholder="菜谱分类" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in typeList"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%" class="table-fixed">
        <el-table-column label="菜谱名称" prop="name"></el-table-column>
        <el-table-column label="所属分类" prop="ctypeName"></el-table-column>
        <el-table-column label="排序" prop="sort"></el-table-column>
        <el-table-column label="浏览数" prop="viewCount"></el-table-column>
        <el-table-column label="收藏数" prop="collCount"></el-table-column>
        <el-table-column label="创建时间" prop="createdTime"></el-table-column>
        <el-table-column label="当前状态" prop="enabled">
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.enabled"
              @change="handleStatus(scope.row.enabled,scope.row.pkey)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template slot-scope="scope">
          <div>
            <!-- <el-button
              type="text"
              size="small"
              @click="handleRecome(scope.row.recom,scope.row.pkey)"
            >{{scope.row.recom ? '取消首页推荐' : '加入首页推荐'}}</el-button> -->
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
              <el-button slot="reference" size="mini" type="danger">删除</el-button>
            </el-popconfirm>
          </div>
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
  </div>
</template>
<script>
import qs from 'qs';
import AddComp from './sub/CookfdAdd.vue';
import EditComp from './sub/CookfdEdit.vue';
import dropdown from '@/assets/js/dropdown';

export default {
  data() {
    return {
      loading: false,
      mType: '',
      searchKey: 'name',
      selectOptions: [
        {
          name: '菜谱名称',
          key: 'name',
        },
      ],
      enabledList: [
        {
          pkey: '',
          name: '启停',
        },
        {
          pkey: 'true',
          name: '启用',
        },
        {
          pkey: 'false',
          name: '停用',
        },
      ],
      // recomList: [
      //   { pkey: "", name: "今日推荐" },
      //   { pkey: "true", name: "是" },
      //   { pkey: "false", name: "否" }
      // ],
      typeList: [], //菜谱分类数据
      cookfdType: '',
      enabled: '',
      recom: '',
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      sortData: { // 排序
        sort: '',
        sortType: '',
      },
    };
  },
  mounted() {
    this.getData();
    //获取菜谱分类下拉列表
    dropdown.getCookfdType().then((result) => {
      this.typeList = result.content;
    });
  },
  components: {
    AddComp,
    EditComp,
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
    handleChange: function () {
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
     * 点击新增
     */
    handelAdd: function () {
      this.$refs.AddComp.show();
    },
    /**
     * 点击修改
     */
    handleEdit: function (row) {
      this.$refs.EditComp.show({
        row: row,
      });
    },
    /**
     * 删除
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.mkt_marketing.delCookfd, qs.stringify(params), {
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
        url = api.mkt_marketing.startCookfd;
        text = '启用';
      } else {
        url = api.mkt_marketing.stopCookfd;
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
          this.getData();
        });
    },
    handleRecome: function (status, pkey) {
      let url = '',
        text = '',
        params = {
          pkey: pkey,
        };
      if (status) {
        url = api.mkt_marketing.stopRecom;
        text = '取消';
      } else {
        url = api.mkt_marketing.startRecom;
        text = '加入';
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success(text + '成功');
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
        recom: this.recom,
        enabled: this.enabled,
        ctype: this.cookfdType,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.mkt_marketing.queryCookfd, qs.stringify(params), {
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
     * @desc: 排序
     * @param {Object} row  行数据
     */
     handleSortChange({ column, prop, order }) {
      console.log(column, prop, order)
      if(order == null) {
        this.sortData = {
          sort: '',
          sortType: '',
        }
      }
      if(order == 'ascending') {
        this.sortData.sort = false
        this.sortData.sortType = this.sortTypeList[prop]
      }
      if(order == 'descending') {
        this.sortData.sort = true
        this.sortData.sortType = this.sortTypeList[prop]
      }
      this.getData()
    },
  },
};
</script>