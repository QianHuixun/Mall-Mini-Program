<!-- 
@name: Lottery.vue 
@description: 抽奖活动配置
@author: sx
@url: /activity/lottery
@date: 2020/07/07
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <!-- <div class="search-box"> -->
    <!-- 搜索表单 -->
    <!-- <div class="search-box-form">
      </div> -->
    <!-- 操作按钮 -->
    <!-- <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelRule">
          修改规则
        </el-button>
      </div> -->
    <!-- </div> -->
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="奖品图片" prop="name">
          <template slot-scope="scope">
            <img :src="scope.row.photo ? scope.row.photo : noPic" width="35px" height="35px" />
          </template>
        </el-table-column>
        <el-table-column label="奖品名称" prop="name"></el-table-column>
        <el-table-column label="类型" prop="ptype">
          <template slot-scope="scope">
            <span v-if="scope.row.ptype=='INTEGRAL_PRIZE'">积分</span>
            <span v-if="scope.row.ptype=='CARD_PRIZE'">优惠券</span>
            <span v-if="scope.row.ptype=='GIFT_PRIZE'">实物</span>
            <span v-if="scope.row.ptype=='THANK_PRIZE'">谢谢惠顾</span>
          </template>
        </el-table-column>
        <el-table-column label="中奖概率" prop="probability"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 组件 -->
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
    <rule-edit-comp ref="RuleEditComp"></rule-edit-comp>
  </div>
</template>
<script>
  import qs from "qs";
  import EditComp from "./sub/LotteryEdit.vue";
  import RuleEditComp from "./sub/LotteryRule.vue";
  export default {
    data() {
      return {
        loading: false,
        noPic: require("@/assets/images/no-pic.jpg"),
        tableData: []
      };
    },
    components: {
      EditComp,
      RuleEditComp
    },
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
       * 修改规则
       */
      // handelRule: function() {
      //   this.$refs.RuleEditComp.show();
      // },
      /**
       * 点击修改
       */
      handleEdit: function (row) {
        this.$refs.EditComp.show({
          row: row
        });
      },
      /**
       * 获取列表
       */
      getData: function () {
        this.loading = true;
        const params = {};
        axios.post(api.marketing.queryLottery, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            this.tableData = response;

            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
      }
    }
  };
</script>