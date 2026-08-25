package cn.tofocus.lejia.bean.dto.refund;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WebRefundOrderOnInfo
{
    @Schema(description = "订单主键")
    private Integer pkey;
    
    @Schema(description = "退单明细")
    private List<WebRefundOnLine> lines;
    
    @Schema(description = "退款理由")
    private String reason;
}
