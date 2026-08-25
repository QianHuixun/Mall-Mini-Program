package cn.tofocus.lejia.bean.dto.market.jd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdPostageConfigDTO
{
    @Schema(description = "京东专区是否消费者承担运费")
    private Boolean isConsumerPostage = true;
}
