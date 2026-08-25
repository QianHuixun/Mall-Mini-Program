package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import cn.tofocus.lejia.bean.enums.OrderGroupStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOrderGroupOnList 
{
	
	/**
	 * pkey
	 */
	@Schema(description = "pkey", required = true)
    private Integer pkey;

	/**
    * goods
    */
	@Schema(description = "goods", required = true)
    private Integer goods;
	private String goodsName;

	/**
    * 团购组
    */
	@Schema(description = "团购组", required = true)
    private Integer groupId;

	/**
    * 状态 未成团/已成团
    */
	@Schema(description = "状态 未成团/已成团", required = true)
    private OrderGroupStatus status;
	private String statusName;

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
    * 建档时间
    */
	@Schema(description = "建档时间", required = true)
    private Date createdTime;
}
