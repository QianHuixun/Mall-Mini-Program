<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
        <el-select v-model="replyStatus" @change="handleChange" placeholder="回复状态" clearable >
          <el-option value="" label="全部"></el-option>
          <el-option value="REPLIED" label="已回复"></el-option>
          <el-option value="NOT_REPLIED" label="未回复"></el-option>
        </el-select>
        <el-select v-model="applyStatus" @change="handleChange" placeholder="审核状态" clearable >
          <el-option value="" label="全部"></el-option>
          <el-option value="APPLY" label="开启"></el-option>
          <el-option value="NOT_APPLY" label="关闭"></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleSet"  v-if="userIdentity == 1">
          评价功能
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleMultiPass">
          批量通过
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleMultiHide">
          批量隐藏
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%"  class="table-fixed" @selection-change="handleSelectionChange">
        <el-table-column  align="center" type="selection" key="1"> </el-table-column>
        <el-table-column label="评价用户" prop="memberMobile"  min-width="110"></el-table-column>
        <el-table-column label="评价订单号" prop="orderCode" min-width="150"></el-table-column>
        <el-table-column label="评价商品" prop="goodsName"  show-overflow-tooltip  min-width="140"></el-table-column>
        <el-table-column label="评分" prop="score"></el-table-column>
        <el-table-column label="评价内容" prop="content" show-overflow-tooltip min-width="140"></el-table-column>
        <el-table-column label="评价图片" prop="photo">
          <template slot-scope="scope">
            <el-image v-if="scope.row.photo.length" :src="scope.row.photo[0]" style="width: 50px; height: 50px;display:block;"
            :preview-src-list="scope.row.photo"></el-image>
            <div v-else>无</div>
          </template>
        </el-table-column>
        <el-table-column label="评价时间" prop="createdTime"  min-width="150"></el-table-column>
        <el-table-column label="回复状态" prop="replyStatusName"></el-table-column>
        <el-table-column label="审核状态" prop="applyStatus">
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.status" @change="handleStatus(scope.row.status,scope.row.pkey)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="建档时间" prop="createdTime"  min-width="150"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)" v-if="scope.row.replyStatus == 'NOT_REPLIED'">
              回复
            </el-button>
            <el-button type="text" size="small" @click="handleLook(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <!-- 组件 -->
    <set-comp ref="SetComp"></set-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
    <look-comp ref="LookComp"></look-comp>
  </div>
</template>
<script>
import qs from 'qs';
import SetComp from './sub/GoodsComment/CommentSet.vue';
import EditComp from './sub/GoodsComment/CommentEdit.vue';
import LookComp from './sub/GoodsComment/CommentLook.vue';
export default {
  data() {
    return {
      loading: false,
      replyStatus	: "",
      applyStatus	: "",
      searchKey: 'memberMobile',
      selectOptions: [
        {
          name: '手机号',
          key: 'memberMobile',
        },
        {
          name: '订单号',
          key: 'orderCode',
        },
        {
          name: '评价商品',
          key: 'goodsName',
        },
      ],
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      multipleSelection: [],
    };
  },
  mounted() {
    this.getData();
  },
  components: {
    SetComp,
    EditComp,
    LookComp
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
  //  判断账号类型
    userIdentity: function() {
      return this.$store.state.userIdentity; //1 运营商 2 市场 3公司
    },
  },
  methods: {
    // 批量通过
    handleMultiPass: function() {
      console.log("this.multipleSelection",this.multipleSelection)
      if (!this.multipleSelection.length) {
        this.$message.warning('请选择批量通过数据');
        return;
      }
      const params = {
        pkeys: this.multipleSelection
          .map((item) => {
            return item.pkey;
          })
          .join(','),
          applyStatus: "APPLY"
      };
      axios.post(api.market.CommentApply, qs.stringify(params)).then(() => {
        this.$message.success("通过成功！")
        this.page = 1
        this.getData()
      })
    },
    // 批量隐藏
    handleMultiHide: function() {
      if (!this.multipleSelection.length) {
        this.$message.warning('请选择批量隐藏数据');
        return;
      }
      const params = {
        pkeys: this.multipleSelection
          .map((item) => {
            return item.pkey;
          })
          .join(','),
          applyStatus: "NOT_APPLY"
      };
      axios.post(api.market.CommentApply, qs.stringify(params)).then(() => {
        this.$message.success("隐藏成功！")
        this.page = 1
        this.getData()
      })
    },
    /**筛选 */
        handleChange() {
          this.page = 1;
          this.getData();
        },
    /**
     * @desc: 表格选中回调
     * @param {Object} Selection  被选中的行数据集合
     */
    handleSelectionChange(selection) {
      this.multipleSelection = selection;
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
     * 评价功能
     */
    handleSet: function () {
      this.$refs.SetComp.show();
    },
    /**
     * 点击回复
     */
    handleEdit: function (row) {
      this.$refs.EditComp.show({ row: row });
    },
    /**
     * 点击查看
     */
    handleLook: function (row) {
      this.$refs.LookComp.show({ row: row });
    },

    /**
     * 启停状态
     * @param  {[type]} status [新状态值]
     * @param  {[type]} pkey   [记录的pkey]
     * @return {[type]}        [description]
     */
    handleStatus: function (status, pkey) {
      const text =  status ? '开启' : '关闭';
      const params = {
          pkeys: pkey,
          applyStatus: status ? "APPLY" : 'NOT_APPLY'
        };
      axios
        .post(api.market.CommentApply, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success(`${text}成功 `);
          this.getData();
        })
        .catch(() => {
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
        replyStatus: this.replyStatus,
        applyStatus: this.applyStatus
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.market.CommentQuery, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.tableData = response.content.map(item=> {
            item.status = item.applyStatus== 'APPLY' ? true : false;
            return item;
          });
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
  },
};
</script>