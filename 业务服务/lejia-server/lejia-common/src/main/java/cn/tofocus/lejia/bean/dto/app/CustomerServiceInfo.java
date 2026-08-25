package cn.tofocus.lejia.bean.dto.app;

import java.util.List;

import cn.tofocus.lejia.bean.entity.sys.SysFarmerTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerServiceInfo
{
    @Schema(description = "售后电话")
    private String tel;
    
    @Schema(description = "微信客服企业ID")
    private String customerServiceId;
    
    @Schema(description = "客服链接")
    private String customerServiceLink;
    
    @Schema(description = "营业日期")
    private String days;
    
    @Schema(description = "营业时间")
    private List<SysFarmerTime> times;
}
