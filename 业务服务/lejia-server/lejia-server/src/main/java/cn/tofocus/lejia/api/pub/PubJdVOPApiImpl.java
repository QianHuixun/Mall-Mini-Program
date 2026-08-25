package cn.tofocus.lejia.api.pub;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cn.tofocus.lejia.service.HttpClientService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/pub/jdVOP")
@RestController
public class PubJdVOPApiImpl
{
    @Value("${jd.vop.appKey}")
    private String appKey;
    
    @Value("${jd.vop.appSecret}")
    private String appSecret;
    
    @Value("${jd.vop.openAuth.baseUrl}")
    private String openAuthBaseUrl;
    
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>()
    {
    };
    
    @Resource
    private HttpClientService httpClientService;
    
    @GetMapping("/callbackToken")
    public Map<String, Object> callbackToken(@RequestParam("code") String code)
    {
        String accessTokenUrl = openAuthBaseUrl + "/oauth2/access_token?" + "app_key=" + appKey + "&app_secret="
            + appSecret + "&code=" + code + "&grant_type=authorization_code";
        log.info("开始请求获取accessToken：{}", accessTokenUrl);
        HttpGet httpGet = new HttpGet(accessTokenUrl);
        String tokenResp = httpClientService.httpGet(httpGet);
        return JSON.parseObject(tokenResp, MAP_TYPE);
    }
}
