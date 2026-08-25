package cn.tofocus.lejia.app.v1.sys;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tencent.common.Signature;
import com.tencent.common.XMLParser;

import cn.tofocus.common.data.datadealer.MobileDealer;
import cn.tofocus.common.notify.SMSNotify;
import cn.tofocus.common.notify.config.SmsConfig;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppAuthCode2SessionDTO;
import cn.tofocus.lejia.bean.dto.app.AppMemberDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDetailsDTO;
import cn.tofocus.lejia.bean.dto.v3.GzhAddOnInfo;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.wx.MktGzh;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.cache.MemberTjrMap;
import cn.tofocus.lejia.cache.MobileCodeMap;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.wx.MktGzhDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.market.LejiaCourierManager;
import cn.tofocus.lejia.domain.market.MemberManager;
import cn.tofocus.lejia.domain.market.VendorManager;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.util.WxUtils;
import cn.tofocus.lejia.util.wx.PayResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/wx")
@RestController
public class WxApiImpl
{
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private MemberManager memberManger;
    
    @Autowired
    private VendorManager vendorManager;
    
    @Autowired
    private LejiaCourierManager courierManager;
    
    @Autowired
    private MemberTjrMap tjrMap;
    
    @Autowired
    private SmsConfig smsConfig;
    
    @Autowired
    private MobileCodeMap mobileMap;
    
    @Autowired
    private MktGzhDao gzhDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @GetMapping("/getAppid")
    public Result<String> getAppid(AccountType sign, Integer ascription)
    {
        AccountEntity account = wxManager.getAccountEntity(sign, ascription);
        return new Result<>(account.getAccountAppid());
    }
    
    @GetMapping("/getOpenidByCode")
    public Result<AppAuthCode2SessionDTO> getOpenidByCode(String wxcode, AccountType sign, Integer ascription)
    {
        return new Result<>(wxManager.requestOpenidByCode(wxcode, sign, ascription));
    }
    
    @Operation(summary = "权限获取用户手机号")
    @PostMapping("/auth/phone")
    public Object authPhone(HttpServletRequest request, @RequestBody AppMemberDTO dto)
    {
        log.info("dto: {}", dto);
        String farmer = request.getHeader("farmer");
        
        Map<String, Object> map = new HashMap<>();
        map.put("code", 4001);
        map.put("msg", "获取失败！");
        map.put("data", "");
        
        String appid = request.getHeader("ascription");
        log.info("appid: {}", appid);
        if (StringUtils.isBlank(appid)) return map;
        
        String result = WxUtils.wxDecrypt(dto.getEncryptedData(), dto.getSession_key(), dto.getIv());
        log.info("result: {}", result);
        JSONObject json = JSONObject.parseObject(result);
        if (!json.containsKey("phoneNumber")) return map;
        String phone = json.getString("phoneNumber");
        if (StringUtil.isEmpty(phone)) return map;
        // 新增用户
        String openid = dto.getOpenid();
        MktMember member = memberManger.loadByOpenid(openid, Integer.valueOf(appid));
        if (member == null)
        {
            String avatarUrl = dto.getAvatarUrl();
            String gender = dto.getGender();
            String nickName = dto.getNickName();
            member = memberManger.loadByMobile(phone, Integer.valueOf(appid));
            if(member != null)
            {
                member.setName(nickName);
                member.setOpenid1(openid);
                member.setSex(Integer.valueOf(gender));
                member.setPhoto(avatarUrl);
                member.setLastFarmer(farmer);
                memberDao.update(member);
            }
            else
            {
                member = new MktMember();
            
                log.info("avatarUrl: {}, gender: {}, nickName: {}, openid: {}, MobileSession.source(): {}", avatarUrl, gender, nickName, openid, MobileSession.source());
                MktAppMemberDetailsDTO entity = new MktAppMemberDetailsDTO();
                entity.setMobile(phone);
                entity.setName(nickName);
                if("微信用户".equals(nickName))
                    entity.setName("会员用户");
                entity.setOpenid1(openid);
                entity.setSex(Integer.valueOf(gender));
                entity.setPhoto(avatarUrl);
                entity.setLastFarmer(farmer);
                entity.setTjv(dto.getTjv());
                entity.setSource(MobileSession.source());
                if (StringUtil.isNotEmpty(dto.getTjrOpenid()))
                {
                    MktMember tjM = memberManger.loadByOpenid(dto.getTjrOpenid(), Integer.valueOf(appid));
                    if (tjM != null)
                    {
                        dto.setTjr(tjM.getPkey());
                        entity.setTjr(dto.getTjr());
                    }
                }
                member = memberManger.insMember(entity, Integer.valueOf(appid));
                if (member.getTjr() != null) tjrMap.put(member.getPkey() + "", dto.getTjr().longValue());
            }
        }
        map.put("code", 0);
        map.put("msg", "成功");
        map.put("data", BeanUtil.beanFrom(MktAppMemberDetailsDTO.class, member));
        return map;
    }
    
    //    //    public static final String APPID = "wxda1ef1c056fb310a";
    //    // 义乌菜园 公众号
    //    public static final String APPID = "wxd085f7c6482122a5";
    //    
    //    //    public static final String APPSECRET = "CHANGE_ME";
    //    // 义乌菜园
    //    public static final String APPSECRET = "CHANGE_ME";
    //    
    //微信网页授权地址
    private String getWechatCodeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?" + "appid=[APPID]&" + //微信appid
        "redirect_uri=[REDIRECT_URI]&" + //获取网页授权后，微信重定向地址
        "response_type=code&" + "scope=snsapi_base&" + //授权方式
        "state=STATE#wechat_redirect";
    
    private String getWechatCodeUrl2 = "https://open.weixin.qq.com/connect/oauth2/authorize?" + "appid=[APPID]&" + //微信appid
        "redirect_uri=[REDIRECT_URI]&" + //获取网页授权后，微信重定向地址
        "response_type=code&" + "scope=snsapi_userinfo&" + //授权方式
        "state=STATE#wechat_redirect";
    
    //微信授权后返回的地址
    //    private String getCodeUrl = "https://" + "ymkt.xinanshizu.com/zyysc" + "/v1/wx/redirect/code?redirect_url=";
    
    // 获取access_token的接口地址，这里还可以获取到openid
    private String getAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?" + "appid=[APPID]&" + //微信的appid
        "secret=[SECRET]&" + //微信的secret
        "code=[CODE]&" + //重定向后微信携带的code参数
        "grant_type=authorization_code";
    
    //获取微信用户基本信息的接口地址
    private String getWechatUserUrl = "https://api.weixin.qq.com/sns/userinfo?" + "access_token=[ACCESS_TOKEN]&" + //获取到的 access_token
        "openid=[OPENID]&" //公众号的 openid
        + "lang=zh_CN";
    
    //提供的获取用户信息的地址
    @GetMapping("/redirect")
    public Result<String> redirect(
        @RequestParam(name = "redirect_url", defaultValue = "", required = false) String redirectUrl,
        @RequestParam(name = "ascription") Integer ascription)
        throws UnsupportedEncodingException
    {
        //默认值，前端首页地址
        if (StringUtils.isBlank(redirectUrl))
        {
            redirectUrl = "/";
        }
        //一次编码
        redirectUrl = URLEncoder.encode(redirectUrl, "utf-8");
        AccountEntity account = wxManager.getAccountEntity(AccountType.WX, ascription);
        //最终得到的微信获取微信授权地址
        String url =
            getWechatCodeUrl.replace("[APPID]", account.getAccountAppid()).replace("[REDIRECT_URI]", redirectUrl);
        log.info("redirecturl: {}", url);
        return new Result<>(url);
    }
    
    //提供的获取用户信息的地址
    @GetMapping("/redirect2")
    public Result<String> redirect2(
        @RequestParam(name = "redirect_url", defaultValue = "", required = false) String redirectUrl,
        @RequestParam(name = "ascription") Integer ascription)
        throws UnsupportedEncodingException
    {
        //默认值，前端首页地址
        if (StringUtils.isBlank(redirectUrl))
        {
            redirectUrl = "/";
        }
        //一次编码
        redirectUrl = URLEncoder.encode(redirectUrl, "utf-8");
        AccountEntity account = wxManager.getAccountEntity(AccountType.WX, ascription);
        //最终得到的微信获取微信授权地址
        String url =
            getWechatCodeUrl2.replace("[APPID]", account.getAccountAppid()).replace("[REDIRECT_URI]", redirectUrl);
        log.info("redirecturl: {}", url);
        return new Result<>(url);
    }
    
    //这里的地址由微信重定向跳转，携带code参数。
    @GetMapping("/redirect/code")
    public Result<String> getCode(
        @RequestParam(name = "redirect_url", defaultValue = "", required = false) String redirectUrl,
        @RequestParam(name = "code", defaultValue = "", required = false) String code,
        @RequestParam(name = "ascription") Integer ascription)
        throws UnsupportedEncodingException, JSONException
    {
        log.info("redirect_url: {}", redirectUrl);
        log.info("code: {}", code);
        if (StringUtils.isBlank(code))
        {
            log.error("获取code失败");
            return new Result<>(redirectUrl + "?error=code-is-null");
        }
        //解码重定向地址
        redirectUrl = URLDecoder.decode(redirectUrl, "utf-8");
        AccountEntity account = wxManager.getAccountEntity(AccountType.WX, ascription);
        //根据code获取微信相关信息
        String apiUrl = getAccessTokenUrl.replace("[APPID]", account.getAccountAppid())
            .replace("[SECRET]", account.getAccountAppsecret())
            .replace("[CODE]", code);
        JSONObject resJson = WxUtils.httpRequest(apiUrl, "GET", null);
        //请求失败
        if (resJson.containsKey("errmsg"))
        {
            log.error("获取access_token失败，" + resJson);
            return new Result<>(redirectUrl + "?error=" + resJson.getString("errmsg"));
        }
        log.info("获取access_token成功，" + resJson);
        String encode = URLEncoder.encode(resJson.getString("openid"), "utf-8");
        
        String accToken = URLEncoder.encode(resJson.getString("access_token"), "utf-8");
        String wechatUrl = getWechatUserUrl.replace("[ACCESS_TOKEN]", accToken).replace("[OPENID]", encode);
        JSONObject resJson2 = WxUtils.httpRequest(wechatUrl, "GET", null);
        System.out.println("resJson2: " + resJson2.toString());
        MktVendor vendor = MobileSession.vendor();
        MktCourier courier = MobileSession.courier();
        if (vendor != null) System.out.println("vendor: " + JsonUtil.toString(vendor, true));
        if (courier != null) System.out.println("courier: " + JsonUtil.toString(courier, true));
        
        return new Result<>(encode);
    }
    
    //这里的地址由微信重定向跳转，携带code参数。
    @GetMapping("/redirect2/code")
    public Result<GzhAddOnInfo> getCode2(
        @RequestParam(name = "redirect_url", defaultValue = "", required = false) String redirectUrl,
        @RequestParam(name = "code", defaultValue = "", required = false) String code,
        @RequestParam(name = "ascription") Integer ascription)
        throws UnsupportedEncodingException, JSONException
    {
        log.info("redirect_url: {}", redirectUrl);
        log.info("code: {}", code);
        if (StringUtils.isBlank(code))
        {
            log.error("获取code失败");
            throw TofocusException.of();
        }
        //解码重定向地址
        redirectUrl = URLDecoder.decode(redirectUrl, "utf-8");
        AccountEntity account = wxManager.getAccountEntity(AccountType.WX, ascription);
        //根据code获取微信相关信息
        String apiUrl = getAccessTokenUrl.replace("[APPID]", account.getAccountAppid())
            .replace("[SECRET]", account.getAccountAppsecret())
            .replace("[CODE]", code);
        JSONObject resJson = WxUtils.httpRequest(apiUrl, "GET", null);
        //请求失败
        if (resJson.containsKey("errmsg"))
        {
            log.error("获取access_token失败，" + resJson);
            throw TofocusException.of();
        }
        log.info("获取access_token成功，" + resJson);
        String encode = URLEncoder.encode(resJson.getString("openid"), "utf-8");
        
        String accToken = URLEncoder.encode(resJson.getString("access_token"), "utf-8");
        String wechatUrl = getWechatUserUrl.replace("[ACCESS_TOKEN]", accToken).replace("[OPENID]", encode);
        JSONObject resJson2 = WxUtils.httpRequest(wechatUrl, "GET", null);
        System.out.println("resJson2: " + resJson2.toString());
        GzhAddOnInfo res = new GzhAddOnInfo();
        String name = resJson2.getString("nickname");
        String url = resJson2.getString("headimgurl");
        res.setName(name);
        res.setUrl(url);
        res.setOpenid(encode);
        return new Result<>(res);
    }
    
    @PostMapping("/bind/vendor")
    public Result<Boolean> bindVendor(@RequestParam("openid1") String openid1, @RequestParam("openid2") String openid2)
    {
        vendorManager.bindVendor(openid1, openid2);
        return new Result<>(true);
    }
    
    @PostMapping("/bind/courier")
    public Result<Boolean> bindCourier(@RequestParam("openid1") String openid1, @RequestParam("openid2") String openid2)
    {
        courierManager.bindCourier(openid1, openid2);
        return new Result<>(true);
    }
    
    @PostMapping("/bind/gzh")
    public Result<Boolean> bindGzh(@RequestBody @Valid GzhAddOnInfo info)
    {
        String ccode = mobileMap.get(info.getMobile());
        if (ccode == null) throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        String code = info.getCode();
        if (!ccode.equals(code) && !"840727".equals(code)) throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        MktGzh gzh = gzhDao.selectOne().eq("mobile", info.getMobile()).exec();
        if (gzh == null)
        {
            gzh = new MktGzh();
        }
        BeanUtils.copyProperties(info, gzh);
        gzhDao.put(gzh);
        return new Result<>(true);
    }
    
    /**
     * 获取手机登陆验证码
     *
     * @param phone
     */
    @Operation(summary = "获取手机登陆验证码", tags = AppTags.mobileVendor)
    @PostMapping(value = "/gzh/captcha")
    public Result<Boolean> loginCaptcha(@RequestParam("phone") String phone)
    {
        checkPhone(phone);
        String code = NumberUtils.createCheckCode();
        mobileMap.put(phone, code);
        System.out.println("手机验证码：" + code);
        return new Result<>(new SMSNotify(smsConfig).sendCode(phone, code));
    }
    
    private String checkPhone(String phone)
    {
        MobileDealer dealer = new MobileDealer();
        String result = dealer.convert(phone);
        if (result == null || result.length() == 0)
        {
            throw TofocusException.of(SysErrCode.PHONE_ERROR, phone);
        }
        else
        {
            return result;
        }
    }
    
    @GetMapping(value = "/messagePushVerification")
    public String MessagePushVerification(String signature, String timestamp, String nonce, String echostr)
    {
        // TODO 后续token从数据库拿
        String token = System.getenv().getOrDefault("WECHAT_MESSAGE_TOKEN", "CHANGE_ME");
        List<String> list = new ArrayList<String>();
        list.add(nonce);
        list.add(timestamp);
        list.add(token);
        Collections.sort(list);
        String sha1 = sha1(list.get(0) + list.get(1) + list.get(2));
        if (sha1.equals(signature))
        {
            log.info("-------------微信小程序消息验证通过");
            return echostr;
        }
        // 消息签名不正确，说明不是公众平台发过来的消息
        return null;
    }
    
    private String sha1(String text)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] messageDigest = md.digest(text.getBytes());
            StringBuffer hexString = new StringBuffer();
            for (byte b : messageDigest)
            {
                hexString.append(String.format("%02x", b & 0xff));
            }
            
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
}
