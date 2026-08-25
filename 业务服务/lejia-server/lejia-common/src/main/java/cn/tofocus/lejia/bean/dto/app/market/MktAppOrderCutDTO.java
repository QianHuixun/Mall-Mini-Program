package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktAppOrderCutDTO {

	
	/**
	 * pkey
	 */
	@Schema(description = "pkey")
    private Integer pkey;
	/**
	 * pkey
	 */
	@Schema(description = "pkey1")
	private Integer pkey1;
	/**
	 * pkey
	 */
	@Schema(description = "pkey2")
	private Integer pkey2;
    
    /**
     * 推荐人
     */
    @Schema(description = "推荐人")
    private Integer tjr;

    /**
     * 购买者
     */
    @Schema(description = "购买者")
    private Integer member;
    private String memberName;
    private String memberPhoto;
    private Boolean isMember;
    
    /**
     * 购买者
     */
    @Schema(description = "订单号")
    private String code;

    
    /**
    * 订单价格
    */
	@Schema(description = "订单价格")
    private BigDecimal amto;
		
    /**
    * 总价
    */
	@Schema(description = "总价")
    private BigDecimal amtall;

    /**
    * 支付金额
    */
	@Schema(description = "支付金额")
    private BigDecimal amtn;

	/**
    * 商品
    */
	@Schema(description = "商品", required = true)
    private Integer goods;
	private String goodsName;
	
	/**
	 * 商品
	 */
	@Schema(description = "商品图片", required = true)
	private String photo;
	
	/**
    * 规格
    */
	@Schema(description = "规格", required = true)
    private Integer space;
	/**
	 * 规格
	 */
	@Schema(description = "规格名称", required = true)
	private String goodsSpaceName;
	
	/**
	 * 价格
	 */
	@Schema(description = "价格", required = true)
	private BigDecimal price;
	
	/**
    * 已经砍价多少
    */
	@Schema(description = "已经砍价多少")
    private BigDecimal cutAmt;
	@Schema(description = "还剩多少可以砍")
	private BigDecimal rCutAmt;
	@Schema(description = "剩余砍价时间")
	private Long endTime;
	@Schema(description = "已经砍价成功人数")
	private Integer cutSuccessNum;
	@Schema(description = "本人是否砍价")
	private Boolean isCut;

	private List<MktAppCutMemberDTO> cutMemberList;
	
    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
    private Date createdTime;
	
	
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
	

	
}
