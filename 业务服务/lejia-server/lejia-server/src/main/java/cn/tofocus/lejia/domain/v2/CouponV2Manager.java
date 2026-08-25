package cn.tofocus.lejia.domain.v2;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.v2.card.MemberCardV2OnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktManager;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.market.MktActivityCouponDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktManagerDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import io.micrometer.core.instrument.util.StringUtils;

@Component
public class CouponV2Manager
{
    @Autowired
    private MktManagerDao managerDao;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MktActivityCouponDao activityCouponDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktGtypeDao mktGtypeDao;
    
    public MemberCardV2OnList load4WriteOff(String cardNumber)
    {
        MktMemberCard memberCard = memberCardDao.getByCardNumber(cardNumber);
        checkCard(memberCard);
        MemberCardV2OnList mc = BeanUtil.beanFrom(MemberCardV2OnList.class, memberCard);
        if(memberCard.getUserGoods() != null)
        {
            MktGoods mktGoods = goodsDao.get(memberCard.getUserGoods());
            if(mktGoods != null)
            {
                mc.setUserGoodsName(mktGoods.getTitle());
            }
        }
        if(StringUtils.isNotBlank(mc.getUserFarmer()))
        {
            SysFarmer farmer = farmerDao.get(mc.getUserFarmer());
            if(farmer != null)
            {
                mc.setUserFarmerName(farmer.getName());
            }
        }
        if(memberCard.getCard() != null)
        {
            MktCard card = cardDao.get(memberCard.getCard());
            if(card != null)
            {
                mc.setTitle(card.getTitle());
                mc.setUserFarmer(card.getUserFarmer());
            }
        }
        if(memberCard.getUserType() != null)
        {
            MktGtype mktGtype = mktGtypeDao.get(memberCard.getUserType());
            if(mktGtype != null)
                mc.setUserTypeName(mktGtype.getName());
        }
        Integer userVendor = mc.getUserVendor();
        if(userVendor != null)
        {
            MktVendor mktVendor = vendorDao.get(userVendor);
            if(mktVendor != null)
                mc.setUserVendorName(mktVendor.getDisplayName());
        }
        if(mc.getType() != null)
            mc.setTypeName(mc.getType().getName());
        if(mc.getUserOrderType() != null)
            mc.setUserOrderTypeName(mc.getUserOrderType().getName());
        return mc;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean writeOff(String cardNumber)
    {
        MktMemberCard card = memberCardDao.getByCardNumber(cardNumber);
        card = checkCard(card);
        card.setUserTime(new Date());
        card.setStatus(CardStatus.USED);
        memberCardDao.update(card);
        
        MktCard mktCard = cardDao.get(card.getCard());
        Integer usedNum = mktCard.getUsedNum();
        if (usedNum == null) usedNum = 0;
        mktCard.setUsedNum(usedNum + 1);
        cardDao.update(mktCard);
        if(card.getActivity() != null)
        {
            MktActivity mktActivity = activityDao.get(card.getActivity());
            if(mktActivity != null)
            {
                Integer useNum = mktActivity.getUseNum();
                if(useNum == null)
                    useNum = 0;
                useNum += 1;
                activityDao.updUseNum(mktActivity.getPkey(), useNum);
            }
        }
        return true;
    }
    
    public MktMemberCard checkCard(MktMemberCard card)
    {
        MktMember member = MobileSession.member();
        if (card == null || Boolean.TRUE.equals(card.getInvalid()) || Boolean.TRUE.equals(card.getInvalid()))
            throw TofocusException.of(LejiaErrCode.COUPON_ERROR4, "卡券已禁用");
        if (card.getStatus() == CardStatus.USED) throw TofocusException.of(LejiaErrCode.COUPON_ERROR2, "卡券已使用");
        Date today = DateUtil.atStartOfToday();
        if (DateUtil.compareDate(card.getStartDate(), today) > 0)
            throw TofocusException.of(LejiaErrCode.COUPON_ERROR1, "还未开始核销");
        if (card.getStatus() == CardStatus.EXPIRED) throw TofocusException.of(LejiaErrCode.COUPON_ERROR3, "卡券已过期");
        if (DateUtil.compareDate(card.getEndDate(), today) < 0)
            throw TofocusException.of(LejiaErrCode.COUPON_ERROR3, "卡券已过期");
        if (member == null || StringUtil.isBlank(member.getMobile()))
            throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE, "无权核销该优惠券");
        MktManager manager =
            managerDao.getByMobileAndFarmer(member.getMobile(), card.getUserFarmer(), card.getAscription());
        if (manager == null) throw TofocusException.of(LejiaErrCode.COUPON_ERROR, "无权核销该优惠券");
        if (card.getUserFarmer() == null) card.setUserFarmer(manager.getFarmer());
        // 如果是活动卡券，检查活动限制
        if (card.getActivity() != null)
        {
            MktActivity activity = activityDao.get(card.getActivity());
            if (activity != null && activity.getLimitDailyCardNum() != -1)
            {
                long usedNum = memberCardDao.countByActivity(activity.getPkey(),
                    card.getMember(),
                    CardStatus.USED,
                    cn.tofocus.lejia.utils.DateUtil.atStartOfToday(),
                    cn.tofocus.lejia.utils.DateUtil.atStartOfTomorrow());
                if (usedNum >= activity.getLimitDailyCardNum())
                    throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR, "该活动优惠券已达到今日使用上限");
            }
        }
        
        //        if (card.getUserOrderType() != null)
        //        {
        //            if (card.getUserOrderType() == CardUserOrderType.PICKUP && distributionType != DistributionType.PICKUP)
        //                throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR, "该优惠券仅支持自提使用");
        //            if (card.getUserOrderType() == CardUserOrderType.DELIVERY
        //                && distributionType != DistributionType.IMMEDIATELY && distributionType != DistributionType.ORDERED)
        //                throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR, "该优惠券仅支持配送使用");
        //        }
        // TODO 限制商户等需要调整
        //        if (card.getUserGoods() != null)
        //        {
        //            if (!cardMap.containsKey(card.getUserGoods()))
        //            {
        //                throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        //            }
        //            GoodsCardInfo cardInfo = cardMap.get(card.getUserGoods());
        //            if (cardInfo.getCost().compareTo(card.getLimitCost()) == -1)
        //                throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        //        }
        //        else if (card.getUserType() != null)
        //        {
        //            Boolean userType = true;
        //            Boolean userTypeCost = true;
        //            for (Integer key : cardMap.keySet())
        //            {
        //                GoodsCardInfo info = cardMap.get(key);
        //                if (info.getUserType() != null && card.getUserType().equals(info.getUserType()))
        //                {
        //                    userType = false;
        //                    if (info.getCost().compareTo(card.getLimitCost()) != -1)
        //                    {
        //                        userTypeCost = false;
        //                    }
        //                }
        //            }
        //            if (userType) throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        //            if (userTypeCost) throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        //        }
        //        else
        //        {
        //            BigDecimal cost = BigDecimal.ZERO;
        //            for (Integer key : cardMap.keySet())
        //            {
        //                GoodsCardInfo info = cardMap.get(key);
        //                cost = cost.add(info.getCost());
        //            }
        //            System.out.println("cardMap: " + JsonUtil.toString(cardMap, true));
        //            System.out.println("cost: " + cost);
        ////            cost = cost.add(postage);
        //            if (cost.compareTo(card.getLimitCost()) == -1) throw TofocusException.of(LejiaErrCode.CARD_USER_ERROR);
        //        }
        return card;
    }
}
