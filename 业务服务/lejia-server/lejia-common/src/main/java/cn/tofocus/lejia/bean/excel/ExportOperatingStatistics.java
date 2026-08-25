package cn.tofocus.lejia.bean.excel;

import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
@ColumnWidth(25)
public class ExportOperatingStatistics
{
    
    @ExcelProperty("市场pkey")
    @JsonIgnore
    private String farmer;
    
    @ExcelProperty("市场")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "farmer")
    private String farmerName;
    
    @ExcelProperty("日期")
    private String yesterTime;
    
    @ExcelProperty("访问人数")
    private int accCount = 0;
    
    @ExcelProperty("支付人数")
    private int ymemberPayNum = 0;
    
    @ExcelProperty("成交订单")
    private int orderCount = 0;
    
    @ExcelProperty("商品金额")
    private BigDecimal amto = BigDecimal.ZERO;
    
    @ExcelProperty("配送费")
    private BigDecimal postage = BigDecimal.ZERO;
    
    @ExcelProperty("优惠金额")
    private BigDecimal cardAmt = BigDecimal.ZERO;
    
    @ExcelProperty("退款金额")
    private BigDecimal refundAmt = BigDecimal.ZERO;
    
    @ExcelProperty("营收金额")
    private BigDecimal revenueAmt = BigDecimal.ZERO;
    
}
