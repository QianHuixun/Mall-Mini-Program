<!-- 
@name: AreaAds.vue 
@description: 专区广告
@author: 池仁杰
@url: /popup/areaAds
@date: 2021/010/14
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
      <span class="red-font" style="padding-left:20px;font-size:18px"
        v-if="$store.state.userIdentity==2&&flag">注：运营端已开启统一配置</span>
      <el-radio-group v-model="flag" @change="handelConfigChange" v-if="$store.state.userIdentity==1">
        <el-radio-button :label="true">统一配置</el-radio-button>
        <el-radio-button :label="false">市场自定义</el-radio-button>
      </el-radio-group>
    </h1>
    <el-radio-group v-model="position" @change="getData">
      <el-radio-button key="ADVERT_POSITION_INDEX" label="ADVERT_POSITION_INDEX">首页</el-radio-button>
      <!-- <el-radio-button key="ADVERT_POSITION_MEMBER" label="ADVERT_POSITION_MEMBER">会员</el-radio-button> -->
      <!-- <el-radio-button key="ADVERT_POSITION_POVERTY_ALLEVIATION" label="ADVERT_POSITION_POVERTY_ALLEVIATION">扶贫专区</el-radio-button> -->
      <!-- <el-radio-button key="ADVERT_POSITION_SHARE" label="ADVERT_POSITION_SHARE">分享专区</el-radio-button> -->
      <el-radio-button key="ADVERT_POSITION_COM" label="ADVERT_POSITION_COM">组合广告</el-radio-button>
      <el-radio-button key="ADVERT_POSITION_SALE" label="ADVERT_POSITION_SALE">预售专区</el-radio-button>
      <!-- <el-radio-button key="ADVERT_POSITION_SPECIAL" label="ADVERT_POSITION_SPECIAL">抢购</el-radio-button> -->
      <!-- <el-radio-button key="ADVERT_POSITION_CUT" label="ADVERT_POSITION_CUT">砍价</el-radio-button> -->
      <!-- <el-radio-button key="ADVERT_POSITION_COLLAGE" label="ADVERT_POSITION_COLLAGE">拼团</el-radio-button> -->
      <el-radio-button key="ADVERT_POSITION_COOKFD" label="ADVERT_POSITION_COOKFD">菜谱</el-radio-button>
    </el-radio-group>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="searchData.farmers" @change="handleChange()" placeholder="请选择市场"
          v-if="$store.state.userIdentity==1" multiple collapse-tags clearable>
          <el-option v-for="(item,index) in marketList" :value="item.pkey" :label="item.name" :key="index"></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增广告
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column v-if="position == 'ADVERT_POSITION_COM'" label="展示位置" prop="locationType" key="locationType">
          <template slot-scope="scope">
            {{ locationType[scope.row.locationType] }}
          </template>
        </el-table-column>
        <el-table-column label="名称" prop="name" key="name"></el-table-column>
        <el-table-column label="广告图" prop="photo"  key="photo">
          <template slot-scope="scope">
            <img :src="scope.row.photo ? scope.row.photo : noPic" width="35px" height="35px" />
          </template>
        </el-table-column>
        <el-table-column label="点击效果" prop="urlType"  key="urlType">
          <template slot-scope="scope">
            {{ urlTypeObj[scope.row.urlType] }}
          </template>
        </el-table-column>
        <el-table-column label="内容" prop="objKey"  key="objKey">
          <template slot-scope="scope">
            {{ scope.row.objKeyName ? scope.row.objKeyName : (scope.row.urlType=="GOODS" ? (scope.row.goodsName||'无') : (scope.row.urlType=="ACTIVITY" ? (scope.row.activityName||'无') : (scope.row.objKey||'无'))) }}
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sort"  key="sort"></el-table-column>
        <el-table-column label="可见用户" v-if="position == 'ADVERT_POSITION_COM'" prop="visibleRangeName"  key="visibleRangeName"></el-table-column>
        <el-table-column label="投放市场" prop="farmersName" v-if="$store.state.userIdentity==1"  key="farmersName">
          <template slot-scope="scope">
            <span v-for="(item,index) in scope.row.farmersName" :key="index">{{item}} <span v-if="index!=scope.row.farmersName.length-1">；</span></span>
          </template>
        </el-table-column>
        <el-table-column v-show="position == 'ADVERT_POSITION_COM'" label="状态" prop="enabled" key="enabled">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.enabled" active-color="#13ce66" @change="handleEnable(scope.row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdTime" key="createdTime"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
              <el-button slot="reference" size="mini" type="danger">
                删除
              </el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <!-- 组件 -->
    <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
  </div>
</template>
<script>
import qs from 'qs';
import utils from '@/assets/js/utils';
import AddComp from './sub/areaAds/AdsAdd.vue';
import EditComp from './sub/areaAds/AdsEdit.vue';
import dropdown from '@/assets/js/dropdown';

export default {
  data() {
    return {
      urlTypeObj: utils.urlTypeObj(),
      noPic: require('@/assets/images/no-pic.jpg'),
      loading: false,
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数
      position: 'ADVERT_POSITION_INDEX',
      isOperation: false,
      marketList: [],
      searchData: {
        farmers: [],
      },
      flag: false,
      locationType: {
        LEFT: '左',
        UPPERRIGHT: '右上',
        CEZONTER: '中',
        LOWERRIGHT: '右下',
      }
    };
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
    if (this.$store.state.userIdentity == 1) {
      this.getMarketData();
    }
    this.getConfig();
  },
  components: {
    AddComp,
    EditComp,
  },
  methods: {
    /**
     * @desc 获取市场下拉列表
     */
    getMarketData() {
      // dropdown.getMarket().then(result => {
      //   this.marketList = result.content;
      // });
      axios.post(api.dropdown.newMarketList).then((res) => {
        this.marketList = res;
      });
    },
    getConfig() {
      let params = {
        pkey: 'ADVERTISE_MANAGER_DEPLOY',
      };
      axios
        .post(api.common.queryConfig, qs.stringify(params))
        .then((response) => {
          if (response.hasOwnProperty('result')) {
            this.flag = response.result;
          } else {
            this.flag = response;
          }
        });
    },
    /**
     * @desc 广告配置发生改变
     */
    handelConfigChange() {
      let params = {
        pkey: 'ADVERTISE_MANAGER_DEPLOY',
        flag: this.flag,
      };
      axios
        .post(api.common.updConfig, qs.stringify(params))
        .then((response) => {
          this.$message.success('广告配置修改成功');
          this.getConfig();
        });
    },
    /**
     * 点击新增
     */
    handelAdd: function () {
      this.$refs.AddComp.show({
        position: this.position,
      });
    },
    /**
     * 点击修改
     */
    handleEdit: function (row) {
      this.$refs.EditComp.show({
        row: row,
      });
    },
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.mall.delImg, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success('删除成功');
          this.getData();
        });
    },
    handleEnable({pkey, enabled}) {
      const url = enabled ? api.mall.startImg : api.mall.stopImg
      const params = {pkey, enabled}
      axios.post(url, this.$qs.stringify(params))
        .then(res => {
          console.log(res);
          this.$message.success('切换成功')
          this.getData()
        })
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
     * @desc 筛选
     */
    handleChange() {
      this.page = 1;
      this.getData();
    },
    /**
     * 获取列表
     */
    getData: function () {
      this.loading = true;
      const params = {
          position: this.position,
          farmers: this.searchData.farmers.join(','),
          page: this.page - 1,
          pagesize: this.pageSize,
        },
        url =
          this.$store.state.userIdentity == 1
            ? api.ads.queryAreaList
            : api.mall.queryImg;
      axios
        .post(url, qs.stringify(params), {
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
.title {
  .el-radio-group {
    margin-left: 20px;

    /deep/.el-radio-button__orig-radio:checked + .el-radio-button__inner {
      color: #fff;
      background-color: #1abc9c;
      border-color: #1abc9c;
      box-shadow: -1px 0 0 0 #1abc9c;
    }

    /deep/.el-radio-button .el-radio-button__inner {
      padding: 6px 10px;
    }
  }
}

.red-font {
  color: #f56c6c;
}

.search-box-form {
  /deep/ .el-select {
    width: 200px !important;
  }
}
</style>