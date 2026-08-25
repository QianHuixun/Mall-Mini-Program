<template>
  <el-dialog title="专区显示名称" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form label-width="120px">
      <el-form-item label="专区显示名称">
        <el-input v-model="inputModel.name" placeholder="请输入专区显示名称" maxlength="10"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
        保 存
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: {
    mType: {
      type: String,
      default: '',
    }
  },
  data() {
    return {
      visible: false,
      loading: false,
      inputModel: {
        name: '',
      }
    };
  },
  methods: {
    show() {
      this.visible = true;
      this.getData()
    },
    hide() {
      this.visible = false;
      this.inputModel = {
        name: ''
      }
    },
    getData() {
      axios.post(api.jd.getSpecialZone)
        .then(res => {
          this.inputModel.name = res
        })
    },
    handleSubmit() {
      if(!this.inputModel.name) {
        return this.$message.warning('请输入专区名称')
      }
      const params = {
        jdGoodsName: this.inputModel.name
      };
      axios
        .post(api.jd.setSpecialZone, this.$qs.stringify(params))
        .then((res) => {
          if (res) {
            this.$message.success('保存成功');
            this.hide();
          }
        });
    },
  },
};
</script>
