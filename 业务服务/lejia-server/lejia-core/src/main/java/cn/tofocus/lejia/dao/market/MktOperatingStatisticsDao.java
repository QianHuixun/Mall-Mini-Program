package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktOperatingStatistics;
import cn.tofocus.lejia.bean.entity.market.MktOperatingStatistics.F;
import cn.tofocus.lejia.Constant;

@Component
public class MktOperatingStatisticsDao extends JpaSpecificationDelegate<Long, MktOperatingStatistics>
{
    public <T> PageResult<T> queryOperatingStatistics(int page, int pagesize, String farmer, String startDate,
        String endDate, Integer ascription, String marketPkey, Class<T> clazz)
    {
        SelectPageBuilder<Long, MktOperatingStatistics> builder = this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.farmer, farmer)
            .between(F.yesterTime, startDate, endDate)
            .eq(F.ascription, ascription);
        if (!(Constant.Operation + ascription).equals(marketPkey)) builder.eq(F.farmer, marketPkey);
        return builder.sort(F.yesterTime).execDto(clazz);
    }
    
    public <T> List<T> countOperatingStatistics(String farmer, String startDate, String endDate, Integer ascription,
        String marketPkey, Class<T> clazz)
    {
        AggregationBuilder<Long, MktOperatingStatistics> builder = this.aggregation()
            .eq(F.farmer, farmer)
            .between(F.yesterTime, startDate, endDate)
            .eq(F.ascription, ascription)
            .sum(F.accCount, F.accCount)
            .sum(F.orderCount, F.orderCount)
            .sum(F.revenueAmt, F.revenueAmt);
        if (!(Constant.Operation + ascription).equals(marketPkey)) builder.eq(F.farmer, marketPkey);
        return builder.execList(clazz);
    }
    
    public MktOperatingStatistics byFarmerAndAscription(String yesterTime, String farmer, Integer ascription)
    {
        return this.selectOne() 
        .eq(F.farmer, farmer)
        .eq(F.yesterTime, yesterTime)
        .eq(F.ascription, ascription)
        .exec();
    }
}