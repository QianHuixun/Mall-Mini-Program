package cn.tofocus.lejia.bean.dto.market;

import java.util.List;

import cn.tofocus.lejia.bean.enums.PrizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktDrawPrizeOnList {

	/**
	 * pkey
	 */
	@Schema(description = "pkey")
	private Integer pkey;

	/**
	 * 礼品类型 积分/优惠券/礼品券/礼品/
	 */
	@Schema(description = "礼品类型 积分/优惠券/礼品券/礼品/", required = true)
	private PrizeType pType;

	@Schema(description = "商品名称")
	private String name;
	
	/**
	 * 中奖概率（%）
	 */
	@Schema(description = "中奖概率", required = true)
	private Integer probability;

	/**
	 * 图片
	 */
	@Schema(description = "图片", required = true)
	private List<String> photo;

	/**
	 * 奖品值
	 */
	@Schema(description = "奖品值")
	private Integer pvalue;

	/**
	 * 中奖描述
	 */
	@Schema(description = "中奖描述", required = true)
	private String descp;

	/**
	 * 排序
	 */
//	@Schema(description = "排序", hidden = true)
//	private Integer sort;

	/**
	 * 启用标志
	 */
//	@Schema(description = "启用标志", hidden = true)
//	private Integer enabled;
}
