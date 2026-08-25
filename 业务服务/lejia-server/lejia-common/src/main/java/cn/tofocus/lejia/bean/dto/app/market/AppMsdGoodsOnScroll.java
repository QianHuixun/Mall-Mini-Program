package cn.tofocus.lejia.bean.dto.app.market;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "民生商品搜索滚动查询结果")
public class AppMsdGoodsOnScroll
{
    @Schema(description = "商品列表（MALL 在前，JD 在后）")
    private List<AppMsdGoodsOnList> list;

    @Schema(description = "下次滚动起始值；null 表示已无更多")
    private Integer nextOffset;
}
