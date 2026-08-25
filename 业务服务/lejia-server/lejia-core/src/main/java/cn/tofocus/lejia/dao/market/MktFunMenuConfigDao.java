package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktFunMenuConfig;
import cn.tofocus.lejia.bean.entity.market.MktFunMenuConfig.F;
@Component
@DataSourceWithFileUrl
public class MktFunMenuConfigDao extends JpaSpecificationDelegate<Integer,MktFunMenuConfig>
{
    public <T>PageResult<T> queryMktFunMenuConfig(int page,int pagesize,List<String> farmers,Class<T>clazz)
    {
        return this.selectPage().page(page).pagesize(pagesize).in(F.farmer, farmers).sort(F.sort,false).sort(F.createdTime).execDto(clazz);
    }

    public <T> List<T> listFunMenuConfig(List<String> farmers,Class<T>clazz)
    {
        return this.select().in(F.farmer, farmers).eq(F.enabled,true).sort(F.sort,false).sort(F.createdTime).execDto(clazz);
    }
}
