package cn.tofocus.lejia.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.HttpUtil;

public class DingTalkWarning
{
    private static final String DING_TALK_URL =
        "https://oapi.dingtalk.com/robot/send?access_token=0be33e2c0b5ff7e0c00ae7d17d9923c335a95b37d9df294cda0a45bf73d5113d";
    
    private static final String DING_TALK_SECRET =
        "SEC693695a4e248845f030b3af9f28a885fac9f9a35c43d6a075ac526c3667ef021";

    /**
     * 发送钉钉预警通知
     * @param content 通知内容
     * @param project 项目，可为null
     * @param profilesActive 项目配置运行环境列表，可为null
     */
    public static void sendMsg(String content, String project, List<String> profilesActive)
    {
        sendMsg(content, project, extractActive(profilesActive));
    }

    /**
     * 发送钉钉预警通知
     * @param content 通知内容
     * @param project 项目，可为null
     * @param active 运行环境，可为null
     */
    public static void sendMsg(String content, String project, String active)
    {
        try
        {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + DING_TALK_SECRET;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(DING_TALK_SECRET.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            JSONObject json = new JSONObject();
            json.put("msgtype", "text");
            
            JSONObject atJson = new JSONObject();
            atJson.put("isAtAll", true);
            json.put("at", atJson);
            
            JSONObject textJson = new JSONObject();
            StringBuilder sb = new StringBuilder("【对账异常通知】\n");
            if (project != null) sb.append("【项目】：\n").append(project).append("\n");
            if (active != null) sb.append("【运行环境】：\n").append(active).append("\n");
            sb.append("【异常详情】：\n");
            textJson.put("content", sb.append(content).append("\n").toString());
            json.put("text", textJson);
            
            String urlPath = DING_TALK_URL + "&timestamp=" + timestamp + "&sign=" + sign;
            
            HttpUtil.forString(urlPath).body(json).post().exec();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static String extractActive(List<String> profilesActive)
    {
        if (CollectionUtil.isEmpty(profilesActive)) return "";
        return profilesActive.stream()
            .filter(a -> !"common".equals(a) && !"running".equals(a))
            .collect(Collectors.toList())
            .toString();
    }
    
    public static void main(String[] args)
    {
        sendMsg("天津测试1", "天津测试2", "release");
    }
}