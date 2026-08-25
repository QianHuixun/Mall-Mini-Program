package cn.tofocus.lejia.bean.entity.refund;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "mkt_order_refund_line")
@FieldNameConstants(innerTypeName = "F")
public class MktOrderRefundLine implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_order_refund_line")
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "退款表主键")
    private Integer refundPkey;
    
    @Schema(description = "订单明细表主键")
    private Integer orderLinePkey;
    
    @Schema(description = "商品pkey")
    private Long goods;
    
    @Schema(description = "规格pkey")
    private Long space;
    
    @Schema(description = "退款数量")
    private Integer refundNum;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "退款京东金额")
    private BigDecimal refundJd;

    @Size(max = 200)
    @Column(length = 200)
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
