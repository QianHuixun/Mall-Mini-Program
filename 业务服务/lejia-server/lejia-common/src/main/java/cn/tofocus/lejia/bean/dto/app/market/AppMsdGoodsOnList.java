package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 民生商品搜索列表项（MSD 滚动查询专用）：MALL 与 JD 源统一。
 * 相比 {@link AppMallGoodsOnPage}，去掉了 MSD 路径恒为 null 的无用字段（point/spaces/sellingPoints/gwcNum）。
 */
@Data
@Schema(description = "民生商品搜索列表项（MSD 滚动查询，MALL/JD 统一）")
public class AppMsdGoodsOnList
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

    @Schema(description = "原价（仅 JD 源）")
    private BigDecimal priceOld;

    @Schema(description = "照片")
    private List<String> photo1;

    @Schema(description = "列表小图")
    private String wrapperPhoto;

    @Schema(description = "销量")
    private Integer xsNum;

    @Schema(description = "最低起购量（仅 JD 源）")
    private Integer lowestBuy;

    @Schema(description = "商城一级分类（仅 MALL 源）")
    private Integer gtype;

    @Schema(description = "商城二级分类（仅 MALL 源）")
    private Integer goodsMain;
}
