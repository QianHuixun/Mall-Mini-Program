package cn.tofocus.lejia.bean.entity.market;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.WareType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  mkt_ware_line
* @author zdw 2020-09-25
*/

@Entity
@Data
@Table(name = "mkt_ware_line")
public class MktWareLine implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_ware_line")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "ware_type", required = true)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private WareType wareType;
    
    @Schema(description = "商品pkey", required = true)
    private Integer goods;
    
    @Schema(description = "商品名称", required = true)
    private String goodsName;
    
    @Schema(description = "规格pkey", required = true)
    private Integer space;
    
    @Schema(description = "规格名称", required = true)
    private String spaceName;
    
    @Schema(description = "批次号", required = false)
    private String orderNumber;
    
    @Schema(description = "采购价", required = false)
    private BigDecimal price;
    
    @Schema(description = "调整的数量", required = true)
    private Integer num;
    
    @Schema(description = "供应商", required = false)
    private String supplier;
    
    @Schema(description = "备注", required = false)
    private String remark;
    
    @Schema(description = "现库存数量", required = true)
    private Integer actualNum;
    
    @Schema(description = "建档员", required = false)
    @CreatedBy
    private Integer createdBy;
    
    @Schema(description = "建档时间", required = true)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}