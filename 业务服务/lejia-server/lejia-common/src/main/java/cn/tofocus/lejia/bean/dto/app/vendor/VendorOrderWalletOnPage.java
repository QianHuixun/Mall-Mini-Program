package cn.tofocus.lejia.bean.dto.app.vendor;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorOrderWalletOnPage
{
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "规格")
    private String spaceName;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "单价")
    private BigDecimal goodsPrice;
    
    @Schema(description = "商品金额")
    public BigDecimal getGoodsAmt()
    {
        return goodsPrice.multiply(new BigDecimal(num));
    }
    
    @JsonIgnore
    @Schema(description = "结算金额（没扣手续费，扣了佣金，扣了打包费）")
    private BigDecimal amt;
    
    @Schema(description = "交易佣金")
    private BigDecimal commissions;
    
    @Schema(description = "支付渠道手续费")
    private BigDecimal payComm;
    
    @Schema(description = "应结金额")
    public BigDecimal getOrderAmt()
    {
        BigDecimal commissions = this.commissions != null ? this.commissions : BigDecimal.ZERO;
        BigDecimal packingCharge = this.packingCharge != null ? this.packingCharge : BigDecimal.ZERO;
        return amt.add(commissions).add(packingCharge);
    }
    
    @Schema(description = "退款金额")
    public BigDecimal getRefundAmt()
    {
        return getGoodsAmt().subtract(getOrderAmt());
    }
    
    @Schema(description = "打包费用")
    @JsonIgnore
    private BigDecimal packingCharge;
    
}
