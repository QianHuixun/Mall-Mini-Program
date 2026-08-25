package cn.tofocus.lejia.bean.dto.goods;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GtypeDropV2Info
{
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "二级分类")
    private List<TwoGtypeDrop> threeGtypeList;
}
