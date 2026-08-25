package cn.tofocus.lejia.api.v1.sys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.api.v4.RoleApiV4;
import cn.tofocus.account.bean.role.RoleAclTree;
import cn.tofocus.account.bean.role.RoleMenuTree;
import cn.tofocus.account.bean.role.RoleMenuTree.AppMenuTree;
import cn.tofocus.account.bean.user.app.AppRoleInfo;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.Util;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.enums.MenuType;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.entity.sys.SysUser;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.SysUserDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/sys/role")
@RestController
public class LejiaSysRoleApiImpl implements LejiaSysRoleApi
{
    @Autowired
    private RoleApiV4 roleApiV4;
    
    @Autowired
    private SysUserDao sysUserDao;
    
    /*****************
     * 角色
     ****************/
    
    @Override
    @LogApi(operation = "新增角色", format = "新增角色,名称:{name}", resultFormat = "新增了角色,角色名称是: {name}")
    public Result<Boolean> insRole(String name, String description)
    {
        AppRoleInfo ari = new AppRoleInfo();
        ari.setDomainid(Constant.DomainId);
        ari.setOrgid(CurrentSession.companyPkey());
        ari.setDeptid(CurrentSession.marketPkey());
        ari.setName(name);
        ari.setDescription(description);
        ari.setPkey("zyysc_" + Util.getUUID().substring(16));
        ari.setEnable(true);
        Boolean r = roleApiV4.addRole(ari).fetchResult();
        
        RoleAclTree rat = new RoleAclTree();
        rat.setPkey(ari.getPkey());
     
        TreeModel<String, String> tm = new TreeModel<>();
        tm.setPkey("def");
        tm.setName("其他权限");
        tm.setSort(0);
        tm.setSelected(false);
        tm.setDisabled(false);
        tm.setLeaf(false);
        List<TreeModel<String, String>> sub = new ArrayList<>();
        TreeModel<String, String> s = new TreeModel<>();
        s.setPkey(Constant.Function.ZYYSC_LOGIN);
        s.setName("登陆权限");
        s.setSort(0);
        s.setSelected(true);
        s.setDisabled(false);
        s.setLeaf(true);
        sub.add(s);
        tm.setSub(sub);
        TreeModel<String,String> assembleRoleFuncTree = assembleRoleFuncTree();
        List<TreeModel<String, String>> data = new ArrayList<>();
        data.add(assembleRoleFuncTree);
        data.add(tm);
        rat.setData(data);
        roleApiV4.setRoleFuncTree(rat);
        return new Result<>(r);
    }
    
    private TreeModel<String, String> assembleRoleFuncTree()
    {
        TreeModel<String, String> tm = new TreeModel<>();
        tm.setPkey("sys");
        tm.setName("系统权限");
        tm.setSort(0);
        tm.setSelected(true);
        tm.setDisabled(false);
        tm.setLeaf(false);
        List<TreeModel<String, String>> sub = new ArrayList<>();
        TreeModel<String, String> s1 = new TreeModel<>();
        s1.setPkey(Constant.SysF.MANAGER_USER);
        s1.setName("用户管理");
        s1.setSort(0);
        s1.setSelected(true);
        s1.setDisabled(false);
        s1.setLeaf(true);
        sub.add(s1);
        TreeModel<String, String> s2 = new TreeModel<>();
        s2.setPkey(Constant.SysF.MANAGER_ROLE);
        s2.setName("角色管理");
        s2.setSort(0);
        s2.setSelected(true);
        s2.setDisabled(false);
        s2.setLeaf(true);
        sub.add(s2);
        TreeModel<String, String> s3 = new TreeModel<>();
        s3.setPkey(Constant.SysF.MANAGER_ORG);
        s3.setName("机构管理");
        s3.setSort(0);
        s3.setSelected(true);
        s3.setDisabled(false);
        s3.setLeaf(true);
        sub.add(s3);
        
        tm.setSub(sub);
        return tm;
    }
    
    @Override
    @LogApi(operation = "修改角色", format = "修改角色,名称:{name}")
    public Result<Boolean> updRole(String pkey, String name, String description)
    {
        AppRoleInfo ari = new AppRoleInfo();
        ari.setPkey(pkey);
        ari.setName(name);
        ari.setDescription(description);
        ari.setEnable(true);
        Boolean r = roleApiV4.updRole(ari).fetchResult();
        return new Result<>(r);
    }
    
    @Override
    @LogApi(operation = "删除角色", format = "删除角色")
    public Result<?> delRole(String pkey)
    {
        List<SysUser> exec = sysUserDao.select().eq("roleKey", pkey).exec();
        if (exec != null && !exec.isEmpty()) throw TofocusException.of(WsaleErrCode.USER_ROLE);
        String r = roleApiV4.delRole(pkey, false).fetchResult();
        return new Result<>(r);
    }
    
    @Override
    public Result<PageResult<AppRoleInfo>> query(int page, int pagesize)
    {
        String deptid = CurrentSession.marketPkey();
        List<AppRoleInfo> list = roleApiV4.listRoleInDept(Constant.DomainId, null, deptid).fetchResult();
//        SysUser user = sysUserDao.get(CurrentSession.userPkey());
//        int judg = judg(user);
//        if(judg == 1)
//        {
//            AppRoleInfo ar = new AppRoleInfo();
//            ar.setPkey(Constant.Role.COMPANY_HEAD);
//            ar.setName("超级管理员");
//            list.add(0, ar);
//        }
//        if(judg == 2)
//        {
//            AppRoleInfo ar = new AppRoleInfo();
//            ar.setPkey(Constant.Role.MARKET_MANAGER);
//            ar.setName("市场负责人");
//            list.add(0, ar);
//        }
        PageResult<AppRoleInfo> p = PageUtil.page(list, PageParameter.of(page, pagesize));
        return new Result<>(p);
    }
    
    @Override
    public Result<List<String>> getFunction(String pkey)
    {
//        Integer userPkey = CurrentSession.userPkey();
//        SysUser sysUser = sysUserDao.get(userPkey);
//        RoleMenuTree rmt = roleApiV4.getRoleMenu(sysUser.getRoleKey()).fetchResult();
        RoleMenuTree rmt = roleApiV4.getRoleMenu(pkey).fetchResult();
        List<String> list = new ArrayList<>();
        for(AppMenuTree m : rmt.getData())
        {
            if(m.getSub() != null)
            {
                for(TreeModel<String, MenuType> tm : m.getSub())
                {
                    if(tm.getSub() != null)
                    {
                        for(TreeModel<String, MenuType> tms : tm.getSub())
                        {
                            if(tms.isSelected())
                                list.add(tms.getPkey());
                        }
                    }
                    else
                    {
                        if(tm.isSelected())
                            list.add(tm.getPkey());
                    }
                }
            }
        }
        
        
//        RoleAclTree fetchResult = roleApiV4.getRoleFunc(pkey, null).fetchResult();
        //        RoleAccessGroup group = roleApiV3.listAppRoleAcl(pkey).fetchResult();
        //        RoleFunctionTree tree = buildRoleFunctionTree(pkey, group.getGroup().get(Constant.rootRoleGroup));
       
//        for (TreeModel<String, String> t : fetchResult.getData())
//        {
//            if (t.getSub() != null)
//            {
//                for (TreeModel<String, ?> lline : t.getSub())
//                {
//                    if (lline.isSelected()) list.add(lline.getPkey());
//                }
//            }
//            else
//            {
//                if (t.isSelected()) list.add(t.getPkey());
//            }
//        }
        //        if (tree.getData() != null)
        //        {
        //            for (TreeModel<String, ?> line : tree.getData().getSub())
        //            {
        //                if (line.getSub() != null)
        //                    for (TreeModel<String, ?> lline : line.getSub())
        //                    {
        //                        if (lline.isSelected()) list.add(lline.getPkey());
        //                    }
        //                else
        //                {
        //                    if (line.isSelected()) list.add(line.getPkey());
        //                }
        //            }
        //        }
        return new Result<>(list);
    }
    
    @Override
    public Result<RoleMenuTree> getCurrentUserFunction()
    {
        Integer userPkey = CurrentSession.userPkey();
        SysUser sysUser = sysUserDao.get(userPkey);
        return roleApiV4.getRoleMenu(sysUser.getRoleKey());
    }
    
    @Override
    @LogApi(operation = "设置角色权限", format = "设置角色权限")
    public Result<Boolean> setFunction(RoleMenuTree rmt)
    {
        Boolean r = roleApiV4.setRoleMenu(rmt).fetchResult();
        return new Result<>(r);
    }
    
    //    private RoleFunctionTree buildRoleFunctionTree(String pkey, AccessConfig accessConfig)
    //    {
    //        RoleFunctionTree tree = new RoleFunctionTree();
    //        tree.setRolePkey(pkey);
    //        tree.setData(buildTree(accessConfig));
    //        return tree;
    //    }
    
    //    private TreeModel<String, String> buildTree(AccessConfig accessConfig)
    //    {
    //        if (accessConfig == null
    //            || (accessConfig.isGroup() && (accessConfig.getSub() == null || accessConfig.getSub().size() == 0)))
    //            return null;
    //        TreeModel<String, String> tree = new TreeModel<String, String>();
    //        tree.setPkey(accessConfig.getPkey());
    //        tree.setName(accessConfig.getName());
    //        tree.setSort(accessConfig.getSort());
    //        tree.setDisabled(accessConfig.isReadonly());
    //        tree.setSelected(accessConfig.getAccept() != null && accessConfig.getAccept());
    //        if (accessConfig.getSub() != null && accessConfig.getSub().size() > 0)
    //        {
    //            for (AccessConfig subConfig : accessConfig.getSub())
    //            {
    //                TreeModel<String, String> subitem = buildTree(subConfig);
    //                if (subitem != null && subitem.isSelected()) tree.setSelected(true);
    //                tree.addSub(subitem);
    //            }
    //        }
    //        if (!tree.isSelected()) return null;
    //        return tree;
    //    }
    
    //    private void buildAccessConfig(AccessConfig accessConfig, TreeModel<String, ?> data)
    //    {
    //        if (accessConfig == null || data == null) return;
    //        if (accessConfig.isGroup())
    //        {
    //            if (accessConfig.getSub() != null && data.getSub() != null)
    //            {
    //                Map<Integer, TreeModel<String, ?>> map = new HashMap<>();
    //                for (TreeModel<String, ?> subNode : data.getSub())
    //                {
    //                    map.put(subNode.getSort(), subNode);
    //                }
    //                for (AccessConfig subConfig : accessConfig.getSub())
    //                {
    //                    TreeModel<String, ?> subNode = map.get(subConfig.getSort());
    //                    buildAccessConfig(subConfig, subNode);
    //                }
    //            }
    //        }
    //        else
    //        {
    //            if (!accessConfig.isReadonly())
    //            {
    //                if (data.isShow())
    //                    accessConfig.setAccept(true);
    //                else
    //                    accessConfig.setAccept(null);
    //            }
    //        }
    //    }
    
    @Override
    public Result<PageResult<AppRoleInfo>> queryForUser()
    {
        log.info("queryForUser: {}", CurrentSession.marketPkey());
        String deptid = CurrentSession.marketPkey();
        List<AppRoleInfo> list = roleApiV4.listRoleInDept(Constant.DomainId, null, deptid).fetchResult();
        SysUser user = sysUserDao.get(CurrentSession.userPkey());
        int judg = judg(user);
        if(judg == 1)
        {
            AppRoleInfo ar = new AppRoleInfo();
            ar.setPkey(Constant.Role.COMPANY_HEAD);
            ar.setName("超级管理员");
            list.add(0, ar);
        }
        if(judg == 2)
        {
            AppRoleInfo ar = new AppRoleInfo();
            ar.setPkey(Constant.Role.MARKET_MANAGER);
            ar.setName("市场负责人");
            list.add(0, ar);
        }
        PageResult<AppRoleInfo> p = PageUtil.page(list, PageParameter.of(0, 200));
        return new Result<>(p);
    }
    
    @Override
    public Result<RoleMenuTree> getFunctionAll()
    {
//        Integer ascription = CurrentSession.ascriptionPkey();
//        if (CurrentSession.marketPkey() == null) return getRoleFunction1(Constant.Role.MARKET_HEAD);
//        if ((Constant.Operation + ascription).equals(CurrentSession.marketPkey()))
//            return getRoleFunction1(Constant.Role.COMPANY_HEAD);
//        return getRoleFunction1(Constant.Role.MARKET_MANAGER);
        Integer userPkey = CurrentSession.userPkey();
        SysUser sysUser = sysUserDao.get(userPkey);
        RoleMenuTree fetchResult = roleApiV4.getRoleMenu(sysUser.getRoleKey()).fetchResult();
        List<AppMenuTree> list = new ArrayList<>();
        int judg = judg(sysUser);
        for(AppMenuTree am : fetchResult.getData())
        {
            if(judg == 1 && Constant.App.WEB.equals(am.getPkey()))
            {
                list.add(am);
            }
            if(judg == 2 && Constant.App.WEB_MARKET.equals(am.getPkey()))
            {
                list.add(am);
            }
            if(judg == 3 && Constant.App.WEB_COMPANY.equals(am.getPkey()))
            {
                list.add(am);
            }
        }
        fetchResult.setData(list);
        return new Result<>(fetchResult);
        
//        RoleMenuTree rmt = roleApiV4.getRoleMenu(sysUser.getRoleKey()).fetchResult();
//        RoleMenuTree r = new RoleMenuTree();
//        r.setPkey(rmt.getPkey());
//        r.setName(rmt.getName());
//        List<AppMenuTree> data = rmt.getData();
//        AppMenuTree amt = new AppMenuTree();
//        if(data == null || data.isEmpty())
//        {
//            r.setData(data);
//        }
//        else
//        {
//            AppMenuTree appMenuTree = data.get(0);
//            amt.setPkey(appMenuTree.getPkey());
//            amt.setName(appMenuTree.getName());
//            List<TreeModel<String, MenuType>> sub = new ArrayList<>();
//            for(TreeModel<String, MenuType> tm : appMenuTree.getSub())
//            {
//                if(tm.getSub() != null)
//                {
//                    TreeModel<String, MenuType> ntm = new TreeModel<>();
//                    BeanUtils.copyProperties(tm, ntm);
//                    List<TreeModel<String, MenuType>> subs = new ArrayList<>();
//                    for(TreeModel<String, MenuType> tms : tm.getSub())
//                    {
//                        if(tms.isSelected())
//                            subs.add(tms);
//                    }
//                    ntm.setSub(subs);
//                    sub.add(ntm);
//                }
//                else
//                {
//                    if(tm.isSelected())
//                        sub.add(tm);
//                }
//            }
//            amt.setSub(sub);
//            r.setData(Arrays.asList(amt));
//        }
//        return new Result<>(r);
    }
    
    private int judg(SysUser user)
    {
        if(user == null)
            return 1;
        if (user.getFarmer() != null)
            if ((Constant.Operation + user.getAscription()).equals(user.getFarmer()))
                // 1代表运营者
                return 1;
            else
                // 2代表市场
                return 2;
        else
            // 3代表公司
            return 3;
    }
    
//    private Result<RoleAclTree> getRoleFunction1(String pkey)
//    {
//        Result<RoleAclTree> r = roleApiV4.getRoleFunc(pkey, null);
//        
//        //        RoleAccessGroup group = roleApiV3.listAppRoleAcl(pkey).fetchResult();
//        //        RoleFunctionTree tree = buildRoleFunctionTree(pkey, group.getGroup().get(Constant.rootRoleGroup));
//        //        return new Result<>(tree);
//        return r;
//    }
}
