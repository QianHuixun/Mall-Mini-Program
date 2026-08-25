package cn.tofocus.lejia.bean.dto.refund;

import java.math.BigDecimal;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderRefundOnInfo
{
    @Schema(description = "退款订单数")
    private Integer num;
    
    @Schema(description = "总退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "分页明细")
    private PageResult<OrderRefundOnPage> onPage;
}
