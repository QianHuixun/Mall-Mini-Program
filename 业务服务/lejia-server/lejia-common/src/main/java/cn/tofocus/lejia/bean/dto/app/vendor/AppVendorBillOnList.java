package cn.tofocus.lejia.bean.dto.app.vendor;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppVendorBillOnList
{
    @Schema(description = "订单时间")
    private String orderTime;
    
    @Schema(description = "预计结算时间/已结算时间")
    private String settlementTime;
    
    @Schema(description = "金额")
    private BigDecimal amount;
    
    @JsonIgnore
    private Date time;
}
