<!--
 * @Author: 沙晓
 * @Date: 2024-04-07 11:36:15
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-04-07 14:52:36
 * @Description: 版本屏蔽设置
 * @FilePath: /lejia-web/src/views/sys/Version.vue
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="table-form">
      <el-form ref="form" :model="inputModel" label-width="120px">
        <el-form-item label="商城端版本号">
          <el-input v-model="inputModel.userVersion" placeholder="请输入商城端版本号"></el-input>
        </el-form-item>
        <el-form-item label="商户端版本号">
          <el-input v-model="inputModel.vendorVersion" placeholder="请输入商户端版本号"></el-input>
        </el-form-item>
        <el-form-item label="骑手端版本号">
          <el-input v-model="inputModel.courierVersion" placeholder="请输入骑手端版本号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit" :loading="loading">保存</el-button>
          <el-button @click="getData" :loading="loading">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
  import qs from "qs";
  export default {
    data() {
      return {
        inputModel: {},
        loading: false,
      }
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
    mounted() {
      this.getData();
    },
    methods: {
      getData() {
        axios.post(api.sys.getVersion, qs.stringify({}), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            this.inputModel = response;
          });
      },
      onSubmit() {
        this.loading = true;
        const url = api.sys.saveVersion,params = this.inputModel;
        axios.post(url, params, {
            headers: {
              Authorization: this.$store.state.token,
              "Content-Type": "application/json"
            }
          })
          .then(() => {
            this.$message.success("保存成功");
            this.loading = false;
          });
      },
    }
  }
</script>
<style scoped>
.table-form {
  margin-top: 20px;
  width: 500px;
}
</style>