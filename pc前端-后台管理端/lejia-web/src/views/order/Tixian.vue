<!-- 
@name: Tixian.vue 
@description: 提现管理
@author: crj
@url: /order/tixian
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
      <div class="search-box-form">
        <el-select v-model="status" @change="handleChange" placeholder="提现状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">

      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="编号" prop="orderNumber"></el-table-column>
        <el-table-column label="提现金额" prop="comms">
          <template slot-scope="scope">
            <span>{{scope.row.comms}}（含手续费{{0.003*scope.row.comms}}）</span>
          </template>
        </el-table-column>
        <el-table-column label="提现银行" prop="accountBank"></el-table-column>
        <el-table-column label="卡号" prop="custCard"></el-table-column>
        <el-table-column label="收款人姓名" prop="custName"></el-table-column>
        <el-table-column label="申请时间" prop="createdTime"></el-table-column>
        <el-table-column label="状态" prop="statusName"></el-table-column>
        <el-table-column label="备注" prop="remark"></el-table-column>
        <el-table-column label="操作" width="180" v-if="!isOnlyBrowse">
          <template slot-scope="scope">
  <el-button slot="reference" size="mini" type="text" @click="handleEdit(scope.row)">编辑</el-button>
  <el-popconfirm title="确定已打款吗？" placement="top" @onConfirm="handleTixianchange(scope.row,'PAYMENT')" v-if="scope.row.status=='COMMDRAW_SENT'">
    <el-button slot="reference" size="mini" type="text">已打款</el-button>
  </el-popconfirm>
  <el-popconfirm title="确定同意吗？" placement="top" @onConfirm="handleTixianchange(scope.row,'AGREE')" v-if="scope.row.status=='COMMDRAW_INITIAL'">
    <el-button slot="reference" size="mini" type="text">同意</el-button>
  </el-popconfirm>
  <el-popconfirm title="确定拒绝吗？" placement="top" @onConfirm="handleTixianchange(scope.row,'REFUND')" v-if="scope.row.status=='COMMDRAW_INITIAL'">
    <el-button slot="reference" size="mini" type="text">拒绝</el-button>
  </el-popconfirm>
</template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>

    </div>
    <el-dialog :title="'提现编辑'" center :visible.sync="visible" :closeOnClickModal="false">
      <el-form>
        <el-form-item label="备注" :label-width="'100px'">
          <el-input type="textarea" maxlength="30" v-model="remark" ref="remarkInput" placeholder="请输入备注"
            show-word-limit>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="medium" @click="hide">
          取 消
        </el-button>
        <el-button size="medium" type="primary" @click="handleSubmit" :loading="confirmLoading">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import qs from 'qs';

var LODOP;
export default {
  data() {
    return {
      loading: false,
      confirmLoading: false,
      tableData: [],
      searchKey: 'code',
      selectOptions: [
        {
          name: '订单编号',
          key: 'orderNumber',
        },
      ],
      statusList: [
        {
          pkey: '',
          name: '全部',
        },
        {
          pkey: 'COMMDRAW_INITIAL',
          name: '申请中',
        },
        {
          pkey: 'COMMDRAW_SENT',
          name: '已同意',
        },
        {
          pkey: 'COMMDRAW_REFUSE',
          name: '已拒绝',
        },
        {
          pkey: 'COMMDRAW_PAID',
          name: '已打款',
        },
      ],
      status: '',
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      visible: false,
      remark: '', //备注
      peky: '', //编辑选中的pkey
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
  methods: {
    /**提交编辑 */
    handleSubmit() {
      let params = {
        pkey: this.pkey,
        remark: this.remark,
      };
      axios
        .post(api.order.updTixianRemark, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success('编辑成功');
          this.hide();
          this.getData();
        });
    },
    /**取消隐藏弹窗 */
    hide() {
      this.visible = false;
      this.clearData();
    },
    /**清空编辑数据 */
    clearData() {
      this.pkey = '';
      this.remark = '';
    },
    /**编辑备注 */
    handleEdit(row) {
      this.remark = row.remark;
      this.pkey = row.pkey;
      this.visible = true;
    },
    /**
     * 提现同意、拒绝操以及已打款操作
     * {e} 对象数据
     * type 类型 'PAYMENT';'AGREE';'REFUND'
     */
    handleTixianchange(e, type) {
      console.log(e, type);
      let params = {
          pkey: e.pkey,
        },
        url,
        txt;
      switch (type) {
        case 'PAYMENT':
          url = api.order.paymentTixian;
          txt = '已打款成功';
          break;
        case 'AGREE':
          url = api.order.agreeTixian;
          txt = '同意成功';
          break;
        case 'REFUND':
          url = api.order.refuseTixian;
          txt = '拒绝成功';
          break;
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success(txt);
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
     * 开始搜索
     */
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    /**搜索条件改变事件 */
    handleChange: function () {
      this.page = 1;
      this.getData();
    },
    /**
     * 获取列表
     */
    getData: function () {
      let params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        status: this.status,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.order.queryTixian, qs.stringify(params), {
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
<style lang="less" scoped>
/deep/.el-textarea .el-input__count {
  bottom: -5px;
  background: transparent;
}
</style>