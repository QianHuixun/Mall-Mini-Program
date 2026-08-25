package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppMemberCentreMsdLine
{
    @Schema(description = "加减标志")
    private Boolean direct;
    
    @Schema(description = "操作金额")
    private BigDecimal amt;
    
    @Schema(description = "余额")
    private BigDecimal balance;
    
    @Schema(description = "操作类型")
    private MsdOperationType operationType;
    
    @JoinEnum(from = "operationType")
    private String operationTypeName;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "建档时间")
    private Date createdTime;
    
    @JsonIgnore
    private String formId;
}
