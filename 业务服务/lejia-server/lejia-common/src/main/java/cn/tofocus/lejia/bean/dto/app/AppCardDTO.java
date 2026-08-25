package cn.tofocus.lejia.bean.dto.app;

import cn.tofocus.lejia.bean.enums.CardType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AppCardDTO 
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey")
	private Integer pkey;

	/**
	 * 标题
	 */
	@Schema(description = "标题")
	private String title;

	/**
	 * 价值
	 */
	@Schema(description = "价值")
	private BigDecimal cost;
	
	/**
	 * 最低消费
	 */
	@Schema(description = "最低消费")
	private BigDecimal limitCost;
	
	@Schema(description = "到期选择, true 为 多少天后到期   false 为 指定到期日期", required = true)
	private Boolean expireChoose;
	
	/**
	 * 有效期(天)
	 */
	@Schema(description = "有效期(天)")
	private Integer effective;
	
	/**
	 * 开始日期
	 */
	@Schema(description = "开始日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM月dd日" , timezone = "GMT+8")
	private Date startDate;
	
	/**
	 * 到期日期
	 */
	@Schema(description = "到期日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM月dd日" , timezone = "GMT+8")
	private Date endDate;
	
	@Schema(description = "有效期")
	private String effectiveDate;
	
	/**
	 * 介绍
	 */
	@Schema(description = "介绍")
	private String content;
	private Boolean isReceive = false;
	/**
	 * 使用市场
	 */
	@Schema(description = "使用市场")
	private String userFarmer;
	
	/**
	 * 使用分类
	 */
	@Schema(description = "使用分类")
	private Integer userType;

	@Schema(description = "使用分类名称")
	private String userTypeName = "";

	/**
	 * 使用商品
	 */
	@Schema(description = "使用商品")
	private Integer userGoods;

	@Schema(description = "使用商品中文")
	private String userGoodsName = "";

	/**
	 * 领取方式 手动发放/二维码自领/所有
	 */
	@Schema(description = "领取方式")
	private CardType cardType;
	
	/**
	 * 领券码
	 */
	@Schema(description = "领券码")
	private String cardCode;

	@Schema(description = "优惠券数量")
	private Integer count;
	
    @Schema(description = "免邮费")
    private Boolean avoidPostage;
}
