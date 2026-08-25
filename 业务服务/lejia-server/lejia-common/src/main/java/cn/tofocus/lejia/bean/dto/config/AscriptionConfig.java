package cn.tofocus.lejia.bean.dto.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AscriptionConfig
{
    @Schema(description = "是否开启评价功能")
    private Boolean enableComment = false;
}
