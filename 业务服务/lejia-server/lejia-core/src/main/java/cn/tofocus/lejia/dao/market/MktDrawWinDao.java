package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktDrawWin;
import cn.tofocus.lejia.bean.enums.PrizeStatus;
import cn.tofocus.lejia.bean.enums.PrizeType;
import cn.tofocus.lejia.repository.market.MktDrawWinRepository;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Component
public class MktDrawWinDao extends JpaSpecificationDelegate<Integer, MktDrawWin>
{
    @Autowired
    private MktDrawWinRepository repository;
    
    public MktDrawWin insDrawWin(Integer memberPkey, Integer prize, PrizeType pType, String descp, Integer ascription)
    {
        MktDrawWin drawWin = new MktDrawWin();
        drawWin.setMember(memberPkey);
        drawWin.setPType(pType);
        drawWin.setStatus(PrizeStatus.NOT_ISSUED);
        drawWin.setPrize(prize);
        drawWin.setDescp(descp);
        drawWin.setAscription(ascription);
        if (pType.getIndex() == 0 || pType.getIndex() == 1) drawWin.setStatus(PrizeStatus.ISSUED);
        return add(drawWin);
    }
    
    public PageResult<MktDrawWin> queryDrawWin(int page, int pagesize, PrizeStatus status, Integer ascription)
    {
        SelectPageBuilder<Integer, MktDrawWin> builder = selectPage().page(page)
            .pagesize(pagesize)
            .eq("ascription", ascription)
            .notEq("pType", PrizeType.THANK_PRIZE)
            .sort("createdTime", true);
        if (status != null) builder.eq("status", status);
        return builder.exec();
    }
    
    public List<List<Object>> getDrawWin(Integer ascription)
    {
        return repository.getDrawWin(ascription);
    }
    
    // 统计一定时间内 中奖的物流数量
    public List<MktDrawWin> aggreLogisticeSum(String startTime, String endTime, Integer ascription)
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
        AggregationBuilder<Integer, MktDrawWin> builder =
            aggregation().eq("ascription", ascription).count("pkey", "pkey").groupby("logistics", "logistics");
        if (startTime != null) builder.between(substring(f("sendTime"), 1, 10), startTime, endTime);
        builder.isNotNull("logistics").isNotNull("express").isNotNull("sendTime").eq("status", PrizeStatus.ISSUED);
        PageResult<MktDrawWin> exec = builder.exec(MktDrawWin.class);
        return exec.getContent();
    }
}
