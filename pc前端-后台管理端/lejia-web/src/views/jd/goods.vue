
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <el-cascader v-model="searchData.category" :options="categoryList" :props="props" clearable placeholder="商品分类" @change="handleChange"></el-cascader>
        <el-select v-model="searchData.enabled" @change="handleChange" placeholder="上下架" clearable>
          <el-option value="true" label="已上架"></el-option>
          <el-option value="false" label="已下架"></el-option>
        </el-select>
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-upload class="upload-demo" action="" :show-file-list="false" :http-request="handleImport"
          accept=".xlsx, .xls">
          <el-button type="primary" size="medium">
            导入
          </el-button>
        </el-upload>
        <el-button type="primary" size="medium" @click="handleExport">
          导出
        </el-button>
        <el-button type="primary" size="medium" @click="handelEnabled(true)">
          批量上架
        </el-button>
        <el-button type="primary" size="medium" @click="handelEnabled(false)">
          批量下架
        </el-button>
        <el-button type="primary" size="medium" @click="handleChangeDisplayName">
          专区显示名称
        </el-button>
        <el-button type="primary" size="medium" @click="handleShowServiceContent">
          商品服务内容
        </el-button>
        <el-button type="primary" size="medium" @click="handleShowPostageConfig">
          运费配置
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table class="table-fixed" :data="tableData" :loading="loading" border style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column align="center" type="selection"></el-table-column>
        <el-table-column label="商品图片" prop="image" min-width="120">
          <template slot-scope="scope">
            <el-image v-if="scope.row.photo1" :src="scope.row.photo1[0]" style="width: 100px; height: 100px"
              :preview-src-list="scope.row.photo1">
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="title" min-width="120"></el-table-column>
        <el-table-column label="SPU" prop="spuId" min-width="120"></el-table-column>
        <el-table-column label="SKU" prop="skuNum" min-width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.skuNum }}个SKU</span>
          </template>
        </el-table-column>
        <el-table-column label="所属分类" prop="categoryName" min-width="120"></el-table-column>
        <el-table-column label="京东价" prop="salePrice" min-width="120"></el-table-column>
        <el-table-column label="商城价格" prop="price" min-width="120"></el-table-column>
        <el-table-column label="状态" prop="enabled" min-width="120">
          <template slot-scope="scope">
            <el-switch active-color="#13ce66" v-model="scope.row.enabled"
              @change="handelEnabled(scope.row.enabled, scope.row.spuId)">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleUpdate(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination background layout="total, prev, pager, next, jumper" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <goods-update ref="goodsUpdate"></goods-update>
    <display-name ref="displayName"></display-name>
    <service-content ref="serviceContent"></service-content>
    <postage-config ref="postageConfig"></postage-config>
    <progress-dlog ref="ProgressDlog" :title="'导入'" @refresh="getData" :uploadPercent.sync="uploadPercent">
    </progress-dlog>
  </div>
</template>

<script>
import GoodsUpdate from './Goods/update.vue'
import DisplayName from './Goods/DisplayName.vue';
import ServiceContent from './Goods/ServiceContent.vue';
import PostageConfig from './Goods/PostageConfig.vue';
import ProgressDlog from '@/components/global/ProgressDlog';
export default {
  data() {
    return {
      loading: false,
      tableData: [],
      page: 1, //显示页码
      pageSize: 10, //表格一页显示几条
      total: 0,
      searchData: {
        category: "",
        enabled: "",
        title: '',
        spuId: '',
        skuId: '',
      },
      selectOptions: [
        {
          name: '商品名称',
          key: 'title',
        },
        {
          name: 'SPU',
          key: 'spuId',
        },
        {
          name: 'SKU',
          key: 'skuId',
        },
      ],
      categoryList: [],
      props: {
        value: 'pkey',
        label: 'categoryName',
        children: 'list',
        emitPath: false,
        checkStrictly: true
      },
      multipleSelection: [],
      uploadPercent: 0,
    }
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
    GoodsUpdate,
    DisplayName,
    ServiceContent,
    PostageConfig,
    ProgressDlog
  },
  mounted() {
    this.getData()
    this.getCategory()
  },
  methods: {
    getData() {
      this.loading = true;
      const params = {
        page: this.page - 1,
        pagesize: this.pageSize,
        category: this.searchData.category,
        enabled: this.searchData.enabled,
        title: this.searchData.title,
        spuId: this.searchData.spuId,
        skuId: this.searchData.skuId,
      };
      let url = api.jd.goodsQuery;
      axios.post(url, this.$qs.stringify(params))
        .then((response) => {
          this.tableData = response.content;
          this.total = response.total;
          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    /**
     * 获取三级分类下拉
     */
    getCategory() {
      axios.post(api.jd.categoryThreeDrop)
        .then((response) => {
          this.categoryList = response
        });
    },
    handleChange() {
      this.page = 1
      this.getData()
    },
    /**
     * 搜索
     */
    startSearch({ key, keywords }) {
      this.selectOptions.forEach(item => {
        this.searchData[item.key] = null
      })
      this.searchData[key] = keywords;
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
    handleImport(file) {
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
      _this.$refs.ProgressDlog.show();
      axios
        .post(api.jd.goodsImport, params, {
          headers: {
            'Content-Type': 'multipart/form-data;charset=UTF-8',
            Authorization: this.$store.state.token,
          },
          responseType: 'blob',
          onUploadProgress(progress) {
            // console.log(progress, Math.round((progress.loaded / progress.total) * 100))
            _this.uploadPercent = Math.round((progress.loaded / progress.total) * 99);
          },
        })
        .then(function (response) {
            _this.uploadPercent = 100
            _this.$refs.ProgressDlog.hide();
            setTimeout(() => {
              _this.uploadPercent = 0
            }, 100)
          console.log(response);
          if (response.data.type == "application/json") {
            //适配不同的接口有些导入成功或者失败会返回json格式的数据
            const reader = new FileReader();
            reader.onload = function () {
              const msgResult = JSON.parse(reader.result); //此处的msg就是后端返回的msg内容
              console.log(msgResult, "msgResult");
              if (msgResult.success) {
                _this.$message.success('恭喜你，导入成功');
              } else {
                _this.$message.warning(msgResult.msg || "文件错误");
              }
              setTimeout(() => {
                _this.getData();
              }, 1000);
            };
            reader.readAsText(response.data);
            return;
          }
          let blob = new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8',
          });

          if (blob.size == 0) {
            _this.$message.success('恭喜你，导入成功');
            _this.getData();
          } else {
            const h = _this.$createElement;
            _this.$msgbox({
              title: '提示',
              message: h('div', null, '导入出错，请下载出错数据重新导入'),
              confirmButtonText: '确定',
              callback: () => {
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
        })
        .catch(function () { });
    },
    /**
     * 批量导出
     */
    handleExport() {
      const { category, enabled, title, spuId, skuId } = this.searchData
      const params = {
        category: category,
        enabled: enabled,
        title: title,
        spuId: spuId,
        skuId: skuId,
      };
      let url = api.jd.goodsExport;
      axios.post(url, this.$qs.stringify(params), {
          responseType: 'blob',
        })
        .then((response) => {
          var _this = this;
          if (response.data.type == 'application/json') {
            const reader = new FileReader();
            reader.onload = function () {
              const msgResult = JSON.parse(reader.result); //此处的msg就是后端返回的msg内容
              _this.$message.warning(msgResult.msg);
            };
            reader.readAsText(response.data);
            return;
          }
          let blob = new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8',
          });
          var disposition = response.headers['content-disposition'];
          var headersFileName = disposition ? disposition.split('=') : '';
          var fileName = headersFileName && headersFileName.length != 0 ? decodeURI(headersFileName[1]) : '京东商品明细.xlsx'
          if (!!window.ActiveXObject || 'ActiveXObject' in window) {
            window.navigator.msSaveOrOpenBlob(blob, fileName);
          } else {
            const link = document.createElement('a');
            link.style.display = 'none';
            link.href = URL.createObjectURL(blob);
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
          }
        });
    },
    /**
     * 批量选中
     */
    handleSelectionChange(selection) {
      this.multipleSelection = selection;
    },
    /**
     * 切换上下架状态
     */
    handelEnabled(enabled, spuId) {
      if(!spuId && !this.multipleSelection.length) {
        this.$message.warning('请选择商品')
        return
      }
      const spuIds = spuId ? [spuId] : this.multipleSelection.map(item => item.spuId)
      const params = {
        enabled,
        spuId: spuIds.join(','),
      }
      let url = api.jd.goodsSpuIdEnable;
      axios.post(url, this.$qs.stringify(params))
        .then(() => {
          this.getData()
        });
    },
    /**
     * 修改专区名称
     */
    handleChangeDisplayName() {
      this.$refs.displayName.show()
    },
    /**
     * 商品服务内容
     */
    handleShowServiceContent() {
      this.$refs.serviceContent.show()
    },
    handleShowPostageConfig() {
      this.$refs.postageConfig.show()
    },
    /**
     * 编辑sku
     */
    handleUpdate(row) {
      this.$refs.goodsUpdate.show(row)
    }
  }
}
</script>

<style></style>