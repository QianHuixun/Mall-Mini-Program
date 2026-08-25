<!-- 
@name: AdsUpdate.vue 
@description: 广告管理--编辑模板 
@author: sx
@date: 2020/06/29
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入名称"></el-input>
      </el-form-item>
      <el-form-item label="图片" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="1" @change="changeImg"></img-upload>
        <div class="tips">推荐尺寸：750*{{ position == 'ADVERT_POSITION_MSD' && (ascription == 13 || ascription == 22) ? '210' : '360' }}</div>
      </el-form-item>
      <click-effect ref="ClickEffect" :inputModel.sync="inputModel"></click-effect>
      <el-form-item label="排序" :label-width="labelWidth">
        <el-input v-model="inputModel.sort" ref="sortInput" placeholder="请输入排序"
          v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.sort =val;}"></el-input>
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
  import ImgUpload from "@/components/global/ImgUpload";
  import ClickEffect from '@/components/global/ClickEffect';
  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        inputModel: {
          position: "",
          name: "",
          photo: "",
          urlType: "NOT_URL",
          objKey: "",
          sort: 0,
          enabled: true
        },
        ascription: localStorage.getItem("ascription"),
      };
    },
    inject: ['position'],
    computed: {
      selectOption() {
        let selectOption = [{
            key: "NOT_URL",
            name: "无"
          },
          {
            key: "LINK",
            name: "链接"
          },
          {
            key: "POINTS_MALL",
            name: "积分商城"
          },
          // {
          //   key: "MEMBERSHIP",
          //   name: "会员办理"
          // },
          {
            key: "GOODS",
            name: "商品"
          },
          {
            key: "PERSONAL_CENTER",
            name: "个人中心"
          },
          {
            key: "SPECIAL_GOODS",
            name: "特价专区"
          },
          // {
          //   key: "MEMBER_GOODS",
          //   name: "会员专区"
          // },
          {
            key: "PRESALE_GOODS",
            name: "预售专区"
          },
          {
            key: "COOKFD_GOODS",
            name: "菜谱专区"
          },
          // {
          //   key: "CUT_GOODS",
          //   name: "砍价专区"
          // },
          // {
          //   key: "COLLAGE_GOODS",
          //   name: "拼团专区"
          // },
          // {
          //   key: "SHARE_GOODS",
          //   name: "分享专区"
          // },
          // {
          //   key: "POVERTY_ALLEVIATION_GOODS",
          //   name: "扶贫专区"
          // },
          {
            key: "CARD_CENTER",
            name: "领券中心"
          }
        ];
        if (this.$store.state.userIdentity == 1)
          selectOption.push({
            key: "POINT_RULES",
            name: "积分规则"
          })
        return selectOption
      }
    },
    mounted() {},
    components: {
      ImgUpload,
      ClickEffect
    },
    methods: {
      /**
       * 图片修改事件
       */
      changeImg: function (imgUrl) {
        this.inputModel.photo = imgUrl[0];
      },
      getGoods: function (pkey) {
        this.inputModel.objKey = pkey;
      },
      /**
       * 编辑是更新商品选中信息
       */
      updateGoods: function({goodsInfo}) {
        this.$refs.goodsPicker.updateGoods({
          goodsInfo
        });
      },
      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          position: "",
          name: "",
          photo: "",
          urlType: "GOODS",
          objKey: "",
          sort: 0,
          enabled: true
        };

        this.$nextTick(() => {
          if(this.inputModel.urlType == 'GOODS') {
          const goodsInfo =  {
            goodsName: "",
            goodsKey: ""
          };
          this.updateGoods({goodsInfo});
        }
          this.$refs.ImgUpload.updateImg("");
        });
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel
      }) {
        this.inputModel = inputModel;
        this.$refs.ImgUpload.updateImg(this.inputModel.photo);
      },
      show: function () {
        console.log(this.position);
        
        this.visible = true;
        this.clearData();
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.clearData();
        this.visible = false;
        this.$emit("hide");
      },
      /**
       * 处理提交
       */
      handleSubmit: function () {
        if (!this.inputModel.name) {
          this.$message.error("请输入名称");
          this.$refs.nameInput.focus();
          return;
        }

        if (!this.inputModel.photo) {
          this.$message.error("请选择图片");
          this.$refs.photoInput.focus();
          return;
        }
        if(this.$refs.ClickEffect.validate() === false) {
          return
        }
        if (this.inputModel.urlType == 'GTYPE') {
          inputModel.objKey = inputModel.objKey.join(',');
        }
        if (!this.inputModel.sort) {
          this.inputModel.sort = 0;
        }
        this.$emit("confirm", {
          inputModel: this.inputModel
        });
      }
    },
    props: {
      title: {
        type: String,
        default: "新增"
      }
    }
  };
</script>