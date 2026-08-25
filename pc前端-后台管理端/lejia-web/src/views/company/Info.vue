<!-- 
@name: Info.vue 
@description: 公司信息
@author: sx
@url: /company/info
@date: 2020/06/24
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- form表单 -->
    <div class="form-box">
      <el-form>
        <el-form-item label="名称" :label-width="labelWidth">
          <el-input v-model="inputModel.name" ref="nameInput" :disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="管理员" :label-width="labelWidth">
          <el-input v-model="inputModel.manager" ref="managerInput" :disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="登录手机" :label-width="labelWidth">
          <el-input v-model="inputModel.mobile" ref="mobileInput" :disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="地址" :label-width="labelWidth">
          <el-input v-model="inputModel.addr" ref="addrInput" :disabled="disabled"></el-input>
        </el-form-item>
      </el-form>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="inputModel.markets" :loading="loading" border style="width: 100%">
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="管理员" prop="manager"></el-table-column>
        <el-table-column label="手机号码" prop="mobile"></el-table-column>
        <el-table-column label="售后电话" prop="tel"></el-table-column>
        <el-table-column label="编码" prop="code"></el-table-column>
        <el-table-column label="地址" prop="config.addr"></el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import qs from "qs";

export default {
  data() {
    return {
      labelWidth: "140px",
      disabled: true,
      loading: false,
      inputModel: {},
    };
  },
  components: {},

  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    }
  },
  mounted() {
    this.getData();
  },
  methods: {
    /**
     * 获取列表
     */
    getData: function() {
      this.loading = true;
      const params = {
        // pkey: this.$store.state.userinfo.orgs[0].pkey
        pkey: this.$store.state.marketPkey
      };

      axios.post(api.market.getComInfo, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.inputModel = response;
          this.loading = false;
        });
    }
  }
};
</script>