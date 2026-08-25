<!--
* @description 卡券活动编辑
* @fileName CouponEventsUpd.vue
* @author zs
* @date 2024/04/26
!-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="活动名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" placeholder="请输入活动名称"></el-input>
      </el-form-item>
      <el-form-item label="用户可参与次数" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.limitMemberTimes" placeholder="请输入用户可参与次数"
          v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.limitMemberTimes =val;}">
          <template slot="append">次</template>
        </el-input>
      </el-form-item>
      <el-form-item label="活动时间" :label-width="labelWidth" :required="true">
        <el-date-picker v-model="time" format="yyyy-MM-dd HH:mm" value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange"
          start-placeholder="开始时间" end-placeholder="结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="套餐总数" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.num" placeholder="请输入套餐总数"
          v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.num =val;}">
          <template slot="append">份</template>
        </el-input>
      </el-form-item>
      <el-form-item label="每日限量" :label-width="labelWidth" :required="true">
        <div style="display:flex;">
          <el-input v-model="inputModel.limitDailyNum" placeholder="请输入每日限量"
            v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.limitDailyNum =val;}"
            :disabled="inputModel.isNoLimitDailyNum">
            <template slot="append">份</template>
          </el-input>
          <el-checkbox style="margin-left:12px;" v-model="inputModel.isNoLimitDailyNum">无限制</el-checkbox>
        </div>
      </el-form-item>
      <el-form-item label="优惠券限用张数" :label-width="labelWidth" :required="true">
        <div style="display:flex;">
          <el-input v-model="inputModel.limitDailyCardNum" placeholder="请输入优惠券限用张数"
            v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.limitDailyCardNum =val;}"
            :disabled="inputModel.isNoLimitDailyCardNum">
            <template slot="append">张</template>
          </el-input>
          <el-checkbox style="margin-left:12px;" v-model="inputModel.isNoLimitDailyCardNum">无限制</el-checkbox>
        </div>
      </el-form-item>
      <el-form-item label="礼品券限用张数" :label-width="labelWidth" :required="true">
        <div style="display:flex;">
          <el-input v-model="inputModel.limitDailyGiftNum" placeholder="请输入礼品券限用张数"
            v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.limitDailyGiftNum =val;}"
            :disabled="inputModel.isNoLimitDailyGiftNum">
            <template slot="append">张</template>
          </el-input>
          <el-checkbox style="margin-left:12px;" v-model="inputModel.isNoLimitDailyGiftNum">无限制</el-checkbox>
        </div>
      </el-form-item>
      <el-form-item label="套餐内容" :label-width="labelWidth" :required="true">
        <el-button @click="favorableAddClick()">添加优惠券</el-button>
        <el-button @click="giftAddClick()">添加礼品券</el-button>
        <el-table :data="inputModel.coupons" border style="width: 100%;margin-top:12px;">
          <el-table-column label="序号" width="60">
            <template slot-scope="scope">
              {{scope.$index + 1}}
            </template>
          </el-table-column>
          <el-table-column label="卡券名称" min-width="100">
            <template slot-scope="scope">
              {{scope.row.couponTitle}}
            </template>
          </el-table-column>
          <el-table-column label="卡券类型" width="80">
            <template slot-scope="scope">
              {{scope.row.couponType == 'CARD' ? "优惠券" : "礼品券"}}
            </template>
          </el-table-column>
          <el-table-column label="库存数量" width="80">
            <template slot-scope="scope">
              {{scope.row.couponCount-scope.row.couponIssuedNum}}
            </template>
          </el-table-column>
          <el-table-column label="单次派发张数" min-width="160">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.num" :min="1"></el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="有效期" min-width="100">
            <template slot-scope="scope">
              {{scope.row.effective==null?scope.row.startDate+'~'+scope.row.endDate:'领券后'+scope.row.effective+'天内'}}
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDel(scope.row, scope.$index)">
                <el-button slot="reference" size="mini" type="danger">删除</el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <el-form-item label="活动宣传图" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
        <div class="tips">建议尺寸750*1264px</div>
      </el-form-item>
      <el-form-item label="售卖价格" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.price" placeholder="请输入售卖价格"
          @input="inputModel.price = formatPrice(inputModel.price)"></el-input>
      </el-form-item>
      <el-form-item label="活动形式" :label-width="labelWidth" :required="true">
        <el-radio-group v-model="inputModel.distributeType" :disabled="isEdit">
          <el-radio label="QRCode">二维码分发</el-radio>
          <el-radio label="WeChatGroup">微信群分发</el-radio>
          <el-radio label="memberWelfare">会员福利</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="会员福利展示图" :label-width="labelWidth" :required="true" v-if="inputModel.distributeType =='memberWelfare'">
        <img-upload ref="ImgUpload2" :limit="1" @change="changeImg2"></img-upload>
        <div class="tips">推荐尺寸：700*275px</div>
      </el-form-item>
      <el-form-item label="可见用户" :label-width="labelWidth"  v-if="inputModel.distributeType =='memberWelfare'"  :required="true" class="radio_block">
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
      <el-form-item label="是否消息通知" v-if="inputModel.distributeType == 'memberWelfare'" :label-width="labelWidth">
        <el-switch active-color="#13ce66" v-model="inputModel.sendWechatMsg"></el-switch>
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
    <!-- 组件 -->
    <coupon-events-favorable-add ref="couponEventsFavorableAdd" @confirm="favorableAddItems">
    </coupon-events-favorable-add>
    <coupon-events-gift-add ref="couponEventsGiftAdd" @confirm="giftAddItems">
    </coupon-events-gift-add>
  </el-dialog>
</template>
<script>
  import ImgUpload from '@/components/global/ImgUpload';
  import utils from '@/assets/js/utils';
  import qs from 'qs';
  import couponEventsFavorableAdd from './CouponEventsFavorableAdd.vue';
  import couponEventsGiftAdd from './CouponEventsGiftAdd.vue';
  import dropdown from '@/assets/js/dropdown';
  export default {
    data() {
      return {
        labelWidth: '120px',
        visible: false,
        loading: false,
        inputModel: {
          pkey: '',
          name: '',
          startTime: '',
          endTime: '',
          photo: '',
          price: '',
          num: '',
          sendWechatMsg: false,
          limitMemberTimes: 1,
          limitDailyNum: "",
          isNoLimitDailyNum: false,
          limitDailyCardNum: '',
          isNoLimitDailyCardNum: false,
          limitDailyGiftNum: '',
          isNoLimitDailyGiftNum: false,
          enabled: false,
          coupons: [],
          distributeType:"",
          visibleRange: 'ALL',
          tagKeys: [],
          welfarePhoto: ''
        },
        time: [],
        isEdit:false
      };
    },
    computed: {},
    mounted() {

      dropdown.getTagsList({}).then((result) => {
      this.TagsList = result;
    });
    },
    components: {
      ImgUpload,
      couponEventsFavorableAdd,
      couponEventsGiftAdd
    },
    methods: {
      //格式化价格
      formatPrice: function (price) {
        return utils.formatPrice(price);
      },
      /**
       * 图片修改事件
       */
      changeImg: function (imgUrl) {
        this.inputModel.photo = imgUrl[0];
      },
      changeImg2: function (imgUrl) {
        this.inputModel.welfarePhoto = imgUrl[0];
      },
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          pkey: '',
          name: '',
          startTime: '',
          endTime: '',
          photo: '',
          price: '',
          num: '',
          sendWechatMsg: false,
          limitMemberTimes: 1,
          limitDailyNum: "",
          isNoLimitDailyNum: false,
          limitDailyCardNum: '',
          isNoLimitDailyCardNum: false,
          limitDailyGiftNum: '',
          isNoLimitDailyGiftNum: false,
          enabled: false,
          coupons: [],
          distributeType:"",
          visibleRange: 'ALL',
          tagKeys: [],
          welfarePhoto: ''
        };
        this.time = [];
        this.$nextTick(() => {
          this.$refs.ImgUpload.updateImg('');
        });
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel
      }) {
        console.log(inputModel);
        this.isEdit = true;
        this.getData(inputModel.pkey);
      },
      getData(pkey) {
        this.loading = true;
        const params = {
          pkey: pkey,
        };
        axios
          .post(api.marketing.activityGet, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token,
            },
          })
          .then((response) => {
            this.inputModel = response;
            this.time = [this.inputModel.startTime, this.inputModel.endTime];
            this.$nextTick(() => {
            this.$refs.ImgUpload.updateImg(this.inputModel.photo);
            if( this.inputModel.distributeType =='memberWelfare') {
              this.$refs.ImgUpload2.updateImg(this.inputModel.welfarePhoto);
            }
            });
            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
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
       * 添加优惠券
       */
      favorableAddClick() {
        this.$refs.couponEventsFavorableAdd.show();
      },
      /**
       * 优惠券选择返回
       */
      favorableAddItems({
        selectList
      }) {
        var list = [];
        selectList.forEach((selectItem) => {
          let isHave = false;
          this.inputModel.coupons.forEach((item) => {
            if (item.couponType == 'CARD') {
              if (item.coupon == selectItem.pkey) {
                isHave = true;
                return;
              }
            }
          });
          if (!isHave) {
            let newItem = {
              coupon: selectItem.pkey,
              couponType: 'CARD',
              num: 1,
              couponTitle: selectItem.title,
              effective: selectItem.effective,
              startDate: selectItem.startDate,
              endDate: selectItem.endDate,
              couponCount: selectItem.count,
              couponIssuedNum: selectItem.issuedNum,
            };
            list.push(newItem);
          }
        });
        this.inputModel.coupons = this.inputModel.coupons.concat(list);
      },
      /**
       * 添加礼品券
       */
      giftAddClick() {
        this.$refs.couponEventsGiftAdd.show();
      },
      /**
       * 礼品券选择返回
       */
      giftAddItems({
        selectList
      }) {
        var list = [];
        selectList.forEach((selectItem) => {
          let isHave = false;
          this.inputModel.coupons.forEach((item) => {
            if (item.couponType == 'GIFT') {
              if (item.coupon == selectItem.pkey) {
                isHave = true;
                return;
              }
            }
          });
          if (!isHave) {
            let newItem = {
              coupon: selectItem.pkey,
              couponType: 'GIFT',
              num: 1,
              couponTitle: selectItem.title,
              effective: selectItem.effective,
              startDate: selectItem.startDate,
              endDate: selectItem.endDate,
              couponCount: selectItem.count,
              couponIssuedNum: selectItem.issuedNum,
            };
            list.push(newItem);
          }
        });
        this.inputModel.coupons = this.inputModel.coupons.concat(list);
      },
      /**
       * 套餐内容删除
       */
      handleDel: function (row, index) {
        this.inputModel.coupons.splice(index, 1);
      },
      /**
       * 处理提交
       */
      handleSubmit: function () {
        if (this.inputModel.name === '') {
          this.$message.error('请输入活动名称');
          return;
        }
        if (this.inputModel.limitMemberTimes === '') {
          this.$message.error('请输入用户可参与次数');
          return;
        }
        if (!this.time || this.time === '' || this.time === []) {
          this.$message.error('请输入活动时间');
          return;
        }
        if (this.inputModel.num === '') {
          this.$message.error('请输入套餐总数');
          return;
        }
        if (this.inputModel.limitDailyNum === '' && !this.inputModel.isNoLimitDailyNum) {
          this.$message.error('请输入每日限量');
          return;
        }
        if (this.inputModel.limitDailyCardNum === '' && !this.inputModel.isNoLimitDailyCardNum) {
          this.$message.error('请输入优惠券限用张数');
          return;
        }
        if (this.inputModel.limitDailyGiftNum === '' && !this.inputModel.isNoLimitDailyGiftNum) {
          this.$message.error('请输入礼品券限用张数');
          return;
        }
        if (this.inputModel.photo === '') {
          this.$message.error('请选择活动图片');
          return;
        }
        if (this.inputModel.price === '') {
          this.$message.error('请输入售卖价格');
          return;
        }
        if (this.inputModel.coupons.length == 0) {
          this.$message.error('请选择套餐内容');
          return;
        }
        if (!this.inputModel.distributeType) {
          this.$message.error('请选择活动形式');
          return;
        }
        console.log(this.inputModel.distributeType, this.inputModel.welfarePhoto)
        if(this.inputModel.distributeType =='memberWelfare') {
          if(!this.inputModel.welfarePhoto) {
            this.$message.error('请选择会员福利展示图');
            return;
          }

          if(this.inputModel.visibleRange == 'TAG' && this.inputModel.tagKeys.length == 0) {
            this.$message.error('至少选择一项指定标签');
            return;
          }
        }

        this.inputModel.time = this.time;
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
  .el-input-number {
    width: 150px;
    line-height: 33px;
  }
  .radio_block .el-radio {
  display: block;

  .el-select {
    display: inline-block !important;
    margin:8px;
    width: 300px;
  }

}
</style>