package cn.tofocus.lejia.dao.market;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.member.MktMemberActivity;
import cn.tofocus.lejia.bean.entity.member.MktMemberActivity.F;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;

@Component
public class MktMemberActivityDao extends JpaSpecificationDelegate<Integer, MktMemberActivity>
{
    public <T> PageResult<T> query(int page, int pagesize, List<Integer> members, Date startDate, Date endDate,
        Integer activity, OrderStatus status, String farmer, Integer ascription, Class<T> clazz)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.farmer, farmer)
            .eq(F.ascription, ascription)
            .in(F.member, members)
            .ge(F.payTime, startDate)
            .lt(F.payTime, endDate)
            .eq(F.activity, activity)
            .eq(F.status, status)
            .sort(F.payTime)
            .sort(F.pkey)
            .execDto(clazz);
    }
    
    public long count(Integer activity, Integer member, Date startDate, Date endDate, OrderStatus status)
    {
        return this.aggregation()
            .eq(F.activity, activity)
            .eq(F.member, member)
            .ge(F.payTime, startDate)
            .lt(F.payTime, endDate)
            .eq(F.status, status)
            .execCount();
    }
    
    public MktMemberActivity byCode(String code)
    {
        return this.selectOne().eq(F.code, code).exec();
    }
    
    public List<MktMemberActivity> querySettlementBill(String farmer, String code,
        String startDate, String endDate, SettlementType settlementType, List<String> farmers,Integer ascription)
    {
        return this.select()
            .like(F.code, code)
            .eq(F.farmer, farmer)
            .in(F.farmer, farmers)
            .eq(F.ascription, ascription)
            .eq(F.settlementType, settlementType)
            .eq(F.status, OrderStatus.CONFIRM_ORDER)
            .gt(F.amt, 0)
            .iF(startDate != null && endDate != null)
                .between(F.createdTime, startDate + " 00:00:00", endDate + " 23:59:59")
            .endIf()
            .sort(F.createdTime)
            .exec();
    }
}
