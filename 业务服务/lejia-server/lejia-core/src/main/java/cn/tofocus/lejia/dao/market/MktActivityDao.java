package cn.tofocus.lejia.dao.market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.ConditionBuilder;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktActivity.F;
import cn.tofocus.lejia.bean.enums.ActivityDistributeType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;

@Component
public class MktActivityDao extends JpaSpecificationDelegate<Integer, MktActivity>
{
    public <T> PageResult<T> query(int page, int pagesize, String name, Boolean enabled,
        ActivityDistributeType distributeType, List<String> farmers, Integer ascription, Class<T> clazz)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .in(F.farmer, farmers)
            .eq(F.ascription, ascription)
            .like(F.name, name)
            .eq(F.enabled, enabled)
            .eq(F.distributeType, distributeType)
            .sort(F.createdTime)
            .sort(F.pkey)
            .execDto(clazz);
    }
    
    public <T> List<T> list(String name, Boolean enabled, ActivityDistributeType distributeType, List<String> farmers,
        Integer ascription, Class<T> clazz)
    {
        return this.select()
            .in(F.farmer, farmers)
            .eq(F.ascription, ascription)
            .like(F.name, name)
            .eq(F.enabled, enabled)
            .eq(F.distributeType, distributeType)
            .sort(F.createdTime)
            .sort(F.pkey)
            .execDto(clazz);
    }
    
    public <T> T get(Integer pkey, Integer ascription, Class<T> clazz)
    {
        return this.selectOne().eq(F.pkey, pkey).eq(F.ascription, ascription).execDto(clazz);
    }
    
    public void enable(Integer pkey, Boolean enabled)
    {
        this.select().strict(true).eq(F.pkey, pkey).update(F.enabled, enabled);
    }
    
    public void updIssuedNum(Integer pkey, Integer issuedNum, Integer receiveNum)
    {
        Map<String, Object> values = new HashMap<>();
        values.put("issuedNum", issuedNum);
        values.put("receiveNum", receiveNum);
        this.select().strict(true).eq(F.pkey, pkey).update(values);
    }
    
    public void updUseNum(Integer pkey, Integer useNum)
    {
        this.select().strict(true).eq(F.pkey, pkey).update(F.useNum, useNum);
    }
    
    public <T> List<T> appList(String farmer, List<Long> keys, Integer ascription, Class<T> clazz)
    {
        SelectBuilder<Integer,MktActivity> builder = this.select()
        .eq(F.farmer, farmer)
        .eq(F.ascription, ascription)
        .eq(F.enabled, true)
        .eq(F.distributeType, ActivityDistributeType.memberWelfare);
        
     ConditionBuilder<SelectBuilder<Integer, MktActivity>> or = builder.or()
            .eq("visibleRange", MemberVisibleRange.ALL);
        if(!keys.isEmpty())
        {
            or = or
            .and()
                .eq("visibleRange", MemberVisibleRange.TAG)
                .in("pkey", keys)
            .close();
        }
        builder = or.close().done();
        
        return builder
            .sort(F.endTime, false)
            .sort(F.createdTime)
            .sort(F.pkey)
            .execDto(clazz);
    }
    
}
