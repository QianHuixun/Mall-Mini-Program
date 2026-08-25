package cn.tofocus.lejia.bean.excel;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.CommSourceType;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
@ColumnWidth(25)
public class ExportMemberCommLine
{
    @JsonIgnore
    private Integer pkey;
    
    @JsonIgnore
    private Integer member;
    
    @ExcelProperty("用户名称")
    private String memberName;
    
    @ExcelProperty("手机号")
    private String memberMobile;
    
    @JsonIgnore
    private CommSourceType source;
    
    @ExcelProperty("类型")
    private String sourceName;
    
    @ExcelProperty("交易金额")
    private String amtStr;
    
    @ExcelProperty("余额")
    private BigDecimal balance;
    
    @ExcelProperty("时间")
    private Date createdTime;
}
