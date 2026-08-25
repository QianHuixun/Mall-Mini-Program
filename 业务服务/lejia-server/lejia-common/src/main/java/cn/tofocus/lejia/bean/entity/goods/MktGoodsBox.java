package cn.tofocus.lejia.bean.entity.goods;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 
 * 包厢商品和桌位的管理
 */
@Entity
@Data
@Table(name = "mkt_goods_box")
@FieldNameConstants(innerTypeName = "F")
public class MktGoodsBox implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_goods_box")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "对应桌位主键")
    private Integer desktop;
    
    @Schema(description = "对应桌位")
    private String desktopName;
    
    @Schema(description = "包厢门锁ID")
    private String lockId;
    
    @Schema(description = "中午场价格")
    private BigDecimal noonPrice;
    
    @Schema(description = "晚上场价格")
    private BigDecimal nightPrice;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
