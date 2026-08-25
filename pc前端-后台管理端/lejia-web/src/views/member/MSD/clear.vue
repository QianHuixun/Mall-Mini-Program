<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" @close="hide">
    <el-form>
      <el-form-item label="标签" :label-width="labelWidth" :required="true">
        <el-select v-model="tags" multiple collapse-tags clearable placeholder="请选择用户标签" @change="handleChange">
          <el-option  value="-1" label="全部" ></el-option>
          <el-option v-for="item in tagsList" :key="item.pkey" :value="item.pkey" :label="item.name" :disabled="disabled"></el-option>
        </el-select>
        <span>选择清空热力豆的用户标签</span>
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
      disabled: false,
      inputModel: {
        tags: [],
      },
      tags: [],
      tagsList: [],
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
        tags: [],
      };
      this.tags = []
    },
    /**
     * 初始化数据
     */
    getData: function () {
      axios
        .post(api.marketing.msdTagDrop)
        .then((response) => {
          this.tagsList = response
        });
    },
    show: function () {
      this.visible = true;
      this.getData();
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
      this.disabled = false;
      this.$emit('refresh');
    },
    handleChange(val) {
      if (val.includes('-1')) {
        this.farmers = ['-1'];
        this.disabled = true;
      } else {
        this.disabled = false;
      }
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if(!this.tags || !this.tags.length) {
        this.$message.error('请选择用户标签');
        return;
      }
      if(this.tags.includes('-1')) {
        this.inputModel.tags = this.tagsList.map(item => item.pkey)
      } else {
        this.inputModel.tags = this.tags
      }
      this.loading = true
      const params = {
        tags: this.inputModel.tags.join(',')
      }
      axios.post(api.marketing.msdBalanceClear, this.$qs.stringify(params))
        .then(res => {
          this.$message.success('清空成功！')
          this.hide()
          this.$emit('refresh')
        })
        .finally(() => {
          this.loading = false
        })
    },
  },
  props: {
    title: {
      type: String,
      default: '清空热力豆',
    },
  },
};
</script>
<style lang="less" scoped>
/deep/ .el-form {
  overflow: visible !important;
}
</style>