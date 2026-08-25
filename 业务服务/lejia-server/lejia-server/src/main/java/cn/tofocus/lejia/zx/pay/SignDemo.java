package cn.tofocus.lejia.zx.pay;

import java.util.HashMap;
import java.util.Map;

import cn.tofocus.lejia.zx.pay.constants.Constants;
import cn.tofocus.lejia.zx.pay.util.Sign;
import cn.tofocus.lejia.zx.pay.util.SignRSA;

public class SignDemo
{
    public static void main(String[] args)
    {
        // 待加签/待验签的数据示例
        Map<String, String> info = new HashMap<>();
//        info.put("service", "unified.trade.micropay");
//        info.put("mch_id", "023200010000001");
//        info.put("out_trade_no", "7777D020122510481090901");
//        info.put("body", "一个测试商品");
//        info.put("total_fee", "14000");
//        info.put("mch_create_ip", "127.0.0.1");
//        info.put("auth_code", "134861978743589542");
//        info.put("Chinese", "中文，测试");
        info.put("transaction_id", "4200001159202111182283257818");
        info.put("bank_type", "ICBC_CREDIT");
        info.put("openid", "owUmjwmsjQvNPhp2lyjt7rLlChXs");
        info.put("err_code", "");
        info.put("return_msg", "OK");
        info.put("settleDate","");
        info.put("fee_type","CNY");
        info.put("mch_id","421010060000001");
        info.put("sub_appid","wx23587f1c70f6b8da");
        info.put("cash_fee","1");
        info.put("sub_openid","o85kU5JuPuKOUqBtkhShFKG7tNOU");
        info.put("device_info","WEB");
        info.put("out_trade_no","91181121827419");
        info.put("coupon_fee","0");
        info.put("err_msg","");
        info.put("total_fee","1");
        info.put("cardAttr","");
        info.put("trade_type","pay.weixin.native");
        info.put("attach","");
        info.put("time_end","20211118135830");
        info.put("return_code","SUCCESS");
        
        
        // 数据排序及拼接处理
        String plain = Sign.sortByASCII(info);
        System.out.println("待加签数据拼接结果：" + plain);
        
        System.out.println("\n==========Sign and VerifySign==========");
        
        // 对待发送数据进行加签
        String sign = Sign.sign(plain, Constants.PWD, Constants.PRI_KEY, Constants.PUB_CER);
        System.out.println("加签结果：" + sign);
        
        // 对待验证数据进行验签
        System.out.println("验签结果：" + Sign.verifySign(plain, sign, Constants.PUB_CER));
        
        System.out.println("\n\n==========Sign and VerifySign by RSA==========");
        
        // 获取私钥
        String priKey = SignRSA.getPriKey(Constants.PWD, Constants.PRI_KEY);
        System.out.println("RSA私钥：" + priKey);
        // 对待发送数据进行加签
        String signRSA = SignRSA.signRSA(plain, priKey);
        System.out.println("RSA加签结果：" + signRSA);
        
        // 获取公钥
        String pubCer = SignRSA.getPubCer(Constants.PUB_CER);
        System.out.println("RSA公钥：" + pubCer);
        // 对待验证数据进行验签
        System.out.println("RSA验签结果：" + SignRSA.verifySignRSA(plain, signRSA, pubCer));
    }
}
