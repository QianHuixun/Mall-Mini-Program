package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;


import cn.tofocus.db.dto.JoinProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktSupplierSaleSummary
{
    @Schema(description = "供应商主键")
    private Integer supplier;
    
    @JoinProperty(dataQuery = "mktSupplierDao", from = "supplier", propertyName = "name")
    @Schema(description = "供应商名称")
    private String supplierName;
    
    @Schema(description = "订单笔数")
    private Long orderCount;
    
    @Schema(description = "商品销售数量")
    private Long goodsCount;
    
    @Schema(description = "积分总价")
    private Integer pointnSum;
    
    @Schema(description = "积分退款")
    private Integer refundPoint;
    
    @Schema(description = "商品总价")
    private BigDecimal amtoSum;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "配送费")
    private BigDecimal postageSum;
    
    @Schema(description = "配送费退款")
    private BigDecimal refundPostage;
    
    @Schema(description = "合计金额")
    private BigDecimal amtnSum;
    
    public static MktSupplierSaleSummary empty()
    {
        return new MktSupplierSaleSummary().fillDefault();
    }
    
    public MktSupplierSaleSummary fillDefault()
    {
        if (orderCount == null) orderCount = 0L;
        if (goodsCount == null) goodsCount = 0L;
        if (pointnSum == null) pointnSum = 0;
        if (amtoSum == null) amtoSum = BigDecimal.ZERO;
        if (postageSum == null) postageSum = BigDecimal.ZERO;
        if (amtnSum == null) amtnSum = BigDecimal.ZERO;
        if (refundAmt == null) refundAmt = BigDecimal.ZERO;
        if (refundPostage == null) refundPostage = BigDecimal.ZERO;
        return this;
    }
}
