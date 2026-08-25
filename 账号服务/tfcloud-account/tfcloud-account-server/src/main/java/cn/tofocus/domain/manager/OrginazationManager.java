package cn.tofocus.domain.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.tofocus.account.db.dao.org.DepartmentDao;
import cn.tofocus.account.db.dao.org.OrginazationDao;
import cn.tofocus.account.db.entity.org.DepartmentEntity;
import cn.tofocus.account.db.entity.org.OrginazationEntity;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;

/**
 * 
 * 负责整个机构的管理
 * <功能详细描述>
 * 
 * @author  wyw
 * @version  [版本号, 2018年8月16日]
 */
@Component
public class OrginazationManager
{
    @Autowired
    private OrginazationDao orgs;
    
    //部门
    @Autowired
    private DepartmentDao depts;
    
    /**************************
     * 
     *     机构管理通用方法
     * 
     **************************/
    
    public void saveOrg(String domainid, String orgid, String name)
    {
        OrginazationEntity org = orgs.get(orgid);
        if(org == null)
        {
            org = new OrginazationEntity();
            org.setOrgid(orgid);
            org.setName(name);
            org.setDomainid(domainid);
        }
        else
        {
            if(!domainid.equals(org.getDomainid()))
                throw TofocusException.of(SysErrCode.ACCESS_DENIED,
                    "orgid 已存在并且属于[" + org.getDomainid() + "]域");
            org.setName(name);
        }
        orgs.put(org);
    }
    
    public void delOrg(String domainid, String orgid)
    {
        OrginazationEntity org = orgs.get(orgid);
        if(org != null)
        {
            if(!domainid.equals(org.getDomainid()))
                throw TofocusException.of(SysErrCode.ACCESS_DENIED,
                    "orgid 存在并且属于[" + org.getDomainid() + "]域");
            orgs.removeById(orgid);
        }
    }
    
    public OrginazationEntity getOrg(String orgid)
    {
        OrginazationEntity org = orgs.get(orgid);
        return org;
    }
    
    /**************************
     * 
     *     部门管理
     *     （系统管理员可操作所有机构，机构管理员可操作下属部门，部门管理员可以操作下属部门，
     *     查询系统管理员查询所有，机构职员可查询本机构部门）
     *     新增修改部门
     *     删除部门
     *     获取一个部门
     *     获取一个机构的子部门
     *     获取一个部门的子部门
     * 
     **************************/
    
    public void saveDepartment(String domainid, String deptid, String orgid, String name)
    {
        DepartmentEntity dept = depts.get(deptid);
        if(dept == null)
        {
            dept = new DepartmentEntity();
            dept.setDeptid(deptid);
            dept.setOrgid(orgid);
            dept.setName(name);
            dept.setDomainid(domainid);
        }
        else
        {
            if(!domainid.equals(dept.getDomainid()))
                throw TofocusException.of(SysErrCode.ACCESS_DENIED,
                    "orgid 已存在并且属于[" + dept.getDomainid() + "]域");
            dept.setName(name);
            dept.setOrgid(orgid);
        }
        depts.put(dept);
    }
    
    public void delDepartment(String domainid, String deptid)
    {
        DepartmentEntity dept = depts.get(deptid);
        if(dept != null)
        {
            if(!domainid.equals(dept.getDomainid()))
                throw TofocusException.of(SysErrCode.ACCESS_DENIED,
                    "deptid 存在并且属于[" + dept.getDomainid() + "]域");
            depts.removeById(deptid);
        }
    }
    
    public DepartmentEntity getDepartment(String deptid)
    {
        DepartmentEntity dept = depts.get(deptid);
        return dept;
    }
}
