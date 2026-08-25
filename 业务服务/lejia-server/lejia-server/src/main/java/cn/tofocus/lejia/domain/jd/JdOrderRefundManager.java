package cn.tofocus.lejia.domain.jd;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.createAfsApply.ApplyAfterSaleOpenReq;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.createAfsApply.ApplyInfoItemOpenReq;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.createAfsApply.CustomerInfoOpenReq;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.createAfsApply.PickupWareInfoOpenReq;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.createAfsApply.ReturnWareInfoOpenReq;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.createAfsApply.WareDescInfoOpenReq;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.createAfsApply.WareDetailInfoOpenReq;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.updateSendInfo.WaybillInfoVoOpenReq;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.getGoodsAttributes.SupportedInfoOpenResp;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppGwcDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderDTO;
import cn.tofocus.lejia.bean.dto.jd.JdRefundOrderDetails;
import cn.tofocus.lejia.bean.dto.market.MktOrderExpressInfo;
import cn.tofocus.lejia.bean.dto.refund.OrderRefundOnInfo;
import cn.tofocus.lejia.bean.dto.refund.OrderRefundOnPage;
import cn.tofocus.lejia.bean.dto.refund.VendorRefundOrderOnInfo;
import cn.tofocus.lejia.bean.dto.refund.VendorRefundOrderOnList;
import cn.tofocus.lejia.bean.dto.v2.order.OrderV2Info;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.market.MktExpress;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.market.MktRefund;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundExtend;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.jd.CourierType;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.jd.JdOrderCorrelationDao;
import cn.tofocus.lejia.dao.market.MktExpressDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundExtendDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.domain.jdvop.JdVOPAddrManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPAfsManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPOrderManager;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPAreaInfo;
import cn.tofocus.lejia.domain.market.MktMemberMsdManager;

@Slf4j
@Component
public class JdOrderRefundManager
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
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOrderDescDao descDao;
    
    @Autowired
    private JdOrderCorrelationDao jdOrderCorrelationDao;
    
    @Autowired
    private MktExpressDao expressDao;
    
    @Autowired
    private JdGoodsDao jdGoodsDao;

    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private JdVOPOrderManager jdVOPOrderManager;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private MktMemberMsdManager memberMsdManager;
    
    @Autowired
    private JdVOPAfsManager jdVOPAfsManager;
    
    @Autowired
    private JdVOPAddrManager jdVOPAddrManager;
    
    @Value("${zyysc.app.pickup.write.off.url:https://small.xinanshizu.com/writeOffIntegralPresale}")
    private String pickupWriteOffUrl;
    
    public OrderRefundOnInfo queryOrderRefund(int page, int pagesize, String code, List<RefundStatus> status,
        String startDate, String endDate)
    {
        if (StringUtils.isNoneBlank(startDate)) startDate = startDate + " 00:00:00";
        if (StringUtils.isNoneBlank(endDate)) endDate = endDate + " 23:59:59";
        List<OrderRefundOnInfo> list = orderRefundDao.joinSelect()
            .join(MktOrder.class, MktOrderRefund.F.code, MktOrder.F.code)
            .eq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .endJoin()
            .in("status", status)
            .ge("createdTime", startDate)
            .le("createdTime", endDate)
            .eq("farmer", CurrentSession.marketPkey())
            .eq("ascription", CurrentSession.ascriptionPkey())
            .like("code", code)
            .count("pkey", "num")
            .sum("amtre", "refundAmt")
            .sum("refundPoint", "refundPoint")
            .exec(OrderRefundOnInfo.class);
        OrderRefundOnInfo res = new OrderRefundOnInfo();
        res.setNum(0);
        res.setRefundAmt(BigDecimal.ZERO);
        res.setRefundPoint(0);
        if (list != null && !list.isEmpty())
        {
            res = list.get(0);
        }
        PageResult<OrderRefundOnPage> onPage = orderRefundDao.joinSelectPage()
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
            .as(MktOrderRefund.F.refundPoint)
            .as(MktOrderRefund.F.status)
            .as(MktOrderRefund.F.reason)
            .as(MktOrderRefund.F.createdTime)
            .as(MktOrderRefund.F.jdType)
            .as(MktOrderRefund.F.againRefund)
            .as(MktOrderRefund.F.refundWeixinAmt)
            .join(MktOrder.class, MktOrderRefund.F.code, MktOrder.F.code)
            .eq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .endJoin()
            .join(JdOrderCorrelation.class, MktOrderRefund.F.code, JdOrderCorrelation.F.orderCode)
            .as(JdOrderCorrelation.F.jdCode, "jdOrderId")
            .endJoin()
            .in("status", status)
            .ge("createdTime", startDate)
            .le("createdTime", endDate)
            .eq("farmer", CurrentSession.marketPkey())
            .eq("ascription", CurrentSession.ascriptionPkey())
            .like("code", code)
            .sort("createdTime")
            .sort("pkey")
            .exec(OrderRefundOnPage.class);
        for (OrderRefundOnPage d : onPage.getContent())
        {
            if (d.getRefundPoint() == null) d.setRefundPoint(0);
            if (d.getAgainRefund() == null) d.setAgainRefund(false);
        }
        res.setOnPage(onPage);
        return res;
    }
    
    public JdRefundOrderDetails getRefundOrder(Integer pkey)
    {
        MktOrderRefund or = orderRefundDao.get(pkey);
        MktAppOrderDTO order = loadInitOrder(or.getOrderPkey());
        JdRefundOrderDetails res = BeanUtil.beanFrom(JdRefundOrderDetails.class, order);
        res.setRefundPkey(pkey);
        res.setRefundAmt(order.getRefundAmt());
        res.setCurrentRefundAmt(or.getAmtre());
        res.setRefundGoodsAmt(or.getRefundGoodsAmt());
        res.setRefundPostage(or.getRefundPostage());
        res.setRefundCard(or.getRefundCard());
        res.setRefundCardPostage(or.getRefundCardPostage());
        res.setJdType(or.getJdType());
        if (order.getOldPostage() != null) res.setPostage(order.getOldPostage());
        JdOrderCorrelation joc = jdOrderCorrelationDao.getByCode(or.getCode());
        if (joc != null)
            res.setJdOrderId(joc.getJdCode());
        
        res.setReason(or.getReason());
        res.setDescribe(or.getDescribe());
        res.setRefundStatus(or.getStatus());
        res.setRefundStatusName(or.getStatus().getName());
        res.setRefundPhoto(or.getPhoto());
        res.setDelDesc(or.getDelDesc());
        res.setOrderTypeName(res.getOrderType().getName());
        List<MktOrderRefundLine> list = orderRefundLineDao.listRefundPkey(pkey);
//        Map<Integer, List<VendorRefundOrderOnList>> map = new HashMap<>();
        Integer resRefundPoint = res.getRefundPoint();
        if (resRefundPoint == null) resRefundPoint = 0;
        
        MktOrderRefundExtend ore = orderRefundExtendDao.byRefundPkey(pkey);
        if (ore != null)
        {
            BeanUtils.copyProperties(ore, res);
            res.setPickupAddr(ore.getAddr());
        }
        
        VendorRefundOrderOnInfo vroInfo = new VendorRefundOrderOnInfo();
        vroInfo.setName("");
        vroInfo.setBooth("");
        Integer num = 0;
        BigDecimal sumAmt = BigDecimal.ZERO;
        Integer refundPoint = 0;
        List<VendorRefundOrderOnList> vroList = new ArrayList<>();
        for (MktOrderRefundLine orl : list)
        {
            MktOrderLine orderLine = orderLineDao.get(orl.getOrderLinePkey());
            VendorRefundOrderOnList vro = new VendorRefundOrderOnList();
            vro.setPkey(orl.getPkey());
            vro.setNum(orl.getRefundNum());
            vro.setSpaceName(orderLine.getSpaceName());
            vro.setGoodsName(orderLine.getGoodsName());
            JdGoods jdGoods = jdGoodsDao.get(orderLine.getSpace());
            if (jdGoods != null)
            {
                vro.setWeight(new BigDecimal(jdGoods.getWeight()));
                if (jdGoods.getPhoto1() != null && !jdGoods.getPhoto1().isEmpty())
                    vro.setPhoto(jdGoods.getPhoto1().get(0));
            }
            vro.setRefundAmt(orl.getRefundAmt());
            vro.setRefundPoint(orl.getRefundPoint());
            if (vro.getRefundPoint() == null)
            {
                vro.setRefundPoint(orl.getRefundNum() * orderLine.getPoint());
            }
            if (orl.getRefundPoint() != null)
            {
                resRefundPoint += vro.getRefundPoint();
            }
            MktOrderLine mktOrderLine = orderLineDao.get(orl.getOrderLinePkey());
            vro.setSumPrice(mktOrderLine.getCouponAmt());
            vro.setPrice(mktOrderLine.getPrice());
            if (vro.getNum() != null) num = num + vro.getNum();
            if (vro.getRefundAmt() != null) sumAmt = sumAmt.add(vro.getRefundAmt());
            if (vro.getRefundPoint() != null) refundPoint += vro.getRefundPoint();
            vroList.add(vro);
        }
        List<VendorRefundOrderOnInfo> refundOrder = new ArrayList<>();
        vroInfo.setNum(num);
        vroInfo.setSumAmt(sumAmt);
        vroInfo.setRefundPoint(refundPoint);
        vroInfo.setList(vroList);
        refundOrder.add(vroInfo);
        res.setRefundOrder(refundOrder);
        if (!RefundStatus.REFUND_FINAL.equals(or.getStatus()))
        {
            res.setRefundPoint(resRefundPoint);
        }
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
    
    public MktAppOrderDTO loadInitOrder(int pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktOrder order = orderDao.selectOne().eq("pkey", pkey)
//            .notEq("status", OrderStatus.VOID_ORDER)
            .exec();
        MktAppOrderDTO dto = new MktAppOrderDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setPointn(0);
        
        MktExpress e = expressDao.selectOne().eq("orderId", pkey).eq("code", order.getCode()).exec();
        if (e != null)
        {
            dto.setExpressStatus(e.getStatus());
        }
        MktAppAddrDTO addDto = new MktAppAddrDTO();
        MktOrderDesc orderDesc = descDao.get(pkey);
        dto.setLogistics("");
        dto.setKdCode("");
        if (orderDesc != null)
        {
            addDto.setAddrDetail(orderDesc.getAddr());
            addDto.setMobile(orderDesc.getMobile());
            addDto.setName(orderDesc.getName());
            addDto.setEnabled(true);
            addDto.setDistance(orderDesc.getDistance());
            dto.setRemark(orderDesc.getRemark());
            dto.setAddr(addDto);
            String logistics = orderDesc.getLogistics();
            dto.setLogistics(logistics == null ? "" : logistics);
            String kdCode = orderDesc.getKdCode();
            dto.setKdCode(kdCode == null ? "" : kdCode);
            if (StringUtils.isBlank(dto.getPstime())
                && (Constant.Operation + ascription).equals(CurrentSession.marketPkey()))
                dto.setPstime(DateUtil.formatDate(orderDesc.getFhTime()));
        }
        
        List<MktAppGwcDTO> list = new ArrayList<>();
        List<MktOrderLine> lineList = orderLineDao.select().eq("orderPkey", pkey).exec();
        for (MktOrderLine line : lineList)
        {
            MktAppGwcDTO gwcDto = new MktAppGwcDTO();
            BeanUtils.copyProperties(line, gwcDto);
            JdGoods jdGoods = jdGoodsDao.get(line.getSpace());
            if (jdGoods != null)
            {
                if (jdGoods.getPhoto1() != null && !jdGoods.getPhoto1().isEmpty())
                    gwcDto.setPhoto(jdGoods.getPhoto1().get(0));
            }
            gwcDto.setSpaceName(line.getSpaceName());
            gwcDto.setGoodsName(line.getGoodsName());
            gwcDto.setPrice(line.getPricen());
            gwcDto.setMTypeName("京东商品");
            gwcDto.setPrice(order.getAmto());
            list.add(gwcDto);
        }
        dto.setList1(list);
        if (order.getStatus().equals(OrderStatus.REFUND_APPLICATION_ORDER)
            || order.getStatus().equals(OrderStatus.REFUNDED_ORDER))
        {
            MktRefund refund = orderRefundDao.selectOne().eq("orderPkey", pkey).execDto(MktRefund.class);
            dto.setRefund(refund);
        }
        if (dto.getMember() != null)
        {
            MktMember mktMember = memberDao.get(dto.getMember());
            if (mktMember != null) dto.setMemberName(mktMember.getName());
        }
        dto.setUrl(pickupWriteOffUrl);
        return dto;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Boolean refuseRefund(Integer pkey, String delDesc)
    {
        MktOrderRefund or = orderRefundDao.get(pkey);
        or.setDelDesc(delDesc);
        or.setDelTime(new Date());
        or.setStatus(RefundStatus.REFUND_REFUSE);
        orderRefundDao.update(or);
        
        List<MktOrderLine> listOrder = orderLineDao.listOrder(or.getOrderPkey());
        Map<Integer, MktOrderRefundLine> map = orderRefundLineDao.mapOrderLinePkey(pkey);
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
        return true;
    }
    
    // 校验是否可售后  批量查询订单下商品售后权益
    public Boolean getGoodsAttributes(RefundJdType jdType, List<Long> skuIdList, Long jdOrderId, Map<Long,Integer> map)
    {
        List<SupportedInfoOpenResp> goodsAttributes = jdVOPAfsManager.getGoodsAttributes(jdOrderId, skuIdList);
        List<Long> errSkuIdList = new ArrayList<>();
        List<Long> errSkuId40List = new ArrayList<>();
        List<Long> errcustomeSkuIdList = new ArrayList<>();
        List<Long> errNumSkuIdList = new ArrayList<>();
        for(SupportedInfoOpenResp si : goodsAttributes)
        {
            if(si.getPickupWareType() != null)
            {
                if(!si.getPickupWareType().contains(4))
                    errSkuIdList.add(si.getWareId());
                if(!si.getPickupWareType().contains(40))
                    errSkuId40List.add(si.getWareId());
            }
            
            if((RefundJdType.RETURN_GOODS.equals(jdType) && si.getCustomerExpect() != null && !si.getCustomerExpect().contains(10))
                || (RefundJdType.EXCHANGE.equals(jdType) && si.getCustomerExpect() != null && !si.getCustomerExpect().contains(20)))
            {
                errcustomeSkuIdList.add(si.getWareId());
            }
            if(map.containsKey(si.getWareId()))
            {
                Integer num = map.get(si.getWareId());
                if(num > si.getWareNum())
                    errNumSkuIdList.add(si.getWareId());
            }
        }
        if(!errSkuIdList.isEmpty())
        {
            List<JdGoods> list = jdGoodsDao.byPkey(errSkuIdList);
            StringBuffer sb = new StringBuffer();
            for(JdGoods jg : list)
            {
                sb.append(jg.getTitle()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            throw TofocusException.of(LejiaErrCode.JD_REFUND_ERROR, "[京东VOP]" + sb.toString() + " 以上商品不支持京东上门取件");
        }
        if(!errSkuId40List.isEmpty())
        {
            List<JdGoods> list = jdGoodsDao.byPkey(errSkuId40List);
            StringBuffer sb = new StringBuffer();
            for(JdGoods jg : list)
            {
                sb.append(jg.getTitle()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            throw TofocusException.of(LejiaErrCode.JD_REFUND_ERROR, "[京东VOP]" + sb.toString() + " 以上商品不支持自行寄出");
        }
        if(!errcustomeSkuIdList.isEmpty())
        {
            List<JdGoods> list = jdGoodsDao.byPkey(errcustomeSkuIdList);
            StringBuffer sb = new StringBuffer();
            for(JdGoods jg : list)
            {
                sb.append(jg.getTitle()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            throw TofocusException.of(LejiaErrCode.JD_REFUND_ERROR, "[京东VOP]" + sb.toString() + " 以上商品不支持退货/换货");
        }
        if(!errNumSkuIdList.isEmpty())
        {
            List<JdGoods> list = jdGoodsDao.byPkey(errNumSkuIdList);
            StringBuffer sb = new StringBuffer();
            for(JdGoods jg : list)
            {
                sb.append(jg.getTitle()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            throw TofocusException.of(LejiaErrCode.JD_REFUND_ERROR, "[京东VOP]" + sb.toString() + " 以上商品退货/换货数量超过可退货/换货数量");
        }
        return true;
    }

    // 校验是否可售后  批量查询订单下商品售后权益
    public Map<Long,OrderV2Info> mapGoodsAttributes(RefundJdType jdType, List<Long> skuIdList, Long jdOrderId, Map<Long,OrderV2Info> map)
    {
        Map<Long,OrderV2Info> res = new HashMap<>();
        List<SupportedInfoOpenResp> goodsAttributes = jdVOPAfsManager.getGoodsAttributes(jdOrderId, skuIdList);
        for(SupportedInfoOpenResp si : goodsAttributes)
        {
            log.info("si: {}", JsonUtil.toString(si, true));
            OrderV2Info oi = map.get(si.getWareId());
            oi.setJdDoor(false);
            oi.setSelfMailing(false);
            if(si.getPickupWareType() != null)
            {
                if(si.getPickupWareType().contains(4))
                {
                    oi.setJdDoor(true);
                }
                if(si.getPickupWareType().contains(40))
                {
                    oi.setSelfMailing(true);
                }
            }
            if(RefundJdType.RETURN_GOODS.equals(jdType) && si.getCustomerExpect() != null && !si.getCustomerExpect().contains(10))
            {
                oi.setJdAttributes("该商品不支持退货");
                res.put(si.getWareId(), oi);
            }
            if(RefundJdType.EXCHANGE.equals(jdType) && si.getCustomerExpect() != null && !si.getCustomerExpect().contains(20))
            {
                oi.setJdAttributes("该商品不支持换货");
                res.put(si.getWareId(), oi);
            }
            if(si.getWareNum() <= 0)
                oi.setJdAttributes("可退数量为0");
            oi.setJdRefundNum(si.getWareNum());
            res.put(si.getWareId(), oi);
        }
        return res;
    }
    
    public Boolean agreeRefund(Integer pkey, String delDesc)
    {
        MktOrderRefund or = orderRefundDao.get(pkey);
        if (or == null) throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到退款订单");
        JdOrderCorrelation joc = jdOrderCorrelationDao.getByCode(or.getCode());
        RefundJdType jdType = or.getJdType();
        String outRefundNo = numberUtils.createRefundOrderNumber();
        or.setDelDesc(delDesc);
        or.setOutRefundNo(outRefundNo);
        or.setStatus(RefundStatus.REFUND_JD_HANDLE);
        orderRefundDao.update(or);
//        List<MktOrderLine> list = orderLineDao.listOrder(or.getOrderPkey());
//        MktOrder mktOrder = orderDao.get(or.getOrderPkey());
//        if (mktOrder.getRefundAmt() != null)
//            mktOrder.setRefundAmt(mktOrder.getRefundAmt().add(or.getAmtre()));
//        else
//            mktOrder.setRefundAmt(or.getAmtre());
//        BigDecimal refundJd = orderRefundLineDao.aggSumRefundJd(or.getPkey());
//        if (mktOrder.getRefundJd() != null)
//            mktOrder.setRefundJd(mktOrder.getRefundJd().add(refundJd));
//        else
//            mktOrder.setRefundJd(refundJd);
//        orderDao.update(mktOrder);
        
       
        if(jdType == null || RefundJdType.RETURN_MONEY.equals(jdType))
        {
            // 请求京东,取消订单
            String thirdOrderId = or.getCode();
            if(joc.getParentOrder() != null)
            {
                JdOrderCorrelation pjoc = jdOrderCorrelationDao.getByJdCode(joc.getParentOrder());
                thirdOrderId = pjoc.getOrderCode();
            }
            jdVOPOrderManager.cancelOrder(joc.getJdCode(), thirdOrderId, or.getReason());
            return true;
        }
        Integer customerExpect = 10;
        Integer wareType = 10;
        Integer returnWareType = 10;
        Integer pickWareType = 4;
        MktOrderRefundExtend ore = orderRefundExtendDao.byRefundPkey(or.getPkey());
        MktOrderDesc orderDesc = descDao.get(or.getOrderPkey());
        if(RefundJdType.EXCHANGE.equals(or.getJdType()))
        {
            // 请求京东,换货
            customerExpect = 20;
        }
        if(CourierType.SELF_MAILING.equals(ore.getCourierType()))
            pickWareType = 40;
        ApplyAfterSaleOpenReq apply = new ApplyAfterSaleOpenReq();
//        String thirdApplyId = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss");
        apply.setThirdApplyId(or.getOutRefundNo());
        log.info("ThirdApplyId: " + apply.getThirdApplyId());
        apply.setIsHasInvoice(false);
        apply.setOrderId(joc.getJdCode());
        List<ApplyInfoItemOpenReq> applyInfoItemList = new ArrayList<>();
        
        List<MktOrderRefundLine> orlList = orderRefundLineDao.listRefundPkey(or.getPkey());
        for(MktOrderRefundLine orl : orlList)
        {
            ApplyInfoItemOpenReq applyInfoItem = new ApplyInfoItemOpenReq();
            applyInfoItem.setCustomerExpect(customerExpect);
            WareDescInfoOpenReq wareDescInfo = new WareDescInfoOpenReq();
            wareDescInfo.setQuestionDesc(or.getReason());
//                // 问题描述图片，最多2000字符，支持多张图片，用逗号分隔（英文逗号）
//                if(or.getPhoto() != null && !or.getPhoto().isEmpty())
//                {
//                    wareDescInfo.setQuestionPic(String.join(",", or.getPhoto()));
//                }
            applyInfoItem.setWareDescInfoOpenReq(wareDescInfo);
            WareDetailInfoOpenReq wareDetailInfo = new WareDetailInfoOpenReq();
            wareDetailInfo.setWareId(orl.getSpace());
            wareDetailInfo.setMainWareId(orl.getSpace());
            JdGoods jdGoods = jdGoodsDao.get(orl.getSpace());
            wareDetailInfo.setWareName(jdGoods.getTitle());
            wareDetailInfo.setWareNum(orl.getRefundNum());
            wareDetailInfo.setWareType(wareType);
            applyInfoItem.setWareDetailInfoOpenReq(wareDetailInfo);
            applyInfoItemList.add(applyInfoItem);
        }
        apply.setApplyInfoItemOpenReqList(applyInfoItemList);
        
        CustomerInfoOpenReq customerInfo = new CustomerInfoOpenReq();
        customerInfo.setCustomerName("天津国成VOP");
      
        customerInfo.setCustomerMobilePhone(orderDesc.getMobile());
        customerInfo.setCustomerContactName(orderDesc.getName());
        apply.setCustomerInfoVo(customerInfo);
        
        PickupWareInfoOpenReq pickupWareInfo = new PickupWareInfoOpenReq();
        pickupWareInfo.setPickWareType(pickWareType);
        
        JdVOPAreaInfo jdArea = jdVOPAddrManager.convert2AreaInfo(ore.getPro(), ore.getCity(), ore.getArea(), ore.getTown());
        
        pickupWareInfo.setPickWareProvince(jdArea.getProvinceId().intValue());
        pickupWareInfo.setPickWareCity(jdArea.getCityId().intValue());
        pickupWareInfo.setPickWareCounty(jdArea.getCountyId().intValue());
        pickupWareInfo.setPickWareVillage(jdArea.getTownId().intValue());
        
        pickupWareInfo.setPickWareAddress(ore.getAddr());
        // yyyy-MM-dd HH:mm:ss
        pickupWareInfo.setReserveDateBegin(ore.getPickupTimeStart());
        pickupWareInfo.setReserveDateEnd(ore.getPickupTimeEnd());
        apply.setPickupWareInfoOpenReq(pickupWareInfo);
        
        ReturnWareInfoOpenReq returnWareInfo = new ReturnWareInfoOpenReq();
        // 返件方式。10自营配送，20第三方配送
        returnWareInfo.setReturnWareType(returnWareType);
        JdVOPAreaInfo jdAreaInfo = jdVOPAddrManager.convert2AreaInfo(orderDesc.getPro(), orderDesc.getCity(), orderDesc.getArea(), orderDesc.getTown());
        returnWareInfo.setReturnWareProvince(jdAreaInfo.getProvinceId().intValue());
        returnWareInfo.setReturnWareCity(jdAreaInfo.getCityId().intValue());
        returnWareInfo.setReturnWareCountry(jdAreaInfo.getCountyId().intValue());
        returnWareInfo.setReturnWareVillage(jdAreaInfo.getTownId().intValue());
        // 返件地址 默认拿了原下单地址
        returnWareInfo.setReturnWareAddress(orderDesc.getAddr());
        apply.setReturnWareInfoOpenReq(returnWareInfo);
        
        jdVOPAfsManager.createAfsApply(apply);
       
        return true;
    }
    
    
    public MktOrderRefund assemblyRefund(MktOrder order, String orderCode, String outRefundNo)
    {
        MktOrderRefund oldOr = orderRefundDao.byJdOrderCode(orderCode);
        MktOrderRefund or = new MktOrderRefund();
        or.setCode(order.getCode());
        or.setOrderPkey(order.getPkey());
        or.setOutRefundNo(outRefundNo);
        or.setStatus(RefundStatus.REFUND_FINAL);
        or.setMember(order.getMember());
        if(oldOr != null)
        {
            or.setReason(oldOr.getReason());
            or.setDescribe(oldOr.getDescribe());
            or.setPhoto(oldOr.getPhoto());
        }
        or.setPreferentialAmt(order.getCardAmt());
        or.setPreferentialPostageAmt(order.getCardPostageAmt());
        or.setOldPostage(order.getOldPostage());
        or.setPostage(order.getPostage());
        or.setAmtall(order.getAmtn());
        or.setRefundPoint(0);
        Date now = new Date();
        or.setDelBy(CurrentSession.userPkey());
        or.setDelTime(now);
        or.setReTime(now);
        or.setGoodsAmt(order.getAmto());
        or.setRefundGoodsAmt(order.getAmto());
        or.setRefundPostage(order.getPostage());
        or.setAmtre(order.getAmtn());
        or.setFarmer(order.getFarmer());
        or.setCompany(order.getCompany());
        or.setAscription(order.getAscription());
        MktOrderRefund mktOrderRefund = orderRefundDao.add(or);
        
        List<MktOrderRefundLine> refundLines = new ArrayList<>();
        List<MktOrderLine> orderLineList = orderLineDao.listOrder(order.getPkey());
        for(MktOrderLine orderLine : orderLineList)
        {
            MktOrderRefundLine orl = new MktOrderRefundLine();
            orl.setOrderLinePkey(orderLine.getPkey());
            orl.setGoods(orderLine.getGoods());
            orl.setSpace(orderLine.getSpace());
            orl.setRefundPoint(0);
            orl.setRefundNum(orderLine.getNum());
            orl.setFarmer(orderLine.getFarmer());
            orl.setAscription(orderLine.getAscription());
            orl.setRefundJd(orderLine.getPrice().multiply(new BigDecimal(orderLine.getNum()).setScale(2, BigDecimal.ROUND_HALF_UP)));
            orl.setRefundAmt(orderLine.getPricen().multiply(new BigDecimal(orderLine.getNum()).setScale(2, BigDecimal.ROUND_HALF_UP)));
            orl.setRefundPkey(mktOrderRefund.getPkey());
            refundLines.add(orl);

            orderLine.setRefundNum(orderLine.getNum());
            orderLine.setRefundAmt(orl.getRefundAmt());
            orderLine.setStatus(OrderStatus.REFUNDED_ORDER);
        }
        orderLineDao.updateAll(orderLineList);
        orderRefundLineDao.addAll(refundLines);
        return or;
    }
    
    public Boolean testRefund(Long orderId)
    {
        JdOrderCorrelation joc = jdOrderCorrelationDao.getByJdCode(orderId);
        if (joc != null)
        {    
            String outRefundNo = numberUtils.createRefundOrderNumber();
            // 这里有点问题，一个订单可能多个refund
            MktOrderRefund or = orderRefundDao.byJdOrderCodeHandle(joc.getOrderCode());
            MktOrder order = orderDao.get(joc.getPkey());
            if (order == null)
            {
                log.warn("[京东VOP-消息队列]找不到京东订单：{}", orderId);
                return false;
            }
            if(or == null)
            {
                JdOrderCorrelation pjoc = jdOrderCorrelationDao.getByJdCode(joc.getParentOrder());
                or = assemblyRefund(order, pjoc.getOrderCode(), outRefundNo);
            }
            else
            {
                or.setOutRefundNo(outRefundNo);
                or.setDelTime(new Date());
                or.setStatus(RefundStatus.REFUND_FINAL);
                // 京东订单直接取消,邮费直接退回  TODO
                or.setRefundPostage(or.getPostage());
                or.setRefundJdPostage(or.getOldPostage());
                orderRefundDao.update(or);
            }
           
            order.setStatus(OrderStatus.REFUNDED_ORDER);
            if (order.getRefundAmt() != null)
                order.setRefundAmt(order.getRefundAmt().add(or.getAmtre()));
            else
                order.setRefundAmt(or.getAmtre());
            order.setRefundJd(order.getPayDetailMoney());
            orderDao.update(order);
            BigDecimal amtnMsd = order.getAmtn();
            if(PayType.MSD_COMBINATION.equals(order.getPayType()))
                amtnMsd = order.getOtherAmt();
            // 退还热力豆
            memberMsdManager.updMsdBalance(order.getMember(),
                null,
                true,
                amtnMsd,
                MsdOperationType.REFUND,
                order.getCode() + "订单退款",
                order.getCode(),
                order.getAscription(),
                true);
            log.info("[京东VOP-消息队列]订单（{}）已主动取消成功", order.getCode());
        }
        else
        {
            log.warn("[京东VOP-消息队列]找不到京东订单：{}", orderId);
        }
        return true;
    }
    
    // ====================== 可配置参数 ======================
    private static final int START_HOUR = 9;       // 开始营业时间 9点
    private static final int END_HOUR = 18;        // 结束营业时间 20点
    private static final int INTERVAL_HOURS = 1;   // 每段间隔1小时
    private static final int MAX_COUNT = 7; // 最多生成8个时段
    private static final int DELAY_HOURS = 2;     // 延迟2小时
    // ======================================================
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


    public List<String> generateTimeList() {
        List<String> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        // 核心：当前时间 + 2小时
        LocalDateTime limitTime = now.plusHours(DELAY_HOURS);

        LocalDateTime current = limitTime;
        int count = 0;

        while (count < MAX_COUNT) {
            // 只处理营业时段
            if (current.getHour() >= START_HOUR && current.getHour() < END_HOUR) {
                // 取当前小时的整点作为开始
                LocalDateTime slotStart = current.withMinute(0).withSecond(0).withNano(0);
                LocalDateTime slotEnd = slotStart.plusHours(INTERVAL_HOURS);

                // 必须满足：时间段开始 >= 延迟2小时的时间
                if (!slotStart.isBefore(limitTime)) {
                    String date = slotStart.toLocalDate().toString();
                    String start = slotStart.format(FORMATTER);
                    String end = slotEnd.format(FORMATTER);
                    result.add(date + " " + start + "-" + end);
                }
            }

            // 下一个小时
            current = current.plusHours(1);

            // 跨天自动跳到次日9点
            if (current.getHour() >= END_HOUR) {
                current = current.plusDays(1).withHour(START_HOUR).withMinute(0);
                count++;
            }
        }
        return result;
    }

    public List<String> courierDrop()
    {
        List<String> res = new ArrayList<>();
        res.add("顺丰速运");
        res.add("中国邮政（EMS、邮政包裹）");
        res.add("中通快递");
        res.add("圆通速递");
        res.add("申通快递");
        res.add("韵达速递");
        res.add("极兔速递");
        res.add("京东快递（京东物流）");
        res.add("德邦快递");
        res.add("菜鸟速递（原丹鸟）");
        return res;
    }
    
    public Boolean updateSendInfo(Integer refundPkey, String courierCompany, String courierNumber,
        BigDecimal postage)
    {
        MktOrderRefund or = orderRefundDao.get(refundPkey);
        JdOrderCorrelation joc = jdOrderCorrelationDao.get(or.getOrderPkey());
        MktOrderRefundExtend ore = orderRefundExtendDao.byRefundPkey(refundPkey);
        ore.setCourierCompany(courierCompany);
        ore.setCourierNumber(courierNumber);
        ore.setPostage(postage);
        orderRefundExtendDao.update(ore);
        List<MktOrderRefundLine> list = orderRefundLineDao.listRefundPkey(refundPkey);
        List<WaybillInfoVoOpenReq> waybills = new ArrayList<>();
        Date time = Calendar.getInstance().getTime();
        for(MktOrderRefundLine orl : list)
        {
            WaybillInfoVoOpenReq wi = new WaybillInfoVoOpenReq();
            wi.setDeliverDate(DateUtil.formatDate(time));
            wi.setWareNum(orl.getRefundNum());
            wi.setExpressCode(courierNumber);
            wi.setWareId(orl.getSpace());
            wi.setWareType(10);
            wi.setExpressCompany(courierCompany);
            wi.setFreightMoney(postage);
            waybills.add(wi);
        }
        jdVOPAfsManager.updateSendInfo(waybills, joc.getJdCode(), or.getOutRefundNo());
        return true;
    }
    
    // 售后取消
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelAfsApply(Integer refundPkey)
    {
        MktOrderRefund or = orderRefundDao.get(refundPkey);
        JdOrderCorrelation joc = jdOrderCorrelationDao.get(or.getOrderPkey());
        jdVOPAfsManager.cancelAfsApply(joc.getJdCode(), or.getOutRefundNo(), "取消");
        or.setOutProcessing(true);
        orderRefundDao.update(or);
        return true;
    }
    
    // 售后确认完成
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmed(Integer refundPkey)
    {
        MktOrderRefund or = orderRefundDao.get(refundPkey);
        JdOrderCorrelation joc = jdOrderCorrelationDao.get(or.getOrderPkey());
        jdVOPAfsManager.confirmAfsOrder(joc.getJdCode(), or.getOutRefundNo(), "确认完成");
        or.setOutProcessing(true);
        orderRefundDao.update(or);
        return true;
    }
}
