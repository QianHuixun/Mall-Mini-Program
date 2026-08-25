package cn.tofocus.lejia.bean.dto.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FarmerGoodsZoneConfig
{
    @Schema(description = "特价商品专区显示名称")
    private String specialDisplayName = "限时秒杀";
}
