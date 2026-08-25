package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.member.TagType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktTagInfo
{
    @Schema(description = "主键")
    private Integer pkey;

    @NotNull(message = "标签类型不能为空")
    @Schema(description = "类型")
    private TagType type;

    @JoinEnum(from = "type")
    @Schema(description = "类型名称")
    private String typeName;
    
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50)
    @Schema(description = "名称")
    private String name;
    
    @Size(max = 200)
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "建档时间")
    private Date createdTime;
}
