package cn.tofocus.lejia.bean.dto.app.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.dto.goods.GoodsProcessOnInfo;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppGoodsV4OnList
{
    @Schema(hidden = true)
    private Integer pkey;
    
    @Schema(description = "分类")
    private Integer gtype;
    
    @Schema(hidden = true)
    private String gtypeName;
    
    @Schema(hidden = true)
    private String name;
    
    @Schema(description = "商品库")
    private Integer goodsMain;
    
    @Schema(description = "三级分类")
    private Integer threeGtype;
    
    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    private MType mType;
    
    @Schema(description = "标题")
    private String title;
    
    private Integer vendor;

    @Schema(description = "商户名称")
    private String vendorName;
    
    @Schema(description = "供应商主键")
    private Integer supplier;

    @Schema(description = "供应商名称")
    private String supplierName;
    
    @Schema(description = "照片1")
    private List<String> photo1 = new ArrayList<String>();
    
    @Schema(description = "列表小图")
    private String wrapperPhoto;
    
    public String getWrapperPhoto()
    {
        if (getPhoto1() == null) return "";
        return getPhoto1().size() > 0 ? getPhoto1().get(0) : "";
    }
    
    @Schema(description = "照片2")
    private String photo2;
    
    @Schema(description = "照片3")
    private String photo3;

    @Schema(description = "标准编号")
    private String serialNumber;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "正文")
    private List<String> content = new ArrayList<String>();
    
    @Schema(description = "起售日期", example = "2020-06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期", example = "2020-06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "起售日期-月日", example = "06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd", timezone = "GMT+8")
    private Date startDateMd;
    
    public Date getStartDateMd()
    {
        return getStartDate();
    }
    
    @Schema(description = "到期日期-月日", example = "06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd", timezone = "GMT+8")
    private Date endDateMd;
    
    public Date getEndDateMd()
    {
        return getEndDate();
    }
    
    @Schema(description = "发货日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date sendDate;
    
    @Schema(description = "浏览数量")
    private Integer viewCount;
    
    @Schema(description = "销售数量")
    private Integer xsNum;

    @Schema(description = "限购数量")
    private Integer purchaseNum;
    
    @Schema(description = "购物车商品数量")
    private Integer gwcNum = 0;
    
    @Schema(description = "是否免邮")
    private Boolean isPostage;
    
    private Integer sort;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "建档时间", hidden = true)
    private Date createdTime;
    
    @Schema(description = "规格")
    private List<MktGoodsSpaceOnList> spaces;
    
    @Schema(description = "库存,预售使用")
    private Integer kcNum;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    public BigDecimal getPrice()
    {
        if (getSpaces() != null && getSpaces().size() > 0)
        {
            if (mType != null && (mType.equals(MType.SHARE_GOODS) || mType.equals(MType.SPECIAL_GOODS)
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS) || mType.equals(MType.PRESALE_GOODS)))
                return getSpaces().get(getSpaces().size() - 1).getPrice();
            else
                return getSpaces().get(0).getPrice();
        }
        return price;
    }
    
    @Schema(description = "原价")
    private BigDecimal priceOld;
    
    public BigDecimal getPriceOld()
    {
        if (getSpaces() != null && getSpaces().size() > 0)
        {
            if (mType != null && (mType.equals(MType.SHARE_GOODS) || mType.equals(MType.SPECIAL_GOODS)
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS) || mType.equals(MType.PRESALE_GOODS)))
            {
                if (getSpaces().get(getSpaces().size() - 1).getPriceOld().compareTo(getPrice()) < 0) return getPrice();
                return getSpaces().get(getSpaces().size() - 1).getPriceOld();
            }
            else
            {
                if (getSpaces().get(0).getPriceOld().compareTo(getPrice()) < 0) return getPrice();
                return getSpaces().get(0).getPriceOld();
            }
        }
        return priceOld;
    }
    
    @Schema(description = "会员价")
    private BigDecimal priceMember;
    
    public BigDecimal getPriceMember()
    {
        if (getSpaces() != null && getSpaces().size() > 0)
        {
            if (mType != null && (mType.equals(MType.SHARE_GOODS) || mType.equals(MType.SPECIAL_GOODS)
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS) || mType.equals(MType.PRESALE_GOODS)))
                return getSpaces().get(getSpaces().size() - 1).getPriceMember();
            else
                return getSpaces().get(0).getPriceMember();
        }
        return priceMember;
    }
    
    @Schema(description = "佣金")
    private BigDecimal comm;
    
    public BigDecimal getComm()
    {
        if (getSpaces() != null && getSpaces().size() > 0)
        {
            if (mType != null && (mType.equals(MType.SHARE_GOODS) || mType.equals(MType.SPECIAL_GOODS)
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS) || mType.equals(MType.PRESALE_GOODS)))
                return getSpaces().get(getSpaces().size() - 1).getComm();
            else
                return getSpaces().get(0).getComm();
        }
        return comm;
    }
    
    @Schema(description = "积分")
    private Integer point;
    
    public Integer getPoint()
    {
        if (getSpaces() != null && getSpaces().size() > 0)
        {
            if (mType != null && (mType.equals(MType.SHARE_GOODS) || mType.equals(MType.SPECIAL_GOODS)
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS) || mType.equals(MType.PRESALE_GOODS)))
                return getSpaces().get(getSpaces().size() - 1).getPoint();
            else
                return getSpaces().get(0).getPoint();
        }
        return point;
    }
    
    @Schema(description = "是否可加工")
    private Boolean isProcess;
    
    @Schema(description = "加工可选项")
    private List<GoodsProcessOnInfo> processLines;
    
//    @Schema(description = "剩余时间")
//    private Long remainingTime;
//    
//    @Schema(description = "库存百分比")
//    private Integer kcNumPer;
    
//    @Schema(description = "是否收藏")
//    private boolean isCollection = false;
//    
//    @Schema(description = "收藏主键")
//    private Integer collectionPkey;
    
//    @Schema(description = "是否自提", required = false)
//    private Boolean pickupType;
}
