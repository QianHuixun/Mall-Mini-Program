package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "mkt_ori_ven")
public class MktOriVen implements HasPkey<Integer> 
{
	/**
	 * pkey
	 */
	@Id
	@AutoRedisID(domain = "zyysc", sequence = "mkt_ori_ven")
	@Schema(description = "pkey")
	private Integer pkey;

	/**
	 * 溯源商户
	 */
	@Schema(description = "溯源商户")
	private String merchant;
	
	/**
	 * 溯源商品
	 */
	@Schema(description = "溯源商品")
	private String goods;

	/**
	 * 供应商
	 */
	@Schema(description = "供应商")
	private String vendor;

	/**
	 * 进货日期
	 */
	@Schema(description = "进货日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date oriDate;

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
