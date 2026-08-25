<!-- 
@name: PointAds.vue 
@description: 积分商城广告管理
@author: crj
@route: /ads/pointAds
@date: 2021/10/14
-->
<template lang="html">
  <div class="table-container">
    <!-- <h1 class="title">
      {{ title }}
      
    </h1> -->
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form"></div>
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
    </div>
    <!-- 组件 -->
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
  </div>
</template>
<script>
import qs from 'qs';
import utils from '@/assets/js/utils';
import AddComp from './sub/pointAds/AdsAdd.vue';
import EditComp from './sub/pointAds/AdsEdit.vue';
export default {
  props: ['position'],
  provide() {
    return {
      position: this.position
    };
  },
  data() {
    return {
      urlTypeObj: utils.urlTypeObj(),
      noPic: require('@/assets/images/no-pic.jpg'),
      loading: false,
      tableData: [],
      // position: 'ADVERT_POSITION_POINTS_MALL',
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
      this.$refs.AddComp.show({
        position: this.position,
      });
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
        .post(api.mall.delImg, qs.stringify(params), {
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