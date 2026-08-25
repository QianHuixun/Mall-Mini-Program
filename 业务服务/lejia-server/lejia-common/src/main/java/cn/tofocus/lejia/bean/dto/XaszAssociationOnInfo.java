package cn.tofocus.lejia.bean.dto;

import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class XaszAssociationOnInfo
{
    private Integer pkey;
    
    @Schema(description = "saas市场主键")
    private String farmer;
    
    @JoinDTO(dataQuery = "sysFarmerDao", from = "farmer")
    private String farmerName;
    
    @Schema(description = "云农贸市场主键")
    private Integer market;
    
    private String marketName;
    
}
