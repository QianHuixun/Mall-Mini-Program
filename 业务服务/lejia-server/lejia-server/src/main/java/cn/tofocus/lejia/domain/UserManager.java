package cn.tofocus.lejia.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import cn.tofocus.common.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.api.v4.AdminApiV4;
import cn.tofocus.account.api.v4.RoleApiV4;
import cn.tofocus.account.api.v4.UserInDeptApiV4;
import cn.tofocus.account.api.v4.UserInDomainApiV4;
import cn.tofocus.account.api.v4.UserInOrgApiV4;
import cn.tofocus.account.dto.user.SysUserInfo;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.Util;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.domain.user.role.RoleInfo;
import cn.tofocus.lejia.bean.dto.sys.UserDTO;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.entity.sys.SysUser;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysUserDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserManager
{
    
    @Autowired
    private SysUserDao sysUserDao;
    
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    @Autowired
    private CompanyManager companyManager;
    
    @Autowired
    private MarketManager marketManager;
    
    @Autowired
    private AdminApiV4 adminApi;
    
    @Autowired
    private UserInDomainApiV4 userInDomainApiV4;
    
    @Autowired
    private UserInDeptApiV4 userInDeptApiV4;
    
    @Autowired
    private UserInOrgApiV4 userInOrgApiV4;
    
    @Autowired
    private RoleApiV4 roleApiV4;
    
    private static final String DEFAULT_PWD = System.getenv().getOrDefault("DEFAULT_USER_PASSWORD", "CHANGE_ME");
    
    // 创建用户
    public SysUserInfo createOrBindSysUser(String userName, String userMobile, String roleKey)
    {
        if (CurrentSession.companyPkey() == null)
        {
            // 换个错误提醒 CurrentCompany不能为空
            throw TofocusException.of(WsaleErrCode.UNKOWN_COMPANY);
        }
        // 判断手机号重复
        SysUser exist = sysUserDao.findbyPhone(userMobile);
        if (exist != null) throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
        // 新增账号服务用户
        SysUserInfo sysUser = adminApi.addUserByMobile(userName, true, userMobile).fetchResult();
        adminApi.resetPassword(sysUser.getPkey(), DEFAULT_PWD);
        // 绑定账号服务角色
        Integer ascription = CurrentSession.ascriptionPkey();
        if ((Constant.Operation + ascription).equals(CurrentSession.companyPkey()))
            userInDomainApiV4.addUserRoleInDomain(sysUser.getPkey(), roleKey);
        else
            userInDeptApiV4.addUserRole(sysUser.getPkey(), roleKey, CurrentSession.marketPkey());
        // 新增系统用户
        SysUser user = new SysUser();
        user.setPkey(sysUser.getPkey().intValue());
        user.setMobile(userMobile);
        user.setRowVension(0);
        user.setNickname(userName);
        user.setCompany(CurrentSession.companyPkey());
        user.setFarmer(CurrentSession.marketPkey());
        user.setRoleKey(roleKey);
        user.setAscription(ascription);
        SysUser add = sysUserDao.add(user);
        log.info("addUser: {}", add);
        return sysUser;
    }
    
    // 获取用户
    public UserDTO getUserInfo(Integer pkey)
    {
        SysUser sysUser = sysUserDao.get(pkey);
        return BeanUtil.beanFrom(UserDTO.class, sysUser);
    }
    
    // 获取用户列表
    public PageResult<UserDTO> listUser(int page, int pagesize)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        SysAscription sa = sysAscriptionDao.get(ascription);
        PageResult<SysUser> pageResult = sysUserDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("company", CurrentSession.companyPkey())
            .eq("farmer", CurrentSession.marketPkey())
            .notEq("nickname", sa.getAccount())
            .sort("pkey", true)
            .exec();
        PageResult<UserDTO> result = BeanUtil.beanPageFrom(UserDTO.class, pageResult);
        assembleRoleNameList(result);
        return result;
    }
    
    // 修改用户
    @Transactional
    public UserDTO updUser(UserDTO user)
    {
        SysUser employee = sysUserDao.get(user.getPkey());
        if (employee != null)
        {
            if (!Util.equal(employee.getMobile(), user.getMobile())
                || !Util.equal(employee.getNickname(), user.getNickname()))
            {
                Integer pkey = employee.getPkey();
                Boolean result =
                    adminApi.modifyuserinfo(pkey.longValue(), "tf_" + pkey, user.getMobile(), user.getNickname())
                        .fetchResult();
                if (Boolean.TRUE.equals(result))
                {
                    // 更改了名字或者手机号码 如果是公司管理员 或者 市场管理员 同步修改名字电话
                    if (employee.getFarmer() != null)
                    {
                        String marketMobile = marketManager.marketMobile(employee.getFarmer());
                        if (marketMobile != null && Util.equal(employee.getMobile(), marketMobile)) marketManager
                            .updCompanyNameAndMobile(employee.getFarmer(), user.getNickname(), user.getMobile());
                    }
                    else
                    {
                        String companyMobile = companyManager.companyMobile(employee.getCompany());
                        if (Util.equal(employee.getMobile(), companyMobile)) companyManager
                            .updCompanyNameAndMobile(employee.getCompany(), user.getNickname(), user.getMobile());
                    }
                    employee.setMobile(user.getMobile());
                    employee.setNickname(user.getNickname());
                }
            }
            
            if (!Util.equal(employee.getRoleKey(), user.getRoleKey()))
            {
                if (employee.getFarmer() != null)
                {
                    // 解除和原来角色的绑定
                    userInDeptApiV4
                        .delUserRole(Long.valueOf(employee.getPkey()), employee.getRoleKey(), employee.getFarmer());
                    // 绑定和新角色的绑定
                    userInDeptApiV4
                        .addUserRole(Long.valueOf(employee.getPkey()), user.getRoleKey(), employee.getFarmer());
                }
                else
                {
                    // 解除和原来角色的绑定
                    userInOrgApiV4
                        .delUserRole(Long.valueOf(employee.getPkey()), employee.getRoleKey(), employee.getCompany());
                    // 绑定和新角色的绑定
                    userInOrgApiV4
                        .addUserRole(Long.valueOf(employee.getPkey()), user.getRoleKey(), employee.getCompany());
                }
                employee.setRoleKey(user.getRoleKey());
            }
            
            employee = sysUserDao.update(employee);
            return BeanUtil.beanFrom(UserDTO.class, employee);
        }
        else
        {
            throw TofocusException.of(SysErrCode.Cache.OLD_VALUE_IS_NOT_EXIST);
        }
    }
    
    @Transactional
    public Boolean delUser(int pkey)
    {
        boolean result = false;
        SysUser employee = sysUserDao.get(pkey);
        if (employee != null)
        {
            // 解绑账号服务角色
            userInDomainApiV4.clearUserRoleInstance(employee.getPkey().longValue());
            // 删除帐号服务用户
            adminApi.delUser(employee.getPkey().longValue());
            // 删除员工
            result = sysUserDao.removeById(pkey);
        }
        log.info("result: {}", result);
        return result;
    }
    
    // 新增用户 (仅 市场和公司新增时 使用)
    public SysUserInfo insUser(SysUser entity)
    {
        String mobile = entity.getMobile();
        SysUser user = sysUserDao.selectOne().eq("mobile", mobile).exec();
        if (user != null) throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
        Result<SysUserInfo> sysUserResult = adminApi.addUserByMobile(entity.getNickname(), true, mobile);
        SysUserInfo manager = null;
        if (sysUserResult.isSuccess())
        {
            manager = sysUserResult.getResult();
            adminApi.resetPassword(manager.getPkey(), DEFAULT_PWD);
            entity.setPkey(manager.getPkey().intValue());
            if (StringUtil.isNotBlank(entity.getRoleKey()))
            {
                if (entity.getFarmer() != null)
                    userInDeptApiV4.addUserRole(manager.getPkey(), entity.getRoleKey(), entity.getFarmer());
                else
                    userInOrgApiV4.addUserRole(manager.getPkey(), entity.getRoleKey(), entity.getCompany());
            }
        }
        else
        {
            throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
        }
        sysUserDao.add(entity);
        return manager;
    }
    
    // 组装角色名字
    private void assembleRoleNameList(PageResult<UserDTO> result)
    {
        List<UserDTO> content = result.getContent();
        if (CollectionUtil.isEmpty(content)) return;
        List<Long> pkeyList = new ArrayList<>();
        for (UserDTO dto : content)
        {
            pkeyList.add(Long.valueOf(dto.getPkey()));
        }
        Map<Long, List<RoleInfo>> map = userInDomainApiV4.mapUserRole(pkeyList).fetchResult();
        if (map == null) return;
        for (UserDTO dto : content)
        {
            if (map.containsKey(Long.valueOf(dto.getPkey())))
            {
                List<RoleInfo> list = map.get(Long.valueOf(dto.getPkey()));
                if (!list.isEmpty())
                {
                    dto.setRoleKeyName(list.get(0).getName());
                }
            }
        }
    }
    
}
