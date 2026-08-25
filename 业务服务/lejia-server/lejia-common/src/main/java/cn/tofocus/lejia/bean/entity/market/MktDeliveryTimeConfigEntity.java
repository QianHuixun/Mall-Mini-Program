package cn.tofocus.lejia.bean.entity.market;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 
 * 预计送达时间配置
 * 
 * @author  MSI_NB
 * @version  [版本号, 2024-4-15]
 */
@Entity
@Data
@Table(name="mkt_delivery_time_config")
@FieldNameConstants(innerTypeName = "F")
public class MktDeliveryTimeConfigEntity implements HasPkey<String>
{
    @Id
    @Schema(description = "pkey")
    private String pkey;

    /**
    * 距离
    */
    @Schema(description = "距离")
    private BigDecimal distance;

    /**
    * 小时
    */
    @Schema(description = "小时")
    private Integer hour;
    /**
    * 分钟
    */
    @Schema(description = "分钟")
    private Integer minute;
    
    /**
    * 市场
    */
    @Schema(description = "市场")
    private String farmer;

    /**
    * 公司
    */
    @Schema(description = "公司")
    private String company;

    /**
    * 最后更新时间
    */
    @Schema(description = "最后更新时间")
    @LastModifiedDate
    private Date updateTime;

    /**
    * 建档时间
    */
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;

    /**
    * 建档员
    */
    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;

    @Schema(description = "归属主键")
    private Integer ascription;
}
