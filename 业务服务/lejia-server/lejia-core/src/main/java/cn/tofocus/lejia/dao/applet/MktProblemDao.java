package cn.tofocus.lejia.dao.applet;


import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.applet.MktProblemEntity;

@Component
public class MktProblemDao extends JpaSpecificationDelegate<Integer,MktProblemEntity>
{
    public <T> PageResult<T> query(int page, int pagesize, List<Integer> types, String content, Integer ascription, Class<T> clazz)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .in("type", types)
            .eq("ascription", ascription)
            .or()
            .like("name", content)
            .like("answer", content)
            .close()
            .done()
            .sort("sort", false)
            .sort("pkey", false)
            .execDto(clazz);
    }
    
    public <T> List<T> listApp(Integer type, Integer ascription, Class<T> clazz)
    {
        return this.select()
            .eq("type", type)
            .eq("enabled", true)
            .eq("ascription", ascription)
            .sort("sort", false)
            .sort("pkey",false)
            .execDto(clazz);
    }
}