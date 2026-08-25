package cn.tofocus.lejia.domain.jdvop;

import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;

import cn.tofocus.common.util.security.RSAUtils;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.jdvop.JdVOPAccessToken;
import cn.tofocus.lejia.config.LejiaConfig;
import cn.tofocus.lejia.exception.LejiaErrCode;

public abstract class BaseJdVOPManager
{
    @Value("${jd.vop.api.serverUrl}")
    String serverUrl;
    
    @Value("${jd.vop.appKey}")
    String appKey;
    
    @Value("${jd.vop.appSecret}")
    String appSecret;
    
    @Value("${jd.vop.privateRsaKey}")
    String privateRsaKey;
    
    @Autowired
    LejiaConfig lejiaConfig;
    
    static final String accessTokenKey = "jd:vop:accessToken";
    
    private static final String VOP_ERR_MSG_PREFIX = "VOP：";
    
    private static final int connectTimeout = 60000;
    
    private static final int readTimeout = 300000;
    
    JdVOPAccessToken getAccessToken()
    {
        return JsonUtil.getBean(lejiaConfig.get(accessTokenKey), JdVOPAccessToken.class);
    }
    
    JdClient jdClient()
    {
        JdVOPAccessToken accessToken = getAccessToken();
        if (accessToken == null)
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "找不到access_token");
        return new DefaultJdClient(serverUrl, accessToken.getAccessToken(), appKey, appSecret, connectTimeout, readTimeout);
    }
    
    String collection2Str(Collection<?> skuIdList)
    {
        return skuIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
    
    String wrapVopErrMsg(String errMsg)
    {
        return VOP_ERR_MSG_PREFIX + errMsg;
    }

    void printResponseFailedLog(Logger log, Object request, Object response)
    {
        log.info("[京东VOP]请求结果异常\n【请求参数】{}\n【请求响应】{}", JsonUtil.toString(request), JsonUtil.toString(response));
    }
    
    public String decodeRsa(String content)
        throws Exception
    {
        if (content == null)
            return null;
        if (content.isEmpty())
            return content;
        return RSAUtils.decrypt(content, privateRsaKey);
    }
}
