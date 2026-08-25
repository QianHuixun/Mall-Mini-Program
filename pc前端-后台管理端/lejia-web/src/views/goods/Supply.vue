<!-- 
@name: Supply.vue
@description: 商品供应库
@author: 池仁杰
@url: /good/supply
@date: 2021/10/08
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
      <span class="red-font" style="padding-left:20px;font-size:18px" v-if="$store.state.userIdentity==2&&isAdminConfig">注：运营端已开启统一配置</span>
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="searchData.farmer" @change="handleChange(true)" placeholder="请选择市场" v-if="$store.state.userIdentity==1">
          <el-option v-for="(item,index) in marketList" :value="item.pkey" :label="item.name" :key="index"></el-option>
        </el-select>
        <el-select v-model="searchData.enabled" @change="handleChange" placeholder="商品启用状态" clearable>
          <el-option :value="true" label="开启"></el-option>
          <el-option :value="false" label="关闭"></el-option>
        </el-select>
        <el-select v-model="searchData.gtype" @change="handleChange" placeholder="商品分类" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in gtypeList"></el-option>
        </el-select>
        <el-select v-model="searchData.mtype" @change="handleChange" placeholder="商品类型" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in mtypeList"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" size="medium" @click="handleConfig" :disabled="marketType==='VENDOR_SHOPPING_MALL'" v-if="$store.state.userIdentity==1">
          派单配置
        </el-button>
        <el-button type="primary" icon="el-icon-download" size="medium" @click="handleExport">
          导出
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd" :disabled="marketType==='VENDOR_SHOPPING_MALL'" v-if="!($store.state.userIdentity==2&&isAdminConfig)">
          新增
        </el-button>
        <el-button plain type="primary" icon="el-icon-delete" size="medium" @click="handleDelete(1)" :disabled="marketType==='VENDOR_SHOPPING_MALL'" v-if="!($store.state.userIdentity==2&&isAdminConfig)">
          删除
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="dataList" :loading="loading" border style="width: 100%" class="table-fixed" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="70">
        </el-table-column>
         <el-table-column label="类型" prop="serialNumber">
          <template slot-scope="scope">
            {{scope.row.mtypeName || '无'}}
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="title"></el-table-column>
        <el-table-column label="商品ID" prop="serialNumber">
          <template slot-scope="scope">
            {{scope.row.serialNumber || '无'}}
          </template>
        </el-table-column>
        <el-table-column label="所属分类" prop="gtypeName"></el-table-column>
        <el-table-column label="规格" prop="space">
          <template slot-scope="scope">
            <div class="table_list_li" v-for="item in scope.row.details">
              {{item.spaceName}}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="供应商" prop="viewCount" min-width="140">
          <template slot-scope="scope">
            <div class="table_list_li" v-for="(item,index) in scope.row.details" :key="index">
              {{item.vendorName}}
              <span class="red-font" v-if="!item.isExist">(商户不存在)</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column v-if="settlementMethod == 'PURCHASE_SETTLEMENT'" label="采购价（元）" prop="xsNum">
          <template slot-scope="scope">
            <div class="table_list_li" v-for="(item,index) in scope.row.details" :key="index">
              {{item.purchasingPrice}}
            </div>
          </template>
        </el-table-column>
        <el-table-column v-if="settlementMethod == 'COMMISSION_SETTLEMENT'" label="费率" prop="commissionRate">
          <template slot-scope="scope">
            <div class="table_list_li" v-for="(item,index) in scope.row.details" :key="index">
              {{item.commissionRate ? item.commissionRate + '%' :  '--'}}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="是否启用" prop="enabled" width="80">
          <template slot-scope="scope">
            <div class="table_list_li" v-for="(item,index) in scope.row.details" :key="index">
              <el-switch active-color="#13ce66" v-model="item.enabled" @change="handleStatus(item.enabled,item.pkey)" :disabled="$store.state.userIdentity==2&&isAdminConfig">
              </el-switch>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sort" width="80">
          <template slot-scope="scope">
            <div class="table_list_li" v-for="(item,index) in scope.row.details" :key="index">
              {{item.sort}}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" v-if="!($store.state.userIdentity==2&&isAdminConfig)">
          <template slot-scope="scope">
            <div class="table_list_li" v-for="(item,index) in scope.row.details" :key="index">
              <el-button type="text" size="mini" v-if="scope.row.vendorShopping" :disabled="marketType==='VENDOR_SHOPPING_MALL'" @click="handleEdit(scope.row)">修改</el-button>
              <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(0,item)">
                <el-button slot="reference" size="mini" type="danger" v-if="scope.row.vendorShopping" :disabled="marketType==='VENDOR_SHOPPING_MALL'">删除</el-button>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <!-- 组件 -->
    <add-comp ref="AddComp" @refresh="getData" :isAdminConfig="isAdminConfig"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData" :isAdminConfig="isAdminConfig"></edit-comp>
    <paidan-config ref="PaidanConfig" @refresh="getData"></paidan-config>
  </div>
</template>

<script>
import dropdown from '@/assets/js/dropdown';
import AddComp from './sub/supplySub/AddComp.vue';
import EditComp from './sub/supplySub/EditComp';
import PaidanConfig from './sub/supplySub/PaidanConfig.vue';

export default {
  data() {
    return {
      searchKey: 'title',
      selectOptions: [
        {
          name: '商品名称',
          key: 'goodsName',
        },
      ],
      searchData: {
        enabled: '',
        gtype: '',
        farmer: '',
        mtype: '',
      },
      mtypeList: [
        {
          name: '市场商品',
          pkey: 'MARKET_GOODS',
        },
        {
          name: '扶贫商品',
          pkey: 'POVERTY_ALLEVIATION_GOODS',
        },
        {
          name: '特价商品',
          pkey: 'SPECIAL_GOODS',
        },
        {
          name: '分享商品',
          pkey: 'SHARE_GOODS',
        },
        {
          name: '砍价商品',
          pkey: 'CUT_GOODS',
        },
        {
          name: '团购商品',
          pkey: 'COLLAGE_GOODS',
        },
        {
          name: '预售商品',
          pkey: 'PRESALE_GOODS',
        },
      ],
      dataList: [],
      gtypeList: [],
      pageSize: 10,
      page: 1,
      total: 0,
      loading: false,
      multipleSelection: [],
      marketList: [],
      isAdminConfig: false,
      marketType: this.$store.state.marketType,
    };
  },
  components: {
    AddComp,
    EditComp,
    PaidanConfig,
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
    settlementMethod() {
      return this.$store.state.settlementMethod
    }
  },

  mounted() {
    if (this.$store.state.userIdentity == 1) this.getMarketData();
    else {
      this.getData();
      this.getConfig();
    }
    dropdown.getType().then((result) => {
      this.gtypeList = result.content;
    });
  },
  methods: {
    getConfig() {
      axios.post(api.goods.queryAdminSupply).then((response) => {
        if (response.hasOwnProperty('result')) {
          this.isAdminConfig = response.result;
        } else {
          this.isAdminConfig = response;
        }
      });
    },
    /**
     * @desc 产生一个数值，值是min到max之间的数
     * @param Min 最大限制
     * @param Max 最小限制
     */
    GetRandomNum(Min, Max) {
      var num = new Array();
      var cha = Max - Min;
      //产生随机数 0到count不包含count的随机数
      var randomNub = Min + Math.floor(Math.random() * cha);
      num = randomNub;
      return num;
    },
    /**
     * @desc 获取市场下拉列表
     */
    getMarketData() {
      axios.post(api.dropdown.newMarketList).then((res) => {
        this.marketList = res;
        console.log(localStorage.getItem('supplyMarket'));
        if (localStorage.getItem('supplyMarket')) {
          this.searchData.farmer = localStorage.getItem('supplyMarket');
        } else {
          this.searchData.farmer =
            this.marketList[this.GetRandomNum(0, res.length)].pkey;
          localStorage.setItem('supplyMarket', this.searchData.farmer);
        }
        this.getData();
      });
    },
    /**
     * @desc 显示派单配置弹窗
     */
    handleConfig() {
      this.$refs.PaidanConfig.show();
    },
    /**
     * @desc 启停状态
     * @param  {Boolean} status 新状态值
     * @param  {String} pkey   记录的pkey
     */
    handleStatus(status, pkey) {
      let text = '',
        params = {
          pkey: pkey,
        };
      if (status) {
        text = '启用';
      } else {
        text = '关闭';
      }
      axios
        .post(api.goods.changeSupplyStatus, this.$qs.stringify(params))
        .then((response) => {
          this.$message.success(text + '成功');
          this.getData();
        })
        .catch((error) => {
          this.getData();
        });
    },
    // 表格选中项
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    /**
     * @desc 获取列表数据
     */
    getData() {
      let params = {
        page: parseInt(this.page, 10) - 1,
        pagesize: this.pageSize,
        enabled: this.searchData.enabled,
        gtype: this.searchData.gtype,
        farmer: this.searchData.farmer,
        MType: this.searchData.mtype,
      };
      params[this.searchKey] = this.keywords;
      this.loading = true;
      axios
        .post(api.goods.querySupply, this.$qs.stringify(params))
        .then((res) => {
          this.loading = false;
          this.dataList = [...res.content];
          this.total = res.total ? (res.total > 10000 ? 10000 : res.total) : 0;
        })
        .catch((err) => {
          this.loading = false;
        });
    },
    /**
     * @desc 删除商品
     * @param {Number} type 是否为批量删除
     * @param {Object} row 单个删除的数据
     */
    handleDelete(type, row) {
      let params = {},
        message = '',
        url = api.goods.delSupplySpecs;
      if (type) {
        if (!this.multipleSelection.length) {
          this.$message.warning('请选择要删除的数据');
          return;
        }
        message = '确定删除这些数据吗';
        params.goodPkeys = this.multipleSelection
          .map((item) => {
            return item.goodsPkey;
          })
          .join(',');
        url = api.goods.delSupplyGoods;
        this.$confirm(message, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          axios.post(url, this.$qs.stringify(params)).then((res) => {
            this.$message.success('删除成功');
            this.getData();
          });
        });
      } else {
        params.pkeys = row.pkey;
        message = '确定删除这条数据吗';
        axios.post(url, this.$qs.stringify(params)).then((res) => {
          this.$message.success('删除成功');
          this.getData();
        });
      }
    },
    /**
     * @desc 筛选
     * @param  {Boolean} isMarket 是否是市场选择发生变化
     */
    handleChange(isMarket) {
      if (isMarket)
        localStorage.setItem('supplyMarket', this.searchData.farmer);
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
     * @desc 导出
     */
    handleExport() {
      let params = {
        enabled: this.searchData.enabled,
        gtype: this.searchData.gtype,
        farmer: this.searchData.farmer,
        MType: this.searchData.mtype,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.goods.exportSupply, this.$qs.stringify(params), {
          responseType: 'blob',
          timeout: 0,
        })
        .then((res) => {
          let data = new Blob([res.data], {
            type: 'application/json',
          });
          var reader = new FileReader();
          reader.addEventListener('loadend', function (e) {
            if (e.target.result.indexOf('result') > 0) {
              let result = JSON.parse(e.target.result);
              that.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: 'application/vnd.ms-excel',
              });
              if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                window.navigator.msSaveOrOpenBlob(blob, '商品供应库清单.xlsx');
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute('download', '商品供应库清单.xlsx');
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
              that.$message.success('导出成功');
            }
          });
          reader.readAsText(data);
        });
    },
    /**
     * @desc 新增
     */
    handelAdd() {
      this.$refs.AddComp.show();
    },
    /**
     *@desc 修改
     */
    handleEdit(row) {
      this.$refs.EditComp.show({
        row: row,
        market: this.searchData.farmer,
      });
    },
  },
};
</script>

<style lang="less" scoped>
.table-container > .search-box > .search-box-form > .el-select {
  width: 160px !important;
}

.red-font {
  color: #f56c6c;
}
/deep/ .el-table__fixed-right {
  height: 100% !important;
}
</style>