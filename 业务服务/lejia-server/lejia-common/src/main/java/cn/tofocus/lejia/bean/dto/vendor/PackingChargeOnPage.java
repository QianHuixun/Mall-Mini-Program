package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PackingChargeOnPage
{
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "商户展示名称")
    private String displayName;
    
    @Schema(description = "摊位号")
    private String booth;
    
    @Schema(description = "订单金额")
    private BigDecimal orderAmt;
    
    @Schema(description = "打包费用")
    private BigDecimal packingCharge;
    
    @Schema(description = "结算金额")
    private BigDecimal amt;
    
    @Schema(description = "付款时间")
    private Date paymentTime;
    
    @JsonIgnore
    private RefundStatus refundStatus;
    @JsonIgnore
    private BigDecimal refundAmt;
}
