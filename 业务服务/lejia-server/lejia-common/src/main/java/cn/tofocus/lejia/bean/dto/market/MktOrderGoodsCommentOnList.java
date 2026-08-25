package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;
import java.util.List;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.CommentApplyStatus;
import cn.tofocus.lejia.bean.enums.CommentReplyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(innerTypeName = "F")
public class MktOrderGoodsCommentOnList
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "用户手机号")
    private String memberMobile;
    
    @Schema(description = "订单号")
    private String orderCode;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "评分")
    private Integer score;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "图片")
    private List<String> photo;
    
    @Schema(description = "回复状态")
    private CommentReplyStatus replyStatus;
    
    @Schema(description = "回复状态名称")
    @JoinEnum(from = "replyStatus")
    private String replyStatusName;
    
    @Schema(description = "审核状态")
    private CommentApplyStatus applyStatus;
    
    @Schema(description = "建档时间")
    private Date createdTime;
}
