package cn.tofocus.lejia.dao.market;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.ConditionBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.PayLineDTO;
import cn.tofocus.lejia.bean.entity.market.MktZxPayLine;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.utils.DateUtil;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Component
public class MktZxPayLineDao extends JpaSpecificationDelegate<Integer, MktZxPayLine>
{
    
    public PageResult<PayLineDTO> queryPayLines(int page, int pagesize, Boolean buy, Boolean recharge, Boolean member,
        String startTime, String endTime)
    {
        SelectPageBuilder<Integer, MktZxPayLine> builder =
            selectPage().page(page).pagesize(pagesize).sort("createdTime", true);
        if (StringUtils.isNotBlank(startTime))
        {
            startTime = startTime.replace("-", "");
            if (StringUtils.isBlank(endTime))
                endTime = "20300101";
            else
                endTime = endTime.replace("-", "");
            builder.between(substring(f("time_end"), 1, 8), startTime, endTime);
        }
        List<String> orderNumList = new ArrayList<>();
        if (buy) orderNumList.add("91");
        if (member) orderNumList.add("92");
        if (recharge) orderNumList.add("93");
        
        if (orderNumList.size() > 0)
            builder.in(substring(f("out_trade_no"), 1, 2), orderNumList.toArray());
        else
            builder.isNull("out_trade_no");
        
        PageResult<MktZxPayLine> pageResult = builder.exec();
        List<PayLineDTO> pldContent = new ArrayList<>();
        for (MktZxPayLine l : pageResult.getContent())
        {
            PayLineDTO dto = new PayLineDTO();
            dto.setPayTime(l.getTime_end());
            dto.setAmt(l.getTotal_fee() + "");
            dto.setPayType(PayType.ZXYW_WEIXIN);
            dto.setCode(l.getTransaction_id());
            dto.setOrderNumber(l.getOut_trade_no());
            dto.setStatus(l.getReturn_code());
            pldContent.add(dto);
        }
        PageResult<PayLineDTO> res = BeanUtil.beanPageFrom(PayLineDTO.class, pageResult);
        res.setContent(pldContent);
        return res;
    }
    
    // optM 0:根据日 来统计  1:根据月 来统计 
    public PageResult<MktZxPayLine> aggregationPay(Boolean buy, Boolean recharge, Boolean member, String startTime,
        String endTime, int optM, List<String> orderNumList)
    {
        int i = 8;
        if (optM == 1) i = 6;
        AggregationBuilder<Integer, MktZxPayLine> builder = aggregation();
        builder.count("pkey", "pkey")
            .sum("total_fee", "total_fee")
            .groupby(substring(f("time_end"), 1, i), "time_end");
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime)) endTime = "20300101";
            builder.between(substring(f("time_end"), 1, i), startTime, endTime);
        }
        if (orderNumList.size() > 0) builder.in("out_trade_no", orderNumList.toArray());
        ConditionBuilder<AggregationBuilder<Integer, MktZxPayLine>> or = builder.or();
        if (buy) or.eq(substring(f("out_trade_no"), 1, 2), "91");
        if (recharge) or.eq(substring(f("out_trade_no"), 1, 2), "92");
        if (member) or.eq(substring(f("out_trade_no"), 1, 2), "93");
        PageResult<MktZxPayLine> result = or.done().exec(MktZxPayLine.class);
        return result;
    }
    
    // optM 0:根据日 来统计  1:根据月 来统计 
    public PageResult<MktZxPayLine> aggregationPay(Boolean buy, Boolean recharge, Boolean member, String startTime,
        String endTime, int optM)
    {
        int i = 8;
        if (optM == 1) i = 6;
        AggregationBuilder<Integer, MktZxPayLine> builder = aggregation();
        builder.count("pkey", "pkey")
            .sum("total_fee", "total_fee")
            .groupby(substring(f("time_end"), 1, i), "time_end");
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime)) endTime = "20300101";
            builder.between(substring(f("time_end"), 1, i), startTime, endTime);
        }
        ConditionBuilder<AggregationBuilder<Integer, MktZxPayLine>> or = builder.or();
        if (buy) or.eq(substring(f("out_trade_no"), 1, 2), "91");
        if (recharge) or.eq(substring(f("out_trade_no"), 1, 2), "92");
        if (member) or.eq(substring(f("out_trade_no"), 1, 2), "93");
        PageResult<MktZxPayLine> result = or.done().exec(MktZxPayLine.class);
        return result;
    }
    
    public List<String> listCode(String startTime, String endTime)
    {
        List<MktZxPayLine> list = this.select()
        .between("createdTime", DateUtil.atStartOfDay(startTime), DateUtil.atEndOfDay(endTime))
        .exec();
        return list.stream().map(MktZxPayLine::getOut_trade_no).collect(Collectors.toList());
    }
    
    
}