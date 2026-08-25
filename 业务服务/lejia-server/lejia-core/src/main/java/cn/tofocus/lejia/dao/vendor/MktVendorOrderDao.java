package cn.tofocus.lejia.dao.vendor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.tofocus.db.aggs.DateInterval;
import cn.tofocus.lejia.bean.dto.app.vendor.AppVendorBillOnList;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.BaseSelectBuilder;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOrderOnList;
import cn.tofocus.lejia.bean.dto.order.VendorOrderReportLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder.F;
import cn.tofocus.lejia.bean.enums.DataEnums;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.repository.market.MktVendorOrderRepository;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class MktVendorOrderDao extends JpaSpecificationDelegate<Integer, MktVendorOrder>
{
    
    @Autowired
    private MktVendorOrderRepository repository;
    
    /**
     * 设置拼接条件
     * @param builder     条件构建对象
     * @param pkeys       mkt_vendor_order主键列表
     * @param vendor      商户主键列表
     * @param startDate   开始日期
     * @param endDate     结束日期
     * @param marketPkeys 市场主键列表
     * @param status      结算状态
     * @param createTimeSort 是否按创建时间倒序
     * @param purchaseStatus 采购状态
     * @return               结果
     */
    private BaseSelectBuilder setBuilder(BaseSelectBuilder builder, List<Integer> pkeys, List<Integer> vendor,
        String startDate, String endDate, List<String> marketPkeys, List<SettlementType> status, Boolean createTimeSort,
        PurchaseStatus purchaseStatus, Integer ascription)
    {
        
        // 采购状态
        if (Objects.nonNull(purchaseStatus))
        {
            builder.in("purchaseStatus", purchaseStatus);
        }
        
        if (CollectionUtils.isNotEmpty(pkeys))
        {
            builder.in("pkey", pkeys);
        }
        
        if (CollectionUtils.isNotEmpty(vendor))
        {
            builder.in("vendor", vendor);
        }
        
        if (StringUtil.isNotEmpty(startDate))
        {
            builder.ge("createdTime", startDate + " 00:00:00");
        }
        
        if (StringUtil.isNotEmpty(endDate))
        {
            builder.le("createdTime", endDate + " 23:59:59");
        }
        
        if (CollectionUtils.isNotEmpty(marketPkeys))
        {
            builder.in("farmer", marketPkeys);
        }
        
        if (CollectionUtils.isNotEmpty(status))
        {
            builder.in("status", status);
        }
        
        // 采购日期排序
        return builder.eq("ascription", ascription).sort("createdTime", createTimeSort);
    }
    
    /**
     * 分页查询
     * @return 结果
     */
    public PageResult<MktVendorOrder> query(List<Integer> pkeys, int page, int pagesize, List<Integer> vendor,
        String startDate, String endDate, List<String> marketPkeys, List<SettlementType> status, Boolean createTimeSort,
        PurchaseStatus purchaseStatus, Integer ascription)
    {
        SelectPageBuilder<Integer, MktVendorOrder> builder = selectPage().page(page).pagesize(pagesize);
        SelectPageBuilder<Integer, MktVendorOrder> builder2 =
            (SelectPageBuilder<Integer, MktVendorOrder>)setBuilder(builder,
                pkeys,
                vendor,
                startDate,
                endDate,
                marketPkeys,
                status,
                createTimeSort,
                purchaseStatus,
                ascription);
        return builder2.exec();
    }
    
    /**
     * 计算订单数量
     * @return 结果
     */
    public Integer getOrderCount(List<Integer> pkeys, List<Integer> vendor, String startDate, String endDate,
        List<String> marketPkeys, List<SettlementType> status, PurchaseStatus purchaseStatus, Integer ascription)
    {
        SelectBuilder<Integer, MktVendorOrder> builder1 = this.select();
        SelectBuilder<Integer, MktVendorOrder> builder2 = (SelectBuilder<Integer, MktVendorOrder>)setBuilder(builder1,
            pkeys,
            vendor,
            startDate,
            endDate,
            marketPkeys,
            status,
            true,
            purchaseStatus,
            ascription);
        // group by 结果不对
        Long count = builder2.exec().stream().map(MktVendorOrder::getOrderPkey).distinct().count();
        return count.intValue();
    }
    
    /**
     * 列表查询
     * @return 结果
     */
    public List<MktVendorOrder> list(List<Integer> pkeys, List<Integer> vendor, String startDate, String endDate,
        List<String> marketPkeys, List<SettlementType> status, Boolean createTimeSort, Integer ascription)
    {
        SelectBuilder<Integer, MktVendorOrder> builder = select();
        SelectBuilder<Integer, MktVendorOrder> builder2 = (SelectBuilder<Integer, MktVendorOrder>)setBuilder(builder,
            pkeys,
            vendor,
            startDate,
            endDate,
            marketPkeys,
            status,
            createTimeSort,
            PurchaseStatus.PURCHASE_CONFIRM,
            ascription);
        return builder2.exec();
    }
    
    /**
     * 商户对账/撤销-总金额
     * @return       结果
     */
    public BigDecimal sumTotalPrice(List<Integer> pkeys, List<Integer> vendor, String startDate, String endDate,
        List<String> marketPkeys, List<SettlementType> status, PurchaseStatus purchaseStatus, Integer ascription)
    {
        if (StringUtil.isNotEmpty(endDate))
        {
            endDate = endDate + " 23:59:59";
        }
        // 结算状态
        List<Integer> statusInt = null;
        if (CollectionUtils.isNotEmpty(status))
        {
            statusInt = new ArrayList<>();
            for (SettlementType s : status)
            {
                statusInt.add(s.getIndex());
            }
        }
        
        // 设置采购状态
        List<Integer> purchaseStatusList = new ArrayList<>();
        purchaseStatusList.add(purchaseStatus.getIndex());
        
        BigDecimal amt =
            repository.sumTotalPrice(pkeys, marketPkeys, vendor, startDate, endDate, purchaseStatusList, statusInt, ascription);
        return amt == null ? BigDecimal.ZERO : amt;
    }
    
    public BigDecimal countAmtDate(Integer vendor, String startDate)
    {
        BigDecimal amt = repository.countAmtDate(vendor, startDate);
        return amt == null ? BigDecimal.ZERO : amt;
    }
    
    public List<VendorOrderReportLine> purchaseReport(DataEnums dataEnums, Integer status, List<Integer> vendorKeys,
        String startTime, String endTime, String market, String createTimeSort)
    {
        List<List<Object>> report = null;
        Sort sort = Sort.by(Direction.ASC, "time");
        if ("desc".equals(createTimeSort))
        {
            sort = Sort.by(Direction.DESC, "time");
        }
        // 设置排序
        if (dataEnums.getIndex() == DataEnums.DAY.getIndex())
        {
            report = repository.purchaseReportDay(status, vendorKeys, startTime, endTime, market, sort);
        }
        if (dataEnums.getIndex() == DataEnums.MONTH.getIndex() || dataEnums.getIndex() == DataEnums.SEASON.getIndex())
        {
            report = repository.purchaseReportMonth(status, vendorKeys, startTime, endTime, market, sort);
        }
        if (dataEnums.getIndex() == DataEnums.YEAR.getIndex())
        {
            report = repository.purchaseReportYear(status, vendorKeys, startTime, endTime, market, sort);
        }
        List<VendorOrderReportLine> res = new ArrayList<>();
        for (List<Object> o : report)
        {
            VendorOrderReportLine l = new VendorOrderReportLine();
            l.setVendor(Integer.valueOf(o.get(0).toString()));
            l.setDate(o.get(1).toString());
            l.setNum(o.get(2).toString());
            l.setAmt(o.get(3).toString());
            res.add(l);
        }
        return res;
    }
    
    public Boolean checkFarTimeConfirm(List<Integer> orderKeys)
    {
        long count = this.aggregation()
            .notEq("vendor", 0)
            .in("orderPkey", orderKeys.toArray())
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .eq("status", SettlementType.AWAIT_CONFIRM)
            .execCount();
        return count <= 0;
    }
    
    public <T> List<T> sumVendorPrice(Class<T> clazz, List<Integer> keys)
    {
        if(keys.isEmpty())
            return new ArrayList<>();
        return this.aggregation()
            .notEq("vendor", 0)
            .eq("status", SettlementType.NOT_START)
            .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
            .isNull("settlementPkey")
            .in("orderPkey", keys)
            /**
             * 2021-12-28日修改
             * 每日的清分金额 必须和 每日的小程序支付金额一致
             */
            //            .between("farmerTime", DateUtil.atStartOfDay(startTime), DateUtil.atEndOfDay(endTime))
            .sum("totalPrice", "purchaseAmt")
            .sum("discountAmt", "discountAmt")
            .sum("postage", "postage")
            .sum("difference", "difference")
            .count("vendor", "purchaseNum")
            .groupby("vendor", "vendor")
            .execListDto(clazz);
    }
    
    public <T> List<T> sumVendorPrice2(Class<T> clazz, List<Integer> keys)
    {
        if(keys.isEmpty())
            return new ArrayList<>();
        return this.aggregation()
            .notEq("vendor", 0)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .in("orderPkey", keys)
            .sum("totalPrice", "purchaseAmt")
            .sum("discountAmt", "discountAmt")
            .sum("postage", "postage")
            .sum("difference", "difference")
            .count("vendor", "purchaseNum")
            .groupby("vendor", "vendor")
            .execListDto(clazz);
    }
    
    public <T> List<T> sumVendorPrice(List<Integer> keys, Class<T> clazz)
    {
        return this.aggregation()
            .notEq("vendor", 0)
            .eq("status", SettlementType.NOT_START)
            .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
            .isNull("settlementPkey")
            /**
             * 2021-12-28日修改
             * 每日的清分金额 必须和 每日的小程序支付金额一致
             */
            .in("orderPkey", keys)
            .sum("totalPrice", "purchaseAmt")
            .sum("discountAmt", "discountAmt")
            .sum("postage", "postage")
            .sum("difference", "difference")
            .count("vendor", "purchaseNum")
            .groupby("vendor", "vendor")
            .execListDto(clazz);
    }
    
    public BigDecimal sumVendorPrice(String time)
    {
        Number sum = this.aggregation()
            .notEq("vendor", 0)
            .eq("status", SettlementType.NOT_START)
            .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
            .isNull("settlementPkey")
            .between("createdTime", DateUtil.atStartOfDay(time), DateUtil.atEndOfDay(time))
            .execSum("totalPrice");
        BigDecimal res = BigDecimal.ZERO;
        if (sum != null)
        {
            res = new BigDecimal(String.valueOf(sum));
        }
        return res;
    }
    
    public void updateSettlementPkey(Integer settlementPkey, List<Integer> keys)
    {
        repository.updateSettlementPkey(settlementPkey, keys);
    }
    
    public List<MktVendorOrder> getVendorOrderNotStart(List<Integer> keys)
    {
        if(keys == null || keys.isEmpty())
            return new ArrayList<>();
        return this.select()
            .in("orderPkey", keys)
            .notEq("vendor", 0)
            .in("status", SettlementType.NOT_START, SettlementType.DOING)
            .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
            .exec();
    }
    
    public List<MktVendorOrder> getVendorOrderNotStartTest(List<Integer> keys)
    {
        if(keys == null || keys.isEmpty())
            return new ArrayList<>();
        return this.select()
            .in("orderPkey", keys)
            .notEq("vendor", 0)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .exec();
    }
    
    public <T> List<T> findByLine(String startDate, String endDate,
        String startSettlementDate, String endSettlementDate, String startVendorTime, String endVendorTime,
        List<Integer> vendor, List<Integer> orderPkeys, List<SettlementType> status, String marketPkey, Integer ascription, 
        Class<T> clazz)
    {
        return this.select()
            .eq("farmer", marketPkey)
            .in("vendor", vendor)
            .in("orderPkey", orderPkeys)
            .eq("ascription", ascription)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .in("status", status)
            // 查询付款时间
            .iF(startDate != null)
                .ge("endDate", DateUtil.atStartOfDay(startDate))
            .endIf()
            .iF(endDate != null)
                .le("endDate", DateUtil.atEndOfDay(endDate))
            .endIf()
            // 结算时间
            .iF(startSettlementDate != null)
                .ge("startDate", DateUtil.atStartOfDay(startSettlementDate))
                .isNotNull("startDate")
            .endIf()
            .iF(endSettlementDate != null)
                .le("startDate", DateUtil.atEndOfDay(endSettlementDate))
                .isNotNull("startDate")
            .endIf()
            // 采购确认时间
            .iF(startVendorTime != null)
                .ge("farmerTime", DateUtil.atStartOfDay(startVendorTime))
                .isNotNull("farmerTime")
            .endIf()
            .iF(endVendorTime != null)
                .le("farmerTime", DateUtil.atEndOfDay(endVendorTime))
                .isNotNull("farmerTime")
            .endIf()
            .sort("endDate")
            .sort("createdTime")
            .sort("pkey")
            .execDto(clazz);
    }
    
//    public Integer getVendorXsNum(Integer vendor)
//    {
//        Number execSum = this.aggregation()
//            .eq("vendor", vendor)
//            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
//            .execSum("num");
//        if(execSum == null)
//            return 0;
//        return execSum.intValue();
//    }
    public List<MktVendorOrder> listOrder(Integer orderPkey)
    {
        return this.select().eq("orderPkey", orderPkey).notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE).exec();
    }
    
    public List<MktVendorOrder> listCertainDayBefore(String time, Integer ascription)
    {
        return this.select()
            .eq("status", SettlementType.NOT_START)
            .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
            .le("createdTime", time)
            .notEq("ascription", ascription)
            .or()
                .in("refundStatus", RefundStatus.REFUND_REFUSE, RefundStatus.REFUND_FINAL)
                .isNull("refundStatus")
            .close()
            .done()
            .exec();
    }
    
    public List<MktVendorOrder> listCertainDayBeforeAmtZero(String time, Integer ascription)
    {
        return this.select()
            .eq("status", SettlementType.NOT_START)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .eq("amt", 0)
            .le("createdTime", time)
            .notEq("ascription", ascription)
            .or()
                .in("refundStatus", RefundStatus.REFUND_REFUSE, RefundStatus.REFUND_FINAL)
                .isNull("refundStatus")
            .close()
            .done()
            .exec();
    }
    
    public List<MktVendorOrder> listZxCertainDayBeforeAmtZero(String time)
    {
        return this.select()
            .eq("status", SettlementType.NOT_START)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .eq("amt", 0)
            .le("createdTime", time)
            .eq("ascription", 13)
            .in("refundStatus", RefundStatus.REFUND_REFUSE, RefundStatus.REFUND_FINAL)
            .exec();
    }
    
    public List<AppWalletOrderOnList> listAppWalletOrder(Integer vendorKey, String time, List<SettlementType> statuses)
    {
        return this.select()
        .isNotNull(F.vendor)
        .eq(F.vendor, vendorKey)
        .in(F.status, statuses)
        .notEq(F.purchaseStatus, PurchaseStatus.PURCHASE_REVOKE)
        .between(F.createdTime, DateUtil.atStartOfDay(time), DateUtil.atEndOfDay(time))
        .sort(F.createdTime)
        .sort(F.pkey)
        .execDto(AppWalletOrderOnList.class);
    }
    
    public List<MktVendorOrder> listOrderByVendor(Integer orderPkey, Integer vendor)
    {
        return this.select().eq("orderPkey", orderPkey).eq("vendor", vendor).notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE).exec();
    }
    
    public List<MktVendorOrder> listOrderRefundStatus(Integer orderPkey)
    {
        return this.select().eq("orderPkey", orderPkey)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .eq("refundStatus", RefundStatus.REFUND_APPLYING)
            .exec();
    }
    
    public MktVendorOrder getOrderLinePkey(Integer orderLinePkey)
    {
        return this.selectOne().eq("orderLinePkey", orderLinePkey)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .exec();
    }
    
    // 获取对应文件 结算中的数据
    public List<MktVendorOrder> listFilePkey(Integer filePkey)
    {
        return this.select()
            .eq("status", SettlementType.DOING)
            .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
            .eq("filePkey", filePkey)
            .exec();
    }
}