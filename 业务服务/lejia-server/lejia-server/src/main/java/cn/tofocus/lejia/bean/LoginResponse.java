package cn.tofocus.lejia.bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.core.data.NamedBean;
import lombok.Data;

@Data
public class LoginResponse 
{
	 /*********
     * 用户
     ********/
    private Long userkey;
    
    private String userid;
    
    private String nickname;
    
    private String bindPhone;
    
    private NamedBean currentDomain;
    
    private NamedBean currentApp;

    private NamedBean lastAccessOrg;
    
    private NamedBean lastAccessDept;

    private List<NamedBean> orgs = new ArrayList<>();

//    private List<MarketSimpleInfo> depts = new ArrayList<>();
    
    private List<AppMenu> menus = new ArrayList<>();

    /*********
     * 登录
     ********/
    private OAuth2AccessToken accessToken;
}
