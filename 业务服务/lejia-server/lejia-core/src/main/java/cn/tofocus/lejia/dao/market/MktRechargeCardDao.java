package cn.tofocus.lejia.dao.market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tofocus.lejia.bean.enums.RechargeStatus;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.recharge.RechargeCardOnPage;
import cn.tofocus.lejia.bean.dto.market.recharge.RechargeCardSum;
import cn.tofocus.lejia.bean.entity.member.MktRechargeCard;
import cn.tofocus.lejia.bean.entity.member.MktRechargeCard.F;

@Component
public class MktRechargeCardDao extends JpaSpecificationDelegate<String, MktRechargeCard>
{
    public PageResult<RechargeCardOnPage> query(int page, int pagesize, List<String> types, String cardNumber,
        String status, String mobile, String createdStart, String createdEnd, String useStart, String useEnd,
        Integer ascription)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .in(F.type, types)
            .like(F.cardNumber, cardNumber)
            .eq(F.status, status)
            .like(F.mobile, mobile)
            .ge(F.createdTime, createdStart)
            .le(F.createdTime, createdEnd)
            .ge(F.useTime, useStart)
            .le(F.useTime, useEnd)
            .eq(F.ascription, ascription)
            .sort(F.createdTime)
            .execDto(RechargeCardOnPage.class);
    }
    
    public List<RechargeCardSum> querySum(List<String> types, String cardNumber, String status, String mobile,
        String createdStart, String createdEnd, String useStart, String useEnd, Integer ascription)
    {
        return this.aggregation()
            .in(F.type, types)
            .like(F.cardNumber, cardNumber)
            .eq(F.status, status)
            .like(F.mobile, mobile)
            .ge(F.createdTime, createdStart)
            .le(F.createdTime, createdEnd)
            .ge(F.useTime, useStart)
            .le(F.useTime, useEnd)
            .eq(F.ascription, ascription)
            .count(F.pkey, "num")
            .sum(F.cost, "sumCost")
            .groupby(F.status, "status")
            .execListDto(RechargeCardSum.class);
    }
    
    public List<MktRechargeCard> listPkey(List<String> keys, Integer ascription)
    {
        return this.select().in(F.pkey, keys).eq(F.ascription, ascription).exec();
    }
    
    public Map<String,Integer> getRepeatMap(Integer ascription)
    {
        Map<String,Integer> repeat = new HashMap<>();
        List<MktRechargeCard> list = this.select().eq(F.ascription, ascription).exec();
        list.forEach(e -> 
        {
            if(!repeat.containsKey(e.getCardNumber()))
                repeat.put(e.getCardNumber(), 1);
        });
        return repeat;
    }
    
    public MktRechargeCard byCardNumber(String cardNumber)
    {
        return this.selectOne().eq(F.cardNumber, cardNumber).exec();
    }

    public boolean existByTag(Integer tag, RechargeStatus status)
    {
        return this.selectOne().eq(F.tag, tag).eq(F.status, status).exec() != null;
    }
}
