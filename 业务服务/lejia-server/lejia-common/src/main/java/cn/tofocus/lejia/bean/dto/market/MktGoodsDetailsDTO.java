package cn.tofocus.lejia.bean.dto.market;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Sort;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class MktGoodsDetailsDTO 
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
	
    @Schema(description = "商户主键")
    private Integer vendor;
	
	@Schema(description = "商户名称")
	private String vendorName;

	@Schema(description = "供应商主键")
	private Integer supplier;

	@Schema(description = "供应商名称")
	private String supplierName;
	
	@Schema(description = "摊位号")
	private String booth;
	
	/**
    * 商品库
    */
	@Schema(description = "商品库", required = true)
	private Integer goodsMain;
	
    @Schema(description = "三级分类")
    private Integer threeGtype;
    
    private String threeGtypeName;
	
	/**
    * 商品属性：积分/市场/会员/特价/分享/砍价/团购/预售
    */
	@Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售", required = true)
    private MType mType;
	
    @Schema(description = "是否可加工")
    private Boolean isProcess;
    
    /**
    * 标题
    */
	@Schema(description = "标题", required = true)
    private String title;
    
    @Size(max = 6, message = "标签长度不允许超过6个字")
    @Schema(description = "标签")
    private String tag;

    @Schema(description = "民生豆商品专用标签")
    private List<Integer> msdTags;
    
    /**
    * 照片1
    */
	@Schema(description = "照片1")
    private List<String> photo1 = new ArrayList<String>();

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

	private String content2;
	
	@Schema(description = "起售日期", example = "2020-06-01", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date startDate;
	
	@Schema(description = "到期日期", example = "2020-06-01", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date endDate;
	
	@Schema(description = "预售配送起售日期", example = "2020-06-01", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date presaleStartDate;
	
	@Schema(description = "预售配送到期日期", example = "2020-06-01", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date presaleEndDate;
	
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
	
    @Schema(description = "限购数量")
    private Integer purchaseNum;
    
    /**
     * 扩展内容  砍价、团购使用
     */
    @Schema(description = "扩展内容  砍价、团购使用")
    private String extendCon;
    private List<String> extendConList;
    
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

	@Schema(description = "礼品券兑换有效期 false:无")
    private Boolean expireChoose;
	
    @Schema(description = "礼品券兑换-使用市场")
    private String userFarmer;
    private String userFarmerName;
    
    @Schema(description = "礼品券兑换-使用商户")
    private Integer userVendor;
    private String userVendorName;
    
    @Schema(description = "礼品券兑换有效期-开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date giftStartDate;
    
    @Schema(description = "礼品券兑换有效期-到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date giftEndDate;
	
	private Integer sort;
	
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

	@Schema(description = "是否“猜我喜欢”")
	private Boolean guessLike;
	
    @Schema(description = "猜我喜欢-排序")
    private Integer guessSort;
	
	@Schema(description = "是否自提")
	private Boolean pickupType;
    
    @Schema(description = "是否专区推荐")
    private Boolean zoneRecommend;
	
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Schema(description = "标签主键")
    private List<Integer> tagKeys;

	@Schema(description = "已推荐商品数量")
	private Long recommendNum;
    
    @Size(max = 4, message = "卖点不允许超过4个")
    @Schema(description = "卖点列表")
    @JoinDTO(dataQuery = "mktGoodsSellingPointDao", referencedName = "goods", sort = "pkey", sortDirection = Sort.Direction.ASC, type = GoodsSellingPointDTO.class)
    private List<GoodsSellingPointDTO> sellingPoints;
    
    @Schema(description = "发送微信订阅消息")
    private boolean sendWechatMsg;
}
