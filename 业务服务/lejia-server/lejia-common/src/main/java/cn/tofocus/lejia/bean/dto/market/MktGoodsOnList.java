package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktGoodsOnList 
{
	
	/**
	 * pkey
	 */
    private Integer pkey;

    /**
    * 分类
    */
	@Schema(description = "分类")
    private Integer gtype;
	
	/**
	* 商品名称 新增的时候 不需要传
	*/
	@Schema(hidden = true)
	private String name;
	
	/**
    * 商品属性：积分/市场/会员/特价/分享/砍价/团购/预售
    */
	@Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    private MType mType;
	
    /**
    * 标题
    */
	@Schema(description = "标题")
    private String title;

		
    /**
    * 标准编号
    */
	@Schema(description = "标准编号")
    private String number;

    /**
    * 描述
    */
	@Schema(description = "描述")
    private String description;

	/**
	 * 浏览数量
	 */
	@Schema(description = "浏览数量")
	private Integer viewCount;
	
	/**
	* 是否免邮
	*/
	@Schema(description = "是否免邮", required = true)
	private Boolean isPostage;
	
	/**
	* 销售数量
	*/
	@Schema(description = "销售数量")
	private Integer xsNum;
	
    /**
    * 启用标志
    */
	@Schema(description = "启用标志")
    private Boolean enabled;
	
    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
    private Date createdTime;
	
}
