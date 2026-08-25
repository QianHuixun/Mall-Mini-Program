<!--
@name: Winning.vue
@description: 中奖清单
@author: sx
@url: /activity/winning
@date: 2020/07/07
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="data-box">
      <el-row>
        <el-col :span="8" class="data-col" v-for="(item,index) in numData" :key="index">
          <p>{{ item.title }}</p>
          <span>{{ item.num }}</span>
        </el-col>
      </el-row>
    </div>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select
          v-model="status"
          @change="handleChange"
          placeholder="选择状态"
          clearable
        >
          <el-option
            :value="item.pkey"
            :key="index"
            :label="item.name"
            v-for="(item, index) in statusList"
          ></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button"></div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="奖品名称" prop="name"></el-table-column>
        <el-table-column label="类型" prop="ptype">
          <template slot-scope="scope">
            <span v-if="scope.row.ptype == 'INTEGRAL_PRIZE'">积分</span>
            <span v-if="scope.row.ptype == 'CARD_PRIZE'">优惠券</span>
            <span v-if="scope.row.ptype == 'GIFT_PRIZE'">实物</span>
            <span v-if="scope.row.ptype == 'THANK_PRIZE'">谢谢惠顾</span>
          </template>
        </el-table-column>
        <el-table-column label="中奖人" prop="memberName"></el-table-column>
        <el-table-column label="领奖信息" prop="addr"></el-table-column>
        <el-table-column label="中奖时间" prop="createdTime"></el-table-column>
        <el-table-column label="状态" prop="status">
          <template slot-scope="scope">
            <span v-if="scope.row.status == 'NOT_ISSUED'">未发货</span>
            <span v-if="scope.row.status == 'ISSUED'">已发货</span>
          </template>
        </el-table-column>
        <el-table-column label="物流信息" prop="logistics">
          <template slot-scope="scope">
            <span
              >{{ scope.row.logistics
              }}{{
                scope.row.logistics != null && scope.row.logistics != ""
                  ? "单号"
                  : ""
              }}</span
            >
            <span>{{ scope.row.express }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发奖时间" prop="sendTime"></el-table-column>
        <el-table-column label="操作" width="100" v-if="!isOnlyBrowse">
          <template
            slot-scope="scope"
            v-if="scope.row.status == 'NOT_ISSUED' && scope.row.addr"
          >
            <el-button size="mini" type="danger" @click="handleUpd(scope.row)">
              发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination
        hide-on-single-page
        background
        layout="prev, pager, next"
        :total="total"
        :current-page="page"
        :page-size="pageSize"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </div>
    <delivery-add ref="DeliveryAdd" @refresh="getData"></delivery-add>
  </div>
</template>
<script>
import DeliveryAdd from "./sub/WinningDeliveryAdd";
import qs from "qs";
export default {
  data() {
    return {
      loading: false,
      numData: [],
      tableData: [],
      statusList: [
        //NOT_ISSUED(0, "初始"), ISSUED(1, "已发");
        {
          pkey: "",
          name: "发货状态"
        },
        {
          pkey: "NOT_ISSUED",
          name: "未发货"
        },
        {
          pkey: "ISSUED",
          name: "已发货"
        }
      ],
      status: "",
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0 //总页数
    };
  },
  mounted() {
    this.getData();
    this.getNumData();
  },
  components: {
    DeliveryAdd
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
    /**是否为仅浏览 */
    isOnlyBrowse() {
      let hasBrowse = false;
      if (this.$store.state.activeName) {
        hasBrowse =
          this.$store.state.activeName.indexOf("仅浏览") > 0 ? true : false;
      }
      return hasBrowse;
    }
  },
  methods: {
    handleChange: function() {
      this.page = 1;
      this.getData();
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
    /**
     * 发货
     */
    handleUpd: function(row) {
      this.$refs.DeliveryAdd.show({
        row: row
      });
    },
    /**
     * 获取中奖记录次数
     */
    getNumData: function() {
      const params = {};
      axios
        .post(api.marketing.queryWinningData, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.numData = response;
        });
    },
    /**
     * 获取列表
     */
    getData: function() {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        status: this.status
      };

      axios
        .post(api.marketing.queryWinning, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          this.tableData = response.content;
          this.total = response.total;

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    }
  }
};
</script>
<style lang="less" scoped>
.data-box {
  margin: 10px;

  .el-row {
    display: flex;

    .data-col {
      flex: 1;
      margin: 10px;
      padding: 20px 0;
      line-height: 25px;
      border-radius: 5px;
      font-size: 20px;
      text-align: center;
      background: #d3dce6;

      p {
        font-size: 14px;
      }

      span {
        font-size: 20px;
      }
    }
  }
}
</style>
