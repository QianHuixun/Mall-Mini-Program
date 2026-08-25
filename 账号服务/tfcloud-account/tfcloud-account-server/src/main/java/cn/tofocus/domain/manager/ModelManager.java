package cn.tofocus.domain.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.bean.application.ModelInfo;
import cn.tofocus.account.db.dao.application.MenuDao;
import cn.tofocus.account.db.dao.domain.ModelDao;
import cn.tofocus.account.db.dao.org.DepartmentDao;
import cn.tofocus.account.db.dao.org.DeptModelDao;
import cn.tofocus.account.db.dao.org.OrgModelDao;
import cn.tofocus.account.db.entity.domain.ModelEntity;
import cn.tofocus.common.Constant;
import cn.tofocus.common.cachemap.read.CacheNotiyer;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.data.TreeBuilder;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.enums.ModelStatus;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

@Component
public class ModelManager
{
    @Autowired
    private ModelDao modelDao;
    
    @Autowired
    private OrgModelDao orgModelDao;
    
    @Autowired
    private DeptModelDao deptModelDao;
    
    @Autowired
    private MenuDao menuDao;
    
    @Autowired
    private CacheNotiyer notiyer;
    
    private HashedWheelTimer timer;
    
    private String exchangeRoot;
    
    @Value("${tofocus.prefix}")
    private String prefix;

    @Autowired
    private DepartmentDao departmentDao;
    
    /**************************
     * 
     *    模块配置
     *    
     **************************/
    
    @PostConstruct
    protected void init()
    {
        exchangeRoot =
            prefix + ".fanout.readCache." + Constant.TfDomain + "." + "d" + "." + AccountConstant.DeptModlesAccess;
        timer = new HashedWheelTimer(500, TimeUnit.MILLISECONDS);
        timer.start();
    }
    
    /**
     * 机构启用的模块
     * @param domainid
     * @param orgid
     * @param deptid
     * @return
     */
    public Map<String, ModelInfo> activeModels(String domainid, String orgid, String deptid)
    {
        Map<String, ModelInfo> result = new HashMap<>();
        //加载所有启用的模块
        Map<String, ModelInfo> models = modelDao.findLiveModelByDomain(domainid);
        if (orgid != null)
        {
            //应用公司模块配置
            Map<String, Boolean> orgConfig = orgModelDao.findByOrg(orgid);
            for (Entry<String, Boolean> e : orgConfig.entrySet())
            {
                //只有存在的模块可以应用
                if (models.containsKey(e.getKey()))
                    models.get(e.getKey()).setDefEnable(e.getValue());
            }
            if (deptid != null)
            {
                //应用市场模块配置
                Map<String, Boolean> deptConfig = deptModelDao.findByDept(deptid);
                for (Entry<String, Boolean> e : deptConfig.entrySet())
                {
                    //只有存在并且开启的模块可以应用
                    if (models.containsKey(e.getKey()) && models.get(e.getKey()).isDefEnable())
                        models.get(e.getKey()).setDefEnable(e.getValue());
                }
            }
        }
        for (Entry<String, ModelInfo> e : models.entrySet())
        {
            if (e.getValue().isDefEnable())
                result.put(e.getKey(), e.getValue());
        }
        return result;
    }

    private List<TreeModel<String, String>> toModelConfigs(Map<String, ModelInfo> models)
    {
        Collection<ModelInfo> list = models.values();
        TreeBuilder<String, String> builder = new TreeBuilder<>();
        for (ModelInfo model : list)
        {
            String pkey = model.getPkey();
            String name = model.getName();
            Integer sort = model.getSort();
            builder.addNode(pkey, null, name, model.isDefEnable(), false, null, sort);
        }
        return builder.build();
    }
    
    private Map<String, Boolean> mergeModelConfigsForUpdate(List<TreeModel<String, String>> current,
        List<TreeModel<String, String>> list)
    {
        Map<String, Boolean> modelConfigs = new HashMap<>();
        if (current.size() != list.size())
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR);
        for (int i = 0; i < current.size(); i++)
        {
            TreeModel<String, String> c = current.get(i);
            TreeModel<String, String> n = list.get(i);
            if (!c.getPkey().equals(n.getPkey()) || c.isDisabled())
                throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR);
            modelConfigs.put(n.getPkey(), n.isSelected());
        }
        return modelConfigs;
    }
    
    /**
     * 机构模块配置列表
     * @param domainid
     * @param orgid
     * @return
     */
    public List<TreeModel<String, String>> listModelConfigByOrg(String domainid, String orgid)
    {
        Map<String, ModelInfo> models = modelDao.findLiveModelByDomain(domainid);
        //应用公司模块配置
        Map<String, Boolean> orgConfig = orgModelDao.findByOrg(orgid);
        for (Entry<String, Boolean> e : orgConfig.entrySet())
        {
            if (models.containsKey(e.getKey()))
                models.get(e.getKey()).setDefEnable(e.getValue());
        }
        //组装
        return toModelConfigs(models);
    }
    
    /**
     * 配置机构模块
     * @param domainid
     * @param orgid
     * @param list
     */
    public void updateModelConfigByOrg(String domainid, String orgid, List<TreeModel<String, String>> list)
    {
        List<TreeModel<String, String>> current = listModelConfigByOrg(domainid, orgid);
        Map<String, Boolean> modelConfigs = mergeModelConfigsForUpdate(current, list);
        orgModelDao.updateConfig(domainid, orgid, modelConfigs);
        
        notifyDeptChanged(departmentDao.listByOrg(orgid));
    }
    
    /**
     * 部门模块配置列表
     * @param domainid
     * @param orgid
     * @param deptid
     * @return
     */
    public List<TreeModel<String, String>> listModelConfigByDept(String domainid, String orgid, String deptid)
    {
        Map<String, ModelInfo> models = modelDao.findLiveModelByDomain(domainid);
        Map<String, Boolean> orgConfig = orgModelDao.findByOrg(orgid);
        //应用公司模块配置
        for (Entry<String, Boolean> e : orgConfig.entrySet())
        {
            if (models.containsKey(e.getKey()))
                models.get(e.getKey()).setDefEnable(e.getValue());
        }
        //过滤未启用的模块
        Iterator<Entry<String, ModelInfo>> iter = models.entrySet().iterator();
        while (iter.hasNext())
        {
            Entry<String, ModelInfo> e = iter.next();
            if (!e.getValue().isDefEnable())
                iter.remove();
        }
        //应用市场模块配置
        Map<String, Boolean> deptConfig = deptModelDao.findByDept(deptid);
        for (Entry<String, Boolean> e : deptConfig.entrySet())
        {
            if (models.containsKey(e.getKey()) && models.get(e.getKey()).isDefEnable())
                models.get(e.getKey()).setDefEnable(e.getValue());
        }
        //组装
        return toModelConfigs(models);
    }
    
    /**
     * 配置部门模块
     * @param domainid
     * @param orgid
     * @param deptid
     * @param list
     */
    public void updateModelConfigByDept(String domainid, String orgid, String deptid,
        List<TreeModel<String, String>> list)
    {
        List<TreeModel<String, String>> current = listModelConfigByDept(domainid, orgid, deptid);
        Map<String, Boolean> modelConfigs = mergeModelConfigsForUpdate(current, list);
        deptModelDao.updateConfig(domainid, orgid, deptid, modelConfigs);
        
        notifyDeptChanged(deptid);
    }
    
    /**************************
     * 
     *    模块管理
     *    
     **************************/
    
    public ModelEntity getModel(String pkey)
    {
        return modelDao.get(pkey);
    }
    
    public List<StrKeyName> listModelName(String domain)
    {
        return modelDao.listModelName(domain);
    }
    
    public PageResult<ModelInfo> queryModel(Integer page, Integer pagesize, String domain)
    {
        return modelDao.queryModel(page, pagesize, domain);
    }
    
    public void saveModel(ModelEntity model)
    {
        modelDao.put(model);
        notifyDeptChanged("*");
    }
    
    public String delModel(String pkey, boolean force)
    {
        ModelEntity model = modelDao.get(pkey);
        if (!ModelStatus.Disabled.equals(model.getStatus()))
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "模块停用后才能删除");
        if (menuDao.countByModel(pkey) > 0)
            throw TofocusException.of(SysErrCode.ACCESS_DENIED, "模块下还有菜单，不能删除");
        if (!force)
        {
            if (orgModelDao.countByModel(pkey) > 0 || deptModelDao.countByModel(pkey) > 0)
            {
                return "模块已配置到公司和市场，是否强制删除";
            }
            modelDao.removeById(pkey);
        }
        else
        {
            orgModelDao.delByModel(pkey);
            deptModelDao.delByModel(pkey);
            modelDao.removeById(pkey);
        }
        notifyDeptChanged("*");
        return null;
    }
    
    private void notifyDeptChanged(Object key)
    {
        timer.newTimeout(new NotifyTask(key), 1, TimeUnit.SECONDS);
    }
    
    class NotifyTask implements TimerTask
    {
        private Object key;
        
        public NotifyTask(Object key)
        {
            super();
            this.key = key;
        }
        
        @Override
        public void run(Timeout timeout)
            throws Exception
        {
            notiyer.notifyChanged(exchangeRoot, JsonUtil.toString(key));
        }
    }
}
