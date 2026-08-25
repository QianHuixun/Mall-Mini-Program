package cn.tofocus.lejia.bean.dto.zx;

import javax.validation.constraints.NotNull;

import cn.tofocus.common.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ZxUserInfoForUpdBank extends BaseZxUserInfoForUpdBank
{
    @NotNull(message = "主键不能为空")
    @Schema(description = "pkey")
    private Integer pkey;
}
