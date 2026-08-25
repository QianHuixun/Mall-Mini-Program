<!-- 
@name:MembersCard.vue 
@description: 会员明细卡片弹窗（积分记录与余额）
@author: crj
@date: 2020/08/14
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" width="400px">
    <el-tabs v-model="activeName" @tab-click="handleClick($event)">
      <el-tab-pane v-for="(item,index) in tabList" :label="item.name" :name="item.pkey" :key="index">
        <ul v-infinite-scroll="getData" infinite-scroll-disabled="disabled">
          <!-- v-for="(item,index) in tabList" -->
          <li class="border-line" v-for="(item,dataIndex) in dataList" :key="dataIndex">
            <div class="left-box">
              <div class="title">{{item.sourceName}}</div>
              <div class="time">{{item.createdTime}}</div>
              <div class="number">{{type?'余额：'+item.balance:'积分余额：'+item.balance}}</div>
            </div>
            <div :class="!item.direct?'lose':'gain'">
              <!-- {{item.soure=='POINTS_EMPTY'?`+${item.price}`:`-${item.price}`}} -->
              {{item.direct?'+':'-'}}{{type?item.comms:item.points}}
            </div>
          </li>
            <p v-if="loading">加载中...</p>
            <p v-if="!loading&&noMore">没有更多了</p>
        </ul>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script>
import qs from "qs";
export default {
  data() {
    return {
      visible: false,
      title: "",
      activeName: "",
      tabList: [],
      dataList: [],
      total: 0,
      loading: false, //控制加载提示
      noMore: false, //控制有无数据
      //接口配置
      url: "", //路径
      params: {}, //参数
      page: 1, //显示页码
      pageSize: 6, //表格一页显示几条
      total: 0,
      typeField: "",
      type:0
    };
  },
  mounted() {},
  computed: {
    disabled() {
      return this.loading || this.noMore;
    }
  },
  methods: {
    /**
     * tabs切换事件
     */
    handleClick: function(e) {
      this.noMore=false,
      this.page = 1;
      if (e.name == "ALL") this.params[this.typeField] = "";
      else {
        if (this.typeField == "source") {
          this.params[this.typeField] = e.name;
        } else {
          if (e.name == "true") this.params[this.typeField] = true;
          else this.params[this.typeField] = false;
        }
      }

      this.dataList = [];
      this.getData();
    },
    clearData: function() {
      this.noMore=false,
      (this.title = ""),
        (this.url = ""),
        (this.params = {}),
        (this.total = 0),
        (this.page = 1),
        (this.dataList = {});
    },
    show: function(row, { tabs, title, typeField }) {
      this.activeName = "ALL";
      console.log(this.activeName);
      this.visible = true;
      this.clearData();
      this.title = title;
      this.tabList = tabs;
      const query = this.judgement({
        pkey: row.pkey,
        typeField
      });
      this.getData(query);
    },
    /**
     * 关闭弹出框
     */
    hide: function() {
      this.clearData();
      this.visible = false;
      this.$emit("hide");
    },
    /**
     * 获取数据
     */
    getData(query = "") {
      this.loading = true;
      let url, params;
      query
        ? (url = query.url) &&
          (params = {
            ...query.params,
            page: this.page - 1,
            pagesize: this.pageSize
          })
        : (url = this.url) &&
          (params = {
            ...this.params,
            page: this.page - 1,
            pagesize: this.pageSize
          });

      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          if (response.content.length) {
            if (this.page == 1) {
              this.total = response.total;
              this.dataList = response.content;
            } else this.dataList = this.dataList.concat(response.content);
          } else {
            this.noMore = true;
            if (this.page == 1) this.total = response.total;
          }
          this.page++;
          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    /**
     *
     * 判断接口路径以及接口参数，分页变量
     */
    judgement({ pkey, typeField }) {
      let title = this.title,
        url,
        params;
      switch (title) {
        case "积分记录":
          (url = api.marketing.queryPoint),
            (params = {
              member: pkey
            });
          this.type =0;

          break;
        case "余额":
          (url = api.data.balanceList),
            (params = {
              member: pkey
            });
          this.type =1;
          break;
      }
      (this.url = url), (this.params = params), (this.typeField = typeField);
      let query = {
        url,
        params
      };
      query[typeField] = "";

      return query;
    }
  }
};
</script>

<style lang="less" scoped>
ul {
  list-style: none;
  padding-left: 0;
  overflow: auto;
  height: 352px;
  .border-line {
    border-bottom: solid 1px #e4e7ed;
  }
  li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px 10px 20px;
    margin-bottom: 15px;
    .left-box {
      color: #303133;
      max-width: 250px;
      .title {
        font-weight: 600;
        font-size: 16px;
        line-height: 22px;
      }
      .time,
      .number {
        color: #909399;
        font-size: 14px;
        line-height: 20px;
      }
    }
    .lose,
    .gain {
      font-weight: 500;
      font-size: 20px;
    }
    .gain {
      color: #409eff;
    }
    .lose {
      color: #606266;
    }
  }
  p {
    display: block;
    width: 100%;
    text-align: center;
    padding-bottom: 20px;
  }
}
</style>
<style lang="less" scoped>
/deep/ .is-active {
  color: #409eff;
}
/deep/ .el-tabs__active-bar {
  background-color: #409eff;
}
/deep/ .el-tabs__item:hover {
  color:#409eff;
}
/deep/ .el-dialog__body {
  max-height: 444px;
}
</style>