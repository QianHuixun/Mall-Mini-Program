package cn.tofocus.lejia;

import java.math.BigDecimal;
import java.util.*;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.jd.AppJdGoodsOnPage;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberAddrFourArea;
import cn.tofocus.lejia.bean.dto.jd.JdSplitOrderLine;
import cn.tofocus.lejia.domain.app.AppJdManager;
import cn.tofocus.lejia.domain.app.AppMemberAddrManager;
import cn.tofocus.lejia.domain.jd.JdAppOrderManager;
import cn.tofocus.lejia.utils.DateUtil;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.updateSendInfo.WaybillInfoVoOpenReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jd.open.api.sdk.domain.vopdd.OperaOrderOpenProvider.request.submitOrder.*;
import com.jd.open.api.sdk.domain.vopdd.OperaOrderOpenProvider.response.submitOrder.QueryOrderOpenResp;
import com.jd.open.api.sdk.domain.vopdd.QueryBalanceOpenProvider.response.checkAccountBalance.CheckAccountBalanceOpenResp;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryDeliveryInfo.DeliveryInfoQueryOpenResp;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.querySkuFreight.FreightQueryOpenResp;
import com.jd.open.api.sdk.domain.vopdz.QueryAddressOpenProvider.response.queryJdAreaIdList.AreaInfoBaseResp;
import com.jd.open.api.sdk.domain.vopkc.SkuInfoGoodsProvider.response.getNewStockById.GetStockByIdGoodsResp;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.createAfsApply.*;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.getAfsOutline.AfsOutLineOpenResp;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.getGoodsAttributes.SupportedInfoOpenResp;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.queryAfsAddressInfos.AfsAddressInfoOpenResp;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.queryLogicticsInfo.WayBillInfoOpenResp;
import com.jd.open.api.sdk.domain.vopsp.CategoryInfoGoodsProvider.response.getCategoryInfoList.GetCategoryInfoGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.checkAreaLimitList.CheckAreaLimitGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.checkSkuSaleList.CheckSkuSaleGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSellPrice.GetSellPriceGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSimilarSkuList.GetSimilarSkuGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuDetailInfo.GetSkuPoolInfoGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuImageList.GetSkuImageGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkusAllSaleState.GetSkuCanSaleResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.querySkuAreaLimit.QuerySkuAreaLimitResp;
import com.jd.open.api.sdk.domain.vopsp.SkuPoolGoodsProvider.response.getSkuPoolInfo.GetSkuPoolInfoItemGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuPoolGoodsProvider.response.querySkuByPage.OpenPagingResult;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.security.RSAUtils;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.app.jd.JdOrderDeliveryInfo;
import cn.tofocus.lejia.bean.entity.jd.JdAddress;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.dao.jd.JdAddressDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.jd.JdOrderCorrelationDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.domain.jdvop.*;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPAreaInfo;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPSkuNum;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class ManualJdVOPTest
{
    @Autowired
    private JdVOPGoodsManager jdVOPGoodsManager;
    
    @Autowired
    private JdVOPAddrManager jdVOPAddrManager;
    
    @Autowired
    private JdVOPOrderManager jdVOPOrderManager;
    
    @Autowired
    private JdVOPMsgManager jdVOPMsgManager;
    
    @Autowired
    private JdVOPAfsManager jdVOPAfsManager;
    
    @Autowired
    private JdAddressDao jdAddressDao;
    
    @Autowired
    private JdGoodsDao jdGoodsDao;

    @Autowired
    private AppJdManager appJdManager;
    
    @Autowired
    private JdOrderCorrelationDao jdOrderCorrelationDao;

    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Test
    public void test()
    {
        PageResult<AppJdGoodsOnPage> res = appJdManager.byTitleGoods(0, 10, "酸奶");
        System.out.println(JsonUtil.toString(res));
    }
    
    @Test
    public void getSkuPoolInfo()
    {
        List<GetSkuPoolInfoItemGoodsResp> list = jdVOPGoodsManager.getSkuPoolInfo();
        System.out.println(JsonUtil.toString(list));
    }
    
    @Test
    public void querySkuByPage()
    {
        OpenPagingResult result = jdVOPGoodsManager.querySkuByPage("20260227", 0, 100);
        System.out.println(JsonUtil.toString(result));
    }
    
    @Test
    public void getSkuDetailInfo()
    {
        //List<JdGoods> all = jdGoodsDao.findAll();
        //List<Long> keyList = CollectionUtil.keyList(all);
        List<Long> list = Lists.newArrayList(100040246133L, 100040246135L);
        Set<Integer> queryExtSet = Sets.newHashSet(3);
        int i = 1;
        for (long skuId : list)
        {
//            GetSkuPoolInfoGoodsResp result = jdVOPGoodsManager.getSkuDetailInfo(skuId, queryExtSet);
            GetSkuPoolInfoGoodsResp result = jdVOPGoodsManager.getSkuDetailInfo(skuId, null);
//            System.out.println("i: " + i++);
            System.out.println(JsonUtil.toString(result));
//            if(result.getLowestBuy() != null)
//            {
//                System.out.println("result.getSkuId(): " + result.getSkuId());
//                System.out.println("lowestBuy: " + result.getLowestBuy());
//            System.out.println(JsonUtil.toString(result));
//            System.out.println(JsonUtil.toString(result.getSpuId()));
//            System.out.println(JsonUtil.toString(result.getSpuName()));
//            System.out.println(JsonUtil.toString(result.getLowestBuy()));
//            }
        }
    }
    
    @Test
    public void getSimilarSkuList()
    {
        List<GetSimilarSkuGoodsResp> list = jdVOPGoodsManager.getSimilarSkuList(100149591950L);
        System.out.println(JsonUtil.toString(list));
    }
    
    @Test
    public void getSkuImageList()
    {
        List<Long> skuIdList = Lists.newArrayList(241191L, 241204L);
        List<GetSkuImageGoodsResp> list = jdVOPGoodsManager.getSkuImageList(skuIdList);
        System.out.println(JsonUtil.toString(list));
    }
    
    @Test
    public void querySkuAreaLimit()
    {
        List<Long> skuIdList = Lists.newArrayList(241191L, 241204L);
        List<QuerySkuAreaLimitResp> list = jdVOPGoodsManager.querySkuAreaLimit(skuIdList);
        System.out.println(JsonUtil.toString(list));
    }
    
    @Test
    public void checkAreaLimitList()
    {
        List<Long> skuIdList = Lists.newArrayList(241191L, 241204L);
        List<CheckAreaLimitGoodsResp> list = jdVOPGoodsManager.checkAreaLimitList(skuIdList, 7L, 412L, 2782L, 47077L);
        System.out.println(JsonUtil.toString(list));
    }
    
    @Test
    public void getSellPrice()
    {
        List<Long> skuIdList = Lists.newArrayList(100013159743L);
        List<GetSellPriceGoodsResp> list = jdVOPGoodsManager.getSellPrice(skuIdList);
        System.out.println(JsonUtil.toString(list));
    }
    
    @Test
    public void checkSkuSaleList()
    {
        List<Long> skuIdList = Lists.newArrayList(100146441480L);
        List<CheckSkuSaleGoodsResp> list = jdVOPGoodsManager.checkSkuSaleList(skuIdList);
        System.out.println(JsonUtil.toString(list));
        for (CheckSkuSaleGoodsResp item: list)
        {
            System.out.println(item.getSaleState()+"\t"+item.getBanCause());
        }
    }
    
    @Test
    public void getCategoryInfoList()
    {
        Set<Long> categoryIdSet = Sets.newHashSet(37462L, 37564L, 37711L);
        List<GetCategoryInfoGoodsResp> list = jdVOPGoodsManager.getCategoryInfoList(categoryIdSet);
        System.out.println(JsonUtil.toString(list));
    }
    
    @Test
    public void getNewStockById()
    {
        List<JdVOPSkuNum> skuNumInfoList = new ArrayList<>();
        skuNumInfoList.add(new JdVOPSkuNum(1345200L, 1));
        //skuNumInfoList.add(new JdVOPSkuNum(241204L, 2));
        JdVOPAreaInfo areaInfo = new JdVOPAreaInfo(3L, 51044L, 55574L, 0L);
        
        List<GetStockByIdGoodsResp> list = jdVOPGoodsManager.getNewStockById(skuNumInfoList, areaInfo);
        System.out.println(JsonUtil.toString(list));
    }
    
    @Test
    public void getSkusAllSaleState()
    {
        List<JdVOPSkuNum> skuNumInfoList = new ArrayList<>();
        skuNumInfoList.add(new JdVOPSkuNum(241191L, 1));
        skuNumInfoList.add(new JdVOPSkuNum(241204L, 2));
        JdVOPAreaInfo areaInfo = new JdVOPAreaInfo(15L, 1233L, 42324L, 59244L);
        
        List<GetSkuCanSaleResp> list = jdVOPGoodsManager.getSkusAllSaleState(skuNumInfoList, areaInfo);
        System.out.println(JsonUtil.toString(list));
    }
    
    @Test
    public void querySkuFreight()
    {
        List<JdVOPSkuNum> skuNumInfoList = new ArrayList<>();
        skuNumInfoList.add(new JdVOPSkuNum(2191979L, 1));
        //skuNumInfoList.add(new JdVOPSkuNum(241204L, 2));
        JdVOPAreaInfo areaInfo = new JdVOPAreaInfo(15L, 1233L, 42324L, 59244L);
        
        FreightQueryOpenResp resp = jdVOPOrderManager.querySkuFreight(skuNumInfoList, areaInfo, 4);
        System.out.println(JsonUtil.toString(resp));
    }
    
    @Test
    public void submitOrder()
    {
        SubmitOrderOpenReq req = new SubmitOrderOpenReq();
        // 是否预占库存，0是预占库存（需要调用确认订单接口），1是不预占库存，直接进入生产
        req.setSubmitStateType(0);
        req.setThirdOrderId("TEST202603101120000001");
        
        List<SkuInfoOrderOpenReq> skuInfoList = new ArrayList<>();
        SkuInfoOrderOpenReq skuInfo1 = new SkuInfoOrderOpenReq();
        skuInfo1.setSkuId(241191L);
        skuInfo1.setSkuNum(1);
        skuInfo1.setSkuUnitPrice(new BigDecimal("22.00"));
        skuInfoList.add(skuInfo1);
        SkuInfoOrderOpenReq skuInfo2 = new SkuInfoOrderOpenReq();
        skuInfo2.setSkuId(241204L);
        skuInfo2.setSkuNum(2);
        skuInfo2.setSkuUnitPrice(new BigDecimal("20.80"));
        skuInfoList.add(skuInfo2);
        req.setSkuInfoList(skuInfoList);
        
        PaymentInfoOrderOpenReq paymentInfo = new PaymentInfoOrderOpenReq();
        paymentInfo.setPaymentType(4);
        req.setPaymentInfo(paymentInfo);
        
        ConsigneeInfoOrderOpenReq consigneeInfo = new ConsigneeInfoOrderOpenReq();
        consigneeInfo.setConsigneeName("测试人员");
        consigneeInfo.setConsigneeProvinceId(1L);
        consigneeInfo.setConsigneeCityId(2810L);
        consigneeInfo.setConsigneeCountyId(51081L);
        consigneeInfo.setConsigneeTownId(0L);
        consigneeInfo.setConsigneeAddress("北京市经济技术开发区京东大厦四号楼");
        consigneeInfo.setConsigneeMobile("13186809930");
        req.setConsigneeInfo(consigneeInfo);
        
        InvoiceInfoOrderOpenReq invoiceInfo = new InvoiceInfoOrderOpenReq();
        // 发票类型（23:增值税普通发票,24:增值税专用发票） 当发票类型为24时，开票方式只支持2集中开票
        invoiceInfo.setInvoiceType(23);
        invoiceInfo.setInvoicePutType(4);
        invoiceInfo.setInvoicePhone("131850XXXXX");
        req.setInvoiceInfo(invoiceInfo);
        
        QueryOrderOpenResp resp = jdVOPOrderManager.submitOrder(req);
        System.out.println(JsonUtil.toString(resp));
    }
    
    @Test
    public void checkAccountBalance()
    {
        CheckAccountBalanceOpenResp resp = jdVOPOrderManager.checkAccountBalance();
        System.out.println(JsonUtil.toString(resp));
    }
    
    @Test
    @Rollback(false)
    public void syncAllAddress()
    {
        Integer firstLevel = 1;
        Integer secondLevel = 2;
        Integer thirdLevel = 3;
        Integer fourthLevel = 4;
        Map<Long, JdAddress> beanMap = new HashMap<>();
        List<AreaInfoBaseResp> firstList = jdVOPAddrManager.queryJdAreaIdList(firstLevel, null);
        int firstNum = 0;
        for (AreaInfoBaseResp first : firstList)
        {
            JdAddress firstBean = new JdAddress();
            firstBean.setAreaId(first.getAreaId());
            firstBean.setAreaName(first.getAreaName());
            firstBean.setAreaLevel(firstLevel);
            firstBean.setParent(null);
            beanMap.put(firstBean.getAreaId(), firstBean);
            
            List<AreaInfoBaseResp> secondList = jdVOPAddrManager.queryJdAreaIdList(secondLevel, first.getAreaId());
            for (AreaInfoBaseResp second : secondList)
            {
                JdAddress secondBean = new JdAddress();
                secondBean.setAreaId(second.getAreaId());
                secondBean.setAreaName(second.getAreaName());
                secondBean.setAreaLevel(secondLevel);
                secondBean.setParent(first.getAreaId());
                beanMap.put(secondBean.getAreaId(), secondBean);
                
                List<AreaInfoBaseResp> thirdList = jdVOPAddrManager.queryJdAreaIdList(thirdLevel, second.getAreaId());
                for (AreaInfoBaseResp third : thirdList)
                {
                    JdAddress thirdBean = new JdAddress();
                    thirdBean.setAreaId(third.getAreaId());
                    thirdBean.setAreaName(third.getAreaName());
                    thirdBean.setAreaLevel(thirdLevel);
                    thirdBean.setParent(second.getAreaId());
                    beanMap.put(thirdBean.getAreaId(), thirdBean);
                    
                    List<AreaInfoBaseResp> fourthList =
                        jdVOPAddrManager.queryJdAreaIdList(fourthLevel, third.getAreaId());
                    for (AreaInfoBaseResp fourth : fourthList)
                    {
                        JdAddress fourthBean = new JdAddress();
                        fourthBean.setAreaId(fourth.getAreaId());
                        fourthBean.setAreaName(fourth.getAreaName());
                        fourthBean.setAreaLevel(fourthLevel);
                        fourthBean.setParent(third.getAreaId());
                        beanMap.put(fourthBean.getAreaId(), fourthBean);
                    }
                }
            }
            firstNum++;
            log.info("[京东VOP]地址同步-[{}]的子类目查询完成，查询进度{}/{}", first.getAreaName(), firstNum, firstList.size());
        }
        log.info("[京东VOP]地址同步-查询完成");
        // 查询数据库内areaId
        List<Long> oldIds = jdAddressDao.select().execDto(JdAddress.F.areaId, Long.class);
        List<Long> newIds = Lists.newArrayListWithCapacity(beanMap.size());
        newIds.addAll(beanMap.keySet());
        // 新areaId删除数据库内areaId，结果为新增数据
        newIds.removeAll(oldIds);
        // 数据库内areaId删除map内key，结果为删除数据
        oldIds.removeAll(beanMap.keySet());
        // 删除数据
        if (!oldIds.isEmpty())
        {
            jdAddressDao.removeAllById(oldIds);
            log.info("[京东VOP]地址同步-删除弃用数据完成");
        }
        else
        {
            log.info("[京东VOP]地址同步-没有删除弃用数据");
        }
        // 新增数据
        List<JdAddress> beans = Lists.newArrayListWithCapacity(newIds.size());
        for (Long newId : newIds)
        {
            beans.add(beanMap.get(newId));
        }
        int batchSize = 1000; // 每批处理条数
        int totalSize = beans.size();
        for (int i = 0; i < totalSize; i += batchSize)
        {
            int end = Math.min(i + batchSize, totalSize);
            List<JdAddress> toAdd = beans.subList(i, end);
            jdAddressDao.addAll(toAdd);
            log.info("[京东VOP]地址同步-新增同步新数据，进度{}/{}", end, totalSize);
        }
        log.info("[京东VOP]地址同步已完成");
    }

    @Test
    public void list3LevelsPro()
    {
        Set<Long> ids = Sets.newHashSet();
        Set<String> names = Sets.newHashSet();
        List<JdAddress> pros = jdAddressDao.select().eq(JdAddress.F.areaLevel, 1).exec();
        for (JdAddress pro : pros)
        {
            List<JdAddress> cities = jdAddressDao.listByParent(pro.getPkey(), JdAddress.class);
            for (JdAddress city : cities)
            {
                List<JdAddress> areas = jdAddressDao.listByParent(city.getPkey(), JdAddress.class);
                for (JdAddress area : areas)
                {
                    List<JdAddress> towns = jdAddressDao.listByParent(area.getPkey(), JdAddress.class);
                    if (CollectionUtil.isEmpty(towns))
                    {
                        ids.add(pro.getPkey());
                        names.add(pro.getAreaName());
                    }
                }
            }
        }
        System.out.println(JsonUtil.toString(ids));
        System.out.println(JsonUtil.toString(names));
    }
    
    @Test
    public void testMatchArea()
    {
        String pro = "天津市";
        String city = "天津市";
        String area = "津南区";
        
        // 先按区查，分别按areaName或clientName查
        List<JdAddress> areaList = jdAddressDao.listByName(area);
        if (CollectionUtil.isEmpty(areaList))
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "匹配不到地区");
        for (JdAddress areaBean : areaList)
        {
            if (areaBean.getParent() != null)
            {
                JdAddress parent = jdAddressDao.get(areaBean.getParent());
                if (parent != null && (city.equals(parent.getAreaName()) || city.equals(parent.getClientName())))
                {
                    if (parent.getParent() != null)
                    {
                        JdAddress grandParent = jdAddressDao.get(parent.getParent());
                        if (grandParent != null
                            && (pro.equals(grandParent.getAreaName()) || pro.equals(grandParent.getClientName())))
                        {
                            System.out.println(JsonUtil.toString(grandParent));
                            System.out.println(JsonUtil.toString(parent));
                            System.out.println(JsonUtil.toString(areaBean));
                            return;
                        }
                    }
                    else
                    {
                        System.out.println(JsonUtil.toString(parent));
                        System.out.println(JsonUtil.toString(areaBean));
                        return;
                    }
                }
            }
        }
        throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "匹配不到地区");
    }
    
    @Test
    public void convert2AreaInfo()
    {
        MktAddr addr = new MktAddr();
        addr.setPro("天津市");
        addr.setCity("天津市");
        addr.setArea("河东区");
        addr.setTown("鲁山道街道");
        JdVOPAreaInfo areaInfo = jdVOPAddrManager.convert2AreaInfo(addr);
        System.out.println(JsonUtil.toString(areaInfo, true));
    }

    @Autowired
    private AppMemberAddrManager appMemberAddrManager;

    @Test
    public void convertFourAreaByLatLng()
    {
        BigDecimal longitude = new BigDecimal("117.703076");
        BigDecimal latitude = new BigDecimal("39.021058");
        AppMemberAddrFourArea addr = appMemberAddrManager.convertFourAreaByLatLng(longitude, latitude);
        System.out.println(JsonUtil.toString(addr, true));
    }
    
    @Test
    public void jdVOPMsgConsumeTask()
    {
        jdVOPMsgManager.consumeMsgTask();
    }
    
    @Test
    public void cancelOrder()
    {
        Long jdOrderId = 348983998177L;
        String cancelReason = "不想要了";
        Boolean res = jdVOPOrderManager.cancelOrder(jdOrderId, null, cancelReason);
        System.out.println(res);
    }
    
    @Test
    public void queryOrderDetail()
    {
        List<Long> jdOrderIds = Lists.newArrayList(331045643412L);
        
        List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> list =
            new ArrayList<>();
        for (Long jdOrderId : jdOrderIds)
        {
            List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> resp =
                jdVOPOrderManager.queryOrderDetail(jdOrderId, null);
            list.addAll(resp);
        }
        System.out.println(JsonUtil.toString(list, true));
    }
    
    @Test
    public void queryDeliveryInfo()
    {
        Long jdOrderId = 349365815790L;
        DeliveryInfoQueryOpenResp resp = jdVOPOrderManager.queryDeliveryInfo(jdOrderId, null);
        System.out
            .println(JsonUtil.toString(JsonUtil.getBean(JsonUtil.toString(resp), JdOrderDeliveryInfo.class), true));
    }
    
    @Test
    public void getGoodsAttributes()
    {
        Long jdOrderId = 349351002985L;
        List<Long> skuIdList = Lists.newArrayList(100095136295L);
        List<SupportedInfoOpenResp> respList = jdVOPAfsManager.getGoodsAttributes(jdOrderId, skuIdList);
        System.out.println(JsonUtil.toString(respList, true));
    }
    
    @Test
    public void createAfsApply()
    {
        ApplyAfterSaleOpenReq apply = new ApplyAfterSaleOpenReq();
        apply.setThirdApplyId("TEST20260421001");
        apply.setIsHasInvoice(false);
        apply.setOrderId(330350366000L);
        
        List<ApplyInfoItemOpenReq> applyInfoItemList = new ArrayList<>();

        ApplyInfoItemOpenReq applyInfoItem = new ApplyInfoItemOpenReq();
        applyInfoItem.setCustomerExpect(10);
        WareDescInfoOpenReq wareDescInfo = new WareDescInfoOpenReq();
        wareDescInfo.setQuestionDesc("测试退货");
        // 问题描述图片，最多2000字符，支持多张图片，用逗号分隔（英文逗号）
        wareDescInfo.setQuestionPic(null);
        applyInfoItem.setWareDescInfoOpenReq(wareDescInfo);
        WareDetailInfoOpenReq wareDetailInfo = new WareDetailInfoOpenReq();
        wareDetailInfo.setWareId(237558L);
        wareDetailInfo.setMainWareId(237558L);
        wareDetailInfo.setWareName("威猛先生（Mr Muscle）管道疏通剂500g 下水道疏通剂 马桶卫生间管道疏通 超市同款");
        wareDetailInfo.setWareNum(1);
        // 商品类型。10主商品，20赠品
        wareDetailInfo.setWareType(10);
        applyInfoItem.setWareDetailInfoOpenReq(wareDetailInfo);
        applyInfoItemList.add(applyInfoItem);

        // 商品2
        ApplyInfoItemOpenReq applyInfoItem2 = new ApplyInfoItemOpenReq();
        applyInfoItem2.setCustomerExpect(10);
        WareDescInfoOpenReq wareDescInfo2 = new WareDescInfoOpenReq();
        wareDescInfo2.setQuestionDesc("测试退货");
        // 问题描述图片，最多2000字符，支持多张图片，用逗号分隔（英文逗号）
        wareDescInfo2.setQuestionPic(null);
        applyInfoItem2.setWareDescInfoOpenReq(wareDescInfo2);
        WareDetailInfoOpenReq wareDetailInfo2 = new WareDetailInfoOpenReq();
        wareDetailInfo2.setWareId(206792L);
        wareDetailInfo2.setMainWareId(206792L);
        wareDetailInfo2.setWareName("沙宣修护水养护发素750g大红瓶深层修护蛋白强韧发丝护发润发乳男女");
        wareDetailInfo2.setWareNum(1);
        // 商品类型。10主商品，20赠品
        wareDetailInfo2.setWareType(10);
        applyInfoItem2.setWareDetailInfoOpenReq(wareDetailInfo2);
        applyInfoItemList.add(applyInfoItem2);

        apply.setApplyInfoItemOpenReqList(applyInfoItemList);
        
        CustomerInfoOpenReq customerInfo = new CustomerInfoOpenReq();
        customerInfo.setCustomerName("天津国成VOP");
        customerInfo.setCustomerMobilePhone("13676774402");
        customerInfo.setCustomerContactName("刘哈哈");
        apply.setCustomerInfoVo(customerInfo);
        
        PickupWareInfoOpenReq pickupWareInfo = new PickupWareInfoOpenReq();
        // 取件方式，4上门取件，7客户送货，40客户发货
        pickupWareInfo.setPickWareType(4);
        // 下面的取件地址，如果是自行寄出（客户发货），默认传订单收货地址
        pickupWareInfo.setPickWareProvince(15);
        pickupWareInfo.setPickWareCity(1233);
        pickupWareInfo.setPickWareCounty(42324);
        pickupWareInfo.setPickWareVillage(59244);
        pickupWareInfo.setPickWareAddress("数安大厦A幢1401");
        // yyyy-MM-dd HH:mm:ss
        pickupWareInfo.setReserveDateBegin("2026-04-21 13:00:00");
        pickupWareInfo.setReserveDateEnd("2026-04-21 14:00:00");
        apply.setPickupWareInfoOpenReq(pickupWareInfo);
        
        ReturnWareInfoOpenReq returnWareInfo = new ReturnWareInfoOpenReq();
        // 返件方式。10自营配送，20第三方配送
        returnWareInfo.setReturnWareType(10);
        // 下面的地址，默认传订单收货地址
        returnWareInfo.setReturnWareProvince(15);
        returnWareInfo.setReturnWareCity(1233);
        returnWareInfo.setReturnWareCountry(42324);
        returnWareInfo.setReturnWareVillage(59244);
        returnWareInfo.setReturnWareAddress("数安大厦A幢1401");
        apply.setReturnWareInfoOpenReq(returnWareInfo);
        
        Boolean res = jdVOPAfsManager.createAfsApply(apply);
        System.out.println(res);
    }
    
    @Test
    public void getAfsOutline()
    {
        Long jdOrderId = 349365585102L;
        String thirdApplyId = "3100100626334275";
        //Long wareId = 5326793L;
        //String thirdApplyId = "1";
        //Long wareId = 1L;
        //String thirdApplyId = null;
        Long wareId = null;
        List<AfsOutLineOpenResp> list = jdVOPAfsManager.getAfsOutline(jdOrderId, thirdApplyId, wareId);
        System.out.println(JsonUtil.toString(list, true));
    }
    
    @Test
    public void queryAfsAddressInfos()
    {
        Long jdOrderId = 330862713589L;
        String thirdApplyId = "tj3151240426150131";
        String customerPin = "天津国成VOP";
        List<AfsAddressInfoOpenResp> list = jdVOPAfsManager.queryAfsAddressInfos(jdOrderId, thirdApplyId, customerPin);
        System.out.println(JsonUtil.toString(list, true));
        try
        {
            for (AfsAddressInfoOpenResp item : list)
            {
                System.out.println("售后收货人：" + jdVOPAfsManager.decodeRsa(item.getAfterServiceReceiver()));
                System.out.println("售后收货人电话：" + jdVOPAfsManager.decodeRsa(item.getAfterServiceTel()));
                System.out.println("售后收货人手机号：" + jdVOPAfsManager.decodeRsa(item.getAfterServicePhone()));
                System.out.println("售后地址描述：" + jdVOPAfsManager.decodeRsa(item.getAfterServiceAddress()));
                System.out.println("省：" + jdAddressDao.getNameById(item.getAfterServiceProvince().longValue()));
                System.out.println("市：" + jdAddressDao.getNameById(item.getAfterServiceCity().longValue()));
                System.out.println("区：" + jdAddressDao.getNameById(item.getAfterServiceCounty().longValue()));
                System.out.println("街道：" + jdAddressDao.getNameById(item.getAfterServiceVillage().longValue()));
                System.out.println();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void updateSendInfo()
    {
        Long jdOrderId = 349263126373L;
        String thirdApplyId = "TEST20260415002";
        List<WaybillInfoVoOpenReq> waybills = new ArrayList<>();
        WaybillInfoVoOpenReq waybill = new WaybillInfoVoOpenReq();
        waybill.setDeliverDate(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        waybill.setWareId(237558L);
        waybill.setWareNum(1);
        // 商品类型。10主商品，20赠品
        waybill.setWareType(10);
        waybill.setExpressCode("SF1566418491691");
        waybill.setExpressCompany("顺丰速运");
        waybill.setFreightMoney(new BigDecimal("12.00"));
        waybills.add(waybill);
        Boolean res = jdVOPAfsManager.updateSendInfo(waybills, jdOrderId, thirdApplyId);
        System.out.println(res);
    }
    
    @Test
    public void queryLogicticsInfo()
    {
        Long jdOrderId = 349263126373L;
        String thirdApplyId = "TEST20260415002";
        List<WayBillInfoOpenResp> list = jdVOPAfsManager.queryLogicticsInfo(jdOrderId, thirdApplyId);
        System.out.println(JsonUtil.toString(list, true));
    }
    
    @Test
    public void cancelAfsApply()
    {
        Long jdOrderId = 349078501571L;
        String thirdApplyId = "3155280426933851";
        String remark = "地址错误";
        Boolean res = jdVOPAfsManager.cancelAfsApply(jdOrderId, thirdApplyId, remark);
        System.out.println(res);
    }
    
    @Autowired
    private JdAppOrderManager jdAppOrderManager;
    @Test
    public void orderSplitTest()
    {
        List<Long> list = new ArrayList<>();
        list.add(349205632544l); 
        list.add(330524711218l);
        list.add(330800952728l);
        list.add(331041244215l);
        list.add(331045644244l);
        list.add(349194901891l);
        jdAppOrderManager.orderSplitTest(331045643412L, list);
    }
    @Test
    public void addOrder()
    {
        long pOrder = 331325001786L;
        JdOrderCorrelation byJdCode = jdOrderCorrelationDao.getByJdCode(pOrder);
        MktOrder order = orderDao.get(byJdCode.getPkey());
        OrderStatus status = order.getStatus();
        Map<Long,List<JdSplitOrderLine>> orderMap = new HashMap<>();
        Map<Long,BigDecimal> freightMap = new HashMap<>();
        freightMap.put(350001895819L, BigDecimal.ZERO);
        freightMap.put(349541042060L, BigDecimal.ZERO);
        JdSplitOrderLine mktOrderLine = orderLineDao.selectOne().eq("pkey", 99278).execDto(JdSplitOrderLine.class);
        orderMap.put(350001895819L, new ArrayList<>());
        mktOrderLine.setJdNum(1);
        orderMap.get(350001895819L).add(mktOrderLine);
        orderMap.put(349541042060L, new ArrayList<>());
        orderMap.get(349541042060L).add(mktOrderLine);
        jdAppOrderManager.addOrder(pOrder, orderMap, order, status, freightMap);
    }
    
    @Test
    public void orderSplitTest2()
    {
        List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> childOrderDetail =
            jdVOPOrderManager.queryOrderDetail(349859628137l, null); 
        log.info("childOrderDetail: {}", JsonUtil.toString(childOrderDetail, true));
    }
}
