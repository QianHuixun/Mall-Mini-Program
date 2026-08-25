package cn.tofocus.lejia.bean.dto.vendor;

import java.util.List;

import cn.tofocus.lejia.bean.enums.ShowType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorBoutiqueInfo
{
    private Integer pkey;
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
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
}
