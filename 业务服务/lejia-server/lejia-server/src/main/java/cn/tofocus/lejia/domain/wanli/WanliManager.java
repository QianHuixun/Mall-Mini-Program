package cn.tofocus.lejia.domain.wanli;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import cn.tofocus.lejia.Constant;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.common.util.Util;
import cn.tofocus.common.util.security.MD5;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonObject;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.wanli.OrderBillingOnInfo;
import cn.tofocus.lejia.bean.dto.wanli.WanliCorrelationInfo;
import cn.tofocus.lejia.bean.dto.wanli.WanliCourierOnInfo;
import cn.tofocus.lejia.bean.entity.market.MktExpress;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDeliveryMsgEntity;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.enums.CancelType;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.ThirdPartyStatus;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktExpressDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDeliveryMsgDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.domain.app.AppExpressManager;
import cn.tofocus.lejia.domain.market.OrderManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.LocationUtils;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class WanliManager
{
    // 正式地址  https://openapi.wlhulian.com/ 
    @Value("${sec.courier.wanli.prefixUrl:https://testapi.wlhulian.com}")
    private String prefixUrl;
    
//    @Value("${sec.courier.wanli.secret:ee8ffe11657a446b9d3becba41544ff6}")
//    private String secret;
    
//    @Value("${sec.courier.wanli.appId:6526390260b2c47d9bccf189}")
//    private String appId;
//    
//    @Value("${sec.courier.wanli.storeId:861968451f8844148f64ae021d2e089b}")
//    private String storeId;
    
//    @Value("${sec.courier.wanli.shopId:3abcd2344bc14c55b94ca4aede99402e}")
//    private String shopId;
    
    @Value("${sec.courier.wanli.config.onOff:false}")
    private Boolean onOff;
    
    @Value("${sec.courier.wanli.ascrPaidan:1,2,9,22}")
    private String ascrPaidan;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktExpressDao expressDao;
    
    @Autowired
    private MktOrderDescDao descDao;
    
    @Autowired
    private SysFarmerConfigDao farmerConfigDao;
    
    //    @Autowired
    //    private OrderRepateCache  orderRepateCache;
    
    @Autowired
    private MktOrderDeliveryMsgDao mktOrderDeliveryMsgDao;
    
    @Autowired
    private AppExpressManager expressManager;
    
    @Autowired
    private OrderManager orderManager;
    
    private WanliCorrelationInfo getInfo(String farmer)
    {
        if (StringUtil.isBlank(farmer))
            throw TofocusException.of(LejiaErrCode.FARMER_CONFIG_NOT_FOUND, "市场不能为空");
        if (farmer.startsWith(Constant.Operation))
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT, "不支持该操作");
        log.info("getInfo-farmer: {}", farmer);
        SysFarmerConfig config = farmerConfigDao.get(farmer);
        if (config == null)
            throw TofocusException.of(LejiaErrCode.FARMER_CONFIG_NOT_FOUND);
        WanliCorrelationInfo res = new WanliCorrelationInfo();
        res.setAppId(config.getWanliAppId());
        res.setSecret(config.getWanliSecret());
        res.setStoreId(config.getStoreId());
        return res;
    }

    
    // 新增门店
    public Boolean storeCreate(String farmer, String contactName, String shopName, String shopAddress, String contactPhone,
        String shopLng, String shopLat)
    {
        WanliCorrelationInfo info = getInfo(farmer);
        
        JsonObject json = new JsonObject();
        json.put("contactName", contactName);
        json.put("outShopId", "ajfahfhqiahfjhafa");
        json.put("callOrderType", 1);
        json.put("shopName", shopName);
        json.put("shopAddress", shopAddress);
        //        json.put("shopAddress", "温州市国家大学科技园孵化器1号楼");
        json.put("cityName", "温州市");
        json.put("industryType", 1);
        json.put("deliverySupplierList", listSupplier(farmer));
        json.put("coordinateType", 0);
        if (StringUtils.isBlank(shopLng))
            shopLng = "120.668";
        json.put("shopLng", shopLng);
        json.put("contactPhone", contactPhone);
        json.put("shopAddressDetail", "1号楼702");
        if (StringUtils.isBlank(shopLat))
            shopLat = "27.94835";
        json.put("shopLat", shopLat);
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getNoStoreIdSign(json.toString(), timestamp, nonce, info.getSecret());
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("sign", sign);
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        String res = HttpUtil.forString(prefixUrl + "/api/v1/store/create").post().body(param.toString()).exec();
        log.info("res: {}", res);
        
        return true;
    }
    
    // 新增发货店铺
    public String shopCreate(String farmer, String shopLng, String shopLat, String shopName, String contactName, String shopAddress,
        String cityName, String contactPhone, String shopAddressDetail)
    {
        WanliCorrelationInfo info = getInfo(farmer);
        
        JsonObject json = new JsonObject();
        json.put("contactName", contactName);
        json.put("outShopId", info.getStoreId());
        
        json.put("callOrderType", 1);
        json.put("shopName", shopName);
        json.put("shopAddress", shopAddress);
        json.put("cityName", cityName);
        json.put("industryType", 1);
        json.put("deliverySupplierList", listSupplier(farmer));
        json.put("coordinateType", 0);
        json.put("shopLng", shopLng);
        json.put("contactPhone", contactPhone);
        json.put("shopAddressDetail", shopAddressDetail);
        json.put("shopLat", shopLat);
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getSign(json.toString(), timestamp, nonce, info);
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("storeId", info.getStoreId());
        param.put("sign", sign);
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        String res = HttpUtil.forString(prefixUrl + "/api/v1/shop/create").post().body(param.toString()).exec();
        log.info("res: {}", res);
        JSONObject parseObject = JSON.parseObject(res);
        JSONObject data = parseObject.getJSONObject("data");
        return data.get("shopId").toString();
    }
    
    public void testH(String farmer, String orderNo, Integer status)
    {
        WanliCorrelationInfo info = getInfo(farmer);
        JsonObject json = new JsonObject();
        json.put("orderNo", orderNo);
        json.put("status", status);
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getNoStoreIdSign(json.toString(), timestamp, nonce, info.getSecret());
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("sign", sign);
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        String res = HttpUtil.forString(prefixUrl + "/api/v1/order/test").post().body(param.toString()).exec();
        log.info("res: {}", res);
    }
    
    // 充值接口
    public String walletAccountRecharge(String farmer, BigDecimal rechargePrice, String rechargeType)
    {
        WanliCorrelationInfo info = getInfo(farmer);
        System.out.println("传入的参数: " + rechargePrice);
        rechargePrice = rechargePrice.multiply(new BigDecimal(100));
        System.out.println("修改后的参数: " + rechargePrice);
        JsonObject json = new JsonObject();
        json.put("rechargePrice", rechargePrice);
        json.put("rechargeType", rechargeType);
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getSign(json.toString(), timestamp, nonce, info);
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("sign", sign);
        param.put("storeId", info.getStoreId());
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        log.info("walletAccountRecharge-param: {}", JsonUtil.toString(param, true));
        String res =
            HttpUtil.forString(prefixUrl + "/api/v1/wallet/accountRecharge").post().body(param.toString()).exec();
        log.info("res: {}", res);
        JSONObject jo = JSONObject.parseObject(res);
        JSONObject ob = jo.getJSONObject("data");
        return ob.getString("qrCodeUrl");
    }
    
    // 查询账户余额
    public String walletBalance(String farmer)
    {
        WanliCorrelationInfo info = getInfo(farmer);
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getSign("null", timestamp, nonce, info);
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("sign", sign);
        param.put("storeId", info.getStoreId());
        param.put("data", "null");
        param.put("timestamp", timestamp);
        String res = HttpUtil.forString(prefixUrl + "/api/v1/wallet/balance").post().body(param.toString()).exec();
        log.info("res: {}", res);
        JSONObject jo = JSONObject.parseObject(res);
        JSONObject ob = jo.getJSONObject("data");
        BigDecimal amt = new BigDecimal(ob.getString("usableAmt")).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        return "余额: " + amt + "元";
    }
    
    // 充值订单查询
    public Boolean walletQueryRechargeStatus(String farmer, String rechargeOrdNo)
    {
        WanliCorrelationInfo info = getInfo(farmer);
        JsonObject json = new JsonObject();
        json.put("rechargeOrdNo", rechargeOrdNo);
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getSign(json.toString(), timestamp, nonce, info);
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("sign", sign);
        param.put("storeId", info.getStoreId());
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        String res =
            HttpUtil.forString(prefixUrl + "/api/v1/wallet/queryRechargeStatus").post().body(param.toString()).exec();
        log.info("res: {}", res);
        return true;
    }
    
    public List<String> listSupplier(String farmer)
    {
        WanliCorrelationInfo info = getInfo(farmer);
        List<String> res = new ArrayList<>();
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getNoStoreIdSign("null", timestamp, nonce, info.getSecret());
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("sign", sign);
        param.put("data", "null");
        param.put("timestamp", timestamp);
        String exec = HttpUtil.forString(prefixUrl + "/api/v1/supplier/query").post().body(param.toString()).exec();
        JSONObject parseObject = JSON.parseObject(exec);
        JSONArray jsonArray = parseObject.getJSONArray("data");
        Iterator<Object> ite = jsonArray.iterator();
        while (ite.hasNext())
        {
            Object next = ite.next();
            JSONObject dto = JSON.parseObject(next.toString());
            res.add(dto.getString("deliveryCode"));
        }
        return res;
    }
    
    // 查询运力审核状态
//    public void shopSupplierQuery()
//    {
//        String timestamp = System.currentTimeMillis() + "";
//        String nonce = UUID.randomUUID().toString();
//        JsonObject json = new JsonObject();
//        json.put("shopId", "01a79ba43ed0474b92f5bdda9fd79f50");
//        String sign = getSign(json.toString(), timestamp, nonce);
//        JsonObject param = new JsonObject();
//        param.put("appId", appId);
//        param.put("timestamp", timestamp);
//        param.put("nonce", nonce);
//        param.put("sign", sign);
//        param.put("data", json.toString());
//        param.put("storeId", storeId);
//        param.put("timestamp", timestamp);
//        String exec =
//            HttpUtil.forString(prefixUrl + "/api/v1/shop/supplier/query").post().body(param.toString()).exec();
//        log.info("res: {}", exec);
//    }
    
    // 获取计价
    public List<OrderBillingOnInfo> orderBilling(String shopId, String outOrderNo, String toAddress,
        String toAddressDetail, String toLng, String toLat, String toReceiverName, String toMobile, Integer weight)
    {
        WanliCorrelationInfo info = getInfo(CurrentSession.marketPkey());
        long k1 = System.currentTimeMillis();
        JsonObject json = new JsonObject();
        json.put("shopId", shopId);
        json.put("outOrderNo", outOrderNo);
        json.put("toAddress", toAddress);
        json.put("toAddressDetail", toAddressDetail);
        json.put("toLng", toLng);
        json.put("toLat", toLat);
        json.put("toReceiverName", toReceiverName);
        json.put("toMobile", toMobile);
        json.put("goodType", 1);
        json.put("weight", weight);
        json.put("coordinateType", 0);
        
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getSign(json.toString(), timestamp, nonce, info);
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("storeId", info.getStoreId());
        param.put("sign", sign);
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        log.info("orderBilling-param: {}", JsonUtil.toString(param, true));
        long k2 = System.currentTimeMillis();
        System.out.println("耗时: " + (k2 - k1));
        String exec = HttpUtil.forString(prefixUrl + "/api/v1/order/billing").post().body(param.toString()).exec();
        long k3 = System.currentTimeMillis();
        System.out.println("exec耗时: " + (k3 - k2));
        log.info("exec: {}", exec);
        JSONObject jo = JSON.parseObject(exec);
        if (!"200".equals(jo.getString("code")))
            throw TofocusException.of(LejiaErrCode.SEC_WANLI_RESULT_ERROR, jo.getString("message"));
        JSONObject data = jo.getJSONObject("data");
        
        JSONArray jsonArray = data.getJSONArray("billingDetailList");
        return JSONObject.parseArray(jsonArray.toJSONString(), OrderBillingOnInfo.class);
    }
    
    // 创建订单
    public Boolean orderCreate(String shopId, String outOrderNo, List<Integer> multipleSupplierCodes, String toAddress,
        String toAddressDetail, String toLng, String toLat, String toReceiverName, String toMobile, Integer weight, 
        Integer smallTicket)
    {
        if (!checkAsc())
            throw TofocusException.of(LejiaErrCode.SEC_WANLI_ERROR);
        
        WanliCorrelationInfo info = getInfo(CurrentSession.marketPkey());
        String strOrder = Util.getUUID();
        
        JsonObject json = new JsonObject();
        //    json.put("outOrderNo", outOrderNo);
        json.put("outOrderNo", strOrder);
        json.put("multipleSupplierCodes", multipleSupplierCodes);
        json.put("shopId", shopId);
        
        json.put("toAddress", toAddress);
        json.put("toAddressDetail", toAddressDetail);
        json.put("toLng", toLng);
        json.put("toLat", toLat);
        json.put("toReceiverName", toReceiverName);
        json.put("toMobile", toMobile);
        json.put("goodType", 1);
        json.put("weight", weight);
        json.put("coordinateType", 0);
        // 订单备注,加打印小票码
        if(smallTicket != null)
            json.put("remarks", smallTicket + "");

        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getSign(json.toString(), timestamp, nonce, info);
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("storeId", info.getStoreId());
        param.put("sign", sign);
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        log.info("orderCreate-param: {}", JsonUtil.toString(param, true));
        String exec = HttpUtil.forString(prefixUrl + "/api/v1/order/create").post().body(param.toString()).exec();
        log.info("exec: {}", exec);
        JSONObject jo = JSON.parseObject(exec);
        if (!"200".equals(jo.getString("code")))
            throw TofocusException.of(LejiaErrCode.SEC_WANLI_RESULT_ERROR, jo.getString("message"));
        
        String rdata = jo.getString("data");
        JSONObject receiveorderNo = JSON.parseObject(rdata);
        String thirdPartyOrderNo=receiveorderNo.getString("orderNo");

        buildMtkOrderDeliveryMsg(outOrderNo,strOrder,thirdPartyOrderNo,shopId);
        return true;
    }
    
    private void buildMtkOrderDeliveryMsg(String orderNo, String outOrderNo, String thirdPartyOrderNo, String shopId)
    {
        MktOrder order = orderDao.selectOne().eq("code", orderNo).exec();
        if (order != null)
        {
            MktOrderDeliveryMsgEntity entity = new MktOrderDeliveryMsgEntity();
            entity.setCompany(order.getCompany());
            entity.setFarmer(order.getFarmer());
            entity.setShopId(shopId);
            entity.setOrderNo(orderNo);
            entity.setPkey(outOrderNo);
            entity.setThirdPartyOrderNo(thirdPartyOrderNo);
            order.setThirdPartyOrderNo(thirdPartyOrderNo);
            orderDao.update(order);
            mktOrderDeliveryMsgDao.put(entity);
        }
    }
    
    // 查看送货员详情  
    public WanliCourierOnInfo orderQueryCourier(String orderNo)
    {
        WanliCorrelationInfo info = getInfo(CurrentSession.marketPkey());
        JsonObject json = new JsonObject();
        json.put("orderNo", orderNo);
//        json.put("outOrderNo", outOrderNo);
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getSign(json.toString(), timestamp, nonce, info);
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("storeId", info.getStoreId());
        param.put("sign", sign);
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        String exec =
            HttpUtil.forString(prefixUrl + "/api/v1/order/query/courier").post().body(param.toString()).exec();
        log.info("exec: {}", exec);
        JSONObject jo = JSON.parseObject(exec);
        JSONObject data = jo.getJSONObject("data");
        String lat = data.getString("lat");
        String lng = data.getString("lng");
        WanliCourierOnInfo res = new WanliCourierOnInfo();
        if(StringUtils.isNotBlank(lat))
            res.setLatitude(new BigDecimal(lat));
        if(StringUtils.isNotBlank(lng))
            res.setLongitude(new BigDecimal(lng));
        res.setName(data.getString("name"));
        res.setMobile(data.getString("mobile"));
        return res;
    }
    
    
    public WanliCourierOnInfo getWanliCourier(Integer pkey)
    {
        MktOrder mktOrder = orderDao.get(pkey);
        if(mktOrder == null || StringUtils.isBlank(mktOrder.getThirdPartyOrderNo()))
            return new WanliCourierOnInfo();
        WanliCourierOnInfo res = orderQueryCourier(mktOrder.getThirdPartyOrderNo());
        MktOrderDesc orderDesc = descDao.get(pkey);
        SysFarmerConfig config = farmerConfigDao.get(mktOrder.getFarmer());
        res.setMarketLongitude(config.getLongitude());
        res.setMarketLatitude(config.getLatitude());
        if(res.getLatitude() != null && res.getLongitude() != null)
        {
            if(ThirdPartyStatus.THIRD_PARTY_PICKING_UP.equals(mktOrder.getThirdPartyStatus()) 
                && config.getLatitude() != null && config.getLongitude() != null)
            {
                Double a = LocationUtils.getDistance(config.getLatitude().doubleValue(),
                    config.getLongitude().doubleValue(),
                    res.getLatitude().doubleValue(),
                    res.getLongitude().doubleValue());
                BigDecimal distance = new BigDecimal(a.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                res.setDistance(distance);
            }
            if(ThirdPartyStatus.THIRD_PARTY_DELIVERY.equals(mktOrder.getThirdPartyStatus())
                && orderDesc.getLatitude() != null && orderDesc.getLongitude() != null)
            {
                Double a = LocationUtils.getDistance(orderDesc.getLatitude().doubleValue(),
                    orderDesc.getLongitude().doubleValue(),
                    res.getLatitude().doubleValue(),
                    res.getLongitude().doubleValue());
                BigDecimal distance = new BigDecimal(a.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                res.setDistance(distance);
            }
        }
        res.setMemberLongitude(orderDesc.getLongitude());
        res.setMemberLatitude(orderDesc.getLatitude());
    
        if(StringUtils.isBlank(res.getName()) || StringUtils.isEmpty(res.getMobile()))
        {
            MktExpress express = expressDao.selectOne().eq("orderId", pkey).exec();
            if (express != null)
            {
                if (express.getCourier().equals(-1))
                {
                    res.setName(express.getCourierName());
                    res.setMobile(express.getCourierMobile());
                }
            }
        }
        return res;
    }
    
    //    public void runOrderQuery()
    //    {
    
    
    //        String[] split = ascrPaidan.split(",");
    //        List<String> ascrList = Arrays.asList(split);
    //        Calendar calendar = Calendar.getInstance();
    //        calendar.add(Calendar.MINUTE, -10);
    //        List<MktOrder> list = orderDao.select()
    //            .in("status", OrderStatus.SHIPPED_ORDER,OrderStatus.ARRIVED_ORDER, OrderStatus.CONFIRM_ORDER)
    //            .in("ascription", ascrList)
    //            .ge("createdTime", calendar.getTime())
    //            .exec();
    //        for(MktOrder o : list)
    //        {
    //            orderQueryDetail(o);
    //        }
    //  }
    
    // 查看订单详情  
    public void orderQueryDetail(MktOrder order)
    {
        JsonObject json = new JsonObject();
        
        MktOrderDeliveryMsgEntity msg = mktOrderDeliveryMsgDao.selectOne().eq("orderNo", order.getCode()).sort("createdTime").exec();
        if(msg==null)
        {
            json.put("outOrderNo",order.getCode());
        }
        else
            json.put("outOrderNo", msg.getPkey());
        
        WanliCorrelationInfo info = getInfo(CurrentSession.marketPkey());
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getSign(json.toString(), timestamp, nonce, info);
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("storeId", info.getStoreId());
        param.put("sign", sign);
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        System.out.println("param: " + JsonUtil.toString(param, true));
        System.out.println("prefixUrl: " + prefixUrl);
        String exec = HttpUtil.forString(prefixUrl + "/api/v1/order/query/detail").post().body(param.toString()).exec();
        log.info("查看订单详情返回结果: {}", exec);
        JSONObject jo = JSON.parseObject(exec);
        JSONObject data = jo.getJSONObject("data");
        if (data == null)
            return;
        Integer sendStatus = data.getInteger("sendStatus");
        //派送状态 1-初始化 20-待接单、30取货中、40-配送中、50-已完成、60- 已取消、70- 配送异常 Integer(3)
        MktExpress express;
        switch (sendStatus)
        {
            case 1:
                log.info("派送状态:{} ", "初始化");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_INIT);
                break;
            case 20:
                log.info("派送状态:{} ", "待接单");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_PENDING);
                break;
            case 30:
                log.info("派送状态:{} ", "取货中");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_PICKING_UP);
                express = expressDao.selectOne().eq("orderId", order.getPkey()).exec();
                if (express != null)
                {
                  //  express.setCourierName(data.getString("courierMobile"));
                  //  express.setCourierMobile(data.getString("courierName"));
                    express.setCourierName(data.getString("courierName"));
                     express.setCourierMobile(data.getString("courierMobile"));
                    express.setStatus(ExpressStatus.EXPRESS_GOODS);
                    express.setStatusName(ExpressStatus.EXPRESS_GOODS.getName());
                    express.setJdTime(new Date());
                    expressDao.update(express);
                }
                break;
            case 40:
                log.info("派送状态:{} ", "配送中");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_DELIVERY);
                express = expressDao.selectOne().eq("orderId", order.getPkey()).exec();
                if (express != null)
                {
//                    express.setCourierName(data.getString("courierMobile"));
//                    express.setCourierMobile(data.getString("courierName"));
                    express.setCourierName(data.getString("courierName"));
                    express.setCourierMobile(data.getString("courierMobile"));
                    express.setStatus(ExpressStatus.EXPRESS_GOODS);
                    express.setStatusName(ExpressStatus.EXPRESS_GOODS.getName());
                    express.setJdTime(new Date());
                    expressDao.update(express);
                }
                break;
            case 50:
                log.info("派送状态:{} ", "已完成");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_CONFIRM);
                express = expressDao.selectOne().eq("orderId", order.getPkey()).exec();
                if (express != null)
                {
//                    express.setCourierName(data.getString("courierMobile"));
//                    express.setCourierMobile(data.getString("courierName"));
                    express.setCourierName(data.getString("courierName"));
                    express.setCourierMobile(data.getString("courierMobile"));
                    express.setStatus(ExpressStatus.EXPRESS_ARRIVED);
                    express.setStatusName(ExpressStatus.EXPRESS_ARRIVED.getName());
                    express.setQrTime(new Date());
                    expressDao.update(express);
                    if (order != null && order.getStatus().equals(OrderStatus.SHIPPED_ORDER))
                    {
                        order.setStatus(OrderStatus.ARRIVED_ORDER);
                        order.setQrTime(new Date());
                        orderDao.update(order);
                    }
                    // 小程序消息推送
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
                            expressManager.expressArrivedSend(order, express);
                        }
                    }).start();
                }
                break;
            case 60:
                log.info("派送状态:{} ", "已取消");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_VOID);
                order.setStatus(OrderStatus.DELIVERED_ORDER);
                break;
            case 70:
                log.info("派送状态:{} ", "配送异常");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_ERROR);
                break;
            
            default:
                break;
        }
        orderDao.update(order);
    }
    
    public Boolean checkAsc()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String[] split = ascrPaidan.split(",");
        List<String> ascrList = Arrays.asList(split);
        return ascrList.contains(ascription.toString());
    }
    
    public void paidan(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktOrder order = orderDao.get(pkey);
        if (ascription == null)
            ascription = order.getAscription();
        
        order.setExpressType(ExpressType.WANLI);
        order.setStatus(OrderStatus.SHIPPED_ORDER);
        orderDao.update(order);
        MktExpress e = expressDao.selectOne().eq("orderId", pkey).eq("code", order.getCode()).exec();
        if (e != null)
        {
            return;
        }
        
        MktExpress express = new MktExpress();
        express.setCode(order.getCode());
        express.setOrderId(pkey);
        express.setStatus(ExpressStatus.EXPRESS_ORDER);
        express.setCourier(-1);
        express.setFarmer(order.getFarmer());
        express.setCompany(order.getCompany());
        express.setCreatedTime(new Date());
        express.setAscription(ascription);
        PurchaseStatus purchaseStatus = order.getPurchaseStatus();
        if (purchaseStatus != null && PurchaseStatus.AWAIT_PURCHASE.equals(purchaseStatus))
            express.setStatusName("待取货");
        if (purchaseStatus != null && (PurchaseStatus.PURCHASEING.equals(purchaseStatus)
            || PurchaseStatus.PURCHASE_FINISH.equals(purchaseStatus)))
            express.setStatusName("拣货中");
        if (purchaseStatus != null && purchaseStatus.getIndex() == PurchaseStatus.PURCHASE_CONFIRM.getIndex())
            express.setStatusName("拣货完成");
        expressDao.add(express);
        MktOrderDesc desc = descDao.get(pkey);
        if (desc != null)
        {
            // 加一个第三方的名字
            desc.setLogistics("第三方派送");
            desc.setKdCode(order.getCode());
            desc.setFhTime(new Date());
            descDao.update(desc);
        }
        
        orderManager.uploadShippingInfo(order, 2);
    }
    
    private String getSign(String data, String timestamp, String nonce, WanliCorrelationInfo info)
    {
        String splicing = info.getSecret() + timestamp + nonce + info.getStoreId() + data;
        return MD5.getMD5(splicing);
    }
    
    private String callbackSign(String data, String timestamp, String nonce, String secret)
    {
        String splicing = secret + timestamp + nonce + data;
        return MD5.getMD5(splicing);
    }
    
    private String getNoStoreIdSign(String data, String timestamp, String nonce, String secret)
    {
        String splicing = secret + timestamp + nonce + data;
        return MD5.getMD5(splicing);
    }
    
    public String callback(JSONObject object, String farmer)
    {
        
        JSONObject resp = new JSONObject();
        log.info("call bcak {}", object);
        
        String timestamp = object.getString("timestamp");
        
        String sign = object.getString("sign");
        
        String nonce = object.getString("nonce");
        String data = object.getString("data");

        
        log.info("call bcak  server sign={}", sign);
        JSONObject cbdata = object.getJSONObject("data");
        
        WanliCorrelationInfo info = getInfo(farmer);
        String checkSignsign = callbackSign(data, timestamp, nonce, info.getSecret());
        log.info("call bcak received sign={}", checkSignsign.toLowerCase());
        if (sign.equals(checkSignsign.toLowerCase()))
        {
            
            String paramstr = cbdata.getString("param");
            JSONObject param = (JSONObject)JSON.parse(paramstr);
            
            String outOrderNo = param.getString("outOrderNo");
            String orderNo = param.getString("orderNo");
            if (outOrderNo != null&&orderNo!=null)
            {
                MktOrderDeliveryMsgEntity msg = mktOrderDeliveryMsgDao.get(outOrderNo);
                if(msg!=null)
                {
                    outOrderNo=msg.getOrderNo();
                }
          
                MktOrder order = orderDao.getOrderByCodeAndThirdPartyNo(outOrderNo, orderNo);
                if (order != null)
                {
                    String finishCode = param.getString("finishCode");
                    if(StringUtils.isNotBlank(finishCode))
                        order.setPickupCode(finishCode);
                    changeOrderStatus(param, order);
                    resp.put("code", "0000");
                    resp.put("resMsg", "成功");
                    return JsonUtil.toString(resp);
                }
                
            }
            resp.put("resMsg", "订单不存在");
        }
        else
        {
            
            resp.put("resMsg", "验签失败");
            log.warn("验签失败");
        }
        resp.put("code", "1002");
        return JsonUtil.toString(resp);
        
    }
    
    private void changeOrderStatus(JSONObject data, MktOrder order)
    {
        Integer sendStatus = data.getInteger("sendStatus");
        //派送状态 1-初始化 20-待接单、30取货中、40-配送中、50-已完成、60- 已取消、70- 配送异常 Integer(3)
        MktExpress express;
        switch (sendStatus)
        {
            case 1:
                log.info("派送状态:{} ", "初始化");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_INIT);
                break;
            case 20:
                log.info("派送状态:{} ", "待接单");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_PENDING);
                break;
            case 30:
                log.info("派送状态:{} ", "取货中");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_PICKING_UP);
                express = expressDao.selectOne().eq("orderId", order.getPkey()).exec();
                if (express != null)
                {
                   // express.setCourierName(data.getString("courierMobile"));
                   // express.setCourierMobile(data.getString("courierName"));
                    express.setCourierName(data.getString("courierName"));
                    express.setCourierMobile(data.getString("courierMobile"));
                    express.setStatus(ExpressStatus.EXPRESS_GOODS);
                    express.setStatusName(ExpressStatus.EXPRESS_GOODS.getName());
                    express.setJdTime(new Date());
                    expressDao.update(express);
                }
                break;
            case 40:
                log.info("派送状态:{} ", "配送中");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_DELIVERY);
                express = expressDao.selectOne().eq("orderId", order.getPkey()).exec();
                if (express != null)
                {
//                    express.setCourierName(data.getString("courierMobile"));
//                    express.setCourierMobile(data.getString("courierName"));
                    express.setCourierName(data.getString("courierName"));
                    express.setCourierMobile(data.getString("courierMobile"));
                    express.setStatus(ExpressStatus.EXPRESS_GOODS);
                    express.setStatusName(ExpressStatus.EXPRESS_GOODS.getName());
                    express.setJdTime(new Date());
                    expressDao.update(express);
                }
                break;
            case 50:
                log.info("派送状态:{} ", "已完成");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_CONFIRM);
                express = expressDao.selectOne().eq("orderId", order.getPkey()).exec();
                if (express != null)
                {
//                    express.setCourierName(data.getString("courierMobile"));
//                    express.setCourierMobile(data.getString("courierName"));
                    express.setCourierName(data.getString("courierName"));
                    express.setCourierMobile(data.getString("courierMobile"));
                    express.setStatus(ExpressStatus.EXPRESS_ARRIVED);
                    express.setStatusName(ExpressStatus.EXPRESS_ARRIVED.getName());
                    express.setQrTime(new Date());
                    expressDao.update(express);
                    if (order != null && order.getStatus().equals(OrderStatus.SHIPPED_ORDER))
                    {
                        order.setStatus(OrderStatus.ARRIVED_ORDER);
                        order.setQrTime(new Date());
                        orderDao.update(order);
                    }
                    // 小程序消息推送
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
                            expressManager.expressArrivedSend(order, express);
                        }
                    }).start();
                }
                break;
            case 60:
                log.info("派送状态:{} ", "已取消");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_VOID);
                order.setStatus(OrderStatus.DELIVERED_ORDER);
                break;
            case 70:
                log.info("派送状态:{} ", "配送异常");
                order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_ERROR);
                break;
            
            default:
                break;
        }
        orderDao.update(order);
    }
    
    public Boolean cancleOrder(CancelType cancelType, Integer pkey)
    {
        MktOrder order = orderDao.get(pkey);
        if (order == null)
        {
            throw TofocusException.of(LejiaErrCode.ORDER_DEL);
        }
        JsonObject json = new JsonObject();
        String cancleorder= order.getCode();
        MktOrderDeliveryMsgEntity msg =mktOrderDeliveryMsgDao.selectOne().eq("orderNo", order.getCode()).sort("createdTime").exec();
        if (msg != null)
        {
            cancleorder = msg.getPkey();
        }
        WanliCorrelationInfo info = getInfo(order.getFarmer());
        json.put("outOrderNo", cancleorder);
        json.put("cancelType", cancelType.getIndex());
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getSign(json.toString(), timestamp, nonce, info);
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("storeId", info.getStoreId());
        param.put("sign", sign);
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        log.info("param: {}", param);
        String exec = HttpUtil.forString(prefixUrl + "/api/v1/order/cancel").post().body(param.toString()).exec();
        log.info("exec: {}", exec);
        JSONObject jo = JSON.parseObject(exec);
        if (!"200".equals(jo.getString("code")))
            throw TofocusException.of(LejiaErrCode.SEC_WANLI_RESULT_ERROR, jo.getString("message"));
        order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_VOID);
        order.setStatus(OrderStatus.DELIVERED_ORDER);
        orderDao.update(order);
        return true;
        
    }
    
    public Boolean ordertest(String orderNo, Integer status)
    {
        WanliCorrelationInfo info = getInfo(CurrentSession.marketPkey());
        
        JsonObject json = new JsonObject();
        json.put("orderNo", orderNo);
        json.put("status", status);
        String timestamp = System.currentTimeMillis() + "";
        String nonce = UUID.randomUUID().toString();
        String sign = getSign(json.toString(), timestamp, nonce, info);
        JsonObject param = new JsonObject();
        param.put("appId", info.getAppId());
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("storeId", info.getStoreId());
        param.put("sign", sign);
        param.put("data", json.toString());
        param.put("timestamp", timestamp);
        log.info("param: {}", param);
        String exec = HttpUtil.forString(prefixUrl + "/api/v1/order/test").post().body(param.toString()).exec();
        log.info("exec: {}", exec);
        JSONObject jo = JSON.parseObject(exec);
        return "200".equals(jo.getString("code"));
        
    }
    
    public Boolean orderReach(Integer pkey)
    {
        MktOrder order = orderDao.get(pkey);
        if (order == null)
        {
            throw TofocusException.of(LejiaErrCode.ORDER_DEL);
        }
        if (!order.getThirdPartyStatus().equals(ThirdPartyStatus.THIRD_PARTY_VOID))
            throw TofocusException.of(LejiaErrCode.ORDER_OVERSTATUS, order.getThirdPartyStatus().getName());
        
        order.setStatus(OrderStatus.ARRIVED_ORDER);
        order.setThirdPartyStatus(ThirdPartyStatus.THIRD_PARTY_CONFIRM);
        orderDao.update(order);
        
        return true;
    }
    
}
