<!-- 
@name: DispatchUpd.vue 
@description: 派单配置-骑手配送派单
@author: crj
@date: 2021/09/29
-->
<template>
  <el-dialog title="请选择自动派单骑手名单" center :visible.sync="visible" :closeOnClickModal="false">
    <el-transfer filterable filter-placeholder="请输入骑手名称或者手机号" v-model="rider" :data="riderList"
      :props="{ key: 'pkey',label: 'value'}" :titles="['骑手列表', '已选骑手']">
    </el-transfer>
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
        visible: false,
        loading: false,
        riderList: [],
        rider: [],
      }
    },
    mounted() {

    },
    methods: {
      /**
       * @desc 显示
       */
      show() {
        this.visible = true;
        this.getData();
      },
      /**
       * @desc 清空数据
       */
      clearData() {
        this.rider = [];
      },
      /**
       * @desc 获取骑手列表
       */
      getData() {
        axios.post(api.mall.queryDispatchCourier, {}, {
          headers: {
            Authorization: this.$store.state.token
          }
        }).then(res => {
          res.map(item => {
            if (item.selected) {
              this.rider.push(item.pkey);
            }
          });
          this.riderList = res;
        });
      },
      hide() {
        this.visible = false;
      },
      handleSubmit() {
        this.visible = false;
        let  rider = JSON.parse(JSON.stringify(this.rider));
        let params = rider.map(item => {
          return {
            mobile: "",
            name: "",
            pkey: item
          }
        });
        this.loading = true;
        axios.post(api.mall.updDispatchCourier, params, {
          headers: {
            Authorization: this.$store.state.token
          }
        }).then(res => {
          this.loading = false;
          this.$message.success('自动派单骑手名单保存成功');
        }).catch(err => {
          this.loading = false;
        })
      }
    }
  }
</script>

<style lang="less" scoped>
  /deep/ .el-dialog {
    width: 800px;

    .el-dialog__body {
      display: flex;
      justify-content: center;
    }
  }

  /deep/.el-transfer-panel {
    width: 300px !important;
  }
</style>