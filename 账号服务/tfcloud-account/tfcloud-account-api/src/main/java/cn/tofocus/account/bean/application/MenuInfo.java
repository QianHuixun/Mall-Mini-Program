package cn.tofocus.account.bean.application;

import javax.validation.constraints.NotNull;

import cn.tofocus.core.enums.MenuType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MenuInfo extends MenuForUpd
{
    @Schema(description = "所属域")
    @NotNull
    private String domainid;

    @Schema(description = "所属应用")
    @NotNull
    private String appid;

    @Schema(description = "类型")
    @NotNull
    private MenuType type;
}
