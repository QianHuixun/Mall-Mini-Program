package cn.tofocus.lejia.bean.dto.market;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktFunMenuConfigInfo
{
    /**
     * pkey
     */

    @Schema(description = "pkey")
    private Integer pkey;
    

    /**
    * 名称
    */
    @Schema(description = "名称")
    @NotBlank
    @Size(max=5)
    private String name;
    

    
    @Schema(description = "图片Url")
    @NotBlank
    @Size(max=255)
    private String photos;
    
    
    @Schema(description = "点击效果")
    private LinkType urlType;
    
    @Schema(description = "点击效果名称")
    @JoinEnum(from = "urlType")
    private String urlTypeName;

    
   
    
    /**
     * 对象
     */
    @Schema(description = "对象")
    private String objKey = "";

    @Schema(description = "商品名称", hidden = true)
    private String goodsName = "";
    
    @Schema(description = "分类名称")
    private String objKeyName = "";

    @Schema(description = "卡券活动名称", hidden = true)
    private String activityName = "";
    
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "指定标签")
    private List<Integer> targerKeys;
    

    
    @Schema(description = "状态")
    private Boolean  enabled;
    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
}
