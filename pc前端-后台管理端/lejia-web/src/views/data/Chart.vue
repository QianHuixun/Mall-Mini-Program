<!-- 
@name: Chart.vue 
@description: 折线图报表模板页面
@author: crj
@url: /data/chart/:pkey
@date: 2020/08/13
-->

<template lang="html">
  <div class="table-container">
    <h1 class="title">{{ title }}</h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-date-picker
          v-model="date"
          v-if="searchConfig.daterangePicker||searchConfig.datePicker"
          :type="(searchConfig.daterangePicker?'daterange':'date')"
          range-separator="至"
          placeholder="请选择时间"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleChange"
        ></el-date-picker>
        <el-select
          v-model="goodsPkey"
          filterable
          @change="handleChange"
          placeholder="单品"
          v-if="$route.params.pkey == 'GOODSSALE'||$route.params.pkey == 'TIMESALE'"
        >
          <el-option
            :value="item.pkey"
            :key="index"
            :label="item.title"
            v-for="(item, index) in goodsList"
          ></el-option>
        </el-select>
      </div>
      <div class="search-box-button" v-if="$route.params.pkey=='TIMESALE'">
        <el-button v-loading="downLoading" type="primary" icon="el-icon-edit" size="medium" @click="handelExport">导出</el-button>
      </div>
    </div>
    <!-- Echart图表 -->
    <div id="echart-box" class=" echart-box " :class="searchConfig.table?'min-echart-box':'max-echart-box'" ></div>
    <!-- 表格框 -->
    <div class="table-box"  v-if="searchConfig.table">
      <el-table
        :data="tableData"
        :loading="loading"
        border
        style="width: 100%"
        class="table-fixed"
        :default-sort="{prop:'cost',order:'descending'}"
      >
        <el-table-column
          v-for="(item,index) in  tableContentData"
          :key="index"
          :label="item.label"
          :prop="item.propName"
          :sortable="item.sortable"
        ></el-table-column>
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
  </div>
</template>

<script>
import qs from 'qs';
import dropdown from '@/assets/js/dropdown';
import utils from '@/assets/js/utils';
export default {
  data() {
    return {
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0, //总页数
      params: {
        //接口参数
        table: {},
        chart: {},
      },
      url: {
        //接口路径
        table: '',
        chart: '',
      },
      tableContentData: [], //表格显示内容
      loading: false,
      date: '',
      goodsList: [],
      goodsPkey: '',
      searchConfig: {
        //搜索配置
        datePicker: false,
        daterangePicker: true,
        goodSelect: false,
        table: false,
      },
      chartConfig: {
        echartType: 'line',
        xNumber: 24,
        dataZoom: true,
        xDisplay: 'month',
      },
      downLoading: false,
    };
  },
  mounted() {
    let query = this.judgement();
    dropdown.getGoods('', 0, 999).then((result) => {
      this.goodsList = result.content;
      if (query.params.chart.hasOwnProperty('goodsPkey')) {
        this.goodsPkey = result.content[0].pkey;
        query.params.chart.goodsPkey = result.content[0].pkey;
      }
      if (query.searchConfig.table) this.getData(query);
      this.getEchartData(query);
    });
    console.log(query)
    // this.getData(query);
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

  methods: {
    /**
     * @desc 导出
     */
    handelExport() {
      let params = {
        ...this.params.table,
      };
      let that = this;
      this.downLoading = true;
      if (this.searchConfig.datePicker) {
        params.time = this.date;
      } else if (this.searchConfig.daterangePicker) {
        params.endTime = this.date[0];
        params.startTime = this.date[1];
      }
      if (this.searchConfig.goodSelect) {
        params.goodsPkey = this.goodsPkey;
      }
      axios
        .post(api.data.exportTime, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
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
              that.downLoading = false;
              that.$message.error(result.codeMsg);
              return;
            } else {
              let blob = new Blob([res.data], {
                type: 'application/vnd.ms-excel',
              });
              if (!!window.ActiveXObject || 'ActiveXObject' in window) {
                window.navigator.msSaveOrOpenBlob(blob, `${that.title}.xlsx`);
              } else {
                const link = document.createElement('a');
                link.style.display = 'none';
                link.href = URL.createObjectURL(blob);
                link.setAttribute('download', `${that.title}.xlsx`);
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
              that.downLoading = false;
              that.$message.success('导出成功');
            }
          });
          reader.readAsText(data);
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
     * 搜索改变事件
     */
    handleChange: function () {
      this.page = 1;
      if (this.searchConfig.table) this.getData();
      this.getEchartData();
    },
    /**
     * 图表渲染
     */
    chartInit(chartData) {
      let myChart = this.$echarts.init(document.getElementById('echart-box')),
        _this = this;
      myChart.setOption({
        tooltip: {
          trigger: 'axis',
        },
        xAxis: {
          type: 'time',
          splitLine: {
            show: false,
          },
          splitNumber:
            chartData.length > 25 ? this.chartConfig.xNumber : chartData.length,
          axisLine: {
            lineStyle: {
              color: '#1B2232',
            },
          },
          axisLabel: {
            formatter: function (value, index) {
              return _this.axisLabelJudge(_this.chartConfig.xDisplay, value);
            },
            color: '#1B2232',
          },
        },
        yAxis: {
          nameTextStyle: {
            color: '#1B2232',
            fontSize: 15,
          },
          type: 'value',
          boundaryGap: [0, '100%'],
          axisLine: {
            lineStyle: {
              color: '#1B2232',
            },
          },
          axisLabel: {
            color: '#1B2232',
          },
        },
        series: [
          {
            type: this.chartConfig.echartType,
            // hoverAnimation: false,
            smooth: true,
            // symbolSize: 4,
            data: chartData,
            color: '#409EFF',
          },
        ],
      });
      window.addEventListener('resize', function () {
        myChart.resize();
      });
      if (this.chartConfig.dataZoom && chartData.length > 15) {
        myChart.setOption({
          dataZoom: [
            {
              startValue: utils.formatTimeInArr(
                chartData[0][0] / 1000,
                'Y-M-D'
              ),
            },
            {
              type: 'inside',
            },
          ],
        });
      } else {
        myChart.setOption({
          dataZoom: [],
        });
      }
    },
    /**
     * 获取列表
     */
    getData: function (query = '') {
      this.loading = true;
      let url, params;
      query
        ? (url = query.url.table) &&
          (params = {
            ...query.params.table,
          })
        : (url = this.url.table) &&
          (params = {
            ...this.params.table,
          });
      if (this.searchConfig.datePicker) {
        params.time = this.date;
      } else if (this.searchConfig.daterangePicker) {
        params.endTime = this.date[0];
        params.startTime = this.date[1];
      }
      if (this.searchConfig.goodSelect) {
        params.goodsPkey = this.goodsPkey;
      }
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
    /**
     * 获取图表
     */
    getEchartData: function (query = '') {
      let url, params;

      query
        ? (url = query.url.chart) &&
          (params = {
            ...query.params.chart,
          })
        : (url = this.url.chart) &&
          (params = {
            ...this.params.chart,
          });
      if (this.searchConfig.datePicker) {
        params.time = this.date;
      } else if (this.searchConfig.daterangePicker) {
        params.endTime = this.date[1];
        params.startTime = this.date[0];
      }
      if (this.searchConfig.goodSelect) {
        params.goodsPkey = this.goodsPkey;
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          let chartData = response.map((item) => {
            return [item.timeStamp, item.value];
          });
          this.chartInit(chartData);
        });
    },
    /**
     * 判断调用接口以及表格显示内容
     */
    judgement: function () {
      const pkey = this.$route.params.pkey;
      let searchConfig = this.searchConfig,
        chartConfig = this.chartConfig,
        url = {
          table: '',
          chart: '',
        },
        params = {
          table: {},
          chart: {},
        },
        date = [
          utils.formatTimeInArr(
            (new Date().getTime() - 2678400000) / 1000,
            'Y-M-D'
          ),
          utils.formatTimeInArr(new Date().getTime() / 1000, 'Y-M-D'),
        ];
      switch (pkey) {
        //商品销售分析
        case 'GOODSSALE':
          url.chart = api.data.goodsChart;
          searchConfig = {
            goodSelect: true,
          };
          params.chart = {
            endTime: date[1],
            startTime: date[0],
            goodsPkey: '',
          };
          break;
        //时间段销售分析
        case 'TIMESALE':
          url = {
            table: api.data.timeList,
            chart: api.data.timeChart,
          };
          (chartConfig.dataZoom = false),
            (chartConfig.xDisplay = 'date'),
            (searchConfig = {
              goodSelect: true,
              datePicker: true,
              table: true,
            });
          (params = {
            table: {
              goodsPkey: '',
              time: date[1],
            },
            chart: {
              goodsPkey: '',
              time: date[1],
            },
          }),
            (this.tableContentData = [
              {
                propName: 'kcCode',
                label: '订单号',
              },
              {
                propName: 'name',
                label: '单品',
              },
              {
                propName: 'num',
                label: '数量',
              },
              {
                propName: 'pricen',
                label: '金额',
              },
              {
                propName: 'createdTime',
                label: '下单时间',
              },
            ]);
          break;
        //付费会员办理分析
        case 'PAYMEMBER':
          url.chart = api.data.queryMemPay;
          params.chart = {
            endTime: date[1],
            startTime: date[0],
          };
          break;
        //用户访问分析
        case 'ACCESS':
          url.chart = api.data.queryAccess;
          chartConfig.echartType = 'bar';
          params.chart = {
            endTime: date[1],
            startTime: date[0],
          };
          break;
        //新增用户分析
        case 'NEWUSERS':
          url.chart = api.data.newUserList;
          params.chart = {
            endTime: '',
            startTime: '',
          };

          break;
      }
      (this.searchConfig = searchConfig),
        (this.url = url),
        (this.params = params),
        (this.chartCongig = chartConfig);
      searchConfig.daterangePicker ? (this.date = date) : (this.date = date[1]);
      return {
        url,
        params,
        searchConfig,
        chartConfig,
      };
    },
    /**
     * 渲染图表x周的显示值
     */
    axisLabelJudge: function (type, value) {
      let axisLabel;
      switch (type) {
        case 'month':
          var date = new Date(value);
          var month = date.getMonth() + 1;
          var day = date.getDate();
          if (month < 10) {
            month = '0' + month;
          }
          if (day < 10) {
            day = '0' + day;
          }
          axisLabel = `${month}-${day}`;
          break;
        default:
          var date = new Date(value);
          var hour = date.getHours();
          var minute = date.getMinutes();
          if (hour < 10) {
            hour = '0' + hour;
          }
          if (minute < 10) {
            minute = '0' + minute;
          }
          axisLabel = `${hour}:${minute}`;
          break;
      }

      return axisLabel;
    },
  },
};
</script>

<style lang="less" scoped>
.echart-box {
  margin: 0 auto;
}
.max-echart-box {
  width: 100%;
  height: 600px;
}
.min-echart-box {
  width: 90%;
  height: 300px;
}
</style>
