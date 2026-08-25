package cn.tofocus.lejia.bean.dto.market;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.lejia.bean.enums.MType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import javax.persistence.Column;

/**
 * 商品供应库分页列表
 * 外层信息通过mktGoods查询
 */
@Data
@Schema(description = "商品供应库分页列表")
public class MktSupplyOnList
{
    /**
     * 市场pkey farmer
     */
    @Schema(description = "市场pkey")
    private String farmer;

    /**
     * 市场名称
     */
    @Schema(description = "市场名称")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "farmer")
    private String farmerName;

    
    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    public String getMTypeName()
    {
        if(mType != null)
        {
            if(mType.getIndex() == 1 || mType.getIndex() == 2)
                return "市场商品";
            else
                return mType.getName() + "商品";
        }
        return "";
    }
    @JsonIgnore
    private MType mType;
    
    /**
     * 商品pkey
     */
    @Schema(description = "商品pkey")
    @JsonIgnore
    private Integer pkey;

    /**
     * 商品pkey（显示用）
     */
    @Schema(description = "商品pkey")
    private Integer goodsPkey;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String title;

    /**
     * 标准编号
     */
    @Schema(description = "标准编号（页面显示“商品ID”）")
    private String serialNumber;

    /**
     * 商品分类
     */
    @Schema(description = "商品分类pkey")
    private Integer gtype;

    /**
     * 商品分类名称
     */
    @Schema(description = "商品分类名称")
    @JoinDTO(dataQuery = "mktGtypeDao", from = "gtype")
    private String gtypeName;

    /**
     * 商品供应信息
     */
    @Schema(description = "商品供应信息")
    @JoinDTO(dataQuery = "mktSupplyDao", referencedName = "good", type = MktSupplyPageDetail.class, cascade = true)
    private List<MktSupplyPageDetail> details;
	
    @Schema(description = "是否可修改,true:可以修改")
    private Boolean vendorShopping;
}
