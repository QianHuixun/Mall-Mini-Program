<!-- 
@name: EditComp.vue 
@description: 商品供应商 -- 修改组件 
@author: crj
@date: 2021/10/09
-->
<template lang="html">
  <update-comp :title="'修改'" ref="updateComp" @confirm="handleUpdate" @hide="hide" :needRefesh="true"></update-comp>
</template>
<script>
import updateComp from './UpdComp.vue';

export default {
  data() {
    return {
      visible: false,
    };
  },
  props: {
    isAdminConfig: {
      type: Boolean,
      default: () => {
        return false;
      },
    },
  },
  components: {
    updateComp,
  },
  methods: {
    show: function ({ row, market }) {
      this.getData(row.goodsPkey, market);
    },
    /**
     * @desc 获取明细
     * @param {String} goodsPkey 商品标识
     * @param {String}
     */
    getData(goodsPkey, marketPkey) {
      let params = {
        goodsPkey: goodsPkey,
        marketPkey: marketPkey,
      };
      axios
        .post(api.goods.querySupplyDetail, this.$qs.stringify(params))
        .then((res) => {
          this.$refs.updateComp.show(marketPkey);
          this.$refs.updateComp.initData({
            inputModel: JSON.parse(JSON.stringify(res)),
          });
        });
    },
    hide: function () {
      this.$emit('refresh');
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      // if (this.isAdminConfig) {

      // }
      // this.$confirm("<p style=''>当前采购派单程序为“系统自动派单”，编辑内容提交后自动派单系统会按新保存的商户名单重新开始循环派单。</p><p style=''>是否确认提交更新？</p>", "提示", {
      //     confirmButtonText: "确定",
      //     cancelButtonText: "取消",
      //     type: "warning",
      //     dangerouslyUseHTMLString: true
      //   })
      //   .then(() => {

      //   });
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      axios
        .post(api.goods.updSupply, params, {
          headers: {
            'Content-Type': 'application/json',
          },
        })
        .then((response) => {
          this.$message.success('修改成功');
          this.$emit('refresh');
          this.$refs.updateComp.hide();
        });
      setTimeout(() => {
        this.$refs.updateComp.loading = false;
      }, 300);
    },
  },
};
</script>