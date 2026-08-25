package cn.tofocus.lejia;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOnInfo;
import cn.tofocus.lejia.bean.dto.finance.SettlementBillOnPage;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.member.MktMemberActivity;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWalletLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWithdrawal;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.ZxFileType;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.v2.ZxCardStatus;
import cn.tofocus.lejia.bean.enums.v2.ZxStatus;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import cn.tofocus.lejia.bean.enums.vendor.WithdrawalStatus;
import cn.tofocus.lejia.dao.market.MktMemberActivityDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWalletLineDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWithdrawalDao;
import cn.tofocus.lejia.dao.zx.ZxUserInfoDao;
import cn.tofocus.lejia.domain.FinanceManager;
import cn.tofocus.lejia.domain.TjZxFileManager;
import cn.tofocus.lejia.domain.TjZxManager;
import cn.tofocus.lejia.domain.app.AppZxEqManager;
import cn.tofocus.lejia.domain.market.OrderManager;
import cn.tofocus.lejia.domain.vendor.VendorWalletManager;
import cn.tofocus.lejia.domain.vendor.VendorWalletUpdManager;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@SpringBootTest
public class ZxTest
{
    
    @Autowired
    private AppZxEqManager manager; 
    
    @Autowired
    private TjZxManager tjZxManager;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private TjZxFileManager tjZxFileManager;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktMemberActivityDao memberActivityDao;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private ZxUserInfoDao zxUserInfoDao;
    
    @Autowired
    private VendorWalletManager vendorWalletManager;
    
    @Autowired
    private VendorWalletUpdManager vendorWalletUpdManager;
    
    @Autowired
    private MktVendorWalletLineDao vendorWalletLineDao;
    
    @Autowired
    private MktVendorWithdrawalDao vendorWithdrawalDao;
    
    @Autowired
    private FinanceManager financeManager;
    
    @Test
    public void zxRegister()
    { 
//        ZxUserInfo info = new ZxUserInfo();
//        PageResult<SettlementBillOnPage> result = financeManager.querySettlementBill(0, 10, null, null, null, null, null);
//        System.out.println("result: " + JsonUtil.toString(result, true));
//        Result<PageResult<SettlementBillOnPage>> a = new Result<>(result);
//        System.out.println("a: " + JsonUtil.toString(a, true));
        tjZxManager.t21000032("J0405910000000010336162025061902");
    }
    
    @Test
    public void runSettle()
    {
        // 清分里 需要清分给商户 并带民营企业市场
//        List<MktOrder> list = orderDao.select().in("pkey", 6869, 6868).exec();
//        tjZxManager.runSettle(list, "20250526", 22);
//        System.out.println("runSettle:" + runSettle.toString());
//        tjZxFileManager.addFile(runSettle.toString(), "2025052605");
//        tjZxFileManager.sendFile("J0405910000000010336162025052605.ZIP", "6");
        ZxUserInfo zu = new ZxUserInfo();
        zu.setType(ZxUserType.SYSTEM);
        zu.setValue("system_22");
        zu.setComms(BigDecimal.ZERO);
        zu.setMarketAuto(true);
        zu.setVendorAuto(true);
        //zu.setZxStatus(ZxStatus.AUDIT_SUCCESS);
        zu.setCardStatus(ZxCardStatus.BINDING_SUCCESS);
        zxUserInfoDao.add(zu);
        
        ZxUserInfo zum = new ZxUserInfo();
        zum.setType(ZxUserType.MARKET);
        zum.setValue("zy_mkt_0023");
        zum.setComms(BigDecimal.ZERO);
        zum.setMarketAuto(true);
        zum.setVendorAuto(true);
        //zum.setZxStatus(ZxStatus.AUDIT_SUCCESS);
        zum.setCardStatus(ZxCardStatus.BINDING_SUCCESS);
        zum.setZxUserId("J04059100000051");
        zxUserInfoDao.add(zum);

        ZxUserInfo v1 = new ZxUserInfo();
        v1.setType(ZxUserType.VENDOR);
        v1.setValue("124");
        v1.setComms(BigDecimal.ZERO);
        v1.setMarketAuto(true);
        v1.setVendorAuto(true);
        //v1.setZxStatus(ZxStatus.AUDIT_SUCCESS);
        v1.setCardStatus(ZxCardStatus.BINDING_SUCCESS);
        v1.setZxUserId("J04059100000005");
        zxUserInfoDao.add(v1);
        
        ZxUserInfo v2 = new ZxUserInfo();
        v2.setType(ZxUserType.VENDOR);
        v2.setValue("147");
        v2.setComms(BigDecimal.ZERO);
        v2.setMarketAuto(true);
        v2.setVendorAuto(true);
        //v2.setZxStatus(ZxStatus.AUDIT_SUCCESS);
        v2.setCardStatus(ZxCardStatus.BINDING_SUCCESS);
        v2.setZxUserId("J04059100000004");
        zxUserInfoDao.add(v2);
    }
    
    @Test
    public void runSettleV2()
    {
        // 清分里不需要清分给商户 积分商城
//        List<MktOrder> list = orderDao.select().in("pkey", 6870).exec();
//        String date = "2025-06-03";
//        List<MktOrder> list = orderDao
//            .select()
//            .in("status",
//                OrderStatus.CONFIRM_ORDER,
//                OrderStatus.REFUND_APPLICATION_ORDER,
//                OrderStatus.ARRIVED_ORDER,
//                OrderStatus.SHIPPED_ORDER)
//            .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
//            .eq("settlementType", SettlementType.NOT_START)
//            .between("createdTime", date + " 00:00:00", date + " 23:59:59")
//            .eq("ascription", 22)
//            .exec();     
//        tjZxManager.runSettle("2025-07-22", 13, false);
        List<MktOrder> list = orderDao.select()
            .in("code", "911307269426912", "911307269644901211", "911307268023031211","911307263657131611","911307264262851611")
//            .in("code", "911407266305682", "911407265643872", "911407266570151521","91140726537438521")
            .exec();
        tjZxManager.runSettle(list, "20260713", 22, new ArrayList<>());
        
//        String appointOrder = tjZxManager.appointOrder(14581);
//        String appointOrder = tjZxManager.appointOrder(14630);
//        String appointOrder = tjZxManager.appointOrder(14667);
//        System.out.println("appointOrder: " + appointOrder);
//        System.out.println("list:" + JsonUtil.toString(list, true));
//        tjZxFileManager.addFile(runSettle.toString(), "2025052607");
//        tjZxFileManager.sendFile("J0405910000000010336162025052607.ZIP", "1");
    }
    
    @Test
    public void runSettleV3()
    {
        // 清分里 不需要清分给商户
        List<MktOrder> list = new ArrayList<>();
        
        List<MktMemberActivity> maList = memberActivityDao.select()
            .in("pkey", 133)
            .exec();
        if(maList != null && !maList.isEmpty())
        {
            for(MktMemberActivity ma : maList)
            {
                if(ma.getAmt().compareTo(BigDecimal.ZERO) > 0)
                {
                    MktOrder o = new MktOrder();
                    o.setPkey(-3);
                    o.setCode(ma.getCode());
                    o.setAmtn(ma.getAmt());
                    o.setCreatedTime(ma.getCreatedTime());
                    o.setFarmer(ma.getFarmer());
                    list.add(o);
                }
            }
        }
//        tjZxManager.runSettle(list, "20250526", 22);
//        System.out.println("runSettle:" + runSettle.toString());
//        tjZxFileManager.addFile(runSettle.toString(), "2025052608");
//        tjZxFileManager.sendFile("J0405910000000010336162025052608.ZIP", "1");
    }
 
    @Test
    public void sendFile()
    {
        tjZxFileManager.sendFile("J0105670000000010336162025071001.ZIP", "4");
    }
    
    @Test
    public void t2206()
    {
//        String a1 = tjZxManager.t2206("J04059100000000", "00");
//        System.out.println("00余额" + a1);
//        String a2 = tjZxManager.t2206("J04059100000000", "12");
        String a3 = tjZxManager.t2206("J04059100000000", "13");
//        System.out.println("自有资金登记薄: " + a2);
        System.out.println("担保登记簿: " + a3); 
//        String ta = tjZxManager.t2206("J04059100000000", "TA");
//        System.out.println("ta余额" + ta);
//        String user5 = tjZxManager.t2206("J04059100000051", "14");
//        System.out.println("user5余额: " + user5);
//        String user4 = tjZxManager.t2206("J04059100000005", "14");
//        System.out.println("user4余额: " + user4);
//        String user1 = tjZxManager.t2206("J04059100000001", "14");
//        System.out.println("user1余额: " + user1); 
    }
    
    @Test
    public void runGuarantee()
    {
        // 渠道入金 划到 担保登记簿
//        tjZxManager.runGuarantee(new BigDecimal("500"), "2505281NR5000388579700044803837g", Calendar.getInstance().getTime(), ZxFileType.QUDAO_RUJIN, 22);
//        tjZxFileManager.addFile(runGuarantee, "2025052606");
//        tjZxFileManager.sendFile("J0405910000000010336162025052606.ZIP", "1");
        tjZxManager.runWithdraw(null, new BigDecimal("10"), "01");
        tjZxManager.runWithdraw("J04059100000005", new BigDecimal("10"), "00");
    }

    @Test
    public void runGuaranteeArriveUser()
    {
        // 担保登记簿 划到 用户自有登记簿
//        String runGuaranteeArriveUser = tjZxManager.runGuaranteeArriveUser(new BigDecimal("500"), "J04059100000005", "2505271NYB002243980900012017555", DateUtil.formatDateStr("20250526104122", "yyyyMMddHHmmss"));
//        System.out.println("runGuaranteeArriveUser:" + runGuaranteeArriveUser);
//        tjZxFileManager.addFile(runGuaranteeArriveUser, "2025052606");
//        tjZxFileManager.sendFile("J0405910000000010336162025052606.ZIP", "1");
//        ZxUserInfo info = zxUserInfoDao.get(2);
//        tjZxManager.t22000007(info, "1");
        
    }
 
    @Test
    public void t21000029()
    {
        String a1 = tjZxManager.t21000029("J04059100000000", "01", "20250523", "99");
        System.out.println("01:" + a1);
    }
    
    @Test
    public void runTJVendor()
    {
//        MktVendorWalletLine vw = vendorWalletLineDao.get(18304);
        vendorWalletManager.runTJVendor();
//        List<MktVendorWalletLine> list = vendorWalletLineDao.select().in("pkey", 18220, 18234, 18279, 18283).exec();
//        Calendar cal = Calendar.getInstance();
//        Date now = cal.getTime();     
//        for(MktVendorWalletLine vw : list)
//        {
//            vendorWalletUpdManager.updWalletLineBalance(vw.getVendorKey(), vw.getPkey(), vw.getAmount(), now);
//            vendorWalletUpdManager
//            .updWalletAmount(vw.getVendorKey(), vw.getAmount(), true, now, vw.getPkey());
//        }
    }
    
    @Test
    public void testupdWalletAmount()
    {
        List<String> formIds = new ArrayList<>();
        formIds.add("911806257301832");
        List<MktVendorWalletLine> vendorWalletList = vendorWalletLineDao.listCertainDayBefore(formIds);
        Date now = new Date();
        for (MktVendorWalletLine vw : vendorWalletList)
        {
            vendorWalletUpdManager.updWalletLineBalance(vw.getVendorKey(), vw.getPkey(), vw.getAmount(), now);
            vendorWalletUpdManager
            .updWalletAmount(vw.getVendorKey(), vw.getAmount(), true, now, vw.getPkey());
            updWalletAmount(vw.getVendorKey(), vw.getAmount());
        }
    }
    
    private void updWalletAmount(Integer vendorKey, BigDecimal amount)
    {
        AppWalletOnInfo aw = vendorWalletUpdManager.loadWalletAmount(vendorKey);
        // 操作钱包,减少可提现金额
        vendorWalletUpdManager.updWalletAmount(vendorKey, amount, false, null, null);
        MktVendor vendor = vendorDao.get(vendorKey);
        // 增加钱包明细
        MktVendorWalletLine line = new MktVendorWalletLine();
        line.setVendorKey(vendorKey);
        line.setDirect(false);
        line.setAmount(amount);
        line.setSource(VendorWalletSource.WITHDRAWAL);
        line.setFarmer(vendor.getFarmer());
        line.setAscription(vendor.getAscription());
        // .add(aw.getSettlementAmt())
        line.setBalance(aw.getWalletAmt().subtract(amount));
        MktVendorWalletLine vwLine = vendorWalletLineDao.add(line);
        // 增加提现记录
        ZxUserInfo user = zxUserInfoDao.get(ZxUserType.VENDOR, vendorKey + "");
        MktVendorWithdrawal withdrawal = new MktVendorWithdrawal();
        withdrawal.setLineKey(vwLine.getPkey());
        withdrawal.setVendorKey(vendorKey);
        withdrawal.setStatus(WithdrawalStatus.PAYMENT);
        withdrawal.setAmount(amount);
        withdrawal.setBalance(line.getBalance());
        withdrawal.setBankname(user.getAcctNm());
        withdrawal.setBankuser(user.getUserNm());
        withdrawal.setBankcard(user.getPan());
//        withdrawal.setBankBranchName(user.getPanNum());
        withdrawal.setFarmer(vendor.getFarmer());
        withdrawal.setAscription(vendor.getAscription());
        vendorWithdrawalDao.add(withdrawal);
    }
    
    @Test
    public void test()
    {
        tjZxManager.handle616File("J0405910000000010336162025061903");
    }
    
    @Test
    public void test2()
    {
        orderManager.presaleOrder();
    }
    
    @Test
    public void test3()
    {
        try
        {
            financeManager.test();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test4()
    {
        vendorWalletManager.runWalletBug();
    }
    
    @Test
    public void test5()
    {
        tjZxManager.regenerateFile(428, "03");
    }
}
