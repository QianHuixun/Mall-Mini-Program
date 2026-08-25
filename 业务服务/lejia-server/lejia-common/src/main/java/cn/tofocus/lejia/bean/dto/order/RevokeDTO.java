package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.VendorOrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 合作商户-撤销记录分页DTO
 */
@Data
public class RevokeDTO
{

    /**
     * mkt_vendor_order的主键
     */
    @Schema(description = "主键")
    private Integer pkey;

    @Schema(description = "订单主键")
    private Integer orderPkey;

    @JoinDTO(dataQuery = "mktOrderDao", from = "orderPkey")
    @JsonIgnore
    private MktOrder mktOrder;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    public String getCode(){
        if (Objects.nonNull(mktOrder))
        {
            return mktOrder.getCode();
        }
        else
        {
            return null;
        }
    }

    @Schema(description = "订单类型英文")
    private VendorOrderType vendorOrderType;

    @Schema(description = "订单类型中文")
    @JoinEnum(from = "vendorOrderType")
    private String vendorOrderTypeName;

    @Schema(description = "商户")
    @JsonIgnore
    private Integer vendor;

    @JoinDTO(dataQuery = "mktVendorDao", from = "vendor")
    @JsonIgnore
    private MktVendor mktVendor;

    @Schema(description = "商户名称")
    public String getVendorName()
    {
        if(vendor != null && vendor == 0)
        {
            return "自采";
        }
        if (mktVendor != null)
        {
            return mktVendor.getDisplayName();
        }
        return null;
    }

    @Schema(description = "商品ID")
    @JsonIgnore
    private Integer goods;

    @Schema(description = "商品名称")
    private String goodsName;

    @Schema(description = "数量")
    private Integer num;

    @Schema(description = "商品原价")
    private BigDecimal goodsPrice;

    /**
     * 仅仅采购价结算时使用
     */
    @Schema(description = "采购单价")
    private BigDecimal price;

    /**
     * 仅仅采购价结算时使用
     */
    @Schema(description = "采购总价")
    private BigDecimal totalPrice;

    /**
     * 仅仅采购价结算时使用
     */
    @Schema(description = "结算金额")
    private BigDecimal amt;

    @Schema(description = "采购时间")
    private Date createdTime;

    @Schema(description = "采购备注")
    private String remark;

    @Schema(description = "撤销时间")
    private Date revokeTime;
}
