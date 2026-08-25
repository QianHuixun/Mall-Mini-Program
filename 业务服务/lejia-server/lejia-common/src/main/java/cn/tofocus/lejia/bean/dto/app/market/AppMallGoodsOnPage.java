package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.List;

import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商城商品分页DTO（mkt_goods 与 京东商品统一）")
public class AppMallGoodsOnPage
{
    @Schema(description = "商品pkey：商城为 mkt_goods.pkey，京东为 skuid")
    private Long pkey;

    @Schema(description = "来源：MALL/JD")
    private String source;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "商品标签")
    private String tag;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal priceOld;

    @Schema(description = "积分")
    private Integer point;

    @Schema(description = "照片")
    private List<String> photo1;

    @Schema(description = "列表小图")
    private String wrapperPhoto;

    @Schema(description = "规格列表")
    private List<MktGoodsSpaceOnList> spaces;

    @Schema(description = "卖点列表")
    private List<String> sellingPoints;

    @Schema(description = "销量")
    private Integer xsNum;

    @Schema(description = "购物车数量")
    private Integer gwcNum;

    @Schema(description = "最低起购量")
    private Integer lowestBuy;

    @Schema(description = "商城一级分类")
    private Integer gtype;

    @Schema(description = "商城二级分类")
    private Integer goodsMain;
}
