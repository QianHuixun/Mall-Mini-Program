package cn.tofocus.account.bean.org;

import cn.tofocus.core.data.StrKeyName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OrgKV extends StrKeyName
{

    @Schema(description = "所属域")
    private String domainid;
}
