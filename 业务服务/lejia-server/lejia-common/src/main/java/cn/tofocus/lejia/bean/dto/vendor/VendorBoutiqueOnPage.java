package cn.tofocus.lejia.bean.dto.vendor;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.ShowType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorBoutiqueOnPage
{
    private Integer pkey;
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
    @Schema(description = "商户名称")
    private String name;
    
    @Schema(description = "商户展示名称")
    private String displayName;
    
    @Schema(description = "标签")
    private List<String> labels;
    
    @Schema(description = "展示类型1")
    private ShowType showType1;
    
    @Schema(description = "展示内容1")
    private String showContent1;
    
    @Schema(description = "展示类型2")
    private ShowType showType2;
    
    @Schema(description = "展示内容2")
    private String showContent2;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "建档时间")
    private Date createdTime;
    
    @JsonIgnore
    private String label;
}
