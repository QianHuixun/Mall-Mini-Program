package cn.tofocus.lejia.bean.dto.v2.gwc;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.db.dto.JoinProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GwcJdSkuOnList {
    @Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "京东商品主键")
    private Long skuId;

    @Schema(description = "京东商品spu主键")
    private Long spuId;

    @JoinProperty(dataQuery = "jdGoodsDao", from = "skuId", propertyName = "title", type = String.class)
    @Schema(description = "标题")
    private String title;

    @JsonIgnore
    @JoinProperty(dataQuery = "jdGoodsDao", from = "skuId", propertyName = "photo1", type = List.class)
    private List<String> photo1;

    @Schema(description = "商品图片")
    public String getPhoto()
    {
//        List<String> list =  JsonUtil.getBean(this.photo1, new TypeReference<List<String>>(){});
        if (CollectionUtil.isNotEmpty(this.photo1)) return this.photo1.get(0);
        return null;
    }

    @JoinProperty(dataQuery = "jdGoodsDao", from = "skuId", propertyName = "price", type = BigDecimal.class)
    @Schema(description = "京东销售价，实际下单价格以此为准")
    private BigDecimal salePrice;

    @JoinProperty(dataQuery = "jdGoodsDao", from = "skuId", propertyName = "enabled", type = Boolean.class)
    @Schema(description = "启用标志")
    private Boolean enabled;

    @JoinProperty(dataQuery = "jdGoodsDao", from = "skuId", propertyName = "skuState", type = Integer.class)
    @Schema(description = "主站上下架状态 (1上架 0下架)")
    private Integer skuState;

    @Schema(description = "是否在售（综合判断了启用及京东上下架）")
    public Boolean getOnSale()
    {
        return Boolean.TRUE.equals(enabled) && skuState == 1;
    }

    @Schema(description = "数量")
    private Integer num;

    @Schema(description = "合计价格")
    private BigDecimal getSumPrice()
    {
        BigDecimal sumPrice = BigDecimal.ZERO;
        if (getOnSale() && this.salePrice != null && this.num != null)
        {
            sumPrice = this.salePrice.multiply(new BigDecimal(num));
        }
        return sumPrice;
    }

    @JoinProperty(dataQuery = "jdGoodsDao", from = "skuId", propertyName = "lowestBuy", type = Integer.class)
    @Schema(description = "最低起购量")
    private Integer lowestBuy;
}
