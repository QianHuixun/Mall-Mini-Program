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
public class HourSalesExcel
{
    @ExcelProperty(value = "订单号")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ContentFontStyle(fontName = "宋体", fontHeightInPoints = 11)
    @ColumnWidth(value = 25)
    private String kcCode;
    
    @ExcelProperty(value = "单品")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ContentFontStyle(fontName = "宋体", fontHeightInPoints = 11)
    @ColumnWidth(value = 25)
    private String name;
    
    @ExcelProperty(value = "数量")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ContentFontStyle(fontName = "宋体", fontHeightInPoints = 11)
    @ColumnWidth(value = 25)
    private String num;
    
    @ExcelProperty(value = "金额")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ContentFontStyle(fontName = "宋体", fontHeightInPoints = 11)
    @ColumnWidth(value = 25)
    private String pricen;
    
    @ExcelProperty(value = "下单时间")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ContentFontStyle(fontName = "宋体", fontHeightInPoints = 11)
    @ColumnWidth(value = 25)
    private String createdTime;
    
}
