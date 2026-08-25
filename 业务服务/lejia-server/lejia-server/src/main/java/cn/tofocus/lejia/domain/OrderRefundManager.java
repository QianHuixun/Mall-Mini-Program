package cn.tofocus.lejia.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.queryAfsAddressInfos.AfsAddressInfoOpenResp;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderDTO;
import cn.tofocus.lejia.bean.dto.app.refund.AppRefundOrderOnInfo;
import cn.tofocus.lejia.bean.dto.app.refund.AppRefundOrderOnPage;
import cn.tofocus.lejia.bean.dto.market.MktOrderExpressInfo;
import cn.tofocus.lejia.bean.dto.refund.OrderRefundOnInfo;
import cn.tofocus.lejia.bean.dto.refund.OrderRefundOnPage;
import cn.tofocus.lejia.bean.dto.refund.PreRefundOrderCommit;
import cn.tofocus.lejia.bean.dto.refund.PreRefundOrderInfo;
import cn.tofocus.lejia.bean.dto.refund.PreUpdRefundOrderInfo;
import cn.tofocus.lejia.bean.dto.refund.RefundOnLine;
import cn.tofocus.lejia.bean.dto.refund.RefundOrderDetails;
import cn.tofocus.lejia.bean.dto.refund.RefundOrderOnInfo;
import cn.tofocus.lejia.bean.dto.refund.RefundUpdOnInfo;
import cn.tofocus.lejia.bean.dto.refund.VendorRefundOrderOnInfo;
import cn.tofocus.lejia.bean.dto.refund.VendorRefundOrderOnList;
import cn.tofocus.lejia.bean.dto.refund.WebOrderRefundOnInfo;
import cn.tofocus.lejia.bean.dto.refund.WebOrderRefundOnList;
import cn.tofocus.lejia.bean.dto.v2.order.OrderGwcV2OnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdGoodsSpace;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktActivityCoupon;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.market.MktOrderExpress;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.market.MktPayLine;
import cn.tofocus.lejia.bean.entity.market.MktWareLine;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundExtend;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWalletLine;
import cn.tofocus.lejia.bean.entity.zx.ThirdPayLineEntity;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.CouponType;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.RefundType;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.bean.enums.WareType;
import cn.tofocus.lejia.bean.enums.jd.CourierType;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import cn.tofocus.lejia.bean.enums.jd.ReturnExchange;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.MktSpaceKcDao;
import cn.tofocus.lejia.dao.jd.JdAddressDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.jd.JdGoodsSpaceDao;
import cn.tofocus.lejia.dao.jd.JdOrderCorrelationDao;
import cn.tofocus.lejia.dao.market.MktActivityCouponDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktAddrDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.market.MktOrderExpressDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.market.MktPayLineDao;
import cn.tofocus.lejia.dao.market.MktWareLineDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundExtendDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWalletLineDao;
import cn.tofocus.lejia.dao.zx.ThirdPayLineDao;
import cn.tofocus.lejia.domain.app.SaasTokenPublicManager;
import cn.tofocus.lejia.domain.jd.JdOrderRefundManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPAfsManager;
import cn.tofocus.lejia.domain.market.MemberCommManager;
import cn.tofocus.lejia.domain.market.MemberPointManager;
import cn.tofocus.lejia.domain.market.MktMemberMsdManager;
import cn.tofocus.lejia.domain.market.OrderManager;
import cn.tofocus.lejia.domain.market.VendorOrderManager;
import cn.tofocus.lejia.domain.market.mall.AppConfigManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.pay.ChinaUmsRefundManager;
import cn.tofocus.lejia.domain.pay.WxRefundManager;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsRefundResponse;
import cn.tofocus.lejia.domain.vendor.VendorWalletUpdManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderRefundManager
{
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOrderRefundExtendDao orderRefundExtendDao;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderDescDao descDao;
    
    @Autowired
    private MktAddrDao addrDao;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MemberCommManager commManager;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private WxRefundManager wxRefundManager;
    
    @Autowired
    private MktPayLineDao payDao;
    
    @Autowired
    private ThirdPayLineDao thirdPayLineDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private AppOrderManager appOrderManager;
    
    @Autowired
    private VendorWalletUpdManager vendorWalletManager;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private MktOrderExpressDao orderExpressDao;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MktActivityCouponDao activityCouponDao;
    
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private MktWareLineDao wareLineDao;
    
    @Autowired
    private GoodsBoxManager goodsBoxManager;
    
    @Autowired
    private VendorOrderManager vendorOrderManager;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private AppConfigManager appConfigManager;
    
    @Autowired
    private SaasTokenPublicManager saasTokenPublicManager;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    @Autowired
    private ChinaUmsRefundManager chinaUmsRefundManager;
    
    @Autowired
    private MktVendorWalletLineDao vendorWalletLineDao;
    
    @Autowired
    private MemberPointManager memberPointManager;
    
    @Autowired
    private JdGoodsDao jdGoodsDao;
    
    @Autowired
    private JdGoodsSpaceDao jdGoodsSpaceDao;
    
    @Autowired
    private JdOrderRefundManager jdOrderRefundManager;
    
    @Autowired
    private JdOrderCorrelationDao jdOrderCorrelationDao;
    
    @Autowired
    private JdVOPAfsManager jdVOPAfsManager;
    
    @Autowired
    private JdAddressDao jdAddressDao;
    
    @Autowired
    private MktMemberMsdManager memberMsdManager;
    
    
    // 预退款
    public PreRefundOrderInfo preRefundOrder(PreRefundOrderCommit info)
    {
        MktOrder mktOrder = orderDao.get(info.getPkey());
        BigDecimal refundGoodsAmt = BigDecimal.ZERO;
        BigDecimal goodsAmt = BigDecimal.ZERO;
        if (orderRefundDao.checkApplying(info.getPkey()))
            throw TofocusException.of(LejiaErrCode.ORDER_REFUND_STATUS_ERROR);
        List<MktOrderLine> listOrder = orderLineDao.listOrder(mktOrder.getPkey());
        Map<Integer, MktOrderLine> map = new HashMap<>();
        for (MktOrderLine ol : listOrder)
        {
            goodsAmt = goodsAmt.add(ol.getPricen().multiply(new BigDecimal(ol.getNum())));
            map.put(ol.getPkey(), ol);
        }
        List<MktVendorOrder> vendorOrderList = vendorOrderDao.listOrder(mktOrder.getPkey());
        vendorOrderList.forEach(e -> {
            if (SettlementType.SUCCESS.equals(e.getStatus()))
                throw TofocusException.of(LejiaErrCode.VENDOR_ORDER_SETTLEMENTTYPE_REFUND_ERROR);
        });
        Integer refundPoint = 0;
        for (RefundOnLine rl : info.getLines())
        {
            // RefundOnLine pkey 对应的是 MktOrderLine 的pkey
            refundGoodsAmt = refundGoodsAmt.add(rl.getRefundAmt());
            if (map.containsKey(rl.getPkey()))
            {
                MktOrderLine orderLine = map.get(rl.getPkey());
                
                Integer num = orderLine.getNum();
                if (orderLine.getRefundNum() != null && orderLine.getRefundNum() > 0)
                {
                    num = num - orderLine.getRefundNum();
                }
                if (rl.getNum() > num) throw TofocusException.of(LejiaErrCode.REFUND_LINE_NUM_ERROR);
                BigDecimal couponAmt = orderLine.getCouponAmt();
                if (couponAmt == null) couponAmt = orderLine.getPricen().multiply(new BigDecimal(num));
                
                BigDecimal remainAmt = null;
                if (orderLine.getRefundAmt() != null && orderLine.getRefundAmt().compareTo(BigDecimal.ZERO) > 0)
                {
                    remainAmt = couponAmt.subtract(orderLine.getRefundAmt());
                    orderLine.setRefundAmt(orderLine.getRefundAmt().add(rl.getRefundAmt()));
                }
                else
                {
                    remainAmt = couponAmt;
                    orderLine.setRefundAmt(rl.getRefundAmt());
                }
                if (rl.getRefundAmt().compareTo(remainAmt) > 0)
                    throw TofocusException.of(LejiaErrCode.REFUND_LINE_AMT_ERROR);
                if(orderLine.getRefundAmt().compareTo(orderLine.getCouponAmt()) == 0 && orderLine.getPoint() != null)
                {
                    refundPoint += (orderLine.getPoint() * orderLine.getNum());
                }
            }
        }
        
        // 查询已发起和已完成的退款商品金额，判断是否退完
        BigDecimal haveRefundedGoodsAmt = orderRefundDao.aggRefundGoodsAmt(mktOrder.getPkey(),
            Lists.newArrayList(RefundStatus.REFUND_APPLYING, RefundStatus.REFUND_AGREE, RefundStatus.REFUND_FINAL));
        BigDecimal subtract = goodsAmt.subtract(mktOrder.getCardAmt());
        if (subtract.compareTo(BigDecimal.ZERO) < 0)
        {
            subtract = BigDecimal.ZERO;
        }
        BigDecimal allowedRefundGoodsAmt = subtract.subtract(haveRefundedGoodsAmt);
        if (allowedRefundGoodsAmt.compareTo(refundGoodsAmt) < 0)
            throw TofocusException.of(LejiaErrCode.REFUND_LINE_AMT_ERROR, "退款商品总价大于剩余允许退款商品总价");
        PreRefundOrderInfo preRefund = new PreRefundOrderInfo();
        preRefund.setRefundGoodsAmt(refundGoodsAmt);
        BigDecimal refundPostage = BigDecimal.ZERO;
        Integer refundCard = null;
        Integer refundPostageCard = null;
        if (allowedRefundGoodsAmt.compareTo(refundGoodsAmt) == 0)
        {
            if (OrderStatus.DELIVERED_ORDER.equals(mktOrder.getStatus())
                || OrderStatus.PAYING_ORDER.equals(mktOrder.getStatus())) 
                refundPostage = mktOrder.getPostage();
            refundCard = mktOrder.getCard();
            refundPostageCard = mktOrder.getCardPostage();
        }
        preRefund.setRefundPostage(refundPostage);
        preRefund.setHasRefundCard(refundCard != null);
        preRefund.setHasRefundCardPostage(refundPostageCard != null);
        preRefund.setRefundAmt(refundGoodsAmt.add(refundPostage));
        preRefund.setRefundPoint(refundPoint);
        
        if(OrderType.INTEGRAL_JD_ORDER.equals(mktOrder.getOrderType())
            && info.getReturnExchange() != null 
            && ReturnExchange.EXCHANGE.equals(info.getReturnExchange()))
        {
            preRefund.setRefundAmt(BigDecimal.ZERO);
        }
        
        return preRefund;
    }
    
    // 申请退款
    @Transactional(rollbackFor = Exception.class)
    public Integer applyForOrderRefund(RefundOrderOnInfo info, RefundStatus status, RefundType type)
    {
        log.info("applyForOrderRefund-status: {}", status);
        MktOrder mktOrder = orderDao.get(info.getPkey());
        BigDecimal refundGoodsAmt = BigDecimal.ZERO;
        BigDecimal goodsAmt = BigDecimal.ZERO;
        if (orderRefundDao.checkApplying(info.getPkey()))
            throw TofocusException.of(LejiaErrCode.ORDER_REFUND_STATUS_ERROR);
        // 查询 京东商品是否可以售后
        if(OrderType.INTEGRAL_JD_ORDER.equals(mktOrder.getOrderType()) && 
            info.getJdType() != null && !info.getJdType().equals(RefundJdType.RETURN_MONEY))
        {
            JdOrderCorrelation joc = jdOrderCorrelationDao.getByCode(mktOrder.getCode());
            List<Long> skuIdList = new ArrayList<>();
            Map<Long,Integer> map = new HashMap<>();
            for(RefundOnLine rol : info.getLines())
            {
                MktOrderLine orderLine = orderLineDao.get(rol.getPkey());
                skuIdList.add(orderLine.getSpace());
                map.put(orderLine.getSpace(), rol.getNum());
            }
            jdOrderRefundManager.getGoodsAttributes(info.getJdType(), skuIdList, joc.getJdCode(), map);
        }
        
        List<MktOrderLine> listOrder = orderLineDao.listOrder(mktOrder.getPkey());
        Map<Integer, MktOrderLine> map = new HashMap<>();
        for (MktOrderLine ol : listOrder)
        {
            goodsAmt = goodsAmt.add(ol.getPricen().multiply(new BigDecimal(ol.getNum())));
            map.put(ol.getPkey(), ol);
        }
        List<MktVendorOrder> updVendorOrderList = new ArrayList<>();
        List<MktVendorOrder> vendorOrderList = vendorOrderDao.listOrder(mktOrder.getPkey());
        Map<Integer, MktVendorOrder> vendorMap = new HashMap<>();
        vendorOrderList.forEach(e -> {
            if (SettlementType.SUCCESS.equals(e.getStatus()))
                throw TofocusException.of(LejiaErrCode.VENDOR_ORDER_SETTLEMENTTYPE_REFUND_ERROR);
            vendorMap.put(e.getOrderLinePkey(), e);
        });
        
        List<MktOrderRefundLine> refundLines = new ArrayList<>();
        List<MktOrderLine> updOLineList = new ArrayList<>();
        //        BigDecimal divide = new BigDecimal("1");
        Integer refundPoint = 0;
        for (RefundOnLine rl : info.getLines())
        {
            // RefundOnLine pkey 对应的是 MktOrderLine 的pkey
            refundGoodsAmt = refundGoodsAmt.add(rl.getRefundAmt());
            if (map.containsKey(rl.getPkey()))
            {
                MktOrderLine orderLine = map.get(rl.getPkey());
                MktOrderRefundLine orl = new MktOrderRefundLine();
                orl.setOrderLinePkey(orderLine.getPkey());
                orl.setGoods(orderLine.getGoods());
                orl.setSpace(orderLine.getSpace());
                orl.setRefundPoint(0);
                
                Integer num = orderLine.getNum();
                if (orderLine.getRefundNum() != null && orderLine.getRefundNum() > 0)
                {
                    num = num - orderLine.getRefundNum();
                    orderLine.setRefundNum(orderLine.getRefundNum() + rl.getNum());
                }
                else
                    orderLine.setRefundNum(rl.getNum());
                if (rl.getNum() > num) throw TofocusException.of(LejiaErrCode.REFUND_LINE_NUM_ERROR);
                BigDecimal couponAmt = orderLine.getCouponAmt();
                if (couponAmt == null) couponAmt = orderLine.getPricen().multiply(new BigDecimal(orderLine.getNum()));
                
                BigDecimal remainAmt = null;
                if (orderLine.getRefundAmt() != null && orderLine.getRefundAmt().compareTo(BigDecimal.ZERO) > 0)
                {
                    remainAmt = couponAmt.subtract(orderLine.getRefundAmt());
                    orderLine.setRefundAmt(orderLine.getRefundAmt().add(rl.getRefundAmt()));
                }
                else
                {
                    remainAmt = couponAmt;
                    orderLine.setRefundAmt(rl.getRefundAmt());
                }
                if (rl.getRefundAmt().compareTo(remainAmt) > 0)
                    throw TofocusException.of(LejiaErrCode.REFUND_LINE_AMT_ERROR);
                orl.setRefundAmt(rl.getRefundAmt());
                // 京东商品
                if(OrderType.INTEGRAL_JD_ORDER.equals(mktOrder.getOrderType()))
                    orl.setRefundJd(orderLine.getPrice().multiply(new BigDecimal(orderLine.getNum()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                if(RefundJdType.EXCHANGE.equals(info.getJdType()))
                {
                    orl.setRefundAmt(BigDecimal.ZERO);
                    orl.setRefundJd(BigDecimal.ZERO);
                    orderLine.setRefundAmt(BigDecimal.ZERO);
                }
                orl.setRefundNum(rl.getNum());
                orl.setFarmer(orderLine.getFarmer());
                orl.setAscription(orderLine.getAscription());
             
                BigDecimal refundAmt = orl.getRefundAmt();
                List<MktOrderRefundLine> listOrderLinePkey = orderRefundLineDao.listOrderLinePkey(orderLine.getPkey());
                if(listOrderLinePkey != null && !listOrderLinePkey.isEmpty())
                {
                    for(MktOrderRefundLine d : listOrderLinePkey)
                    {
                        refundAmt = refundAmt.add(d.getRefundAmt());
                    }
                }
                if(refundAmt.compareTo(orderLine.getCouponAmt()) == 0)
                {
                    orl.setRefundPoint(orderLine.getPoint()==null?0 : orderLine.getPoint() * orderLine.getNum());
                    refundPoint += (orderLine.getPoint()==null?0 : orderLine.getPoint() * orderLine.getNum());
                }
                refundLines.add(orl);
                updOLineList.add(orderLine);
                
                if (vendorMap.containsKey(rl.getPkey()))
                {
                    MktVendorOrder vo = vendorMap.get(rl.getPkey());
                    vo.setRefundStatus(status);
//                    vo = vendorOrderRefundAmt(orderLine.getRefundAmt(), couponAmt, vo, mktOrder.getCode());
                    updVendorOrderList.add(vo); // 减少商户钱包明细
                }
            }
        }
        // 查询已发起和已完成的退款商品金额，判断是否退完
        BigDecimal haveRefundedGoodsAmt = orderRefundDao.aggRefundGoodsAmt(mktOrder.getPkey(),
            Lists.newArrayList(RefundStatus.REFUND_APPLYING, RefundStatus.REFUND_AGREE, RefundStatus.REFUND_FINAL));
        BigDecimal subtract = goodsAmt.subtract(mktOrder.getCardAmt());
        if (subtract.compareTo(BigDecimal.ZERO) < 0)
        {
//            subtract = new BigDecimal("0.01");
            subtract = BigDecimal.ZERO;
        }
        BigDecimal allowedRefundGoodsAmt = subtract.subtract(haveRefundedGoodsAmt);
        if (allowedRefundGoodsAmt.compareTo(refundGoodsAmt) < 0)
            throw TofocusException.of(LejiaErrCode.REFUND_LINE_AMT_ERROR, "退款商品总价大于剩余允许退款商品总价");
        MktOrderRefund or = assembleOrderRefund(mktOrder,
            status,
            info.getReason(),
            info.getDescribe(),
            info.getPhoto(),
            goodsAmt,
            refundGoodsAmt,
            allowedRefundGoodsAmt.compareTo(refundGoodsAmt) == 0,
            info.getJdType());
        or.setType(type);
        or.setRefundPoint(refundPoint);
        
        if(OrderType.INTEGRAL_JD_ORDER.equals(mktOrder.getOrderType()))
        {
            or.setIsJd(true);
            or.setJdType(info.getJdType());
            if (or.getJdType() == null || or.getJdType() == RefundJdType.RETURN_MONEY)
            {
                or.setRefundJdPostage(mktOrder.getOldPostage());
            }
            else
            {
                or.setRefundJdPostage(BigDecimal.ZERO);
            }
        }
        else
        {
            or.setJdType(RefundJdType.RETURN_MONEY);
            or.setIsJd(false);
        }
        MktOrderRefund add = orderRefundDao.add(or);
        if(OrderType.INTEGRAL_JD_ORDER.equals(mktOrder.getOrderType()))
        {
            MktOrderRefundExtend ore = new MktOrderRefundExtend();
            BeanUtils.copyProperties(info, ore, "pkey");
//            if(StringUtils.isNotBlank(info.getAppointmentPickupTime()))
//            {
//                String apt = info.getAppointmentPickupTime();
//                ore.setPickupTimeStart(apt.substring(0, 16) + ":00");
//                String pickupTimeEnd = apt.substring(0, 10) + " " + apt.substring(17,22) + ":00";
//                ore.setPickupTimeEnd(pickupTimeEnd);
//            }
            if(StringUtils.isNotBlank(ore.getPickupTimeStart()) && ore.getPickupTimeStart().length() == 16)
                ore.setPickupTimeStart(ore.getPickupTimeStart() + ":00");
            if(StringUtils.isNotBlank(ore.getPickupTimeEnd()) && ore.getPickupTimeEnd().length() == 16)
                ore.setPickupTimeEnd(ore.getPickupTimeEnd() + ":00");
            if(info.getAddrPkey() != null)
            {
                MktAddr addr = addrDao.get(info.getAddrPkey());
                ore.setPro(addr.getPro());
                ore.setCity(addr.getCity());
                ore.setArea(addr.getArea());
                ore.setTown(addr.getTown());
                ore.setAddr(addr.getAddr());
                ore.setName(addr.getName());
                ore.setMobile(addr.getMobile());
            }
            if(info.getReceiptAddrPkey() != null)
            {
                MktAddr receiptAddr = addrDao.get(info.getReceiptAddrPkey());
                ore.setReceiptPro(receiptAddr.getPro());
                ore.setReceiptCity(receiptAddr.getCity());
                ore.setReceiptArea(receiptAddr.getArea());
                ore.setReceiptTown(receiptAddr.getTown());
                ore.setReceiptAddr(receiptAddr.getAddr());
                ore.setReceiptName(receiptAddr.getName());
                ore.setReceiptMobile(receiptAddr.getMobile());
            }
            ore.setRefundPkey(add.getPkey());
            ore.setFarmer(add.getFarmer());
            ore.setAscription(add.getAscription());
            orderRefundExtendDao.add(ore);
        }
        for (MktOrderRefundLine orl : refundLines)
        {
            orl.setRefundPkey(add.getPkey());
        }
        orderRefundLineDao.addAll(refundLines);
        
        if (!updVendorOrderList.isEmpty()) vendorOrderDao.updateAll(updVendorOrderList);
        orderLineDao.updateAll(updOLineList);
        
        //        if(mktOrder.getRefundAmt() != null)
        //            mktOrder.setRefundAmt(mktOrder.getRefundAmt().add(refundAmt));
        //        else
        //            mktOrder.setRefundAmt(refundAmt);
        //        orderDao.update(mktOrder);
        
        if (RefundStatus.REFUND_FINAL.equals(status))
        {
            // 直接扣钱
            //            new Thread(new Runnable()
            //            {
            //                @Override
            //                public void run()
            //                {
            //                    
            //                }
            //            });
            agreeRefund(add, "商家发起退款： " + info.getReason());
        }
        if(OrderType.INTEGRAL_JD_ORDER.equals(mktOrder.getOrderType()))
            jdOrderRefundManager.agreeRefund(add.getPkey(), info.getReason());
        return add.getPkey();
    }
    
    /**
     * 修改商户采购订单退款金额（不执行Dao）
     * @param oRefundAmt 实际退款金额
     * @param oPayAmt 实付金额
     * @param vo 商户采购订单
     * @param code 销售单号
     * @return
     */
    private MktVendorOrder vendorOrderRefundAmt(BigDecimal oRefundAmt, BigDecimal couponPrice, BigDecimal oPayAmt,
        MktVendorOrder vo, String code, Date orderTime)
    {
        // , BigDecimal oPayAmt
        log.info("oRefundAmt: {}, oPayAmt: {}, couponPrice: {}", oRefundAmt, oPayAmt, couponPrice);
        BigDecimal amt = vo.getAmt();
        if(CommissionType.MERCHANT.equals(vo.getCommissionType())
            && vo.getPayComm() != null
            && vo.getPayComm().compareTo(BigDecimal.ZERO) > 0)
        {
            amt = amt.subtract(vo.getPayComm());
        }
        vendorWalletManager.updWalletLockAmount(vo.getVendor(), amt, false, VendorWalletSource.REVOKE, code, orderTime);
        BigDecimal commissionRate = vo.getCommissionRate();
//        BigDecimal commissions = vo.getCommissions();
//        vo.setRefundAmt(oRefundAmt);
        log.info("开始前vo.getAmt: {}", vo.getAmt());
//        if (vo.getProcureRefundAmt() != null) vo.setAmt(vo.getAmt().add(vo.getProcureRefundAmt()));
        BigDecimal bigNum = new BigDecimal(vo.getNum());
      
        // 商品总价
        BigDecimal voAmt = vo.getGoodsPrice().multiply(bigNum);
        
//        if(vo.getPackingCharge() != null)
        //            procureRefundAmt = procureRefundAmt.subtract(vo.getPackingCharge());
        // TDDO 打包费未处理
        
        vo.setRefundAmt(oRefundAmt);
        
        // 商品单价 和 采购单价 不同，则代表需要计算佣金
        if (vo.getGoodsPrice().compareTo(vo.getPrice()) != 0)
        {
            if (commissionRate.compareTo(BigDecimal.ZERO) == 0)
            {
                // 采购价模式
                // 总佣金 = (商品单价 - 采购单价) * 数量
                BigDecimal totalCommissions = vo.getGoodsPrice().subtract(vo.getPrice()).multiply(bigNum);
                
                // 退款后佣金 = (实际付款金额 - 退款金额) / 总金额 * 总佣金
                if(oPayAmt.compareTo(BigDecimal.ZERO) != 0)
                {
                    BigDecimal divide = oPayAmt.subtract(oRefundAmt).multiply(totalCommissions).divide(oPayAmt, 2, RoundingMode.HALF_UP);
                    vo.setCommissions(divide);
                }
                else
                    vo.setCommissions(oPayAmt.subtract(oRefundAmt).multiply(totalCommissions));
                
                // 采购应退金额 = 退款金额 / 商品总价 * 采购总价 
//                BigDecimal procureRefundAmt = oRefundAmt.multiply(vo.getPrice().multiply(bigNum)).divide(oPayAmt, 2, RoundingMode.HALF_UP);
                vo.setProcureRefundAmt(oRefundAmt);
                
                // 应该退 = 退款金额 / 实际付款金额 * 商品总价
                if(oPayAmt.compareTo(BigDecimal.ZERO) != 0)
                    oRefundAmt = oRefundAmt.multiply(voAmt).divide(oPayAmt, 2, RoundingMode.HALF_UP);
                vo.setRefundAmt(oRefundAmt);
//                voAmt = vo.getPrice().multiply(bigNum);
                
                
                // 采购价模式
                // 退款后佣金 = 总佣金 - 退款金额 / 实付金额 * 总佣金
//                vo.setCommissions(totalCommissions
//                    .subtract(oRefundAmt.multiply(totalCommissions).divide(oPayAmt, 2, RoundingMode.HALF_UP)));
//                voAmt = vo.getPrice().multiply(bigNum);
            }
            else
            {
                BigDecimal realCommissionRate = commissionRate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                if(vo.getGoodsPrice().compareTo(couponPrice) == 0)
                {
                    BigDecimal multiply = voAmt.subtract(oRefundAmt).multiply(realCommissionRate).setScale(2, RoundingMode.HALF_UP);
                    System.out.println("==0oRefundAmt: " + oRefundAmt);
                    System.out.println("==0multiply: " + multiply);
                    vo.setCommissions(multiply);
                    vo.setMarketCommissions(vo.getCommissions());
                    if (vo.getSysCommissionRate() != null)
                    {
                        vo.setSysCommissions(voAmt.subtract(oRefundAmt)
                            .multiply(vo.getSysCommissionRate().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                            .setScale(2, RoundingMode.HALF_UP)
                            );
                        vo.setMarketCommissions(vo.getMarketCommissions().subtract(vo.getSysCommissions()));
                    }
                    // 采购退款
                    BigDecimal refundAmtComm = oRefundAmt.multiply(realCommissionRate);
                    BigDecimal procureRefundAmt = oRefundAmt.subtract(refundAmtComm);
                    log.info("=====oRefundAmt: {}, realCommissionRate: {}, refundAmtComm: {}, procureRefundAmt: {}",
                            oRefundAmt,
                            realCommissionRate,
                            refundAmtComm,
                            oRefundAmt);
                    vo.setProcureRefundAmt(procureRefundAmt);
                }
                else
                {
                    // 退款金额 / 实付金额 * 优惠总金额 = 优惠退款
                    // 应结算金额 = 总价 - 退款金额 - 优惠退款

                    
                    // 用户退款金额 / 实际支付金额 * 订单总价 = 商户退款金额
                    if(oPayAmt.compareTo(BigDecimal.ZERO) != 0)
                        oRefundAmt = oRefundAmt.multiply(voAmt).divide(oPayAmt, 2, RoundingMode.HALF_UP);
                    vo.setRefundAmt(oRefundAmt);
                    log.info("有优惠券 oRefundAmt: {}, voRefundAmt: {}, voAmt: {}", oRefundAmt, oRefundAmt, voAmt);
                    
                    BigDecimal multiply = voAmt.subtract(oRefundAmt).multiply(realCommissionRate).setScale(2, RoundingMode.HALF_UP);
                    vo.setCommissions(multiply);
                    vo.setMarketCommissions(vo.getCommissions());
                    if (vo.getSysCommissionRate() != null)
                    {
                        vo.setSysCommissions(voAmt.subtract(oRefundAmt)
                            .multiply(vo.getSysCommissionRate().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                            .setScale(2, RoundingMode.HALF_UP)
                            );
                        vo.setMarketCommissions(vo.getMarketCommissions().subtract(vo.getSysCommissions()));
                    }
                    // 采购退款
                    BigDecimal refundAmtComm = oRefundAmt.multiply(realCommissionRate);
                    BigDecimal procureRefundAmt = oRefundAmt.subtract(refundAmtComm);
                    log.info("=====oRefundAmt: {}, realCommissionRate: {}, refundAmtComm: {}, procureRefundAmt: {}",
                        oRefundAmt,
                        realCommissionRate,
                        refundAmtComm,
                        oRefundAmt);
                    vo.setProcureRefundAmt(procureRefundAmt);
                }
            }
        }
        else
        {
            if(vo.getGoodsPrice().compareTo(couponPrice) != 0)
            {
                log.info("无佣金-有优惠券 oRefundAmt: {}, voAmt: {}, oPayAmt: {}", oRefundAmt, voAmt, oPayAmt);
                if(oPayAmt.compareTo(BigDecimal.ZERO) != 0)
                    oRefundAmt = oRefundAmt.multiply(voAmt).divide(oPayAmt, 2, RoundingMode.HALF_UP);
                vo.setRefundAmt(oRefundAmt);
            }
            vo.setProcureRefundAmt(oRefundAmt);
        }
        vo.setAmt(voAmt.subtract(vo.getCommissions()).subtract(oRefundAmt));
        log.info("计算后vo.getAmt: {}", vo.getAmt());
        if (vo.getAmt().compareTo(BigDecimal.ZERO) < 0) vo.setAmt(BigDecimal.ZERO);
        vendorWalletManager.updWalletLockAmount(vo.getVendor(), vo.getAmt(), true, VendorWalletSource.CONSUME, code, orderTime);
        return vo;
    }
    
    /**
     * 修改商户采购订单退款金额（不执行Dao）
     * @param oRefundAmt 实际退款金额
     * @param oPayAmt 实付金额
     * @param vo 商户采购订单
     * @param code 销售单号
     * @return
     */
//    private MktVendorOrder vendorOrderRefundAmtY(BigDecimal oRefundAmt, BigDecimal oPayAmt, MktVendorOrder vo,
//        String code, Date orderTime)
//    {
//        log.info("oRefundAmt: {}, oPayAmt: {}", oRefundAmt, oPayAmt);
//        BigDecimal amt = vo.getAmt();
//        if(CommissionType.MERCHANT.equals(vo.getCommissionType())
//            && vo.getPayComm() != null
//            && vo.getPayComm().compareTo(BigDecimal.ZERO) > 0)
//        {
//            amt = amt.subtract(vo.getPayComm());
//        }
//        vendorWalletManager.updWalletLockAmount(vo.getVendor(), amt, false, VendorWalletSource.REVOKE, code, orderTime);
//        BigDecimal commissionRate = vo.getCommissionRate();
////        BigDecimal commissions = vo.getCommissions();
//        vo.setRefundAmt(oRefundAmt);
//        log.info("开始前vo.getAmt: {}", vo.getAmt());
//        if (vo.getProcureRefundAmt() != null) vo.setAmt(vo.getAmt().add(vo.getProcureRefundAmt()));
//        BigDecimal bigNum = new BigDecimal(vo.getNum());
//        
//        // 采购总价（扣除佣金后的）
//        BigDecimal voAmt = vo.getGoodsPrice().multiply(bigNum);
////        BigDecimal voAmt = vo.getPrice().multiply(bigNum);
//        //        if(vo.getPackingCharge() != null)
//        //            procureRefundAmt = procureRefundAmt.subtract(vo.getPackingCharge());
//        // 商品单价 和 采购单价 不同，则代表需要计算佣金
//        if (!vo.getGoodsPrice().equals(vo.getPrice()))
//        {
//            if (commissionRate.compareTo(BigDecimal.ZERO) == 0)
//            {
//                // 采购价模式
//                // 总佣金 = (商品单价 - 采购单价) * 数量
//                // 退款后佣金 = 总佣金 - 退款金额 / 实付金额 * 总佣金
//                BigDecimal totalCommissions = vo.getGoodsPrice().subtract(vo.getPrice()).multiply(bigNum);
//                if(oPayAmt.compareTo(BigDecimal.ZERO) != 0)
//                {
//                    vo.setCommissions(totalCommissions
//                        .subtract(oRefundAmt.multiply(totalCommissions).divide(oPayAmt, 2, RoundingMode.HALF_UP)));
//                }
//                else
//                {
//                    vo.setCommissions(totalCommissions
//                        .subtract(oRefundAmt.multiply(totalCommissions)));
//                }
//                voAmt = vo.getPrice().multiply(bigNum);
//            }
//            else
//            {
//                // 佣金模式
//                // 退款后佣金 = 总费率 - 退款金额部分的费率
//                // =总价 * 费率 - 退款金额 / 实付金额 * 总价 * 费率
//                // =(实付金额 - 退款金额) / 实付金额 * 总价 * 费率
//                // 商品总金额
//                BigDecimal totalPrice = vo.getGoodsPrice().multiply(bigNum);
//                BigDecimal realCommissionRate = commissionRate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
//                voAmt = voAmt.subtract(voAmt.multiply(realCommissionRate));
//                BigDecimal multiply = oPayAmt.subtract(oRefundAmt);
//                if(oPayAmt.compareTo(BigDecimal.ZERO) != 0)
//                {
//                    multiply =
//                        oPayAmt.subtract(oRefundAmt).divide(oPayAmt, 20, RoundingMode.HALF_UP).multiply(totalPrice);
//                }
//                System.out.println("oPayAmt: " + oPayAmt);
//                System.out.println("oRefundAmt: " + oRefundAmt);
//                System.out.println("multiply: " + multiply);
//                vo.setCommissions(multiply.multiply(realCommissionRate).setScale(3, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
//                vo.setMarketCommissions(vo.getCommissions());
//                if (vo.getSysCommissionRate() != null)
//                {
//                    vo.setSysCommissions(multiply
//                        .multiply(vo.getSysCommissionRate().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
//                        .setScale(2, RoundingMode.HALF_UP)
//                        );
//                    vo.setMarketCommissions(vo.getMarketCommissions().subtract(vo.getSysCommissions()));
//                }
//            }
//        }
//        
//        // 采购退款金额 = 退款金额 / 实付金额 * 采购总价
//        BigDecimal procureRefundAmt = BigDecimal.ZERO;
//        if(oRefundAmt.compareTo(oPayAmt) == 0)
//            procureRefundAmt = vo.getAmt();
//        else
//            procureRefundAmt = oRefundAmt.divide(oPayAmt, 20, RoundingMode.HALF_UP).multiply(voAmt).setScale(3, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
//        vo.setProcureRefundAmt(procureRefundAmt);
//        vo.setAmt(vo.getAmt().subtract(procureRefundAmt));
//        log.info("计算后vo.getAmt: {}", vo.getAmt());
//        if (vo.getAmt().compareTo(BigDecimal.ZERO) < 0) vo.setAmt(BigDecimal.ZERO);
//        vendorWalletManager.updWalletLockAmount(vo.getVendor(), vo.getAmt(), true, VendorWalletSource.CONSUME, code, orderTime);
//        return vo;
//    }
    
    private MktOrderRefund assembleOrderRefund(MktOrder mktOrder, RefundStatus status, String reason, String describe,
        List<String> photo, BigDecimal goodsAmt, BigDecimal refundGoodsAmt, boolean isRefundAll, RefundJdType jdType)
    {
        MktOrderRefund or = new MktOrderRefund();
        or.setJdType(jdType);
        or.setCode(mktOrder.getCode());
        or.setOrderPkey(mktOrder.getPkey());
        or.setStatus(status);
        or.setMember(mktOrder.getMember());
        or.setReason(reason);
        or.setDescribe(describe);
        or.setPhoto(photo);
        or.setPreferentialAmt(mktOrder.getCardAmt());
        or.setPreferentialPostageAmt(mktOrder.getCardPostageAmt());
        or.setOldPostage(mktOrder.getOldPostage());
        or.setPostage(mktOrder.getPostage());
        or.setAmtall(mktOrder.getAmtn());
        or.setRefundPoint(0);
        if (RefundStatus.REFUND_FINAL.equals(status))
        {
            Date now = new Date();
            or.setDelBy(CurrentSession.userPkey());
            or.setDelTime(now);
            or.setReTime(now);
        }
        or.setGoodsAmt(goodsAmt);
        or.setRefundGoodsAmt(refundGoodsAmt);
        BigDecimal refundPostage = BigDecimal.ZERO;
        Integer refundCard = null;
        Integer refundCardPostage = null;
        if (isRefundAll)
        {
            if (OrderStatus.DELIVERED_ORDER.equals(mktOrder.getStatus())
                || OrderStatus.PAYING_ORDER.equals(mktOrder.getStatus())) 
                refundPostage = mktOrder.getPostage();
            refundCard = mktOrder.getCard();
            refundCardPostage = mktOrder.getCardPostage();
        }
        or.setRefundPostage(refundPostage);
        or.setRefundCard(refundCard);
        or.setRefundCardPostage(refundCardPostage);
        or.setAmtre(refundGoodsAmt.add(refundPostage));
        if(RefundJdType.EXCHANGE.equals(or.getJdType()))
        {
            or.setAmtre(BigDecimal.ZERO);
            or.setRefundGoodsAmt(BigDecimal.ZERO);
            or.setRefundPostage(BigDecimal.ZERO);
            or.setRefundCard(null);
            or.setRefundCardPostage(null);
        }
        or.setRefundWeixinAmt(or.getAmtre());
        or.setRefundOtherAmt(BigDecimal.ZERO);
        if(PayType.MSD_COMBINATION.equals(mktOrder.getPayType()) || PayType.ELECTRONIC_ACCOUNT_COMBINATION.equals(mktOrder.getPayType()))
        {
            BigDecimal otherAmt = mktOrder.getOtherAmt();
            if(otherAmt == null)
                otherAmt = BigDecimal.ZERO;
            BigDecimal refundOtherAmt = mktOrder.getRefundOtherAmt();
            if(refundOtherAmt == null)
                refundOtherAmt = BigDecimal.ZERO;
            BigDecimal subtract = otherAmt.subtract(refundOtherAmt).subtract(or.getAmtre());
            if(subtract.compareTo(BigDecimal.ZERO) < 0)
            {
                BigDecimal s = otherAmt.subtract(refundOtherAmt);
                or.setRefundWeixinAmt(or.getAmtre().subtract(s));
                or.setRefundOtherAmt(s);
            }
            else
            {
                or.setRefundWeixinAmt(BigDecimal.ZERO);
                or.setRefundOtherAmt(or.getAmtre());
            }
        }
        if(PayType.ORDER_MSD.equals(mktOrder.getPayType()) || PayType.ORDER_ELECTRONIC_ACCOUNT.equals(mktOrder.getPayType()))
        {
            or.setRefundWeixinAmt(BigDecimal.ZERO);
            or.setRefundOtherAmt(or.getAmtre());
        }
        or.setFarmer(mktOrder.getFarmer());
        or.setCompany(mktOrder.getCompany());
        or.setAscription(mktOrder.getAscription());
        return or;
    }
    
    @Deprecated
    // 旧数据 跑批使用
    public List<MktOrderLine> assembleOrderLine(Integer orderPkey, BigDecimal cardAmt)
    {
        List<MktOrderLine> listOrder = orderLineDao.listOrder(orderPkey);
        if (cardAmt != null && cardAmt.compareTo(BigDecimal.ZERO) != 0)
        {
            BigDecimal sumAmt = BigDecimal.ZERO;
            for (MktOrderLine ol : listOrder)
            {
                sumAmt = sumAmt.add(ol.getPricen());
            }
            BigDecimal zCardAmt = cardAmt;
            for (int i = 0; i < listOrder.size(); i++)
            {
                MktOrderLine ol = listOrder.get(i);
                BigDecimal olNum = new BigDecimal(ol.getNum());
                if (i == (listOrder.size() - 1))
                {
                    ol.setCouponPrice(ol.getPricen().subtract(zCardAmt.divide(olNum, 2, BigDecimal.ROUND_HALF_UP)));
                }
                else
                {
                    BigDecimal multiply =
                        ol.getPricen().multiply(olNum).divide(sumAmt, 2, BigDecimal.ROUND_HALF_UP).multiply(cardAmt);
                    zCardAmt = zCardAmt.subtract(multiply);
                    ol.setCouponPrice(ol.getPricen().subtract(multiply.divide(olNum, 2, BigDecimal.ROUND_HALF_UP)));
                }
                
            }
        }
        else
        {
            for (MktOrderLine ol : listOrder)
            {
                ol.setCouponPrice(ol.getPricen());
            }
        }
        return orderLineDao.updateAll(listOrder);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Boolean agree(Integer pkey, String delDesc)
    {
        MktOrderRefund or = orderRefundDao.get(pkey);
        agreeRefund(or, delDesc);
        return true;
    }
    
    
    // 同意退款 运营端兼容原先
    private Boolean agreeRefund(MktOrderRefund or, String delDesc)
    {
        String outRefundNo = numberUtils.createRefundOrderNumber();
        //        MktOrderRefund or = orderRefundDao.get(pkey);
        or.setOutRefundNo(outRefundNo);
        or.setDelDesc(delDesc);
        or.setDelTime(new Date());
        or.setStatus(RefundStatus.REFUND_FINAL);
        orderRefundDao.update(or);
        
        MktOrder mktOrder = orderDao.get(or.getOrderPkey());
        if (mktOrder.getRefundAmt() != null)
            mktOrder.setRefundAmt(mktOrder.getRefundAmt().add(or.getAmtre()));
        else
            mktOrder.setRefundAmt(or.getAmtre());
        if(mktOrder.getRefundWeixinAmt() == null)
            mktOrder.setRefundWeixinAmt(BigDecimal.ZERO);
        if(mktOrder.getRefundOtherAmt() == null)
            mktOrder.setRefundOtherAmt(BigDecimal.ZERO);
        if(PayType.MSD_COMBINATION.equals(mktOrder.getPayType()) || PayType.ELECTRONIC_ACCOUNT_COMBINATION.equals(mktOrder.getPayType()))
        {
            mktOrder.setRefundWeixinAmt(mktOrder.getRefundWeixinAmt().add(or.getRefundWeixinAmt()));
            mktOrder.setRefundOtherAmt(mktOrder.getRefundOtherAmt().add(or.getRefundOtherAmt()));
        }
        if(PayType.ORDER_MSD.equals(mktOrder.getPayType()) || PayType.ORDER_ELECTRONIC_ACCOUNT.equals(mktOrder.getPayType()))
        {
            mktOrder.setRefundOtherAmt(mktOrder.getRefundOtherAmt().add(or.getRefundOtherAmt()));
        }
        
        // 查询已完成的退款商品金额，判断是否退完
        BigDecimal haveRefundFinalGoodsAmt = orderRefundDao
            .aggRefundGoodsAmt(mktOrder.getPkey(), Lists.newArrayList(RefundStatus.REFUND_FINAL), or.getPkey());
        BigDecimal orderCouponGoodsAmt = mktOrder.getAmto().subtract(mktOrder.getCardAmt());
        if (orderCouponGoodsAmt.compareTo(haveRefundFinalGoodsAmt.add(or.getRefundGoodsAmt())) <= 0)
        {
            mktOrder.setStatus(OrderStatus.REFUNDED_ORDER);
            if(!OrderType.INTEGRAL_MSD_ORDER.equals(mktOrder.getOrderType()))
            {
                memberPointManager.updPointForAmt(mktOrder.getMember(),
                    mktOrder.getAmtn(),
                    false,
                    SourceType.POINTS_REFUND_CONSUMPTION,
                    or.getOutRefundNo(),
                    "消费退款",
                    CurrentSession.ascriptionPkey(),
                    mktOrder.getCode());
            }
        }
        
        if(OrderOir.POINTS_MALL.equals(mktOrder.getOrderOir()))
        {
            Integer refundPoint = 0;
            List<MktOrderRefundLine> listRefundPkey = orderRefundLineDao.listRefundPkey(or.getPkey());
            for(MktOrderRefundLine orl : listRefundPkey)
            {
                MktOrderLine orderLine = orderLineDao.get(orl.getOrderLinePkey());
                if(orderLine.getCouponAmt().compareTo(orderLine.getRefundAmt()) == 0)
                {
                    refundPoint += (orderLine.getPoint() * orderLine.getNum());
                }
            }
            if(refundPoint > 0)
            {
                memberPointManager.updPoint(mktOrder.getMember(),
                    refundPoint,
                    true,
                    SourceType.POINTS_REFUND,
                    DateUtil.formatDate(new Date(), "yyyyMMddHHmmss"),
                    "积分退款",
                    CurrentSession.ascriptionPkey());
            }
            if(mktOrder.getRefundPoint() != null)
                mktOrder.setRefundPoint(mktOrder.getRefundPoint() + refundPoint);
            else
                mktOrder.setRefundPoint(refundPoint);
        }
        orderDao.update(mktOrder);
        List<MktVendorOrder> vendorOrderList = vendorOrderDao.listOrderRefundStatus(mktOrder.getPkey());
        if (vendorOrderList != null && !vendorOrderList.isEmpty())
        {
            Map<Integer, MktVendorOrder> vendorMap = new HashMap<>();
            for (MktVendorOrder vo : vendorOrderList)
            {
                vo.setRefundStatus(RefundStatus.REFUND_FINAL);
                vendorMap.put(vo.getOrderLinePkey(), vo);
            }
            List<MktOrderRefundLine> listRefundPkey = orderRefundLineDao.listRefundPkey(or.getPkey());
            for(MktOrderRefundLine orl : listRefundPkey)
            {
                MktOrderLine orderLine = orderLineDao.get(orl.getOrderLinePkey());
                BigDecimal couponAmt = orderLine.getCouponAmt();
                if (couponAmt == null) couponAmt = orderLine.getPricen().multiply(new BigDecimal(orderLine.getNum()));
                MktVendorOrder vo = vendorMap.get(orl.getOrderLinePkey());
                vo = vendorOrderRefundAmt(orderLine.getRefundAmt(), orderLine.getCouponPrice(), couponAmt, vo, mktOrder.getCode(), mktOrder.getCreatedTime());
            }
            vendorOrderDao.updateAll(vendorOrderList);
        }
    
        // 打包费重新计算
        SysFarmerConfig farmerConfig = sysFarmerConfigDao.get(mktOrder.getFarmer());
        List<MktVendorOrder> listOrder = vendorOrderDao.listOrder(mktOrder.getPkey());
        if (Boolean.TRUE.equals(farmerConfig.getIsPackingCharge()))
        {
            Map<Integer, List<MktVendorOrder>> map = new HashMap<>();
            for (MktVendorOrder vo : listOrder)
            {
                if (!map.containsKey(vo.getVendor()))
                {
                    map.put(vo.getVendor(), new ArrayList<MktVendorOrder>());
                }
                map.get(vo.getVendor()).add(vo);
            }
            vendorOrderManager.handlePackingCharge(map, mktOrder, true);
        }
        // 优惠券退款,手续费,配送费计算
        refundHandleCommission(or, mktOrder, listOrder);
        BigDecimal amtre = or.getAmtre();
        if (PayType.MSD_COMBINATION.equals(mktOrder.getPayType()))
        {
            amtre = or.getRefundWeixinAmt();
            Boolean jdOrder = OrderType.INTEGRAL_JD_ORDER.equals(mktOrder.getOrderType());
            // 退还热力豆
            memberMsdManager.updMsdBalance(mktOrder.getMember(),
                null,
                true,
                or.getRefundOtherAmt(),
                MsdOperationType.REFUND,
                mktOrder.getCode() + "订单退款",
                mktOrder.getCode(),
                mktOrder.getAscription(),
                jdOrder);
//            memberMsdManager.updMsdPayFail(mktOrder.getMember(), null, or.getRefundOtherAmt(), mktOrder.getAscription());
        }
        if(PayType.ELECTRONIC_ACCOUNT_COMBINATION.equals(mktOrder.getPayType()))
        {
            amtre = or.getRefundWeixinAmt();
//            commManager.updCommPayFail(mktOrder.getMember(), or.getRefundOtherAmt(), mktOrder.getAscription());
            commManager.updCommRefund(mktOrder.getMember(),
                or.getRefundOtherAmt(),
                true,
                CommSourceType.COMM_RETURN,
                mktOrder.getCode(),
                mktOrder.getAscription());
        }
        if (PayType.ORDER_WEIXIN.equals(mktOrder.getPayType()) ||
            PayType.MSD_COMBINATION.equals(mktOrder.getPayType()) 
            || PayType.ELECTRONIC_ACCOUNT_COMBINATION.equals(mktOrder.getPayType()))
        {
            String orderNumber = mktOrder.getCode();
            if (StringUtils.isBlank(orderNumber))
            {
                log.info("该订单无订单号,无法向微信发起退款,订单主键: {}", mktOrder.getPkey());
                throw TofocusException.of(LejiaErrCode.WEIXIN_AGREE_ERROR);
            }
            //            orderNumber = orderNumber.substring(0, orderNumber.length() - 1);
            orderNumber = orderNumber.substring(0, 14);
            MktPayLine pl = payDao.getOrderNumber(orderNumber);
            BigDecimal refund = amtre.multiply(new BigDecimal("100"));
            //            BigDecimal amtn = mktOrder.getAmtn().multiply(new BigDecimal("100"));
            SysAscription asc = sysAscriptionDao.get(mktOrder.getAscription());
            if (asc == null || StringUtils.isBlank(asc.getCertificateSerialNo())
                || StringUtils.isBlank(asc.getConfigMchid()) || StringUtils.isBlank(asc.getConfigLocalpath()))
            {
                log.info("该订单缺乏数据,无法向微信发起退款,订单主键: {}", mktOrder.getPkey());
                throw TofocusException.of(LejiaErrCode.WEIXIN_AGREE_ERROR);
            }
            if (mktOrder.getAscription().equals(13))
            {
            	ThirdPayLineEntity tpl = thirdPayLineDao.byMerOrderId(orderNumber);
                ChinaUmsRefundResponse chinaUmsRefund = chinaUmsRefundManager.chinaUmsRefund(tpl.getMerOrderId(), outRefundNo, refund);
                if(chinaUmsRefund == null || Boolean.FALSE.equals(chinaUmsRefund.isSuccess()))
                {
                    // 退款失败 打上标签 可重新退款 其他流程显示完成
                    or.setAgainRefund(true);
                    orderRefundDao.update(or);
//                    throw TofocusException.of(LejiaErrCode.ZX_PAY_REFUND_ERROR, chinaUmsRefund.getErrMsg());
                }
            }
            else
            {
                if(pl != null)
                {
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
                        or.setAgainRefund(true);
                        orderRefundDao.update(or);
                    }
                }
            }
        }
        if (PayType.NM_MEMBER.equals(mktOrder.getPayType()))
        {
            saasTokenPublicManager
                .saasRefund(mktOrder.getMember(), mktOrder.getFarmer(), mktOrder.getXaszConsumption(), or.getAmtre());
        }
        if (PayType.ORDER_MSD.equals(mktOrder.getPayType()))
        {
            Boolean jdOrder = OrderType.INTEGRAL_JD_ORDER.equals(mktOrder.getOrderType());
            // 退还热力豆
            memberMsdManager.updMsdBalance(mktOrder.getMember(),
                null,
                true,
                or.getAmtre(),
                MsdOperationType.REFUND,
                mktOrder.getCode() + "订单退款",
                mktOrder.getCode(),
                mktOrder.getAscription(),
                jdOrder);
        }
        if (PayType.ORDER_ELECTRONIC_ACCOUNT.equals(mktOrder.getPayType()))
        {
            commManager.updCommRefund(mktOrder.getMember(),
                or.getAmtre(),
                true,
                CommSourceType.COMM_RETURN,
                mktOrder.getCode(),
                mktOrder.getAscription());
            
        }
        
        if (StringUtils.isNotBlank(mktOrder.getBoxPassword()))
        {
            List<MktOrderLine> exec = orderLineDao.select().eq("orderPkey", or.getOrderPkey()).exec();
            goodsBoxManager.incrementSpaceKc(exec);
            //            for (MktOrderLine ol : exec)
            //            {
            //                spaceKcCache.increment(String.valueOf(ol.getSpace()), ol.getNum(), null);
            //                ol.setStatus(OrderStatus.REFUNDED_ORDER);
            //            }
            //            orderLineDao.updateAll(exec);
        }
        handleCardAfterAgreeRefund(or);
        orderManager.printOrder(or.getOrderPkey(), false);
        if (ExpressType.EXPRESS_SF.equals(mktOrder.getExpressType())
            && OrderStatus.REFUNDED_ORDER.equals(mktOrder.getStatus()))
        {
            try
            {
                MktOrderExpress orderExpress = orderExpressDao.byOrder(mktOrder.getPkey());
                orderManager.cancelDeliveryRefund(orderExpress);
            }
            catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        // 库存处理
        refundHandleKc(or.getPkey(), mktOrder.getPkey());
        return true;
    }
    
    // 优惠券退款,手续费计算  配送费清分的时候重新计算
    private void refundHandleCommission(MktOrderRefund or, MktOrder mktOrder, List<MktVendorOrder> listOrder)
    {
        // 手续费处理
        BigDecimal payCommissionRate = Constant.ZxConfig.TJ_COMMISSION_RATE;
        BigDecimal orderAmt = mktOrder.getAmtn();
        if (mktOrder.getRefundAmt() != null) orderAmt = orderAmt.subtract(mktOrder.getRefundAmt());
        BigDecimal payCommission = orderAmt.multiply(payCommissionRate).setScale(2, RoundingMode.HALF_UP);
        System.out.println("orderAmt: " + orderAmt);
        System.out.println("payCommission: " + payCommission);
        BigDecimal postage = mktOrder.getPostage();
        BigDecimal postageSurplus = postage;
        BigDecimal voSum = BigDecimal.ZERO;
        for (MktVendorOrder vo : listOrder)
        {
            voSum = voSum.add(vo.getAmt());
            if(vo.getCommissions() != null)
                voSum = voSum.add(vo.getCommissions());
        }
        BigDecimal voPayCommission = payCommission;
        if(voSum.compareTo(BigDecimal.ZERO) == 0)
        {
            for (MktVendorOrder vo : listOrder)
            {
                vo.setPostage(BigDecimal.ZERO);
                vo.setPayComm(BigDecimal.ZERO);
            }
        }
        else
        {
            for (int i = 0; i < listOrder.size(); i++)
            {
                MktVendorOrder vo = listOrder.get(i);
                BigDecimal voAmt = vo.getAmt();
                if(vo.getCommissions() != null)
                    voAmt = voAmt.add(vo.getCommissions());
                // 该订单应该分到的 手续费 四舍五入
                BigDecimal voPc = voAmt.divide(voSum, 2, RoundingMode.HALF_UP).multiply(payCommission).setScale(2, RoundingMode.HALF_UP);
                System.out.println("voPc: " + voPc);
                if (i != listOrder.size() - 1)
                {
                    voPayCommission = voPayCommission.subtract(voPc);
                }
                else
                {
                    voPc = voPayCommission;
                }
                if(postage != null)
                {
                    if (i != listOrder.size() - 1)
                    {
                        BigDecimal postageAmt = voAmt
                            .divide(voSum, 6, BigDecimal.ROUND_HALF_UP)
                            .multiply(postage)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                        vo.setPostage(postageAmt);
                        postageSurplus = postageSurplus.subtract(postageAmt);
                    }
                    else
                    {
                        vo.setPostage(postageSurplus);
                    }
                }
                vo.setPayComm(voPc);
                if(CommissionType.MERCHANT.equals(vo.getCommissionType()) && voPc.compareTo(BigDecimal.ZERO) > 0)
                {
                    MktVendorWalletLine mvwl = vendorWalletLineDao.byKeyAndFormIdPayComm(vo.getVendor(), mktOrder.getCode());
                    vendorWalletManager.updWalletPayComm(mvwl, voPc);
                }
            }
        }
        vendorOrderDao.updateAll(listOrder);
        
        // 填写优惠券退款金额
        if (mktOrder.getCardAmt() != null && mktOrder.getCard() != null)
        {
            List<MktOrderRefundLine> listRefundLine = orderRefundLineDao.listRefundPkey(or.getPkey());
            for (MktOrderRefundLine orl : listRefundLine)
            {
                MktOrderLine mktOrderLine = orderLineDao.get(orl.getOrderLinePkey());
                MktVendorOrder vo = vendorOrderDao.getOrderLinePkey(orl.getOrderLinePkey());
                if(vo != null)
                {
                    
                    /**
                     * 20251207 by yx 因order_line中优惠后实付金额+优惠分摊的金额 <> vendor_order中的discount_amt不一致
                     * 造成结算账单中，商户应结 与 商户结算不一致问题
                     * 商户应结=商品金额-退款金额-退款优惠金额（取的vendor_order表内discount_refund_amt）
                     * 商户结算=vendor_order中amt字段的合计
                     * 订单下单时的order_line优惠分摊 与 采购时的vendor_order优惠分摊目前是不同算法。原因不明？
                     * 现更改退款发生时，取order_line的商品价格+vendor_order中的优惠分摊来重新计算
                     */
                    // 优惠后的价格 减去 退款金额 等于 银行实际收到的钱
                    //BigDecimal ols = mktOrderLine.getCouponAmt().subtract(mktOrderLine.getRefundAmt());
                    BigDecimal ols = mktOrderLine.getPricen().multiply(new BigDecimal(mktOrderLine.getNum()));
                    if(vo.getDiscountAmt() != null)
                        ols = ols.subtract(vo.getDiscountAmt());
                    ols = ols.subtract(mktOrderLine.getRefundAmt());
                    log.info("退款优惠计算：银行实收{} = 单价{} - 数量{} - 原优惠{} - 退款{}", ols, mktOrderLine.getPrice(),mktOrderLine.getNum(),
                        vo.getDiscountAmt(),mktOrderLine.getRefundAmt());
                    // 结算给商户的金额 加上 佣金抽点 等于 原订单应该收到的钱
                    BigDecimal amt = vo.getAmt();
                    if(vo.getCommissions() != null)
                        amt = amt.add(vo.getCommissions());
                    // 原订单应该收到的钱 减去 银行实际收到的钱 等于 平台需要补的优惠金额
                    BigDecimal da = amt.subtract(ols);
 
                    // 原优惠金额 减去 需要补的优惠金额 等于 这次退掉的 优惠金额
                    if(vo.getDiscountAmt() == null)
                        vo.setDiscountAmt(BigDecimal.ZERO);
                    vo.setDiscountRefundAmt(vo.getDiscountAmt().subtract(da));
                    log.info("退款优惠计算：退回优惠{} = 原优惠金额{} - (商户结算{} - 银行实收{})", vo.getDiscountRefundAmt(),
                        vo.getDiscountAmt(), amt, ols);
                }
            }
        }
    }
    
    private void refundHandleKc(Integer pkey, Integer orderPkey)
    {
        List<MktOrderLine> listOrder = orderLineDao.listOrder(orderPkey);
        Map<Integer, MktOrderRefundLine> map = orderRefundLineDao.mapOrderLinePkey(pkey);
        List<MktGoodsSpace> s = new ArrayList<>();
        List<MktWareLine> addWareLineAll = new ArrayList<>();
        for (MktOrderLine ol : listOrder)
        {
            if (map.containsKey(ol.getPkey()))
            {
                MktOrderRefundLine orl = map.get(ol.getPkey());
                if(ol.getCouponPrice().compareTo(BigDecimal.ZERO) <= 0)
                    continue;
                int intValue = orl.getRefundAmt().divide(ol.getCouponPrice(), 0, RoundingMode.DOWN).intValue();
                if (intValue > 0)
                {
                    MktGoodsSpace gs = goodsSpaceDao.get(ol.getSpace().intValue());
                    gs.setKcNum(gs.getKcNum() + intValue);
                    s.add(gs);
                    
                    MktGoods mktGoods = goodsDao.get(gs.getGoods());
                    MktWareLine add = new MktWareLine();
                    add.setWareType(WareType.REFUND);
                    add.setGoods(mktGoods.getPkey());
                    add.setGoodsName(mktGoods.getTitle());
                    add.setSpace(gs.getPkey());
                    add.setSpaceName(gs.getSpace());
                    add.setNum(intValue);
                    add.setActualNum(gs.getKcNum());
                    add.setAscription(CurrentSession.ascriptionPkey());
                    addWareLineAll.add(add);
                }
            }
        }
        wareLineDao.addAll(addWareLineAll);
        goodsSpaceDao.updateAll(s);
        List<MktSpaceKc> kcList = BeanUtil.beanListFrom(MktSpaceKc.class, s);
        spaceKcDao.putAll(kcList);
        kcList.forEach(sk -> spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum())));
    }
    
    private void handleCardAfterAgreeRefund(MktOrderRefund or)
    {
        if (or.getRefundCard() != null)
        {
            MktMemberCard memberCard = memberCardDao.get(or.getRefundCard());
            if (memberCard != null)
            {
                if (memberCard.getCard() != null)
                {
                    MktCard card = cardDao.getCard(memberCard.getCard());
                    if (card != null)
                    {
                        memberCard.setInvalid(card.getInvalid());
                        memberCard.setUserFarmer(card.getUserFarmer());
                        card.setUsedNum(card.getUsedNum() - 1);
                        cardDao.put(card);
                        
                        MktActivityCoupon activityCoupon =
                            activityCouponDao.byActivityCoupon(CouponType.CARD, memberCard.getCard());
                        if (activityCoupon != null)
                        {
                            MktActivity mktActivity = activityDao.get(activityCoupon.getActivity());
                            if (mktActivity != null)
                            {
                                Integer useNum = mktActivity.getUseNum();
                                if (useNum == null) useNum = 0;
                                useNum -= 1;
                                activityDao.updUseNum(mktActivity.getPkey(), useNum);
                            }
                        }
                    }
                }
                memberCard.setOrderId(null);
                memberCard.setUserTime(null);
                if (DateUtil.compareDate(memberCard.getEndDate(), DateUtil.atStartOfToday()) < 0)
                    memberCard.setStatus(CardStatus.EXPIRED);
                else
                    memberCard.setStatus(CardStatus.UNUSED);
                memberCardDao.put(memberCard);
            }
        }
        if (or.getRefundCardPostage() != null)
        {
            MktMemberCard memberCard = memberCardDao.get(or.getRefundCardPostage());
            if (memberCard != null)
            {
                if (memberCard.getCard() != null)
                {
                    MktCard card = cardDao.getCard(memberCard.getCard());
                    if (card != null)
                    {
                        memberCard.setInvalid(card.getInvalid());
                        memberCard.setUserFarmer(card.getUserFarmer());
                        card.setUsedNum(card.getUsedNum() - 1);
                        cardDao.put(card);
                        
                        MktActivityCoupon activityCoupon =
                            activityCouponDao.byActivityCoupon(CouponType.CARD, memberCard.getCard());
                        if (activityCoupon != null)
                        {
                            MktActivity mktActivity = activityDao.get(activityCoupon.getActivity());
                            if (mktActivity != null)
                            {
                                Integer useNum = mktActivity.getUseNum();
                                if (useNum == null) useNum = 0;
                                useNum -= 1;
                                activityDao.updUseNum(mktActivity.getPkey(), useNum);
                            }
                        }
                    }
                }
                memberCard.setOrderId(null);
                memberCard.setUserTime(null);
                if (DateUtil.compareDate(memberCard.getEndDate(), DateUtil.atStartOfToday()) < 0)
                    memberCard.setStatus(CardStatus.EXPIRED);
                else
                    memberCard.setStatus(CardStatus.UNUSED);
                memberCardDao.put(memberCard);
            }
        }
    }
    
    // 拒绝退款
    @Transactional(rollbackFor = Exception.class)
    public Boolean refuseRefund(Integer pkey, String delDesc)
    {
        MktOrderRefund or = orderRefundDao.get(pkey);
        or.setDelDesc(delDesc);
        or.setDelTime(new Date());
        or.setStatus(RefundStatus.REFUND_REFUSE);
        orderRefundDao.update(or);
        MktOrder mktOrder = orderDao.get(or.getOrderPkey());
        //        BigDecimal orderRefundAmt = mktOrder.getRefundAmt();
        //        if(orderRefundAmt == null)
        //            orderRefundAmt = BigDecimal.ZERO;
        //        mktOrder.setRefundAmt(mktOrder.getRefundAmt().subtract(or.getAmtre()));
        
        List<MktOrderLine> listOrder = orderLineDao.listOrder(or.getOrderPkey());
        Map<Integer, MktOrderRefundLine> map = orderRefundLineDao.mapOrderLinePkey(pkey);
        
        List<MktVendorOrder> vendorOrderList = vendorOrderDao.listOrder(mktOrder.getPkey());
        Map<Integer, MktVendorOrder> vendorMap = new HashMap<>();
        vendorOrderList.forEach(e -> vendorMap.put(e.getOrderLinePkey(), e));
        List<MktVendorOrder> updVendorOrderList = new ArrayList<>();
        for (MktOrderLine ol : listOrder)
        {
            //            BigDecimal divide = new BigDecimal("1");
            if (map.containsKey(ol.getPkey()))
            {
                MktOrderRefundLine orl = map.get(ol.getPkey());
                ol.setRefundAmt(ol.getRefundAmt().subtract(orl.getRefundAmt()));
                ol.setRefundNum(ol.getRefundNum() - orl.getRefundNum());
                
                if (vendorMap.containsKey(ol.getPkey()))
                {
                    MktVendorOrder vo = vendorMap.get(ol.getPkey());
                    vo.setRefundStatus(RefundStatus.REFUND_REFUSE);
                    vo = vendorOrderRefundAmt(ol.getRefundAmt(), ol.getCouponPrice(), ol.getCouponAmt(), vo, mktOrder.getCode(), mktOrder.getCreatedTime());
                    updVendorOrderList.add(vo);
                }
            }
        }
        
        vendorOrderDao.updateAll(updVendorOrderList);
        //        orderRefundLineDao.removeRefundPkey(pkey);
        orderLineDao.updateAll(listOrder);
        //        orderDao.update(mktOrder);
        return true;
    }
    
    public void rollbackRefundJd(Long jdCode, String thirdApplyId, RefundStatus status)
    {
        JdOrderCorrelation joc = jdOrderCorrelationDao.getByJdCode(jdCode);
        MktOrderRefund or = orderRefundDao.byJdOrderCodeHandle(joc.getOrderCode(), thirdApplyId);
        if (status == null)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "售后状态不能为空");
        switch (status)
        {
            case JD_APPROVAL_REJECTED:
                or.setDelDesc("京东售后审核不通过");
                break;
            case JD_PROCESSED_FAILED:
                or.setDelDesc("京东售后质检处理不通过");
                break;
            case JD_CANCELED:
                or.setDelDesc("京东售后取消");
                break;
            default:
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "售后状态不支持回滚操作");
        }
        or.setDelTime(new Date());
        or.setStatus(status);
        or.setOutProcessing(false);
        orderRefundDao.update(or);
        
        List<MktOrderLine> listOrder = orderLineDao.listOrder(or.getOrderPkey());
        Map<Integer, MktOrderRefundLine> map = orderRefundLineDao.mapOrderLinePkey(or.getPkey());
        
        for (MktOrderLine ol : listOrder)
        {
            if (map.containsKey(ol.getPkey()))
            {
                MktOrderRefundLine orl = map.get(ol.getPkey());
                ol.setRefundAmt(ol.getRefundAmt().subtract(orl.getRefundAmt()));
                ol.setRefundNum(ol.getRefundNum() - orl.getRefundNum());
            }
        }
        orderLineDao.updateAll(listOrder);
    }
    
    // 预修改退款金额
    public PreUpdRefundOrderInfo preUpdRefundLine(RefundUpdOnInfo info)
    {
        MktOrderRefund orderRefund = orderRefundDao.get(info.getRefundPkey());
        if (!RefundStatus.REFUND_APPLYING.equals(orderRefund.getStatus()))
            throw TofocusException.of(LejiaErrCode.REFUND_STATUS_ERROR);
        
        handleUpdRefund(info, orderRefund, null, null, null);
        
        PreUpdRefundOrderInfo preUpdRefund = new PreUpdRefundOrderInfo();
        preUpdRefund.setRefundGoodsAmt(orderRefund.getRefundGoodsAmt());
        preUpdRefund.setRefundPostage(orderRefund.getRefundPostage());
        preUpdRefund.setRefundCard(orderRefund.getRefundCard());
        preUpdRefund.setRefundAmt(orderRefund.getAmtre());
        preUpdRefund.setRefundCardPostage(orderRefund.getRefundCardPostage());
        preUpdRefund.setRefundPoint(orderRefund.getRefundPoint());
        if (preUpdRefund.getRefundCard() != null)
        {
            MktMemberCard memberCard = memberCardDao.get(preUpdRefund.getRefundCard());
            if (memberCard != null && memberCard.getCard() != null)
            {
                MktCard card = cardDao.getCard(memberCard.getCard());
                if (card != null) preUpdRefund.setRefundCardTitle(card.getTitle());
            }
        }
        if (preUpdRefund.getRefundCardPostage() != null)
        {
            MktMemberCard memberCard = memberCardDao.get(preUpdRefund.getRefundCardPostage());
            if (memberCard != null && memberCard.getCard() != null)
            {
                MktCard card = cardDao.getCard(memberCard.getCard());
                if (card != null) preUpdRefund.setRefundCardPostageTitle(card.getTitle());
            }
        }
        return preUpdRefund;
    }
    
    // 修改退款金额
    @Transactional(rollbackFor = Exception.class)
    public Boolean updRefundLine(RefundUpdOnInfo info)
    {
        MktOrderRefund orderRefund = orderRefundDao.get(info.getRefundPkey());
        if (!RefundStatus.REFUND_APPLYING.equals(orderRefund.getStatus()))
            throw TofocusException.of(LejiaErrCode.REFUND_STATUS_ERROR);
        
        List<MktOrderRefundLine> updORL = new ArrayList<>();
        List<MktOrderLine> updOrderLines = new ArrayList<>();
        List<MktVendorOrder> updVendorOrderList = new ArrayList<>();
        
        handleUpdRefund(info, orderRefund, updORL, updOrderLines, updVendorOrderList);
        
        orderRefundDao.update(orderRefund);
        orderRefundLineDao.updateAll(updORL);
        orderLineDao.updateAll(updOrderLines);
        //        orderDao.update(mktOrder);
        vendorOrderDao.updateAll(updVendorOrderList);
        
        agreeRefund(orderRefund, "");
        return true;
    }
    
    private void handleUpdRefund(RefundUpdOnInfo info, MktOrderRefund orderRefund, List<MktOrderRefundLine> updORL,
        List<MktOrderLine> updOrderLines, List<MktVendorOrder> updVendorOrderList)
    {
//        MktOrder mktOrder = orderDao.get(orderRefund.getOrderPkey());
        //        BigDecimal orderRefundAmt = mktOrder.getRefundAmt();
        //        if(orderRefundAmt == null)
        //            orderRefundAmt = BigDecimal.ZERO;
        // 原先的退款金额先加回去
        //        orderRefundAmt = orderRefundAmt.subtract(orderRefund.getAmtre());
        Map<Integer, MktOrderRefundLine> map = orderRefundLineDao.mapRefundPkey(info.getRefundPkey());
        BigDecimal refundGoodsAmt = BigDecimal.ZERO;
        
        List<MktVendorOrder> vendorOrderList = vendorOrderDao.listOrder(orderRefund.getOrderPkey());
        Map<Integer, MktVendorOrder> vendorMap = new HashMap<>();
        vendorOrderList.forEach(e -> vendorMap.put(e.getOrderLinePkey(), e));
        
        MktOrder mktOrder = orderDao.get(orderRefund.getOrderPkey());
        List<MktOrderLine> listOrder = orderLineDao.listOrder(mktOrder.getPkey());
        Map<Integer, MktOrderLine> orderLineMap = new HashMap<>();
        BigDecimal goodsAmt = BigDecimal.ZERO;
        for (MktOrderLine ol : listOrder)
        {
            goodsAmt = goodsAmt.add(ol.getPricen().multiply(new BigDecimal(ol.getNum())));
            orderLineMap.put(ol.getPkey(), ol);
        }
        Integer refundPoint = 0;
        for (RefundOnLine rl : info.getLines())
        {
            // RefundOnLine pkey 对应的是 MktOrderRefundLine 的pkey
            if (map.containsKey(rl.getPkey()))
            {
                //                BigDecimal divide = new BigDecimal("1");
                MktOrderRefundLine mktOrderRefundLine = map.get(rl.getPkey());
                MktOrderLine orderLine = orderLineMap.get(mktOrderRefundLine.getOrderLinePkey());
                if (orderLine == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER_DETAIL);
                BigDecimal orderLineRefundAmt = orderLine.getRefundAmt();
                BigDecimal yOrderRefundLineRefundAmt = mktOrderRefundLine.getRefundAmt();
                if (orderLineRefundAmt != null)
                {
                    orderLineRefundAmt = orderLineRefundAmt.subtract(yOrderRefundLineRefundAmt);
                }
                else
                    orderLineRefundAmt = BigDecimal.ZERO;
                BigDecimal couponAmt = orderLine.getCouponAmt();
                if (couponAmt == null) couponAmt = orderLine.getPricen().multiply(new BigDecimal(orderLine.getNum()));
                BigDecimal remainAmt = couponAmt.subtract(orderLineRefundAmt);
                if (rl.getRefundAmt().compareTo(remainAmt) > 0)
                    throw TofocusException.of(LejiaErrCode.REFUND_LINE_AMT_ERROR);
                orderLine.setRefundAmt(orderLineRefundAmt.add(rl.getRefundAmt()));
                if(orderLine.getRefundAmt().compareTo(couponAmt) == 0 && orderLine.getPoint() != null)
                {
                    mktOrderRefundLine.setRefundPoint(orderLine.getPoint() * orderLine.getNum());
                    refundPoint += (orderLine.getPoint() * orderLine.getNum());
                }
                else
                {
                    mktOrderRefundLine.setRefundPoint(0);
                }
                if (updVendorOrderList != null && vendorMap.containsKey(orderLine.getPkey()))
                {
                    // 原退款金额 除以 原总价 获得原先比例
//                    MktVendorOrder vo = vendorMap.get(orderLine.getPkey());
//                    vo = vendorOrderRefundAmt(orderLine.getRefundAmt(),
//                        orderLine.getCouponAmt(),
//                        vo,
//                        orderRefund.getCode());
//                    updVendorOrderList.add(vo);
                }
                
                mktOrderRefundLine.setRefundAmt(rl.getRefundAmt());
                if (updORL != null) updORL.add(mktOrderRefundLine);
                if (updOrderLines != null) updOrderLines.add(orderLine);
            }
            refundGoodsAmt = refundGoodsAmt.add(rl.getRefundAmt());
        }
        
        // 查询已发起和已完成的退款商品金额，判断是否退完
        BigDecimal haveRefundedGoodsAmt = orderRefundDao.aggRefundGoodsAmt(mktOrder.getPkey(),
            Lists.newArrayList(RefundStatus.REFUND_APPLYING, RefundStatus.REFUND_AGREE, RefundStatus.REFUND_FINAL),
            info.getRefundPkey());
        BigDecimal allowedRefundGoodsAmt = goodsAmt.subtract(mktOrder.getCardAmt()).subtract(haveRefundedGoodsAmt);
        if (allowedRefundGoodsAmt.compareTo(refundGoodsAmt) < 0)
            throw TofocusException.of(LejiaErrCode.REFUND_LINE_AMT_ERROR, "退款商品总价大于剩余允许退款商品总价");
        orderRefund.setRefundGoodsAmt(refundGoodsAmt);
        BigDecimal refundPostage = BigDecimal.ZERO;
        Integer refundCard = null;
        Integer refundCardPostage = null;
        if (allowedRefundGoodsAmt.compareTo(refundGoodsAmt) == 0)
        {
            if (OrderStatus.DELIVERED_ORDER.equals(mktOrder.getStatus())
                || OrderStatus.PAYING_ORDER.equals(mktOrder.getStatus())) 
                refundPostage = mktOrder.getPostage();
            refundCard = mktOrder.getCard();
            refundCardPostage = mktOrder.getCardPostage();
        }
        orderRefund.setRefundPostage(refundPostage);
        orderRefund.setRefundCard(refundCard);
        orderRefund.setRefundCardPostage(refundCardPostage);
        orderRefund.setAmtre(refundGoodsAmt.add(refundPostage));
        if(PayType.MSD_COMBINATION.equals(mktOrder.getPayType()) || PayType.ELECTRONIC_ACCOUNT_COMBINATION.equals(mktOrder.getPayType()))
        {
            BigDecimal otherAmt = mktOrder.getOtherAmt();
            if(otherAmt == null)
                otherAmt = BigDecimal.ZERO;
            BigDecimal refundOtherAmt = mktOrder.getRefundOtherAmt();
            if(refundOtherAmt == null)
                refundOtherAmt = BigDecimal.ZERO;
            BigDecimal subtract = otherAmt.subtract(refundOtherAmt).subtract(orderRefund.getAmtre());
            if(subtract.compareTo(BigDecimal.ZERO) < 0)
            {
                BigDecimal s = otherAmt.subtract(refundOtherAmt);
                orderRefund.setRefundWeixinAmt(orderRefund.getAmtre().subtract(s));
                orderRefund.setRefundOtherAmt(s);
            }
            else
            {
                orderRefund.setRefundWeixinAmt(BigDecimal.ZERO);
                orderRefund.setRefundOtherAmt(orderRefund.getAmtre());
            }
        }
        if(PayType.ORDER_MSD.equals(mktOrder.getPayType()) || PayType.ORDER_ELECTRONIC_ACCOUNT.equals(mktOrder.getPayType()))
        {
            orderRefund.setRefundWeixinAmt(BigDecimal.ZERO);
            orderRefund.setRefundOtherAmt(orderRefund.getAmtre());
        }
        orderRefund.setRefundPoint(refundPoint);
        //        mktOrder.setRefundAmt(orderRefundAmt.add(refundAmt));
    }
    
    // 已经有退款申请,再有采购. 调一下这个接口
    @Transactional(rollbackFor = Exception.class)
    public void vendorOrderRefund(Integer pkey)
    {
        List<MktOrderRefund> listOrderRefund = orderRefundDao.listOrderPkey(pkey);
        if (listOrderRefund == null || listOrderRefund.isEmpty()) return;
        for (MktOrderRefund orderRefund : listOrderRefund)
        {
            List<MktOrderRefundLine> list = orderRefundLineDao.listRefundPkey(orderRefund.getPkey());
            RefundUpdOnInfo info = new RefundUpdOnInfo();
            info.setRefundPkey(orderRefund.getPkey());
            List<RefundOnLine> lines = new ArrayList<>();
            list.forEach(e -> {
                RefundOnLine rol = new RefundOnLine();
                rol.setPkey(e.getPkey());
                rol.setRefundAmt(e.getRefundAmt());
                rol.setNum(e.getRefundNum());
                lines.add(rol);
            });
            info.setLines(lines);
            updRefundLine(info);
        }
    }
    
    // ----------------------------web端 市场退款订单页面接口
    public OrderRefundOnInfo queryOrderRefund(int page, int pagesize, String code, List<RefundStatus> status,
        String startDate, String endDate)
    {
        if (StringUtils.isNoneBlank(startDate))
            startDate = startDate + " 00:00:00";
        if (StringUtils.isNoneBlank(endDate))
            endDate = endDate + " 23:59:59";
        
        OrderRefundOnInfo res = aggRefundAmtAndNum(code,
            status,
            startDate,
            endDate,
            CurrentSession.marketPkey(),
            CurrentSession.ascriptionPkey());
        
        PageResult<OrderRefundOnPage> onPage = queryOrderRefund(page,
            pagesize,
            code,
            status,
            startDate,
            endDate,
            CurrentSession.marketPkey(),
            CurrentSession.ascriptionPkey());
        for (OrderRefundOnPage d : onPage.getContent())
        {
            if (d.getRefundPoint() == null)
                d.setRefundPoint(0);
            if (d.getAgainRefund() == null)
                d.setAgainRefund(false);
        }
        res.setOnPage(onPage);
        return res;
    }
    
    private OrderRefundOnInfo aggRefundAmtAndNum(String code, List<RefundStatus> status, String startDate,
        String endDate, String farmer, Integer ascription)
    {
        List<OrderRefundOnInfo> aggList = orderRefundDao.joinSelect()
            .in(MktOrderRefund.F.status, status)
            .ge(MktOrderRefund.F.createdTime, startDate)
            .le(MktOrderRefund.F.createdTime, endDate)
            .eq(MktOrderRefund.F.farmer, farmer)
            .eq(MktOrderRefund.F.ascription, ascription)
            .like(MktOrderRefund.F.code, code)
            .count(MktOrderRefund.F.pkey, "num")
            .sum(MktOrderRefund.F.amtre, "refundAmt")
            .sum(MktOrderRefund.F.refundPoint, "refundPoint")
            .join(MktOrder.class, MktOrderRefund.F.orderPkey, MktOrder.F.pkey)
            .notEq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .endJoin()
            .exec(OrderRefundOnInfo.class);
        
        OrderRefundOnInfo res = null;
        if (aggList.isEmpty())
        {
            res = new OrderRefundOnInfo();
            res.setNum(0);
            res.setRefundAmt(BigDecimal.ZERO);
            res.setRefundPoint(0);
        }
        else
        {
            res = aggList.get(0);
        }
        return res;
    }
    
    private PageResult<OrderRefundOnPage> queryOrderRefund(int page, int pagesize, String code,
        List<RefundStatus> status, String startDate, String endDate, String farmer, Integer ascription)
    {
        return orderRefundDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .as(MktOrderRefund.F.pkey)
            .as(MktOrderRefund.F.code)
            .as(MktOrderRefund.F.goodsAmt)
            .as(MktOrderRefund.F.oldPostage)
            .as(MktOrderRefund.F.postage)
            .as(MktOrderRefund.F.preferentialAmt)
            .as(MktOrderRefund.F.preferentialPostageAmt)
            .as(MktOrderRefund.F.refundGoodsAmt)
            .as(MktOrderRefund.F.refundPostage)
            .as(MktOrderRefund.F.amtall)
            .as(MktOrderRefund.F.amtre)
            .as(MktOrderRefund.F.refundWeixinAmt)
            .as(MktOrderRefund.F.refundPoint)
            .as(MktOrderRefund.F.status)
            .as(MktOrderRefund.F.jdType)
            .as(MktOrderRefund.F.reason)
            .as(MktOrderRefund.F.createdTime)
            .as(MktOrderRefund.F.againRefund)
            .in(MktOrderRefund.F.status, status)
            .ge(MktOrderRefund.F.createdTime, startDate)
            .le(MktOrderRefund.F.createdTime, endDate)
            .eq(MktOrderRefund.F.farmer, farmer)
            .eq(MktOrderRefund.F.ascription, ascription)
            .like(MktOrderRefund.F.code, code)
            .join(MktOrder.class, MktOrderRefund.F.orderPkey, MktOrder.F.pkey)
            .notEq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .endJoin()
            .sort(MktOrder.F.createdTime)
            .sort(MktOrder.F.pkey)
            .exec(OrderRefundOnPage.class);
    }
    
    public RefundOrderDetails getRefundOrder(Integer pkey)
    {
        MktOrderRefund or = orderRefundDao.get(pkey);
        MktAppOrderDTO order = appOrderManager.loadInitOrder(or.getOrderPkey(), false);
        RefundOrderDetails res = BeanUtil.beanFrom(RefundOrderDetails.class, order);
        res.setRefundPkey(pkey);
        res.setRefundAmt(order.getRefundAmt());
        res.setCurrentRefundAmt(or.getAmtre());
        res.setRefundGoodsAmt(or.getRefundGoodsAmt());
        res.setRefundPostage(or.getRefundPostage());
        res.setRefundCard(or.getRefundCard());
        res.setRefundCardPostage(or.getRefundCardPostage());
        if (order.getOldPostage() != null) res.setPostage(order.getOldPostage());
        if (or.getRefundCard() != null)
        {
            MktMemberCard memberCard = memberCardDao.get(or.getRefundCard());
            if (memberCard != null && memberCard.getCard() != null)
            {
                MktCard card = cardDao.getCard(memberCard.getCard());
                if (card != null) res.setRefundCardTitle(card.getTitle());
            }
        }
        if (or.getRefundCardPostage() != null)
        {
            MktMemberCard memberCard = memberCardDao.get(or.getRefundCardPostage());
            if (memberCard != null && memberCard.getCard() != null)
            {
                MktCard card = cardDao.getCard(memberCard.getCard());
                if (card != null) res.setRefundCardPostageTitle(card.getTitle());
            }
        }
        
        res.setReason(or.getReason());
        res.setDescribe(or.getDescribe());
        res.setRefundStatus(or.getStatus());
        res.setRefundStatusName(or.getStatus().getName());
        res.setRefundPhoto(or.getPhoto());
        res.setDelDesc(or.getDelDesc());
        res.setOrderTypeName(res.getOrderType().getName());
        List<MktOrderRefundLine> list = orderRefundLineDao.listRefundPkey(pkey);
        Map<Integer, List<VendorRefundOrderOnList>> map = new HashMap<>();
        Integer resRefundPoint = res.getRefundPoint();
        if(resRefundPoint == null)
            resRefundPoint = 0;
        for (MktOrderRefundLine orl : list)
        {
            MktOrderLine orderLine = orderLineDao.get(orl.getOrderLinePkey());
            VendorRefundOrderOnList vro = new VendorRefundOrderOnList();
            vro.setPkey(orl.getPkey());
            vro.setNum(orl.getRefundNum());
            MktGoodsSpace goodsSpace = goodsSpaceDao.get(orderLine.getSpace().intValue());
            if (goodsSpace != null)
            {
                vro.setSpaceName(goodsSpace.getSpace());
                vro.setWeight(goodsSpace.getWeight());
                vro.setPhoto(goodsSpace.getPhoto1());
            }
            vro.setRefundAmt(orl.getRefundAmt());
            vro.setRefundPoint(orl.getRefundPoint());
            if (vro.getRefundPoint() == null && orderLine.getPoint() != null)
            {
                vro.setRefundPoint(orl.getRefundNum() * orderLine.getPoint());
            }
            if(orl.getRefundPoint() != null)
            {
                resRefundPoint += vro.getRefundPoint();
            }
            MktOrderLine mktOrderLine = orderLineDao.get(orl.getOrderLinePkey());
            handlePrice4RefundOrder(vro, mktOrderLine);
            MktGoods goods = goodsDao.get(orl.getGoods().intValue());
            if (goods != null)
            {
                vro.setGoodsName(goods.getTitle());
                if (StringUtils.isBlank(vro.getPhoto()))
                {
                    if (goods.getPhoto1() != null && !goods.getPhoto1().isEmpty())
                        vro.setPhoto(goods.getPhoto1().get(0));
                    else if (StringUtils.isNoneBlank(goods.getPhoto2()))
                    {
                        vro.setPhoto(goods.getPhoto2());
                    }
                    else
                        vro.setPhoto(goods.getPhoto3());
                }
            }
            MktVendorOrder vo = vendorOrderDao.getOrderLinePkey(orl.getOrderLinePkey());
            int voKey = -5;
            if (vo != null) voKey = vo.getVendor();
            if (!map.containsKey(voKey))
            {
                List<VendorRefundOrderOnList> v = new ArrayList<>();
                map.put(voKey, v);
            }
            map.get(voKey).add(vro);
        }
        if(!RefundStatus.REFUND_FINAL.equals(or.getStatus()))
        {
            res.setRefundPoint(resRefundPoint);
        }
        
        List<VendorRefundOrderOnInfo> refundOrder = new ArrayList<>();
        for (Map.Entry<Integer, List<VendorRefundOrderOnList>> entry : map.entrySet())
        {
            Integer key = entry.getKey();
            VendorRefundOrderOnInfo vro = new VendorRefundOrderOnInfo();
            if (-5 == key.intValue())
            {
                vro.setName("");
                vro.setBooth("");
            }
            else
            {
                MktVendor vendor = vendorDao.get(key);
                if (vendor != null)
                {
                    vro.setName(vendor.getDisplayName());
                    vro.setBooth(vendor.getBooth());
                }
            }
            Integer num = 0;
            BigDecimal sumAmt = BigDecimal.ZERO;
            Integer refundPoint = 0;
            for (VendorRefundOrderOnList ro : entry.getValue())
            {
                if (ro.getNum() != null) num = num + ro.getNum();
                if (ro.getRefundAmt() != null) sumAmt = sumAmt.add(ro.getRefundAmt());
                if (ro.getRefundPoint() != null) refundPoint += ro.getRefundPoint();
            }
            vro.setNum(num);
            vro.setSumAmt(sumAmt);
            vro.setRefundPoint(refundPoint);
            vro.setList(entry.getValue());
            refundOrder.add(vro);
        }
        res.setRefundOrder(refundOrder);
        if (res.getOrderExpressInfo() == null)
        {
            MktOrderDesc od = descDao.get(res.getPkey());
            if (od != null)
            {
                MktOrderExpressInfo ori = new MktOrderExpressInfo();
                ori.setExpressCompanyName(od.getLogistics());
                ori.setExpressNo(od.getKdCode());
                ori.setPickupTime(od.getFhTime());
                if (StringUtils.isNotBlank(ori.getExpressCompanyName())) ori.setStatusName("已下单");
                res.setOrderExpressInfo(ori);
            }
        }
        return res;
    }
    
    // ----------------------------小程序端 市场退款订单页面接口
    public PageResult<AppRefundOrderOnPage> queryAppRefundOrder(int page, int pagesize, Integer member,
        Integer ascription, Integer orderPkey)
    {
        PageResult<MktOrderRefund> pageResult =
            orderRefundDao.queryAppOrderRefund(page, pagesize, member, ascription, orderPkey);
        PageResult<AppRefundOrderOnPage> res = BeanUtil.beanPageFrom(AppRefundOrderOnPage.class, pageResult);
        List<AppRefundOrderOnPage> content = new ArrayList<>();
        
        for (MktOrderRefund or : pageResult.getContent())
        {
            AppRefundOrderOnPage dto = new AppRefundOrderOnPage();
            dto.setCode(or.getCode());
            dto.setOrderPkey(or.getOrderPkey());
            dto.setRefundAmt(or.getAmtre());
            dto.setCreatedTime(or.getCreatedTime());
            dto.setReason(or.getReason());
            dto.setDescribe(or.getDescribe());
            dto.setRefundPhoto(or.getPhoto());
            dto.setStatus(or.getStatus());
            dto.setStatusName(or.getStatus().getName());
            dto.setPkey(or.getPkey());
            SysFarmer farmer = farmerDao.get(or.getFarmer());
            if (farmer != null) dto.setTel(farmer.getTel());
            List<MktOrderRefundLine> orlList = orderRefundLineDao.listRefundPkey(or.getPkey());
            List<VendorRefundOrderOnList> list = new ArrayList<>();
            Integer num = 0;
            BigDecimal sumAmt = BigDecimal.ZERO;
            for (MktOrderRefundLine orl : orlList)
            {
                if (orl.getRefundNum() != null) num = num + orl.getRefundNum();
                if (orl.getRefundAmt() != null) sumAmt = sumAmt.add(orl.getRefundAmt());
                VendorRefundOrderOnList vro = new VendorRefundOrderOnList();
                MktOrderLine orderLine = orderLineDao.get(orl.getOrderLinePkey());
                vro.setPkey(orl.getPkey());
                vro.setRefundAmt(orl.getRefundAmt());
                handlePrice4RefundOrder(vro, orderLine);
                vro.setNum(orderLine.getNum());
                if (orderLine != null)
                {
                    vro.setGoodsName(orderLine.getGoodsName());
                }
                MktGoodsSpace goodsSpace = goodsSpaceDao.get(orderLine.getSpace().intValue());
                if (goodsSpace != null)
                {
                    vro.setSpaceName(goodsSpace.getSpace());
                    vro.setWeight(goodsSpace.getWeight());
                    vro.setPhoto(goodsSpace.getPhoto1());
                }
                else
                {
                    JdGoods jg = jdGoodsDao.get(orderLine.getSpace());
                    JdGoodsSpace jgs = jdGoodsSpaceDao.get(orderLine.getSpace());
                    vro.setSpaceName(jgs.getSpaceName());
                    BigDecimal weight = BigDecimal.ZERO;
                    if(StringUtils.isNotBlank(jg.getWeight()))
                        weight = new BigDecimal(jg.getWeight());
                    vro.setWeight(weight);
                    if (jg.getPhoto1() != null && !jg.getPhoto1().isEmpty())
                        vro.setPhoto(jg.getPhoto1().get(0));
                }
                if (StringUtils.isBlank(vro.getPhoto()))
                {
                    MktGoods goods = goodsDao.get(orl.getGoods().intValue());
                    if (goods != null)
                    {
                        if (goods.getPhoto1() != null && !goods.getPhoto1().isEmpty())
                            vro.setPhoto(goods.getPhoto1().get(0));
                        else if (StringUtils.isNoneBlank(goods.getPhoto2()))
                        {
                            vro.setPhoto(goods.getPhoto2());
                        }
                        else
                            vro.setPhoto(goods.getPhoto3());
                    }
                }
                list.add(vro);
            }
            dto.setNum(num);
            dto.setSumAmt(sumAmt);
            dto.setList(list);
            MktOrder mktOrder = orderDao.get(or.getOrderPkey());
            if (mktOrder != null) dto.setDistributionType(mktOrder.getDistributionType());
            content.add(dto);
        }
        res.setContent(content);
        return res;
    }
    
    public AppRefundOrderOnInfo getAppRefundOrder(Integer refundPkey, Integer orderPkey)
    {
        MktOrderRefund or = null;
        if (refundPkey != null)
        {
            or = orderRefundDao.get(refundPkey);
        }
        if (orderPkey != null)
        {
            or = orderRefundDao.getOrderPkey(orderPkey);
        }
        AppRefundOrderOnInfo dto = new AppRefundOrderOnInfo();
        dto.setCode(or.getCode());
      
        dto.setRefundGoodsAmt(or.getRefundGoodsAmt());
        dto.setRefundPostage(or.getRefundPostage());
        dto.setHasRefundCard(or.getRefundCard() != null);
        dto.setHasRefundCardPostage(or.getRefundCardPostage() != null);
        dto.setRefundAmt(or.getAmtre());
        dto.setRefundWeixinAmt(or.getRefundWeixinAmt());
        dto.setRefundOtherAmt(or.getRefundOtherAmt());
        dto.setRefundMsdAmt(BigDecimal.ZERO);
        dto.setRefundElectronicAccountAmt(BigDecimal.ZERO);
        MktOrder mktOrder = orderDao.get(or.getOrderPkey());
        dto.setPayType(mktOrder.getPayType());
//        dto.setRefundOtherTypeName("");
        if(PayType.ELECTRONIC_ACCOUNT_COMBINATION.equals(mktOrder.getPayType()))
        {
//            dto.setRefundOtherTypeName("I DO支付");
            dto.setRefundElectronicAccountAmt(dto.getRefundOtherAmt());
        }
        if(PayType.MSD_COMBINATION.equals(mktOrder.getPayType()))
        {
//            dto.setRefundOtherTypeName("热力豆支付");
            dto.setRefundMsdAmt(dto.getRefundOtherAmt());
        }
        dto.setRefundPoint(or.getRefundPoint());
        dto.setOrderPkey(or.getOrderPkey());
        dto.setCreatedTime(or.getCreatedTime());
        dto.setReason(or.getReason());
        dto.setDescribe(or.getDescribe());
        dto.setRefundPhoto(or.getPhoto());
        dto.setStatus(or.getStatus());
        dto.setStatusName(or.getStatus().getName());
        dto.setDelDesc(or.getDelDesc());
        dto.setPkey(or.getPkey());
        dto.setIsJd(or.getIsJd());
        dto.setJdType(RefundJdType.RETURN_MONEY);
        dto.setJdTypeName("退钱");
        if(or.getJdType() != null)
        {
            dto.setJdType(or.getJdType());
            dto.setJdTypeName(or.getJdType().getName());
        }
        dto.setOutProcessing(or.getOutProcessing());
        SysFarmer farmer = farmerDao.get(or.getFarmer());
        if (farmer != null)
        {
            dto.setTel(farmer.getTel());
        }
        if(!RefundJdType.RETURN_MONEY.equals(or.getJdType()))
        {
            MktOrderRefundExtend ore = orderRefundExtendDao.byRefundPkey(or.getPkey());   
            if(ore != null && CourierType.SELF_MAILING.equals(ore.getCourierType()))
            {
                JdOrderCorrelation joc = jdOrderCorrelationDao.getByCode(or.getCode());
                List<AfsAddressInfoOpenResp> list = jdVOPAfsManager.queryAfsAddressInfos(joc.getJdCode(), or.getOutRefundNo(), "天津国成VOP");
                log.info("京东商家地址list: {}", JsonUtil.toString(list, true));
                if(list != null && !list.isEmpty())
                {
                    AfsAddressInfoOpenResp item = list.get(0);
                    String pro = jdAddressDao.getNameById(item.getAfterServiceProvince().longValue());
                    String city = jdAddressDao.getNameById(item.getAfterServiceCity().longValue());
                    String area = jdAddressDao.getNameById(item.getAfterServiceCounty().longValue());
                    String town = jdAddressDao.getNameById(item.getAfterServiceVillage().longValue());
                    String afterServiceAddr = pro + city + area + town;
                    try
                    {
                        String afterService = jdVOPAfsManager.decodeRsa(item.getAfterServiceReceiver());
                        String afterServiceTel = jdVOPAfsManager.decodeRsa(item.getAfterServiceTel());
                        String afterServicePhone = jdVOPAfsManager.decodeRsa(item.getAfterServicePhone());
                        dto.setAfterService(afterService);
                        dto.setAfterServiceTel(afterServiceTel);
                        dto.setAfterServicePhone(afterServicePhone);
                        afterServiceAddr = afterServiceAddr + jdVOPAfsManager.decodeRsa(item.getAfterServiceAddress());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    dto.setAfterServiceAddr(afterServiceAddr);
                }
            }
        }
        
        if(RefundStatus.JD_APPROVAL_ACCEPTED.equals(or.getStatus()))
        {
            MktOrderRefundExtend ore = orderRefundExtendDao.byRefundPkey(or.getPkey());
            StringBuffer sb = new StringBuffer();
            if(CourierType.JD_DOOR_TO_DOOR_PICKUP.equals(ore.getCourierType()))
            {
                sb.append("上门取件: 京东快递 ");
                String pt = ore.getPickupTimeStart().substring(0, 10);
//                Date date = DateUtil.formatDateStr(pt, "yyyy-MM-dd");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate targetDate = LocalDate.parse(pt, formatter);
                boolean isToday = targetDate.isEqual(LocalDate.now());
                if(isToday)
                    sb.append("今天").append(ore.getPickupTimeStart().substring(11,16))
                    .append("-").append(ore.getPickupTimeEnd().substring(11,16));
                else
                    sb.append(ore.getPickupTimeStart().substring(0, 16)).append("-")
                    .append("-").append(ore.getPickupTimeEnd().substring(11,16));
                sb.append(" 上门");
            }
            else
            {
                sb.append("自行寄出(");
                if(StringUtils.isNotBlank(ore.getCourierCompany()))
                {
                    sb.append("已寄出):您已寄出商品,待商家收货");
                }
                else
                {
                    sb.append("未寄出):审核已完成,待寄出商品");
                }
            }
            dto.setJdExpress(sb.toString());
        }
    
        if ((Constant.Operation + or.getAscription()).equals(or.getFarmer()))
        {
            MktAppConfig appConfig = appConfigManager.getAppConfig(or.getAscription());
            dto.setTel(appConfig.getTel());
        }
        MktOrderRefundExtend ore = orderRefundExtendDao.byRefundPkey(or.getPkey());
        if(ore != null && ore.getCourierType() != null)
        {
            dto.setCourierType(ore.getCourierType());
            dto.setCourierTypeName(ore.getCourierType().getName());
            dto.setAddr(ore.getPro() + ore.getCity() + ore.getAddr() + ore.getTown() + ore.getAddr());
            dto.setReceiptAddr(ore.getReceiptPro() + ore.getReceiptCity() 
            + ore.getReceiptAddr() + ore.getReceiptTown() + ore.getReceiptAddr());
            dto.setCourierCompany(ore.getCourierCompany());
            dto.setCourierNumber(ore.getCourierNumber());
            dto.setRefuseCourierCompany(ore.getRefuseCourierCompany());
            dto.setRefuseCourierNumber(ore.getRefuseCourierNumber());
        }
        List<MktOrderRefundLine> orlList = orderRefundLineDao.listRefundPkey(or.getPkey());
        Integer num = 0;
        BigDecimal sumAmt = BigDecimal.ZERO;
        Map<Integer, VendorRefundOrderOnList> map = new HashMap<>();
        List<VendorRefundOrderOnList> list = new ArrayList<>();
        for (MktOrderRefundLine orl : orlList)
        {
            if (orl.getRefundNum() != null) num = num + orl.getRefundNum();
            if (orl.getRefundAmt() != null) sumAmt = sumAmt.add(orl.getRefundAmt());
            VendorRefundOrderOnList vro = new VendorRefundOrderOnList();
            MktOrderLine orderLine = orderLineDao.get(orl.getOrderLinePkey());
            vro.setPkey(orl.getPkey());
            vro.setGoods(orderLine.getGoods().intValue());
            vro.setRefundAmt(orl.getRefundAmt());
            handlePrice4RefundOrder(vro, orderLine);
            vro.setNum(orl.getRefundNum());
            if (orderLine != null)
            {
                vro.setGoodsName(orderLine.getGoodsName());
            }
            MktGoodsSpace goodsSpace = goodsSpaceDao.get(orderLine.getSpace().intValue());
            if (goodsSpace != null)
            {
                vro.setSpaceName(goodsSpace.getSpace());
                vro.setWeight(goodsSpace.getWeight());
                vro.setPhoto(goodsSpace.getPhoto1());
            }
            else
            {
                JdGoods jg = jdGoodsDao.get(orderLine.getSpace());
                JdGoodsSpace jgs = jdGoodsSpaceDao.get(orderLine.getSpace());
                vro.setSpaceName(jgs.getSpaceName());
                vro.setEnabled(jg.getEnabled());
                vro.setMType(MType.INTEGRAL_MSD_GOODS);
                BigDecimal weight = BigDecimal.ZERO;
                if(StringUtils.isNotBlank(jg.getWeight()))
                    weight = new BigDecimal(jg.getWeight());
                vro.setWeight(weight);
                if (jg.getPhoto1() != null && !jg.getPhoto1().isEmpty())
                    vro.setPhoto(jg.getPhoto1().get(0));
            }
        
            if (StringUtils.isBlank(vro.getPhoto()))
            {
                MktGoods goods = goodsDao.get(orl.getGoods().intValue());
                if (goods != null)
                {
                    if (goods.getPhoto1() != null && !goods.getPhoto1().isEmpty())
                        vro.setPhoto(goods.getPhoto1().get(0));
                    else if (StringUtils.isNoneBlank(goods.getPhoto2()))
                    {
                        vro.setPhoto(goods.getPhoto2());
                    }
                    else
                        vro.setPhoto(goods.getPhoto3());
                }
            }
            if (!map.containsKey(vro.getGoods()))
            {
                map.put(vro.getGoods(), vro);
            }
            OrderGwcV2OnList l = new OrderGwcV2OnList();
            l.setPhoto(vro.getPhoto());
            l.setSpace(orl.getSpace().intValue());
            l.setSpaceName(vro.getSpaceName());
            l.setPrice(vro.getPrice());
            l.setCouponPrice(orderLine.getCouponPrice());
            l.setCouponAmt(orderLine.getCouponAmt());
            l.setNum(vro.getNum());
            l.setWeight(vro.getWeight());
            l.setRefundAmt(vro.getRefundAmt());
            l.setRefundNum(orl.getRefundNum());
            map.get(vro.getGoods()).getLines().add(l);
        }
        dto.setNum(num);
        dto.setSumAmt(sumAmt);
        for (VendorRefundOrderOnList vro : map.values())
        {
            MktGoods goods = goodsDao.get(vro.getGoods());
            if(goods != null)
            {
                vro.setEnabled(goods.getEnabled());
                vro.setMType(goods.getMType());
            }
            
            list.add(vro);
        }
        dto.setList(list);
        return dto;
    }
    
    public List<WebOrderRefundOnInfo> loadRefundOrder(Integer pkey)
    {
        List<WebOrderRefundOnInfo> res = new ArrayList<>();
        Map<Integer,WebOrderRefundOnInfo> map = new HashMap<>();
        Map<Integer,WebOrderRefundOnList> spaceMap = new HashMap<>();
        List<MktOrderLine> listOrder = orderLineDao.listOrder(pkey);
        for(MktOrderLine ol : listOrder)
        {
            WebOrderRefundOnList orl = BeanUtil.beanFrom(WebOrderRefundOnList.class, ol);
            orl.setSpace(ol.getSpace().intValue());
            orl.setSumPrice(ol.getCouponAmt());
            orl.setRefundPoint(0);
            orl.setSurplusRefundAmt(ol.getCouponAmt());
            MktGoodsSpace goodsSpace = goodsSpaceDao.get(ol.getSpace().intValue());
            if (goodsSpace.getPhoto1() != null) {
                if (JSONArray.isValidArray(goodsSpace.getPhoto1())) {
                    JSONArray array = JSONArray.parseArray(goodsSpace.getPhoto1());
                    if (array.size()>0)
                        orl.setPhoto(array.getString(0));
                }else {
                    orl.setPhoto(goodsSpace.getPhoto1());
                }
            }
            spaceMap.put(orl.getSpace(), orl);
        }
        List<MktVendorOrder> vendorOrderList = vendorOrderDao.listOrder(pkey);
        if(vendorOrderList != null && !vendorOrderList.isEmpty())
        {
            for(MktVendorOrder vo : vendorOrderList)
            {
                if(!map.containsKey(vo.getVendor()))
                {
                    MktVendor vendor = vendorDao.get(vo.getVendor());
                    WebOrderRefundOnInfo ori = new WebOrderRefundOnInfo();
                    ori.setVendor(vendor.getPkey());
                    ori.setName(vendor.getDisplayName());
                    ori.setBooth(vendor.getBooth());
                    ori.setList(new ArrayList<>());
                    map.put(vo.getVendor(), ori);
                }
                map.get(vo.getVendor()).getList().add(spaceMap.get(vo.getSpace()));
            }
            res.addAll(new ArrayList<>(map.values()));
        }
        else
        {
            WebOrderRefundOnInfo ori = new WebOrderRefundOnInfo();
            ori.setList(new ArrayList<>(spaceMap.values()));
            res.add(ori);
        }
        
        List<Integer> oRefundKeys = orderRefundDao.listStatusKey(pkey);
        if(oRefundKeys != null && !oRefundKeys.isEmpty())
        {
            List<MktOrderRefundLine> list = orderRefundLineDao.listRefundPkeys(oRefundKeys);
            Map<Integer,List<MktOrderRefundLine>> refundLineMap = new HashMap<>();
            for(MktOrderRefundLine l : list)
            {
                refundLineMap.computeIfAbsent(l.getOrderLinePkey(), e->new ArrayList<>()).add(l);
            }
            for(WebOrderRefundOnInfo ori : res)
            {
                for(WebOrderRefundOnList wor : ori.getList())
                {
                    if(refundLineMap.containsKey(wor.getPkey()))
                    {
                        for(MktOrderRefundLine orl : refundLineMap.get(wor.getPkey()))
                        {
                            wor.setRefundPoint(wor.getRefundPoint() + orl.getRefundPoint());
                            wor.setSurplusRefundAmt(wor.getSurplusRefundAmt().subtract(orl.getRefundAmt()));
                        }
                    }
                }
            }
        }
        return res;
    }
    
    private void handlePrice4RefundOrder(VendorRefundOrderOnList vro, MktOrderLine mktOrderLine)
    {
        if (mktOrderLine.getCouponAmt() != null)
        {
            vro.setSumPrice(mktOrderLine.getCouponAmt());
            if (mktOrderLine.getCouponPrice() != null)
                vro.setPrice(mktOrderLine.getCouponPrice());
            else
                vro.setPrice(
                    mktOrderLine.getCouponAmt().divide(new BigDecimal(mktOrderLine.getNum()), 2, RoundingMode.HALF_UP));
        }
        else
        {
            if (mktOrderLine.getCouponPrice() != null)
                vro.setPrice(mktOrderLine.getCouponPrice());
            else
                vro.setPrice(mktOrderLine.getPricen());
            vro.setSumPrice(vro.getPrice().multiply(new BigDecimal(mktOrderLine.getNum())));
        }
    }
    
    public Boolean weixinAgain(Integer refundPkey, Integer orderPkey)
    {
        BigDecimal amtre = BigDecimal.ZERO;
        MktOrder order = null;
        MktOrderRefund or = null;
        Integer orderKey = null;
        Integer ascription = CurrentSession.ascriptionPkey();
        if (refundPkey != null)
        {
            or = orderRefundDao.get(refundPkey);
            if(or == null)
                return false;
            if(!Boolean.TRUE.equals(or.getAgainRefund()))
                return false;
            amtre = or.getRefundWeixinAmt();
            if(amtre == null)
                amtre = or.getAmtre();
            ascription = or.getAscription();
            orderKey = or.getOrderPkey();
        }
        if (orderPkey != null)
        {
            order = orderDao.get(orderPkey);
            if(order == null)
                return false;
            if(!Boolean.TRUE.equals(order.getAgainRefund()))
                return false;
            amtre = order.getWeixinAmt();
            ascription = order.getAscription();
            orderKey = order.getPkey();
        }
        BigDecimal refund = amtre.multiply(new BigDecimal("100"));
        String outRefundNo = numberUtils.createRefundOrderNumber();
        
        MktOrder mktOrder = orderDao.get(orderKey);
        String orderNumber = mktOrder.getCode();
        orderNumber = orderNumber.substring(0, 14);
        boolean flag = false;
        if (ascription.equals(13))
        {
            ThirdPayLineEntity tpl = thirdPayLineDao.byMerOrderId(orderNumber);
            ChinaUmsRefundResponse chinaUmsRefund = chinaUmsRefundManager.chinaUmsRefund(tpl.getMerOrderId(), outRefundNo, refund);
            flag = (chinaUmsRefund != null && Boolean.TRUE.equals(chinaUmsRefund.isSuccess()));
        }
        else
        {
            MktPayLine pl = payDao.getOrderNumber(orderNumber);
            if(pl != null)
            {
                SysAscription asc = sysAscriptionDao.get(mktOrder.getAscription());
                if (asc == null || StringUtils.isBlank(asc.getCertificateSerialNo())
                    || StringUtils.isBlank(asc.getConfigMchid()) || StringUtils.isBlank(asc.getConfigLocalpath()))
                {
                    log.info("该订单缺乏数据,无法向微信发起退款,订单主键: {}", mktOrder.getPkey());
                    throw TofocusException.of(LejiaErrCode.WEIXIN_AGREE_ERROR);
                }
                Boolean refundOrder = wxRefundManager.createRefundOrder(pl.getCode(),
                    outRefundNo,
                    refund.longValue(),
                    Long.valueOf(pl.getAmt()),
                    asc.getConfigMchid(),
                    asc.getCertificateSerialNo(),
                    asc.getConfigLocalpath());
                flag = !Boolean.TRUE.equals(refundOrder);
            }
        }
        if(flag) 
        {
            if(or != null)
            {
                or.setAgainRefund(false);
                or.setOutRefundNo(outRefundNo);
                orderRefundDao.update(or);
            }
            if(order != null)
            {
                order.setAgainRefund(false);
                orderDao.update(order);
            }
        }
        return true;
    }
}
