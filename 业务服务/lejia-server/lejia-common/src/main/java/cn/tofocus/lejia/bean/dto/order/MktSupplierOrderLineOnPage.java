package cn.tofocus.lejia.bean.dto.order;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MktSupplierOrderLineOnPage extends MktOrderLineOnPage
{
    @Schema(description = "供应商主键")
    private Integer supplier;
    
    @JoinProperty(dataQuery = "mktSupplierDao", from = "supplier", propertyName = "name")
    @Schema(description = "供应商名称")
    private String supplierName;
    
    //  @JoinProperty(dataQuery = "mktGoodsSpaceDao", from = "space", propertyName = "point")
    @Schema(description = "积分单价")
    private Integer point;
    
    @Schema(description = "积分总价")
    public Integer getPointSum()
    {
        if (getNum() != null && point != null) return getNum() * point;
        return null;
    }
    
    @Schema(description = "用户标签")
    private String tagName;

    @Schema(description = "支付方式")
    @JoinEnum(from = "payType")
    private String payTypeName;
    
    private PayType payType;
}
