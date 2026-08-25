package cn.tofocus.lejia.dao.applet;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.applet.MktProblemTypeEntity;

@Component
public class MktProblemTypeDao extends JpaSpecificationDelegate<Integer,MktProblemTypeEntity>
{
    public <T> PageResult<T> query(int page, int pagesize, Integer ascription, Class<T> clazz)
    {
        return this.selectPage().page(page).pagesize(pagesize).eq("ascription", ascription).sort("sort", false).execDto(clazz);
        
    }
    
    public <T> List<T> list(Integer ascription, Class<T> clazz)
    {
        return this.select().eq("ascription", ascription).sort("sort", false).execDto(clazz);
    }
    
    public Integer getMaxSort(Integer ascription)
    {
        Number max = this.aggregation().eq("ascription", ascription).execMax("sort");
        return max.intValue();
    }
}