package cn.tofocus.domain.manager;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import cn.tofocus.account.server.CaptchaManager;
import cn.tofocus.authentication.config.TfPasswordEncoder;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.Util;
import cn.tofocus.core.captcha.CaptchaChecker;
import cn.tofocus.core.enums.CaptchaPurpose;
import cn.tofocus.core.enums.CaptchaRouter;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.account.db.cache.user.UserCache;
import cn.tofocus.account.db.dao.user.UserDao;
import cn.tofocus.account.db.entity.user.UserEntity;

/**
 * 
 * 用户管理
 * <功能详细描述>
 * 
 * @author  wyw
 * @version  [版本号, 2018年8月17日]
 */
@Component(value = "doman.userManager")
public class UserManager
{
    private final String defaultpwd = "Zyy6666";
    
    @Autowired
    private CaptchaManager captchaManager;
    
    //用户信息
    @Autowired
    private UserCache users;
    
    @Autowired
    private UserDao userDao;
    
    //用户权限配置
    @Autowired
    private UserPermissionManager userPermissionManager;
    
    @Value("${forgotpassword.url}")
    private String forgotpasswordUrl;
    
    @Autowired
    private TfPasswordEncoder passwordEncoder;
    
    /**************************
     * 
     *    常用方法
     * 
     **************************/
    
    /**
     * 获取用户的绑定手机
     * <功能详细描述>
     * @param tofocusid
     * @return
     */
    public String getBindPhone(Long userkey)
    {
        UserEntity user = users.get(userkey);
        if (user == null)
        {
            throw TofocusException.of(SysErrCode.Auth.UNKNOW_USER);
        }
        else
        {
            return user.getBindPhone();
        }
    }
    
    /**
     * 检查验证码
     * <p/>
     * <功能详细描述>
     * @param captchaRouter
     * @param captchaPurpose
     * @param phone
     * @param captcha
     */
    private void checkCaptcha(CaptchaRouter captchaRouter, CaptchaPurpose captchaPurpose, String phone, String captcha)
    {
        CaptchaChecker.checkErr(captchaManager.checkCaptcha(captchaRouter, captchaPurpose, phone, captcha));
    }
    
    /**************************
     * 
     *    用户管理
     *    （系统管理员可操作所有用户，机构管理员可操作所有本机构用户，部门管理员可操作本部门所有用户。
     *    因此，首先判断是否系统管理员，否则去OrginazationManager询问是否有操作权限
     *    机构管理员新增的用户，自动成为该机构职员）
     *    
     *        激活用户（可选有效期）
     *        停用用户
     *        管理员直接增加用户
     *        管理员删除用户
     *        管理员重置用户密码
     *        管理员修改用户信息
     *        管理员修改用户绑定的手机（短信通知变更）
     *        管理员修改用户绑定的邮箱（邮箱通知变更）
     *        查询用户列表（有管理权限可查询）
     * 
     **************************/
    
    public UserEntity addUserByMobile(String domainid, String name, boolean actived, String mobile)
    {
        UserEntity user = userDao.getUserbyPhone(domainid, mobile);
        if (user != null)
        {
            if (!name.equals(user.getNickname()) || user.isActived() != actived)
            {
                user.setActived(actived);
                user.setNickname(name);
                users.update(user);
            }
        }
        else
        {
            user = addUser(domainid, name, actived, null, mobile);
        }
        return user;
    }
    
    public UserEntity addUserByUserId(String domainid, String name, boolean actived, String userid, String mobile)
    {
        if (mobile != null && userDao.isPhoneUsed(domainid, userid, mobile))
        {
            throw TofocusException.of(SysErrCode.Auth.PHONE_USED);
        }
        UserEntity user = userDao.getUserByUserid(userid);
        if (user != null)
        {
            if (!domainid.equals(user.getRegistFromDomain()))
                throw TofocusException.of(SysErrCode.ACCESS_DENIED,
                    "userid 已存在并且属于[" + user.getRegistFromDomain() + "]域");
            if (!name.equals(user.getNickname()) || user.isActived() != actived
                || !Objects.equals(mobile, user.getBindPhone()))
            {
                user.setActived(actived);
                user.setNickname(name);
                user.setBindPhone(mobile);
                users.update(user);
            }
        }
        else
        {
            user = addUser(domainid, name, actived, userid, mobile);
        }
        return user;
    }
    
    /**
     * 管理员直接增加用户
     * <p/>
     * 自动生成 tofocusid
     * <br/>
     * 初始密码 123456
     * @param user
     */
    private UserEntity addUser(String domainid, String name, boolean actived, String userid, String mobile)
    {
        UserEntity user = new UserEntity();
        user.setUserid(userid);
        user.setNickname(name);
        user.setBindPhone(mobile);
        user.setRegistFromDomain(domainid);
        user.setActived(actived);
        user.setRegisttime(new Date());
        user.setPassword(passwordEncoder.encode(defaultpwd));
        UserEntity r = users.add(user);
        return r;
    }
    
    /**
     * 管理员删除用户
     * <功能详细描述>
     * @param tofocusid
     */
    public void delUser(String domain, Long userkey)
    {
        UserEntity user = users.getForUpdate(domain, userkey);
        if (user != null)
        {
            userPermissionManager.removeByUserkey(userkey);
            users.removeById(userkey);
        }
        else
            throw TofocusException.of(SysErrCode.Auth.UNKNOW_USER);
    }
    
    /**
     * 修改用户基本信息
     */
    public void modifyUserinfo(String domain, Long userkey, String userid, String name, String mobile)
    {
        UserEntity user = users.getForUpdate(domain, userkey);
        if (user != null)
        {
            if (!userid.equals(user.getUserid()))
            {
                if (userDao.getUserByUserid(userid) != null)
                    throw TofocusException.of(SysErrCode.Auth.USERID_USED);
                else
                    user.setUserid(userid);
            }
            if (mobile != null && userDao.isPhoneUsed(domain, user.getPkey(), mobile))
            {
                throw TofocusException.of(SysErrCode.Auth.PHONE_USED);
            }
            else
                user.setBindPhone(mobile);
            user.setNickname(name);
            users.update(user);
        }
        else
        {
            throw TofocusException.of(SysErrCode.Auth.UNKNOW_USER);
        }
    }
    
    public void enableUser(String domain, Long userkey, boolean actived)
    {
        UserEntity user = users.getForUpdate(domain, userkey);
        if (user != null)
        {
            user.setActived(actived);
            users.update(user);
        }
        else
            throw TofocusException.of(SysErrCode.Auth.UNKNOW_USER);
    }

    public void resetPassword(String domain, Long userkey, String pwd)
    {
        UserEntity user = users.getForUpdate(domain, userkey);
        if (user != null)
        {
            user.setPassword(passwordEncoder.encode(pwd));
            users.update(user);
        }
        else
            throw TofocusException.of(SysErrCode.Auth.UNKNOW_USER);
    }
    
    public UserEntity getUser(Long userkey)
    {
        return users.get(userkey);
    }
    
    /**************************
     * 
     *    用户自主操作
     *    （新注册的用户等同于消费者）
     *    
     *        用户通过手机注册
     *        用户重置自己的密码
     *        重新绑定手机
     *        重新绑定邮箱
     *        修改密码
     *        修改用户基本信息
     * 
     **************************/
    
    /**
     * 获取手机登陆的验证码
     * @param checkPhone
     */
    public void loginCaptcha(String phone)
    {
        String captcha = captchaManager.createCaptcha(CaptchaRouter.phone, CaptchaPurpose.login, phone);
    }
    
    //-----------------------------------------
    
    /**
     * 重置密码step1：获取验证码
     * <功能详细描述>
     * @param phone 手机号码
     */
    public void prepareResetPassword(String phone)
    {
        String captcha = captchaManager.createCaptcha(CaptchaRouter.phone, CaptchaPurpose.resetPassword, phone);
    }
    
    /**
     * 重置密码step2：重置自己的密码
     * <功能详细描述>
     * @param phone 手机号码
     * @param captcha 验证码
     * @return 新密码
     */
    public String resetPassword(String phone, String captcha, String pwd)
    {
        checkCaptcha(CaptchaRouter.phone, CaptchaPurpose.resetPassword, phone, captcha);
        List<UserEntity> userlist = users.getByIndex(phone);
        if (userlist != null && !userlist.isEmpty())
        {
            UserEntity user = userlist.get(0);
            if (pwd == null || pwd.length() == 0)
                pwd = Util.getUUID().substring(0, 10);
            user.setPassword(passwordEncoder.encode(pwd));
            users.update(user);
            return pwd;
        }
        else
        {
            throw TofocusException.of(SysErrCode.Auth.UNKNOW_USER);
        }
    }
    
    //-----------------------------------------
    
    /**
     * 重新绑定手机step1：获取验证码
     * <功能详细描述>
     */
    public void prepareRebindPhone1()
    {
        String oldphonenumber = getBindPhone(SecurityContextUtil.getAuthenticationContext().getUserkey());
        if (oldphonenumber != null)
        {
            String captcha =
                captchaManager.createCaptcha(CaptchaRouter.phone, CaptchaPurpose.bindPhone, oldphonenumber);
        }
    }
    
    /**
     * 重新绑定手机step2:验证旧手机验证码，确认是本人操作
     * <功能详细描述>
     * @param captcha 验证码
     * @return 重新绑定手机的操作码，用于进入step4
     */
    public String prepareRebindPhone2(String captcha)
    {
        String oldphonenumber = getBindPhone(SecurityContextUtil.getAuthenticationContext().getUserkey());
        if (oldphonenumber != null)
        {
            checkCaptcha(CaptchaRouter.phone, CaptchaPurpose.bindPhone, oldphonenumber, captcha);
            //重新生成一个随机操作码
            String code = captchaManager.createCaptcha(CaptchaRouter.system, CaptchaPurpose.bindPhone, oldphonenumber);
            return code;
        }
        return null;
    }
    
    /**
     * 重新绑定手机step3：向新手机发生验证码
     * <功能详细描述>
     * @param phonenumber 新手机号码
     */
    public void prepareRebindPhone3(String phonenumber)
    {
        if (StringUtil.isEmpty(phonenumber))
        {
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL);
        }
        else
        {
            //新手机发送验证码
            captchaManager.createCaptcha(CaptchaRouter.phone, CaptchaPurpose.bindPhone, phonenumber);
        }
    }
    
    /**
     * 重新绑定手机step4：绑定新号码
     * <功能详细描述>
     * @param phonenumber
     * @param captcha
     * @param code
     */
    public void rebindPhone(String phonenumber, String captcha, String code)
    {
        String oldphonenumber = getBindPhone(SecurityContextUtil.getAuthenticationContext().getUserkey());
        if (StringUtil.isEmpty(phonenumber) || StringUtil.isEmpty(captcha))
        {
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL);
        }
        else
        {
            //检查随机操作码
            if (oldphonenumber != null)
                checkCaptcha(CaptchaRouter.system, CaptchaPurpose.bindPhone, oldphonenumber, code);
            //检查手机验证码
            checkCaptcha(CaptchaRouter.phone, CaptchaPurpose.bindPhone, phonenumber, captcha);
            
            Set<Long> ids = users.getIdsByIndex(phonenumber);
            if (ids != null && !ids.isEmpty())
                throw TofocusException.of(SysErrCode.Auth.PHONE_USED);
            
            Long userkey = SecurityContextUtil.getAuthenticationContext().getUserkey();
            UserEntity user = users.get(userkey);
            if (user == null)
            {
                throw TofocusException.of(SysErrCode.Auth.UNKNOW_USER);
            }
            else
            {
                user.setBindPhone(phonenumber);
                users.update(user);
            }
        }
    }
    
    //-----------------------------------------
    /**
     * 修改密码
     * <功能详细描述>
     * @param oldpassword
     * @param newpassword
     */
    public void modifyPassword(String oldpassword, String newpassword)
    {
        Long userkey = SecurityContextUtil.getAuthenticationContext().getUserkey();
        UserEntity user = users.get(userkey);
        if (user == null)
        {
            throw TofocusException.of(SysErrCode.Auth.UNKNOW_USER);
        }
        else
        {
            if (passwordEncoder.matches(oldpassword, user.getPassword()))
            {
                user.setPassword(passwordEncoder.encode(newpassword));
                users.update(user);
            }
            else
            {
                throw TofocusException.of(SysErrCode.Auth.CHECK_PASSWORD_FAIL, "用户名或密码错误");
            }
        }
    }
    
}
