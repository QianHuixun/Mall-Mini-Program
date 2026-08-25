package cn.tofocus.lejia.bean.dto.gtype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsMainInfo
{
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
}
