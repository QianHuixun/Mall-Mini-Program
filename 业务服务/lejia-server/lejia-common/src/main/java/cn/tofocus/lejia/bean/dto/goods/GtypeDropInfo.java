package cn.tofocus.lejia.bean.dto.goods;

import java.util.List;

import cn.tofocus.lejia.bean.dto.market.MktGoodsMainSimple;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GtypeDropInfo
{
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "商品名称")
    private List<MktGoodsMainSimple> goodsList;
}
