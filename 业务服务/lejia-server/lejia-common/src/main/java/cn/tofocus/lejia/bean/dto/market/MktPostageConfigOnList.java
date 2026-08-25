package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;

import javax.persistence.Id;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktPostageConfigOnList
{
    /**
     * pkey
     */
    @Id
    @Schema(description = "pkey", hidden = true)
    private Integer pkey;
    
    /**
    * 重量
    */
    @Schema(description = "重量", required = true)
    private BigDecimal weight;
    
    /**
    * 邮费
    */
    @Schema(description = "邮费", required = true)
    private BigDecimal postage;
    
}
