package cn.tofocus.lejia.bean.entity.market;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2024/11/28]
 */
@Data
@Entity
@Schema(description = "供应商自提点")
@Table(name = "mkt_supplier_pickup_location")
@FieldNameConstants(innerTypeName = "F")
public class MktSupplierPickupLocation implements HasPkey<Integer>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_supplier_pickup_location")
    private Integer pkey;
    
    @Schema(description = "供应商主键")
    @Column
    private Integer supplier;
    
    @Schema(description = "自提点地址")
    @Column(length = 200)
    private String address;
    
    @Schema(description = "经度")
    @Column(precision = 11, scale = 6)
    private BigDecimal longitude;
    
    @Schema(description = "纬度")
    @Column(precision = 11, scale = 6)
    private BigDecimal latitude;
    
    @Schema(description = "修改时间")
    @LastModifiedDate
    @Column
    private Date updateTime;
    
    @Schema(description = "建档时间")
    @CreatedDate
    @Column
    private Date createdTime;
    
    @Schema(description = "建档员")
    @CreatedBy
    @Column
    private Integer updateBy;
    
    @Schema(description = "归属主键")
    @Column
    private Integer ascription;
}
