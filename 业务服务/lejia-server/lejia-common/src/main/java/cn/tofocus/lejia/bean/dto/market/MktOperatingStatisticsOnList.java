package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;

import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOperatingStatisticsOnList
{
    @Schema(description = "pkey")
    private Long pkey;
    
    @Schema(description = "市场pkey")
    private String farmer;
    
    @Schema(description = "市场pkey")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "farmer")
    private String farmerName;
    
    @Schema(description = "日期")
    private String yesterTime;
    
    @Schema(description = "访问人数")
    private int accCount;
    
    @Schema(description = "支付人数")
    private int ymemberPayNum;
    
    @Schema(description = "成交订单")
    private int orderCount;
    
    @Schema(description = "商品金额")
    private BigDecimal amto;
    
    @Schema(description = "配送费")
    private BigDecimal postage;
    
    @Schema(description = "优惠金额")
    private BigDecimal cardAmt;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "营收金额")
    private BigDecimal revenueAmt;
    
}
