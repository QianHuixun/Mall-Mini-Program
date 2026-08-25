package cn.tofocus.lejia.bean.dto.sys;

import java.util.Date;
import java.util.List;

import cn.tofocus.lejia.bean.enums.ManagerRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ManagerOnPage
{
    private Integer pkey;
    
    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "角色")
    private List<String> roleNames;

    @Schema(description = "建档时间")
    private Date createdTime;
    
    @Schema(description = "角色枚举")
    private List<ManagerRole> roles;
}
