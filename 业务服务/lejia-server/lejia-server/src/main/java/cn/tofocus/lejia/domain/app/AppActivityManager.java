package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.WeixinConfig;
import cn.tofocus.lejia.bean.dto.app.market.AppActivityDistributeOnPage;
import cn.tofocus.lejia.bean.dto.app.market.AppActivityInfo;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsGift;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktActivityCoupon;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktManager;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberActivity;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberGift;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.enums.ActivityDistributeType;
import cn.tofocus.lejia.bean.enums.CouponType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsGiftDao;
import cn.tofocus.lejia.dao.market.MktActivityCouponDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktMemberActivityDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberGiftDao;
import cn.tofocus.lejia.dao.market.MktMemberTagDao;
import cn.tofocus.lejia.dao.market.MktTagVisibleDao;
import cn.tofocus.lejia.dao.market.MktManagerDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.domain.pay.ChinaUmsPayManager;
import cn.tofocus.lejia.domain.pay.WxPayManager;
import cn.tofocus.lejia.domain.v2.GiftV2Manager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.util.wx.PayJs;
import cn.tofocus.lejia.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppActivityManager
{
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private MktActivityCouponDao activityCouponDao;
    
    @Autowired
    private MktMemberActivityDao memberActivityDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MktGoodsGiftDao giftDao;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktMemberGiftDao memberGiftDao;
    
    @Autowired
    private MktManagerDao managerDao;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private GiftV2Manager giftManager;
    
    @Autowired
    private WxPayManager wxPayManger;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private MktTagVisibleDao tagVisibleDao;
    
    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    public SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private ChinaUmsPayManager chinaUmsPayManager;
    
    public AppActivityInfo get(Integer pkey)
    {
        Integer ascription = MobileSession.appid();
        AppActivityInfo info = activityDao.get(pkey, ascription, AppActivityInfo.class);
        if (info == null) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
        MktMember member = MobileSession.member();
        if (member != null)
        {
            MktManager manager = managerDao.getByMobileAndFarmer(member.getMobile(), info.getFarmer(), ascription);
            if (manager != null) info.setAllowedShare(true);
        }
        return info;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public WxPayData join(Integer pkey)
    {
        Integer ascription = MobileSession.appid();
        if (ascription == null) throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        String openid = MobileSession.openid();
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey == null) throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        MktActivity activity = activityDao.get(pkey);
        if (activity == null) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
        // 检查活动状态
        if (!Boolean.TRUE.equals(activity.getEnabled())) throw TofocusException.of(LejiaErrCode.ACTIVITY_DISABLED);
        // 检查活动时间
        Date now = new Date();
        if (DateUtil.compareDate(activity.getStartTime(), now) > 0)
            throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_STARTED);
        if (DateUtil.compareDate(activity.getEndTime(), now) < 0)
            throw TofocusException.of(LejiaErrCode.ACTIVITY_ENDED);
        // 检查总库存
        if (activity.getNum() - activity.getIssuedNum() < 1) throw TofocusException.of(LejiaErrCode.ACTIVITY_NO_STOCK);
        // 检查今日限量
        if (activity.getLimitDailyNum() != -1)
        {
            long todayCount = memberActivityDao.count(activity
                .getPkey(), null, DateUtil.atStartOfToday(), DateUtil.atStartOfTomorrow(), OrderStatus.CONFIRM_ORDER);
            if (activity.getLimitDailyNum() - todayCount < 1)
                throw TofocusException.of(LejiaErrCode.ACTIVITY_TODAY_NO_STOCK);
        }
        // 检查用户参与次数
        long memberCount =
            memberActivityDao.count(activity.getPkey(), memberPkey, null, null, OrderStatus.CONFIRM_ORDER);
        if (activity.getLimitMemberTimes() - memberCount < 1)
            throw TofocusException.of(LejiaErrCode.ACTIVITY_MEMBER_TIMES_LIMIT);
        
        // 遍历检查卡券状态及库存
        List<MktActivityCoupon> activityCoupons = activityCouponDao.listByActivity(pkey);
        Map<Integer, MktCard> cardMap = new HashMap<>();
        Map<Integer, MktGoodsGift> giftMap = new HashMap<>();
        checkAndCollectCoupon(activityCoupons, cardMap, giftMap);
        
        WxPayData wxPayData = null;
        MktMemberActivity memberActivity = new MktMemberActivity();
        memberActivity.setMember(memberPkey);
        memberActivity.setActivity(pkey);
        String payNumber = numberUtils.createOrderNumber();
        payNumber = "95" + payNumber;
        memberActivity.setCode(payNumber);
        memberActivity.setAmt(activity.getPrice());
        memberActivity.setSettlementType(SettlementType.NOT_START);
        SysFarmerConfig config = sysFarmerConfigDao.get(activity.getFarmer());
        memberActivity.setCommissionType(config.getCommissionType());
        memberActivity.setFarmer(activity.getFarmer());
        memberActivity.setCompany(activity.getCompany());
        memberActivity.setAscription(MobileSession.appid());
        // 免费直接领
        if (activity.getPrice() == null || BigDecimal.ZERO.compareTo(activity.getPrice()) == 0)
        {
            memberActivity.setStatus(OrderStatus.CONFIRM_ORDER);
            memberActivity.setPayTime(now);
            memberActivity = memberActivityDao.add(memberActivity);
        }
        // 发起支付
        else
        {
            memberActivity.setStatus(OrderStatus.UNPAID_ORDER);
            memberActivity = memberActivityDao.add(memberActivity);
            WeixinConfig wxc = ascriptionDao.getWxConfig(ascription);
            try
            {
                if (MobileSession.appid().equals(13))
                {
                    wxPayData = chinaUmsPayManager.chinaUmsPay(MobileSession.openid(), payNumber, memberActivity.getAmt());
                }
                else
                {
                    PayJs payJs =
                        wxPayManger.topayIvc(MobileSession.billIp(), openid, payNumber, memberActivity.getAmt(), wxc);
                    wxPayData = BeanUtil.beanFrom(WxPayData.class, payJs);
                }
            }
            catch (Exception e)
            {
                log.error("[卡券活动] 发起微信支付失败，memberActivityPkey={}", memberActivity.getPkey(), e);
            }
        }
        // 如果是成功的，发放卡券，并修改卡券发放数及活动参与数量
        if (OrderStatus.CONFIRM_ORDER == memberActivity.getStatus())
        {
            afterJoinActivitySuccess(activity, memberPkey, activityCoupons, cardMap, giftMap, memberActivity.getCode());
        }
        return wxPayData;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Boolean payOrder(String code)
    {
        MktMemberActivity memberActivity = memberActivityDao.byCode(code);
        if (memberActivity == null)
        {
            log.error("购买卡券活动有问题，卡券活动参与记录找不到，code：{}", code);
            return false;
        }
        memberActivity.setStatus(OrderStatus.CONFIRM_ORDER);
        
        MktActivity activity = activityDao.get(memberActivity.getActivity());
        if (activity == null) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
        
        // 遍历检查卡券状态及库存
        List<MktActivityCoupon> activityCoupons = activityCouponDao.listByActivity(memberActivity.getActivity());
        Map<Integer, MktCard> cardMap = new HashMap<>();
        Map<Integer, MktGoodsGift> giftMap = new HashMap<>();
        checkAndCollectCoupon(activityCoupons, cardMap, giftMap);
        afterJoinActivitySuccess(activity, memberActivity.getMember(), activityCoupons, cardMap, giftMap, code);
        
        return true;
    }
    
    public void checkAndCollectCoupon(List<MktActivityCoupon> activityCoupons, Map<Integer, MktCard> cardMap,
        Map<Integer, MktGoodsGift> giftMap)
    {
        for (MktActivityCoupon activityCoupon : activityCoupons)
        {
            // 优惠券
            if (activityCoupon.getCouponType() == CouponType.CARD)
            {
                MktCard card = cardDao.getCard(activityCoupon.getCoupon());
                if (card == null) throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
                if (Boolean.TRUE.equals(card.getInvalid())) throw TofocusException.of(LejiaErrCode.CARD_INVALID);
                if (!Boolean.TRUE.equals(card.getEnabled()) || card.getCount() - card.getIssuedNum() <= 0)
                    throw TofocusException.of(LejiaErrCode.CARD_IS_EMPTY);
                cardMap.put(card.getPkey(), card);
            }
            // 礼品券
            else
            {
                MktGoodsGift gift = giftDao.get(activityCoupon.getCoupon());
                if (gift == null) throw TofocusException.of(LejiaErrCode.GIFT_NOT_FOUND);
                if (Boolean.TRUE.equals(gift.getInvalid())) throw TofocusException.of(LejiaErrCode.GIFT_INVALID);
                if (!Boolean.TRUE.equals(gift.getEnabled()) || gift.getCount() - gift.getIssuedNum() <= 0)
                    throw TofocusException.of(LejiaErrCode.GIFT_IS_EMPTY);
                giftMap.put(gift.getPkey(), gift);
            }
        }
    }
    
    public void afterJoinActivitySuccess(MktActivity activity, Integer memberPkey,
        List<MktActivityCoupon> activityCoupons, Map<Integer, MktCard> cardMap, Map<Integer, MktGoodsGift> giftMap,
        String code)
    {
        // 遍历卡券，发放卡券，修改卡券发放数
        List<MktMemberCard> memberCards = new ArrayList<>();
        List<MktMemberGift> memberGifts = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("卡券已到账：");
        for (MktActivityCoupon ac : activityCoupons)
        {
            int couponNum = ac.getNum();
            // 优惠券
            if (ac.getCouponType() == CouponType.CARD)
            {
                MktCard card = cardMap.get(ac.getCoupon());
                if (card == null) card = cardDao.get(ac.getCoupon());
                if (card == null) throw TofocusException.of(LejiaErrCode.CARD_INEXISTENCE);
               
                Integer issuedNum = card.getIssuedNum() == null ? 0 : card.getIssuedNum();
                Integer count = card.getCount();
                if ((count - issuedNum) == 0)
                {
                    continue;
                }
                if((count - issuedNum - ac.getNum()) < 0)
                    couponNum = card.getCount() - issuedNum;
                for (int i = 0; i < couponNum; i++)
                {
                    MktMemberCard entity = cardManager.setMktMemberCard(memberPkey, card.getPkey(), card);
                    entity.setActivity(activity.getPkey());
                    memberCards.add(entity);
                }
                cardDao.updIssuedNum(card.getPkey(), issuedNum + couponNum);
                sb.append(card.getTitle());
            }
            // 礼品券
            else
            {
                MktGoodsGift gift = giftMap.get(ac.getCoupon());
                if (gift == null) gift = giftDao.get(ac.getCoupon());
                if (gift == null) throw TofocusException.of(LejiaErrCode.GIFT_NOT_FOUND);
                Integer issuedNum = gift.getIssuedNum() == null ? 0 : gift.getIssuedNum();
                Integer count = gift.getCount();
                if(count != null)
                {
                    if ((count - issuedNum) == 0)
                    {
                        continue;
                    }
                    if((count - issuedNum - ac.getNum()) < 0)
                        couponNum = gift.getCount() - issuedNum;
                }
                // 剩余张，需要新的卡券编号
                for (int i = 0; i < couponNum; i++)
                {
                    MktMemberGift entity = giftManager.makeMemberGift(memberPkey, gift);
                    entity.setActivity(activity.getPkey());
                    memberGifts.add(entity);
                }
                giftDao.updIssuedNum(gift.getPkey(), issuedNum + ac.getNum());
                sb.append(gift.getTitle());
            }
            sb.append("*");
            sb.append(ac.getNum());
            sb.append("，");
        }
        memberCardDao.addAll(memberCards);
        memberGiftDao.addAll(memberGifts);
        // 修改活动参与数
        Integer issuedNum = activity.getIssuedNum() == null ? 0 : activity.getIssuedNum();
        activityDao.updIssuedNum(activity.getPkey(), issuedNum + 1, activity.getReceiveNum() + activityCouponDao.getSumNum(activity.getPkey()));
        
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                // 卡券活动 微信发货
                try
                {
                    String itemDesc = sb.toString();
                    if(itemDesc.length() > 0)
                        itemDesc = itemDesc.substring(0, itemDesc.length() - 1);
                    if(itemDesc.length() > 120)
                        itemDesc = itemDesc.substring(0, 120);
                    String openid = null;
                    String mchid = null;
                    SysAscription sysAscription = ascriptionDao.get(activity.getAscription());
                    if(sysAscription != null)
                    {
                        mchid = sysAscription.getConfigMchid();
                    }
                    MktMember mktMember = memberDao.get(memberPkey);
                    if(mktMember != null)
                        openid = mktMember.getOpenid1();
                    if(openid != null && mchid != null)
                    {
                        wxManager.uploadShippingInfo(null,
                            code + "1",
                            mchid,
                            itemDesc,
                            3,
                            null,
                            null,
                            null,
                            null,
                            openid,
                            activity.getAscription());
                    }
                }
                catch (Exception e)
                {
                    log.error(e.getMessage());
                    log.error("微信确认收货报错");
                }
            }
            
        }).start();
        
    }
    
    public PageResult<AppActivityDistributeOnPage> queryDistributeActivity(int page, int pagesize)
    {
        Integer ascription = MobileSession.appid();
        if (ascription == null) throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        MktMember member = MobileSession.member();
        if (member == null) throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        List<String> allowedFarmers = managerDao.listAllowedFarmer(member.getMobile(), ascription);
        if (CollectionUtil.isEmpty(allowedFarmers)) throw TofocusException.of(LejiaErrCode.NOT_RIGHT, "没有活动分发权限");
        return activityDao.query(page,
            pagesize,
            null,
            true,
            ActivityDistributeType.WeChatGroup,
            allowedFarmers,
            ascription,
            AppActivityDistributeOnPage.class);
    }
    
    public List<AppActivityInfo> listWelfare()
    {
        Integer ascription = MobileSession.appid();
        if (ascription == null) throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        MktMember member = MobileSession.member();
        if (member == null) throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        List<Integer> listTag = memberTagDao.listTag(MobileSession.memberPkey(), MobileSession.appid());
        List<Long> keys = new ArrayList<>();
        if(!listTag.isEmpty())
        {
            keys = tagVisibleDao.listTarget(TagVisibleTargetType.ACTIVITY, listTag);
        }
        return activityDao.appList(MobileSession.farmerPkey(), keys, ascription, AppActivityInfo.class);
    }
}
