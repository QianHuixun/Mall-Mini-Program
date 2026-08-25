package cn.tofocus.lejia.bean.dto.express;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SfGetFreightAddedServicesResult
{
    @Schema(description = "基础运费原价")
    private String originalPrice;
    
    @Schema(description = "基础运费折后价")
    private String discountPrice;
    
    @Schema(description = "基础运费折扣差价")
    private String subPrice;
    
    @Schema(description = "总价，计算用")
    private BigDecimal totalBD;
    
    @Schema(description = "总价")
    private String total;
    
    @Schema(description = "增值服务列表")
    private List<Vas> vas;
    
    // 产品相关附加增值费列表，如超长超重费；透传pvs，字段非固定，可与快递管家开发沟通并择取有用字段
    @Schema(description = "产品相关附加增值费列表")
    private List<ServiceFee> productServiceFeeList;
    
    @Data
    public static class Vas
    {
        @Schema(description = "增值服务名称")
        private String vasName;
        
        @Schema(description = "增值服务费用，值为空时以- -表示")
        private String vasValue;
    }
    
    @Data
    public static class ServiceFee
    {
        @Schema(description = "币种")
        private String currency;
        
        @Schema(description = "是否指导价")
        private Boolean guide;
        
        @Schema(description = "产品附加增值服务代码")
        private String serviceCode;
        
        @Schema(description = "服务费")
        private Double serviceFee;
        
        @Schema(description = "服务名称")
        private String serviceName;
        
        @Schema(description = "标准服务费")
        private Double stdServiceFee;
        
        @Schema(description = "计费重量")
        private Double weight;
    }
}
