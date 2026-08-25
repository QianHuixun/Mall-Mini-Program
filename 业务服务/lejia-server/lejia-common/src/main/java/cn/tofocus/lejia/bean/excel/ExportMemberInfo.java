package cn.tofocus.lejia.bean.excel;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
@ColumnWidth(25)
public class ExportMemberInfo
{
    @JsonIgnore
    private Integer pkey;
    
    @ExcelProperty("昵称")
    private String name;
    
    @ExcelProperty("手机")
    private String mobile;
    
    @ExcelProperty("注册时间")
    private Date createdTime;
    
    @ExcelProperty("标签")
    private String tagNames;
    
    @ExcelProperty("积分")
    private Integer points = 0;
    
    @ExcelProperty("账户余额")
    private BigDecimal balance = BigDecimal.ZERO;
    
    @ExcelProperty("消费金额")
    private BigDecimal consumeAmt = BigDecimal.ZERO;
    
    @ExcelProperty("消费笔数")
    private Long consumeCount = 0L;
    
    @JsonIgnore
    private String lastConsumeFarmer;
    
    @ExcelProperty("最近消费市场名称")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "lastConsumeFarmer")
    private String lastConsumeFarmerName;
    
    @ExcelProperty("最近消费时间")
    private Date lastConsumeTime;
    
    @ExcelProperty("用户来源")
    private String source;
    
    @ExcelProperty("备注")
    private String remark;
    
}
