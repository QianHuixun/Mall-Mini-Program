import Vue from "vue";
import Vuex from "vuex";
import utils from "@/assets/js/utils"; //载入更新路由方法
Vue.use(Vuex);

const state = {
  userinfo: JSON.parse(localStorage.getItem("userinfo")), //用户信息
  token: localStorage.getItem("token"),
  menuList: JSON.parse(localStorage.getItem("menuList")), //导航菜单
  activeMenu: localStorage.getItem("activeMenu") === undefined ? JSON.parse(localStorage.getItem("activeMenu")) : "", //选中菜单
  activeName: localStorage.getItem("activeName"), //选中的三级菜单名称
  ascription: localStorage.getItem("ascription"), //SAAS商城身份编号
  userIdentity: localStorage.getItem("userIdentity"), //用户角色
  settlementMethod: localStorage.getItem("settlementMethod"), //市场采购结算方式
  saasName: localStorage.getItem("saasName"),
  saasPhoto: localStorage.getItem("saasPhoto"),
  marketType: localStorage.getItem("marketType"), // 市场类型MARKET_SHOPPING_MALL(1, "市场商城"),VENDOR_SHOPPING_MALL(2, "商户商城"),
  marketPkey: localStorage.getItem("marketPkey"), // 市场PKey
  marketName: localStorage.getItem("marketName"), // 市场名称
};

const mutations = {
  /**用户信息及token值*/
  SET_USERINFO (state, userinfo) {
    localStorage.setItem("userinfo", JSON.stringify(userinfo));
    localStorage.setItem("token", "Bearer " + userinfo.accessToken.access_token);
    state.userinfo = userinfo;
    state.token = "Bearer " + userinfo.accessToken.access_token;
    axios.post(api.common.getIdentity, {}, {
      headers: {
        "Authorization": state.token
      }
    }).then(response => {
      localStorage.setItem("ascription", response.ascription);
      localStorage.setItem("userIdentity", response.identity);// 1 运营 ，2 市场 ， 3 公司
      localStorage.setItem("saasName", response.name);
      localStorage.setItem("saasPhoto", response.photo);
      localStorage.setItem("marketType", response.type);
      localStorage.setItem("marketPkey", response.marketPkey);
      localStorage.setItem("marketName", response.marketName);
      state.ascription = response.ascription;
      state.userIdentity = response.identity;
      state.saasName = response.name;
      state.saasPhoto = response.photo;
      state.marketType = response.type;
      state.marketPkey = response.marketPkey;
      state.marketName = response.marketName;
    });

  },
  /**导航菜单*/
  GET_MENULIST (state, menuList) {
    let menu = utils.updateMenuList({
      menuList: [...menuList]
    });
    localStorage.setItem("menuList", JSON.stringify(menu));
    state.menuList = menu;
  },
  /**获取选中菜单*/
  GET_ACTIVEMENU (state, activeMenu) {
    localStorage.setItem("activeMenu", JSON.stringify(activeMenu));
    state.activeMenu = activeMenu;
  },
  /**获取选中三级菜单*/
  GET_ACTIVENAME (state, activeName) {
    localStorage.setItem("activeName", activeName);
    state.activeName = activeName;
  },
  /**市场采购结算方式 */
  SET_SETTLEMENTMETHOD (state, settlementMethod) {
    localStorage.setItem("settlementMethod", settlementMethod);
    state.settlementMethod = settlementMethod;
  },
};

const actions = {
  /**用户信息*/
  SET_USERINFO ({
    commit
  }, userinfo) {
    commit("SET_USERINFO", userinfo);
  },
  /**导航菜单*/
  GET_MENULIST ({
    commit
  }, menuList) {
    commit("GET_MENULIST", menuList);
  },
  /**获取选中菜单*/
  GET_ACTIVEMENU ({
    commit
  }, activeMenu) {
    commit("GET_ACTIVEMENU", activeMenu);
  },
  /**获取选中三级菜单*/
  GET_ACTIVENAME ({
    commit
  }, activeName) {
    commit("GET_ACTIVENAME", activeName);
  },
  SET_SETTLEMENTMETHOD ({
    commit
  }, settlementMethod) {
    commit("SET_SETTLEMENTMETHOD", settlementMethod);
  },
};


export default new Vuex.Store({
  state,
  mutations,
  actions
});