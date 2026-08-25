package cn.tofocus.lejia.bean.dto.zx;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.common.util.StringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ZxUserInfoForUpdUser extends BaseZxUserInfoForUpdUser
{
    @Schema(description = "pkey")
    private Integer pkey;
}
