<!--
 * @Author: 沙晓
 * @Date: 2025-07-04 15:05:15
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-07-30 16:05:07
 * @Description: 分类页广告
 * @FilePath: /lejia-web/src/views/market/GtypeAds.vue
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <!-- <div class="search-box-form">
      </div> -->
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增广告
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="广告图" prop="photo">
          <template slot-scope="scope">
            <img :src="scope.row.photo ? scope.row.photo : noPic" width="35px" height="35px" />
          </template>
        </el-table-column>
          <el-table-column label="展示分类" prop="positionObjName">
        </el-table-column>
        <el-table-column label="点击效果" prop="urlType">
          <template slot-scope="scope">
            {{ urlTypeObj[scope.row.urlType] }}
          </template>
        </el-table-column>
        <el-table-column label="内容" prop="objKey">
          <template slot-scope="scope">
            {{ scope.row.objKeyName ? scope.row.objKeyName : (scope.row.urlType=="GOODS" ? (scope.row.goodsName||'无') : (scope.row.urlType=="ACTIVITY" ? (scope.row.activityName||'无') : (scope.row.objKey||'无'))) }}
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sort"></el-table-column>
        <el-table-column label="创建时间" prop="createdTime"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
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
import utils from '@/assets/js/utils';
import AddComp from './sub/GtypeAds/AdsAdd.vue';
import EditComp from './sub/GtypeAds/AdsEdit.vue';

export default {
  data() {
    return {
      urlTypeObj: utils.urlTypeObj(),
      noPic: require('@/assets/images/no-pic.jpg'),
      loading: false,
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数
      // position: 'ADVERT_POSITION_GOODS_MAIN',
      isOperation: false,
      marketList: [],
      searchData: {
        farmers: [],
      },
      flag: false,
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
    position() {
      return this.$store.state.userIdentity == 1 ? 'ADVERT_POSITION_MSD_GOODS_MAIN' : 'ADVERT_POSITION_GOODS_MAIN'
    }
  },
  mounted() {
    this.getData();
  },
  components: {
    AddComp,
    EditComp,
  },
  methods: {
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
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.mall.delImg, qs.stringify(params))
        .then(() => {
          this.$message.success('删除成功');
          this.getData();
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
    /**
     * @desc 筛选
     */
    handleChange() {
      this.page = 1;
      this.getData();
    },
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          position: this.position,
        };
      axios
        .post(api.mall.queryImg, qs.stringify(params), {
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