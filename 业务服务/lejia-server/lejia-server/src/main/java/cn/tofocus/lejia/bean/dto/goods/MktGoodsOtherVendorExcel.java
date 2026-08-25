package cn.tofocus.lejia.bean.dto.goods;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

import lombok.Data;

@Data
public class MktGoodsOtherVendorExcel extends MktGoodsVendorExcel implements ExcelHasSellingPoints
{
    @ExcelProperty(value = "商品标签", index = 2)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String tag;

    @ExcelProperty(value = "每日限购", index = 6)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private Integer purchaseNum;

    @ExcelProperty(value = "是否包邮(必填,0表示不包邮,1表示包邮)", index = 7)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    @NotNull(message = "该字段不可为空")
    private Integer isPostage;

    @ExcelProperty(value = "排序", index = 10)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private Integer sort;

    @ExcelProperty(value = "卖点1名称", index = 11)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String sellingPointName1;

    @ExcelProperty(value = "卖点1内容", index = 12)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String sellingPointContent1;

    @ExcelProperty(value = "卖点2名称", index = 13)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String sellingPointName2;

    @ExcelProperty(value = "卖点2内容", index = 14)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String sellingPointContent2;

    @ExcelProperty(value = "卖点3名称", index = 15)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String sellingPointName3;

    @ExcelProperty(value = "卖点3内容", index = 16)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String sellingPointContent3;

    @ExcelProperty(value = "卖点4名称", index = 17)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String sellingPointName4;

    @ExcelProperty(value = "卖点4内容", index = 18)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String sellingPointContent4;

    @ExcelProperty(value = "重量", index = 22)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private BigDecimal weight;

    @ExcelProperty(value = "商品轮播图", index = 25)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String photo1;

    @ExcelProperty(value = "商品详情", index = 26)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN)
    private String content2;

}
