package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;

import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktAppGwcDTO {


	/**
    * pkey
    */
	@Schema(description = "pkey", required = true)
    private Integer pkey;

	/**
    * 用户
    */
	@Schema(description = "用户", required = true)
    private Integer member;

	/**
    * 商品
    */
	@Schema(description = "商品", required = true)
    private Integer goods;
	
	/**
	 * 商品名称
	 */
	@Schema(description = "商品名称", required = true)
	private String goodsName;
	
	/**
	 * 商品
	 */
	@Schema(description = "商品图片", required = true)
	private String photo;
	
	/**
	 * 商品属性ID
	 */
	@Schema(description = "商品类型", required = true)
	private MType mType;
	
	/**
	 * 商品属性描述
	 */
	@Schema(description = "商品类型描述", required = true)
	private String mTypeName;

	/**
    * 规格
    */
	@Schema(description = "规格", required = true)
    private Integer space;
	private String spaceName;

	
	/**
	 * 价格
	 */
	@Schema(description = "价格", required = true)
	private BigDecimal price;

	/**
    * num
    */
	@Schema(description = "num", required = true)
    private Integer num;
	
	private Integer vendor;
	private String verdorName;
	private String verdorMobile;
	private String verdorAddr;
    @Schema(description = "摊位号")
    private String booth;

	/**
    * 市场
    */
	@Schema(description = "市场", required = true)
    private String farmer;

	/**
    * 公司
    */
	@Schema(description = "公司", required = true)
    private String company;
}
