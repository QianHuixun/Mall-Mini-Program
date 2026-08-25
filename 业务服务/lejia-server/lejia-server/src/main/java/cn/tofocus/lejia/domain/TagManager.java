package cn.tofocus.lejia.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.db.DbData;
import cn.tofocus.db.DbListResult;
import cn.tofocus.lejia.bean.entity.market.MktTagVisible;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberTag;
import cn.tofocus.lejia.bean.entity.member.MktTag;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberTagDao;
import cn.tofocus.lejia.dao.market.MktTagDao;
import cn.tofocus.lejia.dao.market.MktTagVisibleDao;

@Component
public class TagManager
{
    @Autowired
    private MktTagDao tagDao;
    
    @Autowired
    private MktTagVisibleDao tagVisibleDao;
    
    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    // 增加关联表
    public Boolean putTagVisibles(TagVisibleTargetType type, Long target, List<Integer> tags, Integer ascription)
    {
        List<Integer> repeatTags = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        set.addAll(tags);
        repeatTags.addAll(set);
        tagVisibleDao.removeByTargetType(type, target);
        List<MktTagVisible> list = new ArrayList<>();
        for (Integer tag : repeatTags)
        {
            MktTagVisible tv = new MktTagVisible();
            tv.setPkey(type, target, tag);
            tv.setAscription(ascription);
            list.add(tv);
        }
        tagVisibleDao.addAll(list);
        return true;
    }
    
    public List<String> getMemberTagname(Integer member, Integer ascription)
    {
        List<String> res = new ArrayList<>();
        List<Integer> keys = memberTagDao.listTag(member, ascription);
        if (keys.isEmpty())
            return res;
        List<MktTag> list = tagDao.listKeys(keys);
        list.forEach(e -> res.add(e.getName()));
        return res;
    }
    
    public List<Integer> getGoodsTags(Long goods)
    {
        return tagVisibleDao.listTagKeys(TagVisibleTargetType.SPECIAL_GOODS, goods);
    }

    public String getMsdGoodsTagsName(Long goods)
    {
        StringBuffer sb = new StringBuffer();
        List<Integer> keys = tagVisibleDao.listTagKeys(TagVisibleTargetType.INTEGRAL_MSD_GOODS, goods);
        List<MktTag> list = tagDao.listKeys(keys);
        list.forEach(e -> 
        {
            sb.append(e.getName());
            sb.append(",");
        });
        if(sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    public List<String> listMemberOpenid(Integer ascription, List<Integer> tagList)
    {
        if (tagList == null || tagList.isEmpty())
        {
            return memberDao.listOpenidByAscription(ascription);
        }
        else
        {
            DbListResult result = memberDao.joinSelect()
                .as(MktMember.F.openid1)
                .join(MktMemberTag.class, MktMember.F.pkey, MktMemberTag.F.member)
                .in(MktMemberTag.F.tag, tagList)
                .endJoin()
                .exec(0, 10000);
            List<String> list = new ArrayList<>();
            for (DbData data : result.dataList())
            {
                list.add(data.getAsString(MktMember.F.openid1));
            }
            return list;
        }
    }

    public List<Integer> getCardTags(Long pkey)
    {
        return tagVisibleDao.listTagKeys(TagVisibleTargetType.CARD, pkey);
    }

    public List<Integer> getActivityTags(Long pkey)
    {
        return tagVisibleDao.listTagKeys(TagVisibleTargetType.ACTIVITY, pkey);
    }
}
