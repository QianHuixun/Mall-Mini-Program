package cn.tofocus.lejia.dao.market;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.CardStatisticsInfo;
import cn.tofocus.lejia.bean.dto.v2.gift.MemberGiftV2OnList;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberGift;
import cn.tofocus.lejia.bean.entity.member.MktMemberGift.F;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.Constant;

@Component
public class MktMemberGiftDao extends JpaSpecificationDelegate<Integer, MktMemberGift>
{
    public void invalidUnusedByGift(Integer gift)
    {
        this.select().strict(true).eq(F.gift, gift).eq(F.status, CardStatus.UNUSED).update(F.invalid, true);
    }
    
    public void expiredLtEndDate(Date ltEndDate)
    {
        this.select()
            .strict(true)
            .eq(F.status, CardStatus.UNUSED)
            .lt(F.endDate, ltEndDate)
            .update(F.status, CardStatus.EXPIRED);
    }
    
    public MktMemberGift getByCardNumber(String cardNumber)
    {
        return this.selectOne().eq(F.cardNumber, cardNumber).exec();
    }
    
    public List<MemberGiftV2OnList> listMemberGiftV2(Integer memberPkey, CardStatus status)
    {
        Calendar cal = Calendar.getInstance();
        Date time = null;
        //非初始状态，仅搜索近3个月内的数据 -- 参考我的优惠券的逻辑
        if (!status.equals(CardStatus.UNUSED))
        {
            cal.add(Calendar.MONTH, -3);
            time = cal.getTime();
        }
        SelectBuilder<Integer, MktMemberGift> builder = this.select()
            .eq(MktMemberGift.F.member, memberPkey)
            .gt(MktMemberGift.F.createdTime, time)
            .sort(MktMemberGift.F.pkey);
        if (status.equals(CardStatus.EXPIRED))
        {
            builder.or() //过期或失效的
                .eq(MktMemberGift.F.status, status)
                .eq(MktMemberGift.F.invalid, true)
                .close()
                .done();
        }
        else
        {
            builder.eq(MktMemberGift.F.status, status).eq(MktMemberGift.F.invalid, false);
        }
        return builder.execDto(MemberGiftV2OnList.class);
    }
    
    public Integer countByMember(Integer memberPkey)
    {
        long count = this.aggregation()
            .eq(F.member, memberPkey)
            .eq(F.status, CardStatus.UNUSED)
            .eq(F.invalid, false)
            .execCount();
        return (int)count;
    }
    
    public <T> PageResult<T> query(int page, int pagesize, String userFarmer, Date startUserTime, Date endUserTime, 
        Date st, Date et,  List<Integer> members,
        List<Integer> gifts, String farmer, CardStatus status, Boolean invalid, Integer ascription, Class<T> clazz)
    {
        SelectPageBuilder<Integer, MktMemberGift> builder = this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.ascription, ascription)
            .eq(F.status, status)
            .eq(F.invalid, invalid)
            .eq(F.userFarmer, userFarmer)
            .ge(F.userTime, startUserTime)
            .lt(F.userTime, endUserTime)
            .ge(F.createdTime, st)
            .lt(F.createdTime, et)
            .in(F.member, members)
            .in(F.gift, gifts);
        if (!(Constant.Operation + ascription).equals(farmer)) builder.eq(MktMemberCard.F.farmer, farmer);
        return builder.sort(F.createdTime, true).sort(F.userTime, true).execDto(clazz);
    }
    
    public List<CardStatisticsInfo> sum(String userFarmer, Date startUserTime, Date endUserTime, 
        Date st, Date et,  List<Integer> members,List<Integer> gifts, String farmer, CardStatus status, Boolean invalid, 
        Integer ascription)
    {
        AggregationBuilder<Integer,MktMemberGift> builder = this.aggregation()
            .eq(F.ascription, ascription)
            .eq(F.status, status)
            .eq(F.invalid, invalid)
            .eq(F.userFarmer, userFarmer)
            .ge(F.userTime, startUserTime)
            .lt(F.userTime, endUserTime)
            .ge(F.createdTime, st)
            .lt(F.createdTime, et)
            .in(F.member, members)
            .in(F.gift, gifts);
        if (!(Constant.Operation + ascription).equals(farmer)) builder.eq(MktMemberCard.F.farmer, farmer);
        builder 
        .groupby("invalid")
        .groupby("status")
        .count("pkey", "sum");
        return builder.execListDto(CardStatisticsInfo.class);
    }
    
    public long countByActivity(Integer activity, Integer member, CardStatus status, Date startUserTime,
        Date endUserTime)
    {
        return this.aggregation()
            .eq(F.activity, activity)
            .eq(F.member, member)
            .eq(F.status, status)
            .ge(F.userTime, startUserTime)
            .lt(F.userTime, endUserTime)
            .execCount();
    }
    
    public Integer getMemberGiftMarketNum(Integer member, String market)
    {
        long count =
            this.aggregation().eq("member", member).eq("farmer", market)
            .eq("status", CardStatus.UNUSED)
            .eq("invalid", false)
            .execCount();
        return (int)count;
    }
    
    public List<MktMemberGift> listMemberGiftMarket(Integer member, String market)
    {
        return this.select()
            .eq("member", member)
            .eq("farmer", market)
            .eq("invalid", false)
            .eq("status", CardStatus.UNUSED)
            .sort("createdTime")
            .exec();
    }
}