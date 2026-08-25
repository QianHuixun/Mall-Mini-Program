package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdTagDrop;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd.F;

@Component
public class MktMemberMsdDao extends JpaSpecificationDelegate<Integer, MktMemberMsd>
{
    public MktMemberMsd get(Integer pkey, Integer ascription)
    {
        return this.selectOne().eq(F.pkey, pkey).eq(F.ascription, ascription).exec();
    }
    
    public List<MktMemberMsd> listByTags(Integer ascription, List<Integer> tags)
    {
        return this.select().eq(F.ascription, ascription).in(F.tag, tags).exec();
    }
    
    public List<MktMemberMsdTagDrop> listTags(Integer ascription)
    {
        return this.aggregation()
            .eq(F.ascription, ascription)
            .groupby(F.tag, "pkey")
            .count()
            .sort(F.tag)
            .execListDto(MktMemberMsdTagDrop.class);
    }
    
    public void updateTag(Integer pkey, Integer ascription, Integer tag)
    {
        this.select().strict(true).eq(F.pkey, pkey).eq(F.ascription, ascription).update(F.tag, tag);
    }

    public boolean existByTag(Integer tag)
    {
        return this.selectOne().eq(F.tag, tag).exec() != null;
    }
}
