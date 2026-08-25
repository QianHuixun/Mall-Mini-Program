<!--
 * @Author: 沙晓
 * @Date: 2025-07-21 13:57:33
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-08-08 11:21:18
 * @Description: 查看评价
 * @FilePath: /lejia-web/src/views/market/sub/GoodsComment/CommentLook.vue
-->
<template lang="html">
  <el-dialog title="查看评价" center :visible.sync="visible" :closeOnClickModal="false" width="40%">
    <div class="dialog-main">
      <el-form>
        <el-form-item label="评价用户：">
          {{  commentData.memberMobile || "无"  }}
        </el-form-item>
        <el-form-item label="订单号：">
          {{  commentData.orderCode || "无"  }}
        </el-form-item>
        <el-form-item label="评价时间：">
          {{  commentData.createdTime || "无"  }}
        </el-form-item>
        <el-form-item label="评价内容：">
          {{  commentData.content || "无" }}
        </el-form-item>
        <el-form-item>
          <el-image :src="item" style="width: 100px; height: 100px;margin-right: 10px;" v-for="(item, index) in commentData.photo" :key="index"
            :preview-src-list="commentData.photo"></el-image>
        </el-form-item>
        <el-form-item label="回复内容：" v-if="commentData.replyContent">
          {{  commentData.replyContent  }}
        </el-form-item>
        <el-form-item label="回复时间："  v-if="commentData.replyTime">
          {{  commentData.replyTime  }}
        </el-form-item>
      </el-form>
    </div>
    </el-dialog>
  </template>
<script>
export default {
  data() {
    return {
      loading: false,
      visible: false,
      commentData: {}
    }
  },
    mounted() {},
    methods: {
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.visible = false;
    },
    show: function ({row}) {
      this.visible = true;
      this.getData(row.pkey);
    },
    getData:function(pkey) {
      const params= {
        pkey: pkey
      }
      axios.post(api.market.CommentGet, this.$qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          this.commentData = res;
        });
    },
  }
}
</script>