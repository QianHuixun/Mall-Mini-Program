package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.app.v1.sys.ChinaUmsPayApiImpl;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.v2.ZxCardStatus;
import cn.tofocus.lejia.dao.zx.ZxUserInfoDao;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import cn.tofocus.common.notify.SMSNotify;
import cn.tofocus.common.notify.config.SmsConfig;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.app.vendor.AppVendorBillOnList;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletBillOnInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOnInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOrderOnInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOrderOnList;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletVendorOrderInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.VendorOrderWalletOnPage;
import cn.tofocus.lejia.bean.dto.app.vendor.VendorWalletBankInfo;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPackingCharge;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWalletLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWithdrawal;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import cn.tofocus.lejia.bean.enums.vendor.WithdrawalStatus;
import cn.tofocus.lejia.cache.MobileWalletCodeMap;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderPackingChargeDao;
import cn.tofocus.lejia.dao.vendor.MktVendorPackingChargeDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWalletLineDao;
import cn.tofocus.lejia.dao.vendor.MktVendorWithdrawalDao;
import cn.tofocus.lejia.domain.TjZxManager;
import cn.tofocus.lejia.domain.vendor.VendorWalletUpdManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class AppVendorWalletV3Manager
{
    @Autowired
    private SmsConfig smsConfig;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MobileWalletCodeMap mobileWalletCodeMap;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktVendorWalletLineDao vendorWalletLineDao;
    
    @Autowired
    private VendorWalletUpdManager vendorWalletUpdManager;
    
    @Autowired
    private MktVendorWithdrawalDao vendorWithdrawalDao;
    
    @Autowired
    private MktVendorPackingChargeDao vendorPackingChargeDao;

    @Autowired
    private ZxUserInfoDao zxUserInfoDao;
    
    @Autowired
    private TjZxManager tjZxManager;

    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;

    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public AppWalletBillOnInfo listBill(Integer day, String startDate, String endDate)
    {
        Calendar cal = Calendar.getInstance();
        //        cal.add(Calendar.DAY_OF_MONTH, -day);
        //        Date time = cal.getTime();
        //        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date monthTime = cal.getTime();
        log.info("小程序查询商户钱包明细时间,待结算时间: {}, 已结算时间: {}", null, DateUtil.formatDate(monthTime));
        AppWalletBillOnInfo res = new AppWalletBillOnInfo();
        Integer vendorKey = MobileSession.vendorPkey();
        List<AppVendorBillOnList> noSettlement = vendorWalletLineDao.listAppBill(vendorKey,
            Lists.newArrayList(SettlementType.NOT_START, SettlementType.DOING, SettlementType.FAIL),
            null,
            null,
            null);
        for (AppVendorBillOnList vb : noSettlement)
        {
            String orderTime = vb.getOrderTime();
            Date date = DateUtil.formatDateStr(orderTime, "yyyy-MM-dd");
            LocalDate localDate = DateUtil.date2LocalDate(date);
            LocalDate plusDays = localDate.plusDays(day);
            vb.setSettlementTime("预计" + formatDateStr(plusDays) + "结算");
        }
        // 根据时间排序
        Collections.sort(noSettlement, new Comparator<AppVendorBillOnList>()
        {
            @Override
            public int compare(AppVendorBillOnList o1, AppVendorBillOnList o2)
            {
                return o2.getSettlementTime().compareTo(o1.getSettlementTime());
            }
        });
        res.setNoSettlement(noSettlement);
        String strMonthTimt = null;
        if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate))
            strMonthTimt = DateUtil.formatDate(monthTime);
        if (StringUtils.isNotBlank(startDate))
        {
            startDate = startDate + "-01";
            endDate = startDate;
        }
        //        if(StringUtils.isNotBlank(endDate))
        //            endDate = endDate + "-01";
        List<AppVendorBillOnList> settlement = vendorWalletLineDao
            .listAppBill(vendorKey, Lists.newArrayList(SettlementType.SUCCESS), strMonthTimt, startDate, endDate);
        for (AppVendorBillOnList vb : settlement)
        {
            vb.setSettlementTime(formatDateStr(DateUtil.date2LocalDate(vb.getTime())) + "已结算");
        }
        // 根据时间排序
        settlement.sort((o1, o2) -> o2.getSettlementTime().compareTo(o1.getSettlementTime()));
        res.setSettlement(settlement);
        return res;
    }
    
    public AppWalletOrderOnInfo listOrder(String time, List<SettlementType> statuses)
    {
        AppWalletOrderOnInfo res = new AppWalletOrderOnInfo();
        Integer vendorKey = MobileSession.vendorPkey();
        List<AppWalletOrderOnList> list = vendorOrderDao.listAppWalletOrder(vendorKey, time, statuses);
        Map<Integer, AppWalletOrderOnList> map = new HashMap<>();
        BigDecimal packingCharge = BigDecimal.ZERO;
        BigDecimal totalOrderAmt = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (AppWalletOrderOnList awo : list)
        {
            if (awo.getPackingCharge() == null)
                awo.setPackingCharge(BigDecimal.ZERO);
            
            // 处理金额
            BigDecimal oAmt = awo.getAmt();
            if (awo.getCommissions() != null)
                oAmt = oAmt.add(awo.getCommissions());
            if (awo.getPackingCharge() != null)
                oAmt = oAmt.add(awo.getPackingCharge());
            awo.setOrderAmt(oAmt);
            BigDecimal amount = awo.getAmt();
            if (awo.getCommissionType() == CommissionType.MERCHANT)
                amount = amount.subtract(awo.getPayComm());
            awo.setAmount(amount);
            
            // 如果存在，加进去
            Integer orderPkey = awo.getOrderPkey();
            if (map.containsKey(orderPkey))
            {
                AppWalletOrderOnList aw = map.get(orderPkey);
                aw.plus(awo);
            }
            // 不存在，直接插入map
            else
            {
                map.put(orderPkey, awo);
            }
            
            packingCharge = packingCharge.add(awo.getPackingCharge());
            totalOrderAmt = totalOrderAmt.add(awo.getOrderAmt());
            totalAmount = totalAmount.add(awo.getAmount());
        }
        List<AppWalletOrderOnList> appWalletOrderOnList = new ArrayList<>(map.values());
        res.setPackingCharge(packingCharge);
        res.setOrderAmt(totalOrderAmt);
        res.setAmt(totalAmount);
        res.setIsPackingCharge(false);
        List<MktVendorPackingCharge> listByVendor = vendorPackingChargeDao.listByVendor(MobileSession.vendorPkey());
        if (!listByVendor.isEmpty())
            res.setIsPackingCharge(true);
        appWalletOrderOnList.sort((o1, o2) -> o2.getOrderTime().compareTo(o1.getOrderTime()));
        res.setAppWalletOrderOnList(appWalletOrderOnList);
        if (list.isEmpty())
            res.setSettlementTime("");
        else
        {
            Date startDate = list.get(0).getStartDate();
            if (startDate == null)
            {
                Date date = DateUtil.formatDateStr(time, "yyyy-MM-dd");
                LocalDate localDate = DateUtil.date2LocalDate(date);
                LocalDate plusDays = localDate.plusDays(3);
                res.setSettlementTime("预计" + formatDateStr(plusDays) + "结算");
            }
            else
            {
                res.setSettlementTime(formatDateStr(DateUtil.date2LocalDate(startDate)) + "已结算");
            }
        }
        return res;
    }
    
    private String formatDateStr(LocalDate localDate)
    {
        if (localDate == null)
            return "";
        return localDate.format(FORMATTER_DATE);
    }
    
    public AppWalletVendorOrderInfo getVendorOrderWallet(Integer pkey)
    {
        AppWalletVendorOrderInfo res = new AppWalletVendorOrderInfo();
        MktOrder mktOrder = orderDao.get(pkey);
        res.setCode(mktOrder.getCode());
        res.setSmallTicket(mktOrder.getSmallTicket());
        res.setPickupCode(mktOrder.getPickupCode());
        res.setDistributionType(mktOrder.getDistributionType());
        res.setOrderTime(mktOrder.getCreatedTime());
        res.setPstime(mktOrder.getPstime());
        res.setCommissionType(mktOrder.getCommissionType());
        List<MktVendorOrder> listOrder = vendorOrderDao.listOrderByVendor(pkey, MobileSession.vendorPkey());
        List<VendorOrderWalletOnPage> list = BeanUtil.beanListFrom(VendorOrderWalletOnPage.class, listOrder);
        BigDecimal orderAmt = BigDecimal.ZERO;
        BigDecimal commissions = BigDecimal.ZERO;
        BigDecimal payComm = BigDecimal.ZERO;
        BigDecimal packingCharge = BigDecimal.ZERO;
        for (VendorOrderWalletOnPage e : list)
        {
            orderAmt = orderAmt.add(e.getOrderAmt());
            if (e.getCommissions() != null)
                commissions = commissions.add(e.getCommissions());
            if (res.getCommissionType() == CommissionType.MERCHANT && e.getPayComm() != null)
                payComm = payComm.add(e.getPayComm());
            if (e.getPackingCharge() != null)
                packingCharge = packingCharge.add(e.getPackingCharge());
        }
        res.setAmt(orderAmt.subtract(commissions).subtract(payComm).subtract(packingCharge));
        res.setOrderAmt(orderAmt);
        res.setCommissions(commissions);
        res.setPayComm(payComm);
        res.setPackingCharge(packingCharge);
        res.setListOrder(list);
        res.setIsPackingCharge(false);
        List<MktVendorPackingCharge> listByVendor = vendorPackingChargeDao.listByVendor(MobileSession.vendorPkey());
        if(!listByVendor.isEmpty())
            res.setIsPackingCharge(true);
        return res;
    }
    
    public Boolean createCaptcha(String phone)
    {
        MktVendor vendor = vendorDao.selectOne().eq("mobile", phone).eq("idDel", false).exec();
        if (vendor == null)
            throw TofocusException.of(WsaleErrCode.NOT_VENDOR);
        if (qfAscription.equals(vendor.getAscription()))
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT, "不支持修改提现账户");
        String code = NumberUtils.createCheckCode();
        mobileWalletCodeMap.put(phone, code);
        log.info("商户银行信息保存验证码：{}", code);
        return new SMSNotify(smsConfig).sendCode(phone, code);
    }
    
    public VendorWalletBankInfo getBankOnInfo()
    {
        MktVendor mktVendor = vendorDao.get(MobileSession.vendorPkey());
        if (qfAscription.equals(mktVendor.getAscription()))
        {
            VendorWalletBankInfo res = new VendorWalletBankInfo();
            res.setMobile(mktVendor.getMobile());
            ZxUserInfo zxUserInfo = zxUserInfoDao
                .get(ZxUserType.VENDOR, mktVendor.getPkey().toString(), mktVendor.getAscription(), ZxUserInfo.class);
            if (zxUserInfo != null)
            {
                res.setBankcard(zxUserInfo.getPan());
                res.setBankname("中信银行");
                res.setBankuser(zxUserInfo.getAcctNm());
//                res.setBankBranchName(zxUserInfo.getPanNum());
                res.setAllowedUpd(false);
            }
            return res;
        }
        return BeanUtil.beanFrom(VendorWalletBankInfo.class, mktVendor);
    }
    
    public Boolean updBankOnInfo(VendorWalletBankInfo info)
    {
        String code = info.getCode();
        String ccode = mobileWalletCodeMap.get(info.getMobile());
        if (StringUtils.isBlank(code) || StringUtils.isBlank(ccode) || !ccode.equals(code))
            throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        MktVendor mktVendor = vendorDao.get(MobileSession.vendorPkey());
        if (qfAscription.equals(mktVendor.getAscription()))
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT, "不支持修改提现账户");
        mktVendor.setBankcard(info.getBankcard());
        mktVendor.setBankname(info.getBankname());
        mktVendor.setBankBranchName(info.getBankBranchName());
        mktVendor.setBankuser(info.getBankuser());
        vendorDao.update(mktVendor);
        mobileWalletCodeMap.remove(info.getMobile());
        return true;
    }

    public Boolean applyWithdrawal(BigDecimal amount)
    {
        Integer vendorKey = MobileSession.vendorPkey();
        AppWalletOnInfo aw = vendorWalletUpdManager.loadWalletAmount(vendorKey);
        if (aw.getWalletAmt().compareTo(amount) < 0)
            throw TofocusException.of(LejiaErrCode.VENDOR_WALLET_AMOUNT_ERROR, "提现金额大于商户可提现金额");
        MktVendor vendor = vendorDao.get(vendorKey);
        ZxUserInfo zxUser = null;
        if (qfAscription.equals(vendor.getAscription()))
        {
            zxUser = zxUserInfoDao.get(ZxUserType.VENDOR, vendorKey + "");
            if (zxUser == null || zxUser.getZxUserId() == null
                || ZxCardStatus.BINDING_SUCCESS != zxUser.getCardStatus())
                throw TofocusException.of(LejiaErrCode.ZX_VENDOR_ERROR);
        }
        // 操作钱包,减少可提现金额
        vendorWalletUpdManager.updWalletAmount(vendorKey, amount, false, null, null);
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
        line.setOrderTime(new Date());
        MktVendorWalletLine vwLine = vendorWalletLineDao.add(line);
        // 增加提现记录
        MktVendorWithdrawal withdrawal = new MktVendorWithdrawal();
        withdrawal.setLineKey(vwLine.getPkey());
        withdrawal.setVendorKey(vendorKey);
        withdrawal.setStatus(WithdrawalStatus.NO_PAYMENT);
        withdrawal.setAmount(amount);
        withdrawal.setBalance(line.getBalance());
        if (zxUser != null)
        {
            withdrawal.setBankname("中信银行");
            withdrawal.setBankuser(zxUser.getUserNm());
            withdrawal.setBankcard(zxUser.getPan());
            Boolean runWithdraw = tjZxManager.runWithdraw(zxUser.getZxUserId(), amount, "00");
            if (Boolean.TRUE.equals(runWithdraw))
                withdrawal.setStatus(WithdrawalStatus.PAYMENT);
        }
        else
        {
            withdrawal.setBankname(vendor.getBankname());
            withdrawal.setBankuser(vendor.getBankuser());
            withdrawal.setBankcard(vendor.getBankcard());
            withdrawal.setBankBranchName(vendor.getBankBranchName());
        }
        withdrawal.setFarmer(vendor.getFarmer());
        withdrawal.setAscription(vendor.getAscription());
        vendorWithdrawalDao.add(withdrawal);
        return true;
    }
    
    
 
}
