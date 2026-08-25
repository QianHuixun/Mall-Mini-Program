<template>
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false"
    append-to-body @closed="handleCancel"
  >
    <el-form>
      <el-form-item label="商户" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.vendor" ref="vendorSelect" placeholder="请选择商户" filterable @change="getGoods">
          <el-option :value="item.pkey" :label="item.name" v-for="item in vendorList" :key="item.pkey"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商户标签" :label-width="labelWidth" :required="true">
        <el-tag :key="tag" v-for="tag in this.inputModel.labels" closable :disable-transitions="false" @close="handleClose(tag)">
          {{tag}} 
        </el-tag>
        <el-input class="input-new-tag" v-model="inputValue" size="small" placeholder="请输入商户标签，按回车完成"
          ref="saveTagInput" @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm">
        </el-input>
      </el-form-item>
      <el-form-item label="展示类型-1" :label-width="labelWidth" :required="true">
        <el-radio-group v-model="inputModel.showType1" @change="handleTypeChange('showContent1')">
          <el-radio label="SHOW_GOODS">显示商品</el-radio>
          <el-radio label="SHOW_PHOTO">显示图片</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="展示内容" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.showContent1" v-if="inputModel.showType1 == 'SHOW_GOODS'" ref="content1Select" placeholder="请选择商品">
          <el-option :value="item.pkey" :label="item.title" v-for="item in goodsList" :key="item.pkey"></el-option>
        </el-select>
        <img-upload ref="ImgUpload1" v-else :limit="1" isCube :autoCropWidth='400' :autoCropHeight='650' @change="changeImg($event, 'showContent1')"></img-upload>
        <div class="tips" v-if="inputModel.showType1 == 'SHOW_PHOTO'">建议尺寸400*650像素</div>
      </el-form-item>
      <el-form-item label="展示类型-2" :label-width="labelWidth" :required="true">
        <el-radio-group v-model="inputModel.showType2" @change="handleTypeChange('showContent2')">
          <el-radio label="SHOW_GOODS">显示商品</el-radio>
          <el-radio label="SHOW_PHOTO">显示图片</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="展示内容" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.showContent2" v-if="inputModel.showType2 == 'SHOW_GOODS'" ref="content2Select" placeholder="请选择商品">
          <el-option :value="item.pkey" :label="item.title" v-for="item in goodsList" :key="item.pkey"></el-option>
        </el-select>
        <img-upload ref="ImgUpload2" v-else :limit="1" isCube :autoCropWidth='400' :autoCropHeight='650' @change="changeImg($event, 'showContent2')"></img-upload>
        <div class="tips" v-if="inputModel.showType2 == 'SHOW_PHOTO'">建议尺寸400*650像素</div>
      </el-form-item>
      <el-form-item label="排序" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.sort" placeholder="请输入排序" ref="sortInput"
          v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.sort = val;}"
        ></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button size="medium" @click="handleCancel"> 取 消 </el-button>
      <el-button size="medium" type="primary" @click="handleSubmit" :loading="loading"> 确 定 </el-button>
    </div>
  </el-dialog>
</template>

<script>
import dropdown from '@/assets/js/dropdown';
import ImgUpload from "@/components/global/ImgUpload";
export default {
  data() {
    return {
      visible: false,
      loading: false,
      labelWidth: '100px',
      inputModel: {
        vendor: "",
        labels: [],
        showType1: "SHOW_GOODS",
        showContent1: "",
        photo1: "",
        showType2: "SHOW_GOODS",
        showContent2: "",
        photo1: "",
        sort: 0,
        pkey: "",
      },
      vendorList: [],
      goodsList: [],
      inputValue: "",
    }
  },
  mounted() {
    // dropdown.getVendorList().then((result) => {
    //   this.vendorList = result;
    // });
    this.getVendorList()
  },
  methods: {
    show() {
      this.visible = true
    },
    update(row) {
      this.inputModel = row
      this.getGoods()
      this.$nextTick(() => {
        if(this.inputModel.showType1 == 'SHOW_PHOTO') {
          this.$refs.ImgUpload1.updateImg(row.showContent1)
        } else {
          this.inputModel.showContent1 = parseInt(this.inputModel.showContent1)
        }
        if(this.inputModel.showType2 == 'SHOW_PHOTO') {
          this.$refs.ImgUpload2.updateImg(row.showContent2)
        } else {
          this.inputModel.showContent2 = parseInt(this.inputModel.showContent2)
        }
      })
      
    },
    clearData() {
      this.inputModel = {
        vendor: "",
        labels: [],
        showType1: "SHOW_GOODS",
        showContent1: "",
        photo1: "",
        showType2: "SHOW_GOODS",
        showContent2: "",
        photo1: "",
        sort: 0,
        pkey: "",
      }
    },
    getVendorList() {
      const params = {enabled:true}
      axios.post(api.data.queryMerchant, this.$qs.stringify(params))
        .then(res => this.vendorList = res)
    },
    getGoods() {
      const params = {vendor: this.inputModel.vendor}
      axios.post(api.vendor.boutiqueGoods, this.$qs.stringify(params))
        .then(res => this.goodsList = res)
    },
    handleClose(tag) {
      this.inputModel.labels.splice(this.inputModel.labels.indexOf(tag), 1);
    },
    handleInputConfirm() {
      let inputValue = this.inputValue;
      const str = inputValue.trim()
      console.log(str);
      if(str === '') return this.inputValue = '';
      if (inputValue) {
        this.inputModel.labels.push(inputValue);
      }
      this.inputValue = '';
    },
    handleTypeChange(name) {
      this.inputModel[name] = ''
    },
    changeImg(imgUrl, name) {
      this.inputModel[name] = imgUrl[0];
    },
    handleCancel() {
      this.clearData()
      this.visible = false
    },
    handleSubmit() {
      if(!this.inputModel.vendor) {
        this.$message.error('请选择商户');
        this.$refs.vendorSelect.focus();
        return;
      }
      if(!this.inputModel.labels.length) {
        this.$message.error('请输入商户标签');
        this.$refs.saveTagInput.focus();
        return;
      }
      if(!this.inputModel.showContent1) {
        this.$message.error(this.inputModel.showType1 =='SHOW_GOODS' ? '请选择商品' : '请上传图片');
        if(this.inputModel.showType1 =='SHOW_GOODS') this.$refs.content1Select.focus();
        return;
      }
      if(!this.inputModel.showContent2) {
        this.$message.error(this.inputModel.showType2 =='SHOW_GOODS' ? '请选择商品' : '请上传图片');
        if(this.inputModel.showType2 =='SHOW_GOODS') this.$refs.content2Select.focus();
        return;
      }
      if(!this.inputModel.sort === '') {
        this.$message.error('请输入排序');
        this.$refs.sortInput.focus();
        return;
      }
      this.$emit('confirm', {inputModel: this.inputModel})
    },
  },
  components: {
    ImgUpload,
  },
  props: {
    title: {
      type: String,
      default: "新增",
    },
  },
};
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
.el-tag + .input-new-tag {
  margin-left: 10px;
}
.input-new-tag {
  width: 300px;
}
</style>
