<!-- 
@name: GoodsUpdate.vue 
@description: 商品维护--编辑模板 
@author: sx
@date: 2020/06/30
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" width="950px">
    <el-form>
      <el-form-item label="供应商" :label-width="labelWidth" :required="true"
        v-if="inputModel.mtype.includes('INTEGRAL')">
        <el-select v-model="inputModel.supplier" placeholder="请选择供应商" ref="supplierInput" filterable>
          <el-option :value="item.pkey" :label="item.name" v-for="(item,index) in supplierList" :key="index"></el-option>
        </el-select>
      </el-form-item> 
      <el-form-item label="商户" :label-width="labelWidth" :required="true"
        v-if="marketType === 'VENDOR_SHOPPING_MALL' && (inputModel.mtype == 'MARKET_GOODS' || inputModel.mtype == 'SPECIAL_GOODS')">
        <el-select v-model="inputModel.vendor" placeholder="请选择商户" ref="vendorInput" filterable>
          <el-option :value="item.vendor" :label="item.vendorName" v-for="(item,index) in vendorList" :key="index"></el-option>
        </el-select>
      </el-form-item> 
      <el-form-item label="商品库" :label-width="labelWidth" :required="true" >
        <!-- <el-cascader v-model="goodsType" :options="typeList" filterable :props="props" @change="handleChange">
        </el-cascader> -->
        <el-cascader v-model="goodsType" :options="categoryList" :props="props" clearable filterable @change="handleChange" placeholder="商品类型"></el-cascader>
      </el-form-item>
      <el-form-item label="商品名称" :label-width="labelWidth" :required="true"  >
        <el-input v-model="inputModel.title" ref="titleInput" placeholder="建议标题：品牌+商品+重量"></el-input>
      </el-form-item>
      <el-form-item label="商品标签" :label-width="labelWidth" >
        <el-input v-model="inputModel.tag" ref="tagInput" placeholder="请输入标签，最长6个字" :maxlength="6"></el-input>
      </el-form-item>
      <el-form-item label="描述" :label-width="labelWidth" v-if="inputModel.mtype != 'COUPON_GOODS'">
        <el-input v-model="inputModel.description" ref="descriptionInput" placeholder="请输入描述"></el-input>
      </el-form-item>
      <el-form-item label="砍价金额" :label-width="labelWidth" :required="true" class=""
        v-if="inputModel.mtype == 'CUT_GOODS'">
        <div class="cut-item" v-for="(item,index) in cutList" :key="index">
          <el-input type="text" v-on:input="(val)=>{cutList[index][0] = limitInput(val)}"
            maxlength='2' :ref="`startCutInput${index}`" v-model="cutList[index][0]"
            placeholder="请输入砍价下限">
            <i slot="suffix">%</i>
          </el-input>
          <span class="line">-</span>
          <el-input type="text" v-on:input="(val)=>{cutList[index][1] = limitInput(val)}"
            maxlength="2" :ref="`endCutInput${index}`" v-model="cutList[index][1]"
            placeholder="请输入砍价上限">
            <i slot="suffix">%</i>
          </el-input>
          <el-button type="primary" size="small" round @click="handleCutAdd" style="margin-left: 8px;"
            v-show="index+1==cutList.length">新增</el-button>
          <el-button v-show="index!=0" round type="primary" size="small" @click="handelCutDel" class="cut-item-del">
            删除
          </el-button>
        </div>
        <!-- </el-col> -->
      </el-form-item>
      <el-form-item label="成团人数" :label-width="labelWidth" :required="true" v-if="inputModel.mtype == 'COLLAGE_GOODS'">
        <el-input v-model="inputModel.collageNum" ref="collageNumInput" placeholder="请输入成团人数"
          v-on:input="limitInput2($event,'collageNum') "></el-input>
      </el-form-item>
      <el-form-item label="商品ID" :label-width="labelWidth" v-if="inputModel.mtype != 'COUPON_GOODS'">
        <el-input v-model="inputModel.serialNumber" ref="serialNumberInput" placeholder="请输入商品ID"></el-input>
      </el-form-item>
      <el-form-item label="显示销量" :label-width="labelWidth" :required="true" v-if="inputModel.mtype != 'COUPON_GOODS'">
        <el-input v-model="inputModel.xsNum" ref="xsNumInput" placeholder="请输入销量" maxlength="8"
          v-on:input="limitInput2($event,'xsNum') "></el-input>
      </el-form-item>
      <el-form-item label="每日限购" :label-width="labelWidth"
        v-if="inputModel.mtype != 'COLLAGE_GOODS'&&inputModel.mtype != 'CUT_GOODS'">
        <el-input v-model="inputModel.purchaseNum" ref="purchaseNumInput" placeholder="请输入每日限购"
          v-on:input="limitInput2($event,'purchaseNum') "></el-input>
      </el-form-item>
      <el-form-item label="是否免邮" :label-width="labelWidth" :required="true" 
        v-if="inputModel.mtype != 'GIFT_GOODS'&&inputModel.mtype != 'COUPON_GOODS'">
        <el-switch v-model="inputModel.isPostage" active-color="#13ce66" :active-value="true" :inactive-value="false">
        </el-switch>
      </el-form-item>
      <el-form-item label="市场推荐" :label-width="labelWidth" :required="true" v-if="marketType === 'MARKET_SHOPPING_MALL' && inputModel.mtype == 'MARKET_GOODS'">
        <el-switch v-model="inputModel.guessLike" active-color="#13ce66" :active-value="true" :inactive-value="false">
        </el-switch>
      </el-form-item>
      <el-form-item label="上架时间" :label-width="labelWidth" class="range_date" :required="inputModel.mtype == 'INTEGRAL_PRESALE_GOODS'">
        <el-date-picker v-model="inputModel.startDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
          placeholder="开始日期" end-placeholder="结束日期" ref="startDateInput">
        </el-date-picker>
        <span>至</span>
        <el-date-picker v-model="inputModel.endDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
          placeholder="结束日期" ref="endDateInput" :picker-options="pickerOptions">
        </el-date-picker>
      </el-form-item>
      <el-form-item v-if="inputModel.mtype == 'PRESALE_GOODS' || inputModel.mtype == 'INTEGRAL_PRESALE_GOODS' || inputModel.mtype == 'INTEGRAL_MSD_GOODS'" 
        label="发货时间" :label-width="labelWidth" class="range_date" :required="inputModel.mtype == 'PRESALE_GOODS' || inputModel.mtype == 'INTEGRAL_PRESALE_GOODS'">
        <el-date-picker v-model="inputModel.presaleStartDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
          placeholder="开始日期" end-placeholder="结束日期" ref="startDateInput">
        </el-date-picker>
        <span>至</span>
        <el-date-picker v-model="inputModel.presaleEndDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
          placeholder="结束日期" ref="endDateInput" :picker-options="pickerOptions">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="排序" :label-width="labelWidth"
        v-if="inputModel.mtype == 'MARKET_GOODS'||inputModel.mtype == 'INTEGRAL_GOODS'||inputModel.mtype == 'COUPON_GOODS' || inputModel.mtype == 'SPECIAL_GOODS'">
        <el-input v-model="inputModel.sort" ref="sortInput" placeholder="请输入排序" v-on:input="limitInput2($event,'sort') ">
        </el-input>
      </el-form-item>
      <el-form-item label="市场推荐排序" :label-width="labelWidth"
        v-if="marketType === 'MARKET_SHOPPING_MALL' && inputModel.mtype == 'MARKET_GOODS'">
        <el-input v-model="inputModel.guessSort" placeholder="请输入市场推荐排序" v-on:input="limitInput2($event,'guessSort') ">
        </el-input>
      </el-form-item>
      <el-form-item label="商品规格" :label-width="labelWidth">
        <el-table :data="inputModel.spaces" :loading="loading" border style="width: 100%">
          <el-table-column label="规格*" min-width="100">
            <template slot-scope="scope">
              <el-input v-model="scope.row.space" :ref="`spaceInput${scope.$index}`" placeholder="规格"
                @blur="handleSpaceChange(scope.row, scope.$index)"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="原价*" min-width="100" v-if="inputModel.mtype != 'COUPON_GOODS'">
            <template slot-scope="scope">
              <el-input type="text" v-model="scope.row.priceOld" :ref="`priceOldInput${scope.$index}`" placeholder="原价" @blur="handleSpaceChange(scope.row, scope.$index, 'priceOld')"
                v-on:input="num($event,'priceOld',scope.$index)">
              </el-input>
            </template>
          </el-table-column>
          <el-table-column :label="priceLabel+'*'" min-width="100">
            <template slot-scope="scope">
              <el-input type="text" v-model="scope.row.price" :ref="`priceInput${scope.$index}`" :placeholder="priceLabel" @blur="handleSpaceChange(scope.row,scope.$index, 'price')"
                v-on:input="num($event,'price',scope.$index)"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="积分*" min-width="100"
            v-if="inputModel.mtype == 'INTEGRAL_GOODS' || inputModel.mtype == 'INTEGRAL_BNYP_GOODS' ||inputModel.mtype == 'GIFT_GOODS'||inputModel.mtype == 'COUPON_GOODS'">
            <template slot-scope="scope">
              <el-input type="text" v-model="scope.row.point" :ref="`pointInput${scope.$index}`" placeholder="积分" @blur="handleSpaceChange(scope.row, scope.$index)"
                v-on:input="(val)=>{scope.row.point = limitInput(val)}">
              </el-input>
            </template>
          </el-table-column>
          <el-table-column label="佣金*" min-width="100" v-if="inputModel.mtype == 'SHARE_GOODS'">
            <template slot-scope="scope">
              <el-input type="text" v-model="scope.row.comm" :ref="`commInput${scope.$index}`" placeholder="佣金" @blur="handleSpaceChange(scope.row, scope.$index, 'comm')"
                v-on:input="num($event,'comm',scope.$index)"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="重量(kg)" width="110" v-if="inputModel.mtype != 'GIFT_GOODS'&&inputModel.mtype != 'COUPON_GOODS'">
            <template slot-scope="scope">
              <el-input type="number" v-model="scope.row.weight" :ref="`weightInput${scope.$index}`" placeholder="默认kg" @mousewheel.native.prevent @blur="handleSpaceChange(scope.row, scope.$index)"
                v-on:input="num($event,'weight',scope.$index)"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="库存*" min-width="100">
            <template slot-scope="scope">
              <el-input type="text" v-model="scope.row.kcNum" :ref="`kcNumInput${scope.$index}`" placeholder="库存" @blur="handleSpaceChange(scope.row, scope.$index)"
                v-on:input="(val)=>{scope.row.kcNum = limitInput(val)}">
              </el-input>
            </template>
          </el-table-column>
          <el-table-column label="图片" min-width="100">
            <template slot-scope="scope">
              <img-upload class="row-img" :ref="'spaceImg'+scope.$index" :needDownload="true" :limit="1" @change="changeImg5" :compIndex="scope.$index"></img-upload>
            </template>
          </el-table-column>
          <el-table-column label="操作" v-if="inputModel.mtype != 'CUT_GOODS'&&inputModel.mtype != 'COLLAGE_GOODS'
            &&inputModel.mtype != 'GIFT_GOODS'&&inputModel.mtype != 'COUPON_GOODS'">
            <template slot-scope="scope">
              <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleSpaceDel(scope.row, scope.$index)">
                <el-button slot="reference" size="mini" type="danger">删除</el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" round @click="handleSpaceAdd" style="margin-top: 8px;"
          v-if="inputModel.mtype != 'CUT_GOODS'&&inputModel.mtype != 'COLLAGE_GOODS'&&inputModel.mtype != 'GIFT_GOODS'&&inputModel.mtype != 'COUPON_GOODS'">
          新增规格</el-button>
        <div class="tips">售价填0，表示无需支付金额</div>
      </el-form-item>
      <el-form-item label="可见用户" :label-width="labelWidth"  v-if="inputModel.mtype == 'SPECIAL_GOODS' || inputModel.mtype == 'INTEGRAL_MSD_GOODS'">
        <el-radio-group v-model="inputModel.visibleRange">
          <el-radio label="ALL">全部用户</el-radio>
          <el-radio label="TAG" v-if="inputModel.mtype == 'SPECIAL_GOODS'">
            指定标签
            <el-select
              v-model="inputModel.tagKeys"
              filterable
              multiple
              collapse-tags
              ref="tagKeysSelect"
              placeholder="请选择">
              <el-option
                v-for="item in TagsList"
                :key="item.pkey"
                :label="item.name"
                :value="item.pkey">
              </el-option>
            </el-select>
          </el-radio>
          <el-radio label="TAG" v-if="inputModel.mtype == 'INTEGRAL_MSD_GOODS'">
            指定标签
            <el-select
              v-model="inputModel.msdTags"
              filterable
              multiple
              collapse-tags
              clearable
              ref="tagKeysSelect"
              @change="handleMsdTagsChange"
              placeholder="请选择">
              <el-option value="all" label="全部"></el-option>
              <el-option
                v-for="item in TagsList"
                :key="item.pkey"
                :label="item.name"
                :value="item.pkey">
              </el-option>
            </el-select>
          </el-radio>
        </el-radio-group>
        </el-form-item>
        <el-form-item label="商品卖点" :label-width="labelWidth" >
          <div class="input-item" v-for="(item,index) in inputModel.sellingPoints" :key="index">
              <el-input
                placeholder="名称"
                v-model="item.name" :maxlength="6">
              </el-input>
              <el-input
                placeholder="内容"
                v-model="item.content" :maxlength="6">
              </el-input>
            </div>

        </el-form-item>
      <el-form-item label="面值金额" :label-width="labelWidth" required  v-if="inputModel.mtype == 'COUPON_GOODS'">
        <el-input v-model="inputModel.cost" ref="costInput" placeholder="请输入面值金额" v-on:input="(val)=>{inputModel.cost = formatPrice(val)}">
        </el-input>
        <div class="tips">此劵可以抵消的金额</div>
      </el-form-item>
      <el-form-item label="最小订单金额" :label-width="labelWidth"  required   v-if="inputModel.mtype == 'COUPON_GOODS'">
        <el-input v-model="inputModel.limitCost" ref="limitCostInput" placeholder="请输入最小订单金额"  v-on:input="(val)=>{inputModel.limitCost = formatPrice(val)}">
        </el-input>
        <div class="tips">只有商品总金额到达这个数的订单才能使用此劵</div>
      </el-form-item>
      <el-form-item :label="inputModel.mtype == 'COUPON_GOODS'?'限制市场':'适用市场'" :label-width="labelWidth"  
        v-if="inputModel.mtype == 'COUPON_GOODS'||inputModel.mtype == 'GIFT_GOODS'" :required="inputModel.mtype == 'GIFT_GOODS'">
        <el-select v-model="inputModel.userFarmer" placeholder="请选择" clearable @change="userTypeChange" ref="farmerInput">
          <el-option :label="item.name" :value="item.pkey" v-for="(item,index) in marketList" :key="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="适用商户" :label-width="labelWidth" :required="true" v-if="inputModel.mtype == 'GIFT_GOODS'">
        <el-select v-model="inputModel.userVendor" placeholder="请选择商户" ref="vendorInput">
          <el-option :label="item.name" :value="item.pkey" v-for="(item,index) in giftVendorList" :key="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="限制品类" :label-width="labelWidth" v-if="inputModel.mtype == 'COUPON_GOODS'">
        <el-select v-model="inputModel.userType" placeholder="请选择" clearable @change="userTypeChange" >
          <el-option :label="item.name" :value="item.pkey" v-for="(item,index) in goodTypeList" :key="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="限制商品" :label-width="labelWidth" v-if="inputModel.mtype == 'COUPON_GOODS'">
        <el-select v-model="inputModel.userGoods"  placeholder="请选择限制商品"   clearable :disabled="!inputModel.userType">
          <el-option v-for="item in goodList" :key="item.pkey" :label="item.name" :value="item.pkey">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="兑换有效期" :label-width="labelWidth"  v-if="inputModel.mtype == 'GIFT_GOODS'">
        <el-date-picker v-model="giftDate" type="daterange" value-format="yyyy-MM-dd" range-separator="至" start-placeholder="起始日期"
          end-placeholder="截止日期">
        </el-date-picker>
        <div class="tips">只有当前时间介于起始日期和截止日期时，此券才可以兑换，如不填写则表示永久有效
        </div>
      </el-form-item>
      <el-form-item label="有效期" :label-width="labelWidth" :required="true" v-if="inputModel.mtype == 'COUPON_GOODS'">
        <el-radio-group v-model="validityType" @change="validityChange">
          <el-radio label="1">领券后<el-input v-model="inputModel.effective" ref="effectiveInput"
            v-on:input="limitInput2($event,'effective')" >
            </el-input>天</el-radio>
          <el-radio label="2">
            起止时间
            <el-date-picker v-model="inputModel.cardStartDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
              placeholder="开始日期" end-placeholder="结束日期" ref="effectiveStartInput">
            </el-date-picker>
            <span>至</span>
            <el-date-picker v-model="inputModel.cardEndDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
              placeholder="结束日期" ref="effectiveEndInput" :picker-options="pickerOptions">
            </el-date-picker>
          </el-radio>
        </el-radio-group>
        <div class="tips">只有当前时间介于起始日期和截止日期时，此券才可以使用，如不填写则表示永久有效
        </div>
      </el-form-item>
        <el-form-item label="是否消息通知" :label-width="labelWidth" v-if="inputModel.mtype == 'SPECIAL_GOODS'">
          <el-switch active-color="#13ce66" v-model="inputModel.sendWechatMsg"></el-switch>
        </el-form-item>
      <el-form-item label="商品轮播图" :label-width="labelWidth">
        <img-upload ref="ImgUpload" :limit="3" @change="changeImg"></img-upload>
        <div class="tips">建议尺寸750*750像素，不上传将显示默认占位图</div>
      </el-form-item>
      <el-form-item label="其它规格" :label-width="labelWidth"  v-if="inputModel.mtype != 'COUPON_GOODS'&&inputModel.mtype != 'GIFT_GOODS'">
        <img-upload ref="ImgUpload3" :limit="1" @change="changeImg3"></img-upload>
        <div class="tips">建议尺寸750*240像素，不上传将显示默认占位图</div>
      </el-form-item>
      <el-form-item label="缩略图" :label-width="labelWidth">
        <img-upload ref="ImgUpload4" :limit="1" @change="changeImg4"></img-upload>
        <div class="tips">建议尺寸100*100像素，不上传将显示默认占位图</div>
      </el-form-item>
      <el-form-item label="商品详情" :label-width="labelWidth">
        <el-button size="mini" @click="handleExportModel">导入模板</el-button>
        <editor ref="editor" :idName="title==='新增商品'?'addUpdateEditor':'editUpdateEditor'" :frameHeight="350" :frameWidth="600"></editor>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
        确 定
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import qs from 'qs';
import utils from '@/assets/js/utils';
import dropdown from '@/assets/js/dropdown';
import ImgUpload from '@/components/global/ImgUpload';
import Editor from '@/components/global/Editor.vue';
export default {
  data() {
    return {
      labelWidth: '120px',
      visible: false,
      loading: false,
      supplierList:[],// 供应商列表
      cutList: [['', '']],
      inputModel: {
        cutList: [['', '']],
        mtype: '',
        supplier:"",
        gtype: '', //分类pkey
        goodsMain: '', //商品库pkey
        threeGtype: '', //三级分类
        title: '',
        xsNum: 0,
        isPostage: false, //是否免邮
        sort: '',
        guessSort: '',
        sendWechatMsg: false,
        purchaseNum: 0, //每日限购
        tag: "",
        sellingPoints: [{name: "",content: ""},{name: "",content: ""},{name: "",content: ""},{name: "",content: ""}],
        spaces: [
          {
            pkey: '',
            comm: 0, //佣金
            goods: 0, //商品
            kcNum: '', //库存数量
            point: 0, //积分
            price: 0, //现价
            priceOld: 0, //原价
            priceMember: 0, //会员价
            space: '', //规格
            weight: '0', //毛重
            status: 1, //0:未修改 1:新增   2:修改  3:删除
          },
        ],
        description: '',
        serialNumber: '',
        photo1: [],
        photo2: '', //分类图
        photo3: '', //缩略图
        startDate: '',
        endDate: '',
        presaleEndDate: '', //预售配送起售日期
        presaleStartDate: '', //预售配送到期日期
        content: [],
        enabled: true,
        startCut: '0', //砍价下限
        endCut: '0', //砍价上限
        collageNum: '', //成团人数
        extendCon: '', //成团人数 或者 砍价上下限
        limitCost: '',
        cost: '',
        userType: '',
        userFarmer: '',
        userVendor: '',
        userGoods: '',
        expireChoose: true,
        giftEndDate: '',
        giftStartDate: '',
        vendor: "",
        visibleRange: 'ALL',
        tagKeys: [],
        msdTags: [],
      },
      delSpace: [], //需要被删除的规格列表
      typeList: [], //商品库二级列表
      goodTypeList: [], //优惠券限制品类列表
      goodsType: [], //
      categoryList: [],
      dates: '',
      props: {
        value: 'pkey',
        label: 'name',
        children: 'threeGtypeList',
      }, //级联选择器配置
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7;
        },
      },
      priceLabel:
        this.$route.params.pkey == 'CUT_GOODS'
          ? '底价'
          : this.$route.params.pkey == 'COUPON_GOODS'
          ? '金额'
          : '现价',
      giftVendorList: [], //供应商-商户列表
      vendorList: [],
      marketList: [],
      goodList: [], //商品列表
      goodListCopy: [], //商品列表copy
      validityType: '1', //有效期类型
      TagsList:[],
      giftDate: '',
      marketType: this.$store.state.marketType
    };
  },
  mounted() {
    dropdown.getVendorList().then((result) => {
      this.vendorList = result;
    });
    this.inputModel.mtype = this.$route.params.pkey;
    let params;
    if (
      this.$store.state.userIdentity == 1 &&
      this.inputModel.mtype != 'COUPON_GOODS'
    ) {
      params = {
        showPoint: true,
      };
    } else {
      params = {
        showMarket: true,
      };
    }

    axios.post(api.dropdown.newMarketList).then((res) => {
      this.marketList = res;
    });
    if(this.inputModel.mtype == 'SPECIAL_GOODS') {
      dropdown.getTagsList(params).then((result) => {
        this.TagsList = result;
      });
    } else if(this.inputModel.mtype == 'INTEGRAL_MSD_GOODS') {
      this.getTagsList()
    }

    if (
      this.inputModel.mtype == 'GIFT_GOODS' ||
      this.inputModel.mtype == 'COUPON_GOODS'
    ) {
      let gParams = {
        key: this.inputModel.mtype == 'GIFT_GOODS' ? 2 : 1,
      };
      dropdown.getCategory(gParams).then(result => {
        this.categoryList = result
      })
      if (this.inputModel.mtype == 'COUPON_GOODS') {
        console.log(params);
        dropdown.getType(params).then((result) => {
          this.goodTypeList = result.content;
        });
      }
    } else {
      dropdown.getCategory(params).then((result) => {
        this.categoryList = result;
      });
    }
    this.getVendorData();
    if(this.inputModel.mtype.includes('INTEGRAL')) {
      dropdown.getSupplierList(params).then((result) => {
        this.supplierList = result;
      });
    }
  },
  components: {
    ImgUpload,
    Editor,
  },
  methods: {
    limitInput(val){
      return val.replace(/[^\d]/g,'');
    },
    //格式化价格
    formatPrice: function (price) {
      return utils.formatPrice(price);
    },
    /**
     * @desc 限制市场或者品类发生改变
     * @param {Boolean} type  0 不用刷新联动变量的值 1需要
     */
    userTypeChange(type = 1) {
      if (this.inputModel.mtype == 'COUPON_GOODS') {
        if (type) this.inputModel.userGoods = '';
        let params = {
          farmer: this.inputModel.userFarmer,
          gtype: this.inputModel.userType,
        };
        axios
          .post(api.common.marketGoodsDrop, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then((response) => {
            this.goodList = response;
          });
      } else {
        if (type) this.inputModel.userVendor = '';
        this.getVendorData();
      }
    },
    /**限制整数 */
    limitInput2(val, name) {
      val = val.replace(/[^\d]/g, '');
      this.inputModel[name] = val;
    },
    /**
     * 获取商户列表
     */
    getVendorData: function () {
      const params = {
        farmer: this.inputModel.userFarmer,
      };
      axios
        .post(api.dropdown.giftVendorList, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          console.log(response);
          this.giftVendorList = response;
        });
    },
    num: function (val, obj, index) {
      console.log(this.inputModel.spaces[index][obj], val);
      val = val.replace(/[^\d.]/g, ''); //清除"数字"和"."以外的字符
      val = val.replace(/^\./g, ''); //验证第一个字符是数字
      val = val.replace(/\.{2,}/g, '.'); //只保留第一个, 清除多余的
      val = val.replace('.', '$#$').replace(/\./g, '').replace('$#$', '.');
      if (obj === 'weight') {
        val = val.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3'); //只能输入三位小数
      } else {
        val = val.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数
      }
      this.$set(this.inputModel.spaces[index], obj, val);
    },
    handleChange(value) {
      this.inputModel.gtype = value[0];
      this.inputModel.goodsMain = value[1];
      this.inputModel.threeGtype = value[2]
    },
    /**
     * 图片修改事件
     */
    changeImg: function (imgUrl) {
      this.inputModel.photo1 = imgUrl;
    },
    /**
     * 图片修改事件
     */
    changeImg2: function (imgUrl) {
      this.inputModel.content = imgUrl;
    },
    /**
     * 图片修改事件
     */
    changeImg3: function (imgUrl) {
      this.inputModel.photo2 = imgUrl[0];
    },
    /**
     * 图片修改事件
     */
    changeImg4: function (imgUrl) {
      this.inputModel.photo3 = imgUrl[0];
    },
    changeImg5: function (imgUrl, compIndex) {
      console.log(this.inputModel.spaces[compIndex])
      this.inputModel.spaces[compIndex].photo1 = imgUrl[0] || '';
      if(this.inputModel.spaces[compIndex].pkey) {
        this.inputModel.spaces[compIndex].status = 2;
      } 
    },
    /**
     * 获取热力豆用户标签
     */
    getTagsList() {
      const params = {
        types: "MSD"
      }
      axios
        .post(api.marketing.msdTagDrop, this.$qs.stringify(params))
        .then((response) => {
          this.TagsList = response
        });
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.cutList = [['', '']];
      this.inputModel = {
        mtype: this.$route.params.pkey,
        supplier:"",
        gtype: '', //分类pkey
        goodsMain: '', //商品库pkey
        threeGtype: '', //三级分类
        title: '',
        xsNum: 0,
        isPostage: false, //是否免邮
        sort: '',
        guessSort: '',
        purchaseNum: 0, //每日限购
        tag: "",
        sendWechatMsg: false,
        sellingPoints: [{name: "",content: ""},{name: "",content: ""},{name: "",content: ""},{name: "",content: ""}],
        spaces: [
          {
            pkey: '',
            comm: 0, //佣金
            goods: 0, //商品
            kcNum: '', //库存数量
            point: 0, //积分
            price: '', //价格
            priceOld: '', //原价
            priceMember: '', //会员价
            space: '', //规格
            weight: '0', //毛重
            status: 1, //0:未修改 1:新增   2:修改  3:删除
          },
        ],
        photo1: [],
        photo2: '', //分类图
        photo3: '', //缩略图
        // pickupType: false, //是否自提
        description: '',
        serialNumber: '',
        startDate: '',
        endDate: '',
        presaleEndDate: '', //预售配送起售日期
        presaleStartDate: '', //预售配送到期日期
        content: [],
        enabled: true,
        extendCon: '',
        startCut: '0',
        endCut: '0',
        collageNum: '',
        limitCost: '',
        cost: '',
        userType: '',
        userFarmer: '',
        userVendor: '',
        userGoods: '',
        expireChoose: true,
        giftEndDate: '',
        giftStartDate: '',
        vendor: '',
        visibleRange: 'ALL',
        tagKeys: [],
        msdTags: [],
      };
      this.delSpace = []; //需要被删除的规格
      this.validityType = '1';
      this.giftDate = '';
    },
    /**
     * @desc 优惠券有效期类型改变
     */
    validityChange(e) {
      if (e == '1') {
        this.inputModel.expireChoose = true;
      } else {
        this.inputModel.expireChoose = false;
      }
    },

    /**
     * 初始化数据
     */
    initData: function ({ inputModel, cutList }) {
      this.inputModel = inputModel;
      this.cutList = cutList;
      if (inputModel.mtype == 'GIFT_GOODS') {
        this.giftDate = inputModel.giftStartDate
          ? [inputModel.giftStartDate, inputModel.giftEndDate]
          : '';
      }
      if (inputModel.mtype == 'COUPON_GOODS') {
        if (inputModel.expireChoose) {
          this.validityType = '1';
        } else {
          this.validityType = '2';
        }
      }
      this.userTypeChange(0);
      this.$nextTick(() => {
        this.inputModel.spaces.map((item, index) => {
          this.$refs[`spaceImg${index}`].updateImg(item.photo1);
        });
        this.$refs.ImgUpload.updateImg(this.inputModel.photo1);
        // this.$refs.ImgUpload2.updateImg(inputModel.content);
        if (
          inputModel.mtype != 'COUPON_GOODS' &&
          inputModel.mtype != 'GIFT_GOODS'
        ) {
          this.$refs.ImgUpload3.updateImg(inputModel.photo2);
        }

        this.$refs.ImgUpload4.updateImg(inputModel.photo3);
        this.goodsType = [this.inputModel.gtype, this.inputModel.goodsMain, this.inputModel.threeGtype];
        if (inputModel.content2) {
          setTimeout(() => {
            this.$refs.editor.updateUEContent(inputModel.content2);
          }, 500);
        }
      });
    },
    show: function () {
      this.visible = true;
      this.clearData();
      let yy = new Date().getFullYear();
      let mm = new Date().getMonth() + 1;
      let dd = new Date().getDate();
      this.inputModel.mtype = this.$route.params.pkey;
      this.inputModel.startDate = yy + '-' + mm + '-' + dd;
      this.goodsType = [];

      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg('');
        this.inputModel.spaces.map((item, index) => {
          this.$refs[`spaceImg${index}`].updateImg('');
        });
        // this.$refs.ImgUpload2.updateImg('');
        if (
          this.inputModel.mtype != 'COUPON_GOODS' &&
          this.inputModel.mtype != 'GIFT_GOODS'
        ) {
          this.$refs.ImgUpload3.updateImg('');
        }
        this.$refs.ImgUpload4.updateImg('');
        this.$refs.editor.updateUEContent('');
      });
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
     * 新增砍价次数
     */
    handleCutAdd() {
      if (this.cutList.length >= 20) return;
      this.cutList.push(['', '']);
    },
    /**
     * 删除砍价次数
     */
    handelCutDel() {
      this.cutList.pop();
    },
    /**
     * 新增规格
     */
    handleSpaceAdd: function () {
      this.inputModel.spaces.push({
        pkey: '',
        comm: 0, //佣金
        goods: 0, //商品
        kcNum: '', //库存数量
        point: 0, //积分
        price: '', //价格
        priceOld: '', //原价
        priceMember: '', //会员价
        space: '', //规格
        weight: 0, //毛重
        status: 1, //0:未修改 1:新增   2:修改  3:删除
      });
    },
    handleSpaceDel: function (row, index) {
      if (row.status == 1) {
        this.inputModel.spaces.splice(index, 1);
      } else {
        this.inputModel.spaces[index].status = 3;
        this.delSpace.push(this.inputModel.spaces[index]);
        this.inputModel.spaces.splice(index, 1);
      }
      if (this.inputModel.spaces.length == 0) {
        this.handleSpaceAdd();
      }
    },
    handleSpaceChange: function (row, index, obj) {
      // console.log(row, index, obj);
      if (obj) {
        if (Object.is(Number(this.inputModel.spaces[index][obj]), NaN))
          this.inputModel.spaces[index][obj] = '0.00';
        else
          this.inputModel.spaces[index][obj] = Number(
            this.inputModel.spaces[index][obj]
          ).toFixed(2);
        this.$forceUpdate();
      }
      if (row.status == 1 || row.status == 2) {
        return;
      }
      this.inputModel.spaces[index].status = 2;
    },
    /**导入商品详情模板 */
    handleExportModel() {
      axios
        .post(api.goods.richTempGet, '', {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          console.log(res);
          if (res) {
            this.$refs.editor.insUEContent(res);
          }
        });
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if ((this.inputModel.mtype.includes('INTEGRAL')) && !this.inputModel.supplier) {
        this.$message.error('请选择供应商');
        return;
      }
      if (!this.inputModel.gtype || !this.inputModel.goodsMain) {
        this.$message.error('请选择商品库');
        return;
      }
      if (!this.inputModel.title) {
        this.$message.error('请输入商品名称');
        this.$refs.titleInput.focus();
        return;
      }
      if (
        this.inputModel.gtype != 'COUPON_GOODS' &&
        this.inputModel.xsNum === ''
      ) {
        this.$message.error('请输入销量');
        this.$refs.xsNumInput.focus();
        return;
      }
      if (
        this.inputModel.mtype == 'COLLAGE_GOODS' &&
        (this.inputModel.collageNum == '' || this.inputModel.collageNum == null)
      ) {
        this.$message.error('请输入成团人数');
        this.$refs.collageNumInput.focus();
        return;
      }
      if(this.inputModel.mtype == 'INTEGRAL_PRESALE_GOODS') {
        if (!this.inputModel.startDate) {
          this.$message.error('请输入上架开始时间');
          return;
        }
        if (!this.inputModel.endDate) {
          this.$message.error('请输入下架结束时间');
          return;
        }
      }
      if (this.inputModel.endDate) {
        if (
          new Date(this.inputModel.endDate) <
          new Date(this.inputModel.startDate)
        ) {
          this.$message.error('结束时间不能比开始时间早');
          return;
        }
      }
      if(this.inputModel.mtype == 'PRESALE_GOODS' || this.inputModel.mtype == 'INTEGRAL_PRESALE_GOODS') {
        console.log(this.inputModel)
        if (!this.inputModel.presaleStartDate) {
          this.$message.error('请输入发货开始时间');
          // this.$refs.presaleStartDateInput.focus();
          return;
        }
        if (!this.inputModel.presaleEndDate) {
          this.$message.error('请输入发货结束时间');
          // this.$refs.presaleEndDateInput.focus();
          return;
        }
        if (this.inputModel.presaleEndDate != '') {
          if (
            new Date(this.inputModel.presaleEndDate) <
            new Date(this.inputModel.presaleStartDate)
          ) {
            this.$message.error('结束时间不能比开始时间早');
            return;
          }
        }
      }
      if(this.inputModel.mtype == 'INTEGRAL_MSD_GOODS' && (this.inputModel.presaleStartDate || this.inputModel.presaleEndDate)) {
        if (!this.inputModel.presaleStartDate) {
          this.$message.error('请输入发货开始时间');
          // this.$refs.presaleStartDateInput.focus();
          return;
        }
        if (!this.inputModel.presaleEndDate) {
          this.$message.error('请输入发货结束时间');
          // this.$refs.presaleEndDateInput.focus();
          return;
        }
        if (this.inputModel.presaleEndDate != '') {
          if (
            new Date(this.inputModel.presaleEndDate) <
            new Date(this.inputModel.presaleStartDate)
          ) {
            this.$message.error('结束时间不能比开始时间早');
            return;
          }
        }
      }
      if (this.inputModel.mtype == 'CUT_GOODS') {
        for (let i in this.cutList) {
          if (this.cutList[i][0] == '') {
            // console.log(this.$refs);
            this.$message.error('请输入砍价上限');
            this.$refs[`startCutInput${i}`][0].focus();

            return;
          }
          if (this.cutList[i][1] == '') {
            // console.log(this.$refs);
            this.$message.error('请输入砍价上限');
            this.$refs[`endCutInput${i}`][0].focus();
            return;
          }
          if (parseInt(this.cutList[i][0]) > parseInt(this.cutList[i][1])) {
            this.$message.error('砍价上限不能比砍价下限小');
            this.$refs[`endCutInput${i}`][0].focus();
            return;
          }
        }
      }

      let _this = this,
        spaces = this.inputModel.spaces;
      for (var i = 0; i < spaces.length; i++) {
        if (spaces[i].space == '') {
          _this.$message.error('请输入规格');
          _this.$refs[`spaceInput${i}`].focus();
          return;
        }
        // if (_this.inputModel.mtype != "MARKET_GOODS") {
        if (
          this.inputModel.mtype != 'COUPON_GOODS' &&
          spaces[i].priceOld === ''
        ) {
          _this.$message.error('请输入原价');
          _this.$refs[`priceOldInput${i}`].focus();
          return;
        }
        // }
        if (spaces[i].price === '') {
          _this.$message.error(`请输入${this.priceLabel}`);
          _this.$refs[`priceInput${i}`].focus();
          return;
        }

        // if (_this.inputModel.mtype == "MARKET_GOODS") {
        //   if (spaces[i].priceMember === "") {
        //     _this.$message.error("请输入会员价");
        //     _this.$refs[`priceMemberInput${i}`].focus();
        //     return;
        //   }
        // } else {
        //   spaces[i].priceMember = 0;
        // }

        if (_this.inputModel.mtype == 'SHARE_GOODS') {
          if (spaces[i].comm === '') {
            _this.$message.error('请输入佣金');
            _this.$refs[`commInput${i}`].focus();
            return;
          }
          console.log(Number(spaces[i].comm), Number(spaces[i].price));
          // if (spaces[i].comm > spaces[i].price) {
          //   _this.$message.error(`佣金不能高于${this.priceLabel}`);
          //   _this.$refs[`commInput${i}`].focus();
          //   return
          // }
          if (Number(spaces[i].comm) > Number(spaces[i].price)) {
            _this.$message.error(`佣金不能高于${this.priceLabel}`);
            _this.$refs[`commInput${i}`].focus();
            return;
          }
        }
        if (
          _this.inputModel.mtype == 'COUPON_GOODS' ||
          _this.inputModel.mtype == 'GIFT_GOODS' ||
          _this.inputModel.mtype == 'INTEGRAL_GOODS' ||
          _this.inputModel.mtype == 'INTEGRAL_BNYP_GOODS'
        ) {
          if (spaces[i].point === '') {
            _this.$message.error('请输入积分');
            _this.$refs[`pointInput${i}`].focus();
            return;
          }
        }
        if (spaces[i].kcNum === '') {
          _this.$message.error('请输入库存');
          _this.$refs[`kcNumInput${i}`].focus();
          return;
        }
        if (
          _this.inputModel.mtype != 'COUPON_GOODS' &&
          _this.inputModel.mtype != 'MARKET_GOODS'
        ) {
          if (Number(spaces[i].price) > Number(spaces[i].priceOld)) {
            _this.$message.error(`${this.priceLabel}不能大于原价`);
            _this.$refs[`priceInput${i}`].focus();
            return;
          }
        }
      }
      if (this.inputModel.mtype == 'COUPON_GOODS') {
        if (this.inputModel.cost == '') {
          this.$message.error('请输入面值金额');
          this.$refs.costInput.focus();
          return;
        }
        if (this.inputModel.limitCost == '') {
          this.$message.error('请输入最小订单金额');
          this.$refs.limitCostInput.focus();
          return;
        }
        if (this.validityType == '1') {
          if (!this.inputModel.effective) {
            this.$message.error('请输入有效期');
            this.$refs.effectiveInput.focus();
            return;
          }
          this.inputModel.cardStartDate = '';
          this.inputModel.cardEndDate = '';
        } else {
          if (!this.inputModel.cardStartDate) {
            this.$message.error('请输入起始时间');
            this.$refs.effectiveStartInput.focus();
            return;
          }
          if (!this.inputModel.cardEndDate) {
            this.$message.error('请输入截止时间');
            this.$refs.effectiveEndInput.focus();
            return;
          }
          this.inputModel.effective = '';
        }
      }
      if (this.inputModel.mtype == 'GIFT_GOODS') {
        if (!this.inputModel.userFarmer) {
          this.$message.error('请选择适用市场');
          this.$refs.farmerInput.focus();
          return;
        }
        if (!this.inputModel.userVendor) {
          this.$message.error('请选择使用商户');
          this.$refs.vendorInput.focus();
          return;
        }
      }
      if (!this.inputModel.sort) {
        this.inputModel.sort = 0;
      }
      let inputModel = JSON.parse(JSON.stringify(this.inputModel));
      if (this.inputModel.mtype == 'CUT_GOODS') {
        inputModel.extendConList = this.cutList.map((item) => {
          return `${item[0]},${item[1]}`;
        });
        delete inputModel.extendCon;
      } else if (this.inputModel.mtype == 'COLLAGE_GOODS') {
        inputModel.extendCon = parseInt(inputModel.collageNum);
        delete inputModel.extendConList;
      }

      if (this.inputModel.mtype == 'COUPON_GOODS') {
        inputModel.space = inputModel.spaces[0];
        delete inputModel.spaces;
      }
      if (this.inputModel.mtype == 'GIFT_GOODS') {
        inputModel.giftEndDate =
          this.giftDate && this.giftDate[1] ? this.giftDate[1] : '';
        inputModel.giftStartDate =
          this.giftDate && this.giftDate[0] ? this.giftDate[0] : '';
        inputModel.expireChoose = inputModel.giftStartDate ? true : false;
      }

      if(this.inputModel.mtype == 'SPECIAL_GOODS') {
        if(this.inputModel.visibleRange == 'TAG' && this.inputModel.tagKeys.length == 0) {
        this.$message.error('至少选择一项指定标签');
          this.$refs.tagKeysSelect.focus();
          return;
      } else {
        this.inputModel.visibleRange = 'ALL';
        this.inputModel.tagKeys = [];
      }
    }
    if(this.inputModel.mtype == 'INTEGRAL_MSD_GOODS') {
        if(this.inputModel.visibleRange == 'TAG' && (!this.inputModel.msdTags || this.inputModel.msdTags.length == 0)) {
        this.$message.error('至少选择一项指定标签');
          this.$refs.tagKeysSelect.focus();
          return;
      } else {
        this.inputModel.visibleRange = 'ALL';
        this.inputModel.msdTags = [];
      }
    }

      delete inputModel.cutList && delete inputModel.collageNum;

      if (this.delSpace.length) {
        this.delSpace.map((item) => {
          inputModel.spaces.push(item);
        });
      }
      const editorStr = this.$refs.editor.getUEContent();
      inputModel.content2 = editorStr;
      this.$emit('confirm', {
        inputModel,
      });
    },
    /**
     * 可见用户标签
     */
    handleMsdTagsChange(e) {
      console.log(e);
      if(e && e.length && e.includes('all')) {
        this.inputModel.msdTags = this.TagsList.map(item => item.pkey)
      }
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
  },
};
</script>
<style lang="less" scoped>
.cut-item {
  width: 500px;

  /deep/.el-input {
    width: 150px;
  }

  .line {
    display: inline-block;
    width: 30px;
    text-align: center;
  }

  .cut-item-del {
    background: #f56c6c;
    border-color: #f56c6c;
  }
}
.input-item {
  .el-input {width: 200px;}
  .el-input + .el-input {
    margin-left: 10px;
  }
}
/deep/.row-img {
  .el-upload {
    width: 80px !important;
    height: 80px !important;
    line-height: 80px !important;
  }
  .el-upload-list__item {
    width: 80px !important;
    height: 80px !important;
  }
  .el-upload-list__item {
    transition: none !important;
  }
}
.el-radio {
  display: block;

  .el-input {
    width: 80px;
    margin: 8px;
  }

  .el-select {
    display: inline-block !important;
    margin:8px;
    width: 300px;
  }

  .el-date-editor {
    width: 150px !important;
  }
}
</style>
<style lang="less">
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
}

input[type='number'] {
  -moz-appearance: textfield;
}
</style>