package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class MarketTechConfig
{
    @Schema(description = "打印机设备编码")
    @Size(max = 200)
    private String printCode;
    
    @Schema(description = "第三方派送应用id")
    @Size(max = 50)
    private String wanliAppId;
    
    @Schema(description = "第三方派送应用密钥")
    @Size(max = 50)
    private String wanliSecret;
    
    @Schema(description = "第三方派送门店id")
    @Size(max = 200)
    private String storeId;
    
    @Schema(description = "第三方派送店铺id")
    @Size(max = 200)
    private String shopId;
    
    @Schema(description = "小票内容")
    private String content;
    
    @Schema(description = "图1")
    private String photo1;
    
    @Schema(description = "图1文字")
    private String photo1Text;
    
    @Schema(description = "图2")
    private String photo2;
    
    @Schema(description = "图2文字")
    private String photo2Text;
}
