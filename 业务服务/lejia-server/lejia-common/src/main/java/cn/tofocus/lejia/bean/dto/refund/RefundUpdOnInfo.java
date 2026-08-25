package cn.tofocus.lejia.bean.dto.refund;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RefundUpdOnInfo
{
    private Integer refundPkey;
    
    @Schema(description = "退单明细")
    private List<RefundOnLine> lines;
}
