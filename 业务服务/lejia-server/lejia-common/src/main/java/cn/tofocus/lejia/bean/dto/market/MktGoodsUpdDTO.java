package cn.tofocus.lejia.bean.dto.market;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class MktGoodsUpdDTO
{
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "分类", required = true)
    private Integer gtype;
    
    @Schema(description = "商品库", required = true)
    private Integer goodsMain;
    
    @Schema(description = "三级分类")
    private Integer threeGtype;
    
    @Schema(description = "商户主键")
    private Integer vendor;

    @Schema(description = "供应商主键")
    private Integer supplier;

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

    @Size(max = 6, message = "标签长度不允许超过6个字")
    @Schema(description = "标签")
    private String tag;
    
    @Schema(description = "民生豆商品专用标签")
    private List<Integer> msdTags;
    
    @Schema(description = "民生商品是否全标签用户可见,true:全部可见")
    private Boolean msdTag;
    
    /**
    * 照片1
    */
    @Schema(description = "照片1")
    private List<String> photo1 = new ArrayList<String>();
    
    /**
    * 照片2
    */
    @Schema(description = "照片2")
    private String photo2;
    
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
    
    /**
     * 起售日期
     */
    @Schema(description = "起售日期", example = "2020-6-1", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    /**
     * 到期日期
     */
    @Schema(description = "到期日期", example = "2020-6-1", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "预售配送起售日期", example = "2020-06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date presaleStartDate;
    
    @Schema(description = "预售配送到期日期", example = "2020-06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date presaleEndDate;
    
    /**
     * 发货日期
     */
    @Schema(description = "发货日期", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
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
    
    @Schema(description = "是否加入热门商品 true 加入")
    private Boolean isPopular;
    
    @Schema(description = "礼品券兑换有效期 false:无")
    private Boolean expireChoose;
    
    @Schema(description = "礼品券兑换-使用市场", required = false)
    private String userFarmer;
    
    @Schema(description = "礼品券兑换-使用商户", required = false)
    private Integer userVendor;
    
    @Schema(description = "礼品券兑换有效期-开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date giftStartDate;
    
    @Schema(description = "礼品券兑换有效期-到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date giftEndDate;
    
    private Integer sort;
    
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
     * 规格
     */
    @Schema(description = "规格")
    private List<MktGoodsSpaceOnList> spaces;
    
    @Schema(description = "是否“猜我喜欢”")
    private Boolean guessLike;
    
    @Schema(description = "猜我喜欢-排序")
    private Integer guessSort;
    
    @Schema(description = "是否团购商品")
    private Boolean pickupType;
    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;

    @Schema(description = "标签主键")
    private List<Integer> tagKeys;

    @Size(max = 4, message = "仅允许最多4个卖点")
    @Schema(description = "卖点列表")
    private List<GoodsSellingPointDTO> sellingPoints;

    @Schema(description = "发送微信订阅消息")
    private boolean sendWechatMsg;
}
