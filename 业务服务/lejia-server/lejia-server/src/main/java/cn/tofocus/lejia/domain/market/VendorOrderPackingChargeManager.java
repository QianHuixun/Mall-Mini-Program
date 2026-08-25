package cn.tofocus.lejia.domain.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.vendor.PackingChargeOnPage;
import cn.tofocus.lejia.bean.dto.vendor.PackingChargeStatistics;
import cn.tofocus.lejia.bean.dto.vendor.VendorPackingChargeInfo;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPackingCharge;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderPackingChargeDao;
import cn.tofocus.lejia.dao.vendor.MktVendorPackingChargeDao;

@Component
public class VendorOrderPackingChargeManager
{
    @Autowired
    private MktVendorPackingChargeDao vendorPackingChargeDao;
    
    @Autowired
    private MktVendorOrderPackingChargeDao vendorOrderPackingChargeDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    public PackingChargeStatistics queryPackingCharge(int page, int pagesize, String code, String vendorName, String booth,
        Date startDate, Date endDate)
    {
        PackingChargeStatistics res = new PackingChargeStatistics();
        res.setAmt(BigDecimal.ZERO);
        res.setOrderAmt(BigDecimal.ZERO);
        res.setOrderCount(0);
        res.setPackingCharge(BigDecimal.ZERO);
        res.setLines(PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize)));
//        List<Integer> keys = new ArrayList<>();
//        List<Integer> orderPkeys = new ArrayList<>();
//        if(StringUtils.isNotBlank(vendorName) || StringUtils.isNotBlank(booth))
//        {
//            List<MktVendor> exec = vendorDao.select().like("displayName", vendorName).like("booth", booth).eq("farmer", CurrentSession.marketPkey()).exec();
//            if(exec.isEmpty())
//                return res;
//            keys.addAll(CollectionUtil.keyList(exec));
//        }
//        if(StringUtils.isNotBlank(code))
//        {
//            List<MktOrder> exec = orderDao.select().like("code", code).eq("farmer", CurrentSession.marketPkey()).exec();
//            if(exec.isEmpty())
//                return res;
//            orderPkeys.addAll(CollectionUtil.keyList(exec));
//        }
        Date ed = null;
        if(endDate != null)
            ed = DateUtil.atEndOfDay(endDate);
        PageResult<PackingChargeOnPage> lines = vendorOrderPackingChargeDao.selectPage()
        .page(page)
        .pagesize(pagesize)
        .like("code", code)
        .like("displayName", vendorName)
        .like("booth", booth)
        .between("paymentTime", startDate, ed)
        .eq("farmer", CurrentSession.marketPkey())
        .sort("paymentTime")
        .sort("pkey")
        .execDto(PackingChargeOnPage.class);
        List<PackingChargeStatistics> list = vendorOrderPackingChargeDao.aggregation()
        .like("code", code)
        .like("displayName", vendorName)
        .like("booth", booth)
        .between("paymentTime", startDate, ed)
        .eq("farmer", CurrentSession.marketPkey())
        .count("pkey", "orderCount")
        .sum("orderAmt", "orderAmt")
        .sum("packingCharge", "packingCharge")
        .sum("amt", "amt")
        .execListDto(PackingChargeStatistics.class);
        if(!list.isEmpty())
        {
            res = list.get(0);
            if(res.getAmt() == null)
                res.setAmt(BigDecimal.ZERO);
            if(res.getOrderAmt() == null)
                res.setOrderAmt(BigDecimal.ZERO);
            if(res.getOrderCount() == null)
                res.setOrderCount(0);
            if(res.getPackingCharge() == null)
                res.setPackingCharge(BigDecimal.ZERO);
        }
        
        res.setLines(lines);
        return res;
    }
//    public PackingChargeStatistics queryPackingCharge(int page, int pagesize, String code, String vendorName, String booth,
//        Date startDate, Date endDate)
//    {
//        PackingChargeStatistics res = new PackingChargeStatistics();
//        res.setAmt(BigDecimal.ZERO);
//        res.setOrderAmt(BigDecimal.ZERO);
//        res.setOrderCount(0);
//        res.setPackingCharge(BigDecimal.ZERO);
//        res.setLines(PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize)));
//        List<Integer> keys = new ArrayList<>();
//        List<Integer> orderPkeys = new ArrayList<>();
//        if(StringUtils.isNotBlank(vendorName) || StringUtils.isNotBlank(booth))
//        {
//            List<MktVendor> exec = vendorDao.select().like("displayName", vendorName).like("booth", booth).eq("farmer", CurrentSession.marketPkey()).exec();
//            if(exec.isEmpty())
//                return res;
//            keys.addAll(CollectionUtil.keyList(exec));
//        }
//        if(StringUtils.isNotBlank(code))
//        {
//            List<MktOrder> exec = orderDao.select().like("code", code).eq("farmer", CurrentSession.marketPkey()).exec();
//            if(exec.isEmpty())
//                return res;
//            orderPkeys.addAll(CollectionUtil.keyList(exec));
//        }
//        Date ed = null;
//        if(endDate != null)
//            ed = DateUtil.atEndOfDay(endDate);
//        PageResult<MktVendorOrder> pageResult = vendorOrderDao.selectPage()
//            .page(page)
//            .pagesize(pagesize)
//            .between("createdTime", startDate, ed)
//            .in("vendor", keys)
//            .in("orderPkey", orderPkeys)
//            .eq("farmer", CurrentSession.marketPkey())
//            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
//            .sort("endDate").sort("pkey").exec();
//        PageResult<PackingChargeOnPage> lines = BeanUtil.beanPageFrom(PackingChargeOnPage.class, pageResult);
//        List<PackingChargeOnPage> content = new ArrayList<>();
//        for(MktVendorOrder vo : pageResult.getContent())
//        {
//            PackingChargeOnPage pco = new PackingChargeOnPage();
//            pco.setOrderAmt(vo.getTotalPrice());
//            pco.setPackingCharge(vo.getPackingCharge());
//            if(pco.getPackingCharge() == null)
//                pco.setPackingCharge(BigDecimal.ZERO);
//            pco.setAmt(vo.getTotalPrice().subtract(pco.getPackingCharge()));
//            if(RefundStatus.REFUND_FINAL.equals(vo.getRefundStatus()) && vo.getRefundAmt() != null)
//            {
//                pco.setOrderAmt(pco.getOrderAmt().subtract(vo.getRefundAmt()));
//                pco.setAmt(pco.getAmt().subtract(vo.getRefundAmt()));
//            }
//            pco.setPaymentTime(vo.getEndDate());
//            MktVendor vendor = vendorDao.get(vo.getVendor());
//            if(vendor != null)
//            {
//                pco.setDisplayName(vendor.getDisplayName());
//                pco.setBooth(vendor.getBooth());
//            }
//            MktOrder order = orderDao.get(vo.getOrderPkey());
//            if(order != null)
//                pco.setCode(order.getCode());
//            
//            content.add(pco);
//        }
//        lines.setContent(content);
//        
//        List<PackingChargeStatistics> list = vendorOrderDao.aggregation()
//            .between("createdTime", startDate, ed).in("vendor", keys).in("orderPkey", orderPkeys)
//            .eq("farmer", CurrentSession.marketPkey())
//            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
//            .count("pkey", "orderCount")
//            .sum("totalPrice", "orderAmt")
//            .sum("packingCharge", "packingCharge")
//            .sum("amt", "amt")
//            .execListDto(PackingChargeStatistics.class);
//        res = list.get(0);
//        res.setLines(lines);
//        if(res.getPackingCharge() == null)
//            res.setPackingCharge(BigDecimal.ZERO);
//        if(res.getOrderAmt() != null)
//            res.setAmt(res.getOrderAmt().subtract(res.getPackingCharge()));
//        // List<PackingChargeStatistics> listRefund = 
//        Number execSum = vendorOrderDao.aggregation()
//            .between("createdTime", startDate, ed).in("vendor", keys).in("orderPkey", orderPkeys)
//            .eq("farmer", CurrentSession.marketPkey())
//            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
//            .eq("refundStatus", RefundStatus.REFUND_FINAL)
//            .isNotNull("refundAmt")
//            .execSum("refundAmt");
//        if(execSum != null)
//        {
//            BigDecimal refundAmt = new BigDecimal(execSum.toString());
//            System.out.println("refundAmt: " + refundAmt);
//            if(res.getOrderAmt() != null)
//                res.setOrderAmt(res.getOrderAmt().subtract(refundAmt));
//            if(res.getAmt() != null)
//                res.setAmt(res.getAmt().subtract(refundAmt));
//        }
//        
//        return res;
//    }
    
    public Boolean putPackingCharge(List<VendorPackingChargeInfo> infos)
    {
        if(infos == null || infos.isEmpty())
            return false;
        List<MktVendorPackingCharge> list = new ArrayList<>();
        for(int i = 0; i < infos.size(); i++)
        {
            VendorPackingChargeInfo info = infos.get(i);
            MktVendorPackingCharge vpc = vendorPackingChargeDao.byVendorAndInterval(info.getVendor(), info.getGrade());
            if(vpc == null)
                vpc = new MktVendorPackingCharge();
            BeanUtils.copyProperties(info, vpc, "pkey");
            vpc.setAscription(CurrentSession.ascriptionPkey());
            list.add(vpc);
        }
        vendorPackingChargeDao.putAll(list);
        return true;
    }
    
    public Boolean putPackingChargeFarmer(String farmer)
    {
        List<MktVendorPackingCharge> list = new ArrayList<>();
        
        List<MktVendor> validVendor = vendorDao.getValidVendor(farmer, null);
        for(MktVendor v : validVendor)
        {
            MktVendorPackingCharge vpc1 = new MktVendorPackingCharge();
            vpc1.setVendor(v.getPkey());
            vpc1.setGrade(1);
            vpc1.setOrderAmt(new BigDecimal(10));
            vpc1.setPackingCharge(new BigDecimal(1));
            vpc1.setAscription(v.getAscription());
            MktVendorPackingCharge vpc2 = new MktVendorPackingCharge();
            vpc2.setVendor(v.getPkey());
            vpc2.setGrade(1);
            vpc2.setOrderAmt(new BigDecimal(20));
            vpc2.setPackingCharge(new BigDecimal(2));
            vpc2.setAscription(v.getAscription());
            MktVendorPackingCharge vpc3 = new MktVendorPackingCharge();
            vpc3.setVendor(v.getPkey());
            vpc3.setGrade(1);
            vpc3.setOrderAmt(new BigDecimal(30));
            vpc3.setPackingCharge(new BigDecimal(3));
            vpc3.setAscription(v.getAscription());
            list.add(vpc1);
            list.add(vpc2);
            list.add(vpc3);
        }
        vendorPackingChargeDao.addAll(list);
        return true;
    }
}
