package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.ActivityDistributeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktActivityOnPage
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "卡券数量")
    private Integer couponNum;
    
    @Schema(description = "套餐总数")
    private Integer num;
    
    @Schema(description = "已发放数量")
    private Integer issuedNum;
    
    @Schema(description = "已领取卡券数")
    private Integer receiveNum = 0;
    
    @Schema(description = "已使用卡券数")
    private Integer useNum = 0;
    
    @Schema(description = "是否启用")
    private Boolean enabled;
    
    @Schema(description = "活动分发方式")
    private ActivityDistributeType distributeType;

    @Schema(description = "会员福利展示图")
    private String welfarePhoto;
    
    @Schema(description = "活动分发方式名称")
    @JoinEnum(from = "distributeType")
    private String distributeTypeName;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "建档时间")
    private Date createdTime;
}
