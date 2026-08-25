package cn.tofocus.lejia.util;

import java.util.Date;


import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.common.util.security.MD5;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import lombok.extern.slf4j.Slf4j;

/**
 * <通通锁工具>
 * <功能详细描述>
 * 
 * @author  lyl
 * @version  [版本号, 2024-7-17]
 */
@Slf4j
public class TongTongSuoUtil
{
    private static final String CLIENT_ID = System.getenv().getOrDefault("TTLOCK_CLIENT_ID", "CHANGE_ME");
    
    private static final String CLIENT_SECRET = System.getenv().getOrDefault("TTLOCK_CLIENT_SECRET", "CHANGE_ME");
    
    private static final String USER_NAME = System.getenv().getOrDefault("TTLOCK_USERNAME", "CHANGE_ME");
    
    private static final String PASSWORD = MD5.getMD5(System.getenv().getOrDefault("TTLOCK_PASSWORD", "CHANGE_ME")).toLowerCase();
    
    /**
     * 获取token和刷新token
     */
    private static final String TOKEN_URL = "https://cnapi.sciener.com/oauth2/token";
    
    /**
     * 获取用户添加的锁
     */
    private static final String LOCK_LIST_URL = "https://cnapi.sciener.com/v3/lock/list";
    
    /**
     * 修改锁名称
     */
    private static final String LOCK_RENAME_URL = "https://cnapi.sciener.com/v3/lock/rename";
    
    /**
     * 获取随机密码
     */
    private static final String GET_PASSWORD_URL = "https://cnapi.sciener.com/v3/keyboardPwd/get";
    
    /**
     * 修改密码
     */
    private static final String UPD_KEYBOARDPWD_URL = "https://cnapi.sciener.com/v3/keyboardPwd/change";
    
    /**
     * 获取锁密码列表
     */
    private static final String KEYBOARDPWD_LIST_URL = "https://cnapi.sciener.com/v3/lock/listKeyboardPwd";
    
    //token
    private static String accessToken;
    
    //刷新token
    private static String refreshToken;
    
    public static String getAccessToken()
    {
        return accessToken;
    }
    
    public static String getRefreshToken()
    {
        return refreshToken;
    }
    
    //    "access_token": "2021aaa2843588ed866d8e0a528361b6",
    //    "refresh_token": "4150d76cfa2e0f22f44fa2dccd93184d",
    //    "uid": 26260689,
    //    "openid": 1437719445,
    //    "scope": "user,key,room",
    //    "token_type": "Bearer",
    //    "expires_in": 7764495
    /** <获取token>
     * <默认有效期90天>
     */
    public static String token()
    {
        if (getAccessToken() != null)
            return null;
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("clientId", CLIENT_ID);
        param.add("clientSecret", CLIENT_SECRET);
        param.add("username", USER_NAME);
        param.add("password", PASSWORD);
        String resultStr = HttpUtil.postForString(TOKEN_URL, param);
        JSONObject resultJson = JSONObject.parseObject(resultStr);
        //判断接口是否成功
        error(resultJson);
        accessToken = resultJson.getString("access_token");
        refreshToken = resultJson.getString("refresh_token");
        log.info("通通锁获取token：{}", resultJson);
        return resultStr;
    }
    
    /** <获取刷新tolen>
     * <默认有效期10年>
     */
    public static String refreshToken()
    {
        if (getRefreshToken() == null)
            token();
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("clientId", CLIENT_ID);
        param.add("clientSecret", CLIENT_SECRET);
        param.add("grant_type", "refresh_token");
        param.add("refresh_token", refreshToken);
        String resultStr = send(TOKEN_URL, param);
        JSONObject resultJson = JSONObject.parseObject(resultStr);
        refreshToken = resultJson.getString("refresh_token");
        log.info("通通锁重新获取token：{}", resultStr);
        return resultStr;
    }
    
    /** <获取账户下锁列表>
     * <获取账户绑定的全部锁分页查询>
     * @param param 参数：
     *         lockAlias  String  锁别名模糊匹配 
     *         groupId    int     分组ID 
     *         pageNo     int     页码，从1开始
     * @return
     */
    public static String lockList(MultiValueMap<String, Object> param)
    {
        token();
        if (param == null)
            param = new LinkedMultiValueMap<>();
        param.add("clientId", CLIENT_ID);
        param.add("accessToken", accessToken);
        param.add("grant_type", "refresh_token");
        if (!param.containsKey("pageNo"))
            param.add("pageNo", 1);
        param.add("pageSize", 1000);
        param.add("date", System.currentTimeMillis());
        String resultStr = send(LOCK_LIST_URL, param);
        log.info("通通锁获取全部锁：{}", resultStr);
        return resultStr;
    }
    
    /** <锁重命名>
     * <修改锁名称>
     * @param lockId    锁ID
     * @param lockAlias 锁别名
     * @return
     */
    public static String lockRename(int lockId, String lockAlias)
    {
        token();
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("clientId", CLIENT_ID);
        param.add("accessToken", accessToken);
        param.add("lockId", lockId);
        param.add("lockAlias", lockAlias);
        param.add("date", System.currentTimeMillis());
        String resultStr = send(LOCK_RENAME_URL, param);
        log.info("通通锁锁重命名：{}", resultStr);
        return resultStr;
    }
    
    /** <获取限期密码>
     * <开始结束时间内有限的密码，精确到小时，获取时锁可以离线>
     * @param lockId    锁ID
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    public static String timeLimitPwd(int lockId, String keyboardPwdName, Date startDate, Date endDate)
    {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("lockId", lockId);
        param.add("keyboardPwdType", 3);
        param.add("keyboardPwdName", keyboardPwdName);
        param.add("startDate", startDate.getTime());
        param.add("endDate", endDate.getTime());
        String resultStr = getKeyboardPwd(param);
        log.info("通通锁获取限期密码：{}", resultStr);
        JSONObject po = JSONObject.parseObject(resultStr);
        return po.getString("keyboardPwd");
    }
    
    /** <修改密码>
     * <锁必须在线>
     * @param lockId
     * @param keyboardPwdId     密码id
     * @param keyboardPwdName   密码名称
     * @param newKeyboardPwd    新密码
     * @return
     */
    public static String updPwd(int lockId, int keyboardPwdId, String keyboardPwdName, String newKeyboardPwd)
    {
        token();
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("clientId", CLIENT_ID);
        param.add("accessToken", accessToken);
        param.add("lockId", lockId);
        param.add("keyboardPwdId", keyboardPwdId);
        param.add("keyboardPwdName", keyboardPwdName);
        param.add("newKeyboardPwd", newKeyboardPwd);
        param.add("changeType", 2);
        param.add("date", System.currentTimeMillis());
        String resultStr = send(UPD_KEYBOARDPWD_URL, param);
        log.info("通通锁修改锁密码：{}", resultStr);
        return resultStr;
    }
    
    public static String pwdList(int lockId)
    {
        token();
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("clientId", CLIENT_ID);
        param.add("accessToken", accessToken);
        param.add("lockId", lockId);
        param.add("pageNo", 1);
        param.add("pageSize", 200);
        param.add("orderBy", 1);
        param.add("date", System.currentTimeMillis());
        String resultStr = send(KEYBOARDPWD_LIST_URL, param);
        log.info("通通锁修改锁密码：{}", resultStr);
        return resultStr;
    }
    
    private static String getKeyboardPwd(MultiValueMap<String, Object> param)
    {
        token();
        param.add("clientId", CLIENT_ID);
        param.add("accessToken", accessToken);
        param.add("date", System.currentTimeMillis());
        String resultStr = send(GET_PASSWORD_URL, param);
        return resultStr;
    }
    
    private static String send(String url, MultiValueMap<String, Object> param)
    {
        //检查token
        token();
        //发起请求
        String resultStr = HttpUtil.postForString(url, param);
        //响应解析
        JSONObject resultJson = JSONObject.parseObject(resultStr);
        //判断接口是否成功
        error(resultJson);
        log.info("通通锁请求接口：{}，响应内容：{}", url, resultStr);
        return resultStr;
    }
    
    private static void error(JSONObject json)
    {
        if (json.containsKey("errcode"))
        {
            //错误码
            Integer errcode = json.getInteger("errcode");
            switch (errcode)
            {
                case 0:
                    break;
                //token过期
                case 10004:
                    accessToken = null;
                    token();
                    break;
                default:
                    throw TofocusException.of(SysErrCode.PRC_EXCEPTION, json.toString());
            }
        }
    }
    
    public static void main(String[] args)
    {
        String lockList = lockList(null);
        System.out.println("lockList: " + lockList);
//        Date startDate = DateUtil.formatDateStr("20240817 10:30:00", "yyyy-MM-dd HH:mm:ss");
//        Date endDate = DateUtil.formatDateStr("20240817 15:00:00", "yyyy-MM-dd HH:mm:ss");
        //        pwdList(16304453);
//        String timeLimitPwd = timeLimitPwd(16565507, "911006227911024", startDate, endDate);
        //        updPwd(16304453, 534630229, "修改", "123456");
//        System.out.println(timeLimitPwd);
    }
}
