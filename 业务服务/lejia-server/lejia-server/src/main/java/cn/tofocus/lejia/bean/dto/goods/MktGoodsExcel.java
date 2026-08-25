package cn.tofocus.lejia.bean.dto.goods;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.Size;


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

@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 12, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
public class MktGoodsExcel extends ErrMsgModel
{
    @ExcelProperty("商品库(必填)")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ColumnWidth(25)
    private String gooodsMainName;
    
    @ExcelProperty("商品名称(必填)")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ColumnWidth(25)
    @Size(max = 80, message = "最多输入80个字符")
    private String title;
    
    @ExcelProperty("描述")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ColumnWidth(25)
    @Size(max = 200, message = "最多输入200个字符")
    private String description;
    
    @ExcelProperty("商品ID")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ColumnWidth(25)
    private String serialNumber;
    
    @ExcelProperty("显示销量")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private Integer xsNum;
    
    @ExcelProperty("每日限购")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private Integer purchaseNum;
    
    @ExcelProperty("上架时间")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ColumnWidth(20)
    private String startTime;
    
    @ExcelProperty("下架时间")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @ColumnWidth(20)
    private String endTime;
    
    @ExcelProperty("商品规格(必填)")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @Size(max = 40, message = "最多输入40个字符")
    private String space;
    
    @ExcelProperty("原价(必填)")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private BigDecimal priceOld;
    
    @ExcelProperty("现价(必填)")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private BigDecimal price;
    
    @ExcelProperty("库存(必填)")
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private Integer kcNum;
    
    @ExcelIgnore
    private Integer goods;
    
    @ExcelIgnore
    private Integer pkey;
    
    @ExcelIgnore
    private Integer point;
    
    @ExcelIgnore
    private BigDecimal comm;
    
    @ExcelIgnore
    private BigDecimal priceMember;
    
    @ExcelIgnore
    private Date startDate;
    
    @ExcelIgnore
    private Date endDate;
    
    @ExcelIgnore
    private Boolean guessLike;
    
    @ExcelIgnore
    private String extendCon;
    
    @ExcelIgnore
    private Integer isPostage;
    
    @ExcelIgnore
    private String userFarmerKey;
    
    @ExcelIgnore
    private Integer userVendorKey;
}
