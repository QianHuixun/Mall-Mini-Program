package cn.tofocus.lejia.bean.dto.refund;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.tofocus.lejia.bean.dto.v2.order.OrderGwcV2OnList;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorRefundOrderOnList
{
    @Schema(description = "明细主键")
    private Integer pkey;
    
    private Integer goods;
    
    @Schema(description = "商品图片")
    private String photo;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    @Schema(description = "商品类型")
    private MType mType;
    
    @Schema(description = "启用标志 true 还存在")
    private Boolean enabled;
    
    @Schema(description = "实际购买数量")
    private Integer num;
    
    @Schema(description = "重量")
    private BigDecimal weight;
    
    @Schema(description = "实付单价")
    private BigDecimal price;

    @Schema(description = "实付金额")
    private BigDecimal sumPrice;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "规格详情")
    private List<OrderGwcV2OnList> lines = new ArrayList<>();
}
