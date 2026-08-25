<!-- 
@name: Member.vue 
@description: 会员管理
@author: sx
@url: /member/member
@date: 2020/07/08
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- 搜索栏 -->
    <div class="search-box">
      <!-- 搜索表单 -->
      <div class="search-box-form">
        <search-bar ref="searchBar" @search="startSearch" placeholder="请输入关键字" :select-options="selectOptions">
        </search-bar>
        <el-date-picker v-model="createdTime" type="daterange" range-separator="至" start-placeholder="注册开始日期"
          end-placeholder="注册结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
        <el-date-picker v-model="consumeTime" type="daterange" range-separator="至" start-placeholder="最近消费开始日期"
          end-placeholder="最近消费结束日期" value-format="yyyy-MM-dd" @change="handleChange">
        </el-date-picker>
         <el-select class="tags-select" v-model="tagKeys" @change="handleChange" filterable multiple collapse-tags placeholder="选择标签" clearable>
          <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in tagList"></el-option>
        </el-select>
      </div>
      <!-- 操作按钮 -->
      <div class="search-box-button">
        <el-button type="primary" size="medium" @click="handleTagsShow(-1)">
          打标签
        </el-button>
        <el-button type="primary" size="medium" @click="handleImportExcel" :loading="downLoading">
          导出会员
        </el-button>
        <el-upload class="upload-demo" action="" :show-file-list="false" :http-request="handleImport"
          accept=".xlsx, .xls">
          <el-button type="primary" size="medium">
            导入标签
          </el-button>
        </el-upload>
        <el-button type="primary" size="medium" @click="handelDownload" :loading="downLoading">
          下载模板
        </el-button>
      </div>
    </div>
    <!-- 表格框 -->
    <div class="table-box">
      <el-table :data="tableData" @selection-change="handleSelectionChange" :loading="loading" border style="width: 100%" class="table-fixed">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column label="昵称" prop="name"></el-table-column>
        <el-table-column label="手机号" prop="mobile" width="110"></el-table-column>
        <!-- <el-table-column label="地区" prop="area"></el-table-column> -->
        <el-table-column label="注册时间" prop="createdTime" width="150"></el-table-column>
        <el-table-column label="标签" prop="tagNames" width="100" show-overflow-tooltip>
           <template slot-scope="scope">
            <span>{{scope.row.tagNames.length != 0 ? scope.row.tagNames.join(',') : '-'}}</span>
          </template>
        </el-table-column>
        <el-table-column label="积分" prop="points"></el-table-column>
        <el-table-column label="账户余额" prop="balance"></el-table-column>
        <el-table-column label="消费金额" prop="consumeAmt"></el-table-column>
        <el-table-column label="消费笔数" prop="consumeCount"></el-table-column>
        <el-table-column label="最近消费市场" prop="lastConsumeFarmerName"></el-table-column>
        <el-table-column label="最近消费时间" prop="lastConsumeTime"></el-table-column>
        <el-table-column label="注册来源" prop="source"></el-table-column>
        <el-table-column label="备注" prop="remark"></el-table-column>
        <el-table-column label="操作" width="330" fixed="right" v-if="!isOnlyBrowse">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleTagsShow(scope.row)">
              打标签
            </el-button>
            <el-button type="text" size="small" @click="handleRemarkShow(scope.row)">
              备注
            </el-button>
            <el-button type="text" size="small" @click="handleCouponShow(scope.row)">
              赠送优惠券
            </el-button>
            <el-button type="text" size="small" @click="handlePointShow(scope.row)">
              调整积分
            </el-button>
            <el-button type="text" size="small" @click="handleDetails(scope.row)">
              积分明细
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <el-pagination hide-on-single-page background layout="total,prev, pager, next" :total="total" :current-page="page"
        :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
    </div>
    <!-- 赠送优惠券 -->
    <el-dialog :visible.sync="couponVisible" center title="赠送优惠券" :closeOnClickModal="false">
      <el-form>
        <el-form-item label="优惠券" label-width="120px">
          <el-select v-model="inputCoupon.card" filterable placeholder="请选择">
            <el-option :label="item.title" :value="item.pkey" v-for="(item,index) in CouponList" :key="index">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="medium" @click="hide">
          取 消
        </el-button>
        <el-button size="medium" type="primary" @click="handleCoupon(currentPkey)" :loading="loading">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 调整积分 -->
    <el-dialog :visible.sync="pointVisible" center title="调整积分" :closeOnClickModal="false">
      <el-form>
        <el-form-item label="积分类型" label-width="120px">
          <el-radio v-model="inputPoint.source" label="POINTS_MANUAL_ADD">增加</el-radio>
          <el-radio v-model="inputPoint.source" label="POINTS_MANUAL_LESS">减少</el-radio>
        </el-form-item>
        <el-form-item label="积分数量" label-width="120px">
          <el-input type="text" v-model="inputPoint.point" ref="pointInput" placeholder="积分"
            v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputPoint.point =val;}"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="medium" @click="hide">
          取 消
        </el-button>
        <el-button size="medium" type="primary" @click="handlePoint(currentPkey)" :loading="loading">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 开通年费会员 -->
    <el-dialog :visible.sync="yearVisible" center title="开通年费会员" :closeOnClickModal="false">
      <el-row>
        <el-col style="text-align: center; line-height: 30px;">
          <p>确认为该会员开通年费会员？</p>
          <span style="color: #ddd">开通后会赠与积分和优惠券。</span>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button size="medium" @click="hide">
          取 消
        </el-button>
        <el-button size="medium" type="primary" @click="handleYear(currentPkey)" :loading="loading">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 备注 -->
    <el-dialog :visible.sync="remarkVisible" center title="编辑备注" :closeOnClickModal="false">
      <el-form>
        <el-form-item label="备注" label-width="120px">
          <el-input type="textarea" :rows="6" v-model="inputRemark" placeholder="请输入备注" maxlength="200" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="medium" @click="hide">
          取 消
        </el-button>
        <el-button size="medium" type="primary" @click="handleRemark(currentPkey)" :loading="loading">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <progress-dlog ref="ProgressDlog" :title="'导入'" @refresh="getData" :uploadPercent.sync="uploadPercent">
    </progress-dlog>
    <member-tags-upd ref="MemberTagsUpd" @refresh="getData"></member-tags-upd>
  </div>
</template>
<script>
  import qs from "qs";
  import dropdown from "@/assets/js/dropdown";
  import ProgressDlog from '@/components/global/ProgressDlog';
  import MemberTagsUpd from './sub/MemberTagsUpd';
  export default {
    data() {
      return {
        loading: false,
        uploadPercent: 0,
        numData: [],
        tableData: [],
        searchKey: "name",
        selectOptions: [{
          name: "昵称",
          key: "name"
        }, {
          name: "手机号码",
          key: "mobile"
        }, {
          name: "最近消费市场",
          key: "lastConsumeFarmer"
        }, {
          name: "备注",
          key: "remark"
        }, {
          name: "注册来源",
          key: "source"
        }],
        levelList: [{
            pkey: "",
            name: "会员类型"
          },
          {
            pkey: "ORDINARY_MEMBER",
            name: "普通会员"
          },
          {
            pkey: "PAID_MEMBER",
            name: "年费会员"
          }
        ],
        inputCoupon: {
          card: ' '
        },
        inputPoint: {
          source: "POINTS_MANUAL_ADD",
          point: ""
        },
        level: "",
        page: 1, //显示页码
        pageSize: 10, //表格一页显示几条     
        keywords: "", // 搜索关键字
        total: 0, //总页数
        couponVisible: false,
        pointVisible: false,
        yearVisible: false,
        currentPkey: "",
        CouponList: [],
        consumeTime: [],
        createdTime: [],
        downLoading: false,
        remarkVisible: false,
        inputRemark: "",
        tagKeys:[],
        tagList:[],
        selectList:[],// 列表选中数据
      };
    },
    mounted() {
      this.getData();
      this.getTagData();
    },
    components: {
      ProgressDlog,
      MemberTagsUpd
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
        let hasBrowse = false
        if (this.$store.state.activeName) {
          hasBrowse = this.$store.state.activeName.indexOf('仅浏览') > 0 ? true : false;
        }
        return hasBrowse
      }
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
     * @desc 获取标签列表
     */
    getTagData() {
      axios.post(api.marketing.tagsDrop).then((response) => {
        this.tagList = response;
      });
    },
    /**
     * 模板下载
     */
    handelDownload: function () {
      let that = this;
        this.downLoading = true;
        axios
          .post(api.marketing.downTemplateMember, qs.stringify({}), {
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
                    `${'模板 ' }.xlsx`
                  );
                } else {
                  const link = document.createElement('a');
                  link.style.display = 'none';
                  link.href = URL.createObjectURL(blob);
                  link.setAttribute(
                    'download',
                    `${'模板'}.xlsx`
                  );
                  document.body.appendChild(link);
                  link.click();
                  document.body.removeChild(link);
                }
                that.downLoading = false;
                that.$message.success('下载成功');
              }
            });
            reader.readAsText(data);
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
          .post(api.marketing.importexcelMember, params, {
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
      // 打标签
      handleTagsShow(row){
        if(row == -1){
          if(this.selectList.length == 0){
            this.$message.error('请选择会员');
            return;
          }
          const pkeyArr = [];
          this.selectList.forEach(item => {
            pkeyArr.push(item.pkey);
          });
          this.$refs.MemberTagsUpd.show(pkeyArr);
        }else{
          this.$refs.MemberTagsUpd.show([row.pkey]);
        }
      },
      handleSelectionChange(val) {
        console.log('更改选中', val);
        this.selectList = val;
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
      /**列表导出 */
      handleImportExcel() {
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          level: this.level,
          startCreatedTime: this.createdTime ? this.createdTime[0] : '',
          endCreatedTime: this.createdTime ? this.createdTime[1] : '',
          startLastConsumeTime: this.consumeTime ? this.consumeTime[0] : '',
          endLastConsumeTime: this.consumeTime ? this.consumeTime[1] : '',
          tagKeys: this.tagKeys.join(',')
        };
        params[this.searchKey] = this.keywords;
        let that = this;
        this.downLoading = true;
        axios
          .post(api.marketing.exportMember, qs.stringify(params), {
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
                   '会员信息'
                  }.xlsx`
                  );
                } else {
                  const link = document.createElement('a');
                  link.style.display = 'none';
                  link.href = URL.createObjectURL(blob);
                  link.setAttribute(
                    'download',
                    `${
                   '会员信息'
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
      /**
       * 显示赠送优惠券dialog
       */
      handleCouponShow: function (row) {
        this.currentPkey = row.pkey;
        dropdown.getCoupon().then(result => {
          this.CouponList = result;
        });
        this.inputCoupon = {
          card: ''
        };
        this.couponVisible = true;
      },

      /**
       * 显示备注dialog
       */
      handleRemarkShow: function (row) {
        this.currentPkey = row.pkey;
        this.inputRemark = row.remark;
        this.remarkVisible = true;
      },
      /**
       * 显示调整积分dialog
       */
      handlePointShow: function (row) {
        this.currentPkey = row.pkey;
        this.inputPoint = {
          source: "POINTS_MANUAL_ADD",
          point: ""
        };
        this.pointVisible = true;
      },
      /**
       * 显示开通年费会员dialog
       */
      handleYearShow: function (row) {
        this.currentPkey = row.pkey;
        this.yearVisible = true;
      },
      /**
       * 积分明细
       */
      handleDetails: function (row) {
        this.$router.push({
          path: "/member/points",
          query: {
            pkey: row.pkey
          }
        });
      },
      /**
       * 隐藏全部弹出dialog
       */
      hide: function () {
        this.couponVisible = false;
        this.pointVisible = false;
        this.yearVisible = false;
        this.remarkVisible = false;
      },
      /**
       * 备注
       */
      handleRemark: function (pkey) {
        const params = {
          pkey: pkey,
          remark: this.inputRemark,
        }
        axios.post(api.marketing.tagsMember, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(() => {
            this.$message.success("修改成功");
            this.remarkVisible = false;
            this.getData();
          });
      },
      /**
       * 赠送优惠券
       */
      handleCoupon: function (pkey) {
        const params = {
          member: pkey,
          card: this.inputCoupon.card,
          status: "10"
        }
        axios.post(api.marketing.grantCoupon, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(() => {
            this.$message.success("开通成功");
            this.couponVisible = false;
          });
      },
      /**
       * 调整积分
       */
      handlePoint: function (pkey) {
        console.log(this.inputPoint.point)
        if (!this.inputPoint.point) {
          this.$message.error("请填写积分数量");
          this.$refs.pointInput.focus();
          return;
        }
        const params = this.inputPoint;
        params["pkey"] = pkey;
        axios.post(api.marketing.adjustMember, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(() => {
            this.$message.success("积分调整成功");
            this.getData();
            this.pointVisible = false;
          });
      },
      /**
       * 开通年费会员
       */
      handleYear: function (pkey) {
        let name = '';
        for (let i in this.tableData) {
          if (this.tableData[i].pkey == pkey) {
            name = this.tableData[i].name
            break
          }
        }
        const params = {
          pkey: pkey,
          name: name
        }
        axios.post(api.marketing.openMember, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(() => {
            this.$message.success("赠送优惠券成功");
            this.getData();
            this.yearVisible = false;
          });
      },
      /**
       * 获取列表
       */
      getData: function () {
        this.loading = true;
        const params = {
          page: this.page - 1,
          pagesize: this.pageSize,
          level: this.level,
          startCreatedTime: this.createdTime ? this.createdTime[0] : '',
          endCreatedTime: this.createdTime ? this.createdTime[1] : '',
          startLastConsumeTime: this.consumeTime ? this.consumeTime[0] : '',
          endLastConsumeTime: this.consumeTime ? this.consumeTime[1] : '',
          tagKeys: this.tagKeys.join(',')
        };
        params[this.searchKey] = this.keywords;
        axios.post(api.marketing.queryMember, qs.stringify(params), {
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
  }
</script>
<style lang="less" scoped>
.search-box-form {
  /deep/ .tags-select {
    width: 180px !important;
  }
}
</style>
