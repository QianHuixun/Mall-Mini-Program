<!-- 搜索下拉框-->
<template lang='html'>
  <el-select v-model="inputModel" filterable :placeholder="placeholder" @change="handleSearchChange($event)" remote
    :remote-method="filterMethod" @focus="focusMethod" v-loadmore="loadMore" clearable :reserve-keyword="false">
    <el-option v-for="(item,index) in list" :key="index" :label="option.label?item[option.label]:item"
      :value="option.value?item[option.value]:item">
    </el-option>
  </el-select>
</template>
<script>
  import qs from 'qs';
  export default {
    data() {
      return {
        inputModel: "",
        searchKey: '',
        listCopy: [],
        list: [],
        page: 0,
        pageSize: 10,
        selectVal: "",
        totalPages: 0
      };
    },
    directives: {
      'loadmore': {
        bind(el, binding) {
          const SELECTWRAP_DOM = el.querySelector(
            '.el-select-dropdown .el-select-dropdown__wrap'
          );
          SELECTWRAP_DOM.addEventListener('scroll', function () {
            const condition = this.scrollHeight - this.scrollTop - 1 <= this.clientHeight;
            if (condition) {
              binding.value();
            }
          });
        }
      }
    },
    created() {},
    mounted() {},
    components: {},
    methods: {
      /**
       * 加载更多数据
       */
      loadMore: function () {
        if (this.selectVal != "") return;
        if (this.totalPages <= this.page+1) return;
        this.page++
        // dropdown.getGoods('ALL', this.page).then(result => {
        //   this.GoodsList = this.GoodsList.concat(result.content);
        //   this.totalPages = result.totalPages;
        // });
        let params = {
          page: this.page,
          pagesize: this.pageSize
        }
        if (this.requireParam.length) {
          this.requireParam.map(item => {
            params[item.name] = item.val;
          })
        }
        axios.post(this.url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        }).then(response => {
          if (response.content.length) {
            this.list = this.list.concat(response.content);
            this.listCopy = this.listCopy.concat(response.content);
          } else {
            this.list = [];
            this.listCopy = [];
          }
          this.totalPages = response.totalPages;
        });
      },
      /**
       * 下拉框获取焦点 
       */
      focusMethod: function () {
        this.page = 0;
        let params = {
          page: this.page,
          pagesize: this.pageSize
        }
        if (this.requireParam.length) {
          this.requireParam.map(item => {
            params[item.name] = item.val;
          })
        }
        axios.post(this.url, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        }).then(response => {
          if (response.content.length) {
            this.list = response.content
            this.listCopy = response.content
          } else {
            this.list = [];
            this.listCopy = [];
          }
          this.totalPages = response.totalPages;
        });
      },
      /**
       * 下拉 搜索
       */
      filterMethod: function (val) {
        val = val.replace(/(^\s*)|(\s*$)/g, "");
        this.selectVal = val;
        if (val == "") {
          this.list = this.listCopy;
          return;
        }
        const params = {};
        params[this.params] = val;
        if (this.requireParam.length) {
          this.requireParam.map(item => {
            params[item.name] = item.val;
          })
        }
        axios.post(this.url, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(response => {
            this.list = response.content;
          });
      },
      // 选择改变事件
      handleSearchChange(val) {
        this.selectVal = "";
        this.$emit("confirm", val)

      }
    },
    props: {
      url: { //接口路径
        type: String,
        default: () => {
          return ""
        }
      },
      params: { //接收的参数名称
        type: String,
        default: () => {
          return ""
        }
      },
      option: { //下拉框选项配置
        type: Object,
        default: () => {
          return {
            label: "",
            value: "pkey"
          }
        }
      },
      requireParam: { //需要额外传递给接口的参数
        type: Array,
        default: () => {
          return []
        }
      },
      placeholder: {
        type: String,
        default: () => {
          return "请选择"
        }
      }
    }
  };
</script>