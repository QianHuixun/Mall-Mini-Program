package cn.tofocus.lejia.bean.dto.market.jd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdCategoryDrop
{
    @Schema(description = "pkey")
    private Long pkey;
    
    @Schema(description = "当前分类名称")
    private String categoryName;
}
