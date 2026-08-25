package cn.tofocus.lejia.zx.bean.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PayQuery
{
    @Schema(description = "接口类型")
    private String service;
    
    @Schema(description = "商户号")
    private String mch_id;
    
    @Schema(description = "商户订单号")
    private String out_trade_no;
    
    @Schema(description = "签名")
    private String sign;
}
