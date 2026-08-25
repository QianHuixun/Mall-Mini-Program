package cn.tofocus.lejia.dao.market;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.member.MktMemberTag;
import cn.tofocus.lejia.bean.entity.member.MktMemberTag.F;

@Component
public class MktMemberTagDao extends JpaSpecificationDelegate<String, MktMemberTag>
{
    public List<Integer> listTag(Integer member, Integer ascription)
    {
        List<Integer> res = new ArrayList<>();
        List<MktMemberTag> list = this.select().eq(F.member, member).eq(F.ascription, ascription).exec();
        list.forEach(e -> res.add(e.getTag()));
        return res;
    }

    public void removeByTag(Integer tag)
    {
        this.select().strict(true).eq(F.tag, tag).del();
    }
    
    public void removeByMember(Integer member)
    {
        this.select().strict(true).eq(F.member, member).del();
    }
    
    public List<Integer> listMember(List<Integer> tagKeys)
    {
        List<Integer> res = new ArrayList<>();
        List<MktMemberTag> list = this.select().in(F.tag, tagKeys).exec();
        Map<Integer,List<Integer>> map = new HashMap<>();
        list.forEach(e -> {
            if(!map.containsKey(e.getMember()))
            {
                List<Integer> k = new ArrayList<>();
                map.put(e.getMember(), k);
            }
            map.get(e.getMember()).add(e.getTag());
        });
        for(Integer key : map.keySet())
        {
            List<Integer> v = map.get(key);
            boolean b = v.containsAll(tagKeys);
            if(b)
                res.add(key);
        }
        return res;
    }
    
    public Map<Integer,String> mapTag(Integer member, Integer ascription)
    {
        Map<Integer,String> map = new HashMap<>();
        List<MktMemberTag> list = this.select().eq(F.member, member).eq(F.ascription, ascription).exec();
        list.forEach(e -> map.put(e.getTag(), e.getPkey()));
        return map;
    }
    
}
