  <!-- 
  @name: Merchant.vue 
  @description: 积分商户管理 市场商户管理 商户管理
  @author: crj
  @url: /vendor/merchant or /vendor/marketMerchant
  @date: 2021/10/18
  -->
<template lang="html">
    <div class="table-container">
      <h1 class="title">
        {{ title }}
        <span class="red-font" style="padding-left:20px;font-size:18px"
          v-if="$store.state.userIdentity==2&&flag">注：运营端已开启统一配置</span>
        <el-radio-group v-model="flag" v-if="$store.state.userIdentity==1&&$route.path=='/vendor/marketMerchant'"
          @change="configChange">
          <el-radio-button :label="true">统一配置</el-radio-button>
          <el-radio-button :label="false">市场自定义</el-radio-button>
        </el-radio-group>
      </h1>
      <!-- 搜索栏 -->
      <div class="search-box">
        <!-- 搜索表单 -->
        <div class="search-box-form">
          <el-select v-model="searchData.marketPkeys" @change="handleChange" placeholder="请选择市场"
            v-if="($store.state.userIdentity==1&&$route.path=='/vendor/marketMerchant')" multiple collapse-tags
            filterable>
            <el-option v-for="(item,index) in marketList" :value="item.pkey" :label="item.name" :key="index">
            </el-option>
          </el-select>
          <el-select v-model="searchData.scopes" @change="handleChange" placeholder="请选择经营范围"
            v-if="($store.state.userIdentity==1&&$route.path=='/vendor/marketMerchant')||($store.state.userIdentity==2&&$route.path=='/vendor/merchant')"
            multiple collapse-tags filterable>
            <el-option v-for="(item,index) in gtypeList" :value="item.pkey" :label="item.name" :key="index"></el-option>
          </el-select>
          <el-select v-model="searchData.zxStatus" @change="handleChange" placeholder="请选择状态"
            v-if="($store.state.userIdentity==1&&$route.path=='/vendor/marketMerchant')||($store.state.userIdentity==2&&$route.path=='/vendor/merchant')"
          >
            <el-option  value="NOT_AUDIT" label="未审核" ></el-option>
            <el-option  value="AUDIT_SUCCESS" label="审核通过" ></el-option>
            <el-option  value="AUDIT_FAILURE" label="审核未通过" ></el-option>
          </el-select>
          <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
          </search-bar>
        </div>
        <!-- 操作按钮 -->
        <div class="search-box-button">
          <el-button type="primary" size="medium" @click="handleClerk"
            v-if="($store.state.userIdentity==1&&$route.path=='/vendor/marketMerchant')||($store.state.userIdentity==2&&$route.path=='/vendor/merchant')">
            市场店员管理
          </el-button>
          <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd"
            v-if="!isOnlyBrowse&&!($store.state.userIdentity==2&&flag)">
            新增商户
          </el-button>
          <!-- <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelDownloadAll"
            v-loading="downLoading">
            下载全部二维码
          </el-button> -->
          <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelDownloadAll"
            v-loading="downLoading">
            商户二维码
          </el-button>
        </div>
      </div>
      <!-- 表格框 -->
      <div class="table-box">
        <el-table :data="tableData" :loading="loading" border style="width: 100%" class="table-fixed">
          <el-table-column v-if="$store.state.userIdentity==1&&$route.path=='/vendor/marketMerchant'" label="市场"
            prop="farmerName" min-width="120"></el-table-column>
          <el-table-column label="商户名称" prop="name" min-width="80" align="center"></el-table-column>
          <el-table-column label="展示名称" prop="displayName" min-width="80" align="center"></el-table-column>
          <el-table-column label="联系方式" prop="mobile" width="110"  align="center"></el-table-column>
          <el-table-column label="摊位号" prop="booth" width="110"  align="center"></el-table-column>
          <!-- <el-table-column v-if="!($store.state.userIdentity==1&&$route.path=='/vendor/merchant')" label="状态" prop="zxStatusName" width="80"  align="center">
            <template slot-scope="scope">
            <span :class="scope.row.zxStatus=='AUDIT_SUCCESS'?'green-font':'red-font'"> {{scope.row.zxStatusName || '--'}} </span>
            </template>
          </el-table-column> -->
          <el-table-column v-if="!($store.state.userIdentity==1&&$route.path=='/vendor/merchant')" label="经营范围"
            prop="businessScope" min-width="150"  align="center">
            <template slot-scope="scope">
              {{scope.row.businessScope || '--'}}
            </template>
          </el-table-column>
          <el-table-column v-if="($store.state.userIdentity==1&&$route.path=='/vendor/merchant')" label="商户地址"
            prop="addr" width="200"  align="center" >
          </el-table-column>
          <el-table-column v-if="!($store.state.userIdentity==1&&$route.path=='/vendor/merchant')" label="银行账户名称"
            prop="bankname" min-width="120"  align="center">
            <template slot-scope="scope">
              {{scope.row.bankname || '--'}}
            </template>
          </el-table-column>
          <el-table-column v-if="!($store.state.userIdentity==1&&$route.path=='/vendor/merchant')" label="银行卡号"
            prop="bankcard" min-width="120"  align="center">
            <template slot-scope="scope">
              {{scope.row.bankcard || '--'}}
            </template>
          </el-table-column>
          <el-table-column v-if="!($store.state.userIdentity==1&&$route.path=='/vendor/merchant')" label="开户人姓名"
            prop="bankuser" min-width="100"  align="center">
            <template slot-scope="scope">
              {{scope.row.bankuser || '--'}}
            </template>
          </el-table-column>
          <el-table-column v-if="!($store.state.userIdentity==1&&$route.path=='/vendor/merchant')" label="银行卡绑定手机"
            prop="bankuserMoblie" min-width="120"  align="center">
            <template slot-scope="scope">
              {{scope.row.bankuserMoblie || '--'}}
            </template>
          </el-table-column>
          <el-table-column v-if="!($store.state.userIdentity==1&&$route.path=='/vendor/merchant')" label="开户人身份证号"
            prop="zxIdentity" min-width="120"  align="center">
            <template slot-scope="scope">
              {{scope.row.zxIdentity || '--'}}
            </template>
          </el-table-column>
          <el-table-column label="累计收入积分" prop="points" min-width="120"  align="center"
            v-if="$store.state.userIdentity==1&&$route.path!='/vendor/marketMerchant'"></el-table-column>
          <el-table-column label="创建时间" prop="createdTime" width="150" align="center"></el-table-column>
          <el-table-column label="是否启用" prop="enabled"  align="center">
            <template slot-scope="scope">
              <el-switch active-color="#13ce66" v-model="scope.row.enabled" @change="handleStatus(scope.row.enabled,scope.row.pkey)"
               :disabled="isOnlyBrowse||($store.state.userIdentity==2&&flag)"></el-switch>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="300"  v-if="!isOnlyBrowse" fixed="right"  align="center">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="handleEdit(scope.row)" v-if="!($store.state.userIdentity==2&&flag)">
                编辑
              </el-button>
              <el-button type="text" size="small" @click="handleClerk(scope.row)"  v-if="($store.state.userIdentity==1&&$route.path=='/vendor/marketMerchant')||($store.state.userIdentity==2&&$route.path=='/vendor/merchant')">
                店员管理
              </el-button>
              <el-button type="text" size="small" @click="handleDetails(scope.row)">
                商户流水
              </el-button>
              <el-button type="text" size="small" @click="handleImg(scope.row)">
                生成二维码
              </el-button>
              <el-button type="text" size="small" @click="handleAccountInfo(scope.row)"  v-if="isTianJin">
              账户信息
            </el-button>
            <el-button type="text" size="small" @click="handleCardInfo(scope.row)" v-if="isTianJin">
              银行卡信息
            </el-button>
              <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)" v-if="!($store.state.userIdentity==2&&flag)">
                <el-button slot="reference" size="mini" type="danger">
                  删除
                </el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <!-- 页码 -->
        <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
          :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
      </div>
      <!-- 组件 -->
      <ponit-add-comp ref="PonitAddComp" @refresh="getData"></ponit-add-comp>
      <ponit-edit-comp ref="PonitEditComp" @refresh="getData"></ponit-edit-comp>
      <merchant-clerk-comp ref="MerchantClerkComp" ></merchant-clerk-comp>


    </div>
</template>
<script>
import qs from 'qs';

export default {
  data() {
    return {
      isTianJin: localStorage.getItem("ascription") == (process.env.VUE_APP_TITLE =='production' ? 13 : 22) ? true : false,
      loading: false,
      searchKey: 'name',
      selectOptions: [
        {
          name: '商户名',
          key: 'name',
        },
        {
          name: '展示名称',
          key: 'displayName',
        },
        {
          name: '手机号码',
          key: 'mobile',
        },
      ],
      marketList: [],
      gtypeList: [],
      searchData: {
        scopes: [],
        marketPkeys: [],
        zxStatus: '',
      },
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      downLoading: false, //下载控量
      flag: false,
    };
  },
  components: {
    PonitAddComp(resolve) {
      require(['./sub/ponitMerchant/MerchantAdd.vue'], resolve);
    },
    PonitEditComp(resolve) {
      require(['./sub/ponitMerchant/MerchantEdit.vue'], resolve);
    },
    MerchantClerkComp(resolve) {
      require(['./sub/merchant/merchantClerk/MerchantClerk.vue'], resolve);
    },
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
  mounted() {
    if (
      this.$store.state.userIdentity == 1 &&
      this.$route.path == '/vendor/marketMerchant'
    ) {
      this.getMarketData();
    }
    this.getGtypeData();
    this.getConfig();
    this.getData();
  },
  methods: {
    /**
     * @desc 市场店员管理
     */
    handleClerk(row = '') {
      if (row) {
        this.$refs.MerchantClerkComp.show(row.pkey);
      } else this.$refs.MerchantClerkComp.show();
    },
    /**
     * @desc
     */
    configChange(e) {
      let params = {
        pkey: 'vendor_manager_deploy',
        flag: this.flag,
      };
      axios
        .post(api.common.updConfig, qs.stringify(params))
        .then(() => {
          this.$message.success('市场商户配置修改成功');
          this.getConfig();
        });
    },
    /**
     * @desc 获取市场配置
     */
    getConfig() {
      let params = {
        pkey: 'vendor_manager_deploy',
      };
      axios
        .post(api.common.queryConfig, qs.stringify(params))
        .then((response) => {
          if (response.hasOwnProperty('result')) {
            this.flag = response.result;
          } else {
            this.flag = response;
          }
        });
    },
    /**
     * @desc 获取市场下拉列表
     */
    getMarketData() {
      axios.post(api.dropdown.newMarketList).then((res) => {
        this.marketList = res;
      });
    },
    /**
     * @desc 获取经营范围列表
     */
    getGtypeData() {
      axios.post(api.market.queryGtype).then((response) => {
        this.gtypeList = response;
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
     * @desc 筛选
     */
    handleChange() {
      this.page = 1;
      this.getData();
    },
    /**
     * 下载全部二维码
     */
    handelDownloadAll: function () {
      let params = {
        flag:
          this.$store.state.userIdentity == 1 &&
          this.$route.path == '/vendor/marketMerchant'
            ? true
            : false,
      };
      const {ascription, marketPkey} = this.$store.state
      location.href= `${api.market.downMerchantAllCode}?flag=${params.flag}&ascription=${ascription}&marketPkey=${marketPkey}`
      // axios
      //   .post(api.market.downMerchantAllCode, this.$qs.stringify(params), {
      //     headers: {
      //       Authorization: this.$store.state.token,
      //     },
      //     responseType: 'blob',
      //     timeout: 0,
      //   })
      //   .then((res) => {
      //     let data = new Blob([res.data], {
      //       type: 'application/json',
      //     });
      //     var reader = new FileReader();
      //     reader.addEventListener('loadend', function (e) {
      //       if (e.target.result.indexOf('result') > 0) {
      //         let result = JSON.parse(e.target.result);
      //         that.downLoading = false;
      //         that.$message.error(result.codeMsg);
      //         return;
      //       } else {
      //         let blob = new Blob([res.data], {
      //           type: 'application/x-zip-compressed',
      //         });
      //         if (!!window.ActiveXObject || 'ActiveXObject' in window) {
      //           window.navigator.msSaveOrOpenBlob(blob, '商户二维码.zip');
      //         } else {
      //           const link = document.createElement('a');
      //           link.style.display = 'none';
      //           link.href = URL.createObjectURL(blob);
      //           link.setAttribute('download', '商户二维码.zip');
      //           document.body.appendChild(link);
      //           link.click();
      //           document.body.removeChild(link);
      //         }
      //         that.downLoading = false;
      //         that.$message.success('下载成功');
      //       }
      //     });
      //     reader.readAsText(data);
      //   });
    },
    /**
     * 生成二维码
     */
    handleImg: function (row) {
      location.href = api.market.downMerchantCode + '?pkey=' + row.pkey;
      // const url = api.market.downMerchantCode + '?pkey=' + row.pkey;
      // await axios.get(url, {
      //   responseType: 'blob'
      // })
      //   .then(res => {
      //     console.log(res);
      //   })
    },
    /**
     * 商户流水
     */
    handleDetails: function (row) {
      let path;
      if (
        this.$store.state.userIdentity == 1 &&
        this.$route.path == '/vendor/merchant'
      )
        path = '/vendor/order';
      else path = '/vendor/bill';
      this.$router.push({
        path,
        query: {
          pkey: row.pkey,
        },
      });
    },
    /**
     * 点击新增公司
     */
    handelAdd: function () {
      if (this.title == '积分商城商户') {
        this.$refs.PonitAddComp.show();
      } else {
        this.$router.push({
          path: '/vendor/merchant/add',
        });
      }
    },
    /**
     * 点击修改公司
     */
    handleEdit: function (row) {
      if (this.title == '积分商城商户') {
        this.$refs.PonitEditComp.show({
          row: row,
        });
      } else {
        this.$router.push({
          path: '/vendor/merchant/edit',
          query: {
            pkey: row.pkey,
          },
        });
        localStorage.setItem('vendorMerchant', row.pkey);
      }
    },
    /**
     * 删除公司
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.market.delMerchant, qs.stringify(params), {
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
     * 启停状态
     * @param  {[type]} status [新状态值]
     * @param  {[type]} pkey   [记录的pkey]
     * @return {[type]}        [description]
     */
    handleStatus: function (status, pkey) {
      let url = '',
        text = '',
        params = {
          pkey: pkey,
        };
      if (status) {
        url = api.market.startMerchant;
        text = '启用';
      } else {
        url = api.market.stopMerchant;
        text = '停用';
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          if(response) {
            this.$message.success(text + '成功');
          }
          this.getData();
        })
        .catch(() => {
          this.getData();
        })
    },

    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        scopes: this.searchData.scopes.join(','),
        marketPkeys: this.searchData.marketPkeys.join(','),
        flag:
          this.$store.state.userIdentity == 1 &&
          this.$route.path == '/vendor/marketMerchant'
            ? 'market'
            : '',
        zxStatus: this.searchData.zxStatus,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.market.queryMerchant, qs.stringify(params), {
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
.table-container > .search-box > .search-box-form > .el-select {
  width: 200px !important;
}

.red-font {
  color: #f56c6c;
}

/deep/.el-select .el-input {
  height: auto !important;
}

.title {
  .el-radio-group {
    margin-left: 20px;

    /deep/.el-radio-button__orig-radio:checked + .el-radio-button__inner {
      color: #fff;
      background-color: #1abc9c;
      border-color: #1abc9c;
      box-shadow: -1px 0 0 0 #1abc9c;
    }

    /deep/.el-radio-button .el-radio-button__inner {
      padding: 6px 10px;
    }
  }
}
/deep/.el-table .el-table__fixed-right {
  height: 100% !important; //设置高优先，以覆盖内联样式
}
.green-font {
  color: #67c23a;
}
.red-font {
  color: #f56c6c;
}
</style>