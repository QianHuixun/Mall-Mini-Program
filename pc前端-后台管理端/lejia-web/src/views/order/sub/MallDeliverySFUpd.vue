<!--
* @description 顺丰发货
* @fileName MallDeliverySFUpd.vue
* @author zs
* @date 2024/12/10
!-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="上门取件时间" :label-width="labelWidth">
       <el-date-picker v-model="pickupTime" format="yyyy-MM-dd HH:mm:ss" value-format="yyyy-MM-dd HH:mm:ss" type="datetime"
          placeholder="上门取件时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="寄托物" :label-width="labelWidth">
        <el-tag :class="selectItem === item ? 'cust-tag select-tag' :'cust-tag'" :closable="index>6" v-for="(item,index) in items" :key="item" effect="plain" 
        @click="tagClick(item)" @close="handleClose(item)">
          {{ item }}
        </el-tag>
        <el-input class="input-new-tag" v-if="inputVisible" v-model="inputValue" ref="saveTagInput" size="small" @keyup.enter.native="handleInputConfirm"
          autosize type="textarea" maxlength="128"
          @blur="handleInputConfirm">
        </el-input>
        <el-button v-else class="button-new-tag" size="small" @click="showInput" v-show="items.length==7">自定义</el-button>
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
      items: ["生鲜", "水果", "农产品", "酒水", "茶叶", "水产", "日用品"],
      selectItem: "",
      inputValue: "",
      inputVisible: false,
      pickupTime: "",
    };
  },
  mounted() {},
  methods: {
    // 选中
    tagClick(item) {
      this.selectItem = item;
    },
    // 删除
    handleClose(item) {
      this.items.splice(this.items.indexOf(item), 1);
    },

    showInput() {
      this.inputVisible = true;
      this.$nextTick((_) => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },

    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.items.push(inputValue);
        this.selectItem = inputValue;
      }
      this.inputVisible = false;
      this.inputValue = "";
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.items = ["生鲜", "水果", "农产品", "酒水", "茶叶", "水产", "日用品"];
      this.selectItem = "";
      this.pickupTime = "";
    },
    show: function () {
      this.visible = true;
      this.clearData();
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
      if (!this.pickupTime) {
        this.$message.error("请选择上门取件时间");
        return;
      }

      if (!this.selectItem) {
        this.$message.error("请选择寄托物内容");
        return;
      }

      this.$emit("confirm", {
        inputModel: {
          pickupTime: this.pickupTime,
          sendContent: this.selectItem,
        },
      });
    },
  },
  props: {
    title: {
      type: String,
      default: "发货",
    },
  },
};
</script>

<style lang="less" scoped>
.cust-tag {
  padding: 3px 12px;
  height: auto;
  margin-right: 12px;
  margin-bottom: 12px;
  border-color: #d3d4d6;
  color: #909399;
}

.select-tag {
  border-color: #b3d8ff;
  color: #409eff;
}

.input-new-tag {
  width: 60px;
}
.cust-tag{
  white-space: normal;
  word-wrap: break-word;
}
.el-textarea {
  margin-right: 12px;
  margin-bottom: 12px;
  min-width: 160px;
  /deep/ .el-textarea__inner {
    min-height: 38px !important;
  }
}
</style>
