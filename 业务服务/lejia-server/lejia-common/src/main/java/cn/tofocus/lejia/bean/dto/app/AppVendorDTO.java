package cn.tofocus.lejia.bean.dto.app;

import java.math.BigDecimal;

import cn.tofocus.lejia.bean.enums.v2.VendorZxStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppVendorDTO
{
    /**
     * pkey
     */
    @Schema(description = "pkey")
    private Integer pkey;
    
    /**
     * 店名
     */
    @Schema(description = "店名")
    public String getName()
    {
        return displayName;
    }
    
    private String displayName;
    
    @Schema(description = "头像")
    private String headIcon;
    
    @Schema(description = "中信银行审核结果")
    private VendorZxStatus zxStatus;
    
    @Schema(description = "中信银行审核结果")
    public String getZxStatusName()
    {
        if (zxStatus != null) return zxStatus.getName();
        return "";
    }
    
    /**
     * 负责人
     */
    @Schema(description = "负责人")
    private String manager;
    
    /**
     * 手机
     */
    @Schema(description = "手机")
    private String mobile;
    
    @Schema(description = "积分收益")
    private Integer points = 0;
    
    @Schema(description = "核销卡券")
    private BigDecimal useCardNum;
    
    private BigDecimal amtToday = BigDecimal.ZERO;
    
    private BigDecimal amtMonth = BigDecimal.ZERO;
    
    @Schema(description = "商品管理,true:显示,false:不显示")
    private Boolean goodsManage = false;
}
