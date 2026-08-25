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

/**
 *	抽奖配置 
 *
 */
@Entity
@Data
@Table(name = "mkt_draw_conf")
public class MktDrawConf implements HasPkey<Integer> 
{

	/**
	 * pkey
	 */
	@Id
	@AutoRedisID(domain = "zyysc", sequence = "mkt_draw_conf")
	@Schema(description = "pkey")
	private Integer pkey;

	/**
	 * 支付积分
	 */
	@Schema(description = "支付积分")
	private Integer point;

	/**
	 * 每日次数限制
	 */
	@Schema(description = "每日次数限制")
	private Integer limitNum;

	/**
	 * 启用标志
	 */
	@Schema(description = "启用标志")
	private Boolean enabled;

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
