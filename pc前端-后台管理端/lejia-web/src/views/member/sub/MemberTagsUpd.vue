<template lang="html">
  <el-dialog title="打标签" :visible.sync="visible" :closeOnClickModal="false" :modal-append-to-body="false">
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
          <el-table-column label="标签名称" prop="name"></el-table-column>
          <el-table-column label="标签描述" prop="description">
          </el-table-column>
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
        searchKey: 'name',
        selectOptions: [{
          name: '标签名称',
          key: 'name',
        }, {
          name: '标签描述',
          key: 'description',
        }, ],
        keywords: '', // 搜索关键字
        selectList: [],
        pkeys: [],
        defineSelectList: [],
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
      /**初始化列表数据 */
      defineData() {
        this.page = 1;
        this.tableData = [];
        this.getData();
        this.$nextTick(() => {
          this.toggleSelection(this.defineSelectList);
        })
      },
      /**显示弹窗 */
      show: function (pkeys) {
        this.visible = true;
        this.pkeys = pkeys;
        this.getTagData();
      },

      getTagData() {
        let that = this;
        this.defineSelectList = [];
        const params = {};
        if (this.pkeys.length == 1) {
          params['pkey'] = this.pkeys[0];
        } else {
          this.defineData();
          return;
        }
        /**获取表格数据 */
        axios.post(api.marketing.tagsGetTrue, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(res => {
            res.forEach(item => {
              const model = {
                pkey: item
              };
              that.defineSelectList.push(model);
            });
            that.defineData();
          });
      },
      toggleSelection(rows) {
        console.log('rows', rows)
        if (this.$refs.multipleTable) {
          this.$refs.multipleTable.clearSelection();
          this.$nextTick(() => {
            rows.forEach(row => {
              this.$refs.multipleTable.toggleRowSelection(row, true);
            });
          });
        }
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.$refs.multipleTable.clearSelection();
        this.visible = false;
      },
      /**
       * 确定
       */
      handleSubmit() {
        this.loading = true;
        const arr = [];
        this.selectList.forEach(item => {
          arr.push(item.pkey);
        });
        const params = {
          pkeys: this.pkeys.join(','),
          tagKeys: arr.join(',')
        };
        axios
          .post(api.marketing.tagsMarkTag, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then((response) => {
            this.$message.success('打标签成功');
            this.$emit('refresh');
            this.visible = false;
          });

        setTimeout(() => {
          this.loading = false;
        }, 300);
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
        if (this.pkeys.length == 1) {
          params['pkey'] = this.pkeys[0];
        }
        params[this.searchKey] = this.keywords;
        /**获取表格数据 */
        axios.post(api.marketing.tagsGet, qs.stringify(params), {
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