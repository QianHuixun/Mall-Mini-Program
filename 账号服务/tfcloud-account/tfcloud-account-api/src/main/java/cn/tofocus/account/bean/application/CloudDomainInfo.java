package cn.tofocus.account.bean.application;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CloudDomainInfo
{
    @NotBlank
    @Size(max = 40)
    private String pkey;

    @NotBlank
    @Size(max = 40)
    @Schema(description = "名称")
    private String name;
}
