<!-- 
@name: UpdComp.vue 
@description: 商品供应商 -- 更新组件 
@author: crj
@date: 2021/10/09
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" width="850px" @close="hide">
    <el-form>
      <el-form-item label="选择市场" :label-width="labelWidth" :required="$store.state.userIdentity==1"
        v-if="$store.state.userIdentity==1">
        <el-select v-model="inputModel.marketPkey" placeholder="请选择市场" @change="marketChange" :disabled="isEdit">
          <el-option v-for="(item,index) in marketList" :key="index" :label="item.name" :value="item.pkey">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="选择类型" :label-width="labelWidth">
        <el-select v-model="inputModel.mtype" placeholder="请选择商品类型" @change="mtypeChange" :disabled="isEdit">
          <el-option v-for="(item,index) in mtypeList" :key="index" :label="item.name" :value="item.pkey">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="选择商品" :label-width="labelWidth" :required="true">
        <el-cascader v-model="inputModel.goodsPkey" :options="goodsList" @change="handleGoodsChange" :disabled="isEdit"
          :props="{ children: 'sub',value:'pkey',label:'name',emitPath:false}"></el-cascader>
      </el-form-item>
      <el-form-item v-if="settlementMethod == 'COMMISSION_SETTLEMENT'" label="请输入费率" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.commissionRate2" placeholder="请输入费率" v-on:input="(val) => {inputModel.commissionRate2 = commissionRateInput(val);}"></el-input>
      </el-form-item>
      <el-form-item :label-width="labelWidth">
        <div slot="label">
          <div class="reform-info-box">
            <span>采购信息</span>
          </div>
          <div class="reform-info-add-btn">
            <el-button type="text" size="mini" @click="handleAdd">
              增加一条
            </el-button>
          </div>
        </div>
        <el-table :data="inputModel.list" border style="width: 100%">
          <el-table-column label="选择规格">
            <template slot-scope="scope">
              <el-select v-model="scope.row.space" @change="handleChange" placeholder="请选择规格" :ref="`spaceInput${scope.$index}`"
                :disabled="!inputModel.goodsPkey">
                <el-option :label="item.name" :value="item.pkey" v-for="(item,index) in spaceList" :key="index">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="供应商">
            <template slot-scope="scope">
              <el-select v-model="scope.row.vendor" placeholder="请选择供应商" @change="handleChange" :ref="`vendorInput${scope.$index}`">
                <el-option :label="item.name+(item.isExist?'':'(供应商不存在)')" :value="item.pkey" v-for="(item,index) in vendorList" :key="index" :disabled="!item.isExist">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column v-if="settlementMethod == 'PURCHASE_SETTLEMENT'" label="采购价（元）">
            <template slot-scope="scope">
              <el-input type="number" v-model="scope.row.purchasingPrice" :ref="`priceInput${scope.$index}`" placeholder="请输入采购价" v-on:input="(val) => {scope.row.purchasingPrice = inputNumberFixed(val);}">
              </el-input>
            </template>
          </el-table-column>
          <el-table-column v-if="settlementMethod == 'COMMISSION_SETTLEMENT'" label="费率">
            <template slot-scope="scope">
              <el-input type="number" v-model="scope.row.commissionRate1" :ref="`priceInput${scope.$index}`" placeholder="请输入费率" v-on:input="(val) => {scope.row.commissionRate1 = commissionRateInput(val);}">
              </el-input>
            </template>
          </el-table-column>
          <el-table-column label="采购派单顺序">
            <template slot-scope="scope">
              <el-input type="number" v-model="scope.row.sort" :ref="`sortInput${scope.$index}`" @input="handleChange" placeholder="请输入采购派单顺序" v-on:input="(val)=>{ val =val.replace(/\D|^0/g,''); scope.row.sort =val;}"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right" v-if="inputModel.list.length!=1">
            <template slot-scope="scope">
              <el-button size="mini" type="danger" @click="handleDelete(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
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
import utils from '@/assets/js/utils';
import dropdown from '@/assets/js/dropdown';
export default {
  data() {
    return {
      labelWidth: '100px',
      visible: false,
      loading: false,
      inputModel: {
        mType: 'MARKET_GOODS',
        goodsPkey: '',
        marketPkey: '',
        commissionRate2: '',
        list: [
          {
            pkey: '',
            purchasingPrice: '',
            space: '',
            sort: '',
            vendor: '',
          },
          {
            pkey: '',
            purchasingPrice: '',
            space: '',
            sort: '',
            vendor: '',
          },
          {
            pkey: '',
            purchasingPrice: '',
            space: '',
            sort: '',
            vendor: '',
          },
        ],
      },
      mtypeList: [
        {
          name: '市场商品',
          pkey: 'MARKET_GOODS',
        },
        {
          name: '扶贫商品',
          pkey: 'POVERTY_ALLEVIATION_GOODS',
        },
        {
          name: '特价商品',
          pkey: 'SPECIAL_GOODS',
        },
        {
          name: '分享商品',
          pkey: 'SHARE_GOODS',
        },
        {
          name: '砍价商品',
          pkey: 'CUT_GOODS',
        },
        {
          name: '团购商品',
          pkey: 'COLLAGE_GOODS',
        },
        {
          name: '预售商品',
          pkey: 'PRESALE_GOODS',
        },
      ],
      saveGoods: [], //暂时保存的商品采购信息
      goodsList: [],
      marketList: [],
      vendorList: [],
      spaceList: [],
      goodsPkey: '',
      isEdit: false,
      noChange: true
    };
  },
  computed: {
    settlementMethod() {
      return this.$store.state.settlementMethod
    }
  },
  mounted() {},
  components: {},
  methods: {
    mtypeChange() {
      this.getGoodsList();
    },
    /**
     * @desc 市场发生改变
     */
    marketChange() {
      this.getGoodsList();
      this.getVendorList();
      /**判断是否需要保存数据 */
      let isSave = false;
      for (let i in this.inputModel.list) {
        let item = this.inputModel.list[i];
        if (item.purchasingPrice || item.space || item.sort || item.vendor) {
          isSave = true;
          break;
        }
      }
      if (isSave) {
        this.saveGoods.push({
          goodsPkey: this.inputModel.goodsPkey,
          list: this.inputModel.list,
        });
      }
      /** end 判断是否需要保存数据 */

      this.inputModel.goodsPkey = '';
      this.inputModel.list = [
        {
          pkey: '',
          purchasingPrice: '',
          space: '',
          sort: '',
          vendor: '',
        },
        {
          pkey: '',
          purchasingPrice: '',
          space: '',
          sort: '',
          vendor: '',
        },
        {
          pkey: '',
          purchasingPrice: '',
          space: '',
          sort: '',
          vendor: '',
        },
      ];
    },
    /**
     * @desc 精确到小数点后两位
     * @param {String} val  输入的值
     */
    inputNumberFixed(val) {
      val = utils.inputNumberFixed(val);
      return val;
    },
    commissionRateInput(val) {
      val = utils.inputNumberFixed(val);
      val = val > 100 ? 100 : val
      return val;
    },
    /**
     * @desc 获取商品下拉列表
     */
    getGoodsList() {
      let params = {
        marketPkey: this.inputModel.marketPkey,
        mType: this.inputModel.mtype,
      };
      axios
        .post(api.goods.supplyGoodsList, this.$qs.stringify(params))
        .then((res) => {
          this.goodsList = res.map((item) => {
            item.pkey = 'class_' + item.pkey;
            return item;
          });
        });
    },
    /**
     * @desc 获取市场下拉列表
     */
    getMarketList() {
      axios.post(api.dropdown.supplyMarketList).then((res) => {
        this.marketList = res;
      });
    },
    /**
     * @desc 获取规格下拉列表
     */
    getSpaceList() {
      let params = {
        goodsPkey: this.inputModel.goodsPkey,
      };

      axios
        .post(api.goods.supplySpaceList, this.$qs.stringify(params))
        .then((res) => {
          this.spaceList = res;
        });
    },
    /**
     * @desc 获取商户下拉列表
     */
    getVendorList() {
      let params = {
        marketPkey: this.inputModel.marketPkey,
      };
      axios
        .post(api.goods.supplyVendorList, this.$qs.stringify(params))
        .then((res) => {
          this.vendorList = res;
        });
    },
    /**
     *@desc 商品发生改变
     */
    handleGoodsChange(val) {
      if (val) {
        this.getSpaceList();
        /**判断是否需要保存数据 */
        let isSave = false;
        for (let i in this.inputModel.list) {
          let item = this.inputModel.list[i];
          if (item.purchasingPrice || item.space || item.sort || item.vendor) {
            isSave = true;
            break;
          }
        }
        if (isSave) {
          this.saveGoods.push({
            goodsPkey: this.goodsPkey ? this.goodsPkey : val,
            list: this.inputModel.list,
          });
        }
        /** end 判断是否需要保存数据 */
        this.goodsPkey = val;

        let list = '';
        for (let i in this.saveGoods) {
          let item = this.saveGoods[i];
          if (item.goodsPkey == val) {
            list = item.list;
          }
        }
        this.inputModel.list = list
          ? list
          : [
              {
                pkey: '',
                purchasingPrice: '',
                commissionRate1: '',
                space: '',
                sort: '',
                vendor: '',
              },
              {
                pkey: '',
                purchasingPrice: '',
                commissionRate1: '',
                space: '',
                sort: '',
                vendor: '',
              },
              {
                pkey: '',
                purchasingPrice: '',
                commissionRate1: '',
                space: '',
                sort: '',
                vendor: '',
              },
            ];
      }
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        goodsPkey: '',
        marketPkey: '',
        list: [
          {
            pkey: '',
            purchasingPrice: '',
            commissionRate1: '',
            space: '',
            sort: '',
            vendor: '',
          },
          {
            pkey: '',
            purchasingPrice: '',
            commissionRate1: '',
            space: '',
            sort: '',
            vendor: '',
          },
          {
            pkey: '',
            purchasingPrice: '',
            commissionRate1: '',
            space: '',
            sort: '',
            vendor: '',
          },
        ],
      };
      this.saveGoods = [];
      this.isEdit = false;
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      this.inputModel = inputModel;
      this.getSpaceList();
      this.getGoodsList();
      this.isEdit = true;
    },
    /**
     * @desc 显示并数据初始化
     */
    show: function (market) {
      console.log(market);
      this.visible = true;
      this.noChange = true
      if (market) {
        this.inputModel.marketPkey = market;
      }
      this.getMarketList();
      this.getVendorList();
      if (!this.needRefesh) this.getGoodsList();
    },
    /**
     * @desc 删除
     * @param {Number} index 要删除的下标
     */
    handleDelete(index) {
      let dataList = JSON.parse(JSON.stringify(this.inputModel.list));
      dataList.splice(index, 1);
      this.$set(this.inputModel, 'list', dataList);
    },
    /**
     * @desc 增加一条新的
     */
    handleAdd() {
      let dataList = JSON.parse(JSON.stringify(this.inputModel.list));
      dataList.push({
        pkey: '',
        purchasingPrice: '',
        commissionRate1: '',
        space: '',
        sort: '',
        vendor: '',
      });
      this.$set(this.inputModel, 'list', dataList);
    },
    handleChange() {
      this.noChange = false
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
      if (this.$store.state.userIdentity == 1 && !this.inputModel.marketPkey) {
        this.$message.warning('请选择市场');
        return;
      }
      if (!this.inputModel.goodsPkey) {
        this.$message.warning('请选择商品');
        return;
      }
      if (this.settlementMethod == 'COMMISSION_SETTLEMENT' && !this.inputModel.commissionRate2) {
        this.$message.warning('请输入费率');
        return;
      }
      let inputModel = JSON.parse(JSON.stringify(this.inputModel));
      for (let i = inputModel.list.length - 1; i >= 0; i--) {
        let item = inputModel.list[i];
        if (item.space || item.sort || item.vendor || item.purchasingPrice) {
          if (!item.space) {
            this.$message.warning('请选择规格');
            return;
          }
          if (!item.vendor) {
            this.$message.warning('请选择供应商');
            return;
          }
          if (this.settlementMethod == 'PURCHASE_SETTLEMENT' && !item.purchasingPrice && item.purchasingPrice != 0) {
            this.$message.warning('请输入采购价格');
            return;
          }
          if (!item.sort) {
            inputModel.list[i].sort = i + 1;
          }
        } else {
          inputModel.list.splice(i, 1);
        }
      }
      axios.post(api.goods.queryAutoConfig).then((res) => {
        console.log(res);
        if (res.hasOwnProperty('result')) {
          this.$emit('confirm', {
            inputModel,
          });
        } else {
          if(this.noChange) {
            this.$emit('confirm', {
              inputModel,
            });
          } else {
            this.$confirm(
              "<P sytle='text-indent:2em;margin-bootom:20px'>当前采购派单程序为“系统自动派单”，编辑内容提交后自动派单系统会按新保存的商户名单重新开始循环派单</p><P sytle='text-indent:2em'>是否确认提交更新？</p>",
              '提示',
              {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
                dangerouslyUseHTMLString: true,
              }
            ).then(() => {
              this.$emit('confirm', {
                inputModel,
              });
            });
          }
          
        }
      });
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
    needRefesh: {
      type: Boolean,
      default: false,
    },
  },
};
</script>
<style lang="less" scoped>
</style>
<style lang="less">
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
}

input[type='number'] {
  -moz-appearance: textfield;
}
</style>