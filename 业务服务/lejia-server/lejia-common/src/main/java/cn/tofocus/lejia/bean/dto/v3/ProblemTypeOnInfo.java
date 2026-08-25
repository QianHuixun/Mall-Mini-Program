package cn.tofocus.lejia.bean.dto.v3;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProblemTypeOnInfo
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "类型名称")
    @NotEmpty(message = "问题名称不能为空")
    @Size(max = 20, message = "字数超出限制")
    private String name;
    
}
