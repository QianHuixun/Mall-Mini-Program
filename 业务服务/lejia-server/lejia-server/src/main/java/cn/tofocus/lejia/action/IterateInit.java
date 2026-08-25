package cn.tofocus.lejia.action;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.jd.open.api.sdk.domain.vopdd.QueryBalanceOpenProvider.response.checkAccountBalance.CheckAccountBalanceOpenResp;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.KeyValue;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.express.SfGetFreightAddedServicesResult;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerTime;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWalletLine;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.enums.*;
import cn.tofocus.lejia.bean.enums.member.TagType;
import cn.tofocus.lejia.bean.enums.v2.ZxCardStatus;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.market.*;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysFarmerTimeDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWalletLineDao;
import cn.tofocus.lejia.dao.zx.ZxUserInfoDao;
import cn.tofocus.lejia.domain.IterateManager;
import cn.tofocus.lejia.domain.OrderRefundManager;
import cn.tofocus.lejia.domain.express.ExpressSfManager;
import cn.tofocus.lejia.domain.jd.JdAppOrderManager;
import cn.tofocus.lejia.domain.jd.JdErrorDataManager;
import cn.tofocus.lejia.domain.jd.JdGoodsManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPAddrManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPOrderManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.pay.ChinaUmsPayManager;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsWxQueryResponse;
import cn.tofocus.lejia.domain.v2.RunManager;
import cn.tofocus.lejia.domain.v3.ProblemManager;
import cn.tofocus.lejia.domain.vendor.VendorWalletManager;
import cn.tofocus.lejia.domain.zx.ZxUserManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.utils.DateUtil;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class IterateInit
{
    @Autowired
    private IterateManager manager;
    
    @Autowired
    private ProblemManager problemManager;
    
    @Autowired
    public SysFarmerDao sysFarmerDao;
    
    @Autowired
    public SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private SysFarmerTimeDao sysFarmerTimeDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    public AppOrderManager appOrderManager;
    
    @Autowired
    public MktOrderDao orderDao;
    
    @Autowired
    private VendorWalletManager vendorWalletManager;
    
    @Autowired
    private ZxUserManager zxUserManager;
    
    @Autowired
    private RunManager runManager;
    
    @Autowired
    private JdErrorDataManager jdErrorDataManager;
    
    @Autowired
    private JdAppOrderManager jdAppOrderManager;

    // 2022-05-05 库存调整
    @PostMapping("/init/run3.1.1")
    public Result<Boolean> init3_1_1(@RequestParam(value = "ascription") Integer ascription)
    {
        manager.initKc(ascription);
        return new Result<>(true);
    }

    //    // 版本3.1.0 上线初始化
    //    @PostMapping("/init/run3.1.0")
    //    public Result<Boolean> init3_1_0()
    //    {
    //        // 初始化一个一级分类 优惠券和礼品券
    ////        manager.init3_1_0Gtype();
    //        manager.init3_1_0AccessLog();
    //        return new Result<>(true);
    //    }

    @PostMapping("/init/run/test/reset")
    public Result<Boolean> te()
    {
//        domainAdminApi.resetDomain();
        return new Result<>(true);
    }

    @PostMapping("/upd/fileurl")
    public Result<Boolean> updFileUrl()
    {
        manager.updFileUrl();
        return new Result<>(true);
    }

    @PostMapping("/runProblemType")
    public Result<Boolean> runProblemType()
    {
        problemManager.runProblemType();
        return new Result<>(true);
    }

    @PostMapping("/runFarmerTime")
    public Result<Boolean> runFarmerTime()
    {
        List<SysFarmer> findAll = sysFarmerDao.select().notLike("pkey", Constant.Operation).exec();
        List<SysFarmerTime> list = new ArrayList<>();
        for(SysFarmer f : findAll)
        {
            f.setType(FarmerType.MARKET_SHOPPING_MALL);
            SysFarmerConfig config = f.getConfig();
            if(config != null && StringUtils.isNotBlank(config.getYytb()) && StringUtils.isNotBlank(config.getYyte()))
            {
                String[] s1 = config.getYytb().split(":");
                String[] s2 = config.getYyte().split(":");
                SysFarmerTime ft = new SysFarmerTime();
                ft.setFarmer(f.getPkey());
                ft.setAscription(f.getAscription());
                ft.setStartHour(Integer.valueOf(s1[0]));
                ft.setStartMinute(Integer.valueOf(s1[1]));
                ft.setEndHour(Integer.valueOf(s2[0]));
                ft.setEndMinute(Integer.valueOf(s2[1]));
                list.add(ft);
            }
        }
        sysFarmerDao.updateAll(findAll);
        sysFarmerTimeDao.addAll(list);
        log.info("新增市场时间{}条", list.size());
        return new Result<>(true);
    }

    @PostMapping("/runVendorSettlementMethodType")
    public Result<Boolean> runVendorSettlementMethodType()
    {
        List<SysFarmerConfig> list = sysFarmerConfigDao.select()
            .eq("settlementMethod", SettlementMethodType.COMMISSION_SETTLEMENT)
            .notLike("pkey", Constant.Operation).exec();
        List<String> keyList = CollectionUtil.keyList(list);
        List<MktVendor> exec = vendorDao.select().in("farmer", keyList).exec();
        for(MktVendor v : exec)
        {
            v.setSettlementMethod(SettlementMethodType.COMMISSION_SETTLEMENT);
        }
        vendorDao.updateAll(exec);
        return new Result<>(true);
    }

    @Autowired
    private OrderRefundManager orderRefundManager;

    @PostMapping("/runOrderLineCouponPrice")
    public Result<Boolean> runOrderLineCouponPrice()
    {
        List<MktOrder> list = orderDao.select().notEq("status", OrderStatus.UNPAID_ORDER)
        .notEq("status", OrderStatus.VOID_ORDER)
        .isNotNull("cardAmt")
        .gt("cardAmt", 0)
        .gt("createdTime", "2024-01-01")
        .exec();
        for(MktOrder o : list)
        {
            orderRefundManager.assembleOrderLine(o.getPkey(), o.getCardAmt());
        }
        return new Result<>(true);
    }

    // 4.1.10 初始化会员最近消费数据
    @Autowired
    private MktMemberDao memberDao;

    @PostMapping("/init/member/recentConsume")
    public Result<String> initMemberRecentConsume()
    {
        int page = 0;
        int pagesize = 1000;
        long num = 0;
        PageResult<MktMember> pageResult = null;
        do
        {
            pageResult = memberDao.selectPage().page(page).pagesize(pagesize).exec();
            for (MktMember member : pageResult)
            {
                MktOrder lastOrder = orderDao.selectOne()
                    .eq(MktOrder.F.member, member.getPkey())
                    .in(MktOrder.F.status,
                        OrderStatus.DELIVERED_ORDER,
                        OrderStatus.SHIPPED_ORDER,
                        OrderStatus.WAIT_ARRIVAL_ORDER,
                        OrderStatus.WAIT_WRITEOFF_ORDER,
                        OrderStatus.ARRIVED_ORDER,
                        OrderStatus.CONFIRM_ORDER,
                        OrderStatus.REFUND_APPLICATION_ORDER,
                        OrderStatus.REFUNDED_ORDER)
                    .sort(MktOrder.F.createdTime)
                    .sort(MktOrder.F.pkey)
                    .exec();
                if (lastOrder != null)
                {
                    memberDao.updLastConsume(member.getPkey(), lastOrder.getCreatedTime(), lastOrder.getFarmer());
                    num++;
                }
            }
            page++;
        }
        while (pageResult.hasNext());
        String s = "执行成功，共初始化会员最近消费数据" + num + "/" + pageResult.getTotalElements();
        log.info("[初始化会员最近消费数据] {}", s);
        return new Result<>(s);
    }

    // 4.1.10 初始化交易送达时间
    @PostMapping("/init/order/qrTime")
    public Result<String> initOrderQrTime()
    {
        int page = 0;
        int pagesize = 1000;
        long num = 0;
        PageResult<MktOrder> pageResult = null;
        do
        {
            pageResult = orderDao.selectPage()
                .page(page)
                .pagesize(pagesize)
                .in(MktOrder.F.status, Lists.newArrayList(OrderStatus.ARRIVED_ORDER, OrderStatus.CONFIRM_ORDER))
                .notEq(MktOrder.F.distributionType, DistributionType.PICKUP)
                .exec();
            for (MktOrder order : pageResult)
            {
                String psTime = order.getPstime();
                if (psTime.length() == 19)
                {
                    Date qrTime = DateUtil.formatDateStr(psTime, "yyyy-MM-dd HH:mm:ss");
                    orderDao.select()
                        .strict(true)
                        .eq(MktOrder.F.pkey, order.getPkey())
                        .update(MktOrder.F.qrTime, qrTime);
                    num++;
                }
            }
            page++;
        }
        while (pageResult.hasNext());
        String s = "执行成功，共初始化交易到货时间" + num;
        log.info("[初始化交易到货时间] {}", s);
        return new Result<>(s);
    }


    /**
     * 天津定制1期批处理，后续删除
     */
    @Autowired
    private MktAddrDao addrDao;

    @PostMapping(value = "/batch/addr/handle")
    public Result<String> batchAddrHandle()
    {
        log.info("开始地址拆分地区处理...");
        int successNum = 0;
        List<MktAddr> addrList = addrDao.select().eq(MktAddr.F.type, AddrType.DELIVERY).exec();
        for (MktAddr addr : addrList)
        {
            String s = addr.getAddr();
            String pro = null;
            String city = null;
            String area = null;

            KeyValue<String, String> zxs = handleZXS(s); // 处理直辖市
            if (zxs != null)
            {
                pro = zxs.getKey();
                city = pro;
                s = zxs.getValue();
            }
            else
            {
                KeyValue<String, String> proKV = handlePro(s); // 处理省
                if (proKV != null)
                {
                    pro = proKV.getKey();
                    s = proKV.getValue();
                    KeyValue<String, String> cityKV = handleCity(s); // 处理市
                    if (cityKV != null)
                    {
                        city = cityKV.getKey();
                        s = cityKV.getValue();
                    }
                }
            }
            if (pro != null && city != null)
            {
                KeyValue<String, String> areaKV = handleArea(s); // 处理区
                if (areaKV != null)
                {
                    area = areaKV.getKey();
                    successNum++;
                }
            }
            if (pro == null && city == null)
            {
                if (s.startsWith("瓯海区") || s.startsWith("甌海區"))
                {
                    pro = "浙江省";
                    city = "温州市";
                    area = "瓯海区";
                    successNum++;
                }
                if (s.startsWith("鹿城区") || s.startsWith("鹿城") || s.startsWith("黎明西路") || s.startsWith("七都街道"))
                {
                    pro = "浙江省";
                    city = "温州市";
                    area = "鹿城区";
                    successNum++;
                }
                if (s.startsWith("海珠区"))
                {
                    pro = "广东省";
                    city = "广州市";
                    area = "海珠区";
                    successNum++;
                }
                if (s.startsWith("青云谱区"))
                {
                    pro = "江西省";
                    city = "南昌市";
                    area = "青云谱区";
                    successNum++;
                }
                if (s.startsWith("滨海新区"))
                {
                    pro = "天津市";
                    city = "天津市";
                    area = "滨海新区";
                    successNum++;
                }
            }
            if (pro != null && pro.length() <= 40) addr.setPro(pro);
            if (city != null && city.length() <= 40) addr.setCity(city);
            if (area != null && area.length() <= 40) addr.setArea(area);
            if (StringUtil.isNotBlank(addr.getAddrDetail())) addr.setAddr(addr.getAddr() + addr.getAddrDetail());
        }
        addrDao.updateAll(addrList);
        String res = "完成地址批处理，共" + successNum + "条成功拆分地区，共" + (addrList.size() - successNum) + "条待人工处理";
        log.info(res);
        return new Result<>(res);
    }

    private KeyValue<String, String> handleZXS(String s)
    {
        List<String> zxsList = Lists.newArrayList("北京市", "天津市", "上海市", "重庆市");
        for (String zxs : zxsList)
        {
            if (s.startsWith(zxs))
            {
                s = s.substring(3);
                if (s.startsWith(zxs)) s = s.substring(3);
                return new KeyValue<>(zxs, s);
            }
        }
        return null;
    }

    private KeyValue<String, String> handlePro(String s)
    {
        int index = s.indexOf("自治区");
        if (index >= 0)
        {
            String pro = s.substring(0, index + 3);
            s = s.substring(index + 3);
            return new KeyValue<>(pro, s);
        }
        index = s.indexOf("省");
        if (index >= 0)
        {
            String pro = s.substring(0, index + 1);
            s = s.substring(index + 1);
            return new KeyValue<>(pro, s);
        }
        return null;
    }

    private KeyValue<String, String> handleCity(String s)
    {
        int index = s.indexOf("自治州");
        if (index >= 0)
        {
            String city = s.substring(0, index + 3);
            s = s.substring(index + 3);
            return new KeyValue<>(city, s);
        }
        index = s.indexOf("市");
        if (index >= 0)
        {
            String city = s.substring(0, index + 1);
            s = s.substring(index + 1);
            return new KeyValue<>(city, s);
        }
        return null;
    }

    private KeyValue<String, String> handleArea(String s)
    {
        int index1 = s.indexOf("县");
        int index2 = s.indexOf("市");
        int index3 = s.indexOf("区");
        List<Integer> list = new ArrayList<>();
        if (index1 > 0) list.add(index1);
        if (index2 > 0) list.add(index2);
        if (index3 > 0)
        {
            String temp = s.substring(index3 - 1, index3);
            if (!"小".equals(temp) && !"社".equals(temp) && !"市".equals(temp)) list.add(index3);
        }
        if (!list.isEmpty())
        {
            int index = Collections.min(list);
            String area = s.substring(0, index + 1);
            s = s.substring(index + 1);
            return new KeyValue<>(area, s);
        }
        return null;
    }

    @PostMapping(value = "/payAfterOrder")
    public Result<Boolean> payAfterOrder(String code)
    {
        MktOrder order = orderDao.selectOne().eq("code", code).exec();
        if(order == null)
        {
            System.out.println("找不到订单");
            return new Result<>(false);
        }
        System.out.println("订单主键: " + order.getPkey());
        appOrderManager.payAfterOrder(order);
        return new Result<>(true);
    }

    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    @Autowired
    private MktOrderLineDao orderLineDao;

    // 上线跑一次
    @PostMapping(value = "/updOrderLineWeight")
    public Result<Boolean> updOrderLineWeight()
    {
        List<MktOrderLine> list = orderLineDao.findAll();
        List<MktGoodsSpace> gsList = goodsSpaceDao.findAll();
        Map<Integer,MktGoodsSpace> map = new HashMap<>();
        gsList.forEach(e -> map.put(e.getPkey(), e));
        for(MktOrderLine ol : list)
        {
            ol.setWeight(BigDecimal.ZERO);
            if(map.containsKey(ol.getSpace()))
            {
                MktGoodsSpace space = map.get(ol.getSpace());
                if(space.getWeight() != null)
                    ol.setWeight(space.getWeight().multiply(new BigDecimal(ol.getNum())));
            }

        }
        orderLineDao.updateAll(list);
        return new Result<>(true);
    }

    // 将老的订单全部确认收货
    @PostMapping(value = "/runTJVendor")
    public Result<Boolean> runTJVendor()
    {
        vendorWalletManager.runTJVendor();
        return new Result<>(true);
    }
    
    // 天津 7.10到7.14 钱已经线下结算 线上流水和商户待结算金额未处理
    @PostMapping(value = "/runWalletBug")
    public Result<Boolean> runWalletBug()
    {
        vendorWalletManager.runWalletBug();
        return new Result<>(true);
    }

    @Value("${sec.courier.wanli.config.onOff:false}")
    private Boolean onOff;

    // 批处理填写技术配置的第三方派送参数
    @PostMapping(value = "/runFarmerTechConfig")
    public Result<String> runFarmerTechConfig()
    {
        List<SysFarmerConfig> list = new ArrayList<>();
        List<SysFarmer> farmers = sysFarmerDao.select().eq(SysFarmer.F.idDel, false).exec();
        for (SysFarmer farmer : farmers)
        {
            SysFarmerConfig fc = sysFarmerConfigDao.get(farmer.getPkey());
            if (fc != null)
            {
                if (Boolean.TRUE.equals(onOff))
                {
                    if (farmer.getAscription() == 13)
                    {
                        fc.setWanliAppId("66b08996e4b0be25155ad5fc");
                        fc.setWanliSecret("f025bf0f22ff47d39662cb126e279176");
                        fc.setStoreId("8d0ee085d29045008d5644e3dacda5c6");
                    }
                    else
                    {
                        fc.setWanliAppId("652f4f1de4b032fb93e9cbd9");
                        fc.setWanliSecret("af87c63304fb47d7936040189a7f90bd");
                        fc.setStoreId("fb19a074812a4b03ab4ff3823ef27e50");
                    }
                }
                else
                {
                    fc.setWanliAppId("6526390260b2c47d9bccf189");
                    fc.setWanliSecret("ee8ffe11657a446b9d3becba41544ff6");
                    fc.setStoreId("861968451f8844148f64ae021d2e089b");
                }
                list.add(fc);
            }
        }
        sysFarmerConfigDao.putAll(list);
        return new Result<>("成功修改第三方派送配置市场共" + list.size() + "个");
    }

    @Autowired
    private ZxUserInfoDao zxUserInfoDao;

    @Autowired
    private SysAscriptionDao sysAscriptionDao;

    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;

    // 初始化zxUserInfo的批处理
    @PostMapping(value = "/runInitZxUserInfo")
    public Result<String> runInitZxUserInfo()
    {
        List<ZxUserInfo> list = new ArrayList<>();
        
        SysAscription ascription = sysAscriptionDao.get(qfAscription);
        if (ascription == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到运营端数据");
        SysFarmer systemFarmer = sysFarmerDao.get(Constant.Operation + qfAscription);
        if (systemFarmer == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到集团市场");
        ZxUserInfo systemInfo = zxUserInfoDao.get(ZxUserType.SYSTEM, systemFarmer.getPkey());
        if (systemInfo == null)
        {
            systemInfo = new ZxUserInfo();
            systemInfo.setType(ZxUserType.SYSTEM);
            systemInfo.setValue(systemFarmer.getPkey());
            systemInfo.setName(ascription.getName());
            systemInfo.setComms(BigDecimal.ZERO);
            systemInfo.setMarketAuto(Boolean.FALSE);
            systemInfo.setVendorAuto(Boolean.FALSE);
            systemInfo.setCardStatus(ZxCardStatus.NOT_BINDING);
            systemInfo.setDelFlag(Boolean.FALSE);
            systemInfo.setAscription(qfAscription);
            list.add(systemInfo);
        }
        
        List<SysFarmer> farmers = sysFarmerDao.select()
            .eq(SysFarmer.F.ascription, qfAscription)
            .notEq(SysFarmer.F.pkey, Constant.Operation + qfAscription)
            .eq(SysFarmer.F.idDel, false)
            .exec();
        for (SysFarmer farmer : farmers)
        {
            ZxUserInfo info = zxUserInfoDao.getByFarmer(farmer.getPkey());
            if (info == null)
            {
                info = new ZxUserInfo();
                info.setType(ZxUserType.SELF_MARKET);
                info.setValue(farmer.getPkey());
                info.setName(farmer.getName());
                info.setVendorAuto(Boolean.FALSE);
                info.setDelFlag(Boolean.FALSE);
                info.setAscription(farmer.getAscription());
                list.add(info);
            }
        }
        
        zxUserInfoDao.putAll(list);
        return new Result<>("成功初始化中信用户数据共" + list.size() + "个");
    }
    
    @Autowired
    private MktVendorWalletLineDao mktVendorWalletLineDao;
    
    @Autowired
    private MktOrderDao mktOrderDao;
    
    // 批处理 mkt_vendor_wallet_line 历史数据的 order_time
    @PostMapping(value = "/runHandleOrderTime4WalletLine")
    public Result<String> runHandleOrderTime4WalletLine()
    {
        long num = 0;
        int page = 0;
        int pageSize = 1000;
        boolean hasNext = true;
        while (hasNext)
        {
            PageResult<MktVendorWalletLine> pageResult =
                mktVendorWalletLineDao.selectPage().page(page).pagesize(pageSize).exec();
            List<MktVendorWalletLine> list = new ArrayList<>();
            for (MktVendorWalletLine line : pageResult)
            {
                if (StringUtil.isNotBlank(line.getFormId()))
                {
                    MktOrder order = mktOrderDao.selectOne().eq(MktOrder.F.code, line.getFormId()).exec();
                    if (order != null)
                    {
                        line.setOrderTime(order.getCreatedTime());
                        list.add(line);
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(list))
            {
                mktVendorWalletLineDao.updateAll(list);
                log.info("成功处理 mkt_vendor_wallet_line 历史数据共" + list.size() + "条");
                num += list.size();
            }
            
            hasNext = pageResult.hasNext();
            page++;
        }
     
        return new Result<>("处理 mkt_vendor_wallet_line 历史数据全部完成，共成功处理" + num + "条记录");
    }
    
    
    // 批处理 mkt_vendor_wallet_line 历史数据的 order_time
    @PostMapping(value = "/runTjZxUserId")
    public Result<Boolean> runTjZxUserId()
    {
        zxUserManager.runZxUserId();
        return new Result<>(true);
    }
    @PostMapping(value = "/runKc")
    public Result<Boolean> runKc()
    {
        runManager.runKc();
        return new Result<>(true);
    }

    @Autowired
    private JdVOPAddrManager jdVOPAddrManager;

    @PostMapping(value = "/jd/vop/syncAddr")
    public Result<Boolean> syncJdVOPAddr()
    {
        jdVOPAddrManager.syncJdAddrTask();
        return new Result<>(true);
    }
    
    @Autowired
    private JdVOPOrderManager jdVOPOrderManager;
    
    @PostMapping(value = "/jd/vop/checkAccountBalance")
    public Result<CheckAccountBalanceOpenResp> check()
    {
        CheckAccountBalanceOpenResp resp = jdVOPOrderManager.checkAccountBalance();
        return new Result<>(resp);
    }

    @Autowired
    private JdGoodsManager jdGoodsManager;

    @Autowired
    private JdGoodsDao jdGoodsDao;

    @PostMapping(value = "/jd/vop/syncLowestBuy4AllSku")
    public Result<String> syncLowestBy4AllSku()
    {
        jdGoodsManager.syncLowestBuy4AllSku();
        return new Result<>("全量同步京东sku最低起购量任务已创建");
    }

    // 统计所有京东订单明细各商品销量并回写 jd_goods.xs_num
    @PostMapping(value = "/jd/goods/xsNum/init")
    public Result<String> initJdGoodsXsNum()
    {
        // 关联 mkt_order 过滤京东订单（不限状态），按商品聚合明细 num 合计
        List<JdGoodsXsNum> list = orderLineDao.joinSelect()
            .groupby(MktOrderLine.F.goods)
            .sum(MktOrderLine.F.num, "num")
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .eq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .endJoin()
            .exec(JdGoodsXsNum.class);
        // 按商品回写销量
        for (JdGoodsXsNum g : list)
        {
            if (g.getGoods() == null || g.getNum() == null)
                continue;
            jdGoodsDao.increaseXsNum(g.getGoods(), g.getNum().intValue());
        }
        String res = "执行成功，共更新商品销量" + list.size() + "个";
        log.info("[初始化京东商品销量] {}", res);
        return new Result<>(res);
    }

    @Data
    public static class JdGoodsXsNum
    {
        private Long goods;
        private Long num;
    }
    
    @Autowired
    private ChinaUmsPayManager chinaUmsPayManager;
    
    @PostMapping(value = "/chinaUms/query")
    public Result<ChinaUmsWxQueryResponse> chinaUmsQuery(String merOrderId)
    {
        ChinaUmsWxQueryResponse resp = chinaUmsPayManager.chinaUmsQuery(merOrderId);
        return new Result<>(resp);
    }
    
    @Autowired
    private ExpressSfManager expressSfManager;
    
    @PostMapping(value = "/express/sf/getFreight")
    public Result<SfGetFreightAddedServicesResult> getSfFreight(String appId, String sk, String monthlyCard,
        String senderProvince, String senderCity, String senderAddress, String receiverProvince, String receiverCity,
        String receiverAddress, String sendContent, BigDecimal sendWeight)
    {
        SfGetFreightAddedServicesResult res = expressSfManager.getFreightAddedServices(appId,
            sk,
            monthlyCard,
            senderProvince,
            senderCity,
            senderAddress,
            receiverProvince,
            receiverCity,
            receiverAddress,
            sendContent,
            sendWeight);
        return new Result<>(res);
    }
    
    /**
     * 批量将所有热力豆账户关联的标签的类型设为热力豆标签
     */
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Autowired
    private MktTagDao tagDao;
    
    @PostMapping(value = "/updMsdTagType")
    public Result<String> updMsdTagType()
    {
        List<Integer> tags = memberMsdDao.select().execDto(MktMemberMsd.F.tag, Integer.class);
        Set<Integer> tagSet = new HashSet<>(tags);
        tagDao.updateType(tagSet, TagType.MSD);
        return new Result<>("成功更新" + tagSet.size() + "个标签为热力豆标签");
    }
    
    @PostMapping(value = "/processRefund")
    public Result<Boolean> processRefund()
    {
        jdErrorDataManager.processRefund();
        return new Result<>(true);
    }
    @PostMapping(value = "/processOrderSplit")
    public Result<Boolean> processOrderSplit()
    {
        jdErrorDataManager.processOrderSplit();
        return new Result<>(true);
    }
    
    @PostMapping(value = "/jdRefundAmtError")
    public Result<Boolean> jdRefundAmtError()
    {
        jdErrorDataManager.jdRefundAmtError();
        return new Result<>(true);
    }
    @PostMapping(value = "/refundWxPay")
    public Result<Boolean> refundWxPay(Integer pkey)
    {
        MktOrder order = orderDao.get(pkey);
        jdAppOrderManager.refundWxPay(order);
        return new Result<>(true);
    }
}
