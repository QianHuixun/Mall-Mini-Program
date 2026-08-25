<!-- 
@name: Pastage_mkt.vue 
@description: 运费配置-市场端
@author: zs
@route: /base/market/postage
@date: 2020/07/30
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="postage">
      <span class="title-sub inline-row"><span class="red"></span>配送费配置</span>
      <el-radio-group v-model="inputModel.distributionConfig">
        <el-radio :label="true">按正常方式计算</el-radio>
        <el-radio :label="false">统一展示设定金额,展示<el-input v-model="inputModel.fee" ref="freeDeliveryInput" 
        v-on:input="limitInput($event,'fee')"></el-input>元 </el-radio>
      </el-radio-group>
      <el-row v-if="dataList.length">
        <el-col><span class="text">重量 0 (kg)</span></el-col>
        <el-col></el-col>
        <el-col></el-col>
      </el-row>
      <div v-for="(item, index) in dataList" v-bind:key="index">
        <el-row>
          <el-col>
            <span class="iconfont iconjiantou-shangxia"></span>
            <span class="iconfont iconweibiaoti40"></span>
          </el-col>
          <el-col>
            <el-input placeholder="请输入内容" size="mini" v-model="item.postage"
              v-on:input="limitInput($event,'postage',index)">
              <template slot="prepend"><span>运费</span>
              </template>
              <template slot="append">
                <span>元</span>
              </template>
            </el-input>
          </el-col>
          <el-col>
            <span v-if="dataList.length == (index + 1)">重量大于{{dataList[index-1].weight}}kg,运费为{{item.postage}};</span>
            <span v-else>{{index == 0 ? "0" : dataList[index-1].weight}}~{{item.weight}}kg,运费为{{item.postage}};</span>
          </el-col>
        </el-row>
        <el-row v-if="dataList.length == (index + 1)">
          <el-col><span class="text">重量 ∞ (kg)</span></el-col>
          <el-col></el-col>
          <el-col></el-col>
        </el-row>
        <el-row v-else>
          <el-col>
            <el-input placeholder="请输入内容" size="mini" v-model="item.weight"
              v-on:input="limitInput($event,'weight',index)">
              <template slot="prepend">
                <span>重量</span>
              </template>
              <template slot="append">
                <span>kg</span>
              </template>
            </el-input>
          </el-col>
          <el-col></el-col>
          <el-col></el-col>
        </el-row>
      </div>
      <el-row class="postage-free-box">
        <el-col></el-col>
        <el-col></el-col>
        <el-col></el-col>
      </el-row>

      <div style="width: 500px;">
        <el-form>
          <span class="title-sub"><span class="red"></span>配送费优惠规则</span>
           <el-form-item label="" label-width="80px">
            <el-checkbox v-model="inputModel.isReductionOne">订单满<el-input v-model="inputModel.reachOne" ref="reachOneInput"
                v-on:input="limitInput($event,'reachOne')"></el-input>元，配送费减免<el-input v-model="inputModel.reductionDeliveryOne" ref="reductionDeliveryOneInput"
                v-on:input="limitInput($event,'reductionDeliveryOne')"></el-input>元
            </el-checkbox>
          </el-form-item>
           <el-form-item label="" label-width="80px">
             <el-checkbox v-model="inputModel.isReductionTwo">订单满<el-input v-model="inputModel.reachTwo" ref="reachTwoInput"
                v-on:input="limitInput($event,'reachTwo')"></el-input>元，配送费减免<el-input v-model="inputModel.reductionDeliveryTwo" ref="reductionDeliveryTwoInput"
                v-on:input="limitInput($event,'reductionDeliveryTwo')"></el-input>元
            </el-checkbox>
          </el-form-item>
          <el-form-item label="" label-width="80px">
            <el-checkbox v-model="inputModel.isFree">订单满<el-input v-model="inputModel.freeDelivery"
                ref="freeDeliveryInput" v-on:input="limitInput($event,'freeDelivery')"></el-input>元包邮</el-checkbox>
          </el-form-item>
          <span class="title-sub"><span class="red">* </span>跑腿配置</span>
          <el-form-item label="配送范围" :label-width="labelWidth">
            <el-input type="number" v-model="inputModel.deliveryRange" ref="deliveryRangeInput"
              oninput="value=value.indexOf('.') > -1?value.slice(0, value.indexOf('.') + 2):value">
              <template slot="append">
                <span>公里</span>
              </template>
            </el-input>
          </el-form-item>
          <span class="title-sub bold">配送时间设置:</span>

         
      <div v-for="(item, index) in deliveryDataList" :key="index">
        <el-row v-if="index==0">
            <el-col><span class="text">距离0公里</span></el-col>
            <el-col></el-col>
            <el-col></el-col>
          </el-row>
          <el-row v-else>
          <el-col>
            <el-input placeholder="请输入内容" size="mini" v-model="item.distance"
              v-on:input="limitInput3($event,'distance',index)">
              <template slot="prepend">
                <span>距离</span>
              </template>
              <template slot="append">
                <span>公里</span>
              </template>
            </el-input>
          </el-col>
          <el-col></el-col>
          <el-col></el-col>
        </el-row>
        <el-row>
          <el-col>
            <span class="iconfont iconjiantou-shangxia"></span>
            <span class="iconfont iconweibiaoti40"></span>
          </el-col>
          <el-col>
            <el-input placeholder="请输入内容" size="mini" v-model="item.hour"
              v-on:input="limitInput3($event,'hour',index)">
              <template slot="prepend"><span>在下单后</span>
              </template>
              <template slot="append">
                <span>时</span>
              </template>
            </el-input>
          </el-col>
          <el-col>
            <el-input placeholder="请输入内容" size="mini" v-model="item.minute"
              v-on:input="limitInput3($event,'minute',index)">
              <template slot="append">
                <span>分 可送达</span>
              </template>
            </el-input>
          </el-col>
        </el-row>

      </div>
      <el-row v-if="deliveryDataList.length">
          <el-col><span class="text">距离{{inputModel.deliveryRange}}公里</span></el-col>
          <el-col></el-col>
          <el-col></el-col>
    </el-row>

      <el-row class="postage-free-box">
        <el-col></el-col>
        <el-col></el-col>
        <el-col></el-col>
      </el-row>
      <el-form-item label="可选日期:" :label-width="labelWidth" style="margin-top: 12px;">
            <el-select v-model="today" style="width: 120px;">
              <el-option label="今天" value="TODAY"></el-option>
            </el-select>
            ~
            <el-select v-model="inputModel.deliveryDate" style="width: 120px;">
              <el-option label="今天" value="TODAY"></el-option>
              <el-option label="明天" value="TOMORROW"></el-option>
              <el-option label="后天" value="AFTER_TOMORROW"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="订单起送价" :label-width="labelWidth">
            满<el-input class="start-price" v-model="inputModel.startingPrice" ref="freeDeliveryInput" 
            v-on:input="limitInput($event,'startingPrice')"></el-input>元起送
          </el-form-item>
          <span class="title-sub bold" >自提时间设置:</span>
          <el-form-item label="在下单后" :label-width="labelWidth">
            <el-input type="number" v-model="inputModel.pickupHour" size="mini" style="width: 50px"
              oninput="if(value>11)value=11"
            ></el-input>
            <span class="unti">时</span>
            <el-input type="number" v-model="inputModel.pickupMinute" size="mini" style="width: 50px"
              oninput="if(value>59)value=59"
            ></el-input>
            <span class="unti">分</span>
            <span>可自提</span>
          </el-form-item>
          <el-form-item label="可选日期:" :label-width="labelWidth">
            <el-select v-model="today" style="width: 120px;">
              <el-option label="今天" value="TODAY"></el-option>
            </el-select>
            ~
            <el-select v-model="marketInfo.pickupDeliveryDate" style="width: 120px;">
              <el-option label="今天" value="TODAY"></el-option>
              <el-option label="明天" value="TOMORROW"></el-option>
              <el-option label="后天" value="AFTER_TOMORROW"></el-option>
            </el-select>
          </el-form-item>
          <span class="title-sub bold" >配送方式管理:</span>
            <el-table :data="marketInfo.types" :loading="loading" border style="margin-left:50px; width:300px">
                <el-table-column label="分类" prop="mtypeName"  width="140">
                    <template slot-scope="scope">
                   {{scope.row.mtypeName+'商品'}}
                  </template>
                </el-table-column>
                <el-table-column label="配送"  width="80">
                  <template slot-scope="scope">
                    <el-switch v-model="scope.row.delivery" active-color="#13ce66" inactive-color="#ff4949"  @change="handleTypeChange(scope.$index,'delivery')"></el-switch>
                  </template>
                </el-table-column>
                <el-table-column label="自提" width="80">
                   <template slot-scope="scope">
                    <el-switch v-model="scope.row.pickup" active-color="#13ce66" inactive-color="#ff4949" @change="handleTypeChange(scope.$index,'pickup')"></el-switch>
                  </template>
                </el-table-column>
            </el-table>
        </el-form>
      </div>

      <el-row class="btn-bar">
        <el-col>
          <el-button size="medium" @click="restore">
            还 原
          </el-button>
          <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
            保 存
          </el-button>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import qs from 'qs';
export default {
  data() {
    return {
      labelWidth: '140px',
      disabled: true,
      loading: false,
      dataList: [],
      deliveryDataList: [],
      inputModel: {},
      input1: '',
      types: [],
      marketInfo: {},
      today: "TODAY"
    };
  },
  components: {},
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
    this.getData();
    this.getInfoData();
  },
  methods: {
    /**限制整数 */
    limitInput2(val, name) {
      val = val.replace(/[^\d]/g, '');
      this.inputModel[name] = val;
    },
    /**限制input只能输入数字和小数点后2位 */
    limitInput(value, dataName, index = -1) {
      value = value.replace(/[^\d.]/g, ''); //清除"数字"和"."以外的字符
      value = value.replace(/^\./g, ''); //验证第一个字符是数字
      value = value.replace(/\.{2,}/g, ''); //只保留第一个, 清除多余的
      value = value.replace('.', '$#$').replace(/\./g, '').replace('$#$', '.');
      value = value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数
      if (index != -1) this.dataList[index][dataName] = value;
      else this.inputModel[dataName] = value;
    },
    /**限制input只能输入数字和小数点后2位 */
    limitInput3(value, dataName, index = -1) {
      value = value.replace(/[^\d.]/g, ''); //清除"数字"和"."以外的字符
      value = value.replace(/^\./g, ''); //验证第一个字符是数字
      value = value.replace(/\.{2,}/g, ''); //只保留第一个, 清除多余的
      value = value.replace('.', '$#$').replace(/\./g, '').replace('$#$', '.');
      value = value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数
      if (index != -1) this.deliveryDataList[index][dataName] = value;
      else this.inputModel[dataName] = value;
    },
    /**配送时间改变 */
    handleTimeChange(e, index) {
      if (e == null) {
        this.inputModel.psTime.splice(index, 1);
      }
    },
    /**新增配送时间 */
    handelAdd() {
      this.inputModel.psTime.push('');
    },
    /**删除 */
    handelDel() {
      this.inputModel.psTime.pop();
    },
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {};
      axios
        .post(api.mall.queryPostage, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.dataList = response;
          this.loading = false;
        });
        console.log(api.mall.queryDeliveryPostage)
        axios.post(api.mall.queryDeliveryPostage, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          console.log(response);
          this.deliveryDataList = response;
          this.loading = false;
        });
    },
    /**
     * 获取信息
     */
    getInfoData: function () {
      this.loading = true;
      const params = {
        pkey: this.$store.state.marketPkey
      };
      axios
        .post(api.market.getMarketInfo, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          console.log(response)
          this.inputModel = response.config;
          this.marketInfo = response;
          // this.inputModel.pickupHour = response.pickupHour
          // this.inputModel.pickupMinute = response.pickupMinute
          this.$set(this.inputModel, 'pickupHour', response.pickupHour);
          this.$set(this.inputModel, 'pickupMinute', response.pickupMinute);
          this.$set(this.inputModel, 'pickupDeliveryDate', response.pickupDeliveryDate);
        });
    },
    /**
     * 还原修改
     */
    restore() {
      this.getData();
      this.getInfoData();
    },
    handleTypeChange(index, type) {
      if (
        !this.marketInfo.types[index].delivery &&
        !this.marketInfo.types[index].pickup
      ) {
        this.marketInfo.types[index][type] = true;
        this.$message.warning('配送和自提不能同时关闭');
      }
    },
    /**
     * @desc 修改配送方式
     */
    requstType() {
      let params = this.marketInfo;
      axios
        .post(api.market.updMarket, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'application/json',
          },
        })
        .then(() => {});
    },
    /**
     * 保存修改
     */
    handleSubmit: function () {
      let dataList = this.dataList;
      for (let i = 0; i < dataList.length; i++) {
        if (!dataList[i].weight.toString() && i != 3) {
          this.$message.error('请输入重量');
        }
        if (!dataList[i].postage.toString()) {
          this.$message.error('请输入运费');
          return;
        }
        if (i) {
          if (
            parseFloat(dataList[i].weight) <=
              parseFloat(dataList[i - 1].weight) &&
            i != 3
          ) {
            this.$message.error(`第${i + 1}个重量不能小于等于第${i}个重量`);
            return;
          }
        } else {
          if (parseFloat(dataList[i].weight) <= 0) {
            this.$message.error(`第1个重量不能小于等于0`);
            return;
          }
        }
      }

      if (this.inputModel.isReductionOne && (!this.inputModel.reachOne || !this.inputModel.reductionDeliveryOne)) {
          this.$message.error("请输入满减金额");
          this.$refs.reachOneInput.focus()
          this.$refs.reductionDeliveryOneInput.focus()
          return;
        }

        if (this.inputModel.isReductionTwo && (!this.inputModel.reachTwo || !this.inputModel.reductionDeliveryTwo)) {
          this.$message.error("请输入满减金额");
          this.$refs.reachTwoInput.focus()
          this.$refs.reductionDeliveryTwoInput.focus()
          return;
        }

      if (this.inputModel.isFree && !this.inputModel.freeDelivery) {
        this.$message.error('请输入满免金额');
        this.$refs.freeDeliveryInput.focus();
        return;
      }
      if (!this.inputModel.deliveryRange) {
        this.$message.error('请输入配送范围');
        return;
      }

      if (
        !(
          parseInt(this.inputModel.pickupHour) >= 0 &&
          parseInt(this.inputModel.pickupMinute) >= 0
        )
      ) {
        this.$message.error('请设置自提时间');
        return;
      }

      var params = {
        deliveryRange: this.inputModel.deliveryRange,
        pcList: this.dataList,
        deliveryTimes: this.deliveryDataList,
        psTime: this.inputModel.psTime,
        freeDelivery: this.inputModel.freeDelivery,
        isFree: this.inputModel.isFree ? this.inputModel.isFree : false,
        startingPrice: this.inputModel.startingPrice,
        phour: this.inputModel.phour,
        pminute: this.inputModel.pminute,
        pickupHour: parseInt(this.inputModel.pickupHour),
        pickupMinute: parseInt(this.inputModel.pickupMinute),
        pickupDeliveryDate: this.inputModel.pickupDeliveryDate,
        distributionConfig: this.inputModel.distributionConfig,
        fee: this.inputModel.fee,

        reachOne: this.inputModel.reachOne,
        isReductionOne: this.inputModel.isReductionOne ? this.inputModel.isReductionOne : false,
        reductionDeliveryOne: this.inputModel.reductionDeliveryOne,
        reachTwo: this.inputModel.reachTwo,
        isReductionTwo: this.inputModel.isReductionTwo ? this.inputModel.isReductionTwo : false,
        reductionDeliveryTwo: this.inputModel.reductionDeliveryTwo
      };
      this.loading = true;
      this.requstType();
      console.log(params);
      axios
        .post(api.market.updPostage, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'application/json',
          },
        })
        .then(() => {
          this.$message.success('修改成功');
          this.getData();
          this.getInfoData();
        });
      setTimeout(() => {
        this.loading = false;
      }, 300);
    },
  },
};
</script>
<style lang="less" scoped>
.postage {
  margin-top: 50px;

  .el-checkbox {
    display: block;

    .el-input {
      width: 80px;
      margin: 0 8px;
    }
  }
  .start-price {
    width: 80px;
    margin: 0 8px;
  }
  .el-radio .el-input {
    width: 80px;
    margin: 0 8px;
  }
  .el-row {
    width: 800px;
    margin-left: 50px;
    display: flex;
    align-items: center;

    .el-col {
      // border: 1px solid #eee;
      width: 200px;
      padding-left: 10px;
      flex: 1;
    }

    .el-col:first-child {
      text-align: center;

      .text {
        background: #ddd;
        padding: 2px 5px;
      }

      .iconfont {
        font-size: 40px;
        margin-left: 20px;
      }

      .iconfont:first-child {
        margin-left: 60px;
      }
    }
  }

  .btn-bar {
    margin-top: 50px;
    margin-left: 0;
  }
}

.title-big {
  margin: 12px;
  color: #4696e7;
  font-size: 20px;
}
.title-sub.bold {
  font-weight: bold;
}

.title-sub {
  display: block;
  margin: 14px;
}
.inline-row {
  display: inline-block;
}
.red {
  color: red;
}
.postage .unti {
  font-weight: bold;
  margin: 0 6px;
}

/deep/ input::-webkit-outer-spin-button,
/deep/ input::-webkit-inner-spin-button {
  -webkit-appearance: none !important;
}
/deep/ input[type='number'] {
  appearance: none;
  -moz-appearance: textfield !important;
}
</style>