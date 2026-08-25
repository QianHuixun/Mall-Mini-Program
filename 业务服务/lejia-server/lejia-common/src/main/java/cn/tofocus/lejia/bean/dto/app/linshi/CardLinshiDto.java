package cn.tofocus.lejia.bean.dto.app.linshi;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CardLinshiDto
{
    private Integer card;
    
    @Schema(description = "一次赠送卡券的数量")
    private Integer num;
    
    private Date startDate;
    
    private Date endDate;
    
    @Schema(description = "一天可赠送卡券的数量")
    private Integer dayNum;
    
    @Schema(description = "支付价格,单位元")
    private BigDecimal amt;
    
    private String pkey;
}
