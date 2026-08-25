package cn.tofocus.lejia.dao.market;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktCommDraw;
import cn.tofocus.lejia.bean.enums.CommDrawStatus;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Component
public class MktCommDrawDao extends JpaSpecificationDelegate<Integer, MktCommDraw>
{
    public PageResult<MktCommDraw> queryCommDraw(int page, int pagesize, CommDrawStatus status, String orderNumber,
        Integer ascription)
    {
        SelectPageBuilder<Integer, MktCommDraw> builder =
            selectPage().page(page).pagesize(pagesize).eq("ascription", ascription).sort("createdTime", true);
        if (status != null) builder.eq("status", status);
        if (StringUtils.isNotBlank(orderNumber)) builder.like("orderNumber", orderNumber);
        return builder.exec();
    }
    
    // optM 0:根据日 来统计  1:根据月 来统计 
    public PageResult<MktCommDraw> aggregationCommDraw(String startTime, String endTime, int optM, Integer ascription)
    {
        int i = 10;
        if (optM == 1) i = 7;
        return aggregation().count("pkey", "pkey")
            .eq("ascription", ascription)
            .sum("comms", "comms")
            .groupby(substring(f("checkTime"), 1, i), "remark")
            .eq("status", CommDrawStatus.COMMDRAW_SENT)
            .between(substring(f("checkTime"), 1, i), startTime, endTime)
            .exec(MktCommDraw.class);
    }
}