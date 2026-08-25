package cn.tofocus.lejia.dao.market;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.member.MktTag;
import cn.tofocus.lejia.bean.entity.member.MktTag.F;
import cn.tofocus.lejia.bean.enums.member.TagType;

@Component
public class MktTagDao extends JpaSpecificationDelegate<Integer, MktTag>
{
    public <T> PageResult<T> query(int page, int pagesize, Integer ascription, List<TagType> types, String name,
        String description, Class<T> clazz)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.ascription, ascription)
            .in(F.type, types)
            .like(F.name, name)
            .like(F.description, description)
            .eq(F.idDel, false)
            .sort(F.createdTime)
            .sort(F.pkey)
            .execDto(clazz);
    }
    
    public <T> T get(Integer pkey, Class<T> clazz)
    {
        return this.selectOne().eq(F.pkey, pkey).eq(F.idDel, false).execDto(clazz);
    }
    
    public MktTag getByName(String name, Integer ascription)
    {
        return this.selectOne().eq(F.name, name).eq(F.ascription, ascription).eq(F.idDel, false).exec();
    }
    
    public boolean exist(String name, Integer notEqPkey, Integer ascription)
    {
        return this.selectOne()
            .eq(F.name, name)
            .eq(F.ascription, ascription)
            .notEq(F.pkey, notEqPkey)
            .eq(F.idDel, false)
            .exec() != null;
    }
    
    public List<MktTag> listKeys(List<Integer> keys)
    {
        return this.select().in(F.pkey, keys).eq(F.idDel, false).exec();
    }
    
    public Map<String, Integer> map(Integer ascription)
    {
        List<MktTag> list = this.select().eq(F.ascription, ascription).eq(F.idDel, false).exec();
        Map<String, Integer> map = new HashMap<>();
        list.forEach(e -> map.put(e.getName(), e.getPkey()));
        return map;
    }
    
    public Map<Integer, String> mapName(List<Integer> tagKeys, Integer ascription)
    {
        List<MktTag> list = this.select().in(F.pkey, tagKeys).eq(F.ascription, ascription).eq(F.idDel, false).exec();
        Map<Integer, String> map = new HashMap<>();
        list.forEach(e -> map.put(e.getPkey(), e.getName()));
        return map;
    }

    public void updateType(Integer pkey, TagType type)
    {
        this.select().strict(true).eq(F.pkey, pkey).update(F.type, type);
    }

    public void updateType(Collection<Integer> pkeys, TagType type)
    {
        this.select().strict(true).in(F.pkey, pkeys).update(F.type, type);
    }
}
