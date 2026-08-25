package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PriceStatus;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorOrderOnList
{
    private Integer pkey;
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
    @Schema(description = "商户名称")
//    @JoinDTO(dataQuery = "mktVendorDao", from = "vendor")
    private String vendorName;
    
//    public String getVendorName()
//    {
//        if (vendor != null && vendor == 0) return "自采";
//        return vendorName;
//    }
    
    @Schema(description = "商品主键")
    private Integer goods;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "规格ID")
    private Integer space;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    @Schema(description = "商品原价")
    private BigDecimal goodsPrice;
    
    @Schema(description = "类型")
    private OrderType type;
    
    @Schema(description = "类型名称")
    public String getTypeName()
    {
        if (type != null) return type.getName();
        return "";
    }
    
    @Schema(description = "采购状态")
    private PurchaseStatus purchaseStatus;
    
    @Schema(description = "采购状态名称")
    public String getPurchaseStatusName()
    {
        if (purchaseStatus != null) return purchaseStatus.getName();
        return "";
    }
    
    @Schema(description = "推荐采购价格", required = false)
    private BigDecimal recommendPrice;
    
    @JsonIgnore
    private PriceStatus priceStatus;
    
    @Schema(description = "价格异常状态")
    public String getPriceStatusName()
    {
        if (priceStatus != null) return priceStatus.getName();
        return "正常";
    }
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "采购价格")
    private BigDecimal price;
    
    @Schema(description = "总价")
    private BigDecimal totalPrice;
    
    @Schema(description = "结算金额")
    private BigDecimal amt;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "撤销时间")
    private Date revokeTime;
    
    @Schema(description = "商户确认时间")
    private Date vendorTime;
    
    @Schema(description = "市场确认时间")
    private Date farmerTime;
    
    @Schema(description = "采购时间")
    private Date createdTime;
    
}
