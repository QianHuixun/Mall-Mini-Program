<!-- 
@name: AdsUpdate.vue 
@description: 广告管理--编辑模板 
@author: sx
@date: 2020/06/29
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="市场" :label-width="labelWidth" :required="true" v-if="$store.state.userIdentity==1">
        <el-select v-model="farmers" placeholder="请选择市场" multiple ref="farmersInput" @change="marketChange">
          <el-option  value="-1" label="全部" ></el-option>
          <el-option v-for="(item,index) in marketList" :value="item.pkey" :label="item.name" :key="index" :disabled="marketDisabed"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="展示位置" v-if="inputModel.position === 'ADVERT_POSITION_COM'" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.locationType" placeholder="请选择展示位置">
          <el-option v-for="item in locationList" :label="item.label" :value="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入名称"></el-input>
      </el-form-item>
      <el-form-item label="图片" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
        <div class="tips" v-if="inputModel.position === 'ADVERT_POSITION_COM' && inputModel.locationType">
          推荐尺寸：{{ recommendSize[inputModel.locationType] }}
        </div>
      </el-form-item>
      <click-effect ref="ClickEffect" :inputModel.sync="inputModel"></click-effect>
      <el-form-item label="排序" :label-width="labelWidth">
        <el-input v-model="inputModel.sort" ref="sortInput" placeholder="请输入排序"
          v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.sort =val;}"></el-input>
      </el-form-item>
      <el-form-item v-if="inputModel.position == 'ADVERT_POSITION_COM'" label="可见用户" :label-width="labelWidth">
        <el-radio-group v-model="inputModel.visibleRange">
          <el-radio label="ALL">全部用户</el-radio>
          <el-radio label="TAG">
            指定标签
            <el-select v-model="inputModel.targerKeys" clearable filterable multiple collapse-tags ref="targerKeysSelect"
              placeholder="请选择" @change="handleTagsChange">
              <el-option value="all" label="全部"></el-option>
              <el-option v-for="item in TagsList" :key="item.pkey" :label="item.name" :value="item.pkey">
              </el-option>
            </el-select>
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="hide">
        取 消
      </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
        确 定
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import dropdown from '@/assets/js/dropdown';
import ImgUpload from '@/components/global/ImgUpload';
import ClickEffect from '@/components/global/ClickEffect';
export default {
  data() {
    return {
      labelWidth: '100px',
      visible: false,
      loading: false,
      inputModel: {
        farmers: [],
        position: '',
        name: '',
        photo: '',
        urlType: 'NOT_URL',
        objKey: '',
        sort: 0,
        enabled: true,
        visibleRange: 'ALL',
      },
      farmers: [],
      marketDisabed: false,
      marketList: [],
      props: {
        value: 'pkey',
        label: 'name',
        children: 'goodsList',
        checkStrictly: true,
      }, //级联选择器配置
      activityList:[],
      TagsList: [],
      locationList: [
        {label: '左', value: 'LEFT'},
        {label: '右上', value: 'UPPERRIGHT'},
        {label: '中', value: 'CEZONTER'},
        {label: '右下', value: 'LOWERRIGHT'},
      ],
      recommendSize: {
        LEFT: '340*448',
        UPPERRIGHT: '340*216',
        CEZONTER: '160*216',
        LOWERRIGHT: '160*216',
      }
    };
  },
  mounted() {},
  components: {
    ImgUpload,
    ClickEffect,
  },
  methods: {
    /**
     * @Desc
     */
    handleChange(e) {
      console.log(e);
    },
    marketChange(val) {
      if (val.includes('-1')) {
        this.farmers = ['-1'];
        this.marketDisabed = true;
      } else {
        this.marketDisabed = false;
      }
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
     * 图片修改事件
     */
    changeImg: function (imgUrl) {
      this.inputModel.photo = imgUrl[0];
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        farmers: [],
        position: '',
        name: '',
        photo: '',
        urlType: 'NOT_URL',
        objKey: '',
        sort: 0,
        enabled: true,
        visibleRange: 'ALL',
      };
      this.marketDisabed = false;
      this.farmers = [];
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg('');
      });
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      this.inputModel = inputModel;
      if (this.inputModel.urlType == 'GTYPE') {
        this.inputModel.objKey = this.inputModel.objKey
          .split(',')
          .map((item) => {
            return Number(item);
          });
      }
      this.farmers = this.inputModel.farmers;
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg(this.inputModel.photo);
      })
    },
    show: function () {
      this.visible = true;
      if (this.$store.state.userIdentity == 1) {
        this.getMarketData();
      }
      dropdown.getTagsList().then((result) => {
        this.TagsList = result;
      });
      this.clearData();
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
      this.$emit('hide');
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      let inputModel = JSON.parse(JSON.stringify(this.inputModel));

      if (this.$store.state.userIdentity == 1 && !this.farmers.length) {
        this.$message.error('请选择市场');
        this.$refs.farmersInput.focus();
        return;
      }
      if (this.$store.state.userIdentity == 1 && this.farmers.includes('-1')) {
        inputModel.farmers = this.marketList.map((item) => {
          return item.pkey;
        });
      } else if (this.$store.state.userIdentity == 1) {
        inputModel.farmers = this.farmers;
      }
      if (!this.inputModel.name) {
        this.$message.error('请输入名称');
        this.$refs.nameInput.focus();
        return;
      }

      if (!this.inputModel.photo) {
        this.$message.error('请选择图片');
        this.$refs.photoInput.focus();
        return;
      }
      if(this.$refs.ClickEffect.validate() === false) {
        return
      }
      if (this.inputModel.urlType == 'GTYPE') {
        inputModel.objKey = inputModel.objKey.join(',');
      }
      if (!this.inputModel.sort) {
        inputModel.sort = 0;
      }
      if(this.inputModel.visibleRange == 'TAG' && (!this.inputModel.targerKeys || !this.inputModel.targerKeys.length)) {
        this.$message.error('请选择指定标签用户');
        return;
      }
      this.$emit('confirm', {
        inputModel: inputModel,
      });
    },
    handleTagsChange(e) {
      if(e && e.length && e.includes('all')) {
        this.inputModel.targerKeys = this.TagsList.map(item => item.pkey)
      }
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
  },
};
</script>
<style lang="less" scoped>
/deep/.el-select .el-input {
  height: auto !important;
}
.el-radio {
  display: block;

  .el-input {
    width: 80px;
    margin: 8px;
  }

  .el-select {
    display: inline-block !important;
    margin:8px;
    width: 300px;
  }

  .el-date-editor {
    width: 150px !important;
  }
}
</style>