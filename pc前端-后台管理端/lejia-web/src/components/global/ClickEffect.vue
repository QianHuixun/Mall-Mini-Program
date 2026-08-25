<template>
  <div>
    <el-form-item label="点击效果" :label-width="labelWidth">
      <el-select v-model="inputModel.urlType" placeholder="选择" @change="handleUrlTypeChange">
        <el-option :value="key" :key="index" :label="value" v-for="(value, key, index) in urlTypeObj">
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item :label="urlTypeObj[inputModel.urlType] || '内容'" :label-width="labelWidth" v-if="hasContent(inputModel.urlType)">
      <el-input v-if="['LINK', 'WEIXIN_MINI_PROGRAM'].includes(inputModel.urlType)" v-model="inputModel.objKey"
        ref="objKeyInput" :placeholder="inputModel.urlType == 'LINK' ? 'http://' : '请输入内容'"></el-input>
      <el-cascader v-if="inputModel.urlType == 'GTYPE'" v-model="inputModel.objKey" :options="typeList" filterable
        :props="props">
      </el-cascader>
      <goods-one-picker v-if="inputModel.urlType == 'GOODS'" ref="goodsPicker" @handle="getGoods"></goods-one-picker>
      <el-select v-if="inputModel.urlType == 'ACTIVITY'" v-model="inputModel.objKey" placeholder="选择">
        <el-option :value="item.pkey + ''" :key="index" :label="item.name" v-for="(item, index) in activityList">
        </el-option>
      </el-select>
      <el-select v-if="inputModel.urlType == 'VENDOR'" v-model="inputModel.objKey" placeholder="选择" @change="handleVendorChange">
        <el-option :value="item.vendor + ''" :key="item.vendor" :label="item.vendorName" v-for="item in vendorList">
        </el-option>
      </el-select>
    </el-form-item>
  </div>
</template>

<script>
import utils from '@/assets/js/utils';
import dropdown from '@/assets/js/dropdown';
import GoodsOnePicker from '@/components/global/GoodsOnePicker';
export default {
  props: {
    labelWidth: {
      type: String,
      default: '100px',
    },
    inputModel: {
      type: Object,
      default: () => {
        return {}
      }
    },
  },
  data() {
    return {
      urlTypeObj: utils.urlTypeObj(),
      typeList: [],
      props: {
        value: 'pkey',
        label: 'name',
        children: 'goodsList',
        checkStrictly: true,
      }, //级联选择器配置
      activityList: [],
      vendorList: [],
    }
  },
  watch: {
    'inputModel.urlType': {
      handler(value) {
        switch (value) {
          case 'GOODS':
            if (value == 'GOODS' && this.inputModel.goodsName) {
              this.updateGoods()
            }
            break;
          case 'ACTIVITY':
            this.getActivityData()
            break;
          case 'VENDOR':
            dropdown.getVendorList().then((result) => {
              this.vendorList = result;
            });
            break;
          default:
            break;
        }
      }
    }
  },
  components: {
    GoodsOnePicker,
  },
  mounted() {
    let params = {
      showMarket: true,
    };
    dropdown.getType(params).then((result) => {
      this.typeList = result.content;
    });
  },
  methods: {
    handleUrlTypeChange() {
      this.inputModel.objKey = ''
    },
    hasContent(type) {
      return utils.hasContent(type)
    },
    getGoods(pkey) {
      this.inputModel.objKey = pkey
    },
    updateGoods() {
      this.$nextTick(() => {
        this.$refs.goodsPicker.updateGoods({
          goodsInfo: {
            pkey: this.inputModel.objKey,
            name: this.inputModel.goodsName
          },
        });
      })
    },
    getActivityData() {
      axios.post(api.mall.activityList, this.$qs.stringify({
        enabled: true,
      })).then((res) => {
        this.activityList = res;
      });
    },
    handleVendorChange(val) {
      console.log(val);
      // let vendorName = this.vendorList.find(item => item.vendor == val).vendorName
      // this.inputModel.objKeyName = vendorName
    },
    validate() {
      if(['LINK', 'GOODS', 'GTYPE', 'ACTIVITY', 'VENDOR', 'WEIXIN_MINI_PROGRAM'].includes(this.inputModel.urlType)) {
        if (!this.inputModel.objKey) {
          this.$message.error(`请输入${this.urlTypeObj[this.inputModel.urlType]}`);
          return false;
        }
      }
    }
  }
}
</script>

<style></style>