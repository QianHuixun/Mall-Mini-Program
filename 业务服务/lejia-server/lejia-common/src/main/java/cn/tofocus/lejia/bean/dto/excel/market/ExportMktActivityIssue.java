package cn.tofocus.lejia.bean.dto.excel.market;

import java.math.BigDecimal;
import java.util.Date;

import cn.tofocus.common.data.Amt;
import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ExportMktActivityIssue
{
    @ExcelProperty(value = "会员手机号")
    private String memberMobile;
    
    @ExcelProperty(value = "领取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    @Amt
    @ExcelProperty(value = "付款金额")
    private BigDecimal amt;
}
