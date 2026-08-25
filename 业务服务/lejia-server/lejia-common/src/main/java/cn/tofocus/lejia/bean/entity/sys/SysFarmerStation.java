package cn.tofocus.lejia.bean.entity.sys;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.v4.DeliveryDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_farmer_station")
public class SysFarmerStation implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "sys_farmer_station")
    @Schema(description = "pkey", hidden = true)
    private Integer pkey;
    
    @Schema(description = "市场", hidden = true)
    @Column(length = 50)
    @Size(max = 50)
    private String market;
    
    /**
     * 营业状态 营业/休息
     */
    @Schema(description = "营业状态 营业/休息", required = false)
    @Column(name = "y_status")
    private Boolean yStatus;
    
    /**
     * 营业时间起始
     */
    @Schema(description = "营业时间起始", required = false)
    @Column(length = 40)
    @Size(max = 40)
    private String yytb;
    
    /**
     * 营业时间结束
     */
    @Schema(description = "营业时间结束", required = false)
    @Column(length = 40)
    @Size(max = 40)
    private String yyte;
    
    @Schema(description = "省", required = false)
    @Column(length = 40)
    @Size(max = 40)
    private String prov;
    
    @Schema(description = "市", required = false)
    @Column(length = 40)
    @Size(max = 40)
    private String city;
    
    @Schema(description = "区", required = false)
    @Column(length = 40)
    @Size(max = 40)
    private String area;
    
    /**
     * 地址
     */
    @Schema(description = "地址", required = false)
    @Column(length = 200)
    @Size(max = 200)
    private String address;
    
    /**
     * 经度
     */
    @Schema(description = "经度", required = false)
    @Column(precision = 11, scale = 6)
    private BigDecimal longitude;
    
    /**
     * 纬度
     */
    @Schema(description = "纬度", required = false)
    @Column(precision = 11, scale = 6)
    private BigDecimal latitude;
    
    /**
     * 自提范围
     */
    @Schema(description = "自提范围", required = false)
    private Integer deliveryRange;
    
    /**
     * 自提时
     */
    @Schema(description = "自提时间小时", required = false)
    private Integer phour;
    
    /**
     * 自提分
     */
    @Schema(description = "自提时间分", required = false)
    private Integer pminute;
    
    @Schema(description = "自提日期", required = false)
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private DeliveryDate deliveryDate;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
