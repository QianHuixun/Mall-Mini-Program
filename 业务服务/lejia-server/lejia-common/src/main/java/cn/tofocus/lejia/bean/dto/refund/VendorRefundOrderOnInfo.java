package cn.tofocus.lejia.bean.dto.refund;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorRefundOrderOnInfo
{
    @Schema(description = "商户名称")
    private String name;
    
    @Schema(description = "商户摊位")
    private String booth;
    
    private List<VendorRefundOrderOnList> list;
    
    @Schema(description = "合计商品数量")
    private Integer num;
    
    @Schema(description = "合计退款")
    private BigDecimal sumAmt;
    
    @Schema(description = "合计积分退款")
    private Integer refundPoint;
}
