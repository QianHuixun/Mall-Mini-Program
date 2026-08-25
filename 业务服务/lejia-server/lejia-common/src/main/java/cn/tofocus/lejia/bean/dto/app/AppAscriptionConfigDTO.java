package cn.tofocus.lejia.bean.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppAscriptionConfigDTO
{
    @Schema(description = "是否开启评价功能")
    private Boolean enableComment;
}
