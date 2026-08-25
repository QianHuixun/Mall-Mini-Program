package cn.tofocus.account.bean.application;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ModelInfo extends ModelforUpd
{
    @Schema(description = "所属域")
    @NotNull
    private String domainid;

    @Schema(description = "模块下的菜单是否默认显示")
    @NotNull
    private boolean defShowMenu = true;
}
