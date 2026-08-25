<!-- 
@name: Purchase.vue 
@description: 商户采购报表
@author: 池仁杰
@route: /data/Purchase
@date: 2021/10/14
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="searchData.dataEnums" @change="handleChange()" placeholder="时间">
          <el-option value="DAY" label="日"></el-option>
          <el-option value="MONTH" label="月"></el-option>
          <el-option value="SEASON" label="季度"></el-option>
          <el-option value="YEAR" label="年"></el-option>
        </el-select>
        <el-date-picker v-if="searchData.dataEnums=='DAY'" v-model="searchData.dayDate" type="daterange"
          :editable="false" @change="handleChange" range-separator="至" format="yyyy-MM-dd" value-format="yyyy-MM-dd"
          start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
        <el-date-picker v-else-if="searchData.dataEnums=='MONTH'" v-model="searchData.monthDate" type="monthrange"
          :editable="false" @change="handleChange" range-separator="至" format="yyyy-MM" value-format="yyyy-MM"
          start-placeholder="开始月份" end-placeholder="结束月份">
        </el-date-picker>
        <div class="season-range" v-else-if="searchData.dataEnums=='SEASON'" @mouseover="seasonOver"
          @mouseleave="seasonLeave">
          <season-picker ref="seasonStart" :time.sync="searchData.seasonStartDate" :disabledType="1"
            :disabledTime="seasonEndDate" placeholder="开始季度"></season-picker>
          <span>至</span>
          <season-picker ref="seasonEnd" :time.sync="searchData.seasonEndDate" :disabledType="2"
            :disabledTime="seasonStartDate" placeholder="结束季度"></season-picker>
          <span v-show="seasonCloseShow" class="el-icon-circle-close" @click="clearSeasonDate"></span>
        </div>
        <div class="year-range" v-else>
          <el-date-picker v-model="searchData.yearStartDate" type="year" :editable="false"
            @change="yearDateChange($event,true)" format="yyyy" value-format="yyyy" placeholder="开始年份"
            :picker-options="yearStartOption"></el-date-picker>
          <span>至</span>
          <el-date-picker v-model="searchData.yearEndDate" type="year" :editable="false"
            @change="yearDateChange($event,false)" format="yyyy" value-format="yyyy" placeholder="结束年份"
            :picker-options="yearEndOption"></el-date-picker>
        </div>
        <el-select v-model="searchData.vendorKeys" @change="handleChange()" placeholder="商户" clearable>
          <el-option v-for="(item,index) in merchantList" :value="item.pkey" :label="item.name" :key="index">
          </el-option>
        </el-select>
        <el-select v-model="searchData.status" @change="handleChange()" placeholder="结算状态">
          <el-option value="NOT_START" label="未结算"></el-option>
          <el-option value="SUCCESS" label="已结算"></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-download" size="medium" @click="handleExport">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="时间" prop="date"></el-table-column>
        <el-table-column label="商户名称" prop="name">
        </el-table-column>
        <el-table-column label="采购笔数" prop="num">
        </el-table-column>
        <el-table-column label="采购金额(元)" prop="amt">
        </el-table-column>
      </el-table>
      <div class="all-num">
        <span class="title">合计</span>
        <span>总采购笔数：{{purchaseNum}}笔</span>
        <span>总采购金额：{{purchaseAmt}}元</span>
      </div>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <!-- 组件 -->
    <!-- <add-comp ref="AddComp" @refresh="getData"></add-comp>
    <edit-comp ref="EditComp" @refresh="getData"></edit-comp> -->
  </div>
</template>
<script>
// import AddComp from "./sub/pointAds/AdsAdd.vue";
// import EditComp from "./sub/pointAds/AdsEdit.vue";
import SeasonPicker from '@/components/global/SeasonDatePicker.vue';
import utils from '@/assets/js/utils.js';
export default {
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      pageSize: 10,
      page: 1,
      searchData: {
        dataEnums: 'DAY',
        dayDate: [utils.getMonthAgoDate(), utils.getNowDate()],
        monthDate: utils.getMothRange(),
        yearStartDate: '',
        yearEndDate: '',
        status: 'SUCCESS',
        vendorKeys: [],
        seasonStartDate: '',
        seasonEndDate: '',
      },
      yearStartOption: {},
      yearEndOption: {},
      merchantList: [],
      seasonCloseShow: false,
      purchaseAmt: 0,
      purchaseNum: 0,
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
    seasonStartDate() {
      let date = '';
      if (this.searchData.seasonStartDate) {
        date = this.searchData.seasonStartDate.substring(
          0,
          this.searchData.seasonStartDate.indexOf('年')
        );
        let season = Number(
          this.searchData.seasonStartDate.substring(
            this.searchData.seasonStartDate.indexOf('年') + 1,
            this.searchData.seasonStartDate.indexOf('季')
          )
        );
        if (season == 1) {
          date = date + '-01-01';
        } else if (season == 2) {
          date = date + '-04-01';
        } else if (season == 3) {
          date = date + '-07-01';
        } else {
          date = date + '-10-01';
        }
      }
      return date;
    },
    seasonEndDate() {
      let date = '';
      if (this.searchData.seasonEndDate) {
        date = this.searchData.seasonEndDate.substring(
          0,
          this.searchData.seasonEndDate.indexOf('年')
        );
        let season = Number(
          this.searchData.seasonEndDate.substring(
            this.searchData.seasonEndDate.indexOf('年') + 1,
            this.searchData.seasonEndDate.indexOf('季')
          )
        );
        if (season == 1) {
          date = date + '-01-01';
        } else if (season == 2) {
          date = date + '-04-01';
        } else if (season == 3) {
          date = date + '-07-01';
        } else {
          date = date + '-10-01';
        }
      }
      return date;
    },
    seasonDate() {
      return {
        seasonStartDate: this.searchData.seasonStartDate,
        seasonEndDate: this.searchData.seasonEndDate,
      };
    },
  },
  watch: {
    seasonDate(newVal, oladVal) {
      if (newVal.seasonStartDate && newVal.seasonEndDate) {
        this.handleChange();
      }
    },
  },
  mounted() {
    this.getMerData();
    this.getData();
  },
  components: {
    // AddComp,
    // EditComp
    SeasonPicker,
  },
  methods: {
    seasonLeave() {
      this.seasonCloseShow = false;
    },
    /**
     * @desc 季节范围选择器鼠标移入
     */
    seasonOver() {
      if (this.$refs.seasonStart.showValue || this.$refs.seasonEnd.showValue)
        this.seasonCloseShow = true;
    },
    /**
     * @desc 清空季度日期
     */
    clearSeasonDate() {
      this.$refs.seasonStart.showValue = '';
      this.$refs.seasonEnd.showValue = '';
      this.handleChange();
    },
    /**
     * @desc 获取商户数据
     */
    getMerData() {
      axios.post(api.data.queryMerchant).then((response) => {
        this.merchantList = response;
      });
    },
    /**
     * @desc 年份日期发生改变
     */
    yearDateChange(val, type) {
      let _this = this;
      if (type) {
        this.yearEndOption = {
          disabledDate(time) {
            return (
              time.getTime() <
              new Date(_this.searchData.yearStartDate).getTime()
            );
          },
        };
      } else {
        if (!this.searchData.yearEndDate) {
          this.searchData.yearStartDate = '';
          this.yearEndOption = {};
          this.$set(this, 'yearStartOption', {
            disabledDate(time) {
              return false;
            },
          });
        } else {
          this.yearStartOption = {
            disabledDate(time) {
              return (
                time.getTime() >
                new Date(_this.searchData.yearEndDate).getTime()
              );
            },
          };
        }
      }
      if (this.searchData.yearStartDate && this.searchData.yearEndDate) {
        this.handleChange();
      }
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
     * @desc 导出
     */
    handleExport() {
      let date = '',that = this;
      if (this.searchData.dataEnums == 'DAY') {
        date = this.searchData.dayDate;
      } else if (this.searchData.dataEnums == 'MONTH') {
        date = this.searchData.monthDate;
      } else if (this.searchData.dataEnums == 'SEASON') {
        date = [
          this.seasonStartDate.substring(0, this.seasonStartDate.length - 3),
          this.seasonEndDate.substring(0, this.seasonEndDate.length - 3),
        ];
      } else {
        date = [this.searchData.yearStartDate, this.searchData.yearEndDate];
      }
      let params = {
        dataEnums: this.searchData.dataEnums,
        startDate: date[0],
        endDate: date[1],
        status: this.searchData.status,
        vendorKeys: this.searchData.vendorKeys,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.data.exportPurchase, this.$qs.stringify(params), {
          responseType: 'blob',
          timeout: 0,
        })
        .then((res) => {
          let data = new Blob([res.data], {
            type: 'application/json',
          });
          var reader = new FileReader();
          reader.addEventListener('loadend', function (e) {
            if (e.target.result.indexOf('result') > 0) {
              let result = JSON.parse(e.target.result);
              that.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: 'application/vnd.ms-excel',
              });
              if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                window.navigator.msSaveOrOpenBlob(blob, '商户采购报表.xlsx');
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute('download', '商户采购报表.xlsx');
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
              that.$message.success('导出成功');
            }
          });
          reader.readAsText(data);
        });
    },
    /**
     * @desc 搜索条件发生变化
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
      let date = '';
      if (this.searchData.dataEnums == 'DAY') {
        date = this.searchData.dayDate;
      } else if (this.searchData.dataEnums == 'MONTH') {
        date = this.searchData.monthDate;
      } else if (this.searchData.dataEnums == 'SEASON') {
        date = [
          this.seasonStartDate.substring(0, this.seasonStartDate.length - 3),
          this.seasonEndDate.substring(0, this.seasonEndDate.length - 3),
        ];
      } else {
        date = [this.searchData.yearStartDate, this.searchData.yearEndDate];
      }
      const params = {
        dataEnums: this.searchData.dataEnums,
        startDate: date != null ? date[0] : '',
        endDate: date != null ? date[1] : '',
        status: this.searchData.status,
        vendorKeys: this.searchData.vendorKeys,
        page: parseInt(this.page, 10) - 1,
        pagesize: this.pageSize,
      };
      axios
        .post(api.data.queryPurchase, this.$qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.tableData = response.lines.content;
          this.total = response.lines.total;
          this.purchaseAmt = response.purchaseAmt;
          this.purchaseNum = response.purchaseNum;
        });
      setTimeout(() => {
        this.loading = false;
      }, 300);
    },
  },
};
</script>
<style lang="less" scoped>
.all-num {
  height: 100%;
  text-align: right;
  padding-right: 20px;
  line-height: 40px;
  border: 1px solid #ebeef5;
  border-top: none;

  .title {
    font-weight: bold;
  }

  span {
    display: inline-block;
    margin-right: 10px;
  }
}

.year-range {
  display: inline-block;
  width: 310px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  box-sizing: border-box;
  padding-left: 10px;

  /deep/ .el-date-editor {
    width: 140px !important;

    &:last-child {
      .el-input__prefix {
        display: none;
      }
    }

    &:first-child {
      .el-input__suffix {
        display: none;
      }
    }

    .el-input__icon {
      display: flex;
      align-items: center;
    }

    input {
      border: none;
      text-align: center;
    }
  }
}

.season-range {
  display: inline-block;
  width: 330px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  box-sizing: border-box;
  padding-left: 10px;

  /deep/.season-date-picker {
    display: inline-block;

    .el-input__prefix .el-input__icon {
      display: none;
      align-items: center;
    }

    .el-input input {
      border: none;
    }

    &:first-child {
      .el-input {
        .el-input__prefix .el-input__icon {
          display: flex;
          align-items: center;
        }
      }
    }
  }

  .el-icon-circle-close {
    color: #dcdfe6;
    cursor: pointer;
  }
}
</style>