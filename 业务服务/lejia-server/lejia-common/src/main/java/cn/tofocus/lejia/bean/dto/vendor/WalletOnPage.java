package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinEntity;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WalletOnPage
{
    @Schema(description = "商户主键")
    private Integer pkey;
    
    @Schema(description = "商户名称")
//    private String name;
    public String getName()
    {
        if(mktVendor != null)
            return mktVendor.getDisplayName();
        return "";
    }
    
    @Schema(description = "摊位号")
//    private String booth;
    public String getBooth()
    {
        if(mktVendor != null)
            return mktVendor.getBooth();
        return "";
    }
    
    @Schema(description = "可提现余额")
    private BigDecimal amount;
    
    @Schema(description = "待结算金额")
    private BigDecimal lockAmount;
    
    @JsonIgnore
    @JoinEntity(dataQuery = "mktVendorDao", from = "pkey")
    private MktVendor mktVendor;
}
