<!-- 
@name: AdsUpdate.vue 
@description: 广告管理--编辑模板 
@author: sx
@date: 2020/06/29
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <!-- <el-form-item label="市场" :label-width="labelWidth" :required="true" v-if="$store.state.userIdentity==1">
        <el-select v-model="farmers" placeholder="请选择市场" multiple ref="farmersInput" @change="marketChange">
          <el-option  value="-1" label="全部" ></el-option>
          <el-option v-for="(item,index) in marketList" :value="item.pkey" :label="item.name" :key="index" :disabled="marketDisabled"></el-option>
        </el-select>
      </el-form-item> -->
      <el-form-item label="名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入名称"></el-input>
      </el-form-item>
      <el-form-item label="图片" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
        <div class="tips">推荐尺寸：550*148px</div>
      </el-form-item>
      <el-form-item label="展示分类" :label-width="labelWidth" required>
        <el-cascader v-model="inputModel.positionObj" :options="typeList" filterable :props="props">
        </el-cascader>
      </el-form-item>
      <click-effect ref="ClickEffect" :inputModel.sync="inputModel"></click-effect>
      <el-form-item label="排序" :label-width="labelWidth">
        <el-input v-model="inputModel.sort" ref="sortInput" placeholder="请输入排序"
          v-on:input="limitInput($event)"></el-input>
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
import ImgUpload from '@/components/global/ImgUpload';
import ClickEffect from '@/components/global/ClickEffect';
import dropdown from '@/assets/js/dropdown';
import qs from 'qs';
export default {
  data() {
    return {
      labelWidth: '100px',
      visible: false,
      loading: false,
      inputModel: {
        farmers: [],
        position: this.$store.state.userIdentity == 1 ? 'ADVERT_POSITION_MSD_GOODS_MAIN' : 'ADVERT_POSITION_GOODS_MAIN',
        name: '',
        photo: '',
        urlType: 'NOT_URL',
        objKey: '',
        sort: 0,
        enabled: true,
        positionObj: ""
      },
      typeList: [],
      farmers: [],
      marketDisabled: false,
      marketList: [],
      props: {
        value: 'pkey',
        label: 'name',
        children: 'goodsList',
        checkStrictly: false,
      }, //级联选择器配置
    };
  },
  mounted() {},
  components: {
    ImgUpload,
    ClickEffect,
  },
  methods: {
    limitInput(val){
      val =val.replace(/[^\d]/g,'');
      this.inputModel.sort =val;
    },
    marketChange(val) {
      if (val.includes('-1')) {
        this.farmers = ['-1'];
        this.marketDisabled = true;
      } else {
        this.marketDisabled = false;
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
        position: this.$store.state.userIdentity == 1 ? 'ADVERT_POSITION_MSD_GOODS_MAIN' : 'ADVERT_POSITION_GOODS_MAIN',
        name: '',
        photo: '',
        urlType: 'NOT_URL',
        objKey: '',
        sort: 0,
        enabled: true,
        positionObj: ""
      };
      this.marketDisabled = false;
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
      if (this.inputModel.positionObj) {
        this.inputModel.positionObj = this.inputModel.positionObj
          .split(',')
          .map((item) => {
            return Number(item);
          });
      }

      console.log(inputModel, this.inputModel.objKey);
      this.farmers = this.inputModel.farmers;
      this.$refs.ImgUpload.updateImg(this.inputModel.photo);
    },
    show: function () {
      console.log(this.inputModel);
      
      this.visible = true;
      if (this.$store.state.userIdentity == 1) {
        this.getMarketData();
      }
      let params = {
        showMarket: true,
      };
      dropdown.getType(params).then((result) => {
        this.typeList = result.content;
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

      // if (this.$store.state.userIdentity == 1 && !this.farmers.length) {
      //   this.$message.error('请选择市场');
      //   this.$refs.farmersInput.focus();
      //   return;
      // }
      // if (this.$store.state.userIdentity == 1 && this.farmers.includes('-1')) {
      //   inputModel.farmers = this.marketList.map((item) => {
      //     return item.pkey;
      //   });
      // } else if (this.$store.state.userIdentity == 1) {
      //   inputModel.farmers = this.farmers;
      // }
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
      if (!this.inputModel.positionObj) {
        this.$message.error('请选择展示分类');
        return;
      }
      inputModel.positionObj = inputModel.positionObj.join(',');

      if(this.$refs.ClickEffect.validate() === false) {
        return
      }
      if (this.inputModel.urlType == 'GTYPE') {
        inputModel.objKey = inputModel.objKey.join(',');
      }
      if (!this.inputModel.sort) {
        inputModel.sort = 0;
      }
      this.$emit('confirm', {
        inputModel: inputModel,
      });
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
</style>