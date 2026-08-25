package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CommsDetailOnPage
{
    @Schema(description = "订单号")
    private String kcCode;
    
    @Schema(description = "购买时间")
    private String buyTime;
    
    @Schema(description = "购买人")
    private String buyMember;
    
    @Schema(description = "购买金额")
    private String buyAmtn;
    
    @Schema(description = "佣金")
    private String comms;
    
    @Schema(description = "推荐人")
    private String tjr;
    
    @Schema(description = "佣金发放时间")
    private String commsTime;
    
}
