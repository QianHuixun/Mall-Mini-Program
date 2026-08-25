package cn.tofocus.lejia.app.v1.sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.syb.lib.SybPayService;
import lombok.extern.slf4j.Slf4j;
//import net.sf.json.JSONObject;

@Slf4j
@RequestMapping("/v1/syb/pay")
@RestController
public class SybPayApiImpl
{
    @Autowired
    private SybPayService sps;
    
    @PostMapping("/test")
    public Result<Map<String, String>> test()
    {
//        String res = "";
        Map<String,String> res = new HashMap<>();
        try
        {
            Map<String, String> map = sps.xtPay();
            String payinfo = map.get("payinfo");
            
            JSONObject jo = JSON.parseObject(payinfo);
//            JSONObject jo = JSONObject.fromObject(payinfo);
            res.put("paypath", jo.getString("paypath"));
            res.put("appid", jo.getString("appid"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Result<>(res);
    }
    
    @PostMapping("/reurl")
    public void reurl(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        BufferedReader reader = null;
        reader = request.getReader();
        String line = "";
        StringBuffer inputString = new StringBuffer();
        while ((line = reader.readLine()) != null)
        {
            inputString.append(line);
        }
        System.out.println("inputString.toString():" + inputString.toString());
    }
    
//    public static void main(String[] args)
//    {
//        SybPayService s = new SybPayService();
//        try
//        {
//            Map<String, String> map = s.xtPay();
//            String payinfo = map.get("payinfo");
//            JSONObject jo = JSON.parseObject(payinfo);
//            System.out.println("jo: " + jo);
//        }
//        catch (Exception e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
    
}
