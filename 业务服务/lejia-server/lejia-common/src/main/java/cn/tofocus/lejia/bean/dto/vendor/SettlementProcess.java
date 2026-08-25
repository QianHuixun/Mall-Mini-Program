package cn.tofocus.lejia.bean.dto.vendor;

import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.db.dto.UserName;
import cn.tofocus.lejia.bean.enums.ProcessNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SettlementProcess
{
    
    private ProcessNode processNode;
    
    @JoinEnum(from = "processNode")
    private String typeName;
    
    @Schema(description = "content")
    private String content;
    
    @Schema(description = "rem")
    private String rem;
    
    @Schema(description = "建档时间")
    private Date createdTime;
    
    @JsonIgnore
    private Integer createdBy;
    
    @UserName(from = "createdBy")
    @ExcelProperty("建档员")
    private String appByName;
}