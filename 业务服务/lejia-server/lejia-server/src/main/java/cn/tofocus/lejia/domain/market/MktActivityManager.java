package cn.tofocus.lejia.domain.market;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.excel.market.ExportMktActivityIssue;
import cn.tofocus.lejia.bean.dto.market.MktActivityCouponOnList;
import cn.tofocus.lejia.bean.dto.market.MktActivityInfo;
import cn.tofocus.lejia.bean.dto.market.MktActivityIssueOnPage;
import cn.tofocus.lejia.bean.dto.market.MktActivityOnList;
import cn.tofocus.lejia.bean.dto.market.MktActivityOnPage;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsGift;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktActivityCoupon;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.enums.CouponType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsGiftDao;
import cn.tofocus.lejia.dao.market.MktActivityCouponDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktMemberActivityDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktTagVisibleDao;
import cn.tofocus.lejia.domain.TagManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.ExportUtil;
import cn.tofocus.lejia.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MktActivityManager
{
    @Value("${tofocus.miniProgram.mktActivity.url:}")
    private String activityUrl;
    
    @Value("${tofocus.miniProgram.mktActivity.popUp.url:}")
    private String activityPopUpUrl;
    
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private MktActivityCouponDao activityCouponDao;
    
    @Autowired
    private MktMemberActivityDao memberActivityDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MktGoodsGiftDao giftDao;
    
    @Autowired
    private TagManager tagManager;
    
    @Autowired
    private MktTagVisibleDao tagVisibleDao;
    
    public PageResult<MktActivityOnPage> query(int page, int pagesize, String name, Boolean enabled, String farmer)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        List<String> farmers;
        if ((Constant.Operation + ascription).equals(currentFarmer) && StringUtil.isNotBlank(farmer))
            farmers = Lists.newArrayList(farmer);
        else
            farmers = Lists.newArrayList(currentFarmer);
        return activityDao.query(page, pagesize, name, enabled, null, farmers, ascription, MktActivityOnPage.class);
    }
    
    public List<MktActivityOnList> list(String name, Boolean enabled, String farmer)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        List<String> farmers;
        if ((Constant.Operation + ascription).equals(currentFarmer) && StringUtil.isNotBlank(farmer))
            farmers = Lists.newArrayList(farmer);
        else
            farmers = Lists.newArrayList(currentFarmer);
        return activityDao.list(name, enabled, null, farmers, ascription, MktActivityOnList.class);
    }
    
    public PageResult<MktActivityIssueOnPage> queryIssue(int page, int pagesize, String memberMobile, String startDate,
        String endDate, Integer activity)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String farmer = CurrentSession.marketPkey();
        List<Integer> members = null;
        if (cn.tofocus.common.util.StringUtil.isNotBlank(memberMobile))
        {
            members = memberDao.listPkeys(ascription, memberMobile);
            if (CollectionUtil.isEmpty(members))
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        return memberActivityDao.query(page,
            pagesize,
            members,
            DateUtil.atStartOfDay(startDate),
            DateUtil.atStartOfNextDay(endDate),
            activity,
            OrderStatus.CONFIRM_ORDER,
            farmer,
            ascription,
            MktActivityIssueOnPage.class);
    }
    
    public void exportIssue(String memberMobile, String startDate, String endDate, Integer activity,
        HttpServletResponse response)
    {
        try
        {
            MktActivity bean = activityDao.get(activity);
            if (bean == null) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
            PageResult<MktActivityIssueOnPage> pageResult =
                queryIssue(0, 10000, memberMobile, startDate, endDate, activity);
            List<ExportMktActivityIssue> list =
                BeanUtil.beanListFrom(ExportMktActivityIssue.class, pageResult.getContent());
            String name = "活动发放记录";
            ExportUtil
                .exportData(ExportMktActivityIssue.class, list, response, name + "_" + bean.getName(), name, name);
        }
        catch (Exception e)
        {
            log.error("导出优惠券使用记录失败", e);
        }
    }
    
    public MktActivityInfo get(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktActivityInfo info = activityDao.get(pkey, ascription, MktActivityInfo.class);
        info.handleLimit4Get();
        if(MemberVisibleRange.TAG.equals(info.getVisibleRange()))
        {
            List<Integer> tagKeys = new ArrayList<>();
            tagKeys = tagVisibleDao.listTagKeys(TagVisibleTargetType.ACTIVITY, info.getPkey().longValue());
            info.setTagKeys(tagKeys);
        }
        // 处理卡券内容
        for (MktActivityCouponOnList coupon : info.getCoupons())
        {
            // 优惠券
            if (CouponType.CARD == coupon.getCouponType())
            {
                MktCard card = cardDao.getCard(coupon.getCoupon());
                if (card != null)
                {
                    coupon.setCouponTitle(card.getTitle());
                    coupon.setEffective(card.getEffective());
                    coupon.setStartDate(card.getStartDate());
                    coupon.setEndDate(card.getEndDate());
                    coupon.setCouponCount(card.getCount());
                    coupon.setCouponIssuedNum(card.getIssuedNum());
                }
            }
            // 礼品券
            else
            {
                MktGoodsGift gift = giftDao.get(coupon.getCoupon());
                if (gift != null)
                {
                    coupon.setCouponTitle(gift.getTitle());
                    coupon.setEffective(gift.getEffective());
                    coupon.setStartDate(gift.getStartDate());
                    coupon.setEndDate(gift.getEndDate());
                    coupon.setCouponCount(gift.getCount());
                    coupon.setCouponIssuedNum(gift.getIssuedNum());
                }
            }
        }
        return info;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public MktActivity save(MktActivityInfo info)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String farmer = CurrentSession.marketPkey();
        String company = CurrentSession.companyPkey();
        info.checkLimit4Save();
        MktActivity activity = null;
        // 新增
        if (info.getPkey() == null)
        {
            activity = BeanUtil.beanFrom(MktActivity.class, info);
            activity.setEnabled(false);
            activity.setAscription(ascription);
            activity.setFarmer(farmer);
            activity.setCompany(company);
            activity.setIssuedNum(0);
            activity.setReceiveNum(0);
            activity.setUseNum(0);
        }
        // 编辑
        else
        {
            activity = activityDao.get(info.getPkey());
            if (activity == null) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
            if (!Objects.equals(activity.getAscription(), ascription) || !Objects.equals(activity.getFarmer(), farmer))
                throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
            BeanUtils.copyProperties(info,
                activity,
                MktActivity.F.distributeType,
                MktActivity.F.ascription,
                MktActivity.F.farmer,
                MktActivity.F.company,
                MktActivity.F.enabled,
                MktActivity.F.issuedNum);
        }
        // 限制特殊处理
        if (Boolean.TRUE.equals(info.getIsNoLimitDailyNum())) activity.setLimitDailyNum(-1);
        if (Boolean.TRUE.equals(info.getIsNoLimitDailyCardNum())) activity.setLimitDailyCardNum(-1);
        if (Boolean.TRUE.equals(info.getIsNoLimitDailyGiftNum())) activity.setLimitDailyGiftNum(-1);
        // 处理活动关联卡券
        List<MktActivityCoupon> activityCoupons = Lists.newArrayListWithCapacity(info.getCoupons().size());
        int couponNum = 0;
        for (MktActivityCouponOnList coupon : info.getCoupons())
        {
            MktActivityCoupon ac = BeanUtil.beanFrom(MktActivityCoupon.class, coupon);
            activityCoupons.add(ac);
            couponNum += ac.getNum();
        }
        activity.setCouponNum(couponNum);
        activity = activityDao.put(activity);
        for (MktActivityCoupon ac : activityCoupons)
        {
            ac.setActivity(activity.getPkey());
        }
        activityCouponDao.removeAllByActivity(activity.getPkey());
        activityCouponDao.putAll(activityCoupons);
        if(info.getTagKeys() != null && !info.getTagKeys().isEmpty())
        {
            tagManager.putTagVisibles(TagVisibleTargetType.ACTIVITY, activity.getPkey().longValue(), info.getTagKeys(), ascription);
        }
        return activity;
    }
    
    public boolean enable(Integer pkey, Boolean enabled)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String farmer = CurrentSession.marketPkey();
        MktActivity activity = activityDao.get(pkey);
        if (activity == null) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
        if (!Objects.equals(activity.getAscription(), ascription) || !Objects.equals(activity.getFarmer(), farmer))
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        // 没有卡券了，不能启用
        if (Boolean.TRUE.equals(enabled) && !activityCouponDao.existByActivity(pkey))
            throw TofocusException.of(LejiaErrCode.ACTIVITY_HAS_NO_COUPON);
        activityDao.enable(pkey, enabled);
        return true;
    }
    
    public boolean qrCode(Integer pkey, HttpServletRequest request, HttpServletResponse response)
    {
        BufferedImage img = null;
        MktActivity activity = activityDao.get(pkey);
        if (activity == null) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
        try
        {
            img = FileUtil.createImage(activityUrl + "_" + activity.getAscription() + "?pkey=" + pkey, 500, 500);
        }
        catch (Exception e)
        {
            log.error("卡券活动（{}）生成二维码失败", pkey, e);
        }
        FileUtil.buildExcelDocument(activity.getName() + "-活动", img, request, response);
        return true;
    }
    
    public boolean popUpQrCode(Integer pkey, HttpServletRequest request, HttpServletResponse response)
    {
        BufferedImage img = null;
        MktActivity activity = activityDao.get(pkey);
        if (activity == null) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
        try
        {
            img =
                FileUtil.createImage(activityPopUpUrl + "_" + activity.getAscription() + "?activity=" + pkey, 500, 500);
        }
        catch (Exception e)
        {
            log.error("卡券活动（{}）生成弹框二维码失败", pkey, e);
        }
        FileUtil.buildExcelDocument(activity.getName() + "-弹框活动", img, request, response);
        return true;
    }
    
    public void removeCouponFromActivity(CouponType couponType, Integer couponPkey)
    {
        // 加入活动了，将卡券移出活动（如果活动没卡券了则停用）
        if (activityCouponDao.existByCoupon(couponType, couponPkey))
        {
            List<MktActivityCoupon> list = activityCouponDao.listByCoupon(couponType, couponPkey);
            Set<Integer> activities = list.stream().map(MktActivityCoupon::getActivity).collect(Collectors.toSet());
            for (Integer activity : activities)
            {
                // 活动除了当前卡券，没有其他卡券则停用
                if (!activityCouponDao.existByActivityNotEqCoupon(activity, couponType, couponPkey))
                    activityDao.enable(activity, false);
            }
            activityCouponDao.removeAll(list);
        }
    }
}
