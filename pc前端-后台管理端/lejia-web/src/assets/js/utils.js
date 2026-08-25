import Vue from "vue";
import store from "@/store";
import { MenuData, MenuIcon } from "@/assets/js/menu";

export default {
    /**
     * 更新导航菜单
     * @pkey  唯一标识
     * @title 名称
     * @code  后台定义的
     * @url   路由
     * @icon  图标
     * @return 新的导航菜单
     */
    updateMenuList: function({ menuList }) {
        console.log('menuList', menuList);
        let newMenuList = [],
            productPkey = "",
            mainUrl = "",
            userInfo = JSON.parse(localStorage.getItem("userinfo"));
        menuList.forEach(item => {
            let subList = [];
            //二级菜单
            if (item.sub) {
                item.sub.forEach((subitem, subindex) => {
                    console.log('subitem', subitem);
                    let thirdList = [];

                    //三级菜单
                    if (subitem.sub) {
                        subitem.sub.forEach((thirditem, thirdindex) => {
                            let thirdMenu = {
                                pkey: thirditem.pkey,
                                title: thirditem.name,
                                code: thirditem.code,
                                url: MenuData[thirditem.code]
                            };

                            thirdList.push(thirdMenu);
                        });
                    }

                    if (
                        (userInfo.domainRoles['*'] &&
                            userInfo.domainRoles['*'][0].pkey == "zy_company_head" &&
                            subitem.code.indexOf("_2") < 0) ||
                        !userInfo.domainRoles['*'] ||
                        userInfo.domainRoles['*'][0].pkey != "zy_company_head"
                    ) {
                        let subMenu = {
                            pkey: subitem.pkey,
                            title: subitem.name,
                            code: subitem.code,
                            sub: thirdList,
                            url: MenuData[subitem.code]
                        };
                        if (subitem.code == "zy_market_vendor_settlement") {
                            subMenu.url = MenuData["zy_market_vendor_settlements"];
                        } else if (subitem.code == "zy_market_vendor_bill") {
                            subMenu.url = MenuData["zy_market_vendor_bills"];
                        }

                        subList.push(subMenu);
                    }
                });
            }
            //一级菜单
            let List = {
                pkey: item.pkey,
                title: item.name,
                code: item.code,
                icon: MenuIcon[item.code],
                url: subList.length ? subList[0].url : MenuData[item.code],
                // url: MenuData[item.code],
                sub: subList
            };
            newMenuList.push(List);
        });
        return newMenuList;
    },
    /**
     * 身份证
     * 输入时只允许输入数字和字母
     * @keyup.active="text=clearNoNum(text)"
     */
    clearNoNumLetter(text) {
        let txt = text;
        txt = txt.replace(/[\W]/g, ""); //清除"数字"和"字母"以外的字符
        txt = txt.replace(/^\./g, ""); //验证第一个字符是数字而不是
        return txt;
    },
    /**
     * 身份证验证
     */
    IdentityCodeValid(idCode) {
        // 加权因子
        var weight_factor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
        // 校验码
        var check_code = ["1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"];

        var code = idCode + "";
        var last = idCode[17]; //最后一个

        var seventeen = code.substring(0, 17);

        // ISO 7064:1983.MOD 11-2
        // 判断最后一位校验码是否正确
        var arr = seventeen.split("");
        var len = arr.length;
        var num = 0;
        for (var i = 0; i < len; i++) {
            num = num + arr[i] * weight_factor[i];
        }

        // 获取余数
        var resisue = num % 11;
        var last_no = check_code[resisue];

        // 格式的正则
        // 正则思路
        /*
         第一位不可能是0
         第二位到第六位可以是0-9
         第七位到第十位是年份，所以七八位为19或者20
         十一位和十二位是月份，这两位是01-12之间的数值
        十三位和十四位是日期，是从01-31之间的数值
         十五，十六，十七都是数字0-9
         十八位可能是数字0-9，也可能是X
         */
        var idcard_patter = /^[1-9][0-9]{5}([1][9][0-9]{2}|[2][0][0|1][0-9])([0][1-9]|[1][0|1|2])([0][1-9]|[1|2][0-9]|[3][0|1])[0-9]{3}([0-9]|[X])$/;

        // 判断格式是否正确
        var format = idcard_patter.test(idCode);

        // 返回验证结果，校验码和格式同时正确才算是合法的身份证号码
        return last === last_no && format ? idCode : false;
    },
    /**
     * 检查手机号码是否符合要求
     * @param  {[type]} phone [需要检查的手机号码]
     * @return {[type]}       [true： 格式正确  false：格式错误]
     */
    checkMobile: phone => {
        return /^1\d{10}$/.test(phone);
    },
    /**
     * 检查密码是否符合要求--只能输入6-20个字母、数字、下划线
     * @param  {[type]} password [需要检查的密码]
     * @return {[type]}       [true： 格式正确  false：格式错误]
     */
    checkPassword: password => {
        return /^(\w){6,20}$/.test(password);
    },
    /**
     * 校验email格式
     */
    checkEmail: email => {
        return /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/.test(
            email
        );
    },
    /**
     * 格式化 价格
     */
    formatPrice: function(price) {
        price = price.replace(/[^\d.]/g, ""); //清除"数字"和"."以外的字符
        price = price.replace(/^\./g, ""); //验证第一个字符是数字
        price = price.replace(/\.{2,}/g, "."); //只保留第一个, 清除多余的
        price = price
            .replace(".", "$#$")
            .replace(/\./g, "")
            .replace("$#$", ".");
        price = price.replace(/^(\-)*(\d+)\.(\d\d).*$/, "$1$2.$3"); //只能输入两个小数
        return price;
    },
    /**
     * 格式化 数字
     * 12345格式化为12,345.00
     * @param  {[string]} s [需要转换的数字]
     * @param  {[number]} n [需要保留的小数点]
     */
    formatMoney: function(s, n) {
        n = n >= 0 && n <= 20 ? n : 2;
        s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";
        var l = s
            .split(".")[0]
            .split("")
            .reverse(),
            r = s.split(".")[1],
            t = "";
        for (let i = 0; i < l.length; i++) {
            t += l[i] + ((i + 1) % 3 == 0 && i + 1 != l.length ? "," : "");
        }
        let a = t
            .split("")
            .reverse()
            .join("");
        return n == 0 ? a : a + "." + r;
    },
    /**
     * 时间戳转化为年 月 日 时 分 秒
     * number: 传入时间戳
     * format：返回格式，支持自定义，但参数必须与formateArr里保持一致
     */
    formatTimeInArr: function(number, format) {
        var formateArr = ["Y", "M", "D", "h", "m", "s"];
        var returnArr = [];
        let date = new Date(number * 1000);
        const formatNumber = n => {
            n = n.toString();
            return n[1] ? n : "0" + n;
        };
        returnArr.push(date.getFullYear());
        returnArr.push(formatNumber(date.getMonth() + 1));
        returnArr.push(formatNumber(date.getDate()));
        returnArr.push(formatNumber(date.getHours()));
        returnArr.push(formatNumber(date.getMinutes()));
        returnArr.push(formatNumber(date.getSeconds()));
        for (var i in returnArr) {
            format = format.replace(formateArr[i], returnArr[i]);
        }
        return format;
    },
    /**
     * @desc input只能输入数字 并精确到小数点后两位
     * @param {String} val  输入的值
     * @returns val
     */
    inputNumberFixed: val => {
        val = val.replace(/[^\d.]/g, ""); //清除"数字"和"."以外的字符
        val = val.replace(/^\./g, ""); //验证第一个字符是数字
        val = val.replace(/\.{2,}/g, "."); //只保留第一个, 清除多余的
        val = val
            .replace(".", "$#$")
            .replace(/\./g, "")
            .replace("$#$", ".");
        val = val.replace(/^(\-)*(\d+)\.(\d\d).*$/, "$1$2.$3"); //只能输入两个小数
        return val;
    },
    /*
     *获取指定几天前的日期
     */
    getCustDate: num => {
        var date = new Date();
        date.setDate(date.getDate() - num);
        var seperator1 = "-";
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month < 10) {
            month = `0${month}`;
        }
        if (strDate < 10) {
            strDate = `0${strDate}`;
        }

        var currentDate = year + seperator1 + month + seperator1 + strDate;

        return currentDate;
    },
    /*
     *获取一个月之前日期
     */
    getMonthAgoDate: () => {
        var date = new Date();
        var seperator1 = "-";
        var year = date.getFullYear();
        var month = date.getMonth();
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentDate = year + seperator1 + month + seperator1 + strDate;
        return currentDate;
    },
    /*
     *获取今日日期
     */
    getNowDate: () => {
        var date = new Date();
        var seperator = "-";
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentDate = year + seperator + month + seperator + strDate;
        return currentDate;
    },
    /**
     * @desc 获取近N个月的日期范围
     * @param {Number} num 多少个月 默认12个月
     */
    getMothRange: (num = 12) => {
        var dataArr = [];
        var data = new Date();
        var year = data.getFullYear();
        data.setMonth(data.getMonth() + 1, 1); //获取到当前月份,设置月份
        for (var i = 0; i < num; i++) {
            data.setMonth(data.getMonth() - 1); //每次循环一次 月份值减1
            var m = data.getMonth() + 1;
            m = m < 10 ? "0" + m : m;
            if (i == 0 || i == num - 1) dataArr.push(data.getFullYear() + "-" + m);
        }
        return [dataArr[1], dataArr[0]];
    },
    urlTypeObj: () => {
        let enums = {
            NOT_URL: '无',
            LINK: '链接',
            POINTS_MALL: '积分商城', // 自营专区
            // MEMBERSHIP: '会员办理',
            GOODS: '商品',
            PERSONAL_CENTER: '个人中心',
            SPECIAL_GOODS: '特价秒杀',
            // MEMBER_GOODS: '会员专区',
            PRESALE_GOODS: '预售专区',
            COOKFD_GOODS: '菜谱专区',
            // CUT_GOODS: '砍价专区',
            // COLLAGE_GOODS: '拼团专区',
            // SHARE_GOODS: '分享专区',
            // POVERTY_ALLEVIATION_GOODS: '扶贫专区',
            CARD_CENTER: '领券中心',
            // POINT_RULES: '积分规则',
            GTYPE: '分类',
            ACTIVITY: '卡券活动',
            VENDOR: '商户',
            WEIXIN_MINI_PROGRAM: '小程序页面',
            BNYP_GOODS: '滨农优品',
            MS_GOODS: '民生专区',
            OFFLINE_STORE: '线下门店',
            JD_GOODS: '京东专区',
        }
        console.log(store);
        if(store.state.ascription == '22' || store.state.ascription == '13') {
            enums.POINTS_MALL = '自营专区'
        }
        if(store.state.userIdentity == '1') {
            delete enums.VENDOR
            delete enums.ACTIVITY
        }
        return enums
    },
    hasContent: (type) => {
        const hasContentList = [ 'LINK', 'GTYPE', 'GOODS', 'VENDOR', 'WEIXIN_MINI_PROGRAM', 'ACTIVITY' ]
        return hasContentList.includes(type)
    }
};