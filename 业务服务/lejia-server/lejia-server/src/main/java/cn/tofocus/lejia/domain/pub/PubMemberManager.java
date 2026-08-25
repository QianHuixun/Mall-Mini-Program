package cn.tofocus.lejia.domain.pub;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.data.EndDate;
import cn.tofocus.core.data.StartDate;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.PubGiftFullDto;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDetailsDTO;
import cn.tofocus.lejia.bean.dto.pub.MemberUsingCouponDto;
import cn.tofocus.lejia.bean.dto.pub.MemberUsingCouponInfo;
import cn.tofocus.lejia.bean.dto.pub.MemberUsingGiftOnPage;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsGift;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberGift;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.cache.CardLinshiPubMap;
import cn.tofocus.lejia.cache.PubCouponMap;
import cn.tofocus.lejia.cache.PubGiftFullMap;
import cn.tofocus.lejia.cache.PubGiftMap;
import cn.tofocus.lejia.dao.goods.MktGoodsGiftDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberGiftDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.domain.market.MemberManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class PubMemberManager
{
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktMemberGiftDao memberGiftDao;
    
    @Autowired
    private MktGoodsGiftDao goodsGiftDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private MktGoodsGiftDao giftDao;
    
    @Autowired
    private MemberManager memberManger;
    
    @Value("${small.pub.member.ascription:8}")
    private Integer ascription;
    
    @Autowired
    private PubCouponMap pubCouponMap;
    
    @Autowired
    private PubGiftMap pubGiftMap;
    
    @Autowired
    private PubGiftFullMap pubGiftFullMap;
    
    @Autowired
    private MktOrderDao mktOrderDao;
    
    @Autowired
    private CardLinshiPubMap cardLinshiPubMap;
    
    @Autowired
    private MktActivityDao activityDao;
    
//    private static final String saasMarket = "zy_mkt_0023";
    private static final String saasMarket = "zy_mkt_0035";
    
    public String getSaasMarket()
    {
        return saasMarket;
    }
    
    public Map<String, Integer> getGiftAndCouponNum(String mobile)
    {
        Map<String, Integer> res = new HashMap<>();
        res.put("giftNum", 0);
        res.put("couponNum", 0);
        MktMember member = insMember(mobile);
        List<Integer> cards = new ArrayList<>();
        List<Integer> list = cardLinshiPubMap.get("linshihuodong");
        if(list != null && !list.isEmpty())
            cards.addAll(list);
        Integer couponNum = memberCardDao.getMemberCardMarketNum(member.getPkey(), saasMarket, cards);
        res.put("couponNum", couponNum);
        Integer giftNum = memberGiftDao.getMemberGiftMarketNum(member.getPkey(), saasMarket);
        res.put("giftNum", giftNum);
        return res;
    }
    
    public Boolean addMemberConsumCoupon(Integer card)
    {
        List<Integer> list = cardLinshiPubMap.get("linshihuodong");
        if(list == null)
            list = new ArrayList<>();
        if(card == null)
            cardLinshiPubMap.remove("linshihuodong");
        else
        {
            list.add(card);
            cardLinshiPubMap.put("linshihuodong", list);
        }
        return true;
    }
    
    public PageResult<MemberUsingCouponDto> queryMemberConsumCoupon(Integer page, Integer pagesize, 
        String mobile, CardStatus status)
    {
        MktMember member = insMember(mobile);
        // 临时加 去掉指定优惠券不返回,(指定优惠券不在农贸H5上使用) 2024-08-17
        List<Integer> cards = new ArrayList<>();
        List<Integer> list = cardLinshiPubMap.get("linshihuodong");
        if(list != null && !list.isEmpty())
            cards.addAll(list);
        PageResult<MktMemberCard> pageResult =
            memberCardDao.queryMemberCardMarket(page, pagesize, member.getPkey(), saasMarket, status, cards);
        PageResult<MemberUsingCouponDto> res = BeanUtil.beanPageFrom(MemberUsingCouponDto.class, pageResult);
        List<MemberUsingCouponDto> content = new ArrayList<>();
        for (MktMemberCard mc : pageResult.getContent())
        {
            MemberUsingCouponDto muc = BeanUtil.beanFrom(MemberUsingCouponDto.class, mc);
            muc.setShareLinkType("COUPON");
            muc.setCoupon(mc.getCard());
            muc.setCouponNumber(mc.getCardNumber());
            if (Boolean.TRUE.equals(mc.getInvalid())) muc.setAvailable(false);
            muc.setEnbaled(true);
            muc.setEndDate(DateUtil.atEndOfDay(muc.getEndDate()));
            MktCard card = cardDao.get(mc.getCard());
            if(card != null)
                muc.setCouponName(card.getTitle());
            content.add(muc);
        }
        res.setContent(content);
        return res;
    }
    
    public PageResult<MemberUsingCouponInfo> queryMerchantUse(Integer page, Integer pagesize,
        StartDate startDate, EndDate endDate, String keyword, Integer merchant)
    {
        List<Integer> cardKeys = null;
        if(StringUtils.isNotBlank(keyword))
        {
            cardKeys = new ArrayList<>();
            List<MktCard> list = cardDao.select().like("title", keyword).exec();
            if(list.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for(MktCard c : list)
                cardKeys.add(c.getPkey());
        }
        PageResult<MktMemberCard> pageResult = memberCardDao.selectPage().page(page).pagesize(pagesize)
        .between("userTime", startDate, endDate)
        .eq("userMerchant", merchant)
        .eq("status", CardStatus.USED)
        .in("card", cardKeys)
        .eq("farmer", saasMarket)
        .sort("userTime", true)
        .sort("pkey", true)
        .exec();
        PageResult<MemberUsingCouponInfo> res = BeanUtil.beanPageFrom(MemberUsingCouponInfo.class, pageResult);
        List<MemberUsingCouponInfo> content = new ArrayList<>();
        for(MktMemberCard mc : pageResult.getContent())
        {
            MemberUsingCouponInfo dto = new MemberUsingCouponInfo();
            dto.setPkey(mc.getPkey());
            dto.setStatus("USED");
            dto.setSettleStatus("UNSETTLE");
            dto.setCardTypeName("优惠券");
            dto.setMemberKey(mc.getMember());
            MktMember mktMember = memberDao.get(mc.getMember());
            if(mktMember != null)
                dto.setMemberMobile(mktMember.getMobile());
            dto.setUserTime(mc.getUserTime());
            dto.setCost(mc.getCost());
            MktCard card = cardDao.get(mc.getCard());
            if(card != null)
                dto.setCouponName(card.getTitle());
            content.add(dto);
        }
        res.setContent(content);
        return res;
    }
    
    public List<MemberUsingGiftOnPage> listMemberGift(String mobile)
    {
        MktMember member = insMember(mobile);
        List<MktMemberGift> list = memberGiftDao.listMemberGiftMarket(member.getPkey(), saasMarket);
        List<MemberUsingGiftOnPage> res = new ArrayList<>();
        for (MktMemberGift mg : list)
        {
            MemberUsingGiftOnPage mug = BeanUtil.beanFrom(MemberUsingGiftOnPage.class, mg);
            mug.setStatus("UNUSED");
            MktGoodsGift gg = goodsGiftDao.get(mg.getGift());
            if (gg != null)
            {
                mug.setGiftName(gg.getTitle());
                mug.setPhoto(gg.getPicture());
            }
            //            mug.setCost(BigDecimal.ZERO);
            mug.setGiftNumber(mg.getCardNumber());
            //            mug.setMarket(market);
            res.add(mug);
        }
        return res;
    }
    
    public Boolean useCoupon(String mobile, Integer coupon, Integer merchant, String merchantName)
    {
        insMember(mobile);
        MktMemberCard card = memberCardDao.get(coupon);
        card.setStatus(CardStatus.USED);
        card.setUserTime(new Date());
//        card.setUserVendor(merchant);
        card.setUserMerchant(merchant);
        card.setUserMerchantName(merchantName);
        card.setUserFarmer("");
        memberCardDao.update(card);
        
        MktCard mktCard = cardDao.get(card.getCard());
        Integer usedNum = mktCard.getUsedNum();
        if (usedNum == null) usedNum = 0;
        mktCard.setUsedNum(usedNum + 1);
        cardDao.update(mktCard);
//        if(card.getActivity() != null)
//        {
//            MktActivity mktActivity = activityDao.get(card.getActivity());
//            if(mktActivity != null)
//            {
//                Integer useNum = mktActivity.getUseNum();
//                if(useNum == null)
//                    useNum = 0;
//                useNum += 1;
//                activityDao.updUseNum(mktActivity.getPkey(), useNum);
//            }
//        }
        return true;
    }
    
    public Boolean addCoupon(String mobile, Integer card, Integer num)
    {
        MktMember member = insMember(mobile);
        MktCard mktCard = cardDao.get(card);
        if (mktCard == null) throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
        
        Integer count = mktCard.getCount();
        Integer issuedNum = mktCard.getIssuedNum();
        if (issuedNum == null) issuedNum = 0;
        if ((count - issuedNum) == 0 || (count - issuedNum - num) < 0)
        {
            throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY);
        }
        mktCard.setIssuedNum(issuedNum + num);
        cardDao.update(mktCard);
        
        MktMemberCard entity = new MktMemberCard();
        BeanUtils.copyProperties(mktCard, entity, "pkey");
        entity.setStatus(CardStatus.UNUSED);
        entity.setMember(member.getPkey());
        entity.setCard(card);
        entity.setCost(mktCard.getCost());
        entity.setCardNumber(numberUtils.createCardNumber());
        entity.setEndDate(getEndDate(mktCard));
        entity.setInvalid(mktCard.getInvalid());
        entity.setIsRead(false);
        memberCardDao.add(entity);
        return true;
    }
    
    public Boolean exchangeCoupon(String mobile, Integer xaszCoupon)
    {
        MktMember member = insMember(mobile);
        Integer card = pubCouponMap.get(xaszCoupon + "");
        if(card == null)
            return false;
        MktCard mktCard = cardDao.get(card);
        int num = 1;
        if (mktCard == null) throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
        
//        MktMemberCard mktMemberCard = memberCardDao.selectOne()
//            .eq("member", member.getPkey())
//            .eq("card", card)
//            .exec();
//        if(mktMemberCard != null)
//            return false;
        Integer count = mktCard.getCount();
        Integer issuedNum = mktCard.getIssuedNum();
        if (issuedNum == null) issuedNum = 0;
        if ((count - issuedNum) == 0 || (count - issuedNum - num) < 0)
        {
            throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY);
        }
        mktCard.setIssuedNum(issuedNum + num);
        cardDao.update(mktCard);
        
        MktMemberCard entity = new MktMemberCard();
        BeanUtils.copyProperties(mktCard, entity, "pkey");
        entity.setStatus(CardStatus.UNUSED);
        entity.setMember(member.getPkey());
        entity.setCard(card);
        entity.setCost(mktCard.getCost());
        entity.setCardNumber(numberUtils.createCardNumber());
        entity.setEndDate(getEndDate(mktCard));
        entity.setInvalid(mktCard.getInvalid());
        entity.setIsRead(false);
        memberCardDao.add(entity);
        return true;
    }
    
    public Boolean exchangeGift(String mobile, Integer xaszGift)
    {
        MktMember member = insMember(mobile);
        Integer giftKey = pubGiftMap.get(xaszGift + "");
        Integer num = 1;
        MktGoodsGift gift = giftDao.get(giftKey);
        if (gift == null)
            throw TofocusException.of(LejiaErrCode.GIFT_NOT_FOUND);
        Integer count = gift.getCount();
        Integer issuedNum = gift.getIssuedNum();
        if (issuedNum == null) issuedNum = 0;
        if ((count - issuedNum) == 0 || (count - issuedNum - num) < 0)
        {
            throw TofocusException.of(LejiaErrCode.GIFT_IS_EMPTY);
        }
//        MktMemberGift memberGift = memberGiftDao.selectOne()
//            .eq("member", member.getPkey())
//            .eq("gift", giftKey)
//            .exec();
//            if(memberGift != null)
//                return false;
        
        List<MktMemberGift> addGift = new ArrayList<>();
        for (int i = 0; i < num; i++)
        {
            MktMemberGift entity = new MktMemberGift();
            entity.setStatus(CardStatus.UNUSED);
            entity.setMember(member.getPkey());
            entity.setCardNumber(numberUtils.createGiftNumber());
            entity.setGift(gift.getPkey());
            entity.setExpireChoose(gift.getExpireChoose());
            switch (gift.getExpireChoose())
            {
                case DATE_RANGE:
                    entity.setStartDate(gift.getStartDate());
                    entity.setEndDate(gift.getEndDate());
                    break;
                case DAYS:
                    entity.setStartDate(DateUtil.atStartOfToday());
                    LocalDate endDate = LocalDate.now().plusDays(gift.getEffective());
                    entity.setEndDate(DateUtil.localDate2Date(endDate));
                    break;
            }
            entity.setUserFarmer(gift.getUserFarmer());
            entity.setUserVendor(gift.getUserVendor());
            entity.setInvalid(false);
            entity.setFarmer(gift.getFarmer());
            entity.setCompany(gift.getCompany());
            addGift.add(entity);
        }
        memberGiftDao.addAll(addGift);
        giftDao.updIssuedNum(gift.getPkey(), gift.getIssuedNum() + num);
        return true;
    }
    
    public void putMapCoupon(Integer coupon, Integer xaszCoupon)
    {
        pubCouponMap.put(xaszCoupon + "", coupon);
    }
    
    public void putMapGift(Integer gift, Integer xaszGift)
    {
        pubGiftMap.put(xaszGift + "", gift);
    }
    
    public Boolean existCoupon(String mobile, Integer card)
    {
        MktMember member = insMember(mobile);
        MktCard mktCard = cardDao.get(card);
        if (mktCard == null) throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
        MktMemberCard exec = memberCardDao.selectOne().eq("card", card).eq("member", member.getPkey()).exec();
        return exec != null;
    }
    
    public int getCouponStock(Integer card)
    {
        MktCard mktCard = cardDao.get(card);
        if (mktCard == null) throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
        
        Integer count = mktCard.getCount();
        Integer issuedNum = mktCard.getIssuedNum();
        if (issuedNum == null) issuedNum = 0;
        int stock = count - issuedNum;
        return Math.max(stock, 0);
    }
    
    public Map<String,Object> getCoupon(String mobile, Integer coupon)
    {
        insMember(mobile);
        MktMemberCard memberCard = memberCardDao.get(coupon);
        if(!CardStatus.UNUSED.equals(memberCard.getStatus()))
            throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        MktCard mktCard = cardDao.get(memberCard.getCard());
        if (mktCard == null) throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
        Map<String,Object> res = new HashMap<>();
        res.put("limitCost", mktCard.getLimitCost());
        res.put("cost", mktCard.getCost());
        res.put("coupon", mktCard.getPkey());
        res.put("pkey", coupon);
        return res;
    }
    
    // 取过期日期
    private Date getEndDate(MktCard mktCard)
    {
        if (mktCard == null) return null;
        Integer effective = mktCard.getEffective();
        if (effective == null)
        {
            Date date = mktCard.getEndDate();
            if (date == null) return null;
            return date;
        }
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, effective);
        date = calendar.getTime();
        return date;
    }
    
    public Boolean verifyGift(String mobile, Integer gift)
    {
        insMember(mobile);
        MktMemberGift ent = memberGiftDao.get(gift);
        MktGoodsGift goodsGift = check4WriteOff(ent);
        ent.setUserTime(new Date());
        ent.setStatus(CardStatus.USED);
        memberGiftDao.update(ent);
        giftDao.updUsedNum(goodsGift.getPkey(), goodsGift.getIssuedNum() + 1);
        return true;
    }
    
    public BigDecimal getDayConsumptionAmt(String mobile)
    {
        MktMember member = insMember(mobile);
        Date now = new Date();
        Date sd = DateUtil.atStartOfDay(now);
        Date ed = DateUtil.atEndOfDay(now);
        Number execSum = mktOrderDao.aggregation()
        .eq("farmer", saasMarket)
        .eq("member", member.getPkey())
        .between("createdTime", sd, ed)
        .notEq("status", OrderStatus.UNPAID_ORDER)
        .notEq("status", OrderStatus.REFUNDED_ORDER)
        .notEq("status", OrderStatus.VOID_ORDER)
        .execSum("amtn");
        if(execSum != null)
        {
            return new BigDecimal(execSum.toString());
        }
        return BigDecimal.ZERO;
    }
    
    // 判断是否满足满一百送礼品券
    public Boolean judgeGiftReceive(String mobile)
    {
        if(!pubGiftFullMap.containsKey("8"))
            return false;
        PubGiftFullDto dto = pubGiftFullMap.get("8");
        Date now = new Date();
        if(dto.getSd() == null || dto.getEd() == null)
            return false;
        if(now.compareTo(dto.getSd()) < 0 || now.compareTo(dto.getEd()) > 0)
            return false;
        Integer giftKey = dto.getGift();
        MktGoodsGift gift = giftDao.get(giftKey);
        if (gift == null)
            return false;
        Integer count = gift.getCount();
        Integer issuedNum = gift.getIssuedNum();
        if (issuedNum == null) issuedNum = 0;
        if ((count - issuedNum) == 0 || (count - issuedNum - 1) < 0)
        {
            return false;
        }
        MktMember member = insMember(mobile);
        MktMemberGift memberGift = memberGiftDao.selectOne()
            .eq("member", member.getPkey())
            .eq("gift", giftKey)
            .exec();
        if(memberGift != null)
            return false;
        return true;
    }
    
    // 满一百赠送礼品券
    public Boolean fullGift(String mobile)
    {
        Boolean judgeGiftReceive = judgeGiftReceive(mobile);
        if(Boolean.TRUE.equals(judgeGiftReceive))
        {
            PubGiftFullDto dto = pubGiftFullMap.get("8");
            Integer giftKey = dto.getGift();
            Integer num = 1;
            MktGoodsGift gift = giftDao.get(giftKey);
            if (gift == null)
                throw TofocusException.of(LejiaErrCode.GIFT_NOT_FOUND);
            Integer count = gift.getCount();
            Integer issuedNum = gift.getIssuedNum();
            if (issuedNum == null) issuedNum = 0;
            if ((count - issuedNum) == 0 || (count - issuedNum - num) < 0)
            {
                throw TofocusException.of(LejiaErrCode.GIFT_IS_EMPTY);
            
            }
            MktMember member = insMember(mobile);
            MktMemberGift memberGift = memberGiftDao.selectOne()
                .eq("member", member.getPkey())
                .eq("gift", giftKey)
                .exec();
                if(memberGift != null)
                    return false;
            
            List<MktMemberGift> addGift = new ArrayList<>();
            for (int i = 0; i < num; i++)
            {
                MktMemberGift entity = new MktMemberGift();
                entity.setStatus(CardStatus.UNUSED);
                entity.setMember(member.getPkey());
                entity.setCardNumber(numberUtils.createGiftNumber());
                entity.setGift(gift.getPkey());
                entity.setExpireChoose(gift.getExpireChoose());
                switch (gift.getExpireChoose())
                {
                    case DATE_RANGE:
                        entity.setStartDate(gift.getStartDate());
                        entity.setEndDate(gift.getEndDate());
                        break;
                    case DAYS:
                        entity.setStartDate(DateUtil.atStartOfToday());
                        LocalDate endDate = LocalDate.now().plusDays(gift.getEffective());
                        entity.setEndDate(DateUtil.localDate2Date(endDate));
                        break;
                }
                entity.setUserFarmer(gift.getUserFarmer());
                entity.setUserVendor(gift.getUserVendor());
                entity.setInvalid(false);
                entity.setFarmer(gift.getFarmer());
                entity.setCompany(gift.getCompany());
                addGift.add(entity);
            }
            memberGiftDao.addAll(addGift);
            giftDao.updIssuedNum(gift.getPkey(), gift.getIssuedNum() + num);
            return true;
        }
        return false;
    }
    
    public Boolean addFullGift(PubGiftFullDto dto)
    {
        pubGiftFullMap.put("8", dto);
        return true;
    }
    
    private MktGoodsGift check4WriteOff(MktMemberGift ent)
    {
        if (ent == null) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券不存在");
        MktGoodsGift gift = giftDao.get(ent.getGift());
        if (gift == null || Boolean.TRUE.equals(gift.getInvalid()) || Boolean.TRUE.equals(ent.getInvalid()))
            throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已失效");
        if (ent.getStatus() == CardStatus.USED) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已被使用过");
        if (ent.getStatus() == CardStatus.EXPIRED) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已过期");
        return gift;
    }
    
    private MktMember insMember(String mobile)
    {
        MktMember member = memberDao.getMobile(mobile, ascription);
        if (member == null)
        {
            MktAppMemberDetailsDTO entity = new MktAppMemberDetailsDTO();
            entity.setMobile(mobile);
            entity.setName("会员用户");
            entity.setOpenid1("xasz" + UUID.randomUUID());
            entity.setLastFarmer(saasMarket);
            member = memberManger.insMember(entity, ascription);
        }
        return member;
    }
}
