package cn.tofocus.lejia.bean.dto.data;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

import lombok.Data;

@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 14, bold = BooleanEnum.FALSE)
@HeadRowHeight(18)
public class GoodsIntegralSalesExcel
{
    @ExcelProperty("商品")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ContentFontStyle(fontName = "宋体", fontHeightInPoints = 11)
    @ColumnWidth(value = 20)
    private String name;
    
    @ExcelProperty("兑换次数")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ContentFontStyle(fontName = "宋体", fontHeightInPoints = 11)
    @ColumnWidth(value = 15)
    private String salesNum;
    
    @ExcelProperty("销售金额")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ContentFontStyle(fontName = "宋体", fontHeightInPoints = 11)
    @ColumnWidth(value = 15)
    private String sales;
    
    @ExcelProperty("销售积分")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ContentFontStyle(fontName = "宋体", fontHeightInPoints = 11)
    @ColumnWidth(value = 15)
    private String pointn;
    
}
