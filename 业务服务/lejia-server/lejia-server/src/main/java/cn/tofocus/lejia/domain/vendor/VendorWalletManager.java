package cn.tofocus.lejia.domain.vendor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.vendor.WalletDetailsOnPage;
import cn.tofocus.lejia.bean.dto.vendor.WalletOnInfo;
import cn.tofocus.lejia.bean.dto.vendor.WalletOnPage;
import cn.tofocus.lejia.bean.dto.vendor.WithdrawalOnInfo;
import cn.tofocus.lejia.bean.dto.vendor.WithdrawalOnPage;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWallet;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWalletLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWithdrawal;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import cn.tofocus.lejia.bean.enums.vendor.WithdrawalStatus;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWalletDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWalletLineDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWithdrawalDao;
import cn.tofocus.lejia.dao.zx.ZxUserInfoDao;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VendorWalletManager
{
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;

    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktVendorWalletDao vendorWalletDao;
    
    @Autowired
    private MktVendorWalletLineDao vendorWalletLineDao;
    
    @Autowired
    private MktVendorWithdrawalDao vendorWithdrawalDao;
    
    @Autowired
    private VendorWalletUpdManager vendorWalletUpdManager;
    
    @Autowired
    private ZxUserInfoDao zxUserInfoDao;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    public WalletOnInfo queryWallet(int page, int pagesize, String vendorName, String booth, String marketPkey,
        Integer ascription)
    {
        List<Integer> keys = new ArrayList<>();
        if(StringUtils.isNotBlank(vendorName) || StringUtils.isNotBlank(booth))
        {
            keys = vendorDao.byNameAndBooth(vendorName, booth, marketPkey, ascription);
            if(keys.isEmpty())
            {
                WalletOnInfo res = new WalletOnInfo();
                res.setSettlementAmt(BigDecimal.ZERO);
                res.setWalletAmt(BigDecimal.ZERO);
                res.setWalletOnPage(PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize)));
                return res;
            }
        }
        System.out.println("marketPkey: " + marketPkey);
        WalletOnInfo res = vendorWalletDao.aggregationWalletOnInfo(keys, marketPkey);
        PageResult<WalletOnPage> walletOnPage = vendorWalletDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .in("pkey", keys)
            .eq("farmer", marketPkey)
            .eq("ascription", ascription)
            .execDto(WalletOnPage.class);
        res.setWalletOnPage(walletOnPage);
        return res;
    }
    
    public PageResult<WalletDetailsOnPage> queryWalletLine(int page, int pagesize, Integer pkey)
    {
        List<WalletDetailsOnPage> content = vendorWalletLineDao.queryDayWalletLine(pkey);
        List<WalletDetailsOnPage> byVendorKey = vendorWithdrawalDao.byVendorKey(pkey, qfAscription);
        for (WalletDetailsOnPage wd : content)
        {
            wd.setSource(VendorWalletSource.CONSUME);
            wd.setOrderType(DateUtil.formatDate(wd.getTime(), "yyyy-MM-dd") + "账单结算");
            wd.setStatus("成功");
        }
        
        // 根据时间排序
        Collections.sort(content, new Comparator<WalletDetailsOnPage>()
        {
            @Override
            public int compare(WalletDetailsOnPage o1, WalletDetailsOnPage o2)
            {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
        
        content.addAll(byVendorKey);
        
        // 根据时间排序
        Collections.sort(content, new Comparator<WalletDetailsOnPage>()
        {
            @Override
            public int compare(WalletDetailsOnPage o1, WalletDetailsOnPage o2)
            {
                return o2.getSettlementTime().compareTo(o1.getSettlementTime());
            }
        });
        return PageUtil.page(content, PageParameter.of(page, pagesize));
    }
    
    // 跑批,自动结算 传参,结算几天前的数据
    public void runSettlementWallet(int day, Integer ascription)
    {
        // 每天凌晨10分,昨日 市场订单已发货或已到货 订单  设置成已完成
        // 商户订单自动变成确认完成 
        // 先查询 商户订单 未结算、采购状态是确认完成的订单 时间在day天前的数据 
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -day);
        String time = DateUtil.formatDate(cal.getTime());
        log.info("商户订单自动结算跑批时间计算: {}", time);
        List<MktVendorOrder> vendorOrderList = vendorOrderDao.listCertainDayBefore(time, ascription);
        List<Integer> orderKeys = new ArrayList<>();
        for (MktVendorOrder vo : vendorOrderList)
        {
            vo.setStartDate(now);
            vo.setStatus(SettlementType.SUCCESS);
            orderKeys.add(vo.getOrderPkey());
        }
        if(!orderKeys.isEmpty())
        {
            List<String> formIds = orderDao.listCode(orderKeys);
            if(!formIds.isEmpty())
            {
                List<MktVendorWalletLine> vendorWalletList = vendorWalletLineDao.listCertainDayBefore(formIds);
//                Map<Integer, BigDecimal> map = new HashMap<>();
                for (MktVendorWalletLine vw : vendorWalletList)
                {
//                    vw.setStatus(SettlementType.SUCCESS);
//                    vw.setSettlementTime(now);
//                    Integer vendorKey = vw.getVendorKey();
//                    if (!map.containsKey(vendorKey))
//                    {
//                        map.put(vendorKey, BigDecimal.ZERO);
//                    }
//                    map.put(vendorKey, map.get(vendorKey).add(vw.getAmount()));
                    vendorWalletUpdManager.updWalletLineBalance(vw.getVendorKey(), vw.getPkey(), vw.getAmount(), now);
                    vendorWalletUpdManager
                        .updWalletAmount(vw.getVendorKey(), vw.getAmount(), true, now, vw.getPkey());
                }
                vendorOrderDao.updateAll(vendorOrderList);
            }
        }
        orderKeys.clear();
        List<MktVendorOrder> list = vendorOrderDao.listCertainDayBeforeAmtZero(time, ascription);
        for (MktVendorOrder vo : list)
        {
            vo.setStartDate(now);
            vo.setStatus(SettlementType.SUCCESS);
            orderKeys.add(vo.getOrderPkey());
        }
        if(!orderKeys.isEmpty())
        {
            List<String> formIds = orderDao.listCode(orderKeys);
            if(!formIds.isEmpty())
            {
                List<MktVendorWalletLine> vendorWalletList = vendorWalletLineDao.listCertainDayBefore(formIds);
//                Map<Integer, BigDecimal> map = new HashMap<>();
                for (MktVendorWalletLine vw : vendorWalletList)
                {
//                    vw.setStatus(SettlementType.SUCCESS);
//                    vw.setSettlementTime(now);
//                    Integer vendorKey = vw.getVendorKey();
//                    if (!map.containsKey(vendorKey))
//                    {
//                        map.put(vendorKey, BigDecimal.ZERO);
//                    }
//                    map.put(vendorKey, map.get(vendorKey).add(vw.getAmount()));
                    vendorWalletUpdManager.updWalletLineBalance(vw.getVendorKey(), vw.getPkey(), vw.getAmount(), now);
//                    vendorWalletUpdManager
//                        .updWalletAmount(vw.getVendorKey(), vw.getAmount(), true, now, vw.getPkey());
                }
                vendorOrderDao.updateAll(list);
            }
        }
    }
    
    // 天津 7.10到7.14 钱已经线下结算 线上流水和商户待结算金额未处理
    public void runWalletBug()
    {
        List<MktVendorOrder> list = vendorOrderDao.select()
        .eq("ascription", 13)
        .between("createdTime", "2025-07-10 00:00:00", "2025-07-15 00:00:00")
        .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
        .isNull("filePkey")
        .exec();
        System.out.println("list.size(): " + list.size());
        Map<String,String> map = new HashMap<>();
        List<Integer> vk = new ArrayList<>();
        for(MktVendorOrder vo : list)
        {
            String k = vo.getOrderPkey() + "_" + vo.getVendor();
            if(map.containsKey(k))
                continue;
            map.put(k, k);
            MktOrder order = orderDao.get(vo.getOrderPkey());
            vk.add(vo.getVendor());
            vendorWalletUpdManager.updWalletLockRevoke(vo.getVendor(), order.getCode());
            
//            List<MktVendorWalletLine> vwlList = vendorWalletLineDao.select()
//            .eq("vendorKey", vo.getVendor())
//            .eq("formId", order.getCode())
//            .exec();
//            BigDecimal amt = BigDecimal.ZERO;
//            for(MktVendorWalletLine vwl : vwlList)
//            {
//                amt = amt.add(vwl.getAmount());
//            }
        }
        System.out.println("vk: " + JsonUtil.toString(vk));
    }
    
    
    @Autowired
    private AppOrderManager appOrderManager;
    // 跑批 天津
    public void runTJVendor()
    {
        List<MktOrder> list1 = orderDao.select()
            .in("status", OrderStatus.SHIPPED_ORDER, 
                OrderStatus.WAIT_ARRIVAL_ORDER, 
                OrderStatus.WAIT_WRITEOFF_ORDER, 
                OrderStatus.ARRIVED_ORDER)
            .eq("ascription", 13)
            .exec();
        System.out.println("list1: " + list1.size());
        StringBuffer sb = new StringBuffer();
        for(MktOrder o : list1)
        {
            sb.append(o.getPkey() + ",");
            appOrderManager.drOrder(o);
        }
        System.out.println("o: " + sb.toString());
        // 每天凌晨10分,昨日 市场订单已发货或已到货 订单  设置成已完成
        // 商户订单自动变成确认完成 
        // 先查询 商户订单 未结算、采购状态是确认完成的订单 时间在day天前的数据 
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
//        cal.add(Calendar.DAY_OF_MONTH, -day);
        String time = DateUtil.formatDate(cal.getTime());
        log.info("商户订单自动结算跑批时间计算: {}", time);
        List<MktVendorOrder> vendorOrderList = vendorOrderDao.select()
            .eq("status", SettlementType.NOT_START)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .notEq("purchaseStatus", PurchaseStatus.AWAIT_PURCHASE)
            .le("createdTime", time)
            .eq("ascription", 13)
            .or()
                .in("refundStatus", RefundStatus.REFUND_REFUSE, RefundStatus.REFUND_FINAL)
                .isNull("refundStatus")
            .close()
            .done()
            .exec();;
        System.out.println("vendorOrderList: " + vendorOrderList.size());
        List<Integer> orderKeys = new ArrayList<>();
        for (MktVendorOrder vo : vendorOrderList)
        {
            vo.setStartDate(now);
            vo.setStatus(SettlementType.SUCCESS);
            orderKeys.add(vo.getOrderPkey());
        }
        if(!orderKeys.isEmpty())
        {
            List<String> formIds = orderDao.listCode(orderKeys);
            if(!formIds.isEmpty())
            {
                List<MktVendorWalletLine> vendorWalletList = vendorWalletLineDao.listCertainDayBefore(formIds);
                for (MktVendorWalletLine vw : vendorWalletList)
                {
                    vendorWalletUpdManager.updWalletLineBalance(vw.getVendorKey(), vw.getPkey(), vw.getAmount(), now);
                    vendorWalletUpdManager
                    .updWalletAmount(vw.getVendorKey(), vw.getAmount(), true, now, vw.getPkey());
                }
                vendorOrderDao.updateAll(vendorOrderList);
            }
        }
        orderKeys.clear();
        List<MktVendorOrder> list = vendorOrderDao.listCertainDayBeforeAmtZero(time, null);
        for (MktVendorOrder vo : list)
        {
            vo.setStartDate(now);
            vo.setStatus(SettlementType.SUCCESS);
            orderKeys.add(vo.getOrderPkey());
        }
        if(!orderKeys.isEmpty())
        {
            List<String> formIds = orderDao.listCode(orderKeys);
            if(!formIds.isEmpty())
            {
                List<MktVendorWalletLine> vendorWalletList = vendorWalletLineDao.listCertainDayBefore(formIds);
                for (MktVendorWalletLine vw : vendorWalletList)
                {
                    vendorWalletUpdManager.updWalletLineBalance(vw.getVendorKey(), vw.getPkey(), vw.getAmount(), now);
                }
                vendorOrderDao.updateAll(list);
            }
        }
        
        // 处理之前的bug遗留问题,钱还在锁定余额里 没有划到余额里
        List<MktVendorWallet> vmList = vendorWalletDao.select().gt("lockAmount", 0)
            .eq("ascription", 13)
            .exec();
        for(MktVendorWallet vw : vmList)
        {
            vendorWalletUpdManager
            .updWalletAmount(vw.getPkey(), vw.getLockAmount(), true, now, null);
        }
        
        // 全部跑完 订单就全部结束
        // 下面进行提现 
        vmList = vendorWalletDao.select().gt("amount", 0)
            .eq("ascription", 13)
            .exec();
        for(MktVendorWallet vw : vmList)
        {
            vendorWalletUpdManager.updWalletAmount(vw.getPkey(), vw.getAmount(), false, null, null);
            MktVendor vendor = vendorDao.get(vw.getPkey());
            // 增加钱包明细
            MktVendorWalletLine line = new MktVendorWalletLine();
            line.setVendorKey(vw.getPkey());
            line.setDirect(false);
            line.setAmount(vw.getAmount());
            line.setSource(VendorWalletSource.WITHDRAWAL);
            line.setFarmer(vendor.getFarmer());
            line.setAscription(vendor.getAscription());
            line.setOrderTime(new Date());
            // .add(aw.getSettlementAmt())
            line.setBalance(BigDecimal.ZERO);
            MktVendorWalletLine vwLine = vendorWalletLineDao.add(line);
            // 增加提现记录
            MktVendorWithdrawal withdrawal = new MktVendorWithdrawal();
            withdrawal.setLineKey(vwLine.getPkey());
            withdrawal.setVendorKey(vw.getPkey());
            withdrawal.setStatus(WithdrawalStatus.NO_PAYMENT);
            withdrawal.setAmount(vw.getAmount());
            withdrawal.setBalance(line.getBalance());
            withdrawal.setBankname(vendor.getBankname());
            withdrawal.setBankuser(vendor.getBankuser());
            withdrawal.setBankcard(vendor.getBankcard());
            withdrawal.setBankBranchName(vendor.getBankBranchName());
            withdrawal.setFarmer(vendor.getFarmer());
            withdrawal.setAscription(vendor.getAscription());
            vendorWithdrawalDao.add(withdrawal);
        }
    }
    
    
    
    public void testUpdVendorOrderAndWalletLineTime(String now, String time)
    {
        Date s = DateUtil.atStartOfDay(now);
        Date e = DateUtil.atEndOfDay(now);
        Date createdTime = DateUtil.atStartOfDay(time);
        List<MktVendorOrder> list = vendorOrderDao.select().between("createdTime", s, e).exec();
        List<MktVendorWalletLine> list2 = vendorWalletLineDao.select().between("createdTime", s, e).exec();
        for(MktVendorOrder v : list)
            v.setCreatedTime(createdTime);
        for(MktVendorWalletLine v : list2)
            v.setCreatedTime(createdTime);
        vendorOrderDao.updateAll(list);
        vendorWalletLineDao.updateAll(list2);
    }
    
    
    public WithdrawalOnInfo queryWithdrawal(int page, int pagesize, String startDate, String endDate,
        String vendorName, String booth, WithdrawalStatus status, String marketPkey, Integer ascription)
    {
        WithdrawalOnInfo res = new WithdrawalOnInfo();
        res.setNum(0);
        res.setAmount(BigDecimal.ZERO);
        res.setWithdrawalOnPage(PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize)));
        List<Integer> keys = new ArrayList<>();
        if(StringUtils.isNotBlank(vendorName) || StringUtils.isNotBlank(booth))
        {
            keys = vendorDao.byNameAndBooth(vendorName, booth, marketPkey, ascription);
            if(keys.isEmpty())
                return res;
        }
        PageResult<WithdrawalOnPage> withdrawalOnPage = vendorWithdrawalDao.queryWithdrawalOnPage(page, pagesize, startDate, endDate, 
            keys, status, marketPkey, ascription);
        res.setWithdrawalOnPage(withdrawalOnPage);
        
        
        if(res.getWithdrawalOnPage() != null && res.getWithdrawalOnPage().getContent() != null)
        {
            for(WithdrawalOnPage wo : res.getWithdrawalOnPage().getContent())
            {
                ZxUserInfo zxUserInfo = zxUserInfoDao.get(ZxUserType.VENDOR, wo.getVendorKey() + "");
                if(zxUserInfo != null)
                    wo.setPan(zxUserInfo.getPan());
            }
        }
        if(WithdrawalStatus.PAYMENT.equals(status))
            return res;
        WithdrawalOnInfo info = vendorWithdrawalDao.aggWithdrawalOnInfo(startDate, endDate, keys, marketPkey, ascription);
        if(info.getNum() != null)
            res.setNum(info.getNum());
        if(info.getAmount() != null)
            res.setAmount(info.getAmount());
        return res;
    }
    
    public Boolean confirmWithdrawal(int pkey)
    {
        MktVendorWithdrawal bean = vendorWithdrawalDao.get(pkey);
        bean.setStatus(WithdrawalStatus.PAYMENT);
        vendorWithdrawalDao.update(bean);
        return true;
    }
}
