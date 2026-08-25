package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WalletDetailsOnPage
{
    @Schema(description = "交易类型")
    private String orderType;
    
    @Schema(description = "交易金额")
    private BigDecimal orderAmount;
    
    @Schema(description = "余额")
    private BigDecimal balance;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "时间")
    private Date settlementTime;
    
    @Schema(description = "来源, 消费金额显示正,提现金额显示负")
    private VendorWalletSource source;
    
    @JsonIgnore
    private Date time;
    
    @JsonIgnore
    @Schema(description = "对应钱包账户明细主键")
    private Integer lineKey;
    
}
