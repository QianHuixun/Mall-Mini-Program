package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AppCollectionDTO {
    /**
     * pkey
     */
    @Schema(description = "pkey", required = true)
    private Integer pkey;


    /**
     * 类型 菜谱/商品
     */
    @Schema(description = "类型 菜谱/商品", required = true)
    private Integer ctype;

    /**
     * 对象主键
     */
    @Schema(description = "对象主键", required = true)
    private Integer objKey;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "价格")
    private BigDecimal price = new BigDecimal(0);

    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售", required = false)
    private MType mType;

    @Schema(description = "收藏数量")
    private Integer collCount = 0;

    @Schema(description = "图片")
    private String photo = "";

    @Schema(description = "商品规格")
    private Integer goodsSpace;
    
    @Schema(description = "摊位号")
    private String booth;
    
    @Schema(description = "销售数量")
    private Integer xsNum;
}
