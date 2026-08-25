package cn.tofocus.lejia.bean.dto.sys;

import java.util.Date;

import cn.tofocus.db.dto.UserName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RoleInfo
{
    @Schema(description = "角色主键")
    private String pkey;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "创建时间")
    private Date createdTime;

    @Schema(description = "更新时间")
    private Date updatedTime;

    @Schema(description = "创建人")
    private Long createdBy;

    @Schema(description = "创建人名称")
    @UserName(from = "createdBy")
    private String createdByName;
}
