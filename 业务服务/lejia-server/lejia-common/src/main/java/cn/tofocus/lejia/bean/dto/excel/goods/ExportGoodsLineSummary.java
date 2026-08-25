package cn.tofocus.lejia.bean.dto.excel.goods;

import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class ExportGoodsLineSummary
{
    @ExcelProperty("商品名称")
    private String goodsName;
    
    @ExcelProperty("规格")
    private String spaceName;
    
    @ExcelProperty("订单笔数")
    private Long orderCount;
    
    @ExcelProperty("销售数量")
    private Long goodsCount;
    
    @ExcelProperty("销售额")
    private BigDecimal actualAmtSum;
}
