import store from "@/store";
import qs from "qs";

export default {
    /**
     * [获取角色下拉列表]
     * @return {[roleList]} [返回网点列表Promise对象]
     */
    getRole: function() {
        return axios.post(api.dropdown.roleList, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    /**
     * [获取一级商品分类下拉列表]
     * @return {[TypeList]} [返回分类列表Promise对象]
     */
    getType: function(params = {}) {
        return axios.post(api.mall.queryClassic, qs.stringify(params), {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    getCategory: function(params = {}) {
        return axios.post(api.mall.threeClassic, qs.stringify(params))
    },
    /**
     * [获取商品及规格下拉列表--菜谱]
     * @return {[GoodsList]} [返回商品列表Promise对象]
     */
    getGoods: function(mType = "ALL", page = 0, pagesize = 20) {
        return axios.post(api.goods.queryGoods, qs.stringify({
            mType: mType == "ALL" ? "MARKET_GOODS" : mType,
            page,
            pagesize
        }), {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    /**
     * [获取商品及规格下拉列表--卡券]
     * @return {[GoodsList]} [返回商品列表Promise对象]
     */
    getAllGoods: function() {
        return axios.post(api.dropdown.goodsList, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    /**
     * [获取优惠券下拉列表]
     * @return {[CouponList]} [返回优惠券列表Promise对象]
     */
    getCoupon: function() {
        return axios.post(api.dropdown.couponList, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    /**
     * [获取用户下拉列表]
     * @return {[UserList]} [返回用户列表Promise对象]
     */
    getUser: function() {
        return axios.post(api.marketing.queryMember, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    /**
     * [获取市场下拉列表]
     * @return {[MarketList]} [返回市场列表Promise对象]
     */
    getMarket: function() {
        return axios.post(api.dropdown.marketList, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    /**
     * [获取公司下拉列表]
     * @return {[CompanyList]} [返回公司列表Promise对象]
     */
    getCompany: function() {
        return axios.post(api.market.queryCom, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    /**
     * [获取快递员下拉列表]
     * @return {[CourierList]} [返回快递员列表Promise对象]
     */
    getCourier: function() {
        return axios.post(api.dropdown.courierList, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    /**
     * [获取菜谱分类下拉列表]
     * @return {[CookfdTypeList]} [返回菜谱分类员列表Promise对象]
     */
    getCookfdType: function() {
        return axios.post(api.cookfd.queryCookType, qs.stringify({
            page: 0,
            pagesize: 999,
            enabled: true
        }), {
            headers: {
                "Authorization": store.state.token
            }
        })
    },
    /**
     * [获取合作商户下拉列表]
     * @return {[VendorList]} [返回合作商户列表Promise对象]
     */
    getVendorList: function() {
        return axios.post(api.order.queryVendorPurchase, qs.stringify({
            Pkey: 0
        }), {
            headers: {
                "Authorization": store.state.token
            }
        })
    },
    /**
     * [saas市场下拉列表]
     * @return {[VendorList]} [返回saas市场列表Promise对象]
     */
    getNewMarketList: function() {
        return axios.post(api.dropdown.newMarketList, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    getThridMarketList: function() {
        return axios.post(api.dropdown.thridMarketList, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    /**
     * [标签下拉列表]
     * @return {[TagsList]} [返回标签列表Promise对象]
     */
    getTagsList: function() {
        return axios.post(api.dropdown.tagsList, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    },
    /**
     * [供应商下拉列表]
     * @return {[getSupplierList]} [返回标签列表Promise对象]
     */
    getSupplierList: function() {
        return axios.post(api.dropdown.supplierList, {}, {
            headers: {
                "Authorization": store.state.token
            }
        });
    }
}