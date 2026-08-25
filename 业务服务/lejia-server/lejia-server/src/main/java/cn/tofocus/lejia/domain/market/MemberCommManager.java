package cn.tofocus.lejia.domain.market;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.bean.entity.member.MktMemberComm;
import cn.tofocus.lejia.bean.entity.member.MktMemberCommLine;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktMemberCommDao;
import cn.tofocus.lejia.dao.market.MktMemberCommLineDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class MemberCommManager
{
    @Autowired
    private MktMemberCommDao memberCommDao;
    
    @Autowired
    private MktMemberCommLineDao memberCommLineDao;
    
    @Autowired
    private RedisLockTemplate lock;
    
    private MktMemberComm getMemComm(int pkey, int ascription)
    {
        MktMemberComm mp = memberCommDao.get(pkey);
        if (mp == null) mp = initMemberComm(pkey, ascription);
        return mp;
    }
    
    private MktMemberComm initMemberComm(int pkey, int ascription)
    {
        MktMemberComm memberComm = new MktMemberComm();
        memberComm.setComms(BigDecimal.ZERO);
        memberComm.setLockComms(BigDecimal.ZERO);
        memberComm.setPkey(pkey);
        memberComm.setUpdateTime(new Date());
        memberComm.setAscription(ascription);
        memberCommDao.add(memberComm);
        return memberComm;
    }
    
    /*
     * 读取用户当前余额
     */
    public BigDecimal loadComm(int pkey)
    {
        return getMemComm(pkey, MobileSession.appid()).getComms();
    }
    
    /**
     * 余额变更
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
    public void updComm(int memberPkey, BigDecimal comm, boolean direct, CommSourceType source, String formid,
        Integer ascription)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberComm" + memberPkey);// 业务锁
            System.out.println("开始更新电子帐户");
            MktMemberCommLine old = memberCommLineDao.byFormIdAndSource(formid, source);
            System.out.println("formId:" + formid);
            System.out.println("source:" + source.getIndex());
            if (old != null) throw TofocusException.of(LejiaErrCode.WRONG_FORMID);
            MktMemberCommLine line = new MktMemberCommLine();
            line.setMember(memberPkey);
            line.setDirect(direct);
            line.setComms(comm);
            line.setSource(source);
            line.setFormId(formid);
            line.setAscription(ascription);
            MktMemberComm mp = getMemComm(memberPkey, ascription);
            if (line.getDirect())
            {
                mp.setComms(mp.getComms().add(line.getComms()));
            }
            else
            {
                if (mp.getComms().compareTo(line.getComms()) < 0)
                {
                    throw TofocusException.of(WsaleErrCode.NO_COMMS);
                }
                mp.setComms(mp.getComms().subtract(line.getComms()));
            }
            memberCommDao.update(mp);
            line.setBalance(mp.getComms());
            memberCommLineDao.add(line);
            System.out.println("电子帐户更新完成");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberComm" + memberPkey);
        }
    }

    // 组合支付先锁金额 成功后扣除
    public void updLockComm(int memberPkey, BigDecimal comm, String formid,
        Integer ascription)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberComm" + memberPkey);// 业务锁
            System.out.println("组合支付锁定金额");
            MktMemberComm mp = getMemComm(memberPkey, ascription);
            if (mp.getComms().compareTo(comm) < 0)
            {
                throw TofocusException.of(WsaleErrCode.NO_COMMS);
            }
            mp.setComms(mp.getComms().subtract(comm));
            mp.setLockComms(mp.getLockComms().add(comm));
            memberCommDao.update(mp);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberComm" + memberPkey);
        }
    }
    
    // 组合支付成功后扣除
    public void updComm(int memberPkey, BigDecimal comm, String formid,
        Integer ascription)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberComm" + memberPkey);// 业务锁
            System.out.println("组合支付成功-开始更新电子帐户");
            MktMemberCommLine old = memberCommLineDao.byFormIdAndSource(formid, CommSourceType.COMM_BUY);
            System.out.println("formId:" + formid);
            if (old != null) throw TofocusException.of(LejiaErrCode.WRONG_FORMID);
            MktMemberCommLine line = new MktMemberCommLine();
            line.setMember(memberPkey);
            line.setDirect(false);
            line.setComms(comm);
            line.setSource(CommSourceType.COMM_BUY);
            line.setFormId(formid);
            line.setAscription(ascription);
            MktMemberComm mp = getMemComm(memberPkey, ascription);
            if (mp.getLockComms().compareTo(line.getComms()) < 0)
            {
                throw TofocusException.of(WsaleErrCode.NO_COMMS);
            }
            mp.setLockComms(mp.getLockComms().subtract(line.getComms()));
            memberCommDao.update(mp);
            line.setBalance(mp.getComms());
            memberCommLineDao.add(line);
            System.out.println("组合支付成功-电子帐户更新完成");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberComm" + memberPkey);
        }
    }
    
    
    // 组合支付失败处理
    public void updCommPayFail(int memberPkey, BigDecimal comm, 
        Integer ascription)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberComm" + memberPkey);// 业务锁
            System.out.println("组合支付失败处理-开始更新电子帐户");
            MktMemberComm mp = getMemComm(memberPkey, ascription);
            mp.setComms(mp.getComms().add(comm));
            mp.setLockComms(mp.getLockComms().subtract(comm));
            memberCommDao.update(mp);
            System.out.println("组合支付失败处理-电子帐户更新完成");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberComm" + memberPkey);
        }
    }
    
    public void updCommRefund(int memberPkey, BigDecimal comm, boolean direct, CommSourceType source, String formid,
        Integer ascription)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberComm" + memberPkey);// 业务锁
            System.out.println("开始更新电子帐户-退款");
            MktMemberCommLine line = new MktMemberCommLine();
            line.setMember(memberPkey);
            line.setDirect(direct);
            line.setComms(comm);
            line.setSource(source);
            line.setFormId(formid);
            line.setAscription(ascription);
            MktMemberComm mp = getMemComm(memberPkey, ascription);
            if (line.getDirect())
            {
                mp.setComms(mp.getComms().add(line.getComms()));
            }
            else
            {
                if (mp.getComms().compareTo(line.getComms()) < 0)
                {
                    throw TofocusException.of(WsaleErrCode.NO_COMMS);
                }
                mp.setComms(mp.getComms().subtract(line.getComms()));
            }
            memberCommDao.update(mp);
            line.setBalance(mp.getComms());
            memberCommLineDao.add(line);
            System.out.println("电子帐户更新完成-退款");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberComm" + memberPkey);
        }
    }
}
