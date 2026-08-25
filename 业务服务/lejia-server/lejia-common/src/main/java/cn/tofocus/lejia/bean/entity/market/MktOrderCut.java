package cn.tofocus.lejia.bean.entity.market;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 砍价记录
 * 
 * @author zdw 2020-08-06
 */

@Entity
@Data
@Table(name = "mkt_order_cut")
public class MktOrderCut implements HasPkey<Integer> 
{

	/**
	 * pkey
	 */
	@Id
	@AutoRedisID(domain = "zyysc", sequence = "mkt_order_cut")
	@Schema(description = "pkey", required = true)
	private Integer pkey;

	/**
	 * 会员
	 */
	@Schema(description = "会员", required = true)
	private Integer memberPkey;

	/**
	 * 订单
	 */
	@Schema(description = "订单", required = true)
	private Integer orderPkey;

	/**
	 * 砍价金额
	 */
	@Schema(description = "砍价金额", required = true)
	private BigDecimal cutAmt;

	/**
	 * 到期日期
	 */
	@Schema(description = "到期日期", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date endDate;

	/**
	 * 建档时间
	 */
	@Schema(description = "建档时间", required = true)
	@CreatedDate
	private Date createdTime;
	
    @Schema(description = "归属主键")
    private Integer ascription;
}