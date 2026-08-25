package cn.tofocus.authentication.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.tofocus.core.Result;

@FrameworkEndpoint
public class RevokeTokenEndpoint
{
    @Autowired
    @Qualifier("consumerTokenServices")
    @Lazy
    ConsumerTokenServices consumerTokenServices;
    
    @PostMapping("/removeToken")
    @ResponseBody
    public Result<String> revokeToken(@RequestHeader(name = "Authorization", required =false) String token,
        @RequestParam(name = "access_token", required = false) String access_token)
    {
        String t = null;
        if (token != null && token.length() > 7)
        {
            t = token.substring(7);
        }
        if (access_token != null)
        {
            t = access_token;
        }
        if (t != null && consumerTokenServices.revokeToken(t))
        {
            return new Result<>("注销成功");
        }
        else
        {
            return new Result<>("注销失败");
        }
    }
}
