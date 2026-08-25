<!-- 
@name: process.vue 
@description: 采购结算流程
@author: crj
@date: 2021/12/21
-->
<template lang="html">
  <el-dialog title="采购结算流程" center :visible.sync="visible" :closeOnClickModal="false" @close="hide" :close-on-click-modal="false">
    <el-form class="zy-form" ref="form">
      <div>
        <div class="process-title">
          <img class="process-btn" :src="require('@/assets/images/process_btn.png')" />
          <span>生成结算报表</span>
        </div>
        <div class="zy-form-columns" style="height:60px">
          <el-form-item label="操作时间：">
            {{ headData.createdTime }}
          </el-form-item>
          <el-form-item label="操作备注：">
            {{ headData.rem }}
          </el-form-item>
          <el-form-item label="操作人员：">
            {{ headData.appByName }}
          </el-form-item>
        </div>
      </div>

      <div class="process-item" v-for="(item, index) in inputModel" :key="index">
        <div class="process-title">
          <img class="process-btn" :src="item.processNode == 'FAIL' ? require('@/assets/images/process_btn_fail.png') :
          (item.processNode == 'SUCCESS' ? require('@/assets/images/process_btn_success.png') : require('@/assets/images/process_btn.png'))" />
          <span :class="item.processNode == 'FAIL' ? 'red-font' : (item.processNode == 'SUCCESS' ? 'green-font' : '')">{{ item.typeName }}</span>
        </div>
        <div class="zy-form-columns" style="height:60px">
          <el-form-item :label="item.processNode == 'FAIL' ? '时间：' :
          (item.processNode == 'SUCCESS' ? '结算时间：' : '操作时间：')">
            {{ item.createdTime }}
          </el-form-item>
          <el-form-item v-if="item.processNode != 'SUCCESS'" :label="item.processNode == 'FAIL' ? '失败说明：' : '结算备注：'">
            {{ item.rem || '无' }}
          </el-form-item>
          <el-form-item v-if="item.processNode == 'APPLY'" label="操作人员：">
            {{ item.appByName }}
          </el-form-item>
        </div>
        <el-collapse :value="index" v-if="item.processNode == 'APPLY'">
          <el-collapse-item :name="index">
            <div class="zy-form-columns" style="height:150px">
              <el-form-item :label="subIndex + '：'" v-for="(subItem, subIndex) in item.content" :key="subIndex">
                {{ subItem }}
              </el-form-item>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
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
  data () {
    return {
      inputModel: [],
      headData: {},
      visible: false,
      loading: false,
    };
  },
  mounted () { },
  methods: {
    /**
     * 清空数据
     */
    clearData: function () {
      this.date = '';
    },
    /**
     * @desc  显示并初始化数据
     * @param {Array} pkeys 标识合集
     */
    show: function (pkey) {
      this.pkey = pkey;
      this.initData();
    },
    /**
     * @desc 初始化数据
     */
    initData () {
      let params = {
        linePkey: this.pkey,
      };
      axios
        .post(api.order.querySettleProcess, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.headData = response[0];
          response.splice(0, 1);
          response.map((item) => {
            item.content = JSON.parse(item.content);
            return item.content;
          });
          this.inputModel = response;
          this.visible = true;
        });
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      localStorage.setItem('comSettleDate', JSON.stringify(this.date));
      this.$emit('confirm');
      this.hide();
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
    settlementMethod: {
      type: String,
      default: 'PURCHASE_SETTLEMENT',
    },
  },
};
</script>
<style lang="less" scoped>
/deep/.el-dialog__body {
  text-align: center;

  .title {
    margin-bottom: 10px;
    font-weight: bold;
    text-align: left;
  }
}

/deep/.zy-form-columns {
  display: flex;
  flex-wrap: wrap;
  flex-direction: column;
  margin-bottom: 10px;

  &>.el-form-item {
    width: 50%;

    .el-form-item__label,
    .el-form-item__content {
      line-height: 20px;
      text-align: left;
    }

    .el-form-item__label {
      width: auto !important;
      padding-right: 0;
    }

    .el-form-item__content {
      margin-left: 0;
    }
  }
}

::-webkit-scrollbar {
  display: none;
}

.process-item {
  position: relative;
}

.process-title {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: #0099ff;

  .process-btn {
    width: 20px;
    height: 20px;
    border: none !important;
    margin-right: 5px;
  }
}

.red-font {
  color: #ff0000;
}

.green-font {
  color: #33a954;
}

/deep/.el-collapse-item__header {
  height: 30px;
  padding-bottom: 10px;

  &.is-active {
    border-bottom: 1px solid #ebeef5;
  }
}

/deep/.el-collapse {
  border: none;
  position: relative;
  left: 0;
  right: 0;
  top: -40px;
  background: transparent;

  .el-collapse-item div {
    background: transparent;
  }

  .el-collapse-item__content {
    margin-top: 20px;
    padding-bottom: 0;
    background: #f2f2f2 !important;
    border-radius: 4px;
    padding: 10px 10px 0 10px;
  }

  .el-collapse-item__wrap {
    border-bottom: none;
  }
}
</style>