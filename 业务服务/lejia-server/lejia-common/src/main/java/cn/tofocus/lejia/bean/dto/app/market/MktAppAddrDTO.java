package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktAppAddrDTO
{
    /**
     * pkey
     */
    @Schema(description = "pkey")
    private Integer pkey;
    
    
    @Schema(description = "省")
    private String pro;

    @Schema(description = "市")
    private String city;
    
    @Schema(description = "地址")
    private String addr;
    
    /**
    * 详细地址
    */
    @Schema(description = "详细地址")
    private String addrDetail;
    
    @Schema(description = "距离")
    private BigDecimal distance;
    
    /**
    * 收货人
    */
    @Schema(description = "收货人")
    private String name;
    
    /**
    * 收货人手机
    */
    @Schema(description = "收货人手机")
    private String mobile;
    
    /**
    * 默认地址
    */
    @Schema(description = "是否有效")
    private Boolean enabled;
    
    /**
    * 默认地址
    */
    @Schema(description = "默认地址")
    private Boolean defaultAddr;
}
