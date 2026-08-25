package cn.tofocus.lejia.bean.dto.zx;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ZxUserInfoDrop
{
    private Integer pkey;
    
    @Schema(description = "账户名称")
    private String name;
}
