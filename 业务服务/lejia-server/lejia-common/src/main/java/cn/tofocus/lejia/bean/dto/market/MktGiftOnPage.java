package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktGiftOnPage
{
    
    @Schema(description = "订单编号")
    private String code;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "商品规格")
    private String spaceName;
    
    @Schema(description = "订单金额")
    private BigDecimal amtn;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "下单时间")
    private Date createdTime;
    
    @JsonIgnore
    private Integer orderPkey;
    
    @JsonIgnore
    private Integer space;
}
