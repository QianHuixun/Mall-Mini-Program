package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktSupplierInfo
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "供应商名称")
    @Size(max = 100)
    private String name;
    
    @Schema(description = "手机号码")
    @Size(max = 20)
    private String mobile;
    
    @Schema(description = "开始营业时间")
    private String startBusinessTime;
    
    @Schema(description = "结束营业时间")
    private String endBusinessTime;
    
    @Schema(description = "是否支持自提")
    private Boolean allowedPickup;
    
    @Schema(description = "是否支持配送")
    private Boolean allowedDelivery;
    
    @Schema(description = "快递寄件人")
    private String expressSender;
    
    @Schema(description = "快递寄件手机号")
    private String expressMobile;
    
    @Schema(description = "快递寄件省")
    private String expressPro;
    
    @Schema(description = "快递寄件市")
    private String expressCity;
    
    @Schema(description = "快递寄件区")
    private String expressArea;
    
    @Schema(description = "快递寄件地址")
    private String expressAddress;
    
    @Schema(description = "顺丰月结卡号")
    private String sfMonthlyCard;
    
    @Schema(description = "顺丰寄件appId")
    private String sfAppId;
    
    @Schema(description = "顺丰寄件sk")
    private String sfSk;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
    @Schema(description = "自提地点")
    private List<PickupLocation> pickupLocations;
    
    @Data
    public static class PickupLocation
    {
        @Schema(description = "主键")
        private Integer pkey;
        
        @Schema(description = "自提点地址")
        @Size(max = 200)
        private String address;
        
        @Schema(description = "经度")
        private BigDecimal longitude;
        
        @Schema(description = "纬度")
        private BigDecimal latitude;
    }
}
