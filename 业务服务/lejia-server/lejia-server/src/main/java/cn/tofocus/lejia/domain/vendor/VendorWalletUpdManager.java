package cn.tofocus.lejia.domain.vendor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOnInfo;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWallet;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWalletLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWalletLine.F;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWalletDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWalletLineDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VendorWalletUpdManager
{
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorWalletDao vendorWalletDao;
    
    @Autowired
    private MktVendorWalletLineDao vendorWalletLineDao;
    
    @Autowired
    private RedisLockTemplate lock;
    
    private MktVendorWallet getVendorWallet(int pkey)
    {
        MktVendorWallet vw = vendorWalletDao.get(pkey);
        if(vw == null)
            vw = initVendorWallet(pkey);
        return vw;
    }
    
    private MktVendorWallet initVendorWallet(int pkey)
    {
        MktVendorWallet vw = new MktVendorWallet();
        vw.setAmount(BigDecimal.ZERO);
        vw.setLockAmount(BigDecimal.ZERO);
        vw.setPkey(pkey);
        MktVendor mktVendor = vendorDao.get(pkey);
        vw.setFarmer(mktVendor.getFarmer());
        vw.setAscription(mktVendor.getAscription());
        vw.setUpdateTime(new Date());
        vendorWalletDao.add(vw);
        return vw;
    }
    
    /*
     * 读取商户当前余额和待结束金额
     */
    public AppWalletOnInfo loadWalletAmount(int pkey)
    {
        MktVendorWallet vw = getVendorWallet(pkey);
        AppWalletOnInfo res = new AppWalletOnInfo();
        res.setWalletAmt(vw.getAmount());
        res.setSettlementAmt(vw.getLockAmount());
        return res;
    }
    
    /**
     * <增加明细,并操作商户钱包金额>
     * <功能详细描述>
     * @param pkey
     * @param amount
     * @param direct true: 增加待结算金额   false: 减少待结算金额
     * @param source
     * @param formId
     */
    public void updWalletLockAmount(int pkey, BigDecimal amount, boolean direct, VendorWalletSource source, String formId, Date orderTime)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "vendorWallet" + pkey); // 业务锁
            log.info("商户主键: {}, 操作金额: {}, 操作类型: {}, 来源订单号: {}", pkey, amount, source.getName(), formId);
            MktVendorWalletLine line = new MktVendorWalletLine();
            line.setVendorKey(pkey);
            line.setDirect(direct);
            line.setAmount(amount);
            line.setSource(source);
            line.setFormId(formId);
            line.setOrderTime(orderTime);
            line.setStatus(SettlementType.NOT_START);
            MktVendorWallet vw = getVendorWallet(pkey);
            line.setAscription(vw.getAscription());
            line.setFarmer(vw.getFarmer());
            BigDecimal lockBalance;
            if(direct)
            {
                lockBalance = vw.getLockAmount().add(amount);
            }
            else
            {
                MktVendorWalletLine oldLine = vendorWalletLineDao.getKeyAndFormIdAndAmount(pkey, formId, amount);
                if(oldLine != null)
                    line.setPkey(oldLine.getPkey());
                lockBalance = vw.getLockAmount().subtract(amount);
            }
            vw.setLockAmount(lockBalance);
            line.setLockBalance(lockBalance);
            vendorWalletLineDao.put(line);
            vendorWalletDao.update(vw);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "vendorWallet" + pkey);
        }
    }
    
    public void updWalletPayComm(MktVendorWalletLine line, BigDecimal amount)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "vendorWallet" + line.getVendorKey()); // 业务锁
            log.info("商户主键: {}, 操作金额: {}", line.getVendorKey(), amount);
            line.setAmount(line.getAmount().subtract(amount));
            MktVendorWallet vw = getVendorWallet(line.getVendorKey());
            vw.setLockAmount(vw.getLockAmount().subtract(amount));
            vendorWalletLineDao.update(line);
            vendorWalletDao.update(vw);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "vendorWallet" + line.getVendorKey());
        }
    }
    
    /**
     * <增加明细,并操作商户钱包金额>
     * <功能详细描述>
     * @param pkey
     * @param source
     * @param formId
     */
    public void updWalletLockRevoke(int pkey, String formId)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "vendorWallet" + pkey); // 业务锁
            log.info("商户主键: {}, 来源订单号: {}", pkey, formId);
            List<MktVendorWalletLine> list = vendorWalletLineDao.listKeyAndFormId(pkey, formId);
            MktVendorWallet vw = getVendorWallet(pkey);
            BigDecimal lockBalance = vw.getLockAmount();
            BigDecimal amount = BigDecimal.ZERO;
            for(MktVendorWalletLine vwl : list)
            {
                amount = amount.add(vwl.getAmount());
                lockBalance = lockBalance.subtract(vwl.getAmount());
                vwl.setSource(VendorWalletSource.REVOKE);
                vwl.setDirect(false);
            }
            System.out.println("amount: " + amount);
            vw.setLockAmount(lockBalance);
            vendorWalletLineDao.updateAll(list);
            vendorWalletDao.update(vw);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "vendorWallet" + pkey);
        }
    }
    
    public void updWalletLineBalance(int pkey, int lineKey, BigDecimal amount, Date now)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "updVendorWalletLine" + lineKey); // 业务锁
            log.info("更新明细余额,商户主键: {}, 操作金额: {}, 明细主键: {} ", pkey, amount, lineKey);
            MktVendorWallet vw = getVendorWallet(pkey);
            if(vw.getLockAmount().compareTo(amount) < 0)
            {
                throw TofocusException.of(LejiaErrCode.VENDOR_WALLET_AMOUNT_ERROR, "待结算金额小于要转入余额的金额");
            }
            Map<String,Object> value = new HashMap<>();
            value.put("balance", vw.getAmount().add(amount));
            value.put("settlementTime", now);
            value.put("status", SettlementType.SUCCESS);
            vendorWalletLineDao.select()
            .strict(true)
            .eq(F.pkey, lineKey).update(value);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "updVendorWalletLine" + lineKey);
        }
    }
    
    /**
     * <操作商户钱包待结算金额到可提现金额和提现>
     * @param pkey
     * @param amount
     * @param direct true: +; false:-
     * @param ascription
     */
    public void updWalletAmount(int pkey, BigDecimal amount, boolean direct, Date now, Integer lineKey)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "vendorWallet" + pkey); // 业务锁
            System.out.println("pkey: " + pkey);
            System.out.println("amount: " + amount);
            System.out.println("direct: " + direct);
            System.out.println("now: " + now);
            System.out.println("lineKey: " + lineKey);
            MktVendorWallet vw = getVendorWallet(pkey);
            if(direct)
            {
                if(vw.getLockAmount().compareTo(amount) < 0)
                {
                    throw TofocusException.of(LejiaErrCode.VENDOR_WALLET_AMOUNT_ERROR, "待结算金额小于要转入余额的金额");
                }
                vw.setLockAmount(vw.getLockAmount().subtract(amount));
                vw.setAmount(vw.getAmount().add(amount));
            }
            else
            {
                // false,只有提现的时候出现
                if(vw.getAmount().compareTo(amount) < 0)
                    throw TofocusException.of(LejiaErrCode.VENDOR_WALLET_AMOUNT_ERROR, "提现金额大于商户可提现金额");
                vw.setAmount(vw.getAmount().subtract(amount));
            }
            vendorWalletDao.update(vw);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "vendorWallet" + pkey);
        }
    }
}
