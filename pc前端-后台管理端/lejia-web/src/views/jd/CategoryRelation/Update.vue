<template>
  <el-dialog :title="isEdit ? '编辑关联' : '新增关联分类'" center :visible.sync="visible" :closeOnClickModal="false" width="500px">
    <el-form label-width="100px">
      <el-form-item label="京东分类">
        <el-input v-model="inputModel.jdCategoryName" readonly></el-input>
      </el-form-item>
      <el-form-item label="商城分类" :required="true">
        <el-cascader v-model="inputModel.mallCategory" :options="mallCategoryList" :props="mallProps" clearable placeholder="商城分类"></el-cascader>
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
export default {
  data() {
    return {
      visible: false,
      loading: false,
      isEdit: false,
      inputModel: {
        id: '',
        jdCategory: '',
        jdCategoryName: '',
        mallCategory: '',
      },
      mallCategoryList: [],
      mallProps: {
        value: 'pkey',
        label: 'name',
        children: 'goodsList',
        emitPath: false,
        checkStrictly: false
      }
    }
  },
  methods: {
    /**
     * 打开弹窗 - 编辑模式
     */
    show(row) {
      console.log(row);
      
      this.inputModel = {
        jdCategory: row.pkey || '',
        jdCategoryName: row.name || '',
        mallCategory: row.mallCategory || '',
      };
      this.visible = true;
      this.getMallCategoryList();
    },
    hide() {
      this.visible = false;
      this.inputModel = {
        jdCategory: '',
        jdCategoryName: '',
        mallCategory: '',
      };
    },
    /**
     * 获取商城分类下拉列表
     */
    getMallCategoryList() {
      // 使用平台商品分类下拉接口
      // axios.post(api.goods.sysGoodsList)
      //   .then((res) => {
      //     this.mallCategoryList = res || [];
      //   });
      dropdown.getType().then((result) => {
        this.mallCategoryList = result.content;
      });
    },
    /**
     * 提交保存
     */
    handleSubmit() {
      console.log(this.inputModel);
      
      if (!this.inputModel.mallCategory) {
        this.$message.warning('请选择商城分类');
        return;
      }
      this.loading = true;
      const params = {
        jdCategory: this.inputModel.jdCategory,
        mallCategory: this.inputModel.mallCategory,
      };
      let url = api.jd.categoryRelationUpd;
      axios.post(url, this.$qs.stringify(params))
        .then((res) => {
          if (res || res === undefined) {
            this.$message.success(this.isEdit ? '修改成功' : '新增成功');
            this.hide();
            this.$emit('refresh');
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
  },
};
</script>
