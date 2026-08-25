package cn.tofocus.lejia.bean.dto.excel.market;

import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ExportMktSupplierSaleSummary
{
    @ExcelProperty("供应商")
    private String supplierName;
    
    @ExcelProperty("订单笔数")
    private Long orderCount;
    
    @ExcelProperty("商品销售数量")
    private Long goodsCount;
    
    @ExcelProperty("积分总价")
    private Integer pointnSum;
    
    @ExcelProperty("积分退款")
    private Integer refundPoint;
    
    @ExcelProperty("商品总价")
    private BigDecimal amtoSum;
    
    @ExcelProperty("退款金额")
    private BigDecimal refundAmt;
    
    @ExcelProperty("配送费")
    private BigDecimal postageSum;
    
    @ExcelProperty("配送费退款")
    private BigDecimal refundPostage;
    
    @ExcelProperty("合计金额")
    private BigDecimal amtnSum;
}
