package cn.tofocus.lejia.bean.dto.refund;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WebRefundOnLine
{
    @Schema(description = "明细主键")
    private Integer pkey;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
}
