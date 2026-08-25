<!-- 
@name: Pastage.vue 
@description: 运费配置
@author: sx
@route: /base/postage
@date: 2020/07/03
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="postage">
      <el-row>
        <el-col><span class="text">重量 0 (kg)</span></el-col>
        <el-col></el-col>
        <el-col></el-col>
      </el-row>
      <template v-for="(item, index) in inputModel">
        <el-row>
          <el-col>
            <span class="iconfont iconjiantou-shangxia"></span>
            <span class="iconfont iconweibiaoti40"></span>
          </el-col>
          <el-col>
            <el-input placeholder="请输入内容" size="mini" v-model="item.postage"
              v-on:input="limitInput($event,'postage',index)">
              <template slot="prepend">运费</template>
              <template slot="append">元</template>
            </el-input>
          </el-col>
          <el-col>
            <span
              v-if="inputModel.length == (index + 1)">重量大于{{inputModel[index-1].weight}}kg,运费为{{item.postage}};</span>
            <span v-else>{{index == 0 ? "0" : inputModel[index-1].weight}}~{{item.weight}}kg,运费为{{item.postage}};</span>
          </el-col>
        </el-row>
        <el-row v-if="inputModel.length == (index + 1)">
          <el-col><span class="text">重量 ∞ (kg)</span></el-col>
          <el-col></el-col>
          <el-col></el-col>
        </el-row>
        <el-row v-else>
          <el-col>
            <el-input placeholder="请输入内容" size="mini" v-model="item.weight"
              v-on:input="limitInput($event,'weight',index)">
              <template slot="prepend">重量</template>
              <template slot="append">kg</template>
            </el-input>
          </el-col>
          <el-col></el-col>
          <el-col></el-col>
        </el-row>
      </template>

      <div style="width: 500px;">
        <el-form>
          <span class="title-sub"><span class="red"></span>免运费配置</span>
          <!-- <el-form-item label="" label-width="80px">
            <el-checkbox v-model="isReductionOne">订单满<el-input v-model="reachOne" ref="reachOneInput"
                v-on:input="limitInput($event,'reachOne')"></el-input>元，配送费减免<el-input v-model="reductionDeliveryOne" ref="reductionDeliveryOneInput"
                v-on:input="limitInput($event,'reductionDeliveryOne')"></el-input>元
            </el-checkbox>
          </el-form-item>
           <el-form-item label="" label-width="80px">
             <el-checkbox v-model="isReductionTwo">订单满<el-input v-model="reachTwo" ref="reachTwoInput"
                v-on:input="limitInput($event,'reachTwo')"></el-input>元，配送费减免<el-input v-model="reductionDeliveryTwo" ref="reductionDeliveryTwoInput"
                v-on:input="limitInput($event,'reductionDeliveryTwo')"></el-input>元
            </el-checkbox>
          </el-form-item> -->
           <el-form-item label="满免" :label-width="labelWidth">
            <el-checkbox v-model="isFree">满<el-input v-model="freeDelivery" ref="freeDeliveryInput"
                v-on:input="limitInput($event,'freeDelivery')"></el-input>元包邮
            </el-checkbox>
          </el-form-item>
          <span class="title-sub bold" >自提时间设置:</span>
          <el-form-item label="在下单后" :label-width="labelWidth">
            <el-input type="number" v-model="pickupHour" size="mini" style="width: 50px"
              oninput="if(value>11)value=11"
            ></el-input>
            <span class="unti">时</span>
            <el-input type="number" v-model="pickupMinute" size="mini" style="width: 50px"
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
            <el-select v-model="pickupDeliveryDate" style="width: 120px;">
              <el-option label="今天" value="TODAY"></el-option>
              <el-option label="明天" value="TOMORROW"></el-option>
              <el-option label="后天" value="AFTER_TOMORROW"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <el-row class="btn-bar">
        <el-col>
          <el-button size="medium" @click="getData">
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
  import qs from "qs";

  export default {
    data() {
      return {
        labelWidth: "140px",
        disabled: true,
        loading: false,
        inputModel: [],
        isFree: false,
        freeDelivery: '',
        input1: "",
        today:"TODAY",
        pickupHour:"",
        pickupMinute:"",
        pickupDeliveryDate:"",
        // reachOne: '',
        // isReductionOne: false,
        // reductionDeliveryOne: '',
        // reachTwo: '',
        // isReductionTwo: false,
        // reductionDeliveryTwo: '',
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
      }
    },
    mounted() {
      this.getData();
    },
    methods: {
      /**限制input只能输入数字和小数点后2位 */
      limitInput(value, dataName, index = -1) {
        value = value.replace(/[^\d.]/g, ""); //清除"数字"和"."以外的字符
        value = value.replace(/^\./g, ""); //验证第一个字符是数字
        value = value.replace(/\.{2,}/g, ""); //只保留第一个, 清除多余的
        value = value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
        value = value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数
        if (index != -1)
          this.inputModel[index][dataName] = value
        else
          this[dataName] = value

      },
      /**
       * 获取列表
       */
      getData: function () {
        this.loading = true;
        const params = {};
        axios.post(api.mall.queryPostage, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            console.log(response)
            this.inputModel = response;
            this.loading = false;
          });
        axios.post(api.market.getMarketInfo, {}, {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            this.isFree = response.config.isFree;
            this.freeDelivery = response.config.freeDelivery;

            this.pickupHour = response.pickupHour;
            this.pickupMinute = response.pickupMinute;
            this.pickupDeliveryDate = response.pickupDeliveryDate;

            // this.isReductionOne = response.config.isReductionOne;
            // this.reachOne = response.config.reachOne;
            // this.reductionDeliveryOne = response.config.reductionDeliveryOne;

            // this.isReductionTwo = response.config.isReductionTwo;
            // this.reachTwo = response.config.reachTwo;
            // this.reductionDeliveryTwo = response.config.reductionDeliveryTwo;

          });
      },
      /**
       * 保存修改
       */
      handleSubmit: function () {
        let inputModel = this.inputModel;
        for (let i = 0; i < inputModel.length; i++) {
          if (!inputModel[i].weight.toString() && i != 3) {
            this.$message.error("请输入重量");
            return;
          }
          if (!inputModel[i].postage.toString()) {
            this.$message.error("请输入运费");
            return;
          }
          if (i) {
            if (parseFloat(inputModel[i].weight) <= parseFloat(inputModel[i - 1].weight) && i != 3) {
              this.$message.error(`第${i+1}个重量不能小于等于第${i}个重量`);
              return;
            }
          } else {
            if (parseFloat(inputModel[i].weight) <= 0) {
              this.$message.error(`第1个重量不能小于等于0`);
              return;
            }
          }
        }

        if (this.isFree && !this.freeDelivery) {
          this.$message.error("请输入满免金额");
          this.$refs.freeDeliveryInput.focus()
          return;
        }

        // if (this.isReductionOne && (!this.reachOne || !this.reductionDeliveryOne)) {
        //   this.$message.error("请输入满减金额");
        //   this.$refs.reachOneInput.focus()
        //   this.$refs.reductionDeliveryOneInput.focus()
        //   return;
        // }

        // if (this.isReductionTwo && (!this.reachTwo || !this.reductionDeliveryTwo)) {
        //   this.$message.error("请输入满减金额");
        //   this.$refs.reachTwoInput.focus()
        //   this.$refs.reductionDeliveryTwoInput.focus()
        //   return;
        // }
        // const params = this.inputModel;
        var params = {
          deliveryRange: 1,
          psTime: ["10:30", "17:30", "09:50", "11:54", "09:55", "10:54"],
          pcList: this.inputModel,
          freeDelivery: this.freeDelivery,
          isFree: this.isFree,
          pickupHour: this.pickupHour,
          pickupMinute: this.pickupMinute,
          pickupDeliveryDate: this.pickupDeliveryDate,
          // reachOne: this.reachOne,
          // isReductionOne: this.isReductionOne,
          // reductionDeliveryOne: this.reductionDeliveryOne,
          // reachTwo: this.reachTwo,
          // isReductionTwo: this.isReductionTwo,
          // reductionDeliveryTwo: this.reductionDeliveryTwo
          // yjPos: this.inputModel.yjPos,
          // yjTime: this.inputModel.yjTime
        };
        this.loading = true;
        // api.mall.updPostage
        axios.post(api.market.updPostage, params, {
            headers: {
              Authorization: this.$store.state.token,
              // "Content-Type": "application/json"
            }
          })
          .then(response => {
            this.$message.success("修改成功");
            this.getData();
          });
        setTimeout(() => {
          this.loading = false;
        }, 300);
      }
    }
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

    .el-row {
      width: 800px;
      margin-left: 50px;
      display: flex;
      align-items: center;

      .el-col {
        // border: 1px solid #eee;
        width: 200px;
        padding-left: 30px;
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

    .title-sub {
      display: block;
      margin: 14px;
    }

    .btn-bar {
      margin-top: 50px;
      margin-left: 0;
    }
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