 <template lang="html">
    <div class="table-container">
      <h1 class="title">
        {{ title }}
      </h1>
      <!-- 搜索栏 -->
      <div class="search-box">
        <!-- 搜索表单 -->
        <div class="search-box-form">
          <el-select v-model="enabled" @change="handleChange" placeholder="请选择状态">
            <el-option  value="" label="全部" ></el-option>
            <el-option  :value="true" label="启用" ></el-option>
            <el-option  :value="false" label="禁用" ></el-option>
          </el-select>
          <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
          </search-bar>
        </div>
        <!-- 操作按钮 -->
        <div class="search-box-button">
          <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
            新增
          </el-button>
        </div>
      </div>
      <!-- 表格框 -->
      <div class="table-box">
        <el-table :data="tableData" :loading="loading" border style="width: 100%" class="table-fixed">
          <el-table-column label="供应商" prop="name" align="center"></el-table-column>
          <el-table-column label="手机号" prop="mobile" align="center"></el-table-column>
          <el-table-column label="创建时间" prop="createdTime" align="center"></el-table-column>
          <el-table-column label="状态" prop="enabled"  align="center">
            <template slot-scope="scope">
              <el-switch active-color="#13ce66" v-model="scope.row.enabled" @change="handleStatus(scope.row.enabled,scope.row.pkey)"></el-switch>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)" v-if="!scope.row.enabled">
                <el-button slot="reference" size="mini" type="danger" style="margin-left: 10px;"> 删除 </el-button>
              </el-popconfirm>
              <el-button v-else size="mini" type="danger" @click="tipClick(scope.row)"> 删除 </el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 页码 -->
        <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
          :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
      </div>
    </div>
</template>
<script>
import qs from "qs";

export default {
  data() {
    return {
      loading: false,
      searchKey: "name",
      selectOptions: [
        {
          name: "供应商",
          key: "name",
        },
        {
          name: "手机号码",
          key: "mobile",
        },
      ],
      enabled: "",
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: "", // 搜索关键字
      total: 0, //总页数
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
    },
  },
  mounted() {
    this.getData();
  },
  methods: {
    /**
     * 页码改变事件
     */
    handleCurrentChange(val) {
      this.page = val;
      this.loading = true;
      this.getData();
    },
    /**
     * 开始搜索
     */
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    /**
     * @desc 筛选
     */
    handleChange() {
      this.page = 1;
      this.getData();
    },
    /**
     * 点击新增
     */
    handelAdd: function () {
      this.$router.push({
        path: "/vendor/supplierManager/add",
      });
    },
    /**
     * 点击修改
     */
    handleEdit: function (row) {
      this.$router.push({
        path: "/vendor/supplierManager/edit",
        query: {
          pkey: row.pkey,
        },
      });
      localStorage.setItem("supplierManagerPkey", row.pkey);
    },
    /**
     * 删除
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.vendor.supplierDel, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success("删除成功");
          this.getData();
        });
    },
    tipClick: function (row) {
      if(row.enabled){
        this.$message.error("启用状态，不能删除");
        return;
      }
    },
    /**
     * 启停状态
     * @param  {[type]} status [新状态值]
     * @param  {[type]} pkey   [记录的pkey]
     * @return {[type]}        [description]
     */
    handleStatus: function (status, pkey) {
      let url = api.vendor.supplierEnable,
        text = "",
        params = {
          pkey: pkey,
          enabled: status,
        };
      if (status) {
        text = "启用";
      } else {
        text = "停用";
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          if (response) {
            this.$message.success(text + "成功");
          }
          this.getData();
        })
        .catch(() => {
          this.getData();
        });
    },

    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        enabled: this.enabled,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.vendor.supplierQuery, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
  },
};
</script>
<style lang="less" scoped>
</style>