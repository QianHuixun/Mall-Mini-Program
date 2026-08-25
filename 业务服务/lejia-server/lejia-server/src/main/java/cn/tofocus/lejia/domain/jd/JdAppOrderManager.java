package cn.tofocus.lejia.domain.jd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.lejia.bean.dto.WeixinConfig;
import cn.tofocus.lejia.bean.dto.app.jd.*;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryDeliveryInfo.DeliveryInfoQueryOpenResp;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryDeliveryInfo.LogisticInfoOrderOpenResp;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.jd.open.api.sdk.domain.vopdd.OperaOrderOpenProvider.request.submitOrder.ConsigneeInfoOrderOpenReq;
import com.jd.open.api.sdk.domain.vopdd.OperaOrderOpenProvider.request.submitOrder.InvoiceInfoOrderOpenReq;
import com.jd.open.api.sdk.domain.vopdd.OperaOrderOpenProvider.request.submitOrder.PaymentInfoOrderOpenReq;
import com.jd.open.api.sdk.domain.vopdd.OperaOrderOpenProvider.request.submitOrder.SkuInfoOrderOpenReq;
import com.jd.open.api.sdk.domain.vopdd.OperaOrderOpenProvider.request.submitOrder.SubmitOrderOpenReq;
import com.jd.open.api.sdk.domain.vopdd.OperaOrderOpenProvider.response.submitOrder.QueryOrderOpenResp;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.SkuInfoOrderOpenResp;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.querySkuFreight.FreightQueryOpenResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkusAllSaleState.GetSkuCanSaleResp;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.config.JdGoodsZoneConfig;
import cn.tofocus.lejia.bean.dto.jd.JdSplitOrderLine;
import cn.tofocus.lejia.bean.dto.market.jd.JdPostageConfigDTO;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdGoodsSpace;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.entity.market.MktGwc;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.market.MktOrderTag;
import cn.tofocus.lejia.bean.entity.market.MktPayLine;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.entity.member.MktTag;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.entity.sys.SysConfigEntity;
import cn.tofocus.lejia.bean.entity.wx.MktGzh;
import cn.tofocus.lejia.bean.entity.zx.ThirdPayLineEntity;
import cn.tofocus.lejia.bean.enums.AddrType;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.jd.OrderCorrelationStatus;
import cn.tofocus.lejia.cache.OrderTokenMap;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.jd.JdGoodsSpaceDao;
import cn.tofocus.lejia.dao.jd.JdOrderCorrelationDao;
import cn.tofocus.lejia.dao.market.MktAddrDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberMsdDao;
import cn.tofocus.lejia.dao.market.MktMemberTagDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.market.MktOrderTagDao;
import cn.tofocus.lejia.dao.market.MktPayLineDao;
import cn.tofocus.lejia.dao.market.MktTagDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.dao.sys.SysDynamicAttributeDao;
import cn.tofocus.lejia.dao.wx.MktGzhAssociateDao;
import cn.tofocus.lejia.dao.wx.MktGzhDao;
import cn.tofocus.lejia.dao.zx.ThirdPayLineDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPAddrManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPGoodsManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPOrderManager;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPAreaInfo;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPSkuNum;
import cn.tofocus.lejia.domain.market.MktMemberMsdManager;
import cn.tofocus.lejia.domain.pay.ChinaUmsPayManager;
import cn.tofocus.lejia.domain.pay.ChinaUmsRefundManager;
import cn.tofocus.lejia.domain.pay.NsPayManager;
import cn.tofocus.lejia.domain.pay.WxPayManager;
import cn.tofocus.lejia.domain.pay.WxRefundManager;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsRefundResponse;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.util.wx.PayJs;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdAppOrderManager
{
    @Autowired
    private JdGoodsDao jdGoodsDao;

    @Autowired
    private JdGoodsSpaceDao jdGoodsSpaceDao;
    
    @Autowired
    private JdOrderCorrelationDao jdOrderCorrelationDao;
    
    @Autowired
    private JdVOPGoodsManager jdVOPGoodsManager;
    
    @Autowired
    private JdVOPOrderManager jdVOPOrderManager;
    
    @Autowired
    private JdVOPAddrManager jdVOPAddrManager;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOrderDescDao orderDescDao;
    
    @Autowired
    private MktGzhDao gzhDao;
    
    @Autowired
    private MktGzhAssociateDao gzhAssociateDao;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private SysConfigDao sysConfigDao;
    
    @Autowired
    private OrderTokenMap orderTokenMap;
    
    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private MktOrderTagDao orderTagDao;
    
    @Autowired
    private MktTagDao tagDao;
    
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MktAddrDao addrDao;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktMemberMsdManager memberMsdManager;
    
    @Autowired
    private SysDynamicAttributeDao dynamicAttributeDao;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;
    
    @Autowired
    private JdGoodsManager jdGoodsManager;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private NsPayManager nsPayManager;
    
    @Autowired
    private WxPayManager wxPayManger;
    
    @Autowired
    private ChinaUmsPayManager chinaUmsPayManager;
    
    @Autowired
    private MktPayLineDao payDao;
    
    @Autowired
    private ThirdPayLineDao thirdPayLineDao;
    
    @Autowired
    private ChinaUmsRefundManager chinaUmsRefundManager;
    
    @Autowired
    private WxRefundManager wxRefundManager;
    
    /**
     * 是否对接第三方餐饮系统的会员
     */
    @Value("${catering.enabled:false}")
    private boolean cateringEnabled;

    @Value("${catering.ascription:22}")
    private Integer cateringAscription;
    
    public JdOrderTotalInfo commitOrder(JdOrderTotalInfo info)
    {
        Long k = System.currentTimeMillis();
        log.info("----------提交订单----------");
        Integer memberPkey = MobileSession.memberPkey();
        Long ll = orderTokenMap.get("order:" + memberPkey);
        if (ll != null && System.currentTimeMillis() - ll.longValue() < 2000)
        {
            orderTokenMap.put("order:" + memberPkey, System.currentTimeMillis());
            throw TofocusException.of(LejiaErrCode.WRONG_TIME);
        }
        orderTokenMap.put("order:" + memberPkey, System.currentTimeMillis());
        System.out.println("jddto.getPayType(): " + info.getPayType());
        checkOrder(info);
        BigDecimal loadMsd = getMsdBalance(memberPkey, MobileSession.appid());
        if(!PayType.ORDER_WEIXIN.equals(info.getPayType()) && !PayType.ORDER_MSD.equals(info.getPayType()) 
            && !PayType.MSD_COMBINATION.equals(info.getPayType()))
        {
            throw TofocusException.of(LejiaErrCode.PAYTYPE_ERROR);
        }
        // 判断热力豆
        if(PayType.ORDER_MSD.equals(info.getPayType()) && loadMsd.compareTo(info.getGoodsSumAmtn()) < 0)
        {
            throw TofocusException.of(LejiaErrCode.NO_MSD);
        }
        String payNumber = numberUtils.createOrderNumber();
        JdGoodsZoneConfig jdGoodsZoneConfig = dynamicAttributeDao.getSysAttribute(JdGoodsZoneConfig.class, MobileSession.appid());
        if (jdGoodsZoneConfig == null)
            jdGoodsZoneConfig = new JdGoodsZoneConfig();
        // 写入订单
        MktOrder order = insAllOrder(info, memberPkey, payNumber, jdGoodsZoneConfig);
//        List<MktOrder> listOrder = insAllOrder(info, memberPkey, payNumber, jdGoodsZoneConfig);
//        MktOrder order = listOrder.get(0);
        if (PayType.ORDER_MSD.equals(info.getPayType()) && loadMsd.compareTo(order.getAmto()) < 0) throw TofocusException.of(LejiaErrCode.NO_MSD);
        info.setOrderPkey(order.getPkey());
        // 检验订单金额小于0 的 默认为0元
        if (info.getGoodsSumAmtn().compareTo(BigDecimal.ZERO) <= 0) info.setGoodsSumAmtn(BigDecimal.ZERO);
        
        // 如果是组合支付 先走微信支付 成功后 再处理京东 京东的处理失败  就调微信退款 直接退款
        if(PayType.MSD_COMBINATION.equals(info.getPayType()))
        {
            if(loadMsd.compareTo(order.getAmtn()) >=0)
            {
                order.setPayType(PayType.ORDER_MSD);
                info.setPayType(PayType.ORDER_MSD);
                orderDao.update(order);
            }
            else
            {
                ionvokePayWx(order, info);
                return info;
            }
        }
        if(PayType.ORDER_WEIXIN.equals(info.getPayType()))
        {
            ionvokePayWx(order, info);
            return info;
        }
        try
        {
            // 京东交互
            jdOrderHandle(order.getPkey(), order.getCode(), info.getAddrPkey());
        }
        catch (Exception e)
        {
            order.setStatus(OrderStatus.VOID_ORDER);
            orderDao.update(order);
            throw e;
        }
        // 京东成功后,内部处理
        payAfterOrder(order);
        log.info("----------订单提交用时: {}----------", System.currentTimeMillis() - k);
        return info;
    }
    
    // 组合支付,支付成功回调回来 调用该接口
    public void msdCombination(MktOrder order)
    {
        try
        {
            // 京东交互
            jdOrderHandle(order);
        }
        catch (Exception e)
        {
            order.setStatus(OrderStatus.VOID_ORDER);
            orderDao.update(order);
            refundWxPay(order);
            // 直接退款
//            Integer ascription = order.getAscription();
//            String orderNumber = order.getCode();
//            orderNumber = orderNumber.substring(0, 14);
//            MktPayLine pl = payDao.getOrderNumber(orderNumber);
//            BigDecimal refund = order.getWeixinAmt().multiply(new BigDecimal("100"));
//            String outRefundNo = numberUtils.createRefundOrderNumber();
//            if (ascription.equals(13))
//            {
//                ThirdPayLineEntity tpl = thirdPayLineDao.byMerOrderId(orderNumber);
//                ChinaUmsRefundResponse chinaUmsRefund = chinaUmsRefundManager.chinaUmsRefund(tpl.getMerOrderId(), outRefundNo, refund);
//                if(chinaUmsRefund == null || Boolean.FALSE.equals(chinaUmsRefund.isSuccess()))
//                {
//                    // 退款失败 打上标签 可重新退款 其他流程显示完成
//                    order.setAgainRefund(true);
//                    orderDao.update(order);
//                    log.error("中信直接退款失败: {}, 订单号: {}, 退款金额: {}", outRefundNo, order.getCode(), order.getWeixinAmt());
////                    throw TofocusException.of(LejiaErrCode.ZX_PAY_REFUND_ERROR, chinaUmsRefund.getErrMsg());
//                }
//            }
//            if(pl != null)
//            {
//                SysAscription asc = ascriptionDao.get(order.getAscription());
//                Boolean refundOrder = wxRefundManager.createRefundOrder(pl.getCode(),
//                    outRefundNo,
//                    refund.longValue(),
//                    Long.valueOf(pl.getAmt()),
//                    asc.getConfigMchid(),
//                    asc.getCertificateSerialNo(),
//                    asc.getConfigLocalpath());
//                
//                if(Boolean.TRUE.equals(refundOrder))
//                {
//                    // 退款失败 打上标签 可重新退款 其他流程显示完成
//                    order.setAgainRefund(true);
//                    orderDao.update(order);
//                }
//            }
            throw e;
        }
        // 京东成功后,内部处理
        payAfterOrder(order);
    }
    
    public void refundWxPay(MktOrder order)
    {
        // 直接退款
        Integer ascription = order.getAscription();
        String orderNumber = order.getCode();
        orderNumber = orderNumber.substring(0, 14);
        MktPayLine pl = payDao.getOrderNumber(orderNumber);
        BigDecimal refund = order.getWeixinAmt().multiply(new BigDecimal("100"));
        String outRefundNo = numberUtils.createRefundOrderNumber();
        if (ascription.equals(13))
        {
            ThirdPayLineEntity tpl = thirdPayLineDao.byMerOrderId(orderNumber);
            ChinaUmsRefundResponse chinaUmsRefund = chinaUmsRefundManager.chinaUmsRefund(tpl.getMerOrderId(), outRefundNo, refund);
            if(chinaUmsRefund == null || Boolean.FALSE.equals(chinaUmsRefund.isSuccess()))
            {
                // 退款失败 打上标签 可重新退款 其他流程显示完成
                order.setAgainRefund(true);
                orderDao.update(order);
                log.error("中信直接退款失败: {}, 订单号: {}, 退款金额: {}", outRefundNo, order.getCode(), order.getWeixinAmt());
            }
        }
        if(pl != null)
        {
            SysAscription asc = ascriptionDao.get(order.getAscription());
            Boolean refundOrder = wxRefundManager.createRefundOrder(pl.getCode(),
                outRefundNo,
                refund.longValue(),
                Long.valueOf(pl.getAmt()),
                asc.getConfigMchid(),
                asc.getCertificateSerialNo(),
                asc.getConfigLocalpath());
            
            if(Boolean.TRUE.equals(refundOrder))
            {
                // 退款失败 打上标签 可重新退款 其他流程显示完成
                order.setAgainRefund(true);
                orderDao.update(order);
            }
            
        }
    }
    
    private void ionvokePayWx(MktOrder order, JdOrderTotalInfo dto)
    {
        if(order.getPayType().equals(PayType.MSD_COMBINATION))
        {
            // 第三方餐饮系统没有退款接口 暂时不支持组合支付
            if (cateringEnabled && cateringAscription.equals(order.getAscription()))
            {
                throw TofocusException.of(LejiaErrCode.CATERING_ERROR);
            }
            if(order != null && order.getOtherAmt() != null && order.getOtherAmt().compareTo(BigDecimal.ZERO) > 0)
                memberMsdManager.updLockMsd(order.getMember(), order.getOtherAmt(), order.getAscription());
        }
        try
        {
            BigDecimal amt = order.getWeixinAmt();
            String payNumber = order.getCode();
            // 第三方支付流水表 用的订单号 不带最后一位标识位
            payNumber = payNumber.substring(0, 14);
            Integer appid = order.getAscription();
            if (appid.equals(1))
            {
                WxPayData payData = nsPayManager.topayIvc(MobileSession.openid(), payNumber, amt);
                dto.setWxPayData(payData);
            }
            else if (appid.equals(13))
            {
                WxPayData payData = chinaUmsPayManager.chinaUmsPay(MobileSession.openid(), payNumber, amt);
                dto.setWxPayData(payData);
            }
            else
            {
                WeixinConfig wxc = ascriptionDao.getWxConfig(appid);
                PayJs payJs = wxPayManger.topayIvc(MobileSession.billIp(), MobileSession.openid(), payNumber, amt, wxc);
                dto.setWxPayData(BeanUtil.beanFrom(WxPayData.class, payJs));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw TofocusException.of(LejiaErrCode.WRONG_WEPAY);
        }
    }
    
    private void jdOrderHandle(MktOrder order)
    {
        SubmitOrderOpenReq so = new SubmitOrderOpenReq();
        so.setSubmitStateType(0);
        so.setThirdOrderId(order.getCode());
        List<SkuInfoOrderOpenReq> skuInfoList = new ArrayList<>();
        List<MktOrderLine> line = orderLineDao.select().in("orderPkey", order.getPkey()).exec();
        for (MktOrderLine ol : line)
        {
            SkuInfoOrderOpenReq skuInfo = new SkuInfoOrderOpenReq();
            skuInfo.setSkuId(ol.getSpace());
            skuInfo.setSkuNum(ol.getNum());
            //            skuInfo.setSkuUnitPrice(ol.getPricen());
            skuInfoList.add(skuInfo);
        }
        so.setSkuInfoList(skuInfoList);
        PaymentInfoOrderOpenReq paymentInfo = new PaymentInfoOrderOpenReq();
        paymentInfo.setPaymentType(4);
        so.setPaymentInfo(paymentInfo);
        MktOrderDesc desc = orderDescDao.get(order.getPkey());
        JdVOPAreaInfo areaInfo = jdVOPAddrManager.convert2AreaInfo(desc.getPro(), desc.getCity(), desc.getArea(), desc.getTown());
        ConsigneeInfoOrderOpenReq consigneeInfo = new ConsigneeInfoOrderOpenReq();
        consigneeInfo.setConsigneeName(desc.getName());
        consigneeInfo.setConsigneeProvinceId(areaInfo.getProvinceId());
        consigneeInfo.setConsigneeCityId(areaInfo.getCityId());
        consigneeInfo.setConsigneeCountyId(areaInfo.getCountyId());
        consigneeInfo.setConsigneeTownId(areaInfo.getTownId());
        consigneeInfo.setConsigneeAddress(desc.getAddr());
        consigneeInfo.setConsigneeMobile(desc.getMobile());
        so.setConsigneeInfo(consigneeInfo);
        
        InvoiceInfoOrderOpenReq invoiceInfo = new InvoiceInfoOrderOpenReq();
        // 发票类型（23:增值税普通发票,24:增值税专用发票） 当发票类型为24时，开票方式只支持2集中开票
        invoiceInfo.setInvoiceType(23);
        invoiceInfo.setInvoicePutType(4);
        invoiceInfo.setInvoicePhone(desc.getMobile());
        so.setInvoiceInfo(invoiceInfo);
        QueryOrderOpenResp qoo = jdVOPOrderManager.submitOrder(so);
        // 根据预占订单返回的数据,更新商品单价
        List<MktOrderLine> listOrder = orderLineDao.listOrder(order.getPkey());
        Map<Long,BigDecimal> lineMap = new HashMap<>();
        qoo.getSkuInfoList().forEach(e -> lineMap.put(e.getSkuId(), e.getSkuPrice()));
        for(MktOrderLine ol : listOrder)
        {
            if(lineMap.containsKey(ol.getSpace()))
            {
                ol.setPrice(lineMap.get(ol.getSpace()));
//                ol.setCouponAmt(ol.getPricen().multiply(new BigDecimal(ol.getNum()).setScale(2, BigDecimal.ROUND_HALF_UP)));
            }
        }
        orderLineDao.updateAll(listOrder);
        // 更新订单京东支付金额
        if(qoo.getOrderPaymentInfo() != null && qoo.getOrderPaymentInfo().getPaymentDetailList() != null
            && !qoo.getOrderPaymentInfo().getPaymentDetailList().isEmpty() && order != null)
        {
            order.setPayDetailMoney(qoo.getOrderPaymentInfo().getPaymentDetailList().get(0).getPayDetailMoney());
            orderDao.update(order);
        }
        
        JdOrderCorrelation joc = new JdOrderCorrelation();
        joc.setPkey(order.getPkey());
        joc.setOrderCode(qoo.getThirdOrderId());
        joc.setJdCode(qoo.getJdOrderId());
        joc.setStatus(OrderCorrelationStatus.NORMAL_ORDER);
        jdOrderCorrelationDao.add(joc);
        jdVOPOrderManager.confirmOrder(joc.getJdCode(), joc.getOrderCode());
    }
    
    private void jdOrderHandle(Integer orderPkey, String code, Integer addrPkey)
    {
        SubmitOrderOpenReq so = new SubmitOrderOpenReq();
        so.setSubmitStateType(0);
        so.setThirdOrderId(code);
        List<SkuInfoOrderOpenReq> skuInfoList = new ArrayList<>();
        List<MktOrderLine> line = orderLineDao.select().in("orderPkey", orderPkey).exec();
        for (MktOrderLine ol : line)
        {
            SkuInfoOrderOpenReq skuInfo = new SkuInfoOrderOpenReq();
            skuInfo.setSkuId(ol.getSpace());
            skuInfo.setSkuNum(ol.getNum());
            //            skuInfo.setSkuUnitPrice(ol.getPricen());
            skuInfoList.add(skuInfo);
        }
        so.setSkuInfoList(skuInfoList);
        PaymentInfoOrderOpenReq paymentInfo = new PaymentInfoOrderOpenReq();
        paymentInfo.setPaymentType(4);
        so.setPaymentInfo(paymentInfo);
        
        MktAddr addrObj = addrDao.get(addrPkey);
        JdVOPAreaInfo areaInfo = jdVOPAddrManager.convert2AreaInfo(addrObj);
        ConsigneeInfoOrderOpenReq consigneeInfo = new ConsigneeInfoOrderOpenReq();
        consigneeInfo.setConsigneeName(addrObj.getName());
        consigneeInfo.setConsigneeProvinceId(areaInfo.getProvinceId());
        consigneeInfo.setConsigneeCityId(areaInfo.getCityId());
        consigneeInfo.setConsigneeCountyId(areaInfo.getCountyId());
        consigneeInfo.setConsigneeTownId(areaInfo.getTownId());
        consigneeInfo.setConsigneeAddress(addrObj.getAddr());
        consigneeInfo.setConsigneeMobile(addrObj.getMobile());
        so.setConsigneeInfo(consigneeInfo);
        
        InvoiceInfoOrderOpenReq invoiceInfo = new InvoiceInfoOrderOpenReq();
        // 发票类型（23:增值税普通发票,24:增值税专用发票） 当发票类型为24时，开票方式只支持2集中开票
        invoiceInfo.setInvoiceType(23);
        invoiceInfo.setInvoicePutType(4);
        invoiceInfo.setInvoicePhone(addrObj.getMobile());
        so.setInvoiceInfo(invoiceInfo);
        QueryOrderOpenResp qoo = jdVOPOrderManager.submitOrder(so);
        // 根据预占订单返回的数据,更新商品单价
        List<MktOrderLine> listOrder = orderLineDao.listOrder(orderPkey);
        Map<Long,BigDecimal> lineMap = new HashMap<>();
        qoo.getSkuInfoList().forEach(e -> lineMap.put(e.getSkuId(), e.getSkuPrice()));
        for(MktOrderLine ol : listOrder)
        {
            if(lineMap.containsKey(ol.getSpace()))
            {
                ol.setPrice(lineMap.get(ol.getSpace()));
//                ol.setCouponAmt(ol.getPricen().multiply(new BigDecimal(ol.getNum()).setScale(2, BigDecimal.ROUND_HALF_UP)));
            }
        }
        orderLineDao.updateAll(listOrder);
        // 更新订单京东支付金额
        MktOrder order = orderDao.get(orderPkey);
        if(qoo.getOrderPaymentInfo() != null && qoo.getOrderPaymentInfo().getPaymentDetailList() != null
            && !qoo.getOrderPaymentInfo().getPaymentDetailList().isEmpty() && order != null)
        {
            order.setPayDetailMoney(qoo.getOrderPaymentInfo().getPaymentDetailList().get(0).getPayDetailMoney());
            orderDao.update(order);
        }
        
        JdOrderCorrelation joc = new JdOrderCorrelation();
        joc.setPkey(orderPkey);
        joc.setOrderCode(qoo.getThirdOrderId());
        joc.setJdCode(qoo.getJdOrderId());
        joc.setStatus(OrderCorrelationStatus.NORMAL_ORDER);
        jdOrderCorrelationDao.add(joc);
        jdVOPOrderManager.confirmOrder(joc.getJdCode(), joc.getOrderCode());
    }
    
    private void payAfterOrder(MktOrder order)
    {
        order.setStatus(OrderStatus.PAYING_ORDER);
        order.setPurchaseStatus(PurchaseStatus.AWAIT_PURCHASE);
        orderDao.update(order);
        List<MktOrderLine> line = orderLineDao.select().in("orderPkey", order.getPkey()).exec();
        for (MktOrderLine ol : line)
        {
            ol.setStatus(order.getStatus());
            // 商品销量+1
            jdGoodsDao.increaseXsNum(ol.getGoods(), ol.getNum());
        }
        orderLineDao.updateAll(line);
        Boolean jdOrder = OrderType.INTEGRAL_JD_ORDER.equals(order.getOrderType());
        BigDecimal amtnMsd = order.getAmtn();
        if(PayType.MSD_COMBINATION.equals(order.getPayType()))
        {
            amtnMsd = order.getOtherAmt();
            memberMsdManager.updMsd(order.getMember(), amtnMsd, order.getCode(), order.getAscription());
        }
        if(PayType.ORDER_MSD.equals(order.getPayType()))
        {
            memberMsdManager.updMsdBalance(order.getMember(),
                null,
                false,
                amtnMsd,
                MsdOperationType.CONSUME,
                order.getCode(),
                order.getCode(),
                order.getAscription(),
                jdOrder);
        }
        // 发送消息给对应市场
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                assembleAndSendWx(order);
            }
        }).start();
        // 更新会员的消费记录
        if (order.getMember() != null)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    memberDao.updLastConsume(order.getMember(), order.getCreatedTime(), order.getFarmer());
                }
            }).start();
        }
    }
    
    
    // 校验地址和备注
    private void checkOrder(JdOrderTotalInfo dto)
    {
        for (JdOrderInfo oi : dto.getInfos())
        {
            // 备注字数限制
            if (StringUtils.isNotBlank(oi.getRemark()) && oi.getRemark().length() > 50)
                throw TofocusException.of(LejiaErrCode.EXCEED_THE_LIMIT);
        }
        if (dto.getAddrPkey() == null)
        {
            throw TofocusException.of(LejiaErrCode.DELIVERY_ADDR_ERROR);
        }
    }
    
    private BigDecimal getMsdBalance(Integer memberPkey, Integer ascription)
    {
        BigDecimal balance = BigDecimal.ZERO;
        MktMemberMsd memberMsd = memberMsdDao.get(memberPkey);
        if (memberMsd != null)
        {
            balance = memberMsd.getBalance();
        }
        return balance;
    }
    
    // List<
    private MktOrder insAllOrder(JdOrderTotalInfo dto, Integer memberPkey, String orderNumber,
        JdGoodsZoneConfig jdGoodsZoneConfig)
    {
//        List<MktOrder> orderList = new ArrayList<>();
        List<JdVOPSkuNum> skuNumInfoList = new ArrayList<>();
        List<Long> skuList = new ArrayList<>();
        for (JdOrderInfo oi : dto.getInfos())
        {
            for (JdGoodsOnList line : oi.getGoodsList())
            {
                JdVOPSkuNum jdVOPSkuNum = new JdVOPSkuNum();
                jdVOPSkuNum.setSkuId(line.getSpace());
                jdVOPSkuNum.setSkuNum(line.getNum());
                skuNumInfoList.add(jdVOPSkuNum);
                skuList.add(line.getSpace());
            }
        }
        Integer addrPkey = dto.getAddrPkey();
        MktAddr addrObj = addrDao.get(addrPkey);
        JdVOPAreaInfo areaInfo = jdVOPAddrManager.convert2AreaInfo(addrObj);
        List<GetSkuCanSaleResp> skusAllSaleState = jdVOPGoodsManager.getSkusAllSaleState(skuNumInfoList, areaInfo);
        // 检查京东商品是否可采(包含是否在商品池、是否主站上架状态、是否预约预售、是否合同支持购买此商品、是否区域限售)
        for (GetSkuCanSaleResp gscsr : skusAllSaleState)
        {
            if (Boolean.FALSE.equals(gscsr.getCanPurchase()))
            {
                Long skuId = gscsr.getSkuId();
                JdGoods jdGoods = jdGoodsDao.get(skuId);
                throw TofocusException.of(LejiaErrCode.GOODS_ERROR, gscsr.getMessage() + " 商品名称: " + jdGoods.getTitle());
            }
        }
        // 检查商品是否下架(商城)
        List<JdGoods> list = jdGoodsDao.byPkey(skuList);
        Map<Long, JdGoods> map = new HashMap<>();
        for (JdGoods g : list)
        {
            if (Boolean.FALSE.equals(g.getEnabled())) throw TofocusException.of(LejiaErrCode.GOODS_DISABLED);
            map.put(g.getPkey(), g);
        }
        // 获取京东运费 目前和热力豆 1:1
        FreightQueryOpenResp querySkuFreight = jdVOPOrderManager.querySkuFreight(skuNumInfoList, areaInfo, 4);
        BigDecimal totalFreight = querySkuFreight.getTotalFreight();
        //        OrderV3Info oi = dto.getInfos().get(0);
        MktOrder order = new MktOrder();
        order.setMember(memberPkey);
        order.setStatus(OrderStatus.UNPAID_ORDER);
        order.setSettlementType(SettlementType.NOT_START);
        
        order.setOrderOir(OrderOir.POINTS_MALL);
        order.setOrderType(OrderType.INTEGRAL_JD_ORDER);
        order.setCode(orderNumber + "1");
        orderDao.generateID(order);
        order.setCgCheck(0);
        order.setPayType(dto.getPayType());
        order.setFarmer(Constant.Operation + MobileSession.appid());
        order.setCompany(Constant.Operation + MobileSession.appid());
        order.setAscription(MobileSession.appid());
        order.setDistributionType(DistributionType.IMMEDIATELY);
        order.setIsBox(false);
        if (Boolean.TRUE.equals(jdGoodsZoneConfig.getIsConsumerPostage()))
            order.setPostage(totalFreight);
        else
            order.setPostage(BigDecimal.ZERO);
        order.setOldPostage(totalFreight);
        order.setCardPostageAmt(BigDecimal.ZERO);
        
        BigDecimal amto = BigDecimal.ZERO;
        int pointn = 0;
        BigDecimal weight = BigDecimal.ZERO;
        List<Integer> gwcIds = new ArrayList<>();
        List<MktOrderLine> addOrderlines = new ArrayList<>();
        JdOrderInfo oi = dto.getInfos().get(0);
        for (JdGoodsOnList line : oi.getGoodsList())
        {
            JdGoods jg = map.get(line.getSpace());
            MktOrderLine orderLine = new MktOrderLine();
            orderLine.setStatus(order.getStatus());
            orderLine.setOrderPkey(order.getPkey());
            // 京东商品 skuid对应订单明细表space  spuid对应订单明细表goods
            orderLine.setGoods(jg.getSpuId());
            orderLine.setSpace(line.getSpace());
            JdGoodsSpace jgs = jdGoodsSpaceDao.get(jg.getPkey());
            if(jgs != null)
                orderLine.setSpaceName(jgs.getSpaceName());
            orderLine.setGoodsName(jg.getTitle());
            orderLine.setAscription(order.getAscription());
            orderLine.setPoint(0);
            String regex = "^-?\\d+(\\.\\d+)?$";
            if (StringUtils.isNotBlank(jg.getWeight()) && jg.getWeight().matches(regex))
            {
                orderLine.setWeight(new BigDecimal(jg.getWeight()).multiply(new BigDecimal(line.getNum())));
                weight = weight.add(orderLine.getWeight());
            }
            orderLine.setPrice(jg.getSalePrice());
            orderLine.setPricen(jg.getPrice());
            orderLine.setNum(line.getNum());
            BigDecimal num = new BigDecimal(orderLine.getNum());
            orderLine.setFarmer(order.getFarmer());
            orderLine.setCompany(order.getCompany());
            orderLine.setCouponPrice(orderLine.getPricen());
            orderLine.setCouponAmt(orderLine.getPricen().multiply(num));
            orderLine.setGtype(jg.getCategory().intValue());
            amto = amto.add(orderLine.getPricen().multiply(num)).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (line.getGwcPkey() != null) gwcIds.add(line.getGwcPkey());
            addOrderlines.add(orderLine);
        }
        order.setReducePrice(BigDecimal.ZERO);
        order.setWeight(weight);
        order.setAmto(amto);
        order.setPointn(pointn);
        order.setCommissionType(CommissionType.BLOC);
        MktOrderDesc desc = new MktOrderDesc();
        desc.setPkey(order.getPkey());
        desc.setAscription(order.getAscription());
        desc.setAddr(addrObj.getAddr());
        desc.setName(addrObj.getName());
        desc.setMobile(addrObj.getMobile());
        desc.setLatitude(addrObj.getLatitude());
        desc.setLongitude(addrObj.getLongitude());
        desc.setPro(addrObj.getPro());
        desc.setCity(addrObj.getCity());
        desc.setArea(addrObj.getArea());
        desc.setTown(addrObj.getTown());
        desc.setRemark(oi.getRemark());
        desc.setDistance(BigDecimal.ZERO);
        orderDescDao.add(desc);
        if (Boolean.TRUE.equals(jdGoodsZoneConfig.getIsConsumerPostage()))
            order.setAmtall(order.getAmto().add(order.getOldPostage()));
        else
            order.setAmtall(order.getAmto());
        order.setCommn(BigDecimal.ZERO);
        order.setCard(null);
        order.setCutAmt(BigDecimal.ZERO);
        order.setCardAmt(BigDecimal.ZERO);
        order.setAmtn(order.getAmto());
        if (order.getAmtn().compareTo(BigDecimal.ZERO) <= 0) order.setAmtn(BigDecimal.ZERO);
        order.setAmtn(order.getAmtn().add(order.getPostage()));
        
        order.setWeixinAmt(BigDecimal.ZERO);
        order.setOtherAmt(order.getAmtn());
        //        for (OrderGoodsV3OnList line : oi.getGoodsList())
        //        {
        //            // TODO 处理一下本地商品库存 或者不处理
        //        }
        if(order.getPayType().equals(PayType.ORDER_WEIXIN))
        {
            order.setWeixinAmt(order.getAmtn());
            order.setOtherAmt(BigDecimal.ZERO);
        }
        if(order.getPayType().equals(PayType.MSD_COMBINATION))
        {
            BigDecimal loadMsd = getMsdBalance(order.getMember(), order.getAscription());
            if(loadMsd.compareTo(order.getAmtn()) >= 0)
            {
                order.setPayType(PayType.ORDER_MSD);
                order.setWeixinAmt(BigDecimal.ZERO);
                order.setOtherAmt(order.getAmtn());
            }
            else
            {
                order.setWeixinAmt(order.getAmtn().subtract(loadMsd));
                order.setOtherAmt(loadMsd);
            }
        }
        order = orderDao.add(order);
        List<Integer> listTag = memberTagDao.listTag(order.getMember(), order.getAscription());
        if (listTag != null && !listTag.isEmpty())
        {
            List<MktOrderTag> otl = new ArrayList<>();
            for (Integer a : listTag)
            {
                MktOrderTag ot = new MktOrderTag();
                ot.setAscription(order.getAscription());
                ot.setOrderPkey(order.getPkey());
                ot.setTag(a);
                MktTag mktTag = tagDao.get(a);
                if (mktTag != null) ot.setTagName(mktTag.getName());
                otl.add(ot);
            }
            orderTagDao.addAll(otl);
        }
        orderLineDao.addAll(addOrderlines);
        if (!gwcIds.isEmpty())
        {
            gwcDao.removeAllById(gwcIds);
        }
//        orderList.add(order);
        dto.setGoodsSumAmtn(BigDecimal.ZERO);
        dto.setSumPointn(0);
        dto.setSumPostage(BigDecimal.ZERO);
        dto.setGoodsSumAmtn(order.getAmtn());
        dto.setSumPointn(0);
        dto.setSumPostage(order.getPostage());
        return order;
//        return orderList;
    }
    
    private void assembleAndSendWx(MktOrder order)
    {
        Integer ascription = order.getAscription();
        SysConfigEntity sysConfig;
        MktOrderLine ol = orderLineDao.selectOne().eq("orderPkey", order.getPkey()).exec();
        MktOrderDesc desc = orderDescDao.get(order.getPkey());
        List<Integer> list = gzhAssociateDao.listTrueAssKeys(order.getFarmer());
        if (list.isEmpty()) return;
        JSONObject data = new JSONObject();
        if (DistributionType.IMMEDIATELY.equals(order.getDistributionType()))
        {
            sysConfig = sysConfigDao.getBean(Constant.SysConfig.TEMPLATE_MARKET, ascription);
            if (sysConfig == null) return;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", "订单号: " + order.getCode());
            data.put("first", jsonObject);
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("value", ol.getGoodsName());
            data.put("keyword1", jsonObject2);
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("value", desc.getName());
            data.put("keyword2", jsonObject3);
            
            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("value", desc.getMobile());
            data.put("keyword3", jsonObject5);
            
            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("value", desc.getAddr());
            data.put("keyword4", jsonObject6);
            
            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("value", order.getPstime());
            data.put("keyword5", jsonObject7);
            
            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("value", "请及时处理");
            data.put("remark", jsonObject4);
        }
        else
        {
            sysConfig = sysConfigDao.getBean(Constant.SysConfig.TEMPLATE_MARKET_PICK, ascription);
            if (sysConfig == null) return;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", "您有新的自提订单");
            data.put("first", jsonObject);
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("value", order.getCode());
            data.put("keyword1", jsonObject2);
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("value", ol.getGoodsName());
            data.put("keyword2", jsonObject3);
            
            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("value", desc.getName());
            data.put("keyword3", jsonObject5);
            
            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("value", desc.getMobile());
            data.put("keyword4", jsonObject6);
            
            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("value", DateUtil.formatDate(order.getCreatedTime()));
            data.put("keyword5", jsonObject7);
            
            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("value", "请及时处理");
            data.put("remark", jsonObject4);
            
        }
        List<MktGzh> gzh = gzhDao.listGzh(list);
        gzh = gzh.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getOpenid()))),
                ArrayList::new));
        for (MktGzh g : gzh)
        {
            Boolean msg = wxManager.wechatSendMsgYs(sysConfig.getValue(), g.getOpenid(), null, data, ascription);
            log.info("市场订单发送给市场管理人员微信公众号: {}", msg);
        }
    }
    
    public JdOrderTotalInfo buyGoods(Long space, Integer num, Integer addressPkey)
    {
        List<MktGwc> gwcs = new ArrayList<>();
        MktGwc g = new MktGwc();
        g.setMember(MobileSession.memberPkey());
        g.setSkuId(space);
        g.setNum(num);
        gwcs.add(g);
        return assemblyInfo(gwcs, addressPkey);
    }
    
    public JdOrderTotalInfo buyGwc(List<Integer> gwcList, Integer addressPkey)
    {
        if (gwcList.isEmpty()) throw TofocusException.of(LejiaErrCode.ORDER_NULL);
        List<MktGwc> gwcs = gwcDao.listGwc(gwcList);
        return assemblyInfo(gwcs, addressPkey);
    }
    
    public JdOrderTotalInfo assemblyInfo(List<MktGwc> gwcs, Integer addressPkey)
    {
        MktMember mktMember = MobileSession.member();
        Integer appid = MobileSession.appid();
        JdOrderTotalInfo totalInfo = new JdOrderTotalInfo();
        BigDecimal amto = BigDecimal.ZERO;
        totalInfo.setOrderType(OrderType.INTEGRAL_JD_ORDER);
        totalInfo.setPayType(PayType.ORDER_MSD);
        // 获取民生豆的余额
        totalInfo.setMyMsd(getMsdBalance(mktMember.getPkey(), appid));
        List<JdOrderInfo> infos = new ArrayList<>();
        JdOrderInfo info = new JdOrderInfo();
        List<JdGoodsOnList> goodsList = new ArrayList<>();
        List<JdVOPSkuNum> skuNumInfoList = new ArrayList<>();
        List<String> goodsPhotos = new ArrayList<>();
        for (MktGwc gwc : gwcs)
        {
            JdGoods jg = jdGoodsDao.get(gwc.getSkuId());
            if(jg.getLowestBuy() != null && gwc.getNum() < jg.getLowestBuy())
                throw TofocusException.of(LejiaErrCode.JD_GOODS_LOWEST_BUY_ERROR);
            JdGoodsOnList bean = new JdGoodsOnList();
            bean.setSpace(jg.getPkey());
            bean.setNum(gwc.getNum());
            bean.setGwcPkey(gwc.getPkey());
            
            bean.setGoodsName(jg.getTitle());
            JdGoodsSpace jgs = jdGoodsSpaceDao.get(jg.getPkey());
            if(jgs != null)
                bean.setSpaceName(jgs.getSpaceName());
            bean.setPrice(jg.getPrice());
            amto = amto.add(bean.getPrice().multiply(BigDecimal.valueOf(bean.getNum())));
            String photo = new String();
            if(jg.getPhoto1() != null && !jg.getPhoto1().isEmpty())
                photo = jg.getPhoto1().get(0);
            goodsPhotos.add(photo);
            bean.setPhoto(photo);
            List<JdGoodsSpaceOnInfo> spaceList = new ArrayList<>();
            JdGoodsSpaceOnInfo gs = new JdGoodsSpaceOnInfo();
            gs.setSpace(bean.getSpace());
            gs.setNum(bean.getNum());
            gs.setGwcPkey(bean.getGwcPkey());
            gs.setSpaceName(bean.getSpaceName());
            gs.setPrice(bean.getPrice());
            gs.setPhoto(bean.getPhoto());
            spaceList.add(gs);
            bean.setSpaceList(spaceList);
            goodsList.add(bean);
            
            JdVOPSkuNum jdVOPSkuNum = new JdVOPSkuNum();
            jdVOPSkuNum.setSkuId(jg.getPkey());
            jdVOPSkuNum.setSkuNum(gwc.getNum());
            skuNumInfoList.add(jdVOPSkuNum);
        }
        MktAppAddrDTO loadAddr = loadAddr(mktMember.getPkey(), addressPkey);
        BigDecimal totalFreight = BigDecimal.ZERO;
        JdGoodsZoneConfig jdGoodsZoneConfig = dynamicAttributeDao.getSysAttribute(JdGoodsZoneConfig.class, appid);
        if (jdGoodsZoneConfig == null)
            jdGoodsZoneConfig = new JdGoodsZoneConfig();
        if (loadAddr != null)
        {
            totalInfo.setAddrPkey(loadAddr.getPkey());
            totalInfo.setAddr(loadAddr.getAddr());
            totalInfo.setAddrDetail(loadAddr.getAddrDetail());
            totalInfo.setPro(loadAddr.getPro());
            totalInfo.setCity(loadAddr.getCity());
            totalInfo.setName(loadAddr.getName());
            totalInfo.setMobile(loadAddr.getMobile());

            if (Boolean.TRUE.equals(jdGoodsZoneConfig.getIsConsumerPostage()))
            {
                MktAddr addrObj = addrDao.get(loadAddr.getPkey());
                try
                {
                    JdVOPAreaInfo areaInfo = jdVOPAddrManager.convert2AreaInfo(addrObj);
                    FreightQueryOpenResp querySkuFreight =
                        jdVOPOrderManager.querySkuFreight(skuNumInfoList, areaInfo, 4);
                    totalFreight = querySkuFreight.getTotalFreight();
                    info.setPostage(totalFreight);
                }
                catch (Exception e)
                {
                }
            }
        }
        info.setGoodsList(goodsList);
        info.setSales(amto);
        info.setGoodsPhotos(goodsPhotos);
        info.setSupplierName(jdGoodsZoneConfig.getJdGoodsName());
        infos.add(info);
        totalInfo.setInfos(infos);
        
        totalInfo.setGoodsSumAmto(amto);
        totalInfo.setSumPointn(0);
        totalInfo.setSumPostage(totalFreight);
        totalInfo.setGoodsSumAmtn(amto.add(totalFreight));
        totalInfo.setMsdPay(true);
        return totalInfo;
    }
    
    private MktAppAddrDTO loadAddr(Integer member, Integer addressPkey)
    {
        MktAddr addr = null;
        if (addressPkey != null)
        {
            //指定地址
            addr = addrDao.selectOne()
                .eq("member", member)
                .eq("type", AddrType.DELIVERY)
                .eq("pkey", addressPkey).exec();
        }
        else
        {
            //默认地址
            addr = addrDao.selectOne()
                .eq("member", member).eq("type", AddrType.DELIVERY).eq("defaultAddr", true).exec();
            if(addr == null)
            {
                addr = addrDao.selectOne()
                    .eq("member", member).eq("type", AddrType.DELIVERY).exec();
            }
        }
        if (addr == null)
        {
            return null;
        }
        MktAppAddrDTO dto = new MktAppAddrDTO();
        BeanUtils.copyProperties(addr, dto);
        dto.setAddrDetail(addr.getAddr());
        dto.setEnabled(true);
        return dto;
    }
    
    
    
    // 订单拆掉处理
    public void orderSplit(long pOrder)
    {
        // 京东关联表 设置成 作废
        JdOrderCorrelation byJdCode = jdOrderCorrelationDao.getByJdCode(pOrder);
        // 订单表 设置成 作废
        MktOrder order = orderDao.get(byJdCode.getPkey());
        OrderStatus status = order.getStatus();
        // 查询明细表
        List<MktOrderLine> orderLineList = orderLineDao.listOrder(order.getPkey());
        // 20260611 已经拆过单 第二次就没有数据  需要重新查询
        if(orderLineList == null || orderLineList.isEmpty())
        {
            List<JdOrderCorrelation> byParentOrder = jdOrderCorrelationDao.byParentOrder(pOrder);
            List<Integer> okl = new ArrayList<>();
            MktOrder mktOrder = null;
            for(JdOrderCorrelation joc : byParentOrder)
            {
                okl.add(joc.getPkey());
                if(mktOrder == null)
                    mktOrder = orderDao.get(joc.getPkey());
            }
            if(!okl.isEmpty())
                orderLineList = orderLineDao.listOrders(okl);
            status = mktOrder.getStatus();
        }
        Map<Long,JdSplitOrderLine> map = new HashMap<>();
        orderLineList.forEach(e -> map.put(e.getSpace(), BeanUtil.beanFrom(JdSplitOrderLine.class, e)));
        
        // 查询京东订单详情 获取原始订单和拆单后子订单详情
        List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> queryOrderDetail =
            jdVOPOrderManager.queryOrderDetail(pOrder, null);
        Map<Long,List<JdSplitOrderLine>> orderMap = new HashMap<>();
        Map<Long,BigDecimal> freightMap = new HashMap<>();
        boolean flag = false;
        // 对原来的订单数据 根据京东订单拆分情况 进行拆分
        for(com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp qoo : queryOrderDetail)
        {
            log.info("qoo: {}", JsonUtil.toString(qoo, true));
            List<Long> list = qoo.getChildJdOrderIdList();
            if(list != null && !list.isEmpty())
            {
                List<JdOrderCorrelation> byParentOrder = jdOrderCorrelationDao.byParentOrder(pOrder);
                if(byParentOrder != null && !byParentOrder.isEmpty())
                {
                    List<Long> oldList = new ArrayList<>();
                    for(JdOrderCorrelation joc : byParentOrder)
                    {
                        oldList.add(joc.getJdCode());
                    }
                    if(!oldList.containsAll(list))
                        flag = true;
                }
                else
                {
                    // 处理数据,组合map
                    flag = true;
                }
                if(flag)    
                    assemblyMap(list, map, orderMap, freightMap);
            }
        }
        if(flag)
        {
            // 创建新的订单和订单明细
            addOrder(pOrder, orderMap, order, status, freightMap);
            byJdCode.setStatus(OrderCorrelationStatus.REVOKE_ORDER);
            jdOrderCorrelationDao.update(byJdCode);
            order.setStatus(OrderStatus.VOID_ORDER);
            orderDao.update(order);
        }
        else
        {
            log.info("无子订单,父类订单号: {}", pOrder);
        }
    }
    
    // 订单拆掉处理
    public void orderSplitTest(long pOrder, List<Long> list)
    {
        // 京东关联表 设置成 作废
        JdOrderCorrelation byJdCode = jdOrderCorrelationDao.getByJdCode(pOrder);
        // 订单表 设置成 作废
        MktOrder order = orderDao.get(byJdCode.getPkey());
        OrderStatus status = order.getStatus();
        // 查询明细表
        List<MktOrderLine> orderLineList = orderLineDao.listOrder(order.getPkey());
        // 20260611 已经拆过单 第二次就没有数据  需要重新查询
        if(orderLineList == null || orderLineList.isEmpty())
        {
            List<JdOrderCorrelation> byParentOrder = jdOrderCorrelationDao.byParentOrder(pOrder);
            List<Integer> okl = new ArrayList<>();
            MktOrder mktOrder = null;
            for(JdOrderCorrelation joc : byParentOrder)
            {
                okl.add(joc.getPkey());
                if(mktOrder == null)
                    mktOrder = orderDao.get(joc.getPkey());
            }
            if(!okl.isEmpty())
                orderLineList = orderLineDao.listOrders(okl);
            status = mktOrder.getStatus();
        }
        Map<Long,JdSplitOrderLine> map = new HashMap<>();
        orderLineList.forEach(e -> map.put(e.getSpace(), BeanUtil.beanFrom(JdSplitOrderLine.class, e)));
        
        Map<Long,List<JdSplitOrderLine>> orderMap = new HashMap<>();
        Map<Long,BigDecimal> freightMap = new HashMap<>();
        boolean flag = false;
        // 对原来的订单数据 根据京东订单拆分情况 进行拆分
       
        if(list != null && !list.isEmpty())
        {
            List<JdOrderCorrelation> byParentOrder = jdOrderCorrelationDao.byParentOrder(pOrder);
            if(byParentOrder != null && !byParentOrder.isEmpty())
            {
                List<Long> oldList = new ArrayList<>();
                for(JdOrderCorrelation joc : byParentOrder)
                {
                    oldList.add(joc.getJdCode());
                }
                if(!oldList.containsAll(list))
                    flag = true;
            }
            else
            {
                // 处理数据,组合map
                flag = true;
            }
            if(flag)    
                assemblyMap(list, map, orderMap, freightMap);
        }
        
        if(flag)
        {
            System.out.println("orderMap: " + JsonUtil.toString(orderMap, true));
            // 创建新的订单和订单明细
            addOrder(pOrder, orderMap, order, status, freightMap);
            byJdCode.setStatus(OrderCorrelationStatus.REVOKE_ORDER);
            jdOrderCorrelationDao.update(byJdCode);
            order.setStatus(OrderStatus.VOID_ORDER);
            orderDao.update(order);
        }
        else
        {
            log.info("无子订单,父类订单号: {}", pOrder);
        }
    }
    
    public void orderSplitTest(Long jdCode)
    {
//        JdOrderCorrelation byJdCode = jdOrderCorrelationDao.getByJdCode(jdCode);
        List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> queryOrderDetail =
            jdVOPOrderManager.queryOrderDetail(jdCode, null);
        System.out.println("拆单结果查询: " + JsonUtil.toString(queryOrderDetail, true));
    }
    
    private void assemblyMap(List<Long> list, Map<Long,JdSplitOrderLine> map, Map<Long,List<JdSplitOrderLine>> orderMap, Map<Long,BigDecimal> freightMap)
    {
        for(Long key : list)
        {
            List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> childOrderDetail =
                jdVOPOrderManager.queryOrderDetail(key, null);
            log.info("childOrderDetail: {}", JsonUtil.toString(childOrderDetail, true));
            for(com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp cqoo : childOrderDetail)
            {
                List<SkuInfoOrderOpenResp> skuInfoList = cqoo.getSkuInfoList();
                BigDecimal orderTotalFreight = cqoo.getOrderPrice().getOrderTotalFreight();
                freightMap.put(key, orderTotalFreight);
                for(SkuInfoOrderOpenResp sioo : skuInfoList)
                {
                    long skuId = sioo.getSkuId();
                    if(map.containsKey(skuId))
                    {
                        JdSplitOrderLine mktOrderLine = map.get(skuId);
                        if(!orderMap.containsKey(key))
                        {
                            orderMap.put(key, new ArrayList<>());
                        }
                        if(sioo.getSkuNum() != mktOrderLine.getNum().intValue())
                            mktOrderLine.setJdNum(sioo.getSkuNum());
                        orderMap.get(key).add(mktOrderLine);
                    }
                }
            }
        }
    }
    
    public void addOrder(Long pOrder, Map<Long,List<JdSplitOrderLine>> orderMap, MktOrder order, 
        OrderStatus status, Map<Long,BigDecimal> freightMap)
    {
        List<JdOrderCorrelation> byParentOrder = jdOrderCorrelationDao.byParentOrder(pOrder);
        List<JdOrderCorrelation> addList = new ArrayList<>();
        Map<Long,JdOrderCorrelation> exitMap = new HashMap<>();
        if(byParentOrder != null && !byParentOrder.isEmpty())
        {
            for(JdOrderCorrelation joc : byParentOrder)
            {
                exitMap.put(joc.getJdCode(), joc);
            }
        }
        JdPostageConfigDTO jdPostageConfig = jdGoodsManager.getJdPostageConfig();
        MktOrderDesc orderDesc = orderDescDao.get(order.getPkey());
        List<MktOrderTag> listOrderTag = orderTagDao.listOrderTag(order.getPkey());
        MktOrderRefund oldOr = orderRefundDao.orderPkeyJdHandle(order.getPkey());
        BigDecimal otherAmt = order.getOtherAmt();
        if(otherAmt == null)
            otherAmt = BigDecimal.ZERO;
        for(Long key : orderMap.keySet())
        {
            if(exitMap.containsKey(key))
            {
                log.info("key重复,删除: {}", key);
                JdOrderCorrelation joc = exitMap.get(key);
                MktOrder mktOrder = orderDao.get(joc.getPkey());
                if(mktOrder.getOtherAmt() != null)
                    otherAmt = otherAmt.subtract(mktOrder.getOtherAmt());
                exitMap.remove(key);
                continue;
            }
            // 生成京东订单管理表数据
            JdOrderCorrelation joc = new JdOrderCorrelation();
            joc.setJdCode(key);
            String payNumber = numberUtils.createOrderNumber();
            joc.setOrderCode(payNumber + "1");
            joc.setParentOrder(pOrder);
            joc.setStatus(OrderCorrelationStatus.NORMAL_ORDER);
            
            // 新增订单
            MktOrder orderAdd = new MktOrder();
            BeanUtils.copyProperties(order, orderAdd, "weixinAmt", "otherAmt");
            orderAdd.setPkey(null);
            orderAdd.setStatus(status);
            orderAdd.setCode(joc.getOrderCode());
            orderAdd.setCreatedTime(null);
            BigDecimal amto = BigDecimal.ZERO;
            for(JdSplitOrderLine ol : orderMap.get(key))
            {
                BigDecimal multiply = ol.getPricen().multiply(new BigDecimal(ol.getNum()));
                amto = amto.add(multiply);
            }
            if(freightMap.containsKey(key))
            {
                orderAdd.setPostage(freightMap.get(key));
                orderAdd.setOldPostage(freightMap.get(key));
                if(Boolean.FALSE.equals(jdPostageConfig.getIsConsumerPostage()))
                    orderAdd.setPostage(BigDecimal.ZERO);
            }
            else
            {
                orderAdd.setPostage(BigDecimal.ZERO);
                orderAdd.setOldPostage(BigDecimal.ZERO);
            }
            orderAdd.setAmto(amto);
            orderAdd.setAmtall(amto.add(orderAdd.getOldPostage()));
            orderAdd.setAmtn(amto.add(orderAdd.getPostage()));
            if(OrderStatus.PAYING_ORDER.equals(orderAdd.getStatus()))
            {
                orderAdd.setStatus(OrderStatus.DELIVERED_ORDER);
            }
            MktOrder mktOrder = orderDao.add(orderAdd);
            if(orderDesc != null)
            {
                MktOrderDesc addOrderDesc = new MktOrderDesc();
                BeanUtils.copyProperties(orderDesc, addOrderDesc);
                addOrderDesc.setPkey(mktOrder.getPkey());
                orderDescDao.add(addOrderDesc);
            }
            if(listOrderTag != null && !listOrderTag.isEmpty())
            {
                List<MktOrderTag> otl = new ArrayList<>();
                for(MktOrderTag ot : listOrderTag)
                {
                    MktOrderTag addOrderTag = new MktOrderTag();
                    BeanUtils.copyProperties(ot, addOrderTag);
                    addOrderTag.setPkey(null);
                    addOrderTag.setOrderPkey(mktOrder.getPkey());
                    otl.add(addOrderTag);
                }
                orderTagDao.addAll(otl);
            }
            joc.setPkey(mktOrder.getPkey());
            addList.add(joc);
            
            
            List<MktOrderLine> addOl = new ArrayList<>();
            List<JdSplitOrderLine> list = orderMap.get(key);
            // 修改订单明细
            for(int i = list.size() - 1; i >= 0; i--)
            {
                JdSplitOrderLine ol = list.get(i);
                // TODO 查ol对应orderPkey 然后对比 mktOrder 的status 不相同  更新mktOrder的status
                ol.setStatus(mktOrder.getStatus());
                ol.setOrderPkey(mktOrder.getPkey());
                if(ol.getJdNum() != null)
                {
                    MktOrderLine add = BeanUtil.beanFrom(MktOrderLine.class, ol);
                    add.setNum(ol.getJdNum());
                    add.setCouponAmt(add.getPricen().multiply(BigDecimal.valueOf(add.getNum())));
                    add.setPkey(null);
                    addOl.add(add);
                    list.remove(i);
                    BigDecimal amto2 = add.getCouponAmt();
                    mktOrder.setAmto(amto2);
                    mktOrder.setAmtall(amto2.add(orderAdd.getOldPostage()));
                    mktOrder.setAmtn(amto2.add(orderAdd.getPostage()));
                    orderDao.update(mktOrder);
                }
            }
            orderLineDao.updateAll(BeanUtil.beanListFrom(MktOrderLine.class, list));
            orderLineDao.addAll(addOl);
            
            mktOrder.setWeixinAmt(mktOrder.getAmtn());
            mktOrder.setOtherAmt(BigDecimal.ZERO);
            if(otherAmt.compareTo(BigDecimal.ZERO) > 0)
            {
                if(otherAmt.compareTo(mktOrder.getAmtn()) >= 0)
                {
                    mktOrder.setOtherAmt(mktOrder.getAmtn());
                    mktOrder.setWeixinAmt(BigDecimal.ZERO);
                    otherAmt = otherAmt.subtract(mktOrder.getAmtn());
                }
                else
                {
                    mktOrder.setOtherAmt(otherAmt);
                    mktOrder.setWeixinAmt(mktOrder.getAmtn().subtract(otherAmt));
                    otherAmt = BigDecimal.ZERO;
                }
            }
            orderDao.update(mktOrder);
            
            
            if(oldOr != null)
            {
                MktOrderRefund or = new MktOrderRefund();
                or.setCode(mktOrder.getCode());
                or.setOrderPkey(mktOrder.getPkey());
//                String outRefundNo = numberUtils.createRefundOrderNumber();
//                or.setOutRefundNo(outRefundNo);
                or.setStatus(oldOr.getStatus());
                or.setMember(order.getMember());
                or.setReason(oldOr.getReason());
                or.setDescribe(oldOr.getDescribe());
                or.setPhoto(oldOr.getPhoto());
                or.setPreferentialAmt(mktOrder.getCardAmt());
                or.setPreferentialPostageAmt(mktOrder.getCardPostageAmt());
                or.setOldPostage(mktOrder.getOldPostage());
                or.setPostage(mktOrder.getPostage());
                or.setAmtall(mktOrder.getAmtn());
                or.setRefundPoint(0);
//                Date now = new Date();
//                or.setDelBy(CurrentSession.userPkey());
//                or.setDelTime(now);
//                or.setReTime(now);
                or.setGoodsAmt(mktOrder.getAmto());
                or.setRefundGoodsAmt(mktOrder.getAmto());
                or.setRefundPostage(mktOrder.getPostage());
                or.setRefundJdPostage(mktOrder.getOldPostage());
                or.setAmtre(mktOrder.getAmtn());
                or.setRefundWeixinAmt(mktOrder.getRefundWeixinAmt());
                or.setRefundOtherAmt(mktOrder.getRefundOtherAmt());
                or.setFarmer(mktOrder.getFarmer());
                or.setCompany(mktOrder.getCompany());
                or.setAscription(mktOrder.getAscription());
                MktOrderRefund addOr = orderRefundDao.add(or);
                
                List<MktOrderRefundLine> refundLines = new ArrayList<>();
                for(JdSplitOrderLine ol : orderMap.get(key))
                {
                    MktOrderRefundLine orl = new MktOrderRefundLine();
                    orl.setOrderLinePkey(ol.getPkey());
                    orl.setRefundPkey(addOr.getPkey());
                    orl.setGoods(ol.getGoods());
                    orl.setSpace(ol.getSpace());
                    orl.setRefundPoint(0);
                    orl.setRefundNum(ol.getNum());
                    orl.setFarmer(ol.getFarmer());
                    orl.setAscription(ol.getAscription());
                    orl.setRefundJd(ol.getPrice().multiply(new BigDecimal(ol.getNum()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                    orl.setRefundAmt(ol.getPricen().multiply(new BigDecimal(ol.getNum()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                    refundLines.add(orl);

                    ol.setRefundNum(ol.getNum());
                    ol.setRefundAmt(orl.getRefundAmt());
                    ol.setStatus(OrderStatus.REFUNDED_ORDER);
                }
                orderRefundLineDao.addAll(refundLines);
            }
        }
        jdOrderCorrelationDao.addAll(addList);
        
        if(oldOr != null)
        {
            orderRefundDao.remove(oldOr);
        }
        
        // 处理子订单再拆分 作废
        if(!exitMap.keySet().isEmpty())
        {
            for(JdOrderCorrelation joc : exitMap.values())
            {
                joc.setStatus(OrderCorrelationStatus.REVOKE_ORDER);
                jdOrderCorrelationDao.update(joc);
                MktOrder mktOrder = orderDao.get(joc.getPkey());
                mktOrder.setStatus(OrderStatus.VOID_ORDER);
                orderDao.update(mktOrder);
            }
        }
    }
    
    public JdOrderDeliveryInfo queryDeliveryInfo(Integer pkey)
    {
        MktOrder order = orderDao.get(pkey);
        if (order == null)
            throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        JdOrderCorrelation joc = jdOrderCorrelationDao.getByCode(order.getCode());
        if (joc == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到京东订单");
        DeliveryInfoQueryOpenResp resp = jdVOPOrderManager.queryDeliveryInfo(joc.getJdCode(), null);
        JdOrderDeliveryInfo info = new JdOrderDeliveryInfo();
        if (resp == null)
            return info;
        info.setTrackInfoList(BeanUtil.beanListFrom(JdOrderDeliveryInfo.TrackInfo.class, resp.getTrackInfoList()));
        List<JdOrderDeliveryInfo.LogisticInfo> logisticInfos = new ArrayList<>();
        for (LogisticInfoOrderOpenResp logistic : resp.getLogisticInfoList())
        {
            if (StringUtil.isNotBlank(logistic.getDeliveryCarrier())
                && StringUtil.isNotBlank(logistic.getDeliveryOrderId()))
                logisticInfos.add(BeanUtil.beanFrom(JdOrderDeliveryInfo.LogisticInfo.class, logistic));
        }
        info.setLogisticInfoList(logisticInfos);
        return info;
    }
    
    public Boolean confirmReceiveByOrderTest(Integer pkey)
    {
        JdOrderCorrelation joc = jdOrderCorrelationDao.get(pkey);
        jdVOPOrderManager.confirmReceiveByOrder(joc.getJdCode(), joc.getOrderCode());
        return true;
    }
}
