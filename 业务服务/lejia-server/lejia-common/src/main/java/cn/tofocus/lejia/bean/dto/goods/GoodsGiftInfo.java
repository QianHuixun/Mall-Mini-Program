package cn.tofocus.lejia.bean.dto.goods;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsGiftInfo
{
    @Schema(description = "市场名称")
    private String farmerName;
    
    @Schema(description = "商户名称")
    private String verdorName;
    
    @Schema(description = "商户手机")
    private String verdorMobile;
    
    @Schema(description = "商户地址（非积分市场这里显示市场地址）")
    private String address;
    
    @Schema(description = "礼券兑换-开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "礼券兑换-到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
}
