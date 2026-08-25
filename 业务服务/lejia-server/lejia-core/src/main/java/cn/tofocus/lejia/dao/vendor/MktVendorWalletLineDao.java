package cn.tofocus.lejia.dao.vendor;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.db.aggs.DateInterval;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.app.vendor.AppVendorBillOnList;
import cn.tofocus.lejia.bean.dto.vendor.WalletDetailsOnPage;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWalletLine;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;

@Component
public class MktVendorWalletLineDao extends JpaSpecificationDelegate<Integer, MktVendorWalletLine>
{
    public List<WalletDetailsOnPage> queryDayWalletLine(Integer vendorKey)
    {
        return this.aggregation()
        .eq("vendorKey", vendorKey)
        .eq("source", VendorWalletSource.CONSUME)
        .eq("status", SettlementType.SUCCESS)
        .groupby("settlementTime", "settlementTime")
        .groupby("createdTime", "time", DateInterval.DAY)
//        .max("settlementTime", "settlementTime")
        .sum("amount", "orderAmount")
        .max("balance", "balance")
        .execListDto(WalletDetailsOnPage.class);
    }
    
//    public List<MktVendorWalletLine> listCertainDayBefore(String time)
//    {
//        // 来源:消费,状态:未结算,时间在几天前
//        return this.select()
//            .eq("source", VendorWalletSource.CONSUME)
//            .eq("status", SettlementType.NOT_START)
//            .le("createdTime", time)
//            .exec();
//    }
    
    public List<MktVendorWalletLine> listCertainDayBefore(List<String> formIds)
    {
        return this.select()
            .eq("source", VendorWalletSource.CONSUME)
            .eq("status", SettlementType.NOT_START)
            .in("formId", formIds)
            .exec();
    }
    
    public List<MktVendorWalletLine> listCertainDayBeforeZx(List<String> formIds)
    {
        return this.select()
            .eq("source", VendorWalletSource.CONSUME)
            .eq("status", SettlementType.DOING)
            .in("formId", formIds)
            .exec();
    }
    
    
    public MktVendorWalletLine getKeyAndFormIdAndAmount(Integer vendorKey, String formId, BigDecimal amount)
    {
        return this.selectOne()
            .eq("formId", formId)
            .eq("vendorKey", vendorKey)
            .eq("source", VendorWalletSource.CONSUME)
            .eq("status", SettlementType.NOT_START)
            .eq("amount", amount)
            .exec();
    }
    
    public List<MktVendorWalletLine> listKeyAndFormId(Integer vendorKey, String formId)
    {
        return this.select()
            .eq("formId", formId)
            .eq("vendorKey", vendorKey)
            .eq("source", VendorWalletSource.CONSUME)
            .eq("status", SettlementType.NOT_START)
            .exec();
    }
    
    public List<AppVendorBillOnList> listAppBill(Integer vendorKey, List<SettlementType> statuses, String time, String startDate, String endDate)
    {
        return this.aggregation()
            .eq("vendorKey", vendorKey)
            .eq("source", VendorWalletSource.CONSUME)
            .in("status", statuses)
            .isNotNull("orderTime")
//            .notIn("status", SettlementType.AWAIT_CONFIRM, SettlementType.DOING, SettlementType.FAIL)
//            .groupby("status", "status")
            .groupby("orderTime", "orderTime", DateInterval.DAY)
            .iF(StringUtils.isNotBlank(time))
                .ge("orderTime", time)
            .endIf()
            .iF(StringUtils.isNotBlank(startDate))
                .ge("orderTime", DateUtil.atStartOfThisMonth(startDate))
            .endIf()
            .iF(StringUtils.isNotBlank(endDate))
                .le("orderTime", DateUtil.atEndOfMonth(endDate))
            .endIf()
            .sum("amount", "amount")
            .max("settlementTime", "time")
            .execListDto(AppVendorBillOnList.class);
    }

    public BigDecimal sumDayAmount(Integer vendorKey, String time, List<SettlementType> statuses)
    {
        Number execSum = this.aggregation()
        .eq("vendorKey", vendorKey)
        .in("status", statuses)
        .eq("source", VendorWalletSource.CONSUME)
        .between("createdTime", DateUtil.atStartOfDay(time), DateUtil.atEndOfDay(time))
        .execSum("amount");
        return new BigDecimal(execSum.toString());
    }
   
    public MktVendorWalletLine byKeyAndFormIdPayComm(Integer vendorKey, String formId)
    {
        return this.selectOne() 
            .eq("vendorKey", vendorKey)
            .eq("formId", formId)
            .eq("source", VendorWalletSource.CONSUME)
            .eq("status", SettlementType.NOT_START)
            .sort("pkey")
            .exec();
    }
}