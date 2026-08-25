<!-- 
@name: Coupon.vue 
@description: 卡券管理
@author: sx
@url: /coupon/coupon
@date: 2020/07/07
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
        <el-select v-model="enabled" @change="handleChange" placeholder="选择状态" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in enabledList">
          </el-option>
        </el-select>
        <el-select v-model="invalid" @change="handleChange"  clearable>
          <el-option :value="false"  label="正常" > </el-option>
          <el-option :value="true"  label="失效" > </el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增优惠券
        </el-button>
        <!-- <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelDownCode">
          领券中心二维码
        </el-button> -->
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%" class="table-fixed">
        <el-table-column label="卡券ID" prop="pkey" width="100"></el-table-column>
        <el-table-column label="优惠券名称" prop="title" width="100"></el-table-column>
        <el-table-column label="类型" prop="typeName"></el-table-column>
        <el-table-column label="优惠金额" prop="cost">
          <template slot-scope="scope">
            {{scope.row.type=='POSTAGE_COUPON' && scope.row.avoidPostage ? '免配送费' : scope.row.cost}}
          </template>
        </el-table-column>
        <el-table-column label="使用条件" prop="limitCost">
        </el-table-column>
        <!--
        <el-table-column label="有效时间" prop="effectiveDate" width="180">
          <template slot-scope="scope">
            {{scope.row.effectiveDate!=null?scope.row.startDate+'~'+scope.row.endDate:'领券后'+scope.row.effective+'天内'}}
          </template>
        </el-table-column>
        <el-table-column label="使用范围" prop="rangUse" width="150"></el-table-column> -->
        <el-table-column label="创建时间" prop="createdTime" width="150"></el-table-column>
        <el-table-column label="总数" prop="count"></el-table-column>
        <el-table-column label="已领数" prop="issuedNum"></el-table-column>
        <el-table-column label="已使用" prop="usedNum"></el-table-column>
        <el-table-column label="是否启用" prop="enabled">
          <template slot-scope="scope">
            <el-switch slot="reference" active-color="#13ce66" v-model="scope.row.enabled" 
            @click.native="handleStatuschange(scope.row,scope.$index)" :disabled="scope.row.invalid"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template slot-scope="scope">
            <div>
              <el-button type="text" size="small" @click="handleEdit(scope.row)" :disabled="scope.row.invalid"> 
                编辑
              </el-button>
              <el-button type="text" size="small" @click="handleImg(scope.row)" :disabled="scope.row.invalid">
                生成二维码
              </el-button>
              <el-popconfirm :title="scope.row.cardType=='CARD_CENTER'?'确定移除吗？':'确定加入吗？'" placement="top" @onConfirm="handleCardcenter(scope.row)" >
                <el-button slot="reference" :type="scope.row.cardType=='CARD_CENTER'?'danger':'text'" size="mini" :disabled="scope.row.invalid">
                  {{scope.row.cardType=='CARD_CENTER'?'移除领券中心':'加入领券中心'}}
                </el-button>
              </el-popconfirm>
              <el-popconfirm title="确定使卡券失效吗？" placement="top" @onConfirm="handleInvalid(scope.row)" >
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
import AddComp from './sub/CouponAdd.vue';
import EditComp from './sub/CouponEdit.vue';
export default {
  data() {
    return {
      loading: false,
      numData: [],
      tableData: [],
      searchKey: 'title',
      selectOptions: [
        {
          name: '名称',
          key: 'title',
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
      invalid: '',
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
      let params = {
        pkey: row.pkey,
      };
      axios
        .post(api.marketing.invalidCoupon, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success('卡券失效成功');
          this.getData();
        });
    },
    /**领券中心二维码下载 */
    handelDownCode() {
      location.href = api.marketing.downCouponCenter;
      //  axios.get(api.marketing.downCouponCenter, {})
      //   .then(response => {

      //   });
    },
    /** 加入或移除领券中心事件*/
    handleCardcenter(e) {
      console.log('领券中心', e);
      let url = '',
        text = '',
        params = {};
      if (e.cardType == 'CARD_CENTER') {
        url = api.marketing.updCoupon;
        text = '移除领券中心';
        params = e;
        params.cardType = 'MANUALLY_ISSUE';
      } else {
        url = api.marketing.insCardcenter;
        text = '加入领券中心';
        params.pkey = e.pkey;
        params = qs.stringify(params);
      }
      axios
        .post(url, params, {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          console.log(response);
          this.$message.success(text + '成功');
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
    handleStatuschange(row, index) {
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
        this.$message.error('已停用，无法再启用');
        this.tableData[index].enabled = false;
      } else {
        url = api.marketing.stopCoupon;
        text = '停用';
        this.$confirm(`停用后，该优惠券将不能再次启用，确认停用？`, '提示', {
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
              .then((response) => {
                this.$message.success(text + '成功');
                // this.getData();
              });
          })
          .catch(() => {
            this.tableData[index].enabled = true;
          });
      }
    },

    /**
     * 生成二维码
     */
    handleImg: function (row) {
      location.href = api.marketing.downCoupon + '?pkey=' + row.pkey;
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
        invalid: this.invalid,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.marketing.queryCoupon, qs.stringify(params), {
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