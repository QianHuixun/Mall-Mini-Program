package cn.tofocus.account.api.v4;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.ModelConfig;
import cn.tofocus.account.bean.application.ModelInfo;
import cn.tofocus.account.bean.application.ModelforUpd;
import cn.tofocus.account.db.cache.domain.DomainReadCache;
import cn.tofocus.account.db.entity.domain.ModelEntity;
import cn.tofocus.account.db.entity.org.DepartmentEntity;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.core.security.aop.CheckAppPermission;
import cn.tofocus.core.security.aop.CheckAppPermission.ScopeCheckType;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.domain.manager.ModelManager;
import cn.tofocus.domain.manager.OrginazationManager;

@RequestMapping("/v4/model")
@RestController
public class ModelApiV4Impl implements ModelApiV4
{
    @Autowired
    private ModelManager modelManager;
    
    @Autowired
    private OrginazationManager orginazationManager;
    
    @Autowired
    private DomainReadCache domainReadCache;
    
    private static final String PermFunction = SysFunctionEnum.DomainAdmin;
    
    private static final String ManagerOrgFunction = SysFunctionEnum.ManagerOrg;
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<PageResult<ModelInfo>> queryModel(Integer page, Integer pagesize, String domain)
    {
        PageResult<ModelInfo> data = modelManager.queryModel(page, pagesize, domain);
        return new Result<>(data);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> addModel(@Valid ModelInfo info)
    {
        if (!domainReadCache.isExistKey(info.getDomainid()))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, info.getDomainid() + " 域不存在");
        ModelEntity current = modelManager.getModel(info.getPkey());
        if (current != null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_EXIST);
        ModelEntity newModel = new ModelEntity();
        newModel.setPkey(info.getPkey());
        newModel.setDomainid(info.getDomainid());
        newModel.setName(info.getName());
        newModel.setDefEnable(info.isDefEnable());
        newModel.setDefShowMenu(info.isDefShowMenu());
        newModel.setSort(info.getSort());
        newModel.setStatus(info.getStatus());
        SecurityContextUtil.checkRight(PermFunction, newModel);
        modelManager.saveModel(newModel);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> updModel(@Valid ModelforUpd info)
    {
        ModelEntity current = modelManager.getModel(info.getPkey());
        if (current == null)
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_NOT_EXIST);
        SecurityContextUtil.checkRight(PermFunction, current);
        
        current.setName(info.getName());
        current.setDefEnable(info.isDefEnable());
        current.setSort(info.getSort());
        current.setStatus(info.getStatus());
        
        modelManager.saveModel(current);
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = PermFunction, checkType = ScopeCheckType.current)
    public Result<String> delModel(String pkey, boolean force)
    {
        ModelEntity current = modelManager.getModel(pkey);
        if (current != null)
        {
            SecurityContextUtil.checkRight(PermFunction, current);
            String errMsg = modelManager.delModel(pkey, force);
            return new Result<>(errMsg);
        }
        else
            return new Result<>();
    }
    
    @Override
    public Result<List<StrKeyName>> listModelName(String domain)
    {
        List<StrKeyName> list = modelManager.listModelName(domain);
        return new Result<>(list);
    }
    
    @Override
    @CheckAppPermission(value = ManagerOrgFunction, checkType = ScopeCheckType.current)
    public Result<ModelConfig<String>> listModelConfigByOrg(String orgid)
    {
        String domainid = SecurityContextUtil.currentDomain();
        SecurityContextUtil.checkRight(ManagerOrgFunction, domainid, orgid, null);
        List<TreeModel<String, String>> data = modelManager.listModelConfigByOrg(domainid, orgid);
        return new Result<>(new ModelConfig<>(orgid, data));
    }
    
    @Override
    @CheckAppPermission(value = ManagerOrgFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> setModelConfigByOrg(ModelConfig<String> data)
    {
        String domainid = SecurityContextUtil.currentDomain();
        String orgid = data.getOwner();
        SecurityContextUtil.checkRight(ManagerOrgFunction, domainid, orgid, null);
        modelManager.updateModelConfigByOrg(domainid, orgid, data.getData());
        return new Result<>(true);
    }
    
    @Override
    @CheckAppPermission(value = ManagerOrgFunction, checkType = ScopeCheckType.current)
    public Result<ModelConfig<String>> listModelConfigByDept(String deptid)
    {
        String domainid = SecurityContextUtil.currentDomain();
        DepartmentEntity dept = orginazationManager.getDepartment(deptid);
        SecurityContextUtil.checkRight(ManagerOrgFunction, domainid, dept.getOrgid(), deptid);
        List<TreeModel<String, String>> data = modelManager.listModelConfigByDept(domainid, dept.getOrgid(), deptid);
        return new Result<>(new ModelConfig<>(deptid, data));
    }
    
    @Override
    @CheckAppPermission(value = ManagerOrgFunction, checkType = ScopeCheckType.current)
    public Result<Boolean> setModelConfigByDept(ModelConfig<String> data)
    {
        String domainid = SecurityContextUtil.currentDomain();
        String deptid = data.getOwner();
        DepartmentEntity dept = orginazationManager.getDepartment(deptid);
        SecurityContextUtil.checkRight(ManagerOrgFunction, domainid, dept.getOrgid(), deptid);
        modelManager.updateModelConfigByDept(domainid, dept.getOrgid(), deptid, data.getData());
        return new Result<>(true);
    }
}
