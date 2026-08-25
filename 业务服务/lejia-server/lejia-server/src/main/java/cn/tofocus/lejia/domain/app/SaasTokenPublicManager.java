package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.token.AccessToken;
import cn.tofocus.lejia.bean.entity.applet.XaszAssociationEntity;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.cache.SaasTokenCache;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.applet.XaszAssociationDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SaasTokenPublicManager
{
    @Autowired
    private SaasTokenCache saasTokenCache;
    
    @Autowired
    private XaszAssociationDao xaszAssociationDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Value("${xasz.saas.token.member.url:https://cloud.xinanshizu.com/farm-member}")
    private String prefixUrl;
    
    public String getToken(String mobile, String openid, XaszAssociationEntity xaszAssociationEntity)
    {
        if (xaszAssociationEntity != null)
        {
            AccessToken accessToken = saasTokenCache.get(openid);
            if (accessToken == null)
            {
                //                AuthenticationContext loginAsClient = securityContextUtil.loginAsClient("farmSaas", "ZY652VSFGG");
                MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
                Map<String, Object> json = new HashMap<>();
                json.put("phone", mobile);
                json.put("market", xaszAssociationEntity.getMarket());
                multiValueMap.setAll(json);
                String url = prefixUrl + Constant.FarmSaas.appletLoginUrl;
                accessToken = HttpUtil.forResult(url, new ParameterizedTypeReference<Result<AccessToken>>()
                {
                }).form(multiValueMap).dateTimeFormat("yyyy-MM-dd HH:mm:ss").post().token(getToken()).exec();
                if (accessToken == null) return null;
                saasTokenCache.put(openid, accessToken);
            }
            else
            {
                if (accessToken.getToken().getExpiration().getTime() < System.currentTimeMillis())
                {
                    String url = prefixUrl + Constant.FarmSaas.appletRefreshTokenUrl;
                    //                    AuthenticationContext loginAsClient = securityContextUtil.loginAsClient("farmSaas", "ZY652VSFGG");
                    MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
                    Map<String, Object> json = new HashMap<>();
                    json.put("refreshToken", accessToken.getRefreshToken().getValue());
                    multiValueMap.setAll(json);
                    
                    accessToken = HttpUtil.forResult(url, new ParameterizedTypeReference<Result<AccessToken>>()
                    {
                    }).form(multiValueMap).dateTimeFormat("yyyy-MM-dd HH:mm:ss").post().token(getToken()).exec();
                    if (accessToken == null) return null;
                    saasTokenCache.put(openid, accessToken);
                }
            }
            return accessToken.getToken().getValue();
        }
        return null;
    }
    
    public BigDecimal getAccountBalance(String mobile, String openid)
    {
        // 获取云农贸账户余额
        XaszAssociationEntity xaszAssociationEntity = xaszAssociationDao.getFarmer(MobileSession.farmerPkey());
        String token = getToken(mobile, openid, xaszAssociationEntity);
        if (StringUtils.isNotBlank(token))
        {
            // 查询数据
            String url = prefixUrl + Constant.FarmSaas.appletAccountBalanceUrl;
            System.out.println("getAccountBalance: " + url);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Currentmarket", xaszAssociationEntity.getMarket() + "");
            headers.add("Member-Token", token);
            try
            {
                return HttpUtil.forResult(url, new ParameterizedTypeReference<Result<BigDecimal>>()
                {
                }).post().headers(headers).exec();
            }
            catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        return BigDecimal.ZERO;
    }
    
    public Integer ecardAccountConsume(BigDecimal amt)
    {
        MktMember member = MobileSession.member();
        XaszAssociationEntity xaszAssociationEntity = xaszAssociationDao.getFarmer(MobileSession.farmerPkey());
        String token = getToken(member.getMobile(), member.getOpenid1(), xaszAssociationEntity);
        if (StringUtils.isNotBlank(token))
        {
            // 查询数据
            String url = prefixUrl + Constant.FarmSaas.ecardAccountConsumUrl;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Currentmarket", xaszAssociationEntity.getMarket() + "");
            headers.add("Member-Token", token);
            MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
            Map<String, Object> json = new HashMap<>();
            json.put("paycode", "farmSaas-ecardAccountConsume");
            json.put("amt", amt);
            multiValueMap.setAll(json);
//            throw TofocusException.of(LejiaErrCode.ACTIVITY_DISABLED);
            
            String exec = HttpUtil.forString(url).post().body(json).headers(headers).form(multiValueMap).exec();
            System.out.println("云农贸支付返回" + exec);
            JSONObject object = JSONObject.parseObject(exec);
            JSONObject jo = object.getJSONObject("result");
            Integer msgPkey = jo.getInteger("pkey");
            return msgPkey;
//            HttpUtil.forResult(url, new ParameterizedTypeReference<Result<Boolean>>()
//            {
//            }).post().form(multiValueMap).headers(headers).exec();
            //            try
            //            {
            //              
            //            }
            //            catch (Exception e)
            //            {
            //                log.error(e.getMessage());
            //            }
        }
        return null;
    }
    
    public Boolean saasRefund(Integer memberKey, String farmer, Integer xaszConsumption, BigDecimal refundAmt)
    {
        MktMember member = memberDao.get(memberKey);
        XaszAssociationEntity xaszAssociationEntity = xaszAssociationDao.getFarmer(farmer);
        String token = getToken(member.getMobile(), member.getOpenid1(), xaszAssociationEntity);
        if (StringUtils.isNotBlank(token))
        {
            // 查询数据
            String url = prefixUrl + Constant.FarmSaas.saasRefund;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Currentmarket", xaszAssociationEntity.getMarket() + "");
            headers.add("Member-Token", token);
            MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
            Map<String, Object> json = new HashMap<>();
            json.put("xaszConsumption", xaszConsumption);
            json.put("refundAmt", refundAmt);
            multiValueMap.setAll(json);
            
            String exec = HttpUtil.forString(url).post().body(json).headers(headers).form(multiValueMap).exec();
            System.out.println("云农贸支付返回" + exec);
            JSONObject object = JSONObject.parseObject(exec);
            Boolean res = object.getBoolean("result");
//            JSONObject jo = object.getJSONObject("result");
            return res;
        }
        return false;
    }
    
    public Map<Integer, String> listMarketName()
    {
        try
        {
            String url = prefixUrl + Constant.FarmSaas.listMarketNameUrl;
            return HttpUtil.forResult(url, new ParameterizedTypeReference<Result<Map<Integer, String>>>()
            {
            }).post().token(getToken()).exec();
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            return null;
        }
    }
    
    // 东屿满一百送礼品券活动 拿云农贸用户每日消费金额
    public BigDecimal getDayConsumptionAmt(String mobile, String openid)
    {
        // 获取云农贸账户余额
        XaszAssociationEntity xaszAssociationEntity = xaszAssociationDao.getFarmer(MobileSession.farmerPkey());
        String token = getToken(mobile, openid, xaszAssociationEntity);
        if (StringUtils.isNotBlank(token))
        {
            // 查询数据
            String url = prefixUrl + Constant.FarmSaas.appletDayConsumptionAmtUrl;
            System.out.println("getDayConsumptionAmt: " + url);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Currentmarket", xaszAssociationEntity.getMarket() + "");
            headers.add("Member-Token", token);
            try
            {
                return HttpUtil.forResult(url, new ParameterizedTypeReference<Result<BigDecimal>>()
                {
                }).post().headers(headers).exec();
            }
            catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        return BigDecimal.ZERO;
    }
    
    private String getToken()
    {
        String url = null;
        if(prefixUrl.contains("farm-member"))
        {
            url = prefixUrl.replace("farm-member", "")
                + "auth/oauth/token?grant_type=client_credentials&client_id=farmSaas&client_secret=ZY652VSFGG";
        }
        else
        {
            url = prefixUrl.replace(":22292", ":21000")
                + "/oauth/token?grant_type=client_credentials&client_id=farmSaas&client_secret=ZY652VSFGG";
        }
        JSONObject json = JSONObject.parseObject(HttpUtil.forString(url).post().exec());
        return json.get("access_token").toString();
    }
}
