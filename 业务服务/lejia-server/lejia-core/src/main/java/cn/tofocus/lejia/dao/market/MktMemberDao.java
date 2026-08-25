package cn.tofocus.lejia.dao.market;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMember.F;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.repository.market.MktMemberRepository;

@Component
@DataSourceWithFileUrl
public class MktMemberDao extends JpaSpecificationDelegate<Integer, MktMember>
{
    @Autowired
    private MktMemberRepository repository;
    
    public List<MktMember> getNotOrder(Integer member, Integer ascription)
    {
        return repository.getNotOrder(member, ascription);
    }
    
    public PageResult<MktMember> queryMember(int page, int pagesize, LevelType level, Boolean sex, Integer ascription)
    {
        SelectPageBuilder<Integer, MktMember> builder = selectPage().page(page)
            .pagesize(pagesize)
            .eq("enabled", true)
            .eq("ascription", ascription)
            .sort("createdTime", true);
        if (level != null) builder.eq("level", level);
        if (sex != null) builder.eq("sex", sex);
        return builder.exec();
    }
    
    public <T> PageResult<T> queryMember(int page, int pagesize, LevelType level, String name, String mobile,
        Integer ascription, String area, String remark, Date startCreatedTime, Date endCreatedTime,
        Date startLastConsumeTime, Date endLastConsumeTime, List<String> lastConsumeFarmers, 
        String source, List<Integer> keys, Class<T> clazz)
    {
        SelectPageBuilder<Integer, MktMember> builder = selectPage().page(page)
            .pagesize(pagesize)
            .eq(F.ascription, ascription)
            .in(F.pkey, keys)
            .like(F.area, area)
            .like(F.remark, remark)
            .ge(F.createdTime, startCreatedTime)
            .lt(F.createdTime, endCreatedTime)
            .ge(F.lastConsumeTime, startLastConsumeTime)
            .lt(F.lastConsumeTime, endLastConsumeTime)
            .in(F.lastConsumeFarmer, lastConsumeFarmers)
            .like(F.source, source)
            .sort(F.pkey, true);
        if (level != null) builder.eq(F.level, level);
        if (StringUtils.isNotBlank(name)) builder.like(F.name, name);
        if (StringUtils.isNotBlank(mobile)) builder.like(F.mobile, mobile);
        return builder.execDto(clazz);
    }
    
    public List<List<Object>> getAddMemberCount(@Param("startTime") String startTime, @Param("endTime") String endTime,
        Integer ascription)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        return repository.getAddMemberCount(startTime, endTime, ascription);
    }
    
    public Long countMember(String time, Integer ascription)
    {
        return aggregation().eq("ascription", ascription)
            .count("pkey")
            .between("createdTime", time + " 00:00:00", time + " 23:59:59")
            .execCount();
    }
    
    public Map<String, Integer> getOpenidKeyMap(List<String> openids)
    {
        Map<String, Integer> res = new HashMap<>();
        if (openids == null || openids.isEmpty()) return res;
        List<MktMember> list = this.select().in("openid1", openids.toArray()).exec();
        list.forEach(e -> {
            res.put(e.getOpenid1(), e.getPkey());
        });
        return res;
    }
    
    public MktMember byOpenid1(String openid)
    {
        return this.selectOne().eq("openid1", openid).exec();
    }
    
    public List<MktMember> byMobile(List<String> mobiles, Integer ascription)
    {
        return this.select().in("mobile", mobiles).eq("ascription", ascription).exec();
    }
    
    public void updLastConsume(Integer pkey, Date consumeTime, String consumeFarmer)
    {
        Map<String, Object> updateMap = Maps.newHashMapWithExpectedSize(2);
        updateMap.put(F.lastConsumeTime, consumeTime);
        updateMap.put(F.lastConsumeFarmer, consumeFarmer);
        this.select().strict(true).eq(F.pkey, pkey).update(updateMap);
    }
    
    public List<Integer> listPkeys(Integer ascription, String mobile)
    {
        return this.select().eq(F.ascription, ascription).like(F.mobile, mobile).execDto(F.pkey, Integer.class);
    }
    
    public MktMember getMobile(String mobile, Integer ascription)
    {
        return this.selectOne().eq("mobile", mobile).eq("ascription", ascription).exec();
    }
    
    public Map<String, Integer> map(Integer ascription)
    {
        List<MktMember> list = this.select().eq("ascription", ascription).exec();
        Map<String, Integer> map = new HashMap<>();
        list.forEach(e -> map.put(e.getMobile(), e.getPkey()));
        return map;
    }
    
    public List<String> listOpenidByAscription(Integer ascription)
    {
        return this.select().eq(F.ascription, ascription).isNotNull(F.openid1).execDto(F.openid1, String.class);
    }
}