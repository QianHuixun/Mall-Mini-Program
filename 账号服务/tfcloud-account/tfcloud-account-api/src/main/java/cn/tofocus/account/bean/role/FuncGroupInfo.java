package cn.tofocus.account.bean.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class FuncGroupInfo extends FuncGroupForUpd
{
    @Schema(description = "域")
    private String domainid;

}
