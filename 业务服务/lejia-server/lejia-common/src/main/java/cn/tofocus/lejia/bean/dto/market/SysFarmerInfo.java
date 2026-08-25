package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysFarmerInfo extends SysFarmerOnList
{
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
