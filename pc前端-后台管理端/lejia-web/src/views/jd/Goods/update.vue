<template>
  <el-dialog title="编辑商品" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form label-width="80px">
      <el-form-item label="SPU">
        <el-input v-model="inputModel.spuId" readonly></el-input>
      </el-form-item>
      <el-form-item label="商品分类">
        <el-input v-model="inputModel.categoryName" readonly></el-input>
      </el-form-item>
      <el-form-item label="可见用户">
        <el-radio-group v-model="inputModel.visibleRange">
          <el-radio label="ALL">全部用户</el-radio>
          <el-radio label="TAG">
            指定标签
            <el-select v-model="inputModel.tagKeys" ref="tagKeysSelect" filterable multiple collapse-tags clearable
              placeholder="请选择" @change="handleTagsChange">
              <el-option value="all" label="全部"></el-option>
              <el-option v-for="item in TagsList" :key="item.pkey" :label="item.name" :value="item.pkey"></el-option>
            </el-select>
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div>
      <el-button type="primary" @click="handleShowBulkPricing">批量设置价格</el-button>
    </div>
    <el-table :data="inputModel.skuList">
      <el-table-column label="商品图片" prop="photo1" min-width="120">
        <template slot-scope="scope">
          <el-image v-if="scope.row.photo1" :src="scope.row.photo1[0]" style="width: 100px; height: 100px"
            :preview-src-list="scope.row.photo1">
          </el-image>
        </template>
      </el-table-column>
      <el-table-column v-for="(item, index) in flexibleColumns" :label="item.label" min-width="120" :key="item.key">
        <template slot-scope="scope">
          <span>{{ scope.row['spaceValue' + (index + 1)] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="京东价格" prop="salePrice" min-width="120"></el-table-column>
      <el-table-column label="商城销售价格" prop="price" min-width="200">
        <template slot-scope="scope">
          <el-input v-model="scope.row.price" placeholder="输入商城销售价格"
            @input="scope.row.price = formatPrice(scope.row.price)">
            <template slot="prepend">￥</template>
          </el-input>
        </template>
      </el-table-column>
      <el-table-column label="京东状态" prop="skuState" min-width="120">
        <template slot-scope="scope">
          <span>{{ scope.row.skuState == 1 ? '上架' : scope.row.skuState == 0 ? '下架' : '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="enabled" min-width="120">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.enabled" active-color="#13ce66"
            @change="handleEnabledChange(scope.row)"></el-switch>
        </template>
      </el-table-column>
    </el-table>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="handleClose">
        取 消
      </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading">
        确 定
      </el-button>
    </div>
    <bulk-pricing ref="BulkPricing" @confirm="handleBullPricingSet"></bulk-pricing>
  </el-dialog>
</template>

<script>
import utils from '@/assets/js/utils';
import BulkPricing from './BulkPricing.vue'
export default {
  data() {
    return {
      visible: false,
      loading: false,
      spuId: null,
      inputModel: {},
      TagsList: [],
      flexibleColumns: [],
    }
  },
  components: {
    BulkPricing,
  },
  mounted() {
    this.getTagsList()
  },
  methods: {
    show({ spuId }) {
      this.visible = true
      this.spuId = spuId
      this.getData()
      this.getTagsList()
    },
    getData() {
      const url = api.jd.spuGoodsQuery
      const params = { spuId: this.spuId }
      axios.post(url, this.$qs.stringify(params))
        .then((response) => {
          this.inputModel = response
          this.getFlexibleColumns()
        });
    },
    getFlexibleColumns() {
      this.flexibleColumns = []
      const item = this.inputModel.skuList[0]
      for (let key in item) {
        if (key.includes('space') && !key.includes('spaceValue')) {
          if (item[key]) {
            this.flexibleColumns.push({
              key,
              label: item[key]
            })
          }
        }
      }
    },
    /**
     * 获取热力豆用户标签
     */
    getTagsList() {
      const params = { types: "MSD" };
      axios.post(api.marketing.msdTagDrop, this.$qs.stringify(params))
        .then((response) => {
          this.TagsList = response
        });
    },
    /**
     * 可见用户标签
     */
    handleTagsChange(e) {
      console.log(e);
      if (e && e.length && e.includes('all')) {
        this.inputModel.tagKeys = this.TagsList.map(item => item.pkey)
      }
    },
    /**
     * 关闭弹窗
     */
    handleClose() {
      this.visible = false
      this.inputModel = {}
      this.flexibleColumns = []
    },
    /**
     * 切换上下架状态
     */
    handleEnabledChange({ pkey, enabled }) {
      let url = api.jd.spuGoodsEnable;
      const params = {
        pkeys: pkey,
        enabled
      }
      axios.post(url, this.$qs.stringify(params))
        .then(() => {
          this.getData()
        });
    },
    /**
     * 确认提交
     */
    handleSubmit() {
      if(this.inputModel.visibleRange == 'TAG' && (!this.inputModel.tagKeys || !this.inputModel.tagKeys.length)) {
        this.$message.warning("请选择可见用户标签")
        return
      }
      let validate = true
      this.inputModel.skuList.map(item => {
        if (!item.price) validate = false
      })
      if (!validate) {
        this.$message.warning("请输入商城销售价")
        return
      }
      let url = api.jd.spuGoodsUpd;
      axios.post(url, this.inputModel)
        .then(() => {
          this.$message.success('编辑成功')
          this.handleClose()
        });
    },
    /**
     * 批量设置价格
     */
    handleShowBulkPricing() {
      this.$refs.BulkPricing.show()
    },
    handleBullPricingSet(percentage) {
      console.log(percentage);
      const product = 1 + (percentage / 100)
      this.inputModel.skuList = this.inputModel.skuList.map(item => {
        item.price = (Math.round((item.salePrice * product) * 100) / 100).toFixed(2);
        return item
      })
      this.$refs.BulkPricing.handleClose()
    },
    /**
     * 格式化价格
     */
    formatPrice: function (price) {
      return utils.formatPrice(price);
    },
  }
}
</script>

<style lang="less" scoped>
.el-radio {
  display: block;

  .el-input {
    width: 80px;
    margin: 8px;
  }

  .el-select {
    display: inline-block !important;
    margin: 8px;
    width: 300px;
  }

  .el-date-editor {
    width: 150px !important;
  }
}
</style>