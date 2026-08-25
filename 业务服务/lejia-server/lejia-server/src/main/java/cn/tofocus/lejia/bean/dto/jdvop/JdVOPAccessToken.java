package cn.tofocus.lejia.bean.dto.jdvop;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class JdVOPAccessToken
{
    @JSONField(name = "access_token")
    private String accessToken;
    
    @JSONField(name = "refresh_token")
    private String refreshToken;
    
    private String uid;
    
    private String xid;
    
    private int code;
    
    @JSONField(name = "open_id")
    private String openId;
    
    private String scope;
    
    private long time;
    
    @JSONField(name = "token_type")
    private String tokenType;
    
    @JSONField(name = "expires_in")
    private long expiresIn;
}
