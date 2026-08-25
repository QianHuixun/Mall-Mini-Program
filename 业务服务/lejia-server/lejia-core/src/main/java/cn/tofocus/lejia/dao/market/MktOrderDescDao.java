package cn.tofocus.lejia.dao.market;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.repository.market.MktOrderDescRepository;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Component
public class MktOrderDescDao extends JpaSpecificationDelegate<Integer, MktOrderDesc>
{
    @Autowired
    private MktOrderDescRepository repository;
    
    public PageResult<MktOrderDesc> aggreLogisticeCount(String startTime, String endTime, Integer ascription)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        AggregationBuilder<Integer, MktOrderDesc> builder = aggregation().page(0)
            .pagesize(1000000)
            .eq("ascription", ascription)
            .count("pkey", "pkey")
            .groupby("logistics", "logistics")
            .isNotNull("logistics");
        if (StringUtils.isNotBlank(startTime)) builder.between(substring(f("fhTime"), 1, 10), startTime, endTime);
        return builder.exec(MktOrderDesc.class);
    }
    
    public List<Map<String, Object>> aggreLogisticeSum(String startTime, String endTime, Integer ascription)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        List<Map<String, Object>> result = new ArrayList<>();
        List<List<Object>> sum = repository.aggreLogisticeSum(startTime, endTime, ascription);
        for (List<Object> o : sum)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0));
            map.put("postageSum", o.get(1));
            result.add(map);
        }
        return result;
    }
    
    public Map<Integer, MktOrderDesc> mapKey(List<Integer> orderKeys)
    {
        Map<Integer, MktOrderDesc> map = new HashMap<>();
        if (orderKeys == null || orderKeys.isEmpty()) return map;
        List<MktOrderDesc> list = this.select().in("pkey", orderKeys.toArray()).exec();
        for (MktOrderDesc od : list)
        {
            map.put(od.getPkey(), od);
        }
        return map;
    }
    
    public Boolean checkKdCode(String code)
    {
        long count = this.aggregation().eq("kdCode", code).execCount();
        return count > 0;
    }
}