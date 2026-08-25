<!-- 
@name: GoodsKC.vue 
@description: 库存管理
@author: crj
@date: 2020/09/27
-->
<template lang="html">
  <el-dialog title="库存管理" center :visible.sync="visible" :closeOnClickModal="false" width="1500px"
    :modal-append-to-body="false">
    <div class="table-container">
      <!-- <h1 class="title">
        库存管理
      </h1> -->
      <!-- 搜索栏 -->
      <div class="search-box">
        <!-- 搜索表单 -->
        <div class="search-box-form">
          <el-select v-model="status" @change="handleChange" placeholder="选择状态" clearable>
            <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in statusList">
            </el-option>
          </el-select>
          <div class="count-container">
            <div class="count-box"><span class="count-tit">总入库</span>
              <span class="count-num-rk">{{rkNum}}</span>
            </div>
            <div class="count-box"><span class="count-tit">总销售</span>
              <span class="count-num-xs">{{xsNum}}</span>
            </div>
          </div>
        </div>
        <!-- 操作按钮 -->
        <div class="search-box-button">
          <el-button type="primary" size="medium" @click="handelRecord(goodsData)">
            采购入库
          </el-button>
          <el-button type="primary" size="medium" @click="handelcheck(goodsData)">
            库存盘点
          </el-button>
        </div>
      </div>
      <!-- 表格框 -->
      <div class="table-box">
        <el-table :data="tableData" :loading="loading" border style="width: 100%">
          <el-table-column label="商品名" prop="goodsName"></el-table-column>
          <el-table-column label="规格型号" prop="spaceName"></el-table-column>
          <el-table-column label="出入库类型" prop="wareTypeName"></el-table-column>
          <el-table-column label="批次号" prop="orderNumber"></el-table-column>
          <el-table-column label="数量" prop="num"></el-table-column>
          <el-table-column label="单价" prop="price"></el-table-column>
          <el-table-column label="供应商" prop="supplier"></el-table-column>
          <el-table-column label="备注" prop="remark"></el-table-column>
          <el-table-column label="时间" prop="createdTime"></el-table-column>
          <el-table-column label="库存余额" prop="actualNum"></el-table-column>
        </el-table>
        <el-pagination hide-on-single-page background layout="prev, pager, next" :total="total" :current-page="page"
          :page-size="pageSize" @current-change="handleCurrentChange"></el-pagination>
      </div>
      <record-comp ref="RecordComp" @refresh="refreshData"></record-comp>
      <check-comp ref="CheckComp" @refresh="refreshData"></check-comp>
    </div>
  </el-dialog>
</template>
<script>
  import qs from "qs";
  import RecordComp from './sub/GoodsKcRecord'
  import CheckComp from './sub/GoodsKcCheck'


  export default {
    data() {
      return {
        visible: false,
        loading: false,
        tableData: [],
        total: 0, //总页数
        status: '',
        statusList: [ //NOT_ISSUED(0, "初始"), ISSUED(1, "已发");
          {
            pkey: "",
            name: "状态"
          },
          {
            pkey: "WAREHOUSING",
            name: "入库"
          },
          {
            pkey: "INVENTORY",
            name: "盘点"
          },
          {
            pkey: "SALES",
            name: "销售"
          },

        ],
        goodsData: { //商品传递参数
          pkey: '', //商品pkey
          goodsName: '', //商品名称
          spaceList: [], //规格列表
        },
        rkNum: 0, //入库数量
        xsNum: 0, //销售数量
        pageSize: 8, //一页的数量
        page: 1, //页数

      };
    },
    computed: {
      /**
       * 获取菜单标题
       * @return {[title]} [返回从state状态中获取的选中菜单名]
       */
      title() {
        return this.$store.state.activeName;
      },

    },
    components: {
      RecordComp,
      CheckComp
    },
    mounted() {
      // this.getData();
    },
    methods: {
      /**
       * 页码改变事件
       */
      handleCurrentChange(val) {
        this.page = val;
        this.loading = true;
        this.getData();
      },
      /**刷新数据 */
      refreshData() {
        this.getData();
        this.getNumData();
        this.getGoodSpace();
        this.$emit("refresh");

      },
      /**清空数据 */
      clearData() {
        this.tableData = [];
        this.goodsData = { //商品传递参数
          pkey: '', //商品pkey
          goodsName: '', //商品名称
          spaceList: [], //规格列表
        };
      },
      /**显示弹窗 */
      show: function ({
        row
      }) {
        console.log(row)
        this.visible = true;
        this.clearData();
        this.goodsData = { //商品传递参数
          pkey: row.pkey,
          goodsName: row.title,
        };
        this.getData();
        this.getNumData();
        this.getGoodSpace()
      },
      /**下拉框切换 */
      handleChange() {
        this.page = 1;
        this.getData();
      },
      /**库存盘点 */
      handelcheck(data) {
        this.goodsData.num = this.rkNum
        this.$refs.CheckComp.show({
          goodsData: this.goodsData
        });
      },
      /**采购入库 */
      handelRecord(data) {
        this.$refs.RecordComp.show({
          goodsData: this.goodsData
        });
      },

      /**
       * 获取列表
       * @return {[type]} [description]
       */
      getData: function () {
        this.loading = true;
        const params = {
          goodsPkey: this.goodsData.pkey,
          type: this.status,
          page:this.page-1,
          pagesize:this.pageSize
        };
        /**获取表格数据 */
        axios.post(api.mall.queryGoodsKc, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(res => {
            this.tableData = res.content;
            this.total = res.total;
            setTimeout(() => {
              this.loading = false;
            }, 300);
          });


      },
      /**获取总数数据 */
      getNumData() {
        let params = {
          goodsPkey: this.goodsData.pkey
        }
        axios.post(api.mall.queryKcNum, qs.stringify(params), {
            headers: {
              Authorization: this.$store.state.token
            }
          })
          .then(res => {
            this.rkNum = res[0].num;
            console.log(typeof res[1].num)
            this.xsNum = res[1].num.toString().replace("-",'');

          });
      },
      /**获取商品规格*/
      getGoodSpace() {
        let params = {
          pkey: this.goodsData.pkey
        };
        axios.post(api.goods.getGoods, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        }).then(res => {
          this.goodsData.spaceList = res.spaces
        });
      }
    }
  }
</script>
<style lang="less" scoped>
  .count-container {
    display: inline-block;

    .count-box {
      display: inline-block;
      padding: 0 10px;

      .count-tit {
        padding-right: 5px;
      }

      .count-num-rk {
        color: #67C23A
      }

      .count-num-xs {
        color: #409EFF
      }
    }
  }
</style>
<style lang="less" scoped>
  /deep/.el-dialog {
    padding-bottom: 20px;
  }
</style>