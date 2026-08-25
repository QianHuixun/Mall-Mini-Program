package cn.tofocus.lejia.bean.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorOrderReportLine
{
    @Schema(description = "时间")
    private String date;
    
    @Schema(description = "商户名称")
    private String name;
    
    @Schema(description = "采购笔数")
    private String num;
    
    @Schema(description = "采购金额(元)")
    private String amt;
    
    @JsonIgnore
    private Integer vendor;
}
