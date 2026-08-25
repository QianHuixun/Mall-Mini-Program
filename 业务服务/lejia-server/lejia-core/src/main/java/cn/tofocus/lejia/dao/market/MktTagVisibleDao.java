package cn.tofocus.lejia.dao.market;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktTagVisible;
import cn.tofocus.lejia.bean.entity.market.MktTagVisible.F;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;

@Component
public class MktTagVisibleDao extends JpaSpecificationDelegate<String, MktTagVisible>
{
    public List<Long> listTarget(TagVisibleTargetType type, List<Integer> listTag)
    {
        List<Long> res = new ArrayList<>();
        List<MktTagVisible> list = this.select().eq(F.type, type).in(F.tag, listTag).exec();
        list.forEach(e -> res.add(e.getTarget()));
        return res;
    }

    public List<Integer> listTagKeys(TagVisibleTargetType type, Long target)
    {
        List<Integer> res = new ArrayList<>();
        List<MktTagVisible> list = this.select().eq(F.type, type).eq(F.target, target).exec();
        list.forEach(e -> res.add(e.getTag()));
        return res;
    }

    public Set<Long> filterVisibleTargets(TagVisibleTargetType type, List<Long> targets, List<Integer> tags)
    {
        List<Long> res = this.select().eq(F.type, type).in(F.target, targets).in(F.tag, tags).execDto(F.target, Long.class);
        return res == null ? new HashSet<>() : new HashSet<>(res);
    }
    
    public void removeByTag(Integer tag)
    {
        this.select().strict(true).eq(F.tag, tag).del();
    }
    
    public void removeByTargetType(TagVisibleTargetType type, Long target)
    {
        this.select()
        .strict(true)
        .eq(F.type, type)
        .eq(F.target, target)
        .del();
    }
    
    public void removeByTargetType(TagVisibleTargetType type, List<Long> targets)
    {
        this.select().strict(true).eq(F.type, type).in(F.target, targets).del();
    }
    
    public List<MktTagVisible> listByTargets(TagVisibleTargetType type, List<Long> targets)
    {
        return this.select().strict(true).eq(F.type, type).in(F.target, targets).exec();
    }
    
}
