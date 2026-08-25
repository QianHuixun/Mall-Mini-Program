package cn.tofocus.lejia.domain.pay;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.com.eagle.sdk.base.INetTools;
import cn.com.eagle.sdk.bean.NetReqConfBean;
import cn.com.eagle.sdk.bean.OipReqBean;
import cn.com.eagle.sdk.bean.OipRspBean;
import cn.com.eagle.sdk.net.factory.NetToolsHttpFactory;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.pay.ns.WXBillGetRequestBean;
import cn.tofocus.lejia.bean.dto.pay.ns.WXOrderCreateGetRequestBean;

@Component
public class NsPayManager
{
    
    private static String APPID = System.getenv().getOrDefault("NS_PAY_APP_ID", "CHANGE_ME");
    private static String DLPID = System.getenv().getOrDefault("NS_PAY_DLP_ID", "CHANGE_ME");
    private static String PRODID = System.getenv().getOrDefault("NS_PAY_PRODUCT_ID", "CHANGE_ME");
    private static String NETURL = "https://api.zj96596.com/oip-gateway-server/zjrcuoip/gateway.do";
    private static String APPPRIVATEKEY = System.getenv().getOrDefault("NS_PAY_PRIVATE_KEY", "CHANGE_ME");
    private static String PUBLICKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm7e3KBAv4xmkiJ11lsYAjEkS4YTBC2vlq6Lz/Vv73vE1sS4+yucCzuiatxs6im0dL9Q4UwlZlTGicQHAvNl+Rr6xLWIPiBXRmVAyTOtUKQoKm4LHvqmpAVRtqQydnC0m+sUCrgoyoSmd/pmwGJfBkY/VDo9exMVXaTrXeOEdthDrNBi3BG43y64mZ7HdEvOTXhils+CXRXV3ORv8DNxSqnVaur4bNFtaZ04Se49obq3+v7i9ePDVBLFOaJ6CY7HI7a+CKkepSq5TkH5hrV0LRUV6joUJXXsB7KqE7dEQMWJGdZow5pOfWMVqRHNhcw+5hAc4Ch1mAIx0fq8IaxpClwIDAQAB";
    
    
    public static void createOrder(String vendorKey, String orderCode) 
    {
        System.out.println("-订单创建-run--");
        // 1.获取通讯连接对象
        INetTools netTools = NetToolsHttpFactory.getHttpInstance();
        // 2.生成公共请求报文对象
        OipReqBean oipReqBean = new OipReqBean();
        // 设置应用id
        oipReqBean.setAppId(APPID);
        // 设置开发者id
        oipReqBean.setDlpId(DLPID);
        // 设置产品id
        oipReqBean.setProdId(PRODID);
        // 设置api方法名
        oipReqBean.setMethod("weixin.payment.create.order");
        // 设置api版本
        oipReqBean.setVersion("1.0.0");
        // 设置时间戳
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        oipReqBean.setTimestamp(sf.format(new Date()));
        // 3.生成请求配置对象,配置请求地址/url等信息
        NetReqConfBean netReqConfBean = new NetReqConfBean();
        // 应用私钥
        netReqConfBean.setAppPrivateKey(APPPRIVATEKEY);
        
        // 请求地址
        netReqConfBean.setNetUrl(NETURL);
        // 开发者公钥
        netReqConfBean.setPublicKey(PUBLICKEY);
        // 4.生成请求业务报文对象
        WXOrderCreateGetRequestBean WXorderCreateGetRequestBean = new WXOrderCreateGetRequestBean();

        WXorderCreateGetRequestBean.setAppTp("04"); //01-电脑端WEB接入，02-POS接入，03-移动应用接入，04-移动端H5接入
        WXorderCreateGetRequestBean.setTxTp("20"); //20-消费
        WXorderCreateGetRequestBean.setPurpPrtry("A6004004");//A6004004-第三方收单
        // TODO 手动修改
        WXorderCreateGetRequestBean.setMerchId("801737201519609");//商户号-TODO
        WXorderCreateGetRequestBean.setBizId(orderCode);//业务流水号
        
        WXorderCreateGetRequestBean.setPayCtAmount("0.01"); //支付金额，单位元 | 需要小数点?
        WXorderCreateGetRequestBean.setProductDesc("缴费"); //商品描述
        WXorderCreateGetRequestBean.setChannelType("WEIXIN"); //WEIXIN-微信支付
        WXorderCreateGetRequestBean.setDeviceInfo("WEB"); //网页或公众号内支付请传"WEB"
        WXorderCreateGetRequestBean.setUserIp("127.0.0.1");
        WXorderCreateGetRequestBean.setUserMac("127.0.0.1");
        WXorderCreateGetRequestBean.setTradeType("MPROGRAM");//JSAPI-公众号支付 NATIVE-扫码支付 APP-APP支付 MWEB-H5支付 MPROGRAM-小程序支付
        WXorderCreateGetRequestBean.setSubAppid("wx8b4db457e120ce0d"); //公众账号id

        
        WXorderCreateGetRequestBean.setSubOpenid("o7rOf57pFmUoqemLi2VrbwqPwKVQ"); //用户标识

        // 6.生成业务报文json对象
        oipReqBean.setBizContent(JSON.toJSONString(WXorderCreateGetRequestBean));
        System.out.println("请求业务："+oipReqBean.getBizContent());
        // 通讯获取报文对象
        OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
        
        System.out.println("oipRspBean.isSuccess():"+oipRspBean.isSuccess());
        System.out.println("oipRspBean.getCode()："+oipRspBean.getCode());
        System.out.println("oipRspBean.isSignValid():"+oipRspBean.isSignValid());
        System.out.println("oipRspBean.getBizContent():"+oipRspBean.getBizContent());
        
        // 响应成功
        if (oipRspBean.isSuccess()) {
            System.out.println("响应码："+oipRspBean.getCode()+"，验签结果："+oipRspBean.isSignValid());
            String bizJson = oipRspBean.getBizContent();
            if (bizJson != null && bizJson.isEmpty() == false) {
                Map bizData = JSON.parseObject(bizJson, Map.class);
                System.out.println(bizData);
            }
        }
    }
    
    
    // 正式调用，对应公钥 私钥 公众号appid 请求地址  商户号未修改 2022-06-07
    public WxPayData topayIvc(String openid, String tradeNo, BigDecimal amt) 
    {
        System.out.println("-订单创建-run--");
        // 1.获取通讯连接对象
        INetTools netTools = NetToolsHttpFactory.getHttpInstance();
        // 2.生成公共请求报文对象
        OipReqBean oipReqBean = new OipReqBean();
        // 设置应用id
        oipReqBean.setAppId(APPID);
        // 设置开发者id
        oipReqBean.setDlpId(DLPID);
        // 设置产品id
        oipReqBean.setProdId(PRODID);
        // 设置api方法名
        oipReqBean.setMethod("weixin.payment.create.order");
        // 设置api版本
        oipReqBean.setVersion("1.0.0");
        // 设置时间戳
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        oipReqBean.setTimestamp(sf.format(new Date()));
        // 3.生成请求配置对象,配置请求地址/url等信息
        NetReqConfBean netReqConfBean = new NetReqConfBean();
        // 应用私钥
        netReqConfBean.setAppPrivateKey(APPPRIVATEKEY);
        // 请求地址
        netReqConfBean.setNetUrl(NETURL);
        // 开发者公钥
        netReqConfBean.setPublicKey(PUBLICKEY);
        // 4.生成请求业务报文对象
        WXOrderCreateGetRequestBean WXorderCreateGetRequestBean = new WXOrderCreateGetRequestBean();

        WXorderCreateGetRequestBean.setAppTp("04"); //01-电脑端WEB接入，02-POS接入，03-移动应用接入，04-移动端H5接入
        WXorderCreateGetRequestBean.setTxTp("20"); //20-消费
        WXorderCreateGetRequestBean.setPurpPrtry("A6004004");//A6004004-第三方收单
        // TODO 手动修改
        WXorderCreateGetRequestBean.setMerchId("803539923182789");//商户号-TODO
        WXorderCreateGetRequestBean.setBizId(tradeNo);//业务流水号
        
        DecimalFormat df = new DecimalFormat("0.00#");//配置格式:保留2位小数，不够补0
        WXorderCreateGetRequestBean.setPayCtAmount(df.format(amt)); //支付金额，单位元 | 需要小数点?
        WXorderCreateGetRequestBean.setProductDesc("缴费"); //商品描述
        WXorderCreateGetRequestBean.setChannelType("WEIXIN"); //WEIXIN-微信支付
        WXorderCreateGetRequestBean.setDeviceInfo("WEB"); //网页或公众号内支付请传"WEB"
        WXorderCreateGetRequestBean.setUserIp("127.0.0.1");
        WXorderCreateGetRequestBean.setUserMac("127.0.0.1");
        WXorderCreateGetRequestBean.setTradeType("MPROGRAM");//JSAPI-公众号支付 NATIVE-扫码支付 APP-APP支付 MWEB-H5支付 MPROGRAM-小程序支付
//        WXorderCreateGetRequestBean.setSubAppid("3456587789641368"); //公众账号id
//        WXorderCreateGetRequestBean.setSubAppid("wx8b4db457e120ce0d"); //公众账号id
        WXorderCreateGetRequestBean.setSubAppid("wxa8a6cfdb6ea1e8f0"); //公众账号id

        
        WXorderCreateGetRequestBean.setSubOpenid(openid); //用户标识

        // 6.生成业务报文json对象
        oipReqBean.setBizContent(JSON.toJSONString(WXorderCreateGetRequestBean));
        System.out.println("请求业务："+oipReqBean.getBizContent());
        // 通讯获取报文对象
        OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
        
        System.out.println("oipRspBean.isSuccess():"+oipRspBean.isSuccess());
        System.out.println("oipRspBean.getCode()："+oipRspBean.getCode());
        System.out.println("oipRspBean.isSignValid():"+oipRspBean.isSignValid());
        System.out.println("oipRspBean.getBizContent():"+oipRspBean.getBizContent());
        
        
        // 响应成功
        if (oipRspBean.isSuccess()) {
            System.out.println("响应码："+oipRspBean.getCode()+"，验签结果："+oipRspBean.isSignValid());
            String bizJson = oipRspBean.getBizContent();
            if (bizJson != null && bizJson.isEmpty() == false) {
                Map bizData = JSON.parseObject(bizJson, Map.class);
                System.out.println(bizData);
            }
            String bizContent = oipRspBean.getBizContent();
            JSONObject object = JSON.parseObject(bizContent);  
            JSONObject po = JSON.parseObject(object.get("wcPayData").toString());
            WxPayData res = new WxPayData();
            res.setAppId(po.get("appId").toString());
            res.setTimeStamp(po.get("timeStamp").toString());
            res.setNonceStr(po.get("nonceStr").toString());
            res.setPack(po.get("package").toString());
            res.setSignType(po.get("signType").toString());
            res.setPaySign(po.get("paySign").toString());
            return res;
        }
        else
            throw TofocusException.of();
    }
    
    
    // 支付对账单获取
    public static void downloadBill(String billDate) 
    {
        // 1.获取通讯连接对象
        INetTools netTools = NetToolsHttpFactory.getHttpInstance();
        // 2.生成公共请求报文对象
        OipReqBean oipReqBean = new OipReqBean();
        // 设置应用id
        oipReqBean.setAppId(APPID);
        // 设置开发者id
        oipReqBean.setDlpId(DLPID);
        // 设置产品id
        oipReqBean.setProdId(PRODID);
        // 设置api方法名
        oipReqBean.setMethod("zjrcuoip.payment.bill.downloadurl.query");
        // 设置api版本
        oipReqBean.setVersion("1.0.0");
        // 设置时间戳

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        oipReqBean.setTimestamp(sf.format(new Date()));
        // 3.生成请求配置对象,配置请求地址/url等信息
        NetReqConfBean netReqConfBean = new NetReqConfBean();
        // 应用私钥
        netReqConfBean.setAppPrivateKey(APPPRIVATEKEY);
        // 请求地址
        netReqConfBean.setNetUrl(NETURL);
        // 开发者公钥
        netReqConfBean.setPublicKey(PUBLICKEY);
        // 4.生成请求业务报文对象
        WXBillGetRequestBean WXbillGetRequestBean = new WXBillGetRequestBean();
        // 5.设置业务报文 -- 下载报参数错误时，可能是准生产方当天的对账单的.ok文件未产生
        // 加载当前用户信息
        WXbillGetRequestBean.setBillDate(billDate);
        WXbillGetRequestBean.setBillType("ALL");
        WXbillGetRequestBean.setMerchId("801737201519609");
        WXbillGetRequestBean.setTarType("GZIP");
        
        // 6.生成业务报文json对象
        oipReqBean.setBizContent(JSON.toJSONString(WXbillGetRequestBean));
        // 通讯获取报文对象
        OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
        // 响应成功
        if (oipRspBean.isSuccess()) {
            System.out.println("响应码："+oipRspBean.getCode()+"，验签结果：" + oipRspBean.isSignValid());
            String bizJson = oipRspBean.getBizContent();
            Map bizData = JSON.parseObject(bizJson, Map.class);
            System.out.println(bizData);
        }
        System.out.println("响应原文：");
        System.out.println(oipRspBean.toMap());

    }
    
}
