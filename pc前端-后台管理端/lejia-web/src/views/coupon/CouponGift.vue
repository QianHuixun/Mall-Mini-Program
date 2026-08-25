<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="enabled" @change="handleChange" placeholder="选择状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in enabledList">
          </el-option>
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
        <el-table-column label="图片" prop="title" width="100">
          <template slot-scope="scope">
            <img :src="scope.row.picture ? scope.row.picture : noPic" width="35px" height="35px" />
          </template>
        </el-table-column>
        <el-table-column label="礼品券名称" prop="title" width="100"></el-table-column>
        <el-table-column label="有效时间" prop="effectiveDate" width="180">
          <template slot-scope="scope">
            {{scope.row.effective==null?scope.row.startDate+'~'+scope.row.endDate:'领券后'+scope.row.effective+'天内'}}
          </template>
        </el-table-column>
        <el-table-column label="总数" prop="count"></el-table-column>
        <el-table-column label="已领数" prop="issuedNum"></el-table-column>
        <el-table-column label="已使用" prop="usedNum"></el-table-column>
        <el-table-column label="状态" prop="enabled">
          <template slot-scope="scope">
            <el-switch slot="reference" active-color="#13ce66" v-model="scope.row.enabled"
              @click.native="handleStatusChange(scope.row, scope.$index)" :disabled="scope.row.invalid"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template slot-scope="scope">
            <div>
              <el-button type="text" size="small" @click="handleEdit(scope.row)" :disabled="scope.row.invalid">
                编辑
              </el-button>
              <el-popconfirm title="确定使礼品券失效吗？" placement="top" @onConfirm="handleInvalid(scope.row)">
                <el-button slot="reference" type="danger" size="mini" :disabled="scope.row.invalid">
                  失效
                </el-button>
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
  import AddComp from './sub/CouponGiftAdd.vue';
  import EditComp from './sub/CouponGiftEdit.vue';
  export default {
    data() {
      return {
        noPic: require('@/assets/images/no-pic.jpg'),
        loading: false,
        numData: [],
        tableData: [],
        searchKey: 'title',
        selectOptions: [{
          name: '礼品券名称',
          key: 'title',
        }, ],
        enabledList: [{
            pkey: '',
            name: '状态',
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
        enabled: '',
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条
        keywords: '', // 搜索关键字
        total: 0, //总页数
      };
    },
    mounted() {
      this.getData();
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
       * @desc 卡券失效
       */
      handleInvalid(row) {
        if (row.isInActivity && row.enabled) {
          this.$confirm(`该礼品券已添加进礼品券活动，禁用后，卡券活动内将移除此礼品券，是否确认？`, '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            })
            .then(async () => {
              let params = {
                pkey: row.pkey,
              };
              axios
                .post(api.marketing.giftInvalid, qs.stringify(params), {
                  headers: {
                    Authorization: this.$store.state.token,
                  },
                })
                .then(() => {
                  this.$message.success('卡券失效成功');
                  this.getData();
                });
            })
            .catch(() => {})
        } else {
          let params = {
            pkey: row.pkey,
          };
          axios
            .post(api.marketing.giftInvalid, qs.stringify(params), {
              headers: {
                Authorization: this.$store.state.token,
              },
            })
            .then(() => {
              this.$message.success('卡券失效成功');
              this.getData();
            });
        }
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
       * 新增
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
       * 改变启停状态
       * @param  {[type]} row [新状态值]
       * @return {[type]}        [description]
       */
      handleStatusChange(row, index) {
        if (row.invalid) {
          return;
        }
        let url = '',
          text = '',
          status = row.enabled,
          pkey = row.pkey,
          params = {
            pkey: pkey,
          };
        if (status) {
          url = api.marketing.giftEnableStart;
          text = '启用';

        } else {
          url = api.marketing.giftEnableStop;
          text = '停用';
        }

        if (row.isInActivity && !status) {

          this.$confirm(`该礼品券已添加进礼品券活动，禁用后，卡券活动内将移除此礼品券，是否确认？`, '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            })
            .then(async () => {
              axios
                .post(url, qs.stringify(params), {
                  headers: {
                    Authorization: this.$store.state.token,
                  },
                })
                .then(() => {
                  this.$message.success(text + '成功');
                });
            })
            .catch(() => {
              this.tableData[index].enabled = true;
            })
        } else {
          axios
            .post(url, qs.stringify(params), {
              headers: {
                Authorization: this.$store.state.token,
              },
            })
            .then(() => {
              this.$message.success(text + '成功');
            });
        }
      },
      /**
       * 获取列表
       */
      getData: function () {
        this.loading = true;
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          enabled: this.enabled,
        };
        params[this.searchKey] = this.keywords;
        axios
          .post(api.marketing.giftQuery, qs.stringify(params), {
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
  /deep/ .el-switch {
    box-shadow: none;
  }

  /deep/ .el-table__fixed-right {
    height: 100% !important;
  }
</style>