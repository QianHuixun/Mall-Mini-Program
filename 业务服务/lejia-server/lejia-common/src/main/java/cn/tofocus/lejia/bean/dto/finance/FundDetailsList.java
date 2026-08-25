package cn.tofocus.lejia.bean.dto.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.ZxWithdrawStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FundDetailsList
{
    private Integer pkey;
    @Schema(description = "交易市场")
    private String name;
    
    @Schema(description = "类型")
    @JoinEnum(from = "status")
    private String statusName;
    
    private ZxWithdrawStatus status;
    
    @Schema(description = "账单日期")
    private String billDate;
    
    @Schema(description = "交易金额")
    private BigDecimal comms;
    
    @Schema(description = "余额")
    private BigDecimal balance;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "交易时间")
    private Date withdrawTime;

    @JsonIgnore
    private ZxUserType type;
    @JsonIgnore
    private String value;
    
}
