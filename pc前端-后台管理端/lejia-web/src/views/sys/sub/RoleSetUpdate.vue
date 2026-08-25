<!-- 
@name: OurletUpdate.vue 
@description: 网点设置--编辑模板 
@author: sx
@date: 2020/03/24
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false">
    <el-form>
      <el-form-item label="配置权限" :label-width="labelWidth" :required="true">
        <el-tree ref="tree" :data="treeList" show-checkbox default-expand-all node-key="pkey" :props="defaultProps">
        </el-tree>
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
  import qs from "qs";

  export default {
    data() {
      return {
        labelWidth: "100px",
        visible: false,
        loading: false,
        inputModel: {

        },
        pkey: "",
        data: [],
        treeList: [],
        defaultProps: {
          children: 'sub',
          label: 'name'
        }
      };
    },
    mounted() {
      this.getData();
    },
    methods: {

      /**
       * 清空数据
       */
      clearData: function () {
        this.inputModel = {
          functions: []
        }
        this.$refs.tree.setCheckedKeys([]);
      },
      /**
       * 初始化数据
       */
      initData: function ({
        inputModel
      }) {
        this.pkey = inputModel.pkey;
        let params = {
          pkey: inputModel.pkey
        }
        axios.post(api.sys.getRole, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            this.$refs.tree.setCheckedKeys(response);
            let node = this.$refs.tree.getNode('lj_boss_index'),
              data;
            if (node == null)
              node = this.$refs.tree.getNode('lj_market_index')
            data = node.data;
            data.disabled = true;
            this.$refs.tree.setChecked(data, true);
          });

      },
      show: function () {
        this.visible = true;
      },
      /**
       * 关闭弹出框
       */
      hide: function () {
        this.visible = false;
        this.clearData();
        this.$emit("hide");
      },
      /**
       * 获取权限列表
       */
      getData: function () {
        axios.post(api.sys.getAllRole, {}, {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            this.data = response;
            this.treeList = (response.data && response.data.length == 1) ? response.data[0].sub : [];
          });
      },
      /**
       * 处理提交
       */
      handleSubmit: function () {
        if (!this.$refs.tree.getCheckedKeys().length) {
          this.$message.error("请选择权限");
          return;
        }
        let menus = this.$refs.tree.getCheckedKeys();
        let dataSub = (this.data.data && this.data.data.length == 1) ? this.data.data[0].sub : [];
        dataSub = dataSub.map((item, index) => {
          if (menus.indexOf(item.pkey) == -1) {
            item.show = false;
            item.selected = false
          } else {
            console.log(item.name)
            item.show = true;
            item.selected = true
          }
          if (item.sub != null) {
            item.sub = item.sub.map((subitem, subindex) => {
              if (menus.indexOf(subitem.pkey) == -1) {
                subitem.show = false;
                subitem.selected = false
              } else {
                subitem.show = true;
                subitem.selected = true
              }
              return subitem;
            });
          }
          return item;
        });

        this.data.pkey  = this.pkey;
        if(this.data.data.length == 1){
          this.data.data[0].sub = dataSub;
        }
        this.inputModel = this.data;
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