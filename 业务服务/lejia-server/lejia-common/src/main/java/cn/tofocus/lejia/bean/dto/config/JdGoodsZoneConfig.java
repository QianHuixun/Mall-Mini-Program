package cn.tofocus.lejia.bean.dto.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdGoodsZoneConfig
{
    @Schema(description = "京东商品专区显示名称")
    private String jdGoodsName = "京东优选";

    @Schema(description = "京东专区是否消费者承担运费")
    private Boolean isConsumerPostage = true;
}
