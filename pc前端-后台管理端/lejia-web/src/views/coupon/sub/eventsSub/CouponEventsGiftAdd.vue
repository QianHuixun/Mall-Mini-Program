<!--
* @description 添加礼品券
* @fileName CouponEventsFavorableAdd.vue
* @author zs
* @date 2024/04/28
!-->
<template lang="html">
  <el-dialog title="添加礼品券" :visible.sync="visible" :closeOnClickModal="false" width="1500px"
    :modal-append-to-body="false" append-to-body>
    <div class="table-container">
      <!-- 搜索栏 -->
      <div class="search-box">
        <!-- 搜索表单 -->
        <div class="search-box-form">
          <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
          </search-bar>
        </div>
      </div>
      <!-- 表格框 -->
      <div class="table-box">
        <el-table ref="multipleTable" :data="tableData" :loading="loading" border style="width: 100%"
          @selection-change="handleSelectionChange" row-key="pkey">
          <el-table-column type="selection" width="55" :reserve-selection="true">
          </el-table-column>
          <el-table-column label="名称" prop="title"></el-table-column>
          <el-table-column label="有效时间">
            <template slot-scope="scope">
              {{scope.row.effective==null?scope.row.startDate+'~'+scope.row.endDate:'领券后'+scope.row.effective+'天内'}}
            </template>
          </el-table-column>
          <el-table-column label="库存数量" prop="wareTypeName">
            <template slot-scope="scope">
              {{scope.row.count-scope.row.issuedNum}}
            </template>
          </el-table-column>
          <el-table-column label="创建时间" prop="createdTime"></el-table-column>
        </el-table>
        <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total" :current-page="page"
          :page-size="pageSize" @current-change="handleCurrentChange" @size-change="handleSizeChange"></el-pagination>
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button size="medium" type="primary" :loading="loading" @click="handleSubmit">
        确 定
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
  import qs from "qs";

  export default {
    data() {
      return {
        visible: false,
        loading: false,
        tableData: [],
        total: 0, //总页数
        pageSize: 10, //一页的数量
        page: 1, //页数
        searchKey: 'title',
        selectOptions: [{
          name: '卡券名称',
          key: 'title',
        }, ],
        keywords: '', // 搜索关键字
        selectList: []
      };
    },
    computed: {},
    components: {},
    mounted() {},
    methods: {
      /**
       * 开始搜索
       */
      startSearch: function ({
        key,
        keywords
      }) {
        this.keywords = keywords;
        this.searchKey = key;
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
      handleSizeChange(val) {
        this.pageSize = val
        this.loading = true;
        this.getData();
      },
      handleSelectionChange(val) {
        console.log('更改选中', val);
        this.selectList = val;
      },
      /**清空数据 */
      clearData() {
        this.tableData = [];
      },
      /**显示弹窗 */
      show: function () {
        this.visible = true;
        this.clearData();
        this.getData();
        this.$nextTick(() => {
          this.toggleSelection([]);
        });
      },
      toggleSelection(rows) {
        console.log('rows', rows)
        this.$refs.multipleTable.clearSelection();
        if (rows.length) {
          rows.forEach((row) => {
            this.$refs.multipleTable.toggleRowSelection(row);
          });
        } else {
          this.$refs.multipleTable.clearSelection();
        }
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.clearData();
        this.visible = false;
        this.$emit('hide');
      },
      /**
       * 确定
       */
      handleSubmit() {
        this.visible = false;
        this.$emit('confirm', {
          selectList: this.selectList,
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
          enabled: true,
          invalid: false,
        };
        params[this.searchKey] = this.keywords;
        /**获取表格数据 */
        axios.post(api.marketing.giftQuery, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(res => {
            this.tableData = res.content;
            this.total = res.total;
            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
      },
    }
  }
</script>
<style lang="less" scoped>
  /deep/.el-dialog {
    padding-bottom: 20px;
  }

  /deep/.table-box {
    .el-input .el-input__inner {
      height: 28px;
      line-height: 28px;
    }
  }
</style>