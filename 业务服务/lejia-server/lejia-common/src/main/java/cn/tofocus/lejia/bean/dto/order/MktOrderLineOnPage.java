package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOrderLineOnPage
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    @Schema(description = "订单编号")
    private String kcCode;
    
    @Schema(description = "用户")
    private Integer member;
    
    @Schema(description = "用户手机号")
    @JoinProperty(dataQuery = "mktMemberDao", from = "member", propertyName = "mobile")
    private String memberMobile;
    
    @Schema(description = "商品pkey")
    private Integer goods;
    
    @Schema(description = "规格pkey")
    private Integer space;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @JoinProperty(dataQuery = "mktGoodsSpaceDao", from = "space", propertyName = "space")
    @Schema(description = "规格名称")
    private String spaceName;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "商品单价")
    private BigDecimal pricen;
    
    @Schema(description = "商品总价")
    public BigDecimal getAmt()
    {
        if (pricen == null || num == null) return BigDecimal.ZERO;
        return pricen.multiply(new BigDecimal(num)).setScale(2, RoundingMode.HALF_UP);
    }
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt = BigDecimal.ZERO;

    @Schema(description = "积分退款")
    private Integer refundPoint;
    
    @JsonIgnore
    private BigDecimal couponAmt;
    
    @Schema(description = "合计金额")
    public BigDecimal getSumGoodsAmt()
    {
        if(refundAmt != null)
            return getAmt().subtract(refundAmt);
        return getAmt();
    }
    
    @Schema(description = "建档时间")
    private Date createdTime;
}
