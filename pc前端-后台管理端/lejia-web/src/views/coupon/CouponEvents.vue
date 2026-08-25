<!--
* @description 卡券活动
* @fileName CouponEvents.vue
* @author zs
* @date 2024/04/26
!-->
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
        <el-table-column label="ID" prop="pkey" width="100"></el-table-column>
        <el-table-column label="活动名称" prop="name"></el-table-column>
        <el-table-column label="包含卡券" prop="couponNum" width="100"></el-table-column>
        <el-table-column label="总数" prop="num" width="100"></el-table-column>
        <el-table-column label="已领取人数" prop="issuedNum" width="150">
          <template slot-scope="scope">
            <div>
              <span style="width:50px;display: inline-block;">{{scope.row.issuedNum}}</span>
              <el-button type="text" size="small" @click="handleLook(scope.row)">
                查看
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="已领取卡券数" prop="receiveNum" width="100"></el-table-column>
        <el-table-column label="已使用卡券数" prop="useNum" width="100"></el-table-column>
        <el-table-column label="状态" prop="enabled" width="100">
          <template slot-scope="scope">
            <el-switch slot="reference" active-color="#13ce66" v-model="scope.row.enabled"
              @click.native="handleStatusChange(scope.row,scope.$index)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="分发方式" prop="distributeTypeName" width="120"></el-table-column>
        <el-table-column label="售卖价格" prop="price" width="100"></el-table-column>
        <el-table-column label="创建时间" prop="createdTime"></el-table-column>

        <el-table-column label="操作" width="260" fixed="right">
          <template slot-scope="scope">
            <div>
              <el-button type="text" size="small" @click="handleEdit(scope.row)">
                编辑
              </el-button>
              <el-button v-if="scope.row.distributeType == 'QRCode'" type="text" size="small" @click="handleImg(scope.row)">
                活动二维码
              </el-button>
              <el-button v-if="scope.row.distributeType == 'QRCode'" type="text" size="small" @click="handlepopUpImg(scope.row)">
                弹框二维码
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange" @size-change="handleSizeChange"></el-pagination>
    </div>
    <!-- 组件 -->
    <events-records ref="eventsRecords"></events-records>
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
  </div>
</template>
<script>
  import qs from 'qs';
  import eventsRecords from './sub/eventsSub/CouponEventsRecords.vue';
  import AddComp from './sub/eventsSub/CouponEventsAdd.vue';
  import EditComp from './sub/eventsSub/CouponEventsEdit.vue';
  export default {
    data() {
      return {
        loading: false,
        tableData: [],
        searchKey: 'name',
        selectOptions: [{
          name: '名称',
          key: 'name',
        }, ],
        enabledList: [{
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
      eventsRecords,
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
      handleSizeChange(val) {
        this.pageSize = val
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
      /**查看弹窗 */
      handleLook(row) {
        this.$refs.eventsRecords.show({
          row: row,
        });
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
      handleStatusChange: function (row, index) {
        console.log(row,'row');
        let text = '',
          status = row.enabled,
          pkey = row.pkey,
          params = {
            pkey: pkey,
            enabled: status
          };
        if (status) {
          text = '启用';
        } else {
          text = '停用';
        }
        axios
          .post(api.marketing.activityEnable, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then(() => {
            this.$message.success(text + '成功');
          }).catch(()=> {
            this.tableData[index].enabled = !status;
          });
      },

      /**
       * 活动二维码
       */
      handleImg: function (row) {
        location.href = api.marketing.activityQrCodeDown + '?pkey=' + row.pkey;
      },
      /**
       * 弹框二维码
       */
      handlepopUpImg: function (row) {
        location.href = api.marketing.activityPopUpQrCodeDown + '?pkey=' + row.pkey;
      },
      /**
       * 获取列表
       */
      getData: function () {
        this.loading = true;
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          enabled: this.enabled
        };
        params[this.searchKey] = this.keywords;
        axios
          .post(api.marketing.activityQuery, qs.stringify(params), {
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
    // border: green solid 1px !important;
    box-shadow: none;
  }

  /deep/ .el-table__fixed-right {
    height: 100% !important;
  }
</style>