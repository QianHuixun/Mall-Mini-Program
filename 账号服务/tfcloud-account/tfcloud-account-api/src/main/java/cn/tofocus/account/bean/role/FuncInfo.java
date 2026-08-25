package cn.tofocus.account.bean.role;

import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class FuncInfo extends FuncForUpd
{
    @Schema(description = "域")
    private String domainid;

    @Schema(description = "权限组")
    @JoinDTO(from = "funcGroup", dataQuery = "appFunctionGroupDao")
    private String funcGroupName;

    public FuncInfo(SysFunctionEnum funcEnum)
    {
        setPkey(funcEnum.name());
        setName(funcEnum.getName());
        setDescription(funcEnum.getDescription());
        setFuncGroup("sys");
    }
}
