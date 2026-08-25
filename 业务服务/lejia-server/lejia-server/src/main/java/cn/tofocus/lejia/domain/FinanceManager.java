package cn.tofocus.lejia.domain;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alibaba.excel.util.StringUtils;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.data.TjZxInfo;
import cn.tofocus.lejia.bean.dto.finance.FundDetailsList;
import cn.tofocus.lejia.bean.dto.finance.FundDetailsTotal;
import cn.tofocus.lejia.bean.dto.finance.SettlementBillOnPage;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.member.MktMemberActivity;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.ZxWithdrawStatus;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktMemberActivityDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.zx.ZxUserInfoDao;
import cn.tofocus.lejia.dao.zx.ZxWithdrawDao;
import cn.tofocus.lejia.domain.app.AppZxEqManager;
import cn.tofocus.lejia.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinanceManager
{

    private final AppZxEqManager appZxEqManager;
    
    private final MktVendorOrderDao vendorOrderDao;
    
    private final MktVendorDao vendorDao;
    
    private final MktOrderDao orderDao;
    
    private final MktMemberActivityDao memberActivityDao;
    
    private final ZxUserInfoDao zxUserInfoDao;
    
    private final SysFarmerDao sysFarmerDao;
    
    private final SysFarmerConfigDao sysFarmerConfigDao;
    
    private final MktOrderRefundDao orderRefundDao;
    
    private final ZxWithdrawDao zxWithdrawDao;

    public PageResult<SettlementBillOnPage> querySettlementBill(int page, int pagesize, String farmer, String code,
        String startDate, String endDate, SettlementType settlementType)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        List<String> fl = new ArrayList<>();
        if (StringUtils.isNotBlank(farmer))
        {
            List<SysFarmer> list = sysFarmerDao.select().eq("ascription", ascriptionPkey).like("name", farmer).exec();
            if (list.isEmpty()) return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            list.forEach(e -> fl.add(e.getPkey()));
        }
        String marketPkey = CurrentSession.marketPkey();
        if (marketPkey.startsWith(Constant.Operation))
        {
            marketPkey = null;
            //            List<SysFarmerConfig> list = sysFarmerConfigDao.select()
            //            .eq("ascription", ascriptionPkey)
            //            .notEq("isEnterprise", Boolean.TRUE)
            //            .exec();
            //            list.forEach(e -> fl.add(e.getPkey()));
        }
        List<MktMemberActivity> activityList = memberActivityDao
            .querySettlementBill(marketPkey, code, startDate, endDate, settlementType, fl, ascriptionPkey);
        List<MktOrder> orderList = orderDao.select()
            .like("code", code)
            .eq("farmer", marketPkey)
//            .notEq("orderType", OrderType.INTEGRAL_MSD_ORDER)
//            .notEq("orderType", OrderType.INTEGRAL_JD_ORDER)
            .in("farmer", fl)
            .notIn("status", OrderStatus.UNPAID_ORDER, OrderStatus.VOID_ORDER, OrderStatus.REFUNDED_ORDER)
            .eq("ascription", ascriptionPkey)
            .eq("settlementType", settlementType)
            .iF(startDate != null && endDate != null)
            .between("createdTime", startDate + " 00:00:00", endDate + " 23:59:59")
            .endIf()
            .or()
                .and()
                    .eq("orderOir", OrderOir.POINTS_MALL)
                    .notEq("payType", PayType.ORDER_MSD)
                .close()
                .and()
                    .eq("orderOir", OrderOir.MARKET_MALL)
                .close()
            .close()
            .done()
            .sort("createdTime")
            .exec();
        for (MktMemberActivity ma : activityList)
        {
            MktOrder o = new MktOrder();
            o.setPkey(-3);
            o.setCode(ma.getCode());
            o.setAmtn(ma.getAmt());
            o.setCreatedTime(ma.getCreatedTime());
            o.setFarmer(ma.getFarmer());
            o.setSettlementType(ma.getSettlementType());
            orderList.add(o);
        }
        //        Collections.sort(orderList, new Comparator<MktOrder>() 
        //        {
        //            @Override
        //            public int compare(MktOrder o1, MktOrder o2)
        //            {
        //                return o2.getCreatedTime().compareTo(o1.getCreatedTime());
        //            }
        //        });
        orderList = orderList.stream()
            .sorted(Comparator.comparing(MktOrder::getCreatedTime).reversed())
            .collect(Collectors.toList());
        Map<String, String> nameMap = sysFarmerDao.findNameMap(ascriptionPkey);
        Map<String, Boolean> bMap = sysFarmerConfigDao.mapIsEnterprise(ascriptionPkey);
        List<SettlementBillOnPage> list = BeanUtil.beanListFrom(SettlementBillOnPage.class, orderList);
        PageResult<SettlementBillOnPage> res = PageUtil.page(list, PageParameter.of(page, pagesize));
        //        List<SettlementBillOnPage> content = new ArrayList<>();
        for (SettlementBillOnPage dto : res.getContent())
        {
            dto.setPayComm(BigDecimal.ZERO);
            //            SettlementBillOnPage dto = BeanUtil.beanFrom(SettlementBillOnPage.class, order);
            if (dto.getPkey() == -3)
            {
                dto.setAmtall(dto.getAmtn());
                dto.setPostage(BigDecimal.ZERO);
                dto.setOldPostage(BigDecimal.ZERO);
                dto.setCardAmt(BigDecimal.ZERO);
                dto.setCardPostageAmt(BigDecimal.ZERO);
                dto.setRefundCardAmt(BigDecimal.ZERO);
                dto.setAmto(dto.getAmtn());
                BigDecimal payCommissionRate = Constant.ZxConfig.TJ_COMMISSION_RATE;
                BigDecimal payCommission = dto.getAmtn().multiply(payCommissionRate).setScale(2, RoundingMode.HALF_UP);
                dto.setPayComm(payCommission);
                
                if (Boolean.TRUE.equals(bMap.get(dto.getFarmer())))
                {
                    dto.setMarketCommissions(dto.getAmtn());
                    dto.setCommissionType(CommissionType.MARKET);
                }
                else
                {
                    dto.setSysCommissions(dto.getAmtn());
                    dto.setCommissionType(CommissionType.BLOC);
                }
            }
            dto.setAmt(BigDecimal.ZERO);
            //            dto.setRefundAmt(BigDecimal.ZERO);
            dto.setRefundCardAmt(BigDecimal.ZERO);
            dto.setRefundPostageAmt(BigDecimal.ZERO);
            
            dto.setActualAmtVendor(BigDecimal.ZERO);
            
            dto.setFarmerName(nameMap.get(dto.getFarmer()));
            dto.setCardPostageAmt2(dto.getCardPostageAmt());
            if (dto.getCommissionType() != null) dto.setCommissionTypeName(dto.getCommissionType().getName());
            if (dto.getSettlementType() != null) dto.setSettlementTypeName(dto.getSettlementType().getName());
            BigDecimal refundAmt = dto.getRefundAmt();
            if (refundAmt == null)
            {
                refundAmt = BigDecimal.ZERO;
                dto.setRefundAmt(BigDecimal.ZERO);
            }
            dto.setNeedAmt(dto.getAmtall().subtract(refundAmt));
            dto.setActualPayment(dto.getAmtn().subtract(refundAmt));
       
            BigDecimal refundPostageAmt = orderRefundDao.aggRefundPostageAmt(dto.getPkey());
            if (refundPostageAmt == null) refundPostageAmt = BigDecimal.ZERO;
            dto.setRefundPostageAmt(refundPostageAmt);
            dto.setGoodsNeedAmt(dto.getAmto().subtract(refundAmt).add(refundPostageAmt));
            if (dto.getOldPostage() != null)
                dto.setNeedPostageAmt(dto.getOldPostage().subtract(refundPostageAmt));
            else
                dto.setNeedPostageAmt(BigDecimal.ZERO);
            
            if(PayType.MSD_COMBINATION.equals(dto.getPayType()) && OrderOir.POINTS_MALL.equals(dto.getOrderOir()))
            {
                dto.setNeedAmt(dto.getAmtall().subtract(dto.getOtherAmt()));
                dto.setActualPayment(dto.getWeixinAmt());
                dto.setGoodsNeedAmt(dto.getAmto().subtract(dto.getOtherAmt()));
                if(dto.getRefundWeixinAmt() != null)
                {
                    dto.setNeedAmt(dto.getNeedAmt().subtract(dto.getRefundWeixinAmt()));
                    dto.setActualPayment(dto.getWeixinAmt().subtract(dto.getRefundWeixinAmt()));
                    dto.setGoodsNeedAmt(dto.getGoodsNeedAmt().subtract(dto.getRefundWeixinAmt()));
                }
//                if(refundAmt.compareTo(dto.getOtherAmt()) >= 0)
//                {
//                    
//                }
//                dto.setNeedAmt(dto.getNeedAmt().subtract(dto.getOtherAmt()));
//                dto.setActualPayment(dto.getActualPayment().subtract(dto.getOtherAmt()));
//                dto.setGoodsNeedAmt(dto.getGoodsNeedAmt().subtract(dto.getOtherAmt()));
//                if(dto.getRefundWeixinAmt() != null)
//                {
//                    dto.setNeedAmt(dto.getNeedAmt().add(dto.getRefundWeixinAmt()));
//                    dto.setActualPayment(dto.getActualPayment().add(dto.getRefundWeixinAmt()));
//                    dto.setGoodsNeedAmt(dto.getGoodsNeedAmt().add(dto.getRefundWeixinAmt()));
//                }
//                10 7热力豆 3微信
//                退款8元
//                10 - 8 =2 -7热力豆
//                退款6元
//                10 - 6 =4 -7热力豆
                
            }
            
            List<SettlementBillOnPage> execListDto = vendorOrderDao.aggregation()
                .eq("orderPkey", dto.getPkey())
                .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
                .sum("discountRefundAmt", "refundCardAmt")
                .sum("payComm", "payComm")
                .sum("sysCommissions", "sysCommissions")
                .sum("marketCommissions", "marketCommissions")
                .sum("amt", "amt")
                .execListDto(SettlementBillOnPage.class);
            if (!execListDto.isEmpty())
            {
                SettlementBillOnPage e = execListDto.get(0);
                if (e.getPayComm() != null) dto.setPayComm(e.getPayComm());
                if (e.getRefundCardAmt() != null) dto.setRefundCardAmt(e.getRefundCardAmt());
                if (e.getSysCommissions() != null) dto.setSysCommissions(e.getSysCommissions());
                if (e.getMarketCommissions() != null) dto.setMarketCommissions(e.getMarketCommissions());
                if (e.getAmt() != null) dto.setAmt(e.getAmt());
                if (e.getPayComm() == null && marketPkey == null)
                {
                    BigDecimal setScale = dto.getActualPayment()
                        .multiply(Constant.ZxConfig.TJ_COMMISSION_RATE)
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                    dto.setPayComm(setScale);
                }
            }
            
            dto.setRefundAmt(dto.getRefundAmt().subtract(refundPostageAmt));
            if (dto.getPostage() != null) dto.setPostage(dto.getPostage().subtract(refundPostageAmt));
            
            if (dto.getRefundCardAmt() != null)
            {
                dto.setNeedAmt(dto.getNeedAmt().subtract(dto.getRefundCardAmt()));
                dto.setGoodsNeedAmt(dto.getGoodsNeedAmt().subtract(dto.getRefundCardAmt()));
            }
            //            if(dto.getCardPostageAmt() != null)
            //            {
            //                dto.setNeedAmt(dto.getNeedAmt().subtract(dto.getCardPostageAmt()));
            //                dto.setCardPostageAmt2(dto.getCardPostageAmt());
            //                dto.setNeedPostageAmt(dto.getNeedPostageAmt().subtract(dto.getCardPostageAmt()));
            //                dto.setCardPostageAmt2(BigDecimal.ZERO);
            //            }
            
            if (!Boolean.TRUE.equals(bMap.get(dto.getFarmer())) && OrderOir.POINTS_MALL.equals(dto.getOrderOir()))
            {
                dto.setSysCommissions(dto.getGoodsNeedAmt());
            }
            if (dto.getPayComm().compareTo(BigDecimal.ZERO) == 0 && dto.getPostage().compareTo(BigDecimal.ZERO) > 0)
            {
                BigDecimal setScale = dto.getActualPayment()
                    .multiply(Constant.ZxConfig.TJ_COMMISSION_RATE)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
                dto.setPayComm(setScale);
            }
            
            if (Boolean.TRUE.equals(bMap.get(dto.getFarmer())))
                dto.setPostageAmtMarket(dto.getPostage());
            else
                dto.setPostageAmtSys(dto.getPostage());
            
            if (dto.getSysCommissions() == null) dto.setSysCommissions(BigDecimal.ZERO);
            
            if (dto.getPostageAmtSys() == null) dto.setPostageAmtSys(BigDecimal.ZERO);
            
            if (dto.getMarketCommissions() == null) dto.setMarketCommissions(BigDecimal.ZERO);
            if (dto.getPostageAmtMarket() == null) dto.setPostageAmtMarket(BigDecimal.ZERO);
            dto.setActualAmtSys(dto.getSysCommissions().add(dto.getPostageAmtSys()));
            dto.setActualAmtMarket(dto.getMarketCommissions().add(dto.getPostageAmtMarket()));
            dto.setActualAmtVendor(dto.getAmt());
            if (dto.getCommissionType() != null)
            {
                switch (dto.getCommissionType())
                {
                    case BLOC:
                        dto.setActualAmtSys(dto.getActualAmtSys().subtract(dto.getPayComm()));
                        break;
                    case MARKET:
                        dto.setActualAmtMarket(dto.getActualAmtMarket().subtract(dto.getPayComm()));
                        break;
                    case MERCHANT:
                        dto.setActualAmtVendor(dto.getActualAmtVendor().subtract(dto.getPayComm()));
                        break;
                    default:
                        break;
                }
            }
            if (dto.getActualAmtSys().compareTo(BigDecimal.ZERO) < 0)
            {
                if (dto.getActualAmtMarket().compareTo(BigDecimal.ZERO) > 0)
                {
                    dto.setActualAmtMarket(dto.getActualAmtMarket().add(dto.getActualAmtSys()));
                    dto.setActualAmtSys(BigDecimal.ZERO);
                }
                else if (dto.getActualAmtVendor().compareTo(BigDecimal.ZERO) > 0)
                {
                    dto.setActualAmtVendor(dto.getActualAmtVendor().add(dto.getActualAmtSys()));
                    dto.setActualAmtSys(BigDecimal.ZERO);
                }
            }
            if (dto.getActualAmtMarket().compareTo(BigDecimal.ZERO) < 0)
            {
                if (dto.getActualAmtSys().compareTo(BigDecimal.ZERO) > 0)
                {
                    dto.setActualAmtSys(dto.getActualAmtSys().add(dto.getActualAmtMarket()));
                    dto.setActualAmtMarket(BigDecimal.ZERO);
                }
                else if (dto.getActualAmtVendor().compareTo(BigDecimal.ZERO) > 0)
                {
                    dto.setActualAmtVendor(dto.getActualAmtVendor().add(dto.getActualAmtMarket()));
                    dto.setActualAmtMarket(BigDecimal.ZERO);
                }
            }
            if (dto.getActualAmtVendor().compareTo(BigDecimal.ZERO) < 0)
            {
                if (dto.getActualAmtSys().compareTo(BigDecimal.ZERO) > 0)
                {
                    dto.setActualAmtSys(dto.getActualAmtSys().add(dto.getActualAmtVendor()));
                    dto.setActualAmtVendor(BigDecimal.ZERO);
                }
                else if (dto.getActualAmtMarket().compareTo(BigDecimal.ZERO) > 0)
                {
                    dto.setActualAmtMarket(dto.getActualAmtMarket().add(dto.getActualAmtVendor()));
                    dto.setActualAmtVendor(BigDecimal.ZERO);
                }
            }
        }
        return res;
    }
    
    public PageResult<FundDetailsList> queryFundDetails(int page, int pagesize, ZxWithdrawStatus status,
        String startDate, String endDate)
    {
        PageResult<FundDetailsList> res = zxWithdrawDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("status", status)
            .iF(startDate != null)
            .ge("withdrawTime", startDate + " 00:00:00")
            .endIf()
            .iF(endDate != null)
            .le("withdrawTime", endDate + " 23:59:59")
            .endIf()
            .eq("value", CurrentSession.marketPkey())
            //            .iF(!CurrentSession.marketPkey().startsWith(Constant.Operation))
            .eq("ascription", CurrentSession.ascriptionPkey())
            .sort("pkey")
            .sort("withdrawTime")
            .execDto(FundDetailsList.class);
        for (FundDetailsList dto : res.getContent())
        {
            if (ZxUserType.SYSTEM.equals(dto.getType()) || ZxUserType.MARKET.equals(dto.getType())
                || ZxUserType.SELF_MARKET.equals(dto.getType()))
            {
                SysFarmer farmer = sysFarmerDao.get(dto.getValue());
                if (farmer != null) dto.setName(farmer.getName());
            }
            if (ZxUserType.VENDOR.equals(dto.getType()))
            {
                MktVendor vendor = vendorDao.get(Integer.valueOf(dto.getValue()));
                if (vendor != null) dto.setName(vendor.getName());
            }
        }
        return res;
    }
    
    public FundDetailsTotal byFundDetailsTotal()
    {
        FundDetailsTotal res = new FundDetailsTotal();
        res.setMakePaymentAmt(BigDecimal.ZERO);
        res.setPendingSettlementAmt(BigDecimal.ZERO);
        //        List<String> marketKeys = new ArrayList<>();
        //        marketKeys.add(CurrentSession.marketPkey());
        ZxUserType zut = ZxUserType.MARKET;
        if (CurrentSession.marketPkey().startsWith("system_"))
        {
            zut = ZxUserType.SYSTEM;
        }
        ZxUserInfo zxUserInfo = zxUserInfoDao.get(zut, CurrentSession.marketPkey());
        if (zxUserInfo != null) res.setPan(zxUserInfo.getPan());
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        BigDecimal pendingSettlementAmt = BigDecimal.ZERO;
        List<MktOrder> orderList = orderDao.select()
            .eq("orderOir", OrderOir.POINTS_MALL)
            .notEq("orderType", OrderType.INTEGRAL_MSD_ORDER)
            .notEq("orderType", OrderType.INTEGRAL_JD_ORDER)
            .eq("settlementType", SettlementType.NOT_START)
            .eq("ascription", ascriptionPkey)
            .notIn("status", OrderStatus.UNPAID_ORDER, OrderStatus.VOID_ORDER, OrderStatus.REFUNDED_ORDER)
            .exec();
        
        if (orderList != null && !orderList.isEmpty())
        {
            for (MktOrder d : orderList)
            {
                BigDecimal amtn = d.getAmtn();
                if (d.getRefundAmt() != null) amtn = amtn.subtract(d.getRefundAmt());
                BigDecimal payComm =
                    amtn.multiply(Constant.ZxConfig.TJ_COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
                amtn = amtn.subtract(payComm);
                pendingSettlementAmt = pendingSettlementAmt.add(amtn);
            }
        }
        
        // 计算合计
        List<SettlementBillOnPage> list = vendorOrderDao.aggregation()
            .iF(!CurrentSession.marketPkey().startsWith("system_"))
            .eq("farmer", CurrentSession.marketPkey())
            .endIf()
            //          .in("status", SettlementType.NOT_START, SettlementType.DOING, SettlementType.FAIL)
            .eq("status", SettlementType.NOT_START)
            .eq("ascription", ascriptionPkey)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .sum("payComm", "payComm")
            .sum("commissions", "oldPostage")
            .sum("postage", "postage")
            .sum("sysCommissions", "sysCommissions")
            .sum("marketCommissions", "marketCommissions")
            .groupby("farmer", "farmer")
            .groupby("commissionType", "commissionType")
            .execListDto(SettlementBillOnPage.class);
        
        Map<String, Boolean> bMap = sysFarmerConfigDao.mapIsEnterprise(ascriptionPkey);
        if (!list.isEmpty())
        {
            for (SettlementBillOnPage d : list)
            {
                if (ZxUserType.SYSTEM.equals(zut))
                {
                    if (CommissionType.BLOC.equals(d.getCommissionType()) && d.getPayComm() != null)
                    {
                        pendingSettlementAmt = pendingSettlementAmt.subtract(d.getPayComm());
                    }
                    if (d.getSysCommissions() != null)
                        pendingSettlementAmt = pendingSettlementAmt.add(d.getSysCommissions());
                    else if (d.getOldPostage() != null)
                        pendingSettlementAmt = pendingSettlementAmt.add(d.getOldPostage());
                    if (!Boolean.TRUE.equals(bMap.get(d.getFarmer())) && d.getPostage() != null)
                        pendingSettlementAmt = pendingSettlementAmt.add(d.getPostage());
                }
                if (ZxUserType.MARKET.equals(zut))
                {
                    if (CommissionType.MARKET.equals(d.getCommissionType()) && d.getPayComm() != null)
                    {
                        pendingSettlementAmt = pendingSettlementAmt.subtract(d.getPayComm());
                    }
                    if (d.getMarketCommissions() != null)
                        pendingSettlementAmt = pendingSettlementAmt.add(d.getMarketCommissions());
                    else if (d.getOldPostage() != null)
                        pendingSettlementAmt = pendingSettlementAmt.add(d.getOldPostage());
                    if (Boolean.TRUE.equals(bMap.get(d.getFarmer())) && d.getPostage() != null)
                        pendingSettlementAmt = pendingSettlementAmt.add(d.getPostage());
                }
            }
        }
        
        Number execSum = memberActivityDao.aggregation()
            .notEq("status", OrderStatus.UNPAID_ORDER)
            .notEq("status", OrderStatus.REFUNDED_ORDER)
            .notEq("status", OrderStatus.VOID_ORDER)
            .in("settlementType", SettlementType.NOT_START, SettlementType.DOING, SettlementType.FAIL)
            .eq("ascription", ascriptionPkey)
            .iF(!CurrentSession.marketPkey().startsWith("system_"))
            .eq("farmer", CurrentSession.marketPkey())
            .endIf()
            .ge("amt", 0)
            .execSum("amt");
        System.out.println("execSum: " + execSum);
        if (execSum != null)
        {
            BigDecimal bigDecimal = new BigDecimal(execSum.toString());
            BigDecimal setScale =
                bigDecimal.multiply(Constant.ZxConfig.TJ_COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
            bigDecimal = bigDecimal.subtract(setScale);
            res.setPendingSettlementAmt(res.getPendingSettlementAmt().add(bigDecimal));
        }
        ZxUserInfo zui = zxUserInfoDao.get(zut, CurrentSession.marketPkey());
        if (zui != null && zui.getComms() != null) res.setMakePaymentAmt(zui.getComms());
        
        res.setPendingSettlementAmt(res.getPendingSettlementAmt().add(pendingSettlementAmt));
        return res;
    }
    
    // 天津 中信查账使用
    public void test()
        throws IOException
    {
        String folderPath = "D:\\gitclone\\lejia-server\\distribution\\lejia-server-4.2.7.Test.Dev-SNAPSHOT\\数据"; // 替换为文件夹的实际路径
        List<TjZxInfo> list = listTjZxInfo(folderPath);
        Map<String, TjZxInfo> map = new HashMap<>();
        for (TjZxInfo t : list)
        {
            if (!"2".equals(t.getQdLy()))
            {
                continue;
            }
            String sysCode = t.getSysCode();
            sysCode = sysCode.substring(0, sysCode.indexOf("s"));
            if (!map.containsKey(sysCode))
            {
                TjZxInfo d = new TjZxInfo();
                d.setAmtn("0");
                d.setSysAmt("0");
                d.setDate(t.getDate());
                map.put(sysCode, d);
            }
            TjZxInfo info = map.get(sysCode);
            BigDecimal amtn = new BigDecimal(info.getAmtn());
            amtn = amtn.add(new BigDecimal(t.getAmtn()));
            info.setAmtn(amtn.toString());
            
            BigDecimal sysAmt = new BigDecimal(info.getSysAmt());
            sysAmt = sysAmt.add(new BigDecimal(t.getSysAmt()));
            info.setSysAmt(sysAmt.toString());
        }
        //        System.out.println("map: " + JsonUtil.toString(map, true));
        List<MktOrder> exec = orderDao.select()
            .eq("ascription", 13)
            .between("createdTime", "2025-07-10 00:00:00", "2025-08-01 00:00:00")
            .notEq("status", OrderStatus.VOID_ORDER)
            .notEq("status", OrderStatus.UNPAID_ORDER)
            .exec();
        List<Integer> orderKeys = new ArrayList<>();
        exec.forEach(e -> orderKeys.add(e.getPkey()));
        List<MktVendorOrder> voList = vendorOrderDao.select()
            .in("orderPkey", orderKeys)
            .eq("ascription", 13)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
            .notEq("purchaseStatus", PurchaseStatus.AWAIT_PURCHASE)
            .exec();
        Map<Integer, MktOrder> orderMap = new HashMap<>();
        exec.forEach(e -> orderMap.put(e.getPkey(), e));
        Map<String, MktVendorOrder> vendorMap = new HashMap<>();
        
        voList.forEach(e -> {
            if (orderMap.containsKey(e.getOrderPkey()))
            {
                MktOrder order = orderMap.get(e.getOrderPkey());
                if (!vendorMap.containsKey(order.getCode()))
                {
                    // 订单支付金额 放在该字段临时使用
                    e.setPackingCharge(order.getAmtn());
                    // 时间使用订单的时间
                    e.setCreatedTime(order.getCreatedTime());
                    vendorMap.put(order.getCode(), e);
                }
                else
                {
                    MktVendorOrder vo = vendorMap.get(order.getCode());
                    vo.setAmt(vo.getAmt().add(e.getAmt()));
                    vo.setPayComm(vo.getPayComm().add(e.getPayComm()));
                    vo.setCommissions(vo.getCommissions().add(e.getCommissions()));
                }
                
            }
        });
        Map<String, String> errMap = new HashMap<>();
        Map<String, BigDecimal> errDateMap = new HashMap<>();
        Map<String, BigDecimal> errSysDateMap = new HashMap<>();
        Map<String, BigDecimal> errAmtMap = new HashMap<>();
        BigDecimal errSysPayComm = BigDecimal.ZERO;
        BigDecimal errVendorAmt = BigDecimal.ZERO;
        for (String key : map.keySet())
        {
            if (vendorMap.containsKey(key))
            {
                TjZxInfo info = map.get(key);
                MktVendorOrder vo = vendorMap.get(key);
                // 订单支付金额
                BigDecimal orderAmtn = vo.getPackingCharge();
                BigDecimal amtn = new BigDecimal(info.getAmtn());
                
                BigDecimal payComm = vo.getPayComm();
                BigDecimal commissions = vo.getCommissions();
                //                BigDecimal commissionRate = vo.getCommissionRate();
                //                orderAmtn = orderAmtn.subtract(vo.getPayComm())
                BigDecimal sysComm = commissions.subtract(payComm);
                BigDecimal sysAmt = new BigDecimal(info.getSysAmt());
                
                String formatDate = DateUtil.formatDate(vo.getCreatedTime(), "yyyy-MM-dd");
                if (amtn.compareTo(orderAmtn) == 0)
                {
                    if (sysComm.compareTo(sysAmt) != 0)
                    {
                        errMap.put(key, "平台分佣不正确,文件平台分佣金额: " + sysAmt + " 订单平台分佣: " + sysComm);
                        errAmtMap.put(key, sysAmt.subtract(sysComm));
                        errSysPayComm = errSysPayComm.add(sysAmt.subtract(sysComm));
                        
                        if (!errSysDateMap.containsKey(formatDate))
                        {
                            errSysDateMap.put(formatDate, BigDecimal.ZERO);
                        }
                        errSysDateMap.put(formatDate, errSysDateMap.get(formatDate).add(sysAmt.subtract(sysComm)));
                    }
                    else
                    {
                        errMap.put(key, "清分总金额不对,手续费的钱多分给了商户, 应该减去手续费, 订单手续费: " + vo.getPayComm());
                        errAmtMap.put(key, vo.getPayComm());
                        errVendorAmt = errVendorAmt.add(vo.getPayComm());
                        if (!errDateMap.containsKey(formatDate))
                        {
                            errDateMap.put(formatDate, BigDecimal.ZERO);
                        }
                        errDateMap.put(formatDate, errDateMap.get(formatDate).add(vo.getPayComm()));
                    }
                    continue;
                }
                // 平台分佣
                if (sysComm.compareTo(sysAmt) != 0)
                {
                    errMap.put(key, "平台分佣不正确,文件平台分佣金额: " + sysAmt + " 订单平台分佣: " + sysComm);
                    errAmtMap.put(key, sysAmt.subtract(sysComm));
                    errSysPayComm = errSysPayComm.add(sysAmt.subtract(sysComm));
                    
                    if (!errSysDateMap.containsKey(formatDate))
                    {
                        errSysDateMap.put(formatDate, BigDecimal.ZERO);
                    }
                    errSysDateMap.put(formatDate, errSysDateMap.get(formatDate).add(sysAmt.subtract(sysComm)));
                }
//                if (amtn.compareTo(orderAmtn) == 0)
//                {
//                    if (sysComm.compareTo(sysAmt) != 0)
//                    {
//                        errMap.put(key, "平台分佣不正确,文件平台分佣金额: " + sysAmt + " 订单平台分佣: " + sysComm);
//                        errAmtMap.put(key, sysAmt.subtract(sysComm));
//                        errSysPayComm = errSysPayComm.add(sysAmt.subtract(sysComm));
//                        
//                        if (!errSysDateMap.containsKey(info.getDate()))
//                        {
//                            errSysDateMap.put(info.getDate(), BigDecimal.ZERO);
//                        }
//                        errSysDateMap.put(info.getDate(), errSysDateMap.get(info.getDate()).add(sysAmt.subtract(sysComm)));
//                    }
//                    else
//                    {
//                        errMap.put(key, "清分总金额不对,手续费的钱多分给了商户, 应该减去手续费, 订单手续费: " + vo.getPayComm());
//                        errAmtMap.put(key, vo.getPayComm());
//                        errVendorAmt = errVendorAmt.add(vo.getPayComm());
//                        if (!errDateMap.containsKey(info.getDate()))
//                        {
//                            errDateMap.put(info.getDate(), BigDecimal.ZERO);
//                        }
//                        errDateMap.put(info.getDate(), errDateMap.get(info.getDate()).add(vo.getPayComm()));
//                    }
//                    continue;
//                }
//                // 平台分佣
//                if (sysComm.compareTo(sysAmt) != 0)
//                {
//                    errMap.put(key, "平台分佣不正确,文件平台分佣金额: " + sysAmt + " 订单平台分佣: " + sysComm);
//                    errAmtMap.put(key, sysAmt.subtract(sysComm));
//                    errSysPayComm = errSysPayComm.add(sysAmt.subtract(sysComm));
//                    
//                    if (!errSysDateMap.containsKey(info.getDate()))
//                    {
//                        errSysDateMap.put(info.getDate(), BigDecimal.ZERO);
//                    }
//                    errSysDateMap.put(info.getDate(), errSysDateMap.get(info.getDate()).add(sysAmt.subtract(sysComm)));
//                }
            }
        }
        System.out.println("错误订单数量: " + errMap.keySet().size());
        //        System.out.println("errMap: " + JsonUtil.toString(errMap, true));
        //        System.out.println("errAmtMap: " + JsonUtil.toString(errAmtMap, true));
        System.out.println("errDateMap: " + JsonUtil.toString(errDateMap, true));
        System.out.println("errSysDateMap: " + JsonUtil.toString(errSysDateMap, true));
        System.out.println("划给平台多了的金额: " + errSysPayComm);
        System.out.println("划给商户多了的金额: " + errVendorAmt);
        
    }
    
    private List<TjZxInfo> listTjZxInfo(String folderPath)
        throws IOException
    {
        List<TjZxInfo> list = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.isDirectory())
        {
            return list;
        }
        File[] files = folder.listFiles();
        for (File file : files)
        {
            if (!file.isDirectory()) continue;
            File[] listFiles = file.listFiles();
            for (File wfile : listFiles)
            {
                if (wfile.getName().endsWith(".ZIP")) continue;
                byte[] allBytes = Files.readAllBytes(wfile.toPath());
                String s = new String(allBytes);
                String[] split = s.split("\n");
                for (int i = 0; i < split.length; i++)
                {
                    String[] sp;
                    if (s.contains("@"))
                    {
                        sp = split[i].split("@");
                    }
                    else
                    {
                        char cc = 0x03;
                        sp = split[i].split(String.valueOf(cc));
                    }
                    
                    TjZxInfo dto = new TjZxInfo();
                    dto.setMchntId(sp[0]);
                    dto.setUserId(sp[1]);
                    dto.setDate(sp[2]);
                    dto.setDateTime(sp[3]);
                    dto.setQdName(sp[4]);
                    dto.setSysCode(sp[5]);
                    dto.setKcCode(sp[6]);
                    dto.setPayCode(sp[7]);
                    dto.setMicCode(sp[8]);
                    dto.setPayType(sp[9]);
                    dto.setType(sp[10]);
                    dto.setQdLy(sp[11]);
                    dto.setQdCd(sp[12]);
                    dto.setAmto(sp[13]);
                    dto.setAmtn(sp[14]);
                    dto.setSysCoupon(sp[15]);
                    dto.setSysAmt(sp[16]);
                    dto.setSysd(sp[17]);
                    dto.setQdComm(sp[18]);
                    dto.setUserRole(sp[19]);
                    list.add(dto);
                }
            }
        }
        return list;
    }
}
