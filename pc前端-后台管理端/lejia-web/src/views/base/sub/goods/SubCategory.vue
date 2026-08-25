<!-- 
@name: Goods.vue 
@description: 商品库中心
@author: sx
@route: /base/goods
@date: 2020/07/01
-->
<template lang="html">
  <div class="table-container">
    <!-- <h1 class="title">
      {{ title }}
    </h1> -->
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-select v-model="enabled" @change="handleChange" placeholder="启停" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in enabledList"></el-option>
        </el-select>
        <el-select v-model="gType" @change="handleChange" placeholder="商品分类" clearable>
          <el-option value="" label="商品分类"></el-option>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in typeList"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions"></search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handleDownload">
          模板下载
        </el-button>
        <el-upload class="upload-demo" action="" :show-file-list="false" :http-request="handleImport" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
          <el-button type="primary" size="medium">
            导入
          </el-button>
        </el-upload>
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="handelAdd">
          新增商品
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" :loading="loading" element-loading-text="拼命导入中"
    element-loading-spinner="el-icon-loading"  border style="width: 100%" >
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="所属分类" prop="gtypeName"></el-table-column>
        <el-table-column label="是否启用" prop="enabled">
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.enabled" @change="handleStatus(scope.row.enabled,scope.row.pkey)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sort"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
  <div>
    <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
    <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleDelete(scope.row)">
      <el-button slot="reference" size="mini" type="danger">删除</el-button>
    </el-popconfirm>
  </div>
</template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
      <!-- 组件 -->
      <add-comp ref="AddComp" @refresh="getData"></add-comp>
      <edit-comp ref="EditComp" @refresh="getData"></edit-comp>
      <progress-dlog ref="ProgressDlog" :title="'导入商品库'" @refresh="getData" :uploadPercent.sync="uploadPercent"></progress-dlog>
    </div>
  </div>
</template>
<script>
import qs from 'qs';
import AddComp from './sub/GoodsAdd.vue';
import EditComp from './sub/GoodsEdit.vue';
import dropdown from '@/assets/js/dropdown';
import ProgressDlog from '@/components/global/ProgressDlog';
export default {
  data() {
    return {
      loading: false,
      searchKey: 'name',
      selectOptions: [{ name: '商品名称', key: 'name' }],
      tableData: [],
      enabledList: [
        { pkey: '', name: '启停' },
        { pkey: 'true', name: '启用' },
        { pkey: 'false', name: '停用' },
      ],
      enabled: '',
      gType: '',
      typeList: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      keywords: '', // 搜索关键字
      total: 0, //总页数
      type: [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel',
      ],
      excelList: [],
      uploadPercent: 0,
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
  components: {
    AddComp,
    EditComp,
    ProgressDlog,
  },
  mounted() {
    this.getData();
    dropdown.getType().then((result) => {
      this.typeList = result.content;
    });
  },
  methods: {
    handleChange: function () {
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
    startSearch: function ({ key, keywords }) {
      this.keywords = keywords;
      this.searchKey = key;
      this.page = 1;
      this.getData();
    },
    /**
     * 点击新增
     */
    handelAdd: function () {
      this.$refs.AddComp.show();
    },
    /**
     * 点击修改
     */
    handleEdit: function (row) {
      this.$refs.EditComp.show({ row: row });
    },
    /**
     * 删除公司
     */
    handleDelete: function (row) {
      const params = {
        pkey: row.pkey,
      };
      axios
        .post(api.mall.delGoods, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success('删除成功');
          this.getData();
        });
    },
    /**
     * 启停状态
     * @param  {[type]} status [新状态值]
     * @param  {[type]} pkey   [记录的pkey]
     * @return {[type]}        [description]
     */
    handleStatus: function (status, pkey) {
      let url = '',
        text = '',
        params = {
          pkey: pkey,
        };
      if (status) {
        url = api.mall.startGoods;
        text = '启用';
      } else {
        url = api.mall.stopGoods;
        text = '停用';
      }
      axios
        .post(url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.$message.success(text + '成功');
          // this.getData();
        });
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
        enabled: this.enabled,
        gtype: this.gType,
      };
      params[this.searchKey] = this.keywords;
      axios
        .post(api.mall.queryGoodsList, qs.stringify(params), {
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
     * 导入
     */
    async handleImport(file) {
      let acceptType = [
        '.csv',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel',
      ];
      if (!acceptType.includes(file.file.type)) {
        this.$message.warning('请导入excel文件');
        return;
      }
      let _this = this;
      let params = {};
      params = new FormData();
      params.append('myfile', file.file);
      axios
        .post(api.mall.importGoods, params, {
          headers: {
            'Content-Type': 'multipart/form-data;charset=UTF-8',
            Authorization: this.$store.state.token,
          },
          responseType: 'blob',
        })
        .then(function (response) {
          console.log(response);
          let blob = new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8',
          });

          if (blob.size == 0) {
            // _this.leadingVisible = true;
            _this.$refs.ProgressDlog.show();
            var timer = setInterval(() => {
              _this.uploadPercent = _this.uploadPercent + 1;
              if (_this.uploadPercent >= 100) {
                _this.uploadPercent = 0;
                // _this.leadingVisible = false;
                _this.$refs.ProgressDlog.hide();
                clearInterval(timer);
                _this.$message.success('恭喜你，导入成功');
                _this.getData();
              }
            }, 50);
          } else {
            _this.$refs.ProgressDlog.show();
            var timer = setInterval(() => {
              _this.uploadPercent = _this.uploadPercent + 1;
              if (_this.uploadPercent >= 100) {
                _this.uploadPercent = 0;
                _this.$refs.ProgressDlog.hide();
                clearInterval(timer);
                const h = _this.$createElement;
                _this.$msgbox({
                  title: '提示',
                  message: h('div', null, '导入出错，请下载出错数据重新导入'),
                  confirmButtonText: '确定',
                  callback: (action) => {
                    let objectUrl = URL.createObjectURL(blob);
                    let link = document.createElement('a');
                    link.style.display = 'none';
                    link.href = objectUrl;
                    link.setAttribute('download', '出错数据.xls');
                    document.body.appendChild(link);
                    link.click();
                    _this.getData();
                  },
                });
              }
            }, 50);
          }
        })
        .catch(function (error) {});
    },
    /**
     * 模板下载
     */
    handleDownload: function () {
      console.log(api.mall.downGoods);
      location.href = api.mall.downGoods;
    },
  },
};
</script>