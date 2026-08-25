package cn.tofocus.lejia.bean.dto.order;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorOrderInfo
{
    @Schema(description = "采购状态")
    private String statusName;
    
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "商品信息")
    List<VendorOrderOnList> vendors;
    
    @Schema(description = "撤销记录")
    List<VendorOrderOnList> revokes;
    
    
}
