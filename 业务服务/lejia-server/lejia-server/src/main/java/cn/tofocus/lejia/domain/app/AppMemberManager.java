package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.transaction.Transactional;

import cn.tofocus.lejia.bean.entity.market.MktManager;
import cn.tofocus.lejia.dao.market.MktManagerDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.DateUtils;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.WeixinConfig;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberCentreDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberCentreMsdLine;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberSignOnList;
import cn.tofocus.lejia.bean.dto.app.market.AppOrderStatusNum;
import cn.tofocus.lejia.bean.dto.app.market.MktAppCardDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberCardOnList;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDrawOnList;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.market.MktMemberOnList;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktDrawPrize;
import cn.tofocus.lejia.bean.entity.market.MktDrawWin;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberCommLine;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.entity.member.MktMemberPay;
import cn.tofocus.lejia.bean.entity.member.MktMemberSign;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.bean.enums.MemberPType;
import cn.tofocus.lejia.bean.enums.MemberStatus;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.PayStatus;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktDrawPrizeDao;
import cn.tofocus.lejia.dao.market.MktDrawWinDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktMemberCommLineDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberGiftDao;
import cn.tofocus.lejia.dao.market.MktMemberMsdDao;
import cn.tofocus.lejia.dao.market.MktMemberMsdLineDao;
import cn.tofocus.lejia.dao.market.MktMemberPayDao;
import cn.tofocus.lejia.dao.market.MktMemberSignDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.market.MemberCommManager;
import cn.tofocus.lejia.domain.market.MemberManager;
import cn.tofocus.lejia.domain.market.MemberPointManager;
import cn.tofocus.lejia.domain.market.mall.AppConfigManager;
import cn.tofocus.lejia.domain.pay.ChinaUmsPayManager;
import cn.tofocus.lejia.domain.pay.NsPayManager;
import cn.tofocus.lejia.domain.pay.WxPayManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.wx.PayJs;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppMemberManager
{
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MemberPointManager memberPointManager;
    
    @Autowired
    private MktMemberSignDao memberSignDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MemberCommManager memberCommManager;
    
    @Autowired
    private MktMemberCommLineDao memberCommLineDao;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktMemberGiftDao memberGiftDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private AppConfigManager appConfigManager;
    
    @Autowired
    private MktDrawWinDao drawWinDao;
    
    @Autowired
    private MktDrawPrizeDao drawPrizeDao;
    
    @Autowired
    private MemberManager memberManager;
    
    @Autowired
    private WxPayManager wxPayManager;
    
    @Autowired
    private AppConfigManager configManager;
    
    @Autowired
    private AppMemberPayManager memberPayManager;
    
    @Autowired
    private MktMemberPayDao memberPayDao;

    @Autowired
    private MktManagerDao managerDao;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private NsPayManager nsPayManager;
    
    @Autowired
    private SaasTokenPublicManager saasTokenPublicManager;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Autowired
    private MktMemberMsdLineDao memberMsdLineDao;
    
    @Autowired
    private ChinaUmsPayManager chinaUmsPayManager;
    
    @Value("${xasz.saas.token.member.url:https://cloud.xinanshizu.com/farm-member}")
    private String prefixUrl;
    
    public WxPayData beforeMemberPay(BigDecimal amt, MemberPType memberPType, PayType payType)
    {
        WxPayData wxPayData = new WxPayData();
        try
        {
            Integer appid = MobileSession.appid();
            MktMemberPay payOrder =
                memberPayManager.createdOrder(MobileSession.memberPkey(), amt, memberPType, payType, appid);
            if (memberPType.equals(MemberPType.ANNUAL_FEE)) amt = configManager.getAppConfig().getMemberPriceN();
            if (appid.equals(1))
            {
                wxPayData = nsPayManager.topayIvc(MobileSession.openid(), payOrder.getOrderNumber(), amt);
            }
            else if (appid.equals(13))
            {
                wxPayData = chinaUmsPayManager.chinaUmsPay(MobileSession.openid(), payOrder.getOrderNumber(), amt);
            }
            else
            {
                WeixinConfig wxc = ascriptionDao.getWxConfig(appid);
                PayJs payJs = wxPayManager
                    .topayIvc(MobileSession.billIp(), MobileSession.openid(), payOrder.getOrderNumber(), amt, wxc);
                wxPayData = BeanUtil.beanFrom(WxPayData.class, payJs);
            }
        }
        catch (Exception e)
        {
            throw TofocusException.of(LejiaErrCode.WRONG_WEPAY);
        }
        return wxPayData;
    }
    
    public void payOrder(String payNumber, Boolean success)
    {
        MktMemberPay order = memberPayDao.selectOne().start("orderNumber", payNumber).exec();
        if (order != null)
        {
            if (success)
            {
                order.setStatus(PayStatus.PAYMENT_SUCCESSFUL);
                if (order.getPType().equals(MemberPType.ANNUAL_FEE))
                {
                    memberManager.openMember(order.getMember());
                }
                else if (order.getPType().equals(MemberPType.RECHARGE))
                {
                    memberCommManager.updComm(order.getMember(),
                        order.getAmt(),
                        true,
                        CommSourceType.RECHARGE,
                        order.getOrderNumber(),
                        order.getAscription());
                }
            }
            else
            {
                order.setStatus(PayStatus.PAYMENT_FAILED);
            }
            order.setPayTime(new Date());
            memberPayDao.update(order);
            
            
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
                    try
                    {
                        // 会员充值后 微信发货
                        StringBuilder sb = new StringBuilder();
                        sb.append("充值已到账：");
                        sb.append(order.getAmt());
                        sb.append("元");
                        String itemDesc = sb.toString();
                        if(itemDesc.length() > 120)
                            itemDesc = itemDesc.substring(0, 120);
                        String openid = null;
                        String mchid = null;
                        SysAscription sysAscription = ascriptionDao.get(order.getAscription());
                        if(sysAscription != null)
                        {
                            mchid = sysAscription.getConfigMchid();
                        }
                        MktMember mktMember = memberDao.get(order.getMember());
                        if(mktMember != null)
                            openid = mktMember.getOpenid1();
                        if(openid != null && mchid != null)
                        {
                            wxManager.uploadShippingInfo(
                                null,
                                order.getOrderNumber() + "1",
                                mchid,
                                itemDesc,
                                3,
                                null,
                                null,
                                null,
                                null,
                                openid,
                                order.getAscription());
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
    }
    
    public MktMemberOnList getMktMemberOnList(Integer pkey)
    {
        MktMemberOnList res = BeanUtil.beanFrom(MktMemberOnList.class, memberDao.get(pkey));
        Integer memberPkey = MobileSession.memberPkey();
        long count = memberCardDao.aggregation()
            .eq("member", pkey)
            .eq("status", CardStatus.UNUSED)
            .or()
            .isNull("userFarmer")
            .eq("userFarmer", memberPkey)
            .close()
            .done()
            .execCount();
        long giftCount = memberGiftDao.aggregation()
            .eq("member", pkey)
            .eq("status", CardStatus.UNUSED)
            .or()
            .isNull("userFarmer")
            .eq("userFarmer", memberPkey)
            .close()
            .done()
            .execCount();
        int num = (int)(count + giftCount);
        res.setCardNum(num);
        return res;
    }
    
    public AppMemberSignOnList queryMemberPoints(String signMonth)
    {
        AppMemberSignOnList result = new AppMemberSignOnList();
        Integer memberPkey = MobileSession.memberPkey();
        Calendar date = Calendar.getInstance();
        Integer year = date.get(Calendar.YEAR);
        Integer month = date.get(Calendar.MONTH) + 1;
        if (StringUtils.isNotBlank(signMonth) && signMonth.contains("-"))
        {
            String[] split = signMonth.split("-");
            if(split.length > 1)
            {
                year = Integer.valueOf(split[0]);
                month = Integer.valueOf(split[1]);
            }
        }
        log.info("year: {}, month: {}", year, month);
        Boolean nowDays = false;
        List<MktMemberSign> signs = memberSignDao.getSigns(memberPkey, year, month);
        if (signs.size() == 0)
            result.setSignNum(0);
        else
        {
            Integer signNum = memberSignDao.getSignNum(memberPkey);
            result.setSignNum(signNum);
            for (MktMemberSign sign : signs)
            {
                Integer signDay = Integer.valueOf(DateUtil.formatDate(sign.getSignDate(), "dd"));
                result.getSignDates().add(signDay);
            }
        }
        
        List<MktMemberSign> verificationSigns = memberSignDao.getSignsDate(memberPkey, new Date());
        if (verificationSigns.size() > 0) nowDays = true;
        
        result.setNowDays(nowDays);
        result.setPoints(memberPointManager.loadPoints(memberPkey.intValue()));
        return result;
    }
    
    @Transactional
    public Boolean insMemberPoints()
    {
        Integer memberPkey = MobileSession.memberPkey();
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();
        List<MktMemberSign> toDaySign = memberSignDao.getSignsDate(memberPkey, today);
        if (toDaySign.size() > 0) throw TofocusException.of(WsaleErrCode.CHECKED_IN);
        List<MktMemberSign> yesToDaySign = memberSignDao.getSignsDate(memberPkey, yesterday);
        System.out.println("yesToDaySin:" + yesToDaySign.size());
        MktAppConfig config = appConfigManager.getAppConfig();
        Integer pointsQd = config.getPointsQd();
        Integer pointsQdDz = config.getPointsQdDz();
        Integer pointsQdSx = config.getPointsQdSx();
        if (yesToDaySign.size() == 0)
        {
            MktMemberSign sign = new MktMemberSign();
            sign.setMember(memberPkey);
            sign.setSignDate(new Date());
            sign.setSignNum(1);
            sign.setPoints(pointsQd);
            memberSignDao.add(sign);
            memberPointManager
                .updPoint(memberPkey, pointsQd, true, SourceType.POINTS_SIGN_DAY, "sign", "", MobileSession.appid());
            return true;
        }
        MktMemberSign yestay = yesToDaySign.get(0);
        MktMemberSign sign = new MktMemberSign();
        int point = 0;
        if (yestay.getSignNum().intValue() >= pointsQdSx)
            point = pointsQdDz * (pointsQdSx - 1) + pointsQd;
        else
            point = pointsQd + pointsQdDz * (yestay.getSignNum().intValue());
        sign.setPoints(point);
        sign.setMember(memberPkey);
        sign.setSignDate(new Date());
        sign.setSignNum(yestay.getSignNum() + 1);
        sign.setAscription(MobileSession.appid());
        memberSignDao.add(sign);
        memberPointManager
            .updPoint(memberPkey, point, true, SourceType.POINTS_SIGN_DAY, "sign", "", MobileSession.appid());
        return true;
    }
    
    public AppMemberCentreDTO getMemberCentre()
    {
        Integer memberPkey = MobileSession.memberPkey();
        AppMemberCentreDTO result = new AppMemberCentreDTO();
        
        MktMember member = memberDao.get(memberPkey);
        result.setNickName(member.getName());
        result.setPhoto(member.getPhoto());
        result.setStatus(member.getStatus());
        String mobile = member.getMobile();
        result.setMobile(mobile);
        StringBuffer buffer = new StringBuffer(mobile);
        result.setHideMobile(buffer.replace(3, 7, "****").toString());
        result.setLevel(member.getLevel());
        List<MktMemberSign> signsDate = memberSignDao.getSignsDate(memberPkey, null);
        if (signsDate != null && !signsDate.isEmpty())
        {
            Calendar cal = Calendar.getInstance();
            String nowFor = DateUtils.format(cal.getTime(), "yyyy-MM-dd");
            cal.add(Calendar.DATE, -1);
            String format = DateUtils.format(cal.getTime(), "yyyy-MM-dd");
            String signDate = DateUtils.format(signsDate.get(0).getSignDate(), "yyyy-MM-dd");
            if (format.equals(signDate) || nowFor.equals(signDate))
                result.setSignNum(signsDate.get(0).getSignNum());
            else
                result.setSignNum(0);
        }
        else
        {
            result.setSignNum(0);
        }
        
        result.setAcceptCardNum(cardDao.getCardNum(memberPkey));
        
        result.setComms(memberCommManager.loadComm(memberPkey));
        // 2024-07-25 东屿农贸市场上线使用  使用云农贸的钱包 进行修改
//        BigDecimal comms = appOrderV2Manager.checkNmMemberPay();
//        if(comms == null)
//            comms = BigDecimal.ZERO;
//        result.setComms(comms);
        
        result.setPoints(memberPointManager.loadPoints(memberPkey));
        int cardNum = memberCardDao.getMemberCardCount(memberPkey);
        int giftNum = memberGiftDao.countByMember(memberPkey);
        result.setCardNum(cardNum + giftNum);
        Map<String, Long> map = orderDao.aggregation().eq("member", memberPkey).execGroupByCount("status", "pkey");
        for (Entry<String, Long> e : map.entrySet())
        {
            String key = e.getKey();
            if (key.equals("0")) result.setUnpaidOrderNum(e.getValue().intValue());
        }
        // 设置云农贸账户余额
        result.setXaszComms(BigDecimal.ZERO);
        try
        {
            BigDecimal xaszComms = saasTokenPublicManager.getAccountBalance(mobile, member.getOpenid1());
            result.setXaszComms(xaszComms);
        }
        catch (Exception e)
        {
            log.debug("获取农贸会员卡余额失败报错; {}", e.getMessage());
        }
        result.setNowDays(false);
        List<MktMemberSign> verificationSigns = memberSignDao.getSignsDate(memberPkey, new Date());
        if (!verificationSigns.isEmpty()) result.setNowDays(true);

        // 是否允许分发活动
        MktManager manager = managerDao.getByMobileAndFarmer(member.getMobile(), MobileSession.farmerPkey(), member.getAscription());
        result.setAllowedDistributeActivity(manager != null);
        result.setIsMsd(false);
        result.setMsdBalance(BigDecimal.ZERO);
        // 查看是否是民生豆用户
        MktMemberMsd memberMsd = memberMsdDao.get(memberPkey);
        if(memberMsd != null)
        {
            result.setIsMsd(true);
            result.setMsdBalance(memberMsd.getBalance());
        }
        return result;
    }
    
    public AppOrderStatusNum getOrderStatusNum()
    {
        AppOrderStatusNum res = new AppOrderStatusNum();
        Integer memberPkey = MobileSession.memberPkey();
        Map<String, Long> map = orderDao.aggregation()
        .eq("member", memberPkey)
        .in("status", 
            OrderStatus.UNPAID_ORDER, 
//            OrderStatus.PAYING_ORDER, 
            OrderStatus.DELIVERED_ORDER,
            OrderStatus.SHIPPED_ORDER,
            OrderStatus.WAIT_ARRIVAL_ORDER,
            OrderStatus.WAIT_WRITEOFF_ORDER,
            OrderStatus.ARRIVED_ORDER,
            OrderStatus.CONFIRM_ORDER)
        .execGroupByCount("status", "pkey");
        for(String k : map.keySet())
        {
            if(OrderStatus.UNPAID_ORDER.name().equals(k))
            {
                res.setUnpaidNum(res.getUnpaidNum() + map.get(k));
            }
            if(OrderStatus.DELIVERED_ORDER.name().equals(k))
            {
                res.setDeliveredNum(res.getDeliveredNum() + map.get(k));
            }
            if(OrderStatus.SHIPPED_ORDER.name().equals(k) || OrderStatus.ARRIVED_ORDER.name().equals(k))
            {
                res.setShippedNum(res.getShippedNum() + map.get(k));
            }
        }
        res.setRefundedNum(orderRefundDao.countApplying(memberPkey));
        orderRefundDao.countApplying(memberPkey);
        return res;
    }
    
    public PageResult<AppMemberCentreMsdLine> queryMsdLine(int page, int pagesize)
    {
        PageResult<AppMemberCentreMsdLine> res = memberMsdLineDao.selectPage().page(page)
        .pagesize(pagesize)
        .eq("member", MobileSession.memberPkey())
        .sort("createdTime")
        .execDto(AppMemberCentreMsdLine.class);
        for(AppMemberCentreMsdLine d : res.getContent())
        {
            if(StringUtils.isBlank(d.getRemark()))
            {
                d.setRemark(d.getFormId());
            }
        }
        return res;
    }
    
    public List<MktAppMemberCardOnList> getMemberCard()
    {
        Integer memberPkey = MobileSession.memberPkey();
        List<MktMemberCard> list =
            memberCardDao.select().eq("member", memberPkey).eq("status", CardStatus.UNUSED).sort("endDate").exec();
        List<MktAppMemberCardOnList> result = BeanUtil.beanListFrom(MktAppMemberCardOnList.class, list);
        for (MktAppMemberCardOnList bean : result)
        {
            MktCard card = cardDao.get(bean.getCard());
            bean.setDetail(BeanUtil.beanFrom(MktAppCardDetailsDTO.class, card));
        }
        return result;
    }
    
    public Boolean upd(String photo, String name)
    {
        Integer memberPkey = MobileSession.memberPkey();
        MktMember member = memberDao.get(memberPkey);
        if(StringUtils.isNotBlank(photo))
            member.setPhoto(photo);
        if(StringUtils.isNotBlank(name))
            member.setName(name);
        memberDao.update(member);
        return true;
    }
    
    
    
    public PageResult<MktAppMemberDrawOnList> getMemberDraw(Integer page, Integer pagesize)
    {
        Integer memberPkey = MobileSession.memberPkey();
        PageResult<MktDrawWin> pageList = drawWinDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("member", memberPkey)
            .sort("createdTime", true)
            .exec();
        
        PageResult<MktAppMemberDrawOnList> result = BeanUtil.beanPageFrom(MktAppMemberDrawOnList.class, pageList);
        for (MktAppMemberDrawOnList bean : result)
        {
            MktDrawPrize prize = drawPrizeDao.get(bean.getPrize());
            bean.setName(prize.getName());
            bean.setPhoto(prize.getPhoto());
            bean.setPvalue(prize.getPvalue());
            // 如果中奖奖品是实物 并且还没发货  isAddr true 说明需要用户填写地址 false不需要
            if (bean.getStatus().getIndex() == 0 && bean.getPType().getIndex() == 2
                && StringUtils.isBlank(bean.getAddr()))
                bean.setIsAddr(true);
            else
                bean.setIsAddr(false);
        }
        return result;
    }
    
    public Boolean ins(String custCard, String custName, String accountBank)
    {
        MktMember member = MobileSession.member();
        if (member == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        member.setCustCard(custCard);
        member.setAccountBank(accountBank);
        member.setCustName(custName);
        memberDao.update(member);
        return true;
    }
    
    public List<MktAppMemberDetailsDTO> listTjr()
    {
        List<MktAppMemberDetailsDTO> rs = new ArrayList<>();
        List<MktMember> list = memberDao.select().eq("tjr", MobileSession.memberPkey()).exec();
        for (MktMember mem : list)
        {
            MktAppMemberDetailsDTO line = BeanUtil.beanFrom(MktAppMemberDetailsDTO.class, mem);
            MktMemberCommLine comLine = memberCommLineDao.selectOne()
                .eq("member", MobileSession.memberPkey())
                .eq("source", CommSourceType.SHARE_NEW)
                .eq("formId", mem.getPkey() + "")
                .exec();
            if (comLine != null) line.setTjComm(comLine.getComms());
            rs.add(line);
        }
        return rs;
    }
    
    public Boolean logOut()
    {
        MktMember member = MobileSession.member();
        member.setStatus(MemberStatus.LOG_OUTING);
        member.setLogOutTime(new Date());
        memberDao.put(member);
        return true;
    }
    
    public Boolean cancelLogOut()
    {
        MktMember member = MobileSession.member();
        member.setStatus(MemberStatus.NORMAL);
        member.setLogOutTime(null);
        memberDao.put(member);
        return true;
    }
    
}
