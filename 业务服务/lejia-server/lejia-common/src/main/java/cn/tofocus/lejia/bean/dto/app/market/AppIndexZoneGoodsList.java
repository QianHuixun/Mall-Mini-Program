package cn.tofocus.lejia.bean.dto.app.market;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppIndexZoneGoodsList
{
    @Schema(description = "特价商品专区商品列表")
    private List<AppGoodsAppOnList> specialList;
    
    @Schema(description = "积分商城专区商品列表")
    private List<AppGoodsAppOnList> integralList;
    
    @Schema(description = "预售专区商品列表")
    private List<AppGoodsAppOnList> integralPresaleList;
    
    @Schema(description = "滨农优品专区商品列表")
    private List<AppGoodsAppOnList> integralBNYPList;
    
    @Schema(description = "民生豆专区商品列表")
    private List<AppGoodsAppOnList> integralMsdList;
}
