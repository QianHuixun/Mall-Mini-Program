package cn.tofocus.lejia;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.common.util.security.MD5;
import cn.tofocus.core.json.JsonObject;
import cn.tofocus.lejia.bean.dto.wanli.WanliCorrelationInfo;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.domain.wanli.WanliManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class WanliTest
{
    @Autowired
    private WanliManager wanliManager;
    
    @Test
    public void test1()
    {
//        wanliManager.storeCreate(); 
        JsonObject json = new JsonObject();
        json.put("outOrderNo", "621aedeefcf7417691ac57ee50876cbf");
        WanliCorrelationInfo info = new WanliCorrelationInfo();
        info.setSecret("af87c63304fb47d7936040189a7f90bd");
        info.setAppId("652f4f1de4b032fb93e9cbd9");
        info.setStoreId("fb19a074812a4b03ab4ff3823ef27e50");
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
        String exec = HttpUtil.forString("https://openapi.wlhulian.com/" + "/api/v1/order/query/detail").post().body(param.toString()).exec();
        log.info("查看订单详情返回结果: {}", exec);
    }
    
    private String getSign(String data, String timestamp, String nonce, WanliCorrelationInfo info)
    {
        String splicing = info.getSecret() + timestamp + nonce + info.getStoreId() + data;
        return MD5.getMD5(splicing);
    }
}
