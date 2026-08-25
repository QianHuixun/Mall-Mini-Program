package cn.tofocus.account.bean.user.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class AppRoleInfo extends AppRoleForUpd
{
    @Schema(description = "角色所属域")
    private String domainid;

    @Schema(description = "角色所属机构")
    private String orgid;

    @Schema(description = "角色所属部门")
    private String deptid;
}
