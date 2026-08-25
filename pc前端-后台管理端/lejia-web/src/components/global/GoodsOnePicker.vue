<template lang="html">
  <div class="goods-picker">
    <el-input ref="nameInput" placeholder="请选择" v-model="goodsName" :disabled="true">
      <el-button slot="append" icon="el-icon-plus" @click="show"></el-button>
    </el-input>
    <el-dialog title="商品选择" center :visible.sync="visible" :closeOnClickModal="false" append-to-body>
      <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
      <div class="table-box" style="height: 500px; ">
        <el-table ref="table" :data="tableData" :loading="loading" border style="width: 100%" @select="handleSelectionChange">
          <el-table-column type="selection" width="55">
          </el-table-column>
          <el-table-column label="商品名称" prop="title"></el-table-column>
          <el-table-column label="商品图片" prop="photo1">
            <template slot-scope="scope">
              <img :src="scope.row.photo1 ? scope.row.photo1 : noPic" width="35px" height="35px" />
            </template>
          </el-table-column>
          <el-table-column label="商品类别" prop="mtypeName">
          </el-table-column>
          <el-table-column label="创建时间" prop="createdTime"></el-table-column>
        </el-table>
         <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button size="medium" @click="hide">
          取 消
        </el-button>
        <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import qs from "qs";
export default {
  data() {
    return {
      noPic: require("@/assets/images/no-pic.jpg"),
      loading: false,
      visible: false,
      goodsRow: [],
      goodsKey: "",
      goodsName: "",
      searchKey: "name",
      selectOptions: [
        { name: "商品名称", key: "title" }
      ],
      tableData: [],
      page: 1, //显示页码
      pageSize: 5, //表格一页显示几条     
      keywords: "", // 搜索关键字
      total: 0 //总页数
    };
  },
  mounted() {
    this.getData();
  },
  components: {},
  methods: {
    /**
     * 关闭弹窗
     */
    hide: function() {
      this.visible = false;
    },
    /**
     * 显示弹窗
     */
    show: function() {
      this.visible = true;
      this.$nextTick(() => {
        this.$refs.table.clearSelection();
        if (this.goodsKey != "") {
          this.goodsRow = this.tableData.filter(item => item.pkey == this.goodsKey);
          this.$refs.table.toggleRowSelection(this.goodsRow[0]);
        }
      });

      this.keywords = "";
    },
    /**
     * 更新选中项
     */
    updateGoods: function({ goodsInfo }) {
      this.goodsName = goodsInfo.name;
      this.goodsKey = goodsInfo.pkey;
    },
    /**
     * 表格勾选 获取勾选值
     */
    handleSelectionChange(selection, row) {
      if (this.goodsRow.pkey != row.pkey) {
        this.$refs.table.clearSelection();
        this.$refs.table.toggleRowSelection(row);
        this.goodsRow = [row];
      } else {
        this.goodsRow = [];
      }

    },
    /**
     * 提交
     */
    handleSubmit: function() {
      if (this.goodsRow.length == 0) {
        this.$message.error("请选择商品");
        return;
      }
      this.goodsName = this.goodsRow[0].title;
      this.$emit("handle", this.goodsRow[0].pkey);
      this.visible = false;
    },
    getData: function() {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize
      };
      params[this.searchKey] = this.keywords;
      axios.post(api.mall.queryGoods, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
            // "Content-Type": "application/json"
          }
        })
        .then(response => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
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
    startSearch: function({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
  }
}
</script>
<style lang="less" scoped>
/deep/.el-pagination {
    margin: 15px auto;
    text-align: center;
}
</style>