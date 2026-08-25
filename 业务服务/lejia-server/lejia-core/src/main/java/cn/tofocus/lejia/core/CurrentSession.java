package cn.tofocus.lejia.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.lejia.bean.entity.h5.H5User;
import cn.tofocus.lejia.bean.entity.sys.SysUser;
import cn.tofocus.lejia.dao.h5.H5UserDao;
import cn.tofocus.lejia.dao.sys.SysUserDao;

@Component
public class CurrentSession
{
    private static CurrentSession instance;
    
    @Autowired
    private SysUserDao userDao;
    
    @Autowired
    private H5UserDao h5UserDao;
    
    private ThreadLocal<SysUser> sysUser = new ThreadLocal<>();
    
    private ThreadLocal<H5User> h5User = new ThreadLocal<>();
    
    private CurrentSession()
    {
        instance = this;
    }
    
    /**
     * 当前用户
     * 
     * @return
     */
    public SysUser loginUser()
    {
        Long userId = SecurityContextUtil.getAuthenticationContext().getUserkey();
        if (userId != null)
        {
            SysUser user = userDao.get(userId.intValue());
            sysUser.set(user);
            return user;
        }
        return null;
    }
    
    public H5User loginH5User()
    {
        String userid = SecurityContextUtil.getAuthenticationContext().getUserid();
        if (userid != null)
        {
            H5User user = h5UserDao.byUserid(userid);
            h5User.set(user);
            return user;
        }
        return null;
    }
    
    /*********
     * 公司
     ********/
    
    /**
     * 当前公司
     * 
     * @return
     */
    public static String companyPkey()
    {
        if (instance.loginUser() != null) return instance.loginUser().getCompany();
        if (instance.loginUser() == null && instance.loginH5User() != null) return instance.loginH5User().getCompany();
        return null;
    }
    
    /*********
     * 市场
     ********/
    
    /**
     * 当前市场
     * 
     * @return
     */
    public static String marketPkey()
    {
        if (instance.loginUser() != null) return instance.loginUser().getFarmer();
        if (instance.loginUser() == null && instance.loginH5User() != null)
        {
            return instance.loginH5User().getFarmer();
        }
        return null;
    }
    
    public static Integer ascriptionPkey()
    {
        if (instance.loginUser() != null) return instance.loginUser().getAscription();
        if (instance.loginUser() == null && instance.loginH5User() != null)
            return instance.loginH5User().getAscription();
        return null;
    }
    
    public static String moblie()
    {
        if (instance.loginUser() != null) return instance.loginUser().getMobile();
        if (instance.loginUser() == null && instance.loginH5User() != null) return instance.loginH5User().getMobile();
        return null;
    }
    
    public static Integer userPkey()
    {
        if (instance.loginUser() != null) return instance.loginUser().getPkey();
        if (instance.loginUser() == null && instance.loginH5User() != null) return instance.loginH5User().getPkey();
        return null;
    }
    
    public static H5User getH5User()
    {
        if(instance.loginH5User() != null) return instance.loginH5User();
        return null;
    }
}
