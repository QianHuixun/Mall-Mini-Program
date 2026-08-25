<!--
* @description 钱包查询
* @fileName PurseManage.vue
* @author zs
* @date 2024/08/26
!-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="source" @change="handleChange" placeholder="选择类型" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in sourceList"></el-option>
        </el-select>
        <el-date-picker v-model="date" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
         <el-button type="primary" size="medium" @click="handleImportExcel" :loading="downLoading">
          导出
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" border style="width: 100%">
        <el-table-column label="会员名称" prop="memberName"></el-table-column>
        <el-table-column label="手机号" prop="memberMobile"></el-table-column>
        <el-table-column label="类型" prop="sourceName"></el-table-column>
        <el-table-column label="交易金额" prop="amtStr"></el-table-column>
        <el-table-column label="余额" prop="balance"></el-table-column>
        <el-table-column label="时间" prop="createdTime"></el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
  </div>
</template>
<script>
  import qs from 'qs';
  export default {
    data() {
      return {
        loading: false,
        searchKey: 'mobile',
        selectOptions: [{
          name: '手机号',
          key: 'mobile'
        }],
        tableData: [],
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条
        keywords: '', // 搜索关键字
        total: 0, //总页数
        downLoading: false,
        sourceList: [{
            pkey: "",
            name: "全部"
          },
          {
            pkey: "RECHARGE",
            name: "充值"
          },
          {
            pkey: "COMM_SHARE",
            name: "分享"
          },
          {
            pkey: "SHARE_NEW",
            name: "邀新"
          },
          {
            pkey: "COMM_BUY",
            name: "消费"
          },
          {
            pkey: "POINTS_MANUAL_ADD",
            name: "手动"
          },
          {
            pkey: "COMM_RETURN",
            name: "退货"
          }
        ],
        date: "",
        source: "",
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
    components: {},
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
      startSearch: function ({
        key,
        keywords
      }) {
        this.keywords = keywords;
        this.searchKey = key;
        this.page = 1;
        this.getData();
      },
      handleChange: function () {
        this.page = 1;
        this.getData();
      },
      /**
       * 获取列表
       * @return {[type]} [description]
       */
      getData: function () {
        this.loading = true;
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          source: this.source,
          startDate: this.date ? this.date[0] : "",
          endDate: this.date ? this.date[1] : "",
        };
        params[this.searchKey] = this.keywords;
        axios
          .post(api.marketing.purseQuery, qs.stringify(params), {
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

      /**列表导出 */
      handleImportExcel() {
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          source: this.source,
          startDate: this.date ? this.date[0] : "",
          endDate: this.date ? this.date[1] : "",
        };
        params[this.searchKey] = this.keywords;
        let that = this;
        this.downLoading = true;
        axios
          .post(api.marketing.downTemplatepurse, qs.stringify(params), {
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
                  window.navigator.msSaveOrOpenBlob(
                    blob,
                    `${
                   '钱包信息'
                  }.xlsx`
                  );
                } else {
                  const link = document.createElement('a');
                  link.style.display = 'none';
                  link.href = URL.createObjectURL(blob);
                  link.setAttribute(
                    'download',
                    `${
                   '钱包信息'
                  }.xlsx`
                  );
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
    },
  };
</script>