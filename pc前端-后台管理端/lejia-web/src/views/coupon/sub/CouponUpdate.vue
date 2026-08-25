<!-- 
@name: CouponUpdate.vue 
@description: 卡券管理--编辑模板 
@author: sx
@date: 2020/07/08
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <div class="fs18">基本信息</div>
      <el-form-item label="优惠券类型" :label-width="labelWidth" :required="true">
        <div class="flex-item">
        <el-radio v-model="inputModel.type" label="GOODS_COUPON">满减券</el-radio>
        <el-radio v-model="inputModel.type" label="POSTAGE_COUPON">配送券</el-radio>
      </div>
      </el-form-item>
      <el-form-item label="优惠券名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.title" ref="titleInput" placeholder="请输入优惠券名称"></el-input>
      </el-form-item>
      <el-form-item label="优惠金额" :label-width="labelWidth" :required="true">
        <div class="flex-item">
        <el-input v-model="inputModel.cost" ref="costInput" placeholder="请输入优惠金额"
          @input="inputModel.cost = formatCost(inputModel.cost)" maxlength="5"></el-input>
          <el-checkbox v-model="inputModel.avoidPostage" v-if="inputModel.type == 'POSTAGE_COUPON'" style="margin-left:10px;">免配送费</el-checkbox>

          </div>
      </el-form-item>
      <el-form-item label="使用条件" :label-width="labelWidth" :required="true">
        <div class="flex-item">
        <span>订单满</span>
        <el-input v-model="inputModel.limitCost" ref="limitCostInput" placeholder=""
          @input="inputModel.limitCost = formatPrice(inputModel.limitCost)" style="width:150px;margin: 0 10px;"></el-input> 
          <span> 元可用</span>
          </div>
      </el-form-item>
      <el-form-item label="优惠券总数" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.count" ref="countInput" placeholder="请输入优惠券总数"
          v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.count =val;}"></el-input>
      </el-form-item>
      <el-form-item label="有效期" :label-width="labelWidth" :required="true">
        <el-radio-group v-model="validityType" @change="validityChange">
          <el-radio label="1">领券后<el-input v-model="inputModel.effective" ref="effectiveInput"
            v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.effective =val;}" >
            </el-input>天</el-radio>
          <el-radio label="2">
            起止时间
            <el-date-picker v-model="inputModel.startDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
              placeholder="开始日期" end-placeholder="结束日期" ref="startDateInput">
            </el-date-picker>
            <span>至</span>
            <el-date-picker v-model="inputModel.endDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
              placeholder="结束日期" ref="endDateInput" :picker-options="pickerOptions">
            </el-date-picker>
          </el-radio>
        </el-radio-group>
        <!-- <div class="tips">只有当前时间介于起始日期和截止日期时，此券才可以使用，如不填写则表示永久有效
        </div> -->
      </el-form-item>
      <el-form-item label="领取方式" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.cardType" placeholder="请选择">
          <el-option label="手动发放" value="MANUALLY_ISSUE"></el-option>
          <!-- <el-option label="二维码自领" value="SELF_LEADING_QR_CODE"></el-option> -->
          <el-option label="领券中心" value="CARD_CENTER"></el-option>
          <!-- <el-option label="所有" value="ALL"></el-option> -->
        </el-select>
      </el-form-item>
      <el-form-item label="可见用户" :label-width="labelWidth" v-if="inputModel.cardType == 'CARD_CENTER'">
        <el-radio-group v-model="inputModel.visibleRange">
          <el-radio label="ALL">全部用户</el-radio>
          <el-radio label="TAG">
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
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否消息通知" v-if="inputModel.cardType != 'MANUALLY_ISSUE'" :label-width="labelWidth">
        <el-switch active-color="#13ce66" v-model="inputModel.sendWechatMsg"></el-switch>
      </el-form-item>
      <div  class="fs18">适用范围</div>
      <el-form-item label="适用市场" :label-width="labelWidth">
        <el-select v-model="inputModel.userFarmer" placeholder="请选择" :disabled="isMarket" clearable @change="handleUserFarmerChange">
          <el-option :label="item.name" :value="item.pkey" v-for="(item,index) in MarketList" :key="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="适用专区" :label-width="labelWidth" v-if="inputModel.type=='GOODS_COUPON'">
        <el-select v-model="inputModel.userMtype" placeholder="请选择" multiple collapse-tags clearable @change="userTypeChange">
          <el-option :label="item.name" :value="item.pkey" v-for="(item,index) in mtypeList" :key="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="适用品类" :label-width="labelWidth" v-if="inputModel.type=='GOODS_COUPON'">
        <el-select v-model="inputModel.userType" placeholder="请选择" clearable @change="userTypeChange">
          <el-option :label="item.name" :value="item.pkey" v-for="(item,index) in TypeList" :key="index"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="适用商品" :label-width="labelWidth" v-if="inputModel.type=='GOODS_COUPON'">
        <el-select v-model="inputModel.userGoodsList" multiple collapse-tags clearable filterable placeholder="请选择限制商品" @change="handleGoodsChange">
          <el-option v-for="item in GoodsList" :key="item.pkey" :label="item.name" :value="item.pkey">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="适用订单" :label-width="labelWidth" v-if="inputModel.type=='GOODS_COUPON'">
        <el-select v-model="inputModel.userOrderType" placeholder="请选择" clearable>
          <el-option label="配送订单" value="DELIVERY"></el-option>
          <el-option label="自提订单" value="PICKUP"></el-option>
        </el-select>
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
import utils from '@/assets/js/utils';
import dropdown from '@/assets/js/dropdown';
import qs from 'qs';
export default {
  data() {
    return {
      labelWidth: '120px',
      visible: false,
      loading: false,
      inputModel: {
        pkey: '',
        type: "GOODS_COUPON",
        title: '',
        count: '',
        cost: '',
        limitCost: '',
        sendWechatMsg: false,
        cardType: 'MANUALLY_ISSUE', //MANUALLY_ISSUE(0, "手动发放"), SELF_LEADING_QR_CODE(1, "二维码自领"),ALL(2, "所有");CARD_CENTER(3, "领券中心领取");
        userFarmer: this.$store.state.marketPkey,
        userMtype: [],
        userGoodsList: [],
        userType: '',
        effective: '',
        startDate: '',
        endDate: '',
        expireChoose: true,
        avoidPostage: false,
        userOrderType:"",
        visibleRange: 'ALL',
        tagKeys: []
      },
      visibleRange: 'ALL',
      validityType: '1', //有效期类型
      MarketList: [],
      mtypeList: [],
      TypeList: [],
      GoodsList: [], //商品列表
      GoodsListCopy: [], //商品列表copy
      TagsList:[],
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7;
        },
      },
      totalPages: 0, //商品列表总页数
      page: 0, //商品列表当前页数
      pageSize: 8, //商品列表一页大小
      selectVal: '', //商品列表搜索关键字
    };
  },
  computed: {
    isMarket() {
      return this.$store.state.userIdentity == 1 ? false : true;
    },
  },
  mounted() {
    let params;
    if (this.$store.state.userIdentity == 1) {
      params = {};
    } else {
      params = {
        showMarket: true,
      };
    }
    dropdown.getTagsList(params).then((result) => {
      this.TagsList = result;
    });
    // dropdown.getAllGoods().then(result => {
    //   this.GoodsList = result.content;
    // });
    // dropdown.getGoods('ALL', this.page).then(result => {
    //   this.GoodsList = result.content;
    //   this.totalPages = result.totalPages;
    // });

    // dropdown.getMarket().then(result => {
    //   this.MarketList = result.content;
    // });
    axios.post(api.dropdown.marketDrop, this.$qs.stringify({includeAscription: true})).then((res) => {
      this.MarketList = res;
    });
    this.getTypeList()
    this.userTypeChange();
    this.getMtypeList()
    console.log('this.$store.state.marketPkey', this.$store.state.marketPkey);
  },
  components: {},
  methods: {
    handleUserFarmerChange() {
      this.inputModel.userMtype = []
      this.inputModel.userType = ''
      this.getTypeList()
      this.getMtypeList()
      this.userTypeChange()
    },
    userTypeChange() {
      this.inputModel.userGoodsList = [];
      let params = {
        farmer: this.inputModel.userFarmer,
        gtype: this.inputModel.userType,
        mtype: this.inputModel.userMtype.join(',')
      };
      axios
        .post(api.common.marketGoodsDrop, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.GoodsList = response;
        });
    },
    handleGoodsChange() {
      this.selectVal = '';
    },
    getTypeList() {
      const params = {
        farmer: this.inputModel.userFarmer
      }
      axios.post(api.marketing.gtypeDrop, this.$qs.stringify(params))
      .then(res => {
        this.TypeList = res
      })
    },
    getMtypeList() {
      const params = {
        farmer: this.inputModel.userFarmer
      }
      axios.post(api.marketing.mtypeDrop, this.$qs.stringify(params))
      .then(res => {
        this.mtypeList = res
      })
    },
    /**
     * @desc 格式化优惠券金额
     */
    formatCost(val) {
      val = val.replace(/[^\d.]/g, ''); //清除"数字"和"."以外的字符
      val = val.replace(/^\./g, ''); //验证第一个字符是数字
      val = val.replace(/\.{2,}/g, '.'); //只保留第一个, 清除多余的
      val = val.replace('.', '$#$').replace(/\./g, '').replace('$#$', '.');
      val = val.replace(/^(\-)*(\d+)\.(\d).*$/, '$1$2.$3'); //只能输入两个小数
      return val;
    },
    //格式化价格
    formatPrice: function (price) {
      return utils.formatPrice(price);
    },
    //有效期类型改变
    validityChange(e) {
      if (e == '1') {
        this.inputModel.expireChoose = true;
      } else {
        this.inputModel.expireChoose = false;
      }
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        pkey: '',
        title: '',
        type: "GOODS_COUPON",
        count: '',
        cost: '',
        limitCost: '',
        sendWechatMsg: false,
        cardType: 'MANUALLY_ISSUE', //MANUALLY_ISSUE(0, "手动发放"), SELF_LEADING_QR_CODE(1, "二维码自领"),ALL(2, "所有");
        userFarmer: this.isMarket
          ? this.$store.state.marketPkey
          : '',
        userGoodsList: [],
        userMtype: [],
        userType: '',
        effective: '',
        startDate: '',
        endDate: '',
        expireChoose: true,
        avoidPostage: false,
        userOrderType:"",
        visibleRange: 'ALL',
        tagKeys: []
      };
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      console.log(inputModel);
      this.inputModel = inputModel;
      if (inputModel.expireChoose) {
        this.validityType = '1';
      } else {
        this.validityType = '2';
      }
    },
    show: function () {
      this.visible = true;
      this.clearData();
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
     * 处理提交
     */
    handleSubmit: function () {
      if (this.inputModel.title === '') {
        this.$message.error('请输入标题');
        this.$refs.titleInput.focus();
        return;
      }
      if (this.inputModel.count === '') {
        this.$message.error('请输入优惠券总数');
        this.$refs.countInput.focus();
        return;
      }

      if (this.inputModel.cost === '' && this.inputModel.type === 'GOODS_COUPON') {
        this.$message.error('请输入优惠金额');
        this.$refs.costInput.focus();
        return;
      }

      if (this.inputModel.cost === '' && !this.inputModel.avoidPostage  && this.inputModel.type === 'POSTAGE_COUPON') {
        this.$message.error('请输入优惠金额或选择免配送费');
        this.$refs.costInput.focus();
        return;
      }

      if (this.inputModel.limitCost === '') {
        this.$message.error('请输入使用条件');
        this.$refs.limitCostInput.focus();
        return;
      }
      console.log(this.validityType);
      if (this.validityType == '1') {
        if (!this.inputModel.effective) {
          this.$message.error('请输入有效期');
          this.$refs.effectiveInput.focus();
          return;
        }
        this.inputModel.startDate = '';
        this.inputModel.endDate = '';
      } else {
        if (!this.inputModel.startDate) {
          this.$message.error('请输入起始时间');
          this.$refs.startDateInput.focus();
          return;
        }
        if (!this.inputModel.endDate) {
          this.$message.error('请输入截止时间');
          this.$refs.endDateInput.focus();
          return;
        }
        this.inputModel.effective = '';
      }

      if(this.inputModel.type  ==  'POSTAGE_COUPON') {
        this.inputModel.userOrderType = "";
        this.inputModel.userGoodsList = "";
        this.inputModel.userType = "";
      }
      if(this.inputModel.cardType == 'CARD_CENTER') {

      if(this.inputModel.visibleRange == 'TAG' && this.inputModel.tagKeys.length == 0) {
        this.$message.error('至少选择一项指定标签');
          this.$refs.tagKeysSelect.focus();
          return;
      }
    }

      if(this.inputModel.cardType =='MANUALLY_ISSUE') {
        this.inputModel.visibleRange = 'ALL';
        this.inputModel.tagKeys = [];
      }

      this.$emit('confirm', {
        inputModel: this.inputModel,
      });
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

.fs18 {
  font-size: 18px;
}
.flex-item {
  display: flex; align-items: center;height:40px;
}
</style>