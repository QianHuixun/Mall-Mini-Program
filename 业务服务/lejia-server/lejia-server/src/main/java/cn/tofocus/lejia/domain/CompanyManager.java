package cn.tofocus.lejia.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.api.v4.AdminApiV4;
import cn.tofocus.account.api.v4.UserInOrgApiV4;
//import cn.tofocus.account.api.v2.org.OrginazationApi;
//import cn.tofocus.account.api.v2.user.AdminApi;
import cn.tofocus.account.dto.user.SysUserInfo;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.Util;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
//import cn.tofocus.domain.org.Orginazation.OrgType;
import cn.tofocus.lejia.bean.dto.market.SysFarmerOnList;
import cn.tofocus.lejia.bean.dto.sys.SysCompanyOnList;
import cn.tofocus.lejia.bean.entity.sys.SysCompany;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysUser;
import cn.tofocus.lejia.Constant.Role;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.SysCompanyDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysUserDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CompanyManager
{
    @Autowired
    private UserInOrgApiV4 userInOrgApiV4;
    
    @Autowired
    private SysCompanyDao companyDao;

    @Autowired
    private AdminApiV4 adminApi;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysUserDao sysUserDao;
    
    @Transactional
    public SysCompanyOnList insCompany(SysCompanyOnList company)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        // 初始化公司主键
        SysCompany companyEntity = new SysCompany();
        companyDao.generateID(companyEntity);
        String companyPkey = companyEntity.getPkey();
        // 准备负责人账号
        SysUserInfo manager = null;
        if (company.getManager() != null || company.getMobile() != null)
        {
            SysUser sysUser = new SysUser();
            sysUser.setCompany(companyPkey);
            sysUser.setMobile(company.getMobile());
            sysUser.setNickname(company.getManager());
            sysUser.setAscription(ascription);
            sysUser.setRowVension(1);
            manager = userManager.insUser(sysUser);
        }
        
        // 创建机构
        adminApi.saveOrginazation(companyPkey, company.getName());
        //保存公司
        companyEntity.setAscription(ascription);
        convertCompanyEntity(companyEntity, company, manager);
        SysCompany result = companyDao.add(companyEntity);
        //给负责人账号增加公司负责人角色
        if (manager != null)
        {
            log.info("Long: " + manager.getPkey());
            userInOrgApiV4.addUserRole(manager.getPkey(), Role.MARKET_HEAD, companyPkey);
        }
        company.setPkey(result.getPkey());
        return company;
    }
    
    // 对company类进行转换 
    private void convertCompanyEntity(SysCompany companyEntity, SysCompanyOnList company, SysUserInfo manager)
    {
        companyEntity.setAddr(company.getAddr());
        companyEntity.setEnabled(true);
        companyEntity.setName(company.getName());
        companyEntity.setMobile(company.getMobile());
        companyEntity.setManager(company.getManager());
        if (manager != null) companyEntity.setManagerUser(manager.getPkey());
        companyEntity.setIdDel(false);
        companyEntity.setRowVension(1);
        
    }
    
    public SysCompanyOnList getCompany(String pkey)
    {
        if (StringUtil.isEmpty(pkey)) return null;
        SysCompany company = companyDao.getCompany(pkey);
        if (company == null || company.getIdDel()) return null;
        SysCompanyOnList result = BeanUtil.beanFrom(SysCompanyOnList.class, company);
        assembleCompany(Arrays.asList(result));
        return result;
    }
    
    public PageResult<SysCompanyOnList> queryCompany(int page, int pagesize, String companyName)
    {
        PageResult<SysCompany> pageResult =
            companyDao.queryCompany(page, pagesize, companyName, CurrentSession.ascriptionPkey());
        PageResult<SysCompanyOnList> result = BeanUtil.beanPageFrom(SysCompanyOnList.class, pageResult);
        assembleCompany(result.getContent());
        return result;
    }
    
    // 公司id传进来 获取下属的市场 拼装
    private void assembleCompany(List<SysCompanyOnList> companyList)
    {
        for (SysCompanyOnList cBean : companyList)
        {
            // org字段记录的sys_company的pkey
            List<SysFarmer> exec = sysFarmerDao.select().eq("org", cBean.getPkey()).eq("idDel", false).exec();
            cBean.setMarkets(BeanUtil.beanListFrom(SysFarmerOnList.class, exec));
        }
    }
    
    // 只允许修改地址 和名称
    @Transactional
    public SysCompanyOnList updCompany(String pkey, String name, String addr)
    {
        //查原记录
        SysCompany old = companyDao.getCompany(pkey);
        if (old == null)
            throw TofocusException.of(WsaleErrCode.UNKOWN_COMPANY);
        else
        {
            //名称变更
            if (!Util.equal(old.getName(), name))
            {
                adminApi.saveOrginazation(pkey, name);
                old.setName(name);
            }
            if (StringUtils.isNotBlank(addr)) old.setAddr(addr);
            //保存公司
            SysCompany sysCompany = companyDao.update(old);
            return BeanUtil.beanFrom(SysCompanyOnList.class, sysCompany);
        }
    }
    
    public String companyMobile(String pkey)
    {
        SysCompany company = companyDao.get(pkey);
        return company.getMobile();
    }
    
    public void updCompanyNameAndMobile(String pkey, String name, String mobile)
    {
        SysCompany sysCompany = companyDao.getCompany(pkey);
        if (sysCompany == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        if (StringUtils.isNotBlank(name)) sysCompany.setManager(name);
        if (StringUtils.isNotBlank(mobile)) sysCompany.setMobile(mobile);
        companyDao.update(sysCompany);
    }
    
    public Boolean delCompany(String pkey)
    {
        SysCompany sysCompany = companyDao.getCompany(pkey);
        if (sysCompany == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        if (sysCompany.getEnabled()) throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        String mobile = sysCompany.getMobile();
        List<SysFarmer> exec = sysFarmerDao.select().eq("org", pkey).eq("idDel", false).exec();
        if (exec == null || exec.size() <= 0)
        {
            String PrefixM = "0000";
            List<SysCompany> delExec = companyDao.select().like("mobile", mobile).eq("idDel", true).exec();
            if (delExec != null && delExec.size() > 0)
            {
                List<Integer> prefixMList = new ArrayList<>();
                for (SysCompany c : delExec)
                    prefixMList.add(Integer.valueOf(c.getMobile().substring(0, 4)));
                Collections.sort(prefixMList);
                PrefixM = String.format("%04d", prefixMList.get(prefixMList.size() - 1) + 1);
            }
            sysCompany.setMobile(PrefixM + mobile);
            sysCompany.setIdDel(true);
            SysCompany company = companyDao.update(sysCompany);
            SysUser sysUser = sysUserDao.selectOne().eq("mobile", mobile).eq("company", pkey).exec();
            if (sysUser != null) userManager.delUser(sysUser.getPkey());
            if (company == null) return false;
            return true;
        }
        else
        {
            throw TofocusException.of(WsaleErrCode.EXIST_MARKET);
        }
        
    }
    
    public Boolean enableCompany(String pkey, Boolean enable)
    {
        SysCompany sysCompany = companyDao.getCompany(pkey);
        if (sysCompany == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        sysCompany.setEnabled(enable);
        SysCompany company = companyDao.update(sysCompany);
        if (company == null) return false;
        return true;
    }
    
}
