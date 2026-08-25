package cn.tofocus.lejia.dao.market;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.data.IndexYFDTO;
import cn.tofocus.lejia.bean.entity.market.MktAccessLog;
import cn.tofocus.lejia.repository.market.MktAccessLogRepository;

@Component
public class MktAccessLogDao extends JpaSpecificationDelegate<Integer, MktAccessLog>
{
    @Autowired
    private MktAccessLogRepository repository;
    
    public List<List<Object>> mallAccessNum(String startTime, String endTime, Integer ascription)
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
        return repository.mallAccessNum(startTime, endTime, ascription);
    }
    
    public Long countAccessNum(String date, Integer ascription, String farmer)
    {
        return this.aggregation()
            .eq("ascription", ascription)
            .eq("farmer", farmer)
            .between("accessTime", DateUtil.atStartOfDay(date), DateUtil.atEndOfDay(date))
            .execCount("member");
    }
    
    public List<IndexYFDTO> yesterdayData(String time, int i, Integer ascription)
    {
        AggregationBuilder<Integer, MktAccessLog> builder = aggregation().eq("ascription", ascription)
            .count("pkey", "accessNum")
            .between("accessTime", time + " 00:00:00", time + " 23:59:59");
        if (i == 1) return builder.groupby("farmer", "farmer").exec(IndexYFDTO.class).getContent();
        if (i == 2) return builder.groupby("company", "company").exec(IndexYFDTO.class).getContent();
        return builder.exec(IndexYFDTO.class).getContent();
    }
    
    public List<Integer> getMonthMember(Boolean flag)
    {
        List<Integer> res = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Map<String, Long> map = this.aggregation().ge("accessTime", cal.getTime()).execGroupByCount("member", "pkey");
        for (Map.Entry<String, Long> entry : map.entrySet())
        {
            String key = entry.getKey();
            Long value = entry.getValue();
            if (flag)
            {
                if (value >= 4) res.add(Integer.valueOf(key));
            }
            else
            {
                if (value < 4) res.add(Integer.valueOf(key));
            }
        }
        
        return res;
    }
    
}