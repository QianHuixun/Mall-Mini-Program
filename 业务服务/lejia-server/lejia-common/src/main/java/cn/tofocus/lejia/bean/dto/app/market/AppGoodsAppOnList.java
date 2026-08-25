package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.enums.MType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

@Data
public class AppGoodsAppOnList {
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

    @Schema(description = "标签")
    private String tag;

    @Schema(description = "卖点列表")
    private List<String> sellingPoints;

    /**
     * 照片1
     */
    @Schema(description = "照片1")
    private List<String> photo1 = new ArrayList<String>();

//    public List<String> getPhoto1()
//    {
//        List<String> photo1new = new ArrayList<String>();
//        if(!photo1.isEmpty())
//        {
//            for(String s : photo1)
//            {
//                photo1new.add(s + "&thumb=small");
//            }
//            return photo1new;
//        }
//        return photo1;
//    }
    
    /**
     * 列表小图
     */
    @Schema(description = "列表小图")
    private String wrapperPhoto;

    public String getWrapperPhoto() {
    	if(getPhoto1() == null)
    		return "";
        return getPhoto1().size() > 0 ? getPhoto1().get(0): "";
    }

    /**
     * 照片2
     */
    @Schema(description = "照片2")
    private String photo2;

//    public String getPhoto2()
//    {
//        if(StringUtils.isNotBlank(photo2))
//            return photo2 + "&thumb=small";
//        return photo2;
//    }
    
    
    /**
     * 照片3
     */
    @Schema(description = "照片3")
    private String photo3;
//    public String getPhoto3()
//    {
//        if(StringUtils.isNotBlank(photo3))
//            return photo3 + "&thumb=small";
//        return photo3;
//    }

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    /**
     * 到期日期
     */
    @Schema(description = "到期日期", example = "2020-06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    /**
     * 起售日期-月日
     */
    @Schema(description = "起售日期-月日", example = "06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd", timezone = "GMT+8")
    private Date startDateMd;

    public Date getStartDateMd() {
        return getStartDate();
    }

    /**
     * 到期日期-月日
     */
    @Schema(description = "到期日期-月日", example = "06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd", timezone = "GMT+8")
    private Date endDateMd;

    public Date getEndDateMd() {
        return getEndDate();
    }

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
     * 扩展内容  砍价、团购使用
     */
    @Schema(description = "扩展内容  砍价、团购使用")
    private String extendCon;
    @Schema(description = "已经成功砍价人数")
    private Integer cutMemberNum;
    @Schema(description = "是否已经参与该商品的砍价  true是已经参加")
    private Boolean isCut;
    @Schema(description = "购物车商品数量")
    private Integer gwcNum = 0;
    /**
     * 是否免邮
     */
    @Schema(description = "是否免邮", required = true)
    private Boolean isPostage;
    private Integer sort;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

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

//    @Schema(description = "库存,预售使用")
//    public Integer getKcNum()
//    {
//        if(spaces != null && !spaces.isEmpty())
//        {
//            Integer kcNum = 0;
//            for(MktGoodsSpaceOnList gs : spaces)
//            {
//                kcNum = kcNum + gs.getKcNum();
//            }
//            return kcNum;
//        }
//        return 0;
//    }
    @Schema(description = "库存,预售使用")
    private Integer kcNum;
    
    /**
     * 价格
     */
    @Schema(description = "价格")
    private BigDecimal price;

    public BigDecimal getPrice() {
        if (getSpaces() != null && getSpaces().size() > 0) {
            if(mType != null && (mType.equals(MType.SHARE_GOODS)
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS)
                || mType.equals(MType.PRESALE_GOODS)
                ))
                return getSpaces().get(getSpaces().size() - 1).getPrice();
            else
                return getSpaces().get(0).getPrice();
        }
        return price;
    }


    /**
     * 原价
     */
    @Schema(description = "原价")
    private BigDecimal priceOld;

    public BigDecimal getPriceOld() {
        if (getSpaces() != null && getSpaces().size() > 0) {
            if(mType != null && (mType.equals(MType.SHARE_GOODS) 
                || mType.equals(MType.SPECIAL_GOODS) 
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS)
                || mType.equals(MType.PRESALE_GOODS)
                ))
            {
                if(getSpaces().get(getSpaces().size() - 1).getPriceOld().compareTo(getPrice()) < 0)
                    return getPrice();
                return getSpaces().get(getSpaces().size() - 1).getPriceOld();
            }
            else
            {
                if(getSpaces().get(0).getPriceOld().compareTo(getPrice()) < 0)
                    return getPrice();
                return getSpaces().get(0).getPriceOld();
            }
        } 
        return priceOld;
    }

	/**
	 * 会员价
	 */
	@Schema(description = "会员价", required = true)
	private BigDecimal priceMember;
	
	public BigDecimal getPriceMember() {
	    if (getSpaces() != null && getSpaces().size() > 0) {
	        if(mType != null && (mType.equals(MType.SHARE_GOODS) 
                || mType.equals(MType.SPECIAL_GOODS) 
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS)
                || mType.equals(MType.PRESALE_GOODS)
                ))
                return getSpaces().get(getSpaces().size() - 1).getPriceMember();
            else
                return getSpaces().get(0).getPriceMember();
        }
        return priceMember;
    }
    
    /**
     * 佣金
     */
    @Schema(description = "佣金")
    private BigDecimal comm;

    public BigDecimal getComm() {
        if (getSpaces() != null && getSpaces().size() > 0) {
            if(mType != null && (mType.equals(MType.SHARE_GOODS) 
                || mType.equals(MType.SPECIAL_GOODS) 
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS)
                || mType.equals(MType.PRESALE_GOODS)
                ))
                return getSpaces().get(getSpaces().size() - 1).getComm();
            else
                return getSpaces().get(0).getComm();
        }
        return comm;
    }

    /**
     * 积分
     */
    @Schema(description = "积分", required = true)
    private Integer point;

    public Integer getPoint() {
        if (getSpaces() != null && getSpaces().size() > 0) {
            if(mType != null && (mType.equals(MType.SHARE_GOODS) 
                || mType.equals(MType.SPECIAL_GOODS) 
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS)
                || mType.equals(MType.PRESALE_GOODS)
                ))
                return getSpaces().get(getSpaces().size() - 1).getPoint();
            else
                return getSpaces().get(0).getPoint();
        }
        return point;
    }

    /**
     * 剩余时间
     */
    @Schema(description = "剩余时间")
    private Long remainingTime;
    
    /**
     * 库存百分比
     */
    @Schema(description = "库存百分比")
    private Integer kcNumPer;

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
    
    
    @Schema(description = "是否自提", required = false)
     private Boolean pickupType;
}
