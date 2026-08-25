package cn.tofocus.lejia.bean.dto.refund;

import java.util.List;

import cn.tofocus.lejia.bean.enums.jd.ReturnExchange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预退款提交信息")
public class PreRefundOrderCommit
{
    @Schema(description = "订单主键")
    private Integer pkey;
    
    @Schema(description = "退单明细")
    private List<RefundOnLine> lines;
    
    @Schema(description = "京东已收货订单,需要传,其他不需要传 退货/换货")
    private ReturnExchange returnExchange;
}
