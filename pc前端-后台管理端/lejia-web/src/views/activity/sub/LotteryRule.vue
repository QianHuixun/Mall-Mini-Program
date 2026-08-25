<!-- 
@name: LotteryRule.vue 
@description: 抽奖活动配置--修改规则 
@author: sx
@date: 2020/07/07
-->
<template lang="html">
  <el-dialog title="抽奖活动规则" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="抽奖消费积分" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.point" ref="pointInput" placeholder="请输入抽奖消费积分"></el-input>
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
import qs from "qs";
export default {
  data() {
    return {
      labelWidth: "120px",
      visible: false,
      loading: false,
      inputModel: {
        pkey: "",
        point: ""
      },
    };
  },
  mounted() {},
  methods: {
    /**
     * 清空数据
     */
    clearData: function() {
      this.inputModel = {
        pkey: "",
        point: ""
      };
    },
    show: function() {
      this.visible = true;
      this.clearData();
      this.getData();
    },
    /**
     * 关闭弹出框
     */
    hide: function() {
      this.clearData();
      this.visible = false;
    },
    /**
     * 获取列表
     */
    getData: function() {
      this.loading = true;
      const params = {};
      axios.post(api.marketing.getLotteryRule, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.inputModel = response;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    /**
     * 处理提交
     */
    handleSubmit: function() {
      if (!this.inputModel.point) {
        this.$message.error("请输入抽奖消费积分");
        this.$refs.pointInput.focus();
        return;
      }
      const params = this.inputModel;
      this.loading = true;
      axios.post(api.marketing.updLotteryRule, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.$message.success("修改成功");
          this.hide();
        });
      setTimeout(() => {
        this.loading = false;
      }, 300);
    }
  }
};
</script>