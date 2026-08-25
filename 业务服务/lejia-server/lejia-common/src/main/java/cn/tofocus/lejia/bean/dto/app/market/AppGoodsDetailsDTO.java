package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.dto.goods.GoodsProcessOnInfo;
import cn.tofocus.lejia.bean.dto.market.GoodsSellingPointDTO;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppGoodsDetailsDTO
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
    
    public String getMTypeName()
    {
        if (mType != null)
            return mType.getName();
        return "";
    }
    
    @Schema(description = "是否可加工")
    private Boolean isProcess;
    
    @Schema(description = "加工可选项")
    private List<GoodsProcessOnInfo> processLines;
    
    @Schema(description = "商户")
    private Integer vendor;
    
    @Schema(description = "商户名称")
    private String vendorName;
    
    @Schema(description = "商户摊位号")
    private String vendorBooth;
    
    @Schema(description = "商户头像")
    private String vendorHeadIcon;
    
    @Schema(description = "商户在售数量")
    private Long vendorGoodsNum;
    
    @Schema(description = "供应商名称")
    private String supplierName;
    
    /**
    * 标题
    */
    @Schema(description = "标题", required = true)
    private String title;
    
    @Schema(description = "标签")
    private String tag;
    
    @Schema(description = "卖点列表")
    private List<GoodsSellingPointDTO> sellingPoints;
    
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
    @Schema(description = "起售日期", example = "2020-06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    /**
     * 到期日期
     */
    @Schema(description = "到期日期", example = "2020-06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
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
    
    /**
    * 是否免邮
    */
    @Schema(description = "是否免邮", required = true)
    private Boolean isPostage;
    
    private Boolean isPresale;
    
    /**
    * 备注
    */
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "扩展内容  砍价、团购使用")
    private String extendCon;
    
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
    
    @Schema(description = "已经砍价多少")
    private BigDecimal cutAmt;
    
    @Schema(description = "还剩多少可以砍")
    private BigDecimal rCutAmt;
    
    @Schema(description = "是否已经参与该商品的砍价  true是已经参加")
    private Boolean isCut = false;
    
    @Schema(description = "购物车商品数量")
    private Integer gwcNum = 0;
    
    /**
     * 是否收藏
     */
    @Schema(description = "是否收藏", required = true)
    private boolean isCollection = false;
    
    /**
     * 收藏主键
     */
    @Schema(description = "收藏主键", required = true)
    private Integer collectionPkey;
    
    /**
     * 剩余时间
     */
    @Schema(description = "剩余时间")
    private Long remainingTime;
    
    @Schema(description = "起步价")
    private BigDecimal startingPrice;
    
    @Schema(description = "预售配送起售日期", example = "2020-06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date presaleStartDate;
    
    @Schema(description = "预售配送到期日期", example = "2020-06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date presaleEndDate;
    
    /**
     * 相关菜谱列表
     */
    @Schema(description = "相关菜谱列表")
    private List<MktCookfdAppOnList> cookfdList;
    
}
