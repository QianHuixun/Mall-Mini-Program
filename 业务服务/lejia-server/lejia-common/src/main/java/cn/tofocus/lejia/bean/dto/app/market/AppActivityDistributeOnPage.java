package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppActivityDistributeOnPage
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "活动名称")
    private String name;

    @Schema(description = "活动宣传图")
    private String photo;
    
    @Schema(description = "卡券数量")
    private Integer couponNum;
    
    @Schema(description = "套餐总数")
    private Integer num;
    
    @Schema(description = "已发放数量")
    private Integer issuedNum;
}
