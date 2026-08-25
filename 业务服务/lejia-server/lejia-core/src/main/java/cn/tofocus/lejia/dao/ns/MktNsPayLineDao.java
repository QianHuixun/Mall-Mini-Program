package cn.tofocus.lejia.dao.ns;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.ConditionBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.PayLineDTO;
import cn.tofocus.lejia.bean.entity.ns.MktNsPayLine;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class MktNsPayLineDao extends JpaSpecificationDelegate<Integer, MktNsPayLine>
{
    public List<String> listCode(String startTime, String endTime)
    {
        List<MktNsPayLine> list =
            this.select().between("createdTime", DateUtil.atStartOfDay(startTime), DateUtil.atEndOfDay(endTime)).exec();
        return list.stream().map(MktNsPayLine::getOutTradeNo).collect(Collectors.toList());
    }
    
    public PageResult<PayLineDTO> queryPayLines(int page, int pagesize, Boolean buy, Boolean recharge, Boolean member,
        String startTime, String endTime)
    {
        SelectPageBuilder<Integer, MktNsPayLine> builder =
            selectPage().page(page).pagesize(pagesize).sort("createdTime", true);
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2050-01-01";
            builder.between("createdTime", startTime, endTime);
        }
        List<String> orderNumList = new ArrayList<>();
        if (Boolean.TRUE.equals(buy)) orderNumList.add("91");
        if (Boolean.TRUE.equals(member)) orderNumList.add("92");
        if (Boolean.TRUE.equals(recharge)) orderNumList.add("93");
        if (!orderNumList.isEmpty())
            builder.in(substring(f("outTradeNo"), 1, 2), orderNumList.toArray());
        else
            builder.isNull("outTradeNo");
        return builder.execDto(PayLineDTO.class);
    }
    
    // optM 0:根据日 来统计  1:根据月 来统计 
    public PageResult<MktNsPayLine> aggregationPay(Boolean buy, Boolean recharge, Boolean member, String startTime,
        String endTime, int optM)
    {
        int i = 10;
        if (optM == 1) i = 8;
        AggregationBuilder<Integer, MktNsPayLine> builder = aggregation();
        builder.count("pkey", "pkey")
            .sum("cashFee", "cashFee")
            .groupby(substring(f("createdTime"), 1, i), "createdTime");
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime)) endTime = "2050-01-01";
            builder.between(substring(f("createdTime"), 1, i), startTime, endTime);
        }
        ConditionBuilder<AggregationBuilder<Integer, MktNsPayLine>> or = builder.or();
        if (Boolean.TRUE.equals(buy)) or.eq(substring(f("outTradeNo"), 1, 2), "91");
        if (Boolean.TRUE.equals(recharge)) or.eq(substring(f("outTradeNo"), 1, 2), "92");
        if (Boolean.TRUE.equals(member)) or.eq(substring(f("outTradeNo"), 1, 2), "93");
        return or.done().exec(MktNsPayLine.class);
    }
    
    // optM 0:根据日 来统计  1:根据月 来统计 
    public PageResult<MktNsPayLine> aggregationPay(Boolean buy, Boolean recharge, Boolean member, String startTime,
        String endTime, int optM, List<String> orderNumList)
    {
        int i = 10;
        if (optM == 1) i = 8;
        AggregationBuilder<Integer, MktNsPayLine> builder = aggregation();
        builder.count("pkey", "pkey")
            .sum("cashFee", "cashFee")
            .groupby(substring(f("createdTime"), 1, i), "createdTime");
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime)) endTime = "2050-01-01";
            builder.between(substring(f("createdTime"), 1, i), startTime, endTime);
        }
        if (!orderNumList.isEmpty()) builder.in("outTradeNo", orderNumList.toArray());
        ConditionBuilder<AggregationBuilder<Integer, MktNsPayLine>> or = builder.or();
        if (Boolean.TRUE.equals(buy)) or.eq(substring(f("outTradeNo"), 1, 2), "91");
        if (Boolean.TRUE.equals(recharge)) or.eq(substring(f("outTradeNo"), 1, 2), "92");
        if (Boolean.TRUE.equals(member)) or.eq(substring(f("outTradeNo"), 1, 2), "93");
        return or.done().exec(MktNsPayLine.class);
    }
}