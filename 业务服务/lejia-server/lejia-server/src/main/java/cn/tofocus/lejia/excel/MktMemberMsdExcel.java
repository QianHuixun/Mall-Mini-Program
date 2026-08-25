package cn.tofocus.lejia.excel;

import java.math.BigDecimal;

import javax.validation.constraints.*;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

import cn.tofocus.db.excel.ErrMsgModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 12, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
public class MktMemberMsdExcel extends ErrMsgModel
{
    @ExcelProperty(value = "手机号")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @NotBlank(message = "手机号不能为空")
    // @Pattern(regexp = "^((13[0-9])|(14[5|7|9])|(15([0-3]|[5-9]))|(166)|(17[0235678])|(18[0-9])|(19([0-3]|[5-9])))\\d{8}$", message = MSG_MOBILE)
    @Pattern(regexp = "^\\d{11}$", message = MSG_MOBILE)
    private String mobile;
    
    @ExcelProperty(value = "标签")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @NotBlank(message = "标签不能为空")
    private String tagName;
    
    @ExcelProperty(value = "充值金额")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @NotNull(message = "充值金额不能为空")
    @Digits(integer = 8, fraction = 2)
    @DecimalMax(value = "99999999.99")
    @DecimalMin(value = "0.00")
    private BigDecimal amt;
    
    @ExcelIgnore
    private Integer member;
    
    @ExcelIgnore
    private Integer tag;
}
