package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.lejia.bean.enums.OrderGroupStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  团购记录
* @author zdw 2020-08-06
*/

@Entity
@Data
@Table(name="mkt_order_group")
public class MktOrderGroup implements HasPkey<Integer> 
{
   


	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_order_group")
	@Schema(description = "pkey", required = true)
    private Integer pkey;

	/**
    * goods
    */
	@Schema(description = "goods", required = true)
    private Integer goods;

	/**
    * 团购组
    */
	@Schema(description = "团购组", required = true)
    private Integer groupId;

	/**
    * 状态 未成团/已成团
    */
	@Schema(description = "状态 未成团/已成团", required = true)
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private OrderGroupStatus status;

	/**
    * 当前采购数
    */
	@Schema(description = "当前采购数", required = true)
    private Integer buyNum;

	/**
    * 成团采购数
    */
	@Schema(description = "成团采购数", required = true)
    private Integer groupNum;

	/**
    * 到期日期
    */
	@Schema(description = "到期日期", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date endDate;

	/**
    * 订单号组
    */
	@Schema(description = "订单号组", required = true)
    @Convert(converter = ListConverter.class)
    @ListStrLength(length = 1000)
    private List<String> orderList;

	/**
    * 最后更新时间
    */
	@Schema(description = "最后更新时间", required = true)
	@LastModifiedDate
    private Date updateTime;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间", required = true)
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;
}