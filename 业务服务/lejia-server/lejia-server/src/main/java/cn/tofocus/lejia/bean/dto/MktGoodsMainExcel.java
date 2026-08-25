package cn.tofocus.lejia.bean.dto;

import javax.validation.constraints.NotNull;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;

import cn.tofocus.db.excel.ErrMsgModel;
import lombok.Data;

@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 15, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
@ExcelIgnoreUnannotated
public class MktGoodsMainExcel extends ErrMsgModel
{
    
    @ExcelIgnore
    private Integer pkey;
    
    /**
     * 溯源商户
     */
    @ExcelProperty("分类")
    @NotNull(message = "分类不能为空")
    private String gtypeName;
    
    private Integer gtype;
    
    /**
     * 溯源商品
     */
    @ExcelProperty("名称")
    @NotNull(message = "名称不能为空")
    private String name;
    
    /**
    * 启用标志
    */
    @ExcelProperty("是否启用")
    private Boolean enabled;
    
    /**
     * 供应商
     */
    @ExcelProperty("排序")
    private Integer sort;
    
    /**
     * 进货日期
     */
    //	@ExcelProperty("备注(可不填)")
    private String remark;
    
    /**
     * 是否已删除
     */
    private Boolean idDel;
    
    /**
     * 版本
     */
    private Integer rowVension;
    
    private Integer ascription;
    
    @ExcelIgnore
    private String farmer;
    
}
