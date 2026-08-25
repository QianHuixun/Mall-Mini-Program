package cn.tofocus.lejia.bean.dto.excel.jd;

import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class ExportJdOrderGoodsReport
{
    @ExcelProperty("商品名")
    private String goodsName;
    
    @ExcelProperty("商品规格")
    private String spaceName;
    
    @ExcelProperty("订单笔数")
    private Long orderCount;
    
    @ExcelProperty("销售数量")
    private Long goodsCount;
    
    @ExcelProperty("销售额")
    private BigDecimal amt;
}
