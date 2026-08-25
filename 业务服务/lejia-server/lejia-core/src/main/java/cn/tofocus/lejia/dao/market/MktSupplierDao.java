package cn.tofocus.lejia.dao.market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktSupplier;
import cn.tofocus.lejia.bean.entity.market.MktSupplier.F;

@Component
public class MktSupplierDao extends JpaSpecificationDelegate<Integer, MktSupplier>
{
    public Map<String, Integer> findPkeyMap(Integer ascription)
    {
        Map<String, Integer> res = new HashMap<>();
        List<MktSupplier> list = this.select().eq(F.isDel, false).eq(F.ascription, ascription).exec();
        for (MktSupplier v : list)
        {
            res.put(v.getName(), v.getPkey());
        }
        return res;
    }
    
    public List<Integer> findPkeys(Integer ascription, Boolean enabled)
    {
        return findPkeys(ascription, null, enabled);
    }
    
    public List<Integer> findPkeys(Integer ascription, String name, Boolean enabled)
    {
        return this.select()
            .eq(F.enabled, enabled)
            .like(F.name, name)
            .eq(F.isDel, false)
            .eq(F.ascription, ascription)
            .execDto(F.pkey, Integer.class);
    }
    
    public <T> PageResult<T> query(int page, int pagesize, Integer ascription, String name, String mobile,
        Boolean enabled, Class<T> clazz)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.ascription, ascription)
            .like(F.name, name)
            .like(F.mobile, mobile)
            .eq(F.enabled, enabled)
            .eq(F.isDel, false)
            .sort(F.createdTime)
            .sort(F.pkey)
            .execDto(clazz);
    }
    
    public <T> List<T> list(Integer ascription, String keyword, Class<T> clazz)
    {
        return this.select()
            .eq(F.ascription, ascription)
            .like(F.name, keyword)
            .eq(F.isDel, false)
            .sort(F.createdTime)
            .sort(F.pkey)
            .execDto(clazz);
    }
    
    public boolean existName(String name, Integer notEqPkey, Integer ascription)
    {
        return this.selectOne().eq(F.name, name).eq(F.ascription, ascription).notEq(F.pkey, notEqPkey).exec() != null;
    }
    
    public boolean existMobile(String mobile, Integer notEqPkey, Integer ascription)
    {
        return this.selectOne()
            .eq(F.mobile, mobile)
            .eq(F.ascription, ascription)
            .notEq(F.pkey, notEqPkey)
            .exec() != null;
    }
}
