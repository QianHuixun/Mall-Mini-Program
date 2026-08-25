package cn.tofocus.lejia.bean.entity.market;

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
@Table(name = "mkt_operating_statistics")
@FieldNameConstants(innerTypeName = "F")
public class MktOperatingStatistics implements HasPkey<Long>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_operating_statistics")
    @Schema(description = "pkey")
    private Long pkey;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "日期")
    private String yesterTime;
    
    @Schema(description = "访问人数")
    private int accCount;
    
    @Schema(description = "支付人数")
    private int memberPayNum;
    
    @Schema(description = "成交订单")
    private int orderCount;
    
    @Schema(description = "商品金额")
    private BigDecimal amto;
    
    @Schema(description = "配送费")
    private BigDecimal postage;
    
    @Schema(description = "优惠金额")
    private BigDecimal cardAmt;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "营收金额")
    private BigDecimal revenueAmt;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
