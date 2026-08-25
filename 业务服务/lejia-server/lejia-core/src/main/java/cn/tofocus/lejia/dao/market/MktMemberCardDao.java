package cn.tofocus.lejia.dao.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.CardStatisticsInfo;
import cn.tofocus.lejia.bean.dto.market.MktMemberCardDTO;
import cn.tofocus.lejia.bean.dto.market.MktMemberCardOnList;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardV2OnList;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard.F;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.repository.market.MktMemberCardRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MktMemberCardDao extends JpaSpecificationDelegate<Integer, MktMemberCard>
{
    @Autowired
    private MktMemberCardRepository repository;
    
    public Map<String, Object> getCardStatusCount(Integer id, Integer status)
    {
        return repository.getCardStatusCount(id, status);
    }
    
    public Map<String, Object> getCardCount(Integer id)
    {
        return repository.getCardCount(id);
    }
    
    public Integer getMemberCardCount(Integer memberPkey)
    {
        long count = this.aggregation().eq("status", CardStatus.UNUSED).eq("member", memberPkey).eq("invalid", false).execCount();
        return (int)count;
    }
    
    public PageResult<MktMemberCardOnList> queryUseCard(int page, int pagesize, String userFarmer, String startTime,
        String endTime, String st, String et, List<Integer> members, List<Integer> cards, String marketPkey, CardStatus status, 
        Boolean invalid, Integer ascription)
    {
        SelectPageBuilder<Integer, MktMemberCard> builder = selectPage().page(page)
            .pagesize(pagesize)
            .eq(F.ascription, ascription)
            .eq(F.status, status)
            .eq(F.invalid, invalid)
            .in(F.member, members)
            .in(F.card, cards)
            .sort(F.createdTime, true)
            .sort(F.userTime, true);
        if (!(Constant.Operation + ascription).equals(marketPkey)) builder.eq(F.farmer, marketPkey);
        if (StringUtils.isNotBlank(userFarmer)) builder.eq(F.userFarmer, userFarmer);
        if (StringUtils.isNotBlank(startTime)) builder.ge(F.userTime, startTime);
        if (StringUtils.isNotBlank(endTime))
        {
            try
            {
                Date date = DateUtil.formatDateStr(endTime);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
                String formatDate = DateUtil.formatDate(date);
                log.info("formatDate: {}", formatDate);
                builder.le(F.userTime, formatDate);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotBlank(st)) builder.ge(F.createdTime, st);
        if (StringUtils.isNotBlank(et))
        {
            try
            {
                Date date = DateUtil.formatDateStr(et);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
                String formatDate = DateUtil.formatDate(date);
                log.info("formatDate: {}", formatDate);
                builder.le(F.createdTime, formatDate);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return builder.execDto(MktMemberCardOnList.class);
    }
    
    public List<CardStatisticsInfo> queryUseSumCard(String userFarmer, String startTime,
        String endTime, String st, String et, List<Integer> members, List<Integer> cards, String marketPkey, CardStatus status, 
        Boolean invalid, Integer ascription)
    {
        AggregationBuilder<Integer,MktMemberCard> builder = this.aggregation()
            .eq(F.ascription, ascription)
            .eq(F.status, status)
            .eq(F.invalid, invalid)
            .in(F.member, members)
            .in(F.card, cards);
        if (!(Constant.Operation + ascription).equals(marketPkey)) builder.eq(F.farmer, marketPkey);
        if (StringUtils.isNotBlank(userFarmer)) builder.eq(F.userFarmer, userFarmer);
        if (StringUtils.isNotBlank(startTime)) builder.ge(F.userTime, startTime);
        if (StringUtils.isNotBlank(endTime))
        {
            try
            {
                Date date = DateUtil.formatDateStr(endTime);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
                String formatDate = DateUtil.formatDate(date);
                log.info("formatDate: {}", formatDate);
                builder.le(F.userTime, formatDate);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotBlank(st)) builder.ge(F.createdTime, st);
        if (StringUtils.isNotBlank(et))
        {
            try
            {
                Date date = DateUtil.formatDateStr(et);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
                String formatDate = DateUtil.formatDate(date);
                log.info("formatDate: {}", formatDate);
                builder.le(F.createdTime, formatDate);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return builder
        .groupby("invalid")
        .groupby("status")
        .count("pkey", "sum")
        .execListDto(CardStatisticsInfo.class);
    }
    
    /**
     * mkt_member_card列表
     * @param member   mkt_member的pkey
     * @param farmer   mkt_farmer的pkey
     * @return          结果
     */
    public List<MktMemberCard> listMemberCard(Integer member, String farmer, List<Integer> notInCards,
        List<Integer> inCards)
    {
        return this.select()
            .eq("member", member)
            .eq("status", CardStatus.UNUSED)
            .eq("farmer", farmer)
            .notIn("card", notInCards)
            .in("card", inCards)
            .eq("invalid", false)
            .or()
                .le("startDate", new Date())
                .isNull("startDate")
            .close()
            .done()
            .sort("cost", true)
            .sort("endDate", false)
            .exec();
    }
    
    public List<MktMemberCard> listMemberCardV2(Integer member, CardCouponType type, String farmer)
    {
        return this.select()
            .eq("member", member)
            .eq("status", CardStatus.UNUSED)
            .eq("invalid", false)
            .eq("type", type)
            .or()
                .eq("userFarmer", farmer)
                .isNull("userFarmer")
            .close()
            .done()
            .or()
                .le("startDate", new Date())
                .isNull("startDate")
            .close()
            .done()
            .sort("cost", true)
            .sort("endDate", false)
            .exec();
    }

    public List<MktMemberCard> listMemberCardV2(Integer member, CardCouponType type)
    {
        return this.select()
            .eq("member", member)
            .eq("status", CardStatus.UNUSED)
            .eq("invalid", false)
            .eq("type", type)
            .or()
                .le("startDate", new Date())
                .isNull("startDate")
            .close()
            .done()
            .sort("cost", true)
            .sort("endDate", false)
            .exec();
    }
    
    // 获取最优惠的优惠券主键
    public MktMemberCard optimalCard(Integer member, String farmer, Integer userType, 
        Integer userGoods, BigDecimal cost, List<Integer> notInCards, List<Integer> inCards)
    {
        String time = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        return this.selectOne()
        .and()
            .eq("member", member)
            .eq("status", CardStatus.UNUSED)
            .eq("invalid", false)
            .le("limitCost", cost)
            .ge("endDate", time)
            .notIn("card", notInCards)
            .in("card", inCards)
            .and()
                .or()
                .eq("userType", userType)
                .isNull("userType")
                .close()
            .close()
            .and()
                .or()
                .eq("userGoods", userGoods)
                .isNull("userGoods")
                .close()
            .close()
            .and()
                .or()
                .le("startDate", time)
                .isNull("startDate")
                .close()
            .close()
            .and()
                .or()
                .isNull("userFarmer")
                .eq("userFarmer", farmer)
                .close()
            .close()
        .close()
        .done()
        .sort("cost")
        .exec();
    }
    
    // 获取按最优惠排序的优惠券
    public List<MktMemberCard> optimalCards(Integer member, String farmer, Integer userType, Integer userGoods,
        BigDecimal cost, List<Integer> notInCards, List<Integer> inCards)
    {
        String time = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        return this.select()
            .and()
                .eq("member", member)
                .eq("status", CardStatus.UNUSED)
                .eq("invalid", false)
                .le("limitCost", cost)
                .ge("endDate", time)
                .notIn("card", notInCards)
                .in("card", inCards)
                .and()
                    .or()
                        .eq("userType", userType)
                        .isNull("userType")
                    .close()
                .close()
                .and()
                    .or()
                        .eq("userGoods", userGoods)
                        .isNull("userGoods")
                    .close()
                .close()
                .and()
                    .or()
                        .le("startDate", time)
                        .isNull("startDate")
                    .close()
                .close()
                .and()
                    .or()
                        .isNull("userFarmer")
                        .eq("userFarmer", farmer)
                    .close()
                .close()
            .close()
            .done()
            .sort("cost")
            .exec();
    }
    
    public PageResult<MktMemberCardDTO> queryMemberCardRecord(Integer memberPkey, int page, int pagesize)
    {
        PageResult<MktMemberCardDTO> result = new PageResult<>();
        List<MktMemberCardDTO> content = new ArrayList<MktMemberCardDTO>();
        List<List<Object>> list = repository.queryMemberCardRecord(memberPkey, page * pagesize, pagesize);
        for (List<Object> o : list)
        {
            MktMemberCardDTO dto = new MktMemberCardDTO();
            dto.setCode(o.get(0).toString());
            dto.setCardName(o.get(1).toString());
            dto.setUserFarmerName(o.get(2).toString());
            dto.setUserTime(DateUtil.formatDate((Date)o.get(3), "yyyy-MM-dd HH:mm:ss"));
            content.add(dto);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        result.setTotalElements(repository.queryMemberCardRecordCount(memberPkey));
        result.setContent(content);
        return result;
    }
    
    public List<List<Object>> queryFarmerCardCount(String marketPkey, String companyPkey, String startTime,
        String endTime, int page, int pagesize)
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
        return repository.queryFarmerCardCount(marketPkey, companyPkey, startTime, endTime, page * pagesize, pagesize);
    }
    
    public List<MktMemberCard> queryFarmerCardCount2(List<String> marketPkeys, String startTime,
        String endTime, Integer ascription)
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
        AggregationBuilder<Integer, MktMemberCard> builder = this.aggregation().eq("ascription", ascription);
        if (!marketPkeys.isEmpty()) builder.in("userFarmer", marketPkeys.toArray());
        PageResult<MktMemberCard> pageResult = builder.between("userTime", startTime, endTime)
            .isNotNull("userTime")
            .eq("status", CardStatus.USED)
            .groupby("userFarmer", "farmer")
            .count("pkey", "pkey")
            .sum("cost", "cost")
            .execDto(MktMemberCard.class);
        return pageResult.getContent();
    }
    
    public List<MemberCardV2OnList> listMemberCardV2(Integer memberPkey,CardStatus status)
    {
        Calendar cal = Calendar.getInstance();
        Date time = null;
        if(!status.equals(CardStatus.UNUSED))
        {
            cal.add(Calendar.MONTH, -3);
            time = cal.getTime();
        }
        SelectBuilder<Integer,MktMemberCard> builder = this.select()
            .eq("member", memberPkey)
            .gt("createdTime", time)
            .sort("pkey");
        if(status.equals(CardStatus.EXPIRED))
        {
            builder.or()
            .eq("status", status)
            .eq("invalid", true)
            .close().done();
        }
        else
        {
            builder.eq("status", status).eq("invalid", false);
        }
        return builder.execDto(MemberCardV2OnList.class);
    }
    
    public Boolean checkMemberCardReceive(Integer member, Integer card)
    {
        long count = this.aggregation().eq("member", member).eq("card", card).execCount();
        return count > 0;
    }
 
    public Integer getMemberCardMarketNum(Integer member,String market, List<Integer> cards)
    {
        long count = this.aggregation().eq("member", member).eq("farmer", market)
        .eq("status", CardStatus.UNUSED)
        .eq("invalid", false)
        .notIn("card", cards)
        .execCount();
        return (int)count;
    }
    
    public PageResult<MktMemberCard> queryMemberCardMarket(Integer page, Integer pagesize, Integer member, 
        String market, CardStatus status, List<Integer> cards)
    {
        return this.selectPage().page(page).pagesize(pagesize)
            .eq("member", member)
            .eq("farmer", market)
            .notIn("card", cards)
        .eq("status", status)
        .eq("invalid", false)
        .sort("cost")
        .exec();
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
    
    public MktMemberCard getByCardNumber(String cardNumber)
    {
        return this.selectOne().eq(F.cardNumber, cardNumber).exec();
    }
}
