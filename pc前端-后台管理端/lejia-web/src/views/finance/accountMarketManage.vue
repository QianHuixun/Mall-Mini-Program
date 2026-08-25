<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="info-container">
      <div class="info-title">
        <span>账户信息</span>
        <el-button type="primary" size="medium" @click="handleAccountInfo" v-if="!accountInfo.registered">
            编辑
          </el-button>
      </div>
      <div class="info-content">
        <div class="info-content-item">
          用户类型：{{ userType[accountInfo.userType] || "—-"}}
        </div>
        <div class="info-content-item">
          用户名称：{{ accountInfo.userNm || "—-" }}
        </div>
        <div class="info-content-item">
          手机：{{ accountInfo.userPhone	 || "—-" }}
        </div>
        <div class="info-content-item" v-if="['2','3'].includes(accountInfo.userType)">
          证件类型：{{ cardTypeByOtherList[accountInfo.userIdType] || accountInfo.userIdType }}
        </div>
        <div class="info-content-item" v-else>
          证件类型：{{ cardTypeList[accountInfo.userIdType] || "—-" }}
        </div>
        <div class="info-content-item">
          证件号码：{{ accountInfo.userIdNo || "—-" }}
        </div>
        <div class="info-content-item" v-if="['2','3'].includes(accountInfo.userType)">
          法人姓名：{{accountInfo.corpNm || "—-" }}
        </div>
        <div class="info-content-item" v-if="['2','3'].includes(accountInfo.userType)">
          法人证件类型：{{ cardTypeList[accountInfo.corpIdType] || "—-" }}
        </div>
        <div class="info-content-item" v-if="['2','3'].includes(accountInfo.userType)">
          法人证件号码：{{ accountInfo.corpIdNo || "—-" }}
        </div>
      </div>
    </div>
    <div class="info-container">
      <div class="info-title">
        <span>银行卡信息</span>
        <el-button type="primary" size="medium" @click="handleCardInfo">
            编辑
          </el-button>
      </div>
      <div class="info-content">
        <div class="info-content-item">
          银行账号：{{ bankInfo.pan || "—-" }}
        </div>
        <div class="info-content-item">
          开户银行联行号：{{ bankInfo.panNum || "—-" }}
        </div>
        <div class="info-content-item">
          银行预留手机号：{{ bankInfo.bankPhone || "—-" }}
        </div>
        <div class="info-content-item">
          银行账户类型：{{ acctTypeList[bankInfo.acctType] || "—-" }}
        </div>
      </div>
    </div>
    <account-info-comp ref="accountInfoComp" @refresh="getData"></account-info-comp>
    <card-info-comp ref="cardInfoComp" @refresh="getData"></card-info-comp>
    </div>
    </template>
    <script>
import qs from 'qs';
import accountInfoComp from "./subComp/accountManage/accountInfoComp.vue";
import cardInfoComp from "./subComp/accountManage/cardInfoComp.vue";
export default {
  data() {
    return {
        bankInfo: {},
        accountInfo: {},
        userType: {
        1: "个人",
        2: "企业",
        3: "个体工商户"
      },
      cardTypeByOtherList:{
        "02":  "组织机构代码",
        "03": "统一社会信用代码",
        "04": "民办非企业登记证书",
        "05": "社会团体法人登记证书",
        "06": "事业单位法人登记证",
        "07": "营业执照号码",
        "08": "其他单位证件",
        },
      cardTypeList: {
      "01": "个人身份证",
      "22": "户口簿",
      "23": "外国护照",
      "25": "军人军官证",
      "26": "军人士兵证",
      "27": "武警军官证",
      "28": "港澳居民往来内地通行证（香港）",
      "29": "台湾居民往来大陆通行证",
      "30": "临时居民身份证",
      "31": "外国人永久居留证",
      "32": "中国护照",
      "33": "武警士兵证",
      "34": "港澳居民往来内地通行证（澳门）",
      "35": "边民出入境通行证",
      "36": "台湾居民旅行证",
      "37": "港澳居民居住证（香港）",
      "38": "港澳居民居住证（澳门）",
      "39": "台湾居民居住证",
      "02": "组织机构代码",
      "03": "统一社会信用代码",
      "04": "民办非企业登记证书",
      "05": "社会团体法人登记证书",
      "06": "事业单位法人登记证",
      "07": "营业执照号码",
      "08": "其他单位证件",
      },
      acctTypeList: {
        "1": "中信个人账户",
        "2": "中信企业账户",
        // "3": "他行个人账户",
        // "4": "他行企业账户",
        "5": "中信个人存折",
        // "6": "他行个人存折",
      }
    }
  },
  mounted() {
    this.getData();
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
  components: {
    accountInfoComp,
    cardInfoComp
  },
  methods: {
    handleAccountInfo: function() {
      this.$refs.accountInfoComp.show();
    },
    handleCardInfo:function(){
      if(!this.accountInfo.registered) {
        this.$message.warning("请先完成账户信息!");
        return;
      }
      this.$refs.cardInfoComp.show();
    },
    getData: function() {
      axios
        .post(api.market.financeUserInfoGet, qs.stringify({}), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.accountInfo = response;
        });
        axios
        .post(api.market.financeBankInfoGet, qs.stringify({}), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.bankInfo = response;
        });
    },
  }
}
</script>
<style lang="less" scoped>
.info-container {
  width: 500px;
  .info-title {
    margin: 20px 0;
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: bold;

    span {
      flex: 1;
    }
  }

  .info-content-item {
    line-height: 30px;
  }
}
</style>