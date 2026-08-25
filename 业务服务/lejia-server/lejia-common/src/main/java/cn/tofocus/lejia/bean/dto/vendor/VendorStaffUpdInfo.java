package cn.tofocus.lejia.bean.dto.vendor;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorStaffUpdInfo
{
    private Integer pkey;
    
    @Schema(description = "商户")
    @NotNull(message = "商户主键不能为空")
    private Integer vendor;
    
    @Schema(description = "姓名")
    @NotNull(message = "姓名不能为空")
    private String name;
    
    @Schema(description = "手机号码")
    @NotNull(message = "手机号码不能为空")
    private String mobile;
    
    @Schema(description = "市场主键")
    private String farmer;
}
