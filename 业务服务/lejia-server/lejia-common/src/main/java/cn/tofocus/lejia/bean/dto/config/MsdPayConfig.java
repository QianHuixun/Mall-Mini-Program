package cn.tofocus.lejia.bean.dto.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MsdPayConfig
{
    @Schema(description = "允许市场商品使用热力豆支付")
    private Boolean farmerGoods = false;
    
    @Schema(description = "允许自营、滨农、预售使用热力豆支付")
    private Boolean sysGoods = false;
}
