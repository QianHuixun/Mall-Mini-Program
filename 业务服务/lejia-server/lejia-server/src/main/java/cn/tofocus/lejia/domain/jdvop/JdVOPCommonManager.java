package cn.tofocus.lejia.domain.jdvop;

import java.net.URLEncoder;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.jdvop.JdVOPAccessToken;
import cn.tofocus.lejia.service.HttpClientService;
import cn.tofocus.lejia.util.RsaCoderUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 京东VOP - 通用接口
 */
@Slf4j
@Component
public class JdVOPCommonManager extends BaseJdVOPManager
{
    @Value("${jd.vop.username}")
    private String username;
    
    @Value("${jd.vop.plaintextPassword}")
    private String plaintextPassword;
    
    @Value("${jd.vop.redirectUri}")
    private String redirectUri;
    
    @Value("${jd.vop.openAuth.baseUrl}")
    private String openAuthBaseUrl;
    
    @Resource
    private HttpClientService httpClientService;
    
    private static final String UTF_8 = "utf-8";
    
    @Transactional(rollbackFor = Exception.class)
    public void updateAccessTokenTask()
        throws Exception
    {
        // 如果redis中的token不为空，则先尝试刷新token
        JdVOPAccessToken token = getAccessToken();
        if (token != null && StringUtil.isNotBlank(token.getRefreshToken()))
        {
            // 刷新token
            log.info("开始刷新token");
            String refreshTokenUrl = openAuthBaseUrl + "/oauth2/refresh_token?" + "app_key=" + appKey + "&app_secret="
                + appSecret + "&grant_type=refresh_token&refresh_token=" + token.getRefreshToken();
            log.info("开始请求刷新token：{}", refreshTokenUrl);
            HttpGet httpGet = new HttpGet(refreshTokenUrl);
            String tokenResp = httpClientService.httpGet(httpGet);
            log.info("请求刷新token完成：{}", tokenResp);
            token = JsonUtil.getBean(tokenResp, JdVOPAccessToken.class);
            if (token != null && token.getCode() == 0)
            {
                lejiaConfig.put(accessTokenKey, JsonUtil.toString(token));
                return;
            }
        }
        // 重新获取token
        log.info("开始重新获取token");
        String encodeRedirectUri = URLEncoder.encode(redirectUri, UTF_8);
        String encodeUsername = URLEncoder.encode(username, UTF_8);
        String md5Password = DigestUtils.md5Hex(plaintextPassword);
        String ciphertextPassword = RsaCoderUtils.encryptByPrivateKey(md5Password, privateRsaKey);
        String encodePassword = URLEncoder.encode(ciphertextPassword, UTF_8);
        String getCodeUrl = openAuthBaseUrl + "/oauth2/authorizeForVOP?" + "app_key=" + appKey + "&redirect_uri="
            + encodeRedirectUri + "&username=" + encodeUsername + "&password=" + encodePassword + "&response_type=code"
            + "&scope=snsapi_base";
        log.info("拼接授权url完成：{}", getCodeUrl);
        HttpGet httpGet = new HttpGet(getCodeUrl);
        String tokenResp = httpClientService.httpGet(httpGet);
        log.info("请求获取token完成：{}", tokenResp);
        token = JsonUtil.getBean(tokenResp, JdVOPAccessToken.class);
        if (token != null && token.getCode() == 0)
        {
            lejiaConfig.put(accessTokenKey, JsonUtil.toString(token));
        }
    }
}
