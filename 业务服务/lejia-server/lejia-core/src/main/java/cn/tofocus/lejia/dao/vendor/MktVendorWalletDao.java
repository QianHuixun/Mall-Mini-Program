package cn.tofocus.lejia.dao.vendor;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.vendor.WalletOnInfo;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorWallet;

@Component
public class MktVendorWalletDao extends JpaSpecificationDelegate<Integer,MktVendorWallet>
{
    public WalletOnInfo aggregationWalletOnInfo(List<Integer> vendors, String farmer)
    {
        List<WalletOnInfo> list = this.aggregation()
        .in("pkey", vendors)
        .eq("farmer", farmer)
        .sum("amount", "walletAmt")
        .sum("lockAmount", "settlementAmt")
        .execListDto(WalletOnInfo.class);
        if(list.isEmpty())
        {
            WalletOnInfo res = new WalletOnInfo();
            res.setWalletAmt(BigDecimal.ZERO);
            res.setSettlementAmt(BigDecimal.ZERO);
            return res;
        }
        return list.get(0);
    }
}