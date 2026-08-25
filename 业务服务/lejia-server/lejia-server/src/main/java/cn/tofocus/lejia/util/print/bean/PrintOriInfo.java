package cn.tofocus.lejia.util.print.bean;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PrintOriInfo
{
    private String goodsName;
    
    private String specifications;
    
    private Integer count;
    
    @Schema(description = "商品原价")
    private BigDecimal goodsPrice;
    
    @Schema(description = "采购价格")
    private BigDecimal price;
    
    @Schema(description = "采购总价")
    private BigDecimal totalPrice;
    
    private BigDecimal goodsAmt;
    
    private Integer vendor;
    
    private BigDecimal refundAmt;
    
    @Schema(description = "采购退款金额")
    private BigDecimal procureRefundAmt;
    
    @Schema(description = "结算金额")
    private BigDecimal amt;
}
