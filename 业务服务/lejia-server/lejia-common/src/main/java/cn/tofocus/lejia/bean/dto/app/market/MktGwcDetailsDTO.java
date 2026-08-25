package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车
 *
 * @author zdw 2020-07-16
 */

@Data
public class MktGwcDetailsDTO {

    @Schema(description = "pkey")
    private Integer pkey;
    @Schema(description = "商品")
    private Integer goods;
    @Schema(description = "商品名称")
    private String goodsTitle;
    @Schema(description = "商品是否上架")
    private Boolean goodsEnabled;
    @Schema(description = "商品图片")
    private String photo;
    @Schema(description = "规格")
    private Integer space;
    @Schema(description = "规格名称")
    private String spaceName = "";
    @Schema(description = "规格是否还存在")
    private Boolean sapceEnabled = true;
    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售", required = true)
    private MType mType;
    @Schema(description = "单个价格")
    private BigDecimal price = new BigDecimal(0);
    private BigDecimal priceOld;
    private BigDecimal priceMember;
    @Schema(description = "数量")
    private Integer num;
    private Integer kcNum;
    @Schema(description = "市场")
    private String farmer;
    @Schema(description = "市场名称")
    private String farmerName;
}
