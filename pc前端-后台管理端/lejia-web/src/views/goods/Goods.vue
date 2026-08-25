<!--
@name: Goods.vue
@description: 商品维护
@author: sx
@url: /goods/goods
@date: 2020/06/30
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
        <el-select v-model="enabled" @change="handleChange" placeholder="上下架" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in enabledList">
          </el-option>
        </el-select>
        <el-select v-model="status" @change="handleChange" placeholder="发布状态" clearable v-if="mType != 'COUPON_GOODS' && mType != 'INTEGRAL_GOODS' && mType != 'INTEGRAL_BNYP_GOODS' && mType != 'INTEGRAL_MSD_GOODS'" >
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList"></el-option>
        </el-select>
        <!-- <el-select v-model="gType" @change="handleGTypeChange" placeholder="商品分类"  v-if="mType != 'COUPON_GOODS'" filterable clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in typeList"></el-option>
        </el-select>
        <el-select v-model="goodsMain" @change="handleChange" placeholder="商品小类"  v-if="mType != 'COUPON_GOODS'" filterable clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in goodsList"></el-option>
        </el-select> -->
        <el-cascader v-model="category" :options="categoryList" :props="props" clearable @change="handleCategoryChange" placeholder="商品类型"></el-cascader>
        <el-select v-model="vendor" @change="handleChange" placeholder="商户"  filterable clearable
          v-if="mType != 'COUPON_GOODS' && mType != 'INTEGRAL_GOODS' && mType != 'INTEGRAL_BNYP_GOODS' && mType != 'INTEGRAL_MSD_GOODS' && mType != 'INTEGRAL_PRESALE_GOODS' && mType != 'GIFT_GOODS'">
          <el-option :value="item.vendor" :key="index" :label="item.vendorName" v-for="(item, index) in vendorList"></el-option>
        </el-select>
        <el-select v-model="supplier" @change="handleChange" placeholder="供应商"  filterable clearable v-if="mType.includes('INTEGRAL')">
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in supplierList"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <!-- <el-button type="primary"  size="medium" @click="handelGoodlike">
          市场推荐管理
        </el-button> -->
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelModelEdit">
          模板编辑
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelEnabled(1)">
          批量上架
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelEnabled()">
          批量下架
        </el-button>
        <el-button type="primary" icon="el-icon-delete" size="medium" @click="handleBatchDelete()">
          批量删除
        </el-button>
        <el-upload class="upload-demo" action="" :show-file-list="false" :http-request="handleImport"
          accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
          <el-button type="primary" size="medium" icon="el-icon-upload2" :loading="importLoading">
            导入
          </el-button>
        </el-upload>
        <el-button type="primary" icon="el-icon-download" size="medium" @click="handleImportExcel"
          :loading="downLoading" >
          导出
        </el-button> 
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增
        </el-button>
        <el-button type="primary" icon="el-icon-edit" size="medium" 
          v-if="mType == 'INTEGRAL_GOODS' || mType == 'INTEGRAL_BNYP_GOODS' || mType == 'INTEGRAL_MSD_GOODS' || mType == 'PRESALE_GOODS' || mType == 'SPECIAL_GOODS'"
          @click="handleChangeDisplayName">
          专区显示名称
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%" class="table-fixed" @selection-change="handleSelectionChange" @sort-change="handleSortChange" >
        <el-table-column  align="center" type="selection" key="1"> </el-table-column>
        <el-table-column label="商品名称" prop="title" key="2" min-width="120"></el-table-column>
        <el-table-column label="商品图片" prop="title" key="image" min-width="120">
          <template slot-scope="scope">
            <el-image v-if="scope.row.photo1" :src="scope.row.photo1[0]" style="width: 100px; height: 100px"
            :preview-src-list="scope.row.photo1"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="供应商" prop="supplierName" min-width="120"
          v-if="mType.includes('INTEGRAL')">
        </el-table-column>
        <el-table-column label="商户" prop="vendorName" min-width="120"
          v-if="marketType === 'VENDOR_SHOPPING_MALL' && (mType == 'MARKET_GOODS' || mType == 'SPECIAL_GOODS')">
        </el-table-column>
        <el-table-column label="摊位号" prop="booth" min-width="120" v-if="!mType.includes('INTEGRAL')">
        </el-table-column>
        <el-table-column label="每日限购" prop="purchaseNum" v-if="mType != 'COLLAGE_GOODS'&&mType != 'CUT_GOODS'" key="3">
        </el-table-column>
        <template  v-if="mType != 'COUPON_GOODS'">  
          <el-table-column label="商品ID" prop="serialNumber"  key="4" v-if="mType != 'INTEGRAL_GOODS' && mType != 'INTEGRAL_BNYP_GOODS' && mType != 'INTEGRAL_MSD_GOODS'"></el-table-column>
          <el-table-column label="已推荐商品" prop="recommendNum" width="95"></el-table-column>
          <el-table-column label="所属分类" prop="gtypeName"  key="5"></el-table-column>
          <el-table-column label="描述" prop="description"  key="6" v-if="mType != 'INTEGRAL_GOODS' && mType != 'INTEGRAL_BNYP_GOODS' && mType != 'INTEGRAL_MSD_GOODS'"></el-table-column>
          <el-table-column label="浏览量" prop="viewCount"  key="7" sortable="custom" width="90" ></el-table-column>
          <el-table-column label="销量" prop="xsNum" key="8" sortable="custom"></el-table-column>
        </template>
        <el-table-column label="上架时间" v-if="mType == 'COUPON_GOODS'" prop="startDate" sortable="custom"  key="9" min-width="170">
          <template slot-scope="scope">
          <span>{{scope.row.startDate+'~'+scope.row.endDate}}</span>
          </template>
        </el-table-column>
        <el-table-column label="规格" width="480"  key="10" v-if="mType != 'COUPON_GOODS'">
          <el-table-column label="规格" width="80">
            <template slot-scope="scope">
              <div class="table_list_li" v-for="(item,index) in scope.row.spaces" :key="index">
                {{item.space}}
              </div>
            </template>
          </el-table-column>
          <!-- <el-table-column label="原价" width="80" v-if="mType != 'MARKET_GOODS'"> -->
            <el-table-column label="重量(kg)" prop="weight" width="100" sortable="custom">
            <template slot-scope="scope">
              <div class="table_list_li" v-for="(item,index) in scope.row.spaces" :key="index">{{item.weight}}</div>
            </template>
          </el-table-column>
          <el-table-column label="原价" prop="priceOld" width="80" sortable="custom">
            <template slot-scope="scope">
              <div class="table_list_li" v-for="(item,index) in scope.row.spaces" :key="index">{{"￥" + item.priceOld}}</div>
            </template>
          </el-table-column>
          <el-table-column :label="$route.params.pkey=='CUT_GOODS'? '底价' : '现价'" prop="price" sortable="custom" width="80">
            <template slot-scope="scope">
              <div class="table_list_li" v-for="(item,index) in scope.row.spaces" :key="index">{{"￥" + item.price }}</div>
            </template>
          </el-table-column>
          <!-- <el-table-column label="会员价" prop="priceMember" sortable="custom" width="90" v-if="mType == 'MARKET_GOODS'">
            <template slot-scope="scope">
              <div class="table_list_li" v-for="item in scope.row.spaces">
                {{ item.priceMember ? '￥'+item.priceMember : '无' }}</div>
            </template>
          </el-table-column> -->
          <el-table-column label="支付积分" width="80" v-if="mType == 'INTEGRAL_GOODS' || mType == 'INTEGRAL_BNYP_GOODS'">
            <template slot-scope="scope">
              <div class="table_list_li" v-for="(item,index) in scope.row.spaces" :key="index">{{item.point}}</div>
            </template>
          </el-table-column>
          <el-table-column label="佣金" prop="comm" sortable="custom" width="80" v-if="mType == 'SHARE_GOODS'">
            <template slot-scope="scope">
              <div class="table_list_li" v-for="(item,index) in scope.row.spaces" :key="index">{{"￥" + item.comm}}</div>
            </template>
          </el-table-column>
          <el-table-column label="库存" prop="kcNum" sortable="custom" width="80">
            <template slot-scope="scope">
              <div class="table_list_li" v-for="(item,index) in scope.row.spaces" :key="index">{{item.kcNum }}</div>
            </template>
          </el-table-column>
        </el-table-column>
        <!-- 仅优惠券商品显示 -->
        <template  v-if="mType == 'COUPON_GOODS'">  
          <el-table-column label="金额" prop="price" sortable="custom"  key="11"></el-table-column>
          <el-table-column label="积分" prop="point" sortable="custom"  width="80"  key="12">
          </el-table-column>
          <el-table-column label="库存" prop="kcNum" sortable="custom"  width="80" key="13"></el-table-column>
          <el-table-column label="面值金额" prop="cost" sortable="custom" width="110" key="14"></el-table-column>
          <el-table-column label="最小订单金额" prop="limitCost" width="120"   key="15"></el-table-column>
        </template>
        <!-- end 仅优惠券商品显示 -->
        <el-table-column v-if="mType == 'COUPON_GOODS'||mType == 'GIFT_GOODS'" key="16" min-width="160"
          :label="mType == 'COUPON_GOODS'?'限制市场':'适用市场'" prop="userFarmerName"   ></el-table-column>
        <el-table-column v-if="mType == 'GIFT_GOODS'" key="17" min-width="120"
          label="适用商户" prop="userVendorName" ></el-table-column>
        <el-table-column v-if="mType == 'GIFT_GOODS'" key="18" min-width="180"
          label="兑换有效期" prop="userVendor" sortable="custom">
            <template slot-scope="scope">
              <div>{{scope.row.giftStartDate?scope.row.giftStartDate+'~'+scope.row.giftEndDate:'永久有效'}}</div>
            </template>
        </el-table-column>
         <!-- 仅优惠券商品显示 -->
        <template  v-if="mType == 'COUPON_GOODS'">  
          <el-table-column label="限制品类" prop="userTypeName"  key="19" min-width="160"></el-table-column>
          <el-table-column label="限制商品" prop="userGoodsName"  key="20" min-width="160"></el-table-column>
        </template>
        <!-- end 仅优惠券商品显示 -->

        <el-table-column :label="mType == 'COUPON_GOODS'?'是否启用':'上下架'" prop="enabled"  key="21">
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.enabled"
              @change="handleStatus(scope.row.enabled,scope.row.pkey)" :disabled="mType == 'COUPON_GOODS'&&scope.row.invalid"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="轮播推荐" prod="zoneRecommend"
          v-if="mType.includes('INTEGRAL') || mType === 'SPECIAL_GOODS'"
        >
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.zoneRecommend" @change="handleZoneRecommendChange(scope.row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column v-if="marketType === 'MARKET_SHOPPING_MALL' && mType == 'MARKET_GOODS'" label="市场推荐" prop="enabled"  key="22"> 
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.guessLike"
              @change="handleGuessLikeChange(scope.row.guessLike,scope.row.pkey)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" :width="mType === 'MARKET_GOODS' || mType === 'SPECIAL_GOODS' || mType.includes('INTEGRAL') ? 240 : 160" fixed="right" key="23">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleKcEdit(scope.row)"  v-if="mType != 'COUPON_GOODS'">库存管理</el-button>
            <el-button type="text" size="small" @click="handleEdit(scope.row)" :disabled="mType == 'COUPON_GOODS'&&scope.row.invalid">编辑</el-button>
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
              <el-button slot="reference" size="mini" type="danger" :disabled="mType == 'COUPON_GOODS'&&scope.row.invalid">删除</el-button>
            </el-popconfirm>
            <el-popconfirm title="确定失效吗？" placement="top" @onConfirm="handleInvalid(scope.row)" v-if="mType == 'COUPON_GOODS'">
              <el-button slot="reference" size="mini" type="danger">失效</el-button>
            </el-popconfirm>
            <el-button type="text" size="small" 
              v-if="mType === 'MARKET_GOODS' || mType === 'SPECIAL_GOODS' || mType.includes('INTEGRAL')"
               @click="handleGoRecommend(scope.row)">推荐商品管理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange" @size-change="handleSizeChange"></el-pagination>
    </div>
    <!-- 组件 -->
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
    <kc-edit ref="KcEditComp" @refresh="getData"></kc-edit>
    <progress-dlog ref="ProgressDlog" :title="'导入市场商品清单'" @refresh="getData" :uploadPercent.sync="uploadPercent">
    </progress-dlog>
    <model-edit ref="modelEdit"></model-edit>
    <display-name ref="displayName" :mType="mType"></display-name>
    <!-- <goods-like ref="goodsLike"></goods-like> -->

  </div>
</template>
<script>
import qs from 'qs';
import AddComp from './sub/GoodsAdd.vue';
import EditComp from './sub/GoodsEdit.vue';
import KcEdit from './sub/GoodsKc';
import dropdown from '@/assets/js/dropdown';
import ProgressDlog from '@/components/global/ProgressDlog';
import ModelEdit from './sub/ModelEdit.vue';
import DisplayName from './sub/DisplayName.vue';
// import GoodsLike from './sub/GoodsLike.vue';

// INTEGRAL_GOODS(0, "积分"),
// MARKET_GOODS(1, "市场"),
// MEMBER_GOODS(2, "会员"),
// SPECIAL_GOODS(3, "特价"),
// SHARE_GOODS(4, "分享"),
// CUT_GOODS(5, "砍价"),
// COLLAGE_GOODS(6, "团购"),
// PRESALE_GOODS(7, "预售");
// POVERTY_ALLEVIATION_GOODS(8, "扶贫");
export default {
  data() {
    return {
      loading: false,
      mType: '',
      searchKey: 'title',
      selectOptions: [{
          name: '商品名称',
          key: 'title',
        }],
      statusList: [
        {
          pkey: '1',
          name: '发布状态',
        },
        {
          pkey: '2',
          name: '未开始',
        },
        {
          pkey: '3',
          name: '进行中',
        },
        {
          pkey: '4',
          name: '已结束',
        },
      ],
      enabledList: [
        {
          pkey: 'true',
          name: '上架',
        },
        {
          pkey: 'false',
          name: '已下架',
        },
      ],
      typeList: [],
      categoryList: [],
      goodsList: [],
      vendorList: [],
      supplierList:[],
      props: {
        value: 'pkey',
        label: 'name',
        children: 'threeGtypeList',
        checkStrictly: true
      }, //级联选择器配置
      status: '1',
      enabled: '',
      gType: '',  //一级类目
      goodsMain: '',  //二级类目
      threeGtype: '', //三级类目
      category: [],
      vendor: '',
      supplier:"",
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      downLoading: false, //导出加载
      importLoading: false, //导入加载
      uploadPercent: 0, //进度条进度控量
      multipleSelection: [],
      sortData: { // 排序
        sort: '',
        sortType: '',
      },
      sortTypeList: {
        viewCount: 'PAGEVIEWS_SORT',
        xsNum: 'SALES_SORT',
        priceOld: 'ORIGINAL_PRICE_SORT',
        price: 'CURRENT_PRICE_SORT',
        priceMember: 'MEMBER_SORT',
        kcNum: 'STOCK_SORT',
        comm: 'COMMISSION_SORT',
        point: 'INTEGRAL_SORT',
        userVendor: 'EXCHANGE_VALIDITY_SORT',
        startDate: 'ADDED_TIME_SORT',
        cost: 'FACE_VALUE_SORT',
      },
      marketType: this.$store.state.marketType
    };
  },
  mounted() {
    this.getData();
    this.mType = this.$route.params.pkey;
    console.log('mType', this.mType);
    if(this.mType.includes('INTEGRAL')){
      this.selectOptions = [
        {
          name: '商品名称',
          key: 'title',
        }
      ];
       dropdown.getSupplierList({}).then((result) => {
        this.supplierList = result;
      });
    }else{
      this.selectOptions = [
        {
          name: '商品名称',
          key: 'title',
        },
        {
          name: '摊位号',
          key: 'booth',
        },
      ];
    }
    dropdown.getType().then((result) => {
      this.typeList = result.content;
    });
    dropdown.getVendorList().then((result) => {
      this.vendorList = result;
    });
    dropdown.getCategory().then(result => {
      this.categoryList = result
    })
  },
  components: {
    AddComp,
    EditComp,
    KcEdit,
    ProgressDlog,
    ModelEdit,
    DisplayName,
    // GoodsLike,
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
    /**
     * @desc: 排序
     * @param {Object} row  行数据
     */
    handleSortChange({ column, prop, order }) {
      console.log(column, prop, order)
      if(order == null) {
        this.sortData = {
          sort: '',
          sortType: '',
        }
      }
      if(order == 'ascending') {
        this.sortData.sort = false
        this.sortData.sortType = this.sortTypeList[prop]
      }
      if(order == 'descending') {
        this.sortData.sort = true
        this.sortData.sortType = this.sortTypeList[prop]
      }
      this.getData()
    },
    /**
     * @desc: 失效
     * @param {Object} row  行数据
     */
    handleInvalid(row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.mall.invalidGoodsCoupon, qs.stringify(params))
        .then(() => {
          this.$message.success('失效成功');
          this.getData();
        });
    },
    /**
     * @desc: 表格选中回调
     * @param {Object} Selection  被选中的行数据集合
     */
    handleSelectionChange(selection) {
      this.multipleSelection = selection;
    },
    handelEnabled(enable = false) {
      if (!this.multipleSelection.length) {
        this.$message.warning(
          enable ? '请选择批量上架数据' : '请选择批量下架数据'
        );
        return;
      }
      let url = api.goods.stopGoodsEnable,
        message = '停用成功！';
      if (enable) {
        message = '启用成功！';
        url = api.goods.startGoodsEnable;
      }
      const params = {
        pkeys: this.multipleSelection
          .map((item) => {
            return item.pkey;
          })
          .join(','),
      };
      axios.post(url, qs.stringify(params)).then(() => {
        this.$message.success(message);
        this.getData();
      });
    },
    /**
     * 批量删除商品
     */
    handleBatchDelete() {
      if (!this.multipleSelection.length) {
        this.$message.warning('请选择批量删除数据');
        return;
      }
      const params = {
        pkeys: this.multipleSelection
          .map((item) => {
            return item.pkey;
          })
          .join(','),
      };
      axios.post(api.goods.batchDelete, qs.stringify(params)).then(() => {
        this.$message.success("删除成功！")
        this.page = 1
        this.getData()
      })
    },
    /**
     * @desc 市场推荐管理
     */
    handelGoodlike() {
      this.refs.goodsLike.show();
    },
    /**筛选 */
    handleChange() {
      this.page = 1;
      this.getData();
    },
    /**
     * 商品分类筛选
     */
    handleCategoryChange(e) {
      console.log(e);
      const [gType, goodsMain, threeGtype] = e
      this.gType = gType
      this.goodsMain = goodsMain
      this.threeGtype = threeGtype
      this.handleChange()
    },
    /**
     * 商品分类筛选
     */
    handleGTypeChange(e) {
      this.handleChange()
      console.log(e, this.typeList);
      this.goodsMain = ""
      this.goodsList = []
      if(!e) return
      const found = this.typeList.find(item => {
        return e == item.pkey
      })
      this.goodsList = found.goodsList
    },
    /**模板编辑 */
    handelModelEdit() {
      this.$refs.modelEdit.show();
    },
    /**列表导出 */
    handleImportExcel() {
      const params = {
        mType: this.$route.params.pkey,
        enabled: this.enabled,
        status: this.status,
        gtype: this.gType,
        goodsMain: this.goodsMain,
        threeGtype: this.threeGtype,
        sort: this.sortData.sort,
        sortType: this.sortData.sortType,
        vendor: this.vendor,
        supplier: this.supplier,
      };

      let that = this;
      params[this.searchKey] = this.keywords;
      this.downLoading = true;
      axios
        .post(api.goods.downGoodsExcel, qs.stringify(params), {
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
                window.navigator.msSaveOrOpenBlob(
                  blob,
                  `${
                    that.mType == 'INTEGRAL_GOODS'
                      ? '积分商品清单'
                      : that.title.replace('管理', '清单')
                  }.xlsx`
                );
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute(
                  'download',
                  `${
                    that.mType == 'INTEGRAL_GOODS'
                      ? '积分商品清单'
                      : that.title.replace('管理', '清单')
                  }.xlsx`
                );
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
     * 导入
     */
    handleImport(file) {
      let params = {},
        _this = this;
      params = new FormData();
      this.importLoading = true;
      params.append('myfile', file.file);
      params.append('mType', this.$route.params.pkey);
      axios
        .post(api.goods.ImportGoodsExcel, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'multipart/form-data;charset=UTF-8',
          },
          responseType: 'blob',
          timeout: 0,
        })
        .then((response) => {
          console.log(response);
          let blob = new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8',
          });
          if (blob.size == 0) {
            // _this.leadingVisible = true;
            _this.$refs.ProgressDlog.show();
            const timer = setInterval(() => {
              _this.uploadPercent = _this.uploadPercent + 1;
              if (_this.uploadPercent >= 100) {
                _this.uploadPercent = 0;
                // _this.leadingVisible = false;
                _this.$refs.ProgressDlog.hide();
                clearInterval(timer);
                this.importLoading = false;
                _this.$message.success('恭喜你，导入成功');
                _this.getData();
              }
            }, 50);
          } else {
            _this.$refs.ProgressDlog.show();
            const timer = setInterval(() => {
              _this.uploadPercent = _this.uploadPercent + 1;
              if (_this.uploadPercent >= 100) {
                _this.uploadPercent = 0;
                _this.$refs.ProgressDlog.hide();
                clearInterval(timer);
                const h = _this.$createElement;
                this.importLoading = false;
                _this.$msgbox({
                  title: '提示',
                  message: h('div', null, '导入出错，请下载出错数据重新导入'),
                  confirmButtonText: '确定',
                  callback: () => {
                    let objectUrl = URL.createObjectURL(blob);
                    let link = document.createElement('a');
                    link.style.display = 'none';
                    link.href = objectUrl;
                    link.setAttribute('download', '出错数据.xls');
                    document.body.appendChild(link);
                    link.click();
                    _this.getData();
                  },
                });
              }
            }, 50);
          }
        });
    },
    /**库存管理弹窗 */
    handleKcEdit(row) {
      this.$refs.KcEditComp.show({
        row: row,
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
    handleSizeChange(val) {
      this.pageSize = val
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
     * 点击新增
     */
    handelAdd: function () {
      this.$refs.AddComp.show();
    },
    /**
     * 点击修改
     */
    handleEdit: function (row) {
      row.mtype = this.mType;
      this.$refs.EditComp.show({
        row: row,
      });
    },
    handleGoRecommend(row) {
      this.$router.push({
        path: '/goods/Recommend/market',
        query: {
          pkey: row.pkey
        }
      })
    },
    /**
     * 删除
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.goods.delGoods, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
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
        url = api.goods.startGoods;
        text = '上架';
      } else {
        url = api.goods.stopGoods;
        text = '下架';
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success(text + '成功');
          this.getData();
        })
        .catch(() => {
          this.getData();
        });
    },
    handleZoneRecommendChange({zoneRecommend, pkey}) {
      const params = {
        pkey,
        enabled: zoneRecommend
      }
      axios.post(api.goods.recommendEnable, this.$qs.stringify(params))
        .then(() => {
          this.$message.success('启停成功');
          this.getData();
        })
        .catch(() => {
          this.getData();
        })
    },
    /**
     * 切换市场推荐
     * @param  {[type]} status [新状态值]
     * @param  {[type]} pkey   [记录的pkey]
     * @return {[type]}        [description]
     */
    handleGuessLikeChange(status, pkey) {
      console.log('handleGuessLikeChange');
      const params = { pkey: pkey };
      axios
        .post(api.goods.enableGuessLike, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success('切换成功');
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
        mType: this.$route.params.pkey,
        enabled: this.enabled,
        status: this.status,
        gtype: this.gType,
        goodsMain: this.goodsMain,
        threeGtype: this.threeGtype,
        sort: this.sortData.sort,
        sortType: this.sortData.sortType,
        vendor: this.vendor,
        supplier: this.supplier,
      };
      let url =
        this.$route.params.pkey == 'COUPON_GOODS'
          ? api.mall.queryGoodsCoupon
          : api.goods.queryGoodsV3;
      params[this.searchKey] = this.keywords;
      axios
        .post(url, qs.stringify(params), {
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
    handleChangeDisplayName() {
      this.$refs.displayName.show()
    },
  },
};
</script>
<style lang="less" scoped>
/deep/ .el-table__fixed-right {
  height: 100% !important;
}
</style>