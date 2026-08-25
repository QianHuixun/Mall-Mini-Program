package cn.tofocus.lejia.bean.entity.vendor;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "mkt_vendor_packing_charge")
@FieldNameConstants(innerTypeName = "F")
public class MktVendorPackingCharge implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_vendor_packing_charge")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
    @Schema(description = "等级")
    private Integer grade;
    
    @Schema(description = "订单金额")
    private BigDecimal orderAmt;
    
    @Schema(description = "打包费用")
    private BigDecimal packingCharge;

    @Schema(description = "归属主键")
    private Integer ascription;
}
