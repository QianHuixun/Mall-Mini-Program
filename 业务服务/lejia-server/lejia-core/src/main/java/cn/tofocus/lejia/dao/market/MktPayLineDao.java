package cn.tofocus.lejia.dao.market;

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
import cn.tofocus.lejia.bean.entity.market.MktPayLine;
import cn.tofocus.lejia.utils.DateUtil;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Component
public class MktPayLineDao extends JpaSpecificationDelegate<Integer,MktPayLine>
{
	
	public PageResult<MktPayLine> queryPayLines(int page, int pagesize, Boolean buy, Boolean recharge,
			Boolean member, String startTime, String endTime, Integer ascription)
	{
		SelectPageBuilder<Integer,MktPayLine> builder = selectPage()
				.page(page).pagesize(pagesize)
				.eq("ascription", ascription)
				.sort("createdTime", true);
		if(StringUtils.isNotBlank(startTime))
		{
			startTime = startTime.replace("-", "");
			if(StringUtils.isBlank(endTime))
				endTime = "20300101";
			else
				endTime = endTime.replace("-", "");
			builder.between(substring(f("payTime"), 1, 8), startTime, endTime);
		}
		List<String> orderNumList = new ArrayList<>();
		if(buy)
			orderNumList.add("91");
		if(member)
			orderNumList.add("92");
		if(recharge)
			orderNumList.add("93");
		
		if(orderNumList.size() > 0)
			builder.in(substring(f("orderNumber"), 1, 2), orderNumList.toArray());
		else
			builder.isNull("orderNumber");
		
		PageResult<MktPayLine> result = builder.exec();
		return result;
	}
	
	// optM 0:根据日 来统计  1:根据月 来统计 
	public PageResult<MktPayLine> aggregationPay(Boolean buy, Boolean recharge,
			Boolean member, String startTime, String endTime, int optM, Integer ascription)
	{
		int i = 8;
		if(optM == 1)
			i = 6;
		AggregationBuilder<Integer,MktPayLine> builder = aggregation().eq("ascription", ascription);
		builder.count("pkey","pkey")
		.sum("amt", "amt")
		.groupby(substring(f("payTime"), 1, i), "payTime");
		if(StringUtils.isNotBlank(startTime))
		{
			if(StringUtils.isBlank(endTime))
				endTime = "20300101";
			builder.between(substring(f("payTime"), 1, i), startTime, endTime);
		}
		ConditionBuilder<AggregationBuilder<Integer, MktPayLine>> or = builder.or();
		if(buy)
			or.eq(substring(f("orderNumber"), 1, 2), "91");
		if(recharge)
			or.eq(substring(f("orderNumber"), 1, 2), "92");
		if(member)
			or.eq(substring(f("orderNumber"), 1, 2), "93");
		PageResult<MktPayLine> result = or.done().exec(MktPayLine.class);
		return result;
	}
	
	// optM 0:根据日 来统计  1:根据月 来统计 
	public PageResult<MktPayLine> aggregationPay(Boolean buy, Boolean recharge,
			Boolean member, String startTime, String endTime, int optM, List<String> orderNumList, Integer ascription)
	{
		int i = 8;
		if(optM == 1)
			i = 6;
		AggregationBuilder<Integer,MktPayLine> builder = aggregation().eq("ascription", ascription);
		builder.count("pkey","pkey")
		.sum("amt", "amt")
		.groupby(substring(f("payTime"), 1, i), "payTime");
		if(StringUtils.isNotBlank(startTime))
		{
			if(StringUtils.isBlank(endTime))
				endTime = "20300101";
			builder.between(substring(f("payTime"), 1, i), startTime, endTime);
		}
		if(orderNumList.size() > 0)
			builder.in("orderNumber", orderNumList.toArray());
		ConditionBuilder<AggregationBuilder<Integer, MktPayLine>> or = builder.or();
		if(buy)
			or.eq(substring(f("orderNumber"), 1, 2), "91");
		if(recharge)
			or.eq(substring(f("orderNumber"), 1, 2), "92");
		if(member)
			or.eq(substring(f("orderNumber"), 1, 2), "93");
		PageResult<MktPayLine> result = or.done().exec(MktPayLine.class);
		return result;
	}
	
    
    public List<String> listCode(String startTime, String endTime)
    {
        List<MktPayLine> list = this.select()
        .between("createdTime", DateUtil.atStartOfDay(startTime), DateUtil.atEndOfDay(endTime))
        .exec();
        return list.stream().map(MktPayLine::getOrderNumber).collect(Collectors.toList());
    }
    
    public MktPayLine getOrderNumber(String orderNumber)
    {
        return this.selectOne().eq("orderNumber", orderNumber).exec();
    }
    
    public MktPayLine getCode(String code)
    {
        return this.selectOne().eq("code", code).exec();
    }
}