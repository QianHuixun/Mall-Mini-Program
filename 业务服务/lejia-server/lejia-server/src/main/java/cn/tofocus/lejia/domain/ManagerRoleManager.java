package cn.tofocus.lejia.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.JoinType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.sys.ManagerOnPage;
import cn.tofocus.lejia.bean.entity.market.MktManager;
import cn.tofocus.lejia.bean.entity.market.MktManagerRole;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.enums.ManagerRole;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktManagerDao;
import cn.tofocus.lejia.dao.market.MktManagerRoleDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.exception.LejiaErrCode;

@Component
public class ManagerRoleManager
{
    @Autowired
    private MktManagerDao managerDao;
    
    @Autowired
    private MktManagerRoleDao managerRoleDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    // 新增
    public Boolean ins(ManagerOnPage info)
    {
        MktMember member = memberDao.getMobile(info.getMobile(), CurrentSession.ascriptionPkey());
        if(member == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_MANAGER_ERROR); 
        MktManager mktManager = managerDao.byMobileAndFarmer(info.getMobile(), CurrentSession.marketPkey());
        if(mktManager != null)
            throw TofocusException.of(LejiaErrCode.MANAGER_MOBILE_ERROR); 
        MktManager entity = new MktManager();
        entity.setMobile(info.getMobile());
        entity.setAscription(CurrentSession.ascriptionPkey());
        entity.setFarmer(CurrentSession.marketPkey());
        entity.setCompany(CurrentSession.companyPkey());
        entity.setEnabled(true);
        MktManager add = managerDao.add(entity);
        for(ManagerRole r : info.getRoles())
        {
            MktManagerRole mr = new MktManagerRole();
            mr.setAscription(add.getAscription());
            mr.setFarmer(add.getFarmer());
            mr.setCompany(add.getCompany());
            mr.setManager(add.getPkey());
            mr.setRole(r);
            mr.setPkey(mr.makePkey(add.getPkey(), r));
            managerRoleDao.add(mr);
        }
        return true;
    }
    
    // 编辑
    public Boolean upd(ManagerOnPage info)
    {
        MktManager entity = managerDao.get(info.getPkey());
        if(!entity.getMobile().equals(info.getMobile()))
        {
            entity.setMobile(info.getMobile());
            managerDao.update(entity);
        }
        List<MktManagerRole> list = managerRoleDao.listRoleManager(info.getPkey());
        managerRoleDao.removeAll(list);
        for(ManagerRole r : info.getRoles())
        {
            MktManagerRole mr = new MktManagerRole();
            mr.setAscription(entity.getAscription());
            mr.setFarmer(entity.getFarmer());
            mr.setCompany(entity.getCompany());
            mr.setManager(entity.getPkey());
            mr.setRole(r);
            mr.setPkey(mr.makePkey(entity.getPkey(), r));
            managerRoleDao.add(mr);
        }
        return true;
    }
    
    // 删除
    public Boolean del(Integer key)
    {
        managerDao.removeById(key);
        List<MktManagerRole> listRoleManager = managerRoleDao.listRoleManager(key);
        managerRoleDao.removeAll(listRoleManager);
        return true;
    }
    
    // 列表查询
    public PageResult<ManagerOnPage> query(int page, int pagesize, String mobile, ManagerRole role)
    {
        PageResult<ManagerOnPage> pageResult;
        if(role != null)
        {
            pageResult = managerDao.joinSelectPage()
                .as("pkey")
                .as("mobile")
                .as("createdTime")
                .page(page)
                .pagesize(pagesize)
                .like("mobile", mobile)
                .eq("farmer", CurrentSession.marketPkey())
                .join(MktManagerRole.class, JoinType.INNER, null, "pkey", "manager")
                .eq("role", role)
                .endJoin()
                .sort("createdTime")
                .sort("pkey")
                .exec(ManagerOnPage.class);
        }
        else
        {
            pageResult = managerDao.selectPage()
                .page(page)
                .pagesize(pagesize)
                .like("mobile", mobile)
                .eq("farmer", CurrentSession.marketPkey())
                .sort("createdTime")
                .sort("pkey")
                .execDto(ManagerOnPage.class);
        }
        
        for(ManagerOnPage m : pageResult.getContent())
        {
            List<ManagerRole> listRoles = managerRoleDao.listRoles(m.getPkey());
            m.setRoles(listRoles);
            List<String> roleNames = new ArrayList<>();
            listRoles.forEach(e -> roleNames.add(e.getName()));
            m.setRoleNames(roleNames);
        }
        return pageResult;
    }
    
    // 老数据处理
    public Boolean oldDataHandle()
    {
        List<MktManager> list = managerDao.findAll();
        List<MktManagerRole> addList = new ArrayList<>();
        for(MktManager m : list)
        {
            MktManagerRole mr = new MktManagerRole();
            mr.setRole(ManagerRole.COUPON_MANAGER);
            mr.setManager(m.getPkey());
            mr.setFarmer(m.getFarmer());
            mr.setCompany(m.getCompany());
            mr.setAscription(m.getAscription());
            mr.setPkey(m.getPkey(), ManagerRole.COUPON_MANAGER);
            addList.add(mr);
        }
        managerRoleDao.addAll(addList);
        return true;
    }
}
