package cn.tofocus.lejia.bean.dto.vendor;

import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorStaffOnPage
{
    private Integer pkey;
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
    @Schema(description = "商户名称")
    private String vendorName;
    
    @Schema(description = "姓名")
    private String name;
    
    @Schema(description = "手机号码")
    private String mobile;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "市场名称")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "farmer")
    private String farmerName;
    
    @Schema(description = "市场主键")
    private String farmer;
}
