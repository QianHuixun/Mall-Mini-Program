package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktVendorGoodsDTO
{
    
    /**
     * pkey
     */
    @Schema(description = "pkey")
    private Integer pkey;
    
    /**
    * 商户
    */
    @Schema(description = "商户")
    private Integer vendor;
    
    /**
     * 商户
     */
    @Schema(description = "商户名称")
    private String vendorName;
    public String getVendorName()
    {
        if(vendor != null && vendor == 0)
            return "自采";
        return vendorName;
    }
    
    /**
    * goods
    */
    @Schema(description = "goods")
    private Integer goods;
    
    /**
    * price
    */
    @Schema(description = "最后采购价格")
    private BigDecimal price;
    
    /**
    * 市场
    */
    @Schema(description = "市场")
    private String farmer;
    
    /**
    * 公司
    */
    @Schema(description = "公司")
    private String company;
    
    /**
    * 最后更新时间
    */
    @Schema(description = "最后更新时间")
    private Date updateTime;
    
}
