package cn.tofocus.lejia.domain.market;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.lejia.bean.dto.MemberTagExcel;
import cn.tofocus.lejia.bean.dto.app.market.MemberDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDetailsDTO;
import cn.tofocus.lejia.bean.dto.market.MktMemberCardDTO;
import cn.tofocus.lejia.bean.dto.market.MktMemberCommLineOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberConsumption;
import cn.tofocus.lejia.bean.dto.market.MktMemberOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberPointLineOnList;
import cn.tofocus.lejia.bean.dto.market.TagOnList;
import cn.tofocus.lejia.bean.dto.order.MktOrderCountAmt;
import cn.tofocus.lejia.bean.entity.market.MktAccessLog;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberComm;
import cn.tofocus.lejia.bean.entity.member.MktMemberCommLine;
import cn.tofocus.lejia.bean.entity.member.MktMemberGift;
import cn.tofocus.lejia.bean.entity.member.MktMemberTag;
import cn.tofocus.lejia.bean.entity.member.MktTag.F;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.LoginType;
import cn.tofocus.lejia.bean.enums.MemberStatus;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.bean.excel.ExportMemberCommLine;
import cn.tofocus.lejia.bean.excel.ExportMemberInfo;
import cn.tofocus.lejia.cache.AccessMap;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktAccessLogDao;
import cn.tofocus.lejia.dao.market.MktAppConfigDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktMemberCommDao;
import cn.tofocus.lejia.dao.market.MktMemberCommLineDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberGiftDao;
import cn.tofocus.lejia.dao.market.MktMemberPointLineDao;
import cn.tofocus.lejia.dao.market.MktMemberTagDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktTagDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.domain.TagManager;
import cn.tofocus.lejia.domain.market.mall.AppConfigManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.ExportUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MemberManager
{
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MemberPointManager memberPointManager;
    
    @Autowired
    private MktMemberPointLineDao memberPointLineDao;
    
    @Autowired
    private AppConfigManager configManager;
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktMemberGiftDao memberGiftDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktMemberCommDao memberCommDao;
    
    @Autowired
    private MktMemberCommLineDao memberCommLineDao;
    
    @Autowired
    private MktAppConfigDao appConfigDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private TagManager tagManager;
    
    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private MktTagDao tagDao;
    
    public MktMember insMember(MktAppMemberDetailsDTO entity, Integer ascription)
    {
        MktMember member = BeanUtil.beanFrom(MktMember.class, entity);
        member.setEnabled(true);
        member.setLoginTime(new Date());
        member.setLoginType(LoginType.MP);
        member.setLevel(LevelType.ORDINARY_MEMBER);
        member.setAscription(ascription);
        member.setStatus(MemberStatus.NORMAL);
        if (member.getTjv() != null)
            member.setTjvTime(new Date());
        MktMember add = memberDao.add(member);
        // 赠送卡券
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                MktAppConfig config = appConfigDao.selectOne().eq("ascription", ascription).exec();
                if (config.getNewcomerCard() != null)
                {
                    for (Map<String, Integer> map : config.getNewcomerCard())
                    {
                        cardManager
                            .insMemberCard(add.getPkey(), map.get("newcomerCard"), map.get("newcomerCardNum"), null);
                    }
                }
            }
        });
        t.start();
        
        return add;
    }
    
    public MktAppMemberDetailsDTO updMember(Integer pkey, String name, String password, String photo, String idcard,
        Integer sex, String birth)
    {
        log.info("upd-pkey: {}", pkey);
        MktMember member = memberDao.get(pkey);
        if (StringUtils.isNotBlank(name))
            member.setName(name);
        if (StringUtils.isNotBlank(photo))
            member.setName(photo);
        if (StringUtils.isNotBlank(idcard))
            member.setIdcard(idcard);
        if (StringUtils.isNotBlank(birth))
            member.setBirth(birth);
        if (sex != null)
            member.setSex(sex);
        // TODO 修改密码 需要调整
        if (StringUtils.isNotBlank(password))
            member.setPassword(password);
        MktMember update = memberDao.update(member);
        return BeanUtil.beanFrom(MktAppMemberDetailsDTO.class, update);
    }
    
    // pc运营者涉及会员
    @Transactional
    public Boolean openMember(Integer pkey)
    {
        if (pkey == null)
            return false;
        MktMember member = memberDao.get(pkey);
        if (member == null)
            return false;
        if (MemberStatus.LOGGED_OUT.equals(member.getStatus()))
            throw TofocusException.of(WsaleErrCode.MEMBER_LOGGED_OUT);
        Calendar calendar = Calendar.getInstance();
        if (member.getLevel().equals(LevelType.PAID_MEMBER) && member.getEndDate() != null
            && member.getEndDate().getTime() > new Date().getTime())
        {
            calendar.setTime(member.getEndDate());
        }
        calendar.add(Calendar.YEAR, 1);
        member.setLevel(LevelType.PAID_MEMBER);
        member.setEndDate(calendar.getTime());
        memberDao.update(member);
        
        // 赠送积分和卡券
        MktAppConfig config = configManager.getAppConfig();
        if (config.getMemberPoints() != null)
        {
            memberPointManager.updPoint(pkey,
                config.getMemberPoints(),
                true,
                SourceType.POINTS_CONSUMPTION,
                DateUtil.formatDate(new Date(), "yyyyMMddHHmmss"),
                "开通年份会员赠送积分",
                CurrentSession.ascriptionPkey());
        }
        
        if (config.getMemberCard() != null)
        {
            for (Map<String, Integer> map : config.getMemberCard())
            {
                if (map.containsKey("memberCardNum"))
                {
                    for (int i = 0; i < map.get("memberCardNum"); i++)
                    {
                        cardManager.insMemberCard(pkey, map.get("memberCard"));
                    }
                }
            }
        }
        return true;
    }
    
    public Boolean adjustmentPointMember(Integer pkey, Integer point, String formid, String remark, SourceType source)
    {
        if (remark == null)
            remark = "";
        if (formid == null)
            formid = "";
        MktMember member = memberDao.get(pkey);
        if (MemberStatus.LOGGED_OUT.equals(member.getStatus()))
            throw TofocusException.of(WsaleErrCode.MEMBER_LOGGED_OUT);
        if (source.getIndex() == 3)
        {
            memberPointManager.updPoint(pkey, point, true, source, formid, remark, CurrentSession.ascriptionPkey());
        }
        if (source.getIndex() == 4)
        {
            memberPointManager.updPoint(pkey, point, false, source, formid, remark, CurrentSession.ascriptionPkey());
        }
        return true;
    }
    
    public PageResult<MktMemberOnList> queryMember(int page, int pagesize, LevelType level, String name, String mobile)
    {
        return queryMember(page, pagesize, level, name, mobile, null, null, null, null, null, null, null, null, null);
    }
    
    public PageResult<MktMemberOnList> queryMember(int page, int pagesize, LevelType level, String name, String mobile,
        String area, String remark, String startCreatedTime, String endCreatedTime, String startLastConsumeTime,
        String endLastConsumeTime, String lastConsumeFarmer, String source, List<Integer> tagKeys)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        // 模糊匹配市场
        List<String> lastConsumeFarmers = null;
        if (StringUtil.isNotBlank(lastConsumeFarmer))
        {
            lastConsumeFarmers = farmerDao.listPkeysLikeName(lastConsumeFarmer, ascriptionPkey);
            if (CollectionUtil.isEmpty(lastConsumeFarmers))
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        List<Integer> keys = new ArrayList<>();
        if (tagKeys != null && !tagKeys.isEmpty())
        {
            keys = memberTagDao.listMember(tagKeys);
            if (keys.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        PageResult<MktMemberOnList> result = memberDao.queryMember(page,
            pagesize,
            level,
            name,
            mobile,
            ascriptionPkey,
            area,
            remark,
            DateUtil.atStartOfDay(startCreatedTime),
            DateUtil.atStartOfNextDay(endCreatedTime),
            DateUtil.atStartOfDay(startLastConsumeTime),
            DateUtil.atStartOfNextDay(endLastConsumeTime),
            lastConsumeFarmers,
            source,
            keys,
            MktMemberOnList.class);
        assembleMember(result.getContent());
        return result;
    }
    
    private void assembleMember(List<MktMemberOnList> list)
    {
        List<Integer> members = list.stream().map(MktMemberOnList::getPkey).collect(Collectors.toList());
        Map<Integer, MktOrderCountAmt> consumptionMap = orderDao.consumption(members);
        for (MktMemberOnList bean : list)
        {
            int points = memberPointManager.loadPoints(bean.getPkey());
            long cardCount = memberCardDao.aggregation()
                .eq("member", bean.getPkey())
                .eq("invalid", false)
                .eq("status", CardStatus.UNUSED)
                .execCount();
            long giftCount = memberGiftDao.aggregation()
                .eq(MktMemberGift.F.member, bean.getPkey())
                .eq(MktMemberGift.F.invalid, false)
                .eq(MktMemberGift.F.status, CardStatus.UNUSED)
                .execCount();
            bean.setRemainingCard((int)(cardCount + giftCount));
            bean.setPoints(points);
            MktMemberComm comm = memberCommDao.get(bean.getPkey());
            if (comm != null)
                bean.setBalance(comm.getComms());
            else
                bean.setBalance(BigDecimal.ZERO);
            bean.setActivity("高");
            if (bean.getArea() == null)
                bean.setArea("");
            bean.setLevelName(bean.getLevel().getName());
            // 统计累计消费金额、笔数
            if (consumptionMap.containsKey(bean.getPkey()))
            {
                MktOrderCountAmt countAmt = consumptionMap.get(bean.getPkey());
                bean.setConsumeAmt(countAmt.getAmt());
                bean.setConsumeCount(countAmt.getCount());
            }
            else
            {
                bean.setConsumeAmt(BigDecimal.ZERO);
                bean.setConsumeCount(0L);
            }
            List<String> memberTagname = tagManager.getMemberTagname(bean.getPkey(), CurrentSession.ascriptionPkey());
            bean.setTagNames(memberTagname);
        }
    }
    
    public PageResult<MktMemberPointLineOnList> queryMemberPointLine(int page, int pagesize, Integer member,
        SourceType source, String mobile, String name, String startDate, String endDate, Boolean direct)
    {
        List<Integer> memberPkeys = new ArrayList<>();
        if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(mobile))
        {
            SelectBuilder<Integer, MktMember> builder = memberDao.select();
            if (StringUtils.isNotBlank(name))
                builder.like("name", name);
            if (StringUtils.isNotBlank(mobile))
                builder.like("mobile", mobile);
            List<MktMember> exec = builder.exec();
            for (MktMember m : exec)
                memberPkeys.add(m.getPkey());
            if (memberPkeys.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        PageResult<MktMemberPointLineOnList> result = BeanUtil.beanPageFrom(MktMemberPointLineOnList.class,
            memberPointLineDao.queryMemberPointLine(page,
                pagesize,
                member,
                source,
                memberPkeys,
                startDate,
                endDate,
                direct,
                CurrentSession.ascriptionPkey()));
        for (MktMemberPointLineOnList bean : result.getContent())
        {
            MktMember entity = memberDao.get(bean.getMember());
            if (entity == null)
                continue;
            bean.setMemberMobile(entity.getMobile());
            bean.setMemberName(entity.getName());
            if (StringUtils.isBlank(bean.getFormId()))
                bean.setFormId(null);
            bean.setSourceName(bean.getSource().getName());
        }
        return result;
    }
    
    public MemberDTO getMemberDTO(String openid)
    {
        MktMember member = memberDao.selectOne().or().eq("openid1", openid).eq("openid2", openid).done().exec();
        MemberDTO from = BeanUtil.beanFrom(MemberDTO.class, member);
        from.setOpenid(member.getOpenid1());
        from.setNickName(member.getName());
        from.setGender(member.getSex());
        from.setAvatarUrl(member.getPhoto());
        from.setEndDate(DateUtil.formatDate(member.getEndDate(), "yyyy-MM-dd"));
        return from;
    }
    
    public MktMember loadByOpenid(String openid, Integer ascription)
    {
        return memberDao.selectOne()
            .eq("ascription", ascription)
            .or()
            .eq("openid1", openid)
            .eq("openid2", openid)
            .done()
            .exec();
    }
    
    public MktMember loadByMobile(String mobile, Integer ascription)
    {
        return memberDao.selectOne().eq("ascription", ascription).eq("mobile", mobile).exec();
    }
    
    public void updateFarmer(String farmer)
    {
        MktMember mem = MobileSession.member();
        if (mem != null)
        {
            mem.setLastFarmer(farmer);
            memberDao.update(mem);
            //            mobileSession.setMember(mem);
        }
    }
    
    /*
     * 会员到期跑批处理
     */
    public void runCheckMember()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        List<MktMember> list =
            memberDao.select().eq("level", LevelType.PAID_MEMBER).le("endDate", calendar.getTime()).exec();
        for (MktMember line : list)
        {
            System.out.println("到期会员：" + line.getMobile());
            line.setLevel(LevelType.ORDINARY_MEMBER);
            memberDao.update(line);
        }
    }
    
    public PageResult<MktMemberConsumption> queryMemberConsumption(Integer member, int page, int pagesize)
    {
        return orderDao.queryMemberConsumption(member, page, pagesize);
    }
    
    public PageResult<MktMemberCardDTO> queryMemberCard(Integer member, int page, int pagesize)
    {
        return memberCardDao.queryMemberCardRecord(member, page, pagesize);
    }
    
    @Autowired
    private MktAccessLogDao accessLogDao;
    
    @Autowired
    private AccessMap accessMap;
    
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    public void accessNum()
        throws ParseException
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date d = cal.getTime();
        String nowTime = DateUtil.formatDate(d, "yyyy-MM-dd");
        List<SysAscription> appids = sysAscriptionDao.findAll();
        for (SysAscription as : appids)
        {
            Integer ascription = as.getPkey();
            String time = nowTime + "," + ascription;
            Set<String> all = accessMap.findAll(time);
            if (all.isEmpty() && as.getPkey().equals(1))
                all = accessMap.findAll(nowTime);
            log.info("findAll: {}", JsonUtil.toString(all, true));
            Iterator<String> iterator = all.iterator();
            List<MktAccessLog> list = new ArrayList<>();
            List<String> openids = new ArrayList<>();
            while (iterator.hasNext())
            {
                String string = iterator.next();
                MktAccessLog bean = new MktAccessLog();
                if (string.contains(","))
                {
                    String[] split = string.split(",");
                    bean.setOpenid(split[0]);
                    bean.setFarmer(split[1]);
                    bean.setCompany(split[2]);
                    bean.setAscription(ascription);
                }
                else
                {
                    bean.setOpenid(string);
                    bean.setFarmer("1");
                    bean.setCompany("1");
                }
                openids.add(bean.getOpenid());
                bean.setAccessTime(DateUtils.parseDate(nowTime));
                list.add(bean);
            }
            Map<String, Integer> openidKeyMap = memberDao.getOpenidKeyMap(openids);
            for (MktAccessLog a : list)
            {
                if (openidKeyMap.containsKey(a.getOpenid()))
                    a.setMember(openidKeyMap.get(a.getOpenid()));
            }
            accessLogDao.addAll(list);
            accessMap.removeAll(time);
        }
    }
    
    public PageResult<MktMemberCommLineOnList> queryMemberCommLine(int page, int pagesize, CommSourceType source,
        Integer member, Boolean direct, String mobile, String startDate, String endDate)
    {
        List<Integer> memberPkeys = new ArrayList<>();
        if (StringUtils.isNotBlank(mobile))
        {
            SelectBuilder<Integer, MktMember> builder = memberDao.select();
            if (StringUtils.isNotBlank(mobile))
                builder.like("mobile", mobile);
            List<MktMember> exec = builder.limit(1000).exec();
            memberPkeys = exec.stream().map(MktMember::getPkey).collect(Collectors.toList());
            if (memberPkeys.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        PageResult<MktMemberCommLine> pageResult = memberCommLineDao.queryMemberCommLine(page,
            pagesize,
            member,
            source,
            memberPkeys,
            startDate,
            endDate,
            direct,
            CurrentSession.ascriptionPkey());
        PageResult<MktMemberCommLineOnList> result = BeanUtil.beanPageFrom(MktMemberCommLineOnList.class, pageResult);
        for (MktMemberCommLineOnList bean : result.getContent())
        {
            MktMember entity = memberDao.get(bean.getMember());
            if (entity == null)
                continue;
            bean.setMemberMobile(entity.getMobile());
            bean.setMemberName(entity.getName());
            if (StringUtils.isBlank(bean.getFormId()))
                bean.setFormId(null);
            bean.setSourceName(bean.getSource().getName());
            if(CommSourceType.RECHARGE_CARD.equals(bean.getSource()))
            {
                bean.setSourceName(bean.getSourceName() + "(" + bean.getFormId() + ")");
            }
        }
        return result;
    }
    
    public void exportMemberCommLine(CommSourceType source, Integer member, Boolean direct, String mobile,
        String startDate, String endDate, HttpServletResponse response)
    {
        String excelName = "会员余额明细";
        PageResult<MktMemberCommLineOnList> pageResult =
            queryMemberCommLine(0, 100000, source, member, direct, mobile, startDate, endDate);
        List<ExportMemberCommLine> list = BeanUtil.beanListFrom(ExportMemberCommLine.class, pageResult.getContent());
        ExportUtil.exportData(ExportMemberCommLine.class, list, response, excelName, excelName, excelName);
    }
    
    public void runLogOutMember()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        List<MktMember> list = memberDao.select()
            .isNotNull("logOutTime")
            .le("logOutTime", calendar.getTime())
            .eq("status", MemberStatus.LOG_OUTING)
            .exec();
        for (MktMember line : list)
        {
            line.setStatus(MemberStatus.LOGGED_OUT);
            line.setOpenid1(null);
            line.setOpenid2(null);
        }
        memberDao.updateAll(list);
    }
    
    // 2024-03-07 临时用与 七都农贸市场 做活动  会员充值 
    // 只有会员的钱包增加金额,无其他记录
    public void memberRechargeTest(int pkey, BigDecimal amt)
    {
        MktMember member = memberDao.get(pkey);
        if (member == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_ERROR);
        MktMemberComm comm = memberCommDao.get(pkey);
        if (comm == null)
        {
            MktMemberComm memberComm = new MktMemberComm();
            memberComm.setComms(BigDecimal.ZERO);
            memberComm.setLockComms(BigDecimal.ZERO);
            memberComm.setPkey(pkey);
            memberComm.setUpdateTime(new Date());
            memberComm.setAscription(CurrentSession.ascriptionPkey());
            comm = memberCommDao.add(memberComm);
        }
        comm.setComms(comm.getComms().add(amt));
        memberCommDao.update(comm);
        
        MktMemberCommLine line = new MktMemberCommLine();
        line.setMember(pkey);
        line.setDirect(true);
        line.setComms(amt);
        line.setBalance(comm.getComms());
        line.setSource(CommSourceType.POINTS_MANUAL_ADD);
        line.setFormId("postMan手动请求接口进来");
        line.setAscription(CurrentSession.ascriptionPkey());
        memberCommLineDao.add(line);
    }
    
    public boolean tags(Integer pkey, String remark)
    {
        MktMember member = memberDao.get(pkey);
        if (member == null)
            return false;
        member.setRemark(remark);
        memberDao.put(member);
        return true;
    }
    
    public void exportMemberInfo(String name, String mobile, String remark, String startCreatedTime,
        String endCreatedTime, String startLastConsumeTime, String endLastConsumeTime, String lastConsumeFarmer,
        String source, List<Integer> tagKeys, HttpServletResponse response)
    {
        String excelName = "会员信息列表";
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        // 模糊匹配市场
        List<String> lastConsumeFarmers = null;
        if (StringUtil.isNotBlank(lastConsumeFarmer))
        {
            lastConsumeFarmers = farmerDao.listPkeysLikeName(lastConsumeFarmer, ascriptionPkey);
            if (CollectionUtil.isEmpty(lastConsumeFarmers))
            {
                ExportUtil
                    .exportData(ExportMemberInfo.class, new ArrayList<>(), response, excelName, excelName, excelName);
                return;
            }
        }
        List<Integer> keys = new ArrayList<>();
        if (tagKeys != null && !tagKeys.isEmpty())
        {
            keys = memberTagDao.listMember(tagKeys);
            if (keys.isEmpty())
            {
                ExportUtil
                    .exportData(ExportMemberInfo.class, new ArrayList<>(), response, excelName, excelName, excelName);
                return;
            }
        }
        PageResult<ExportMemberInfo> pageResult = memberDao.queryMember(0,
            10000,
            null,
            name,
            mobile,
            ascriptionPkey,
            null,
            remark,
            DateUtil.atStartOfDay(startCreatedTime),
            DateUtil.atStartOfNextDay(endCreatedTime),
            DateUtil.atStartOfDay(startLastConsumeTime),
            DateUtil.atStartOfNextDay(endLastConsumeTime),
            lastConsumeFarmers,
            source,
            keys,
            ExportMemberInfo.class);
        List<ExportMemberInfo> list = pageResult.getContent();
        List<Integer> members = list.stream().map(ExportMemberInfo::getPkey).collect(Collectors.toList());
        Map<Integer, MktOrderCountAmt> consumptionMap = orderDao.consumption(members);
        list.forEach(x -> {
            //积分和余额
            int points = memberPointManager.loadPoints(x.getPkey());
            x.setPoints(points);
            MktMemberComm comm = memberCommDao.get(x.getPkey());
            if (comm != null)
                x.setBalance(comm.getComms());
            // 统计累计消费金额、笔数
            if (consumptionMap.containsKey(x.getPkey()))
            {
                MktOrderCountAmt countAmt = consumptionMap.get(x.getPkey());
                x.setConsumeAmt(countAmt.getAmt());
                x.setConsumeCount(countAmt.getCount());
            }
            List<String> memberTagname = tagManager.getMemberTagname(x.getPkey(), CurrentSession.ascriptionPkey());
            if (!memberTagname.isEmpty())
            {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < memberTagname.size(); i++)
                {
                    sb.append(memberTagname.get(i));
                    if (i != (memberTagname.size() - 1))
                        sb.append(",");
                }
                x.setTagNames(sb.toString());
            }
            
        });
        ExportUtil.exportData(ExportMemberInfo.class, list, response, excelName, excelName, excelName);
    }
    
    public void importExcel(MultipartFile myfile, OutputStream out)
    {
        long k1 = System.currentTimeMillis();
        ExcelReaderBuilder read;
        List<MktMemberTag> addList = new ArrayList<>();
        
        Map<String, Integer> tagMap = new HashMap<>();
        Map<String, Integer> memberMap = new HashMap<>();
        Integer ascription = CurrentSession.ascriptionPkey();
        memberMap = memberDao.map(ascription);
        tagMap = tagDao.map(ascription);
        try
        {
            read = EasyExcel.read(myfile.getInputStream());
            read.head(MemberTagExcel.class);
            read.registerReadListener(new MemberTagListener(addList, out, tagMap, memberMap, ascription));
            read.headRowNumber(1);
            read.doReadAll();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (!addList.isEmpty())
            memberTagDao.putAll(addList);
        long k2 = System.currentTimeMillis();
        System.out.println("导入标签耗时: " + (k2 - k1) / 1000);
    }
    
    class MemberTagListener extends AnalysisEventListener<MemberTagExcel>
    {
        private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        public void validator(MemberTagExcel v)
        {
            try
            {
                Set<ConstraintViolation<MemberTagExcel>> set = validator.validate(v);
                if (set != null && !set.isEmpty())
                {
                    for (ConstraintViolation<MemberTagExcel> cv : set)
                    {
                        Field declaredField = v.getClass().getDeclaredField(cv.getPropertyPath().toString());
                        throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR,
                            declaredField.getName() + cv.getMessage());
                    }
                }
            }
            catch (NoSuchFieldException | SecurityException e)
            {
                throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e);
            }
        }
        
        MemberTagListener(List<MktMemberTag> addList, OutputStream out, Map<String, Integer> tagMap,
            Map<String, Integer> memberMap, Integer ascription)
        {
            this.out = out;
            this.addList = addList;
            this.tagMap = tagMap;
            this.memberMap = memberMap;
            this.ascription = ascription;
        }
        
        List<MemberTagExcel> errList = new ArrayList<>();
        
        List<MktMemberTag> addList = new ArrayList<>();
        
        ExcelWriterBuilder errBuilder;
        
        OutputStream out;
        
        Map<String, Integer> tagMap = new HashMap<>();
        
        Map<String, Integer> memberMap = new HashMap<>();
        
        Integer ascription;
        
        @Override
        public void invoke(MemberTagExcel data, AnalysisContext context)
        {
            try
            {
                validator(data);
                String tagNames = data.getTagNames();
                tagNames = tagNames.replaceAll("，", ",");
                String[] split = tagNames.split(",");
                if (memberMap.containsKey(data.getMobile()))
                {
                    Integer member = memberMap.get(data.getMobile());
                    for (String s : split)
                    {
                        if (tagMap.containsKey(s))
                        {
                            Integer tag = tagMap.get(s);
                            MktMemberTag mt = new MktMemberTag();
                            mt.setAscription(ascription);
                            mt.setPkey(member, tag);
                            addList.add(mt);
                        }
                        else
                        {
                            data.setErrMsg("表格内的标签不存在");
                            errList.add(data);
                        }
                    }
                }
                else
                {
                    data.setErrMsg("手机号不存在");
                    errList.add(data);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                if (e instanceof TofocusException)
                {
                    String errmsg = e.getMessage();
                    data.setErrMsg(errmsg);
                    errList.add(data);
                }
            }
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context)
        {
            if (!errList.isEmpty())
            {
                errBuilder = EasyExcel.write(out, MemberTagExcel.class);
                ExcelWriter errWriter = errBuilder.build();
                WriteSheet errSheet = EasyExcel.writerSheet("错误数据").build();
                errWriter.write(errList, errSheet);
                errWriter.finish();
            }
        }
    }
    
    public PageResult<TagOnList> getMemberTags(int page, int pagesize, Integer pkey, String name, String description)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<TagOnList> res = tagDao.select()
            .like(F.name, name)
            .like(F.description, description)
            .eq(F.ascription, ascription)
            .eq(F.idDel, false)
            .sort(F.createdTime, false)
            .execDto(TagOnList.class);
        if (pkey != null)
        {
            Map<Integer, String> map = memberTagDao.mapTag(pkey, ascription);
            for (TagOnList t : res)
            {
                if (map.containsKey(t.getPkey()))
                {
                    t.setEnabled(true);
                }
            }
            Collections.sort(res, new Comparator<TagOnList>()
            {
                
                @Override
                public int compare(TagOnList o1, TagOnList o2)
                {
                    int t1 = 0;
                    int t2 = 0;
                    if (o1.getEnabled())
                        t1 = 1;
                    if (o2.getEnabled())
                        t2 = 2;
                    return t2 - t1;
                }
            });
        }
        return PageUtil.page(res, PageParameter.of(page, pagesize));
    }
    
    public List<Integer> listMemberTags(Integer pkey, String name, String description)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<TagOnList> list = tagDao.select()
            .like(F.name, name)
            .like(F.description, description)
            .eq(F.ascription, ascription)
            .eq(F.idDel, false)
            .sort(F.createdTime, false)
            .execDto(TagOnList.class);
        List<Integer> res = new ArrayList<>();
        if (pkey != null)
        {
            Map<Integer, String> map = memberTagDao.mapTag(pkey, ascription);
            for (TagOnList t : list)
            {
                if (map.containsKey(t.getPkey()))
                {
                    res.add(t.getPkey());
                }
            }
        }
        return res;
    }
    
    public Boolean markMemberTags(List<Integer> pkeys, List<Integer> tagKeys)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktMemberTag> addList = new ArrayList<>();
        if (pkeys.size() == 1)
        {
            if (tagKeys != null && !tagKeys.isEmpty())
            {
                for (Integer t : tagKeys)
                {
                    MktMemberTag mt = new MktMemberTag();
                    mt.setAscription(ascription);
                    mt.setPkey(pkeys.get(0), t);
                    addList.add(mt);
                }
            }
            memberTagDao.removeByMember(pkeys.get(0));
        }
        else
        {
            if (tagKeys == null || tagKeys.isEmpty())
                return true;
            for (Integer m : pkeys)
            {
                for (Integer t : tagKeys)
                {
                    MktMemberTag mt = new MktMemberTag();
                    mt.setAscription(ascription);
                    mt.setPkey(m, t);
                    addList.add(mt);
                }
            }
        }
        memberTagDao.addAll(addList);
        return true;
    }
}
