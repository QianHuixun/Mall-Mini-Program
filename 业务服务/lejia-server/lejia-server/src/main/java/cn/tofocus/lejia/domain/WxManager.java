package cn.tofocus.lejia.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.tofocus.lejia.bean.dto.app.AppWxMsgSecCheckResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.app.AppAuthCode2SessionDTO;
import cn.tofocus.lejia.bean.dto.app.AppWxErrMsgDTO;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.dao.sys.AccountDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.exception.WxErrCode;
import cn.tofocus.lejia.util.WxUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WxManager
{
    @Autowired
    private AccountDao accountDao;
    
    public AccountEntity getAccountEntity(AccountType accountType, Integer ascription)
    {
        return accountDao.get(ascription, accountType);
    }
    
    public AppAuthCode2SessionDTO requestOpenidByCode(String wxcode, AccountType sign, Integer ascription)
    {
        if(ascription == null)
            ascription = 8;
        AccountEntity account = accountDao.selectOne().eq("accountType", sign).eq("ascription", ascription).exec();
        log.info("code:{}", wxcode);
        String urlStr = WxUtils.auth_code2Session_url.replace("APPID", account.getAccountAppid())
            .replace("SECRET", account.getAccountAppsecret())
            .replace("JSCODE", wxcode);
        log.info(urlStr);
        
        String rs = HttpUtil.forString(urlStr).exec();
        log.info("getOpenidByCode: {}", rs);
        AppAuthCode2SessionDTO res = JSON.parseObject(rs, AppAuthCode2SessionDTO.class);
        if(StringUtils.isNotBlank(res.getErrcode()))
            throw TofocusException.of(WsaleErrCode.OPENID_ERROR);
        return res;
    }
    
    @Transactional
    public String getAccessToken(AccountEntity account)
    {
        try
        {
            if (account != null)
            {
                String token = account.getAccessToken();
                //小于2小时就直接返回
                if (token != null && (new Date().getTime() - account.getAccessTime().getTime()) < (1000 * 3600))
                {
                    return token; // 有效期内直接返回
                }
                String requestUrl = WxUtils.access_token_url.replace("APPID", account.getAccountAppid())
                    .replace("APPSECRET", account.getAccountAppsecret());
                String result = HttpUtil.forString(requestUrl).exec();
                log.info("getAccessToken:{}", result);
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.containsKey(WxUtils.ERR_CODE)) throw TofocusException.of(WxErrCode.GET_ACCESS_TOKEN_FAIL);
                token = jsonObject.getString("access_token");
                account.setAccessToken(token);
                account.setAccessTime(new Date());
                accountDao.update(account);
                return token;
            }
        }
        catch (Exception e)
        {
            log.info("getAccessToken 获取token失败");
        }
       
        return null;
    }
    
    @Transactional
    public String getAccessTokenV2(AccountEntity account)
    {
        try
        {
            if (account != null)
            {
                String token = account.getAccessToken();
//                //小于2小时就直接返回
//                if (token != null && (new Date().getTime() - account.getAccessTime().getTime()) < (1000 * 3600))
//                {
//                    return token; // 有效期内直接返回
//                }
                String requestUrl = WxUtils.access_token_url.replace("APPID", account.getAccountAppid())
                    .replace("APPSECRET", account.getAccountAppsecret());
                String result = HttpUtil.forString(requestUrl).exec();
                log.info("getAccessToken:{}", result);
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.containsKey(WxUtils.ERR_CODE)) throw TofocusException.of(WxErrCode.GET_ACCESS_TOKEN_FAIL);
                token = jsonObject.getString("access_token");
                account.setAccessToken(token);
                account.setAccessTime(new Date());
                accountDao.update(account);
                return token;
            }
        }
        catch (Exception e)
        {
            log.info("getAccessToken 获取token失败");
        }
        
        return null;
    }
    
    /**
     * 订阅消息
     * 小程序发送订阅消息
     *
     * @param account    小程序信息账号信息
     * @param touser     接收者（用户）的 openid
     * @param templateId 所需下发的订阅模板id
     * @param page       点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。
     * @param data       模板内容，格式形如 { "key1": { "value": any }, "key2": { "value": any } }
     * @return
     */
    public AppWxErrMsgDTO sendWeappSubscribeMessage(AccountEntity account, String touser, String templateId,
        String page, JSONObject data)
    {
        String token = getAccessToken(account);
        if (StringUtils.isBlank(token)) return new AppWxErrMsgDTO();
        //        JSONObject obj = new JSONObject();
        Map<String, Object> obj = new HashMap<>();
        String url = WxUtils.subscribeMessage_send_url.replace("ACCESS_TOKEN", token);
        obj.put("touser", touser);
        obj.put("template_id", templateId);
        obj.put("page", page);
        //        跳转小程序类型：developer为开发版；trial为体验版；formal为正式版；默认为正式版
        obj.put("miniprogram_state", "formal");
        obj.put("data", data);
        log.info("obj: {}", JsonUtil.toString(obj, true));
        AppWxErrMsgDTO result = WxUtils.Post(url, JSON.toJSONString(obj), AppWxErrMsgDTO.class);
        log.info("小程序消息推送结果: {}", JsonUtil.toString(result, true));
        return result;
    }
    
    public JSONObject getMiniprogram(AccountType accountType, Integer ascription, String url)
    {
        AccountEntity account = accountDao.get(ascription, accountType);
        JSONObject miniprogram = new JSONObject();
        miniprogram.put("appid", account.getAccountAppid());
        miniprogram.put("page", url);
        return miniprogram;
    }
    
    /**
     * 微信公众号消息发送 直接调微信官方接口
     * <功能详细描述>
     * @return
     */
    public Boolean wechatSendMsgYs(String templateid, String openid, JSONObject miniprogram,
        JSONObject data, Integer ascription)
    {
        String postUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
        AccountEntity exec = accountDao.selectOne().eq("accountType", AccountType.WX).eq("ascription", ascription).exec();
        String accessToken = getAccessToken(exec);
        if(accessToken == null)
        {
            System.out.println("获取accessToken失败!");
            return false;
        }
        postUrl = postUrl.replace("ACCESS_TOKEN", accessToken);
        JSONObject params = new JSONObject();
        params.put("appid", exec.getAccountAppid());
        params.put("template_id", templateid);
        params.put("touser", openid);
        params.put("data", data);
        params.put("miniprogram", miniprogram);
        JSONObject request = WxUtils.httpRequest(postUrl, "POST", params.toJSONString());
        String string = request.toString();
        JSONObject object = JSONObject.parseObject(string);
        Object res = object.get("errcode");
        boolean b = false;
        if (res != null && res.equals(0))
        {
            b = true;
        }
        return b;
    }

    /**
     * 评价文本内容安全识别 直接调微信官方接口
     * <功能详细描述>
     * @return
     */
    public AppWxMsgSecCheckResult commentSecCheck(String content, String openid, Integer ascription)
    {
        String postUrl = "https://api.weixin.qq.com/wxa/msg_sec_check?access_token=ACCESS_TOKEN";
        AccountEntity exec =
            accountDao.selectOne().eq("accountType", AccountType.USER).eq("ascription", ascription).exec();
        String accessToken = getAccessToken(exec);
        if (accessToken == null)
        {
            System.out.println("获取accessToken失败!");
            return null;
        }
        postUrl = postUrl.replace("ACCESS_TOKEN", accessToken);
        JSONObject params = new JSONObject();
        params.put("content", content);
        params.put("version", 2);
        params.put("scene", 2);
        params.put("openid", openid);
        JSONObject request = WxUtils.httpRequest(postUrl, "POST", params.toJSONString());
        return request.toJavaObject(AppWxMsgSecCheckResult.class);
    }
    
    /**
     * 上传发货信息（按商户订单编号上传）
     * 具体参数含义见内部调用的方法
     * @return 成功与否
     */
    public Boolean uploadShippingInfo(String kcCode, String itemDesc, Integer logisticsType, String openid,
        Integer ascription)
    {
        return uploadShippingInfo(kcCode,
            null,
            null,
            itemDesc,
            logisticsType,
            null,
            null,
            null,
            null,
            openid,
            ascription);
    }
    
    /**
     * 上传发货信息（按微信流水号上传）
     * 具体参数含义见内部调用的方法
     * @return 成功与否
     */
    public Boolean uploadShippingInfo(String orderNumber, String mchid, String itemDesc, Integer logisticsType,
        String openid, Integer ascription)
    {
        return uploadShippingInfo(null,
            orderNumber,
            mchid,
            itemDesc,
            logisticsType,
            null,
            null,
            null,
            null,
            openid,
            ascription);
    }
    
    /**
     * 上传快递物流发货信息（按微信流水号上传）
     * 具体参数含义见内部调用的方法
     * @return 成功与否
     */
    public Boolean uploadExpressShippingInfo(String kcCode, String itemDesc, Integer logisticsType, String trackingNo,
        String expressCompany, String consignorContact, String receiverContact, String openid, Integer ascription)
    {
        return uploadShippingInfo(kcCode,
            null,
            null,
            itemDesc,
            logisticsType,
            trackingNo,
            expressCompany,
            consignorContact,
            receiverContact,
            openid,
            ascription);
    }
    
    /**
     * 上传快递物流发货信息（按商户订单编号上传）
     * 具体参数含义见内部调用的方法
     * @return 成功与否
     */
    public Boolean uploadExpressShippingInfo(String orderNumber, String mchid, String itemDesc, Integer logisticsType,
        String trackingNo, String expressCompany, String consignorContact, String receiverContact, String openid,
        Integer ascription)
    {
        return uploadShippingInfo(null,
            orderNumber,
            mchid,
            itemDesc,
            logisticsType,
            trackingNo,
            expressCompany,
            consignorContact,
            receiverContact,
            openid,
            ascription);
    }
    
    /**
     * 上传发货信息
     * @param kcCode 微信流水号（“微信流水号”或“商户订单编号+商户号”二选一必填）
     * @param orderNumber 商户订单编号（“微信流水号”或“商户订单编号+商户号”二选一必填）
     * @param mchid 商户号（“微信流水号”或“商户订单编号+商户号”二选一必填）
     * @param itemDesc 商品描述
     * @param logisticsType 物流类型（1：快递物流，2：同城配送，3：虚拟商品，4：用户自提）
     * @param trackingNo 快递单号（物流类型为快递物流时必填）
     * @param expressCompany 快递公司（物流类型为快递物流时必填）
     * @param consignorContact 发货人联系方式（快递公司为顺丰时，和收货人联系方式二选一必填）
     * @param receiverContact 收货人联系方式（快递公司为顺丰时，和发货人联系方式二选一必填）
     * @param openid 支付用户openid
     * @param ascription 所属
     * @return 成功与否
     */
    public Boolean uploadShippingInfo(String kcCode, String orderNumber, String mchid, String itemDesc,
        Integer logisticsType, String trackingNo, String expressCompany, String consignorContact,
        String receiverContact, String openid, Integer ascription)
    {
        // 快递物流
        if (logisticsType == 1)
        {
            if (StringUtil.isBlank(trackingNo))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "物流快递发货请输入快递单号");
            if (StringUtil.isBlank(expressCompany))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "物流快递发货请选择快递公司");
        }
        
        String postUrl = "https://api.weixin.qq.com/wxa/sec/order/upload_shipping_info?access_token=ACCESS_TOKEN";
        AccountEntity exec =
            accountDao.selectOne().eq("accountType", AccountType.USER).eq("ascription", ascription).exec();
        String accessToken = getAccessToken(exec);
        if (StringUtil.isBlank(accessToken))
        {
            log.error("获取accessToken失败，ascription：{}", ascription);
            return false;
        }
        postUrl = postUrl.replace("ACCESS_TOKEN", accessToken);
        
        JSONObject orderKey = new JSONObject();
        System.out.println("kcCode: " + kcCode);
        if (StringUtil.isBlank(kcCode))
        {
            orderNumber = orderNumber.substring(0, 14);
            // 商户号和商户侧单号
            orderKey.put("order_number_type", 1);
            orderKey.put("mchid", mchid);
            orderKey.put("out_trade_no", orderNumber);
        }
        else
        {
            // 微信支付单号
            orderKey.put("order_number_type", 2);
            orderKey.put("transaction_id", kcCode);
        }
        
        JSONArray shippingList = new JSONArray();
        JSONObject shopping = new JSONObject();
        shopping.put("item_desc", itemDesc);
        if (StringUtil.isNotBlank(trackingNo)) shopping.put("tracking_no", trackingNo);
        if (StringUtil.isNotBlank(expressCompany)) shopping.put("express_company", expressCompany);
        if (StringUtil.isNotBlank(consignorContact) || StringUtil.isNotBlank(receiverContact))
        {
            JSONObject contact = new JSONObject();
            if (StringUtil.isNotBlank(consignorContact))
                contact.put("consignor_contact", StringUtil.mask(consignorContact, 3, consignorContact.length() - 5));
            if (StringUtil.isNotBlank(receiverContact))
                contact.put("receiver_contact", StringUtil.mask(receiverContact, 3, receiverContact.length() - 5));
            shopping.put("contact", contact);
        }
        shippingList.add(shopping);
        
        JSONObject payer = new JSONObject();
        payer.put("openid", openid);
        
        JSONObject params = new JSONObject();
        params.put("order_key", orderKey);
        // 物流模式，发货方式枚举值：
        // 1、实体物流配送采用快递公司进行实体物流配送形式
        // 2、同城配送
        // 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式
        // 4、用户自提
        params.put("logistics_type", logisticsType);
        // 发货模式，发货模式枚举值：1、UNIFIED_DELIVERY（统一发货）2、SPLIT_DELIVERY（分拆发货）
        params.put("delivery_mode", 1);
        params.put("shipping_list", shippingList);
        params.put("upload_time", formatRFC3339Date(new Date()));
        params.put("payer", payer);
        
        JSONObject request = WxUtils.httpRequest(postUrl, "POST", params.toJSONString());
        String result = request.toString();
        JSONObject object = JSON.parseObject(result);
        Integer res = object.getInteger("errcode");
        if (res == null || !res.equals(0))
        {
            log.error("微信发货失败：{}", JsonUtil.toString(object));
            if(res.equals(40001))
            {
                accessToken = getAccessTokenV2(exec);
                request = WxUtils.httpRequest(postUrl, "POST", params.toJSONString());
                result = request.toString();
                object = JSON.parseObject(result);
                res = object.getInteger("errcode");
                if (res == null || !res.equals(0))
                {
                    log.error("微信发货第二次请求失败：{}", JsonUtil.toString(object));
                }
                else
                    return true;
            }
            return false;
        }
        return true;
    }
    
    /**
     * 获取快递公司列表
     * @param ascription 所属
     * @return 快递公司列表
     */
    public JSONArray getDeliveryList(Integer ascription)
    {
        String postUrl =
            "https://api.weixin.qq.com/cgi-bin/express/delivery/open_msg/get_delivery_list?access_token=ACCESS_TOKEN";
        AccountEntity exec =
            accountDao.selectOne().eq("accountType", AccountType.USER).eq("ascription", ascription).exec();
        String accessToken = getAccessToken(exec);
        if (StringUtil.isBlank(accessToken))
        {
            log.error("获取accessToken失败，ascription：{}", ascription);
            throw TofocusException.of(WsaleErrCode.OPENID_ERROR, "获取accessToken失败");
        }
        postUrl = postUrl.replace("ACCESS_TOKEN", accessToken);
        
        JSONObject request = WxUtils.httpRequest(postUrl, "POST", "{}");
        String result = request.toString();
        JSONObject object = JSON.parseObject(result);
        Integer res = object.getInteger("errcode");
        if (res == null || !res.equals(0))
        {
            log.error("获取快递公司列表失败：{}", JsonUtil.toString(object));
            throw TofocusException.of(WsaleErrCode.OPENID_ERROR, "获取快递公司列表失败");
        }
        return object.getJSONArray("delivery_list");
    }
    
    private String formatRFC3339Date(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        return simpleDateFormat.format(date);
    }
    
}
