package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SettlementLineOnList
{
    private Long pkey;
    
    private Integer vendor;
    
    @Schema(description = "市场名称")
    private String marketName;
    
    @Schema(description = "商户名称")
    private String vendorName;
    
    @Schema(description = "总采购笔数")
    private Integer orderCount;
    
    @Schema(description = "总采购金额")
    private BigDecimal orderAmt;
    
    @Schema(description = "佣金费率")
    private BigDecimal commission;
    
    @Schema(description = "总交易佣金")
    private BigDecimal orderCommission;
    
    @Schema(description = "总结算金额")
    private BigDecimal amt;
    
    @Schema(description = "结算周期")
    private String time;
    
    private String startTime;
    private String endTime;
    
    @Schema(description = "结算操作时间")
    private Date createdTime;
    
    private Integer settlementPkey;
    
    @JsonIgnore
    private String farmer;
}