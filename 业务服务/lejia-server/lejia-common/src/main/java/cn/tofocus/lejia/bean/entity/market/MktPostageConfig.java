package cn.tofocus.lejia.bean.entity.market;

import io.swagger.v3.oas.annotations.media.Schema;
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
import lombok.Data;

/**
*  快递费
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_postage_config")
public class MktPostageConfig implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_postage_config")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 重量
    */
	@Schema(description = "重量")
    private BigDecimal weight;

    /**
    * 邮费
    */
	@Schema(description = "邮费")
    private BigDecimal postage;
	
	/**
	 * 快递公司pkey
	 */
	@Schema(description = "快递公司pkey")
	private Integer logistics;

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

    /**
    * 版本
    */
	@Schema(description = "版本")
	@Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;

    @Schema(description = "归属主键")
    private Integer ascription;
}