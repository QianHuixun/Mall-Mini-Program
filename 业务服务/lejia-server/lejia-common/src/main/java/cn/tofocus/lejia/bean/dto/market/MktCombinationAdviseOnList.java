package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.bean.enums.LocationType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktCombinationAdviseOnList
{
    /**
     * pkey
     */

    @Schema(description = "pkey")
    private Integer pkey;
    @Schema(description = "展示位置")
     private LocationType locationType;
    @Schema(description = "展示位置名称")
    @JoinEnum(from = "locationType")
    private String locationTypeName;

    /**
    * 名称
    */
    @Schema(description = "名称")
    @NotBlank
    @Size(max=64)
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

    
    
    
    @Schema(description = "内容")
    @Size(max=255)
    private String  content;
    
    @Schema(description = "排序")
    private Integer rank;
    
    
    @Schema(description = "指定标签")
    private List<Integer> targerKeys;
    
    @Schema(description = "状态")
    private Boolean  enabled;
    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    
    
    @Schema(description = "建立时间")
    private Date  createdTime;
}
