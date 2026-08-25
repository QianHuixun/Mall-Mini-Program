<!-- 
@name: GoodsKcCheck.vue 
@description: 库存管理-盘点入库
@author: crj
@date: 2020/09/27
-->
<template>
  <el-dialog title="盘点入库" center :visible.sync="visible" :closeOnClickModal="false" width="850px"
    :modal-append-to-body="false" :append-to-body="true">
    <el-form>
      <el-form-item label="商品名" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.goodsName" ref="goodsNameInput" placeholder="请输入商品名称" disabled></el-input>
      </el-form-item>
      <el-form-item label="规格" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.space" placeholder="请选择" @change="handlechange">
          <el-option v-for="(item,index) in spaceList" :key="index" :label="item.spaceName" :value="item.space">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="当前库存" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.nowNum" ref="nowNumInput" placeholder="请输入当前库存" disabled></el-input>
      </el-form-item>
      <el-form-item label="实际库存" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.num" ref="numInput" placeholder="请输入实际库存" v-on:input="limitInput($event) ">
        </el-input>
      </el-form-item>
      <el-form-item label="备注" :label-width="labelWidth">
        <el-input v-model="inputModel.remark" ref="remarkInput" placeholder="请输入备注"></el-input>
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
  import qs from 'qs';
  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        inputModel: {
          goods: "",
          goodsName: "",
          nowNum: "",
          num: 0,
          remark: "",
          space: 0,
          wareType: 'INVENTORY'
        }, //输入框数据
        spaceList: [], //规格下拉列表
        numList: [], //数量列表
      }
    },
    methods: {
      limitInput(val) {
        val = val.replace(/[^\d]/g, '');
        this.inputModel.num = val;
      },
      /**规格选择改变 */
      handlechange(e) {
        let spaceList = this.spaceList
        for (let i = 0; i <= spaceList.length; i++) {
          if (spaceList[i].space == e) {
            this.inputModel.nowNum = spaceList[i].num;
            break;
          }
        }

      },
      /**弹出弹窗 */
      show: function ({
        goodsData
      }) {
        this.visible = true;
        this.clearData();
        this.initData(goodsData)
      },
      /**初始化数据 */
      initData(goodsData) {
        let inputModel = this.inputModel;
        inputModel.goodsName = goodsData.goodsName;
        inputModel.goods = goodsData.pkey;
        inputModel.wareType = 'INVENTORY';
        this.inputModel = inputModel;
        this.spaceList = goodsData.spaceList.map(item => {
          return {
            space: item.pkey,
            spaceName: item.space,
            num: item.kcNum
          }
        })

      },
      /**清除数据 */
      clearData() {
        this.loading = false;
        this.inputModel = {}
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.clearData();
        this.visible = false;
        this.$emit("hide");
      },
      /**提交 */
      handleSubmit() {
        let inputModel = this.inputModel;
        if (!inputModel.goodsName) {
          this.$message.error("请输入商品名");
          this.$refs.goodsNameInput.focus();
          return
        }
        if (!inputModel.space) {
          this.$message.error("请选择规格");
          return
        }
        // if (!inputModel.nowNum) {
        //   this.$message.error("请输入当前库存");
        //   this.$refs.nowNumInput.focus();
        //   return
        // }
        if (!inputModel.num) {
          this.$message.error("请输入实际库存");
          this.$refs.numInput.focus();
          return
        }
        this.loading = true;
        axios.post(api.mall.updGoodsKc, inputModel, {
            headers: {
              Authorization: this.$store.state.token,
            }
          })
          .then(response => {
            this.$message.success("新增成功");
            this.$emit("refresh");
            this.loading = false;
            this.visible = false;
          });
      },

    }
  }
</script>
<style lang="less" scoped>
  /deep/ .el-form {
    overflow: visible !important;
  }
</style>