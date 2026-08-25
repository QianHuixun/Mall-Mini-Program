package cn.tofocus.lejia.bean.dto.goods;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

import lombok.Data;

@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 12, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
public class MktGoodsCollageVendorExcel extends MktGoodsVendorExcel
{
    @ExcelProperty(value = "是否包邮(必填,0表示不包邮,1表示包邮)", index = 6)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @NotNull(message = "该字段不可为空")
    private Integer isPostage;
    
    @ExcelProperty(value = "重量", index = 11)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private BigDecimal weight;
    
    @ExcelProperty(value = "成团人数", index = 13)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @NotNull(message = "成团人数必填,且大于0")
    private Integer collageNum;
    
}
