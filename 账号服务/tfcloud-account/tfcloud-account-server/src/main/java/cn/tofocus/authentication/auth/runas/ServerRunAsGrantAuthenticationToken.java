package cn.tofocus.authentication.auth.runas;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.Getter;

import java.util.Collections;

/**
 * 自定义ServerRunAs登录Token类
 *
 * @author vains
 */
@Getter
public class ServerRunAsGrantAuthenticationToken extends AbstractAuthenticationToken
{
    private String principal;
    
    private String pwd;
    
    private String host;
    
    private String clientId;
    
    private String codeTarget;
    
    private String code;
    
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;
    
    public ServerRunAsGrantAuthenticationToken(String principal, String pwd, String host, String clientId,
        String codeTarget, String code)
    {
        super(Collections.emptyList());
        this.principal = principal;
        this.pwd = pwd;
        this.host = host;
        this.clientId = clientId;
        this.codeTarget = codeTarget;
        this.code = code;
    }
    
    @Override
    public Object getCredentials()
    {
        return null;
    }
    
}
