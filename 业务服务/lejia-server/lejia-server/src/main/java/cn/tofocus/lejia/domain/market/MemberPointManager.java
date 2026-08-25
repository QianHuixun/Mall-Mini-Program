package cn.tofocus.lejia.domain.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberPoint;
import cn.tofocus.lejia.bean.entity.member.MktMemberPointLine;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktAppConfigDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberPointDao;
import cn.tofocus.lejia.dao.market.MktMemberPointLineDao;
import cn.tofocus.lejia.domain.market.mall.AppConfigManager;
import cn.tofocus.lejia.exception.WsaleErrCode;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MemberPointManager
{
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktMemberPointDao memberPointDao;
    
    @Autowired
    private MktMemberPointLineDao memberPointLineDao;
    
    @Autowired
    private AppConfigManager configManager;
    
    @Autowired
    private MktAppConfigDao appConfigDao;
    
    @Autowired
    private RedisLockTemplate lock;
    
    private MktMemberPoint getMemPoint(int pkey)
    {
        MktMemberPoint mp = memberPointDao.get(pkey);
        if (mp == null) mp = initMemberPoints(pkey);
        return mp;
    }
    
    private MktMemberPoint initMemberPoints(int pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if(ascription == null)
            ascription = MobileSession.appid();
        MktMemberPoint memberPoints = new MktMemberPoint();
        memberPoints.setPoints(0);
        memberPoints.setLockPoints(0);
        memberPoints.setPkey(pkey);
        memberPoints.setUpdateTime(new Date());
        memberPoints.setAscription(ascription);
        memberPointDao.add(memberPoints);
        return memberPoints;
    }
    
    /*
     * 读取用户当前积分
     */
    public int loadPoints(int pkey)
    {
        return getMemPoint(pkey).getPoints();
    }
    
    /**
     * 积分变更
     * 
     * @param memberPkey
     *            用户Pkey
     * @param point
     *            变更积分值
     * @param direct
     *            true:加 false:减
     * @param source
     *            来源类型 POINTS_BUY(0, "购买"), POINTS_CONSUMPTION(1, "消费"),
     *            POINTS_ACTIVITY(2, "活动"), POINTS_MANUAL_ADD(3,"手动"),
     * @param formid
     *            来源表单ID
     * @param remark
     *            备注 来源类型为手动是填写操作员，来源类型为购买时填写市场名称
     */
    @Transactional
    public MktMemberPointLine updPoint(int memberPkey, int point, boolean direct, SourceType source, String formid,
        String remark, int ascription)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberPoint" + memberPkey);// 业务锁
            System.out.println("更新积分帐户：" + memberPkey);
            MktMemberPointLine line = new MktMemberPointLine();
            line.setMember(memberPkey);
            line.setDirect(direct);
            line.setPoints(point);
            line.setSource(source);
            line.setFormId(formid);
            if (StringUtils.isNotBlank(remark) && "默认市场".equals(remark)) remark = "";
            line.setRemark(remark);
            line.setRemark(remark);
            line.setAscription(ascription);
            MktMemberPoint mp = getMemPoint(memberPkey);
            if (line.getDirect())
            {
                //				MktMember member = memberDao.get(memberPkey);
                //				if(member.getLevel().equals(LevelType.PAID_MEMBER)){
                //					line.setPoints(line.getPoints() * configManager.getAppConfig().getMemberGetPoints());
                //				}
                mp.setPoints(mp.getPoints() + line.getPoints());
            }
            else
            {
                if (mp.getPoints() < line.getPoints())
                {
                    throw TofocusException.of(WsaleErrCode.NO_P0INTS);
                }
                mp.setPoints(mp.getPoints() - line.getPoints());
            }
            memberPointDao.update(mp);
            line.setBalance(mp.getPoints());
            System.out.println("更新积分帐户完成：" + memberPkey);
            return memberPointLineDao.add(line);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberPoint" + memberPkey);
            // TODO: handle finally clause
        }
    }
    
    /**
     * 积分变更
     *
     * @param memberPkey
     *            用户Pkey
     * @param amt
     *            金额
     * @param direct
     *            true:加 false:减
     * @param source
     *            来源类型 POINTS_BUY(0, "购买"), POINTS_CONSUMPTION(1, "消费"),
     *            POINTS_ACTIVITY(2, "活动"), POINTS_MANUAL_ADD(3,"手动"),
     * @param formid
     *            来源表单ID
     * @param remark
     *            备注 来源类型为手动是填写操作员，来源类型为购买时填写市场名称
     */
    @Transactional
    public MktMemberPointLine updPointForAmt(int memberPkey, BigDecimal amt, boolean direct, SourceType source,
        String formid, String remark, int ascription, String oldFormid)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberPoint" + memberPkey);// 业务锁
            System.out.println("更新积分帐户：" + memberPkey);
            Integer pointsRate = configManager.getAppConfig().getPointsRate();
            Integer moneyRate = configManager.getAppConfig().getMoneyRate();
            if(pointsRate == null)
            {
                MktAppConfig appConfig = appConfigDao.selectOne().eq("ascription", ascription).exec();
                if(appConfig != null)
                    pointsRate = appConfig.getPointsRate();
            }
            if(moneyRate == null)
                moneyRate = 1;
            int points = amt.intValue()/moneyRate*pointsRate;
//            BigDecimal multiply = amt.multiply(new BigDecimal(pointsRate));
            MktMemberPointLine line = new MktMemberPointLine();
            line.setMember(memberPkey);
            line.setDirect(direct);
            // 小数点后面的全部舍掉
            line.setPoints(points);
            line.setSource(source);
            line.setFormId(formid);
            line.setAscription(ascription);
            if (StringUtils.isNotBlank(remark) && "默认市场".equals(remark)) remark = "";
            line.setRemark(remark);
            MktMemberPoint mp = getMemPoint(memberPkey);
            if (line.getDirect())
            {
                MktMember member = memberDao.get(memberPkey);
                if (member.getLevel().equals(LevelType.PAID_MEMBER))
                {
                    line.setPoints(line.getPoints() * configManager.getAppConfig().getMemberGetPoints());
                }
                mp.setPoints(mp.getPoints() + line.getPoints());
            }
            else
            {
                Integer byFormId = memberPointLineDao.byFormId(oldFormid);
                line.setPoints(byFormId);
                if (mp.getPoints() < line.getPoints())
                {
                    throw TofocusException.of(WsaleErrCode.NO_P0INTS);
                }
                mp.setPoints(mp.getPoints() - line.getPoints());
            }
            memberPointDao.update(mp);
            line.setBalance(mp.getPoints());
            System.out.println("更新积分帐户完成：" + memberPkey);
            return memberPointLineDao.add(line);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberPoint" + memberPkey);
            // TODO: handle finally clause
        }
    }
    
    @Transactional
    public void runClearPoint()
    {
        // TODO sass 有问题 2022-06-30  暂时未使用
        MktAppConfig config = appConfigDao.get(1);
        if (config == null)
        {
            log.info("config为空");
            return;
        }
        Date date = config.getPointsDate();
        String time = DateUtil.formatDate(date, "MM-dd");
        log.info("清空积分时间: {}", time);
        if (DateUtil.formatDate(new Date(), "MM-dd").equals(time))
        {
            List<MktMemberPoint> list = memberPointDao.findAll();
            List<MktMemberPointLine> lines = new ArrayList<>();
            for(MktMemberPoint m : list)
            {
                m.setPoints(0);
                MktMemberPointLine l = new MktMemberPointLine();
                l.setPoints(m.getPoints());
                l.setMember(m.getPkey());
                l.setDirect(false);
                l.setBalance(0);
                l.setSource(SourceType.POINTS_EMPTY);
                lines.add(l);
            }
            memberPointDao.updateAll(list);
            memberPointLineDao.addAll(lines);
            log.info("积分清空成功,合计清空{}条数据", lines.size());
        }
        else
            log.info("当前时间不是清空积分时间");
    }
    
}
