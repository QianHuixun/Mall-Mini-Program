package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.OrderGroupStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class AppGoodsCollageDTO
{
	/**
	 * pkey
	 */
	@Schema(hidden = true)
    private Integer pkey;

    /**
    * 分类
    */
	@Schema(description = "分类", required = true)
    private Integer gtype;
	
	/**
	 * 分类名称
	 */
	@Schema(hidden = true)
	private String gtypeName;
	
	/**
	 * 商品名称 新增的时候 不需要传
	 */
	@Schema(hidden = true)
	private String name;
	
	/**
    * 商品库
    */
	@Schema(description = "商品库", required = true)
	private Integer goodsMain;
	
	/**
    * 商品属性：积分/市场/会员/特价/分享/砍价/团购/预售
    */
	@Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售", required = true)
    private MType mType;
	
    /**
    * 标题
    */
	@Schema(description = "标题", required = true)
    private String title;

    /**
    * 照片1
    */
	@Schema(description = "照片1")
    private List<String> photo1 = new ArrayList<String>();

    /**
     * 列表小图
     */
    @Schema(description = "列表小图")
    private String wrapperPhoto;
    
    /**
    * 照片2
    */
	@Schema(description = "照片2")
    private String photo2 ;

	/**
    * 照片2
    */
	@Schema(description = "照片3")
    private String photo3;
		
    /**
    * 标准编号
    */
	@Schema(description = "标准编号")
    private String serialNumber;

    /**
    * 描述
    */
	@Schema(description = "描述")
    private String description;

    /**
    * 正文
    */
	@Schema(description = "正文")
    private List<String> content = new ArrayList<String>();

	/**
	 * 起售日期
	 */
	@Schema(description = "起售日期", example = "2020-06-01", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date startDate;
	
	/**
	 * 到期日期
	 */
	@Schema(description = "到期日期", example = "2020-06-01", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date endDate;
	
	/**
	 * 发货日期
	 */
	@Schema(description = "发货日期", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date sendDate;
	
	/**
	 * 浏览数量
	 */
	@Schema(description = "浏览数量", required = true)
	private Integer viewCount;
	
	/**
	* 销售数量
	*/
	@Schema(description = "销售数量", required = true)
	private Integer xsNum;
	
	/**
	* 是否免邮
	*/
	@Schema(description = "是否免邮", required = true)
	private Boolean isPostage;
	
    /**
    * 备注
    */
	@Schema(description = "备注")
    private String remark;

    /**
    * 启用标志
    */
	@Schema(description = "启用标志", required = true)
    private Boolean enabled;

    /**
    * 建档时间
    */
	@Schema(description = "建档时间", hidden = true)
    private Date createdTime;
	
	/**
	 * 规格
	 */
	@Schema(description = "规格")
	private List<MktGoodsSpaceOnList> spaces;
	@Schema(description = "实付金额")
	private BigDecimal amtn;
	/**
	 * 剩余时间
	 */
	@Schema(description = "剩余时间")
	private Long remainingTime;
	/**
    * 当前采购数
    */
	@Schema(description = "当前采购数", required = true)
    private Integer buyNum;
	private String farmerName;
	/**
    * 成团采购数
    */
	@Schema(description = "成团采购数", required = true)
    private Integer groupNum;
	@Schema(description = "订单号组", required = true)
	private List<String> orderList;
	@Schema(description = "剩余成团人数")
	private Integer remainingGroupNum;
    private OrderGroupStatus status;
    private String statusName;
}
