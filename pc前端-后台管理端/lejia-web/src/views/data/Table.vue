<!-- 
@name: Table.vue 
@description: 普通报表模板页面
@author: crj
@url: /data/table/:pkey
@date: 2020/08/13
-->

<template lang="html">
  <div class="table-container">
    <h1 class="title">{{ title }}</h1>
    <!-- 搜索栏 -->
    <div class="search-box" v-if="Object.keys(searchConfig).length">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange" v-if="searchConfig.datePicker">
        </el-date-picker>
        <el-select v-model="company" @change="handleCompanyChange($event)" placeholder="选择公司"
          v-if="searchConfig.company" value-key="pkey" clearable>
          <el-option value="" key="ALL" label="全部"></el-option>
          <el-option :value="item" :key="item.pkey" :label="item.name" v-for="(item, index) in companyList"></el-option>
        </el-select>
        <!-- <el-select v-model="params.companyPkey" @change="getData" placeholder="选择公司" v-if="searchConfig.company">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in companyList">
          </el-option>
        </el-select> -->
        <el-select v-model="params.marketPkey" @change="handleChange" placeholder="选择市场" v-if="searchConfig.market"
          clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in marketList"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"
          v-if="searchConfig.searchBar">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button v-loading="downLoading" type="primary" icon="el-icon-edit" size="medium" @click="handelExport">导出</el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%" class="table-fixed">
        <template v-for="(item,index) in  tableContentData">
          <el-table-column :key="index" :label="item.label" :prop="item.propName" :sortable="item.sortable"
            v-if="item.propName!='index'"></el-table-column>
          <el-table-column :label="item.label" v-if="item.propName=='index'">
            <template slot-scope="scope">
              {{scope.$index+1}}
            </template>
          </el-table-column>
        </template>
        <!-- <el-table-column
          v-for="(item,index) in  tableContentData"
          :key="index"
          :label="item.label"
          :prop="item.propName"
          :sortable="item.sortable"
          v-if="item.propName!='index'"
        ></el-table-column> -->
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>

<script>
import qs from 'qs';
import dropdown from '@/assets/js/dropdown';

export default {
  data() {
    return {
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      params: {}, //接口参数
      url: '', //接口路径
      tableContentData: [], //表格显示内容
      loading: false,
      date: '',
      companyList: [], //公司列表
      marketList: [], //市场列表
      searchConfig: {
        //搜索栏的配置
        company: true, //公司下拉列表
        market: true, //市场下拉列表
        searchBar: false, //搜索
        datePicker: true, //日期区间
      },
      exportUrl: '',
      selectOptions: [
        {
          name: '商户名',
          key: 'vendorName',
        },
      ],
      company: '', //选中的公司对象
      downLoading: false,
    };
  },
  mounted() {
    let query = this.judgement();
    // if (this.roleType == "lj_company_head" || roleType == "lj_market_head") {
    if (query.searchConfig.market) {
      if (query.ismerketsale) {
        axios.post(api.dropdown.newMarketList).then((res) => {
          this.marketList = res;
        });
      } else {
        dropdown.getMarket().then((result) => {
          this.marketList = result.content;
        });
      }
    }
    // if (this.roleType == "lj_company_head") {
    if (query.searchConfig.company) {
      dropdown.getCompany().then((result) => {
        this.companyList = result.content;
      });
    }

    this.getData({
      url: query.url,
      params: query.params,
    });
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
    roleType() {
      return this.$store.state.userIdentity;
    },
  },
  methods: {
    /**
     * @desc 导出
     */
    handelExport() {
      const params = {
          ...this.params,
        },
        url = this.exportUrl;
      params.endTime = this.date ? this.date[1] : '';
      params.startTime = this.date ? this.date[0] : '';
      let that = this;
      this.downLoading = true;
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
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
              that.downLoading = false;
              that.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: 'application/vnd.ms-excel',
              });
              if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                window.navigator.msSaveOrOpenBlob(blob, `${that.title}.xlsx`);
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute('download', `${that.title}.xlsx`);
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
              that.downLoading = false;
              that.$message.success('导出成功');
            }
          });
          reader.readAsText(data);
        });
    },
    /**
     * 公司下拉框发生改变
     */
    handleCompanyChange(e) {
      // console.log(e)
      this.params.companyPkey = e.pkey;
      this.params.marketPkey = '';
      if (e != '') {
        this.marketList = e.markets;
      } else {
        dropdown.getMarket().then((result) => {
          this.marketList = result.content;
        });
      }
      this.getData();
    },
    /**
     * 开始搜索
     */
    startSearch: function ({ key, keywords }) {
      this.params[key] = keywords;
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

    handleChange: function () {
      this.page = 1;

      this.getData();
    },
    /**
     * 获取列表
     */
    getData: function (query = '') {
      this.loading = true;
      let url, params;

      query && typeof query == 'object'
        ? (url = query.url) &&
          (params = {
            ...query.params,
            page: this.page - 1,
            pagesize: this.pageSize,
          })
        : (url = this.url) &&
          (params = {
            ...this.params,
            page: this.page - 1,
            pagesize: this.pageSize,
          }) &&
          (params.endTime = this.date ? this.date[1] : '') &&
          (params.startTime = this.date ? this.date[0] : '');
      console.log(url, params);
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          response.hasOwnProperty('content')
            ? (this.tableData = response.content) &&
              (this.total = response.total)
            : (this.tableData = response);

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    /**
     * 路由判断调用接口以及表格显示内容
     */
    judgement: function () {
      const pkey = this.$route.params.pkey,
        roleType = this.roleType;
      let url,
        params,
        searchConfig = {
          company: roleType == 1 ? true : false,
          market: roleType == 1 || roleType == 3 ? true : false,
          datePicker: true,
        },
        ismerketsale = false,
        exportUrl = '';
      switch (pkey) {
        //赠品统计
        case 'GIFTS':
          url = api.data.getGifts;
          params = {};
          searchConfig = {};
          this.tableContentData = [
            {
              propName: 'name',
              label: '奖品名称',
            },
            {
              propName: 'type',
              label: '奖品类型',
            },
            {
              propName: 'num',
              label: '累积中奖次数',
            },
          ];
          exportUrl = api.data.exportGifts;
          break;
        //付费会员消费统计报表
        case 'MEMBERCONSUME':
          url = api.data.consumeList;
          params = {
            endTime: '',
            startTime: '',
          };
          searchConfig.company = false;
          searchConfig.market = false;
          this.tableContentData = [
            {
              propName: 'name',
              label: '单品',
            },
            {
              propName: 'Sales',
              label: '销售额',
            },
            {
              propName: 'SalesNum',
              label: '销售笔数',
            },
          ];
          exportUrl = api.data.exportConsumption;
          break;
        //积分兑换统计报表
        case 'INTEGRALCHANGE':
          url = api.data.integralList;
          params = {
            endTime: '',
            startTime: '',
          };
          searchConfig.company = false;
          searchConfig.market = false;
          this.tableContentData = [
            {
              propName: 'name',
              label: '商品',
            },
            {
              propName: 'SalesNum',
              label: '兑换次数',
            },
            {
              propName: 'Sales',
              label: '销售金额',
            },
            {
              propName: 'pointn',
              label: '销售积分',
            },
          ];
          exportUrl = api.data.exportIntegral;
          break;
        //积分商户销售额统计报表
        case 'INTEGRALSALE':
          url = api.data.pointSale;
          params = {
            endTime: '',
            vendorName: '',
            startTime: '',
          };
          searchConfig.searchBar = true;
          searchConfig.company = false;
          searchConfig.market = false;
          this.tableContentData = [
            {
              propName: 'index',
              label: '编号',
            },
            {
              propName: 'name',
              label: '商户名',
            },
            {
              propName: 'mobile',
              label: '手机号',
            },
            {
              propName: 'pointSum',
              label: '积分收入合计',
            },
          ];
          exportUrl = api.data.exportPointSale;
          break;
        //商品销售统计表
        case 'GOODSSALE':
          url = api.data.goodsList;
          params = {
            companyPkey: '',
            endTime: '',
            marketPkey: '',
            startTime: '',
          };
          this.tableContentData = [
            {
              propName: 'name',
              label: '单品',
            },
            {
              propName: 'Sales',
              label: '销售额',
              sortable: true,
            },
            {
              propName: 'SalesNum',
              label: '销售笔数',
            },
          ];
          exportUrl = api.data.exportGoods;
          break;
        //优惠券使用统计报表
        case 'COUPONUSE':
          url = api.data.couponsUse;
          params = {
            companyPkey: '',
            endTime: '',
            marketPkey: '',
            startTime: '',
          };
          this.tableContentData = [
            {
              propName: 'name',
              label: '市场名称',
            },
            {
              propName: 'num',
              label: '使用优惠券数量',
            },
            {
              propName: 'cardPrice',
              label: '合计优惠金额',
            },
          ];
          exportUrl = api.data.exportCoupon;
          break;
        //菜品销售统计报表
        case 'COOKFDSALE':
          url = api.data.goodTypeSale;
          params = {
            companyPkey: '',
            endTime: '',
            marketPkey: '',
            startTime: '',
          };
          this.tableContentData = [
            {
              propName: 'name',
              label: '分类',
            },
            {
              propName: 'Sales',
              label: '销售额',
            },
            {
              propName: 'SalesNum',
              label: '销售笔数',
            },
          ];
          exportUrl = api.data.exportTypeSale;
          break;
        //市场销售统计
        case 'MARKETSALE':
          url = api.data.farmerSale;
          ismerketsale = true;
          params = {
            companyPkey: '',
            endTime: '',
            marketPkey: '',
            startTime: '',
          };
          this.tableContentData = [
            {
              propName: 'farmerName',
              label: '市场',
            },
            {
              propName: 'companyName',
              label: '所属公司',
            },
            {
              propName: 'Sales',
              label: '销售额',
              sortable: true,
            },
            {
              propName: 'SalesNum',
              label: '销售笔数',
            },
          ];
          exportUrl = api.data.exportFarmerSale;

          break;
        //公司销售统计
        case 'COMPANYSALE':
          url = api.data.companySale;
          params = {
            companyPkey: '',
            endTime: '',
            startTime: '',
          };
          searchConfig = {
            company: true,
            datePicker: true,
          };
          this.tableContentData = [
            {
              propName: 'companyName',
              label: '公司',
            },
            {
              propName: 'Sales',
              label: '销售额',
              sortable: true,
            },
            {
              propName: 'SalesNum',
              label: '销售笔数',
            },
          ];
          exportUrl = api.data.exportCompanySale;
          break;
        //运费报表
        case 'FREIGHT':
          url = api.data.freightList;
          params = {
            endTime: '',
            startTime: '',
          };
          searchConfig = {
            datePicker: true,
          };
          this.tableContentData = [
            {
              propName: 'name',
              label: '物流公司',
            },
            {
              propName: 'count',
              label: '总订单数',
            },
            {
              propName: 'postageSum',
              label: '运费收入',
            },
          ];
          exportUrl = api.data.exportFreight;
          break;
        //配送员绩效
        case 'COURIER':
          url = api.data.queryCourier;
          params = {
            endTime: '',
            startTime: '',
          };
          searchConfig = {
            datePicker: true,
          };
          this.tableContentData = [
            {
              propName: 'index',
              label: 'ID',
            },
            {
              propName: 'name',
              label: '配送员',
            },
            {
              propName: 'orderNum',
              label: '总订单数',
            },
            {
              propName: 'successNum',
              label: '总送达数',
            },
          ];
          exportUrl = api.data.exportCourier;
          break;
        //佣金达人
        case 'TOPCOMMISSION':
          url = api.data.queryComms;
          params = {
            memberName: '',
          };
          searchConfig = {
            searchBar: true,
          };
          this.selectOptions = [
            {
              name: '会员名',
              key: 'memberName',
            },
          ];
          this.tableContentData = [
            {
              propName: 'name',
              label: '会员名',
            },
            {
              propName: 'goodsNum',
              label: '分享商品数量',
            },
            {
              propName: 'buyNum',
              label: '分享人购买数量',
            },
            {
              propName: 'comms',
              label: '佣金收入',
            },
          ];
          exportUrl = api.data.exportComms;
          break;
        //佣金收入明细
        case 'COMMISSIONEARN':
          url = api.data.commsDetail;
          params = {
            endTime: '',
            startTime: '',
          };
          searchConfig = {
            datePicker: true,
          };
          this.tableContentData = [
            {
              propName: 'index',
              label: '序号',
            },
            {
              propName: 'kcCode',
              label: '订单号',
            },
            {
              propName: 'buyTime',
              label: '购买时间',
            },
            {
              propName: 'buyMember',
              label: '购买人',
            },
            {
              propName: 'buyAmtn',
              label: '购买金额',
            },
            {
              propName: 'comms',
              label: '佣金',
            },
            {
              propName: 'tjr',
              label: '推荐人',
            },
            {
              propName: 'commsTime',
              label: '佣金发放时间',
            },
          ];
          exportUrl = api.data.exportCommsDetail;
          break;
        //各专区营业报表
        default:
          url = api.data.zoneList;
          params = {
            companyPkey: '',
            endTime: '',
            marketPkey: '',
            startTime: '',
          };
          this.tableContentData = [
            {
              propName: 'name',
              label: '专区名称',
            },
            {
              propName: 'Sales',
              label: '销售额',
              sortable: true,
            },
            {
              propName: 'SalesNum',
              label: '销售笔数',
            },
          ];
          exportUrl = api.data.exportZone;
          // if (type == "company")
          //   this.tableContentData[0].label = "优惠券名称公司";
          break;
      }
      this.url = url;
      this.params = params;
      this.searchConfig = searchConfig;
      this.exportUrl = exportUrl;
      return {
        url,
        params,
        searchConfig,
        ismerketsale,
      };
    },
  },
};
</script>

<style>
</style>