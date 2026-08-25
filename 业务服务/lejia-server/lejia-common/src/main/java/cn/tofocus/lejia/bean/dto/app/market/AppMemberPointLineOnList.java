package cn.tofocus.lejia.bean.dto.app.market;

import java.util.Date;

import cn.tofocus.lejia.bean.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppMemberPointLineOnList
{
    
    /**
     * pkey
     */
    @Schema(description = "pkey")
    private Integer pkey;
    
    /**
     * 用户
     */
    @Schema(description = "用户")
    private Integer member;
    
    /**
     * 借贷标志 借(-)/贷(+)
     */
    @Schema(description = "借贷标志 借(-)/贷(+)")
    private Boolean direct;
    
    /**
     * 积分值
     */
    @Schema(description = "积分值")
    private Integer points;
    
    /**
     * 积分来源 购买+/消费-/手动+-
     */
    @Schema(description = "积分来源 购买+/消费-/手动+-")
    private SourceType source;
    
    public String getSourceName()
    {
        if(source != null)
            return source.getName();
        return "";
    }
    
    @Schema(description = "来源单据")
    private String formId;
    
    private String remark;
    
    /**
     * 建档时间
     */
    @Schema(description = "建档时间")
    private Date createdTime;
}
