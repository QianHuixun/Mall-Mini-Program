package cn.tofocus.lejia.bean.entity.vendor;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.Name;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "mkt_vendor_order_packing_charge")
@FieldNameConstants(innerTypeName = "F")
public class MktVendorOrderPackingCharge implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_vendor_order_packing_charge")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    @Schema(description = "订单号")
    @Column(name = "kc_code")
    @Name
    private String code;
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
    @Schema(description = "商户展示名称")
    private String displayName;
    
    @Schema(description = "摊位号")
    private String booth;
    
    @Schema(description = "订单金额")
    private BigDecimal orderAmt;
    
    @Schema(description = "打包费用")
    private BigDecimal packingCharge;

    @Schema(description = "结算金额")
    private BigDecimal amt;
    
    @Schema(description = "付款时间")
    private Date paymentTime;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
