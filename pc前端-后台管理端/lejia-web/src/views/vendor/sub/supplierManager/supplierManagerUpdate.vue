<!-- 
@name: MerchantUpdate.vue 
@description: 商户管理--修改模板 
@author: crj
@date: 2021/10/18
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div>
      <el-form class="zy-form" ref="form" label-width="130px">
        <div class="zy-form-columns">
          <span class="sub-title">基础信息</span>
          <el-form-item label="供应商名称" required>
            <el-input v-model="inputModel.name" placeholder="请输入供应商名称"></el-input>
          </el-form-item>
          <el-form-item label="手机号" required>
            <el-input v-model="inputModel.mobile" placeholder="请输入手机号" v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.mobile =val;}"></el-input>
          </el-form-item>
          <el-form-item label="营业时间" required>
            <el-time-picker is-range :clearable="false" format="HH:mm"
              value-format="HH:mm" v-model="businessTime" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" placeholder="选择时间范围">
            </el-time-picker>
          </el-form-item>
          <span class="sub-title">配送方式</span>
          <el-form-item label="配送方式" label-width="120px">
            <el-checkbox v-model="inputModel.allowedDelivery">配送</el-checkbox>
             <el-checkbox v-model="inputModel.allowedPickup">自提</el-checkbox>
          </el-form-item>
          <el-form-item label="自提地点" v-if="inputModel.allowedPickup">
          <div class="pickup-locations">
            <div class="location" v-for="(item,index) in inputModel.pickupLocations" :key="index">
              <span>{{item.address}}</span>
              <el-button type="text" style="color:red;margin-left: 24px;" @click="locationDel(item,index)">删除</el-button>
            </div>
            <el-button type="text" @click="locationAdd()">新增</el-button>
          </div>
        </el-form-item>
        </div>
        <div class="zy-form-columns">
          <span class="sub-title">顺丰寄件信息</span>
          <el-form-item label="顺丰月结卡号">
            <el-input v-model="inputModel.sfMonthlyCard" placeholder="请输入顺丰月结卡号"></el-input>
          </el-form-item>
          <el-form-item label="顺丰寄件appId">
            <el-input v-model="inputModel.sfAppId" placeholder="请输入顺丰寄件appId"></el-input>
          </el-form-item>
          <el-form-item label="顺丰寄件sk">
            <el-input v-model="inputModel.sfSk" placeholder="请输入顺丰寄件sk"></el-input>
          </el-form-item>
          <el-form-item label="寄件人姓名">
            <el-input v-model="inputModel.expressSender" placeholder="请输入寄件人姓名"></el-input>
          </el-form-item>
          <el-form-item label="寄件人手机号">
            <el-input v-model="inputModel.expressMobile" placeholder="请输入寄件人手机号" v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.expressMobile =val;}"></el-input>
          </el-form-item>
          <el-form-item label="地区">
            <el-cascader clearable :options="pcaTextArr_place" v-model="expressArea"  placeholder="请选择省市区"></el-cascader>
          </el-form-item>
          <el-form-item label="详细地址">
            <el-input v-model="inputModel.expressAddress" type="textarea" :rows="2" placeholder="请输入详细地址"></el-input>
          </el-form-item>
        </div>
        <el-form-item label-width="0" class="form-footer">
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    <pickup-locations ref="PickupLocations" @confirm="locationAddItem"></pickup-locations>
  </div>
</template>
<script>
import PickupLocations from "@/components/global/PickupLocations";
import { pcaTextArr } from "element-china-area-data";
export default {
  data() {
    return {
      loading: false,
      inputModel: {
        pkey: "",
        name: "",
        mobile: "",
        startBusinessTime: "",
        endBusinessTime: "",
        allowedDelivery: true,
        allowedPickup: false,
        pickupLocations: [],
        ascription: "",
        sfMonthlyCard: "",
        sfAppId: "",
        sfSk: "",
        expressSender: "",
        expressMobile: "",
        expressPro: "",
        expressCity: "",
        expressArea: "",
        expressAddress: "",
      },
      expressArea: [],
      pcaTextArr_place: pcaTextArr,
      businessTime: ["08:00", "18:00"],
    };
  },
  mounted() {},
  components: {
    PickupLocations,
  },
  methods: {
    /**
     * 新增自提地址
     */
    locationAdd() {
      if (this.inputModel.pickupLocations.length >= 10) {
        this.$message.error("最多10个自提点");
        return;
      }
      this.$refs.PickupLocations.show();
    },
    locationAddItem({ pickupLocation }) {
      console.log(pickupLocation, "pickupLocation");
      this.inputModel.pickupLocations.push(pickupLocation);
    },
    /**
     * 删除自提地址
     */
    locationDel(item, index) {
      this.inputModel.pickupLocations.splice(index, 1);
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      this.inputModel = inputModel;
      this.expressArea = [
        inputModel.expressPro,
        inputModel.expressCity,
        inputModel.expressArea,
      ];
      this.businessTime = [
        inputModel.startBusinessTime,
        inputModel.endBusinessTime,
      ];
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if (!this.inputModel.name) {
        this.$message.error("请输入供应商名称");
        return;
      }

      if (!this.inputModel.mobile) {
        this.$message.error("请输入供应商手机号");
        return;
      }

      if (this.businessTime.length == 0) {
        this.$message.error("请选择营业时间");
        return;
      }

      if(!this.inputModel.allowedDelivery && !this.inputModel.allowedPickup) {
        this.$message.error("请至少勾选一项配送方式");
        return;
      }

      // if (
      //   this.inputModel.sfMonthlyCard ||
      //   this.inputModel.sfAppId ||
      //   this.inputModel.sfSk ||
      //   this.inputModel.expressSender ||
      //   this.inputModel.expressMobile ||
      //   this.expressArea.length != 0 ||
      //   this.inputModel.expressAddress
      // ) {
      //   if (!this.inputModel.sfMonthlyCard) {
      //     this.$message.error("请输入顺丰月结卡号");
      //     return;
      //   }

      //   if (!this.inputModel.sfAppId) {
      //     this.$message.error("请输入顺丰寄件appId");
      //     return;
      //   }

      //   if (!this.inputModel.sfSk) {
      //     this.$message.error("请输入顺丰寄件sk");
      //     return;
      //   }

      //   if (!this.inputModel.expressSender) {
      //     this.$message.error("请输入寄件人姓名");
      //     return;
      //   }

      //   if (!this.inputModel.expressMobile) {
      //     this.$message.error("请输入寄件人手机号");
      //     return;
      //   }

      //   if (this.expressArea.length == 0) {
      //     this.$message.error("请选择地区");
      //     return;
      //   }

      //   if (!this.inputModel.expressAddress) {
      //     this.$message.error("请输入详细地址");
      //     return;
      //   }
      // }

      this.inputModel.startBusinessTime = this.businessTime[0];
      this.inputModel.endBusinessTime = this.businessTime[1];

      if (this.expressArea.length != 0) {
        this.inputModel.expressPro = this.expressArea[0];
        this.inputModel.expressCity = this.expressArea[1];
        this.inputModel.expressArea = this.expressArea[2];
      }else{
        this.inputModel.expressPro = "";
        this.inputModel.expressCity = "";
        this.inputModel.expressArea = "";
      }

      this.$emit("confirm", {
        inputModel: this.inputModel,
      });
    },
    /**
     * @desc 取消
     */
    handleCancel() {
      this.$router.push({
        path: "/vendor/supplierManager",
      });
    },
  },
  props: {
    title: {
      type: String,
      default: "新增供应商",
    },
  },
};
</script>
<style lang="less" scoped>
.zy-form {
  display: flex;
  flex-wrap: wrap;
}
.zy-form-columns {
  width: 45%;
  margin-right: 24px;
  .sub-title {
    display: block;
    font-size: 16px;
    margin: 20px;
  }
}
/deep/.form-footer {
  width: 100%;
  margin-top: 32px;
  .el-form-item__content {
    text-align: center;
  }
}
/deep/.zy-form-columns {
  & > .el-form-item {
    margin-bottom: 20px;

    .el-select {
      width: 100%;
    }

    .el-date-editor {
      width: 100%;
    }

    .el-cascader {
      width: 100%;
    }
  }
}
</style>