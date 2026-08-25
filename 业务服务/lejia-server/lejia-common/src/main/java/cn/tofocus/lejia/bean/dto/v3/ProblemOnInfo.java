package cn.tofocus.lejia.bean.dto.v3;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProblemOnInfo
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "问题名称")
    @NotEmpty(message = "问题名称不能为空")
    @Size(max = 50, message = "字数超出限制")
    private String name;
    
    @Schema(description = "问题分类")
    @NotNull(message = "问题分类不能为空")
    private Integer type;
    
    @JoinDTO(dataQuery = "mktProblemTypeDao", from = "type")
    private String typeName;
    
    @Schema(description = "回答")
    @NotEmpty(message = "回答不能为空")
    @Size(max = 200, message = "字数超出限制")
    private String answer;
    
    @Schema(description = "默认问题")
    private Boolean isDefault;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
}
