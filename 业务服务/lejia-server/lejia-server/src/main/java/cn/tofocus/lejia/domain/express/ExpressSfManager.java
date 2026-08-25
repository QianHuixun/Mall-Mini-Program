package cn.tofocus.lejia.domain.express;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.express.SfCancelOrderResult;
import cn.tofocus.lejia.bean.dto.express.SfGetFreightAddedServicesResult;
import cn.tofocus.lejia.bean.dto.express.SfPlaceOrderResult;
import cn.tofocus.lejia.bean.dto.express.SfResult;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExpressSfManager
{
    @Value("${express.sf.baseUrl}")
    private String baseUrl;
    
    /**
     * 运费估算接口（含增值服务）
     * @param appId 顺丰appId
     * @param sk 顺丰sk
     * @param monthlyCard 顺丰月结卡号
     * @param senderProvince 寄件人省份
     * @param senderCity 寄件人城市
     * @param senderAddress 寄件人详细地址
     * @param receiverProvince 收件人省份
     * @param receiverCity 收件人城市
     * @param receiverAddress 收件人详细地址
     * @param sendContent 寄托物内容
     * @param sendWeight 包裹重量，单位千克，精确到小数点后3位并大于0.001
     * @return
     */
    public SfGetFreightAddedServicesResult getFreightAddedServices(String appId, String sk, String monthlyCard,
        String senderProvince, String senderCity, String senderAddress, String receiverProvince, String receiverCity,
        String receiverAddress, String sendContent, BigDecimal sendWeight)
    {
        String httpUrl = baseUrl + "/public/order/v1/getFreightAddedServices";
        JSONObject json = new JSONObject();
        json.put("companyId", appId);
        json.put("senderProvince", standardizePro(senderProvince)); //寄件方所在省份
        json.put("senderCity", standardizeCity(senderProvince, senderCity)); //寄件方所在城市
        json.put("senderAddress", senderAddress);
        json.put("receiptProvince", standardizePro(receiverProvince)); //收件方所在省份
        json.put("receiptCity", standardizeCity(receiverProvince, receiverCity)); //收件方所在城市
        json.put("receiptAddress", receiverAddress);
        json.put("payMethod", "0"); //运费付款方式 0-寄付月结
        json.put("monthlyCard", monthlyCard); //月结卡号
        json.put("expressType", "2"); //快件产品类别 2-标快
        json.put("depositumInfo", sendContent); //托寄物内容
        if (sendWeight.compareTo(new BigDecimal("0.001")) >= 0)
        {
            json.put("parcelWeighs", sendWeight); //包裹重量，订单货物单位重量，包含子母件，单位千克，精确到小数点后3位并大于0.001，跨境件报关需要填写
        }
        HttpHeaders headers = getHeads(appId, sk, json.toString());
        log.info("[顺丰快递] 运费估算接口（含增值服务）请求参数: {}", json);
        SfResult<SfGetFreightAddedServicesResult> result =
            HttpUtil.forEntity(httpUrl, new ParameterizedTypeReference<SfResult<SfGetFreightAddedServicesResult>>()
            {
            }).headers(headers).body(json).exec();
        log.info("[顺丰快递] 运费估算接口（含增值服务）响应参数: {}", JsonUtil.toString(result));
        if (!result.isSuccess())
            throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR, "顺丰运费估算失败：" + result.getMsg());
        return result.getResult();
    }
    
    /**
     * 顺丰下单
     * @param appId 顺丰appId
     * @param sk 顺丰sk
     * @param monthlyCard 顺丰月结卡号
     * @param expressNo 本地系统内生成的物流单号
     * @param pickupTime 上门取件时间
     * @param sender 寄件人姓名
     * @param senderMobile 寄件人手机号
     * @param senderProvince 寄件人省份
     * @param senderCity 寄件人城市
     * @param senderAddress 寄件人详细地址
     * @param receiver 收件人姓名
     * @param receiverMobile 收件人手机号
     * @param receiverProvince 收件人省份
     * @param receiverCity 收件人城市
     * @param receiverAddress 收件人详细地址
     * @param sendContent 寄托物内容
     * @param sendNum 寄托物数量
     * @param remark 寄方备注
     * @return
     */
    public SfPlaceOrderResult placeOrder(String appId, String sk, String monthlyCard, String expressNo, Date pickupTime,
        String sender, String senderMobile, String senderProvince, String senderCity, String senderAddress,
        String receiver, String receiverMobile, String receiverProvince, String receiverCity, String receiverAddress,
        String sendContent, Integer sendNum, String remark)
    {
        String httpUrl = baseUrl + "/public/order/v2/placeOrder";
        JSONObject json = new JSONObject();
        json.put("orderId", expressNo); //订单号
        json.put("sendStartTime", DateUtil.formatDate(pickupTime, "yyyy-MM-dd HH:mm:ss")); //可空、上门取件时间
        json.put("jContact", sender);//寄件人姓名，只支持中文真实姓名
        json.put("jMobile", senderMobile); //真实手机号
        json.put("jProvince", standardizePro(senderProvince)); //寄件人 省
        json.put("jCity", standardizeCity(senderProvince, senderCity)); //寄件人 市
        json.put("jAddress", senderAddress); //如不传寄件省市时需包含省市区详细地址
        json.put("dContact", receiver); //收件人
        json.put("dMobile", receiverMobile); //收件人手机号
        json.put("dProvince", standardizePro(receiverProvince)); //收件人 省
        json.put("dCity", standardizeCity(receiverProvince, receiverCity)); //收件人 市
        json.put("dAddress", receiverAddress);
        json.put("custid", monthlyCard); //月结卡号 - 测试环境9999999999
        json.put("payMethod", "0"); //运费付款方式 0-寄付月结；1-寄付现结；2-收方付；3-第三方付；
        json.put("expressType", "231"); //快件产品类别 2-标快；231-陆运包裹
        json.put("depositumInfo", sendContent); //托寄物内容
        json.put("depositumNo", sendNum + ""); //托寄物数量 限数字，1到10万
        json.put("remark", remark); //寄方备注
        json.put("isDoCall", "1"); //是否下call，是否通过手持终端通知顺丰收派员上门收件 1-要求；0-不要求；
        json.put("companyId", appId); //APP_ID
        HttpHeaders headers = getHeads(appId, sk, json.toString());
        log.info("[顺丰快递] 下单请求参数: {}", json);
        SfResult<SfPlaceOrderResult> result =
            HttpUtil.forEntity(httpUrl, new ParameterizedTypeReference<SfResult<SfPlaceOrderResult>>()
            {
            }).headers(headers).body(json).exec();
        log.info("[顺丰快递] 下单响应参数: {}", JsonUtil.toString(result));
        if (!result.isSuccess())
            throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR, "顺丰下单失败：" + result.getMsg());
        return result.getResult();
    }
    
    /**
     * 订单取消
     * @param appId 顺丰appId
     * @param expressNo 本地系统内生成的物流单号
     * @return
     */
    public SfCancelOrderResult cancelOrder(String appId, String sk, String expressNo)
    {
        String httpUrl = baseUrl + "/public/order/v2/cancelOrder";
        JSONObject json = new JSONObject();
        json.put("companyId", appId);
        json.put("orderId", expressNo);
        HttpHeaders headers = getHeads(appId, sk, json.toString());
        log.info("[顺丰快递] 订单取消请求参数: {}", json);
        SfResult<SfCancelOrderResult> result =
            HttpUtil.forEntity(httpUrl, new ParameterizedTypeReference<SfResult<SfCancelOrderResult>>()
            {
            }).headers(headers).body(json).exec();
        log.info("[顺丰快递] 订单取消响应参数: {}", JsonUtil.toString(result));
        if (!result.isSuccess())
            throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR, "顺丰取消订单失败：" + result.getMsg());
        return result.getResult();
    }
    
    private String standardizePro(String pro)
    {
        if (pro.contains("北京")) return "北京";
        if (pro.contains("天津")) return "天津";
        if (pro.contains("上海")) return "上海";
        if (pro.contains("重庆")) return "重庆";
        return pro;
    }
    
    private String standardizeCity(String pro, String city)
    {
        if (pro.contains("北京")) return "北京市";
        if (pro.contains("天津")) return "天津市";
        if (pro.contains("上海")) return "上海市";
        if (pro.contains("重庆")) return "重庆市";
        return city;
    }
    
    public static HttpHeaders getHeads(String appId, String sk, String body)
    {
        String timestamp = System.currentTimeMillis() + "";
        String sign = genSign(sk, timestamp, body);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("sendAppId", appId);
        headers.set("timestamp", timestamp);
        headers.set("sign", sign);
        return headers;
    }
    
    private static String genSign(String sk, String timestamp, String body)
    {
        if (body == null) body = "";
        StringBuffer sb = new StringBuffer();
        sb.append(body);
        sb.append("&sk=").append(sk);
        sb.append("&timestamp=").append(timestamp);
        byte[] bytes = DigestUtils.sha512(sb.toString());
        return Base64.encodeBase64URLSafeString(bytes);
    }
    
    public static void main(String[] args)
    {
        String httpUrl = "https://butler-ms.sf-express.com/public/order/v1/getResult";
        String appId = "1019015320171839488";
        String sk = "baa430959012bfcae9100d6b556a6dd3";
        
        //String expressNo = "060225525550";
        String expressNo = "060225886962";
        
        JSONObject json = new JSONObject();
        json.put("companyId", appId); //APP_ID
        json.put("orderId", expressNo); //订单号
        HttpHeaders headers = getHeads(appId, sk, json.toString());
        log.info("[顺丰快递] 查询订单结果请求参数: {}", json);
        SfResult<SfPlaceOrderResult> result =
            HttpUtil.forEntity(httpUrl, new ParameterizedTypeReference<SfResult<SfPlaceOrderResult>>()
            {
            }).headers(headers).body(json).exec();
        log.info("[顺丰快递] 查询订单结果响应参数: {}", JsonUtil.toString(result));
    }
}
