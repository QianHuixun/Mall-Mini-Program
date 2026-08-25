package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppOrderStatusNum
{
    @Schema(description = "待付款数量")
    private Long unpaidNum = 0l;
    
    @Schema(description = "待发货数量")
    private Long deliveredNum = 0l;
    
    @Schema(description = "待收货数量")
    private Long shippedNum = 0l;
    
    @Schema(description = "退款订单数量")
    private Long refundedNum = 0l;
}
