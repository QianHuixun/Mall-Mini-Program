package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerMtype;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerTime;
import cn.tofocus.lejia.bean.enums.v4.DeliveryDate;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysFarmerOnList
{
    /**
     * pkey
     */
    @Schema(description = "pkey", hidden = true)
    private String pkey;
    
    /**
     * 菜场名称
     */
    @Schema(description = "菜场名称", required = true)
    private String name;
    
    @Schema(description = "市场类别")
    private FarmerType type;
    
    /**
     * 菜场编码
     */
    @Schema(description = "菜场编码")
    private String code;
    
    /**
     * 管理员
     */
    @Schema(description = "管理员", required = true)
    private String manager;
    
    /**
     * 负责人手机
     */
    @Schema(description = "负责人手机", required = true)
    private String mobile;
    
    /**
     * 市场logo
     */
    @Schema(description = "市场logo")
    private String logo;
    
    /**
     * 介绍
     */
    @Schema(description = "介绍")
    private String content;
    
    /**
     * 售后电话
     */
    @Schema(description = "售后电话")
    private String tel;
    
    /**
     * 市场照片
     */
    @Schema(description = "市场照片1")
    private String photo1;
    
    /**
     * 市场照片
     */
    @Schema(description = "市场照片2")
    private String photo2;
    
    /**
     * 市场照片
     */
    @Schema(description = "市场照片3")
    private String photo3;
    
    /**
     * 公司pkey
     */
    @Schema(description = "公司pkey", required = true)
    private String comPkey;
    
    /**
     * 启用标志
     */
    @Schema(description = "启用标志", required = true)
    private Boolean enabled;
    
    private Date createdTime;
    
    @Schema(description = "市场相关配置信息")
    private SysFarmerConfig config;
    
    @Schema(description = "市场相关配置信息")
    private List<SysFarmerMtype> types;
    
    @Schema(description = "市场营业时间")
    private List<SysFarmerTime> times;
    
    @Schema(description = "自提时间小时", required = false)
    @Min(0)
    @Max(23)
    private Integer pickupHour;
    
    
    @Schema(description = "配送日期", required = false)
    private DeliveryDate pickupDeliveryDate;
    /**
     * 配送分
     */
    @Schema(description = "自提时间分", required = false)
    @Min(0)
    @Max(59)
    private Integer pickupMinute;

    @Deprecated
    @Schema(description = "自提点地址", required = true)
    @NotBlank(message = "自提点不能为空")
    private String pickUpAddress;
    
}
