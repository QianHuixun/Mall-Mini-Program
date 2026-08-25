package cn.tofocus.lejia.bean.dto.market;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.SearchKeywordModule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MktSearchKeywordInfo
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "模块")
    @NotNull(message = "模块不能为空")
    private SearchKeywordModule module;
    
    @Schema(description = "模块名称")
    @JoinEnum(from = "module")
    private String moduleName;
    
    @Schema(description = "关键词")
    @NotBlank(message = "关键词不能为空")
    private String keyword;
    
    @Schema(description = "排序")
    private Integer sort;
}
