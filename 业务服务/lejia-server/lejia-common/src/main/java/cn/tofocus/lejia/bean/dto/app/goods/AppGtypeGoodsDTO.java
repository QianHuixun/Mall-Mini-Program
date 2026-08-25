package cn.tofocus.lejia.bean.dto.app.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AppGtypeGoodsDTO {

    @Schema(description = "商品pkey")
    private Integer goods;
    @Schema(description = "商品标题")
    private String goodsTitle;
    @Schema(description = "销售数量")
    private Integer xsNum;
    List<AppGtypeGoodsSpaceDTO> goodsSpaceList = new ArrayList<>();
}
