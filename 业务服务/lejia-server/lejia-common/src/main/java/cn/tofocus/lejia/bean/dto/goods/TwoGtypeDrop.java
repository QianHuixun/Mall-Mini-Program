package cn.tofocus.lejia.bean.dto.goods;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TwoGtypeDrop
{
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "三级分类")
    private List<ThreeGtypeDropInfo> threeGtypeList;
}
