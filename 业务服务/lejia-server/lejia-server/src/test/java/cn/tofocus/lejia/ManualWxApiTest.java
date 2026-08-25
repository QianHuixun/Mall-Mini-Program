package cn.tofocus.lejia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.app.AppWxMsgSecCheckResult;
import cn.tofocus.lejia.domain.WxManager;

@SpringBootTest
public class ManualWxApiTest
{
    @Autowired
    private WxManager wxManager;
    
    @Test
    public void getDeliveryList()
    {
        Integer ascription = 22;
        JSONArray res = wxManager.getDeliveryList(ascription);
        System.out.println(JsonUtil.toString(res, true));
        for (int i = 0; i < res.size(); i++)
        {
            JSONObject item = res.getJSONObject(i);
            if (item.getString("delivery_name").contains("圆通"))
            {
                System.out.println(item);
            }
        }
    }
    
    @Test
    public void testUploadShippingInfo()
    {
        // 物流模式，发货方式枚举值：
        // 1、实体物流配送采用快递公司进行实体物流配送形式
        // 2、同城配送
        // 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式
        // 4、用户自提
        Integer logisticsType = 1;
        String itemDesc = "鸡蛋(500g)*1";
        String openid = "o0bmw6xYC3QM3LXC1FUcvOFv_pdg";
        Integer ascription = 22;
        
        //        String kcCode = "4200002354202410118687073069";
        //        boolean res = wxManager.uploadShippingInfo(kcCode, itemDesc, logisticsType, openid, ascription);
        
        //        String orderNumber = "91091024492275";
        //        String mchid = "1575634231";
        //        boolean res = wxManager.uploadShippingInfo(orderNumber, mchid, itemDesc, logisticsType, openid, ascription);
        
        String trackingNo = "1111111113";
        String expressCompany = "YTO";
        String kcCode = "4200002353202410117570088724";
        boolean res = wxManager.uploadExpressShippingInfo(kcCode,
            itemDesc,
            logisticsType,
            trackingNo,
            expressCompany,
            null,
            null,
            openid,
            ascription);
        
        System.out.println(res);
    }
    
    @Test
    public void testMsgSecCheck()
    {
        Integer ascription = 22;
        String openid = "o0bmw69PYUEMn9Ds1R3p35_VArMU";
        
        String content = "";
        AppWxMsgSecCheckResult res = wxManager.commentSecCheck(content, openid, ascription);
        System.out.println("识别内容：" + content);
        System.out.println("结果：" + res.getResult().getSuggest());
        System.out.println(JsonUtil.toString(res, true));
    }
}
