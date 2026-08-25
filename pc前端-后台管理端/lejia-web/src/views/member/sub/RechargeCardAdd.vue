<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" @close="hide">
    <el-form :label-width="labelWidth" >
      <el-form-item label="面值" required>
        <el-input v-model="inputModel.cost" ref="costInput" maxlength="20" placeholder="请输入面值"
          v-on:input="limitInput($event,'cost')">
        </el-input>
      </el-form-item>
      <el-form-item label="生成数量" required>
        <el-input v-model="inputModel.num" ref="numInput" maxlength="20" placeholder="请输入生成数量"
          v-on:input="handleNumInput($event)">
        </el-input>
      </el-form-item>
      <el-form-item label="截止时间" required>
        <el-date-picker v-model="inputModel.deadline" type="datetime" ref="deadlineInput" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择截止时间"></el-date-picker>
      </el-form-item>
      <el-form-item label="卡类型" required>
        <el-select v-model="inputModel.type" ref="typeSelect" placeholder="选择卡类型">
          <el-option value="NORMAL" key="NORMAL" label="普通充值"></el-option>
          <el-option value="MSD" key="MSD" label="热力豆充值"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="标签" required  v-if="inputModel.type === 'MSD'">
        <el-select v-model="inputModel.tag" ref="tagSelect" placeholder="选择标签">
          <el-option :value="item.pkey" :key="item.pkey" :label="item.name" v-for="item in TagsList"></el-option>
        </el-select>
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
      labelWidth: '100px',
      visible: false,
      loading: false,
      TagsList: [],
      inputModel: {
        cost: '',
        num: '',
        deadline: '',
        type: "NORMAL",
        tag: "",
      },
    };
  },
  mounted() {},
  components: {},
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        cost: '',
        num: '',
        deadline: '',
        type: "NORMAL",
        tag: "",
      };
    },
    show: function () {
      this.visible = true;
      this.getTagsList();
    },
    /**
     * 获取热力豆用户标签
     */
    getTagsList() {
      const params = {types: "MSD"};
      axios
        .post(api.marketing.msdTagDrop, this.$qs.stringify(params))
        .then((response) => {
          this.TagsList = response
        });
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
      this.$emit('refresh');
    },
    handleNumInput: function (val) {
      val = val.replace(/[^0-9]/g, '');
      this.inputModel.num = val;
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if (!this.inputModel.cost) {
        this.$message.error('请输入面值');
        this.$refs.costInput.focus();
        return;
      }
      if (!this.inputModel.num) {
        this.$message.error('请输入生成数量');
        this.$refs.numInput.focus();
        return;
      }
      if (!this.inputModel.deadline) {
        this.$message.error('请选择截止时间');
        this.$refs.deadlineInput.focus();
        return;
      }
      
      if(!this.inputModel.type) {
        this.$message.error('请选择卡类型');
        this.$refs.typeSelect.focus();
      }

      if(this.inputModel.type == "MSD") {
        if (!this.inputModel.tag) {
        this.$message.error('请选择标签');
        this.$refs.tagSelect.focus();
        return;
      }
      }
      this.loading = true
      axios
        .post(api.marketing.rechargeCardAdd, this.$qs.stringify(this.inputModel))
        .then(() => {
          this.$message.success('生成成功');
          this.hide()
        })
        .finally(() => {
          this.loading = false
        })
    },
    /**限制input只能输入数字和小数点后2位 */
    limitInput(value, dataName) {
      value = value.replace(/[^\d.]/g, ''); //清除"数字"和"."以外的字符
      value = value.replace(/^\./g, ''); //验证第一个字符是数字
      value = value.replace(/\.{2,}/g, ''); //只保留第一个, 清除多余的
      value = value.replace('.', '$#$').replace(/\./g, '').replace('$#$', '.');
      value = value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数
      this.inputModel[dataName] = value;
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
/deep/ .el-form {
  overflow: visible !important;
}
</style>