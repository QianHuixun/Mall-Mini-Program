<!-- 
@name: MerchantUpdate.vue 
@description: 商户管理--修改模板 
@author: crj
@date: 2021/10/18
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <div class="merchant-upd">
      <el-form class="zy-form" ref="form" label-width="160px">
        <div class="zy-form-columns" style="height:780px">
          <el-form-item label="选择市场" required v-if="$store.state.userIdentity==1">
            <el-select v-model="inputModel.farmer" placeholder="请选择市场" filterable @change="handleChange">
              <el-option v-for="(item,index) in marketList" :value="item.pkey" :label="item.name" :key="index">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="商户名称" required>
            <el-input v-model="inputModel.name" placeholder="请输入商户名称" :maxlength="40"></el-input>
          </el-form-item>
          <el-form-item label="展示名称" required>
            <el-input v-model="inputModel.displayName" placeholder="请输入展示名称" :maxlength="40"></el-input>
          </el-form-item>
          <el-form-item label="手机号码" required>
            <el-input v-model="inputModel.mobile" placeholder="请输入商户手机号码"
              v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.mobile =val;}" :maxlength="11"></el-input>
          </el-form-item>
          <el-form-item label="经营范围" required>
            <el-select v-model="inputModel.businessScopes" placeholder="请选择经营范围" multiple collapse-tags filterable>
              <el-option v-for="(item,index) in gtypeList" :value="item.pkey" :label="item.name" :key="index">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="摊位号" required>
            <el-input v-model="inputModel.booth" placeholder="请输入商户摊位号" :maxlength="40" ></el-input>
          </el-form-item>
          <el-form-item label="银行账户名称">
            <el-input v-model="inputModel.bankname" placeholder="请输入商户银行账户名称，如建设银行" :maxlength="40" >
            </el-input>
          </el-form-item>
          <el-form-item label="银行卡号">
            <el-input v-model="inputModel.bankcard" placeholder="请输入商户银行卡号" :maxlength="40" 
              v-on:input="(val)=>{val =val.replace(/[^\d]/g,'');inputModel.bankcard =val;}"></el-input>
          </el-form-item>
          <el-form-item label="开户支行名称">
            <el-input v-model="inputModel.bankBranchName" placeholder="请输入开户支行名称" :maxlength="40" >
            </el-input>
          </el-form-item>
          <el-form-item label="开户行大额行号">
            <el-input v-model="inputModel.bankNo" placeholder="请输入开户行大额行号" :maxlength="40" >
            </el-input>
          </el-form-item>
          <el-form-item label="开户人姓名">
            <el-input v-model="inputModel.bankuser" :disabled="inputModel.zxStatus=='AUDIT_SUCCESS'"
              placeholder="请输入开户人姓名" :maxlength="40"></el-input>
          </el-form-item>
          <el-form-item label="开户人身份证号">
            <el-input v-model="inputModel.zxIdentity" :disabled="inputModel.zxStatus=='AUDIT_SUCCESS'"
              placeholder="请输入开户人身份证号" :maxlength="40"
              v-on:input="(val)=>{val =clearNoNumLetter(val); inputModel.zxIdentity=val}"></el-input>
          </el-form-item>
          <el-form-item label="银行卡绑定手机号">
            <el-input v-model="inputModel.bankuserMoblie" placeholder="请输入银行卡绑定手机号" :maxlength="40" >
            </el-input>
          </el-form-item>
          <el-form-item label="佣金费率配置" class="rate-item">
            <el-input v-model="inputModel.commissionRate" placeholder="请输入费率" :maxlength="10"
              v-on:input="(val)=>{val =inputNumberFixed(val);inputModel.commissionRate =val;}">
              <template slot="append">%</template>
            </el-input>
            <span v-if="inputModel.rateUpdateTime">更新时间：{{inputModel.rateUpdateTime}}</span>
            <p class="tips-p">
              注：设置为0表示平进平出；
            </p>
            <p class="tips-p">
              费率为空表示不做佣金配置，结算报表将不产生结算数据。
            </p>
            

          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="inputModel.addr" placeholder="请输入商户地址" :maxlength="40"></el-input>
          </el-form-item>

          <div class="flex-box">
            <el-form-item>
              <div class='other-label' slot="label">
                <div class="title">商户头像</div>
                <div class="tips">建议尺寸：260*260像素</div>
              </div>
              <img-upload ref="logo" :limit="1" @change="logoChange"></img-upload>
            </el-form-item>
            <el-form-item>
              <div class='other-label' slot="label">
                <div class="title">宣传视频</div>
                <div class="tips">
                  <p>建议比例：16:9</p>
                  <p>建议大小：50M</p>
                  <p>建议格式：mp4/3gp/m3u8</p>
                </div>
              </div>
              <file-upload :fileUrlList.sync="video" ref="video" :limit="1" :loading.sync="fileloading"
                :fileType="fileType" :maxSize="50"></file-upload>
            </el-form-item>
          </div>

          <el-form-item label="证件照">
            <div class='other-label' slot="label">
              <div class="title">证件照</div>
              <div class="tips">建议尺寸：750*320像素，最多5张</div>
            </div>
            <img-upload ref="person" :limit="5" @change="personChange"></img-upload>
          </el-form-item>
          <el-form-item label="商户简介">
            <el-input type="textarea" v-model="inputModel.shortContent" placeholder="请输入商户简介，不要超过50字" :maxlength="50">
            </el-input>
          </el-form-item>
          <el-form-item label="风采展示详情页内容">
            <editor idName="trainEditor" ref="editor" :frameWidth="600" :frameHeight="250"></editor>
          </el-form-item>
        </div>

        <el-form-item label-width="0" class="form-footer">
          <el-button type="primary" @click="handleSubmit" :loading="fileloading||loading">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
  import utils from '@/assets/js/utils';
  import dropdown from '@/assets/js/dropdown';
  import ImgUpload from '@/components/global/ImgUpload';

  export default {
    data() {
      return {
        isTianJin: localStorage.getItem("ascription") ==  (process.env.VUE_APP_TITLE =='production' ? 13 : 22) ? true : false,
        loading: false,
        inputModel: {
          shortContent: '',
          addr: '',
          bankBranchName: '',
          bankNo: '',
          bankcard: '',
          bankname: '',
          bankuser: '',
          bankuserMoblie: '',
          zxIdentity: '',
          businessScopes: [],
          businessScopesName: '',
          commissionRate: 0,
          farmer: '',
          files: [],
          mktVendorBigData: {
            content: '',
            pkey: '',
          },
          mobile: '',
          name: '',
          displayName: '',
          booth: '',
          pkey: '',
        },
        logoImg: '',
        personImg: [],
        video: '',
        fileloading: false,
        marketList: [],
        gtypeList: [],
        fileType: [{
            name: 'mp4',
            type: 'video/mp4',
          },
          {
            name: 'm3u8',
            type: 'audio/x-mpegurl',
          },
          {
            name: '3gp',
            type: 'video/3gpp',
          },
        ],
        bankuser: '',
        zxIdentity: '',
      };
    },
    watch: {
      fileloading: {
        immediate: true,
        handler(newVal, oldVal) {
          console.log(newVal);
        },
      },
    },
    mounted() {
      this.getMarketData();
      if (this.$store.state.userIdentity != 1) {
        this.getGtypeData();
      }
      if (this.isEdit) this.getData();
    },
    components: {
      ImgUpload,
      FileUpload(resolve) {
        require(['@/components/global/FileUpload'], resolve);
      },
      Editor(resolve) {
        require(['@/components/global/Editor'], resolve);
      },
    },
    methods: {
      /**
       * @desc 输入时只允许输入数字和字母
       */
      clearNoNumLetter(val) {
        return utils.clearNoNumLetter(val);
      },
      /**
       * @Desc 精确小数点两位
       */
      inputNumberFixed(val) {
        val = utils.inputNumberFixed(val);
        return val;
      },
      getData: function () {
        let params = {};
        if (this.$route.query.hasOwnProperty('pkey')) {
          params.pkey = this.$route.query.pkey;
        } else {
          params.pkey = localStorage.getItem('vendorMerchant') || '';
        }
        axios
          .post(api.market.queryVendorDetail, this.$qs.stringify(params))
          .then((res) => {
            this.initData({
              inputModel: res,
            });
            if (this.$store.state.userIdentity == 1) {
              this.getGtypeData();
            }
          });
      },
      /**
       * @desc 获取市场下拉列表
       */
      getMarketData() {
        axios.post(api.dropdown.newMarketList).then((res) => {
          this.marketList = res;
        });
      },
      /**
       * @desc 获取市场下拉列表
       */
      getGtypeData() {
        axios.post(api.market.queryGtype, this.$qs.stringify({
          farmer: this.inputModel.farmer || ""
        })).then((response) => {
          this.gtypeList = response;
        });
      },
      /**
       * @desc 切换市场
       */
      handleChange() {
        if (this.$store.state.userIdentity == 1) {
          this.gtypeList = [];
          this.inputModel.businessScopes = [];
          if (this.inputModel.farmer) {
            this.getGtypeData();
          }
        }
      },
      /**
       *@desc 证件照图片修改时间
       */
      personChange(imgUrl) {
        this.personImg = imgUrl;
      },
      /**
       *@desc 图片修改事件
       */
      logoChange(imgUrl) {
        this.logoImg = imgUrl;
      },
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          shortContent: '',
          addr: '',
          bankBranchName: '',
          bankNo: '',
          bankcard: '',
          bankname: '',
          bankuser: '',
          bankuserMoblie: '',
          zxIdentity: '',
          businessScopes: [],
          businessScopesName: '',
          commissionRate: 0,
          farmer: '',
          files: [],
          mktVendorBigData: {
            content: '',
            pkey: '',
          },
          mobile: '',
          name: '',
          displayName: '',
          booth: '',
          pkey: '',
        };
        this.$nextTick(() => {
          setTimeout(() => {
            this.$refs.logo.updateImg('');
            this.$refs.person.updateImg('');
            this.$refs.editor.updateUEContent('');
          }, 0);
        });
        this.bankuser = '';
        this.zxIdentity = '';
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel
      }) {
        if (this.isEdit) {
          this.bankuser = JSON.parse(JSON.stringify(inputModel.bankuser));
          this.zxIdentity = JSON.parse(JSON.stringify(inputModel.zxIdentity));
        }
        this.inputModel = inputModel;
        this.inputModel.files.map((item) => {
          if (item.type == 'HEAD_ICON') {
            this.logoImg = [item.url];
          } else if (item.type == 'VIDEO') {
            this.video = [item.url];
          } else {
            this.personImg.push(item.url);
          }
        });
        this.$nextTick(() => {
          this.$refs.logo.updateImg(this.logoImg);
          this.$refs.person.updateImg(this.personImg);
          setTimeout(() => {
            this.$refs.editor.updateUEContent(
              this.inputModel.mktVendorBigData.content ?
              this.inputModel.mktVendorBigData.content :
              ''
            );
          }, 300);
        });
      },
      /**
       * 处理提交
       */
      handleSubmit: function () {
        if (this.$store.state.userIdentity == 1 && !this.inputModel.farmer) {
          this.$message.warning('请选择市场');
          return;
        }
        if (!this.inputModel.name) {
          this.$message.warning('请输入商户名称');
          return;
        }
        if (!this.inputModel.mobile) {
          this.$message.warning('请输入商户手机号码');
          return;
        }
        if (!utils.checkMobile(this.inputModel.mobile)) {
          this.$message.warning('请输入正确的手机号码');
          return;
        }
        // if (
        //   this.inputModel.zxIdentity &&
        //   !utils.IdentityCodeValid(this.inputModel.zxIdentity)
        // ) {
        //   this.$message.warning('请输入正确的开户人身份证号');
        //   return;
        // }
        // if (
        //   this.inputModel.bankuserMoblie &&
        //   !utils.checkMobile(this.inputModel.bankuserMoblie)
        // ) {
        //   this.$message.warning('请输入正确的银行卡绑定手机号');
        //   return;
        // }

        if (
          !this.inputModel.businessScopes ||
          !this.inputModel.businessScopes.length
        ) {
          this.$message.warning('请选择经营范围');
          return;
        }
        if (!utils.checkMobile(this.inputModel.mobile)) {
          this.$message.warning('请输入正确的手机号码');
          return;
        }
        this.inputModel.files = [];
        if (
          (typeof this.logoImg == 'string' && this.logoImg) ||
          this.logoImg.length
        ) {
          this.inputModel.files = [{
            type: 'HEAD_ICON',
            url: this.logoImg[0],
          }, ];
        }
        if (this.personImg) {
          this.personImg.map((item) => {
            this.inputModel.files.push({
              type: 'PROPAGANDA',
              url: item,
            });
          });
        }
        if ((typeof this.video == 'string' && this.video) || this.video.length) {
          this.inputModel.files.push({
            type: 'VIDEO',
            url: typeof this.video == 'string' ? this.video : this.video[0],
          });
        }
        this.inputModel.mktVendorBigData.content =
          this.$refs.editor.getUEContent();

        this.$emit('confirm', {
          inputModel: this.inputModel,
        });
      },
      /**
       * @desc 取消
       */
      handleCancel() {
        if (this.$store.state.userIdentity == 1) {
          this.$router.push('/vendor/marketMerchant');
        } else {
          this.$router.push('/vendor/merchant');
        }
      },
    },
    props: {
      title: {
        type: String,
        default: '新增',
      },
      isEdit: {
        type: Boolean,
        default: false,
      },
    },
  };
</script>
<style lang="less" scoped>
  /deep/.form-footer {
    .el-form-item__content {
      text-align: center;
    }
  }

  /deep/.zy-form-columns {
    display: flex;
    flex-wrap: wrap;
    flex-direction: column;

    &>.el-form-item {
      margin-bottom: 20px;
      width: 50%;

      .el-select {
        width: 100%;
      }
    }

    .rate-item {
      .el-input {
        width: 50%;
      }

      span {
        padding-left: 20px;
      }
    }

    .flex-box {
      display: flex;

      &>.el-form-item {
        width: 50%;
      }
    }

    .other-label {
      .tips {
        padding-left: 20px;
        line-height: normal;
        color: #a1a1a1;
      }
    }

    .tips-p {
      line-height: normal;
      margin-top: 5px;
    }
  }
</style>