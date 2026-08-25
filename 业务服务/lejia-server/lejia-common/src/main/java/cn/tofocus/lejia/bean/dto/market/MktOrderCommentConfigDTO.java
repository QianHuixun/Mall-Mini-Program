package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MktOrderCommentConfigDTO
{
    @NotNull(message = "评价功能启停状态不能为空")
    @Schema(description = "启停评价功能")
    private Boolean enableComment;
}
