package cn.tofocus.lejia.domain;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.lejia.bean.enums.v2.ZxCardStatus;
import cn.tofocus.lejia.zx.beanV2.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.util.StringUtils;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOnInfo;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.member.MktMemberActivity;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWalletLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWithdrawal;
import cn.tofocus.lejia.bean.entity.zx.ZxFileRecord;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.entity.zx.ZxWithdraw;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.ZxFileStatus;
import cn.tofocus.lejia.bean.enums.ZxFileType;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.ZxWithdrawStatus;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import cn.tofocus.lejia.bean.enums.vendor.WithdrawalStatus;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktMemberActivityDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWalletLineDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWithdrawalDao;
import cn.tofocus.lejia.dao.zx.ThirdPayLineDao;
import cn.tofocus.lejia.dao.zx.ZxFileRecordDao;
import cn.tofocus.lejia.dao.zx.ZxUserInfoDao;
import cn.tofocus.lejia.dao.zx.ZxWithdrawDao;
import cn.tofocus.lejia.domain.vendor.VendorWalletUpdManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.zx.sendMethodV2.BaseSendMethod;
import cn.tofocus.lejia.zx.utilV2.Constants;
import cn.tofocus.lejia.zx.utilV2.HttpsPost;
import cn.tofocus.lejia.zx.utilV2.SignUtil;
import cn.tofocus.lejia.zx.utilV2.Utils;
import cn.tofocus.lejia.zx.utilV2.XstreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TjZxManager
{
    private final MktVendorOrderDao vendorOrderDao;
    
    private final MktVendorDao vendorDao;
    
    private final MktOrderDao orderDao;
    
    private final SysFarmerConfigDao sysFarmerConfigDao;
    
    private final TjZxFileManager tjZxFileManager;
    
    private final MktMemberActivityDao memberActivityDao;
    
    private final ZxFileRecordDao zxFileRecordDao;
    
    private final ZxUserInfoDao zxUserInfoDao;
    
    private final ZxWithdrawDao zxWithdrawDao;
    
    private final RedisLockTemplate lock;
    
    private final VendorWalletUpdManager vendorWalletUpdManager;
    
    private final MktVendorWalletLineDao vendorWalletLineDao;
    
    private final MktVendorWithdrawalDao vendorWithdrawalDao;
    
    private final ThirdPayLineDao thirdPayLineDao;
    
    @Value("${wx.pay.test:false}")
    private Boolean wxPayTest;
    
    // 根据时间清分 date 格式 yyyy-MM-dd,  flag=null 跑所有的数据  flag=true 跑积分商城的数据 flag=false 跑所有市场的数据
    public void runSettle(String date, Integer ascription, Boolean flag)
    {
        BigDecimal s = BigDecimal.ZERO;
        // 获取指定日期需要清分的所有订单
        List<MktOrder> list = orderDao.select()
            .in("status",
                OrderStatus.CONFIRM_ORDER,
                OrderStatus.REFUND_APPLICATION_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.SHIPPED_ORDER)
//            .notEq("orderType", OrderType.INTEGRAL_MSD_ORDER)
//            .notEq("orderType", OrderType.INTEGRAL_JD_ORDER)
            
            .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
            .eq("settlementType", SettlementType.NOT_START)
            .ge("createdTime", "2025-07-10 00:00:00")
            .eq("ascription", ascription)
            .iF(Boolean.TRUE.equals(flag))
                .eq("farmer", Constant.Operation + ascription)
                .notEq("payType", PayType.ORDER_MSD)
                .le("createdTime", date + " 23:59:59")
            .endIf()
            .iF(Boolean.FALSE.equals(flag))
            	.notEq("farmer", Constant.Operation + ascription)
//            	.between("createdTime", date + " 00:00:00", date + " 23:59:59")
            	.le("createdTime", date + " 23:59:59")
        	.endIf()
            .exec();
        List<MktMemberActivity> maList = memberActivityDao.select()
            .between("createdTime", date + " 00:00:00", date + " 23:59:59")
            .eq("ascription", ascription)
            .eq("settlementType", SettlementType.NOT_START)
            .gt("amt", 0)
            .eq("status", OrderStatus.CONFIRM_ORDER)
            .iF(Boolean.TRUE.equals(flag))
            	.eq("farmer", Constant.Operation + ascription)
            .endIf()
	        .iF(Boolean.FALSE.equals(flag))
	        	.notEq("farmer", Constant.Operation + ascription)
	    	.endIf()
            .exec();
        if (maList != null && !maList.isEmpty())
        {
            for (MktMemberActivity ma : maList)
            {
                MktOrder o = new MktOrder();
                o.setPkey(-3);
                o.setCode(ma.getCode());
                o.setAmtn(ma.getAmt());
                o.setCreatedTime(ma.getCreatedTime());
                o.setFarmer(ma.getFarmer());
                list.add(o);
                ma.setSettlementType(SettlementType.DOING);
            }
            memberActivityDao.updateAll(maList);
        }
        System.out.println("list: " + list.size());
        if (!list.isEmpty())
        {
            for(MktOrder o : list)
            {
                s = s.add(o.getAmtn());
            }
            // 正式服 需要走账户验证
            if(Boolean.FALSE.equals(wxPayTest))
            {
                String t2206 = t2206(Constants.MCHNT_ID, Constants.ascription + "");
                System.out.println("担保账户余额: " + t2206);
                System.out.println("需要结算的金额: " + s);
                if(s.compareTo(new BigDecimal(t2206)) > 0)
                {
                    System.out.println("担保账户余额不够,不生成结算文件");
                }
                else
                    runSettle(list, date.replace("-", ""), ascription, maList);
            }
            else
                runSettle(list, date.replace("-", ""), ascription, maList);
        }
    }
    
    public String appointOrder(Integer pkey)
    {
        MktOrder order = orderDao.get(pkey);
        if(order == null)
            return "订单不存在";
        List<MktOrder> list = new ArrayList<>();
        list.add(order);
        String format = DateUtils.format(order.getCreatedTime(), "yyyyMMdd");
        runSettle(list, format, order.getAscription(), new ArrayList<>());
        return "文件生成成功";
    }
    
    // 卡券活动清分  date 格式 yyyy-MM-dd
    public void runSettleMemberActivity(String date, Integer ascription)
    {
        List<MktOrder> list = new ArrayList<>();
        List<MktMemberActivity> maList = memberActivityDao.select()
            .between("createdTime", date + " 00:00:00", date + " 23:59:59")
            .eq("ascription", ascription)
            .eq("settlementType", SettlementType.NOT_START)
            .eq("status", OrderStatus.CONFIRM_ORDER)
            .exec();
        if (maList != null && !maList.isEmpty())
        {
            for (MktMemberActivity ma : maList)
            {
                if (ma.getAmt().compareTo(BigDecimal.ZERO) > 0)
                {
                    MktOrder o = new MktOrder();
                    o.setPkey(-3);
                    o.setCode(ma.getCode());
                    o.setAmtn(ma.getAmt());
                    o.setCreatedTime(ma.getCreatedTime());
                    o.setFarmer(ma.getFarmer());
                    list.add(o);
                    ma.setSettlementType(SettlementType.DOING);
                }
            }
            memberActivityDao.updateAll(maList);
        }
        if (!list.isEmpty()) runSettle(list, date.replace("-", ""), ascription, maList);
    }
    
    // 根据指定订单清分
    public void runSettle(List<MktOrder> list, String day, Integer ascription, List<MktMemberActivity> maList)
    {
        StringBuilder sb = new StringBuilder();
        // 所有订单主键
        List<Integer> keyList = CollectionUtil.keyList(list);
        if (keyList == null || keyList.isEmpty()) return;
        List<String> formIds = new ArrayList<>();
        list.forEach(e -> formIds.add(e.getCode()));
        // 所有商户订单
        List<MktVendorOrder> voList = vendorOrderDao.select()
            .eq("status", SettlementType.NOT_START)
            .in("orderPkey", keyList)
            .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
            .ge("amt", 0)
            .exec();
        Map<Integer, List<MktVendorOrder>> map = new HashMap<>();
        Map<Integer, List<MktVendorOrder>> zeroMap = new HashMap<>();
        voList.forEach(e -> {
            if (e.getAmt().compareTo(BigDecimal.ZERO) == 0)
            {
                if (!zeroMap.containsKey(e.getOrderPkey()))
                    zeroMap.put(e.getOrderPkey(), new ArrayList<>());
                zeroMap.get(e.getOrderPkey()).add(e);
            }
            else
            {
                if (!map.containsKey(e.getOrderPkey()))
                    map.put(e.getOrderPkey(), new ArrayList<>());
                map.get(e.getOrderPkey()).add(e);
            }
            e.setStatus(SettlementType.DOING);
        });
        List<MktVendorWalletLine> vwlList = vendorWalletLineDao.select()
        .eq("status", SettlementType.NOT_START)
        .in("formId", formIds)
        .exec();
        vwlList.forEach(e ->
        {
            e.setStatus(SettlementType.DOING);
        });
        
        //所有商户
        //        Map<Integer,MktVendor> vendorMap = vendorDao.mapZxAsc(ascription);
        Map<String,ZxWithdraw> wMap = new HashMap<>();
        //  所有支付订单流水号
        Map<String, String> tranMap = thirdPayLineDao.tranMap(day);
//        Map<String, String> tranMap = new HashMap<>();
        
        // 查询市场信息,是否是民营企业
        List<SysFarmerConfig> fcList = sysFarmerConfigDao.select().eq("ascription", ascription).exec();
        Map<String, SysFarmerConfig> fcMap = new HashMap<>();
        fcList.forEach(e -> fcMap.put(e.getPkey(), e));
        int num = 0;
        ZxUserInfo zui = zxUserInfoDao.whateverInfo(ascription);
        Map<String, ZxUserInfo> mapZxInfo = zxUserInfoDao.mapZxUserInfo(ascription);
//        Map<String, String> mapZx = zxUserInfoDao.mapZxUserId(ascription);
        
        // 平台提现的金额  
        ZxWithdraw sysW = new ZxWithdraw(ZxUserType.SYSTEM, Constants.MCHNT_ID, "system_" + ascription, ascription);
        if(mapZxInfo.containsKey(ZxUserType.SYSTEM + "_" + "system_" + ascription))
        {
            ZxUserInfo zxUserInfo = mapZxInfo.get(ZxUserType.SYSTEM + "_" + "system_" + ascription);
            if(!Boolean.TRUE.equals(zxUserInfo.getMarketAuto()))
                sysW.setStatus(ZxWithdrawStatus.MANUAL_MAKE_PAYMENT);
        }
        wMap.put(sysW.getZxUserId(), sysW);
        
        // 循环订单 进行清分
        for (MktOrder o : list)
        {
            o.setSettlementType(SettlementType.DOING);
            String transactionId = tranMap.get(o.getCode().substring(0, 14));
            // TODO 临时使用,等支付渠道进来
            if (StringUtils.isBlank(transactionId))
                transactionId = o.getCode() + DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
            SysFarmerConfig config = fcMap.get(o.getFarmer());
            sysW.setBillDate(DateUtil.formatDate(o.getCreatedTime(), "yyyy-MM-dd"));
            // 需要清分给商户
            if (map.containsKey(o.getPkey()))
            {
                List<MktVendorOrder> mapVoList = map.get(o.getPkey());
                MktVendorOrder vo = mapVoList.get(0);
                // 手续费承担
                CommissionType commissionType = vo.getCommissionType();
                // 是否是民营企业
                MktVendorOrder mvo = new MktVendorOrder();
                if (Boolean.TRUE.equals(config.getIsEnterprise()))
                {
                    if (CommissionType.BLOC.equals(commissionType)
                        && (vo.getCommissions() == null || vo.getCommissions().compareTo(BigDecimal.ZERO) == 0))
                        throw TofocusException.of(LejiaErrCode.ZX_PAYCOMM_ERROR);
                    // 增加民营企业应该获得金额
                    mvo.setPkey(-5);
//                    mvo.setAmt(BigDecimal.ZERO);
                    mvo.setCommissions(BigDecimal.ZERO);
                    mvo.setPayComm(BigDecimal.ZERO);
                    // 如果是民营市场 运费全部划给民营市场 
                    BigDecimal postage = o.getPostage();
                    if (postage == null) postage = BigDecimal.ZERO;
                    mvo.setAmt(postage);
                    mvo.setPostage(BigDecimal.ZERO);
                    mvo.setCommissionType(commissionType);
                    for (MktVendorOrder vodto : mapVoList)
                    {
                        if(vodto.getMarketCommissions() != null)
                            mvo.setAmt(mvo.getAmt().add(vodto.getMarketCommissions()));
                        else
                            mvo.setAmt(mvo.getAmt().add(vodto.getCommissions()));
                        if (CommissionType.MARKET.equals(commissionType))
                        {
                            mvo.setAmt(mvo.getAmt().subtract(vodto.getPayComm()));
                        }
                    }
                    mapVoList.add(mvo);
                }
                num = num + mapVoList.size();
                // 商户业务订单号
                o.getCode();
                
                // 交易日期 交易时间
                DateUtil.formatDate(o.getCreatedTime(), "yyyyMMddHHmmss");
                BigDecimal s = BigDecimal.ZERO;
                for (MktVendorOrder dto : mapVoList)
                {
                    // 平台优惠金额
                    BigDecimal discountAmt = dto.getDiscountAmt();
                    if (discountAmt == null) discountAmt = BigDecimal.ZERO;
                    if (dto.getDiscountRefundAmt() != null)
                        discountAmt = discountAmt.subtract(dto.getDiscountRefundAmt());
                    BigDecimal postage = dto.getPostage();
                    if (postage == null) postage = BigDecimal.ZERO;
                    // 平台分成金额
                    BigDecimal commissions = dto.getCommissions();
                    if (Boolean.TRUE.equals(config.getIsEnterprise()))
                    {
                        commissions = dto.getSysCommissions();
                        if (dto.getSysCommissions() == null) commissions = BigDecimal.ZERO;
                        postage = BigDecimal.ZERO;
                    }
                    // 市场佣金
                    BigDecimal sysCommissions = new BigDecimal(commissions.toPlainString());
                    // 原始金额（加上佣金，没处理手续费）
                    BigDecimal oriAmt = dto.getAmt().add(commissions);
                    // 实际金额
                    BigDecimal amt = dto.getAmt();
                    if (CommissionType.BLOC.equals(commissionType))
                    {
                        // 手续费直接扣除
                        sysCommissions = sysCommissions.subtract(dto.getPayComm());
                        oriAmt = oriAmt.subtract(dto.getPayComm());
                    }
                    else if (CommissionType.MERCHANT.equals(commissionType))
                    {
                        // 商户承担手续费 从结算给商户的金额里扣除
                        oriAmt = oriAmt.subtract(dto.getPayComm());
                        amt = amt.subtract(dto.getPayComm());
                        if(oriAmt.compareTo(BigDecimal.ZERO) < 0)
                        {
                            s = oriAmt.abs();
                            if (!Boolean.TRUE.equals(config.getIsEnterprise()))
                                postage = postage.subtract(s);
                            oriAmt = BigDecimal.ZERO;
                        }
                        if(dto.getPkey() == -5)
                           oriAmt = dto.getAmt().subtract(s);
                    }
                    // 市场承担手续费 已经在上方处理
                    // 传文件接口,生成数据  J04059100000051(测试服用的 市场zxUserid)
                    String zxUserId = "J01097900000051";
                    ZxUserType zut = ZxUserType.VENDOR;
                    String value = dto.getVendor() + "";
                    ZxWithdrawStatus zws = ZxWithdrawStatus.NOT_MAKE_PAYMENT;
                    if (dto.getPkey() == -5)
                    {
                        ZxUserInfo zxUserInfo = mapZxInfo.get(ZxUserType.MARKET + "_" + config.getPkey());
                        value = config.getPkey();
                        if(zxUserInfo != null)
                        {
                            zxUserId = zxUserInfo.getZxUserId();
                            if(!Boolean.TRUE.equals(zxUserInfo.getMarketAuto()))
                                zws = ZxWithdrawStatus.MANUAL_MAKE_PAYMENT;
                        }
                        else
                            throw TofocusException.of(LejiaErrCode.ZX_USERID_ERROR);
                        zut = ZxUserType.MARKET;
                    }
                    else
                    {
                        ZxUserInfo zxUserInfo = mapZxInfo.get(ZxUserType.VENDOR + "_" + dto.getVendor());
                        if(zxUserInfo != null)
                        {
                            zxUserId = zxUserInfo.getZxUserId();
                        }
                        else
                            throw TofocusException.of(LejiaErrCode.ZX_USERID_ERROR);
                        if(!Boolean.TRUE.equals(config.getIsEnterprise()))
                        {
//                            zxUserInfo = mapZxInfo.get(ZxUserType.SYSTEM + "_system_" + dto.getAscription());
                            zxUserInfo = zxUserInfoDao.get(ZxUserType.SELF_MARKET, dto.getFarmer());
                            if(zxUserInfo != null && !Boolean.TRUE.equals(zxUserInfo.getVendorAuto()))
                                zws = ZxWithdrawStatus.MANUAL_MAKE_PAYMENT;
                        }
                        else
                        {
                            zxUserInfo = mapZxInfo.get(ZxUserType.MARKET + "_" + dto.getFarmer());
                            if(zxUserInfo != null && !Boolean.TRUE.equals(zxUserInfo.getVendorAuto()))
                                zws = ZxWithdrawStatus.MANUAL_MAKE_PAYMENT;
                        }
                    }
                    sysW.setComms(sysW.getComms().add(sysCommissions).add(postage));
                    if(!wMap.containsKey(zxUserId))
                    {
                        ZxWithdraw zw = new ZxWithdraw(zut, zxUserId, value, ascription);
                        zw.setBillDate(DateUtil.formatDate(o.getCreatedTime(), "yyyy-MM-dd"));
                        zw.setStatus(zws);
                        wMap.put(zxUserId, zw);
                    }
                    ZxWithdraw zw = wMap.get(zxUserId);
                    zw.setComms(zw.getComms().add(amt));
                    
                    if(oriAmt.compareTo(BigDecimal.ZERO) != 0
                        || sysCommissions.compareTo(BigDecimal.ZERO) != 0
                            || postage.compareTo(BigDecimal.ZERO) != 0)
                        tjZxFileManager.assembleFileContent(sb,
                            zxUserId,
                            o.getCode(),
                            transactionId,
                            dto.getPkey(),
                            oriAmt,
                            sysCommissions,
                            postage,
                            new Date());
                }
            }
            // 整单都不需要清分给商户 || 部分vendorOrder不需要清分给商户
            if (!map.containsKey(o.getPkey()) || zeroMap.containsKey(o.getPkey()))
            {
                // 不需要清分给商户 积分商城卖出去的或者市场做的卡券活动
                BigDecimal payCommissionRate = Constant.ZxConfig.TJ_COMMISSION_RATE;
                BigDecimal amtn = o.getAmtn();
                BigDecimal sysComm = BigDecimal.ZERO;
                if (o.getRefundAmt() != null) amtn = amtn.subtract(o.getRefundAmt());
                BigDecimal payCommission = amtn.multiply(payCommissionRate).setScale(2, RoundingMode.HALF_UP);
                amtn = amtn.subtract(payCommission);
                // 部分vendorOrder不需要清分给商户，修改amtn为剩余部分的金额总和
//                if (map.containsKey(o.getPkey()) && zeroMap.containsKey(o.getPkey()))
                if (zeroMap.containsKey(o.getPkey()))
                {
                    List<MktVendorOrder> zeroVendorOrders = zeroMap.get(o.getPkey());
                    amtn = BigDecimal.ZERO;
                    for (MktVendorOrder zvo : zeroVendorOrders)
                    {
                        amtn = amtn.add(zvo.getCommissions()).subtract(zvo.getPayComm());
                        if(zvo.getSysCommissions() != null)
                            sysComm = sysComm.add(zvo.getSysCommissions()); 
                    }
                }
                num += 1;
                // 区分是积分商城还是市场送出去的卡券
                if (o.getFarmer().startsWith(Constant.Operation) || !Boolean.TRUE.equals(config.getIsEnterprise()))
                {
                    // 直接全部划给集团方, 正常不需要调接口 所有的钱已经在担保登记簿
                    // 优惠券的垫付 走担保登记簿
                    // 直接传数据,用户为空 钱直接进入平台商户的自有登记簿
                    // 用户编号  这里随便传一个用户编号就可以
                    // 2026-07-10 修改热力豆组合支付结算模式 
                    if(PayType.MSD_COMBINATION.equals(o.getPayType()))
                    {
                        amtn = o.getWeixinAmt();
                        if(o.getRefundWeixinAmt() != null)
                            amtn = amtn.subtract(o.getRefundWeixinAmt());
                        payCommission = amtn.multiply(payCommissionRate).setScale(2, RoundingMode.HALF_UP);
                        amtn = amtn.subtract(payCommission);
                    }
                    tjZxFileManager.assembleFileContent(sb,
                        zui.getZxUserId(),
                        o.getCode(),
                        transactionId,
                        0,
                        amtn,
                        amtn,
                        BigDecimal.ZERO,
                        new Date());
                    sysW.setComms(sysW.getComms().add(amtn));
                }
                else
                {
                    // J04059100000051(测试服用的 市场zxUserid)
                    String zxUserId = "J01097900000051";
//                    zxUserId = mapZx.get(ZxUserType.MARKET + "_" + config.getPkey());
                    ZxWithdrawStatus zws = ZxWithdrawStatus.NOT_MAKE_PAYMENT;
                    ZxUserInfo zxUserInfo = mapZxInfo.get(ZxUserType.MARKET + "_" + config.getPkey());
                    if(zxUserInfo != null)
                    {
                        zxUserId = zxUserInfo.getZxUserId();
                        if(!Boolean.TRUE.equals(zxUserInfo.getMarketAuto()))
                            zws = ZxWithdrawStatus.MANUAL_MAKE_PAYMENT;
                    }
                    else
                        throw TofocusException.of(LejiaErrCode.ZX_USERID_ERROR);
                    
                    // 传文件接口,生成数据
                    tjZxFileManager.assembleFileContent(sb,
                        zxUserId,
                        o.getCode(),
                        transactionId,
                        0,
                        amtn,
                        sysComm,
                        BigDecimal.ZERO,
                        new Date());
                    
                    if(!wMap.containsKey(zxUserId))
                    {
                        ZxWithdraw zw = new ZxWithdraw(ZxUserType.MARKET, zxUserId, config.getPkey(), ascription);
                        zw.setBillDate(DateUtil.formatDate(o.getCreatedTime(), "yyyy-MM-dd"));
                        zw.setStatus(zws);
                        wMap.put(zxUserId, zw);
                    }
                    ZxWithdraw zw = wMap.get(zxUserId);
                    zw.setComms(zw.getComms().add(amtn));
                }
            }
        }
        ZxFileRecord fileRecord = new ZxFileRecord();
        fileRecord.setStatus(ZxFileStatus.UPLOAD_SYCCESS);
        fileRecord.setType(ZxFileType.QING_FEN);
        fileRecord.setUploadDate(Calendar.getInstance().getTime());
        fileRecord.setAscription(ascription);
        ZxFileRecord add = zxFileRecordDao.add(fileRecord);
        tjZxFileManager.addFile(sb, day, num + "", add);
        
        voList.forEach(e -> e.setFilePkey(add.getPkey()));
        list = list.stream().filter(e -> e.getPkey() != -3).collect(Collectors.toList());
        list.forEach(e -> e.setFilePkey(add.getPkey()));
        maList.forEach(e -> e.setFilePkey(add.getPkey()));
        // 修改order vendor_order 结算状态
        vendorOrderDao.updateAll(voList);
        vendorWalletLineDao.updateAll(vwlList);
        orderDao.updateAll(list);
        memberActivityDao.updateAll(maList);
        // 新增提现记录表
        if(sysW.getComms().compareTo(BigDecimal.ZERO) == 0)
            wMap.remove(sysW.getZxUserId());
        List<ZxWithdraw> zwList = new ArrayList<>(wMap.values());
        zwList.forEach(e -> e.setFilePkey(add.getPkey()));
        zxWithdrawDao.addAll(zwList);
    }
    
    // 生成 渠道来金 到 担保登记簿 的文件
    public void runGuarantee(BigDecimal amt, String transactionId, Date date, ZxFileType zft, String zxUserId,
        Integer ascription)
    {
        StringBuilder sb = new StringBuilder();
        ZxFileRecord fileRecord = new ZxFileRecord();
        fileRecord.setStatus(ZxFileStatus.UPLOAD_SYCCESS);
        fileRecord.setType(zft);
        fileRecord.setUploadDate(Calendar.getInstance().getTime());
        fileRecord.setAscription(ascription);
        ZxFileRecord add = zxFileRecordDao.add(fileRecord);
        // 传文件接口,生成数据
        tjZxFileManager.assembleFileContentGuarantee(sb, Constants.MCHNT_ID, transactionId, amt, date);
        tjZxFileManager.addFile(sb, DateUtil.formatDate(date, "yyyyMMdd"), "1", add);
//        tjZxFileManager.addFile(sb, DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyyMMdd"), "1", add);
        
        if(StringUtils.isBlank(zxUserId))
        {
            log.error("zxUserId字段为空");
            return;
        }
        
        ZxUserInfo info = zxUserInfoDao.byZxUserId(zxUserId);
        if (info != null)
        {
            
            ZxWithdraw zw = new ZxWithdraw(info.getType(), info.getZxUserId(), info.getValue(), ascription);
            zw.setStatus(ZxWithdrawStatus.OFFLINE_RECHARGE);
            zw.setBillDate(DateUtil.formatDate(date, "yyyy-MM-dd"));
            zw.setWithdrawTime(date);
            zw.setComms(amt);
            ZxWithdraw zwp = new ZxWithdraw(info.getType(), info.getZxUserId(), info.getValue(), ascription);
            zwp.setStatus(ZxWithdrawStatus.PADDLE_GUARANTEE);
            zwp.setBillDate(DateUtil.formatDate(date, "yyyy-MM-dd"));
            zwp.setWithdrawTime(date);
            zwp.setComms(amt);
            zxWithdrawDao.add(zw);
            zxWithdrawDao.add(zwp);
            // 根据 入金的方式不一样  进行处理
            // 渠道入金 平台入金 直接进担保账户不需要操作
            // 企业用户入金 个人用户入金 工会 需要给对应的商城钱包里加钱 
            if (ZxUserType.TRADE_UNION.equals(info.getType()) && (ZxFileType.QIYE_RUJIN.equals(zft) || ZxFileType.GREN_RUJIN.equals(zft)))
            {
                // 充值 只处理 民营市场 和 工会
                // 民营市场 只记充的金额合计,后续使用 不处理
                // 工会里的金额 在工会用户充值后 对应减少
                updUserComms(info, amt);
            }
        }
        else
            log.error("云商城没有对应的用户{}", zxUserId);
    
    }
  
    // 工会账户加钱
    private void updUserComms(ZxUserInfo info, BigDecimal comms)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "zxFileComms" + info.getZxUserId());
         
            if(ZxUserType.TRADE_UNION.equals(info.getType()))
            {
                BigDecimal tradeUnionComms = info.getTradeUnionComms();
                if (tradeUnionComms == null) tradeUnionComms = BigDecimal.ZERO;
                info.setTradeUnionComms(tradeUnionComms.add(comms));
                zxUserInfoDao.update(info);
            }
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "zxFileComms" + info.getZxUserId());
        }
    }
    
    // 清分文件完成,后续处理
    public void handle616File(String fileName)
    {
        ZxFileRecord zfr = zxFileRecordDao.byName(fileName);
        if (zfr == null)
        {
            log.error("{}文件不存在", fileName);
            return;
        }
        if(!ZxFileStatus.UPLOAD_SYCCESS.equals(zfr.getStatus()))
        {
            log.error("状态不对,目前状态: {}", zfr.getStatus().getName());
            return;
        }
        zfr.setStatus(ZxFileStatus.FINISH);
        zfr.setUploadDate(Calendar.getInstance().getTime());
        if(ZxFileType.ALLOCATION.equals(zfr.getType()))
        {
            ZxWithdraw zw = zxWithdrawDao.byFilePkey(zfr.getPkey());
            zw.setWithdrawTime(Calendar.getInstance().getTime());
            ZxUserInfo info = zxUserInfoDao.byZxUserId(zw.getZxUserId());
            zw.setBalance(info.getComms().add(zw.getComms()));
            info.setComms(zw.getBalance());
            zxWithdrawDao.update(zw);
            zxUserInfoDao.update(info);
            return;
        }
        List<MktOrder> orderList = orderDao.select().eq("filePkey", zfr.getPkey()).exec();
        List<String> formIds = new ArrayList<>();
        Integer ascription = 22;
        if(!orderList.isEmpty())
        {
            orderList.forEach(e -> 
            {
                e.setSettlementType(SettlementType.SUCCESS);
                formIds.add(e.getCode());
            });
            orderDao.updateAll(orderList);
            ascription = orderList.get(0).getAscription();
        }
        Map<String,List<MktVendorOrder>> voMap = new HashMap<>();
        List<MktVendorOrder> voList = vendorOrderDao.select().eq("filePkey", zfr.getPkey()).exec();
        voList.forEach(e -> 
        {
            e.setStatus(SettlementType.SUCCESS);
            if(!voMap.containsKey(e.getPkey() + ""))
            {
                List<MktVendorOrder> list = new ArrayList<>();
                voMap.put(e.getPkey() + "", list);
            }
            voMap.get(e.getPkey() + "").add(e);
        });
        vendorOrderDao.updateAll(voList);
        List<MktMemberActivity> maList = memberActivityDao.select().eq("filePkey", zfr.getPkey()).exec();
        maList.forEach(e -> e.setSettlementType(SettlementType.SUCCESS));
        if(!maList.isEmpty())
            ascription = maList.get(0).getAscription();
        memberActivityDao.updateAll(maList);
        // 处理提现和钱包金额
        Boolean handleWithdraw = handleWithdraw(zfr.getPkey(), ascription, formIds, voMap);
        if(Boolean.TRUE.equals(handleWithdraw))
        {
            zfr.setStatus(ZxFileStatus.WITHDRAW_FINISH);
        }
        zxFileRecordDao.update(zfr);
    }
    
    public Boolean runAllocation(ZxFileType type, String start, String end)
    {
        List<ZxFileRecord> list = zxFileRecordDao.listAllocation(type, start, end);
        System.out.println("查询需要的文件数量为: " + list.size());
        for(ZxFileRecord zfr : list)
        {
            System.out.println("查询的文件名为: " + zfr.getName() + ".ZIP");
            String t21000032 = t21000032(zfr.getName());
            System.out.println("查询文件结果: " + t21000032);
            if("AA".equals(t21000032))
            {
                ZxWithdraw zw = zxWithdrawDao.byFilePkey(zfr.getPkey());
                zw.setWithdrawTime(Calendar.getInstance().getTime());
                ZxUserInfo info = zxUserInfoDao.byZxUserId(zw.getZxUserId());
                zw.setBalance(info.getComms().add(zw.getComms()));
                info.setComms(zw.getBalance());
                zxWithdrawDao.update(zw);
                zxUserInfoDao.update(info);
            }
        }
        return true;
    }
    
    private Boolean handleWithdraw(Integer filePkey, Integer ascription, List<String> formIds, Map<String,List<MktVendorOrder>> voMap)
    {
        Boolean res = true;
        List<ZxWithdraw> zwList = zxWithdrawDao.listFilePkey(filePkey);
        List<ZxWithdraw> zwAddList = new ArrayList<>();
        Map<String, ZxUserInfo> mapZxInfo = zxUserInfoDao.mapZxUserId(ascription);
        List<ZxUserInfo> zuUpdList = new ArrayList<>();
        // 需要加提现记录的商户
        Map<Integer,Date> map = new HashMap<>();
        if(!zwList.isEmpty())
        {
            for(int i = 0; i < zwList.size(); i++)
            {
                ZxWithdraw zw = zwList.get(i);
                if(ZxWithdrawStatus.NOT_MAKE_PAYMENT.equals(zw.getStatus()))
                {
                    Boolean runWithdraw = true;
                    if(zw.getComms().compareTo(BigDecimal.ZERO) > 0)
                    {
                        if(ZxUserType.SYSTEM.equals(zw.getType()))
                        {
                            runWithdraw = runWithdraw(null, zw.getComms(), "01");
                        }
                        else
                        {
                            if("J01097900000352".equals(zw.getZxUserId()) 
                                || "J01097900002602".equals(zw.getZxUserId())
                                || "J01097900001151".equals(zw.getZxUserId()))
                            {
                                // J01097900000352  85.5
                                // J01097900002602  54.96
                                // J01097900001151  75.53
                                log.info("用户编号: {}, 提现金额: {}, 因2026年5月1日清分文件错误,多打款,暂时不掉提现接口");
                            }
                            else
                                runWithdraw = runWithdraw(zw.getZxUserId(), zw.getComms(), "00");
                        }
                    }
                    if(!Boolean.TRUE.equals(runWithdraw))
                    {
                        zw.setStatus(ZxWithdrawStatus.MAKE_PAYMENT_FAIL);
                        if(voMap.containsKey(zw.getZxUserId()))
                        {
                            List<MktVendorOrder> list = voMap.get(zw.getValue());
                            list.forEach(e -> e.setStatus(SettlementType.FAIL));
                            vendorOrderDao.updateAll(list);
                        }
                        res = false;
                    }
                    try
                    {
                        // 中信提现接口 并发不能超过9次 
                        if(i % 9 == 0)
                            Thread.sleep(10000);
                    }
                    catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                    zw.setWithdrawTime(Calendar.getInstance().getTime());
                    zw.setStatus(ZxWithdrawStatus.INCOME);
                    // 填写用户 当前余额
                    ZxUserInfo zxUserInfo = mapZxInfo.get(zw.getZxUserId());
                    BigDecimal comms = zxUserInfo.getComms();
                    if(comms == null)
                        comms = BigDecimal.ZERO;
                    zw.setBalance(comms);
                    if(zw.getComms() != null)
                        zw.setBalance(zw.getBalance().add(zw.getComms()));
                    ZxWithdraw zwa = new ZxWithdraw();
                    BeanUtils.copyProperties(zw, zwa, "pkey", "balance");
                    zwa.setStatus(ZxWithdrawStatus.MAKE_PAYMENT);
                    zwa.setBalance(comms);
                    zwa.setWithdrawTime(Calendar.getInstance().getTime());
                    zwAddList.add(zwa);
                    if(ZxUserType.VENDOR.equals(zw.getType()))
                    {
                        map.put(Integer.valueOf(zw.getValue()), zw.getWithdrawTime());
                    }
                }
                if(ZxWithdrawStatus.MANUAL_MAKE_PAYMENT.equals(zw.getStatus()))
                {
                    ZxUserInfo zxUserInfo = mapZxInfo.get(zw.getZxUserId());
                    BigDecimal comms = zxUserInfo.getComms();
                    if(comms == null)
                        comms = BigDecimal.ZERO;
                    comms = comms.add(zw.getComms());
                    zxUserInfo.setComms(comms);
                    zuUpdList.add(zxUserInfo);
                    zw.setWithdrawTime(Calendar.getInstance().getTime());
                    zw.setBalance(comms);
                    zw.setStatus(ZxWithdrawStatus.INCOME);
                }
            }
            zxWithdrawDao.updateAll(zwList);
            zxWithdrawDao.addAll(zwAddList);
            log.info("手工提现数据共:{}条", zuUpdList.size());
            if(!zuUpdList.isEmpty())
                zxUserInfoDao.updateAll(zuUpdList);
        }
        if(!formIds.isEmpty())
        {
            List<MktVendorWalletLine> vendorWalletList = vendorWalletLineDao.listCertainDayBeforeZx(formIds);
            Date now = Calendar.getInstance().getTime();
            for (MktVendorWalletLine vw : vendorWalletList)
            {
                if(map.containsKey(vw.getVendorKey()))
                {
                    now = map.get(vw.getVendorKey());
                }
                vendorWalletUpdManager.updWalletLineBalance(vw.getVendorKey(), vw.getPkey(), vw.getAmount(), now);
                vendorWalletUpdManager
                .updWalletAmount(vw.getVendorKey(), vw.getAmount(), true, now, vw.getPkey());
                if(map.containsKey(vw.getVendorKey()))
                {
                    // 增加一条提现记录
                    updWalletAmount(vw.getVendorKey(), vw.getAmount());
                }
            }
        }
        return res;
    }
    
    public void bugRepair(Integer filePkey)
    {
        List<String> formIds = new ArrayList<>();
        List<MktOrder> orderList = orderDao.select().eq("filePkey", filePkey).exec();
        orderList.forEach(e -> formIds.add(e.getCode()));
        List<MktVendorWalletLine> vendorWalletList = vendorWalletLineDao.listCertainDayBeforeZx(formIds);
        Date now = Calendar.getInstance().getTime();
        Map<Integer,Date> map = new HashMap<>();
        List<ZxWithdraw> zwList = zxWithdrawDao.listFilePkey(filePkey);
        for(ZxWithdraw zw : zwList)
        {
            if(ZxUserType.VENDOR.equals(zw.getType()))
            {
                map.put(Integer.valueOf(zw.getValue()), zw.getWithdrawTime());
            }
        }
        for (MktVendorWalletLine vw : vendorWalletList)
        {
            if(map.containsKey(vw.getVendorKey()))
            {
                now = map.get(vw.getVendorKey());
            }
            vendorWalletUpdManager.updWalletLineBalance(vw.getVendorKey(), vw.getPkey(), vw.getAmount(), now);
            vendorWalletUpdManager
            .updWalletAmount(vw.getVendorKey(), vw.getAmount(), true, now, vw.getPkey());
            if(map.containsKey(vw.getVendorKey()))
            {
                // 增加一条提现记录
                updWalletAmount(vw.getVendorKey(), vw.getAmount());
            }
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
        line.setOrderTime(new Date());
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
    
    public Boolean fundDetailsWithdraw(BigDecimal amt)
    {
        Boolean res = false;
        ZxUserType zut = ZxUserType.SYSTEM;
        String zxUserId = "";
        if(CurrentSession.marketPkey().startsWith("system_"))
        {
            res = runWithdraw(Constants.MCHNT_ID, amt, "01");
            zxUserId = Constants.MCHNT_ID;
        }
        else
        {
            ZxUserInfo zxUserInfo = zxUserInfoDao.get(ZxUserType.MARKET, CurrentSession.marketPkey());
            if(zxUserInfo != null && StringUtils.isNotBlank(zxUserInfo.getZxUserId()))
            {
                res = runWithdraw(zxUserInfo.getZxUserId(), amt, "00");
                zxUserId = zxUserInfo.getZxUserId();
            }
            zut = ZxUserType.MARKET;
        }
        if(Boolean.TRUE.equals(res))
        {
            // 减去余额
            ZxUserInfo byZxUserId = zxUserInfoDao.byZxUserId(zxUserId);
            if(byZxUserId != null)
            {
                BigDecimal comms = byZxUserId.getComms();
                if(comms == null)
                    comms = BigDecimal.ZERO;
                comms = comms.subtract(amt);
                byZxUserId.setComms(comms);
                zxUserInfoDao.update(byZxUserId);
                // 增加提现记录
                ZxWithdraw zw = new ZxWithdraw(zut, zxUserId, CurrentSession.marketPkey(), CurrentSession.ascriptionPkey());
                zw.setComms(amt);
                zw.setBalance(comms);
                zw.setStatus(ZxWithdrawStatus.MAKE_PAYMENT);
                zw.setWithdrawTime(Calendar.getInstance().getTime());
                zxWithdrawDao.add(zw);
            }
        }
        return res;
    }
    
    // 提现
    public Boolean runWithdraw(String zxUserId, BigDecimal withAmt, String withType)
    {
        try
        {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000014Request request = new T21000014Request();
            request.setTRANS_CODE("21000014");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID(Constants.MCHNT_ID);
            request.setUSER_ID(zxUserId);
            // 00-用户提现  01-平台提现
            request.setWITH_TYPE(withType);
            // 提现流水号 平台商户端
            request.setBUSS_ID(System.currentTimeMillis() + "");
            request.setTRANS_DT(DateUtil.formatDate(new Date(), "yyyyMMdd"));
            request.setTRANS_TM(DateUtil.formatDate(new Date(), "HHmmss"));
            request.setFEE_TYPE("1");
            request.setWITH_AMT(withAmt);
            
            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            String resStr = HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "21000014");
            //把xml为转换为实体对象
            T21000014Response resData = XstreamUtils.toBean(resStr, T21000014Response.class);
            System.out.println("resData: " + JsonUtil.toString(resData, true));
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc = SignUtil.verifySign(BaseSendMethod.sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            return "00000".equals(resData.getDATA().getRSP_CODE());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public Integer allocation(ZxUserInfo info, BigDecimal amt)
    {
        Calendar cal = Calendar.getInstance();
        Boolean flag = false;
        String zxUserId = info.getZxUserId();
        if(ZxUserType.SYSTEM.equals(info.getType()))
        {
            ZxUserInfo zui = zxUserInfoDao.whateverInfo(info.getAscription());
            zxUserId = zui.getZxUserId();
            flag = true;
        }
        StringBuilder sb = new StringBuilder();
        // 从 担保登记簿 划钱到 用户账户 的文件
        tjZxFileManager.assembleFileContentGuaranteeArriveUser(sb, zxUserId, 97 + DateUtil.formatDate(cal.getTime(), "yyyyMMddHHmmss"), amt, cal.getTime(), flag);
//        StringBuilder sb = runGuaranteeArriveUser(amt, info.getZxUserId(), 97 + DateUtil.formatDate(cal.getTime(), "yyyyMMddHHmmss"), cal.getTime());
        ZxFileRecord fileRecord = new ZxFileRecord();
        fileRecord.setStatus(ZxFileStatus.UPLOAD_SYCCESS);
        fileRecord.setType(ZxFileType.ALLOCATION);
        fileRecord.setUploadDate(Calendar.getInstance().getTime());
        fileRecord.setAscription(info.getAscription());
        ZxFileRecord add = zxFileRecordDao.add(fileRecord);
        tjZxFileManager.addFile(sb, DateUtil.formatDate(cal.getTime(), "yyyyMMdd"), "1", add);
        return add.getPkey();
    }
    
    // 从 担保登记簿 划钱到 用户账户 的文件
//    public StringBuilder runGuaranteeArriveUser(BigDecimal amt, String zxUserid, String transactionId, Date date)
//    {
//        StringBuilder sb = new StringBuilder();
//        // 传文件接口,生成数据
//        tjZxFileManager.assembleFileContentGuaranteeArriveUser(sb, zxUserid, transactionId, amt, date);
//        return sb;
//    }
   
    // 用户注册
    public void zxRegisterUser(ZxUserInfo info)
    {
        try
        {
            T21000001Request request = new T21000001Request();
            request.setTRANS_CODE("21000001");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID(Constants.MCHNT_ID);//平台商户编号              
            request.setMCHNT_USER_ID(info.getPkey().toString());//平台商户自己记录的用户编号

            request.setUSER_TYPE(info.getUserType());
            request.setUSER_NM(info.getUserNm()); // 用户姓名
            request.setUSER_ROLE(Constants.USER_ROLE); // 用户角色
            request.setSIGN_TYPE("00");//签约类型  00:自主注册 01:迁移注册
            request.setUSER_PHONE(info.getUserPhone());
            request.setUSER_ID_TYPE(info.getUserIdType());
            request.setUSER_ID_NO(info.getUserIdNo());
            
            request.setCORP_NM(info.getCorpNm());
            request.setCORP_ID_NO(info.getCorpIdNo());
            request.setCORP_ID_TYPE(info.getCorpIdType());
            
            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            String resStr = HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "21000001");
            //把xml为转换为实体对象
            T21000001Response resData = XstreamUtils.toBean(resStr, T21000001Response.class);
            System.out.println("resData: " + JsonUtil.toString(resData, true));
            
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc =
                SignUtil.verifySign(BaseSendMethod.sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            if (!isSucc)
                throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "中信注册用户验签失败");
            if (!Constants.RSP_CODE.equals(resData.getDATA().getRSP_CODE()))
                throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, resData.getDATA().getRSP_MSG());
            info.setZxRegisterTime(new Date());
            info.setZxUserId(resData.getDATA().getUSER_ID());
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            log.error("创建中信注册用户失败，{}", JsonUtil.toString(info), e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "中信创建用户失败");
        }
    }

    // 用户注册
    public void zxChangeUser(ZxUserInfo info)
    {
        try
        {
            T21000003Request request = new T21000003Request();
            request.setTRANS_CODE("21000003");
            Random r = new Random();
            String reqSsn = Constants.MCHNT_ID + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
                    + String.valueOf(r.nextLong()).substring(1, 8 + 1);
            request.setREQ_SSN(reqSsn);
            request.setMCHNT_ID(Constants.MCHNT_ID);//平台商户编号

            request.setUSER_ID(info.getZxUserId());// 用户编号
            request.setUSER_NM(info.getUserNm());//用户变更姓名
            request.setUSER_TYPE(info.getUserType());
            request.setUSER_CARD_TP(info.getUserIdType());//用户证件类型  03-统一社会信用代码
            request.setUSER_CARD_NO(info.getUserIdNo());//用户证件号码
            request.setUSER_PHONE(info.getUserPhone());//用户手机号
            request.setUSER_ROLE(Constants.USER_ROLE);//用户角色
            request.setCORP_NM(info.getCorpNm());//企业法人姓名
            request.setCORP_ID_TYPE_NEW(info.getCorpIdType());//企业法人证件类型
            request.setCORP_ID_NUM_NEW(info.getCorpIdNo());//企业法人身份证号码

            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());

            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();

            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());

            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            String resStr = HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "21000003");
            //把xml为转换为实体对象
            T21000003Response resData = XstreamUtils.toBean(resStr, T21000003Response.class);

            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc = SignUtil.verifySign(BaseSendMethod.sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc ? "验签成功！" : "验签失败，请检查签名！"));
            if (!isSucc)
                throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "中信修改用户验签失败");
            if (!"00000".equals(resData.getDATA().getRSP_CODE()))
                throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, resData.getDATA().getRSP_MSG());
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            log.error("创建中信修改用户失败，{}", JsonUtil.toString(info));
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "中信创建用户失败");
        }
    }
    
    /**
     * <查询用户及平台商户 余额数据>
     * @param userId
     * @param registerAttr 
     * 00-公共计息收费登记薄
     * 12-自有资金登记薄
     * 13-担保登记薄
     * 17-待结算手续费登记簿
     * 14-子商户/用户登记薄
     * TA-交易资金账户
     * @return
     */
    public String t2206(String userId, String registerAttr)
    {
        try
        {
            T22000006Request request = new T22000006Request();
            request.setTRANS_CODE("22000006");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID(Constants.MCHNT_ID);
            request.setUSER_ID(userId);
            request.setREGISTER_ATTR(registerAttr);
            
            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            
            String resStr = HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "22000006");
            //把xml为转换为实体对象
            T22000006Response resData = XstreamUtils.toBean(resStr, T22000006Response.class);
            System.out.println("resData: " + JsonUtil.toString(resData, true));
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc =
                SignUtil.verifySign(BaseSendMethod.sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            return resData.getDATA().getAMOUNT();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
    
    public String t2206New(String userId, String registerAttr)
    {
        try
        {
            T22000006Request request = new T22000006Request();
            request.setTRANS_CODE("22000006");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID("J01097900000000");
            request.setUSER_ID(userId);
            request.setREGISTER_ATTR(registerAttr);
            
            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            
            String resStr = HttpsPost.post(Constants.httpsUrl, restr, "J01097900000000", "22000006");
            //把xml为转换为实体对象
            T22000006Response resData = XstreamUtils.toBean(resStr, T22000006Response.class);
            System.out.println("resData: " + JsonUtil.toString(resData, true));
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc =
                SignUtil.verifySign(BaseSendMethod.sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            return resData.getDATA().getAMOUNT();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * < 绑卡/解绑 >
     * < 个人客户 我行测试账户对应联行号：302100011000；他行测试账户对应联行号：309391000011 >
     * < 对公客户 我行测试账户对应联行号：302100011000；他行测试账户对应联行号：309391000011 >
     */
    public void zxBindCard(ZxUserInfo info, boolean isBind)
    {
        try
        {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000024Request request = new T21000024Request();
            request.setTRANS_CODE("21000024");
            Random r = new Random();
            String reqSsn = Constants.MCHNT_ID + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
                + String.valueOf(r.nextLong()).substring(1, 8 + 1);
            request.setREQ_SSN(reqSsn);
            request.setMCHNT_ID(Constants.MCHNT_ID);//平台商户编号
            
            request.setUSER_ID(info.getZxUserId());
            request.setOP_TYPE(isBind ? "1" : "2");//1-绑定 2-解绑
            request.setACCT_NM(info.getAcctNm());
            request.setUSER_ID_TYPE(info.getBankCardType());
            request.setBANK_CARD_NO(info.getBankCardNo());
            request.setPAN_NUM(info.getPanNum());
            request.setPAN(info.getPan());
            request.setBANK_PHONE(info.getBankPhone());
            request.setACCT_TYPE(info.getAcctType());
            if (StringUtil.isNotBlank(info.getAuthProtocolVersion()))
                request.setAUTH_PROTOCOL_VERSION(info.getAuthProtocolVersion());
            if (StringUtil.isNotBlank(info.getAuthProtocolNo()))
                request.setAUTH_PROTOCOL_NO(info.getAuthProtocolNo());
            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            String resStr = HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "21000024");
            //把xml为转换为实体对象
            T21000024Response resData = XstreamUtils.toBean(resStr, T21000024Response.class);
            System.out.println("resData: " + JsonUtil.toString(resData, true));
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc =
                SignUtil.verifySign(BaseSendMethod.sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            if (!isSucc)
                throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "中信" + (isBind ? "绑定" : "解绑") + "银行卡验签失败");
            if (!"00000".equals(resData.getDATA().getRSP_CODE()))
                throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, resData.getDATA().getRSP_MSG());
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            log.error("创建中信修改用户失败，{}", JsonUtil.toString(info));
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "中信" + (isBind ? "绑定" : "解绑") + "银行卡失败");
        }
    }
    
    // 登记簿交易明细查询 
    public String t21000029(String userId, String registerAttr, String date, String transType)
    {
        try
        {
            T21000029Request request = new T21000029Request();
            request.setTRANS_CODE("21000029");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID(Constants.MCHNT_ID);
            request.setUSER_ID(userId);
            request.setREGISTER_ATTR(registerAttr);
            request.setPAGE("1");
            request.setTRANS_DATE(date);
            request.setTRANS_TYPE(transType);
            
            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            
            HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "21000029");
            //把xml为转换为实体对象
            //            T22000006Response resData = XstreamUtils.toBean(resStr, T22000006Response.class);
            //            
            //            //-------- 验签 --------------
            //            //获取签名信息
            //            String sigStr = resData.getDATA().getSIGN_INFO();
            //            //验签是否成功
            //            boolean isSucc = SignUtil.verifySign(BaseSendMethod.sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            //            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            //            return resData.getDATA().getAMOUNT();
            return null;
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
    
    // 文件处理状态查询
    public String t21000032(String fileName)
    {
        try
        {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000032Request request = new T21000032Request();
            request.setTRANS_CODE("21000032");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID(Constants.MCHNT_ID);
            request.setFILE_NAME(fileName + ".ZIP");
            
            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            
            String resStr = HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "21000032");
            //把xml为转换为实体对象
            T21000032Response resData = XstreamUtils.toBean(resStr, T21000032Response.class);
            
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc = SignUtil.verifySign(BaseSendMethod.sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            return resData.getDATA().getFILE_ST();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    // 线下转账 划到 担保账户
    public String t21000047(BigDecimal amount, String userNm, String DEAL_TYPE, 
        String USER_C_ID, String TRANS_DT, String TRANS_TM, String FUND_TP)
    {
        if(StringUtils.isBlank(userNm))
            userNm = "天津市滨海新区国成市场管理有限公司";
        if(StringUtils.isBlank(DEAL_TYPE))
            DEAL_TYPE = "03";
        if(StringUtils.isBlank(USER_C_ID))
            USER_C_ID = Constants.MCHNT_ID;
        if(StringUtils.isBlank(FUND_TP))
            FUND_TP = Constants.FUNDS_TYPE;
        Date time = Calendar.getInstance().getTime();
        if(StringUtils.isBlank(TRANS_DT))
            TRANS_DT = DateUtil.formatDate(time, "yyyyMMdd");
        if(StringUtils.isBlank(TRANS_TM))
            TRANS_TM = DateUtil.formatDate(time, "HHmmss");
        
    	try
    	{
    		T21000047Request request = new T21000047Request();
    		request.setTRANS_CODE("21000047");
    		request.setREQ_SSN(getReqSsn());
    		request.setMCHNT_ID(Constants.MCHNT_ID);
    		request.setDEAL_TYPE(DEAL_TYPE);
    		request.setUSER_C_ID(USER_C_ID);
    		request.setUSER_C_NM(userNm);
    		request.setBUSS_ID(getReqSsn());
    		request.setTRANS_DT(TRANS_DT);
    		request.setTRANS_TM(TRANS_TM);
    		request.setAMOUNT(amount);
    		request.setFUND_TP(FUND_TP);
    		
    		
    		//实体对象转换为XML
    		String restr = XstreamUtils.toXml(request, request.getClass());
    		
    		SAXReader reader = new SAXReader();
    		Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
    		Element root = doc.getRootElement();
    		
    		//--------加签名------
    		String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
    		request.setSIGN_INFO(sign);
    		restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
    		
    		//---------- 发送请求数据 ------
    		//初始化https请求参数
    		HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
    		//发送请求获得响应数据
    		String resStr = HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "21000047");
    		//把xml为转换为实体对象
    		T21000047Response resData = XstreamUtils.toBean(resStr, T21000047Response.class);
    		
    		//-------- 验签 --------------
    		//获取签名信息
    		String sigStr = resData.getDATA().getSIGN_INFO();
    		//验签是否成功
    		boolean isSucc = SignUtil.verifySign(BaseSendMethod.sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
    		System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
    		return resData.getDATA().getRSP_CODE();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return null;
    }
    
    // 实时预付 实现平台商户担保交易登记簿和用户登记簿之间的资金划转  transType: 00-预付交易支付  01-预付交易撤销 02-预付交易完成 03-预付完成撤销
    public String t22000007(Integer pkey, String amt, String transType, String bussId)
    {
        ZxUserInfo info = zxUserInfoDao.get(pkey);
        try
        {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T22000007Request request = new T22000007Request();
            request.setTRANS_CODE("22000007");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID(Constants.MCHNT_ID);
            request.setTRANS_TYPE(transType);
            request.setUSER_ID(info.getZxUserId());
            request.setUSER_NM(info.getName());
            Calendar cal = Calendar.getInstance();
            String formatDate = DateUtil.formatDate(cal.getTime(), "yyyyMMddHHmmss");
            request.setBUSS_ID(bussId);
            request.setBUSS_SUB_ID(bussId);
            request.setTRANS_DT(DateUtil.formatDate(cal.getTime(), "yyyyMMdd"));
            cal.add(Calendar.DAY_OF_YEAR, -1);
            request.setORI_USER_TRANS_DT(DateUtil.formatDate(cal.getTime(), "yyyyMMdd"));
            request.setORI_BUSS_ID(formatDate + "003");
            request.setORI_BUSS_SUB_ID(formatDate + "004");
            request.setTRANS_TM(formatDate.substring(8,14));
            request.setAMOUNT(amt);
            request.setFUND_TP(Constants.FUNDS_TYPE);
            
            
            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            
            String resStr = HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "22000007");
            System.out.println("resStr: " + resStr);
            return "";
            //把xml为转换为实体对象
//            T21000032Response resData = XstreamUtils.toBean(resStr, T21000032Response.class);
//            
//            //-------- 验签 --------------
//            //获取签名信息
//            String sigStr = resData.getDATA().getSIGN_INFO();
//            //验签是否成功
//            boolean isSucc = SignUtil.verifySign(BaseSendMethod.sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
//            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
//            return resData.getDATA().getFILE_ST();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    // 登记簿预付  transType: 00-预付交易支付  01-预付交易撤销 02-预付交易完成 03-预付完成撤销
    public String t21000028(Integer pkey, String amt, String transType, String bussId, String dt, String tm)
    {
        ZxUserInfo info = zxUserInfoDao.get(pkey);
        try
        {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000028Request request = new T21000028Request();
            request.setTRANS_CODE("21000028");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID(Constants.MCHNT_ID);
            request.setTRANS_TYPE(transType);
            request.setUSER_ID(info.getZxUserId());
            request.setUSER_NM(info.getUserNm());
            request.setBUSS_ID(bussId);
            request.setBUSS_SUB_ID(bussId);
            request.setTRANS_DT(dt);
            request.setTRANS_TM(tm);
            request.setAMOUNT(amt);
            request.setFUND_TP(Constants.FUNDS_TYPE);
            
            
            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            
            String resStr = HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "22000007");
            System.out.println("resStr: " + resStr);
            return "";
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    Random r = new Random();
    
    private String getReqSsn()
    {
        return Constants.MCHNT_ID + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
            + String.valueOf(r.nextLong()).substring(1, 8 + 1);
    }

    public void withdrawAllVendor()
    {
        List<ZxUserInfo> zxUsers = zxUserInfoDao.select()
            .eq(ZxUserInfo.F.ascription, CurrentSession.ascriptionPkey())
            .eq(ZxUserInfo.F.type, ZxUserType.VENDOR)
            .isNotNull(ZxUserInfo.F.zxUserId)
            .eq(ZxUserInfo.F.delFlag, false)
            .eq(ZxUserInfo.F.cardStatus, ZxCardStatus.BINDING_SUCCESS)
            .exec();
        for (ZxUserInfo zxUser : zxUsers)
        {
            String amtStr = t2206(zxUser.getZxUserId(), "14");
            if(StringUtil.isBlank(amtStr)) log.error("[天津提现所有商户余额] 中信商户（[{}]{}）查询余额失败", zxUser.getValue(), zxUser.getZxUserId());
            BigDecimal amt = new BigDecimal(amtStr);
            if (amt.compareTo(BigDecimal.ZERO) > 0)
            {
                Boolean res = runWithdraw(zxUser.getZxUserId(), amt, "00");
                if (Boolean.TRUE.equals(res))
                {
                    log.info("[天津提现所有商户余额] 中信商户（[{}]{}）提现余额成功，提现金额：{}", zxUser.getValue(), zxUser.getZxUserId(), amt);
                }
                else
                {
                    log.info("[天津提现所有商户余额] 中信商户（[{}]{}）提现余额失败，提现金额：{}", zxUser.getValue(), zxUser.getZxUserId(), amt);
                }
            }
            else
            {
                log.info("[天津提现所有商户余额] 中信商户（[{}]{}）查询余额为0，不执行提现", zxUser.getValue(), zxUser.getZxUserId());
            }
        }
    }
    
    public void zxVendorOrderZero()
    {
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -3);
        String time = DateUtil.formatDate(cal.getTime());
        List<MktVendorOrder> list = vendorOrderDao.listZxCertainDayBeforeAmtZero(time);
        for (MktVendorOrder vo : list)
        {
            vo.setStartDate(now);
            vo.setStatus(SettlementType.SUCCESS);
        }
        vendorOrderDao.updateAll(list);
    }
    
    // 重新生成文件
    public void regenerateFile(Integer pkey, String nextXuhao)
    {
        List<MktOrder> list = orderDao.select()
            .isNotNull("filePkey")
            .eq("filePkey", pkey).exec();
        List<MktMemberActivity> maList = memberActivityDao.select()
        .isNotNull("filePkey")
        .eq("filePkey", pkey).exec();
        if (maList != null && !maList.isEmpty())
        {
            for (MktMemberActivity ma : maList)
            {
                MktOrder o = new MktOrder();
                o.setPkey(-3);
                o.setCode(ma.getCode());
                o.setAmtn(ma.getAmt());
                o.setCreatedTime(ma.getCreatedTime());
                o.setFarmer(ma.getFarmer());
                list.add(o);
                ma.setSettlementType(SettlementType.DOING);
            }
            memberActivityDao.updateAll(maList);
        }
        if (!list.isEmpty())
        {
            regenerateFileSettle(pkey, list, nextXuhao);
        }
    }
    
    public void regenerateFileSettle(Integer pkey, List<MktOrder> list, String nextXuhao)
    {
        Integer ascription = 13;
        Date createdTime = list.get(0).getCreatedTime();
        String format = DateUtils.format(createdTime);
        format = format.substring(0, 10);
        String day = format.replace("-", "");
        StringBuilder sb = new StringBuilder();
        // 所有订单主键
        List<Integer> keyList = CollectionUtil.keyList(list);
        if (keyList == null || keyList.isEmpty()) return;
        List<String> formIds = new ArrayList<>();
        list.forEach(e -> formIds.add(e.getCode()));
        // 所有商户订单
        List<MktVendorOrder> voList = vendorOrderDao.select().isNotNull("filePkey")
            .eq("filePkey", pkey).exec();
        Map<Integer, List<MktVendorOrder>> map = new HashMap<>();
        Map<Integer, List<MktVendorOrder>> zeroMap = new HashMap<>();
        voList.forEach(e -> {
            if (e.getAmt().compareTo(BigDecimal.ZERO) == 0)
            {
                if (!zeroMap.containsKey(e.getOrderPkey()))
                    zeroMap.put(e.getOrderPkey(), new ArrayList<>());
                zeroMap.get(e.getOrderPkey()).add(e);
            }
            else
            {
                if (!map.containsKey(e.getOrderPkey()))
                    map.put(e.getOrderPkey(), new ArrayList<>());
                map.get(e.getOrderPkey()).add(e);
            }
            e.setStatus(SettlementType.DOING);
        });
        List<MktVendorWalletLine> vwlList = vendorWalletLineDao.select()
//        .eq("status", SettlementType.NOT_START)
        .in("formId", formIds)
        .exec();
        vwlList.forEach(e ->
        {
            e.setStatus(SettlementType.DOING);
        });
        
        Map<String,ZxWithdraw> wMap = new HashMap<>();
        //  所有支付订单流水号
        Map<String, String> tranMap = thirdPayLineDao.tranMap(day);
        
        // 查询市场信息,是否是民营企业
        List<SysFarmerConfig> fcList = sysFarmerConfigDao.select().eq("ascription", ascription).exec();
        Map<String, SysFarmerConfig> fcMap = new HashMap<>();
        fcList.forEach(e -> fcMap.put(e.getPkey(), e));
        int num = 0;
        ZxUserInfo zui = zxUserInfoDao.whateverInfo(ascription);
        Map<String, ZxUserInfo> mapZxInfo = zxUserInfoDao.mapZxUserInfo(ascription);
//        Map<String, String> mapZx = zxUserInfoDao.mapZxUserId(ascription);
        
        // 平台提现的金额  
        ZxWithdraw sysW = new ZxWithdraw(ZxUserType.SYSTEM, Constants.MCHNT_ID, "system_" + ascription, ascription);
        if(mapZxInfo.containsKey(ZxUserType.SYSTEM + "_" + "system_" + ascription))
        {
            ZxUserInfo zxUserInfo = mapZxInfo.get(ZxUserType.SYSTEM + "_" + "system_" + ascription);
            if(!Boolean.TRUE.equals(zxUserInfo.getMarketAuto()))
                sysW.setStatus(ZxWithdrawStatus.MANUAL_MAKE_PAYMENT);
        }
        wMap.put(sysW.getZxUserId(), sysW);
        
        // 循环订单 进行清分
        for (MktOrder o : list)
        {
            o.setSettlementType(SettlementType.DOING);
            String transactionId = tranMap.get(o.getCode().substring(0, 14));
            if (StringUtils.isBlank(transactionId))
                transactionId = o.getCode() + DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
            SysFarmerConfig config = fcMap.get(o.getFarmer());
            sysW.setBillDate(DateUtil.formatDate(o.getCreatedTime(), "yyyy-MM-dd"));
            // 需要清分给商户
            if (map.containsKey(o.getPkey()))
            {
                List<MktVendorOrder> mapVoList = map.get(o.getPkey());
                MktVendorOrder vo = mapVoList.get(0);
                // 手续费承担
                CommissionType commissionType = vo.getCommissionType();
                // 是否是民营企业
                MktVendorOrder mvo = new MktVendorOrder();
                if (Boolean.TRUE.equals(config.getIsEnterprise()))
                {
                    if (CommissionType.BLOC.equals(commissionType)
                        && (vo.getCommissions() == null || vo.getCommissions().compareTo(BigDecimal.ZERO) == 0))
                        throw TofocusException.of(LejiaErrCode.ZX_PAYCOMM_ERROR);
                    // 增加民营企业应该获得金额
                    mvo.setPkey(-5);
                    mvo.setCommissions(BigDecimal.ZERO);
                    mvo.setPayComm(BigDecimal.ZERO);
                    // 如果是民营市场 运费全部划给民营市场 
                    BigDecimal postage = o.getPostage();
                    if (postage == null) postage = BigDecimal.ZERO;
                    mvo.setAmt(postage);
                    mvo.setPostage(BigDecimal.ZERO);
                    mvo.setCommissionType(commissionType);
                    for (MktVendorOrder vodto : mapVoList)
                    {
                        if(vodto.getMarketCommissions() != null)
                            mvo.setAmt(mvo.getAmt().add(vodto.getMarketCommissions()));
                        else
                            mvo.setAmt(mvo.getAmt().add(vodto.getCommissions()));
                        if (CommissionType.MARKET.equals(commissionType))
                        {
                            mvo.setAmt(mvo.getAmt().subtract(vodto.getPayComm()));
                        }
                    }
                    mapVoList.add(mvo);
                }
                num = num + mapVoList.size();
                // 商户业务订单号
                o.getCode();
                
                // 交易日期 交易时间
                DateUtil.formatDate(o.getCreatedTime(), "yyyyMMddHHmmss");
                BigDecimal s = BigDecimal.ZERO;
                for (MktVendorOrder dto : mapVoList)
                {
                    // 平台优惠金额
                    BigDecimal discountAmt = dto.getDiscountAmt();
                    if (discountAmt == null) discountAmt = BigDecimal.ZERO;
                    if (dto.getDiscountRefundAmt() != null)
                        discountAmt = discountAmt.subtract(dto.getDiscountRefundAmt());
                    BigDecimal postage = dto.getPostage();
                    if (postage == null) postage = BigDecimal.ZERO;
                    // 平台分成金额
                    BigDecimal commissions = dto.getCommissions();
                    if (Boolean.TRUE.equals(config.getIsEnterprise()))
                    {
                        commissions = dto.getSysCommissions();
                        if (dto.getSysCommissions() == null) commissions = BigDecimal.ZERO;
                        postage = BigDecimal.ZERO;
                    }
                    // 市场佣金
                    BigDecimal sysCommissions = new BigDecimal(commissions.toPlainString());
                    // 原始金额（加上佣金，没处理手续费）
                    BigDecimal oriAmt = dto.getAmt().add(commissions);
                    // 实际金额
                    BigDecimal amt = dto.getAmt();
                    if (CommissionType.BLOC.equals(commissionType))
                    {
                        // 手续费直接扣除
                        sysCommissions = sysCommissions.subtract(dto.getPayComm());
                        oriAmt = oriAmt.subtract(dto.getPayComm());
                    }
                    else if (CommissionType.MERCHANT.equals(commissionType))
                    {
                        // 商户承担手续费 从结算给商户的金额里扣除
                        oriAmt = oriAmt.subtract(dto.getPayComm());
                        amt = amt.subtract(dto.getPayComm());
                        if(oriAmt.compareTo(BigDecimal.ZERO) < 0)
                        {
                            s = oriAmt.abs();
                            if (!Boolean.TRUE.equals(config.getIsEnterprise()))
                                postage = postage.subtract(s);
                            oriAmt = BigDecimal.ZERO;
                        }
                        if(dto.getPkey() == -5)
                           oriAmt = dto.getAmt().subtract(s);
                    }
                    // 市场承担手续费 已经在上方处理
                    String zxUserId = "J01097900000051";
                    ZxUserType zut = ZxUserType.VENDOR;
                    String value = dto.getVendor() + "";
                    ZxWithdrawStatus zws = ZxWithdrawStatus.NOT_MAKE_PAYMENT;
                    if (dto.getPkey() == -5)
                    {
                        ZxUserInfo zxUserInfo = mapZxInfo.get(ZxUserType.MARKET + "_" + config.getPkey());
                        value = config.getPkey();
                        if(zxUserInfo != null)
                        {
                            zxUserId = zxUserInfo.getZxUserId();
                            if(!Boolean.TRUE.equals(zxUserInfo.getMarketAuto()))
                                zws = ZxWithdrawStatus.MANUAL_MAKE_PAYMENT;
                        }
                        else
                            throw TofocusException.of(LejiaErrCode.ZX_USERID_ERROR);
                        zut = ZxUserType.MARKET;
                    }
                    else
                    {
                        ZxUserInfo zxUserInfo = mapZxInfo.get(ZxUserType.VENDOR + "_" + dto.getVendor());
                        if(zxUserInfo != null)
                        {
                            zxUserId = zxUserInfo.getZxUserId();
                        }
                        else
                            throw TofocusException.of(LejiaErrCode.ZX_USERID_ERROR);
                        if(!Boolean.TRUE.equals(config.getIsEnterprise()))
                        {
                            zxUserInfo = zxUserInfoDao.get(ZxUserType.SELF_MARKET, dto.getFarmer());
                            if(zxUserInfo != null && !Boolean.TRUE.equals(zxUserInfo.getVendorAuto()))
                                zws = ZxWithdrawStatus.MANUAL_MAKE_PAYMENT;
                        }
                        else
                        {
                            zxUserInfo = mapZxInfo.get(ZxUserType.MARKET + "_" + dto.getFarmer());
                            if(zxUserInfo != null && !Boolean.TRUE.equals(zxUserInfo.getVendorAuto()))
                                zws = ZxWithdrawStatus.MANUAL_MAKE_PAYMENT;
                        }
                    }
                    sysW.setComms(sysW.getComms().add(sysCommissions).add(postage));
                    if(!wMap.containsKey(zxUserId))
                    {
                        ZxWithdraw zw = new ZxWithdraw(zut, zxUserId, value, ascription);
                        zw.setBillDate(DateUtil.formatDate(o.getCreatedTime(), "yyyy-MM-dd"));
                        zw.setStatus(zws);
                        wMap.put(zxUserId, zw);
                    }
                    ZxWithdraw zw = wMap.get(zxUserId);
                    zw.setComms(zw.getComms().add(amt));
                    
                    if(oriAmt.compareTo(BigDecimal.ZERO) != 0
                        || sysCommissions.compareTo(BigDecimal.ZERO) != 0
                            || postage.compareTo(BigDecimal.ZERO) != 0)
                        tjZxFileManager.assembleFileContent(sb,
                            zxUserId,
                            o.getCode(),
                            transactionId,
                            dto.getPkey(),
                            oriAmt,
                            sysCommissions,
                            postage,
                            new Date());
                }
            }
            // 整单都不需要清分给商户 || 部分vendorOrder不需要清分给商户
            if (!map.containsKey(o.getPkey()) || zeroMap.containsKey(o.getPkey()))
            {
                // 不需要清分给商户 积分商城卖出去的或者市场做的卡券活动
                BigDecimal payCommissionRate = Constant.ZxConfig.TJ_COMMISSION_RATE;
                BigDecimal amtn = o.getAmtn();
                BigDecimal sysComm = BigDecimal.ZERO;
                if (o.getRefundAmt() != null) amtn = amtn.subtract(o.getRefundAmt());
                BigDecimal payCommission = amtn.multiply(payCommissionRate).setScale(2, RoundingMode.HALF_UP);
                amtn = amtn.subtract(payCommission);
                // 部分vendorOrder不需要清分给商户，修改amtn为剩余部分的金额总和
                if (zeroMap.containsKey(o.getPkey()))
                {
                    List<MktVendorOrder> zeroVendorOrders = zeroMap.get(o.getPkey());
                    amtn = BigDecimal.ZERO;
                    for (MktVendorOrder zvo : zeroVendorOrders)
                    {
                        amtn = amtn.add(zvo.getCommissions()).subtract(zvo.getPayComm());
                        if(zvo.getSysCommissions() != null)
                            sysComm = sysComm.add(zvo.getSysCommissions()); 
                    }
                }
                num += 1;
                // 区分是积分商城还是市场送出去的卡券
                if (o.getFarmer().startsWith(Constant.Operation) || !Boolean.TRUE.equals(config.getIsEnterprise()))
                {
                    // 直接全部划给集团方, 正常不需要调接口 所有的钱已经在担保登记簿
                    // 优惠券的垫付 走担保登记簿
                    // 直接传数据,用户为空 钱直接进入平台商户的自有登记簿
                    // 用户编号  这里随便传一个用户编号就可以
                    tjZxFileManager.assembleFileContent(sb,
                        zui.getZxUserId(),
                        o.getCode(),
                        transactionId,
                        0,
                        amtn,
                        amtn,
                        BigDecimal.ZERO,
                        new Date());
                    sysW.setComms(sysW.getComms().add(amtn));
                }
                else
                {
                    // J04059100000051(测试服用的 市场zxUserid)
                    String zxUserId = "J01097900000051";
                    ZxWithdrawStatus zws = ZxWithdrawStatus.NOT_MAKE_PAYMENT;
                    ZxUserInfo zxUserInfo = mapZxInfo.get(ZxUserType.MARKET + "_" + config.getPkey());
                    if(zxUserInfo != null)
                    {
                        zxUserId = zxUserInfo.getZxUserId();
                        if(!Boolean.TRUE.equals(zxUserInfo.getMarketAuto()))
                            zws = ZxWithdrawStatus.MANUAL_MAKE_PAYMENT;
                    }
                    else
                        throw TofocusException.of(LejiaErrCode.ZX_USERID_ERROR);
                    
                    // 传文件接口,生成数据
                    tjZxFileManager.assembleFileContent(sb,
                        zxUserId,
                        o.getCode(),
                        transactionId,
                        0,
                        amtn,
                        sysComm,
                        BigDecimal.ZERO,
                        new Date());
                    
                    if(!wMap.containsKey(zxUserId))
                    {
                        ZxWithdraw zw = new ZxWithdraw(ZxUserType.MARKET, zxUserId, config.getPkey(), ascription);
                        zw.setBillDate(DateUtil.formatDate(o.getCreatedTime(), "yyyy-MM-dd"));
                        zw.setStatus(zws);
                        wMap.put(zxUserId, zw);
                    }
                    ZxWithdraw zw = wMap.get(zxUserId);
                    zw.setComms(zw.getComms().add(amtn));
                }
            }
        }
        ZxFileRecord fileRecord = zxFileRecordDao.get(pkey);
        fileRecord.setStatus(ZxFileStatus.UPLOAD_SYCCESS);
        fileRecord.setType(ZxFileType.QING_FEN);
        fileRecord.setUploadDate(Calendar.getInstance().getTime());
        fileRecord.setAscription(ascription);
        ZxFileRecord add = zxFileRecordDao.update(fileRecord);
        tjZxFileManager.addFileNoSend(sb, day, num + "", add, nextXuhao);
        
        // 修改order vendor_order 结算状态
        vendorOrderDao.updateAll(voList);
        vendorWalletLineDao.updateAll(vwlList);
        orderDao.updateAll(list);
        // 新增提现记录表
//        if(sysW.getComms().compareTo(BigDecimal.ZERO) == 0)
//            wMap.remove(sysW.getZxUserId());
//        List<ZxWithdraw> zwList = new ArrayList<>(wMap.values());
//        zwList.forEach(e -> e.setFilePkey(add.getPkey()));
//        zxWithdrawDao.addAll(zwList);
    }
}
