package cn.tofocus.lejia.bean.dto.refund;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WebOrderRefundOnInfo
{
    @JsonIgnore
    @Schema(description = "商户主键")
    private Integer vendor;
    
    @Schema(description = "商户名称")
    private String name;
    
    @Schema(description = "商户摊位")
    private String booth;
    
    private List<WebOrderRefundOnList> list;
}
