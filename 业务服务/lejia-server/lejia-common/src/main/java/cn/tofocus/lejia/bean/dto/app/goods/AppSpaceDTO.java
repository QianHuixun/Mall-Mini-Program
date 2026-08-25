package cn.tofocus.lejia.bean.dto.app.goods;

import java.util.ArrayList;
import java.util.List;

import cn.tofocus.lejia.bean.dto.goods.GoodsProcessOnInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppSpaceDTO
{
    @Schema(description = "商品pkey")
    private Integer goods;
    
    @Schema(description = "是否是积分商城商品(包括礼品券)")
    private Boolean isGoodsIntegral = false;
    
    @Schema(description = "商品标题")
    private String goodsTitle;
    
    @Schema(description = "规格列表")
    List<AppSpaceDetailsDTO> spaceList = new ArrayList<>();
    
    @Schema(description = "是否可加工")
    private Boolean isProcess;
    
    @Schema(description = "加工可选项")
    private List<GoodsProcessOnInfo> processLines;
}
