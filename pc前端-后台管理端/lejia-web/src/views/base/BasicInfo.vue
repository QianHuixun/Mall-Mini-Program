<!-- 
@name: BasicInfo.vue 
@description: 基础信息配置
@author: sx
@route: /base/info
@date: 2020/06/28
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- form表单 -->
    <div style="width: 550px;">
      <el-form>
        <p class="title-big">积分配置</p>
        <span class="title-sub"><span class="red">* </span>积分规则</span>
        <el-form-item label="消费兑换" :label-width="labelWidth">
          <el-input v-model="inputModel.moneyRate" ref="moneyRateInput" v-on:input="limitInput($event,'moneyRate')" style="width: 45%;">
            <template slot="append">元</template></el-input>
            =
          <el-input v-model="inputModel.pointsRate" ref="pointsRateInput" v-on:input="limitInput($event,'pointsRate')" style="width: 45%;">
            <template slot="append">积分</template></el-input>
        </el-form-item>
        <el-form-item label="积分清理日期" :label-width="labelWidth">
          <el-date-picker style="width: 100%;" v-model="inputModel.pointsDate" type="date" format="MM-dd"
            value-format="MM-dd" placeholder="选择日期">
          </el-date-picker>
        </el-form-item>

        <span class="title-sub"><span class="red">* </span>签到领积分</span>
        <el-form-item label="签到积分" :label-width="labelWidth">
          <el-input v-model="inputModel.pointsQd" ref="pointsQdInput" v-on:input="limitInput($event,'pointsQd') ">
          </el-input>
        </el-form-item>
        <el-form-item label="每天递增积分" :label-width="labelWidth">
          <el-input v-model="inputModel.pointsQdDz" ref="pointsQdDzInput" v-on:input="limitInput($event,'pointsQdDz') ">
          </el-input>
        </el-form-item>
        <el-form-item label="递增天数上限" :label-width="labelWidth">
          <el-input v-model="inputModel.pointsQdSx" ref="pointsQdSxInput" v-on:input="limitInput($event,'pointsQdSx') ">
          </el-input>
        </el-form-item>

        <!-- <span class="title-sub"><span class="red">* </span>积分抽奖</span>
        <el-form-item label="抽奖限制" :label-width="labelWidth">
          <el-input v-model="inputModel.pointsCjXz" ref="pointsCjXzInput" v-on:input="limitInput($event,'pointsCjXz') ">
            <template slot="append"><span>次/天</span></template></el-input>
        </el-form-item>
        <el-form-item label="抽奖消耗积分" :label-width="labelWidth">
          <el-input v-model="inputModel.pointsCjUser" ref="pointsCjUserInput"
            v-on:input="(val)=>{val =val.replace(/\D|^0/g,'');inputModel.pointsCjUser=val}">
          </el-input>
        </el-form-item> -->
        <p class="title-big">新人注册福利</p>
        <el-form-item label="赠送优惠券" :label-width="labelWidth">
          <div style="display:flex;" v-for="(item,index) in inputModel.newcomerCard" :key="index"
            :class="inputModel.newcomerCard.length!=1&&index!=inputModel.newcomerCard.length?'coupon-box':''">
            <el-select v-model="inputModel.newcomerCard[index].newcomerCard" placeholder="请选择" clearable>
              <el-option v-for="item in CouponList" :key="item.pkey" :label="item.title" :value="item.pkey">
              </el-option>
            </el-select>
            <span style="display: block;margin: 0 12px;">X</span>
            <el-input v-model="inputModel.newcomerCard[index].newcomerCardNum" ref="newcomerCardNumInput" v-on:input="limitInputNewcomerCard($event,index)" style="width:140px" maxLength="2">
              <template slot="append"><span>张</span></template>
            </el-input>
            <div class="coupon-btn-box">
              <el-button icon="el-icon-plus" circle type="primary" v-if="index<4&&index==inputModel.newcomerCard.length-1"
                @click="handleComerCouponAdd"></el-button>
              <el-button icon="el-icon-minus" type="danger" circle v-if="index>0&&index==inputModel.newcomerCard.length-1"
                @click="handleComerCouponDel(index)">
              </el-button>
            </div>
          </div>
        </el-form-item>
        <!-- <p class="title-big">会员配置</p>
        <el-form-item label="年费会员原价" :label-width="labelWidth" :required="true" >
          <el-input v-model="inputModel.memberPrice" ref="memberPriceInput"
            v-on:input="limitInput2($event,'memberPrice')" >
          </el-input>
        </el-form-item>
        <el-form-item label="年费会员优惠价" :label-width="labelWidth" :required="true">
          <el-input v-model="inputModel.memberPriceN" ref="memberPriceNInput"
            v-on:input="limitInput2($event,'memberPriceN')">
          </el-input>
        </el-form-item>
        <el-form-item label="年费会员赠送积分" :label-width="labelWidth" :required="true">
          <el-input v-model="inputModel.memberPoints" ref="memberPointsInput"
            v-on:input="limitInput($event,'memberPoints') ">
          </el-input>
        </el-form-item>
        <el-form-item label="消费获取积分" :label-width="labelWidth" :required="true">
          <el-input v-model="inputModel.memberGetPoints" ref="memberGetPointsInput"
          v-on:input="limitInput($event,'memberGetPoints') " >
            <template slot="append"><span>倍</span></template>
          </el-input>
        </el-form-item>
        <el-form-item label="赠送优惠券" :label-width="labelWidth">
          <div style="display:flex;" v-for="(item,index) in inputModel.memberCard" :key="index"
            :class="inputModel.memberCard.length!=1&&index!=inputModel.memberCard.length?'coupon-box':''">
            <el-select v-model="inputModel.memberCard[index].memberCard" placeholder="请选择" clearable>
              <el-option v-for="item in CouponList" :key="item.pkey" :label="item.title" :value="item.pkey">
              </el-option>
            </el-select>
            <span style="display: block;margin: 0 12px;">X</span>
            <el-input v-model="inputModel.memberCard[index].memberCardNum" ref="memberCardNumInput" v-on:input="limitInputMemberCard($event, index)}" style="width:140px" maxLength="2">
              <template slot="append"><span>张</span></template>
            </el-input>
            <div class="coupon-btn-box">
              <el-button icon="el-icon-plus" circle type="primary" v-if="index<4&&index==inputModel.memberCard.length-1"
                @click="handleCouponAdd"></el-button>
              <el-button icon="el-icon-minus" type="danger" circle v-if="index>0&&index==inputModel.memberCard.length-1"
                @click="handleCouponDel(index)">
              </el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="会员办理介绍一" :label-width="labelWidth">
          <img-upload ref="ImgMemberOneUpload" :limit="1" @change="changeMemberOneImg"></img-upload>
        </el-form-item>
        <el-form-item label="会员办理介绍二" :label-width="labelWidth">
          <img-upload ref="ImgMemberTwoUpload" :limit="1" @change="changeMemberTwoImg"></img-upload>
        </el-form-item> -->
        <p class="title-big">邀请有礼</p>
        <el-form-item label="邀请有礼介绍" :label-width="labelWidth">
          <img-upload ref="ImgInviteUpload" :limit="1" @change="changeInviteImg"></img-upload>
        </el-form-item>
        <p class="title-big">商城信息</p>
        <el-form-item label="联系电话" :label-width="labelWidth">
          <el-input v-model="inputModel.tel" ref="telInput"  v-on:input="limitTel($event, 'tel')">
          </el-input>
        </el-form-item>
        <!-- <el-form-item label="退货地址" :label-width="labelWidth">
          <el-input type="textarea" v-model="inputModel.addr" ref="addrInput">
          </el-input>
        </el-form-item> -->
        <el-form-item label="企业ID" :label-width="labelWidth">
          <el-input v-model="inputModel.customerServiceId" ref="wechatNumInput">
          </el-input>
        </el-form-item>
        <el-form-item label="企业客服链接" :label-width="labelWidth">
          <el-input v-model="inputModel.customerServiceLink" ref="wechatNumInput">
          </el-input>
        </el-form-item>
        <el-form-item label="营业时间" :label-width="labelWidth">
          <div class="times-line" v-for="(item, index) in timesList" :key="index">
            <el-time-picker
              is-range
              refs="timeInput"
              v-model="timesList[index]"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              placeholder="选择时间范围"
              format="HH:mm"
              value-format="HH:mm"
              :picker-options="{ selectableRange: '18:30:00 - 20:30:00' }"
            >
            </el-time-picker>
            </div>
        </el-form-item>
        <el-form-item label="客服微信" :label-width="labelWidth">
          <el-input v-model="inputModel.wechatNum" ref="wechatNumInput">
          </el-input>
        </el-form-item>
        <el-form-item label="客服二维码" :label-width="labelWidth">
          <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
        </el-form-item>
        <el-form-item class="el-form-item--submit" style="text-align: center;">
          <el-button type="primary" @click="handleEdit" :loading="loading">
            修改信息
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import qs from 'qs';
import dropdown from '@/assets/js/dropdown';
import ImgUpload from '@/components/global/ImgUpload';

export default {
  data() {
    return {
      labelWidth: '140px',
      inputModel: {},
      CouponList: [],
      loading: false,
      timesList: [["", ""]]
    };
  },
  components: {
    ImgUpload,
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
  mounted() {
    dropdown.getCoupon().then((result) => {
      this.CouponList = result;
    });

    this.getData();
  },
  methods: {
    /**限制整数 */
    limitInput(val, name) {
      val = val.replace(/[^\d]/g, '');
      this.inputModel[name] = val;
    },
    limitInputMemberCard(val, index) {
      val = val.replace(/[^\d]/g, '');
      this.inputModel.memberCard[index].memberCardNum =val;
    },
    limitInputNewcomerCard(val, index) {
      val = val.replace(/[^\d]/g, '');
      this.inputModel.newcomerCard[index].newcomerCardNum =val;
    },
    /**限制整数 */
    limitInput2(val, name) {
      val = val.replace(/[^\d]|^0+/g, '');
      this.inputModel[name] = val;
    },
    limitTel(val, name) {
      val = val.replace(/[^0-9-]+/g, '');
      this.inputModel[name] = val;
    },
    /**
     * 获取基础信息配置的数据
     */
    getData: function () {
      const params = {};

      axios
        .post(api.mall.getConfig, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          if (!response.memberCard.length)
            response.memberCard.push({
              memberCardNum: '',
              memberCard: '',
            });
          if (!response.newcomerCard.length)
            response.newcomerCard.push({
              newcomerCardNum: '',
              newcomerCard: '',
            });
          this.inputModel = response;
          const times = response.times;
          this.timesList = times.map(time => {
            const startTime = `${time.startHour}:${time.startMinute}`;
            const EndTime = `${time.endHour}:${time.endMinute}`;
            return [startTime, EndTime];
          });
          if (!this.timesList.length) this.timesList = [["08:00", "22:00"]];
          console.log("times", this.timesList)
          this.$nextTick(() => {
            // this.$refs.ImgMemberOneUpload.updateImg(response.memberPhoto1);
            // this.$refs.ImgMemberTwoUpload.updateImg(response.memberPhoto2);
            this.$refs.ImgInviteUpload.updateImg(response.invitationPhoto);
          });
          this.$nextTick(() => {
            this.$refs.ImgUpload.updateImg(response.wechatCode);
          });
        });
      
    },
    /**
     * 客服二维码图片修改事件
     */
    changeImg: function (imgUrl) {
      this.inputModel.wechatCode = imgUrl[0];
    },
    /**
     *会员办理图片修改事件一
     */
    changeMemberOneImg(imgUrl) {
      this.inputModel.memberPhoto1 = imgUrl[0];
    },
    /**
     *会员办理图片修改事件二
     */
    changeMemberTwoImg(imgUrl) {
      this.inputModel.memberPhoto2 = imgUrl[0];
    },
    /**
     *邀请有礼介绍图片修改事件
     */
    changeInviteImg(imgUrl) {
      this.inputModel.invitationPhoto = imgUrl[0];
    },
    /**
     * 修改
     */
    handleEdit: function () {
      console.log(this.inputModel);
      if (
        !this.inputModel.moneyRate || this.inputModel.moneyRate == 0
      ) {
        this.$message.error('请输入消费金额');
        return;
      }
      if (
        !this.inputModel.pointsRate &&
        parseInt(this.inputModel.pointsRate) != 0
      ) {
        this.$message.error('请输入积分比');
        return;
      }

      if (!this.inputModel.pointsDate) {
        this.$message.error('请输入积分清理日期');
        return;
      }

      if (
        !this.inputModel.pointsQd &&
        parseInt(this.inputModel.pointsQd) != 0
      ) {
        this.$message.error('请输入签到积分');
        return;
      }

      if (
        !this.inputModel.pointsQdDz &&
        parseInt(this.inputModel.pointsQdDz) != 0
      ) {
        this.$message.error('请输入每天递增积分');
        return;
      }

      if (
        !this.inputModel.pointsQdSx &&
        parseInt(this.inputModel.pointsQdSx) != 0
      ) {
        this.$message.error('请输入递增天数上限');
        return;
      }
      if (parseInt(this.inputModel.pointsQdSx) == 0) {
        this.$message.error('递增天数上限不能为0');
        return;
      }

      if (
        !this.inputModel.pointsCjXz &&
        parseInt(this.inputModel.pointsCjXz) != 0
      ) {
        this.$message.error('请输入抽奖限制');
        return;
      }

      if (
        !this.inputModel.pointsCjUser &&
        parseInt(this.inputModel.pointsCjUser) != 0
      ) {
        this.$message.error('请输入抽奖消耗积分');
        return;
      }

      if (
        !this.inputModel.memberPrice &&
        parseInt(this.inputModel.memberPrice) != 0
      ) {
        this.$message.error('请输入年费会员原价');
        return;
      }
      if (parseInt(this.inputModel.memberPrice) == 0) {
        this.$message.error('年费会员原价不能为0');
        return;
      }

      if (!this.inputModel.memberPriceN) {
        this.$message.error('请输入年费会员优惠价');
        return;
      }
      if (parseInt(this.inputModel.memberPriceN) == 0) {
        this.$message.error('年费会员优惠价不能为0');
        return;
      }

      if (
        !this.inputModel.memberPoints &&
        parseInt(this.inputModel.memberPoints) != 0
      ) {
        this.$message.error('请输入年费会员赠送积分');
        return;
      }

      if (
        !this.inputModel.memberGetPoints &&
        parseInt(this.inputModel.memberGetPoints) != 0
      ) {
        this.$message.error('请输入消费获取积分');
        return;
      }

      if (!this.inputModel.tel) {
        this.$message.error('请输入联系电话');
        return;
      }

      if (!this.inputModel.addr) {
        this.$message.error('请输入退货地址');
        return;
      }
      for (let i = this.inputModel.memberCard.length - 1; i >= 0; i--) {
        let item = this.inputModel.memberCard[i];
        if (
          !item.memberCard ||
          (!item.memberCardNum && parseInt(item.memberCardNum) != 0)
        ) {
          if (this.inputModel.memberCard.length == 1) {
            this.inputModel.memberCard[i].memberCard = '';
            this.inputModel.memberCard[i].memberCardNum = '';
          } else this.inputModel.memberCard.splice(i, 1);
        }
      }
      for (let i = this.inputModel.newcomerCard.length - 1; i >= 0; i--) {
        let item = this.inputModel.newcomerCard[i];
        if (
          !item.newcomerCard ||
          (!item.newcomerCardNum && parseInt(item.newcomerCardNum) != 0)
        ) {
          if (this.inputModel.newcomerCard.length == 1) {
            this.inputModel.newcomerCard[i].newcomerCard = '';
            this.inputModel.newcomerCard[i].newcomerCardNum = '';
          } else this.inputModel.memberCard.splice(i, 1);
        }
      }
      // 获取营业时间段
      this.inputModel.times = this.timesList.map(item => {
        console.log(item);
        const startTime = item[0];
        const endTime = item[1];
        const [startHour, startMinute] = startTime.split(":");
        const [endHour, endMinute] = endTime.split(":");
        return { startHour, startMinute, endHour, endMinute };
      });
      this.loading = true;

      let params = this.inputModel;
      axios
        .post(api.mall.updConfig, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'application/json',
          },
        })
        .then(() => {
          this.$message.success('修改成功');
          this.loading = false;
        }).catch(() => {
          this.loading = false;
        });
    },
    /**
     * 增加优惠券
     */
    handleCouponAdd() {
      let memberCard = this.inputModel.memberCard;
      memberCard.push({
        memberCardNum: '',
        memberCard: '',
      });
      this.inputModel.memberCard = memberCard;
    },
    /**
     * 删除优惠券
     */
    handleCouponDel(index) {
      let memberCard = this.inputModel.memberCard;
      memberCard.splice(index, 1);
      this.inputModel.memberCard = memberCard;
    },
    /**
     *@desc 增加新人优惠券
     */
    handleComerCouponAdd() {
      let newcomerCard = this.inputModel.newcomerCard;
      newcomerCard.push({
        newcomerCardNum: '',
        newcomerCard: '',
      });
      this.inputModel.newcomerCard = newcomerCard;
    },
    /**
     *@desc 删除新人优惠券
     */
    handleComerCouponDel(index) {
      let newcomerCard = this.inputModel.newcomerCard;
      newcomerCard.splice(index, 1);
      this.inputModel.newcomerCard = newcomerCard;
    },
  },
};
</script>
<style lang="less" scoped>
.title-big {
  margin: 12px;
  color: #4696e7;
  font-size: 20px;
}

.title-sub {
  display: block;
  margin: 14px;
}

.red {
  color: red;
}

.coupon-box {
  margin-bottom: 20px;
}

.coupon-btn-box {
  display: flex;
  margin-left: 20px;
  align-items: center;

  /deep/ .el-button {
    padding: 0px !important;
    width: 30px;
    height: 30px;
  }
}
</style>