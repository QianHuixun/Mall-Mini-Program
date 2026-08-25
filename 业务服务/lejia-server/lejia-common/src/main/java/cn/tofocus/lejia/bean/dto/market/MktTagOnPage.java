package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.member.TagType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktTagOnPage
{
    @Schema(description = "主键")
    private Integer pkey;

    @Schema(description = "类型")
    private TagType type;

    @JoinEnum(from = "type")
    @Schema(description = "类型名称")
    private String typeName;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "建档时间")
    private Date createdTime;
}
