package cn.tofocus.lejia.domain.market;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.data.KeyValue;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.db.join.db.SelectPageOps;
import cn.tofocus.db.join.db.SubSelectBuilder;
import cn.tofocus.lejia.bean.dto.MktOrderLineExcel;
import cn.tofocus.lejia.bean.entity.market.*;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.entity.sys.*;
import cn.tofocus.lejia.dao.market.*;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.dao.sys.*;
import cn.tofocus.lejia.exception.WsaleErrCode;
import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.msgpipe.queue.MsgListener;
import cn.tofocus.core.msgpipe.queue.MsgSenderTemplate;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.lejia.bean.dto.express.SfCancelOrderResult;
import cn.tofocus.lejia.bean.dto.express.SfPlaceOrderResult;
import cn.tofocus.lejia.bean.dto.express.SfWaybillNoInfo;
import cn.tofocus.lejia.bean.dto.market.MktOrderGroupOnList;
import cn.tofocus.lejia.bean.dto.market.MktOrderOnList;
import cn.tofocus.lejia.bean.dto.market.print.ScalePrintInfo;
import cn.tofocus.lejia.bean.dto.market.print.ScalePrintOriIfo;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.wx.MktGzh;
import cn.tofocus.lejia.bean.entity.zx.ThirdPayLineEntity;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.OrderGroupStatus;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.PriceStatus;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.express.ExpressCompany;
import cn.tofocus.lejia.bean.enums.express.OrderExpressStatus;
import cn.tofocus.lejia.cache.OrderPrintMap;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.wx.MktGzhAssociateDao;
import cn.tofocus.lejia.dao.wx.MktGzhDao;
import cn.tofocus.lejia.dao.zx.ThirdPayLineDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.app.AppExpressManager;
import cn.tofocus.lejia.domain.express.ExpressSfManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.DingTalkWarning;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.util.WxDataBuilder;
import cn.tofocus.lejia.util.print.XiyeCloudPrint;
import cn.tofocus.lejia.util.print.bean.CustomerPrintBean;
import cn.tofocus.lejia.util.print.bean.PrintOriInfo;
import cn.tofocus.lejia.util.print.bean.XiyeDeliveryMerhantGoodsBean;
import cn.tofocus.lejia.util.print.bean.XiyePrintDeliveryBean;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class OrderManager
{
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOrderDescDao descDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private MktCourierDao courierDao;
    
    @Autowired
    private MktExpressDao expressDao;
    
    @Autowired
    private MktOrderGroupDao orderGroupDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysFarmerPickupLocationDao sysFarmerPickupLocationDao;
    
    @Autowired
    private SysConfigDao sysConfigDao;
    
    @Autowired
    private XiyeCloudPrint xiyeCloudPrint;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;
    
    @Autowired
    private AppExpressManager expressManager;
    
    @Autowired
    private SysFarmerExtendDao farmerExtendDao;
    
    @Autowired
    private OrderPrintMap orderPrintMap;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private MktOrderExpressDao orderExpressDao;
    
    @Autowired
    private ExpressSfManager expressSfManager;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private ThirdPayLineDao thirdPayLineDao;
    
    @Autowired
    private MktOrderTagDao orderTagDao;
    
    @Autowired
    private MktSupplierPickupLocationDao supplierPickupLocationDao;
    
    @Autowired
    private MsgSenderTemplate msgSenderTemplate;
    
    @Autowired
    private AccountDao accountDao;
    
    @Autowired
    private MktGzhAssociateDao gzhAssociateDao;
    
    @Autowired
    private MktOrderDescDao orderDescDao;
    
    @Autowired
    private SysFarmerPickupLocationDao farmerPickupLocationDao;
    
    @Value("${xasz.saas.token.member.url:https://cloud.xinanshizu.com/farm-member}")
    private String prefixUrl;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    @Value("${spring.profiles.active}")
    private List<String> profilesActive;
    
    public PageResult<MktOrderOnList> queryOrder(int page, int pagesize, OrderOir orderOir, String startDate,
        String endDate, OrderStatus status, String code, String mobile, String memberMobile, OrderType orderType,
        PurchaseStatus purchaseStatus, Integer groupPkey, String vrifyCode, Boolean priceAbnormal,
        Boolean priceAbnormalFinsh, String farmer, Boolean falg, ExpressType expressType, List<PayType> payTypes, 
        List<Integer> tags, DistributionType distributionType)
    {
        PageResult<MktOrderOnList> result = new PageResult<>();
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        PageParameter pageable = new PageParameter(page, pagesize);
        result.setPageable(pageable);
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> members = null;
        if (StringUtils.isNotBlank(mobile))
        {
            List<MktOrderDesc> exec = descDao.select().like("mobile", mobile).exec();
            if (exec.isEmpty()) return result;
            for (MktOrderDesc od : exec)
                orderIds.add(od.getPkey());
        }
        if(tags != null && !tags.isEmpty())
        {
            List<MktOrderTag> list = orderTagDao.listTag(tags, ascriptionPkey);
            if(list == null || list.isEmpty())
                return result;
            for (MktOrderTag ot : list)
                orderIds.add(ot.getOrderPkey());
        }
        if (StringUtil.isNotBlank(memberMobile))
        {
            members = memberDao.select().like("mobile", memberMobile).execDto("pkey", Integer.class);
        }
        if (priceAbnormal && priceAbnormalFinsh)
        {
            List<MktVendorOrder> exec = vendorOrderDao.select()
                .eq("farmer", farmer)
                .eq("ascription", ascriptionPkey)
                .or()
                .eq("priceStatus", PriceStatus.ABNORMAL)
                .eq("priceStatus", PriceStatus.ABNORMAL_FINISH)
                .close()
                .done()
                .exec();
            if (exec.isEmpty()) return result;
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        else if (priceAbnormal)
        {
            List<MktVendorOrder> exec = vendorOrderDao.select()
                .eq("priceStatus", PriceStatus.ABNORMAL)
                .eq("farmer", farmer)
                .eq("ascription", ascriptionPkey)
                .exec();
            if (exec.isEmpty()) return result;
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        else if (priceAbnormalFinsh)
        {
            List<MktVendorOrder> exec = vendorOrderDao.select()
                .eq("priceStatus", PriceStatus.ABNORMAL_FINISH)
                .eq("farmer", farmer)
                .eq("ascription", ascriptionPkey)
                .exec();
            if (exec.isEmpty()) return result;
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        if (!orderIds.isEmpty())
        {
            orderIds = orderIds.stream().distinct().collect(Collectors.toList());
        }
        if(payTypes != null && !payTypes.isEmpty())
        {
            List<PayType> add = new ArrayList<>();
            for(PayType pt : payTypes)
            {
                if(PayType.ORDER_MSD.equals(pt))
                {
                    add.add(PayType.MSD_COMBINATION);
                }
                if(PayType.ORDER_ELECTRONIC_ACCOUNT.equals(pt))
                {
                    add.add(PayType.ELECTRONIC_ACCOUNT_COMBINATION);
                }
            }
            if(!add.isEmpty())
                payTypes.addAll(add);
        }
        result = orderDao.queryOrder(page,
            pagesize,
            orderOir,
            startDate,
            endDate,
            status,
            code,
            orderIds,
            orderType,
            members,
            purchaseStatus,
            groupPkey,
            vrifyCode,
            farmer,
            falg,
            expressType,
            ascriptionPkey,
            payTypes,
            distributionType,
            MktOrderOnList.class);
        Boolean flag = false;
        Integer ascription = CurrentSession.ascriptionPkey();
        if ((Constant.Operation + ascription).equals(CurrentSession.marketPkey()))
        {
            flag = true;
        }
        List<Integer> orderKeys = new ArrayList<>();
        result.getContent().forEach(e -> {
            orderKeys.add(e.getPkey());
        });
        Map<Integer, MktOrderDesc> descMap = descDao.mapKey(orderKeys);
        Map<Integer, List<MktOrderLine>> lineMap = orderLineDao.mapOrderPkey(orderKeys);
        List<Integer> spaceList = new ArrayList<>();
        for (Integer key : lineMap.keySet())
        {
            for (MktOrderLine ol : lineMap.get(key))
            {
                spaceList.add(ol.getSpace().intValue());
            }
        }
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(spaceList);
        
        for (MktOrderOnList line : result.getContent())
        {
            if (line.getRefundAmt() == null)
                line.setRefundAmt(BigDecimal.ZERO);
            if (line.getRefundPoint() == null)
                line.setRefundPoint(0);
            if (StringUtils.isNotBlank(line.getPickupCode()))
            {
                line.setOrderTrace("#" + line.getPickupCode());
            }
            if (line.getSmallTicket() != null) line.setOrderTrace("#" + line.getSmallTicket());
            if (line.getStatus() != null && line.getStatus().getIndex() == 0)
            {
//                line.setAmtn(BigDecimal.ZERO);
                line.setPstime("");
//                line.setPayType(null);
            }
            if (line.getStatus() != null) line.setStatusName(line.getStatus().getName());
            if (OrderStatus.CONFIRM_ORDER.equals(line.getStatus())) line.setStatusName("已完成");
            line.setOrderTypeName(line.getOrderType().getName());
            line.setPayTypeName(line.getPayType() == null ? "" : line.getPayType().getName());
            if (line.getCgCheck() != null && line.getCgCheck() == 1)
                line.setCgCheckName("已采购");
            else
                line.setCgCheckName("未采购");
            if (flag && !OrderType.INTEGRAL_PRESALE_ORDER.equals(line.getOrderType()))
            {
                line.setPstime("");
            }
            if (descMap.containsKey(line.getPkey()))
            {
                MktOrderDesc orderDesc = descMap.get(line.getPkey());
                line.setAddr(orderDesc.getAddr());
                line.setName(orderDesc.getName());
                line.setMobile(orderDesc.getMobile());
                line.setLogistics(orderDesc.getLogistics());
                if (flag) 
                {
                    if(!OrderType.INTEGRAL_PRESALE_ORDER.equals(line.getOrderType()))
                        line.setPstime(DateUtil.formatDate(orderDesc.getFhTime()));
                    else if(orderDesc.getFhTime() != null)
                        line.setPstime(DateUtil.formatDate(orderDesc.getFhTime()));
                }
                line.setRemark(orderDesc.getRemark());
            }
            //if (line.getDistributionType() == DistributionType.PICKUP) line.setPstime(line.getPickupTime());
            if (lineMap.containsKey(line.getPkey()))
            {
                List<MktOrderLine> orderLines = lineMap.get(line.getPkey());
                for (MktOrderLine ol : orderLines)
                {
                    Map<String, Object> map = new HashMap<>();
                    map.put("goodsName", ol.getGoodsName());
                    map.put("goodsPricen", ol.getPricen());
                    map.put("goodsNum", ol.getNum());
                    map.put("totalPricen", ol.getPricen().multiply(new BigDecimal(ol.getNum())));
                    String spaceName = "";
                    if (spaceMap.containsKey(ol.getSpace().intValue())) spaceName = spaceMap.get(ol.getSpace().intValue()).getSpace();
                    map.put("spaceName", spaceName);
                    line.getGoodsList().add(map);
                }
            }
            line.setTagName(orderTagDao.getTagName(line.getPkey()));
            //TODO 二维码地址 需要地址确定后调整
            line.setQrCode("http://www.baidu.con");
            MktExpress e = expressDao.selectOne().eq("orderId", line.getPkey()).eq("code", line.getCode()).exec();
            if (e != null && e.getCourier() != null && e.getCourier().intValue() != -1)
            {
                line.setExpressStatus(e.getStatus());
            }
        }
        //TODO 判断是否存在退款申请或者部分退款
        result.getContent().forEach(x -> {
            if (x.getStatus() != OrderStatus.REFUNDED_ORDER)
            {
                List<MktOrderRefund> list = orderRefundDao.listOrderPkey(x.getPkey());
                for (MktOrderRefund l : list)
                {
                    if ((l.getStatus() == RefundStatus.REFUND_AGREE || l.getStatus() == RefundStatus.REFUND_FINAL)
                        && x.getRefundInfo() == null) x.setRefundInfo("(部分退款)");
                    if (l.getStatus() == RefundStatus.REFUND_APPLYING)
                    {
                        x.setRefundInfo("(退款待处理)");
                        break;
                    }
                }
            }
        });
        
        return result;
    }
    
    @Transactional
    public void paidan(Integer pkey, Integer courier)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktOrder order = orderDao.get(pkey);
        if (ascription == null) ascription = MobileSession.appid();
        if (ascription == null) ascription = order.getAscription();
        order.setStatus(OrderStatus.SHIPPED_ORDER);
        order.setExpressType(ExpressType.COURIER);
        orderDao.update(order);
        MktExpress e = expressDao.selectOne().eq("orderId", pkey).eq("code", order.getCode()).exec();
        MktExpress express = new MktExpress();
        if (e != null)
        {
            express = e;
        }
        express.setCode(order.getCode());
        express.setOrderId(pkey);
        express.setStatus(ExpressStatus.EXPRESS_ORDER);
        express.setCourier(courier);
        MktCourier one = courierDao.getOne(courier);
        if (one != null)
        {
            express.setCourierName(one.getName());
            express.setCourierMobile(one.getMobile());
        }
        express.setFarmer(order.getFarmer());
        express.setCompany(order.getCompany());
        express.setCreatedTime(new Date());
        express.setAscription(ascription);
        PurchaseStatus purchaseStatus = order.getPurchaseStatus();
        if (purchaseStatus != null && PurchaseStatus.AWAIT_PURCHASE.equals(purchaseStatus))
            express.setStatusName("待取货");
        if (purchaseStatus != null && (PurchaseStatus.PURCHASEING.equals(purchaseStatus)
            || PurchaseStatus.PURCHASE_FINISH.equals(purchaseStatus))) express.setStatusName("拣货中");
        if (purchaseStatus != null && purchaseStatus.getIndex() == PurchaseStatus.PURCHASE_CONFIRM.getIndex())
            express.setStatusName("拣货完成");
        System.out.println("插入快递单");
        expressDao.put(express);
        MktOrderDesc desc = descDao.get(pkey);
        String addr = "地址请进入详情查看";
        if (desc != null)
        {
            desc.setLogistics("跑腿");
            desc.setKdCode(order.getCode());
            desc.setFhTime(new Date());
            descDao.update(desc);
            addr = desc.getAddr();
        }
        sendWeappSubscribeMessage(courier, addr, order);
        uploadShippingInfo(order, 2);
    }
    
    public void uploadShippingInfo(MktOrder order, Integer logisticsType)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            List<MktOrderLine> listOrder = orderLineDao.listOrder(order.getPkey());
            for (MktOrderLine s : listOrder)
            {
                sb.append(s.getGoodsName());
                sb.append("(");
                sb.append(s.getSpaceName());
                sb.append(")*");
                sb.append(s.getNum());
                sb.append(", ");
            }
            String itemDesc = sb.toString();
            if (itemDesc.length() > 0) itemDesc = itemDesc.substring(0, itemDesc.length() - 1);
            if (itemDesc.length() > 120) itemDesc = itemDesc.substring(0, 120);
            String openid = null;
            String mchid = null;
            SysAscription sysAscription = ascriptionDao.get(order.getAscription());
            if (sysAscription != null)
            {
                mchid = sysAscription.getConfigMchid();
            }
            MktMember mktMember = memberDao.get(order.getMember());
            if (mktMember != null) openid = mktMember.getOpenid1();
            if (openid != null && mchid != null)
            {
            	if(qfAscription == order.getAscription())
            	{
            	    String code = order.getCode();
            	    code = code.substring(0, 14);
            	    ThirdPayLineEntity entity = thirdPayLineDao.byMerOrderId(code);
            	    if(entity != null)
            	    {
            	        wxManager.uploadShippingInfo(entity.getTargetOrderId(), 
            	            null,
            	            null,
            	            itemDesc,
            	            logisticsType,
            	            null,
            	            null,
            	            null,
            	            null,
            	            openid,
            	            order.getAscription());
            	    }
            	}
            	else
            	{
            	    wxManager.uploadShippingInfo(null,
            	        order.getCode(),
            	        mchid,
            	        itemDesc,
            	        logisticsType,
            	        null,
            	        null,
            	        null,
            	        null,
            	        openid,
            	        order.getAscription());
            	}
            }
            else
            {
                log.info("缺少openid或mchid没有发货,订单号: {}", order.getCode());
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            log.error("微信确认收货报错");
        }
    }
    
    private void sendWeappSubscribeMessage(Integer courier, String addr, MktOrder order)
    {
        MktCourier one = courierDao.getOne(courier);
        if (one != null)
        {
            if (StringUtils.isNotBlank(one.getOpenid2()))
            {
                SysConfigEntity sysConfig =
                    sysConfigDao.getBean(Constant.SysConfig.TEMPLATE_COURIER, one.getAscription());
                AccountEntity wxAccount = wxManager.getAccountEntity(AccountType.COURIER, order.getAscription());
                if (sysConfig != null)
                {
                    SysFarmer farmer = sysFarmerDao.get(order.getFarmer());
                    sendWxMsgCourierYs(wxAccount
                        .getAccountAppid(), order, farmer, one.getOpenid2(), sysConfig.getValue());
                }
            }
        }
    }
    
    public Boolean arrivedExpress(Integer pkey)
    {
        MktOrder order = orderDao.get(pkey);
        MktExpress e = expressDao.selectOne().eq("orderId", pkey).eq("code", order.getCode()).exec();
        if (e == null) return false;
        if (e.getCourier() < 0) throw TofocusException.of(LejiaErrCode.EXPRESS_ERROR);
        return expressManager.alterExpressStatus(e.getPkey(), 2);
    }
    
    private void sendWxMsgCourierYs(String appid, MktOrder order, SysFarmer farmer, String openid, String templateid)
    {
        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", "您有新的订单,请注意查收!");
        data.put("first", jsonObject);
        
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("value", order.getCode());
        data.put("keyword1", jsonObject2);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("value", order.getCreatedTime());
        data.put("keyword2", jsonObject3);
        JSONObject key3 = new JSONObject();
        key3.put("value", order.getPstime());
        data.put("keyword3", key3);
        JSONObject key4 = new JSONObject();
        key4.put("value", farmer.getName());
        data.put("keyword4", key4);
        JSONObject key5 = new JSONObject();
        key5.put("value", farmer.getMobile());
        data.put("keyword5", key5);
        
        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("value", "点击查看!");
        data.put("remark", jsonObject4);
        JSONObject miniprogram = new JSONObject();
        miniprogram.put("appid", appid);
        miniprogram.put("page", "pages/introduce/introduce");
        Boolean msg = wxManager.wechatSendMsgYs(templateid, openid, miniprogram, data, order.getAscription());
        log.info("采购订单发送给骑手微信公众号: {}", msg);
    }
    
    @Transactional
    public void sendOrder(Integer pkey, String logistics, String code)
    {
        MktOrder order = orderDao.get(pkey);
        order.setStatus(OrderStatus.SHIPPED_ORDER);
//        order.setPstime(DateUtil.formatDate(new Date()));
        orderDao.update(order);
        MktOrderDesc desc = descDao.get(pkey);
        if (desc != null)
        {
            desc.setLogistics(logistics);
            desc.setKdCode(code);
            desc.setFhTime(new Date());
            descDao.update(desc);
            if (descDao.checkKdCode(code))
            {
                Random random = new Random();
                int randomInt = random.nextInt(1000);
                code = code + randomInt;
            }
            uploadExpressShippingInfo(order, logistics, code);
        }
    }
    
    // 处理商城已发货 微信发货失败的情况
    public void sendOrderTest(Integer pkey, String logistics, String code)
    {
        MktOrder order = orderDao.get(pkey);
        MktOrderDesc desc = descDao.get(pkey);
        if (desc != null)
        {
            if (descDao.checkKdCode(code))
            {
                Random random = new Random();
                int randomInt = random.nextInt(1000);
                code = code + randomInt;
            }
            uploadExpressShippingInfo(order, logistics, code);
        }
    }
    
    private void uploadExpressShippingInfo(MktOrder order, String logistics, String code)
    {
        uploadExpressShippingInfo(order, logistics, code, null);
    }
    
    private void uploadExpressShippingInfo(MktOrder order, String logistics, String code, String consignorContact)
    {
        String expressCompany;
        switch (logistics)
        {
            case "顺丰快递":
                expressCompany = "SF";
                break;
            case "申通快递":
                expressCompany = "STO";
                break;
            case "ems":
                expressCompany = "YZPY";
                break;
            case "京东快递":
                expressCompany = "JD";
                break;
            case "圆通快递":
                expressCompany = "YTO";
                break;
            case "韵达快递":
                expressCompany = "YDKY";
                break;
            default:
                expressCompany = "YZPY";
                break;
        }
        try
        {
            StringBuilder sb = new StringBuilder();
            List<MktOrderLine> listOrder = orderLineDao.listOrder(order.getPkey());
            for (MktOrderLine s : listOrder)
            {
                sb.append(s.getGoodsName());
                sb.append("(");
                sb.append(s.getSpaceName());
                sb.append(")*");
                sb.append(s.getNum());
                sb.append(", ");
            }
            String itemDesc = sb.toString();
            if (!itemDesc.isEmpty()) itemDesc = itemDesc.substring(0, itemDesc.length() - 1);
            if (itemDesc.length() > 120) itemDesc = itemDesc.substring(0, 120);
            String openid = null;
            String mchid = null;
            SysAscription sysAscription = ascriptionDao.get(order.getAscription());
            if (sysAscription != null)
            {
                mchid = sysAscription.getConfigMchid();
            }
            MktMember mktMember = memberDao.get(order.getMember());
            if (mktMember != null) openid = mktMember.getOpenid1();
            if (openid != null && mchid != null)
            {
                if(qfAscription == order.getAscription())
                {
                    String orderCode = order.getCode();
                    orderCode = orderCode.substring(0, 14);
                    ThirdPayLineEntity entity = thirdPayLineDao.byMerOrderId(orderCode);
                    if(entity != null)
                    {
                        wxManager.uploadShippingInfo(entity.getTargetOrderId(), 
                            null,
                            null,
                            itemDesc,
                            2,
                            null,
                            null,
                            null,
                            null,
                            openid,
                            order.getAscription());
                    }
                }
                else
                {
                    wxManager.uploadShippingInfo(null,
                        order.getCode(),
                        mchid,
                        itemDesc,
                        1,
                        code,
                        expressCompany,
                        consignorContact,
                        null,
                        openid,
                        order.getAscription());
                }
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            log.error("微信确认收货报错");
        }
    }
    
    public List<MktCourier> queryCourier()
    {
        return courierDao.select()
            .eq("farmer", CurrentSession.marketPkey())
            .eq("enabled", true)
            .eq("idDel", false)
            .exec();
    }
    
    /*
     * 获取团购订单列表
     */
    public PageResult<MktOrderGroupOnList> queryOrderGroup(int page, int pagesize, Integer goods,
        OrderGroupStatus status)
    {
        List<MktGoods> list = goodsDao.select().eq("farmer", CurrentSession.marketPkey()).exec();
        List<Integer> goodsIds = new ArrayList<>();
        for (MktGoods mg : list)
            goodsIds.add(mg.getPkey());
        PageResult<MktOrderGroup> pageResult = orderGroupDao.queryOrder(page, pagesize, goods, status, goodsIds);
        PageResult<MktOrderGroupOnList> result = BeanUtil.beanPageFrom(MktOrderGroupOnList.class, pageResult);
        for (MktOrderGroupOnList bean : result.getContent())
        {
            MktGoods mktGoods = goodsDao.get(bean.getGoods());
            if (mktGoods != null) bean.setGoodsName(mktGoods.getTitle());
            bean.setStatusName(bean.getStatus().getName());
        }
        return result;
    }
    
    public Map<String, Object> queryOrderSum(OrderOir orderOir, String startDate, String endDate, OrderStatus status,
        String code, String mobile, String memberMobile, OrderType orderType, Integer groupPkey, String vrifyCode,
        Boolean priceAbnormal, Boolean priceAbnormalFinsh, String farmer, List<PayType> payTypes, List<Integer> tags,
        DistributionType distributionType)
    {
        Map<String, Object> result = new HashMap<>();
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> members = null;
        if (StringUtils.isNotBlank(mobile))
        {
            List<MktOrderDesc> exec =
                descDao.select().eq("ascription", CurrentSession.ascriptionPkey()).like("mobile", mobile).exec();
            if (exec.isEmpty()) return result;
            for (MktOrderDesc od : exec)
                orderIds.add(od.getPkey());
        }
        if(tags != null && !tags.isEmpty())
        {
            List<MktOrderTag> list = orderTagDao.listTag(tags, CurrentSession.ascriptionPkey());
            if(list == null || list.isEmpty())
                return result;
            for (MktOrderTag ot : list)
                orderIds.add(ot.getOrderPkey());
        }
        if (StringUtil.isNotBlank(memberMobile))
        {
            members = memberDao.select().like("mobile", memberMobile).execDto("pkey", Integer.class);
        }
        if (priceAbnormal && priceAbnormalFinsh)
        {
            List<MktVendorOrder> exec = vendorOrderDao.select()
                .eq("farmer", farmer)
                .or()
                .eq("priceStatus", PriceStatus.ABNORMAL)
                .eq("priceStatus", PriceStatus.ABNORMAL_FINISH)
                .close()
                .done()
                .exec();
            if (exec.isEmpty()) return result;
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        else if (priceAbnormal)
        {
            List<MktVendorOrder> exec =
                vendorOrderDao.select().eq("priceStatus", PriceStatus.ABNORMAL).eq("farmer", farmer).exec();
            if (exec.isEmpty()) return result;
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        else if (priceAbnormalFinsh)
        {
            List<MktVendorOrder> exec =
                vendorOrderDao.select().eq("priceStatus", PriceStatus.ABNORMAL_FINISH).eq("farmer", farmer).exec();
            if (exec.isEmpty()) return result;
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        if (!orderIds.isEmpty())
        {
            orderIds = orderIds.stream().distinct().collect(Collectors.toList());
        }
        
        AggregationBuilder<Integer, MktOrder> builder = orderDao.aggregation()
            .notEq("status", OrderStatus.VOID_ORDER)
            .eq("farmer", farmer)
            .eq("distributionType", distributionType)
            .notEq("status", OrderStatus.VOID_ORDER)
            .eq("orderOir", orderOir)
            .in("member", members)
            .like("pickupCode", vrifyCode)
            .in("payType", payTypes)
            .sort("pkey", true);
        
        orderDao.assemblyBuilder(builder,
            orderOir,
            startDate,
            endDate,
            status,
            code,
            orderIds,
            orderType,
            groupPkey,
            false);
        result.put("sum", builder.execSum("amtn"));
        result.put("count", builder.execCount("pkey"));
        return result;
    }
    
    public Boolean newOrder()
    {
        String marketPkey = CurrentSession.marketPkey();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, -30);
        long l = orderDao.aggregation()
            .eq("farmer", marketPkey)
            .eq("status", OrderStatus.DELIVERED_ORDER)
            .between("createdTime", c.getTime(), new Date())
            .execCount();
        return l > 0;
    }
    
    public Integer voiceOrder()
    {
        String marketPkey = CurrentSession.marketPkey();
        Calendar c = Calendar.getInstance();
        Date time = c.getTime();
        c.add(Calendar.SECOND, -15);
        List<OrderStatus> statusList = new ArrayList<>();
        statusList.add(OrderStatus.DELIVERED_ORDER);
        statusList.add(OrderStatus.SHIPPED_ORDER);
        statusList.add(OrderStatus.WAIT_ARRIVAL_ORDER);
        statusList.add(OrderStatus.WAIT_WRITEOFF_ORDER);
        statusList.add(OrderStatus.ARRIVED_ORDER);
        statusList.add(OrderStatus.CONFIRM_ORDER);
        long l = orderDao.aggregation()
            .eq("farmer", marketPkey)
            .in("status", statusList.toArray())
            .between("createdTime", c.getTime(), time)
            .execCount();
        return (int)l;
    }
    
    public Integer pendingOrder()
    {
        String marketPkey = CurrentSession.marketPkey();
        long l = orderDao.aggregation().eq("farmer", marketPkey).eq("status", OrderStatus.DELIVERED_ORDER).execCount();
        return (int)l;
    }
    
    public Boolean updatePickupCodeStatus(Integer pkey)
    {
        
        MktOrder morder = orderDao.get(pkey);
        if (morder != null)
        {
            morder.setStatus(OrderStatus.ARRIVED_ORDER);
            morder.setPickupFlag(true);
            morder.setPickupTime(DateUtil.formatDate(new Date()));
            orderDao.update(morder);
            
            uploadShippingInfo(morder, 4);
        }
        return true;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean deliverSf(Integer pkey, Date pickupTime, String sendContent)
    {
        MktOrder order = orderDao.get(pkey);
        if (order == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if ((order.getOrderType() != OrderType.INTEGRAL_PRESALE_ORDER && order.getOrderType() != OrderType.INTEGRAL_ORDER) || order.getOrderOir() != OrderOir.POINTS_MALL)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "当前订单暂不支持顺丰发货");
        if (order.getStatus() != OrderStatus.DELIVERED_ORDER)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "当前订单状态不允许发货");
        // 获取供应商配置
        Integer supplierPkey = order.getSupplier();
        if (supplierPkey == null) throw TofocusException.of(LejiaErrCode.SUPPLIER_NOT_FOUND, "没有供应商信息");
        MktSupplier supplier = supplierDao.get(supplierPkey);
        if (supplier == null) throw TofocusException.of(LejiaErrCode.SUPPLIER_NOT_FOUND);
        // 供应商编辑新增已经控制一旦输入一项快递相关，其他都得输入，这里仅判断三个顺丰直接相关的参数
        if (StringUtil.isBlank(supplier.getSfMonthlyCard()) || StringUtil.isBlank(supplier.getSfAppId())
            || StringUtil.isBlank(supplier.getSfSk()))
            throw TofocusException.of(LejiaErrCode.SUPPLIER_NO_EXPRESS_CONFIG);
        // 收件信息
        MktOrderDesc desc = descDao.get(pkey);
        if (desc == null) throw TofocusException.of(LejiaErrCode.RECEIVE_INFO_NOT_FOUND);
        if (StringUtil.isBlank(desc.getName()) || StringUtil.isBlank(desc.getMobile())
            || StringUtil.isBlank(desc.getPro()) || StringUtil.isBlank(desc.getCity())
            || StringUtil.isBlank(desc.getAddr()))
            throw TofocusException.of(LejiaErrCode.RECEIVE_INFO_NOT_FOUND, "收货信息不完整，无法发货");
        // 订单明细
        List<MktOrderLine> orderLines = orderLineDao.listOrder(pkey);
        StringBuilder goodsDetail = new StringBuilder();
        int sendNum = 0;
        for (int i = 0; i < orderLines.size(); i++)
        {
            MktOrderLine line = orderLines.get(i);
            goodsDetail.append(i + 1).append(".").append(line.getGoodsName()).append("(");
            MktGoodsSpace space = goodsSpaceDao.get(line.getSpace().intValue());
            if (space != null)
            {
                goodsDetail.append(space.getSpace());
            }
            goodsDetail.append(")*").append(line.getNum()).append(";");
            sendNum += line.getNum();
        }
        String expressNo = numberUtils.createOrderExpressNo();
        // 发起顺丰下单
        String remark = StringUtil.limitString(goodsDetail.toString(), 100);
        SfPlaceOrderResult placeOrderRes = expressSfManager.placeOrder(supplier.getSfAppId(),
            supplier.getSfSk(),
            supplier.getSfMonthlyCard(),
            expressNo,
            pickupTime,
            supplier.getExpressSender(),
            supplier.getExpressMobile(),
            supplier.getExpressPro(),
            supplier.getExpressCity(),
            supplier.getExpressArea() + supplier.getExpressAddress(),
            desc.getName(),
            desc.getMobile(),
            desc.getPro(),
            desc.getCity(),
            desc.getAddr(),
            sendContent,
            sendNum,
            remark);
        // 生成物流单
        ExpressCompany expressCompany = ExpressCompany.SF;
        MktOrderExpress express = new MktOrderExpress();
        express.setExpressNo(expressNo);
        express.setOrderPkey(order.getPkey());
        express.setKcCode(order.getCode());
        express.setExpressCompany(expressCompany);
        express.setPickupTime(pickupTime);
        express.setSendContent(sendContent);
        express.setSendNum(sendNum);
        express.setSfMonthlyCard(supplier.getSfMonthlyCard());
        express.setStatus(OrderExpressStatus.ORDERED);
        express.setFarmer(order.getFarmer());
        express.setCompany(order.getCompany());
        express.setAscription(order.getAscription());
        // 目前只支持单票运单，只管母单
        String waybillNo = getMasterWaybillNo(placeOrderRes.getWaybillNoInfos());
        express.setWaybillNo(waybillNo);
        orderExpressDao.add(express);
        // 修改订单状态及信息
        order.setStatus(OrderStatus.SHIPPED_ORDER);
        order.setExpressType(ExpressType.EXPRESS_SF);
        orderDao.update(order);
        desc.setLogistics(expressCompany.getName());
        desc.setKdCode(waybillNo);
        desc.setFhTime(new Date());
        descDao.update(desc);
        uploadExpressShippingInfo(order, expressCompany.getName(), waybillNo, supplier.getExpressMobile());
        return true;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelDelivery(Long pkey)
    {
        // 查物流单
        MktOrderExpress orderExpress = orderExpressDao.get(pkey);
        if (orderExpress == null) throw TofocusException.of(LejiaErrCode.ORDER_EXPRESS_NOT_FOUND);
        if (orderExpress.getStatus() != OrderExpressStatus.ORDERED)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "当前物流状态不允许取消发货");
        // 查订单
        MktOrder order = orderDao.get(orderExpress.getOrderPkey());
        if (order == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if (order.getStatus() != OrderStatus.SHIPPED_ORDER)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "当前订单状态不允许取消发货");
        if (order.getExpressType() != ExpressType.EXPRESS_SF)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "不是在线顺丰发货，不支持取消");
        // 获取供应商，取顺丰参数
        Integer supplierPkey = order.getSupplier();
        MktSupplier supplier = supplierDao.get(supplierPkey);
        if (supplier == null) throw TofocusException.of(LejiaErrCode.SUPPLIER_NOT_FOUND);
        // 收件信息
        MktOrderDesc desc = descDao.get(order.getPkey());
        if (desc == null) throw TofocusException.of(LejiaErrCode.RECEIVE_INFO_NOT_FOUND);
        // 顺丰取消订单
        SfCancelOrderResult cancelOrderResult =
            expressSfManager.cancelOrder(supplier.getSfAppId(), supplier.getSfSk(), orderExpress.getExpressNo());
        if (!cancelOrderResult.isSuccess()) throw TofocusException.of(LejiaErrCode.EXPRESS_CANCEL_ERROR);
        // 退回订单状态
        orderExpress.setStatus(OrderExpressStatus.CANCELED);
        orderExpress.setErrorMsg("手动取消");
        orderExpressDao.update(orderExpress);
        order.setStatus(OrderStatus.DELIVERED_ORDER);
        order.setExpressType(null);
        orderDao.update(order);
        desc.setLogistics(null);
        desc.setKdCode(null);
        desc.setFhTime(null);
        descDao.update(desc);
        return true;
    }
    
    // 退款的时候 取消顺丰
    public boolean cancelDeliveryRefund(MktOrderExpress orderExpress)
    {
        // 查物流单
        if (orderExpress == null) throw TofocusException.of(LejiaErrCode.ORDER_EXPRESS_NOT_FOUND);
        if (orderExpress.getStatus() != OrderExpressStatus.ORDERED)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "当前物流状态不允许取消发货");
        // 查订单
        MktOrder order = orderDao.get(orderExpress.getOrderPkey());
        if (order == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
//        if (order.getStatus() != OrderStatus.SHIPPED_ORDER)
//            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "当前订单状态不允许取消发货");
        if (order.getExpressType() != ExpressType.EXPRESS_SF)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "不是在线顺丰发货，不支持取消");
        // 获取供应商，取顺丰参数
        Integer supplierPkey = order.getSupplier();
        MktSupplier supplier = supplierDao.get(supplierPkey);
        if (supplier == null) throw TofocusException.of(LejiaErrCode.SUPPLIER_NOT_FOUND);
        // 收件信息
        MktOrderDesc desc = descDao.get(order.getPkey());
        if (desc == null) throw TofocusException.of(LejiaErrCode.RECEIVE_INFO_NOT_FOUND);
        // 顺丰取消订单
        SfCancelOrderResult cancelOrderResult =
            expressSfManager.cancelOrder(supplier.getSfAppId(), supplier.getSfSk(), orderExpress.getExpressNo());
        if (!cancelOrderResult.isSuccess()) throw TofocusException.of(LejiaErrCode.EXPRESS_CANCEL_ERROR);
        // 退回订单状态
        orderExpress.setStatus(OrderExpressStatus.CANCELED);
        orderExpress.setErrorMsg("退款的时候取消");
        orderExpressDao.update(orderExpress);
        order.setStatus(OrderStatus.REFUNDED_ORDER);
        order.setExpressType(null);
        orderDao.update(order);
        desc.setLogistics(null);
        desc.setKdCode(null);
        desc.setFhTime(null);
        descDao.update(desc);
        return true;
    }
    
    private String getMasterWaybillNo(List<SfWaybillNoInfo> waybillNoInfoList)
    {
        // 目前只支持单票运单，只管母单
        String waybillNo = null;
        if (waybillNoInfoList.size() == 1)
        {
            waybillNo = waybillNoInfoList.get(0).getWaybillNo();
        }
        else
        {
            for (SfWaybillNoInfo waybillNoInfo : waybillNoInfoList)
            {
                if (waybillNoInfo.isMaster())
                {
                    waybillNo = waybillNoInfo.getWaybillNo();
                    break;
                }
            }
        }
        return waybillNo;
    }
    
    public Boolean printOrderWx(Integer pkey, Boolean electronicScale, List<MktVendorOrder> voList)
    {
        // 2秒内不给重复点手动打印
        String mapKey = "printOrder:" + pkey;
        Long ll = orderPrintMap.get(mapKey);
        if (ll != null && System.currentTimeMillis() - ll.longValue() < 2000)
        {
            orderPrintMap.put(mapKey, System.currentTimeMillis());
            throw TofocusException.of(LejiaErrCode.CLICK_FAST_ERROR);
        }
        orderPrintMap.put(mapKey, System.currentTimeMillis());
        
        MktOrder order = orderDao.get(pkey);
        Integer ascription = order.getAscription();
        SysFarmerExtend fe = farmerExtendDao.get(order.getFarmer());
        if (fe == null || StringUtils.isBlank(fe.getPrintCode())) return true;
        //        if (ascription != 8 && ascription != 22 && ascription != 13 && ascription != 14) return true;
        // 获取小票码
        String maxNum;
        if (StringUtils.isNotBlank(order.getPickupCode()))
        {
            maxNum = order.getPickupCode();
//            if (DistributionType.PICKUP.equals(order.getDistributionType()))
//            {
//                maxNum = maxNum.substring(1, maxNum.length());
//            }
        }
        else
        {
            if (order.getSmallTicket() == null)
            {
                int num = orderDao.getOrderPrintMaxNum(order.getFarmer(),
                    DateUtil.atStartOfDay(order.getCreatedTime()),
                    DateUtil.atEndOfDay(order.getCreatedTime()));
                num += 1;
                order.setSmallTicket(num);
                orderDao.update(order);
            }
            maxNum = order.getSmallTicket().toString();
        }
        SysFarmer farmer = sysFarmerDao.get(order.getFarmer());
        // 电子秤打印
        if (Boolean.TRUE.equals(electronicScale))
            printOrderElectronicScale(order, maxNum, farmer.getName());
        // 蓝牙打印机打印
        printOrderBluetoothPrinter(order, maxNum, farmer.getName(), farmer.getTel(), fe, voList);
        return true;
    }
    
    public Boolean printOrder(Integer pkey, Boolean electronicScale)
    {
        // 2秒内不给重复点手动打印
        String mapKey = "printOrder:" + pkey;
        Long ll = orderPrintMap.get(mapKey);
        if (ll != null && System.currentTimeMillis() - ll.longValue() < 2000)
        {
            orderPrintMap.put(mapKey, System.currentTimeMillis());
            throw TofocusException.of(LejiaErrCode.CLICK_FAST_ERROR);
        }
        orderPrintMap.put(mapKey, System.currentTimeMillis());
        
        MktOrder order = orderDao.get(pkey);
        Integer ascription = order.getAscription();
        SysFarmerExtend fe = farmerExtendDao.get(order.getFarmer());
        if (fe == null || StringUtils.isBlank(fe.getPrintCode())) return true;
        //        if (ascription != 8 && ascription != 22 && ascription != 13 && ascription != 14) return true;
        // 获取小票码
        String maxNum;
        if (StringUtils.isNotBlank(order.getPickupCode()))
        {
            maxNum = order.getPickupCode();
//            if (DistributionType.PICKUP.equals(order.getDistributionType()))
//            {
//                maxNum = maxNum.substring(1, maxNum.length());
//            }
        }
        else
        {
            if (order.getSmallTicket() == null)
            {
                int num = orderDao.getOrderPrintMaxNum(order.getFarmer(),
                    DateUtil.atStartOfDay(order.getCreatedTime()),
                    DateUtil.atEndOfDay(order.getCreatedTime()));
                num += 1;
                order.setSmallTicket(num);
                orderDao.update(order);
            }
            maxNum = order.getSmallTicket().toString();
        }
        SysFarmer farmer = sysFarmerDao.get(order.getFarmer());
        // 电子秤打印
        if (Boolean.TRUE.equals(electronicScale))
            printOrderElectronicScale(order, maxNum, farmer.getName());
        // 蓝牙打印机打印
        printOrderBluetoothPrinter(order, maxNum, farmer.getName(), farmer.getTel(), fe, null);
        return true;
    }
    
    // 蓝牙打印机打印
    private void printOrderBluetoothPrinter(MktOrder order, String maxNum, String farmerName, String marketMobile,
        SysFarmerExtend fe, List<MktVendorOrder> voList)
    {
        // String printCode
        Boolean dineIn = false;
        if (DistributionType.DINE_IN.equals(order.getDistributionType())) dineIn = true;
        // 获取客户联数据
        CustomerPrintBean cpb = xiyeCustomerPrint(order, maxNum, farmerName, marketMobile, voList);
        log.info("cpb: {}", JsonUtil.toString(cpb, true));
        // 单独打印
        //        xiyeCloudPrint.xiyeCustomerPrint(cpb);
        // 拣货联数据
        XiyePrintDeliveryBean xpd = BeanUtil.beanFrom(XiyePrintDeliveryBean.class, cpb);
        List<PrintOriInfo> ori = cpb.getOri();
        List<Integer> keys = new ArrayList<>();
        ori.forEach(e -> {
            if (e.getVendor() != null) keys.add(e.getVendor());
        });
        Map<Integer, XiyeDeliveryMerhantGoodsBean> map = new HashMap<>();
        Map<Integer, MktVendor> mapVendor = vendorDao.getMapVendor(keys);
        for (PrintOriInfo po : ori)
        {
            if (mapVendor.containsKey(po.getVendor()))
            {
                MktVendor vendor = mapVendor.get(po.getVendor());
                if (!map.containsKey(vendor.getPkey()))
                {
                    XiyeDeliveryMerhantGoodsBean xdmgb = new XiyeDeliveryMerhantGoodsBean();
                    xdmgb.setMerchantName(vendor.getDisplayName());
                    if (StringUtils.isNotBlank(vendor.getBooth()))
                    {
                        String[] split = vendor.getBooth().split("/");
                        xdmgb.setBooth(split[0]);
                        if (split.length > 1)
                        {
                            xdmgb.setArea(split[0]);
                            xdmgb.setBooth(split[1]);
                        }
                    }
                    if (xdmgb.getBooth() == null) xdmgb.setBooth("");
                    if (xdmgb.getArea() == null) xdmgb.setArea("");
                    xdmgb.setGoodsCount(0);
                    List<PrintOriInfo> xdmgbOri = new ArrayList<>();
                    xdmgb.setOri(xdmgbOri);
                    map.put(vendor.getPkey(), xdmgb);
                }
                XiyeDeliveryMerhantGoodsBean xdmgb = map.get(vendor.getPkey());
                xdmgb.getOri().add(po);
                xdmgb.setGoodsCount(xdmgb.getGoodsCount() + po.getCount());
            }
        }
        xpd.setMerchantGoods(new ArrayList<>(map.values()));
        // 单独打印
        //        xiyeCloudPrint.xiyeDeliveryPrint(xpd);
        // 合并打印
        //        if (ascription == 8)
        //        {
        //            if ("zy_mkt_0028".equals(order.getFarmer()))
        //                xiyeCloudPrint.xiyePrint(xpd, cpb, "74L9G7UB046194B", dineIn);
        //            else if ("zy_mkt_0032".equals(order.getFarmer()))
        //                xiyeCloudPrint.xiyePrint(xpd, cpb, "749STDJDRY6614B", dineIn);
        //            else if("zy_mkt_0017".equals(order.getFarmer()))
        //                xiyeCloudPrint.xiyePrint(xpd, cpb, "74HM92LFHT0CF4A", dineIn);
        //            else if("zy_mkt_0041".equals(order.getFarmer()))
        //                xiyeCloudPrint.xiyePrint(xpd, cpb, "74P8Q6A9X79324B", dineIn);
        //            else if("zy_mkt_0042".equals(order.getFarmer()))
        //                xiyeCloudPrint.xiyePrint(xpd, cpb, "743WW2JZV71B44B", dineIn);
        //            else if("zy_mkt_0044".equals(order.getFarmer()))
        //                xiyeCloudPrint.xiyePrint(xpd, cpb, "74ML9LJVCAE3448", dineIn);
        //            else if("zy_mkt_0035".equals(order.getFarmer()))
        //                xiyeCloudPrint.xiyePrint(xpd, cpb, "74AEFYCGGD7A849", dineIn);
        //        }
        //        else if (ascription == 13)
        //        {
        //            if("zy_mkt_0046".equals(order.getFarmer()))
        //                xiyeCloudPrint.xiyePrint(xpd, cpb, "74BJL8L00C57148", dineIn);
        //        }
        //        else if (ascription == 14)
        //        {
        //            if("zy_mkt_0040".equals(order.getFarmer()))
        //                xiyeCloudPrint.xiyePrint(xpd, cpb, "74CRU0KJFQ4A549", dineIn);
        //        }
        //        else if (ascription == 22)
        //        {
        //            xiyeCloudPrint.xiyePrint(xpd, cpb, "74UNTMR9NT36248", dineIn);
        //        }
        cpb.setContent(fe.getContent());
        cpb.setPhoto1(fe.getPhoto1());
        cpb.setPhoto1Text(fe.getPhoto1Text());
        cpb.setPhoto2(fe.getPhoto2());
        cpb.setPhoto2Text(fe.getPhoto2Text());
        xiyeCloudPrint.xiyePrint(xpd, cpb, fe.getPrintCode(), dineIn);
    }
    
    // 客户联打印
    private CustomerPrintBean xiyeCustomerPrint(MktOrder order, String maxNum, String farmerName, String marketMobile, List<MktVendorOrder> voList)
    {
        CustomerPrintBean cpb = new CustomerPrintBean();
        cpb.setStatus(order.getStatus());
        cpb.setMarketName(farmerName);
        cpb.setQrcode("https://small.xinanshizu.com/shouye");
        cpb.setOrderNumber(order.getCode());
        cpb.setOrderTime(DateUtil.formatDate(order.getCreatedTime(), "yyyy-MM-dd HH:mm:ss"));
        cpb.setOrderTrace(maxNum + "");
        cpb.setPickUp("配送");
        cpb.setRefundAmt(order.getRefundAmt() == null ? BigDecimal.ZERO : order.getRefundAmt());
        cpb.setReceivedTime(order.getPstime());
        switch (order.getDistributionType())
        {
            case IMMEDIATELY:
                cpb.setDeliveryMode("立即送达");
                break;
            case ORDERED:
                cpb.setDeliveryMode("预约送达");
                break;
            case PICKUP:
                cpb.setPickUp("自提");
                cpb.setDeliveryMode("自提时间");
                break;
            case DINE_IN:
                cpb.setPickUp("堂食");
                cpb.setDeliveryMode("");
                cpb.setReceivedTime("");
                break;
            default:
                break;
        }
        cpb.setMarketMobile(marketMobile);
        
        MktOrderDesc orderDesc = descDao.get(order.getPkey());
        if (orderDesc != null)
        {
            cpb.setRemarket(orderDesc.getRemark());
            cpb.setAddress(
                maskAddress(orderDesc.getPro(), orderDesc.getCity(), orderDesc.getArea(), orderDesc.getAddr()));
            if (!DistributionType.DINE_IN.equals(order.getDistributionType()))
            {
                cpb.setName(desensitizeName(orderDesc.getName()));
                cpb.setMobile(maskPhone(orderDesc.getMobile()));
            }
            else
            {
                MktMember member = memberDao.get(order.getMember());
                if (member != null) cpb.setMobile(maskPhone(member.getMobile()));
                cpb.setName("会员用户");
            }
        }
        if (cpb.getRemarket() == null) cpb.setRemarket("");
        cpb.setDeliveryFee(order.getPostage());
        cpb.setDiscountAmt(order.getCardAmt());
        BigDecimal goodsAmt = BigDecimal.ZERO;
        Integer totalCount = 0;
        List<PrintOriInfo> ori = new ArrayList<>();
        List<MktVendorOrder> vendorOrder;
        if(voList != null && !voList.isEmpty())
            vendorOrder = voList;
        else
            vendorOrder = vendorOrderDao.listOrder(order.getPkey());
        for (MktVendorOrder vo : vendorOrder)
        {
            PrintOriInfo po = new PrintOriInfo();
            po.setGoodsName(vo.getGoodsName());
            MktOrderLine mktOrderLine = orderLineDao.get(vo.getOrderLinePkey());
            po.setSpecifications(vo.getSpaceName());
            if (mktOrderLine != null && mktOrderLine.getAssociation() != null)
            {
                po.setSpecifications(mktOrderLine.getAssociationName());
            }
            po.setCount(vo.getNum());
            po.setGoodsPrice(vo.getGoodsPrice().setScale(2));
            po.setPrice(vo.getGoodsPrice().setScale(2));
            po.setTotalPrice(vo.getGoodsPrice().multiply(BigDecimal.valueOf(po.getCount())).setScale(2));
            po.setGoodsAmt(po.getGoodsPrice().multiply(BigDecimal.valueOf(po.getCount())));
            po.setVendor(vo.getVendor());
            po.setAmt(vo.getAmt() == null ? BigDecimal.ZERO : vo.getAmt());
            po.setRefundAmt(vo.getRefundAmt() == null ? BigDecimal.ZERO : vo.getRefundAmt());
            po.setProcureRefundAmt(vo.getProcureRefundAmt() == null ? BigDecimal.ZERO : vo.getProcureRefundAmt());
            ori.add(po);
            totalCount += vo.getNum();
            goodsAmt = goodsAmt.add(po.getGoodsAmt());
        }
        cpb.setOri(ori);
        cpb.setGoodsAmt(goodsAmt);
        cpb.setTotalCount(totalCount);
        cpb.setTotalAmt(goodsAmt.subtract(cpb.getDiscountAmt()).add(cpb.getDeliveryFee()));
        // 客户联打印
        //        xiyeCloudPrint.xiyeCustomerPrint(cpb);
        return cpb;
    }
    
    private String desensitizeName(String name)
    {
        if (StringUtils.isBlank(name)) return "***";
        if (name.length() <= 1) return name + "**";
        String firstName = name.substring(0, 1);
        return firstName + "**";
    }
    
    private String maskPhone(String value)
    {
        if (value != null)
        {
            if (value.length() == 11)
            {
                return StringUtil.mask(value, 3, value.length() - 5);
            }
            else
                return "********";
        }
        return null;
    }
    
    private static String maskAddress(String pro, String city, String area, String address)
    {
        if (address != null)
        {
            int prefixLen = 0;
            if (pro != null)
                prefixLen += pro.length();
            if (city != null)
                prefixLen += city.length();
            if (area != null)
                prefixLen += area.length();
            int noMaskLen = prefixLen + (address.length() - prefixLen) / 3;
            System.out.println(noMaskLen);
            return StringUtil.left(address, Math.min(noMaskLen, 11)) + "********";
        }
        return null;
    }
    
    // 电子秤打印
    private void printOrderElectronicScale(MktOrder order, String maxNum, String farmerName)
    {
        
        String tradeTime = DateUtil.formatDate(order.getCreatedTime(), "yyyyMMddHHmmss");
        List<MktVendorOrder> vendorOrder = vendorOrderDao.listOrder(order.getPkey());
        Map<Integer, List<MktVendorOrder>> map = new HashMap<>();
        for (MktVendorOrder vo : vendorOrder)
        {
            if (!map.containsKey(vo.getVendor()))
            {
                List<MktVendorOrder> v = new ArrayList<>();
                map.put(vo.getVendor(), v);
            }
            map.get(vo.getVendor()).add(vo);
        }
        String token = getToken();
        
        for (Map.Entry<Integer, List<MktVendorOrder>> entry : map.entrySet())
        {
            MktVendor vendor = vendorDao.get(entry.getKey());
            if (vendor == null || vendor.getMerchant() == null) continue;
            ScalePrintInfo print = new ScalePrintInfo();
            print.setTradeTime(tradeTime);
            print.setOrderNo(order.getCode());
            print.setOrderTrace(maxNum);
            print.setMerchant(vendor.getMerchant());
            print.setMarketName(farmerName);
            print.setMerchantName(vendor.getDisplayName());
            print.setBooth(vendor.getBooth());
            BigDecimal orderAmt = BigDecimal.ZERO;
            List<ScalePrintOriIfo> ori = new ArrayList<>();
            for (MktVendorOrder vo : entry.getValue())
            {
                ScalePrintOriIfo sp = new ScalePrintOriIfo();
                sp.setGoodsName(vo.getGoodsName());
                sp.setSpecifications(vo.getSpaceName());
                sp.setGoodsWeight(new BigDecimal(vo.getNum()));
                sp.setGoodsPrice(vo.getPrice().setScale(2));
                sp.setGoodsAmt(vo.getTotalPrice().setScale(2));
                ori.add(sp);
                orderAmt = orderAmt.add(vo.getTotalPrice());
            }
            print.setOrderAmt(orderAmt);
            print.setOri(ori);
            log.info("打印数据: {}", JsonUtil.toString(print, true));
            // 发送打印
            printVendorOrder(print, token);
        }
        
    }
    
    private Map<Integer, String> printVendorOrder(ScalePrintInfo print, String token)
    {
        try
        {
            String url = getUrl() + Constant.FarmSaas.printVendorOrderUrl;
            return HttpUtil.forResult(url, new ParameterizedTypeReference<Result<Map<Integer, String>>>()
            {
            }).post().body(print).token(token).exec();
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            return null;
        }
    }
    
    private String getUrl()
    {
        String url = null;
        if (prefixUrl.contains("farm-member"))
        {
            url = prefixUrl.replace("farm-member", "");
        }
        else
            url = prefixUrl.replace(":22292", "");
        return url;
    }
    
    private String getToken()
    {
        String url = null;
        if (prefixUrl.contains("farm-member"))
        {
            url = prefixUrl.replace("farm-member", "")
                + "auth/oauth/token?grant_type=client_credentials&client_id=farmSaas&client_secret=ZY652VSFGG";
        }
        else
        {
            url = prefixUrl.replace(":22292", ":21000")
                + "/oauth/token?grant_type=client_credentials&client_id=farmSaas&client_secret=ZY652VSFGG";
        }
        JSONObject json = JSONObject.parseObject(HttpUtil.forString(url).post().exec());
        return json.get("access_token").toString();
    }
    
    // 天津查询 当天是否超过200元的预售订单
    public void presaleOrder()
    {
        LocalDate taskLocalDate = LocalDate.now();
        LocalDate tradeLocalDate = taskLocalDate.minusDays(1);
        String tradeDate = tradeLocalDate.toString();
//        String tradeDate = taskLocalDate.toString();
        System.out.println("计算日期: " + tradeDate);
        BigDecimal amt = orderDao.aggPresaleOrder(tradeDate, qfAscription);
        System.out.println("amt: " + amt);
        if(amt.compareTo(new BigDecimal("200")) >= 0)
        {
            DingTalkWarning.sendMsg("预订单金额: " + amt, "云商城天津客户", profilesActive);
        }
    }
    
    public List<KeyValue<Integer, String>> listPickupLocation(Integer pkey)
    {
        MktOrder order = orderDao.get(pkey);
        if (order == null)
            throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if (order.getStatus() != OrderStatus.DELIVERED_ORDER)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "订单当前状态不支持该操作");
        if (order.getDistributionType() != DistributionType.PICKUP)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "非自提订单不支持该操作");
        List<KeyValue<Integer, String>> list = new ArrayList<>();
        if (order.getOrderOir() != null)
        {
            switch (order.getOrderOir())
            {
                case POINTS_MALL:
                {
                    List<MktSupplierPickupLocation> locations =
                        supplierPickupLocationDao.findBySupplier(order.getSupplier(), order.getAscription());
                    for (MktSupplierPickupLocation location : locations)
                    {
                        list.add(new KeyValue<>(location.getPkey(), location.getAddress()));
                    }
                    break;
                }
                case MARKET_MALL:
                {
                    List<SysFarmerPickupLocation> locations =
                        farmerPickupLocationDao.findByFarmer(order.getFarmer(), order.getAscription());
                    for (SysFarmerPickupLocation location : locations)
                    {
                        list.add(new KeyValue<>(location.getPkey(), location.getAddress()));
                    }
                    break;
                }
                default:
            }
        }
        return list;
    }
    
    public boolean updPickupLocation(Integer pkey, Integer pickupLocation)
    {
        MktOrder order = orderDao.get(pkey);
        if (order == null)
            throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if (order.getStatus() != OrderStatus.DELIVERED_ORDER)
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, "订单当前状态不支持该操作");
        if (order.getDistributionType() != DistributionType.PICKUP)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "非自提订单不支持该操作");
        if (order.getOrderOir() != null)
        {
            MktOrderDesc desc = descDao.get(order.getPkey());
            if (desc == null)
                throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER, "订单信息缺失");
            switch (order.getOrderOir())
            {
                case POINTS_MALL:
                {
                    MktSupplierPickupLocation location = supplierPickupLocationDao.get(pickupLocation);
                    if (location == null || !location.getAscription().equals(order.getAscription()))
                        throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到自提点");
                    if (!location.getSupplier().equals(order.getSupplier()))
                        throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "自提点不属于当前订单供应商");
                    desc.setAddr(location.getAddress());
                    descDao.update(desc);
                    break;
                }
                case MARKET_MALL:
                {
                    SysFarmerPickupLocation location = farmerPickupLocationDao.get(pickupLocation);
                    if (location == null || !location.getAscription().equals(order.getAscription()))
                        throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到自提点");
                    if (!location.getFarmer().equals(order.getFarmer()))
                        throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "自提点不属于当前市场");
                    desc.setAddr(location.getAddress());
                    descDao.update(desc);
                    break;
                }
                default:
            }
        }
        return true;
    }
    
    public void exportOrderLine(int page, int pagesize, OrderOir orderOir, String startDate, String endDate,
        OrderStatus status, String code, String mobile, String memberMobile, OrderType orderType,
        PurchaseStatus purchaseStatus, Integer groupPkey, String vrifyCode, Boolean priceAbnormal,
        Boolean priceAbnormalFinsh, String farmer, Boolean falg, ExpressType expressType, List<PayType> payTypes,
        List<Integer> tags, HttpServletResponse response)
    {
        
        OutputStream out = null;
        Integer ascription = CurrentSession.ascriptionPkey();
        try
        {
            response.setHeader("Content-disposition",
                "attachment;filename=" + java.net.URLEncoder.encode("商城订单明细.xlsx", "UTF-8"));
            out = response.getOutputStream();
            PageResult<MktOrderLineExcel> orders = queryOrderLineExcel(page,
                pagesize,
                orderOir,
                startDate,
                endDate,
                status,
                code,
                mobile,
                memberMobile,
                orderType,
                purchaseStatus,
                groupPkey,
                vrifyCode,
                priceAbnormal,
                priceAbnormalFinsh,
                farmer,
                falg,
                expressType,
                payTypes,
                tags);
            if ((Constant.Operation + ascription).equals(CurrentSession.marketPkey()))
            {
                EasyExcel.write(out, MktOrderLineExcel.class)
                    //.registerWriteHandler(
                    //    new ExcelMergeColStrategy(MktOrderLineExcel.class, 1, orders.getNumberOfElements()))
                    .sheet("订单明细")
                    .doWrite(orders.getContent());
            }
            else
            {
                throw TofocusException.of(SysErrCode.UNIMPLENT_FUNCTION);
            }
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private PageResult<MktOrderLineExcel> queryOrderLineExcel(int page, int pagesize, OrderOir orderOir,
        String startDate, String endDate, OrderStatus status, String code, String mobile, String memberMobile,
        OrderType orderType, PurchaseStatus purchaseStatus, Integer groupPkey, String vrifyCode, Boolean priceAbnormal,
        Boolean priceAbnormalFinsh, String farmer, Boolean falg, ExpressType expressType, List<PayType> payTypes,
        List<Integer> tags)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> members = null;
        if (StringUtils.isNotBlank(mobile))
        {
            List<MktOrderDesc> exec = descDao.select().like("mobile", mobile).exec();
            if (exec.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktOrderDesc od : exec)
                orderIds.add(od.getPkey());
        }
        if (tags != null && !tags.isEmpty())
        {
            List<MktOrderTag> list = orderTagDao.listTag(tags, ascriptionPkey);
            if (list == null || list.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktOrderTag ot : list)
                orderIds.add(ot.getOrderPkey());
        }
        if (StringUtil.isNotBlank(memberMobile))
        {
            members = memberDao.select().like("mobile", memberMobile).execDto("pkey", Integer.class);
        }
        if (priceAbnormal && priceAbnormalFinsh)
        {
            List<MktVendorOrder> exec = vendorOrderDao.select()
                .eq("farmer", farmer)
                .eq("ascription", ascriptionPkey)
                .or()
                .eq("priceStatus", PriceStatus.ABNORMAL)
                .eq("priceStatus", PriceStatus.ABNORMAL_FINISH)
                .close()
                .done()
                .exec();
            if (exec.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        else if (priceAbnormal)
        {
            List<MktVendorOrder> exec = vendorOrderDao.select()
                .eq("priceStatus", PriceStatus.ABNORMAL)
                .eq("farmer", farmer)
                .eq("ascription", ascriptionPkey)
                .exec();
            if (exec.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        else if (priceAbnormalFinsh)
        {
            List<MktVendorOrder> exec = vendorOrderDao.select()
                .eq("priceStatus", PriceStatus.ABNORMAL_FINISH)
                .eq("farmer", farmer)
                .eq("ascription", ascriptionPkey)
                .exec();
            if (exec.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktVendorOrder e : exec)
                orderIds.add(e.getOrderPkey());
        }
        if (!orderIds.isEmpty())
        {
            orderIds = orderIds.stream().distinct().collect(Collectors.toList());
        }
        
        SubSelectBuilder<SelectPageOps> builder = orderLineDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .as(MktOrderLine.F.pkey)
            .as(MktOrderLine.F.orderPkey)
            .as(MktOrderLine.F.goodsName)
            .as(MktOrderLine.F.spaceName)
            .as(MktOrderLine.F.num)
            .as(MktOrderLine.F.pricen)
            .as(MktOrderLine.F.refundAmt, MktOrderLineExcel.F.lineRefundAmt)
            .as(MktOrderLine.F.point)
            .as(MktOrderLine.F.couponAmt, MktOrderLineExcel.F.lineCouponAmt)
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .as(MktOrder.F.code)
            .as(MktOrder.F.status)
            .as(MktOrder.F.distributionType)
            .as(MktOrder.F.createdTime)
            .as(MktOrder.F.payType)
            .as(MktOrder.F.orderType)
            .as(MktOrder.F.amto)
            .as(MktOrder.F.pointn)
            .as(MktOrder.F.postage)
            .as(MktOrder.F.cardAmt)
            .as(MktOrder.F.cardPostageAmt)
            .as(MktOrder.F.amtall)
            .as(MktOrder.F.amtn)
            .as(MktOrder.F.refundAmt)
            .as(MktOrder.F.refundPoint)
            .as(MktOrder.F.pstime)
            .as(MktOrder.F.pickupTime)
            .as(MktOrder.F.supplier)
            .as(MktOrder.F.member, MktOrderLineExcel.F.memberKey)
            .eq(MktOrder.F.farmer, farmer)
            .eq(MktOrder.F.ascription, ascriptionPkey)
            .eq(MktOrder.F.expressType, expressType)
            .in(MktOrder.F.payType, payTypes)
            .notEq(MktOrder.F.status, OrderStatus.VOID_ORDER)
            .notEq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .eq(MktOrder.F.status, status)
            .eq(MktOrder.F.orderOir, orderOir)
            .in(MktOrder.F.member, members)
            .eq(MktOrder.F.purchaseStatus, purchaseStatus)
            .like(MktOrder.F.pickupCode, vrifyCode);
        if (!orderIds.isEmpty())
            builder.in(MktOrder.F.pkey, orderIds.toArray());
        if (StringUtils.isNotBlank(code))
            builder.like(MktOrder.F.code, code);
        if (orderType != null)
            builder.eq(MktOrder.F.orderType, orderType);
        else
        {
            if (!falg)
                builder.notEq(MktOrder.F.orderType, OrderType.COLLAGE_ORDER);
        }
        if (StringUtil.isNotEmpty(startDate))
            builder.ge(MktOrder.F.createdTime, startDate + " 00:00:00");
        if (StringUtil.isNotEmpty(endDate))
        {
            builder.le(MktOrder.F.createdTime, endDate + " 23:59:59");
        }
        if (groupPkey != null)
        {
            MktOrderGroup group = orderGroupDao.get(groupPkey);
            if (group == null)
                throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
            List<String> orderList = group.getOrderList();
            if (!orderList.isEmpty())
                builder.in(MktOrder.F.pkey, orderList.toArray());
        }
        
        PageResult<MktOrderLineExcel> pageResult = builder.endJoin()
            .sort(MktOrderLine.F.orderPkey, true)
            .sort(MktOrderLine.F.pkey, false)
            .exec(MktOrderLineExcel.class);
        
        boolean flag = false;
        if ((Constant.Operation + ascriptionPkey).equals(CurrentSession.marketPkey()))
        {
            flag = true;
        }
        for (MktOrderLineExcel line : pageResult)
        {
            if (line.getRefundAmt() == null)
                line.setRefundAmt(BigDecimal.ZERO);
            if (line.getRefundPoint() == null)
                line.setRefundPoint(0);
            if (line.getStatus() != null && line.getStatus().getIndex() == 0)
            {
                line.setAmtn(BigDecimal.ZERO);
                line.setPstime("");
                line.setPayType(null);
            }
            if (flag)
            {
                if (!OrderType.INTEGRAL_PRESALE_ORDER.equals(line.getOrderType()))
                    line.setPstime(DateUtil.formatDate(line.getFhTime()));
                else if (line.getFhTime() != null)
                    line.setPstime(DateUtil.formatDate(line.getFhTime()));
            }
            if (line.getLineRefundAmt() != null && line.getLineRefundAmt().compareTo(line.getLineCouponAmt()) == 0)
            {
                List<MktOrderRefundLine> listOrderLinePkey = orderRefundLineDao.listOrderLinePkey(line.getPkey());
                int rp = 0;
                for (MktOrderRefundLine orl : listOrderLinePkey)
                {
                    if (orl.getRefundPoint() != null)
                    {
                        MktOrderRefund or = orderRefundDao.get(orl.getRefundPkey());
                        if (RefundStatus.REFUND_FINAL.equals(or.getStatus()))
                            rp += (orl.getRefundPoint());
                    }
                }
                line.setLineRefundPoint(rp);
            }
            else
            {
                line.setLineRefundPoint(0);
            }
            line.setTagName(orderTagDao.getTagName(line.getOrderPkey()));
        }
        return pageResult;
    }
    
    // 批量自提出货
    public Boolean waitArrival(List<Integer> pkeys)
    {
        List<MktOrder> list = orderDao.select().in("pkey", pkeys).exec();
        for(MktOrder o : list)
        {
            if(!OrderStatus.DELIVERED_ORDER.equals(o.getStatus()) || !OrderType.INTEGRAL_MSD_ORDER.equals(o.getOrderType()))
                throw TofocusException.of(LejiaErrCode.MSD_ORDERSTATUS_WAIT_ERROR);
            o.setStatus(OrderStatus.WAIT_ARRIVAL_ORDER);
        }
        orderDao.updateAll(list);
        return true;
    }

    // 批量自提到货
    public Boolean waitWriteoff(List<Integer> pkeys)
    {
        List<MktOrder> list = orderDao.select().in("pkey", pkeys).exec();
        for(MktOrder o : list)
        {
            if(!OrderStatus.WAIT_ARRIVAL_ORDER.equals(o.getStatus()) || !OrderType.INTEGRAL_MSD_ORDER.equals(o.getOrderType()))
                throw TofocusException.of(LejiaErrCode.MSD_ORDERSTATUS_WAIT_ERROR);
            o.setStatus(OrderStatus.WAIT_WRITEOFF_ORDER);
        }
        orderDao.updateAll(list);
        
        // 发送微信给用户
        for(MktOrder o : list)
        {
            try
            {
                msgSenderTemplate.put("", "", o, new WxMsgSender());
            }
            catch (Exception e)
            {
                log.warn("发送自提消息通知异常", e);
            }
        }
        return true;
    }
    
    class WxMsgSender implements MsgListener<MktOrder, Boolean>
    {

        @Override
        public Boolean handleMessage(String pipeId, String correlationId, MktOrder order)
            throws Exception
        {
            Integer ascription = order.getAscription();
            SysConfigEntity sysConfig;
            MktOrderLine ol = orderLineDao.selectOne().eq("orderPkey", order.getPkey()).exec();
//            MktOrderDesc desc = orderDescDao.get(order.getPkey());
            List<Integer> list = gzhAssociateDao.listTrueAssKeys(order.getFarmer());
            if (list.isEmpty()) return false;
            sysConfig = sysConfigDao.getBean(Constant.SysConfig.TEMPLATE_MARKET_PICK, ascription);
            if (sysConfig == null) return false;
            List<SysFarmerPickupLocation> byFarmer = sysFarmerPickupLocationDao.findByFarmer(order.getFarmer(), ascription);
            String pickAddr = null;
            if(byFarmer != null && !byFarmer.isEmpty())
                pickAddr = byFarmer.get(0).getAddress();
            if(StringUtils.isBlank(pickAddr))
            {
                SysFarmer sysFarmer = sysFarmerDao.get(order.getFarmer());
                pickAddr = sysFarmer.getConfig().getAddr();
            }
            JSONObject data = new WxDataBuilder()
                .param("thing1")
                .value(ol.getGoodsName())
                .param("thing2")
                .value(pickAddr)
                .param("character_string4")
                .value(order.getPickupCode())
                .build();
            MktMember member = memberDao.get(order.getMember());
            AccountEntity account = accountDao.get(ascription, AccountType.USER);
            wxManager.sendWeappSubscribeMessage(account, member.getOpenid1(), sysConfig.getValue(), null, data);
            return true;
        }

        @Override
        public void handleResult(String pipeId, String correlationId, Result<Boolean> result)
            throws Exception
        {
            if (!result.isSuccess())
                log.warn("发送自提消息通知异常, {}", result.getMsg());
            else if(!result.getResult())
                log.warn("秒杀自提消息通知的模板未配置");
        }
    }
    
}
