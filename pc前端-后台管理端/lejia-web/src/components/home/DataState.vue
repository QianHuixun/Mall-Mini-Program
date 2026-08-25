<!-- 
@name: DataState.vue 
@description: 数据排版分析
@author: crj
@date: 2020/08/19
-->


<template>
  <div class="container">
    <el-container>
      <el-header>
        <div class="header-box">
          <span class="header-title">实时概况</span>
          <span class="upd-date">更新时间：{{updDate}}</span>
        </div>
      </el-header>
      <el-container>
        <el-aside width="60vh">
          <div class="aside-box">
            <div class="row-one">
              <span>营收收入 (元)</span>
              <span class="second-title">
                环比昨日
                <span class="percent">{{realTimeData.percentageSales}}</span>
                <i class="iconfont"
                  :class="realTimeData.signum==-1?'icontubiaoxiajiangqushi':'icontubiaoshangshengqushi'"
                  :style="{color:realTimeData.signum==-1?'#F56C6C':'#67C23A'}" v-if="realTimeData.signum!=0"></i>
              </span>
            </div>
            <div class="row-two">{{realTimeData.tAmtn}}</div>
            <div class="row-three" v-if="$store.state.userIdentity==1">
              <span class="aside-title">订单</span>
              {{realTimeData.tCount}}笔
            </div>
            <div class="row-three">
              <span class="aside-title">昨天全天</span>
              {{realTimeData.yAmtn}}
            </div>
          </div>
        </el-aside>
        <el-main>
          <div class="main-box">
            <div v-for="(item,index) in dataList" :key="index" class="main-item">
              <div class="main-title">{{item.name}}</div>
              <div class="main-number">
                <!-- <i class="iconfont iconhuiyuan"></i> -->
                <img :src="item.icon" />
                <span>{{item.number}}</span>
              </div>
              <div class="second-title" v-if="item.ynumber!==''">{{item.label?item.label:"昨日"}}：{{item.ynumber}}</div>
              <div class="second-title" v-else></div>
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
  import utils from "@/assets/js/utils";
  export default {
    props: {
      realData: Object
    },
    data() {
      return {
        updDate: utils.formatTimeInArr(new Date().getTime() / 1000, "Y-M-D"),
        inputModel: {
          money: utils.formatMoney("22423.90", 2)
        },
        dataList: [],
        realTimeData: {}
      };
    },
    watch: {
      realData(newVal, oldVal) {
        if (newVal) {
          axios.post(api.common.getIdentity, "", {
              headers: {
                Authorization: this.$store.state.token
              }
            })
            .then(response => {
              console.log(response)
              response.identity == 1 ? this.dataList = [{
                  name: "访客数",
                  number: newVal.tAccessNum,
                  ynumber: newVal.yAccessNum,
                  icon: require("@/assets/images/home_icon_a.png")
                },
                // {
                //   name: "储值金额",
                //   number: newVal.tComms,
                //   ynumber: newVal.comms,
                //   icon: require("@/assets/images/home_icon_b.png")
                // },
                {
                  name: "支付人数",
                  number: newVal.tMemberPayNum,
                  ynumber: newVal.waitSendGoods,
                  icon: require("@/assets/images/home_icon_c.png"),
                  label: '待发货'
                },
                // {
                //   name: "新办年费会员数",
                //   number: newVal.tMemberFeeNum,
                //   ynumber: newVal.memberFeeNum,
                //   icon: require("@/assets/images/home_icon_d.png")
                // },
                {
                  name: "新增普通会员数",
                  number: newVal.tMemberNum,
                  ynumber: newVal.memberNum,
                  icon: require("@/assets/images/home_icon_e.png")
                }
              ] : (response.identity==3?this.dataList = [{
                  name: "访客数",
                  number: newVal.tAccessNum,
                  ynumber: newVal.yAccessNum,
                  icon: require("@/assets/images/home_icon_a.png")
                },
                {
                  name: "支付人数",
                  number: newVal.tMemberPayNum,
                  ynumber: newVal.memberPayNum,
                  icon: require("@/assets/images/home_icon_c.png"),
                  label: '待发货'
                },
                {
                  name: "订单数",
                  number: newVal.tCount,
                  ynumber: newVal.yCount,
                  icon: require("@/assets/images/home_icon_e.png")
                },
                {
                  name: "待配送",
                  number: newVal.tExpressOrder,
                  ynumber: "",
                  icon: require("@/assets/images/home_icon_f.png")
                }, {
                  name: "配送中",
                  number: newVal.tExpressGoods,
                  ynumber: "",
                  icon: require("@/assets/images/home_icon_g.png")
                }, {
                  name: "已完成",
                  number: newVal.tExpressArrived,
                  ynumber: newVal.expressArrived,
                  icon: require("@/assets/images/home_icon_h.png")
                },
              ]:this.dataList = [{
                  name: "访客数",
                  number: newVal.tAccessNum,
                  ynumber: newVal.yAccessNum,
                  icon: require("@/assets/images/home_icon_a.png")
                },
                {
                  name: "支付人数",
                  number: newVal.tMemberPayNum,
                  ynumber: newVal.waitSendGoods,
                  icon: require("@/assets/images/home_icon_c.png"),
                  label: '待发货'
                },
                {
                  name: "订单数",
                  number: newVal.tCount,
                  ynumber: newVal.yCount,
                  icon: require("@/assets/images/home_icon_e.png")
                }
              ])
            });
          this.realTimeData = newVal;
        }
      }
    },
    mounted() {

    },
  };
</script>

<style lang="less" scoped>
  /deep/ .is-vertical {
    height: auto;

    .el-container {
      padding: 0 20px;

      .el-main {
        padding: 0px;
      }

      .el-aside {
        padding: 0;
      }
    }
  }

  .container {
    font-size: 16px;

    .header-box {
      background: #f7f8fa;
      padding: 10px 10px;

      .header-title {
        display: inline-block;
        font-weight: bold;
        border-left: 4px #0033ff solid;
        padding-left: 6px;
      }

      .upd-date {
        padding-left: 10px;
        font-size: 14px;
        color: #909399;
      }
    }

    .aside-box {
      font-weight: bold;
      padding: 6px 20px;

      .row-one {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .second-title {
          font-weight: bold;
          

          .percent {
            padding: 0 5px;
          }
        }
      }

      .row-two {
        padding: 14px 0;
        font-size: 20px;
      }

      .row-three {
        font-size: 15px;
        padding-bottom: 10px;
        color: #909399;

        .aside-title {
          padding-right: 10px;
        }
      }
    }

    .main-box {
      border-radius: 5px;
      background: #f2f2f2;
      padding: 10px 40px;
      height: 100%;
      width: 100%;
      display: flex;
      align-items: center;
      text-align: center;
      justify-content: space-around;

      .main-item {

        // margin-right: 60px;
        .main-title {
          text-align: center;
          font-weight: bold;
        }

        .main-number {
          display: flex;
          align-items: center;
          justify-content: center;
          text-align: center;
          font-weight: bold;
          font-size: 20px;
          padding: 20px 0;

          img {
            width: 50px;
            height: 40px;
            padding-right: 10px;
          }
        }

        .second-title {
          color: #909399;
          font-size: 14px;
          height: 19px;
        }
      }
    }
  }
</style>