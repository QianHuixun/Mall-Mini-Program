<!-- 
@name: AbnormalUpd.vue 
@description: 异常货物--编辑模板 
@author: crj
@date: 2020/08/14
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" width="400px">
    <el-form>
      <el-form-item label="销售阈值" :label-width="labelWidth" :required="true" class="short-input">
       月销售低于 <el-input v-model="inputModel.config.abnormalNum" ref="salesInput" placeholder="请输入销售阈值" v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.config.abnormalNum =val;}"></el-input>为异常
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
export default {
  data() {
    return {
      labelWidth: "100px",
      visible: false,
      loading: false,
      inputModel: {
        config: {
          abnormalNum: 0
        }
      }
    };
  },
  mounted() {},
  components: {},
  methods: {
    /**
     * 清空数据
     */
    clearData: function() {
      this.inputModel = {
        config: {
          abnormalNum: 0
        }
      };
    },
    /**
     * 初始化数据
     */
    initData: function(inputModel) {
      this.inputModel = JSON.parse(JSON.stringify(inputModel)) ;
    },
    show: function() {
      this.visible = true;
      this.clearData();
    },
    /**
     * 关闭弹出框
     */
    hide: function() {
      this.clearData();
      this.visible = false;
      this.$emit("hide");
    },

    /**
     * 处理提交
     */
    handleSubmit: function() {
      console.log(this.inputModel.config.abnormalNum);
      if (this.inputModel.config.abnormalNum == "") {
        this.$message.error("请输入销售阈值");
        this.$refs.salesInput.focus();
        return;
      }

      this.$emit("confirm", { inputModel: this.inputModel });
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
<style lang="less" scoped>
.short-input {
  .el-input {
    width: 130px;
    margin: 0 8px;
  }
}
</style>