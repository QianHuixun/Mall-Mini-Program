package cn.tofocus.lejia.bean.entity.market;

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
@Schema(description = "供应商")
@Table(name = "mkt_supplier")
@FieldNameConstants(innerTypeName = "F")
public class MktSupplier implements HasPkey<Integer>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_supplier")
    private Integer pkey;
    
    @Schema(description = "供应商名称")
    @Column(length = 100)
    private String name;
    
    @Schema(description = "手机号码")
    @Column(length = 20)
    private String mobile;
    
    @Schema(description = "开始营业时间")
    @Column(length = 10)
    private String startBusinessTime;
    
    @Schema(description = "结束营业时间")
    @Column(length = 10)
    private String endBusinessTime;
    
    @Schema(description = "是否支持自提")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean allowedPickup;
    
    @Schema(description = "是否支持配送")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean allowedDelivery;
    
    @Schema(description = "快递寄件人")
    @Column(length = 20)
    private String expressSender;
    
    @Schema(description = "快递寄件手机号")
    @Column(length = 20)
    private String expressMobile;
    
    @Schema(description = "快递寄件省")
    @Column(length = 50)
    private String expressPro;
    
    @Schema(description = "快递寄件市")
    @Column(length = 50)
    private String expressCity;
    
    @Schema(description = "快递寄件区")
    @Column(length = 50)
    private String expressArea;
    
    @Schema(description = "快递寄件地址")
    @Column(length = 100)
    private String expressAddress;
    
    @Schema(description = "顺丰月结卡号")
    @Column(length = 20)
    private String sfMonthlyCard;
    
    @Schema(description = "顺丰寄件appId")
    @Column(length = 20)
    private String sfAppId;

    @Schema(description = "顺丰寄件sk")
    @Column(length = 50)
    private String sfSk;

    @Schema(description = "openid1")
    @Column(length = 40)
    private String openid1;
    
    @Schema(description = "启用标志")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean enabled;
    
    @Schema(description = "是否已删除")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean isDel;
    
    @Schema(description = "市场")
    @Column(length = 40)
    private String farmer;
    
    @Schema(description = "公司")
    @Column(length = 40)
    private String company;
    
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
