package cn.tofocus.lejia.bean.dto.market.jd;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdCategoryThreeDrop
{
    @Schema(description = "pkey")
    private Long pkey;
    
    @Schema(description = "当前分类名称")
    private String categoryName;
    
    @Schema(description = "下级分类")
    private List<JdCategoryThreeDrop> list;
}
