package cn.tofocus.lejia.bean.dto.app.market;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppGoodsCommentOnList
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @JsonIgnore
    @Schema(description = "评价用户")
    private Integer member;
    
    @Schema(description = "评价用户名称")
    @JoinProperty(dataQuery = "mktMemberDao", from = "member", propertyName = "name", type = MktMember.class)
    private String memberName;
    
    @Schema(description = "评价用户头像")
    @JoinProperty(dataQuery = "mktMemberDao", from = "member", propertyName = "photo", type = MktMember.class)
    private String memberPhoto;
    
    @Schema(description = "评分")
    private Integer score;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "图片")
    private List<String> photo;
    
    @Schema(description = "回复内容")
    private String replyContent;
    
    @Schema(description = "建档时间")
    private Date createdTime;
}
