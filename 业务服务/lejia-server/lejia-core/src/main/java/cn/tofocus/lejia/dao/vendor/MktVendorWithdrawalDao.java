package cn.tofocus.lejia.dao.vendor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.vendor.WalletDetailsOnPage;
import cn.tofocus.lejia.bean.dto.vendor.WithdrawalOnInfo;
import cn.tofocus.lejia.bean.dto.vendor.WithdrawalOnPage;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWithdrawal;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import cn.tofocus.lejia.bean.enums.vendor.WithdrawalStatus;

@Component
public class MktVendorWithdrawalDao extends JpaSpecificationDelegate<Integer,MktVendorWithdrawal>
{
    public Map<Integer,MktVendorWithdrawal> mapLineKey(Integer vendorKey)
    {
        List<MktVendorWithdrawal> exec = this.select().eq("vendorKey", vendorKey).exec();
        Map<Integer,MktVendorWithdrawal> map = new HashMap<>();
        exec.forEach(e -> map.put(e.getLineKey(), e));
        return map;
    }
    
    public List<WalletDetailsOnPage> byVendorKey(Integer vendorKey, Integer qfAscription)
    {
        List<WalletDetailsOnPage> res = new ArrayList<>();
        List<MktVendorWithdrawal> list = this.select().eq("vendorKey", vendorKey).exec();
        for(MktVendorWithdrawal w : list)
        {
            WalletDetailsOnPage wd = new WalletDetailsOnPage();
            String bankcard = w.getBankcard();
            if(bankcard == null)
                bankcard = "";
            if (qfAscription.equals(w.getAscription()))
            {
                wd.setOrderType("提现至" + maskCardNumber(bankcard));
            }
            else
            {
                if(bankcard.length() > 4)
                    bankcard = bankcard.substring(bankcard.length() -4 , bankcard.length());
                wd.setOrderType("提现至" + w.getBankname() + "(" + bankcard + ")");
            }
//            wd.setOrderType("提现至" + w.getBankname() + "(" + bankcard + ")");
            wd.setOrderAmount(w.getAmount());
            wd.setBalance(w.getBalance());
            wd.setSource(VendorWalletSource.WITHDRAWAL);
            if(WithdrawalStatus.NO_PAYMENT.equals(w.getStatus()))
                wd.setStatus("打款中");
            else
                wd.setStatus("成功");
            wd.setSettlementTime(w.getCreatedTime());
            res.add(wd);
        }
        return res;
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() <= 4) {
            return cardNumber;  
        }
        StringBuilder maskedCard = new StringBuilder(cardNumber);
        for (int i = 4; i < maskedCard.length() - 4; i++) {
            maskedCard.setCharAt(i, '*');
        }
        return maskedCard.toString();
    }
    
    public PageResult<WithdrawalOnPage> queryWithdrawalOnPage(int page, int pagesize, String startDate, String endDate,
        List<Integer> vendorKeys, WithdrawalStatus status, String marketPkey, Integer ascription)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("farmer", marketPkey)
            .eq("status", status)
            .eq("ascription", ascription)
            .in("vendorKey", vendorKeys)
            .iF(StringUtils.isNotBlank(startDate))
                .ge("createdTime", DateUtil.atStartOfDay(startDate))
            .endIf()
            .iF(StringUtils.isNotBlank(endDate))
                .le("createdTime", DateUtil.atEndOfDay(endDate))
            .endIf()
            .sort("createdTime")
            .sort("pkey")
            .execDto(WithdrawalOnPage.class);
    }
    
    public WithdrawalOnInfo aggWithdrawalOnInfo(String startDate, String endDate,
        List<Integer> vendorKeys, String marketPkey, Integer ascription)
    {
        List<WithdrawalOnInfo> list = this.aggregation()
        .eq("farmer", marketPkey)
        .eq("ascription", ascription)
        .in("vendorKey", vendorKeys)
        .iF(StringUtils.isNotBlank(startDate))
            .ge("createdTime", DateUtil.atStartOfDay(startDate))
        .endIf()
        .iF(StringUtils.isNotBlank(endDate))
            .le("createdTime", DateUtil.atEndOfDay(endDate))
        .endIf()
        .eq("status", WithdrawalStatus.NO_PAYMENT)
        .count("pkey", "num")
        .sum("amount", "amount")
        .execListDto(WithdrawalOnInfo.class);
        if(list.isEmpty())
            return new WithdrawalOnInfo();
        return list.get(0);
    }
}