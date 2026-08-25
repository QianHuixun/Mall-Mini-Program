<!-- 
@name: CookfdUpdate.vue 
@description: 菜谱管理--编辑模板 
@author: sx
@date: 2020/07/02
-->
<template lang="html">
  <el-dialog :title="title" center :visible.sync="visible" :closeOnClickModal="false" width="850px"
    class="CookfdUpdate">
    <el-form>
      <el-form-item label="菜谱名称" :label-width="labelWidth" :required="true">
        <el-input v-model="inputModel.name" ref="nameInput" placeholder="请输入菜谱名称"></el-input>
      </el-form-item>
      <el-form-item label="所属分类" :label-width="labelWidth" :required="true">
        <el-select v-model="inputModel.ctype" ref="cookTypeInput" placeholder="请选择">
          <el-option v-for="item in CookTypeList" :key="item.pkey" :label="item.name" :value="item.pkey">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="轮播图" :label-width="labelWidth" :required="true">
        <img-upload ref="ImgUpload" :limit="3" @change="changeImg"></img-upload>
        <div class="tips">建议尺寸750*750像素</div>
      </el-form-item>
      <el-form-item label="排序" :label-width="labelWidth">
        <el-input v-model="inputModel.sort" ref="sortInput" placeholder="请输入排序" maxlength="10"
          v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); inputModel.sort = val;}"
        ></el-input>
      </el-form-item>
      <el-form-item label="菜谱简介" :label-width="labelWidth">
        <el-input v-model="inputModel.descp" ref="descpInput" placeholder="请输入菜谱简介"></el-input>
      </el-form-item>
      <el-form-item label="关联商品" :label-width="labelWidth" :required="true">
        <el-table :data="inputModel.lines" :loading="loading" border style="width: 100%" max-height="200"
          row-key="sort">
          <el-table-column label="商品*" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.goods" :ref="`goodsInput${scope.$index}`" filterable placeholder="请选择"
                @blur="handleLinesChange(scope.row, scope.$index)"
                @change="handleGoodsChange(scope.row.goods,scope.$index)" v-loadmore="loadMore"
                :filter-method="filterMethod" @focus="focusMethod">
                <el-option v-for="item in GoodsList" :key="item.pkey" :label="item.title" :value="item.pkey">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="规格*" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.space" :ref="`spaceInput${scope.$index}`" placeholder="请选择" @blur="handleLinesChange(scope.row, scope.$index)">
                <el-option v-for="item in SpacesList[scope.$index]" :key="item.pkey" :label="item.space" :value="item.pkey"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量*" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.num" :ref="`numInput${scope.$index}`" placeholder="数量" @blur="handleLinesChange(scope.row, scope.$index)"
                v-on:input="(val)=>{ val =val.replace(/[^\d]/g,''); scope.row.num =val;}">
              </el-input>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-popconfirm title="确定删除吗？" placement="top" @onConfirm="handleLinesDel(scope.row, scope.$index)">
                <el-button slot="reference" size="mini" type="danger" :disabled="inputModel.lines.length==1">删除</el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" round @click="handleLinesAdd" style="margin-top: 8px;">新增关联商品</el-button>
      </el-form-item>
      <el-form-item label="菜谱步骤" :label-width="labelWidth">
        <el-row v-for="(item,index) in content" :key="index">
          <span style="padding-right: 8px;">步骤{{ index+1 }} </span>
          <el-input v-model="item.value" placeholder="请输入" :ref="`content${index}`"></el-input>
          <el-button size="mini" type="danger" @click="handleContentDel(index)" style="margin-left: 8px;height: 36px;"
            v-if="content.length != 1">删除</el-button>
        </el-row>
        <el-button type="primary" round @click="handleContentAdd" style="margin-top: 8px;">新增菜谱步骤</el-button>
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
import utils from '@/assets/js/utils';
import dropdown from '@/assets/js/dropdown';
import ImgUpload from '@/components/global/ImgUpload';
import Sortable from 'sortablejs';

export default {
  data() {
    return {
      labelWidth: '100px',
      visible: false,
      loading: false,
      inputModel: {
        name: '',
        photo1: [],
        descp: '',
        sort: 0,
        lines: [
          {
            pkey: '',
            goods: '', //商品
            num: 0, //数量
            remark: 0, //备注
            space: '', //规格
            sort: 0, //排序
            status: 1, //0:未修改 1:新增   2:修改  3:删除
          },
        ],
        ctype: '',
        content: [],
        recom: false,
        enabled: true,
        viewCount: 0,
      },

      content: [
        {
          value: '',
        },
      ],
      CookTypeList: [],
      GoodsList: [],
      GoodsListCopy: [],
      SpacesList: {},
      selectVal: '',
      delSpace: [], //需要被删除的规格列表
    };
  },
  mounted() {
    this.getGoods();
    dropdown.getCookfdType().then((result) => {
      this.CookTypeList = result.content;
    });
  },

  components: {
    ImgUpload,
  },
  methods: {
    /**
     *@desc 获取商品列表
     */
    getGoods: function () {
      axios
        .post(
          api.goods.queryGoods,
          qs.stringify({
            mType: 'MARKET_GOODS',
            page: 0,
            pagesize: 9999,
          }),
          {
            headers: {
              Authorization: this.$store.state.token,
            },
          }
        )
        .then((res) => {
          this.GoodsList = res.content;
        });
    },
    // 行拖拽
    rowDrop() {
      // console.log(this.inputModel.lines)
      // 此时找到的元素是要拖拽元素的父容器
      const tbody = document.querySelector(
        '.el-dialog .el-table__body-wrapper tbody'
      );
      const _this = this;
      Sortable.create(tbody, {
        //  指定父元素下可被拖拽的子元素
        draggable: '.el-table__row',
        onEnd({ newIndex, oldIndex }) {
          const currRow = _this.inputModel.lines.splice(oldIndex, 1)[0];
          _this.inputModel.lines.splice(newIndex, 0, currRow);
          _this.inputModel.lines = _this.inputModel.lines.map((item, index) => {
            item.sort = index;
            return item;
          });
        },
      });
    },
    /**
     * 图片修改事件
     */
    changeImg: function (imgUrl) {
      this.inputModel.photo1 = imgUrl;
    },
    /**
     * 清空数据
     */
    clearData: function () {
      this.inputModel = {
        name: '',
        photo1: [],
        descp: '',
        sort: 0,
        lines: [
          {
            pkey: '',
            goods: '', //商品
            num: 0, //数量
            remark: 0, //备
            space: '', //规格
            sort: 0, //排序
            status: 1, //0:未修改 1:新增   2:修改  3:删除
          },
        ],
        recom: false,
        content: [
          {
            value: '',
          },
        ],
        enabled: true,
        viewCount: 0,
      };
      this.delSpace = []; //需要被删除的规格
    },
    /**
     * 初始化数据
     */
    initData: function ({ inputModel }) {
      const that = this;
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg(inputModel.photo1);
        this.content = inputModel.content.map((item) => {
          return {
            value: item,
          };
        });
        let list = {};
        inputModel.lines.forEach((item, index) => {
          list = that.GoodsList.filter((subitem) => item.goods == subitem.pkey);
          that.SpacesList[index] = list[0].spaces;
        });
      });
      this.inputModel = inputModel;
    },
    show: function () {
      this.visible = true;
      this.clearData();
      this.$nextTick(() => {
        this.$refs.ImgUpload.updateImg('');
        this.content = [
          {
            value: '',
          },
        ];
        setTimeout(() => {
          this.rowDrop();
        }, 600);
      });
    },
    /**
     * 关闭弹出框
     */
    hide: function () {
      this.clearData();
      this.visible = false;
      this.$emit('hide');
    },
    /**
     * 新增规格
     */
    handleLinesAdd: function () {
      let lines = this.inputModel.lines;
      for (var i = 0; i < lines.length; i++) {
        if (lines[i].goods === '') {
          this.$message.error('请选择商品');
          return;
        }

        if (lines[i].space === '') {
          this.$message.error('请选择规格');
          return;
        }

        if (lines[i].num === '') {
          this.$message.error('请选择数量');
          this.$refs[`numInput${i}`].focus();
          return;
        }
      }

      this.inputModel.lines.push({
        pkey: '',
        goods: '', //商品
        num: 0, //数量
        remark: 0, //备注
        space: '', //规格
        sort: this.inputModel.lines.length, //排序
        status: 1, //0:未修改 1:新增   2:修改  3:删除
      });
    },
    handleLinesDel: function (row, index) {
      if (row.status == 1) {
        this.inputModel.lines.splice(index, 1);
      } else {
        this.inputModel.lines[index].status = 3;
        this.delSpace.push(this.inputModel.lines[index]);
        this.inputModel.lines.splice(index, 1);
      }

      if (this.inputModel.lines.length == 0) {
        this.handleLinesAdd();
      }
    },

    handleLinesChange: function (row, index) {
      if (row.status == 1 || row.status == 2) {
        return;
      }

      this.inputModel.lines[index].status = 2;
    },
    // 商品选择改变事件
    handleGoodsChange(pkey, index) {
      const that = this;
      if (!this.selectVal) {
        this.GoodsList.filter((item) => {
          if (item.pkey == pkey) {
            that.SpacesList[index] = item.spaces;
            return;
          }
        });
      } else {
        this.getGoodSpaces(pkey, index);
      }
      this.selectVal = '';
      this.inputModel.lines[index].space = '';
    },
    /**获取商品规格 */
    async getGoodSpaces(pkey, index) {
      let params = {
        pkey,
      };
      await axios
        .post(api.goods.getGoods, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((res) => {
          this.$set(this.SpacesList, index, res.spaces);
        });
    },
    handleContentDel: function (index) {
      this.content.splice(index, 1);
    },
    handleContentAdd: function () {
      let content = this.content;
      for (var i = 0; i < content.length; i++) {
        if (content[i].value === '') {
          this.$message.error('请输入步骤');
          // this.$refs[`content${i}`].focus();
          return;
        }
      }
      this.content.push({
        value: '',
      });
    },
    /**
     * 处理提交
     */
    handleSubmit: function () {
      if (!this.inputModel.name) {
        this.$message.error('请输入菜谱名称');
        this.$refs.nameInput.focus();
        return;
      }
      if (!this.inputModel.ctype) {
        this.$message.error('请选择所属分类');
        this.$refs.cookTypeInput.focus();
        return;
      }
      if (this.inputModel.photo1.length == 0) {
        this.$message.error('请上传轮播图');
        return;
      }

      let lines = this.inputModel.lines;
      if (lines.length == 1) {
        if (
          lines[0].goods == '' ||
          lines[0].space == '' ||
          lines[0].num == ''
        ) {
          if (lines[0].goods === '') {
            this.$message.error('请选择商品');
            return;
          }

          if (lines[0].space === '') {
            this.$message.error('请选择规格');
            return;
          }

          if (!lines[0].num || lines[0].num == '0') {
            this.$message.error('请选择数量');
            this.$refs[`numInput0`].focus();
            return;
          }
        }
      } else {
        for (var i = 0; i < lines.length; i++) {
          if (lines[i].goods == '') {
            this.$message.error('请选择商品');
            return;
          }

          if (lines[i].space == '') {
            this.$message.error('请选择规格');
            return;
          }
          if (!lines[i].num || lines[i].num == '0') {
            this.$message.error('请选择数量');
            this.$refs[`numInput${i}`].focus();
            return;
          }
        }
      }

      this.inputModel.content = this.content.map((item) => {
        return item.value;
      });
      if (this.delSpace.length) {
        let inputModel = this.inputModel;
        this.delSpace.map((item) => {
          inputModel.lines.push(item);
        });
      }
      this.$emit('confirm', {
        inputModel: this.inputModel,
      });
    },
  },
  props: {
    title: {
      type: String,
      default: '新增',
    },
  },
};
</script>
<style lang="less">
.CookfdUpdate {
  .el-row {
    display: flex;

    .el-input {
      flex: 1;
    }
  }
}
</style>