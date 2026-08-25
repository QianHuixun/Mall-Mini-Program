package cn.tofocus.account.db.dao.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.application.ModelInfo;
import cn.tofocus.account.db.entity.domain.ModelEntity;
import cn.tofocus.account.db.entity.domain.ModelEntity.F;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.enums.ModelStatus;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;

@Component
public class ModelDao extends JpaSpecificationDelegate<String, ModelEntity>
{
    /**
     * 域下活动的模块
     * @param domainid
     * @return
     */
    public Map<String, ModelInfo> findLiveModelByDomain(String domainid)
    {
        Map<String, ModelInfo> map = new HashMap<>();
        List<ModelEntity> models =
            this.select().strict(true).eq(F.domainid, domainid).notEq(F.status, ModelStatus.Disabled).exec();
        for (ModelEntity e : models)
        {
            map.put(e.getPkey(), BeanUtil.beanFrom(ModelInfo.class, e));
        }
        return map;
    }

    public List<ModelEntity> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }
    
    public List<StrKeyName> listModelName(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).execDto(StrKeyName.class);
    }
    
    public PageResult<ModelInfo> queryModel(Integer page, Integer pagesize, String domain)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .strict(true)
            .eq(F.domainid, domain)
            .sort(F.sort, false)
            .execDto(ModelInfo.class);
    }
}
