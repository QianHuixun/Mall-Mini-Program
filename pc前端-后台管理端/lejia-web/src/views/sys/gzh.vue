<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <div class="search-box-form">
        <el-select v-model="search.enabled" clearable @change="handleChange" placeholder="请选择状态">
          <el-option label="已绑定" value="true"></el-option>
          <el-option label="未绑定" value="false"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入会员名称" :select-options="selectOptions">
        </search-bar>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%" class="table-fixed">
        <el-table-column label="昵称" prop="name" min-width="120" align="center"></el-table-column>
        <el-table-column label="手机号码" prop="mobile" min-width="120" align="center"></el-table-column>
        <el-table-column label="是否推送" prop="enabled" min-width="120" align="center">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.enabled" @change="handleEnabledChange(scope.row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" prop="createdTime" min-width="120" align="center"></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import qs from "qs";
export default {
  data() {
    return {
      loading: false,
      page: 1,
      pagesize: 10,
      search: {
        name: '',
        enabled: '',
      },
      tableData: [],
      total: 0,
      selectOptions: [
        {
          name: '昵称',
          key: 'name',
        },
      ],
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
    this.getData()
  },
  methods: {
    getData () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        enabled: this.search.enabled,
        name: this.search.name,
      };
      axios.post(api.sys.gzhQuery, qs.stringify(params), {
        headers: {
          Authorization: this.$store.state.token,
        },
      }).then(res => {
        console.log(res);
        this.tableData = res.content
        this.total = res.total
      })
    },
    /**搜索条件改变 */
    handleChange: function () {
      this.getData();
    },
    /**
     * 开始搜索
     */
    startSearch({ key, keywords }) {
      this.search.name = keywords;
      this.page = 1;
      this.getData();
    },
    handleEnabledChange({enabled, pkey}) {
      axios.post(enabled ? api.sys.gzhStart : api.sys.gzhStop, qs.stringify({pkey}))
        .then(res => {
          this.getData()
          this.$message.success(`${enabled ? '开启' : '关闭'}推送成功`)
        })
        .catch(error => {
          this.getData()
        })
    }
  }
}
</script>

<style scoped>
.table-container > .search-box > .search-box-form > .el-select {
  width: 200px !important;
}
</style>