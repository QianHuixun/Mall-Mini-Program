<!-- 
@name: Ads.vue 
@description: 弹窗广告管理
@author: crj
@url: /popup/ads
@date: 2020/09/22
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form"></div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="medium"
          @click="handleAdd"
        >
          新增
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="市场" prop="farmerName"></el-table-column>
        <el-table-column label="弹窗图" prop="photo">
          <template slot-scope="scope">
            <img
              :src="scope.row.photo ? scope.row.photo : noPic"
              width="35px"
              height="35px"
            />
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
        <el-table-column label="开始时间" prop="startDate"></el-table-column>
        <el-table-column label="结束时间" prop="endDate"></el-table-column>
        <el-table-column label="创建时间" prop="createdTime"></el-table-column>
        <el-table-column label="用户群体" prop="subjectName"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <div>
              <el-button
                slot="reference"
                size="mini"
                type="text"
                @click="handleEdit(scope.row)"
                >编辑</el-button
              >
              <el-popconfirm
                title="确定删除吗？"
                placement="top"
                @onConfirm="handleDelete(scope.row)"
              >
                <el-button slot="reference" size="mini" type="danger"
                  >删除</el-button
                >
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
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
    </div>
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
  </div>
</template>
<script>
import qs from "qs";
import utils from '@/assets/js/utils';
import AddComp from "./sub/ads/AdsAdd.vue";
import EditComp from "./sub/ads/AdsEdit.vue";

var LODOP;
export default {
  data() {
    return {
      urlTypeObj: utils.urlTypeObj(),
      noPic: require("@/assets/images/no-pic.jpg"), //无图片的占位图
      loading: false,
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0 //总页数
    };
  },
  mounted() {
    this.getData();
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
  components: {
    AddComp,
    EditComp
  },
  methods: {
    /**
     * 编辑事件
     */
    handleEdit: function(row) {
      this.$refs.EditComp.show({
        row: row
      });
    },
    /**
     * 删除事件
     */
    handleDelete: function(row) {
      const params = {
        pkey: row.pkey
      };
      axios
        .post(api.popup.delPopAds, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.$message.success("删除成功");
          this.getData();
        });
    },
    /**
     * 新增事件
     */
    handleAdd() {
      this.$refs.AddComp.show();
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
     * 获取列表
     */
    getData: function() {
      let params = {
        page: this.page - 1,
        pagesize: this.pageSize
      };
      axios
        .post(api.popup.queryPopAds, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    }
  }
};
</script>
