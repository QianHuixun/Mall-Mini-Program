<!-- 
@name: DetectionUpdate.vue 
@description: 弹窗广告--编辑模板 
@author: crj
@date: 2020/09/22
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="市场" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.farmer" @change="handleChange" placeholder="选择市场" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in marketList"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="活动名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入活动名称"></el-input>
      </el-form-item>
      <el-form-item label="活动周期" :label-width="labelWidth" :required="true" class="range_date">
        <el-date-picker v-model="inputModel.startDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
          placeholder="开始日期" end-placeholder="结束日期" ref="startDateInput">
        </el-date-picker>
        <span>至</span>
        <el-date-picker v-model="inputModel.endDate" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date"
          placeholder="结束日期" ref="endDateInput" :picker-options="pickerOptions">
        </el-date-picker>
      </el-form-item>
      <!-- <el-form-item label="目标用户" :label-width="labelWidth">
        <el-select v-model="inputModel.subject" placeholder="请选择">
          <el-option label="所有" value="ALL_MEMBER"></el-option>
          <el-option label="年费会员" value="ANNUAL_MEMBER"></el-option>
          <el-option label="普通会员" value="ORDINARY_MEMBER"></el-option>
          <el-option label="活跃会员" value="ACTIVE_MEMBER"></el-option>
          <el-option label="非活跃会员" value="NOT_ACTIVE"></el-option>
          <el-option label="新注册会员" value="NEW_MEMBER"></el-option>
          <el-option label="老会员" value="OLD_MEMBER"></el-option>
          <el-option label="从未消费会员" value="NOT_CONSUMED_MEMBER"></el-option>
        </el-select>
      </el-form-item> -->
      <el-form-item label="弹窗图" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
        <div class="tips">建议尺寸588*995像素</div>
      </el-form-item>
      <click-effect ref="ClickEffect" :inputModel.sync="inputModel"></click-effect>
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
  import ImgUpload from "@/components/global/ImgUpload";
  import ClickEffect from '@/components/global/ClickEffect';
  import dropdown from "@/assets/js/dropdown";
  import qs from 'qs';
  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        inputModel: {
          farmer: "",
          startDate: "",
          endDate: "",
          subject: "ALL_MEMBER",
          photo: "",
          urlType: "NOT_URL",
          name: "",
          objKey: ""
        },
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() < Date.now() - 8.64e7;
          }
        },
        activityList: [],
        marketList:[]
      };
    },
    components: {
      ImgUpload,
      ClickEffect
    },
    mounted() {
      dropdown.getMarket().then(result => {
        this.marketList = result.content;
      });
    },
    methods: {
      handleChange() {
        this.inputModel.objKey = "";
        this.getActivtyData();
      },
      /**
       * @desc 获取活动下拉列表
       */
      getActivtyData() {
        axios.post(api.mall.activityList, qs.stringify({
          enabled: true,
          farmer: this.inputModel.farmer
        })).then((res) => {
          this.activityList = res;
        });
      },
      /**
       * 图片修改事件
       */
      changeImg: function (imgUrl) {
        this.inputModel.photo = imgUrl[0];
      },
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          farmer: "",
          startDate: "",
          endDate: "",
          subject: "ALL_MEMBER",
          photo: "",
          urlType: "GOODS",
          name: "",
          objKey: ""
        };
        
        this.$nextTick(() => {
          this.$refs.ImgUpload.updateImg("");
        });
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel
      }) {
        this.inputModel = inputModel;
        this.$refs.ImgUpload.updateImg(this.inputModel.photo);
        this.getActivtyData();
      },
      show: function () {
        this.visible = true;
        this.clearData();
        this.getActivtyData();
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.clearData();
        this.visible = false;
      },
      /**
       * 处理提交
       */
      handleSubmit: function () {
        if (!this.inputModel.farmer) {
          this.$message.error("请选择市场");
          return;
        }

        if (!this.inputModel.name) {
          this.$message.error("请输入名称");
          this.$refs.nameInput.focus();
          return;
        }

        if (!this.inputModel.photo) {
          this.$message.error("请选弹窗图");
          this.$refs.photoInput.focus();
          return;
        }
        if (!this.inputModel.startDate) {
          this.$message.error("请选择开始时间");
          this.$refs.startDateInput.focus();
          return;
        }
        if (!this.inputModel.endDate) {
          this.$message.error("请选择结束时间");
          this.$refs.endDateInput.focus();
          return;
        }
        if (this.inputModel.endDate != "") {
          if (
            new Date(this.inputModel.endDate) <
            new Date(this.inputModel.startDate)
          ) {
            this.$message.error("结束时间不能比开始时间早");
            return;
          }
        }
        if(this.$refs.ClickEffect.validate() === false) {
          return
        }
        this.$emit("confirm", {
          inputModel: this.inputModel
        });
      }
    },
    props: {
      title: {
        type: String,
        default: "新增"
      }
    }
  };
</script>