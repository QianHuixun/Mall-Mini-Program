<template>
  <el-dialog
    title="物流详情"
    center
    :visible.sync="visible"
    :closeOnClickModal="false"
    :modal-append-to-body="false"
    :append-to-body="true"
    width="30%"
  >
    <el-timeline>
      <el-timeline-item
        v-for="(item, index) in expressRoutes"
        :key="index"
        :timestamp="item.trackMsgTime"
      >
        {{ item.trackContent }}
      </el-timeline-item>
    </el-timeline>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      expressRoutes: [],
    };
  },
  methods: {
    /**弹出弹窗 */
    show: function ({ expressRoutes }) {
      this.visible = true;
      this.expressRoutes = expressRoutes;
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.visible = false;
    },
  },
};
</script>
