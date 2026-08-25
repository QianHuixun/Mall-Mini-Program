package cn.tofocus.lejia.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.json.JsonObject;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.core.MobileSession;

@Configuration
public class CurrentMarketFilter extends OncePerRequestFilter
{
    
    @Autowired
    private MobileSession mobileSession;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException
    {
        String path = ((HttpServletRequest)request).getRequestURI();
        if (path.startsWith("/v1/app/") || path.startsWith("/v2/app/") || path.startsWith("/v3/app/")
            || path.startsWith("/v4/app/") || path.startsWith("/v5/app/"))
        {
            String appid = request.getHeader("ascription");
            if (StringUtil.isBlank(appid))
            {
                JsonObject json = new JsonObject();
                json.put("success", true);
                json.put("code", "998");
                response.getWriter().print(json);
                return;
            }
            System.out.println("-----------------");
            System.out.println("appid:" + appid);
            mobileSession.setAppid(appid);
            String qrCode = request.getHeader("qrCode");
            System.out.println("qrCode:" + qrCode);
            mobileSession.setQrCode(qrCode);
            
            String source = request.getHeader("source");
            System.out.println("setSource-source:" + source);
            //            source = URLDecoder.decode(source, "utf-8");
            mobileSession.setSource(source);
        }
        if (path.startsWith("/v1/wx/"))
        {
            String source = request.getHeader("source");
            // source = URLDecoder.decode(source, "utf-8");
            System.out.println("source:" + source);
            mobileSession.setSource(source);
        }
        // 商城小程序
        if (path.startsWith("/v1/app/market") || path.startsWith("/v2/app/market") || path.startsWith("/v3/app/market")
            || path.startsWith("/v3/app/promote") || path.startsWith("/v4/app/market")
            || path.startsWith("/v5/app/market") || path.startsWith("/v1/app/jd"))
        {
            String openid = request.getHeader("openid");
            String farmer = request.getHeader("farmer");
            System.out.println("-----------------");
            System.out.println("openid:" + openid);
            System.out.println("request.getRemoteAddr():" + request.getRemoteAddr());
            mobileSession.setFarmer(farmer);
            mobileSession.setMember(openid);
            mobileSession.setBillIp(request.getRemoteAddr());
        }
        if (path.startsWith("/v1/zx/pay"))
        {
            String openid = request.getHeader("openid");
            mobileSession.setMember(openid);
            mobileSession.setBillIp(request.getRemoteAddr());
        }
        if (path.startsWith("/v1/app/market/lm/") || path.startsWith("/v2/app/market/lm/") 
            || path.startsWith("/v3/app/market/lm/"))
        {// 排除的url
            System.out.println("+++++++++++++");
//            System.err.println("session:" + MobileSession.member());
//            System.err.println("session:" + JsonUtil.toString(MobileSession.member()));
            if (MobileSession.member() == null)
            {
                JsonObject json = new JsonObject();
                json.put("success", true);
                json.put("code", "999");
                response.getWriter().print(json);
                return;
            }
            System.out.println("memberPkey: " + MobileSession.member().getPkey());
            System.out.println("memberMobile: " + MobileSession.member().getMobile());
        }
        // 商户小程序-商户
        if (path.startsWith("/v1/app/vendor/") || path.startsWith("/v2/app/vendor/")
            || path.startsWith("/v3/app/vendor/") || path.startsWith("/v4/app/vendor/"))
        {
            // 排除的url
            String openid = request.getHeader("openid");
            System.out.println("+++++++++++++vendor+++++++++++++");
            System.out.println("+++++++++++++openid: " + openid);
            if ("[object Null]".equals(openid))
            {
                JsonObject json = new JsonObject();
                json.put("success", true);
                json.put("code", "997");
                response.getWriter().print(json);
                return;
            }
            mobileSession.setVendor(openid);
            if (MobileSession.vendor() == null)
            {
                JsonObject json = new JsonObject();
                json.put("success", true);
                json.put("code", "999");
                response.getWriter().print(json);
                return;
            }
        }
        // 商户小程序-供应商
        if (path.startsWith("/v1/app/supplier/"))
        {
            // 排除的url
            String openid = request.getHeader("openid");
            System.out.println("+++++++++++++supplier+++++++++++++");
            System.out.println("+++++++++++++openid: " + openid);
            if ("[object Null]".equals(openid))
            {
                JsonObject json = new JsonObject();
                json.put("success", true);
                json.put("code", "997");
                response.getWriter().print(json);
                return;
            }
            mobileSession.setSupplier(openid);
            if (MobileSession.supplier() == null)
            {
                JsonObject json = new JsonObject();
                json.put("success", true);
                json.put("code", "999");
                response.getWriter().print(json);
                return;
            }
        }
        // 骑手小程序
        if (path.startsWith("/v1/app/courier/"))
        {// 排除的url
            String openid = request.getHeader("openid");
            mobileSession.setCourier(openid);
            if (MobileSession.courier() == null)
            {
                JsonObject json = new JsonObject();
                json.put("success", true);
                json.put("code", "999");
                response.getWriter().print(json);
                return;
            }
        }
        filterChain.doFilter(request, response);
        mobileSession.removeAll();
    }
    
}
