package cn.tofocus.account.bean.user.app;

import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class AppRoleInfoOnPage extends AppRoleInfo
{
    @JoinDTO(dataQuery = "orgReadCache", from = "orgid")
    @Schema(description = "角色所属机构")
    private String orgName;

    @JoinDTO(dataQuery = "deptReadCache", from = "deptid")
    @Schema(description = "角色所属部门")
    private String deptName;
}
