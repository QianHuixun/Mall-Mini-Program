package cn.tofocus.lejia.domain.v2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.excel.ExcelUtil;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.dto.DtoEnhance;
import cn.tofocus.lejia.bean.dto.app.AppGiftV2ForPublicWriteOff;
import cn.tofocus.lejia.bean.dto.app.AppGiftV2ForWriteOff;
import cn.tofocus.lejia.bean.dto.excel.market.ExportMktMemberGiftUse;
import cn.tofocus.lejia.bean.dto.market.CardStatisticsInfo;
import cn.tofocus.lejia.bean.dto.v2.gift.MemberGiftV2OnList;
import cn.tofocus.lejia.bean.dto.v2.gift.MktGiftV2Info;
import cn.tofocus.lejia.bean.dto.v2.gift.MktGiftV2OnPage;
import cn.tofocus.lejia.bean.dto.v2.gift.MktMemberGiftV2OnPage;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsGift;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktManager;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberGift;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CouponExpireChoose;
import cn.tofocus.lejia.bean.enums.CouponType;
import cn.tofocus.lejia.bean.enums.GiftType;
import cn.tofocus.lejia.bean.enums.MemberStatus;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsGiftDao;
import cn.tofocus.lejia.dao.market.MktActivityCouponDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberGiftDao;
import cn.tofocus.lejia.dao.market.MktManagerDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.domain.market.MktActivityManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.utils.DateUtil;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GiftV2Manager
{
    @Autowired
    private MktGoodsGiftDao giftDao;
    
    @Autowired
    private MktMemberGiftDao memberGiftDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private DtoEnhance dtoEnhance;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktManagerDao managerDao;
    
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private MktActivityCouponDao activityCouponDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;

    @Autowired
    private MktActivityManager activityManager;
    
    public PageResult<MktGiftV2OnPage> query(int page, int pagesize, String title, Boolean enabled, Boolean invalid)
    {
        String farmerPkey = CurrentSession.marketPkey();
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        PageResult<MktGiftV2OnPage> result =
            giftDao.query(page, pagesize, title, enabled, invalid, farmerPkey, ascriptionPkey, MktGiftV2OnPage.class);
        for (MktGiftV2OnPage gift : result)
        {
            gift.setIsInActivity(activityCouponDao.existByCoupon(CouponType.GIFT, gift.getPkey()));
        }
        return result;
    }
    
    public PageResult<MktMemberGiftV2OnPage> queryUse(int page, int pagesize, String userFarmer, String startTime, String endTime,
        String st, String et, String mobile, String title, CardStatus status, Boolean invalid)
    {
        String farmerPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<Integer> members = null;
        if (StringUtil.isNotBlank(mobile))
        {
            members = memberDao.listPkeys(ascription, mobile);
            if (CollectionUtil.isEmpty(members))
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        List<Integer> gifts = null;
        if (StringUtil.isNotBlank(title))
        {
            gifts = giftDao.listPkeys(ascription, farmerPkey, title);
            if (CollectionUtil.isEmpty(gifts))
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        PageResult<MktMemberGiftV2OnPage> result = memberGiftDao.query(page,
            pagesize,
            userFarmer,
            DateUtil.atStartOfDay(startTime),
            DateUtil.atStartOfNextDay(endTime),
            DateUtil.atStartOfDay(st),
            DateUtil.atStartOfNextDay(et),
            members,
            gifts,
            farmerPkey,
            status,
            invalid,
            ascription,
            MktMemberGiftV2OnPage.class);
        for (MktMemberGiftV2OnPage dto : result)
        {
            if (StringUtil.isNotBlank(dto.getUserFarmer()))
            {
                SysFarmer farmer = sysFarmerDao.get(dto.getUserFarmer());
                if (farmer != null) dto.setUserFarmerName(farmer.getName());
            }
        }
        return result;
    }
    
    public CardStatisticsInfo queryUseSum(String userFarmer, String startTime, String endTime,
        String st, String et, String mobile, String title, CardStatus status, Boolean invalid)
    {
        CardStatisticsInfo info = new CardStatisticsInfo();
        info.setSum(0l);
        info.setUnusedNum(0l);
        info.setUsedNum(0l);
        info.setExpiredNum(0l);
        info.setInvalidNum(0l);
        String farmerPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<Integer> members = null;
        if (StringUtil.isNotBlank(mobile))
        {
            members = memberDao.listPkeys(ascription, mobile);
            if (CollectionUtil.isEmpty(members))
                return info;
        }
        List<Integer> gifts = null;
        if (StringUtil.isNotBlank(title))
        {
            gifts = giftDao.listPkeys(ascription, farmerPkey, title);
            if (CollectionUtil.isEmpty(gifts))
                return info;
        }
        List<CardStatisticsInfo> list = memberGiftDao.sum(
            userFarmer,
            DateUtil.atStartOfDay(startTime),
            DateUtil.atStartOfNextDay(endTime),
            DateUtil.atStartOfDay(st),
            DateUtil.atStartOfNextDay(et),
            members,
            gifts,
            farmerPkey,
            status,
            invalid,
            ascription);
        if(list.isEmpty())
            return info;
        for(CardStatisticsInfo cs : list)
        {
            info.setSum(info.getSum() + cs.getSum());
            if(Boolean.FALSE.equals(cs.getInvalid()))
            {
                switch (cs.getStatus())
                {
                    case UNUSED:
                        info.setUnusedNum(info.getUnusedNum() + cs.getSum());
                        break;
                    case USED:
                        info.setUsedNum(info.getUsedNum() + cs.getSum());
                        break;
                    case EXPIRED:
                        info.setExpiredNum(info.getExpiredNum() + cs.getSum());
                        break;
                }
            }
            else
            {
                info.setInvalidNum(info.getInvalidNum() + cs.getSum());
            }
        }
        return info;
    }
    
    public void exportUse(String userFarmer, String startTime, String endTime,
        String st, String et, String mobile, String title, CardStatus status, Boolean invalid, HttpServletResponse response)
    {
        try
        {
            PageResult<MktMemberGiftV2OnPage> pageResult = queryUse(0, 10000, userFarmer, startTime, endTime, st, et, mobile, title, status, invalid);
            List<ExportMktMemberGiftUse> list =
                BeanUtil.beanListFrom(ExportMktMemberGiftUse.class, pageResult.getContent());
            CardStatisticsInfo sumInfo = queryUseSum(userFarmer, startTime, endTime, st, et, mobile, title, status, invalid);
            StringBuilder sb = new StringBuilder();
            sb.append("合计    总数：");
            sb.append(sumInfo.getSum());
            sb.append("张  未使用：");
            sb.append(sumInfo.getUnusedNum());
            sb.append("张  已使用：");
            sb.append(sumInfo.getUsedNum());
            sb.append("张  已过期：");
            sb.append(sumInfo.getExpiredNum());
            sb.append("张");
            ExcelUtil.exportExcel(list,
                "礼品券使用记录",
                response.getOutputStream(),
                ExportMktMemberGiftUse.class,
                new String[] {"礼品券使用记录", sb.toString()});
        }
        catch (Exception e)
        {
            log.error("导出礼品券使用记录失败", e);
        }
    }
    
    public MktGiftV2Info get(Integer pkey)
    {
        return giftDao.get(pkey, MktGiftV2Info.class);
    }
    
    public boolean save(MktGiftV2Info info)
    {
        MktGoodsGift gift = null;
        // 新增
        if (info.getPkey() == null)
        {
            gift = BeanUtil.beanFrom(MktGoodsGift.class, info);
            gift.setGiftType(GiftType.NORMAL);
            gift.setEnabled(true);
            gift.setInvalid(false);
            gift.setFarmer(CurrentSession.marketPkey());
            gift.setCompany(CurrentSession.companyPkey());
            gift.setAscription(CurrentSession.ascriptionPkey());
        }
        // 编辑
        else
        {
            gift = giftDao.get(info.getPkey());
            if (gift == null) throw TofocusException.of(LejiaErrCode.GIFT_NOT_FOUND);
            BeanUtils.copyProperties(info, gift);
        }
        // 指定天数
        if (info.getEffective() != null)
        {
            gift.setExpireChoose(CouponExpireChoose.DAYS);
            gift.setStartDate(null);
            gift.setEndDate(null);
        }
        // 指定日期
        else
        {
            gift.setExpireChoose(CouponExpireChoose.DATE_RANGE);
            gift.setEffective(null);
        }
        giftDao.put(gift);
        return true;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean invalid(Integer pkey)
    {
        MktGoodsGift gift = giftDao.get(pkey);
        if (gift == null) throw TofocusException.of(LejiaErrCode.GIFT_NOT_FOUND);
        gift.setInvalid(true);
        // 加入活动了，将卡券移出活动（如果活动没卡券了则停用）
        activityManager.removeCouponFromActivity(CouponType.GIFT, pkey);
        giftDao.put(gift);
        // 将用户领取的礼品券失效
        memberGiftDao.invalidUnusedByGift(pkey);
        return true;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean enable(Integer pkey, boolean enabled)
    {
        MktGoodsGift gift = giftDao.get(pkey);
        if (gift == null) throw TofocusException.of(LejiaErrCode.GIFT_NOT_FOUND);
        if (enabled && Boolean.TRUE.equals(gift.getInvalid())) throw TofocusException.of(LejiaErrCode.GIFT_INVALID);
        // 如果禁用，检查有没有加入活动
        if (!enabled)
        {
            // 加入活动了，将卡券移出活动（如果活动没卡券了则停用）
            activityManager.removeCouponFromActivity(CouponType.GIFT, pkey);
        }
        gift.setEnabled(enabled);
        giftDao.put(gift);
        return true;
    }
    
    public void expiredTask(Date taskDate)
    {
        memberGiftDao.expiredLtEndDate(taskDate);
    }
    
    public AppGiftV2ForWriteOff load4WriteOff(String cardNumber)
    {
        MktMemberGift ent = memberGiftDao.getByCardNumber(cardNumber);
        check4WriteOff(ent, false);
        AppGiftV2ForWriteOff result = BeanUtil.beanFrom(AppGiftV2ForWriteOff.class, ent);
        dtoEnhance.deal(AppGiftV2ForWriteOff.class, result);
        return result;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean writeOff(String cardNumber)
    {
        MktMemberGift ent = memberGiftDao.getByCardNumber(cardNumber);
        MktGoodsGift gift = check4WriteOff(ent, false);
        ent.setUserTime(new Date());
        ent.setStatus(CardStatus.USED);
        memberGiftDao.update(ent);
        giftDao.updUsedNum(gift.getPkey(), gift.getUsedNum() + 1);
        
        
        if(ent.getActivity() != null)
        {
            MktActivity mktActivity = activityDao.get(ent.getActivity());
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
    
    public AppGiftV2ForPublicWriteOff load4PublicWriteOff(Integer pkey)
    {
        MktMemberGift ent = memberGiftDao.get(pkey);
        if (ent == null) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券不存在");
        AppGiftV2ForPublicWriteOff result = BeanUtil.beanFrom(AppGiftV2ForPublicWriteOff.class, ent);
        
        MktGoodsGift gift = giftDao.get(ent.getGift());
        if (gift == null || Boolean.TRUE.equals(gift.getInvalid())) ent.setInvalid(false);
        
        dtoEnhance.deal(AppGiftV2ForPublicWriteOff.class, result);
        return result;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean publicWriteOff(Integer pkey, String password)
    {
        MktMemberGift ent = memberGiftDao.get(pkey);
        MktGoodsGift gift = check4WriteOff(ent, true);
        if (!"9978".equals(password)) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "核销口令错误");
        ent.setUserTime(new Date());
        ent.setStatus(CardStatus.USED);
        memberGiftDao.update(ent);
        giftDao.updUsedNum(gift.getPkey(), gift.getIssuedNum() + 1);
        return true;
    }
    
    private MktGoodsGift check4WriteOff(MktMemberGift ent, boolean isPublic)
    {
        if (ent == null) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券不存在");
        MktGoodsGift gift = giftDao.get(ent.getGift());
        if (gift == null || Boolean.TRUE.equals(gift.getInvalid()) || Boolean.TRUE.equals(ent.getInvalid()))
            throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已失效");
        if (ent.getStatus() == CardStatus.USED) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已被使用过");
        Date today = DateUtil.atStartOfToday();
        if (DateUtil.compareDate(ent.getStartDate(), today) > 0)
            throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券未到使用时间");
        if (ent.getStatus() == CardStatus.EXPIRED) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已过期");
        if (DateUtil.compareDate(ent.getEndDate(), today) < 0)
            throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已过期");
        if (gift.getGiftType() != GiftType.NORMAL) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "积分商城购买的礼券需要商户核销");
        // 当前用户是否允许核销
        if (!isPublic)
        {
            MktMember member = MobileSession.member();
            if (member == null || StringUtil.isBlank(member.getMobile()))
                throw TofocusException.of(LejiaErrCode.GIFT_ERR, "无权核销该礼券");
            MktManager manager =
                managerDao.getByMobileAndFarmer(member.getMobile(), ent.getUserFarmer(), ent.getAscription());
            if (manager == null) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "无权核销该礼券");
            if (ent.getUserFarmer() == null) ent.setUserFarmer(manager.getFarmer());
        }
        // 如果是活动卡券，检查活动限制
        if (ent.getActivity() != null)
        {
            MktActivity activity = activityDao.get(ent.getActivity());
            if (activity != null && activity.getLimitDailyGiftNum() != -1)
            {
                long usedNum = memberGiftDao.countByActivity(ent.getActivity(),
                    ent.getMember(),
                    CardStatus.USED,
                    DateUtil.atStartOfToday(),
                    DateUtil.atStartOfTomorrow());
                if (usedNum >= activity.getLimitDailyGiftNum())
                    throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该活动礼品券已达到今日核销上限");
            }
        }
        return gift;
    }
    
    /**
     * 临时用于测试，发放礼品券，优先按member，其次用mobile
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean issue(Integer pkey, Integer memberPkey, String mobile, Integer num)
    {
        if (StringUtil.isBlank(mobile) && memberPkey == null)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "手机号和会员主键不能同时为空");
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        MktMember member = null;
        if (memberPkey != null)
            member = memberDao.selectOne()
                .eq(MktMember.F.pkey, memberPkey)
                .eq(MktMember.F.ascription, ascriptionPkey)
                .exec();
        else
            member =
                memberDao.selectOne().eq(MktMember.F.mobile, mobile).eq(MktMember.F.ascription, ascriptionPkey).exec();
        if (member == null) throw TofocusException.of(LejiaErrCode.MEMBER_NOT_ERROR);
        if (member.getStatus() != MemberStatus.NORMAL)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_ERROR, "会员状态异常");
        if (!Boolean.TRUE.equals(member.getEnabled()))
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_ERROR, "会员已停用");
        
        MktGoodsGift gift = giftDao.get(pkey);
        if (gift == null || !ascriptionPkey.equals(gift.getAscription()))
            throw TofocusException.of(LejiaErrCode.GIFT_NOT_FOUND);
        
        List<MktMemberGift> addGift = new ArrayList<>();
        for (int i = 0; i < num; i++)
        {
            MktMemberGift entity = makeMemberGift(member.getPkey(), gift);
            addGift.add(entity);
        }
        memberGiftDao.addAll(addGift);
        giftDao.updIssuedNum(gift.getPkey(), gift.getIssuedNum() + num);
        return true;
    }
    
    public MktMemberGift makeMemberGift(Integer memberPkey, MktGoodsGift gift)
    {
        MktMemberGift entity = new MktMemberGift();
        entity.setStatus(CardStatus.UNUSED);
        entity.setMember(memberPkey);
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
        entity.setAscription(gift.getAscription());
        return entity;
    }
    
    public List<MemberGiftV2OnList> listMemberGift(CardStatus status)
    {
        Integer memberPkey = MobileSession.memberPkey();
        List<MemberGiftV2OnList> res = memberGiftDao.listMemberGiftV2(memberPkey, status);
        List<Integer> keys = new ArrayList<>();
        res.forEach(e -> {
            keys.add(e.getGift());
        });
        
        Map<Integer, MktGoodsGift> mapGift = giftDao.getMap(keys);
        for (MemberGiftV2OnList giftDto : res)
        {
            if (mapGift.containsKey(giftDto.getGift()))
            {
                MktGoodsGift mktGift = mapGift.get(giftDto.getGift());
                giftDto.setTitle(mktGift.getTitle());
                giftDto.setUserFarmer(mktGift.getUserFarmer());
                giftDto.setPicture(mktGift.getPicture());
                giftDto.setContent(mktGift.getContent());
                if (StringUtils.isBlank(giftDto.getUserFarmer())) giftDto.setUserFarmerName(null);
            }
            Integer userVendor = giftDto.getUserVendor();
            if (userVendor != null)
            {
                MktVendor mktVendor = vendorDao.get(userVendor);
                if (mktVendor != null) giftDto.setUserVendorName(mktVendor.getDisplayName());
            }
        }
        
        return res;
    }
}
