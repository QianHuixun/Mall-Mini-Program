package cn.tofocus.lejia.bean.dto.v2.gwc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 购物车
 *
 * @author zdw 2020-07-16
 */

@Data
public class GwcV2Info
{
    
    @Schema(description = "当前菜场购物车列表")
    private List<GwcGoodsOnList> currentFarmer = new ArrayList<>();
    
    @Schema(description = "积分商城购物车列表")
    private List<GwcGoodsOnList> pointsMall = new ArrayList<>();

    @Schema(description = "民生专区购物车列表")
    private List<GwcGoodsOnList> pointsMsd = new ArrayList<>();

    @Schema(description = "民生专区显示名称")
    private String integralMsdDisplayName;

    @Schema(description = "京东优选专区购物车列表")
    private List<GwcJdSkuOnList> jdGoodsList = new ArrayList<>();

    @Schema(description = "京东优选专区显示名称")
    private String jdGoodsDisplayName;
    
    public void setPoints(List<GwcGoodsOnList> list)
    {
        List<GwcGoodsOnList> pointsMall = new ArrayList<>();
        List<GwcGoodsOnList> pointsMsd = new ArrayList<>();
        for (GwcGoodsOnList item : list)
        {
            if (item.getMType() == MType.INTEGRAL_MSD_GOODS)
                pointsMsd.add(item);
            else
                pointsMall.add(item);
        }
        this.pointsMall = pointsMall;
        this.pointsMsd = pointsMsd;
    }
    
    @Schema(description = "当前购物车总数量")
    public Integer getTotal()
    {
        return currentFarmer.size() + pointsMall.size();
    }
    
    @Schema(description = "包邮金额")
    private BigDecimal freeDelivery;
    
    public String getFreeDelivery()
    {
        if(freeDelivery != null)
        {
            return freeDelivery.toString();
        }
        return null;
    }
    
    @Schema(description = "是否免运费")
    private Boolean isFree = false;
    
    @Schema(description = "起步价")
    private BigDecimal startingPrice;
    
    public String getStartingPrice()
    {
        if(startingPrice != null)
        {
            return startingPrice.toString();
        }
        return null;
    }
    
    @Schema(description = "满减运费1")
    private BigDecimal reachOne;
    
    @Schema(description = "满减运费2")
    private BigDecimal reachTwo;
    
    @Schema(description = "减少运费1")
    private BigDecimal reductionDeliveryOne;
    
    @Schema(description = "减少运费2")
    private BigDecimal reductionDeliveryTwo;
    
    @Schema(description = "是否减少运费1")
    private Boolean isReductionOne;
    
    @Schema(description = "是否减少运费2")
    private Boolean isReductionTwo;
}
