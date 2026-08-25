package cn.tofocus.lejia.bean.dto.app.vendor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.ShowType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppVendorBoutiquerIndexInfo
{
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
    @Schema(description = "商户名称")
    private String vendorName;
    
    @Schema(description = "摊位号")
    private String booth;
    
    @Schema(description = "经营范围")
    private String businessScopesName;
    
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
    
    @Schema(description = "展示内容1-商品")
    private AppVendorBoutiquerGoodsInfo goods1;
    
    @Schema(description = "展示内容2-商品")
    private AppVendorBoutiquerGoodsInfo goods2;

    @JsonIgnore
    private String label;
    
}
