<!--
 * @Author: 沙晓
 * @Date: 2025-06-20 09:56:03
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-07-31 11:23:06
 * @Description: file content
 * @FilePath: /lejia-web/src/views/goods/sub/GoodsEdit.vue
-->
<!-- 
@name: GoodsEdit.vue 
@description: 商品维护 --修改组件
@author: sx
@date: 2020/06/30
-->
<template lang="html">
  <update-comp :title="'修改商品'" ref="updateComp" @confirm="handleUpdate" @hide="hide" ></update-comp>
</template>
<script>
import updateComp from './GoodsUpdate.vue';
import qs from 'qs';

export default {
  data() {
    return {
      visible: false,
    };
  },
  components: {
    updateComp,
  },
  methods: {
    show: function ({ row }) {
      let inputModel = {};
      if (row.mtype == 'COUPON_GOODS') {
        let params = {
          pkey: row.pkey,
        };
        axios
          .post(api.mall.queryGoodsCouponDetail, qs.stringify(params))
          .then((res) => {
            res.mtype = row.mtype;
            res.spaces = [res.space];
            inputModel = res;
            let sellingPoints = [];
            if(res.sellPoints && res.sellPoints.length == 4) {
              sellingPoints = res.sellingPoints;
            }else {
              for(let i=0;i<4;i++) {
                if(res.sellingPoints && res.sellingPoints[i]) {
                  sellingPoints.push(res.sellingPoints[i])
                }else {
                  sellingPoints.push({name: "",content: ""})
                }
              }
            }
            console.log("sellingPoints1",sellingPoints);
            inputModel.sellingPoints = sellingPoints;
            this.$refs.updateComp.show();
            setTimeout(() => {
              this.$refs.updateComp.initData({
                inputModel: inputModel,
                cutList: [],
              });
            }, 0);
          });
      } else {
        let sellingPoints = [];
        if(row.sellPoints && row.sellPoints.length == 4) {
          sellingPoints = row.sellingPoints;
        }else {
          for(let i=0;i<4;i++) {
            if(row.sellingPoints && row.sellingPoints[i]) {
              sellingPoints.push(row.sellingPoints[i])
            }else {
              sellingPoints.push({name: "",content: ""})
            }
          }
        }
        console.log("sellingPoints2",sellingPoints);
        inputModel = {
          pkey: row.pkey,
          mtype: row.mtype,
          supplier: row.supplier || "",
          gtype: row.gtype, //分类pkey
          goodsMain: row.goodsMain, //商品库pkey
          threeGtype: row.threeGtype,
          title: row.title,
          xsNum: row.xsNum,
          isPostage: row.isPostage, //是否免邮
          sort: row.sort,
          guessSort: row.guessSort,
          purchaseNum: row.purchaseNum, //每日限购
          tag: row.tag,
          sellingPoints: sellingPoints,
          spaces: JSON.parse(JSON.stringify(row.spaces)),
          description: row.description,
          serialNumber: row.serialNumber,
          startDate: row.startDate,
          endDate: row.endDate,
          presaleStartDate: row.presaleStartDate,
          presaleEndDate: row.presaleEndDate,
          photo1: row.photo1,
          photo2: row.photo2,
          photo3: row.photo3,
          pickupType: row.pickupType, // 是否自提
          content: row.content,
          content2: row.content2,
          enabled: row.enabled,
          extendCon:
            row.mtype == 'GIFT_GOODS' ? parseInt(row.extendCon) : row.extendCon,
          collageNum: row.mtype == 'COLLAGE_GOODS' ? row.extendCon : '0',
          guessLike: row.guessLike,
          limitCost: row.limitCost,
          cost: row.cost,
          userType: row.userType,
          userFarmer: row.userFarmer,
          userGoods: row.userGoods,
          expireChoose: row.expireChoose,
          userVendor: row.userVendor,
          giftEndDate: row.giftEndDate,
          giftStartDate: row.giftStartDate,
          vendor: row.vendor,
          visibleRange: row.visibleRange,
          tagKeys: row.tagKeys,
          msdTags: row.msdTags,
        };
        let cutList;
        if (row.extendConList) {
          cutList = row.extendConList.map((item) => {
            return item.split(',');
          });
        }

        this.$refs.updateComp.show();
        setTimeout(() => {
          this.$refs.updateComp.initData({ inputModel: inputModel, cutList });
        }, 0);
      }
    },
    hide: function () {
      this.$emit('refresh');
    },
    /**
     * 保存数据
     */
    handleUpdate: function ({ inputModel }) {
      const params = inputModel;
      this.$refs.updateComp.loading = true;
      let url = api.goods.updGoods;
      if (inputModel.mtype == 'COUPON_GOODS') {
        url = api.mall.updGoodsCoupon;
      }
      axios
        .post(url, params, {
          headers: {
            Authorization: this.$store.state.token,
            'Content-Type': 'application/json',
          },
        })
        .then(() => {
          this.$message.success('修改成功');
          this.$emit('refresh');
          this.$refs.updateComp.hide();
          if (params.mtype == 'MARKET_GOODS') {
            axios
              .post(
                api.goods.checkPrice,
                qs.stringify({
                  goodsPkey: params.pkey,
                }),
                {
                  headers: {
                    Authorization: this.$store.state.token,
                  },
                }
              )
              .then((response) => {
                if (response && !response.hasOwnProperty('result')) {
                  this.$alert(response, '提示', {
                    confirmButtonText: '确定',
                  });
                }
              });
          }
        });
      setTimeout(() => {
        this.$refs.updateComp.loading = false;
      }, 300);
    },
  },
};
</script>