//package cn.tofocus.lejia.dao.market;
//
//import java.util.List;
//
//import org.springframework.stereotype.Component;
//
//import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdTagDrop;
//import cn.tofocus.lejia.bean.entity.member.MktMemberJd;
//import cn.tofocus.lejia.bean.entity.member.MktMemberJd.F;
//
//@Component
//public class MktMemberJdDao extends JpaSpecificationDelegate<Integer, MktMemberJd>
//{
//    public MktMemberJd get(Integer pkey, Integer ascription)
//    {
//        return this.selectOne().eq(F.pkey, pkey).eq(F.ascription, ascription).exec();
//    }
//    
//    public List<MktMemberJd> listByTags(Integer ascription, List<Integer> tags)
//    {
//        return this.select().eq(F.ascription, ascription).in(F.tag, tags).exec();
//    }
//    
//    public List<MktMemberJdTagDrop> listTags(Integer ascription)
//    {
//        return this.aggregation()
//            .eq(F.ascription, ascription)
//            .groupby(F.tag, "pkey")
//            .count()
//            .sort(F.tag)
//            .execListDto(MktMemberJdTagDrop.class);
//    }
//    
//    public void updateTag(Integer pkey, Integer ascription, Integer tag)
//    {
//        this.select().strict(true).eq(F.pkey, pkey).eq(F.ascription, ascription).update(F.tag, tag);
//    }
//}
