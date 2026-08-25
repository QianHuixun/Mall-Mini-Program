package cn.tofocus.lejia.excel;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

import cn.tofocus.lejia.bean.enums.MsdOperationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 12, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
public class MktMemberMsdLineExportExcel
{
    @Schema(description = "主键")
    @ExcelIgnore
    private Long pkey;
    
    @Schema(description = "用户")
    @ExcelIgnore
    private Integer member;
    
    @ExcelProperty(value = "会员名称")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String name;
    
    @ExcelProperty(value = "手机号")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String mobile;
    
    @Schema(description = "标签主键")
    @ExcelIgnore
    private Integer tag;
    
    @ExcelProperty(value = "用户标签")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String tagName;
    
    @Schema(description = "操作类型")
    @ExcelIgnore
    private MsdOperationType operationType;
    
    @ExcelProperty(value = "类型")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String operationTypeName;
    
    /**
     * 加减标志 true:加 false:减
     */
    @Schema(description = "加减标志")
    @ExcelIgnore
    private Boolean direct;
    
    @Schema(description = "操作金额")
    @ExcelIgnore
    private BigDecimal amt;
    
    @ExcelProperty(value = "交易金额")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String amtStr;
    
    @ExcelProperty(value = "热力豆余额")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private BigDecimal balance;
    
    @ExcelProperty(value = "备注")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String remark;
    
    @ExcelProperty(value = "时间")
    @ColumnWidth(18)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
}
